package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inthree.boon.deliveryapp.request.ResetPasswordReq;

import java.util.ArrayList;


public class UndeliveryResp {

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }

    @SerializedName("res_msg")
    private String res_msg;

    public String getRes_code() {
        return res_code;
    }

    public void setRes_code(String res_code) {
        this.res_code = res_code;
    }

    @SerializedName("res_code")
    private String res_code;

    @SerializedName("data")
    @Expose
    private ArrayList<UndeliveryResp> undelivered = new ArrayList<>();

    public ArrayList<UndeliveryResp> getUndeliveredResp() {
        return undelivered;
    }
}
