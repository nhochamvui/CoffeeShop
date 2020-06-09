package com.example.coffeeshop;

public class Item {
    String name;
    String desc;
    String price;
    String img;
    String category;
    String id;
    public Item() {
        //for returning object from firebase
    }

    public Item(String name, String desc, String price, String img, String category) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.img = img;
        this.category = category;
    }
    public Item(String name, String desc, String price, String img, String category, String id) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.img = img;
        this.category = category;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public Item(String name, String desc, String price) {
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
