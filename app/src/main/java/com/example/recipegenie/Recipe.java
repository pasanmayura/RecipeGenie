package com.example.recipegenie;

public class Recipe {
    private String title;
    private String description;
    private String time;
    private String imageUrl;

    // Empty constructor required for Firebase
    public Recipe() {}

    public Recipe(String title, String description, String time, String imageUrl) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
