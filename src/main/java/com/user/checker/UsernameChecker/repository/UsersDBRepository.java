package com.user.checker.UsernameChecker.repository;

import com.user.checker.UsernameChecker.entity.UsersDb;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersDBRepository extends CassandraRepository<UsersDb, String> {

    UsersDb findByUsername(String username);

//    List<String> findAllUsers();


}
