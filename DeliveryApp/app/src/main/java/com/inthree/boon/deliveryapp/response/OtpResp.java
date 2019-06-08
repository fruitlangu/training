package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpResp {
    @SerializedName("res_msg")
    @Expose
    private String resMsg;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("res_code")
    @Expose
    private Integer resCode;

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }
}
