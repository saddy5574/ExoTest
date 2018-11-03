package com.example.sayyad.exotest.Player.Model;

import java.util.HashMap;

public class Categories {

    private HashMap<String,ClipList> categories;

    public Categories() {
        this.categories = new HashMap<>();
    }

    public HashMap<String, ClipList> getCategories() {
        return categories;
    }

    public void setCategories(HashMap<String, ClipList> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "categories=" + categories +
                '}';
    }
}
