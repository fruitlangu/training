package com.inthree.boon.deliveryapp.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.ServiceReasonAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.response.ReasonVal;
import com.inthree.boon.deliveryapp.response.UndeliveryResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.GPSTracker;
import com.inthree.boon.deliveryapp.utils.SimpleLocation;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;

public class ServiceIncompleteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] countryNames = {"India", "China", "Australia", "Portugle", "America", "New Zealand"};
    int flags[] = {R.drawable.ic_arrow_back_white_36dp, R.drawable.ic_arrow_back_white_36dp, R.drawable.ic_arrow_back_white_36dp, R.drawable.ic_arrow_back_white_36dp, R.drawable.ic_arrow_back_white_36dp, R.drawable.ic_arrow_back_white_36dp};


    ArrayList<ReasonVal> serviceReasonVals;
    /**
     * Get the shipment from reason master
     */
    private String shipmentNumber;

    ProgressIndicatorActivity dialogLoading;

    /**
     * set the shipment number into textview
     */
    private TextView txtShip;

    /**
     * Reason val set the
     */
    ReasonVal reasonVal;

    /**
     * set the product name into textview
     */
    private TextView productName;

    /**
     * set the product name into textview
     */
    private TextView incompleteUploadBtn;

    /**
     * Get the lat and lang of location
     */
    public Location mLastLocation;

    /**
     * Get the reason value store into sqlite
     */
    String reasonValue;

    /**
     * Get the status of value in reason
     */
    String reasonStatus;

    private SimpleLocation mLocation;

    /**
     * Set the reason into spinner
     */
    private Spinner reasonspin;


    /**
     * Open the database
     */
    ExternalDbOpenHelper dbOpenHelper;

    /**
     * Database
     */
    static SQLiteDatabase database;

    /**
     * get the latitude
     */
    double latitude;

    /**
     * Get the longitude
     */
    double longitud;

    /**
     * Get the attempt count from service master sqlite
     */
    private int unIncomAttemptCount;

    /**
     * Name of product which is in order
     */
    private String product;

    LinearLayout service_reason_btn;
    private SweetAlertDialog pDialog;

    private ReasonVal reasonValHead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_incomplete);

        reasonspin = (Spinner) findViewById(R.id.simpleSpinner);
        txtShip = (TextView) findViewById(R.id.shipment_id);
        productName = (TextView) findViewById(R.id.product_id);
        incompleteUploadBtn = (TextView) findViewById(R.id.incomplete_txt_upload);
        service_reason_btn = (LinearLayout) findViewById(R.id.service_reason_btn);

        dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.btn_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        reasonValHead = new ReasonVal();
        reasonValHead.setReason(getResources().getString(R.string.select_reason_def));
        serviceReasonVals = new ArrayList<>();
        serviceReasonVals.add(reasonValHead);

        // construct a new instance
        mLocation = new SimpleLocation(this);

        // reduce the precision to 5,000m for privacy reasons
        mLocation.setBlurRadius(5000);

        // if we can't access the location yet
        if (!mLocation.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        latitude = mLocation.getLatitude();
        longitud = mLocation.getLongitude();

//        Toast.makeText(ServiceIncompleteActivity.this, "Latitude: " + latitude, Toast.LENGTH_SHORT).show();
//        Toast.makeText(ServiceIncompleteActivity.this, "Longitude: " + longitud, Toast.LENGTH_SHORT).show();

        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(this, ServiceIncompleteActivity.this);


        Intent shipNum = getIntent();
        shipmentNumber = shipNum.getStringExtra("ship_num");
        product = shipNum.getStringExtra("product_name");
//        product="productname";
//        shipmentNumber = "32000001";

        txtShip.setText(shipmentNumber);
        productName.setText(product);
        database.execSQL("UPDATE serviceMaster set delivery_status = 'incomplete' where shipment_id ='" +
                shipmentNumber + "' ");
        reasonspin.setOnItemSelectedListener(this);
        getIncompleteData();
        getIncompleteFetchData();
        service_reason_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reasonValue.equals(getResources().getString(R.string.select_reason_def))) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.undeliver_Reason), Toast.LENGTH_LONG).show();
                } else
                    uploadBtn();
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getApplicationContext(), serviceReasonVals.get(position).getReason(), Toast.LENGTH_LONG).show();
        reasonspin.setSelection(position);

        reasonValue = serviceReasonVals.get(position).getReason();
        reasonStatus = serviceReasonVals.get(position).getDioStatus();
        saveIncompleteReasonValue();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Submit the upload btn for incomplete
     */
    private void uploadBtn() {
        if (Utils.checkNetworkAndShowDialog(ServiceIncompleteActivity.this)) {

            if (saveIncompleteReasonValue()) {
                uploadIncompletedelivered();
            }
        } else {
            saveIncompleteReasonValue();
            database.execSQL("UPDATE serviceMaster set sync_status = 'C' where shipment_id ='" +
                    shipmentNumber + "' ");
            alertDialogMsgOffline(ServiceIncompleteActivity.this, getResources().getString(R.string.undeli_title), ServiceIncompleteActivity.this.getString(R.string.delivery_offline), getResources().getString(R.string.ok));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        mLocation.beginUpdates();
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        mLocation.endUpdates();

        super.onPause();
    }

    /**
     * Get the data incomplete reason from service incomplete master
     */
    private void getIncompleteData() {

        Cursor incompleteReason = database.rawQuery("Select IFNULL(rid,0) as rid," +
                "IFNULL(reason,0) as reason," +
                "IFNULL(reasonstatus,0) as reasonstatus  from ServiceIncompleteReasonMaster", null);


        System.out.println("COUNT : " + incompleteReason.getCount());

        if (incompleteReason.getCount() > 0) {


            if (incompleteReason.moveToFirst()) {
                do {
                    reasonVal = new ReasonVal();

                    reasonVal.setId(incompleteReason.getString(incompleteReason.getColumnIndex("rid")));

                    reasonVal.setDioStatus(incompleteReason.getString(incompleteReason.getColumnIndex("reasonstatus")));
                    reasonVal.setReason(incompleteReason.getString(incompleteReason.getColumnIndex("reason")));


                    serviceReasonVals.add(reasonVal);

                } while (incompleteReason.moveToNext());
            }
            incompleteReason.close();
        }
        /* reasonVal.setReason(getResources().getString(R.string.select_reason_def));*/

        ServiceReasonAdapter customAdapter = new ServiceReasonAdapter(getApplicationContext(), serviceReasonVals);
        reasonspin.setAdapter(customAdapter);

    }


    /**
     * Get the data incomplete reason from service incomplete master
     */
    private void getIncompleteFetchData() {
        Cursor incompleteReason = database.rawQuery("Select IFNULL(I.reason_status,0) as reason_status," +
                "IFNULL(I.ship_no,0) as ship_no," +
                "IFNULL(I.reason,0) as reason,IFNULL(I.reason,0) as reason,IFNULL(S.name,0) as name from ServiceIncompleteConfirmation I INNER JOIN serviceItems S ON I.ship_no==S.shipment_number where S.shipment_number='" + shipmentNumber + "'", null);

        System.out.println("COUNT : " + incompleteReason.getCount());
        if (incompleteReason.getCount() > 0) {
            if (incompleteReason.moveToFirst()) {
                do {

                    String spinreason = incompleteReason.getString(incompleteReason.getColumnIndex("reason"));
                    String shipNo = incompleteReason.getString(incompleteReason.getColumnIndex("ship_no"));
                    String name = incompleteReason.getString(incompleteReason.getColumnIndex("name"));

                    txtShip.setText(shipNo);
                    //reasonspin.setSelection(((ArrayAdapter<String>)reasonspin.getAdapter()).getPosition(reason));

                    reasonspin.setSelection(getIndex(spinreason));

                    productName.setText(name);


                } while (incompleteReason.moveToNext());
            }
            incompleteReason.close();
        }


    }

    /**
     * GEt the spinner and value of spinner
     *
     * @param myString get the string
     * @return returh the value
     */
    private int getIndex(String myString) {

        int index = 0;

        for (int i = 0; i < serviceReasonVals.size(); i++) {

            if (serviceReasonVals.get(i).getReason().equals(myString)) {
                index = i;
            }
        }
        return index;
    }


    /**
     * Get the data incomplete value and save into sqlite
     */
    private boolean saveIncompleteReasonValue() {
        Cursor checkOrder = database.rawQuery("Select * from ServiceIncompleteConfirmation where ship_no = '" +
                        shipmentNumber + "'",
                null);
        if (checkOrder.getCount() == 0) {

            Log.v("reasonStatus", shipmentNumber + reasonValue + reasonStatus);

            String insertServiceMaster = "Insert into ServiceIncompleteConfirmation(ship_no,reason,reason_status,incom_lat,incom_long) " +
                    "Values('" + shipmentNumber + "','" + reasonValue + "','" + reasonStatus + "','" + latitude + "','" + longitud + "')";
            database.execSQL(insertServiceMaster);

        } else {


            String queryupdate = "UPDATE ServiceIncompleteConfirmation set reason_status = '" + reasonStatus +
                    "',reason='" + reasonValue + "'" +
                    "," + "ship_no = '" + shipmentNumber + "'" +
                    "," + "incom_lat = '" + latitude + "'" +
                    "," + "incom_long = '" + longitud + "'" +
                    " where " +
                    "ship_no ='" + shipmentNumber + "'  ";
            database.execSQL(queryupdate);

        }

        checkOrder.close();
        return true;
    }


    /**
     * Upload all the data into server side
     */
    public void uploadIncompletedelivered() {
        // Delete from DeliveryConfirmation table to avoid possible duplicate entry.
        database.execSQL("DELETE FROM serviceConfirmation where ship_num='" + shipmentNumber + "'");

        dialogLoading = new ProgressIndicatorActivity(ServiceIncompleteActivity.this);
        dialogLoading.showProgress();
        /*SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTimeString = format.format(new Date());*/
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
        String currentDateTimeString = format.format(new Date());


//            Log.v("shipmentNumber", shipmentNumber);
        Cursor getIncompleteValue = database.rawQuery("select  s.sync_status,s.order_id, s.shipment_id, " +
                "IFNULL(i.ship_no,0) as ship_no,IFNULL(s.attempt, 0) as attempt, IFNULL(i.reason,0) as reason, " +
                "IFNULL(i.reason_status, 0) as reason_status,IFNULL(i.incom_lat, 0) as incom_lat,IFNULL(i.incom_long, 0) as incom_long from serviceMaster s INNER JOIN ServiceIncompleteConfirmation i on i" +
                ".ship_no = s.shipment_id where " +
                " i.ship_no = '" + shipmentNumber + "' ", null);
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
            Log.v("undeliveredupload", shipmentNumber);

            //unIncomAttemptCount = Integer.parseInt(getIncompleteValue.getString(getIncompleteValue.getColumnIndex("attempt")));
            unIncomAttemptCount = getIncompleteValue.getInt(getIncompleteValue.getColumnIndex("attempt"));


            database.execSQL("UPDATE serviceMaster set delivery_status = 'incomplete' where shipment_id ='" +
                    shipmentNumber + "' ");

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
                        updateOrderStatus();
                        alertDialogMsg(ServiceIncompleteActivity.this, "Success", "Service Incomplete Successful", getResources().getString(R.string.ok));
                        dialogLoading.dismiss();


                    } else if (value.getRes_msg().equalsIgnoreCase("service incomplete failed")) {
                        //alertDialogMsg(UndeliveryActivity.this, getResources().getString(R.string.undeli_title),  getResources().getString(R.string.undeli_success_msg), getResources().getString(R.string.ok));
                        dialogLoading.dismiss();
                        //updateOrderStatus();
                    } else {
                        Logger.showShortMessage(ServiceIncompleteActivity.this, "Service Incomplete Failed");
                        dialogLoading.dismiss();
                    }


                }

                @Override
                public void onError(Throwable e) {
                    Log.d("uploadUndelivered", e.toString());
                    dialogLoading.dismiss();
                }

                @Override
                public void onComplete() {
                    Log.v("inhere", "uploadUndelivered");
//                observable.unsubscribeOn(Schedulers.newThread());
                    dialogLoading.dismiss();
                }


            });
        }


    }


    /**
     * Get request for header seconds
     *
     * @return return the okHttpClient
     */
    private OkHttpClient getRequestHeader() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();

        return okHttpClient;
    }

    private void updateOrderStatus() {

        unIncomAttemptCount++;
        database.execSQL("UPDATE serviceMaster set sync_status = 'U', attempt = " + unIncomAttemptCount + " where shipment_id ='" +
                shipmentNumber + "' ");
    }

    public void alertDialogMsg(Context context, String title, String content, String okmsg) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
//        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }
                })
                .show();
        sweetAlertDialog.setCancelable(false);
    }

    public void backAlert(final Context context, String title, String content, String okmsg, String
            canmessage) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        final String currentDateTimeString = format.format(new Date());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setCancelable(false);

        pDialog.setTitleText(title)
                .setContentText(content)
                .setCancelText(canmessage).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        })
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        Cursor count = database.rawQuery("Select * from ServiceIncompleteConfirmation where ship_no = '" +
                                        shipmentNumber + "'",
                                null);
                        if (count.getCount() == 0) {
                            String insertServiceMaster = "Insert into ServiceIncompleteConfirmation(ship_no,reason,reason_status,incom_lat,incom_long) " +
                                    "Values('" + shipmentNumber + "','" + reasonValue + "','" + reasonStatus + "','" + latitude + "','" + longitud + "')";
                            database.execSQL(insertServiceMaster);
                        } else if (count.getCount() > 0) {

                            String queryupdate = "UPDATE ServiceIncompleteConfirmation set reason_status = '" + reasonStatus +
                                    "',reason='" + reasonValue + "'" +
                                    "," + "ship_no = '" + shipmentNumber + "'" +
                                    "," + "incom_lat = '" + latitude + "'" +
                                    "," + "incom_long = '" + longitud + "'" +
                                    " where " +
                                    "ship_no ='" + shipmentNumber + "'  ";
                            database.execSQL(queryupdate);
                        }
                        count.close();
                        finish();
                    }
                })
                .show();
    }

    public void onBackPressed() {
        backAlert(this, getResources()
                .getString(R.string.dl_back_pressed), getResources().getString(R.string.back_pressed), getResources()
                .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel));
    }


    public void AlertDialogCancel(final Context context, String title, String content, String okmsg, String
            canmessage) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        final String currentDateTimeString = format.format(new Date());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setCancelable(false);

        pDialog.setTitleText(title)
                .setContentText(content)
                .setCancelText(canmessage).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();


            }
        })
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        getIncompleteFetchData();
                    }
                })
                .show();
    }

    public void alertDialogMsgOffline(Context context, String title, String content, String okmsg) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
//        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }
                })
                .show();
        sweetAlertDialog.setCancelable(false);
    }

}
