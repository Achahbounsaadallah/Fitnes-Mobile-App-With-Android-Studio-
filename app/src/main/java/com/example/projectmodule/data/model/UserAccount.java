package com.example.projectmodule.data.model;

public class UserAccount {

    private final long id;
    private final String username;
    private final String displayName;

    public UserAccount(long id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }
}

