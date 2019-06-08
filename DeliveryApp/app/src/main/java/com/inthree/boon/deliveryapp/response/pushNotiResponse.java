package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Get the response for push notification
 */

public class pushNotiResponse {

    @SerializedName("res_msg")
    @Expose
    private String resMsg;

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }
}
