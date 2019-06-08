package com.inthree.boon.deliveryapp.model;

public class ServiceConfirm {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShip_num() {
        return ship_num;
    }

    public void setShip_num(String ship_num) {
        this.ship_num = ship_num;
    }

    public String getCustomer_fname() {
        return customer_fname;
    }

    public void setCustomer_fname(String customer_fname) {
        this.customer_fname = customer_fname;
    }

    public String getCustomer_cnum() {
        return customer_cnum;
    }

    public void setCustomer_cnum(String customer_cnum) {
        this.customer_cnum = customer_cnum;
    }

    public String getShip_address() {
        return ship_address;
    }

    public void setShip_address(String ship_address) {
        this.ship_address = ship_address;
    }

    public String getShip_city() {
        return ship_city;
    }

    public void setShip_city(String ship_city) {
        this.ship_city = ship_city;
    }

    public String getShip_phone() {
        return ship_phone;
    }

    public void setShip_phone(String ship_phone) {
        this.ship_phone = ship_phone;
    }

    public String getCustomer_feedback() {
        return customer_feedback;
    }

    public void setCustomer_feedback(String customer_feedback) {
        this.customer_feedback = customer_feedback;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

  int id;
  String ship_num ="";
  String customer_fname="";

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    String product_name="";

    public String getProduct_sku() {
        return product_sku;
    }

    public void setProduct_sku(String product_sku) {
        this.product_sku = product_sku;
    }

    String product_sku;
  String customer_cnum;
  String ship_address;
  String ship_city;
  String ship_phone;
  String customer_feedback;

    public String getShip_pincode() {
        return ship_pincode;
    }

    public void setShip_pincode(String ship_pincode) {
        this.ship_pincode = ship_pincode;
    }

    String ship_pincode;
  String  created_date;
  String function_confirm = "No";
    String document_confirm = "No";
    String feedback_confirm = "No";

    public String getSignProof() {
        return signProof;
    }

    public void setSignProof(String signProof) {
        this.signProof = signProof;
    }

    String signProof = "";

    public String getFunction_confirm() {
        return function_confirm;
    }

    public void setFunction_confirm(String function_confirm) {
        this.function_confirm = function_confirm;
    }

    public String getDocument_confirm() {
        return document_confirm;
    }

    public void setDocument_confirm(String document_confirm) {
        this.document_confirm = document_confirm;
    }

    public String getFeedback_confirm() {
        return feedback_confirm;
    }

    public void setFeedback_confirm(String feedback_confirm) {
        this.feedback_confirm = feedback_confirm;
    }




}
