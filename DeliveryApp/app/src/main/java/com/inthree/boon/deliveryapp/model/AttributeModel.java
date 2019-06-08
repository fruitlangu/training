package com.inthree.boon.deliveryapp.model;



public class AttributeModel {

    public String getText_value() {
        return text_value;
    }

    public void setText_value(String text_value) {
        this.text_value = text_value;
    }

    String text_value;

    boolean isSelected;

    public String getShipment_no() {
        return shipment_no;
    }

    public void setShipment_no(String shipment_no) {
        this.shipment_no = shipment_no;
    }

    String shipment_no;

    public String getAttribute_name() {
        return attribute_name;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    String attribute_name;

    public String getAttribute_id() {
        return attribute_id;
    }

    public void setAttribute_id(String attribute_id) {
        this.attribute_id = attribute_id;
    }

    String attribute_id;

    public String getInput_field_type() {
        return input_field_type;
    }

    public void setInput_field_type(String input_field_type) {
        this.input_field_type = input_field_type;
    }

    String input_field_type;

    public String getText_content() {
        return text_content;
    }

    public void setText_content(String text_content) {
        this.text_content = text_content;
    }

    String text_content;


    public AttributeModel(boolean isSelected, String shipno, String attribute_name,String attribute_id,String input_type) {
        this.isSelected = isSelected;
        this.shipment_no = shipno;
        this.attribute_name = attribute_name;
        this.attribute_id = attribute_id;
        this.input_field_type = input_type;
    }

    public AttributeModel() {

    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}



