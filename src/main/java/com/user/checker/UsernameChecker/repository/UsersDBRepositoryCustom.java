package com.user.checker.UsernameChecker.repository;

import com.user.checker.UsernameChecker.entity.UsersDb;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UsersDBRepositoryCustom {

    CompletableFuture<Void> saveAllAsync(List<UsersDb> users);

}
