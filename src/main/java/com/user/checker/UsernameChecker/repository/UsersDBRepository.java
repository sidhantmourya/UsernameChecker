package com.user.checker.UsernameChecker.repository;

import com.user.checker.UsernameChecker.entity.UsersDb;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface UsersDBRepository extends CassandraRepository<UsersDb, String>, UsersDBRepositoryCustom {

    UsersDb findByUsername(String username);


//    List<String> findAllUsers();

    @Query("select username from user limit :limit allow filtering")
    List<UsersDb> findPaged(@Param("limit") int limit);


    @Query("select username from user where token(username) > token(?0) limit ?1 allow filtering")
    List<UsersDb> findPagedAfter(String lastUsername, int limit);

    @Query("select username from user")
    Slice<UsersDb> findAll(Pageable pageable);

}
