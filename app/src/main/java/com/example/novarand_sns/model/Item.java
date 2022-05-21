package com.example.novarand_sns.model;

public class Item {
    int image;
    String title;
    String date;

    public Item(int image, String title, String date) {
        this.image = image;
        this.title = title;
        this.date = date;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}