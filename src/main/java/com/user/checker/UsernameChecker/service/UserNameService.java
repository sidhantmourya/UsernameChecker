package com.user.checker.UsernameChecker.service;

import com.user.checker.UsernameChecker.component.UserTrie;
import com.user.checker.UsernameChecker.dto.ResponseDTO;
import com.user.checker.UsernameChecker.entity.UsersDb;
import com.user.checker.UsernameChecker.filter.UserNameBloomFilter;
import com.user.checker.UsernameChecker.repository.UsersDBRepository;
import com.user.checker.UsernameChecker.repository.UsersDBRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class UserNameService {

    private static final Logger logger = LoggerFactory.getLogger(UserNameService.class);

    private final UsersDBRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserNameBloomFilter userNameBloomFilter;
    private final UserTrie trie;
    private final ConcurrentLinkedDeque<UsersDb> userBuffer = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final int maxBatches = 10;
    private final Semaphore concurrentSemaphore = new Semaphore(maxBatches);
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private long lastBatchSaveTime = System.currentTimeMillis();


    public UserNameService(UsersDBRepository repository, RedisTemplate<String, Object> redisTemplate, UserNameBloomFilter userNameBloomFilter, UserTrie trie) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.userNameBloomFilter = userNameBloomFilter;
        this.trie = trie;

        startBatchProcessor();
    }

    private void startBatchProcessor() {
        executorService.submit(() -> {
                while(running.get())
                {

                    try {
                        processBatchIfAvailable();
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        });
    }

    private void processBatchIfAvailable() throws InterruptedException {

        long now = System.currentTimeMillis();
        boolean timeExceeded = (now - lastBatchSaveTime) >= 300000;
        
        // Determine how many users to drain
        int usersToDrain;
        if (userBuffer.size() > 300) {
            // Heavy load: drain exactly 50 users
            usersToDrain = 300;
        } else if (timeExceeded && !userBuffer.isEmpty()) {
            // Time exceeded: drain whatever is available
            usersToDrain = userBuffer.size();
        } else {
            // Nothing to do
            return;
        }

        // Drain users from buffer
        List<UsersDb> drainedUsers = new ArrayList<>();
        for (int i = 0; i < usersToDrain; i++) {
            UsersDb user = userBuffer.poll();
            if (user == null) break;
            drainedUsers.add(user);
        }

        if (drainedUsers.isEmpty()) {
            return;
        }

        lastBatchSaveTime = now;
        
        // Split into batches of 10 and process concurrently
        int batchSize = 80;
        
        for (int i = 0; i < drainedUsers.size(); i += batchSize) {
            int end = Math.min(i + batchSize, drainedUsers.size());
            List<UsersDb> batch = new ArrayList<>(drainedUsers.subList(i, end));
            
            concurrentSemaphore.acquire();
            executorService.submit(() -> {
                saveBatchWithRetry(batch, 3);
            });
        }
    }

    private void saveBatchWithRetry(List<UsersDb> batch, int maxRetries) {
        saveBatchWithRetry(batch, maxRetries, 0);
    }

    private void saveBatchWithRetry(List<UsersDb> batch, int maxRetries, int attempt) {
        try {
            logger.debug("Saving batch of {} users (attempt {})", batch.size(), attempt + 1);
            repository.saveAllAsync(batch)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        if (attempt < maxRetries - 1) {
                            // Retry with exponential backoff
                            long delayMs = (long) Math.pow(2, attempt) * 100; // 100ms, 200ms, 400ms
                            logger.warn("Batch save failed (attempt {}), retrying in {}ms", attempt + 1, delayMs, ex);
                            try {
                                Thread.sleep(delayMs);
                                saveBatchWithRetry(batch, maxRetries, attempt + 1);
                            } catch (InterruptedException ie) {
                                logger.error("Retry interrupted", ie);
                                concurrentSemaphore.release();
                            }
                        } else {
                            logger.error("Failed to save batch after {} attempts, dropping {} users", maxRetries, batch.size(), ex);
                            concurrentSemaphore.release();
                        }
                    } else {
                        logger.debug("Successfully saved batch of {} users", batch.size());
                        concurrentSemaphore.release();
                    }
                });
        } catch (Exception e) {
            logger.error("Exception in batch save", e);
            concurrentSemaphore.release();
        }
    }

    public void shutdown()
    {
        running.set(false);
        executorService.shutdown();
    }


    public ResponseDTO checkAndRegisterUser(String username, String email){

        if(userNameBloomFilter.mightContain(username) || redisTemplate.hasKey("userName:"+ username))
        {
            List<String> suggestedUsers = trie.getAllSuggestions(username);
            // Take up to 3 suggestions and join them into a single string
//            return suggestedUsers.stream()
//                    .limit(3)
//                    .collect(Collectors.joining(", "));

            List<String> suggestions = new ArrayList<>();
            int i = 0;
            while(suggestions.size()< 3 && i < suggestedUsers.size()) {
                if (!userNameBloomFilter.mightContain(suggestedUsers.get(i)) && !redisTemplate.hasKey("userName:" + suggestedUsers.get(i))) {
                    if (!suggestions.contains(suggestedUsers.get(i))) { // Avoid duplicate suggestions
                        suggestions.add(suggestedUsers.get(i));
                    }

                }
                i++;
            }


            Random random = new Random();
            while (suggestions.size() < 3) {
                int randomNumber = random.nextInt(999) + 1; // Generates a number between 1 and 999
                String newUsername = username + randomNumber;
                if (!userNameBloomFilter.mightContain(newUsername) && !redisTemplate.hasKey("userName:" + newUsername)) {
                    if (!suggestions.contains(newUsername)) { // Avoid duplicate suggestions
                        trie.insert(newUsername);
                        suggestions.add(newUsername);
                    }
                }
            }
            return new ResponseDTO(true, suggestions);
        }

        UsersDb user = new UsersDb();
        user.setCreated_at(LocalDate.now());
        user.setEmail(email);
        user.setUsername(username);
        user.setUuid(UUID.randomUUID().getMostSignificantBits());

        redisTemplate.opsForValue().set(
                "userName:"+ username, username, 10, TimeUnit.MINUTES
        );

        userNameBloomFilter.put(username);
        trie.insert(username);
        userBuffer.add(user);
        System.out.println("Users Size: "+ userBuffer.size() + " inserted user: "+ user.getUsername());
//        userBuffer.add(user);
//
//        List<UsersDb> usersBatch = null;
//        synchronized (userBuffer)
//        {
//            if(userBuffer.size() >= 1000) {
//                usersBatch = new ArrayList<>(userBuffer);
//                userBuffer.clear();
//            }
//        }
//        if(usersBatch!= null)
//        {
//            try {
//                saveUsersInAsync(usersBatch);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

//        redisTemplate.opsForValue().set("");


//        repository.save(user);

//        BloomFilter<String> bloomFilter = Bl

        return new ResponseDTO(false, null);
    }

    private void saveUsersAsync(List<UsersDb> usersBatch) throws InterruptedException {
        try(var scope = new StructuredTaskScope.ShutdownOnFailure()){

            scope.fork(
                    () ->{
                        repository.saveAll(usersBatch);
                        return null;
                    }
            );

            scope.join();
            scope.throwIfFailed();


        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUsersInAsync(List<UsersDb> usersBatch) throws InterruptedException {
        int totalUsers = usersBatch.size();
        int batchSize = 50;
        for(int i =0 ;i<totalUsers; i+=batchSize)
        {
            int end = Math.min(i+batchSize, totalUsers);
            List<UsersDb> users = usersBatch.subList(i,end);
            saveUsersAsync(users);
        }

    }
}
