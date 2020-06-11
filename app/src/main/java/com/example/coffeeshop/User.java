package com.example.coffeeshop;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String pwd;
    private String id;
    private String role;
    public User()
    {
        //for returning object from firebase
    }
    public User(String username, String pwd, String role) {
        this.username = username;
        this.pwd = pwd;
        this.role = role;
    }
    public User(String username, String pwd, String role, String id) {
        this.username = username;
        this.pwd = pwd;
        this.role = role;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public boolean isNull()
    {
        return (this.username.equals("") && this.pwd.equals("") && this.role.equals(""));
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
