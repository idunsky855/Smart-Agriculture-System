package com.example.androidmobileclient;

public class Sensor {

    private String name;
    private String image;

    public Sensor() {}

    public Sensor(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Sensor setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Sensor setImage(String image) {
        this.image = image;
        return this;
    }
}
