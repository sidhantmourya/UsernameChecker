package com.user.checker.UsernameChecker.filter;

import com.user.checker.UsernameChecker.entity.UsersDb;
import com.user.checker.UsernameChecker.filter.interfaces.BloomFilterIF;
import com.user.checker.UsernameChecker.repository.UsersDBRepository;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Component("redisBloom")
public class RedisBloomFilter implements BloomFilterIF {

    private final Logger logger = Logger.getLogger(RedisBloomFilter.class.getName());

    private volatile RBloomFilter<String> bloomFilter;

    private final String filterName = "username:bloom";

    private static final String LOADED_FLAG = "username:bloom:loaded";

    private final AtomicBoolean ready = new AtomicBoolean(false);

    //    @Autowired
    private RedissonClient redissonClient;

    public RedisBloomFilter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void put(String username) {
        if (!ready.get()) {
            // Skip bloom update during warmup
            return;
        }
        this.bloomFilter.add(username);
    }

    @Override
    public boolean mightContain(String username) {
        if (!ready.get()) {
            // Bloom not ready → act conservative
            return true; // force DB check
        }
        return bloomFilter.contains(username);
    }

    @Override
    public void loadAllUserNames(UsersDBRepository repo) {
        logger.info("Starting Bloom filter population from Cassandra...");
        bloomFilter = redissonClient.getBloomFilter(filterName);
        bloomFilter.tryInit(10000000, 0.01);

        if (isAlreadyLoaded()) {
            logger.info("Redis Bloom already loaded, skipping reload");
            return;
        }

        logger.info("Loading usernames into Redis Bloom filter");

        int size =25_000;


        long totalLoaded = 0;

        Pageable pageable = PageRequest.of(0, size);
        Slice<UsersDb> slice;
        do{
            slice = repo.findAll(pageable);
            totalLoaded += slice.getNumberOfElements();
            slice.getContent().forEach(user -> bloomFilter.add(user.getUsername()));

            logger.info("Loaded usernames into Bloom filter: "+ totalLoaded);

            pageable = slice.nextPageable();
        }while (slice.hasNext());

//        List<UsersDb> userNames = repo.findPaged(size);
       /* while(!userNames.isEmpty())
        {
            totalLoaded += userNames.size();
            userNames.forEach(user -> bloomFilter.add(user.getUsername()));
            userNames = repo.findPagedAfter(userNames.getLast().getUsername(), size);



            logger.info("Loaded usernames into Bloom filter: "+ totalLoaded);

        }*/

        logger.info("Finished loading Bloom filter. Total users loaded: " + totalLoaded);

        markAsLoaded();
        ready.set(true);
    }

    @Override
    public boolean isReady() {
        
        return bloomFilter !=null && bloomFilter.isExists();
        
    }

    private boolean isAlreadyLoaded() {
        return Boolean.TRUE.equals(
                redissonClient.getBucket(LOADED_FLAG).get()
        );
    }

    private void markAsLoaded() {
        redissonClient.getBucket(LOADED_FLAG).set(true);
    }
}
