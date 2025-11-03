package com.user.checker.UsernameChecker.filter;

import com.datastax.oss.driver.shaded.guava.common.hash.BloomFilter;
import com.datastax.oss.driver.shaded.guava.common.hash.Funnels;
import com.user.checker.UsernameChecker.entity.UsersDb;
import com.user.checker.UsernameChecker.repository.UsersDBRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@EnableAsync
public class UserNameBloomFilter {

    private BloomFilter<String> bloomFilter;
    private final UsersDBRepository usersDBRepository;

    public UserNameBloomFilter(UsersDBRepository usersDBRepository) {
        this.usersDBRepository = usersDBRepository;
    }

    @PostConstruct
    public void init() {
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),
                10000000,
                0.01
        );
        loadAllUserNames();


    }

    @Async
    public void loadAllUserNames() {
        List<UsersDb> allusers = usersDBRepository.findAll();
        allusers.forEach(usersDb -> bloomFilter.put(usersDb.getUsername()));
//        allusers.forEach(user -> bloomFilter.put(user.getUsername()));

    }

    public boolean mightContain(String name)
    {
        return  bloomFilter.mightContain(name);
    }

    public void put(String name)
    {
        bloomFilter.put(name);
    }




}
