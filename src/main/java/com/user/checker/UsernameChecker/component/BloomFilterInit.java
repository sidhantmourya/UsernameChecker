package com.user.checker.UsernameChecker.component;

import com.user.checker.UsernameChecker.factory.BloomFilterFactory;
import com.user.checker.UsernameChecker.filter.RedisBloomFilter;
import com.user.checker.UsernameChecker.filter.interfaces.BloomFilterIF;
import com.user.checker.UsernameChecker.repository.UsersDBRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Component
public class BloomFilterInit {

    private final BloomFilterFactory factory;
    private final UsersDBRepository repo;

    private final Logger logger = Logger.getLogger(BloomFilterInit.class.getName());


    public BloomFilterInit(BloomFilterFactory factory, UsersDBRepository repo) {
        this.factory = factory;
        this.repo = repo;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Async("bloomExecutor")
    public void initiailiseFilters()
    {
//        CompletableFuture.runAsync(
//                () -> {
//                    factory.getFilter("redisBloom").loadAllUserNames(repo);
//                }
//        );
//        CompletableFuture.runAsync(
//                () -> {
//                    factory.getFilter("inMemoryBloom").loadAllUserNames(repo);
//                }
//        ).join();

        logger.info("Starting Bloom filter initialization");

        // Redis Bloom
        BloomFilterIF redisBloom = factory.getFilter("redisBloom");
        redisBloom.loadAllUserNames(repo);

        // In-memory Bloom
        BloomFilterIF inMemoryBloom = factory.getFilter("inMemoryBloom");
        inMemoryBloom.loadAllUserNames(repo);

        logger.info("Bloom filter initialization completed");
    }

}
