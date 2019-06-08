package com.inthree.boon.deliveryapp.model;

/**
 * Created by karthika on 12-Jan-18.
 */

public class ParcialShowModel {

    String pcode;
    String pname;
    String pqty;
    String pamt;
    String total;

    public boolean isOther_partial_reason() {
        return other_partial_reason;
    }

    public void setOther_partial_reason(boolean other_partial_reason) {
        this.other_partial_reason = other_partial_reason;
    }

    boolean other_partial_reason =false;


    public String getShip_no() {
        return ship_no;
    }

    public void setShip_no(String ship_no) {
        this.ship_no = ship_no;
    }

    String ship_no;

    public String getPartial_reason() {
        return partial_reason;
    }

    public void setPartial_reason(String partial_reason) {
        this.partial_reason = partial_reason;
    }

    String partial_reason = "";
    String deliveryQty="0";

    public ParcialShowModel(){};


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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        if(total == ""){
            total="0";
        }
        this.total = total;
    }

    public String getDeliveryQty() {
        return deliveryQty;
    }

    public void setDeliveryQty(String deliveryQty) {
        this.deliveryQty = deliveryQty;
    }

}
