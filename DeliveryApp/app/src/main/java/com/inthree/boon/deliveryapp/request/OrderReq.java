package com.inthree.boon.deliveryapp.request;

import com.google.gson.annotations.SerializedName;

public class OrderReq {

    @SerializedName("id")
    private String id;

    @SerializedName("orderid")
    private String order_number;

    @SerializedName("shipment_number")
    private String shipment_number;

    @SerializedName("customer_name")
    private String customer_name;

    @SerializedName("customer_contact_number")
    private String customer_contact_number;

    @SerializedName("alternate_contact_number")
    private String alternate_contact_number;

    @SerializedName("to_be_delivered_by")
    private String to_be_delivered_by;

    @SerializedName("billing_address")
    private String billing_address;

    @SerializedName("billing_city")
    private String billing_city;

    @SerializedName("billing_pincode")
    private String billing_pincode;

    @SerializedName("billing_telephone")
    private String billing_telephone;

    @SerializedName("shipping_address")
    private String shipping_address;

    @SerializedName("shipping_city")
    private String shipping_city;

    @SerializedName("shipping_pincode")
    private String shipping_pincode;

    @SerializedName("shipping_telephone")
    private String shipping_telephone;

    @SerializedName("invoice_amount")
    private String invoice_amount;

    @SerializedName("payment_mode")
    private String payment_mode;

    @SerializedName("client_branch_name")
    private String client_branch_name;

    @SerializedName("branch_address")
    private String branch_address;

    @SerializedName("branch_pincode")
    private String branch_pincode;

    @SerializedName("branch_contact_number")
    private String branch_contact_number;

    @SerializedName("group_leader_name")
    private String group_leader_name;

    @SerializedName("group_leader_contact_number")
    private String group_leader_contact_number;

    @SerializedName("slot_number")
    private String slot_number;

    @SerializedName("product_name")
    private String  product_name;

    @SerializedName("quantity")
    private String quantity;

    @SerializedName("amount")
    private String amount;

    @SerializedName("product_code")
    private String product_code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getShipment_number() {
        return shipment_number;
    }

    public void setShipment_number(String shipment_number) {
        this.shipment_number = shipment_number;
    }
    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_contact_number() {
        return customer_contact_number;
    }

    public void setCustomer_contact_number(String customer_contact_number) {
        this.customer_contact_number = customer_contact_number;
    }

    public String getAlternate_contact_number() {
        return alternate_contact_number;
    }

    public void setAlternate_contact_number(String alternate_contact_number) {
        this.alternate_contact_number = alternate_contact_number;
    }

    public String getTo_be_delivered_by() {
        return to_be_delivered_by;
    }

    public void setTo_be_delivered_by(String to_be_delivered_by) {
        this.to_be_delivered_by = to_be_delivered_by;
    }

    public String getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(String billing_address) {
        this.billing_address = billing_address;
    }

    public String getBilling_city() {
        return billing_city;
    }

    public void setBilling_city(String billing_city) {
        this.billing_city = billing_city;
    }

    public String getBilling_pincode() {
        return billing_pincode;
    }

    public void setBilling_pincode(String billing_pincode) {
        this.billing_pincode = billing_pincode;
    }

    public String getBilling_telephone() {
        return billing_telephone;
    }

    public void setBilling_telephone(String billing_telephone) {
        this.billing_telephone = billing_telephone;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getShipping_city() {
        return shipping_city;
    }

    public void setShipping_city(String shipping_city) {
        this.shipping_city = shipping_city;
    }

    public String getShipping_pincode() {
        return shipping_pincode;
    }

    public void setShipping_pincode(String shipping_pincode) {
        this.shipping_pincode = shipping_pincode;
    }

    public String getShipping_telephone() {
        return shipping_telephone;
    }

    public void setShipping_telephone(String shipping_telephone) {
        this.shipping_telephone = shipping_telephone;
    }

    public String getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(String invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getClient_branch_name() {
        return client_branch_name;
    }

    public void setClient_branch_name(String client_branch_name) {
        this.client_branch_name = client_branch_name;
    }

    public String getBranch_address() {
        return branch_address;
    }

    public void setBranch_address(String branch_address) {
        this.branch_address = branch_address;
    }

    public String getBranch_pincode() {
        return branch_pincode;
    }

    public void setBranch_pincode(String branch_pincode) {
        this.branch_pincode = branch_pincode;
    }

    public String getBranch_contact_number() {
        return branch_contact_number;
    }

    public void setBranch_contact_number(String branch_contact_number) {
        this.branch_contact_number = branch_contact_number;
    }

    public String getGroup_leader_name() {
        return group_leader_name;
    }

    public void setGroup_leader_name(String group_leader_name) {
        this.group_leader_name = group_leader_name;
    }

    public String getGroup_leader_contact_number() {
        return group_leader_contact_number;
    }

    public void setGroup_leader_contact_number(String group_leader_contact_number) {
        this.group_leader_contact_number = group_leader_contact_number;
    }

    public String getSlot_number() {
        return slot_number;
    }

    public void setSlot_number(String slot_number) {
        this.slot_number = slot_number;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

//    public OrderReq(String id) {
//        this.id = id;
//
//    }

}


