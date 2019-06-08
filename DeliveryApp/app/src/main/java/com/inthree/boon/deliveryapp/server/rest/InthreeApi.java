package com.inthree.boon.deliveryapp.server.rest;


import com.inthree.boon.deliveryapp.model.ServiceConfirmResp;
import com.inthree.boon.deliveryapp.model.ServiceOrderResp;
import com.inthree.boon.deliveryapp.response.AttemptResp;
import com.inthree.boon.deliveryapp.response.BFILCheckResp;
import com.inthree.boon.deliveryapp.response.BranchResp;
import com.inthree.boon.deliveryapp.response.DeliveryConfirmResp;
import com.inthree.boon.deliveryapp.response.ImageSyncResp;
import com.inthree.boon.deliveryapp.response.LoginResp;
import com.inthree.boon.deliveryapp.response.OrderChangeResp;
import com.inthree.boon.deliveryapp.response.OrderResp;
import com.inthree.boon.deliveryapp.response.OtpResp;
import com.inthree.boon.deliveryapp.response.PartialResp;
import com.inthree.boon.deliveryapp.response.ReasonResp;
import com.inthree.boon.deliveryapp.response.ResetPasswordResp;
import com.inthree.boon.deliveryapp.response.ServiceIncompleteResp;
import com.inthree.boon.deliveryapp.response.ServiceResp;
import com.inthree.boon.deliveryapp.response.UndeliveredReasonResp;
import com.inthree.boon.deliveryapp.response.UndeliveryResp;
import com.inthree.boon.deliveryapp.response.feedBackResp;
import com.inthree.boon.deliveryapp.response.pushNotiResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface InthreeApi {


    @Multipart
    @POST("change_password.php")
    Call<ResetPasswordResp> setChangePassword(
            @Part("id") RequestBody userId,
            @Part("otp") RequestBody oldPass,
            @Part("password") RequestBody passOne,
            @Part("latitude") RequestBody lat,
            @Part("longitude") RequestBody lang,
            @Part("deviceInfo") RequestBody deviceInfo,
            @Part("battery") RequestBody battery,
            @Part("userID") RequestBody runnerID,
            @Part("confirm_password") RequestBody passTwo);


    /******** Starts Here *********/

    /**
     * Fetch Order Details from server
     */
    @POST("dl_orderdetails.php")
    Observable<OrderResp> getOrders(@Body RequestBody body);

    /**
     * post the tracker page into upload
     * @param body get the body type
     * @return the body
     */
    @POST("dl_pagetracker.php")
    Observable<PartialResp> getTrackerPage(@Body RequestBody body);



    /******** push notifcation *********/
    @POST("dl_push_notification.php")
    Observable<pushNotiResponse> uploadPushNoti(@Body RequestBody body);

    /*
      * Upload Delivered and Partial Delivered Image
      */
    @Multipart
    @POST("dl_deliverConfirm_imageUpload.php")
    Observable<DeliveryConfirmResp> getDeliveryImage(@Part MultipartBody.Part fileDelivery,
                                                     @Part MultipartBody.Part fileInvoice,
                                                     @Part MultipartBody.Part fileAddress,

                                                     @Part MultipartBody.Part fileRelation,
                                                     @Part MultipartBody.Part fileSign,
                                                     @Part MultipartBody.Part filePickup);

    @POST("dl_login.php")
    Observable<LoginResp> getLogin(@Body RequestBody body);


    /*
    * Sync Delivery and Partial Delivery to server every 10 minutes
    */

    @POST("dl_sync_delivery_details.php")
    Observable<PartialResp> getPartialDeliverySync(@Body RequestBody body);


    @POST("dl_sync_delivery_details_branch.php")
    Observable<PartialResp> getBulkBfilDeliverySync(@Body RequestBody body);



    /*
    * Sync Delivery and Partial Delivery to server every 10 minutes
    */

    @POST("bbdelivery")
    Observable<List<BFILCheckResp>> getBFILCheck(@Body RequestBody body);


    /*
    *  Changes the status of the order once the order is received
    */
    @POST("dl_orderstatus.php")
    Observable<OrderChangeResp> getOrderStatus(@Body RequestBody body);



    /*
    * Sync Undelivered Orders every 10 minutes
    */
    @POST("dl_sync_undelivery.php")
    Observable<UndeliveryResp> getUndeliverySync(@Body RequestBody body);


    /*
    * Upload Undelivered Image
    */
    @Multipart
    @POST("dl_undelivery_imageUpload.php")
    Observable<UndeliveryResp> getUnDeliveryImage(@Part MultipartBody.Part fileDelivery);


    @POST("dl_reason_master.php")
    Observable<ReasonResp> getReason();


    @POST("dl_otp_trigger.php")
    Observable<OtpResp> getOtp(@Body RequestBody body);

    /**
     * Buld order for bfil
     * @return null
     */
    @POST("dl_branch_master.php")
    Observable<BranchResp> getBranchMaster();



    @POST("dl_feedback_master.php")
    Observable<feedBackResp> getFeedBack();

    @POST("dl_get_amountCollected.php")
    Observable<AttemptResp> getAmountCollected(@Body RequestBody body);

    @Multipart
    @POST("dl_zipfile_upload.php")
    Observable<UndeliveryResp> uploadZipFile(@Part MultipartBody.Part fileDelivery);

    @Multipart
    @POST("dl_image_offline.php")
    Observable<ImageSyncResp> uploadMultiFile(@Part MultipartBody.Part fileDelivery);

    @POST("dl_change_sync_status.php")
    Observable<OrderChangeResp> getSyncOrder(@Body RequestBody body);


    @POST("dl_sync_service_details.php")
    Observable<ServiceConfirmResp> pushServiceConfirmation(@Body RequestBody body);

    @POST("dl_servicedetails.php")
    Observable<ServiceOrderResp> getServiceDetails(@Body RequestBody body);

    @POST("dl_service_reason.php")
    Observable<ServiceIncompleteResp> getServiceReason();

    @POST("dl_sync_dio_details.php")
    Observable<ServiceResp> pushServiceSync(@Body RequestBody body);

    @POST("dl_service_incomplete.php")
    Observable<UndeliveryResp> getIncompleteSync(@Body RequestBody body);

    /*
     *  Changes the status of the order once the order is received
     */
    @POST("dl_servicestatus.php")
    Observable<OrderChangeResp> getServiceStatus(@Body RequestBody body);

    /*
     * Upload Delivered and Partial Delivered Image
     */
    @Multipart
    @POST("dl_service_signProof.php")
    Observable<DeliveryConfirmResp> getSignatureProof(@Part MultipartBody.Part fileDelivery);

    /*
     * Sync Undelivered Orders every 10 minutes
     */
    @POST("dl_sync_pickup.php")
    Observable<UndeliveryResp> getPickupSync(@Body RequestBody body);



}
