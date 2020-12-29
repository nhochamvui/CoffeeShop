package com.example.coffeeshop;

import java.io.Serializable;

public class Drink extends Item implements Serializable {
    public Drink() {

    }

    public Drink(String name, String desc, String price, String img, String category, String id) {
        super(name, desc, price, img, category, id);
    }
}
