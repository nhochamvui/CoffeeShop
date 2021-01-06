package com.example.coffeeshop;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Sewer implements Serializable {
    private String _id;
    private String name;
    private String description;
    private Map<String, String> location;
    public Sewer(){

    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getLocation() {
        return location;
    }

    public void setLocation(Map<String, String> location) {
        this.location = location;
    }
    public boolean isValidForEdit(){
        if(getLocation().size() != 0 && !getName().equals("") && !getId().equals("")){
            return true;
        }
        return false;
    }
    public boolean isValidForCreate(){
        if(getLocation().size() != 0 && !getName().equals("")){
            return true;
        }
        return false;
    }
}
