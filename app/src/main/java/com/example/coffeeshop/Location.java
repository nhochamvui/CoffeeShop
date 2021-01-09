package com.example.coffeeshop;

import java.util.ArrayList;
import java.util.Map;

public class Location {
    private String _id;
    private String name;
    private ArrayList<Map<String, String>> district;

    public Location(){}

    public Location(String name, ArrayList<Map<String, String>> district){
        this.name = name;
        this.district = district;
    }
    public Location(String _id, String name, ArrayList<Map<String, String>> district){
        this._id = _id;
        this.name = name;
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Map<String, String>> getDistrict() {
        return district;
    }

    public void setDistrict(ArrayList<Map<String, String>> district) {
        this.district = district;
    }
}
