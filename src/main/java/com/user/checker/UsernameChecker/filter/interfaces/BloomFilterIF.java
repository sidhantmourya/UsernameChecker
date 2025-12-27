package com.user.checker.UsernameChecker.filter.interfaces;

import com.user.checker.UsernameChecker.repository.UsersDBRepository;

public interface BloomFilterIF {

    void put(String username);

    boolean mightContain(String username);

    void loadAllUserNames(UsersDBRepository repo);

    boolean isReady();


}
