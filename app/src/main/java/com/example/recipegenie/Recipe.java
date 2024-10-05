package com.example.recipegenie;

public class Recipe {
    private String title;
    private String description;
    private String cooktime;
    private String imageUrl;
    private String meal;
    private String servingInfo;
    private double rating;
    private String recipeID;

    // Empty constructor required for Firebase
    public Recipe() {}

    public Recipe(String title, String description, String cooktime, String imageUrl) {
        this.title = title;
        this.description = description;
        this.cooktime = cooktime;
        this.imageUrl = imageUrl;
    }

    public Recipe(String title, String meal, String servingInfo, String cooktime, double rating, String imageUrl) {
        this.title = title;
        this.meal = meal;
        this.servingInfo = servingInfo;
        this.cooktime = cooktime;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public Recipe(String title, String description, String cooktime, String imageUrl, String recipeID) {
        this.title = title;
        this.description = description;
        this.cooktime = cooktime;
        this.imageUrl = imageUrl;
        this.recipeID = recipeID;
    }


    public String getServingInfo() {
        return servingInfo;
    }

    public void setServingInfo(String servingInfo) {
        this.servingInfo = servingInfo;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
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

    public String getCooktime() {
        return cooktime;
    }

    public void setCooktime(String time) {
        this.cooktime = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
