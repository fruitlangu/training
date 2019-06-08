package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceIncompleteResp {
    @SerializedName("reason_val")
    @Expose
    private List<ReasonVal> reasonVal = null;
    @SerializedName("res_msg")
    @Expose
    private String resMsg;
    @SerializedName("res_code")
    @Expose
    private Integer resCode;

    public List<ReasonVal> getReasonVal() {
        return reasonVal;
    }

    public void setReasonVal(List<ReasonVal> reasonVal) {
        this.reasonVal = reasonVal;
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
