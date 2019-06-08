package com.inthree.boon.deliveryapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This is the model class for barcode data which is used to store the values
 */
public class PrintLetterBarcodeData {
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("lm")
    @Expose
    private String lm;
    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("gname")
    @Expose
    private String gname;
    @SerializedName("house")
    @Expose
    private String house;
    @SerializedName("subdist")
    @Expose
    private String subdist;
    @SerializedName("co")
    @Expose
    private String co;
    @SerializedName("dob")
    @Expose
    private String dob=null;
    @SerializedName("pc")
    @Expose
    private String pc;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("yob")
    @Expose
    private int yob;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("po")
    @Expose
    private String po;
    @SerializedName("vtc")
    @Expose
    private String vtc;
    @SerializedName("dist")
    @Expose
    private String dist;

    /**
     * Get the details of bar code data
     */
    public PrintLetterBarcodeData(){
        //Default arguments
    }

    /**
     *
     * @param name
     * @param dob
     * @param street
     * @param state
     * @param po
     * @param pc
     */
   public PrintLetterBarcodeData(String name,String dob,String street,String state,String po,String pc){
       this.name=name;
       this.dob=dob;
       this.street=street;
       this.state=state;
       this.po=po;
       this.pc=pc;
   }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLm() {
        return lm;
    }

    public void setLm(String lm) {
        this.lm = lm;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getSubdist() {
        return subdist;
    }

    public void setSubdist(String subdist) {
        this.subdist = subdist;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYob() {
        return yob;
    }

    public void setYob(Integer yob) {
        this.yob = yob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getVtc() {
        return vtc;
    }

    public void setVtc(String vtc) {
        this.vtc = vtc;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

}
