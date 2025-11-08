package com.user.checker.UsernameChecker.dto;

import java.util.List;

public class ResponseDTO {
    private boolean userExists;
    private List<String> suggestedUsernames;

    public ResponseDTO() {
    }

    public ResponseDTO(boolean userExists, List<String> suggestedUsernames) {
        this.userExists = userExists;
        this.suggestedUsernames = suggestedUsernames;
    }

    public boolean isUserExists() {
        return userExists;
    }

    public void setUserExists(boolean userExists) {
        this.userExists = userExists;
    }

    public List<String> getSuggestedUsernames() {
        return suggestedUsernames;
    }

    public void setSuggestedUsernames(List<String> suggestedUsernames) {
        this.suggestedUsernames = suggestedUsernames;
    }
}
