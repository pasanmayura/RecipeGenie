package com.example.recipegenie;

public class Users {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userConfirmPassword;

    public Users(String userId, String userName, String userEmail, String userPassword, String userConfirmPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userConfirmPassword = userConfirmPassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserConfirmPassword() {
        return userConfirmPassword;
    }
}
