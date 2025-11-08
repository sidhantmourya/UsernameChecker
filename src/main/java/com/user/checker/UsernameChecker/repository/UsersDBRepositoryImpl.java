package com.user.checker.UsernameChecker.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.user.checker.UsernameChecker.entity.UsersDb;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UsersDBRepositoryImpl implements UsersDBRepositoryCustom {

    private final CqlSession session;
    private final PreparedStatement stmt;

    public UsersDBRepositoryImpl(CqlSession session, PreparedStatement stmt) {
        this.session = session;
        this.stmt = stmt;
    }


    @Override
    public CompletableFuture<Void> saveAllAsync(List<UsersDb> users) {
        // Use UNLOGGED batch for better performance (no distributed transaction overhead)
        // Safe to use for independent inserts
        BatchStatementBuilder batchBuilder = BatchStatement.builder(DefaultBatchType.UNLOGGED);

        for (UsersDb user : users) {
            BoundStatement bound = stmt.bind(
                    user.getUsername(),
                    user.getCreated_at(),
                    user.getEmail(),
                    user.getUuid()
            );
            batchBuilder.addStatement(bound);
        }

        BatchStatement batch = batchBuilder.build();

        return session.executeAsync(batch)
                .toCompletableFuture()
                .thenApply(rs -> null);
    }
}
