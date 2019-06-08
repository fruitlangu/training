package com.inthree.boon.deliveryapp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AttemptResp {

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getShipmentid() {
        return shipmentid;
    }

    public void setShipmentid(String shipmentid) {
        this.shipmentid = shipmentid;
    }

    public String getRunner_id() {
        return runner_id;
    }

    public void setRunner_id(String runner_id) {
        this.runner_id = runner_id;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    @SerializedName("res_msg")
    @Expose
    private String resMsg;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("reference")
    @Expose
    private String reference;

    @SerializedName("orderid")
    @Expose
    private String orderid;

    @SerializedName("shipmentid")
    @Expose
    private String shipmentid;

    @SerializedName("runner_id")
    @Expose
    private String runner_id;

    public String getAttempt() {
        return attempt;
    }

    @SerializedName("attempt")
    @Expose
    private String attempt;

    public String getDelivered_date() {
        return delivered_date;
    }

    public void setDelivered_date(String delivered_date) {
        this.delivered_date = delivered_date;
    }

    @SerializedName("delivered_date")
    @Expose
    private String delivered_date;

    public String getRow_total() {
        return row_total;
    }

    public void setRow_total(String row_total) {
        this.row_total = row_total;
    }

    @SerializedName("row_total")
    @Expose
    private String row_total;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @SerializedName("sku")
    @Expose
    private String sku;

    @SerializedName("attempt_details")
    @Expose
    private ArrayList<AttemptResp> attemptVal = new ArrayList<>();

    public ArrayList<AttemptResp> getAttemptVal() {
        return attemptVal;
    }

    @SerializedName("total_amount")
    @Expose
    private ArrayList<AttemptResp> totalVal = new ArrayList<>();

    public ArrayList<AttemptResp> getTotalAmount() {
        return totalVal;
    }

}
