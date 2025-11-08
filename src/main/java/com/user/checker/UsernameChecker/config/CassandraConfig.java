package com.user.checker.UsernameChecker.config;


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {

    @Bean
    public PreparedStatement userInsertStmt(CqlSession session)
    {
        return (PreparedStatement) session.prepare("insert into user(username, created_at, email, uuid) values(?,?,?,?)");
    }


}
