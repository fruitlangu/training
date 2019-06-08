package com.inthree.boon.deliveryapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.model.BranchVal;
import com.inthree.boon.deliveryapp.model.ServiceOrderResp;
import com.inthree.boon.deliveryapp.request.DeliveryConfirmReq;
import com.inthree.boon.deliveryapp.request.OrderReq;
import com.inthree.boon.deliveryapp.request.OrderStatusReq;
import com.inthree.boon.deliveryapp.request.PartialReq;
import com.inthree.boon.deliveryapp.request.ServiceConfirmReq;
import com.inthree.boon.deliveryapp.request.UndeliveryReq;
import com.inthree.boon.deliveryapp.response.AttemptResp;
import com.inthree.boon.deliveryapp.response.BranchResp;
import com.inthree.boon.deliveryapp.response.DeliveryConfirmResp;
import com.inthree.boon.deliveryapp.response.ImageSyncResp;
import com.inthree.boon.deliveryapp.response.OrderChangeResp;
import com.inthree.boon.deliveryapp.response.OrderResp;
import com.inthree.boon.deliveryapp.response.PartialResp;
import com.inthree.boon.deliveryapp.response.ReasonResp;
import com.inthree.boon.deliveryapp.response.ReasonVal;
import com.inthree.boon.deliveryapp.response.ServiceIncompleteResp;
import com.inthree.boon.deliveryapp.response.ServiceResp;
import com.inthree.boon.deliveryapp.response.UndeliveredReasonResp;
import com.inthree.boon.deliveryapp.response.UndeliveryResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.jakewharton.retrofit2.adapter.rxjava2.Result;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;

public class SyncService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ArrayList<ReasonResp> reasonMasterList;
    private ArrayList<AttemptResp> attemptList;
    private ArrayList<UndeliveryResp> undeliveryList;
    private String file_proofPhoto;
    String battery_level;
    String undelivered_shipAddress;

    String aadhaarEnabled;


    /**
     * Google client to interact with Google API
     */
    private GoogleApiClient mGoogleApiClient;
    private double latitude_user;
    private LocationRequest mLocationRequest;
    /**
     * Get the longitude
     */
    private double longitude_user;
    private String latitude;
    private String longitude;

    public Location mLastLocation;
    String image_url;
    //    String partial_shipAddress;
    private ArrayList<OrderResp> orderList;

    //    private final int UPDATE_INTERVAL = 5*60*1000;
    private final int UPDATE_INTERVAL = 10 * 60 * 1000;
//    private final int UPDATE_INTERVAL = 60 * 1000;

    private Timer timer = new Timer();
    private static final int NOTIFICATION_EX = 1;
    private NotificationManager notificationManager;
    private SQLiteDatabase database;
    private static final String DB_NAME = "boonboxdelivery.sqlite";
    private JSONObject pageTrackObject;
    String pageSyncUrl;
    String res_msg = "";
    int TIMEOUT_MILLISEC;
    String server;
    String file_path = "/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/";
    String file_path_old = "/storage/emulated/0/Pictures/DeliveryApp/";
    String file_path_very_old = "/storage/emulated/0/DCIM/Camera/";
    String sign_path = "/data/data/com.inthree.boon.deliveryapp/files/UserSignature/";
    private ArrayList<DeliveryConfirmResp> deliveryList;
    int attempt_count;
    private String timeIn;
    private String timeOut;
    private String latIn;
    private String latOut;
    private String longIn;
    private String longOut;
    private String shipmentId;
    private String appVersion;
    private String appName;
    private String status;
    private String date;
    private String userName;
    private String page;
    private ArrayList<UndeliveredReasonResp> reasonList;
    OrderDataAsync orderdatadsync;
    UploadImageAsync uploadimageasync;
    ReasonAsync reasonasync;
    UndeliveredDataAsync undelivereddatadsync;
    ModifyRecordDataAsync modifyrecorddataAsync;
    ChangeStatusAsync changestatusssync;
    ArrayList filePaths = new ArrayList();
    ArrayList filePaths2 = new ArrayList();
    ArrayList filePaths3 = new ArrayList();
    /**
     * Check whether role for service or delivery
     */
    private String roleId;

    /**
     * Get unincome count
     */
    private int unIncomAttemptCount;
    int service_attempt_count;
    int pick_attempt_count;


    /**
     * Get unincome count
     */

    int un_attempt_count;

    /**
     * Branch Master list
     */
    private ArrayList<BranchVal> branchMasterList;


    /**
     * Check whether bfil bulk delivery or not delivery
     */
    private String bfilBulkCheck;


    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
//      fix();
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        image_url = getResources().getString(R.string.delivery_url) + "/media/";
        file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
//        sign_path = String.valueOf(this.getFilesDir()) + "/UserSignature/";
        sign_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
        roleId = AppController.getStringPreference(Constants.ROLE_ID, "");
        Log.v("sync_service", "onCreate");
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Log.v("sync_service", "Started!");
        if (isInternetAvailable()) {
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    Log.v("sync_service", "data called!");
                    if (isInternetAvailable()) {

                /*        if (roleId.equalsIgnoreCase("3")) {
                            getData();

                        } else if (roleId.equalsIgnoreCase("4")) {
                            getServiceData();
                            getServiceIncomlpeteReasonData();
                        }

                        uploadimageasync = new UploadImageAsync();
                        uploadimageasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

                        getImagesToSync();
                        syncUploadSer();
                    }

                }
            }, 0, UPDATE_INTERVAL);
        }
        return START_STICKY;
    }


    private void stopService() {
        if (timer != null) timer.cancel();
    }

    private void getData() {
        Log.v("sync_service_getData", "called");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final InthreeApi apiService = retrofit.create(InthreeApi.class);
        final OrderReq order = new OrderReq();
        JSONObject paramObject = null;
        order.setId(AppController.getStringPreference(Constants.USER_ID, ""));
//        Log.v("sync_service_uid", "-" + AppController.getStringPreference(Constants.USER_ID, ""));
//        Log.v("LOGIN_USER_ID",LOGIN_USER_ID);
        try {
            paramObject = new JSONObject();
            paramObject.put("runner_id", order.getId());
//            Log.v("paramObject", paramObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

        final Observable<OrderResp> observable = apiService.getOrders(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<OrderResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(OrderResp value) {

//                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale("en", "US"));
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "US"));
                Date date = null;

                orderList = new ArrayList<>();
                List<OrderResp> detailVal = value.getDetails();
                Log.v("sync_service_orders", value.getResMsg());
                if (value.getResMsg().equals("order success")) {
                    Log.v("sync_service_orders1", value.getResMsg());
                    for (int i = 0; i < detailVal.size(); i++) {
                        Cursor checkOrder = database.rawQuery("Select * from orderheader where Shipment_Number = '" +
                                        detailVal.get(i).getShipmentid() + "'",
                                null);
                        if (checkOrder.getCount() == 0) {

//                            Log.v("OrderResp_service", "otp" + detailVal.get(i).getOtp() + "urn" + detailVal.get(i).getUrn() + detailVal.get(i).getOrderid());
                            try {
                                date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(date);

                            String customer_contact_no = detailVal.get(i).getCustomerContactNumber();
                            String contactnumber = detailVal.get(i).getCustomerContactNumber();
                         /*   String str = contactnumber;
                            int length = str.length();
                            if (length == 10) {
                                customer_contact_no = contactnumber;
                            } else if (length == 12) {
                                String phno = contactnumber.replace("91", "");
                                customer_contact_no = phno;
                            }*/

                            /* language parsing json starts*/
                            String tamil_val = null;
                            String hindi_val = null;
                            String marathi_val = null;
                            String punjabi_val = null;
                            String bengali_val = null;
                            String orissa_val = null;
                            String assam_val = null;
                            String telugu_val = null;
                            String kannada_val = null;
                            JSONObject tamilLangParamObject = null;
                            JSONObject hindiLangParamObject = null;
                            JSONObject marathiLangParamObject = null;
                            JSONObject bengaliLangParamObject = null;
                            JSONObject assamLangParamObject = null;
                            JSONObject orissaLangParamObject = null;
                            JSONObject punjabLangParamObject = null;
                            JSONObject teluguLangParamObject = null;
                            JSONObject kannadaLangParamObject = null;
                            String lang_json = detailVal.get(i).getLanguage_json();
                            if (lang_json != null) {
                                try {
//                                    Log.v("getLanguage_json",lang_json);
                                    JSONObject obj = new JSONObject(lang_json);

                                    try {
                                        //Tamil JSON
                                        tamilLangParamObject = new JSONObject();

                                        JSONObject tamilOneObject = obj.getJSONObject("tamil");
                                        if (tamilOneObject.has("customer_name")) {
                                            String tamil_name = tamilOneObject.getString("customer_name");
                                            tamilLangParamObject.put("customer_name", tamil_name);
                                        }
                                        if (tamilOneObject.has("branch_name")) {
                                            String tamil_branch = tamilOneObject.getString("branch_name");
                                            tamilLangParamObject.put("branch_name", tamil_branch);
                                        }
                                        if (tamilOneObject.has("delivery_address")) {
                                            String tamil_branch_deliaddr = tamilOneObject.getString("delivery_address");
                                            tamilLangParamObject.put("delivery_address", tamil_branch_deliaddr);
                                        }
                                        if (tamilOneObject.has("branch_address")) {
                                            String tamil_branch_addr = tamilOneObject.getString("branch_address");
                                            tamilLangParamObject.put("branch_address", tamil_branch_addr);
                                        }
                                        if (tamilOneObject.has("city")) {
                                            String tamil_branch_city = tamilOneObject.getString("city");
                                            tamilLangParamObject.put("city", tamil_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    tamil_val = tamilLangParamObject.toString();
//                                    Log.v("tamilLangParamObject","-- "+tamil_val);
                                    String hi_val = null;
                                    try {
                                        //Hindi JSON
                                        hindiLangParamObject = new JSONObject();

                                        JSONObject hindiOneObject = obj.getJSONObject("hindi");
                                        if (hindiOneObject.has("customer_name")) {
                                            String hindi_name = hindiOneObject.getString("customer_name");
                                            hindiLangParamObject.put("customer_name", hindi_name);
                                        }
                                        if (hindiOneObject.has("branch_name")) {
                                            String hindi_branch = hindiOneObject.getString("branch_name");
                                            hindiLangParamObject.put("branch_name", hindi_branch);
                                        }
                                        if (hindiOneObject.has("delivery_address")) {
                                            String hindi_branch_deliaddr = hindiOneObject.getString("delivery_address");
                                            hindiLangParamObject.put("delivery_address", hindi_branch_deliaddr);
                                        }
                                        if (hindiOneObject.has("branch_address")) {
                                            String hindi_branch_addr = hindiOneObject.getString("branch_address");
                                            hindiLangParamObject.put("branch_address", hindi_branch_addr);
                                        }
                                        if (hindiOneObject.has("city")) {
                                            String hindi_branch_city = hindiOneObject.getString("city");
                                            hindiLangParamObject.put("city", hindi_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    hindi_val = hindiLangParamObject.toString();

                                    try {
                                        //Bengali JSON
                                        bengaliLangParamObject = new JSONObject();

                                        JSONObject bengaliOneObject = obj.getJSONObject("bengali");
                                        if (bengaliOneObject.has("customer_name")) {
                                            String bengali_name = bengaliOneObject.getString("customer_name");
                                            bengaliLangParamObject.put("customer_name", bengali_name);
                                        }
                                        if (bengaliOneObject.has("branch_name")) {
                                            String bengali_branch = bengaliOneObject.getString("branch_name");
                                            bengaliLangParamObject.put("branch_name", bengali_branch);
                                        }
                                        if (bengaliOneObject.has("delivery_address")) {
                                            String bengali_branch_deliaddr = bengaliOneObject.getString("delivery_address");
                                            bengaliLangParamObject.put("delivery_address", bengali_branch_deliaddr);
                                        }
                                        if (bengaliOneObject.has("branch_address")) {
                                            String bengali_branch_addr = bengaliOneObject.getString("branch_address");
                                            bengaliLangParamObject.put("branch_address", bengali_branch_addr);
                                        }
                                        if (bengaliOneObject.has("city")) {
                                            String bengali_branch_city = bengaliOneObject.getString("city");
                                            bengaliLangParamObject.put("city", bengali_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }

                                    bengali_val = bengaliLangParamObject.toString();

                                    try {
                                        //Marathi JSON
                                        marathiLangParamObject = new JSONObject();
                                        JSONObject marathiOneObject = obj.getJSONObject("marathi");
                                        if (marathiOneObject.has("customer_name")) {
                                            String marathi_name = marathiOneObject.getString("customer_name");
                                            marathiLangParamObject.put("customer_name", marathi_name);
                                        }
                                        if (marathiOneObject.has("branch_name")) {
                                            String marathi_branch = marathiOneObject.getString("branch_name");
                                            marathiLangParamObject.put("branch_name", marathi_branch);
                                        }
                                        if (marathiOneObject.has("delivery_address")) {
                                            String marathi_branch_deliaddr = marathiOneObject.getString("delivery_address");
                                            marathiLangParamObject.put("delivery_address", marathi_branch_deliaddr);
                                        }
                                        if (marathiOneObject.has("branch_address")) {
                                            String marathi_branch_addr = marathiOneObject.getString("branch_address");
                                            marathiLangParamObject.put("branch_address", marathi_branch_addr);
                                        }
                                        if (marathiOneObject.has("city")) {
                                            String marathi_branch_city = marathiOneObject.getString("city");
                                            marathiLangParamObject.put("city", marathi_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }

                                    marathi_val = marathiLangParamObject.toString();
                                    try {
                                        //Assam JSON
                                        assamLangParamObject = new JSONObject();
                                        JSONObject assamOneObject = obj.getJSONObject("assamese");
                                        if (assamOneObject.has("customer_name")) {
                                            String assam_name = assamOneObject.getString("customer_name");
                                            assamLangParamObject.put("customer_name", assam_name);
                                        }
                                        if (assamOneObject.has("branch_name")) {
                                            String assam_branch = assamOneObject.getString("branch_name");
                                            assamLangParamObject.put("branch_name", assam_branch);
                                        }
                                        if (assamOneObject.has("delivery_address")) {
                                            String assam_branch_deliaddr = assamOneObject.getString("delivery_address");
                                            assamLangParamObject.put("delivery_address", assam_branch_deliaddr);
                                        }
                                        if (assamOneObject.has("branch_address")) {
                                            String assam_branch_addr = assamOneObject.getString("branch_address");
                                            assamLangParamObject.put("branch_address", assam_branch_addr);
                                        }
                                        if (assamOneObject.has("city")) {
                                            String assam_branch_city = assamOneObject.getString("city");
                                            assamLangParamObject.put("city", assam_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    assam_val = assamLangParamObject.toString();

                                    try {
                                        //Orissa JSON
                                        orissaLangParamObject = new JSONObject();
                                        JSONObject orissaOneObject = obj.getJSONObject("odia");
                                        if (orissaOneObject.has("customer_name")) {
                                            String orissa_name = orissaOneObject.getString("customer_name");
                                            orissaLangParamObject.put("customer_name", orissa_name);
                                        }
                                        if (orissaOneObject.has("branch_name")) {
                                            String orissa_branch = orissaOneObject.getString("branch_name");
                                            orissaLangParamObject.put("branch_name", orissa_branch);
                                        }
                                        if (orissaOneObject.has("delivery_address")) {
                                            String orissa_branch_deliaddr = orissaOneObject.getString("delivery_address");
                                            orissaLangParamObject.put("delivery_address", orissa_branch_deliaddr);
                                        }
                                        if (orissaOneObject.has("branch_address")) {
                                            String orissa_branch_addr = orissaOneObject.getString("branch_address");
                                            orissaLangParamObject.put("branch_address", orissa_branch_addr);
                                        }
                                        if (orissaOneObject.has("city")) {
                                            String orissa_branch_city = orissaOneObject.getString("city");
                                            orissaLangParamObject.put("city", orissa_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    orissa_val = orissaLangParamObject.toString();


                                    try {
                                        //Telugu JSON
                                        teluguLangParamObject = new JSONObject();
                                        JSONObject teluguOneObject = obj.getJSONObject("telugu");
                                        if (teluguOneObject.has("customer_name")) {
                                            String telugu_name = teluguOneObject.getString("customer_name");
                                            teluguLangParamObject.put("customer_name", telugu_name);
                                        }
                                        if (teluguOneObject.has("branch_name")) {
                                            String telugu_branch = teluguOneObject.getString("branch_name");
                                            teluguLangParamObject.put("branch_name", telugu_branch);
                                        }
                                        if (teluguOneObject.has("delivery_address")) {
                                            String telugu_branch_deliaddr = teluguOneObject.getString("delivery_address");
                                            teluguLangParamObject.put("delivery_address", telugu_branch_deliaddr);
                                        }
                                        if (teluguOneObject.has("branch_address")) {
                                            String telugu_branch_addr = teluguOneObject.getString("branch_address");
                                            teluguLangParamObject.put("branch_address", telugu_branch_addr);
                                        }
                                        if (teluguOneObject.has("city")) {
                                            String telugu_branch_city = teluguOneObject.getString("city");
                                            teluguLangParamObject.put("city", telugu_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    telugu_val = teluguLangParamObject.toString();

                                    try {
                                        //Kannada JSON
                                        kannadaLangParamObject = new JSONObject();
                                        JSONObject kannadaOneObject = obj.getJSONObject("kannada");
                                        if (kannadaOneObject.has("customer_name")) {
                                            String kannada_name = kannadaOneObject.getString("customer_name");
                                            kannadaLangParamObject.put("customer_name", kannada_name);
                                        }
                                        if (kannadaOneObject.has("branch_name")) {
                                            String kannada_branch = kannadaOneObject.getString("branch_name");
                                            kannadaLangParamObject.put("branch_name", kannada_branch);
                                        }
                                        if (kannadaOneObject.has("delivery_address")) {
                                            String kannada_branch_deliaddr = kannadaOneObject.getString("delivery_address");
                                            kannadaLangParamObject.put("delivery_address", kannada_branch_deliaddr);
                                        }
                                        if (kannadaOneObject.has("branch_address")) {
                                            String kannada_branch_addr = kannadaOneObject.getString("branch_address");
                                            kannadaLangParamObject.put("branch_address", kannada_branch_addr);
                                        }
                                        if (kannadaOneObject.has("city")) {
                                            String kannada_branch_city = kannadaOneObject.getString("city");
                                            kannadaLangParamObject.put("city", kannada_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    kannada_val = kannadaLangParamObject.toString();

                                    try {
                                        //Punjabi JSON
                                        JSONObject punjabOneObject = obj.getJSONObject("punjabi");
                                        punjabLangParamObject = new JSONObject();
                                        if (punjabLangParamObject.has("customer_name")) {
                                            String punjab_name = punjabOneObject.getString("customer_name");
                                            punjabLangParamObject.put("customer_name", punjab_name);
                                        }
                                        if (punjabLangParamObject.has("branch_name")) {
                                            String punjab_branch = punjabOneObject.getString("branch_name");
                                            punjabLangParamObject.put("branch_name", punjab_branch);
                                        }
                                        if (punjabLangParamObject.has("delivery_address")) {
                                            String punjab_branch_deliaddr = punjabOneObject.getString("delivery_address");
                                            punjabLangParamObject.put("delivery_address", punjab_branch_deliaddr);
                                        }
                                        if (punjabLangParamObject.has("branch_address")) {
                                            String punjab_branch_addr = punjabOneObject.getString("branch_address");
                                            punjabLangParamObject.put("branch_address", punjab_branch_addr);
                                        }
                                        if (punjabLangParamObject.has("city")) {
                                            String punjab_branch_city = punjabOneObject.getString("city");
                                            punjabLangParamObject.put("city", punjab_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    punjabi_val = punjabLangParamObject.toString();

                                } catch (Throwable t) {
                                    Log.e("lang_json", "Could not parse malformed JSON: \"" + bengali_val + "\"");
                                }
                                /* language parsing json ends*/
                            }

                            String insertOrderHeader = "Insert into orderheader(order_number,customer_name," +
                                    "customer_contact_number,alternate_contact_number,to_be_delivered_by,billing_address,billing_city,billing_pincode," +
                                    "billing_telephone,shipping_address,shipping_city,shipping_pincode, shipping_telephone,invoice_amount,payment_mode," +
                                    "client_branch_name,branch_address,branch_pincode,branch_contact_number,group_leader_name,group_leader_contact_number," +

                                    "slot_number,referenceNumber,processDefinitionCode,Shipment_Number,sync_status,delivery_status, valid, attempt_count,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi,otp,urn,order_type,max_attempt,delivery_aadhar_required,virtual_id) " +


                                    "Values('" + detailVal.get(i).getOrderid() + "', '" + detailVal.get(i).getCustomerName() + "', '" + customer_contact_no + "'," +
                                    "'" + detailVal.get(i).getAlternateContactNumber() + "', '" + outputDateStr + "', " +
                                    "'" + detailVal.get(i).getBillingAddress() + "', '" + detailVal.get(i).getBillingCity() + "', '" + detailVal.get(i).getBillingPincode() + "', '" + detailVal.get(i).getBillingTelephone() + "', '" + detailVal.get(i).getShippingAddress() + "', '" + detailVal.get(i).getShippingCity() + "'," +
                                    " '" + detailVal.get(i).getShippingPincode() + "', '" + detailVal.get(i).getShippingTelephone() + "', '" + detailVal.get(i).getAmount() + "', '" + detailVal.get(i).getPaymentMode() + "', '" + detailVal.get(i).getClient_branch_name() + "', '" + detailVal.get(i).getBranch_address() + "', '" + detailVal.get(i).getBranch_pincode() + "'" +


                                    ", '" + detailVal.get(i).getBranch_contact_number() + "', '" + detailVal.get(i).getGroup_leader_name() + "', '" + detailVal.get(i).getGroup_leader_contact_number() + "', '" + detailVal.get(i).getSlot_number() + "', '" + detailVal.get(i).getReference() + "', '', '" + detailVal.get(i).getShipmentid() + "', 'P', '', '" + detailVal.get(i).getDownloadSync() + "', " + detailVal.get(i).getAttempt() + ",'" + tamil_val + "','" + telugu_val + "','" + punjabi_val + "','" + hindi_val + "','" + bengali_val + "','" + kannada_val + "','" + assam_val + "','" + orissa_val + "','" + marathi_val + "','" + detailVal.get(i).getOtp() + "','" + detailVal.get(i).getUrn() + "','" + detailVal.get(i).getOrder_type() + "','" + detailVal.get(i).getMax_attempt() + "'," + detailVal.get(i).getDelivery_aadhar_required() + ",'" + value.getVirtual_id() + "')";


                            database.execSQL(insertOrderHeader);

                        } else {
                            Log.v("OrderResp1_service", "otp" + detailVal.get(i).getOtp() + "urn" + detailVal.get(i).getUrn() + detailVal.get(i).getOrderid());
                            try {
                                date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(date);

                            String customer_contact_no = detailVal.get(i).getCustomerContactNumber();
                            String contactnumber = detailVal.get(i).getCustomerContactNumber();
                         /*   String str = contactnumber;
                            int length = str.length();
                            if (length == 10) {
                                customer_contact_no = contactnumber;
                            } else if (length == 12) {
                                String phno = contactnumber.replace("91", "");
                                customer_contact_no = phno;
                            }*/

                            String tamil_val = null;
                            String hindi_val = null;
                            String marathi_val = null;
                            String punjabi_val = null;
                            String bengali_val = null;
                            String orissa_val = null;
                            String assam_val = null;
                            String telugu_val = null;
                            String kannada_val = null;
                            JSONObject tamilLangParamObject = null;
                            JSONObject hindiLangParamObject = null;
                            JSONObject marathiLangParamObject = null;
                            JSONObject bengaliLangParamObject = null;
                            JSONObject assamLangParamObject = null;
                            JSONObject orissaLangParamObject = null;
                            JSONObject punjabLangParamObject = null;
                            JSONObject teluguLangParamObject = null;
                            JSONObject kannadaLangParamObject = null;
                            String lang_json = detailVal.get(i).getLanguage_json();
                            if (lang_json != null) {
//                                Log.v("check_if_null", "- " + lang_json);
                                try {
//                                    Log.v("getLanguage_json",lang_json);
                                    JSONObject obj = new JSONObject(lang_json);

                                    try {
                                        //Tamil JSON
                                        tamilLangParamObject = new JSONObject();

                                        JSONObject tamilOneObject = obj.getJSONObject("tamil");
                                        if (tamilOneObject.has("customer_name")) {
                                            String tamil_name = tamilOneObject.getString("customer_name");
                                            tamilLangParamObject.put("customer_name", tamil_name);
                                        }
                                        if (tamilOneObject.has("branch_name")) {
                                            String tamil_branch = tamilOneObject.getString("branch_name");
                                            tamilLangParamObject.put("branch_name", tamil_branch);
                                        }
                                        if (tamilOneObject.has("delivery_address")) {
                                            String tamil_branch_deliaddr = tamilOneObject.getString("delivery_address");
                                            tamilLangParamObject.put("delivery_address", tamil_branch_deliaddr);
                                        }
                                        if (tamilOneObject.has("branch_address")) {
                                            String tamil_branch_addr = tamilOneObject.getString("branch_address");
                                            tamilLangParamObject.put("branch_address", tamil_branch_addr);
                                        }
                                        if (tamilOneObject.has("city")) {
                                            String tamil_branch_city = tamilOneObject.getString("city");
                                            tamilLangParamObject.put("city", tamil_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    tamil_val = tamilLangParamObject.toString();
//                                    Log.v("tamilLangParamObject","-- "+tamil_val);
                                    String hi_val = null;
                                    try {
                                        //Hindi JSON
                                        hindiLangParamObject = new JSONObject();

                                        JSONObject hindiOneObject = obj.getJSONObject("hindi");
                                        if (hindiOneObject.has("customer_name")) {
                                            String hindi_name = hindiOneObject.getString("customer_name");
                                            hindiLangParamObject.put("customer_name", hindi_name);
                                        }
                                        if (hindiOneObject.has("branch_name")) {
                                            String hindi_branch = hindiOneObject.getString("branch_name");
                                            hindiLangParamObject.put("branch_name", hindi_branch);
                                        }
                                        if (hindiOneObject.has("delivery_address")) {
                                            String hindi_branch_deliaddr = hindiOneObject.getString("delivery_address");
                                            hindiLangParamObject.put("delivery_address", hindi_branch_deliaddr);
                                        }
                                        if (hindiOneObject.has("branch_address")) {
                                            String hindi_branch_addr = hindiOneObject.getString("branch_address");
                                            hindiLangParamObject.put("branch_address", hindi_branch_addr);
                                        }
                                        if (hindiOneObject.has("city")) {
                                            String hindi_branch_city = hindiOneObject.getString("city");
                                            hindiLangParamObject.put("city", hindi_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    hindi_val = hindiLangParamObject.toString();

                                    try {
                                        //Bengali JSON
                                        bengaliLangParamObject = new JSONObject();

                                        JSONObject bengaliOneObject = obj.getJSONObject("bengali");
                                        if (bengaliOneObject.has("customer_name")) {
                                            String bengali_name = bengaliOneObject.getString("customer_name");
                                            bengaliLangParamObject.put("customer_name", bengali_name);
                                        }
                                        if (bengaliOneObject.has("branch_name")) {
                                            String bengali_branch = bengaliOneObject.getString("branch_name");
                                            bengaliLangParamObject.put("branch_name", bengali_branch);
                                        }
                                        if (bengaliOneObject.has("delivery_address")) {
                                            String bengali_branch_deliaddr = bengaliOneObject.getString("delivery_address");
                                            bengaliLangParamObject.put("delivery_address", bengali_branch_deliaddr);
                                        }
                                        if (bengaliOneObject.has("branch_address")) {
                                            String bengali_branch_addr = bengaliOneObject.getString("branch_address");
                                            bengaliLangParamObject.put("branch_address", bengali_branch_addr);
                                        }
                                        if (bengaliOneObject.has("city")) {
                                            String bengali_branch_city = bengaliOneObject.getString("city");
                                            bengaliLangParamObject.put("city", bengali_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }

                                    bengali_val = bengaliLangParamObject.toString();

                                    try {
                                        //Marathi JSON
                                        marathiLangParamObject = new JSONObject();
                                        JSONObject marathiOneObject = obj.getJSONObject("marathi");
                                        if (marathiOneObject.has("customer_name")) {
                                            String marathi_name = marathiOneObject.getString("customer_name");
                                            marathiLangParamObject.put("customer_name", marathi_name);
                                        }
                                        if (marathiOneObject.has("branch_name")) {
                                            String marathi_branch = marathiOneObject.getString("branch_name");
                                            marathiLangParamObject.put("branch_name", marathi_branch);
                                        }
                                        if (marathiOneObject.has("delivery_address")) {
                                            String marathi_branch_deliaddr = marathiOneObject.getString("delivery_address");
                                            marathiLangParamObject.put("delivery_address", marathi_branch_deliaddr);
                                        }
                                        if (marathiOneObject.has("branch_address")) {
                                            String marathi_branch_addr = marathiOneObject.getString("branch_address");
                                            marathiLangParamObject.put("branch_address", marathi_branch_addr);
                                        }
                                        if (marathiOneObject.has("city")) {
                                            String marathi_branch_city = marathiOneObject.getString("city");
                                            marathiLangParamObject.put("city", marathi_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }

                                    marathi_val = marathiLangParamObject.toString();
                                    try {
                                        //Assam JSON
                                        assamLangParamObject = new JSONObject();
                                        JSONObject assamOneObject = obj.getJSONObject("assamese");
                                        if (assamOneObject.has("customer_name")) {
                                            String assam_name = assamOneObject.getString("customer_name");
                                            assamLangParamObject.put("customer_name", assam_name);
                                        }
                                        if (assamOneObject.has("branch_name")) {
                                            String assam_branch = assamOneObject.getString("branch_name");
                                            assamLangParamObject.put("branch_name", assam_branch);
                                        }
                                        if (assamOneObject.has("delivery_address")) {
                                            String assam_branch_deliaddr = assamOneObject.getString("delivery_address");
                                            assamLangParamObject.put("delivery_address", assam_branch_deliaddr);
                                        }
                                        if (assamOneObject.has("branch_address")) {
                                            String assam_branch_addr = assamOneObject.getString("branch_address");
                                            assamLangParamObject.put("branch_address", assam_branch_addr);
                                        }
                                        if (assamOneObject.has("city")) {
                                            String assam_branch_city = assamOneObject.getString("city");
                                            assamLangParamObject.put("city", assam_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    assam_val = assamLangParamObject.toString();

                                    try {
                                        //Orissa JSON
                                        orissaLangParamObject = new JSONObject();
                                        JSONObject orissaOneObject = obj.getJSONObject("odia");
                                        if (orissaOneObject.has("customer_name")) {
                                            String orissa_name = orissaOneObject.getString("customer_name");
                                            orissaLangParamObject.put("customer_name", orissa_name);
                                        }
                                        if (orissaOneObject.has("branch_name")) {
                                            String orissa_branch = orissaOneObject.getString("branch_name");
                                            orissaLangParamObject.put("branch_name", orissa_branch);
                                        }
                                        if (orissaOneObject.has("delivery_address")) {
                                            String orissa_branch_deliaddr = orissaOneObject.getString("delivery_address");
                                            orissaLangParamObject.put("delivery_address", orissa_branch_deliaddr);
                                        }
                                        if (orissaOneObject.has("branch_address")) {
                                            String orissa_branch_addr = orissaOneObject.getString("branch_address");
                                            orissaLangParamObject.put("branch_address", orissa_branch_addr);
                                        }
                                        if (orissaOneObject.has("city")) {
                                            String orissa_branch_city = orissaOneObject.getString("city");
                                            orissaLangParamObject.put("city", orissa_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    orissa_val = orissaLangParamObject.toString();


                                    try {
                                        //Telugu JSON
                                        teluguLangParamObject = new JSONObject();
                                        JSONObject teluguOneObject = obj.getJSONObject("telugu");
                                        if (teluguOneObject.has("customer_name")) {
                                            String telugu_name = teluguOneObject.getString("customer_name");
                                            teluguLangParamObject.put("customer_name", telugu_name);
                                        }
                                        if (teluguOneObject.has("branch_name")) {
                                            String telugu_branch = teluguOneObject.getString("branch_name");
                                            teluguLangParamObject.put("branch_name", telugu_branch);
                                        }
                                        if (teluguOneObject.has("delivery_address")) {
                                            String telugu_branch_deliaddr = teluguOneObject.getString("delivery_address");
                                            teluguLangParamObject.put("delivery_address", telugu_branch_deliaddr);
                                        }
                                        if (teluguOneObject.has("branch_address")) {
                                            String telugu_branch_addr = teluguOneObject.getString("branch_address");
                                            teluguLangParamObject.put("branch_address", telugu_branch_addr);
                                        }
                                        if (teluguOneObject.has("city")) {
                                            String telugu_branch_city = teluguOneObject.getString("city");
                                            teluguLangParamObject.put("city", telugu_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    telugu_val = teluguLangParamObject.toString();

                                    try {
                                        //Kannada JSON
                                        kannadaLangParamObject = new JSONObject();
                                        JSONObject kannadaOneObject = obj.getJSONObject("kannada");
                                        if (kannadaOneObject.has("customer_name")) {
                                            String kannada_name = kannadaOneObject.getString("customer_name");
                                            kannadaLangParamObject.put("customer_name", kannada_name);
                                        }
                                        if (kannadaOneObject.has("branch_name")) {
                                            String kannada_branch = kannadaOneObject.getString("branch_name");
                                            kannadaLangParamObject.put("branch_name", kannada_branch);
                                        }
                                        if (kannadaOneObject.has("delivery_address")) {
                                            String kannada_branch_deliaddr = kannadaOneObject.getString("delivery_address");
                                            kannadaLangParamObject.put("delivery_address", kannada_branch_deliaddr);
                                        }
                                        if (kannadaOneObject.has("branch_address")) {
                                            String kannada_branch_addr = kannadaOneObject.getString("branch_address");
                                            kannadaLangParamObject.put("branch_address", kannada_branch_addr);
                                        }
                                        if (kannadaOneObject.has("city")) {
                                            String kannada_branch_city = kannadaOneObject.getString("city");
                                            kannadaLangParamObject.put("city", kannada_branch_city);
                                        }

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    kannada_val = kannadaLangParamObject.toString();

                                    try {
                                        //Punjabi JSON
                                        JSONObject punjabOneObject = obj.getJSONObject("punjabi");
                                        punjabLangParamObject = new JSONObject();
                                        if (punjabLangParamObject.has("customer_name")) {
                                            String punjab_name = punjabOneObject.getString("customer_name");
                                            punjabLangParamObject.put("customer_name", punjab_name);
                                        }
                                        if (punjabLangParamObject.has("branch_name")) {
                                            String punjab_branch = punjabOneObject.getString("branch_name");
                                            punjabLangParamObject.put("branch_name", punjab_branch);
                                        }
                                        if (punjabLangParamObject.has("delivery_address")) {
                                            String punjab_branch_deliaddr = punjabOneObject.getString("delivery_address");
                                            punjabLangParamObject.put("delivery_address", punjab_branch_deliaddr);
                                        }
                                        if (punjabLangParamObject.has("branch_address")) {
                                            String punjab_branch_addr = punjabOneObject.getString("branch_address");
                                            punjabLangParamObject.put("branch_address", punjab_branch_addr);
                                        }
                                        if (punjabLangParamObject.has("city")) {
                                            String punjab_branch_city = punjabOneObject.getString("city");
                                            punjabLangParamObject.put("city", punjab_branch_city);
                                        }


                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    punjabi_val = punjabLangParamObject.toString();

                                } catch (Throwable t) {
                                    Log.e("lang_json", "Could not parse malformed JSON: \"" + bengali_val + "\"");
                                }
                            }
                            String queryupdate = "UPDATE orderheader set order_number = '" + detailVal.get(i).getOrderid() +
                                    "',customer_name='" + detailVal.get(i).getCustomerName() + "',customer_contact_number = '" + customer_contact_no + "'," +
                                    "alternate_contact_number = '" + detailVal.get(i).getAlternateContactNumber() + "'," +
                                    "to_be_delivered_by = '" + outputDateStr + "'," +
                                    "billing_address= '" + detailVal.get(i).getBillingAddress() + "'," +
                                    "billing_city= '" + detailVal.get(i).getBillingCity() + "'," +
                                    "billing_pincode= '" + detailVal.get(i).getBillingPincode() + "'," +
                                    "billing_telephone= '" + detailVal.get(i).getBillingTelephone() + "'," +
                                    "shipping_address= '" + detailVal.get(i).getShippingAddress() + "'," +
                                    "shipping_city= '" + detailVal.get(i).getShippingCity() + "'," +
                                    "shipping_pincode= '" + detailVal.get(i).getShippingPincode() + "', " +
                                    "shipping_telephone= '" + detailVal.get(i).getShippingTelephone() + "'," +
                                    "invoice_amount= '" + detailVal.get(i).getAmount() + "'," +
                                    "payment_mode= '" + detailVal.get(i).getPaymentMode() + "'," +
                                    "client_branch_name= '" + detailVal.get(i).getClient_branch_name() + "'," +
                                    "branch_address= '" + detailVal.get(i).getBranch_address() + "'," +
                                    "branch_pincode= '" + detailVal.get(i).getBranch_pincode() + "'," +
                                    "branch_contact_number= '" + detailVal.get(i).getBranch_contact_number() + "'," +
                                    "group_leader_name= '" + detailVal.get(i).getGroup_leader_name() + "'," +
                                    "group_leader_contact_number= '" + detailVal.get(i).getGroup_leader_contact_number() + "'," +
                                    "slot_number= '" + detailVal.get(i).getSlot_number() + "'," +
                                    "referenceNumber= '" + detailVal.get(i).getReference() + "'," +
                                    "otp= '" + detailVal.get(i).getOtp() + "'," +
                                    "urn= '" + detailVal.get(i).getUrn() + "'," +

                                    "order_type= '" + detailVal.get(i).getOrder_type() + "'," +
                                    "max_attempt= '" + detailVal.get(i).getMax_attempt() + "'," +

                                    "delivery_aadhar_required = '" + detailVal.get(i).getDelivery_aadhar_required() + "'," +

                                    "processDefinitionCode= ''," +
                                    "Shipment_Number= '" + detailVal.get(i).getShipmentid() + "'," +
                                    "attempt_count = '" + detailVal.get(i).getAttempt() + "'," +
                                    "tamil = '" + tamil_val + "'," +
                                    "hindi = '" + hindi_val + "'," +
                                    "assam = '" + assam_val + "'," +
                                    "punjabi = '" + punjabi_val + "'," +
                                    "marathi = '" + marathi_val + "'," +
                                    "telugu = '" + telugu_val + "'," +
                                    "kannada = '" + kannada_val + "'," +
                                    "orissa = '" + orissa_val + "'," +
                                    "virtual_id = '" + value.getVirtual_id() + "'," +
                                    "bengali = '" + bengali_val + "' where " +  //sync_status= 'P'
                                    "Shipment_Number ='" + detailVal.get(i).getShipmentid() + "' ";  //AND sync_status = 'P'
                            database.execSQL(queryupdate);
                        }

                        String product_name = "";
                        for (int j = 0; j < detailVal.get(i).getOrder().size(); j++) {

                            String tamil_pval = null;
                            String hindi_pval = null;
                            String marathi_pval = null;
                            String punjabi_pval = null;
                            String bengali_pval = null;
                            String orissa_pval = null;
                            String assam_pval = null;
                            String telugu_pval = null;
                            String kannada_pval = null;
                            JSONObject tamilItemLangParamObject = null;
                            JSONObject hindiItemLangParamObject = null;
                            JSONObject marathiItemLangParamObject = null;
                            JSONObject bengaliItemLangParamObject = null;
                            JSONObject assamItemLangParamObject = null;
                            JSONObject orissaItemLangParamObject = null;
                            JSONObject punjabItemLangParamObject = null;
                            JSONObject teluguItemLangParamObject = null;
                            JSONObject kannadaItemLangParamObject = null;
                            String item_json;
                            item_json = detailVal.get(i).getOrder().get(j).getItem_json();
                            if (item_json != null && !item_json.equals("null")) {
                                Log.v("check_if_item_json", "- " + item_json);

                                try {

                                    JSONObject obj = new JSONObject(item_json);

                                    try {
                                        //Tamil JSON
                                        JSONObject tamilOneObject = obj.getJSONObject("tamil");
                                        String tamil_pname = tamilOneObject.getString("product_name");

                                        tamilItemLangParamObject = new JSONObject();
                                        tamilItemLangParamObject.put("product_name", tamil_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    tamil_pval = tamilItemLangParamObject.toString();
                                    try {
                                        //Hindi JSON
                                        JSONObject hindiOneObject = obj.getJSONObject("hindi");
                                        String hindi_pname = hindiOneObject.getString("product_name");

                                        hindiItemLangParamObject = new JSONObject();
                                        hindiItemLangParamObject.put("product_name", hindi_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    hindi_pval = hindiItemLangParamObject.toString();

                                    try {
                                        //Bengali JSON
                                        JSONObject bengaliOneObject = obj.getJSONObject("bengali");
                                        String bengali_pname = bengaliOneObject.getString("product_name");

                                        bengaliItemLangParamObject = new JSONObject();
                                        bengaliItemLangParamObject.put("product_name", bengali_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }

                                    bengali_pval = bengaliItemLangParamObject.toString();

                                    try {
                                        //Marathi JSON
                                        JSONObject marathiOneObject = obj.getJSONObject("marathi");
                                        String marathi_pname = marathiOneObject.getString("product_name");

                                        marathiItemLangParamObject = new JSONObject();
                                        marathiItemLangParamObject.put("product_name", marathi_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }

                                    marathi_pval = marathiItemLangParamObject.toString();
                                    try {
                                        //Assam JSON
                                        JSONObject assamOneObject = obj.getJSONObject("assamese");
                                        String assam_pname = assamOneObject.getString("product_name");

                                        assamItemLangParamObject = new JSONObject();
                                        assamItemLangParamObject.put("product_name", assam_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    assam_pval = assamItemLangParamObject.toString();

                                    try {
                                        //Orissa JSON
                                        JSONObject orissaOneObject = obj.getJSONObject("odia");
                                        String orissa_pname = orissaOneObject.getString("product_name");

                                        orissaItemLangParamObject = new JSONObject();
                                        orissaItemLangParamObject.put("product_name", orissa_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    orissa_pval = orissaItemLangParamObject.toString();

                                    try {
                                        //Punjabi JSON
                                        JSONObject punjabOneObject = obj.getJSONObject("punjabi");
                                        String punjab_pname = punjabOneObject.getString("product_name");

                                        punjabItemLangParamObject = new JSONObject();
                                        punjabItemLangParamObject.put("product_name", punjab_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    punjabi_pval = punjabItemLangParamObject.toString();

                                    try {
                                        //Telugu JSON
                                        JSONObject teluguOneObject = obj.getJSONObject("telugu");
                                        String telugu_pname = teluguOneObject.getString("product_name");

                                        teluguItemLangParamObject = new JSONObject();
                                        teluguItemLangParamObject.put("product_name", telugu_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    telugu_pval = teluguItemLangParamObject.toString();

                                    try {
                                        //Kannada JSON
                                        JSONObject kannadaOneObject = obj.getJSONObject("kannada");
                                        String kannada_pname = kannadaOneObject.getString("product_name");

                                        kannadaItemLangParamObject = new JSONObject();
                                        kannadaItemLangParamObject.put("product_name", kannada_pname);

                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                    kannada_pval = kannadaItemLangParamObject.toString();


                                } catch (Throwable t) {
                                    Log.e("new_item_json", "Could not parse malformed JSON: \"" + bengali_pval + "\"");
                                }
                            }

                            if (!detailVal.get(i).getOrder_type().equals("2")) {
                                Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                                detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' AND pickup_type = 0",
                                        null);
                                if (checkProducts.getCount() == 0) {

                                    product_name = detailVal.get(i).getOrder().get(j).getName();
                                    String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                    String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi)" +
                                            " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                            + "', '" + detailVal.get(i).getOrder().get(j).getQty() + "', '" + detailVal.get(i).getOrder().get(j).getPrice() + "', '" +
                                            detailVal.get(i).getOrder().get(j).getSku() + "','" + tamil_pval + "','" + telugu_pval + "','" + punjabi_pval + "','" + hindi_pval + "','" + bengali_pval + "','" + kannada_pval + "','" + assam_pval + "','" + orissa_pval + "','" + marathi_pval + "')";
                                    database.execSQL(insertProduct);
                                } else {
//                                Log.v("prod_name1", detailVal.get(i).getOrder().get(j).getQty() + "--" + detailVal.get(i).getShipmentid());
                                    product_name = detailVal.get(i).getOrder().get(j).getName();
                                    String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                    String updateProducts = "UPDATE ProductDetails set product_name = '" + name_split + "', amount = '" + detailVal.get(i).getOrder().get(j).getPrice() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getQty() + "',tamil = '" + tamil_pval + "',telugu = '" + telugu_pval + "'," +
                                            "punjabi = '" + punjabi_pval + "',hindi = '" + hindi_pval + "', bengali = '" + bengali_pval + "', kannada = '" + kannada_pval + "',assam = '" + assam_pval + "', orissa = '" + orissa_pval + "'," +
                                            "marathi = '" + marathi_pval + "'" +
                                            " where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' ";
                                    database.execSQL(updateProducts);
                                }
                                checkProducts.close();
                            } else {

                                Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                                detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' AND pickup_type = 1",
                                        null);
                                if (checkProducts.getCount() == 0) {

                                    product_name = detailVal.get(i).getOrder().get(j).getName();
                                    String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                    String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi,pickup_type)" +
                                            " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                            + "', '" + detailVal.get(i).getOrder().get(j).getQty() + "', '" + detailVal.get(i).getOrder().get(j).getPrice() + "', '" +
                                            detailVal.get(i).getOrder().get(j).getSku() + "','" + tamil_pval + "','" + telugu_pval + "','" + punjabi_pval + "','" + hindi_pval + "','" + bengali_pval + "','" + kannada_pval + "','" + assam_pval + "','" + orissa_pval + "','" + marathi_pval + "',1)";
                                    database.execSQL(insertProduct);
                                } else {
//                                Log.v("prod_name1", detailVal.get(i).getOrder().get(j).getQty() + "--" + detailVal.get(i).getShipmentid());
                                    product_name = detailVal.get(i).getOrder().get(j).getName();
                                    String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                    String updateProducts = "UPDATE ProductDetails set product_name = '" + name_split + "', amount = '" + detailVal.get(i).getOrder().get(j).getPrice() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getQty() + "',tamil = '" + tamil_pval + "',telugu = '" + telugu_pval + "'," +
                                            "punjabi = '" + punjabi_pval + "',hindi = '" + hindi_pval + "', bengali = '" + bengali_pval + "', kannada = '" + kannada_pval + "',assam = '" + assam_pval + "', orissa = '" + orissa_pval + "'," +
                                            "marathi = '" + marathi_pval + "' , pickup_type = 1 " +
                                            " where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' ";
                                    database.execSQL(updateProducts);
                                }
                                checkProducts.close();

                            }

                           /* Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                            detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' AND pickup_type = 0",
                                    null);
                            try {
                                if (checkProducts.getCount() == 0) {
                                    product_name = detailVal.get(i).getOrder().get(j).getName();
                                    String name_split = product_name.replace("'", "");

                                    String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi)" +
                                            " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                            + "', '" + detailVal.get(i).getOrder().get(j).getQty() + "', '" + detailVal.get(i).getOrder().get(j).getPrice() + "', '" +
                                            detailVal.get(i).getOrder().get(j).getSku() + "','"+tamil_pval+"','"+telugu_pval+"','"+punjabi_pval+"','"+hindi_pval+"','"+bengali_pval+"','"+kannada_pval+"','"+assam_pval+"','"+orissa_pval+"','"+marathi_pval+"')";
                                    database.execSQL(insertProduct);
                                } else {

                                    product_name = detailVal.get(i).getOrder().get(j).getName();
                                    String name_split = product_name.replace("'", "");

                                    String updateProducts = "UPDATE ProductDetails set product_name = '" + name_split + "',amount = '" + detailVal.get(i).getOrder().get(j).getPrice() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getQty() + "',tamil = '"+tamil_pval+"',telugu = '"+telugu_pval+"'," +
                                            "punjabi = '"+punjabi_pval+"',hindi = '"+hindi_pval+"', bengali = '"+bengali_pval+"', kannada = '"+kannada_pval+"',assam = '"+assam_pval+"', orissa = '"+orissa_pval+"'," +
                                            "marathi = '"+marathi_pval+"'" +
                                            " where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' ";
                                    database.execSQL(updateProducts);

                                }
                            } finally {
                                checkProducts.close();
                            }*/

                            if (!detailVal.get(i).getOrder().get(j).getP_sku().equals("")) {

                                Cursor checkPickupProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                                detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getP_sku() + "' AND pickup_type = 1 ",
                                        null);
                                if (checkPickupProducts.getCount() == 0) {
                                    Log.v("getPickupProds", " - " + detailVal.get(i).getOrder().get(j).getP_sku());
//                                    Log.v("getPickupProds", " - "+ checkPickupProducts.getString(checkPickupProducts.getColumnIndex("pickup_type")) );
                                    product_name = detailVal.get(i).getOrder().get(j).getP_skuname();
                                    String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                    String insertPickupProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,pickup_type)" +
                                            " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                            + "', '" + detailVal.get(i).getOrder().get(j).getP_skuqty() + "', '" + detailVal.get(i).getOrder().get(j).getP_price() + "', '" +
                                            detailVal.get(i).getOrder().get(j).getP_sku() + "', 1)";
                                    database.execSQL(insertPickupProduct);
                                } else {
                                    Log.v("getPickupProds1", " - " + detailVal.get(i).getOrder().get(j).getP_sku());
                                    product_name = detailVal.get(i).getOrder().get(j).getP_skuname();
                                    String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                    String updatePickupProducts = "UPDATE ProductDetails set product_name = '" + name_split + "', amount = '" + detailVal.get(i).getOrder().get(j).getP_price() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getP_skuqty() + "' " +
                                            "where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getP_sku() + "' AND pickup_type = 1 ";
                                    database.execSQL(updatePickupProducts);
                                }
                                checkPickupProducts.close();
                            }

                        }
                        try {
//                            changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
//                            new MyTask(order.getId(),detailVal.get(i).getShipmentid()).execute();
                        } catch (Exception ex) {
                        }
//                        changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
                        checkOrder.close();
                    }
                } else if (value.getResMsg().equals("order failed")) {
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    Intent intent = new Intent("SyncServiceAction");
                    Bundle bundle = new Bundle();
                    bundle.putString("current_time", currentDateTimeString);
                    bundle.putString("sync", "sync");
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                } else {

                }

            }

            @Override
            public void onError(Throwable e) {
                Log.d("dataerror", e.toString());
            }

            @Override
            public void onComplete() {
                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private void changeOrderStatus(String runnerID, String shipmentID) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);
        OrderStatusReq orderStatus = new OrderStatusReq();
        JSONObject paramObject = null;
        orderStatus.setRunnerID(runnerID);
        orderStatus.setShipmentID(shipmentID);
        try {
            paramObject = new JSONObject();
            paramObject.put("runner_id", orderStatus.getRunnerID());
            paramObject.put("shipment_id", orderStatus.getShipmentID());
//            Log.v("orderstatus", runnerID + "-" + shipmentID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

        final Observable<OrderChangeResp> observable = apiService.getOrderStatus(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<OrderChangeResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(OrderChangeResp value) {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                orderList = new ArrayList<>();
                OrderChangeResp detailVal = value;

                Intent intent = new Intent("SyncServiceAction");
                Bundle bundle = new Bundle();
                bundle.putString("current_time", currentDateTimeString);
                intent.putExtras(bundle);
                sendBroadcast(intent);
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                uploadComplete();
//                uploadImage();

            }

            @Override
            public void onError(Throwable e) {
                Log.d("changeerror", e.toString());
            }

            @Override
            public void onComplete() {
                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }


    //    public void uploadComplete(final String ship_no) {  //removed on 10-08-2018
    public void uploadComplete() {
        Log.v("uploadComplete", "--" + "");
//        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
//        int battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        battery_level = BatteryManager.EXTRA_LEVEL;
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));

        String currentDateTimeString = format.format(new Date());

        /*** removed on 10-08-2018 ***/
    /*    Cursor customerName = database.rawQuery("select DISTINCT O.sync_status,O.delivery_status, O.order_number, O.valid, O.attempt_count, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber, IFNULL(D.customer_name,0) as customer_name,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(D.amount_collected,0) as amount_collected,IFNULL(O.invoice_amount,0) as invoice_amount,IFNULL(D.customer_contact_number  ,0) as phone,IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city,IFNULL(D.Invoice_proof,0) as Invoice_proof,IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof,0) as signature_proof,IFNULL(D.latitude,0) as latitude,IFNULL(D.longitude,0) as longitude, IFNULL(D.pin_code,0) as pin_code,IFNULL(D.adhaar_details,0) as adhaar_details, IFNULL(D.landmark,0) as landmark, IFNULL(D.redirect, 0) as redirect,IFNULL(D.reason,0) as reason,IFNULL(D.created_at  ,0) as created_at,IFNULL(P" +
                ".product_name,0) as product_name,IFNULL(P.quantity,0) as quantity,IFNULL(P.amount,0) as amount," +
                "IFNULL(P.product_code,0) as product_code,IFNULL(P.amount_collected,0) as " +
                "product_amount_collected,IFNULL(P.delivery_qty,0) as delivery_qty from orderheader O  INNER JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number INNER JOIN PRODUCTDETAILS P ON D.shipmentnumber = P.shipmentnumber where O.sync_status='C' AND D.shipmentnumber = '" + ship_no + "' ", null);*/
        Cursor customerName = database.rawQuery("select DISTINCT O.sync_status,O.delivery_to,IFNULL(bulk_Shipment_append,0) as bulk_Shipment_append,O.delivery_status, O.order_number, O.valid,O.order_type, O.attempt_count, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber, " +
                "IFNULL(D.customer_name,0) as customer_name,IFNULL(O.referenceNumber,0) as referenceNumber," +
                "IFNULL(D.amount_collected,0) as amount_collected,IFNULL(O.invoice_amount,0) as invoice_amount," +
                "IFNULL(D.customer_contact_number  ,0) as phone,IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city,IFNULL(D.Invoice_proof,0) as Invoice_proof,IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.relation_proof, 0) as relation_proof,IFNULL(D.received_by, 0) as received_by,IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof,0) as signature_proof,IFNULL(D.latitude,0) as latitude,IFNULL(D.longitude,0) as longitude, IFNULL(D.pin_code,0) as pin_code,IFNULL(D.adhaar_details,0) as adhaar_details, IFNULL(D.landmark,0) as landmark, IFNULL(D.redirect, 0) as redirect,IFNULL(D.reason,0) as reason,IFNULL(D.created_at  ,0) as created_at,IFNULL(D.feed_back,0) as feed_back,IFNULL(D.verify,0) as verify, IFNULL(D.neft,'') as neft,IFNULL(D.aadhar_voter_type,'') as proof_type from orderheader O  INNER JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where O.sync_status='C'  LIMIT 1 ", null);

        //        Log.v("partial_shipAddress1", "--" + customerName.getCount());
        if (customerName.getCount() > 0) {
            customerName.moveToFirst();
            while (!customerName.isAfterLast()) {
                final String partial_shipAddress = customerName.getString(customerName.getColumnIndex("shipmentnumber"));
                final String bulkShipmentAppend = customerName.getString(customerName.getColumnIndex("bulk_Shipment_append"));
                Log.v("sync_service_shipAdd", "--" + partial_shipAddress);
//                Log.v("---------", "--" + "----");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getRequestHeader())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                final PartialReq partialPelivery = new PartialReq();
                PartialReq.FieldData fieldData = new PartialReq.FieldData();

                JSONObject paramObject = null; // Main JSON Object
                JSONObject jsonFieldObj; // FieldData JSON Object
                JSONObject jsonPickObj;
                JSONArray jsonDetailsArray; // Details JSON Array
                JSONObject jsonAmountCollected; // Amount Collected JSON Object
                JSONObject jsonDummy;
                JSONArray jsonItemCodeArray; // Itemcode JSON Array
                JSONObject jsonItemCodeObject; // Itemcode JSON Object
                JSONObject jsonProofFieldObj;

                partialPelivery.setRunner_id(AppController.getStringPreference(Constants.USER_ID, ""));
                partialPelivery.setReference(customerName.getString(customerName.getColumnIndex("referenceNumber")));
                partialPelivery.setCustomer(customerName.getString(customerName.getColumnIndex("customer_name")));
                partialPelivery.setAddress(customerName.getString(customerName.getColumnIndex("shipping_address")));
                partialPelivery.setLandmark(customerName.getString(customerName.getColumnIndex("landmark")));
                partialPelivery.setPincode(customerName.getString(customerName.getColumnIndex("pin_code")));
                partialPelivery.setLatitude(customerName.getString(customerName.getColumnIndex("latitude")));
                partialPelivery.setLongitude(customerName.getString(customerName.getColumnIndex("longitude")));
                partialPelivery.setPhone(customerName.getString(customerName.getColumnIndex("phone")));
                partialPelivery.setDeliveryproof(customerName.getString(customerName.getColumnIndex("delivery_proof")));
                partialPelivery.setInvoiceproof(customerName.getString(customerName.getColumnIndex("Invoice_proof")));
                partialPelivery.setRelationproof(customerName.getString(customerName.getColumnIndex("relation_proof")));
                partialPelivery.setAddressproof(customerName.getString(customerName.getColumnIndex("id_proof")));
                partialPelivery.setSignproof(customerName.getString(customerName.getColumnIndex("signature_proof")));

                bfilBulkCheck = customerName.getString(customerName.getColumnIndex("delivery_to"));

                if (bfilBulkCheck.equalsIgnoreCase("1")) {
                    partialPelivery.setShipmentnumber(customerName.getString(customerName.getColumnIndex("bulk_Shipment_append")));
                } else {
                    partialPelivery.setShipmentnumber(customerName.getString(customerName.getColumnIndex("shipmentnumber")));
                }


                partialPelivery.setOrderNo(customerName.getString(customerName.getColumnIndex("order_number")));
                partialPelivery.setAadhaarDetails(customerName.getString(customerName.getColumnIndex("adhaar_details")));
                partialPelivery.setAttempt(customerName.getInt(customerName.getColumnIndex("attempt_count")));
                partialPelivery.setRedirect(customerName.getString(customerName.getColumnIndex("redirect")));
                partialPelivery.setReason(customerName.getString(customerName.getColumnIndex("reason")));
                partialPelivery.setFeedback(customerName.getString(customerName.getColumnIndex("feed_back")));
                partialPelivery.setVerify(customerName.getString(customerName.getColumnIndex("verify")));
                partialPelivery.setProof_type(customerName.getString(customerName.getColumnIndex("proof_type")));
                partialPelivery.setReceived_by(customerName.getString(customerName.getColumnIndex("received_by")));
                Log.v("getPartialCount", "_ " + customerName.getInt(customerName.getColumnIndex("attempt_count")));
                attempt_count = partialPelivery.getAttempt();
                attempt_count++;
//                Log.v("delivery_status", customerName.getString(customerName.getColumnIndex("sync_status")));

                if (customerName.getString(customerName.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {
                    fieldData.setAmountCollected(customerName.getString(customerName.getColumnIndex("amount_collected")));
                    partialPelivery.setModeType("Cash");
                    partialPelivery.setAmount_tot(customerName.getString(customerName.getColumnIndex("order_number")));
                    partialPelivery.setTransactionNum("N/A");
                    partialPelivery.setRemarks("N/A");
                    partialPelivery.setReceipt("N/A");
                    partialPelivery.setOriginalAmount(customerName.getString(customerName.getColumnIndex("invoice_amount")));

                    partialPelivery.setActualAmount(customerName.getString(customerName.getColumnIndex("amount_collected")));
                   /* fieldData.setSkuActualQty(customerName.getString(customerName.getColumnIndex("quantity")));
                    fieldData.setProductCode(customerName.getString(customerName.getColumnIndex("product_code")));
                    fieldData.setProductName(customerName.getString(customerName.getColumnIndex("product_name")));
                    fieldData.setQuantity(customerName.getString(customerName.getColumnIndex("delivery_qty")));
                    fieldData.setAmount(customerName.getString(customerName.getColumnIndex("product_amount_collected")));*/

//                    Log.v("paramObject", partialPelivery.getModeType());
                } else {

                }

                try {
                    jsonItemCodeArray = new JSONArray();
                    jsonItemCodeObject = new JSONObject();
                    jsonFieldObj = new JSONObject();
                    jsonPickObj = new JSONObject();
                    jsonDetailsArray = new JSONArray();
                    jsonAmountCollected = new JSONObject();
                    jsonDummy = new JSONObject();
                    paramObject = new JSONObject();
                    jsonProofFieldObj = new JSONObject();

                    paramObject.put("runsheetNo", partialPelivery.getRunner_id());
                    paramObject.put("actualAmount", partialPelivery.getActualAmount());
                    paramObject.put("originalAmount", partialPelivery.getOriginalAmount());
                    paramObject.put("moneyTransactionType", "cash");
                    paramObject.put("attemptCount", "1");
                    paramObject.put("jobType", "delivery_new");
                    paramObject.put("referenceNumber", partialPelivery.getReference());
                    paramObject.put("employeeCode", partialPelivery.getRunner_id());
                    paramObject.put("hubCode", "hub code");
                    paramObject.put("status", "delivered");
                    paramObject.put("battery", battery_level);
                    paramObject.put("deviceInfo", AppController.getdevice());
                    paramObject.put("lastTransactionTime", currentDateTimeString);
                    paramObject.put("erpPushTime", customerName.getString(customerName.getColumnIndex("valid")));
//                    paramObject.put("transactionDate", currentDateTimeString);
                    paramObject.put("transactionDate", customerName.getString(customerName.getColumnIndex("created_at")));
                    paramObject.put("created_at", customerName.getString(customerName.getColumnIndex("created_at")));

                    paramObject.put("runner_id", partialPelivery.getRunner_id());
                    paramObject.put("reference", partialPelivery.getReference());
                    paramObject.put("customer", partialPelivery.getCustomer());
                    paramObject.put("address", partialPelivery.getAddress());
                    paramObject.put("landmark", partialPelivery.getLandmark());
                    paramObject.put("pincode", partialPelivery.getPincode());
                    paramObject.put("latitude", partialPelivery.getLatitude());
                    paramObject.put("longitude", partialPelivery.getLongitude());
                    paramObject.put("deliveryproof", image_url + partialPelivery.getDeliveryproof());
                    paramObject.put("invoiceproof", image_url + partialPelivery.getInvoiceproof());
                    if (!partialPelivery.getRelationproof().equalsIgnoreCase("")) {
                        paramObject.put("otherproof", image_url + partialPelivery.getRelationproof());
                    } else {
                        paramObject.put("otherproof", partialPelivery.getRelationproof());
                    }
                    paramObject.put("addressproof", image_url + partialPelivery.getAddressproof());
                    paramObject.put("signproof", image_url + partialPelivery.getSignproof());
                    paramObject.put("shipmentnumber", partialPelivery.getShipmentnumber());
                    paramObject.put("order_no", partialPelivery.getOrderNo());
                    paramObject.put("aadhaar_details", partialPelivery.getAadhaarDetails());
                    paramObject.put("attemptCount", attempt_count);
                    paramObject.put("redirect", partialPelivery.getRedirect());
                    paramObject.put("feedback", partialPelivery.getFeedback());
                    paramObject.put("verify", partialPelivery.getVerify());
                    paramObject.put("neft", customerName.getString(customerName.getColumnIndex("neft")));
                    paramObject.put("proof_type", partialPelivery.getProof_type());
                    paramObject.put("received_by", partialPelivery.getReceived_by());
                    paramObject.put("order_type", customerName.getString(customerName.getColumnIndex("order_type")));


                    if (!customerName.getString(customerName.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {

                        jsonFieldObj.put("customer_delivery_proof", image_url + partialPelivery.getDeliveryproof());
                        jsonFieldObj.put("invoice", image_url + partialPelivery.getInvoiceproof());
                        jsonFieldObj.put("signproof", image_url + partialPelivery.getSignproof());
                        jsonFieldObj.put("govt_id_proof", image_url + partialPelivery.getAddressproof());
                        jsonFieldObj.put("phone_no", partialPelivery.getPhone());
                        jsonFieldObj.put("aadhaar_card", partialPelivery.getAadhaarDetails());
                        jsonFieldObj.put("name", partialPelivery.getCustomer());
                        jsonFieldObj.put("pincode", partialPelivery.getPincode());
                        jsonFieldObj.put("amount_collected", fieldData.getAmountCollected());
                        paramObject.put("fieldData", jsonFieldObj);
                    }

                    if (customerName.getString(customerName.getColumnIndex("order_type")).equalsIgnoreCase("3")) {

                        Cursor getPickup = database.rawQuery("Select orderno,shipmentno,customername,customerphone,customeraddress,customerphoto,pickup_completed,pickupstatus,createdate,IFNULL(latitude,0 ) as latitude, IFNULL(longitude,0) as longitude  from PickupConfirmation where shipmentno = '" + partial_shipAddress + "' ", null);
                        JSONArray arrayPick = new JSONArray();

                        if (getPickup.getCount() > 0) {
                            getPickup.moveToFirst();
                            ArrayList<String> list = new ArrayList<String>();
                            while (!getPickup.isAfterLast()) {

                                JSONObject obj = new JSONObject();
                                JSONObject picklist = new JSONObject();

                                try {

                                    jsonPickObj.put("orderno", getPickup.getString(getPickup.getColumnIndex("orderno")));
                                    jsonPickObj.put("referenceNumber", partialPelivery.getReference());
                                    jsonPickObj.put("shipmentno", getPickup.getString(getPickup.getColumnIndex("shipmentno")));
                                    jsonPickObj.put("customername", getPickup.getString(getPickup.getColumnIndex("customername")));
                                    jsonPickObj.put("customerphone", getPickup.getString(getPickup.getColumnIndex("customerphone")));
                                    jsonPickObj.put("customeraddress", getPickup.getString(getPickup.getColumnIndex("customeraddress")));
                                    jsonPickObj.put("customerphoto", image_url + getPickup.getString(getPickup.getColumnIndex("customerphoto")));
                                    jsonPickObj.put("pickup_completed", getPickup.getString(getPickup.getColumnIndex("pickup_completed")));
                                    jsonPickObj.put("pickupstatus", getPickup.getString(getPickup.getColumnIndex("pickupstatus")));
                                    jsonPickObj.put("createdate", getPickup.getString(getPickup.getColumnIndex("createdate")));
                                    jsonPickObj.put("latitude", getPickup.getString(getPickup.getColumnIndex("latitude")));
                                    jsonPickObj.put("longitude", getPickup.getString(getPickup.getColumnIndex("longitude")));
                                    jsonPickObj.put("attempt", attempt_count);

                                    Cursor getOrders = database.rawQuery("Select IFNULL(delivery_qty,0) as delivery_qty, IFNULL(product_code, 0) as product_code, IFNULL(product_name, '') as product_name," +
                                            "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected, IFNULL(partial_reason, '') as partial_reason, IFNULL(r_id, 0) as r_id   from ProductDetails where shipmentnumber = '" + partial_shipAddress + "' AND pickup_type = 1  ", null);
                                    JSONArray array = new JSONArray();

                                    if (getOrders.getCount() > 0) {
                                        getOrders.moveToFirst();
                                        ArrayList<String> list1 = new ArrayList<String>();
                                        while (!getOrders.isAfterLast()) {

                                            JSONObject list2 = new JSONObject();

                                            try {
                                                Log.v("getPickup", "- " + getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
                                                list2.put("sku_actual_quantity", getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
                                                list2.put("product_code", getOrders.getString(getOrders.getColumnIndex("product_code")));
                                                list2.put("product_name", getOrders.getString(getOrders.getColumnIndex("product_name")));
                                                list2.put("quantity", getOrders.getString(getOrders.getColumnIndex("quantity")));
                                                list2.put("amount", getOrders.getString(getOrders.getColumnIndex("amount_collected")));
                                                list2.put("partial_reason", getOrders.getString(getOrders.getColumnIndex("partial_reason")));
                                                list2.put("reason_id", getOrders.getString(getOrders.getColumnIndex("r_id")));
                                                array.put(list2);
                                                jsonDummy.put("pickupProd", array);

                                            } catch (JSONException e1) {
                                                // TODO Auto-generated catch block
                                                e1.printStackTrace();
                                            }

                                            getOrders.moveToNext();
                                        }
                                        jsonPickObj.put("pickup", jsonDummy);
                                    }
                                    getOrders.close();


                                } catch (JSONException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }

                                getPickup.moveToNext();
                            }

                        }
                        getPickup.close();
                    }

                    paramObject.put("pickupData", jsonPickObj);

                    if (customerName.getString(customerName.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {
                        paramObject.put("status", "partial delivery");
                        jsonFieldObj.put("Original_Amount", partialPelivery.getOriginalAmount());
                        jsonFieldObj.put("Actual_Amount", partialPelivery.getActualAmount());

                        jsonAmountCollected.put("Mode_Type", partialPelivery.getModeType());
                        jsonAmountCollected.put("Amount", partialPelivery.getAmount_tot());
                        jsonAmountCollected.put("Transaction_Number", partialPelivery.getTransactionNum());
                        jsonAmountCollected.put("Remarks", partialPelivery.getRemarks());
                        jsonAmountCollected.put("Receipt", partialPelivery.getReceipt());

                        jsonDetailsArray.put(jsonAmountCollected);
                        jsonFieldObj.put("details", jsonDetailsArray);
                        jsonDummy.put("amount_collected", jsonFieldObj);
                        jsonDummy.put("customer_delivery_proof", image_url + partialPelivery.getDeliveryproof());
                        jsonDummy.put("invoice", image_url + partialPelivery.getInvoiceproof());

                        if (!partialPelivery.getRelationproof().equalsIgnoreCase("")) {
                            jsonDummy.put("otherproof", image_url + partialPelivery.getRelationproof());
                        } else {
                            jsonDummy.put("otherproof", partialPelivery.getRelationproof());
                        }
                        //jsonDummy.put("relation", image_url + partialPelivery.getRelationproof());


                        jsonDummy.put("signproof", image_url + partialPelivery.getSignproof());
                        jsonDummy.put("govt_id_proof", image_url + partialPelivery.getAddressproof());
                        jsonDummy.put("name", partialPelivery.getCustomer());
                        jsonDummy.put("reason", partialPelivery.getReason());
//                        paramObject.put("fieldData", jsonProofFieldObj);
//                        Cursor getOrders = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" + partial_shipAddress + "' ", null);
                        Cursor getOrders = database.rawQuery("Select IFNULL(delivery_qty,0) as delivery_qty, IFNULL(product_code, 0) as product_code, IFNULL(product_name, '') as product_name," +

                                "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected, IFNULL(partial_reason, '') as partial_reason, IFNULL(r_id, 0) as r_id from ProductDetails where shipmentnumber = '" + partial_shipAddress + "' AND pickup_type = 0 ", null);

                        JSONArray array = new JSONArray();

                        if (getOrders.getCount() > 0) {
                            getOrders.moveToFirst();
                            ArrayList<String> list = new ArrayList<String>();
                            while (!getOrders.isAfterLast()) {

                                JSONObject obj = new JSONObject();
                                JSONObject list1 = new JSONObject();

                                try {

                                    list1.put("sku_actual_quantity", getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
                                    list1.put("product_code", getOrders.getString(getOrders.getColumnIndex("product_code")));
                                    list1.put("product_name", getOrders.getString(getOrders.getColumnIndex("product_name")));
                                    list1.put("quantity", getOrders.getString(getOrders.getColumnIndex("quantity")));
                                    list1.put("amount", getOrders.getString(getOrders.getColumnIndex("amount_collected")));
                                    list1.put("partial_reason", getOrders.getString(getOrders.getColumnIndex("partial_reason")));
                                    list1.put("reason_id", getOrders.getString(getOrders.getColumnIndex("r_id")));
                                    array.put(list1);
                                    jsonDummy.put("item_code", array);

                                } catch (JSONException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }

                                getOrders.moveToNext();
                            }

                        }
                        getOrders.close();

                        paramObject.put("fieldData", jsonDummy);
                    }
                    Log.v("testparamObject", String.valueOf(paramObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                final Observable<PartialResp> observable;

                if (!bfilBulkCheck.equalsIgnoreCase("1")) {

                    observable = apiService.getPartialDeliverySync(requestBody)
                            .subscribeOn
                                    (Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread());
                } else {
                    observable = apiService.getBulkBfilDeliverySync(requestBody)
                            .subscribeOn
                                    (Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread());
                }

                observable.subscribe(new Observer<PartialResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PartialResp value) {
                        List<PartialResp> orderVal = value.getPartialDelivery();
                        Log.v("mainactivity_resmsg", value.getRes_msg() + " -" + value.getRes_code());
                        if (value.getRes_msg().equals("upload success")) {
                            Log.v("mainactivity_resmsg", value.getRes_msg() + " " + partial_shipAddress);
                       /*     database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '"+attempt_count+"',image_status = 'U' where Shipment_Number ='" +
                                    ship_no + "' ");*/

                            if (bfilBulkCheck.equalsIgnoreCase("1")) {

                                database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number IN ( " + bulkShipmentAppend + ") AND delivery_to='1'  ");

                            } else {
                                database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number ='" +
                                        partial_shipAddress + "' ");
                            }
//                            uploadImage(partialPelivery.getShipmentnumber());
//                            Logger.showShortMessage(DeliveryActivity.this,value.getRes_msg());

                        } else if (value.getRes_msg().equals("upload failed")) {
                            Log.v("mainactivity_resmsg", value.getRes_msg() + "E status");
                            // removed 08-11-2018
                          /* database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number ='" +
                                    partial_shipAddress + "' ");*/
                            database.execSQL("UPDATE orderheader set sync_status = 'E' where Shipment_Number ='" +
                                    partial_shipAddress + "' ");
//                            Logger.showShortMessage(SyncService.this, value.getRes_msg());
                        } else if (value.getRes_msg().equals("order unassigned")) {
//                            Logger.showShortMessage(SyncService.this, value.getRes_msg());
                            // removed 08-11-2018
                            /*database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number ='" +
                                    partial_shipAddress + "' ");*/
                            database.execSQL("UPDATE orderheader set sync_status = 'E' where Shipment_Number ='" +
                                    partial_shipAddress + "' ");
                        } else if (value.getRes_msg().equals("already delivered")) {
                            Log.v("mainactivity_resmsg", value.getRes_msg() + "already status");
                            // removed 08-11-2018
                           /* database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number ='" +
                                    partial_shipAddress + "' ");*/
                            database.execSQL("UPDATE orderheader set sync_status = 'E' where Shipment_Number ='" +
                                    partial_shipAddress + "' ");
                        }
                        pageTrackerService();
//                        orderSyncStatus();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("upcomperror", e.toString());
                    }

                    @Override
                    public void onComplete() {
//                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
                customerName.moveToNext();
            }
        } else {
          /*  Cursor customerNameErrOrd = database.rawQuery("select DISTINCT O.sync_status,O.delivery_status, O.order_number, O.valid, O.attempt_count, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber, IFNULL(D.customer_name,0) as customer_name,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(D.amount_collected,0) as amount_collected,IFNULL(O.invoice_amount,0) as invoice_amount,IFNULL(D.customer_contact_number  ,0) as phone,IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city,IFNULL(D.Invoice_proof,0) as Invoice_proof,IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof,0) as signature_proof,IFNULL(D.latitude,0) as latitude,IFNULL(D.longitude,0) as longitude, IFNULL(D.pin_code,0) as pin_code,IFNULL(D.adhaar_details,0) as adhaar_details, IFNULL(D.landmark,'') as landmark,  IFNULL(D.redirect,'') as redirect,IFNULL(D.reason,0) as reason,IFNULL(D.created_at  ,0) as created_at,IFNULL(P" +
                    ".product_name,0) as product_name,IFNULL(P.quantity,0) as quantity,IFNULL(P.amount,0) as amount," +
                    "IFNULL(P.product_code,0) as product_code,IFNULL(P.amount_collected,0) as " +
                    "product_amount_collected,IFNULL(P.delivery_qty,0) as delivery_qty from orderheader O  INNER JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number INNER JOIN PRODUCTDETAILS P ON D.shipmentnumber = P.shipmentnumber where O.sync_status='E' LIMIT 1 ", null);*/
            Cursor customerNameErrOrd = database.rawQuery("select DISTINCT IFNULL(bulk_Shipment_append,0) as bulk_Shipment_append,O.delivery_to,O.sync_status,O.delivery_status, O.order_number, O.valid,O.order_type, O.attempt_count, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber, " +
                    "IFNULL(D.customer_name,0) as customer_name,IFNULL(O.referenceNumber,0) as referenceNumber," +
                    "IFNULL(D.amount_collected,0) as amount_collected,IFNULL(O.invoice_amount,0) as invoice_amount," +
                    "IFNULL(D.customer_contact_number  ,0) as phone,IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city,IFNULL(D.Invoice_proof,0) as Invoice_proof,IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.relation_proof, 0) as relation_proof,IFNULL(D.received_by, 0) as received_by,IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof,0) as signature_proof,IFNULL(D.latitude,0) as latitude,IFNULL(D.longitude,0) as longitude, IFNULL(D.pin_code,0) as pin_code,IFNULL(D.adhaar_details,0) as adhaar_details, IFNULL(D.landmark,0) as landmark, IFNULL(D.redirect, 0) as redirect,IFNULL(D.reason,0) as reason,IFNULL(D.created_at  ,0) as created_at,IFNULL(D.feed_back,0) as feed_back,IFNULL(D.verify,0) as verify, IFNULL(D.neft,'') as neft,IFNULL(D.aadhar_voter_type,'') as proof_type from orderheader O  INNER JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where O.sync_status='E' LIMIT 1 ", null);

            if (customerNameErrOrd.getCount() > 0) {
                customerNameErrOrd.moveToFirst();
                while (!customerNameErrOrd.isAfterLast()) {
                    final String partial_shipAddress = customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("shipmentnumber"));
                    final String bulkShipmentAppend = customerNameErrOrd.getString(customerName.getColumnIndex("bulk_Shipment_append"));
                    Log.v("sync_service_shipAdd1", "--" + partial_shipAddress);
                    Log.v("---------", "--" + "----");


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(getRequestHeader())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                    InthreeApi apiService = retrofit.create(InthreeApi.class);

                    final PartialReq partialPelivery = new PartialReq();
                    PartialReq.FieldData fieldData = new PartialReq.FieldData();

                    JSONObject paramObject = null; // Main JSON Object
                    JSONObject jsonFieldObj; // FieldData JSON Object
                    JSONObject jsonPickObj;
                    JSONArray jsonDetailsArray; // Details JSON Array
                    JSONObject jsonAmountCollected; // Amount Collected JSON Object
                    JSONObject jsonDummy;
                    JSONArray jsonItemCodeArray; // Itemcode JSON Array
                    JSONObject jsonItemCodeObject; // Itemcode JSON Object
                    JSONObject jsonProofFieldObj;

                    partialPelivery.setRunner_id(AppController.getStringPreference(Constants.USER_ID, ""));
                    partialPelivery.setReference(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("referenceNumber")));
                    partialPelivery.setCustomer(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("customer_name")));
                    partialPelivery.setAddress(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("shipping_address")));
                    partialPelivery.setLandmark(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("landmark")));
                    partialPelivery.setPincode(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("pin_code")));
                    partialPelivery.setLatitude(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("latitude")));
                    partialPelivery.setLongitude(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("longitude")));
                    partialPelivery.setPhone(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("phone")));
                    partialPelivery.setDeliveryproof(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("delivery_proof")));
                    partialPelivery.setInvoiceproof(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("Invoice_proof")));
                    partialPelivery.setRelationproof(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("relation_proof")));
                    partialPelivery.setAddressproof(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("id_proof")));
                    partialPelivery.setSignproof(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("signature_proof")));

                    final String bfilBulkCheck = customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("delivery_to"));

                    if (bfilBulkCheck.equalsIgnoreCase("1")) {
                        partialPelivery.setShipmentnumber(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("bulk_Shipment_append")));

                    } else {
                        partialPelivery.setShipmentnumber(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("shipmentnumber")));
                    }
                    partialPelivery.setOrderNo(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("order_number")));
                    partialPelivery.setAadhaarDetails(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("adhaar_details")));
                    partialPelivery.setAttempt(customerNameErrOrd.getInt(customerNameErrOrd.getColumnIndex("attempt_count")));
                    partialPelivery.setRedirect(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("redirect")));
                    partialPelivery.setReason(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("reason")));
                    attempt_count = partialPelivery.getAttempt();
                    attempt_count++;
                    Log.v("delivery_status", customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("sync_status")));

                    if (customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {
                        fieldData.setAmountCollected(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("amount_collected")));
                        partialPelivery.setModeType("Cash");
                        partialPelivery.setAmount_tot(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("order_number")));
                        partialPelivery.setTransactionNum("N/A");
                        partialPelivery.setRemarks("N/A");
                        partialPelivery.setReceipt("N/A");
                        partialPelivery.setOriginalAmount(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("invoice_amount")));

                        partialPelivery.setActualAmount(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("amount_collected")));
                      /*  fieldData.setSkuActualQty(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("quantity")));
                        fieldData.setProductCode(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("product_code")));
                        fieldData.setProductName(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("product_name")));
                        fieldData.setQuantity(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("delivery_qty")));
                        fieldData.setAmount(customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("product_amount_collected")));*/

                        Log.v("paramObject", partialPelivery.getModeType());
                    } else {

                    }

                    try {
                        jsonItemCodeArray = new JSONArray();
                        jsonItemCodeObject = new JSONObject();
                        jsonFieldObj = new JSONObject();
                        jsonPickObj = new JSONObject();
                        jsonDetailsArray = new JSONArray();
                        jsonAmountCollected = new JSONObject();
                        jsonDummy = new JSONObject();
                        paramObject = new JSONObject();
                        jsonProofFieldObj = new JSONObject();

                        paramObject.put("runsheetNo", partialPelivery.getRunner_id());
                        paramObject.put("actualAmount", partialPelivery.getActualAmount());
                        paramObject.put("originalAmount", partialPelivery.getOriginalAmount());
                        paramObject.put("moneyTransactionType", "cash");
                        paramObject.put("attemptCount", "1");
                        paramObject.put("jobType", "delivery_new");
                        paramObject.put("referenceNumber", partialPelivery.getReference());
                        paramObject.put("employeeCode", partialPelivery.getRunner_id());
                        paramObject.put("hubCode", "hub code");
                        paramObject.put("status", "delivered");
                        paramObject.put("battery", battery_level);
                        paramObject.put("deviceInfo", AppController.getdevice());
                        paramObject.put("lastTransactionTime", currentDateTimeString);
                        paramObject.put("erpPushTime", customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("valid")));
                        paramObject.put("transactionDate", customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("created_at")));
//                    paramObject.put("transactionDate", currentDateTimeString);
                        paramObject.put("created_at", customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("created_at")));

                        paramObject.put("runner_id", partialPelivery.getRunner_id());
                        paramObject.put("reference", partialPelivery.getReference());
                        paramObject.put("customer", partialPelivery.getCustomer());
                        paramObject.put("address", partialPelivery.getAddress());
                        paramObject.put("landmark", partialPelivery.getLandmark());
                        paramObject.put("pincode", partialPelivery.getPincode());
                        paramObject.put("latitude", partialPelivery.getLatitude());
                        paramObject.put("longitude", partialPelivery.getLongitude());
                        paramObject.put("deliveryproof", image_url + partialPelivery.getDeliveryproof());
                        paramObject.put("invoiceproof", image_url + partialPelivery.getInvoiceproof());
//                        paramObject.put("relationproof", image_url + partialPelivery.getRelationproof());
                        if (!partialPelivery.getRelationproof().equalsIgnoreCase("")) {
                            jsonDummy.put("otherproof", image_url + partialPelivery.getRelationproof());
                        } else {
                            jsonDummy.put("otherproof", partialPelivery.getRelationproof());
                        }
                        paramObject.put("addressproof", image_url + partialPelivery.getAddressproof());
                        paramObject.put("signproof", image_url + partialPelivery.getSignproof());
                        paramObject.put("shipmentnumber", partialPelivery.getShipmentnumber());
                        paramObject.put("order_no", partialPelivery.getOrderNo());
                        paramObject.put("aadhaar_details", partialPelivery.getAadhaarDetails());
                        paramObject.put("attemptCount", attempt_count);
                        paramObject.put("redirect", partialPelivery.getRedirect());

                        if (!customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {

                            jsonFieldObj.put("customer_delivery_proof", image_url + partialPelivery.getDeliveryproof());
                            jsonFieldObj.put("invoice", image_url + partialPelivery.getInvoiceproof());
                            jsonFieldObj.put("signproof", image_url + partialPelivery.getSignproof());
                            jsonFieldObj.put("govt_id_proof", image_url + partialPelivery.getAddressproof());
                            jsonFieldObj.put("phone_no", partialPelivery.getPhone());
                            jsonFieldObj.put("aadhaar_card", partialPelivery.getAadhaarDetails());
                            jsonFieldObj.put("name", partialPelivery.getCustomer());
                            jsonFieldObj.put("pincode", partialPelivery.getPincode());
                            jsonFieldObj.put("amount_collected", fieldData.getAmountCollected());
                            paramObject.put("fieldData", jsonFieldObj);
                        }

                        if (customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("order_type")).equalsIgnoreCase("3")) {

                            Cursor getPickup = database.rawQuery("Select orderno,shipmentno,customername,customerphone,customeraddress,customerphoto,pickup_completed,pickupstatus,createdate,IFNULL(latitude,0 ) as latitude, IFNULL(longitude,0) as longitude  from PickupConfirmation where shipmentno = '" + partial_shipAddress + "' ", null);
                            JSONArray arrayPick = new JSONArray();

                            if (getPickup.getCount() > 0) {
                                getPickup.moveToFirst();
                                ArrayList<String> list = new ArrayList<String>();
                                while (!getPickup.isAfterLast()) {

                                    JSONObject obj = new JSONObject();
                                    JSONObject picklist = new JSONObject();

                                    try {

                                        jsonPickObj.put("orderno", getPickup.getString(getPickup.getColumnIndex("orderno")));
                                        jsonPickObj.put("referenceNumber", partialPelivery.getReference());
                                        jsonPickObj.put("shipmentno", getPickup.getString(getPickup.getColumnIndex("shipmentno")));
                                        jsonPickObj.put("customername", getPickup.getString(getPickup.getColumnIndex("customername")));
                                        jsonPickObj.put("customerphone", getPickup.getString(getPickup.getColumnIndex("customerphone")));
                                        jsonPickObj.put("customeraddress", getPickup.getString(getPickup.getColumnIndex("customeraddress")));
                                        jsonPickObj.put("customerphoto", image_url + getPickup.getString(getPickup.getColumnIndex("customerphoto")));
                                        jsonPickObj.put("pickup_completed", getPickup.getString(getPickup.getColumnIndex("pickup_completed")));
                                        jsonPickObj.put("pickupstatus", getPickup.getString(getPickup.getColumnIndex("pickupstatus")));
                                        jsonPickObj.put("createdate", getPickup.getString(getPickup.getColumnIndex("createdate")));
                                        jsonPickObj.put("latitude", getPickup.getString(getPickup.getColumnIndex("latitude")));
                                        jsonPickObj.put("longitude", getPickup.getString(getPickup.getColumnIndex("longitude")));
                                        jsonPickObj.put("attempt", attempt_count);

                                        Cursor getOrders = database.rawQuery("Select IFNULL(delivery_qty,0) as delivery_qty, IFNULL(product_code, 0) as product_code, IFNULL(product_name, '') as product_name," +
                                                "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected, IFNULL(partial_reason, '') as partial_reason, IFNULL(r_id, 0) as r_id   from ProductDetails where shipmentnumber = '" + partial_shipAddress + "' AND pickup_type = 1  ", null);
                                        JSONArray array = new JSONArray();

                                        if (getOrders.getCount() > 0) {
                                            getOrders.moveToFirst();
                                            ArrayList<String> list1 = new ArrayList<String>();
                                            while (!getOrders.isAfterLast()) {

                                                JSONObject list2 = new JSONObject();

                                                try {
                                                    Log.v("getPickup", "- " + getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
                                                    list2.put("sku_actual_quantity", getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
                                                    list2.put("product_code", getOrders.getString(getOrders.getColumnIndex("product_code")));
                                                    list2.put("product_name", getOrders.getString(getOrders.getColumnIndex("product_name")));
                                                    list2.put("quantity", getOrders.getString(getOrders.getColumnIndex("quantity")));
                                                    list2.put("amount", getOrders.getString(getOrders.getColumnIndex("amount_collected")));
                                                    list2.put("partial_reason", getOrders.getString(getOrders.getColumnIndex("partial_reason")));
                                                    list2.put("reason_id", getOrders.getString(getOrders.getColumnIndex("r_id")));
                                                    array.put(list2);
                                                    jsonDummy.put("pickupProd", array);

                                                } catch (JSONException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                }

                                                getOrders.moveToNext();
                                            }
                                            jsonPickObj.put("pickup", jsonDummy);
                                        }
                                        getOrders.close();


                                    } catch (JSONException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }

                                    getPickup.moveToNext();
                                }

                            }
                            getPickup.close();
                        }

                        paramObject.put("pickupData", jsonPickObj);


                        if (customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {
                            paramObject.put("status", "partial delivery");
                            jsonFieldObj.put("Original_Amount", partialPelivery.getOriginalAmount());
                            jsonFieldObj.put("Actual_Amount", partialPelivery.getActualAmount());

                            jsonAmountCollected.put("Mode_Type", partialPelivery.getModeType());
                            jsonAmountCollected.put("Amount", partialPelivery.getAmount_tot());
                            jsonAmountCollected.put("Transaction_Number", partialPelivery.getTransactionNum());
                            jsonAmountCollected.put("Remarks", partialPelivery.getRemarks());
                            jsonAmountCollected.put("Receipt", partialPelivery.getReceipt());

                            jsonDetailsArray.put(jsonAmountCollected);
                            jsonFieldObj.put("details", jsonDetailsArray);
                            jsonDummy.put("amount_collected", jsonFieldObj);
                            jsonDummy.put("customer_delivery_proof", image_url + partialPelivery.getDeliveryproof());
                            jsonDummy.put("invoice", image_url + partialPelivery.getInvoiceproof());
                            jsonDummy.put("relation", image_url + partialPelivery.getRelationproof());
                            jsonDummy.put("signproof", image_url + partialPelivery.getSignproof());
                            jsonDummy.put("govt_id_proof", image_url + partialPelivery.getAddressproof());
                            jsonDummy.put("name", partialPelivery.getCustomer());
                            jsonDummy.put("reason", partialPelivery.getReason());
//                        paramObject.put("fieldData", jsonProofFieldObj);
//                        Cursor getOrders = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" + partial_shipAddress + "' ", null);
                            Cursor getOrders = database.rawQuery("Select IFNULL(delivery_qty,0) as delivery_qty, IFNULL(product_code, 0) as product_code, IFNULL(product_name, '') as product_name," +
                                    "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected, IFNULL(partial_reason, '') as partial_reason, IFNULL(r_id, 0) as r_id from ProductDetails where shipmentnumber = '" + partial_shipAddress + "' ", null);
                            JSONArray array = new JSONArray();

                            if (getOrders.getCount() > 0) {
                                getOrders.moveToFirst();
                                ArrayList<String> list = new ArrayList<String>();
                                while (!getOrders.isAfterLast()) {

                                    JSONObject obj = new JSONObject();
                                    JSONObject list1 = new JSONObject();

                                    try {

                                        list1.put("sku_actual_quantity", getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
                                        list1.put("product_code", getOrders.getString(getOrders.getColumnIndex("product_code")));
                                        list1.put("product_name", getOrders.getString(getOrders.getColumnIndex("product_name")));
                                        list1.put("quantity", getOrders.getString(getOrders.getColumnIndex("quantity")));
                                        list1.put("amount", getOrders.getString(getOrders.getColumnIndex("amount_collected")));
                                        list1.put("partial_reason", getOrders.getString(getOrders.getColumnIndex("partial_reason")));
                                        list1.put("reason_id", getOrders.getString(getOrders.getColumnIndex("r_id")));
                                        array.put(list1);
                                        jsonDummy.put("item_code", array);

                                    } catch (JSONException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }

                                    getOrders.moveToNext();
                                }

                            }
                            getOrders.close();

                            paramObject.put("fieldData", jsonDummy);
                        }
                        Log.v("paramObject", String.valueOf(paramObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());
                    final Observable<PartialResp> observable;
                    if (bfilBulkCheck.equalsIgnoreCase("1")) {
                        observable = apiService.getBulkBfilDeliverySync(requestBody)
                                .subscribeOn
                                        (Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    } else {
                        observable = apiService.getPartialDeliverySync(requestBody)
                                .subscribeOn
                                        (Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }

                    observable.subscribe(new Observer<PartialResp>() {

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(PartialResp value) {
                            List<PartialResp> orderVal = value.getPartialDelivery();
                            Log.v("mainactivity1_resmsg", value.getRes_msg() + " -" + value.getRes_code());
                            if (value.getRes_msg().equals("upload success")) {
                                /* removed on 10-08-2018 */
                            /*database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '"+attempt_count+"',image_status = 'U' where Shipment_Number ='" +
                                    ship_no + "' ");*/
                                if (bfilBulkCheck.equalsIgnoreCase("1")) {

                                    database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number IN (" + bulkShipmentAppend + ") AND delivery_to='1'  ");

                                } else {
                                    database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number ='" +
                                            partial_shipAddress + "' ");
                                }

//                            uploadImage(partialPelivery.getShipmentnumber());
//                            Logger.showShortMessage(DeliveryActivity.this,value.getRes_msg());
                            } else if (value.getRes_msg().equals("upload failed")) {
//                            Logger.showShortMessage(SyncService.this, value.getRes_msg());

                                database.execSQL("UPDATE orderheader set sync_status = 'E' where Shipment_Number ='" +
                                        partial_shipAddress + "' ");
                            } else if (value.getRes_msg().equals("order unassigned")) {
//                            Logger.showShortMessage(SyncService.this, value.getRes_msg());

                                database.execSQL("UPDATE orderheader set sync_status = 'E' where Shipment_Number ='" +
                                        partial_shipAddress + "' ");
                            } else if (value.getRes_msg().equals("already delivered")) {
                                database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
                                        partial_shipAddress + "' ");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("uppladerror1", e.toString());
                        }

                        @Override
                        public void onComplete() {
                            Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                        }


                    });
                    customerNameErrOrd.moveToNext();
                }
            }
            customerNameErrOrd.close();
        }
        customerName.close();

    }


    public void uploadImage() {
        /*** removed on 10-08-2018 ***/
       /* Cursor customerName = database.rawQuery("select DISTINCT O.delivery_status, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber ,IFNULL(D.customer_name,0) as customer_name,IFNULL" +
                "(D.amount_collected,0) as amount_collected,IFNULL(D.customer_contact_number,0) as customer_contact_number," +
                "IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                ", IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof," +
                "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D.latitude,0) as latitude,IFNULL" +
                "(D.longitude,0) as longitude" + " from orderheader O INNER JOIN DeliveryConfirmation D on D" +
                ".shipmentnumber = O.Shipment_Number where O" +
                ".sync_status='C'  ", null);*/

        Cursor customerName = database.rawQuery("select DISTINCT O.delivery_status,O.order_type, O.delivery_aadhar_required,D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber ,IFNULL(D.customer_name,0) as customer_name,IFNULL" +

                "(D.amount_collected,0) as amount_collected,IFNULL(D.customer_contact_number,0) as customer_contact_number," +
                "IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                ", IFNULL(D.delivery_proof, 0) as delivery_proof,IFNULL(D.relation_proof, 0) as relation_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof," +
                "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D.latitude,0) as latitude,IFNULL" +
                "(D.longitude,0) as longitude" + " from orderheader O INNER JOIN DeliveryConfirmation D on D" +
                ".shipmentnumber = O.Shipment_Number where O" +
                ".image_status='C' ", null);

        if (customerName.getCount() > 0) {
            customerName.moveToFirst();
            while (!customerName.isAfterLast()) {

                aadhaarEnabled = customerName.getString(customerName.getColumnIndex("delivery_aadhar_required"));

                String file_deliveryProof = file_path + customerName.getString(customerName.getColumnIndex("delivery_proof"));
                String file_addressProof = file_path + customerName.getString(customerName.getColumnIndex("id_proof"));
                String file_invoiceProof = file_path + customerName.getString(customerName.getColumnIndex("Invoice_proof"));
                String file_signature = sign_path + customerName.getString(customerName.getColumnIndex("signature_proof"));
                String file_relationproof = file_path + customerName.getString(customerName.getColumnIndex("relation_proof"));
                final String partial_shipAddress = customerName.getString(customerName.getColumnIndex("shipmentnumber"));
                Log.v("sync_uploadImage", partial_shipAddress);
                String file_pickup = null;

                if (customerName.getString(customerName.getColumnIndex("order_type")).equalsIgnoreCase("3")) {

                    Cursor getPickup = database.rawQuery("Select customerphoto  from PickupConfirmation where shipmentno = '" + partial_shipAddress + "' ", null);


                    if (getPickup.getCount() > 0) {
                        getPickup.moveToFirst();
                        file_pickup = file_path + getPickup.getString(getPickup.getColumnIndex("customerphoto"));

                    }
                    getPickup.close();
                }
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getRequestHeader())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                DeliveryConfirmReq delivery = new DeliveryConfirmReq();
                JSONObject paramObject = null;
//                delivery.setShipmentNumber(customerName.getString(customerName.getColumnIndex("shipmentnumber")));
//                delivery.setDeliveryProof(customerName.getString(customerName.getColumnIndex("delivery_proof")));

                /**** Get Delivery Proof Image****/
                File fileDeliveryProof = new File(file_deliveryProof);
                RequestBody requestBodyDelivery = RequestBody.create(MediaType.parse("*/*"), fileDeliveryProof);
                MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("delivery_file", fileDeliveryProof.getName(), requestBodyDelivery);

                /**** Get Address Proof Image****/
                File fileAddressProof = new File(file_addressProof);
                RequestBody requestBodyAddress = RequestBody.create(MediaType.parse("*/*"), fileAddressProof);
                MultipartBody.Part fileToAddress = MultipartBody.Part.createFormData("address_file", fileAddressProof.getName(), requestBodyAddress);


                /**** Get Invoice Proof Image****/
                File fileInvoiceProof = new File(file_invoiceProof);
                RequestBody requestBodyInvoice = RequestBody.create(MediaType.parse("*/*"), fileInvoiceProof);
                MultipartBody.Part fileToInvoice = MultipartBody.Part.createFormData("invoice_file", fileInvoiceProof.getName(), requestBodyInvoice);

                /**** Get Relation Proof Image****/
//                File filerelationproof = new File(file_relationproof);
//                RequestBody requestBodyrelation = RequestBody.create(MediaType.parse("*/*"), filerelationproof);
//                MultipartBody.Part fileToRelation = MultipartBody.Part.createFormData("relation_file", filerelationproof.getName(), requestBodyrelation);


                /**** Get Signature Proof Image****/
                File fileSignProof = new File(file_signature);
                RequestBody requestBodySign = RequestBody.create(MediaType.parse("*/*"), fileSignProof);
                MultipartBody.Part fileToSign = MultipartBody.Part.createFormData("sign_file", fileSignProof.getName(), requestBodySign);

//                File filePickupProof = new File(file_signature);
//                RequestBody requestBodyPickup = RequestBody.create(MediaType.parse("*/*"), filePickupProof);
//                MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);

                MultipartBody.Part fileToPickup;
                if (customerName.getString(customerName.getColumnIndex("order_type")).equalsIgnoreCase("3")) {
                    /**** Get Pickup Proof Image****/
                    File filePickupProof = new File(file_pickup);
                    RequestBody requestBodyPickup = RequestBody.create(MediaType.parse("*/*"), filePickupProof);
//                    MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                    fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                } else {
                    File filePickupProof = new File(file_invoiceProof);
                    RequestBody requestBodyPickup = RequestBody.create(MediaType.parse("*/*"), filePickupProof);
//                    MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                    fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodySign);
                }

//                MultipartBody.Part fileToRelation;
//                if (!aadhaarEnabled.equals("0")) {
//                    File filerelationproof = new File(file_relationproof);
//                    RequestBody requestBodyrelation = RequestBody.create(MediaType.parse("*/*"), filerelationproof);
//                    fileToRelation = MultipartBody.Part.createFormData("relation_file", filerelationproof.getName(), requestBodyrelation);
//                } else {
//                    File fileRelationProof = new File(file_invoiceProof);
//                    RequestBody requestBodyReation = RequestBody.create(MediaType.parse("*/*"), fileRelationProof);
////                    MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
//                    fileToRelation = MultipartBody.Part.createFormData("relation_file", filePickupProof.getName(), requestBodyReation);
//                }

                MultipartBody.Part fileToRelation;
                if (!aadhaarEnabled.equals("0")) {
                    if (customerName.getString(customerName.getColumnIndex("relation_proof")).equalsIgnoreCase("")) {
                        File fileRelationProof = new File(file_signature);
                        RequestBody requestBodyReation = RequestBody.create(MediaType.parse("*/*"), fileRelationProof);
//                    MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                        fileToRelation = MultipartBody.Part.createFormData("relation_file", fileSignProof.getName(), requestBodyReation);
                    } else {
                        File filerelationproof = new File(file_relationproof);
                        RequestBody requestBodyrelation = RequestBody.create(MediaType.parse("*/*"), filerelationproof);
                        fileToRelation = MultipartBody.Part.createFormData("relation_file", filerelationproof.getName(), requestBodyrelation);
                    }

                } else {
                    File fileRelationProof = new File(file_signature);
                    RequestBody requestBodyReation = RequestBody.create(MediaType.parse("*/*"), fileRelationProof);
//                    MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                    fileToRelation = MultipartBody.Part.createFormData("relation_file", fileSignProof.getName(), requestBodyReation);
                }

                final Observable<DeliveryConfirmResp> observable = apiService.getDeliveryImage(fileToDelivery, fileToInvoice, fileToAddress, fileToRelation, fileToSign, fileToPickup) //,fileToAddress,fileToInvoice,fileToSign

                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<DeliveryConfirmResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeliveryConfirmResp deliveryVal) {
                        deliveryList = new ArrayList<>();
                        List<DeliveryConfirmResp> orderVal = deliveryVal.getDelivery();
//                        Log.v("del_image_response", deliveryVal.getRes_msg());
                        if (deliveryVal.getRes_msg().equals("image success")) {
//                            database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
//                                    shipno + "' ");

//                            database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '"+attempt_count+"' where Shipment_Number ='" +
//                                    shipno + "' ");
                            database.execSQL("UPDATE orderheader set image_status = 'U' where Shipment_Number ='" +
                                    partial_shipAddress + "' ");
//                            attempt_count = 0;
                        /*    Logger.showShortMessage(SyncService.this, "Delivery has been uploaded successfully");
                            Intent goToMain = new Intent(SyncService.this, MainActivity.class);
                            startActivity(goToMain);*/
//                            pageTrackerService();
//                             uploadComplete(partial_shipAddress);  //removed on 10-08-2018
                        } else if (deliveryVal.getRes_msg().equals("image failed")) {

                        }

//                        orderSyncStatus();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("upimgerror", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
                Log.v("sync_service", "move to next");
                customerName.moveToNext();
            }
        }
        customerName.close();


    }

    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {


                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            getLocation();
                            if (latitude_user != 0.0 && longitude_user != 0.0) {
                                latitude = String.valueOf(latitude_user);
                                longitude = String.valueOf(longitude_user);

                            }


                            //toast("Success");
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Logger.logInfo("GPS is not on");
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult((Activity) getApplicationContext(), 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Logger.logInfo("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });


        }
    }

    private void getLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle arg0) {

        Log.d("ACTIVITY", "ApiClient: OnConnected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            startLocationUpdates(); // bind interface if your are not getting the lastlocation. or bind as per your requirement.
        }

        if (mLastLocation != null) {
            while (latitude_user == 0 || longitude_user == 0) {


                latitude_user = mLastLocation.getLatitude();
                longitude_user = mLastLocation.getLongitude();

                if (latitude_user != 0 && longitude_user != 0) {
                    stopLocationUpdates(); // unbind the locationlistner here or wherever you want as per your requirement.
                    // location data received, dismiss dialog, call your method or perform your task.
                }
            }
        }

        // Logger.showShortMessage(this, latitude_user + "" + longitude_user);


        /**
         * Get the current location updates in text
         */


        double latitude = 0.0;
        double langitidue = 0.0;
        // Once connected with google api, get the location
        getLocation();

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            langitidue = mLastLocation.getLongitude();
        }


        /**
         * Set the location of activity
         */
        /**
         * Store the lat and lang in shared preference
         */
        if (mLastLocation != null) {
            AppController.setLongPreference(this, Constants.CUR_LATITUDE, Double.doubleToRawLongBits(mLastLocation
                    .getLatitude()));
            AppController.setLongPreference(this, Constants.CUR_LONGITUDE, Double.doubleToRawLongBits(mLastLocation
                    .getLongitude()));
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("onConnectionFailed", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            } else if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
    }


    /**
     * Sync service for page tracking
     */
    public void pageTrackerService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final InthreeApi apiService = retrofit.create(InthreeApi.class);

        JSONObject paramObject = null;
        JSONArray jsonArray = new JSONArray();
        JSONObject mainParaObject = new JSONObject();
        Cursor pageTracker = database.rawQuery("Select IFNULL(page,0) as page," +
                "IFNULL(time_in,0) as time_in," +
                "IFNULL(time_out,0) as time_out ," +
                "IFNULL(lat_in,0) as lat_in ," +
                "IFNULL(lat_out,0) as lat_out," +
                "IFNULL(long_in,0) as long_in ," +
                "IFNULL(long_out,0) as long_out ," +
                "IFNULL(shipment_id,0) as shipment_id ," +
                "IFNULL(app_version,0) as app_version ," +
                "IFNULL(app_name,0) as app_name ," +
                "IFNULL(user_name,0) as user_name ," +
                "IFNULL(status,0) as status ," +
                "IFNULL(date,0) as date from PageTracker", null);


        if (pageTracker.getCount() > 0) {
            pageTracker.moveToFirst();
            while (!pageTracker.isAfterLast()) {
                page = pageTracker.getString(pageTracker.getColumnIndex("page"));
                timeIn = pageTracker.getString(pageTracker.getColumnIndex("time_in"));
                timeOut = pageTracker.getString(pageTracker.getColumnIndex("time_out"));
                latIn = pageTracker.getString(pageTracker.getColumnIndex("lat_in"));
                latOut = pageTracker.getString(pageTracker.getColumnIndex("lat_out"));
                longIn = pageTracker.getString(pageTracker.getColumnIndex("long_in"));
                longOut = pageTracker.getString(pageTracker.getColumnIndex("long_out"));
                shipmentId = pageTracker.getString(pageTracker.getColumnIndex("shipment_id"));
                appVersion = pageTracker.getString(pageTracker.getColumnIndex("app_version"));
                appName = pageTracker.getString(pageTracker.getColumnIndex("app_name"));
                userName = pageTracker.getString(pageTracker.getColumnIndex("user_name"));
                status = pageTracker.getString(pageTracker.getColumnIndex("status"));
                date = pageTracker.getString(pageTracker.getColumnIndex("date"));


                try {
                    paramObject = new JSONObject();
                    paramObject.put("page", page);
                    paramObject.put("time_in", timeIn);
                    paramObject.put("time_out", timeOut);
                    paramObject.put("lat_in", latIn);
                    paramObject.put("lat_out", latOut);
                    paramObject.put("long_in", longIn);
                    paramObject.put("long_out", longOut);
                    paramObject.put("shipment_id", shipmentId);
                    paramObject.put("app_version", appVersion);
                    paramObject.put("app_name", appName);
                    paramObject.put("user_name", userName);
                    paramObject.put("status", status);
                    paramObject.put("date", date);
                    jsonArray.put(paramObject);

                    mainParaObject.put("navigation_tracker", jsonArray);
                    Log.v("paramObject", paramObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pageTracker.moveToNext();
            }

            pageTracker.close();

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), mainParaObject.toString());
            final Observable<PartialResp> observable = apiService.getTrackerPage(requestBody).subscribeOn
                    (Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            observable.subscribe(new Observer<PartialResp>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(PartialResp value) {
                    orderList = new ArrayList<>();
                    if (value.getRes_msg().equals("page track success")) {
//                            uploadUndelivered();
                    } else if (value.getRes_msg().equals("page track failed")) {
//                            Logger.logInfo("failed");
//                            uploadUndelivered();
                    }
                    File dir = new File(String.valueOf(getApplicationContext().getFilesDir()) + "/DeliveryApp");

                    if (dir.exists() && dir.isDirectory()) {
                        getImagesToSync();
                    }
                    File dir1 = new File(String.valueOf(getApplicationContext().getFilesDir()) + "/ServiceSignApp");
                    if (dir1.exists() && dir1.isDirectory()) {
                        getServiceSignatureToSync();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.d("pageerror", e.toString());
                }

                @Override
                public void onComplete() {
                    Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                }
            });


        }
    }


    public void deleteOlderRecords() {
        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.valid, D.delivery_proof, D.signature_proof, D.id_proof, D.Invoice_proof FROM orderheader O  LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number WHERE O.sync_status = 'U' AND (O.valid <= datetime('now','-1 day') OR O.valid > datetime('now')) ", null);
//    String deleteOrder = "SELECT FROM orderheader WHERE sync_status = 'U' AND (valid <= datetime('now','-1 day') OR valid > datetime('now'))";
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {
                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("order_number")));
                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("valid")));
                File fileImgDeliverProof = new File("/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
                if (fileImgDeliverProof.exists()) {
                    fileImgDeliverProof.delete();
                }
//                File fileImgSignProof = new File("/data/data/com.inthree.boon.deliveryapp/files/UserSignature/" + deleteOrder.getString(deleteOrder.getColumnIndex("signature_proof")));
                File fileImgSignProof = new File("/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("signature_proof")));
                if (fileImgSignProof.exists()) {
                    fileImgSignProof.delete();
                }
                File fileImgIDProof = new File("/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("id_proof")));
                if (fileImgIDProof.exists()) {
                    fileImgIDProof.delete();
                }
                File fileImgInvoiceProof = new File("/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("Invoice_proof")));
                if (fileImgInvoiceProof.exists()) {
                    fileImgInvoiceProof.delete();
                }
                deleteOrder.moveToNext();
            }
            String deleteOrderDetails = " DELETE\n" +
                    "        FROM orderheader\n" +
                    "        WHERE Shipment_Number IN ( SELECT shipmentnumber FROM DeliveryConfirmation WHERE sync_status = 'U' AND (valid <= datetime('now','-1 day') OR valid > datetime('now')) )";
//        String deleteOrderDetails = "DELETE FROM orderheader WHERE sync_status = 'U' AND (valid <= datetime('now','-1 day') OR valid > datetime('now'))";
            database.execSQL(deleteOrderDetails);
//        DELETE
//        FROM optionToValues
//        WHERE optionId IN ( SELECT optionId FROM productOptions WHERE product_id = 82 )


        }
    }

    public void showOlderRecords() {
        String file_path = String.valueOf(this.getFilesDir());

//        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.signature_proof,0) as signature_proof, IFNULL(D.id_proof,0) as id_proof, IFNULL(D.Invoice_proof,0) as Invoice_proof, IFNULL(D.created_at,0) as created_at FROM orderheader O   JOIN DeliveryConfirmation D on O.Shipment_Number = D.shipmentnumber where  ( D.created_at < datetime('now','-1 day')) AND O.sync_status = 'U' AND O.image_status = 'U'  ", null);
        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.signature_proof,0) as signature_proof, IFNULL(D.id_proof,0) as id_proof, IFNULL(D.Invoice_proof,0) as Invoice_proof, IFNULL(D.created_at,0) as created_at FROM orderheader O   JOIN DeliveryConfirmation D on O.Shipment_Number = D.shipmentnumber where  ( D.created_at < datetime('now','-1 day')) AND O.sync_status = 'U' AND O.delivery_status != 'undelivered'   ", null);
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {
                String ship_id = deleteOrder.getString(deleteOrder.getColumnIndex("Shipment_Number"));
                Log.v("fileImgDeliverProof1", deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("order_number")));
//                Log.v("fileImg_created_at", deleteOrder.getString(deleteOrder.getColumnIndex("created_at")));

                /* Delete from orderHeader which are older than one day */
                String deleteOrderDetails = "DELETE FROM orderheader WHERE sync_status = 'U' AND Shipment_Number = '" + ship_id + "'";
                database.execSQL(deleteOrderDetails);

                /* Delete from DeliveryConfirmation which are older than one day */
                String deleteDeliveryDetails = "DELETE FROM DeliveryConfirmation WHERE shipmentnumber = '" + ship_id + "'";
                database.execSQL(deleteDeliveryDetails);

           /*     File deleteImgDeliverProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
                if (deleteImgDeliverProof.exists()) {
                    deleteImgDeliverProof.delete();
                }
//                File deleteImgSignProof = new File(file_path+"/UserSignature/" + deleteOrder.getString(deleteOrder.getColumnIndex("signature_proof")));
                File deleteImgSignProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("signature_proof")));
                if (deleteImgSignProof.exists()) {
                    deleteImgSignProof.delete();
                }
                File deleteImgIDProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("id_proof")));
                if (deleteImgIDProof.exists()) {
                    deleteImgIDProof.delete();
                }
                File deleteImgInvoiceProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("Invoice_proof")));
                if (deleteImgInvoiceProof.exists()) {
                    deleteImgInvoiceProof.delete();
                }*/

                deleteOrder.moveToNext();
            }

        }
    }

    public void showOlderUndelivered() {

        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, IFNULL(U.proof_photo, 0) as proof_photo,  IFNULL(U.created_at,0) as created_at FROM orderheader O  JOIN UndeliveredConfirmation U on O.Shipment_Number = U.shipmentnumber where  (U.created_at < datetime('now','-1 day')) AND O.sync_status = 'U' AND O.delivery_status = 'undelivered' ", null);
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {
                String ship_id = deleteOrder.getString(deleteOrder.getColumnIndex("Shipment_Number"));
                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("proof_photo")));

                String deleteOrderDetails = "DELETE FROM orderheader WHERE sync_status = 'U' AND Shipment_Number = '" + ship_id + "'";
                database.execSQL(deleteOrderDetails);

                String deleteDeliveryDetails = "DELETE FROM UndeliveredConfirmation WHERE shipmentnumber = '" + ship_id + "'";
                database.execSQL(deleteDeliveryDetails);


                deleteOrder.moveToNext();
            }

        }
    }

    public void uploadUndelivered() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String currentDateTimeString = format.format(new Date());

        Cursor getUndeliveryValue = database.rawQuery("select O.order_type,O.delivery_status, O.order_number, O.attempt_count, U.sno, " +
                "IFNULL(U.shipmentnumber,0) as shipmentnumber, IFNULL(U.remarks,0) as remarks, " +
                "IFNULL(U.proof_photo, 0) as proof_photo, IFNULL(U.reason,0) as reason, " +
                "IFNULL(U.latitude, 0) as latitude, IFNULL(U.longitude, 0) as longitude, IFNULL(U.created_at, 0) as created_at,IFNULL(O.invoice_amount,0) as invoice_amount," +
                " IFNULL(U.redirect, 0) as redirect,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O" +
                ".payment_mode,0) as payment_mode,O.attempt_count," +
                "IFNULL(U.shipment_address,0) as shipment_address, IFNULL(U.reason_id,0) as reason_id  from orderheader O INNER JOIN UndeliveredConfirmation U on U" +
                ".shipmentnumber = O.Shipment_Number where O.sync_status='C'  ", null);

        Log.v("getUndeliveryValue", String.valueOf(getUndeliveryValue.getCount()));
        if (getUndeliveryValue.getCount() > 0) {
            getUndeliveryValue.moveToFirst();
            while (!getUndeliveryValue.isAfterLast()) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getRequestHeader())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);


                UndeliveryReq undeliveryReq = new UndeliveryReq();
                UndeliveryReq.FieldData fieldData = new UndeliveryReq.FieldData();


                JSONObject paramObject = null; // Main JSON Object
                JSONObject jsonFieldObj;
                Log.v("LOGIN_USER_ID", AppController.getStringPreference(Constants.USER_ID, ""));
                undelivered_shipAddress = getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("shipmentnumber"));
                undeliveryReq.setRunsheetNo(AppController.getStringPreference(Constants.USER_ID, ""));
                undeliveryReq.setActualAmount(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("invoice_amount")));
                undeliveryReq.setOriginalAmount(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("invoice_amount")));
                undeliveryReq.setMoneyTransactionType(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("payment_mode")));
                undeliveryReq.setReferenceNumber(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("referenceNumber")));
                undeliveryReq.setLatitude(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("latitude")));
                undeliveryReq.setLongitude(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("longitude")));
                undeliveryReq.setAttemptCount(getUndeliveryValue.getInt(getUndeliveryValue.getColumnIndex("attempt_count")));
                undeliveryReq.setJobType("delivery_new");
                undeliveryReq.setEmployeeCode(AppController.getStringPreference(Constants.USER_ID, ""));
                undeliveryReq.setHubCode("1");
                undeliveryReq.setStatus("undelivered");
                undeliveryReq.setTrackHalt("0");
                undeliveryReq.setTrackKm("0");
                undeliveryReq.setTrackTransactionTimeSpent("0");
                undeliveryReq.setMerchantCode("0");
                undeliveryReq.setTransactionDate(currentDateTimeString);
                undeliveryReq.setLastTransactionTime(currentDateTimeString);
                undeliveryReq.setErpPushTime(currentDateTimeString);
                undeliveryReq.setBattery(String.valueOf(battery_level));
                undeliveryReq.setRedirect(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("redirect")));

                un_attempt_count = undeliveryReq.getAttemptCount();
                //attempt_count++;

                fieldData.setImage(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("proof_photo")));
                fieldData.setReason(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("reason")));
                fieldData.setRemarks(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("remarks")));
                fieldData.setAddress(getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("shipment_address")));

                paramObject = new JSONObject();
                jsonFieldObj = new JSONObject();

                try {
                    paramObject.put("runsheetNo", undeliveryReq.getRunsheetNo());
                    paramObject.put("actualAmount", undeliveryReq.getActualAmount());
                    paramObject.put("originalAmount", undeliveryReq.getOriginalAmount());
                    paramObject.put("moneyTransactionType", undeliveryReq.getMoneyTransactionType());
                    paramObject.put("referenceNumber", undeliveryReq.getReferenceNumber());
                    paramObject.put("latitude", undeliveryReq.getLatitude());
                    paramObject.put("longitude", undeliveryReq.getLongitude());
                    paramObject.put("attemptCount", attempt_count);
                    paramObject.put("jobType", undeliveryReq.getJobType());
                    paramObject.put("employeeCode", undeliveryReq.getEmployeeCode());
                    paramObject.put("hubCode", undeliveryReq.getHubCode());
                    paramObject.put("status", undeliveryReq.getStatus());
                    paramObject.put("trackHalt", undeliveryReq.getTrackHalt());
                    paramObject.put("trackKm", undeliveryReq.getTrackKm());
                    paramObject.put("trackTransactionTimeSpent", undeliveryReq.getTrackTransactionTimeSpent());
                    paramObject.put("merchantCode", undeliveryReq.getMerchantCode());
//                    paramObject.put("transactionDate", undeliveryReq.getMoneyTransactionType());
//                    paramObject.put("transactionDate", undeliveryReq.getTransactionDate());
                    paramObject.put("transactionDate", getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("created_at")));
                    paramObject.put("erpPushTime", undeliveryReq.getErpPushTime());
                    paramObject.put("lastTransactionTime", undeliveryReq.getLastTransactionTime());
                    paramObject.put("created_at", getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("created_at")));
                    paramObject.put("battery", undeliveryReq.getBattery());
                    paramObject.put("deviceInfo", AppController.getdevice());
                    paramObject.put("redirect", undeliveryReq.getRedirect());
                    paramObject.put("order_no", getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("order_number")));

                    paramObject.put("shipmentnumber", getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("shipmentnumber")));

                    jsonFieldObj.put("image", image_url + fieldData.getImage());
                    jsonFieldObj.put("reason", fieldData.getReason());
                    jsonFieldObj.put("reason_id", getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("reason_id")));
                    jsonFieldObj.put("remarks", fieldData.getRemarks());
                    jsonFieldObj.put("address", fieldData.getAddress());

                    paramObject.put("fieldData", jsonFieldObj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                file_proofPhoto = file_path + getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("proof_photo"));


                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                final Observable<UndeliveryResp> observable = apiService.getUndeliverySync(requestBody)
                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<UndeliveryResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UndeliveryResp value) {

                        Log.v("image_response_service", value.getRes_msg());

                        // uploadUndeliveredImage(undelivered_shipAddress);

                        if (value.getRes_msg().equalsIgnoreCase("undelivered success")) {
                            un_attempt_count++;
                            database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                    undelivered_shipAddress + "' ");
                        } else if (value.getRes_msg().equalsIgnoreCase("undelivered unassigned")) {
                            database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                    undelivered_shipAddress + "' ");
                        } else if (value.getRes_msg().equalsIgnoreCase("undelivered failed")) {
                            database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                    undelivered_shipAddress + "' ");
                        } else if (value.getRes_msg().equalsIgnoreCase("already undelivered")) {
                            database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + un_attempt_count + "  where Shipment_Number ='" +
                                    undelivered_shipAddress + "' ");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("upunerror", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
                getUndeliveryValue.moveToNext();
            }
        } else {
            Cursor getUndeliveryErrValue = database.rawQuery("select O.delivery_status, O.order_number, O.attempt_count, U.sno, " +
                    "IFNULL(U.shipmentnumber,0) as shipmentnumber, IFNULL(U.remarks,0) as remarks, " +
                    "IFNULL(U.proof_photo, 0) as proof_photo, IFNULL(U.reason,0) as reason, " +
                    "IFNULL(U.latitude, 0) as latitude, IFNULL(U.longitude, 0) as longitude, IFNULL(U.created_at, 0) as created_at,IFNULL(O.invoice_amount,0) as invoice_amount," +
                    " IFNULL(U.redirect, 0) as redirect,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O" +
                    ".payment_mode,0) as payment_mode,O.attempt_count," +
                    "IFNULL(U.shipment_address,0) as shipment_address, IFNULL(U.reason_id,0) as reason_id from orderheader O INNER JOIN UndeliveredConfirmation U on U" +
                    ".shipmentnumber = O.Shipment_Number where O.sync_status='C' ", null);

            Log.v("getUndeliveryValue", String.valueOf(getUndeliveryErrValue.getCount()));
            if (getUndeliveryErrValue.getCount() > 0) {
                getUndeliveryErrValue.moveToFirst();
                while (!getUndeliveryErrValue.isAfterLast()) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(getRequestHeader())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                    InthreeApi apiService = retrofit.create(InthreeApi.class);


                    UndeliveryReq undeliveryReq = new UndeliveryReq();
                    UndeliveryReq.FieldData fieldData = new UndeliveryReq.FieldData();


                    JSONObject paramObject = null; // Main JSON Object
                    JSONObject jsonFieldObj;
                    Log.v("LOGIN_USER_ID", AppController.getStringPreference(Constants.USER_ID, ""));
                    undelivered_shipAddress = getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("shipmentnumber"));
                    undeliveryReq.setRunsheetNo(AppController.getStringPreference(Constants.USER_ID, ""));
                    undeliveryReq.setActualAmount(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("invoice_amount")));
                    undeliveryReq.setOriginalAmount(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("invoice_amount")));
                    undeliveryReq.setMoneyTransactionType(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("payment_mode")));
                    undeliveryReq.setReferenceNumber(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("referenceNumber")));
                    undeliveryReq.setLatitude(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("latitude")));
                    undeliveryReq.setLongitude(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("longitude")));
                    undeliveryReq.setAttemptCount(getUndeliveryErrValue.getInt(getUndeliveryErrValue.getColumnIndex("attempt_count")));
                    undeliveryReq.setJobType("delivery_new");
                    undeliveryReq.setEmployeeCode(AppController.getStringPreference(Constants.USER_ID, ""));
                    undeliveryReq.setHubCode("1");
                    undeliveryReq.setStatus("undelivered");
                    undeliveryReq.setTrackHalt("0");
                    undeliveryReq.setTrackKm("0");
                    undeliveryReq.setTrackTransactionTimeSpent("0");
                    undeliveryReq.setMerchantCode("0");
                    undeliveryReq.setTransactionDate(currentDateTimeString);
                    undeliveryReq.setLastTransactionTime(currentDateTimeString);
                    undeliveryReq.setErpPushTime(currentDateTimeString);
                    undeliveryReq.setBattery(String.valueOf(battery_level));
                    undeliveryReq.setRedirect(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("redirect")));

                    un_attempt_count = undeliveryReq.getAttemptCount();
                    //attempt_count++;

//                    fieldData.setImage(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("proof_photo")));
//                    fieldData.setReason(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("reason")));
//                    fieldData.setRemarks(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("remarks")));
                    fieldData.setImage(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("proof_photo")));
                    fieldData.setReason(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("reason")));
                    fieldData.setRemarks(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("remarks")));
                    fieldData.setAddress(getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("shipment_address")));


                    paramObject = new JSONObject();
                    jsonFieldObj = new JSONObject();

                    try {
                        paramObject.put("runsheetNo", undeliveryReq.getRunsheetNo());
                        paramObject.put("actualAmount", undeliveryReq.getActualAmount());
                        paramObject.put("originalAmount", undeliveryReq.getOriginalAmount());
                        paramObject.put("moneyTransactionType", undeliveryReq.getMoneyTransactionType());
                        paramObject.put("referenceNumber", undeliveryReq.getReferenceNumber());
                        paramObject.put("latitude", undeliveryReq.getLatitude());
                        paramObject.put("longitude", undeliveryReq.getLongitude());
                        paramObject.put("attemptCount", attempt_count);
                        paramObject.put("jobType", undeliveryReq.getJobType());
                        paramObject.put("employeeCode", undeliveryReq.getEmployeeCode());
                        paramObject.put("hubCode", undeliveryReq.getHubCode());
                        paramObject.put("status", undeliveryReq.getStatus());
                        paramObject.put("trackHalt", undeliveryReq.getTrackHalt());
                        paramObject.put("trackKm", undeliveryReq.getTrackKm());
                        paramObject.put("trackTransactionTimeSpent", undeliveryReq.getTrackTransactionTimeSpent());
                        paramObject.put("merchantCode", undeliveryReq.getMerchantCode());
//                    paramObject.put("transactionDate", undeliveryReq.getMoneyTransactionType());
//                    paramObject.put("transactionDate", undeliveryReq.getTransactionDate());
                        paramObject.put("transactionDate", getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("created_at")));
                        paramObject.put("erpPushTime", undeliveryReq.getErpPushTime());
                        paramObject.put("lastTransactionTime", undeliveryReq.getLastTransactionTime());
                        paramObject.put("created_at", getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("created_at")));
                        paramObject.put("battery", undeliveryReq.getBattery());
                        paramObject.put("deviceInfo", AppController.getdevice());
                        paramObject.put("redirect", undeliveryReq.getRedirect());
                        paramObject.put("order_no", getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("order_number")));
                        paramObject.put("shipmentnumber", getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("shipmentnumber")));

//                        jsonFieldObj.put("image", image_url + fieldData.getImage());
//                        jsonFieldObj.put("reason", fieldData.getReason());
//                        jsonFieldObj.put("remarks", fieldData.getRemarks());

                        jsonFieldObj.put("image", image_url + fieldData.getImage());
                        jsonFieldObj.put("reason", fieldData.getReason());
                        jsonFieldObj.put("reason_id", getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("reason_id")));
                        jsonFieldObj.put("remarks", fieldData.getRemarks());
                        jsonFieldObj.put("address", fieldData.getAddress());

                        paramObject.put("fieldData", jsonFieldObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    file_proofPhoto = file_path + getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("proof_photo"));
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                    final Observable<UndeliveryResp> observable = apiService.getUndeliverySync(requestBody)
                            .subscribeOn
                                    (Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread());

                    observable.subscribe(new Observer<UndeliveryResp>() {

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(UndeliveryResp value) {

                            Log.v("image_response", value.getRes_msg());
                            // uploadUndeliveredImage(undelivered_shipAddress);

                            if (value.getRes_msg().equalsIgnoreCase("undelivered success")) {
                                un_attempt_count++;
                                database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                        undelivered_shipAddress + "' ");
                            } else if (value.getRes_msg().equalsIgnoreCase("undelivered unassigned")) {
                                database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                        undelivered_shipAddress + "' ");
                            } else if (value.getRes_msg().equalsIgnoreCase("undelivered failed")) {
                                database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                        undelivered_shipAddress + "' ");
                            } else if (value.getRes_msg().equalsIgnoreCase("already undelivered")) {
                                database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + un_attempt_count + "  where Shipment_Number ='" +
                                        undelivered_shipAddress + "' ");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("upunerror", e.toString());
                        }

                        @Override
                        public void onComplete() {
                            Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                        }


                    });
                    getUndeliveryErrValue.moveToNext();
                }
            }
            getUndeliveryErrValue.close();
        }
        getUndeliveryValue.close();


    }


    public void uploadUndeliveredImage() {

        Cursor getUndeliveryImage = database.rawQuery("select  O.delivery_status, O.order_number, O.attempt_count, U.sno, " +
                "IFNULL(U.shipmentnumber,0) as shipmentnumber, IFNULL(U.remarks,0) as remarks, " +
                "IFNULL(U.proof_photo, 0) as proof_photo, IFNULL(U.reason,0) as reason, " +
                "IFNULL(U.latitude, 0) as latitude, IFNULL(U.longitude, 0) as longitude,IFNULL(O.invoice_amount,0) as invoice_amount," +
                " IFNULL(U.redirect, 0) as redirect,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O" +
                ".payment_mode,0) as payment_mode,O.attempt_count," +
                "IFNULL(U.shipment_address,0) as shipment_address from orderheader O INNER JOIN UndeliveredConfirmation U on U" +
                ".shipmentnumber = O.Shipment_Number where O.sync_status = 'C' ", null);
        if (getUndeliveryImage.getCount() > 0) {
            getUndeliveryImage.moveToFirst();
            while (!getUndeliveryImage.isAfterLast()) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getRequestHeader())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                String file_undeliveryProof = file_path + getUndeliveryImage.getString(getUndeliveryImage.getColumnIndex("proof_photo"));

                File fileProofPhoto = new File(file_undeliveryProof);
                RequestBody requestBodyDelivery = RequestBody.create(MediaType.parse("*/*"), fileProofPhoto);
                MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("image", fileProofPhoto.getName(), requestBodyDelivery);

                final Observable<UndeliveryResp> observable = apiService.getUnDeliveryImage(fileToDelivery) //,fileToAddress,fileToInvoice,fileToSign
                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<UndeliveryResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UndeliveryResp value) {
                        undeliveryList = new ArrayList<>();
                        List<UndeliveryResp> orderVal = value.getUndeliveredResp();
                        if (value.getRes_msg().equalsIgnoreCase(getResources().getString(R.string.undeliver_res))) {

                            Log.v("undelivered_suc", "undelivered_suc");
                        } else if (value.getRes_msg().equalsIgnoreCase("undelivered updated")) {


                        } else {

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("upunimgerror", e.toString());

                    }

                    @Override
                    public void onComplete() {

                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
                getUndeliveryImage.moveToNext();
            }
        }
        getUndeliveryImage.close();


    }


    private void getReasonOld() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);

        final Observable<ReasonResp> observable = apiService.getReason().subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<ReasonResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReasonResp value) {
                reasonMasterList = value.getAllReason();
                ReasonResp detailVal = value;
                for (int i = 0; i < reasonMasterList.size(); i++) {
                    Log.v("reason_message", reasonMasterList.get(i).getId());
                    Cursor uname = database.rawQuery("Select * from ReasonMaster where rid = '" + reasonMasterList.get(i).getRid() + "' ", null);

                    if (uname.getCount() == 0) {
                        uname.moveToFirst();
                        String insertUndeliveredReason = "Insert into ReasonMaster (id,rid,reason,reason_for,reasonstatus,reason_type) Values(" + reasonMasterList.get(i).getId() + ", " + reasonMasterList.get(i).getRid() + "" +
                                ",'" + reasonMasterList.get(i).getReason() + "', '" + reasonMasterList.get(i).getReason_for() + "'," + reasonMasterList.get(i).getReason_status() + "," + reasonMasterList.get(i).getReason_type() + ")";
                        database.execSQL(insertUndeliveredReason);
                    } else {
                        String updateUndeliveredReason = "Update ReasonMaster set id = " + reasonMasterList.get(i).getId() + "," +
                                "rid = " + reasonMasterList.get(i).getRid() + ",reason = '" + reasonMasterList.get(i).getReason() + "', reason_for ='" + reasonMasterList.get(i).getReason_for() + "' ,reasonstatus = " + reasonMasterList.get(i).getReason_status() + ", reason_type = '" + reasonMasterList.get(i).getReason_type() + "' where rid = '" + reasonMasterList.get(i).getRid() + "'";
                        database.execSQL(updateUndeliveredReason);
                    }
                    uname.close();
                }

//                getAttemptCount();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("reasonerror", e.toString());
            }

            @Override
            public void onComplete() {
                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

    private void getReason() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);

        final Observable<ReasonResp> observable = apiService.getReason().subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<ReasonResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReasonResp value) {
                reasonMasterList = value.getAllReason();
                ReasonResp detailVal = value;
                for (int i = 0; i < reasonMasterList.size(); i++) {

                    String tamil_rval = null;
                    String hindi_rval = null;
                    String marathi_rval = null;
                    String punjabi_rval = null;
                    String bengali_rval = null;
                    String orissa_rval = null;
                    String assam_rval = null;
                    String telugu_rval = null;
                    String kannada_rval = null;
                    JSONObject tamilItemLangParamObject = null;
                    JSONObject hindiItemLangParamObject = null;
                    JSONObject marathiItemLangParamObject = null;
                    JSONObject bengaliItemLangParamObject = null;
                    JSONObject assamItemLangParamObject = null;
                    JSONObject orissaItemLangParamObject = null;
                    JSONObject punjabItemLangParamObject = null;
                    JSONObject teluguItemLangParamObject = null;
                    JSONObject kannadaItemLangParamObject = null;
                    String item_json;
                    item_json = reasonMasterList.get(i).getLang_reason();
                    Log.v("check_if_reason_json1", "- " + item_json);
                    if (item_json != null && !item_json.equals("null")) {
                        Log.v("check_if_reason_json", "- " + item_json);

                        try {

                            JSONObject obj = new JSONObject(item_json);

                            try {
                                //Tamil JSON
                                JSONObject tamilOneObject = obj.getJSONObject("tamil");
                                String tamil_pname = tamilOneObject.getString("reason");

                                tamilItemLangParamObject = new JSONObject();
                                tamilItemLangParamObject.put("reason", tamil_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }
                            tamil_rval = tamilItemLangParamObject.toString();
                            try {
                                //Hindi JSON
                                JSONObject hindiOneObject = obj.getJSONObject("hindi");
                                String hindi_pname = hindiOneObject.getString("reason");

                                hindiItemLangParamObject = new JSONObject();
                                hindiItemLangParamObject.put("reason", hindi_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }
                            hindi_rval = hindiItemLangParamObject.toString();

                            try {
                                //Bengali JSON
                                JSONObject bengaliOneObject = obj.getJSONObject("bengali");
                                String bengali_pname = bengaliOneObject.getString("reason");

                                bengaliItemLangParamObject = new JSONObject();
                                bengaliItemLangParamObject.put("reason", bengali_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }

                            bengali_rval = bengaliItemLangParamObject.toString();

                            try {
                                //Marathi JSON
                                JSONObject marathiOneObject = obj.getJSONObject("marathi");
                                String marathi_pname = marathiOneObject.getString("reason");

                                marathiItemLangParamObject = new JSONObject();
                                marathiItemLangParamObject.put("reason", marathi_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }

                            marathi_rval = marathiItemLangParamObject.toString();
                            try {
                                //Assam JSON
                                JSONObject assamOneObject = obj.getJSONObject("assamese");
                                String assam_pname = assamOneObject.getString("reason");

                                assamItemLangParamObject = new JSONObject();
                                assamItemLangParamObject.put("reason", assam_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }
                            assam_rval = assamItemLangParamObject.toString();

                            try {
                                //Orissa JSON
                                JSONObject orissaOneObject = obj.getJSONObject("odia");
                                String orissa_pname = orissaOneObject.getString("reason");
                                orissaItemLangParamObject = new JSONObject();
                                orissaItemLangParamObject.put("reason", orissa_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }
                            orissa_rval = orissaItemLangParamObject.toString();


                            try {
                                //Telugu JSON
                                JSONObject teluguOneObject = obj.getJSONObject("telugu");
                                String telugu_pname = teluguOneObject.getString("reason");
                                teluguItemLangParamObject = new JSONObject();
                                teluguItemLangParamObject.put("reason", telugu_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }
                            telugu_rval = teluguItemLangParamObject.toString();

                            try {
                                //Kannada JSON
                                JSONObject kannadaOneObject = obj.getJSONObject("kannada");
                                String kannada_pname = kannadaOneObject.getString("reason");

                                kannadaItemLangParamObject = new JSONObject();
                                kannadaItemLangParamObject.put("reason", kannada_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }
                            kannada_rval = kannadaItemLangParamObject.toString();

                            try {
                                //Punjabi JSON
                                JSONObject punjabOneObject = obj.getJSONObject("punjabi");
                                String punjab_pname = punjabOneObject.getString("reason");
                                punjabItemLangParamObject = new JSONObject();
                                punjabItemLangParamObject.put("reason", punjab_pname);

                            } catch (JSONException e) {
                                e.getStackTrace();
                            }
                            punjabi_rval = punjabItemLangParamObject.toString();

                        } catch (Throwable t) {
                            Log.e("new_reason_json", "Could not parse malformed JSON: \"" + bengali_rval + "\"");
                        }
                    }


//                    Log.v("reason_message", reasonMasterList.get(i).getId());
                    Cursor uname = database.rawQuery("Select * from ReasonMaster where rid = '" + reasonMasterList.get(i).getRid() + "' ", null);

                    if (uname.getCount() == 0) {
                        uname.moveToFirst();
                        String insertUndeliveredReason = "Insert into ReasonMaster (id,rid,reason,reason_for,reasonstatus,tamil,telugu ,punjabi,hindi,bengali,kannada,assam,orissa,marathi,reason_type) Values(" + reasonMasterList.get(i).getId() + ", " + reasonMasterList.get(i).getRid() + "" +
                                ",'" + reasonMasterList.get(i).getReason() + "', '" + reasonMasterList.get(i).getReason_for() + "'," + reasonMasterList.get(i).getReason_status() + ",'" + tamil_rval + "','" + telugu_rval + "','" + punjabi_rval + "','" + hindi_rval + "','" + bengali_rval + "','" + kannada_rval + "','" + assam_rval + "','" + orissa_rval + "','" + marathi_rval + "','" + reasonMasterList.get(i).getReason_type() + "')";
                        database.execSQL(insertUndeliveredReason);
                    } else {
                        String updateUndeliveredReason = "Update ReasonMaster set id = " + reasonMasterList.get(i).getId() + "," +
                                "rid = " + reasonMasterList.get(i).getRid() + ",reason = '" + reasonMasterList.get(i).getReason() + "', reason_for ='" + reasonMasterList.get(i).getReason_for() + "' ,reasonstatus = " + reasonMasterList.get(i).getReason_status() + ",tamil = '" + tamil_rval + "'," +
                                "telugu = '" + telugu_rval + "',punjabi = '" + punjabi_rval + "', hindi = '" + hindi_rval + "',bengali = '" + bengali_rval + "', kannada = '" + kannada_rval + "'," +
                                "assam = '" + assam_rval + "', orissa = '" + orissa_rval + "', marathi = '" + marathi_rval + "', reason_type = '" + reasonMasterList.get(i).getReason_type() + "'  where rid = '" + reasonMasterList.get(i).getRid() + "'";
                        database.execSQL(updateUndeliveredReason);
                    }
                }

//                getAttemptCount();// removed for testing
            }

            @Override
            public void onError(Throwable e) {
//                Log.d("error", e.toString());
            }

            @Override
            public void onComplete() {
//                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

    public void uploadNonSyncImage() {
//        Log.v("sync_service_ship", "-" + shipno);
        Cursor customerName = database.rawQuery("select  O.delivery_status, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber ,IFNULL(D.customer_name,0) as customer_name,IFNULL" +
                "(D.amount_collected,0) as amount_collected,IFNULL(D.customer_contact_number,0) as customer_contact_number," +
                "IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                ", IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.relation_proof, 0) as relation_proof,IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof," +
                "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D.latitude,0) as latitude,IFNULL" +
                "(D.longitude,0) as longitude" + " from orderheader O LEFT JOIN DeliveryConfirmation D on D" +
                ".shipmentnumber = O.Shipment_Number where O" +
                ".image_status ='C'  ", null);

        if (customerName.getCount() > 0) {
            while (!customerName.isAfterLast()) {
                customerName.moveToFirst();
                String file_deliveryProof = file_path + customerName.getString(customerName.getColumnIndex("delivery_proof"));
                String file_addressProof = file_path + customerName.getString(customerName.getColumnIndex("id_proof"));
                String file_invoiceProof = file_path + customerName.getString(customerName.getColumnIndex("Invoice_proof"));
                String file_relationproof = file_path + customerName.getString(customerName.getColumnIndex("relation_proof"));
                String file_signature = sign_path + customerName.getString(customerName.getColumnIndex("signature_proof"));
                Log.v("sync_service_getname", customerName.getString(customerName.getColumnIndex("delivery_proof")));
                final String ship_no = customerName.getString(customerName.getColumnIndex("shipmentnumber"));

                String file_pickup = null;

                if (customerName.getString(customerName.getColumnIndex("order_type")).equalsIgnoreCase("3")) {

                    Cursor getPickup = database.rawQuery("Select customerphoto  from PickupConfirmation where shipmentno = '" + ship_no + "' ", null);


                    if (getPickup.getCount() > 0) {
                        getPickup.moveToFirst();
                        file_pickup = file_path + getPickup.getString(getPickup.getColumnIndex("customerphoto"));

                    }
                    getPickup.close();
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                DeliveryConfirmReq delivery = new DeliveryConfirmReq();
                JSONObject paramObject = null;
//                delivery.setShipmentNumber(customerName.getString(customerName.getColumnIndex("shipmentnumber")));
//                delivery.setDeliveryProof(customerName.getString(customerName.getColumnIndex("delivery_proof")));

                /**** Get Delivery Proof Image****/
                File fileDeliveryProof = new File(file_deliveryProof);
                RequestBody requestBodyDelivery = RequestBody.create(MediaType.parse("*/*"), fileDeliveryProof);
                MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("delivery_file", fileDeliveryProof.getName(), requestBodyDelivery);

                /**** Get Address Proof Image****/
                File fileAddressProof = new File(file_addressProof);
                RequestBody requestBodyAddress = RequestBody.create(MediaType.parse("*/*"), fileAddressProof);
                MultipartBody.Part fileToAddress = MultipartBody.Part.createFormData("address_file", fileAddressProof.getName(), requestBodyAddress);


                /**** Get Invoice Proof Image****/
                File fileInvoiceProof = new File(file_invoiceProof);
                RequestBody requestBodyInvoice = RequestBody.create(MediaType.parse("*/*"), fileInvoiceProof);
                MultipartBody.Part fileToInvoice = MultipartBody.Part.createFormData("invoice_file", fileInvoiceProof.getName(), requestBodyInvoice);

                /**** Get Relation Proof Image****/
                File filerelationproof = new File(file_relationproof);
                RequestBody requestBodyrelation = RequestBody.create(MediaType.parse("*/*"), filerelationproof);
                MultipartBody.Part fileToRelation = MultipartBody.Part.createFormData("relation_file", filerelationproof.getName(), requestBodyrelation);

                /**** Get Signature Proof Image****/
                File fileSignProof = new File(file_signature);
                RequestBody requestBodySign = RequestBody.create(MediaType.parse("*/*"), fileSignProof);
                MultipartBody.Part fileToSign = MultipartBody.Part.createFormData("sign_file", fileSignProof.getName(), requestBodySign);


                File filePickupProof = new File(file_signature);
                RequestBody requestBodyPickup = RequestBody.create(MediaType.parse("*/*"), filePickupProof);
                MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);

                final Observable<DeliveryConfirmResp> observable = apiService.getDeliveryImage(fileToDelivery, fileToInvoice, fileToAddress, fileToRelation, fileToSign, fileToPickup) //,fileToAddress,fileToInvoice,fileToSign

                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<DeliveryConfirmResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeliveryConfirmResp deliveryVal) {
                        deliveryList = new ArrayList<>();
                        List<DeliveryConfirmResp> orderVal = deliveryVal.getDelivery();
//                        Log.v("del_image_response", deliveryVal.getRes_msg());
                        if (deliveryVal.getRes_msg().equals("image success")) {
//                            database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
//                                    shipno + "' ");
                            database.execSQL("UPDATE orderheader set image_status = 'U' where Shipment_Number ='" +
                                    ship_no + "' ");
                            attempt_count = 0;
                        /*    Logger.showShortMessage(SyncService.this, "Delivery has been uploaded successfully");
                            Intent goToMain = new Intent(SyncService.this, MainActivity.class);
                            startActivity(goToMain);*/
                            pageTrackerService();

                        } else if (deliveryVal.getRes_msg().equals("image failed")) {
                            pageTrackerService();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
//                        Log.d("error", e.toString());
                    }

                    @Override
                    public void onComplete() {
//                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
//                Log.v("sync_service", "move to next");
                customerName.moveToNext();
            }
        }
        customerName.close();


    }

    private class OrderDataAsync extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {

            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

            new MyAsyncTask().execute();

            //  getData();

            return true;
        }

        protected void onPostExecute(Boolean flag) {

            modifyrecorddataAsync = new ModifyRecordDataAsync();
            modifyrecorddataAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            uploadimageasync = new UploadImageAsync();
//            uploadimageasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class UploadImageAsync extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {

            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

//            uploadImage();
            uploadComplete();

            return true;
        }

        protected void onPostExecute(Boolean flag) {
            uploadPickupComplete();
            reasonasync = new ReasonAsync();
            reasonasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class ReasonAsync extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {

            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

            getReason();
            BFILbranchMaster();

            return true;
        }

        protected void onPostExecute(Boolean flag) {

            undelivereddatadsync = new UndeliveredDataAsync();
            undelivereddatadsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class UndeliveredDataAsync extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {

            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

            uploadUndelivered();

            return true;
        }

        protected void onPostExecute(Boolean flag) {

            orderdatadsync = new OrderDataAsync();
            orderdatadsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            modifyrecorddataAsync = new ModifyRecordDataAsync();
//            modifyrecorddataAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    private class ModifyRecordDataAsync extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {

            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

            showOlderRecords();
            showOlderUndelivered();
//            orderSyncStatus();

            return true;
        }

        protected void onPostExecute(Boolean flag) {

            changestatusssync = new ChangeStatusAsync();
            changestatusssync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class MyTask extends AsyncTask<String, Void, Void> {
        String order_id;
        String ship_id;


        MyTask(String orderid, String shipid) {
            // list all the parameters like in normal class define
            this.order_id = orderid;
            this.ship_id = shipid;

        }

        @Override
        protected Void doInBackground(String... strings) {
            changeOrderStatus(order_id, ship_id);

            return null;
        }
    }

    public static void fix() {
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
//            field.set(null, 102400 * 1024);
            field.set(null, 204800 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeStatusOrder() {
        String ship_val = "";
        String userid = "";
        Cursor statusOrder = database.rawQuery("select  * from orderheader where sync_status = 'P'  ", null);
        statusOrder.moveToFirst();
        if (statusOrder.getCount() > 0) {
            while (!statusOrder.isAfterLast()) {

                userid = AppController.getStringPreference(Constants.USER_ID, "");

                if (ship_val.equals("")) {
                    ship_val = statusOrder.getString(statusOrder.getColumnIndex("Shipment_Number"));
                } else {
                    ship_val = ship_val + "," + statusOrder.getString(statusOrder.getColumnIndex("Shipment_Number"));
                }


                statusOrder.moveToNext();
            }


        }
        Log.v("change_shipval", ship_val);
        OrderStatusUpdate(userid, ship_val);
        statusOrder.close();
    }

    private class ChangeStatusAsync extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {

            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

            changeStatusOrder();

            return true;
        }

        protected void onPostExecute(Boolean flag) {
            uploadImage();
            uploadUndeliveredImage();
            orderSyncStatus();
//            removeAlreadyDelivered();
        }
    }

    private void OrderStatusUpdate(String runnerID, String shipmentID) {
        Log.v("changeOrderStatus", runnerID + "-" + shipmentID);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);
        OrderStatusReq orderStatus = new OrderStatusReq();
        JSONObject paramObject = null;
        orderStatus.setRunnerID(runnerID);
        orderStatus.setShipmentID(shipmentID);
        try {
            paramObject = new JSONObject();
            paramObject.put("runner_id", orderStatus.getRunnerID());
            paramObject.put("shipment_id", orderStatus.getShipmentID());
//            Log.v("orderstatus", runnerID + "-" + shipmentID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

        final Observable<OrderChangeResp> observable = apiService.getOrderStatus(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<OrderChangeResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(OrderChangeResp value) {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                orderList = new ArrayList<>();
                OrderChangeResp detailVal = value;
                Log.v("response_message", detailVal.getRes_msg());

                Intent intent = new Intent("SyncServiceAction");
                Bundle bundle = new Bundle();
                bundle.putString("current_time", currentDateTimeString);
                intent.putExtras(bundle);
                sendBroadcast(intent);
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                uploadComplete();
//                uploadImage();

            }

            @Override
            public void onError(Throwable e) {
                Log.d("orderstaterror", e.toString());
            }

            @Override
            public void onComplete() {
                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }


    private OkHttpClient getRequestHeader() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.MINUTES)
                .connectTimeout(15, TimeUnit.MINUTES)
                .writeTimeout(15, TimeUnit.MINUTES)
                .build();

        return okHttpClient;
    }


    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.readTimeout(15, TimeUnit.MINUTES);
            builder.connectTimeout(15, TimeUnit.MINUTES);
            builder.writeTimeout(15, TimeUnit.MINUTES);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getImagesToSync() {


        final String new_file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";

        File directory = new File(new_file_path);
        File[] files1;
        files1 = directory.listFiles();

        String asas = null;
        if (directory.isDirectory()) {
            if (files1.length != 0) {
                for (int i = 0; i < 1; i++) {
                    filePaths.add(new File(new_file_path + "/" + files1[i].getName()));
                    asas = new_file_path + "/" + files1[i].getName();
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getRequestHeader())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                File fileProofPhoto = new File(asas);
                RequestBody requestBodyDelivery = RequestBody.create(MediaType.parse("*/*"), fileProofPhoto);
                MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("sign_file", fileProofPhoto.getName(), requestBodyDelivery);

                final Observable<ImageSyncResp> observable = apiService.uploadMultiFile(fileToDelivery)
                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<ImageSyncResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageSyncResp value) {
                        File deleteImgDeliverProof = new File(new_file_path + value.getRes_sign());
                        if (deleteImgDeliverProof.exists()) {
                            deleteImgDeliverProof.delete();
                        }

                        filePaths.clear();
                        getImagesToSync();
                        File dir = new File(String.valueOf(SyncService.this.getFilesDir()) + "/UserSignature");
                        if (dir.exists() && dir.isDirectory()) {
                            getSignatureToSync();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                    Log.d("error", e.toString());

                    }

                    @Override
                    public void onComplete() {
//                    Log.v("inhere", "--");

                    }


                });
            }


        }
    /*  else{
            filePaths = new ArrayList();
//             final String new_sign_path = String.valueOf(this.getFilesDir()) + "/UserSignature/";
             final String new_sign_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";

            File directory1 = new File(new_sign_path);
            File[] files2;
            files2 = directory1.listFiles();

            String weee = null;

                for (int i = 0; i < 1; i++)
                {

                        filePaths.add(new File(new_sign_path + "/" + files2[i].getName()));
                        weee = new_sign_path + "/" + files2[i].getName();

                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getRequestHeader())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                File fileProofPhoto = new File(weee);

                MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("sign_file", fileProofPhoto.getName(), requestBodyDelivery);

                final Observable<ImageSyncResp> observable = apiService.uploadMultiFile(fileToDelivery)
                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<ImageSyncResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageSyncResp value) {
                        File deleteImgDeliverProof = new File(new_sign_path + value.getRes_sign());
                        if (deleteImgDeliverProof.exists()) {
                            deleteImgDeliverProof.delete();
                        }

                        getImagesToSync();

                    }

                    @Override
                    public void onError(Throwable e) {
                    Log.d("error", e.toString());

                    }

                    @Override
                    public void onComplete() {
//                    Log.v("inhere", "--");

                    }


                });
        }  */
    }


    public void getSignatureToSync() {

        Cursor customerName = database.rawQuery("select DISTINCT O.delivery_status,O.order_type,O.delivery_aadhar_required, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber ,IFNULL(D.customer_name,0) as customer_name,IFNULL" +

                "(D.amount_collected,0) as amount_collected,IFNULL(D.customer_contact_number,0) as customer_contact_number," +
                "IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                ", IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.relation_proof, 0) as relation_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof," +
                "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D.latitude,0) as latitude,IFNULL" +
                "(D.longitude,0) as longitude" + " from orderheader O INNER JOIN DeliveryConfirmation D on D" +
                ".shipmentnumber = O.Shipment_Number where O" +
                ".image_status='C' LIMIT 1 ", null);
//                ".sync_status='C' LIMIT 1 ", null);

        if (customerName.getCount() > 0) {

            final String new_file_path = String.valueOf(this.getFilesDir()) + "/UserSignature/";

            File directory = new File(new_file_path);
            File[] files1;
            files1 = directory.listFiles();

            String asas = null;
            if (files1.length != 0) {
                for (int i = 0; i < 1; i++) {
                    filePaths2.add(new File(new_file_path + "/" + files1[i].getName()));
                    asas = new_file_path + "/" + files1[i].getName();
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getRequestHeader())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                File fileProofPhoto = new File(asas);
                RequestBody requestBodyDelivery = RequestBody.create(MediaType.parse("*/*"), fileProofPhoto);
                MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("sign_file", fileProofPhoto.getName(), requestBodyDelivery);

                final Observable<ImageSyncResp> observable = apiService.uploadMultiFile(fileToDelivery)
                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<ImageSyncResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageSyncResp value) {
                        File deleteImgDeliverProof = new File(new_file_path + value.getRes_sign());
                        if (deleteImgDeliverProof.exists()) {
                            deleteImgDeliverProof.delete();
                        }
                        filePaths2.clear();
                        getSignatureToSync();

                    }

                    @Override
                    public void onError(Throwable e) {
//                    Log.d("error", e.toString());

                    }

                    @Override
                    public void onComplete() {
//                    Log.v("inhere", "--");

                    }


                });


            }
        }

    }

    private void orderSyncStatus() {

        Cursor customerName = database.rawQuery("SELECT Shipment_Number FROM orderheader where sync_status = 'E' ", null);
//         Cursor customerName = database.rawQuery("SELECT Shipment_Number FROM orderheader where sync_status = 'C' OR sync_status = 'E' ", null);
//        Cursor customerName = database.rawQuery("SELECT Shipment_Number FROM orderheader where sync_status != 'U' ", null);

        if (customerName.getCount() > 0) {
            customerName.moveToFirst();
            while (!customerName.isAfterLast()) {
                String shipno = customerName.getString(customerName.getColumnIndex("Shipment_Number"));
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);
                OrderStatusReq orderStatus = new OrderStatusReq();
                JSONObject paramObject = null;
                orderStatus.setShipmentID(shipno);
                try {
                    paramObject = new JSONObject();
                    paramObject.put("shipmentid", orderStatus.getShipmentID());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                final Observable<OrderChangeResp> observable = apiService.getSyncOrder(requestBody).subscribeOn
                        (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<OrderChangeResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OrderChangeResp value) {

                        OrderChangeResp detailVal = value;
                        Log.v("testresponse_message", detailVal.getRes_msg());
                        if (detailVal.getRes_code().equals("1")) {
                            database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
                                    detailVal.getRes_msg() + "' ");


//                            database.execSQL("DELETE FROM orderheader where Shipment_Number = '" + detailVal.getRes_msg() + "' AND  sync_status = 'P'");

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ordererror", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
                customerName.moveToNext();
            }

        }
        customerName.close();
    }


    /*private void orderSyncStatus() {

        Cursor customerName = database.rawQuery("SELECT Shipment_Number FROM orderheader where sync_status = 'C' OR sync_status = 'E' ", null);

        if (customerName.getCount() > 0) {
            customerName.moveToFirst();
            while (!customerName.isAfterLast()) {
                String shipno = customerName.getString(customerName.getColumnIndex("Shipment_Number"));
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);
                OrderStatusReq orderStatus = new OrderStatusReq();
                JSONObject paramObject = null;
                orderStatus.setShipmentID(shipno);
                try {
                    paramObject = new JSONObject();

                    paramObject.put("shipment_id", orderStatus.getShipmentID());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                final Observable<OrderChangeResp> observable = apiService.getSyncOrder(requestBody).subscribeOn
                        (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<OrderChangeResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OrderChangeResp value) {

                        OrderChangeResp detailVal = value;
                        Log.v("response_message", detailVal.getRes_msg());
                        if (detailVal.getRes_code().equals("1")) {
                            database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
                                    detailVal.getRes_msg() + "' ");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ordererror", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
                customerName.moveToNext();
            }

        }

        customerName.close();
    }*/
    public void removeAlreadyDelivered() {
        Cursor deleteOrder = database.rawQuery("SELECT O.Shipment_Number FROM orderheader O  LEFT JOIN DeliveryConfirmation D on O.Shipment_Number = D.shipmentnumber where O.sync_status = 'U' AND O.attempt_count = 0 ", null);
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {
                String shpId = deleteOrder.getString(deleteOrder.getColumnIndex("Shipment_Number"));
                database.execSQL("DELETE FROM orderheader where Shipment_Number = '" + shpId + "' ");
                deleteOrder.moveToNext();
            }
        }
    }

    public void getServiceData() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final InthreeApi apiService = retrofit.create(InthreeApi.class);
        final OrderReq order = new OrderReq();
        JSONObject paramObject = null;


        order.setId(AppController.getStringPreference(Constants.USER_ID, ""));

        try {
            paramObject = new JSONObject();
            paramObject.put("runner_id", order.getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());


        final Observable<ServiceOrderResp> observable = apiService.getServiceDetails(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<ServiceOrderResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ServiceOrderResp value) {


                List<ServiceOrderResp> detailVal = value.getServiceItems();
                if (value.getRes_msg().equals("service success")) {
                    Log.v("get_data_msg", value.getRes_msg());
                    for (int i = 0; i < detailVal.size(); i++) {
                        Cursor checkOrder = database.rawQuery("Select * from serviceMaster where shipment_id = '" +
                                        detailVal.get(i).getShipment_id() + "'",
                                null);
                        if (checkOrder.getCount() == 0) {

                            Log.v("OrderResp", detailVal.get(i).getRes_msg() + "shipid" + detailVal.get(i).getServiceItems() + "getotp");

                            String insertServiceMaster = "Insert into serviceMaster(reference,order_id,shipment_id,customer_name,customer_contact_number,alternate_contact_number,shipping_address,shipping_city," +
                                    "shipping_pincode,shipping_telephone,cityCode,lmp_code,agent_id,sync_status,assigned_at,received_at,download_sync,dio_status,attempt,reason,created_at) " +
                                    "Values('" + detailVal.get(i).getReference() + "','" + detailVal.get(i).getOrder_id() + "','" + detailVal.get(i).getShipment_id() + "','" + detailVal.get(i).getCustomer_name() + "'," +
                                    "'" + detailVal.get(i).getCustomer_contact_number() + "','" + detailVal.get(i).getAlternate_contact_number() + "','" + detailVal.get(i).getShipping_address() + "','" + detailVal.get(i).getShipping_city() + "'," +
                                    "'" + detailVal.get(i).getShipping_pincode() + "','" + detailVal.get(i).getShipping_telephone() + "','" + detailVal.get(i).getCityCode() + "','" + detailVal.get(i).getLmp_code() + "'," +
                                    "'" + detailVal.get(i).getAgent_id() + "','P','','','','','','','')";
                            database.execSQL(insertServiceMaster);

                        } else {


                            String queryupdate = "UPDATE serviceMaster set reference = '" + detailVal.get(i).getReference() +
                                    "',order_id='" + detailVal.get(i).getOrder_id() + "',shipment_id = '" + detailVal.get(i).getShipment_id() + "'," +
                                    "customer_name = '" + detailVal.get(i).getCustomer_name() + "'," +
                                    "customer_contact_number = '" + detailVal.get(i).getCustomer_contact_number() + "'," +
                                    "alternate_contact_number= '" + detailVal.get(i).getAlternate_contact_number() + "'," +
                                    "shipping_address= '" + detailVal.get(i).getShipping_address() + "'," +
                                    "shipping_city= '" + detailVal.get(i).getShipping_city() + "'," +
                                    "shipping_pincode= '" + detailVal.get(i).getShipping_pincode() + "'," +
                                    "shipping_telephone= '" + detailVal.get(i).getShipping_telephone() + "'," +
                                    "cityCode= '" + detailVal.get(i).getCityCode() + "'," +
                                    "lmp_code= '" + detailVal.get(i).getLmp_code() + "', " +
                                    "agent_id= '" + detailVal.get(i).getAgent_id() + "'" +
                                    " where " +
                                    "shipment_id ='" + detailVal.get(i).getShipment_id() + "'  ";
                            database.execSQL(queryupdate);

                        }

                        String product_name = "";
                        for (int j = 0; j < detailVal.get(i).getServiceDetails().size(); j++) {

                            Log.v("prod_names", detailVal.get(i).getServiceDetails().get(j).getName() + "--" + detailVal.get(i).getShipment_id());


                            Cursor checkServiceItems = database.rawQuery("Select * from serviceItems where shipment_number = '" +
                                            detailVal.get(i).getShipment_id() + "' AND sku = '" + detailVal.get(i).getServiceDetails().get(j).getSku() + "'",
                                    null);
                            if (checkServiceItems.getCount() == 0) {

                                String insertProduct = "Insert into serviceItems(service_id,sku,name,qty,item_received,qty_demo_completed,order_item_id,product_serial_no,product_type,created_at,shipment_number)" +
                                        " Values ('" + detailVal.get(i).getServiceDetails().get(j).getService_id() + "', '" + detailVal.get(i).getServiceDetails().get(j).getSku()
                                        + "', '" + detailVal.get(i).getServiceDetails().get(j).getName() + "', '" + detailVal.get(i).getServiceDetails().get(j).getQty() + "', '" +
                                        detailVal.get(i).getServiceDetails().get(j).getItem_received() + "','" + detailVal.get(i).getServiceDetails().get(j).getQty_demo_completed() + "','" + detailVal.get(i).getServiceDetails().get(j).getOrder_item_id() + "','" + detailVal.get(i).getServiceDetails().get(j).getProduct_serial_no() + "','" + detailVal.get(i).getServiceDetails().get(j).getProduct_type() + "','" + detailVal.get(i).getServiceDetails().get(j).getCreated_at() + "','" + detailVal.get(i).getShipment_id() + "')";
                                database.execSQL(insertProduct);
                            } else {


                                String updateProducts = "UPDATE serviceItems set name = '" + detailVal.get(i).getServiceDetails().get(j).getName() + "', item_received = '" + detailVal.get(i).getServiceDetails().get(j).getItem_received() + "', order_item_id = '" + detailVal.get(i).getServiceDetails().get(j).getOrder_item_id() + "',product_serial_no = '" + detailVal.get(i).getServiceDetails().get(j).getProduct_serial_no() + "',product_type = '" + detailVal.get(i).getServiceDetails().get(j).getProduct_type() + "'" +
                                        " where shipment_number = '" + detailVal.get(i).getShipment_id() + "' AND sku = '" + detailVal.get(i).getServiceDetails().get(j).getSku() + "' ";
                                database.execSQL(updateProducts);
                            }

                            for (int k = 0; k < detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().size(); k++) {
                                Cursor checkServiceAttribute = database.rawQuery("Select * from serviceProductAttributes where shipment_no = '" +
                                                detailVal.get(i).getShipment_id() + "' AND attribute_id = '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getAttribute_id() + "'",
                                        null);
                                if (checkServiceAttribute.getCount() == 0) {
                                    String insertProductAttribute = "Insert into serviceProductAttributes(attribute_id,attribute_name,product_type,shipment_no,checked,input_field_type,is_require,attribute_type)" +
                                            " Values ('" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getAttribute_id() + "', '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getAttribute_name()
                                            + "', '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getProduct_type() + "', '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getShip_no() + "', '','" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getInput_field_type() + "','" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getIs_require() + "','" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getAttribute_type() + "')";
                                    database.execSQL(insertProductAttribute);
                                } else {
                                    String updateProductsAttributes = "UPDATE serviceProductAttributes set attribute_name = '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getAttribute_name() + "', input_field_type = '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getInput_field_type() + "', is_require = '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getIs_require() + "',attribute_type ='" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getAttribute_type() + "'" +
                                            " where shipment_no = '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getShip_no() + "' AND attribute_id = '" + detailVal.get(i).getServiceDetails().get(j).getServiceAttributes().get(k).getAttribute_id() + "' ";
                                    database.execSQL(updateProductsAttributes);
                                }
                            }

                            checkServiceItems.close();
                        }

                        checkOrder.close();
                    }


                } else if (value.getRes_msg().equals("service failed")) {

                } else {

                }

                changeStatusService();
            }

            @Override
            public void onError(Throwable e) {
//                Log.d("error", e.toString());
            }

            @Override
            public void onComplete() {
//                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

    public void getServiceIncomlpeteReasonData() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);

        final Observable<ServiceIncompleteResp> observable = apiService.getServiceReason().subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<ServiceIncompleteResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ServiceIncompleteResp value) {


                List<ReasonVal> detailVal = value.getReasonVal();
                if (value.getResMsg().equals("reason success")) {
                    Log.v("get_data_msg", value.getResMsg());
                    for (int i = 0; i < detailVal.size(); i++) {
                        Cursor checkOrder = database.rawQuery("Select * from ServiceIncompleteReasonMaster where rid = '" +
                                        detailVal.get(i).getId() + "'",
                                null);
                        if (checkOrder.getCount() == 0) {

                            Log.v("OrderResp", detailVal.get(i).getReason() + "shipid" + detailVal.get(i).getDioStatus() + "getotp");

                            String insertServiceMaster = "Insert into ServiceIncompleteReasonMaster(rid,reason,reasonstatus) " +
                                    "Values('" + detailVal.get(i).getId() + "','" + detailVal.get(i).getReason() + "','" + detailVal.get(i).getDioStatus() + "')";
                            database.execSQL(insertServiceMaster);

                        } else {


                            String queryupdate = "UPDATE ServiceIncompleteReasonMaster set reasonstatus = '" + detailVal.get(i).getDioStatus() +
                                    "',rid='" + detailVal.get(i).getId() + "'," +
                                    "reason = '" + detailVal.get(i).getReason() + "'" +
                                    " where " +
                                    "rid ='" + detailVal.get(i).getId() + "'  ";
                            database.execSQL(queryupdate);

                        }

                        checkOrder.close();
                    }


                } else if (value.getResMsg().equals("reason failed")) {

                } else {

                }


            }

            @Override
            public void onError(Throwable e) {
//                Log.d("error", e.toString());
            }

            @Override
            public void onComplete() {
//                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

    public void changeStatusService() {
        String ship_val = "";
        String userid = "";
        Cursor statusOrder = database.rawQuery("select  * from serviceMaster where sync_status = 'P'  ", null);
        statusOrder.moveToFirst();
        if (statusOrder.getCount() > 0) {
            while (!statusOrder.isAfterLast()) {

                userid = AppController.getStringPreference(Constants.USER_ID, "");

                if (ship_val.equals("")) {
                    ship_val = statusOrder.getString(statusOrder.getColumnIndex("shipment_id"));
                } else {
                    ship_val = ship_val + "," + statusOrder.getString(statusOrder.getColumnIndex("shipment_id"));
                }


                statusOrder.moveToNext();
            }


        }
        Log.v("change_shipval", ship_val);
        ServiceStatusUpdate(userid, ship_val);
        statusOrder.close();
    }

    private void ServiceStatusUpdate(String runnerID, String shipmentID) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);
        OrderStatusReq orderStatus = new OrderStatusReq();
        JSONObject paramObject = null;
        orderStatus.setRunnerID(runnerID);
        orderStatus.setShipmentID(shipmentID);
        try {
            paramObject = new JSONObject();
            paramObject.put("agent_id", orderStatus.getRunnerID());
            paramObject.put("shipment_id", orderStatus.getShipmentID());
//            Log.v("orderstatus", runnerID + "-" + shipmentID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

        final Observable<OrderChangeResp> observable = apiService.getServiceStatus(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<OrderChangeResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(OrderChangeResp value) {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                orderList = new ArrayList<>();
                OrderChangeResp detailVal = value;
                Log.v("response_message", detailVal.getRes_msg());

                Intent intent = new Intent("SyncServiceAction");
                Bundle bundle = new Bundle();
                bundle.putString("current_time", currentDateTimeString);
                intent.putExtras(bundle);
                sendBroadcast(intent);
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                uploadComplete();
//                uploadImage();

            }

            @Override
            public void onError(Throwable e) {
                Log.d("ordererror", e.toString());
            }

            @Override
            public void onComplete() {
                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

    private void syncUploadSer() {
        if (roleId.equalsIgnoreCase("4")) {
            getServiceData();
            getServiceIncomlpeteReasonData();
            uploadIncompletedelivered();
            uploadServiceComplete();

        } else if (roleId.equalsIgnoreCase("3")) {
            uploadimageasync = new UploadImageAsync();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                uploadimageasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                uploadimageasync.execute();
            }
        }

    }

    /**
     * Upload all the data into server side
     */
    public void uploadIncompletedelivered() {
        // Delete from DeliveryConfirmation table to avoid possible duplicate entry.
        //database.execSQL("DELETE FROM DeliveryConfirmation where shipmentnumber='" + shipmentNumber + "'");


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
        String currentDateTimeString = format.format(new Date());


//            Log.v("shipmentNumber", shipmentNumber);
        Cursor getIncompleteValue = database.rawQuery("select  s.sync_status,s.order_id, s.shipment_id, " +
                "IFNULL(i.ship_no,0) as ship_no,IFNULL(s.attempt, 0) as attempt, IFNULL(i.reason,0) as reason,IFNULL(i.incom_long,0) as incom_long,IFNULL(i.incom_lat,0) as incom_lat, " +
                "IFNULL(i.reason_status, 0) as reason_status  from serviceMaster s INNER JOIN ServiceIncompleteConfirmation i on i" +
                ".ship_no = s.shipment_id where sync_status = 'C' ", null);
//            Log.v("get_attempt_count", String.valueOf(getUndeliveryValue.getCount()));

        if (getIncompleteValue.getCount() > 0) {
            getIncompleteValue.moveToFirst();
//                Log.v("get_attempt_count", String.valueOf(getUndeliveryValue.getInt(getUndeliveryValue.getColumnIndex("attempt_count"))));
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getRequestHeader())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            InthreeApi apiService = retrofit.create(InthreeApi.class);

            JSONObject paramObject = null; // Main JSON Object
            JSONObject jsonFieldObj;


            //unIncomAttemptCount = Integer.parseInt(getIncompleteValue.getString(getIncompleteValue.getColumnIndex("attempt")));
            unIncomAttemptCount = getIncompleteValue.getInt(getIncompleteValue.getColumnIndex("attempt"));
            final String shipmentId = getIncompleteValue.getString(getIncompleteValue.getColumnIndex("shipment_id"));


            //un_attempt_count++;
//                Log.v("attempt_count", String.valueOf(attempt_count));

            paramObject = new JSONObject();
            jsonFieldObj = new JSONObject();

            try {
                paramObject.put("runsheetNo", AppController.getStringPreference(Constants.USER_ID, ""));
                paramObject.put("reasonStatus", getIncompleteValue.getString(getIncompleteValue.getColumnIndex("reason_status")));
                paramObject.put("reason", getIncompleteValue.getString(getIncompleteValue.getColumnIndex("reason")));
                paramObject.put("latitude", getIncompleteValue.getString(getIncompleteValue.getColumnIndex("incom_lat")));
                paramObject.put("longitude", getIncompleteValue.getString(getIncompleteValue.getColumnIndex("incom_long")));
                paramObject.put("attemptCount", unIncomAttemptCount);
                paramObject.put("created_at", currentDateTimeString);
                paramObject.put("shipmentnumber", getIncompleteValue.getString(getIncompleteValue.getColumnIndex("ship_no")));
                paramObject.put("orderno", getIncompleteValue.getString(getIncompleteValue.getColumnIndex("order_id")));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("uploadUndelivered", paramObject.toString());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

            final Observable<UndeliveryResp> observable = apiService.getIncompleteSync(requestBody)
                    .subscribeOn
                            (Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            observable.subscribe(new Observer<UndeliveryResp>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(UndeliveryResp value) {


                    Log.v("undeliveredupload", "- " + value.getRes_msg() + "-- " + value.getRes_code());
//                        uploadImage();
                    if (value.getRes_msg().equalsIgnoreCase("service incomplete success")) {
                        unIncomAttemptCount++;
                        database.execSQL("UPDATE serviceMaster set sync_status = 'U', attempt = " + unIncomAttemptCount + " where shipment_id ='" +
                                shipmentId + "' ");


                    } else if (value.getRes_msg().equalsIgnoreCase("service incomplete failed")) {
                        //alertDialogMsg(UndeliveryActivity.this, getResources().getString(R.string.undeli_title),  getResources().getString(R.string.undeli_success_msg), getResources().getString(R.string.ok));

                        //updateOrderStatus();
                    } else {


                    }


                }

                @Override
                public void onError(Throwable e) {
                    Log.d("uploadUndelivered", e.toString());

                }

                @Override
                public void onComplete() {
                    Log.v("inhere", "uploadUndelivered");
//                observable.unsubscribeOn(Schedulers.newThread());

                }


            });
        }


    }

    public void uploadServiceComplete() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
        String currentDateTimeString = format.format(new Date());
        Log.v("currentDateTimeString22", currentDateTimeString);


        //     Cursor customerName = database.rawQuery("select SC.ship_num,SC.customer_fname,SC.customer_cnum,SC.ship_address,SC.ship_city,SC.ship_phone,SC.customer_feedback,SC.created_date,IFNULL(SC.function, null) as function,IFNULL(SC.documents, null) as documents ,IFNULL(SC.feedback ,null) as feedback ,S.sync_status,IFNULL(S.attempt,0 ) as attempt,S.order_id from serviceMaster S  INNER JOIN serviceConfirmation SC on SC.ship_num = S.shipment_id  where S.sync_status='C' ", null);
        Cursor customerName = database.rawQuery("select SC.ship_num,SC.customer_fname,SC.customer_cnum,SC.ship_address,SC.ship_city,SC.signProof,SC.ship_phone,SC.customer_feedback,SC.created_date,IFNULL(SC.function, null) as function,IFNULL(SC.documents, null) as documents ,IFNULL(SC.feedback ,null) as feedback ,S.sync_status,IFNULL(S.attempt,0 ) as attempt,S.order_id,S.shipping_pincode,S.attempt from serviceMaster S  INNER JOIN serviceConfirmation SC on SC.ship_num = S.shipment_id  where S.sync_status='C' ", null);

        if (customerName.getCount() > 0) {
            customerName.moveToFirst();
            Log.v("get_reason", "--" + customerName.getString(customerName.getColumnIndex("ship_num")));
            final String serviceShipNo = customerName.getString(customerName.getColumnIndex("ship_num"));
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            InthreeApi apiService = retrofit.create(InthreeApi.class);

            ServiceConfirmReq servicePelivery = new ServiceConfirmReq();
            PartialReq.FieldData fieldData = new PartialReq.FieldData();

            JSONObject paramObject = null; // Main JSON Object
            JSONObject jsonFieldObj; // FieldData JSON Object
            JSONArray jsonDetailsArray; // Details JSON Array
            JSONObject jsonAmountCollected; // Amount Collected JSON Object
            JSONObject jsonDummy;
            JSONObject jsonAttribute;
            JSONObject jsonProduct;
            JSONArray jsonItemCodeArray; // Itemcode JSON Array
            JSONObject jsonItemCodeObject; // Itemcode JSON Object
            JSONObject jsonProofFieldObj;
            Log.v("upload_custname", customerName.getString(customerName.getColumnIndex("customer_fname")));
            servicePelivery.setAgent_id(AppController.getStringPreference(Constants.USER_ID, ""));

            servicePelivery.setCustomer_fname(customerName.getString(customerName.getColumnIndex("customer_fname")));
            servicePelivery.setShip_num(customerName.getString(customerName.getColumnIndex("ship_num")));
            servicePelivery.setOrder_num(customerName.getString(customerName.getColumnIndex("order_id")));
            servicePelivery.setShip_address(customerName.getString(customerName.getColumnIndex("ship_address")));
            servicePelivery.setCustomer_cnum(customerName.getString(customerName.getColumnIndex("customer_cnum")));
            servicePelivery.setShip_phone(customerName.getString(customerName.getColumnIndex("ship_phone")));
            servicePelivery.setShip_city(customerName.getString(customerName.getColumnIndex("ship_city")));
            servicePelivery.setCreated_date(customerName.getString(customerName.getColumnIndex("created_date")));
            servicePelivery.setSignProof(customerName.getString(customerName.getColumnIndex("signProof")));
            servicePelivery.setShippincode(customerName.getString(customerName.getColumnIndex("shipping_pincode")));
            servicePelivery.setAttempt_count(customerName.getInt(customerName.getColumnIndex("attempt")));

            service_attempt_count = servicePelivery.getAttempt_count();
            service_attempt_count++;


            try {
                jsonItemCodeArray = new JSONArray();
                jsonItemCodeObject = new JSONObject();
                jsonFieldObj = new JSONObject();
                jsonDetailsArray = new JSONArray();
                jsonAmountCollected = new JSONObject();
                jsonDummy = new JSONObject();
                jsonAttribute = new JSONObject();
                jsonProduct = new JSONObject();
                paramObject = new JSONObject();
                jsonProofFieldObj = new JSONObject();

                paramObject.put("agent_id", servicePelivery.getAgent_id());
                paramObject.put("shipment_no", servicePelivery.getShip_num());
                paramObject.put("order_no", servicePelivery.getOrder_num());
                paramObject.put("customer_name", servicePelivery.getCustomer_fname());
                paramObject.put("customer_phone", servicePelivery.getCustomer_cnum());
                paramObject.put("shipment_address", servicePelivery.getShip_address());
                paramObject.put("shipment_city", servicePelivery.getShip_city());
                paramObject.put("shipment_phone", servicePelivery.getShip_phone());
                paramObject.put("created_at", servicePelivery.getCreated_date());

                paramObject.put("attemptCount", service_attempt_count);


                Cursor getOrders = database.rawQuery("Select attribute_id,attribute_name,product_type,shipment_no,IFNULL(checked, 0) as checked ,input_field_type,is_require,attribute_type,IFNULL(text_content,null) as text_content from serviceProductAttributes where shipment_no = '" + serviceShipNo + "' ", null);
                JSONArray array = new JSONArray();

                if (getOrders.getCount() > 0) {
                    getOrders.moveToFirst();
                    while (!getOrders.isAfterLast()) {

                        JSONObject obj = new JSONObject();
                        JSONObject list1 = new JSONObject();

                        try {

                            list1.put("attribute_id", getOrders.getString(getOrders.getColumnIndex("attribute_id")));
                            list1.put("attribute_name", getOrders.getString(getOrders.getColumnIndex("attribute_name")));
//                                list1.put("product_type", getOrders.getString(getOrders.getColumnIndex("product_type")));
//                                list1.put("shipment_no", getOrders.getString(getOrders.getColumnIndex("shipment_no")));
                            list1.put("checked", getOrders.getString(getOrders.getColumnIndex("checked")));
                            list1.put("input_field_type", getOrders.getString(getOrders.getColumnIndex("input_field_type")));
                            list1.put("is_require", getOrders.getString(getOrders.getColumnIndex("is_require")));
                            list1.put("attribute_type", getOrders.getString(getOrders.getColumnIndex("attribute_type")));
                            list1.put("text_content", getOrders.getString(getOrders.getColumnIndex("text_content")));
                            array.put(list1);
                            jsonDummy.put("product_attribute", array);

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        getOrders.moveToNext();
                    }

                }
                getOrders.close();
                paramObject.put("fieldDataAttributes", jsonDummy);

                Cursor getAttributeOrders = database.rawQuery("Select feedback,feedback_status,feedback_id,shipment_no from serviceFeedbackItems where shipment_no = '" + serviceShipNo + "' ", null);
                JSONArray array1 = new JSONArray();

                if (getAttributeOrders.getCount() > 0) {
                    getAttributeOrders.moveToFirst();
                    while (!getAttributeOrders.isAfterLast()) {

                        JSONObject obj = new JSONObject();
                        JSONObject list1 = new JSONObject();

                        try {

                            list1.put("feedback", getAttributeOrders.getString(getAttributeOrders.getColumnIndex("feedback")));
                            list1.put("feedback_status", getAttributeOrders.getString(getAttributeOrders.getColumnIndex("feedback_status")));
                            list1.put("feedback_id", getAttributeOrders.getString(getAttributeOrders.getColumnIndex("feedback_id")));
//                            list1.put("shipment_no", getAttributeOrders.getString(getAttributeOrders.getColumnIndex("shipment_no")));

                            array1.put(list1);
                            jsonAttribute.put("feedback_attribute", array1);

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        getAttributeOrders.moveToNext();
                    }

                }
                getAttributeOrders.close();


                paramObject.put("fieldDataFeedback", jsonAttribute);

                Cursor getProducts = database.rawQuery("Select service_id,sku,name,qty,item_received,qty_demo_completed,order_item_id,product_serial_no,product_type,created_at,shipment_number from serviceItems where shipment_number = '" + serviceShipNo + "' ", null);
                JSONArray array2 = new JSONArray();

                if (getProducts.getCount() > 0) {
                    getProducts.moveToFirst();

                    while (!getProducts.isAfterLast()) {

                        JSONObject obj = new JSONObject();
                        JSONObject list1 = new JSONObject();

                        try {

                            list1.put("sku", getProducts.getString(getProducts.getColumnIndex("sku")));
                            list1.put("name", getProducts.getString(getProducts.getColumnIndex("name")));
                            list1.put("qty", getProducts.getString(getProducts.getColumnIndex("qty")));
                            list1.put("shipment_number", getProducts.getString(getProducts.getColumnIndex("shipment_number")));
                            list1.put("item_received", getProducts.getString(getProducts.getColumnIndex("item_received")));
                            array2.put(list1);
                            jsonProduct.put("item_code", array2);

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        getProducts.moveToNext();
                    }

                }
                getProducts.close();

                paramObject.put("fieldData", jsonProduct);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("fielddate", paramObject.toString());
//            System.out.print("fielddate"+paramObject.toString());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

            final Observable<ServiceResp> observable = apiService.pushServiceSync(requestBody)
                    .subscribeOn
                            (Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            observable.subscribe(new Observer<ServiceResp>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ServiceResp value) {

                    List<ServiceResp> orderVal = value.getServiceDelivery();
                    Log.v("getRes_msg", "- " + value.getRes_msg());
                    if (value.getRes_msg().equals("service upload success")) {

                        database.execSQL("UPDATE serviceMaster set sync_status = 'U', attempt = '" + service_attempt_count + "' where shipment_id ='" +
                                serviceShipNo + "' ");

                    } else if (value.getRes_msg().equals("service upload duplicate")) {
                        database.execSQL("UPDATE serviceMaster set sync_status = 'E' where shipment_id ='" +
                                serviceShipNo + "' ");

                    } else if (value.getRes_msg().equals("service upload failed")) {
                        database.execSQL("UPDATE serviceMaster set sync_status = 'E' where shipment_id ='" +
                                serviceShipNo + "' ");

                    }


                }

                @Override
                public void onError(Throwable e) {
                    Log.d("upload_response", "error" + e.toString());
//                    dialogLoading.dismiss();
                }

                @Override
                public void onComplete() {
//                    dialogLoading.dismiss();
                    Log.v("upload_response", "--" + "in here");
//                observable.unsubscribeOn(Schedulers.newThread());
                }


            });
        }
        customerName.close();
//        }
    }

    public void getServiceSignatureToSync() {

        final String new_file_path = String.valueOf(this.getFilesDir()) + "/ServiceSignApp/";

        File directory = new File(new_file_path);
        File[] files1;
        files1 = directory.listFiles();

        String asas = null;
        if (files1.length != 0) {
            for (int i = 0; i < 1; i++) {
                filePaths3.add(new File(new_file_path + "/" + files1[i].getName()));
                asas = new_file_path + "/" + files1[i].getName();
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getRequestHeader())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            InthreeApi apiService = retrofit.create(InthreeApi.class);

            File fileProofPhoto = new File(asas);
            RequestBody requestBodyDelivery = RequestBody.create(MediaType.parse("*/*"), fileProofPhoto);
            MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("sign_file", fileProofPhoto.getName(), requestBodyDelivery);

            final Observable<ImageSyncResp> observable = apiService.uploadMultiFile(fileToDelivery)
                    .subscribeOn
                            (Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            observable.subscribe(new Observer<ImageSyncResp>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ImageSyncResp value) {

                    Log.v("serviceConfirmation", " - " + value.getRes_sign());
                    Cursor getImageValue = database.rawQuery("select * from serviceMaster S INNER JOIN serviceConfirmation SC on SC.ship_num = S.shipment_id  where SC.signProof = '" + value.getRes_sign() + "' ", null);
                    getImageValue.moveToFirst();
                    if (getImageValue.getCount() > 0) {
                        String status = getImageValue.getString(getImageValue.getColumnIndex("sync_status"));
                        if (status.equals("U")) {
                            File deleteImgDeliverProof = new File(new_file_path + value.getRes_sign());
                            if (deleteImgDeliverProof.exists()) {
                                deleteImgDeliverProof.delete();
                            }
                        }

                    } else if (getImageValue.getCount() == 0) {
                        File deleteImgDeliverProof = new File(new_file_path + value.getRes_sign());
                        if (deleteImgDeliverProof.exists()) {
                            deleteImgDeliverProof.delete();
                        }
                    }
                    getImageValue.close();
                    filePaths3.clear();
                    getServiceSignatureToSync();

                }

                @Override
                public void onError(Throwable e) {
//                    Log.d("error", e.toString());

                }

                @Override
                public void onComplete() {
//                    Log.v("inhere", "--");

                }


            });


        }

    }


    /**
     * upload the pick up complete
     */
    public void uploadPickupComplete() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
        String currentDateTimeString = format.format(new Date());

//        if(updateComplete()){
        Cursor getPickupDetails = database.rawQuery("select  O.delivery_status,O.customer_name, O.order_number, " +
                "IFNULL(P.shipmentno,0) as shipmentno, IFNULL(P.customerphoto, 0) as customerphoto, IFNULL(P.latitude, 0) as latitude, IFNULL(P.longitude, 0) as longitude, " +
                "IFNULL(P.createdate, 0) as createdate,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O.attempt_count, 0) as attempt_count," +
                "IFNULL(P.customeraddress,0) as shipment_address, IFNULL(P.customerphone,0) as customerphone,IFNULL(P.reason, 0) as reason,IFNULL(P.reason_id, 0) as reason_id, IFNULL(P.pickupstatus, 0) as pickup_status,IFNULL(O.return_id, 0) as return_id, IFNULL(order_type, 0) as order_type from orderheader O INNER JOIN PickupConfirmation P on P" +
                ".shipmentno = O.Shipment_Number where O.sync_status = 'C' LIMIT 1 ", null);

        if (getPickupDetails.getCount() > 0) {
            getPickupDetails.moveToFirst();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            InthreeApi apiService = retrofit.create(InthreeApi.class);
            JSONObject paramObject = null;
            JSONObject jsonFieldObj;
            JSONObject jsonDummy;

            paramObject = new JSONObject();
            jsonFieldObj = new JSONObject();
            jsonDummy = new JSONObject();

            pick_attempt_count = getPickupDetails.getInt(getPickupDetails.getColumnIndex("attempt_count"));
            final String sn = getPickupDetails.getString(getPickupDetails.getColumnIndex("shipmentno"));
            String pickup_status = getPickupDetails.getString(getPickupDetails.getColumnIndex("pickup_status"));

            try {
                paramObject.put("runsheetNo", AppController.getStringPreference(Constants.USER_ID, ""));
                paramObject.put("referenceNumber", getPickupDetails.getString(getPickupDetails.getColumnIndex("referenceNumber")));
                paramObject.put("latitude", "0.0");
                paramObject.put("longitude", "0.0");
                paramObject.put("customer_name", getPickupDetails.getString(getPickupDetails.getColumnIndex("customer_name")));
//                paramObject.put("attemptCount", getPickupDetails.getString(getPickupDetails.getColumnIndex("attempt_count")));
                paramObject.put("attemptCount", pick_attempt_count);
                paramObject.put("employeeCode", AppController.getStringPreference(Constants.USER_ID, ""));
                paramObject.put("transactionDate", currentDateTimeString);
                paramObject.put("erpPushTime", currentDateTimeString);
                paramObject.put("lastTransactionTime", getPickupDetails.getString(getPickupDetails.getColumnIndex("createdate")));
                paramObject.put("return_id", getPickupDetails.getString(getPickupDetails.getColumnIndex("return_id")));
                paramObject.put("created_at", getPickupDetails.getString(getPickupDetails.getColumnIndex("createdate")));
                paramObject.put("battery", String.valueOf(battery_level));
                paramObject.put("deviceInfo", AppController.getdevice());
                paramObject.put("order_no", getPickupDetails.getString(getPickupDetails.getColumnIndex("order_number")));
                paramObject.put("shipmentnumber", getPickupDetails.getString(getPickupDetails.getColumnIndex("shipmentno")));
                paramObject.put("pickup_status", getPickupDetails.getString(getPickupDetails.getColumnIndex("pickup_status")));
                paramObject.put("order_type", getPickupDetails.getString(getPickupDetails.getColumnIndex("order_type")));

                jsonFieldObj.put("image", image_url + getPickupDetails.getString(getPickupDetails.getColumnIndex("customerphoto")));
                jsonFieldObj.put("address", getPickupDetails.getString(getPickupDetails.getColumnIndex("shipment_address")));
                jsonFieldObj.put("phone", getPickupDetails.getString(getPickupDetails.getColumnIndex("customerphone")));
                jsonFieldObj.put("reason", getPickupDetails.getString(getPickupDetails.getColumnIndex("reason")));
                if (!pickup_status.equals("Success")) {
                    jsonFieldObj.put("reason", getPickupDetails.getString(getPickupDetails.getColumnIndex("reason")));
                    jsonFieldObj.put("reason_id", getPickupDetails.getString(getPickupDetails.getColumnIndex("reason_id")));
                } else {
                    jsonFieldObj.put("reason", "");
                    jsonFieldObj.put("reason_id", 0);
                }

                Cursor getOrders = database.rawQuery("Select IFNULL(delivery_qty,0) as delivery_qty, IFNULL(product_code, 0) as product_code, IFNULL(product_name, '') as product_name," +
                        "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected, IFNULL(partial_reason, null) as partial_reason, IFNULL(r_id, null) as r_id   from ProductDetails where shipmentnumber = '" + sn + "'  ", null);
                JSONArray array = new JSONArray();

                if (getOrders.getCount() > 0) {
                    getOrders.moveToFirst();
                    ArrayList<String> list = new ArrayList<String>();
                    while (!getOrders.isAfterLast()) {

                        JSONObject obj = new JSONObject();
                        JSONObject list1 = new JSONObject();


                        try {

                            list1.put("sku_actual_quantity", getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
                            list1.put("product_code", getOrders.getString(getOrders.getColumnIndex("product_code")));
                            list1.put("product_name", getOrders.getString(getOrders.getColumnIndex("product_name")));
                            list1.put("quantity", getOrders.getString(getOrders.getColumnIndex("quantity")));
                            list1.put("amount", getOrders.getString(getOrders.getColumnIndex("amount_collected")));
                            list1.put("partial_reason", getOrders.getString(getOrders.getColumnIndex("partial_reason")));
                            list1.put("reason_id", getOrders.getString(getOrders.getColumnIndex("r_id")));
                            array.put(list1);
                            jsonDummy.put("item_code", array);

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        getOrders.moveToNext();
                    }

                }
                getOrders.close();

                paramObject.put("pickupProducts", jsonDummy);

                paramObject.put("fieldData", jsonFieldObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("pickup_param", paramObject.toString());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

            final Observable<UndeliveryResp> observable = apiService.getPickupSync(requestBody)
                    .subscribeOn
                            (Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            observable.subscribe(new Observer<UndeliveryResp>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(UndeliveryResp value) {
                    Log.v("undeliveredupload", "- " + value.getRes_msg() + "-- " + value.getRes_code());
//                        uploadImage();
                    if (value.getRes_msg().equalsIgnoreCase("pickup success") || value.getRes_msg().equalsIgnoreCase("already pickup")) {


                        database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + pick_attempt_count + " where Shipment_Number ='" +
                                sn + "' ");
                    } else if (value.getRes_msg().equalsIgnoreCase("pickup updated")) {

                        database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + pick_attempt_count + " where Shipment_Number ='" +
                                sn + "' ");
                    } else {
//                        Logger.showShortMessage(SyncService.this, getResources().getString(R.string.undeli_notsuccess_msg));

                    }


                }

                @Override
                public void onError(Throwable e) {
                    Log.d("uploadUndelivered", e.toString());

                }

                @Override
                public void onComplete() {
                    Log.v("inhere", "uploadUndelivered");
//                observable.unsubscribeOn(Schedulers.newThread());
                }


            });
        }
//        }
    }


    /*********************************************************This is for upload the bulk order in main activity each time 50 order will do****************************************************************/

    class MyAsyncTask extends AsyncTask<String, String, Result> {

        @Override
        protected void onPreExecute() {
            // Runs on the UI thread before doInBackground()
        }

        @Override
        protected Result doInBackground(String... params) {
            Log.v("myasynctask", "_ " + "doInBackground");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            final InthreeApi apiService = retrofit.create(InthreeApi.class);
            final OrderReq order = new OrderReq();
            JSONObject paramObject = null;
            order.setId(AppController.getStringPreference(Constants.USER_ID, ""));
//        Log.v("sync_service_uid", "-" + AppController.getStringPreference(Constants.USER_ID, ""));
//        Log.v("LOGIN_USER_ID",LOGIN_USER_ID);
            try {
                paramObject = new JSONObject();
                paramObject.put("runner_id", order.getId());
//            Log.v("paramObject", paramObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

            final Observable<OrderResp> observable = apiService.getOrders(requestBody).subscribeOn
                    (Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            observable.subscribe(new Observer<OrderResp>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(OrderResp value) {

//                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale("en", "US"));
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "US"));
                    Date date = null;

                    orderList = new ArrayList<>();
                    List<OrderResp> detailVal = value.getDetails();
                    Log.v("sync_service_orders", value.getResMsg());
                    if (value.getResMsg().equals("order success")) {
                        Log.v("sync_service_orders1", value.getResMsg());
                        for (int i = 0; i < detailVal.size(); i++) {
                            Cursor checkOrder = database.rawQuery("Select * from orderheader where Shipment_Number = '" +
                                            detailVal.get(i).getShipmentid() + "'",
                                    null);
                            if (checkOrder.getCount() == 0) {

//                            Log.v("OrderResp_service", "otp" + detailVal.get(i).getOtp() + "urn" + detailVal.get(i).getUrn() + detailVal.get(i).getOrderid());
                                try {
                                    date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String outputDateStr = outputFormat.format(date);

                                String customer_contact_no = detailVal.get(i).getCustomerContactNumber();
                                String contactnumber = detailVal.get(i).getCustomerContactNumber();
                         /*   String str = contactnumber;
                            int length = str.length();
                            if (length == 10) {
                                customer_contact_no = contactnumber;
                            } else if (length == 12) {
                                String phno = contactnumber.replace("91", "");
                                customer_contact_no = phno;
                            }*/

                                /* language parsing json starts*/
                                String tamil_val = null;
                                String hindi_val = null;
                                String marathi_val = null;
                                String punjabi_val = null;
                                String bengali_val = null;
                                String orissa_val = null;
                                String assam_val = null;
                                String telugu_val = null;
                                String kannada_val = null;
                                JSONObject tamilLangParamObject = null;
                                JSONObject hindiLangParamObject = null;
                                JSONObject marathiLangParamObject = null;
                                JSONObject bengaliLangParamObject = null;
                                JSONObject assamLangParamObject = null;
                                JSONObject orissaLangParamObject = null;
                                JSONObject punjabLangParamObject = null;
                                JSONObject teluguLangParamObject = null;
                                JSONObject kannadaLangParamObject = null;
                                String lang_json = detailVal.get(i).getLanguage_json();
                                if (lang_json != null) {
                                    try {
//                                    Log.v("getLanguage_json",lang_json);
                                        JSONObject obj = new JSONObject(lang_json);

                                        try {
                                            //Tamil JSON
                                            tamilLangParamObject = new JSONObject();

                                            JSONObject tamilOneObject = obj.getJSONObject("tamil");
                                            if (tamilOneObject.has("customer_name")) {
                                                String tamil_name = tamilOneObject.getString("customer_name");
                                                tamilLangParamObject.put("customer_name", tamil_name);
                                            }
                                            if (tamilOneObject.has("branch_name")) {
                                                String tamil_branch = tamilOneObject.getString("branch_name");
                                                tamilLangParamObject.put("branch_name", tamil_branch);
                                            }
                                            if (tamilOneObject.has("delivery_address")) {
                                                String tamil_branch_deliaddr = tamilOneObject.getString("delivery_address");
                                                tamilLangParamObject.put("delivery_address", tamil_branch_deliaddr);
                                            }
                                            if (tamilOneObject.has("branch_address")) {
                                                String tamil_branch_addr = tamilOneObject.getString("branch_address");
                                                tamilLangParamObject.put("branch_address", tamil_branch_addr);
                                            }
                                            if (tamilOneObject.has("city")) {
                                                String tamil_branch_city = tamilOneObject.getString("city");
                                                tamilLangParamObject.put("city", tamil_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        tamil_val = tamilLangParamObject.toString();
//                                    Log.v("tamilLangParamObject","-- "+tamil_val);
                                        String hi_val = null;
                                        try {
                                            //Hindi JSON
                                            hindiLangParamObject = new JSONObject();

                                            JSONObject hindiOneObject = obj.getJSONObject("hindi");
                                            if (hindiOneObject.has("customer_name")) {
                                                String hindi_name = hindiOneObject.getString("customer_name");
                                                hindiLangParamObject.put("customer_name", hindi_name);
                                            }
                                            if (hindiOneObject.has("branch_name")) {
                                                String hindi_branch = hindiOneObject.getString("branch_name");
                                                hindiLangParamObject.put("branch_name", hindi_branch);
                                            }
                                            if (hindiOneObject.has("delivery_address")) {
                                                String hindi_branch_deliaddr = hindiOneObject.getString("delivery_address");
                                                hindiLangParamObject.put("delivery_address", hindi_branch_deliaddr);
                                            }
                                            if (hindiOneObject.has("branch_address")) {
                                                String hindi_branch_addr = hindiOneObject.getString("branch_address");
                                                hindiLangParamObject.put("branch_address", hindi_branch_addr);
                                            }
                                            if (hindiOneObject.has("city")) {
                                                String hindi_branch_city = hindiOneObject.getString("city");
                                                hindiLangParamObject.put("city", hindi_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        hindi_val = hindiLangParamObject.toString();

                                        try {
                                            //Bengali JSON
                                            bengaliLangParamObject = new JSONObject();

                                            JSONObject bengaliOneObject = obj.getJSONObject("bengali");
                                            if (bengaliOneObject.has("customer_name")) {
                                                String bengali_name = bengaliOneObject.getString("customer_name");
                                                bengaliLangParamObject.put("customer_name", bengali_name);
                                            }
                                            if (bengaliOneObject.has("branch_name")) {
                                                String bengali_branch = bengaliOneObject.getString("branch_name");
                                                bengaliLangParamObject.put("branch_name", bengali_branch);
                                            }
                                            if (bengaliOneObject.has("delivery_address")) {
                                                String bengali_branch_deliaddr = bengaliOneObject.getString("delivery_address");
                                                bengaliLangParamObject.put("delivery_address", bengali_branch_deliaddr);
                                            }
                                            if (bengaliOneObject.has("branch_address")) {
                                                String bengali_branch_addr = bengaliOneObject.getString("branch_address");
                                                bengaliLangParamObject.put("branch_address", bengali_branch_addr);
                                            }
                                            if (bengaliOneObject.has("city")) {
                                                String bengali_branch_city = bengaliOneObject.getString("city");
                                                bengaliLangParamObject.put("city", bengali_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }

                                        bengali_val = bengaliLangParamObject.toString();

                                        try {
                                            //Marathi JSON
                                            marathiLangParamObject = new JSONObject();
                                            JSONObject marathiOneObject = obj.getJSONObject("marathi");
                                            if (marathiOneObject.has("customer_name")) {
                                                String marathi_name = marathiOneObject.getString("customer_name");
                                                marathiLangParamObject.put("customer_name", marathi_name);
                                            }
                                            if (marathiOneObject.has("branch_name")) {
                                                String marathi_branch = marathiOneObject.getString("branch_name");
                                                marathiLangParamObject.put("branch_name", marathi_branch);
                                            }
                                            if (marathiOneObject.has("delivery_address")) {
                                                String marathi_branch_deliaddr = marathiOneObject.getString("delivery_address");
                                                marathiLangParamObject.put("delivery_address", marathi_branch_deliaddr);
                                            }
                                            if (marathiOneObject.has("branch_address")) {
                                                String marathi_branch_addr = marathiOneObject.getString("branch_address");
                                                marathiLangParamObject.put("branch_address", marathi_branch_addr);
                                            }
                                            if (marathiOneObject.has("city")) {
                                                String marathi_branch_city = marathiOneObject.getString("city");
                                                marathiLangParamObject.put("city", marathi_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }

                                        marathi_val = marathiLangParamObject.toString();
                                        try {
                                            //Assam JSON
                                            assamLangParamObject = new JSONObject();
                                            JSONObject assamOneObject = obj.getJSONObject("assamese");
                                            if (assamOneObject.has("customer_name")) {
                                                String assam_name = assamOneObject.getString("customer_name");
                                                assamLangParamObject.put("customer_name", assam_name);
                                            }
                                            if (assamOneObject.has("branch_name")) {
                                                String assam_branch = assamOneObject.getString("branch_name");
                                                assamLangParamObject.put("branch_name", assam_branch);
                                            }
                                            if (assamOneObject.has("delivery_address")) {
                                                String assam_branch_deliaddr = assamOneObject.getString("delivery_address");
                                                assamLangParamObject.put("delivery_address", assam_branch_deliaddr);
                                            }
                                            if (assamOneObject.has("branch_address")) {
                                                String assam_branch_addr = assamOneObject.getString("branch_address");
                                                assamLangParamObject.put("branch_address", assam_branch_addr);
                                            }
                                            if (assamOneObject.has("city")) {
                                                String assam_branch_city = assamOneObject.getString("city");
                                                assamLangParamObject.put("city", assam_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        assam_val = assamLangParamObject.toString();

                                        try {
                                            //Orissa JSON
                                            orissaLangParamObject = new JSONObject();
                                            JSONObject orissaOneObject = obj.getJSONObject("odia");
                                            if (orissaOneObject.has("customer_name")) {
                                                String orissa_name = orissaOneObject.getString("customer_name");
                                                orissaLangParamObject.put("customer_name", orissa_name);
                                            }
                                            if (orissaOneObject.has("branch_name")) {
                                                String orissa_branch = orissaOneObject.getString("branch_name");
                                                orissaLangParamObject.put("branch_name", orissa_branch);
                                            }
                                            if (orissaOneObject.has("delivery_address")) {
                                                String orissa_branch_deliaddr = orissaOneObject.getString("delivery_address");
                                                orissaLangParamObject.put("delivery_address", orissa_branch_deliaddr);
                                            }
                                            if (orissaOneObject.has("branch_address")) {
                                                String orissa_branch_addr = orissaOneObject.getString("branch_address");
                                                orissaLangParamObject.put("branch_address", orissa_branch_addr);
                                            }
                                            if (orissaOneObject.has("city")) {
                                                String orissa_branch_city = orissaOneObject.getString("city");
                                                orissaLangParamObject.put("city", orissa_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        orissa_val = orissaLangParamObject.toString();


                                        try {
                                            //Telugu JSON
                                            teluguLangParamObject = new JSONObject();
                                            JSONObject teluguOneObject = obj.getJSONObject("telugu");
                                            if (teluguOneObject.has("customer_name")) {
                                                String telugu_name = teluguOneObject.getString("customer_name");
                                                teluguLangParamObject.put("customer_name", telugu_name);
                                            }
                                            if (teluguOneObject.has("branch_name")) {
                                                String telugu_branch = teluguOneObject.getString("branch_name");
                                                teluguLangParamObject.put("branch_name", telugu_branch);
                                            }
                                            if (teluguOneObject.has("delivery_address")) {
                                                String telugu_branch_deliaddr = teluguOneObject.getString("delivery_address");
                                                teluguLangParamObject.put("delivery_address", telugu_branch_deliaddr);
                                            }
                                            if (teluguOneObject.has("branch_address")) {
                                                String telugu_branch_addr = teluguOneObject.getString("branch_address");
                                                teluguLangParamObject.put("branch_address", telugu_branch_addr);
                                            }
                                            if (teluguOneObject.has("city")) {
                                                String telugu_branch_city = teluguOneObject.getString("city");
                                                teluguLangParamObject.put("city", telugu_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        telugu_val = teluguLangParamObject.toString();

                                        try {
                                            //Kannada JSON
                                            kannadaLangParamObject = new JSONObject();
                                            JSONObject kannadaOneObject = obj.getJSONObject("kannada");
                                            if (kannadaOneObject.has("customer_name")) {
                                                String kannada_name = kannadaOneObject.getString("customer_name");
                                                kannadaLangParamObject.put("customer_name", kannada_name);
                                            }
                                            if (kannadaOneObject.has("branch_name")) {
                                                String kannada_branch = kannadaOneObject.getString("branch_name");
                                                kannadaLangParamObject.put("branch_name", kannada_branch);
                                            }
                                            if (kannadaOneObject.has("delivery_address")) {
                                                String kannada_branch_deliaddr = kannadaOneObject.getString("delivery_address");
                                                kannadaLangParamObject.put("delivery_address", kannada_branch_deliaddr);
                                            }
                                            if (kannadaOneObject.has("branch_address")) {
                                                String kannada_branch_addr = kannadaOneObject.getString("branch_address");
                                                kannadaLangParamObject.put("branch_address", kannada_branch_addr);
                                            }
                                            if (kannadaOneObject.has("city")) {
                                                String kannada_branch_city = kannadaOneObject.getString("city");
                                                kannadaLangParamObject.put("city", kannada_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        kannada_val = kannadaLangParamObject.toString();

                                        try {
                                            //Punjabi JSON
                                            JSONObject punjabOneObject = obj.getJSONObject("punjabi");
                                            punjabLangParamObject = new JSONObject();
                                            if (punjabLangParamObject.has("customer_name")) {
                                                String punjab_name = punjabOneObject.getString("customer_name");
                                                punjabLangParamObject.put("customer_name", punjab_name);
                                            }
                                            if (punjabLangParamObject.has("branch_name")) {
                                                String punjab_branch = punjabOneObject.getString("branch_name");
                                                punjabLangParamObject.put("branch_name", punjab_branch);
                                            }
                                            if (punjabLangParamObject.has("delivery_address")) {
                                                String punjab_branch_deliaddr = punjabOneObject.getString("delivery_address");
                                                punjabLangParamObject.put("delivery_address", punjab_branch_deliaddr);
                                            }
                                            if (punjabLangParamObject.has("branch_address")) {
                                                String punjab_branch_addr = punjabOneObject.getString("branch_address");
                                                punjabLangParamObject.put("branch_address", punjab_branch_addr);
                                            }
                                            if (punjabLangParamObject.has("city")) {
                                                String punjab_branch_city = punjabOneObject.getString("city");
                                                punjabLangParamObject.put("city", punjab_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        punjabi_val = punjabLangParamObject.toString();

                                    } catch (Throwable t) {
                                        Log.e("lang_json", "Could not parse malformed JSON: \"" + bengali_val + "\"");
                                    }
                                    /* language parsing json ends*/
                                }

                                String insertOrderHeader = "Insert into orderheader(loanrefno,invoice_date,invoice_id,delivery_to,branch_code,order_number,customer_name," +
                                        "customer_contact_number,alternate_contact_number,to_be_delivered_by,billing_address,billing_city,billing_pincode," +
                                        "billing_telephone,shipping_address,shipping_city,shipping_pincode, shipping_telephone,invoice_amount,payment_mode," +
                                        "client_branch_name,branch_address,branch_pincode,branch_contact_number,group_leader_name,group_leader_contact_number," +

                                        "slot_number,referenceNumber,processDefinitionCode,Shipment_Number,sync_status,delivery_status, valid, attempt_count,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi,otp,urn,order_type,max_attempt,delivery_aadhar_required,virtual_id) " +


                                        "Values('" + detailVal.get(i).getLoanrefno() + "','" + detailVal.get(i).getInvoiceDate() + "','" + detailVal.get(i).getInvoiceNumber() + "','" + detailVal.get(i).getDeliveryTo() + "','" + detailVal.get(i).getClientBranchCode() + "','" + detailVal.get(i).getOrderid() + "', '" + detailVal.get(i).getCustomerName() + "', '" + customer_contact_no + "'," +
                                        "'" + detailVal.get(i).getAlternateContactNumber() + "', '" + outputDateStr + "', " +
                                        "'" + detailVal.get(i).getBillingAddress() + "', '" + detailVal.get(i).getBillingCity() + "', '" + detailVal.get(i).getBillingPincode() + "', '" + detailVal.get(i).getBillingTelephone() + "', '" + detailVal.get(i).getShippingAddress() + "', '" + detailVal.get(i).getShippingCity() + "'," +
                                        " '" + detailVal.get(i).getShippingPincode() + "', '" + detailVal.get(i).getShippingTelephone() + "', '" + detailVal.get(i).getAmount() + "', '" + detailVal.get(i).getPaymentMode() + "', '" + detailVal.get(i).getClient_branch_name() + "', '" + detailVal.get(i).getBranch_address() + "', '" + detailVal.get(i).getBranch_pincode() + "'" +
                                        ", '" + detailVal.get(i).getBranch_contact_number() + "', '" + detailVal.get(i).getGroup_leader_name() + "', '" + detailVal.get(i).getGroup_leader_contact_number() + "', '" + detailVal.get(i).getSlot_number() + "', '" + detailVal.get(i).getReference() + "', '', '" + detailVal.get(i).getShipmentid() + "', 'P', '', '" + detailVal.get(i).getDownloadSync() + "', " + detailVal.get(i).getAttempt() + ",'" + tamil_val + "','" + telugu_val + "','" + punjabi_val + "','" + hindi_val + "','" + bengali_val + "','" + kannada_val + "','" + assam_val + "','" + orissa_val + "','" + marathi_val + "','" + detailVal.get(i).getOtp() + "','" + detailVal.get(i).getUrn() + "','" + detailVal.get(i).getOrder_type() + "','" + detailVal.get(i).getMax_attempt() + "'," + detailVal.get(i).getDelivery_aadhar_required() + ",'" + value.getVirtual_id() + "')";


                                database.execSQL(insertOrderHeader);

                            } else {
                                Log.v("OrderResp1_service", "otp" + detailVal.get(i).getOtp() + "urn" + detailVal.get(i).getUrn() + detailVal.get(i).getOrderid());
                                try {
                                    date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String outputDateStr = outputFormat.format(date);

                                String customer_contact_no = detailVal.get(i).getCustomerContactNumber();
                                String contactnumber = detailVal.get(i).getCustomerContactNumber();
                         /*   String str = contactnumber;
                            int length = str.length();
                            if (length == 10) {
                                customer_contact_no = contactnumber;
                            } else if (length == 12) {
                                String phno = contactnumber.replace("91", "");
                                customer_contact_no = phno;
                            }*/

                                String tamil_val = null;
                                String hindi_val = null;
                                String marathi_val = null;
                                String punjabi_val = null;
                                String bengali_val = null;
                                String orissa_val = null;
                                String assam_val = null;
                                String telugu_val = null;
                                String kannada_val = null;
                                JSONObject tamilLangParamObject = null;
                                JSONObject hindiLangParamObject = null;
                                JSONObject marathiLangParamObject = null;
                                JSONObject bengaliLangParamObject = null;
                                JSONObject assamLangParamObject = null;
                                JSONObject orissaLangParamObject = null;
                                JSONObject punjabLangParamObject = null;
                                JSONObject teluguLangParamObject = null;
                                JSONObject kannadaLangParamObject = null;
                                String lang_json = detailVal.get(i).getLanguage_json();
                                if (lang_json != null) {
//                                Log.v("check_if_null", "- " + lang_json);
                                    try {
//                                    Log.v("getLanguage_json",lang_json);
                                        JSONObject obj = new JSONObject(lang_json);

                                        try {
                                            //Tamil JSON
                                            tamilLangParamObject = new JSONObject();

                                            JSONObject tamilOneObject = obj.getJSONObject("tamil");
                                            if (tamilOneObject.has("customer_name")) {
                                                String tamil_name = tamilOneObject.getString("customer_name");
                                                tamilLangParamObject.put("customer_name", tamil_name);
                                            }
                                            if (tamilOneObject.has("branch_name")) {
                                                String tamil_branch = tamilOneObject.getString("branch_name");
                                                tamilLangParamObject.put("branch_name", tamil_branch);
                                            }
                                            if (tamilOneObject.has("delivery_address")) {
                                                String tamil_branch_deliaddr = tamilOneObject.getString("delivery_address");
                                                tamilLangParamObject.put("delivery_address", tamil_branch_deliaddr);
                                            }
                                            if (tamilOneObject.has("branch_address")) {
                                                String tamil_branch_addr = tamilOneObject.getString("branch_address");
                                                tamilLangParamObject.put("branch_address", tamil_branch_addr);
                                            }
                                            if (tamilOneObject.has("city")) {
                                                String tamil_branch_city = tamilOneObject.getString("city");
                                                tamilLangParamObject.put("city", tamil_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        tamil_val = tamilLangParamObject.toString();
//                                    Log.v("tamilLangParamObject","-- "+tamil_val);
                                        String hi_val = null;
                                        try {
                                            //Hindi JSON
                                            hindiLangParamObject = new JSONObject();

                                            JSONObject hindiOneObject = obj.getJSONObject("hindi");
                                            if (hindiOneObject.has("customer_name")) {
                                                String hindi_name = hindiOneObject.getString("customer_name");
                                                hindiLangParamObject.put("customer_name", hindi_name);
                                            }
                                            if (hindiOneObject.has("branch_name")) {
                                                String hindi_branch = hindiOneObject.getString("branch_name");
                                                hindiLangParamObject.put("branch_name", hindi_branch);
                                            }
                                            if (hindiOneObject.has("delivery_address")) {
                                                String hindi_branch_deliaddr = hindiOneObject.getString("delivery_address");
                                                hindiLangParamObject.put("delivery_address", hindi_branch_deliaddr);
                                            }
                                            if (hindiOneObject.has("branch_address")) {
                                                String hindi_branch_addr = hindiOneObject.getString("branch_address");
                                                hindiLangParamObject.put("branch_address", hindi_branch_addr);
                                            }
                                            if (hindiOneObject.has("city")) {
                                                String hindi_branch_city = hindiOneObject.getString("city");
                                                hindiLangParamObject.put("city", hindi_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        hindi_val = hindiLangParamObject.toString();

                                        try {
                                            //Bengali JSON
                                            bengaliLangParamObject = new JSONObject();

                                            JSONObject bengaliOneObject = obj.getJSONObject("bengali");
                                            if (bengaliOneObject.has("customer_name")) {
                                                String bengali_name = bengaliOneObject.getString("customer_name");
                                                bengaliLangParamObject.put("customer_name", bengali_name);
                                            }
                                            if (bengaliOneObject.has("branch_name")) {
                                                String bengali_branch = bengaliOneObject.getString("branch_name");
                                                bengaliLangParamObject.put("branch_name", bengali_branch);
                                            }
                                            if (bengaliOneObject.has("delivery_address")) {
                                                String bengali_branch_deliaddr = bengaliOneObject.getString("delivery_address");
                                                bengaliLangParamObject.put("delivery_address", bengali_branch_deliaddr);
                                            }
                                            if (bengaliOneObject.has("branch_address")) {
                                                String bengali_branch_addr = bengaliOneObject.getString("branch_address");
                                                bengaliLangParamObject.put("branch_address", bengali_branch_addr);
                                            }
                                            if (bengaliOneObject.has("city")) {
                                                String bengali_branch_city = bengaliOneObject.getString("city");
                                                bengaliLangParamObject.put("city", bengali_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }

                                        bengali_val = bengaliLangParamObject.toString();

                                        try {
                                            //Marathi JSON
                                            marathiLangParamObject = new JSONObject();
                                            JSONObject marathiOneObject = obj.getJSONObject("marathi");
                                            if (marathiOneObject.has("customer_name")) {
                                                String marathi_name = marathiOneObject.getString("customer_name");
                                                marathiLangParamObject.put("customer_name", marathi_name);
                                            }
                                            if (marathiOneObject.has("branch_name")) {
                                                String marathi_branch = marathiOneObject.getString("branch_name");
                                                marathiLangParamObject.put("branch_name", marathi_branch);
                                            }
                                            if (marathiOneObject.has("delivery_address")) {
                                                String marathi_branch_deliaddr = marathiOneObject.getString("delivery_address");
                                                marathiLangParamObject.put("delivery_address", marathi_branch_deliaddr);
                                            }
                                            if (marathiOneObject.has("branch_address")) {
                                                String marathi_branch_addr = marathiOneObject.getString("branch_address");
                                                marathiLangParamObject.put("branch_address", marathi_branch_addr);
                                            }
                                            if (marathiOneObject.has("city")) {
                                                String marathi_branch_city = marathiOneObject.getString("city");
                                                marathiLangParamObject.put("city", marathi_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }

                                        marathi_val = marathiLangParamObject.toString();
                                        try {
                                            //Assam JSON
                                            assamLangParamObject = new JSONObject();
                                            JSONObject assamOneObject = obj.getJSONObject("assamese");
                                            if (assamOneObject.has("customer_name")) {
                                                String assam_name = assamOneObject.getString("customer_name");
                                                assamLangParamObject.put("customer_name", assam_name);
                                            }
                                            if (assamOneObject.has("branch_name")) {
                                                String assam_branch = assamOneObject.getString("branch_name");
                                                assamLangParamObject.put("branch_name", assam_branch);
                                            }
                                            if (assamOneObject.has("delivery_address")) {
                                                String assam_branch_deliaddr = assamOneObject.getString("delivery_address");
                                                assamLangParamObject.put("delivery_address", assam_branch_deliaddr);
                                            }
                                            if (assamOneObject.has("branch_address")) {
                                                String assam_branch_addr = assamOneObject.getString("branch_address");
                                                assamLangParamObject.put("branch_address", assam_branch_addr);
                                            }
                                            if (assamOneObject.has("city")) {
                                                String assam_branch_city = assamOneObject.getString("city");
                                                assamLangParamObject.put("city", assam_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        assam_val = assamLangParamObject.toString();

                                        try {
                                            //Orissa JSON
                                            orissaLangParamObject = new JSONObject();
                                            JSONObject orissaOneObject = obj.getJSONObject("odia");
                                            if (orissaOneObject.has("customer_name")) {
                                                String orissa_name = orissaOneObject.getString("customer_name");
                                                orissaLangParamObject.put("customer_name", orissa_name);
                                            }
                                            if (orissaOneObject.has("branch_name")) {
                                                String orissa_branch = orissaOneObject.getString("branch_name");
                                                orissaLangParamObject.put("branch_name", orissa_branch);
                                            }
                                            if (orissaOneObject.has("delivery_address")) {
                                                String orissa_branch_deliaddr = orissaOneObject.getString("delivery_address");
                                                orissaLangParamObject.put("delivery_address", orissa_branch_deliaddr);
                                            }
                                            if (orissaOneObject.has("branch_address")) {
                                                String orissa_branch_addr = orissaOneObject.getString("branch_address");
                                                orissaLangParamObject.put("branch_address", orissa_branch_addr);
                                            }
                                            if (orissaOneObject.has("city")) {
                                                String orissa_branch_city = orissaOneObject.getString("city");
                                                orissaLangParamObject.put("city", orissa_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        orissa_val = orissaLangParamObject.toString();


                                        try {
                                            //Telugu JSON
                                            teluguLangParamObject = new JSONObject();
                                            JSONObject teluguOneObject = obj.getJSONObject("telugu");
                                            if (teluguOneObject.has("customer_name")) {
                                                String telugu_name = teluguOneObject.getString("customer_name");
                                                teluguLangParamObject.put("customer_name", telugu_name);
                                            }
                                            if (teluguOneObject.has("branch_name")) {
                                                String telugu_branch = teluguOneObject.getString("branch_name");
                                                teluguLangParamObject.put("branch_name", telugu_branch);
                                            }
                                            if (teluguOneObject.has("delivery_address")) {
                                                String telugu_branch_deliaddr = teluguOneObject.getString("delivery_address");
                                                teluguLangParamObject.put("delivery_address", telugu_branch_deliaddr);
                                            }
                                            if (teluguOneObject.has("branch_address")) {
                                                String telugu_branch_addr = teluguOneObject.getString("branch_address");
                                                teluguLangParamObject.put("branch_address", telugu_branch_addr);
                                            }
                                            if (teluguOneObject.has("city")) {
                                                String telugu_branch_city = teluguOneObject.getString("city");
                                                teluguLangParamObject.put("city", telugu_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        telugu_val = teluguLangParamObject.toString();

                                        try {
                                            //Kannada JSON
                                            kannadaLangParamObject = new JSONObject();
                                            JSONObject kannadaOneObject = obj.getJSONObject("kannada");
                                            if (kannadaOneObject.has("customer_name")) {
                                                String kannada_name = kannadaOneObject.getString("customer_name");
                                                kannadaLangParamObject.put("customer_name", kannada_name);
                                            }
                                            if (kannadaOneObject.has("branch_name")) {
                                                String kannada_branch = kannadaOneObject.getString("branch_name");
                                                kannadaLangParamObject.put("branch_name", kannada_branch);
                                            }
                                            if (kannadaOneObject.has("delivery_address")) {
                                                String kannada_branch_deliaddr = kannadaOneObject.getString("delivery_address");
                                                kannadaLangParamObject.put("delivery_address", kannada_branch_deliaddr);
                                            }
                                            if (kannadaOneObject.has("branch_address")) {
                                                String kannada_branch_addr = kannadaOneObject.getString("branch_address");
                                                kannadaLangParamObject.put("branch_address", kannada_branch_addr);
                                            }
                                            if (kannadaOneObject.has("city")) {
                                                String kannada_branch_city = kannadaOneObject.getString("city");
                                                kannadaLangParamObject.put("city", kannada_branch_city);
                                            }

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        kannada_val = kannadaLangParamObject.toString();

                                        try {
                                            //Punjabi JSON
                                            JSONObject punjabOneObject = obj.getJSONObject("punjabi");
                                            punjabLangParamObject = new JSONObject();
                                            if (punjabLangParamObject.has("customer_name")) {
                                                String punjab_name = punjabOneObject.getString("customer_name");
                                                punjabLangParamObject.put("customer_name", punjab_name);
                                            }
                                            if (punjabLangParamObject.has("branch_name")) {
                                                String punjab_branch = punjabOneObject.getString("branch_name");
                                                punjabLangParamObject.put("branch_name", punjab_branch);
                                            }
                                            if (punjabLangParamObject.has("delivery_address")) {
                                                String punjab_branch_deliaddr = punjabOneObject.getString("delivery_address");
                                                punjabLangParamObject.put("delivery_address", punjab_branch_deliaddr);
                                            }
                                            if (punjabLangParamObject.has("branch_address")) {
                                                String punjab_branch_addr = punjabOneObject.getString("branch_address");
                                                punjabLangParamObject.put("branch_address", punjab_branch_addr);
                                            }
                                            if (punjabLangParamObject.has("city")) {
                                                String punjab_branch_city = punjabOneObject.getString("city");
                                                punjabLangParamObject.put("city", punjab_branch_city);
                                            }


                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        punjabi_val = punjabLangParamObject.toString();

                                    } catch (Throwable t) {
                                        Log.e("lang_json", "Could not parse malformed JSON: \"" + bengali_val + "\"");
                                    }
                                }
                                String queryupdate = "UPDATE orderheader set order_number = '" + detailVal.get(i).getOrderid() +
                                        "',customer_name='" + detailVal.get(i).getCustomerName() + "',customer_contact_number = '" + customer_contact_no + "'," +
                                        "alternate_contact_number = '" + detailVal.get(i).getAlternateContactNumber() + "'," +
                                        "to_be_delivered_by = '" + outputDateStr + "'," +
                                        "billing_address= '" + detailVal.get(i).getBillingAddress() + "'," +
                                        "billing_city= '" + detailVal.get(i).getBillingCity() + "'," +
                                        "billing_pincode= '" + detailVal.get(i).getBillingPincode() + "'," +
                                        "billing_telephone= '" + detailVal.get(i).getBillingTelephone() + "'," +
                                        "shipping_address= '" + detailVal.get(i).getShippingAddress() + "'," +
                                        "shipping_city= '" + detailVal.get(i).getShippingCity() + "'," +
                                        "shipping_pincode= '" + detailVal.get(i).getShippingPincode() + "', " +
                                        "shipping_telephone= '" + detailVal.get(i).getShippingTelephone() + "'," +
                                        "invoice_amount= '" + detailVal.get(i).getAmount() + "'," +
                                        "payment_mode= '" + detailVal.get(i).getPaymentMode() + "'," +
                                        "client_branch_name= '" + detailVal.get(i).getClient_branch_name() + "'," +
                                        "branch_address= '" + detailVal.get(i).getBranch_address() + "'," +
                                        "branch_pincode= '" + detailVal.get(i).getBranch_pincode() + "'," +
                                        "branch_contact_number= '" + detailVal.get(i).getBranch_contact_number() + "'," +
                                        "group_leader_name= '" + detailVal.get(i).getGroup_leader_name() + "'," +
                                        "group_leader_contact_number= '" + detailVal.get(i).getGroup_leader_contact_number() + "'," +
                                        "slot_number= '" + detailVal.get(i).getSlot_number() + "'," +
                                        "referenceNumber= '" + detailVal.get(i).getReference() + "'," +
                                        "otp= '" + detailVal.get(i).getOtp() + "'," +
                                        "urn= '" + detailVal.get(i).getUrn() + "'," +

                                        "order_type= '" + detailVal.get(i).getOrder_type() + "'," +
                                        "max_attempt= '" + detailVal.get(i).getMax_attempt() + "'," +

                                        "delivery_aadhar_required = '" + detailVal.get(i).getDelivery_aadhar_required() + "'," +

                                        "processDefinitionCode= ''," +
                                        "Shipment_Number= '" + detailVal.get(i).getShipmentid() + "'," +
                                        "attempt_count = '" + detailVal.get(i).getAttempt() + "'," +
                                        "tamil = '" + tamil_val + "'," +
                                        "hindi = '" + hindi_val + "'," +
                                        "assam = '" + assam_val + "'," +
                                        "punjabi = '" + punjabi_val + "'," +
                                        "marathi = '" + marathi_val + "'," +
                                        "telugu = '" + telugu_val + "'," +
                                        "kannada = '" + kannada_val + "'," +
                                        "orissa = '" + orissa_val + "'," +
                                        "virtual_id = '" + value.getVirtual_id() + "'," +
                                        "bengali = '" + bengali_val + "' where " +  //sync_status= 'P'
                                        "Shipment_Number ='" + detailVal.get(i).getShipmentid() + "' ";  //AND sync_status = 'P'
                                database.execSQL(queryupdate);
                            }

                            String product_name = "";
                            for (int j = 0; j < detailVal.get(i).getOrder().size(); j++) {

                                String tamil_pval = null;
                                String hindi_pval = null;
                                String marathi_pval = null;
                                String punjabi_pval = null;
                                String bengali_pval = null;
                                String orissa_pval = null;
                                String assam_pval = null;
                                String telugu_pval = null;
                                String kannada_pval = null;
                                JSONObject tamilItemLangParamObject = null;
                                JSONObject hindiItemLangParamObject = null;
                                JSONObject marathiItemLangParamObject = null;
                                JSONObject bengaliItemLangParamObject = null;
                                JSONObject assamItemLangParamObject = null;
                                JSONObject orissaItemLangParamObject = null;
                                JSONObject punjabItemLangParamObject = null;
                                JSONObject teluguItemLangParamObject = null;
                                JSONObject kannadaItemLangParamObject = null;
                                String item_json;
                                item_json = detailVal.get(i).getOrder().get(j).getItem_json();
                                if (item_json != null && !item_json.equals("null")) {
                                    Log.v("check_if_item_json", "- " + item_json);

                                    try {

                                        JSONObject obj = new JSONObject(item_json);

                                        try {
                                            //Tamil JSON
                                            JSONObject tamilOneObject = obj.getJSONObject("tamil");
                                            String tamil_pname = tamilOneObject.getString("product_name");

                                            tamilItemLangParamObject = new JSONObject();
                                            tamilItemLangParamObject.put("product_name", tamil_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        tamil_pval = tamilItemLangParamObject.toString();
                                        try {
                                            //Hindi JSON
                                            JSONObject hindiOneObject = obj.getJSONObject("hindi");
                                            String hindi_pname = hindiOneObject.getString("product_name");

                                            hindiItemLangParamObject = new JSONObject();
                                            hindiItemLangParamObject.put("product_name", hindi_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        hindi_pval = hindiItemLangParamObject.toString();

                                        try {
                                            //Bengali JSON
                                            JSONObject bengaliOneObject = obj.getJSONObject("bengali");
                                            String bengali_pname = bengaliOneObject.getString("product_name");

                                            bengaliItemLangParamObject = new JSONObject();
                                            bengaliItemLangParamObject.put("product_name", bengali_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }

                                        bengali_pval = bengaliItemLangParamObject.toString();

                                        try {
                                            //Marathi JSON
                                            JSONObject marathiOneObject = obj.getJSONObject("marathi");
                                            String marathi_pname = marathiOneObject.getString("product_name");

                                            marathiItemLangParamObject = new JSONObject();
                                            marathiItemLangParamObject.put("product_name", marathi_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }

                                        marathi_pval = marathiItemLangParamObject.toString();
                                        try {
                                            //Assam JSON
                                            JSONObject assamOneObject = obj.getJSONObject("assamese");
                                            String assam_pname = assamOneObject.getString("product_name");

                                            assamItemLangParamObject = new JSONObject();
                                            assamItemLangParamObject.put("product_name", assam_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        assam_pval = assamItemLangParamObject.toString();

                                        try {
                                            //Orissa JSON
                                            JSONObject orissaOneObject = obj.getJSONObject("odia");
                                            String orissa_pname = orissaOneObject.getString("product_name");

                                            orissaItemLangParamObject = new JSONObject();
                                            orissaItemLangParamObject.put("product_name", orissa_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        orissa_pval = orissaItemLangParamObject.toString();

                                        try {
                                            //Punjabi JSON
                                            JSONObject punjabOneObject = obj.getJSONObject("punjabi");
                                            String punjab_pname = punjabOneObject.getString("product_name");

                                            punjabItemLangParamObject = new JSONObject();
                                            punjabItemLangParamObject.put("product_name", punjab_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        punjabi_pval = punjabItemLangParamObject.toString();

                                        try {
                                            //Telugu JSON
                                            JSONObject teluguOneObject = obj.getJSONObject("telugu");
                                            String telugu_pname = teluguOneObject.getString("product_name");

                                            teluguItemLangParamObject = new JSONObject();
                                            teluguItemLangParamObject.put("product_name", telugu_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        telugu_pval = teluguItemLangParamObject.toString();

                                        try {
                                            //Kannada JSON
                                            JSONObject kannadaOneObject = obj.getJSONObject("kannada");
                                            String kannada_pname = kannadaOneObject.getString("product_name");

                                            kannadaItemLangParamObject = new JSONObject();
                                            kannadaItemLangParamObject.put("product_name", kannada_pname);

                                        } catch (JSONException e) {
                                            e.getStackTrace();
                                        }
                                        kannada_pval = kannadaItemLangParamObject.toString();


                                    } catch (Throwable t) {
                                        Log.e("new_item_json", "Could not parse malformed JSON: \"" + bengali_pval + "\"");
                                    }
                                }

                                if (!detailVal.get(i).getOrder_type().equals("2")) {
                                    Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                                    detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' AND pickup_type = 0",
                                            null);
                                    if (checkProducts.getCount() == 0) {

                                        product_name = detailVal.get(i).getOrder().get(j).getName();
                                        String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                        String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi)" +
                                                " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                                + "', '" + detailVal.get(i).getOrder().get(j).getQty() + "', '" + detailVal.get(i).getOrder().get(j).getPrice() + "', '" +
                                                detailVal.get(i).getOrder().get(j).getSku() + "','" + tamil_pval + "','" + telugu_pval + "','" + punjabi_pval + "','" + hindi_pval + "','" + bengali_pval + "','" + kannada_pval + "','" + assam_pval + "','" + orissa_pval + "','" + marathi_pval + "')";
                                        database.execSQL(insertProduct);
                                    } else {
//                                Log.v("prod_name1", detailVal.get(i).getOrder().get(j).getQty() + "--" + detailVal.get(i).getShipmentid());
                                        product_name = detailVal.get(i).getOrder().get(j).getName();
                                        String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                        String updateProducts = "UPDATE ProductDetails set product_name = '" + name_split + "', amount = '" + detailVal.get(i).getOrder().get(j).getPrice() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getQty() + "',tamil = '" + tamil_pval + "',telugu = '" + telugu_pval + "'," +
                                                "punjabi = '" + punjabi_pval + "',hindi = '" + hindi_pval + "', bengali = '" + bengali_pval + "', kannada = '" + kannada_pval + "',assam = '" + assam_pval + "', orissa = '" + orissa_pval + "'," +
                                                "marathi = '" + marathi_pval + "'" +
                                                " where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' ";
                                        database.execSQL(updateProducts);
                                    }
                                    checkProducts.close();
                                } else {

                                    Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                                    detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' AND pickup_type = 1",
                                            null);
                                    if (checkProducts.getCount() == 0) {

                                        product_name = detailVal.get(i).getOrder().get(j).getName();
                                        String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                        String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi,pickup_type)" +
                                                " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                                + "', '" + detailVal.get(i).getOrder().get(j).getQty() + "', '" + detailVal.get(i).getOrder().get(j).getPrice() + "', '" +
                                                detailVal.get(i).getOrder().get(j).getSku() + "','" + tamil_pval + "','" + telugu_pval + "','" + punjabi_pval + "','" + hindi_pval + "','" + bengali_pval + "','" + kannada_pval + "','" + assam_pval + "','" + orissa_pval + "','" + marathi_pval + "',1)";
                                        database.execSQL(insertProduct);
                                    } else {
//                                Log.v("prod_name1", detailVal.get(i).getOrder().get(j).getQty() + "--" + detailVal.get(i).getShipmentid());
                                        product_name = detailVal.get(i).getOrder().get(j).getName();
                                        String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                        String updateProducts = "UPDATE ProductDetails set product_name = '" + name_split + "', amount = '" + detailVal.get(i).getOrder().get(j).getPrice() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getQty() + "',tamil = '" + tamil_pval + "',telugu = '" + telugu_pval + "'," +
                                                "punjabi = '" + punjabi_pval + "',hindi = '" + hindi_pval + "', bengali = '" + bengali_pval + "', kannada = '" + kannada_pval + "',assam = '" + assam_pval + "', orissa = '" + orissa_pval + "'," +
                                                "marathi = '" + marathi_pval + "' , pickup_type = 1 " +
                                                " where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' ";
                                        database.execSQL(updateProducts);
                                    }
                                    checkProducts.close();

                                }

                           /* Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                            detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' AND pickup_type = 0",
                                    null);
                            try {
                                if (checkProducts.getCount() == 0) {
                                    product_name = detailVal.get(i).getOrder().get(j).getName();
                                    String name_split = product_name.replace("'", "");

                                    String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi)" +
                                            " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                            + "', '" + detailVal.get(i).getOrder().get(j).getQty() + "', '" + detailVal.get(i).getOrder().get(j).getPrice() + "', '" +
                                            detailVal.get(i).getOrder().get(j).getSku() + "','"+tamil_pval+"','"+telugu_pval+"','"+punjabi_pval+"','"+hindi_pval+"','"+bengali_pval+"','"+kannada_pval+"','"+assam_pval+"','"+orissa_pval+"','"+marathi_pval+"')";
                                    database.execSQL(insertProduct);
                                } else {

                                    product_name = detailVal.get(i).getOrder().get(j).getName();
                                    String name_split = product_name.replace("'", "");

                                    String updateProducts = "UPDATE ProductDetails set product_name = '" + name_split + "',amount = '" + detailVal.get(i).getOrder().get(j).getPrice() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getQty() + "',tamil = '"+tamil_pval+"',telugu = '"+telugu_pval+"'," +
                                            "punjabi = '"+punjabi_pval+"',hindi = '"+hindi_pval+"', bengali = '"+bengali_pval+"', kannada = '"+kannada_pval+"',assam = '"+assam_pval+"', orissa = '"+orissa_pval+"'," +
                                            "marathi = '"+marathi_pval+"'" +
                                            " where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' ";
                                    database.execSQL(updateProducts);

                                }
                            } finally {
                                checkProducts.close();
                            }*/

                                if (!detailVal.get(i).getOrder().get(j).getP_sku().equals("")) {

                                    Cursor checkPickupProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                                    detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getP_sku() + "' AND pickup_type = 1 ",
                                            null);
                                    if (checkPickupProducts.getCount() == 0) {
                                        Log.v("getPickupProds", " - " + detailVal.get(i).getOrder().get(j).getP_sku());
//                                    Log.v("getPickupProds", " - "+ checkPickupProducts.getString(checkPickupProducts.getColumnIndex("pickup_type")) );
                                        product_name = detailVal.get(i).getOrder().get(j).getP_skuname();
                                        String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                        String insertPickupProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,pickup_type)" +
                                                " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                                + "', '" + detailVal.get(i).getOrder().get(j).getP_skuqty() + "', '" + detailVal.get(i).getOrder().get(j).getP_price() + "', '" +
                                                detailVal.get(i).getOrder().get(j).getP_sku() + "', 1)";
                                        database.execSQL(insertPickupProduct);
                                    } else {
                                        Log.v("getPickupProds1", " - " + detailVal.get(i).getOrder().get(j).getP_sku());
                                        product_name = detailVal.get(i).getOrder().get(j).getP_skuname();
                                        String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                        String updatePickupProducts = "UPDATE ProductDetails set product_name = '" + name_split + "', amount = '" + detailVal.get(i).getOrder().get(j).getP_price() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getP_skuqty() + "' " +
                                                "where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getP_sku() + "' AND pickup_type = 1 ";
                                        database.execSQL(updatePickupProducts);
                                    }
                                    checkPickupProducts.close();
                                }

                            }
                            try {
//                            changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
//                            new MyTask(order.getId(),detailVal.get(i).getShipmentid()).execute();
                            } catch (Exception ex) {
                            }
//                        changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
                            checkOrder.close();
                        }
                    } else if (value.getResMsg().equals("order failed")) {
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        Intent intent = new Intent("SyncServiceAction");
                        Bundle bundle = new Bundle();
                        bundle.putString("current_time", currentDateTimeString);
                        bundle.putString("sync", "sync");
                        intent.putExtras(bundle);
                        sendBroadcast(intent);
                    } else {

                    }

                }

                @Override
                public void onError(Throwable e) {
                    Log.d("dataerror", e.toString());
                }

                @Override
                public void onComplete() {
                    Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                }


            });

            return null;
        }

       /* @Override
        protected void onProgressUpdate(String... values) {
        }*/

        @Override
        protected void onPostExecute(Result result) {
            Log.v("myasynctask", "_ " + "onPostExecute");
            // Runs on the UI thread after doInBackground()
        }

        @Override
        protected void onCancelled(Result result) {
            // Runs on UI thread after cancel() is invoked
            // and doInBackground() has finished/returned
        }
    }


    /**
     * Bfil branch master  for bulk  delivery process
     */

    public void BFILbranchMaster() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);

        final Observable<BranchResp> observable = apiService.getBranchMaster().subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<BranchResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BranchResp value) {


                if (value.getResMsg().equalsIgnoreCase("Branch success")) {


                    branchMasterList = value.getBranchVal();

                    if (branchMasterList.size() > 0) {
                        for (int i = 0; i < branchMasterList.size(); i++) {
                            //Log.v("branch_master", String.valueOf(branchMasterList.get(i).getName()));

                            Cursor branchName = database.rawQuery("Select * from BranchMaster where branch_id = '" + branchMasterList.get(i).getCode() + "' ", null);

                            if (branchName.getCount() == 0) {
                                branchName.moveToFirst();

                                String insertUndeliveredReason = "Insert into BranchMaster (branch_id,branch_name,status) Values('" + branchMasterList.get(i).getCode() + "', '" + branchMasterList.get(i).getName() + "', '1')";

                                database.execSQL(insertUndeliveredReason);
                            } else {
                                String updateUndeliveredReason = "Update BranchMaster set branch_id = '" + branchMasterList.get(i).getCode() + "',branch_name = '" + branchMasterList.get(i).getName() + "',status = '1' where branch_id = '" + branchMasterList.get(i).getCode() + "'";

                                database.execSQL(updateUndeliveredReason);
                            }
                            branchName.close();
                        }
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
//                Log.d("error", e.toString());
            }

            @Override
            public void onComplete() {
//                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

}