package com.inthree.boon.deliveryapp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inthree.boon.deliveryapp.request.OrderReq;

import java.util.ArrayList;

public class DeliveryConfirmResp {



    @SerializedName("res_msg")
    private String res_msg;

    @SerializedName("any_filed")
    @Expose
    private ArrayList<DeliveryConfirmResp> delivery = new ArrayList<>();

    public ArrayList<DeliveryConfirmResp> getDelivery() {
        return delivery;
    }

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }
}
