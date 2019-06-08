package com.inthree.boon.deliveryapp.model;



public class CheckListModel {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback_status() {
        return feedback_status;
    }

    public void setFeedback_status(String feedback_status) {
        this.feedback_status = feedback_status;
    }

    private int id;
    private String feedback;
    private String feedback_status;

    public CheckListModel(String feedback, String feedback_status){
        this.feedback = feedback;
        this.feedback_status = feedback_status;
    }

}



