package com.inthree.boon.deliveryapp.model;


public class UndeliverConfirm {

    /**
     * Set and get the shipment number for deliver
     */
    private String shipmentNumber = "";

    /**
     * Get the remarks of undelivered
     */
    private String remarks = "";

    /**
     * Get the proofNumber
     */
    private String proofPhoto = "";

    /**
     * Set and Get the reason of undelivered
     */
    private String reason = "";

    /**
     * Set and Get the reason of undelivered
     */
    private String latitude;


    /**
     * Set and Get the reason of undelivered
     */
    private String redirect = "";

    /**
     * Set and Get the reason of undelivered
     */
    private String longtitude;

    private String address;

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getProofPhoto() {
        return proofPhoto;
    }

    public void setProofPhoto(String proofPhoto) {
        this.proofPhoto = proofPhoto;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
