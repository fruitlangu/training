package com.inthree.boon.deliveryapp.request;

import com.google.gson.annotations.SerializedName;



public class ResetPasswordReq {

    @SerializedName("u_id")
    private String id;


    @SerializedName("otp")
    private String otp;

    @SerializedName("password")
    private String password;

    @SerializedName("confirm_password")
    private String confirm_password;

    @SerializedName("id")
    private String user_id;

    @SerializedName("res_msg")
    private String res_msg;

    @SerializedName("res_code")
    private String res_code;



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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
