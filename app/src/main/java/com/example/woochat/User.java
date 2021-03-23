package com.example.woochat;

public class User {
    public String userId;
    public String email;
    public String name;
    public String imageUrl;

    public User(String userId, String email, String name, String imageUrl) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public User() {}
}
