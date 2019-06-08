package com.inthree.boon.deliveryapp.request;


import com.google.gson.annotations.SerializedName;

public class OrderStatusReq {

    @SerializedName("runner_id")
    private String runnerID;

    @SerializedName("shipment_id")
    private String shipmentID;

    public String getRunnerID() {
        return runnerID;
    }

    public void setRunnerID(String runnerID) {
        this.runnerID = runnerID;
    }

    public String getShipmentID() {
        return shipmentID;
    }

    public void setShipmentID(String shipmentID) {
        this.shipmentID = shipmentID;
    }

}
