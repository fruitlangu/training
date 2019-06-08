package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inthree.boon.deliveryapp.model.BranchVal;

import java.util.ArrayList;
import java.util.List;

public class BranchResp {

    @SerializedName("branch_val")
    @Expose
    private ArrayList<BranchVal> branchVal = null;
    @SerializedName("res_msg")
    @Expose
    private String resMsg;
    @SerializedName("res_code")
    @Expose
    private Integer resCode;

    public ArrayList<BranchVal> getBranchVal() {
        return branchVal;
    }

    public void setBranchVal(ArrayList<BranchVal> branchVal) {
        this.branchVal = branchVal;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }
}
