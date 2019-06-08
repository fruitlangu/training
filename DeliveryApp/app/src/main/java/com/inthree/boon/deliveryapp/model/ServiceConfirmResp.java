package com.inthree.boon.deliveryapp.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServiceConfirmResp {



    @SerializedName("res_msg")
    private String res_msg;

    @SerializedName("any_filed")
    @Expose
    private ArrayList<ServiceConfirmResp> delivery = new ArrayList<>();

    public ArrayList<ServiceConfirmResp> getDelivery() {
        return delivery;
    }

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }
}
