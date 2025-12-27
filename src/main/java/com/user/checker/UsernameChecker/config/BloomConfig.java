package com.user.checker.UsernameChecker.config;

import com.user.checker.UsernameChecker.filter.InMemoryBloomFilter;
import com.user.checker.UsernameChecker.filter.RedisBloomFilter;
import com.user.checker.UsernameChecker.filter.interfaces.BloomFilterIF;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomConfig {


    @Bean
    @ConditionalOnProperty("bloom.redis.enabled")
    public BloomFilterIF redisBloomFilter(RedissonClient client) {
        return new RedisBloomFilter(client);
    }

    @Bean
    @ConditionalOnProperty("bloom.in-memory.enabled")
    public BloomFilterIF inMemoryBloomFilter() {
        return new InMemoryBloomFilter();
    }


}
