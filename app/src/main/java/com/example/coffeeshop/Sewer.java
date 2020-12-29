package com.example.coffeeshop;

import java.io.Serializable;

public class Sewer implements Serializable {
    String OFFLINE_COLOR = "#828282";
    String ONLINE_COLOR = "#40B85C";
    String Name;
    String Desc="";
    String Category="";// loai cong (lon, vua, nho)
    String Location="";//dia diem
    String Channel;//channel mqtt va socket
    String Id;
    public Sewer() {
        //for returning object from firebase
    }

    public Sewer(String name, String desc, String category, String location, String channel, String id) {
        Name = name;
        Desc = desc;
        Category = category;
        Location = location;
        Channel = channel;
        Id = id;
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getLocation(){
        return Location;
    }

    public void setLocation(String location){
        Location = location;
    }

    public String getChannel(){
        return Channel;
    }

    public void setChannel(String channel){
        Channel = channel;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
