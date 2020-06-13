package com.example.coffeeshop;

import android.util.Log;

import java.io.Serializable;

public class User implements Serializable {
    private String Username;
    private String Role;
    private String Password;
    public User()
    {

    }

    public User(String username, String role, String password) {
        Username = username;
        Role = role;
        Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
