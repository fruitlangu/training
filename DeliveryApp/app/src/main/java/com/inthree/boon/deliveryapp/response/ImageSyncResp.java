package com.inthree.boon.deliveryapp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ImageSyncResp {



    @SerializedName("res_msg")
    private String res_msg;

    public String getRes_sign() {
        return res_sign;
    }

    public void setRes_sign(String res_sign) {
        this.res_sign = res_sign;
    }

    @SerializedName("res_sign")
    private String res_sign;

    @SerializedName("any_filed")
    @Expose
    private ArrayList<ImageSyncResp> delivery = new ArrayList<>();

    public ArrayList<ImageSyncResp> getDelivery() {
        return delivery;
    }

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }
}
