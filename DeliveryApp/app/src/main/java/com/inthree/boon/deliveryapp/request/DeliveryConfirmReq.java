package com.inthree.boon.deliveryapp.request;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryConfirmReq {


    @SerializedName("sno")
    private String sno;

    @SerializedName("reference")
    private String reference;

    @SerializedName("shipmentnumber")
    private String shipmentNumber;

    @SerializedName("orderNo")
    private String orderNo;

    @SerializedName("landmark")
    private String landmark;

    @SerializedName("customername")
    private String customerName;

    @SerializedName("customercontactno")
    private String customerContactNo;

    @SerializedName("shippingaddress")
    private String shippingAddress;

    @SerializedName("shippingcity")
    private String shippingCity;

    @SerializedName("shippingpincode")
    private String shippingpinCode;

    @SerializedName("deliveryproof")
    private String deliveryProof;

    @SerializedName("invoiceproof")
    private String invoiceProof;

    @SerializedName("addressproof")
    private String addressProof;

    @SerializedName("signproof")
    private String signProof;

    @SerializedName("amountcollected")
    private String amountCollected;

    @SerializedName("syncstatus")
    private String syncStatus;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("pincode")
    private String pincode;


    @SerializedName("fieldData")
    @Expose
    private PartialReq.FieldData fieldData;




    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }




    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContactNo() {
        return customerContactNo;
    }

    public void setCustomerContactNo(String customerContactNo) {
        this.customerContactNo = customerContactNo;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }


    public String getShippingpinCode() {
        return shippingpinCode;
    }

    public void setShippingpinCode(String shippingpinCode) {
        this.shippingpinCode = shippingpinCode;
    }

    public String getDeliveryProof() {
        return deliveryProof;
    }

    public void setDeliveryProof(String deliveryProof) {
        this.deliveryProof = deliveryProof;
    }

    public String getInvoiceProof() {
        return invoiceProof;
    }

    public void setInvoiceProof(String invoiceProof) {
        this.invoiceProof = invoiceProof;
    }

    public String getAddressProof() {
        return addressProof;
    }

    public void setAddressProof(String addressProof) {
        this.addressProof = addressProof;
    }

    public String getSignProof() {
        return signProof;
    }

    public void setSignProof(String signProof) {
        this.signProof = signProof;
    }

    public String getAmountCollected() {
        return amountCollected;
    }

    public void setAmountCollected(String amountCollected) {
        this.amountCollected = amountCollected;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public PartialReq.FieldData getFieldData() {
        return fieldData;
    }

    public void setFieldData(PartialReq.FieldData fieldData) {
        this.fieldData = fieldData;
    }



}
