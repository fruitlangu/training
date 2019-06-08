package com.inthree.boon.deliveryapp.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class UndeliveryReq {

    @SerializedName("runsheetNo")
    @Expose
    private String runsheetNo;
    @SerializedName("actualAmount")
    @Expose
    private String actualAmount;
    @SerializedName("originalAmount")
    @Expose
    private String originalAmount;
    @SerializedName("moneyTransactionType")
    @Expose
    private String moneyTransactionType;
    @SerializedName("referenceNumber")
    @Expose
    private String referenceNumber;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("attemptCount")
    @Expose
    private int attemptCount;
    @SerializedName("jobType")
    @Expose
    private String jobType;
    @SerializedName("employeeCode")
    @Expose
    private String employeeCode;
    @SerializedName("hubCode")
    @Expose
    private String hubCode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("trackHalt")
    @Expose
    private String trackHalt;
    @SerializedName("trackKm")
    @Expose
    private String trackKm;
    @SerializedName("trackTransactionTimeSpent")
    @Expose
    private String trackTransactionTimeSpent;
    @SerializedName("merchantCode")
    @Expose
    private String merchantCode;
    @SerializedName("transactionDate")
    @Expose
    private String transactionDate;
    @SerializedName("erpPushTime")
    @Expose
    private String erpPushTime;
    @SerializedName("lastTransactionTime")
    @Expose
    private String lastTransactionTime;
    @SerializedName("battery")
    @Expose
    private String battery;



    @SerializedName("redirect")
    @Expose
    private String redirect;

    @SerializedName("fieldData")
    @Expose
    private FieldData fieldData;



    public String getRunsheetNo() {
        return runsheetNo;
    }

    public void setRunsheetNo(String runsheetNo) {
        this.runsheetNo = runsheetNo;
    }

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(String originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getMoneyTransactionType() {
        return moneyTransactionType;
    }

    public void setMoneyTransactionType(String moneyTransactionType) {
        this.moneyTransactionType = moneyTransactionType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getHubCode() {
        return hubCode;
    }

    public void setHubCode(String hubCode) {
        this.hubCode = hubCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrackHalt() {
        return trackHalt;
    }

    public void setTrackHalt(String trackHalt) {
        this.trackHalt = trackHalt;
    }

    public String getTrackKm() {
        return trackKm;
    }

    public void setTrackKm(String trackKm) {
        this.trackKm = trackKm;
    }

    public String getTrackTransactionTimeSpent() {
        return trackTransactionTimeSpent;
    }

    public void setTrackTransactionTimeSpent(String trackTransactionTimeSpent) {
        this.trackTransactionTimeSpent = trackTransactionTimeSpent;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getErpPushTime() {
        return erpPushTime;
    }

    public void setErpPushTime(String erpPushTime) {
        this.erpPushTime = erpPushTime;
    }

    public String getLastTransactionTime() {
        return lastTransactionTime;
    }

    public void setLastTransactionTime(String lastTransactionTime) {
        this.lastTransactionTime = lastTransactionTime;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public FieldData getFieldData() {
        return fieldData;
    }

    public void setFieldData(FieldData fieldData) {
        this.fieldData = fieldData;
    }

    public static class FieldData {

        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("reason")
        @Expose
        private String reason;
        @SerializedName("remarks")
        @Expose
        private String remarks;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @SerializedName("address")
        @Expose
        private String address;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

    }

}
