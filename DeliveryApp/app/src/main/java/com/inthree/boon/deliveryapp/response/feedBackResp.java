package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class feedBackResp {
    @SerializedName("feedback_val")
    @Expose
    private List<FeedbackVal> feedbackVal = null;
    @SerializedName("res_msg")
    @Expose
    private String resMsg;
    @SerializedName("res_code")
    @Expose
    private Integer resCode;


    public List<FeedbackVal> getFeedbackVal() {
        return feedbackVal;
    }

    public void setFeedbackVal(List<FeedbackVal> feedbackVal) {
        this.feedbackVal = feedbackVal;
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
