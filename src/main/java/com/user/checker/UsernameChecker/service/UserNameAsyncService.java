package com.user.checker.UsernameChecker.service;

import com.user.checker.UsernameChecker.component.UserTrie;
import com.user.checker.UsernameChecker.dto.ResponseDTO;
import com.user.checker.UsernameChecker.entity.UsersDb;
import com.user.checker.UsernameChecker.factory.BloomFilterFactory;
import com.user.checker.UsernameChecker.filter.interfaces.BloomFilterIF;
import com.user.checker.UsernameChecker.repository.UsersDBRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class UserNameAsyncService {

    private static final Logger logger = LoggerFactory.getLogger(UserNameAsyncService.class);

    private final UsersDBRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BloomFilterFactory factory;
    private final UserTrie trie;

    // In-memory buffer for pending users
    private final ConcurrentLinkedDeque<UsersDb> userBuffer = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    // Executor for async inserts and retry scheduler
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8);
    private final int maxConcurrentInserts = 50;
    private final Semaphore semaphore = new Semaphore(maxConcurrentInserts);

    // Flush configuration
    private long lastFlushTime = System.currentTimeMillis();
    private final int flushThreshold = 300; // max users before flush
    private final long flushIntervalMs = 300_000; // 5 minutes

    public UserNameAsyncService(UsersDBRepository repository,
                                RedisTemplate<String, Object> redisTemplate,
                                BloomFilterFactory factory,
                                UserTrie trie) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.factory = factory;
        this.trie = trie;

        startFlushProcessor();
    }

    /**
     * Periodically flushes the buffer by saving users asynchronously
     */
    private void startFlushProcessor() {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                flushBufferIfNeeded();
            } catch (Exception e) {
                logger.error("Error during flush", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void flushBufferIfNeeded() {
        long now = System.currentTimeMillis();
        boolean timeExceeded = (now - lastFlushTime) >= flushIntervalMs;

        if (userBuffer.isEmpty()) return;
        if (userBuffer.size() < flushThreshold && !timeExceeded) return;

        lastFlushTime = now;

        List<UsersDb> drainedUsers = new ArrayList<>();
        for (int i = 0; i < flushThreshold; i++) {
            UsersDb user = userBuffer.poll();
            if (user == null) break;
            drainedUsers.add(user);
        }

        if (drainedUsers.isEmpty()) return;

        for (UsersDb user : drainedUsers) {
            saveUserAsyncWithRetry(user, 3);
        }
    }

    /**
     * Save a single user asynchronously with retry
     */
    private void saveUserAsyncWithRetry(UsersDb user, int maxRetries) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        saveUserAsync(user)
                .whenComplete((res, ex) -> {
                    semaphore.release();
                    if (ex != null) {
                        if (maxRetries > 1) {
                            long delay = (long) (100 * Math.pow(2, 3 - maxRetries)); // exponential backoff
                            executorService.schedule(() -> saveUserAsyncWithRetry(user, maxRetries - 1),
                                    delay, TimeUnit.MILLISECONDS);
                        } else {
                            logger.error("Failed to save user {} after retries", user.getUsername(), ex);
                        }
                    } else {
                        logger.debug("Successfully saved user {}", user.getUsername());
                    }
                });
    }

    private CompletableFuture<UsersDb> saveUserAsync(UsersDb user) {
        return CompletableFuture.supplyAsync(() -> repository.save(user), executorService);
    }


    /**
     * Register user with Bloom filter + Redis check + Trie suggestions
     */
    public ResponseDTO checkAndRegisterUser(String username, String email, String strategy) {
        BloomFilterIF bloomFilter = factory.getFilter(strategy);

        if (bloomFilter.mightContain(username) || redisTemplate.hasKey("userName:" + username)) {
            List<String> suggestedUsers = trie.getAllSuggestions(username);

            List<String> suggestions = new ArrayList<>();
            int i = 0;
            Random random = new Random();

            while (suggestions.size() < 3 && i < suggestedUsers.size()) {
                String suggestion = suggestedUsers.get(i);
                if (!bloomFilter.mightContain(suggestion) && !redisTemplate.hasKey("userName:" + suggestion)) {
                    suggestions.add(suggestion);
                }
                i++;
            }

            while (suggestions.size() < 3) {
                String newUsername = username + (random.nextInt(999) + 1);
                if (!bloomFilter.mightContain(newUsername) && !redisTemplate.hasKey("userName:" + newUsername)) {
                    trie.insert(newUsername);
                    suggestions.add(newUsername);
                }
            }

            return new ResponseDTO(true, suggestions);
        }

        // Create user
        UsersDb user = new UsersDb();
        user.setCreated_at(LocalDate.now());
        user.setEmail(email);
        user.setUsername(username);
        user.setUuid(UUID.randomUUID().getMostSignificantBits());

        // Update in-memory structures
        redisTemplate.opsForValue().set("userName:" + username, username, 10, TimeUnit.MINUTES);
        bloomFilter.put(username);
        trie.insert(username);

        // Add to buffer for async save
        userBuffer.add(user);

        return new ResponseDTO(false, null);
    }

    public void shutdown() {
        running.set(false);
        executorService.shutdown();
    }
}
