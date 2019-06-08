package com.inthree.boon.deliveryapp.model;

/**
 * Created by karthika on 12-Jan-18.
 */

public class ProductShowModel {

    String pcode;
    String pname;
    String pqty;
    String pamt;
   // String total="";
   String deliveryQty="";

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    String order_type;

    public int getPickupStatus() {
        return pickupStatus;
    }

    public void setPickupStatus(int pickupStatus) {
        this.pickupStatus = pickupStatus;
    }

    int pickupStatus;

    public ProductShowModel(){};


    public String getpcode() {
        return pcode;
    }

    public void setpcode(String pcode) {
        this.pcode = pcode;
    }

    public String getpname() {
        return pname;
    }

    public void setpname(String pname) {
        this.pname = pname;
    }

    public String getpqty() {
        return pqty;
    }

    public void setpqty(String pqty) {
        this.pqty = pqty;
    }

    public String getpamt() {
        return pamt;
    }

    public void setpamt(String pamt) {
        this.pamt = pamt;
    }

  /*  public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }*/

    public String getDeliveryQty() {
        return deliveryQty;
    }

    public void setDeliveryQty(String deliveryQty) {
        this.deliveryQty = deliveryQty;
    }

}
