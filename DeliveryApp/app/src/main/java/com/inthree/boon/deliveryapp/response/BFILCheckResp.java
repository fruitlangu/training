package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inthree.boon.deliveryapp.model.BranchVal;

import java.util.ArrayList;

public class BFILCheckResp {





    @SerializedName("LoanProposalID")
    @Expose
    private String loanProposalID;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Remarks")
    @Expose
    private String remarks;

    public String getLoanProposalID() {
        return loanProposalID;
    }

    public void setLoanProposalID(String loanProposalID) {
        this.loanProposalID = loanProposalID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }



}
