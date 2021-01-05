package com.example.coffeeshop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

public class User implements Serializable {
    private String Username="";
    private String Displayname="";
    private String Password="";
    private String Avatar="";
    private Boolean Add=true;
    private Boolean Remove=true;
    private Boolean Modify=true;
    private String name;
    private String email;
    private String accessToken;
    private String createdDay;
    private String location;
    private String role;
    public User()
    {

    }
    public User(String json){
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> data = new Gson().fromJson(json, type);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }
    public User(String name, String email, String role, String createdDay, String city, String accessToken){
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdDay = createdDay;
        this.location = city;
        this.accessToken = "Bearer "+accessToken;
        // these are
        this.Username = "";
        this.Displayname = "";
        this.Password = "";
        this.Avatar = "";
        this.Add = true;
        this.Remove = true;
        this.Modify = true;
    }

    public User(String username, String displayname, String role, String password, String avatar, Boolean add, Boolean remove, Boolean modify) {
        Username = username;
        Displayname = displayname;
        role = role;
        Password = password;
        Avatar = avatar;
        Add = add;
        Remove = remove;
        Modify = modify;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedDay() {
        return createdDay;
    }

    public void setCreatedDay(String createdDay) {
        this.createdDay = createdDay;
    }

    public String getCity() {
        return location;
    }

    public void setCity(String city) {
        this.location = city;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
    public String getDisplayname() {
        return Displayname;
    }

    public void setDisplayname(String displayname) {
        Displayname = displayname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        role = role;
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
