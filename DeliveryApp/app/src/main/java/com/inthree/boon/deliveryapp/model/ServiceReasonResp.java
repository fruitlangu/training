package com.inthree.boon.deliveryapp.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServiceReasonResp {


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

    public String getDio_status() {
        return dio_status;
    }

    public void setDio_status(String dio_status) {
        this.dio_status = dio_status;
    }

    @SerializedName("id")
    private String id;


    @SerializedName("reason")
    private String reason;


    @SerializedName("dio_status")
    private String dio_status;



    @SerializedName("reason_val")
    @Expose
    private ArrayList<ServiceReasonResp> serviceReason = new ArrayList<>();

    public ArrayList<ServiceReasonResp> getServiceReason() {
        return serviceReason;
    }

}
