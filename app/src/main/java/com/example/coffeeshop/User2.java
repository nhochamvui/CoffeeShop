package com.example.coffeeshop;

import java.io.Serializable;

public class User2 implements Serializable {
    String name;
    String email;
    String role;
    String created_day;
    String city;
    String accessToken;
    public User2(){

    }
    public User2(String name, String email, String role, String created_day, String city, String accessToken){
        this.name = name;
        this.email = email;
        this.role = role;
        this.created_day = created_day;
        this.city = city;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreated_day() {
        return created_day;
    }

    public void setCreated_day(String created_day) {
        this.created_day = created_day;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
