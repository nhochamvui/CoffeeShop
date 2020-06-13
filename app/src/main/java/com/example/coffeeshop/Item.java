package com.example.coffeeshop;

import java.io.Serializable;

public class Item implements Serializable {
    String Name;
    String Desc;
    String Price;
    String Img;
    String Category;
    String Id;
    public Item() {
        //for returning object from firebase
    }

    public Item(String name, String desc, String price, String img, String category, String id) {
        Name = name;
        Desc = desc;
        Price = price;
        Img = img;
        Category = category;
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

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
