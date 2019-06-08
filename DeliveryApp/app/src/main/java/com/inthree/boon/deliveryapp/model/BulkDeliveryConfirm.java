package com.inthree.boon.deliveryapp.model;


import android.widget.LinearLayout;

/**
 * This is the model class for delivery confirmation
 */
public class BulkDeliveryConfirm {
    /**
     * Get the shipment number
     */
    private String shipmentNumber = "";

    /**
     * Store the customer name
     */
    private String customerName = "";



    private String shipAddress="";


    /**
     * Store the contact number
     */
    private String customerContactNumber = "";

    /**
     * Store the amount collected
     */
    private String amountCollected = "";

    /**
     * Store the pincode
     */
    private String pincode = "";

    /**
     * Store the idproof
     */
    private String idProff = "";


    /**
     * Store the address proof
     */
    private String deliveryProof = "";

    /**
     * Store the invoice proof
     */
    private String invoiceProof = "";

    /**
     * Store the invoice proof
     */
    private String relationProof = "";

    /**
     * Store the signature proof
     */
    private String signatureProof = "";

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    private String landMark;

    /**
     * Store the signature proof
     */
    private String latitude = "";

    /**
     * Store the signature proof
     */
    private String longitude = "";



    private String redirect="0";


    /**
     * Store the adhaar details by using QR
     */
    private String adhaarDetails="";



    private String feed_back="";



    private String verify="";




    private String otherSelfType="";

    public String getOtherRelationName() {
        return otherRelationName;
    }

    public void setOtherRelationName(String otherRelationName) {
        this.otherRelationName = otherRelationName;
    }

    private String otherRelationName="";


    private String otp="";
    private String urn="";


    public String getPickup_status() {
        return pickup_status;
    }

    public String getNeft() {
        return neft;
    }



    public void setPickup_status(String pickup_status) {
        this.pickup_status = pickup_status;
    }

    public void setNeft(String neft) {
        this.neft = neft;
    }



    public String getPickup_image() {
        return pickup_image;
    }

    private String neft="";



    public void setPickup_image(String pickup_image) {
        this.pickup_image = pickup_image;
    }

    public String getPickup_check() {
        return pickup_check;
    }

    public void setPickup_check(String pickup_check) {
        this.pickup_check = pickup_check;
    }

    private String pickup_status;
    private String pickup_image = "";
    private String pickup_check;



    public String getVoterOrAadhar() {
        return voterOrAadhar;
    }

    public void setVoterOrAadhar(String voterOrAadhar) {
        this.voterOrAadhar = voterOrAadhar;
    }

    private String voterOrAadhar="";

    public String getVoterOrAadharType() {
        return voterOrAadharType;
    }

    public void setVoterOrAadharType(String voterOrAadharType) {
        this.voterOrAadharType = voterOrAadharType;
    }

    private String voterOrAadharType="";




    public BulkDeliveryConfirm()
    {

    }


    public BulkDeliveryConfirm(String shipmentNumber,String customerContactNumber,String amountCollected,String
            pincode,String idProff,String deliveryProof,String  invoiceProof,String signatureProof){
        this.shipmentNumber=shipmentNumber;
        this.customerContactNumber = customerContactNumber;
        this.amountCollected = amountCollected;
        this.pincode = pincode;
        this.idProff = idProff;
        this.deliveryProof = deliveryProof;
        this.invoiceProof = invoiceProof;
        this.signatureProof = signatureProof;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }
    public String getFeed_back() {
        return feed_back;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public void setFeed_back(String feed_back) {
        this.feed_back = feed_back;
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

    public String getCustomerContactNumber() {
        return customerContactNumber;
    }

    public void setCustomerContactNumber(String customerContactNumber) {
        this.customerContactNumber = customerContactNumber;
    }

    public String getAmountCollected() {
        return amountCollected;
    }

    public void setAmountCollected(String amountCollected) {
        this.amountCollected = amountCollected;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getIdProff() {
        return idProff;
    }

    public void setIdProff(String idProff) {
        this.idProff = idProff;
    }

    public String getInvoiceProof() {
        return invoiceProof;
    }

    public void setInvoiceProof(String invoiceProof) {
        this.invoiceProof = invoiceProof;
    }

    public String getSignatureProof() {
        return signatureProof;
    }

    public void setSignatureProof(String signatureProof) {
        this.signatureProof = signatureProof;
    }

    public String getDeliveryProof() {
        return deliveryProof;
    }

    public void setDeliveryProof(String deliveryProof) {
        this.deliveryProof = deliveryProof;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getAdhaarDetails() {
        return adhaarDetails;
    }

    public void setAdhaarDetails(String adhaarDetails) {
        this.adhaarDetails = adhaarDetails;
    }

    public String getRelationProof() {
        return relationProof;
    }

    public void setRelationProof(String relationProof) {
        this.relationProof = relationProof;
    }

    public String getOtherSelfType() {
        return otherSelfType;
    }

    public void setOtherSelfType(String otherSelfType) {
        this.otherSelfType = otherSelfType;
    }
}
