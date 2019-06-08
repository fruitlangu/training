package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReasonVal {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("dio_status")
    @Expose
    private String dioStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDioStatus() {
        return dioStatus;
    }

    public void setDioStatus(String dioStatus) {
        this.dioStatus = dioStatus;
    }

}
