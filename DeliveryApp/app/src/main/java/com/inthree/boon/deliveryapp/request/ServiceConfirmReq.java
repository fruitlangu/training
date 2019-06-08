package com.inthree.boon.deliveryapp.request;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceConfirmReq {


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

    String ship_num;

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    String order_num;
    String customer_fname;
    String customer_cnum;
    String ship_address;
    String ship_city;
    String ship_phone;
    String customer_feedback;
    String created_date;

    public String getShippincode() {
        return shippincode;
    }

    public void setShippincode(String shippincode) {
        this.shippincode = shippincode;
    }

    String shippincode;

    public String getSignProof() {
        return signProof;
    }

    public void setSignProof(String signProof) {
        this.signProof = signProof;
    }

    String signProof;

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    String agent_id;

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    String referenceNo;

    public int getAttempt_count() {
        return attempt_count;
    }

    public void setAttempt_count(int attempt_count) {
        this.attempt_count = attempt_count;
    }

    int attempt_count;


}
