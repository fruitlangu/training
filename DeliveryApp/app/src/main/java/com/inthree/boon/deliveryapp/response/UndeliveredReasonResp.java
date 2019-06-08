package com.inthree.boon.deliveryapp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UndeliveredReasonResp {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason_status() {
        return reason_status;
    }

    public void setReason_status(String reason_status) {
        this.reason_status = reason_status;
    }

    @SerializedName("id")
    private String id;

    @SerializedName("rid")
    private String rid;

    @SerializedName("reason")
    private String reason;

    @SerializedName("reason_status")
    private String reason_status;

    @SerializedName("undelivered")
    @Expose
    private ArrayList<UndeliveredReasonResp> undeliveryReason = new ArrayList<>();

    public ArrayList<UndeliveredReasonResp> getUndeliveryReason() {
        return undeliveryReason;
    }

}
