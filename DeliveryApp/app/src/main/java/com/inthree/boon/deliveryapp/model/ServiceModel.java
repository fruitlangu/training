package com.inthree.boon.deliveryapp.model;


public class ServiceModel {

    String sno;
    String orderId;
    String shipId;
    String image;
    String customerName;
    boolean isOffline;


    public String getOrderpage() {
        return orderpage;
    }

    public void setOrderpage(String orderpage) {
        this.orderpage = orderpage;
    }

    String orderpage;

    public ServiceModel() {};

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getShipId() {
        return shipId;
    }

    public void setShipId(String shipId) {
        this.shipId = shipId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

}
