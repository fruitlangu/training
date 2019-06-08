package com.inthree.boon.deliveryapp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PartialResp {



    @SerializedName("res_msg")
    private String res_msg;

    @SerializedName("res_code")
    private String res_code;

    @SerializedName("any_filed")
    @Expose
    private ArrayList<PartialResp> delivery = new ArrayList<>();

    public ArrayList<PartialResp> getPartialDelivery() {
        return delivery;
    }

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }

    public String getRes_code() {
        return res_code;
    }

    public void setRes_code(String res_code) {
        this.res_code = res_code;
    }
}
