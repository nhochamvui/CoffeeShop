package com.example.coffeeshop;

import java.io.Serializable;
import java.util.Map;

public class User implements Serializable {
    private String name;
    private String email;
    private String role;
    private String created_day;
    private String city;
    private String accessToken;
    private String password = "";
    private Map<String, String> location;
    public User(){

    }
    public User(String name, String email, String role, String created_day, String city, String accessToken, Map<String, String> location){
        this.name = name;
        this.email = email;
        this.role = role;
        this.created_day = created_day;
        this.city = city;
        this.accessToken = accessToken;
        this.location = location;
    }
    public User(String name, String email, String role, Map<String, String> location){
        this.name = name;
        this.email = email;
        this.role = role;
        this.location = location;
    }

    public User(String name, String email, String role, String password, Map<String, String> location){
        this.name = name;
        this.email = email;
        this.role = role;
        this.location = location;
        this.password = password;
    }

    public Map<String, String> getLocation() {
        return location;
    }

    public void setLocation(Map<String, String> location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
