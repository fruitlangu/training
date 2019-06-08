package com.inthree.boon.deliveryapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BranchVal {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("contactname")
    @Expose
    private String contactname;
    @SerializedName("contactno")
    @Expose
    private String contactno;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("lmp")
    @Expose
    private Object lmp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactname() {
        return contactname;
    }

    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getLmp() {
        return lmp;
    }

    public void setLmp(Object lmp) {
        this.lmp = lmp;
    }

    @Override
    public String toString() {
        return name;
    }
}
