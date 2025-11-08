package com.user.checker.UsernameChecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableCassandraRepositories(basePackages = "com.user.checker.UsernameChecker")
public class UsernameCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsernameCheckerApplication.class, args);
	}

}
