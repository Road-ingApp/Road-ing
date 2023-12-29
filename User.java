package com.example.roading;

import androidx.collection.CircularArray;

import java.util.HashMap;

public class User {

    private int id, coins;
    private String username;
    private String password;
    private String email;
    private String phone;


    public User() {
    }

    public User(int id, String username, String password, String email, String phone, int coins) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.coins = coins;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public int getCoins() {
        return coins;
    }
    public void setCoins(int coins){
        this.coins = coins;
    }
}
