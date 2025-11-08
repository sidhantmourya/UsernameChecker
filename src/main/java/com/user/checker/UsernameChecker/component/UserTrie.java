package com.user.checker.UsernameChecker.component;

import com.user.checker.UsernameChecker.bsl.Trie;
import com.user.checker.UsernameChecker.entity.UsersDb;
import com.user.checker.UsernameChecker.repository.UsersDBRepository;
import com.user.checker.UsernameChecker.repository.UsersDBRepositoryCustom;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableAsync
public class UserTrie {
    private final Trie trie= new Trie();

    private final UsersDBRepository usersDBRepository;

    public UserTrie(UsersDBRepository usersDBRepository) {
        this.usersDBRepository = usersDBRepository;
    }

    @PostConstruct
    public void init()
    {
        loadAllUsersToTrie();
    }

    @Async
    private void loadAllUsersToTrie() {

        List<UsersDb> allusers = usersDBRepository.findAll();
        allusers.forEach(usersDb -> trie.insert(usersDb.getUsername()));


    }

    public boolean contains(String username)
    {
        return trie.search(username);
    }

    public List<String> getAllSuggestions(String preix)
    {
        return trie.startsWith(preix);
    }
    public void insert(String username)
    {
        trie.insert(username);
    }

}
