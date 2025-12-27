package com.user.checker.UsernameChecker.filter;

import com.datastax.oss.driver.shaded.guava.common.hash.BloomFilter;
import com.datastax.oss.driver.shaded.guava.common.hash.Funnels;
import com.user.checker.UsernameChecker.entity.UsersDb;
import com.user.checker.UsernameChecker.filter.interfaces.BloomFilterIF;
import com.user.checker.UsernameChecker.repository.UsersDBRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component("inMemoryBloom")
public class InMemoryBloomFilter implements BloomFilterIF {

    private volatile BloomFilter<String> bloomFilter;
//    private final UsersDBRepository usersDBRepository;

//    public UserNameBloomFilter(UsersDBRepository usersDBRepository) {
//        this.usersDBRepository = usersDBRepository;
//    }

//    @PostConstruct
//    public void init() {
//        bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),
//                10000000,
//                0.01
//        );
//        loadAllUserNames();
//
//
//    }

   /* @Override
    public void loadAllUserNames(UsersDBRepository repo) {
//        List<UsersDb> allusers = usersDBRepository.findAll();
//        allusers.forEach(usersDb -> bloomFilter.put(usersDb.getUsername()));
////        allusers.forEach(user -> bloomFilter.put(user.getUsername()));

    }
*/
    @Override
    public boolean mightContain(String name)
    {
        return  bloomFilter.mightContain(name);
    }


    @Override
    public void put(String name)
    {
        bloomFilter.put(name);
    }

    @Override
    public boolean isReady() {
        return bloomFilter != null;
    }

    @Override
    public void loadAllUserNames(UsersDBRepository repo) {

        bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),
                10000000,
                0.01
        );

        int page = 0, size =10000;
        Page<UsersDb> slice;
        List<UsersDb> userNames = repo.findPaged(size);
        while(!userNames.isEmpty())
        {
            userNames.forEach(user -> bloomFilter.put(user.getUsername()));
            userNames = repo.findPagedAfter(userNames.getLast().getUsername(), size);
        }

    }

}
