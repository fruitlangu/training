package com.inthree.boon.deliveryapp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReasonResp {


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

    public String getReason_for() {
        return reason_for;
    }

    public void setReason_for(String reason_for) {
        this.reason_for = reason_for;
    }

    @SerializedName("reason_for")
    private String reason_for;

    @SerializedName("reason_status")
    private String reason_status;

    public String getLang_reason() {
        return lang_reason;
    }

    public void setLang_reason(String lang_reason) {
        this.lang_reason = lang_reason;
    }

    @SerializedName("lang_reason")
    private String lang_reason;

    public String getReason_type() {
        return reason_type;
    }

    public void setReason_type(String reason_type) {
        this.reason_type = reason_type;
    }

    @SerializedName("reason_type")
    private String reason_type;

    @SerializedName("reason_val")
    @Expose
    private ArrayList<ReasonResp> undeliveryReason = new ArrayList<>();

    public ArrayList<ReasonResp> getAllReason() {
        return undeliveryReason;
    }

}
