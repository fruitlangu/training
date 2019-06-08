package com.inthree.boon.deliveryapp.request;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PartialReq {

    @SerializedName("runner_id")
    @Expose
    private String runner_id;

    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("customer")
    @Expose
    private String customer;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("landmark")
    @Expose
    private String landmark;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("deliveryproof")
    @Expose
    private String deliveryproof;
    @SerializedName("invoiceproof")
    @Expose
    private String invoiceproof;



    @SerializedName("relationproof")
    @Expose
    private String relationproof;

    @SerializedName("addressproof")
    @Expose
    private String addressproof;
    @SerializedName("signproof")
    @Expose
    private String signproof;
    @SerializedName("shipmentnumber")
    @Expose
    private String shipmentnumber;
    @SerializedName("order_no")
    @Expose
    private String orderNo;


    @SerializedName("feedback")
    @Expose
    private String feedback;


    @SerializedName("verify")
    @Expose
    private String verify;

    public String getProof_type() {
        return proof_type;
    }

    public void setProof_type(String proof_type) {
        this.proof_type = proof_type;
    }

    @SerializedName("proof_type")
    @Expose
    private String proof_type;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @SerializedName("reason")
    @Expose
    private String reason;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @SerializedName("phone")
    @Expose
    private String phone;

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    @SerializedName("attempt")
    @Expose
    private int attempt;

    @SerializedName("fieldData")
    @Expose
    private FieldData fieldData;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    @SerializedName("mode_type")
    @Expose
    private String modeType;

    public String getAmount_tot() {
        return amount_tot;
    }

    public void setAmount_tot(String amount_tot) {
        this.amount_tot = amount_tot;
    }

    @SerializedName("amount_tot")
    @Expose
    private String amount_tot;

    @SerializedName("transaction_num")
    @Expose
    private String transactionNum;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("receipt")
    @Expose
    private String receipt;

    @SerializedName("original_amount")
    @Expose
    private String originalAmount;
    @SerializedName("actual_amount")
    @Expose
    private String actualAmount;

    @SerializedName("aadhaar_details")
    @Expose
    private String aadhaarDetails;


    @SerializedName("redirect")
    @Expose
    private String redirect;

    @SerializedName("receiveBy")
    @Expose
    private String received_by;


    public String getRunner_id() {
        return runner_id;
    }

    public void setRunner_id(String runner_id) {
        this.runner_id = runner_id;
    }

    @SerializedName("details")
    @Expose
    private List<PartialReq> details = null;

    public String getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(String originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public List<PartialReq> getDetails() {
        return details;
    }

    public void setDetails(List<PartialReq> details) {
        this.details = details;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }


    public String getTransactionNum() {
        return transactionNum;
    }

    public void setTransactionNum(String transactionNum) {
        this.transactionNum = transactionNum;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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

    public String getDeliveryproof() {
        return deliveryproof;
    }

    public void setDeliveryproof(String deliveryproof) {
        this.deliveryproof = deliveryproof;
    }

    public String getInvoiceproof() {
        return invoiceproof;
    }

    public void setInvoiceproof(String invoiceproof) {
        this.invoiceproof = invoiceproof;
    }

    public String getAddressproof() {
        return addressproof;
    }

    public void setAddressproof(String addressproof) {
        this.addressproof = addressproof;
    }


    public String getRelationproof() {
        return relationproof;
    }

    public void setRelationproof(String relationproof) {
        this.relationproof = relationproof;
    }


    public String getSignproof() {
        return signproof;
    }

    public void setSignproof(String signproof) {
        this.signproof = signproof;
    }

    public String getShipmentnumber() {
        return shipmentnumber;
    }

    public void setShipmentnumber(String shipmentnumber) {
        this.shipmentnumber = shipmentnumber;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAadhaarDetails() {
        return aadhaarDetails;
    }

    public void setAadhaarDetails(String aadhaarDetails) {
        this.aadhaarDetails = aadhaarDetails;
    }

    public FieldData getFieldData() {
        return fieldData;
    }

    public void setFieldData(FieldData fieldData) {
        this.fieldData = fieldData;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getReceived_by() {
        return received_by;
    }

    public void setReceived_by(String received_by) {
        this.received_by = received_by;
    }

    public static class FieldData {

        @SerializedName("amount_collected")
        @Expose
        private String amountCollected;


        public String getAmountCollected() {
            return amountCollected;
        }

        public void setAmountCollected(String amountCollected) {
            this.amountCollected = amountCollected;
        }


        @SerializedName("itemcode")
        @Expose
        private List<PartialReq> itemcode = null;

        public List<PartialReq> getItemcode() {
            return itemcode;
        }

        public void setItemcode(List<PartialReq> itemcode) {
            this.itemcode = itemcode;
        }

        @SerializedName("sku_actual_qty")
        @Expose
        private String skuActualQty;
        @SerializedName("product_code")
        @Expose
        private String productCode;
        @SerializedName("product_name")
        @Expose
        private String productName;
        @SerializedName("quantity")
        @Expose
        private String quantity;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        @SerializedName("amount")
        @Expose
        private String amount;

        public String getSkuActualQty() {
            return skuActualQty;
        }

        public void setSkuActualQty(String skuActualQty) {
            this.skuActualQty = skuActualQty;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

    }
}






