package com.inthree.boon.deliveryapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServiceOrderResp {

    @SerializedName("details")
    @Expose
    private ArrayList<ServiceOrderResp> details = new ArrayList<>();

    public ArrayList<ServiceOrderResp> getServiceItems() {
        return details;
    }


    @SerializedName("reference")
    @Expose
    private String reference;

    @SerializedName("order_id")
    @Expose
    private String order_id;

    @SerializedName("shipment_id")
    @Expose
    private String shipment_id;

    @SerializedName("customer_name")
    @Expose
    private String customer_name;

    @SerializedName("customer_contact_number")
    @Expose
    private String customer_contact_number;

    @SerializedName("alternate_contact_number")
    @Expose
    private String alternate_contact_number;

    @SerializedName("shipping_address")
    @Expose
    private String shipping_address;

    @SerializedName("shipping_city")
    @Expose
    private String shipping_city;

    @SerializedName("shipping_pincode")
    @Expose
    private String shipping_pincode;

    @SerializedName("shipping_telephone")
    @Expose
    private String shipping_telephone;

    @SerializedName("cityCode")
    @Expose
    private String cityCode;

    @SerializedName("lmp_code")
    @Expose
    private String lmp_code;

    @SerializedName("agent_id")
    @Expose
    private int agent_id;

    @SerializedName("sync_status")
    @Expose
    private int sync_status;

    @SerializedName("assigned_at")
    @Expose
    private String assigned_at;

    @SerializedName("received_at")
    @Expose
    private String received_at;

    @SerializedName("download_sync")
    @Expose
    private String download_sync;

    @SerializedName("dio_status")
    @Expose
    private int dio_status;

    @SerializedName("attempt")
    @Expose
    private int attempt;

    @SerializedName("reason")
    @Expose
    private String reason;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(String shipment_id) {
        this.shipment_id = shipment_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_contact_number() {
        return customer_contact_number;
    }

    public void setCustomer_contact_number(String customer_contact_number) {
        this.customer_contact_number = customer_contact_number;
    }

    public String getAlternate_contact_number() {
        return alternate_contact_number;
    }

    public void setAlternate_contact_number(String alternate_contact_number) {
        this.alternate_contact_number = alternate_contact_number;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getShipping_city() {
        return shipping_city;
    }

    public void setShipping_city(String shipping_city) {
        this.shipping_city = shipping_city;
    }

    public String getShipping_pincode() {
        return shipping_pincode;
    }

    public void setShipping_pincode(String shipping_pincode) {
        this.shipping_pincode = shipping_pincode;
    }

    public String getShipping_telephone() {
        return shipping_telephone;
    }

    public void setShipping_telephone(String shipping_telephone) {
        this.shipping_telephone = shipping_telephone;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getLmp_code() {
        return lmp_code;
    }

    public void setLmp_code(String lmp_code) {
        this.lmp_code = lmp_code;
    }

    public int getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(int agent_id) {
        this.agent_id = agent_id;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }

    public String getAssigned_at() {
        return assigned_at;
    }

    public void setAssigned_at(String assigned_at) {
        this.assigned_at = assigned_at;
    }

    public String getReceived_at() {
        return received_at;
    }

    public void setReceived_at(String received_at) {
        this.received_at = received_at;
    }

    public String getDownload_sync() {
        return download_sync;
    }

    public void setDownload_sync(String download_sync) {
        this.download_sync = download_sync;
    }

    public int getDio_status() {
        return dio_status;
    }

    public void setDio_status(int dio_status) {
        this.dio_status = dio_status;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }

    @SerializedName("res_msg")
    private String res_msg;


    @SerializedName("products_items")
    @Expose
    private ArrayList<ServiceOrderResp> service_prod = new ArrayList<>();

    public ArrayList<ServiceOrderResp> getServiceDetails() {
        return service_prod;
    }


    @SerializedName("products_attributes")
    @Expose
    private ArrayList<ServiceOrderResp> service_attributes = new ArrayList<>();

    public ArrayList<ServiceOrderResp> getServiceAttributes() {
        return service_attributes;
    }

    @SerializedName("ship_no")
    @Expose
    private String ship_no;

    public String getShip_no() {
        return ship_no;
    }

    public void setShip_no(String ship_no) {
        this.ship_no = ship_no;
    }

    public String getProd_sku() {
        return prod_sku;
    }

    public void setProd_sku(String prod_sku) {
        this.prod_sku = prod_sku;
    }

    @SerializedName("prod_sku")
    @Expose
    private String prod_sku;

    @SerializedName("attribute_id")
    @Expose
    private String attribute_id;

    @SerializedName("attribute_type")
    @Expose
    private String attribute_type;

    @SerializedName("attribute_code")
    @Expose
    private String attribute_code;

    @SerializedName("attribute_name")
    @Expose
    private String attribute_name;

    @SerializedName("input_field_type")
    @Expose
    private String input_field_type;

    @SerializedName("is_require")
    @Expose
    private String is_require;


    public String getAttri_type() {
        return attri_type;
    }

    public void setAttri_type(String attri_type) {
        this.attri_type = attri_type;
    }

    @SerializedName("attri_type")
    @Expose
    private String attri_type;

    public String getAttribute_id() {
        return attribute_id;
    }

    public void setAttribute_id(String attribute_id) {
        this.attribute_id = attribute_id;
    }

    public String getAttribute_type() {
        return attribute_type;
    }

    public void setAttribute_type(String attribute_type) {
        this.attribute_type = attribute_type;
    }

    public String getAttribute_code() {
        return attribute_code;
    }

    public void setAttribute_code(String attribute_code) {
        this.attribute_code = attribute_code;
    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    public String getInput_field_type() {
        return input_field_type;
    }

    public void setInput_field_type(String input_field_type) {
        this.input_field_type = input_field_type;
    }

    public String getIs_require() {
        return is_require;
    }

    public void setIs_require(String is_require) {
        this.is_require = is_require;
    }

    public String getProduct_type_id() {
        return product_type_id;
    }

    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    @SerializedName("product_type_id")
    @Expose
    private String product_type_id;




    @SerializedName("service_id")
    @Expose
    private String service_id;

    @SerializedName("sku")
    @Expose
    private String sku;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("qty")
    @Expose
    private String qty;

    @SerializedName("item_received")
    @Expose
    private String item_received;

    @SerializedName("qty_demo_completed")
    @Expose
    private String qty_demo_completed;

    @SerializedName("order_item_id")
    @Expose
    private String order_item_id;

    @SerializedName("product_serial_no")
    @Expose
    private String product_serial_no;

    @SerializedName("product_type")
    @Expose
    private String product_type;

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getItem_received() {
        return item_received;
    }

    public void setItem_received(String item_received) {
        this.item_received = item_received;
    }

    public String getQty_demo_completed() {
        return qty_demo_completed;
    }

    public void setQty_demo_completed(String qty_demo_completed) {
        this.qty_demo_completed = qty_demo_completed;
    }

    public String getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(String order_item_id) {
        this.order_item_id = order_item_id;
    }

    public String getProduct_serial_no() {
        return product_serial_no;
    }

    public void setProduct_serial_no(String product_serial_no) {
        this.product_serial_no = product_serial_no;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }



}
