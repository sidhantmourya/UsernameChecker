package com.user.checker.UsernameChecker.service;

import com.user.checker.UsernameChecker.component.UserTrie;
import com.user.checker.UsernameChecker.entity.UsersDb;
import com.user.checker.UsernameChecker.filter.UserNameBloomFilter;
import com.user.checker.UsernameChecker.repository.UsersDBRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeUnit;

@Service
public class UserNameService {

    private static final Logger logger = LoggerFactory.getLogger(UserNameService.class);

    private final UsersDBRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserNameBloomFilter userNameBloomFilter;
    private final UserTrie trie;
    private final List<UsersDb> userBuffer = Collections.synchronizedList(new ArrayList<>());

    public UserNameService(UsersDBRepository repository, RedisTemplate<String, Object> redisTemplate, UserNameBloomFilter userNameBloomFilter, UserTrie trie) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.userNameBloomFilter = userNameBloomFilter;
        this.trie = trie;
    }


    public String checkAndRegisterUser(String username, String email){

        if(userNameBloomFilter.mightContain(username) || redisTemplate.hasKey("userName:"+ username))
        {
            List<String> suggestedUsers = trie.getAllSuggestions(username);
            return suggestedUsers.toString();

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

        List<UsersDb> usersBatch = null;
        synchronized (userBuffer)
        {
            if(userBuffer.size() >= 1000) {
                usersBatch = new ArrayList<>(userBuffer);
                userBuffer.clear();
            }
        }
        if(usersBatch!= null)
        {
            try {
                saveUsersInAsync(usersBatch);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

//        redisTemplate.opsForValue().set("");


//        repository.save(user);

//        BloomFilter<String> bloomFilter = Bl

        return "Save";
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
