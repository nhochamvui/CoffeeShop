package com.example.coffeeshop;

import java.io.Serializable;

public class User implements Serializable {
    private String Username;
    private String Role;
    private String Password;
    private String Avatar;
    private Boolean Add;
    private Boolean Remove;
    private Boolean Modify;
    public User()
    {

    }

    public User(String username, String role, String password, String avatar, Boolean add, Boolean remove, Boolean modify) {
        Username = username;
        Role = role;
        Password = password;
        Avatar = avatar;
        Add = add;
        Remove = remove;
        Modify = modify;
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

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public Boolean getAdd() {
        return Add;
    }

    public void setAdd(Boolean add) {
        Add = add;
    }

    public Boolean getRemove() {
        return Remove;
    }

    public void setRemove(Boolean remove) {
        Remove = remove;
    }

    public Boolean getModify() {
        return Modify;
    }

    public void setModify(Boolean modify) {
        Modify = modify;
    }
}
