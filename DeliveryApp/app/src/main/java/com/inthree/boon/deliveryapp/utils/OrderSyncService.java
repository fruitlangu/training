package com.inthree.boon.deliveryapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.request.OrderReq;
import com.inthree.boon.deliveryapp.request.OrderStatusReq;
import com.inthree.boon.deliveryapp.response.OrderChangeResp;
import com.inthree.boon.deliveryapp.response.OrderResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;

public class OrderSyncService extends Service {
    private ArrayList<OrderResp> orderList;
//    private final int UPDATE_INTERVAL = 60 * 5000;
private final int UPDATE_INTERVAL = 60 * 0;
    private Timer timer = new Timer();
    private static final int NOTIFICATION_EX = 1;
    private NotificationManager notificationManager;
    private SQLiteDatabase database;
    private static final String DB_NAME = "boonboxdelivery.sqlite";

    public static final String MyPREFERENCES = "MyPrefs";
    public static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0;
    public static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

    Context mContext;

    public OrderSyncService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        mContext =this;
        mContext = getApplicationContext();
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        Log.v("OrderSyncService","onCreate");

    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        Log.v("OrderSyncService","Started!");

        Intent sendprog = new Intent("sendProgress");
        Bundle bundle = new Bundle();
        bundle.putString("status", "start");
        sendprog.putExtras(bundle);
        sendBroadcast(sendprog);

//        new JSONProductParse().execute();
//        new JSONParseUpload().execute();
        getData();
        /*timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                new JSONProductParse().execute();
            }
        }, 0, UPDATE_INTERVAL);*/
        return START_STICKY;
    }

    private void stopService() {
        if (timer != null) timer.cancel();
    }


    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    private void getData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final InthreeApi apiService = retrofit.create(InthreeApi.class);
        final OrderReq order = new OrderReq();
        JSONObject paramObject = null;
        order.setId(AppController.getStringPreference(Constants.USER_ID,""));

        try {
            paramObject = new JSONObject();
            paramObject.put("runner_id", order.getId());
            paramObject.put("token", "123456");
            paramObject.put("deviceInfo", AppController.getdevice());
            Log.v("ordersync_paramObject",paramObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),paramObject.toString() );

        final Observable<OrderResp> observable = apiService.getOrders(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<OrderResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(OrderResp value) {

                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null;


                orderList = new ArrayList<>();
                List<OrderResp> detailVal = value.getDetails();
                if (value.getResMsg().equals("order success")) {
                    Log.v("get_data_msg", value.getResMsg());
                    for (int i = 0; i < detailVal.size(); i++) {
                        Cursor checkOrder = database.rawQuery("Select * from orderheader where Shipment_Number = '" +
                                        detailVal.get(i).getShipmentid() + "'",
                                null);
                        if (checkOrder.getCount() == 0) {

                            Log.v("OrderResp",  detailVal.get(i).getShipmentid()+"shipid"+detailVal.get(i).getOtp()+"getotp");

                            try {
                                date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(date);

                            String customer_contact_no = detailVal.get(i).getCustomerContactNumber();

                            String contactnumber = detailVal.get(i).getCustomerContactNumber();
                            String str = contactnumber;
                            /*int length = str.length();
                            if (length == 10) {
                                customer_contact_no = contactnumber;
                            } else if (length == 12) {
                                String phno = contactnumber.replace("91", "");
                                customer_contact_no = phno;
                            }

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
                            if (lang_json != null){
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
                                    String hi_val =null;
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
                                        } if (hindiOneObject.has("city")) {
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
                                        }if (bengaliOneObject.has("branch_name")) {
                                            String bengali_branch = bengaliOneObject.getString("branch_name");
                                            bengaliLangParamObject.put("branch_name", bengali_branch);
                                        }if (bengaliOneObject.has("delivery_address")) {
                                            String bengali_branch_deliaddr = bengaliOneObject.getString("delivery_address");
                                            bengaliLangParamObject.put("delivery_address", bengali_branch_deliaddr);
                                        }if (bengaliOneObject.has("branch_address")) {
                                            String bengali_branch_addr = bengaliOneObject.getString("branch_address");
                                            bengaliLangParamObject.put("branch_address", bengali_branch_addr);
                                        }if (bengaliOneObject.has("city")) {
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
                                        }if (marathiOneObject.has("branch_name")) {
                                            String marathi_branch = marathiOneObject.getString("branch_name");
                                            marathiLangParamObject.put("branch_name", marathi_branch);
                                        }if (marathiOneObject.has("delivery_address")) {
                                            String marathi_branch_deliaddr = marathiOneObject.getString("delivery_address");
                                            marathiLangParamObject.put("delivery_address", marathi_branch_deliaddr);
                                        }if (marathiOneObject.has("branch_address")) {
                                            String marathi_branch_addr = marathiOneObject.getString("branch_address");
                                            marathiLangParamObject.put("branch_address", marathi_branch_addr);
                                        }if (marathiOneObject.has("city")) {
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
                                        }if (assamOneObject.has("branch_name")) {
                                            String assam_branch = assamOneObject.getString("branch_name");
                                            assamLangParamObject.put("branch_name", assam_branch);
                                        }if (assamOneObject.has("delivery_address")) {
                                            String assam_branch_deliaddr = assamOneObject.getString("delivery_address");
                                            assamLangParamObject.put("delivery_address", assam_branch_deliaddr);
                                        }if (assamOneObject.has("branch_address")) {
                                            String assam_branch_addr = assamOneObject.getString("branch_address");
                                            assamLangParamObject.put("branch_address", assam_branch_addr);
                                        }if (assamOneObject.has("city")) {
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
                                        }if (orissaOneObject.has("branch_name")) {
                                            String orissa_branch = orissaOneObject.getString("branch_name");
                                            orissaLangParamObject.put("branch_name", orissa_branch);
                                        }if (orissaOneObject.has("delivery_address")) {
                                            String orissa_branch_deliaddr = orissaOneObject.getString("delivery_address");
                                            orissaLangParamObject.put("delivery_address", orissa_branch_deliaddr);
                                        }if (orissaOneObject.has("branch_address")) {
                                            String orissa_branch_addr = orissaOneObject.getString("branch_address");
                                            orissaLangParamObject.put("branch_address", orissa_branch_addr);
                                        }if (orissaOneObject.has("city")) {
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
                                        }if (teluguOneObject.has("branch_name")) {
                                            String telugu_branch = teluguOneObject.getString("branch_name");
                                            teluguLangParamObject.put("branch_name", telugu_branch);
                                        }if (teluguOneObject.has("delivery_address")) {
                                            String telugu_branch_deliaddr = teluguOneObject.getString("delivery_address");
                                            teluguLangParamObject.put("delivery_address", telugu_branch_deliaddr);
                                        }if (teluguOneObject.has("branch_address")) {
                                            String telugu_branch_addr = teluguOneObject.getString("branch_address");
                                            teluguLangParamObject.put("branch_address", telugu_branch_addr);
                                        }if (teluguOneObject.has("city")) {
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
                                        }if (kannadaOneObject.has("branch_name")) {
                                            String kannada_branch = kannadaOneObject.getString("branch_name");
                                            kannadaLangParamObject.put("branch_name", kannada_branch);
                                        }if (kannadaOneObject.has("delivery_address")) {
                                            String kannada_branch_deliaddr = kannadaOneObject.getString("delivery_address");
                                            kannadaLangParamObject.put("delivery_address", kannada_branch_deliaddr);
                                        }if (kannadaOneObject.has("branch_address")) {
                                            String kannada_branch_addr = kannadaOneObject.getString("branch_address");
                                            kannadaLangParamObject.put("branch_address", kannada_branch_addr);
                                        }if (kannadaOneObject.has("city")) {
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
                                        }if (punjabLangParamObject.has("branch_name")) {
                                            String punjab_branch = punjabOneObject.getString("branch_name");
                                            punjabLangParamObject.put("branch_name", punjab_branch);
                                        }if (punjabLangParamObject.has("delivery_address")) {
                                            String punjab_branch_deliaddr = punjabOneObject.getString("delivery_address");
                                            punjabLangParamObject.put("delivery_address", punjab_branch_deliaddr);
                                        }if (punjabLangParamObject.has("branch_address")) {
                                            String punjab_branch_addr = punjabOneObject.getString("branch_address");
                                            punjabLangParamObject.put("branch_address", punjab_branch_addr);
                                        }if (punjabLangParamObject.has("city")) {
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
                            /* language parsing json ends*/

                            String insertOrderHearder = "Insert into orderheader(order_number,customer_name," +
                                    "customer_contact_number,alternate_contact_number,to_be_delivered_by,billing_address,billing_city,billing_pincode," +
                                    "billing_telephone,shipping_address,shipping_city,shipping_pincode, shipping_telephone,invoice_amount,payment_mode," +
                                    "client_branch_name,branch_address,branch_pincode,branch_contact_number,group_leader_name,group_leader_contact_number," +

                                    "slot_number,referenceNumber,processDefinitionCode,Shipment_Number,sync_status,delivery_status,valid,attempt_count,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi,otp,urn,order_type,max_attempt,delivery_aadhar_required,virtual_id) " +

                                    "Values('" + detailVal.get(i).getOrderid() + "', '" + detailVal.get(i).getCustomerName() + "', '" + customer_contact_no + "'," +
                                    "'" + detailVal.get(i).getAlternateContactNumber() + "', '" + outputDateStr + "', " +
                                    "'" + detailVal.get(i).getBillingAddress() + "', '" + detailVal.get(i).getBillingCity() + "', '" + detailVal.get(i).getBillingPincode() + "', '" + detailVal.get(i).getBillingTelephone() + "', '" + detailVal.get(i).getShippingAddress() + "', '" + detailVal.get(i).getShippingCity() + "'," +


                                    " '" + detailVal.get(i).getShippingPincode() + "', '" + detailVal.get(i).getShippingTelephone() + "', '" + detailVal.get(i).getAmount() + "', '" + detailVal.get(i).getPaymentMode() + "', '" + detailVal.get(i).getClient_branch_name() + "', '" + detailVal.get(i).getBranch_address() + "', '" + detailVal.get(i).getBranch_pincode() + "'" +
                                    ", '" + detailVal.get(i).getBranch_contact_number() + "', '" + detailVal.get(i).getGroup_leader_name() + "', '" + detailVal.get(i).getGroup_leader_contact_number() + "', '" + detailVal.get(i).getSlot_number() + "', '" + detailVal.get(i).getReference() + "', '', '" + detailVal.get(i).getShipmentid() + "', 'P', '', '" + detailVal.get(i).getDownloadSync() + "', " + detailVal.get(i).getAttempt() + ",'"+tamil_val+"','"+telugu_val+"','"+punjabi_val+"','"+hindi_val+"','"+bengali_val+"','"+kannada_val+"','"+assam_val+"','"+orissa_val+"','"+marathi_val+"','"+detailVal.get(i).getOtp()+"','"+detailVal.get(i).getUrn()+"','"+detailVal.get(i).getOrder_type() +"','"+detailVal.get(i).getMax_attempt() +"',"+detailVal.get(i).getDelivery_aadhar_required()+",'"+value.getVirtual_id()+"')";


                            database.execSQL(insertOrderHearder);

                        } else {
                            try {
                                date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(date);

                            String customer_contact_no = detailVal.get(i).getCustomerContactNumber();
                           /* String contactnumber = detailVal.get(i).getCustomerContactNumber();
                            String str = contactnumber;
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
                            if (lang_json != null){
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
                                    String hi_val =null;
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
                                        } if (hindiOneObject.has("city")) {
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
                                        }if (bengaliOneObject.has("branch_name")) {
                                            String bengali_branch = bengaliOneObject.getString("branch_name");
                                            bengaliLangParamObject.put("branch_name", bengali_branch);
                                        }if (bengaliOneObject.has("delivery_address")) {
                                            String bengali_branch_deliaddr = bengaliOneObject.getString("delivery_address");
                                            bengaliLangParamObject.put("delivery_address", bengali_branch_deliaddr);
                                        }if (bengaliOneObject.has("branch_address")) {
                                            String bengali_branch_addr = bengaliOneObject.getString("branch_address");
                                            bengaliLangParamObject.put("branch_address", bengali_branch_addr);
                                        }if (bengaliOneObject.has("city")) {
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
                                        }if (marathiOneObject.has("branch_name")) {
                                            String marathi_branch = marathiOneObject.getString("branch_name");
                                            marathiLangParamObject.put("branch_name", marathi_branch);
                                        }if (marathiOneObject.has("delivery_address")) {
                                            String marathi_branch_deliaddr = marathiOneObject.getString("delivery_address");
                                            marathiLangParamObject.put("delivery_address", marathi_branch_deliaddr);
                                        }if (marathiOneObject.has("branch_address")) {
                                            String marathi_branch_addr = marathiOneObject.getString("branch_address");
                                            marathiLangParamObject.put("branch_address", marathi_branch_addr);
                                        }if (marathiOneObject.has("city")) {
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
                                        }if (assamOneObject.has("branch_name")) {
                                            String assam_branch = assamOneObject.getString("branch_name");
                                            assamLangParamObject.put("branch_name", assam_branch);
                                        }if (assamOneObject.has("delivery_address")) {
                                            String assam_branch_deliaddr = assamOneObject.getString("delivery_address");
                                            assamLangParamObject.put("delivery_address", assam_branch_deliaddr);
                                        }if (assamOneObject.has("branch_address")) {
                                            String assam_branch_addr = assamOneObject.getString("branch_address");
                                            assamLangParamObject.put("branch_address", assam_branch_addr);
                                        }if (assamOneObject.has("city")) {
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
                                        }if (orissaOneObject.has("branch_name")) {
                                            String orissa_branch = orissaOneObject.getString("branch_name");
                                            orissaLangParamObject.put("branch_name", orissa_branch);
                                        }if (orissaOneObject.has("delivery_address")) {
                                            String orissa_branch_deliaddr = orissaOneObject.getString("delivery_address");
                                            orissaLangParamObject.put("delivery_address", orissa_branch_deliaddr);
                                        }if (orissaOneObject.has("branch_address")) {
                                            String orissa_branch_addr = orissaOneObject.getString("branch_address");
                                            orissaLangParamObject.put("branch_address", orissa_branch_addr);
                                        }if (orissaOneObject.has("city")) {
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
                                        }if (teluguOneObject.has("branch_name")) {
                                            String telugu_branch = teluguOneObject.getString("branch_name");
                                            teluguLangParamObject.put("branch_name", telugu_branch);
                                        }if (teluguOneObject.has("delivery_address")) {
                                            String telugu_branch_deliaddr = teluguOneObject.getString("delivery_address");
                                            teluguLangParamObject.put("delivery_address", telugu_branch_deliaddr);
                                        }if (teluguOneObject.has("branch_address")) {
                                            String telugu_branch_addr = teluguOneObject.getString("branch_address");
                                            teluguLangParamObject.put("branch_address", telugu_branch_addr);
                                        }if (teluguOneObject.has("city")) {
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
                                        }if (kannadaOneObject.has("branch_name")) {
                                            String kannada_branch = kannadaOneObject.getString("branch_name");
                                            kannadaLangParamObject.put("branch_name", kannada_branch);
                                        }if (kannadaOneObject.has("delivery_address")) {
                                            String kannada_branch_deliaddr = kannadaOneObject.getString("delivery_address");
                                            kannadaLangParamObject.put("delivery_address", kannada_branch_deliaddr);
                                        }if (kannadaOneObject.has("branch_address")) {
                                            String kannada_branch_addr = kannadaOneObject.getString("branch_address");
                                            kannadaLangParamObject.put("branch_address", kannada_branch_addr);
                                        }if (kannadaOneObject.has("city")) {
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
                                        }if (punjabLangParamObject.has("branch_name")) {
                                            String punjab_branch = punjabOneObject.getString("branch_name");
                                            punjabLangParamObject.put("branch_name", punjab_branch);
                                        }if (punjabLangParamObject.has("delivery_address")) {
                                            String punjab_branch_deliaddr = punjabOneObject.getString("delivery_address");
                                            punjabLangParamObject.put("delivery_address", punjab_branch_deliaddr);
                                        }if (punjabLangParamObject.has("branch_address")) {
                                            String punjab_branch_addr = punjabOneObject.getString("branch_address");
                                            punjabLangParamObject.put("branch_address", punjab_branch_addr);
                                        }if (punjabLangParamObject.has("city")) {
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
//                                    "sync_status= 'P'," +
//                                    "delivery_status= ''," +
                                    "valid = '" + detailVal.get(i).getDownloadSync() + "'," +
                                    "attempt_count = '" + detailVal.get(i).getAttempt() + "'," +
                                    "tamil = '" + tamil_val + "'," +
                                    "hindi = '" + hindi_val + "', "+
                                    "assam = '" + assam_val +"'," +
                                    "punjabi = '" + punjabi_val + "', "+
                                    "marathi = '" + marathi_val + "'," +
                                    "telugu = '" + telugu_val + "'," +
                                    "kannada = '" + kannada_val + "'," +
                                    "orissa = '" + orissa_val + "'," +
                                    "virtual_id = '" + value.getVirtual_id() + "'," +
                                    "bengali = '" + bengali_val +"'" +

                                    " where " +
                                    "Shipment_Number ='" + detailVal.get(i).getShipmentid() + "'  "; //AND sync_status = 'P'
                            database.execSQL(queryupdate);

                        }

                        String product_name = "";
                        for (int j = 0; j < detailVal.get(i).getOrder().size(); j++) {

                            Log.v("prod_name", detailVal.get(i).getOrder().get(j).getQty() + "--" + detailVal.get(i).getShipmentid());


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
                            String item_json = detailVal.get(i).getOrder().get(j).getItem_json();
                            if (item_json != null && !item_json.equals("null")){
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
                                    Log.v("tamil_pval","-- "+ tamil_pval);
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
                                    Log.e("lang_json", "Could not parse malformed JSON: \"" + bengali_pval + "\"");
                                }
                            }


                            if(!detailVal.get(i).getOrder_type().equals("2")){
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
                            }else{

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
                            if (checkProducts.getCount() == 0) {

//                                Log.v("prod_name", detailVal.get(i).getOrderid() + "--" + detailVal.get(i).getOrder().get(j).getQty() + "--" + detailVal.get(i).getShipmentid());
                                product_name = detailVal.get(i).getOrder().get(j).getName();
                                String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                *//*String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code)" +
                                        " Values ('" + detailVal.get(i).getShipmentid() + "', '" + detailVal.get(i).getOrder().get(j).getName()
                                        + "', '" + detailVal.get(i).getOrder().get(j).getQty() + "', '" + detailVal.get(i).getOrder().get(j).getPrice() + "', '" +
                                        detailVal.get(i).getOrder().get(j).getSku() + "')";
                                database.execSQL(insertProduct);*//*
                                String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi)" +
                                        " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                        + "', '" + detailVal.get(i).getOrder().get(j).getQty() + "', '" + detailVal.get(i).getOrder().get(j).getPrice() + "', '" +
                                        detailVal.get(i).getOrder().get(j).getSku() + "','"+tamil_pval+"','"+telugu_pval+"','"+punjabi_pval+"','"+hindi_pval+"','"+bengali_pval+"','"+kannada_pval+"','"+assam_pval+"','"+orissa_pval+"','"+marathi_pval+"')";
                                database.execSQL(insertProduct);
                            } else {
//                                Log.v("prod_name1", detailVal.get(i).getOrder().get(j).getQty() + "--" + detailVal.get(i).getShipmentid());
                                product_name = detailVal.get(i).getOrder().get(j).getName();
                                String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                String updateProducts = "UPDATE ProductDetails set product_name = '" + name_split + "', amount = '" + detailVal.get(i).getOrder().get(j).getPrice() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getQty() + "',tamil = '"+tamil_pval+"',telugu = '"+telugu_pval+"'," +
                                        "punjabi = '"+punjabi_pval+"',hindi = '"+hindi_pval+"', bengali = '"+bengali_pval+"', kannada = '"+kannada_pval+"',assam = '"+assam_pval+"', orissa = '"+orissa_pval+"'," +
                                        "marathi = '"+marathi_pval+"'" +
                                        " where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getSku() + "' ";
                                database.execSQL(updateProducts);
                            }
                            checkProducts.close();*/


                            if(!detailVal.get(i).getOrder().get(j).getP_sku().equals("")){

                                Cursor checkPickupProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" +
                                                detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getP_sku() + "' AND pickup_type = 1 ",
                                        null);
                                if (checkPickupProducts.getCount() == 0) {
                                    Log.v("getPickupProds", " - "+ detailVal.get(i).getOrder().get(j).getP_sku() );
//                                    Log.v("getPickupProds", " - "+ checkPickupProducts.getString(checkPickupProducts.getColumnIndex("pickup_type")) );
                                    product_name = detailVal.get(i).getOrder().get(j).getP_skuname();
                                    String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                    String insertPickupProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code,pickup_type)" +
                                            " Values ('" + detailVal.get(i).getShipmentid() + "', '" + name_split
                                            + "', '" + detailVal.get(i).getOrder().get(j).getP_skuqty() + "', '" + detailVal.get(i).getOrder().get(j).getP_price() + "', '" +
                                            detailVal.get(i).getOrder().get(j).getP_sku() + "', 1)";
                                    database.execSQL(insertPickupProduct);
                                }else{
                                    Log.v("getPickupProds1", " - "+ detailVal.get(i).getOrder().get(j).getP_sku() );
                                    product_name = detailVal.get(i).getOrder().get(j).getP_skuname();
                                    String name_split = product_name.replace("'", "");
//                                Log.v("err_pname",name_split);
                                    String updatePickupProducts = "UPDATE ProductDetails set product_name = '" + name_split + "', amount = '" + detailVal.get(i).getOrder().get(j).getP_price() + "', quantity = '" + detailVal.get(i).getOrder().get(j).getP_skuqty() + "' "+
                                            "where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getOrder().get(j).getP_sku() + "' AND pickup_type = 1 ";
                                    database.execSQL(updatePickupProducts);
                                }
                                checkPickupProducts.close();
                            }
                        }
//                        changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
                        changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
                        checkOrder.close();
                    }

                } else if (value.getResMsg().equals("order failed")) {

                } else {

                }

            }

            @Override
            public void onError(Throwable e) {
                Log.d("error",e.toString());
            }

            @Override
            public void onComplete() {
//                Log.v("inhere","--");
            }


        });
    }

    /*private void getData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final InthreeApi apiService = retrofit.create(InthreeApi.class);
        final OrderReq order = new OrderReq();
        JSONObject paramObject = null;
        order.setId(AppController.getStringPreference(Constants.USER_ID,""));

        try {
            paramObject = new JSONObject();
            paramObject.put("runner_id", order.getId());
            Log.v("ordersync_paramObject",paramObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),paramObject.toString() );

        final Observable<OrderResp> observable = apiService.getOrders(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<OrderResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(OrderResp value) {

                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null;

                orderList = new ArrayList<>();
                List<OrderResp> detailVal = value.getDetails();
                if (value.getResMsg().equals("order success")){

                    for (int i = 0; i < detailVal.size(); i++) {
                        Cursor checkOrder = database.rawQuery("Select * from orderheader where Shipment_Number = '"+
                                        detailVal.get(i).getShipmentid()+"'",
                                null);
                        if (checkOrder.getCount() == 0){
                            Log.v("OrderResp", detailVal.get(i).getOrderid());
                            try {
                                date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(date);

                            String insertOrderHeader = "Insert into orderheader(order_number,customer_name," +
                                    "customer_contact_number,alternate_contact_number,to_be_delivered_by,billing_address,billing_city,billing_pincode," +
                                    "billing_telephone,shipping_address,shipping_city,shipping_pincode, shipping_telephone,invoice_amount,payment_mode," +
                                    "client_branch_name,branch_address,branch_pincode,branch_contact_number,group_leader_name,group_leader_contact_number," +
                                    "slot_number,referenceNumber,processDefinitionCode,Shipment_Number,sync_status,delivery_status, valid, attempt_count) "+
                                    "Values('"+ detailVal.get(i).getOrderid() +"', '"+ detailVal.get(i).getCustomerName() +"', '"+ detailVal.get(i).getCustomerContactNumber() +"'," +
                                    "'"+ detailVal.get(i).getAlternateContactNumber() +"', '"+ outputDateStr +"', " +
                                    "'"+ detailVal.get(i).getBillingAddress()+"', '"+ detailVal.get(i).getBillingCity() +"', '"+ detailVal.get(i).getBillingPincode() +"', '"+ detailVal.get(i).getBillingTelephone() +"', '"+ detailVal.get(i).getShippingAddress() +"', '"+ detailVal.get(i).getShippingCity() +"'," +
                                    " '"+ detailVal.get(i).getShippingPincode() +"', '"+ detailVal.get(i).getShippingTelephone() +"', '"+ detailVal.get(i).getAmount() +"', '"+ detailVal.get(i).getPaymentMode() +"', '"+ detailVal.get(i).getBranchCode() +"', '"+detailVal.get(i).getBranch_address()+"', '"+detailVal.get(i).getBranch_pincode()+"'" +
                                    ", '"+detailVal.get(i).getBranch_contact_number()+"', '"+detailVal.get(i).getGroup_leader_name()+"', '"+detailVal.get(i).getGroup_leader_contact_number()+"', '"+detailVal.get(i).getSlot_number()+"', '"+ detailVal.get(i).getReference() +"', '', '"+ detailVal.get(i).getShipmentid() +"', 'P', '', '" + detailVal.get(i).getDownloadSync() + "', "+detailVal.get(i).getAttempt()+")";
                            database.execSQL(insertOrderHeader);

                        }else{

                            try {
                                date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(date);
                            String queryupdate = "UPDATE orderheader set order_number = '" + detailVal.get(i).getOrderid() +
                                    "',customer_name='"+detailVal.get(i).getCustomerName()+"',customer_contact_number = '"+detailVal.get(i).getCustomerContactNumber()+"'," +
                                    "alternate_contact_number = '"+detailVal.get(i).getAlternateContactNumber()+"'," +
                                    "to_be_delivered_by = '"+outputDateStr+"'," +
                                    "billing_address= '"+detailVal.get(i).getBillingAddress()+"'," +
                                    "billing_city= '"+detailVal.get(i).getBillingCity()+"'," +
                                    "billing_pincode= '"+detailVal.get(i).getBillingPincode() +"'," +
                                    "billing_telephone= '"+ detailVal.get(i).getBillingTelephone()+"'," +
                                    "shipping_address= '"+ detailVal.get(i).getShippingAddress()+"'," +
                                    "shipping_city= '"+detailVal.get(i).getShippingCity()+"'," +
                                    "shipping_pincode= '"+detailVal.get(i).getShippingPincode()+"', " +
                                    "shipping_telephone= '"+detailVal.get(i).getShippingTelephone()+"'," +
                                    "invoice_amount= '"+detailVal.get(i).getAmount()+"'," +
                                    "payment_mode= '"+detailVal.get(i).getPaymentMode()+"'," +
                                    "client_branch_name= '"+detailVal.get(i).getBranchCode()+"'," +
                                    "branch_address= '"+detailVal.get(i).getBranch_address()+"'," +
                                    "branch_pincode= '"+detailVal.get(i).getBranch_pincode()+"'," +
                                    "branch_contact_number= '"+detailVal.get(i).getBranch_contact_number()+"'," +
                                    "group_leader_name= '"+detailVal.get(i).getGroup_leader_name()+"'," +
                                    "group_leader_contact_number= '"+detailVal.get(i).getGroup_leader_contact_number()+"'," +
                                    "slot_number= '"+detailVal.get(i).getSlot_number()+"'," +
                                    "referenceNumber= '"+ detailVal.get(i).getReference() +"'," +
                                    "processDefinitionCode= ''," +
                                    "Shipment_Number= '"+detailVal.get(i).getShipmentid()+"'" +
                                    " where " +  //sync_status= 'P'
                                    "Shipment_Number ='" + detailVal.get(i).getShipmentid() + "' AND sync_status = 'P' ";
                            database.execSQL(queryupdate);
                        }


                        for (int j = 0; j < detailVal.get(i).getOrder().size(); j++) {

                            Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '"+
                                            detailVal.get(i).getShipmentid() +"' AND product_code = '"+detailVal.get(i).getOrder().get(j).getSku()+"'",
                                    null);
                            try{
                                if (checkProducts.getCount() == 0){

                                    Log.v("prod_name", detailVal.get(i).getOrderid() +"--"+detailVal.get(i).getOrder().get(j).getQty()+ "--"+ detailVal.get(i).getShipmentid());

                                    String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code)" +
                                            " Values ('"+ detailVal.get(i).getShipmentid() +"', '"+ detailVal.get(i).getOrder().get(j).getName()
                                            +"', '"+ detailVal.get(i).getOrder().get(j).getQty() +"', '"+ detailVal.get(i).getOrder().get(j).getPrice() +"', '"+
                                            detailVal.get(i).getOrder().get(j).getSku() +"')";
                                    database.execSQL(insertProduct);
                                }else{
                                    Log.v("prod_name1", detailVal.get(i).getOrder().get(j).getQty()+ "--"+ detailVal.get(i).getShipmentid());

                                    String updateProducts = "UPDATE ProductDetails set amount = '"+detailVal.get(i).getOrder().get(j).getPrice()+"', quantity = '"+detailVal.get(i).getOrder().get(j).getQty()+"'" +
                                            " where shipmentnumber = '"+detailVal.get(i).getShipmentid()+"' AND product_code = '"+ detailVal.get(i).getOrder().get(j).getSku()+"' ";
                                    database.execSQL(updateProducts);

                                }
                            }
                            finally{
                                checkProducts.close();
                            }
                        }
                        changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
                        checkOrder.close();
                    }
                }else if(value.getResMsg().equals("order failed")){
                    Log.v("order_staus", value.getResMsg());

                }else{

                }

            }

            @Override
            public void onError(Throwable e) {
                Log.d("error",e.toString());
            }

            @Override
            public void onComplete() {
//                Log.v("inhere","--");
            }


        });
    }*/

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
            Log.v("orderstatus",runnerID+"-"+shipmentID);

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
                Log.v("response_message",detailVal.getRes_msg());

             /*   Intent intent = new Intent("SyncServiceAction");
                Bundle bundle = new Bundle();
                bundle.putString("current_time", currentDateTimeString);
                intent.putExtras(bundle);
                sendBroadcast(intent);*/
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }

            @Override
            public void onError(Throwable e) {
                Log.d("error",e.toString());
            }

            @Override
            public void onComplete() {
                Log.v("inhere","--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

}