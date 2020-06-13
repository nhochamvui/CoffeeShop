package com.example.coffeeshop;

import java.io.Serializable;

public class Food extends Item implements Serializable {
//    String Size;
    public Food() {

    }

    public Food(String name, String desc, String price, String img, String category, String id) {
        super(name, desc, price, img, category, id);
//        Size = size;
    }

//    public String getSize() {
//        return Size;
//    }

//    public void setSize(String size) {
//        Size = size;
//    }
}
