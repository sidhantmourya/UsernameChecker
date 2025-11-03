package com.user.checker.UsernameChecker.bsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TrieNode {

    Map<Character, TrieNode> children= new ConcurrentHashMap<>();
    boolean isEndOfWord;

    public TrieNode() {
        this.isEndOfWord = false;
    }


}

public class Trie{

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }


    public synchronized void insert(String username)
    {
        TrieNode current = root;
        for(char ch : username.toLowerCase().toCharArray())
        {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());

        }
        current.isEndOfWord = true;
    }

    public boolean search(String username)
    {
        TrieNode node = searchNode(username.toLowerCase());
        return node != null && node.isEndOfWord;

    }

    private TrieNode searchNode(String username) {

        TrieNode current = root;
        for(char ch : username.toCharArray())
        {
            current = current.children.get(ch);
            if(current == null)
                return null;
        }
        return  current;

    }


    public List<String> startsWith(String prefix)
    {
        List<String> results = new ArrayList<>();
        TrieNode node = searchNode(prefix.toLowerCase());
        if(node != null)
        {
            collectAllWords(node, new StringBuilder(prefix), results);
        }
        return  results;
    }

    private void collectAllWords(TrieNode node, StringBuilder prefix, List<String> results) {

        if(node.isEndOfWord )
        {
            results.add(prefix.toString());
        }
        for(Map.Entry<Character, TrieNode> entry: node.children.entrySet())
        {
            prefix.append(entry.getKey());
            collectAllWords(entry.getValue(), prefix, results );
            prefix.deleteCharAt(prefix.length()-1);
        }
    }

}