package com.inthree.boon.deliveryapp;


public class pendingModel {

    String sno;
    String orderId;
    String shipId;
    String image;
    String customerName;

    String invoice;



    boolean check;



    String invoicenumber;

    boolean isOffline;


    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    int order_type;


    public String getOrderpage() {
        return orderpage;
    }

    public void setOrderpage(String orderpage) {
        this.orderpage = orderpage;
    }

    String orderpage;

    public pendingModel() {};

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

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getInvoicenumber() {
        return invoicenumber;
    }

    public void setInvoicenumber(String invoicenumber) {
        this.invoicenumber = invoicenumber;
    }

    public boolean getCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }


}
