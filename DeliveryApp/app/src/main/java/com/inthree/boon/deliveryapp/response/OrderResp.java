package com.inthree.boon.deliveryapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inthree.boon.deliveryapp.request.OrderReq;

import java.util.ArrayList;


public class OrderResp {

    @SerializedName("details")
    @Expose
    private ArrayList<OrderResp> details = new ArrayList<>();

    public ArrayList<OrderResp> getDetails() {
        return details;
    }



  /*  @SerializedName("id")
    @Expose
    private String id;*/

    @SerializedName("reference")
    @Expose
    private String reference;

    @SerializedName("loanrefno")
    @Expose
    private String loanrefno;

    @SerializedName("invoice_no")
    @Expose
    private String invoiceNumber;

    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;

    @SerializedName("orderid")
    @Expose
    private String orderid;

    @SerializedName("res_msg")
    @Expose
    private String resMsg;

    @SerializedName("res_code")
    @Expose
    private String resCode;

    public String getVirtual_id() {
        return virtual_id;
    }

    public void setVirtual_id(String virtual_id) {
        this.virtual_id = virtual_id;
    }

    @SerializedName("virtual_id")
    @Expose
    private String virtual_id;

    @SerializedName("shipmentid")
    @Expose
    private String shipmentid;

    @SerializedName("processDefinitionCode")
    @Expose
    private String processDefinitionCode;

    @SerializedName("customer_name")
    @Expose
    private String customerName;

    @SerializedName("customer_contact_number")
    @Expose
    private String customerContactNumber;

    @SerializedName("alternate_contact_number")
    @Expose
    private String alternateContactNumber;

    @SerializedName("to_be_delivered_by")
    @Expose
    private String toBeDeliveredBy;

    @SerializedName("billing_address")
    @Expose
    private String billingAddress;

    @SerializedName("billing_city")
    @Expose
    private String billingCity;

    @SerializedName("billing_pincode")
    @Expose
    private String billingPincode;

    @SerializedName("billing_telephone")
    @Expose
    private String billingTelephone;

    @SerializedName("shipping_address")
    @Expose
    private String shippingAddress;

    @SerializedName("shipping_city")
    @Expose
    private String shippingCity;

    @SerializedName("shipping_pincode")
    @Expose
    private String shippingPincode;

    @SerializedName("shipping_telephone")
    @Expose
    private String shippingTelephone;

    public String getSlot_number() {
        return slot_number;
    }

    public void setSlot_number(String slot_number) {
        this.slot_number = slot_number;
    }

    @SerializedName("slot_number")
    @Expose
    private String slot_number;

    public String getBranch_address() {
        return branch_address;
    }

    public void setBranch_address(String branch_address) {
        this.branch_address = branch_address;
    }

    public String getBranch_pincode() {
        return branch_pincode;
    }

    public void setBranch_pincode(String branch_pincode) {
        this.branch_pincode = branch_pincode;
    }

    public String getBranch_contact_number() {
        return branch_contact_number;
    }

    public void setBranch_contact_number(String branch_contact_number) {
        this.branch_contact_number = branch_contact_number;
    }

    public String getGroup_leader_name() {
        return group_leader_name;
    }

    public void setGroup_leader_name(String group_leader_name) {
        this.group_leader_name = group_leader_name;
    }

    public String getGroup_leader_contact_number() {
        return group_leader_contact_number;
    }

    public void setGroup_leader_contact_number(String group_leader_contact_number) {
        this.group_leader_contact_number = group_leader_contact_number;
    }

    public String getClient_branch_name() {
        return client_branch_name;
    }

    public void setClient_branch_name(String client_branch_name) {
        this.client_branch_name = client_branch_name;
    }

    @SerializedName("client_branch_name")
    @Expose
    private String client_branch_name;

    @SerializedName("branch_address")
    @Expose
    private String branch_address;

    @SerializedName("branch_pincode")
    @Expose
    private String branch_pincode;

    @SerializedName("branch_contact_number")
    @Expose
    private String branch_contact_number;

    @SerializedName("group_leader_name")
    @Expose
    private String group_leader_name;

    @SerializedName("group_leader_contact_number")
    @Expose
    private String group_leader_contact_number;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;

    @SerializedName("item_code")
    @Expose
    private String itemCode;

    @SerializedName("flowCode")
    @Expose
    private String flowCode;

    @SerializedName("cityCode")
    @Expose
    private String cityCode;

    @SerializedName("branchCode")
    @Expose
    private String branchCode;

    @SerializedName("lmdp")
    @Expose
    private String lmdp;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("runner_id")
    @Expose
    private String runnerId;

    @SerializedName("assigned_at")
    @Expose
    private String downloadSync;

    @SerializedName("upload_sync")
    @Expose
    private String uploadSync;

    @SerializedName("sync_status")
    @Expose
    private String syncStatus;

    @SerializedName("attempt")
    @Expose
    private String attempt;


    @SerializedName("client_branch_code")
    @Expose
    private String clientBranchCode;

    @SerializedName("delivery_to")
    @Expose
    private String deliveryTo;


    @SerializedName("otp")
    @Expose
    private String otp;


    @SerializedName("urn")
    @Expose
    private String urn;


    public String getOrder_type() {
        return order_type;
    }

    public String getNeft() {
        return neft;
    }


    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public void setNeft(String neft) {
        this.neft = neft;
    }


    @SerializedName("order_type")
    @Expose
    private String order_type;


    public String getDelivery_aadhar_required() {
        return delivery_aadhar_required;
    }


    @SerializedName("max_attempt")
    @Expose
    private String max_attempt;

    public String getMax_attempt() {
        return max_attempt;
    }

    public void setMax_attempt(String max_attempt) {
        this.max_attempt = max_attempt;
    }


    public void setDelivery_aadhar_required(String delivery_aadhar_required) {
        this.delivery_aadhar_required = delivery_aadhar_required;
    }

    @SerializedName("neft")
    @Expose
    private String neft;

    @SerializedName("delivery_aadhar_required")
    @Expose
    private String delivery_aadhar_required;


    @SerializedName("return_id")
    @Expose
    private String returnId;





   /* public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getShipmentid() {
        return shipmentid;
    }

    public void setShipmentid(String shipmentid) {
        this.shipmentid = shipmentid;
    }

    public String getProcessDefinitionCode() {
        return processDefinitionCode;
    }

    public void setProcessDefinitionCode(String processDefinitionCode) {
        this.processDefinitionCode = processDefinitionCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContactNumber() {
        return customerContactNumber;
    }

    public void setCustomerContactNumber(String customerContactNumber) {
        this.customerContactNumber = customerContactNumber;
    }

    public String getAlternateContactNumber() {
        return alternateContactNumber;
    }

    public void setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
    }

    public String getToBeDeliveredBy() {
        return toBeDeliveredBy;
    }

    public void setToBeDeliveredBy(String toBeDeliveredBy) {
        this.toBeDeliveredBy = toBeDeliveredBy;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingPincode() {
        return billingPincode;
    }

    public void setBillingPincode(String billingPincode) {
        this.billingPincode = billingPincode;
    }

    public String getBillingTelephone() {
        return billingTelephone;
    }

    public void setBillingTelephone(String billingTelephone) {
        this.billingTelephone = billingTelephone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingPincode() {
        return shippingPincode;
    }

    public void setShippingPincode(String shippingPincode) {
        this.shippingPincode = shippingPincode;
    }

    public String getShippingTelephone() {
        return shippingTelephone;
    }

    public void setShippingTelephone(String shippingTelephone) {
        this.shippingTelephone = shippingTelephone;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getLmdp() {
        return lmdp;
    }

    public void setLmdp(String lmdp) {
        this.lmdp = lmdp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(String runnerId) {
        this.runnerId = runnerId;
    }

    public String getDownloadSync() {
        return downloadSync;
    }

    public void setDownloadSync(String downloadSync) {
        this.downloadSync = downloadSync;
    }

    public String getUploadSync() {
        return uploadSync;
    }

    public void setUploadSync(String uploadSync) {
        this.uploadSync = uploadSync;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRowTotal() {
        return rowTotal;
    }

    public void setRowTotal(String rowTotal) {
        this.rowTotal = rowTotal;
    }

  /*  public String getProduct_status() {
        return product_status;
    }

    public void setProduct_status(String product_status) {
        this.product_status = product_status;
    }*/

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }


    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    @SerializedName("products")
    @Expose
    private ArrayList<OrderResp> order = new ArrayList<>();

    public ArrayList<OrderResp> getOrder() {
        return order;
    }

    @SerializedName("id")
    @Expose
    private String pid;

    @SerializedName("transaction_id")
    @Expose
    private String transactionId;

    @SerializedName("sku")
    @Expose
    private String sku;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("qty_received")
    @Expose
    private String qty;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("row_total")
    @Expose
    private String rowTotal;

    public String getItem_json() {
        return item_json;
    }

    public void setItem_json(String item_json) {
        this.item_json = item_json;
    }

    @SerializedName("item_json")
    @Expose
    private String item_json;

  /*  @SerializedName("status")
    @Expose
    private String product_status;*/

    @SerializedName("flag")
    @Expose
    private String flag;

    @SerializedName("created")
    @Expose
    private String created;

    public String getLanguage_json() {
        return language_json;
    }

    public void setLanguage_json(String language_json) {
        this.language_json = language_json;
    }

    @SerializedName("language_json")
    @Expose
    private String language_json;

    public String getP_sku() {
        return p_sku;
    }

    public void setP_sku(String p_sku) {
        this.p_sku = p_sku;
    }

    public String getP_skuname() {
        return p_skuname;
    }

    public void setP_skuname(String p_skuname) {
        this.p_skuname = p_skuname;
    }

    public String getP_price() {
        return p_price;
    }

    public void setP_price(String p_price) {
        this.p_price = p_price;
    }

    public String getP_skuqty() {
        return p_skuqty;
    }

    public void setP_skuqty(String p_skuqty) {
        this.p_skuqty = p_skuqty;
    }

    public String getP_tamount() {
        return p_tamount;
    }

    public void setP_tamount(String p_tamount) {
        this.p_tamount = p_tamount;
    }

    public String getP_imei_number() {
        return p_imei_number;
    }

    public void setP_imei_number(String p_imei_number) {
        this.p_imei_number = p_imei_number;
    }

    @SerializedName("p_sku")
    @Expose
    private String p_sku;

    @SerializedName("p_skuname")
    @Expose
    private String p_skuname;

    @SerializedName("p_price")
    @Expose
    private String p_price;

    @SerializedName("p_skuqty")
    @Expose
    private String p_skuqty;

    @SerializedName("p_tamount")
    @Expose
    private String p_tamount;

    @SerializedName("p_imei_number")
    @Expose
    private String p_imei_number;

    public String getClientBranchCode() {
        return clientBranchCode;
    }

    public void setClientBranchCode(String clientBranchCode) {
        this.clientBranchCode = clientBranchCode;
    }

    public String getDeliveryTo() {
        return deliveryTo;
    }

    public void setDeliveryTo(String deliveryTo) {
        this.deliveryTo = deliveryTo;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getLoanrefno() {
        return loanrefno;
    }

    public void setLoanrefno(String loanrefno) {
        this.loanrefno = loanrefno;
    }
}