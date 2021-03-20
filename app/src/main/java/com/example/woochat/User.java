package com.example.woochat;

public class User {
    public final String userId;
    public final String email;
    public final String name;

    public User(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
