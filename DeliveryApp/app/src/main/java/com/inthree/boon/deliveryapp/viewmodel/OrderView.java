package com.inthree.boon.deliveryapp.viewmodel;

import android.databinding.BaseObservable;



public class OrderView extends BaseObservable {

    private String username;
    private String password;


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

}
