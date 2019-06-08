package com.inthree.boon.deliveryapp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;

public class LoginResp {

    @SerializedName("res_msg")
    @Expose
    private String resMsg;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("user_role")
    @Expose
    private String role;
    @SerializedName("last_ip")
    @Expose
    private String lastIp;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public String getAadhaar_feature() {
        return aadhaar_feature;
    }

    public void setAadhaar_feature(String aadhaar_feature) {
        this.aadhaar_feature = aadhaar_feature;
    }

    @SerializedName("aadhaar_feature")
    @Expose
    private String aadhaar_feature;

    @SerializedName("language")
    @Expose
    private ArrayList<LoginResp> languageArr = new ArrayList<>();

    public ArrayList<LoginResp> getLanguageArray() {
        return languageArr;
    }


    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @SerializedName("language_id")
    @Expose
    private String languageId;

    @SerializedName("name")
    @Expose
    private String language;

    public String getLanguage_active() {
        return language_active;
    }

    public void setLanguage_active(String language_active) {
        this.language_active = language_active;
    }

    @SerializedName("is_active")
    @Expose
    private String language_active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }


    @SerializedName("login")
    @Expose
    private ArrayList<LoginResp> loginUser = new ArrayList<>();

    public ArrayList<LoginResp> getLogin() {
        return loginUser;
    }
}
