package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inthree.boon.deliveryapp.request.ResetPasswordReq;

import java.util.ArrayList;



public class ResetPasswordResp {

    @SerializedName("data")
    @Expose
    private ArrayList<ResetPasswordReq> changePass = new ArrayList<>();

    public ArrayList<ResetPasswordReq> getChangePass() {
        return changePass;
    }
}
