package com.inthree.boon.deliveryapp.activity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.inthree.boon.deliveryapp.LocationUtils.LocationHelper;
import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.model.UndeliverConfirm;
import com.inthree.boon.deliveryapp.newcamera.CameraFragmentMainActivity;
import com.inthree.boon.deliveryapp.newcamera.PreviewActivity;
import com.inthree.boon.deliveryapp.request.UndeliveryReq;
import com.inthree.boon.deliveryapp.response.UndeliveryResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;
import com.inthree.boon.deliveryapp.utils.StringOperationsUtils;
import com.inthree.boon.deliveryapp.utils.SyncService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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


public class UndeliveryActivity extends AppCompatActivity implements View.OnClickListener, com.google.android.gms
        .location
        .LocationListener, View
        .OnFocusChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_CHECK_SETTINGS_GPS = 2000;
    private ArrayList<UndeliveryResp> undeliveryList;

    String image_url;
    String cam_title;
    String img_path;
    SQLiteDatabase database;
    String file_path = "/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/";
    String file_path_old = "/storage/emulated/0/DCIM/Camera/";
    String currentDateandTime;
    private static final int UNDELIVERED_PROOF = 103;
    ImageView img_reasion, img_pic, img_remark;
    ImageView imag_re_pic;
    LinearLayout image_lay2;
    LinearLayout completeRoot;
    int position = 0;
    String imageCapture;

    private int battery_level;

    AlertDialog alertDialog1;
    Spinner sp_reason;
    AppCompatButton bt_back;
    AppCompatButton bt_submit;
    String str_reason;
    Context mcontext;
    ArrayList<String> my_array = new ArrayList<String>();

    List<String> list = new ArrayList<String>();
    CharSequence[] cs;
    ArrayAdapter my_Adapter;

    /**
     * Request code path
     */
    private final static String FILE_PATH_ARG = "file_path_arg";
    /**
     * Enable and disable the root
     */
    private LinearLayout reasonRoot;

    /**
     * Enable and disable the root
     */
    private LinearLayout imageRoot;

    /**
     * Enable and disable the root
     */
    private LinearLayout remarksRoot;


    ProgressIndicatorActivity dialogLoading;

    /**
     * Check whether the status is partial or not
     */
    String statusSync;

    /**
     * Get the shipment number
     */
    private String shipmentNumber;

    /**
     * Initliaze the model
     */
    UndeliverConfirm undeliverConfirm;

    /**
     * Text input layout
     */
    private TextInputLayout textInputLayout;

    /**
     * Text input for showing the alert box
     */
    private AppCompatEditText textInput;

    /**
     * The strop.
     */
    private StringOperationsUtils strop;

    /**
     * Get the remarks
     */
    private String remarks;

    /**
     * Get the latitude
     */
    private double latitude_user;

    public Location mLastLocation;


    /**
     * Get the class name
     */
    private static final String TAG = DeliveryActivity.class.getSimpleName();

    /**
     * Get the longitude
     */
    private double longitude_user;
    private String latitude;
    private String longitude;


    /**
     * Get the location of latitude and longitude
     */
    private LocationHelper locationHelper;

    private LocationManager manager;
    private LocationRequest mLocationRequest;

    /**
     * Google client to interact with Google API
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Get the activity name
     */
    String activityName;

    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;

    /**
     * Get the ship address
     */
    private String shipAdd;

    /**
     * Get the text address store into string
     */
    private AppCompatEditText txtAdd;

    /**
     * Get the address of input address
     */
    private TextInputLayout inputAdd;

    /**
     * Check box to redirect the address of customer
     */
    private AppCompatCheckBox redirect;

    /**
     * Text input layout has been displayed
     */
    AppCompatEditText custName;

    /**
     * Text input layout has been displayed
     */
    AppCompatEditText custPhoneNo;

    /**
     * Text input layout has been displayed
     */
    AppCompatEditText shippingAddress;

    /**
     * Text input layout has been displayed
     */
    AppCompatEditText shipPincode;

    /**
     * Enable and disable the error in edit text
     */
    TextInputLayout textName;

    /**
     * Enable and disable the error in edit text
     */
    TextInputLayout textPhoneNumber;

    /**
     * Enable and disable the error in edit text
     */
    TextInputLayout textAddress;

    /**
     * Enable and disable the error in edit text
     */
    TextInputLayout textLandmark;

    /**
     * Enable and disable the error in edit text
     */
    TextInputLayout textpincode;

    private String strAdd;

    private TextInputLayout textAmount;
    private AppCompatEditText custAmount;
    private AppCompatEditText landMark;
    private AppCompatCheckBox redirectundel;
    private LinearLayout mapRoot;

    /**
     * Get the map current address
     */
    AppCompatButton applyLocAdd;

    /***
     * Response code argument
     */
    private static final String RESPONSE_CODE_ARG = "response_code_arg";

    /**
     * User can see the current location in map
     */
    private AppCompatImageView gMap;

    /**
     * Set the addresskyc of current location
     */
    private TextView locationFindAddress;

    /**
     * Get the current latitude and longititude
     */
    double lat;
    double lang;


    /**
     *
     */
    double unDelat;
    double unDelang;

    public static final int LOCATION_ADDRESS_CODE = 201;

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

    private final static String UN_DELIVERY = "undelivery";

    /**
     * Check the latitude and longtitude
     */
    double latCheck;

    double lonCheck;

    /**
     * Get the current location addres of the map store into string
     */
    private String currentLocation;

    /**
     * Get the pincode of address store into string
     */
    private String postalCode;

    /**
     * Hide the maplayout
     */
    private LinearLayout mapLayout;

    /**
     * show the image by using bitmap
     */
    private Bitmap b;
    private String file_proofPhoto;
    private GoogleApiClient googleApiClient;
    int un_attempt_count;
    String user_language;
    Locale myLocale;

    /**
     *
     */
    private String formattedDate;

    String aadhaarEnabled;
    String rid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_delivery);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        user_language = AppController.getStringPreference(Constants.USER_LANGUAGE,"");
        if(user_language.equals("tamil")){
            AppController.setLocale("ta");
        }else if(user_language.equals("telugu")){
            AppController.setLocale("te");
        }else if(user_language.equals("marathi")){
            AppController.setLocale("mr");
        }else if(user_language.equals("hindi")){
            AppController.setLocale("hi");
        }else if(user_language.equals("punjabi")){
            AppController.setLocale("pa");
        }else if(user_language.equals("odia")){
            AppController.setLocale("or");
        }else if(user_language.equals("bengali")){
            AppController.setLocale("be");
        }else if(user_language.equals("kannada")){
            AppController.setLocale("kn");
        }else if(user_language.equals("assamese")){
            AppController.setLocale("as");
        }else{
            AppController.setLocale("en");
        }

        img_reasion = (ImageView) findViewById(R.id.imag_reasion);
        img_pic = (ImageView) findViewById(R.id.imag_re_pic);
        img_remark = (ImageView) findViewById(R.id.imag_remark);
        imag_re_pic = (ImageView) findViewById(R.id.imag_re_pic);
        reasonRoot = (LinearLayout) findViewById(R.id.reason_lay1);
        imageRoot = (LinearLayout) findViewById(R.id.image_lay2);
        remarksRoot = (LinearLayout) findViewById(R.id.remarks_lay3);
        image_lay2 = (LinearLayout) findViewById(R.id.image_lay2);
        completeRoot = (LinearLayout) findViewById(R.id.ll_complete);
        undeliverConfirm = new UndeliverConfirm();
        strop = new StringOperationsUtils();
        locationHelper = new LocationHelper(this);
        locationHelper.checkpermission();
        image_url = getResources().getString(R.string.delivery_url) + "/media/";
        file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
        Intent shipUndelivered = getIntent();
        statusSync = shipUndelivered.getStringExtra("undelivered");

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());


        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            createLocationRequest();
        }
        getLocation();

        completeRoot.setOnClickListener(this);


        /*******   Database Open *************/
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();

        Intent shipNum = getIntent();
        shipmentNumber = shipNum.getStringExtra(Constants.SHIPMENT_NUMBER);
//        Log.v("shipnum", "--" + shipmentNumber);
        Cursor getUndeliveredStatus = database.rawQuery("Select * from orderheader where Shipment_Number = '" + shipmentNumber + "' ", null);
        if (getUndeliveredStatus.getCount() > 0) {
            getUndeliveredStatus.moveToFirst();
            String getUndeliveredVal = getUndeliveredStatus.getString(getUndeliveredStatus.getColumnIndex("sync_status"));
            aadhaarEnabled = getUndeliveredStatus.getString(getUndeliveredStatus.getColumnIndex("delivery_aadhar_required"));
//            Log.v("getUndelivered", getUndeliveredVal);
            if (getUndeliveredVal.equals("U")) {
                database.execSQL("DELETE FROM UndeliveredConfirmation where shipmentnumber = '" + shipmentNumber + "' ");
            }
        }
        getprofile();
        getTableValues();
        checStatusUndelivered();
        remarksRoot.setEnabled(false);
        image_lay2.setEnabled(false);
        reasonRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.main_bg));
        /*img_reasion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CreateAlertDialogWithRadioButtonGroup();
                schemeDetailAlert();
            }
        });


        image_lay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageCapture();

            }
        });*/

        reasonRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CreateAlertDialogWithRadioButtonGroup();
                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.reason);
                reasonAlertBox();
            }
        });


        imageRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.image);
                imageCapture();

            }
        });
        remarksRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.remark);
                delAlertBox();

            }
        });
        getCustValue();
        updateSyncStatus();
    }


    public void checStatusUndelivered() {
        Cursor statusUndeliver = database.rawQuery("select DISTINCT sno as sno ,IFNULL(remarks,0) as remarks,IFNULL" +
                "(proof_photo,0) as proof_photo,IFNULL(reason,0) as reason,IFNULL(sync_status,0) as sync_status,IFNULL(latitude,0) as latitude,IFNULL" +
                "(longitude,0) as longitude" + " from " + "UndeliveredConfirmation where shipmentnumber='"
                + shipmentNumber + "' ", null);
        if (statusUndeliver.getCount() > 0) {
            AlertDialogCancel(this, getResources().getString(R.string.sure), getResources().getString(R.string.getwarning), getResources()
                    .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel), statusUndeliver);
        } else {
            img_pic.setClickable(false);
            img_remark.setClickable(false);
//            CreateAlertDialogWithRadioButtonGroup();
            reasonAlertBox();
        }
    }


    public void AlertDialogCancel(final Context context, String title, String content, String okmsg, String
            canmessage, final Cursor customerName) {

        final SweetAlertDialog eAlert = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        eAlert.setCancelable(false);
        eAlert
                .setTitleText(title)
                .setContentText(content)
                .setCancelText(canmessage).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
//                delAlertBox();
                reasonAlertBox();
            }
        })
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        Cursor statusUndeliver = database.rawQuery("select DISTINCT sno as sno ,IFNULL(remarks,0) as remarks,IFNULL" +
                                "(proof_photo,0) as proof_photo,IFNULL(shipment_address,0) as shipment_address ,IFNULL(reason,0) as " +
                                "reason,IFNULL" + "(sync_status,0) as sync_status,IFNULL(redirect,0) as redirect," +
                                "IFNULL(latitude,0) as latitude,IFNULL" +
                                "(longitude,0) as longitude" + " from " + "UndeliveredConfirmation where shipmentnumber='"
                                + shipmentNumber + "' ", null);

                        if (statusUndeliver.getCount() > 0) {
                            statusUndeliver.moveToFirst();

                            if (!statusUndeliver.getString(statusUndeliver.getColumnIndex("reason")).equals("")) {
//                                Log.v("sample_text", "tt:" + statusUndeliver.getString(statusUndeliver.getColumnIndex("reason")));
                                undeliverConfirm.setReason(statusUndeliver.getString(statusUndeliver.getColumnIndex("reason")));
                                undeliverConfirm.setAddress(statusUndeliver.getString(statusUndeliver.getColumnIndex
                                        ("shipment_address")));
                                undeliverConfirm.setRedirect(statusUndeliver.getString(statusUndeliver.getColumnIndex
                                        ("redirect")));

                                reasonRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.main_bg));
                                reasonRoot.setEnabled(true);
                                reasonRoot.setClickable(true);
                                imageRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.main_bg));
                                imageRoot.setEnabled(true);
                                imageRoot.setClickable(true);
                                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.image);
                            } else {
                                reasonRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_menu));
                                reasonRoot.setEnabled(false);
                                reasonRoot.setClickable(false);
                                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.reason);
//                                delAlertBox();
                                reasonAlertBox();
//                                Log.v("where_are_we", "customer_name");
                            }


                            if (!statusUndeliver.getString(statusUndeliver.getColumnIndex("proof_photo")).equals("")) {
                                undeliverConfirm.setProofPhoto(statusUndeliver.getString(statusUndeliver.getColumnIndex
                                        ("proof_photo")));
//                                Log.v("proof_photo", statusUndeliver.getString(statusUndeliver.getColumnIndex
//                                        ("proof_photo")));
                                File f = new File(file_path, statusUndeliver.getString(statusUndeliver.getColumnIndex
                                        ("proof_photo")));

                                try {
                                    b = BitmapFactory.decodeStream(new FileInputStream(f));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                imageRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.main_bg));
                                BitmapDrawable background = new BitmapDrawable(b);
                                imageRoot.setBackgroundDrawable(background);
                                imageRoot.setEnabled(true);
                                imageRoot.setClickable(true);
                                remarksRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.main_bg));
                                remarksRoot.setEnabled(true);
                                remarksRoot.setClickable(true);
                                imag_re_pic.setVisibility(View.INVISIBLE);
                                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.remark);
                            } else {
                                if (!statusUndeliver.getString(statusUndeliver.getColumnIndex("reason")).equals("")) {
                                    Constants.UNDELIVERED_TITLE = getResources().getString(R.string.image);
                                    imageCapture();
                                }

                            }

                            if (!statusUndeliver.getString(statusUndeliver.getColumnIndex("remarks")).equals("")) {
                                undeliverConfirm.setRemarks(statusUndeliver.getString(statusUndeliver.getColumnIndex("remarks")));
                                remarksRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.main_bg));
                                remarksRoot.setEnabled(true);
                                remarksRoot.setClickable(true);

                                Constants.UNDELIVERED_TITLE = "done";
                                completeRoot.setVisibility(View.VISIBLE);
                                completeRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_main));
                            } else {

                                if (!statusUndeliver.getString(statusUndeliver.getColumnIndex("proof_photo")).equals("")) {
                                    Constants.UNDELIVERED_TITLE = getResources().getString(R.string.remark);
                                    delAlertBox();
                                }
//                                remarksRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_menu));
//                                remarksRoot.setEnabled(false);
//                                remarksRoot.setClickable(false);


//                                Log.v("where_are_we", "customer_name");
                            }

                            statusUndeliver.close();

                        }
                    }
                })
                .show();
    }

    // ************************** Display Username *******************
    public void getprofile() {
        Cursor getreason = database.rawQuery("Select IFNULL(reason,0) AS reason from UndeliveredReasonMaster ", null);
        if (getreason.getCount() > 0) {
            getreason.moveToFirst();

            if (!getreason.isAfterLast()) {
                do {
                    list.add(getreason.getString(getreason.getColumnIndex("reason")));
                    cs = list.toArray(new CharSequence[list.size()]);
                } while (getreason.moveToNext());
            }
            // closing connection
            getreason.close();
        }
    }


    public void imageCapture() {

        Permissions.check(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                "Camera and storage permissions are required because we need to take the proof", new Permissions
                        .Options()
                        .setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Info"),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                       /* Toast.makeText(UndeliveryActivity.this, "Camera+Storage granted",
                                Toast.LENGTH_SHORT).show();
*/
                        imageCapture = "REIM";

                    /*    cameraImageCapture(UNDELIVERED_PROOF, "Undelivered Proof", UN_DELIVERY, undeliverConfirm
                                .getProofPhoto(), file_path + undeliverConfirm.getProofPhoto());*/
                        cameraImageCapture(UNDELIVERED_PROOF, getString(R.string.undelivered_proof), UN_DELIVERY, undeliverConfirm
                                .getProofPhoto(), file_path + undeliverConfirm.getProofPhoto());
                        // cameraImageCapture(UNDELIVERED_PROOF);

                        //do your task
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Camera+Storage Denied:\n",
                                Toast.LENGTH_SHORT).show();
                    }

                });




    /*   camera = new Camera.Builder()
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)
                .build(UndeliveryActivity.this);
        try {
            camera.takePicture();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    public void CreateAlertDialogWithRadioButtonGroup() {
        reasonRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
        AlertDialog.Builder builder = new AlertDialog.Builder(UndeliveryActivity.this);
        builder.setTitle("Select from below list");


        builder.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

//                Log.e("ITEMCLIKED", String.valueOf(cs[item]));
//                Log.e("ITEMCLIKED", String.valueOf(cs));
                undeliverConfirm.setReason(String.valueOf(cs[item]));
                imageRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                imageRoot.setClickable(true);
                imageRoot.setEnabled(true);
                imageCapture();

                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }

  /*  public void cameraImageCapture(int req_code) {
        Intent i = new Intent(this, CameraTestActivity.class);

        if (req_code == 103) {
            cam_title = "Undelivered Proof";
            if (!undeliverConfirm.getProofPhoto().equals("")) {
                img_path = undeliverConfirm.getProofPhoto().toString();
            } else {
                img_path = "";
            }
        }
        i.putExtra("act_title", cam_title);
        i.putExtra("image_path", img_path);
        i.putExtra("shipment_num", shipmentNumber);
        startActivityForResult(i, req_code);
    }
*/


    public void cameraImageCapture(final int req_code, final String headName, final String fileName, final String
            prooff, final String filePath) {

        Permissions.check(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                "Camera and storage permissions are required because we need to take the proof", new Permissions
                        .Options()
                        .setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Info"),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        if (!prooff.equals("")) {
                            Intent intent = new Intent(UndeliveryActivity.this, PreviewActivity.class);
                            intent.putExtra(FILE_PATH_ARG, filePath);
                            intent.putExtra("code", String.valueOf(req_code));
                            intent.putExtra("PreviewActivity", "PreviewStatus");
                            intent.putExtra("heading", headName);
                            startActivityForResult(intent, req_code);
                        } else {
                            Intent intent = new Intent(UndeliveryActivity.this, CameraFragmentMainActivity.class);
                            intent.putExtra("fileName", fileName);
                            intent.putExtra("shipmentId", shipmentNumber);
                            intent.putExtra("heading", headName);
                            startActivityForResult(intent, req_code);
                        }
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Camera+Storage Denied:\n",
                                Toast.LENGTH_SHORT).show();
                    }

                });


    }

    //********************************************* Cammara open **********************************//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());

        if (requestCode == UNDELIVERED_PROOF && imageCapture.equals("REIM")) {
            if (resultCode == Activity.RESULT_OK) {
                Intent i = getIntent();
                String responseCode = data.getStringExtra(RESPONSE_CODE_ARG);
                String retakes = data.getStringExtra("retake");
                if (responseCode.equalsIgnoreCase("900")) {

                    String imagePath = data.getStringExtra(FILE_PATH_ARG);
                    Log.v("onActivityResult", imagePath);
                    // String imagePath = data.getStringExtra("camera_data");
//                Log.v("imagePath", imagePath);
                    String[] parts = imagePath.split("/");
                    if (imagePath != null) {
//                    Log.v("out_here", "CUSTOMER_DELIVERY_CODE:" + Constants.DELIVERED_TITLE);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                        Glide
                                .with(getApplicationContext())
                                .load(imagePath)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>(300, 300) {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                        Drawable dr = new BitmapDrawable(resource);
                                        imageRoot.setBackgroundDrawable(dr);
                                    }
                                });

//                    ll_cust_deli.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        String lastOne = parts[parts.length - 1];
                        undeliverConfirm.setProofPhoto(lastOne);
//                    Log.v("setDeliveryProof", lastOne);
                        imag_re_pic.setVisibility(View.INVISIBLE);
                        remarksRoot.setEnabled(true);
                        remarksRoot.setClickable(true);
                        if (undeliverConfirm.getRemarks().equals("")) {
                            remarksRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                        }

                        insertUndeliveryInfo();
                        imag_re_pic.setVisibility(View.INVISIBLE);
                        Constants.UNDELIVERED_TITLE = getResources().getString(R.string.remark);
                        if (!undeliverConfirm.getProofPhoto().equals("")) {
                            String deliveryDetailsupdate = "UPDATE UndeliveredConfirmation set proof_photo = '" + undeliverConfirm.getProofPhoto() + "' where shipmentnumber = '" + shipmentNumber + "' ";
                            database.execSQL(deliveryDetailsupdate);
                        }

                        if (retakes != null) {
                            if (retakes.equalsIgnoreCase("emptyRetake")) {
                                delAlertBox();
                            } else if (retakes.equalsIgnoreCase("picRetake")) {

                            }
                        } else {
                            delAlertBox();
                        }

                    } else {
//                    signatureImage.setImageResource(R.drawable.camera);
                    }
                } else if (responseCode.equalsIgnoreCase("901")) {

                    if (undeliverConfirm.getProofPhoto().equals(""))
                        customerActivity(UNDELIVERED_PROOF, "emptyRetake", "UnDelivery Proof", UN_DELIVERY);
                    else
                        customerActivity(UNDELIVERED_PROOF, "picRetake", "UnDelivery Proof", UN_DELIVERY);

                } else if (responseCode.equalsIgnoreCase("902")) {
                    if (undeliverConfirm.getProofPhoto().equals(""))
                        customerActivity(UNDELIVERED_PROOF, "emptyRetake", "UnDelivery Proof", UN_DELIVERY);
                    else
                        customerActivity(UNDELIVERED_PROOF, "picRetake", "UnDelivery Proof", UN_DELIVERY);
                }





               /* String imagePath = data.getStringExtra("camera_data");
//                Log.v("imagePath", imagePath);
                String[] parts = imagePath.split("/");
                if (imagePath != null) {

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    imageRoot.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    String lastOne = parts[parts.length - 1];
                    undeliverConfirm.setProofPhoto(lastOne);
//                    remarksRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                    remarksRoot.setEnabled(true);
                    remarksRoot.setClickable(true);
                    if (undeliverConfirm.getRemarks().equals("")) {
                        remarksRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                    }
                    insertUndeliveryInfo();
                    imag_re_pic.setVisibility(View.INVISIBLE);
                    Constants.UNDELIVERED_TITLE = getResources().getString(R.string.remark);
                    if (!undeliverConfirm.getProofPhoto().equals("")) {
                        String deliveryDetailsupdate = "UPDATE UndeliveredConfirmation set proof_photo = '" + undeliverConfirm.getProofPhoto() + "' where shipmentnumber = '" + shipmentNumber + "' ";
                        database.execSQL(deliveryDetailsupdate);
                    }
                    delAlertBox();*/

            } else {
//                    signatureImage.setImageResource(R.drawable.camera);
            }

            if (resultCode == Activity.RESULT_CANCELED) {

            }


        } else if (requestCode == LOCATION_ADDRESS_CODE) {
            lat = Double.longBitsToDouble(AppController.getLongPreference(this, Constants.UN_DEL_LATITUDE, -1));
            lang = Double.longBitsToDouble(AppController.getLongPreference(this, Constants.UN_DEL_LONGITUDE, -1));
            String lati = String.valueOf(lat);
            String longi = String.valueOf(lang);
            if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lang)) {
                getAddresss(lat, lang);
                if (currentLocation != null)
                    locationFindAddress.setText(currentLocation);
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    buildGoogleApiClient();
                    manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    createLocationRequest();
                    getLocation();
//                    undeliverySuccessMsg();
                    break;
                case Activity.RESULT_CANCELED:
                    Utils.AlertDialogCancel(UndeliveryActivity.this, getResources().getString(R.string.Location_warning),  getResources().getString(R.string.Location_enabled), getResources().getString(R.string.ok),  getResources().getString(R.string.dialog_cancel));
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
                            getLocation();
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }


    private void customerActivity(int customerUnDeliveryCode, String emptyRetake, String headingName, String fileName) {
        Intent intent = new Intent(UndeliveryActivity.this, CameraFragmentMainActivity.class);
        intent.putExtra("fileName", fileName);
        intent.putExtra("retake", emptyRetake);
        intent.putExtra("shipmentId", shipmentNumber);
        intent.putExtra("heading", headingName);
        startActivityForResult(intent, customerUnDeliveryCode);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());
        if (requestCode == Camera.REQUEST_TAKE_PHOTO && imageCapture.equals("REIM") ) {
||||||| .r606
        if (requestCode == Camera.REQUEST_TAKE_PHOTO && imageCapture.equals("REIM") ) {
=======
        if (requestCode == Camera.REQUEST_TAKE_PHOTO && imageCapture.equals("REIM")) {
>>>>>>> .r613
            Bitmap bitmap = camera.getCameraBitmap();
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                imag_cust_address.setImageBitmap(bitmap);
//                applyWaterMarkEffect(bitmap, "Water mark text", 200, 200, Color.GREEN, 80, 24, false);
                bitmap = mark(bitmap, currentDateandTime);
                imageRoot.setBackgroundDrawable(new BitmapDrawable(bitmap));

                Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                File finalFile = new File(getRealPathFromURI(tempUri));
                String[] parts = String.valueOf(finalFile).split("/");
                undeliverConfirm.setProofPhoto(parts[6]);
                remarksRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                remarksRoot.setEnabled(true);
                remarksRoot.setClickable(true);
                delAlertBox();
                imag_re_pic.setVisibility(View.INVISIBLE);
                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.remark);
            } else {
//                Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                // String result=data.getStringExtra("result");
                EnableGPSAutoMatically();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == LOCATION_ADDRESS_CODE) {
            lat = Double.longBitsToDouble(AppController.getLongPreference(this, Constants.UN_DEL_LATITUDE, -1));
            lang = Double.longBitsToDouble(AppController.getLongPreference(this, Constants.UN_DEL_LONGITUDE, -1));
            String lati = String.valueOf(lat);
            String longi = String.valueOf(lang);
            if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lang)) {
                getAddresss(lat, lang);
                if (currentLocation != null)
                    locationFindAddress.setText(currentLocation);
            }
        } else {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
                            getLocation();
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }*/


    /**
     * This is for alert box
     */
    private void delAlertBox() {
//        Log.v("isremark_empty", Constants.UNDELIVERED_TITLE);
        final Dialog elAlertdialog = new Dialog(UndeliveryActivity.this);
        elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        elAlertdialog.setContentView(R.layout.deliver_alerts);
        elAlertdialog.show();


        textPhoneNumber = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_phone);
        textpincode = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_pincode);
        textAddress = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_add);
        textLandmark = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_landmark);
        textAmount = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_amount);
        redirectundel = (AppCompatCheckBox) elAlertdialog.findViewById(R.id.redirect);
        mapLayout = (LinearLayout) elAlertdialog.findViewById(R.id.map_layout);


        custPhoneNo = (AppCompatEditText) elAlertdialog.findViewById(R.id.input_phone);
        shippingAddress = (AppCompatEditText) elAlertdialog.findViewById(R.id.add);
        shipPincode = (AppCompatEditText) elAlertdialog.findViewById(R.id.input_pincode);
        custAmount = (AppCompatEditText) elAlertdialog.findViewById(R.id.input_amount);
        landMark = (AppCompatEditText) elAlertdialog.findViewById(R.id.input_landmark);

        textPhoneNumber.setVisibility(View.GONE);
        textpincode.setVisibility(View.GONE);
        textAddress.setVisibility(View.GONE);
        textAmount.setVisibility(View.GONE);
        redirectundel.setVisibility(View.GONE);
        custPhoneNo.setVisibility(View.GONE);
        shippingAddress.setVisibility(View.GONE);
        shipPincode.setVisibility(View.GONE);
        custAmount.setVisibility(View.GONE);
        textLandmark.setVisibility(View.GONE);
        landMark.setVisibility(View.GONE);
        mapLayout.setVisibility(View.GONE);


        textInputLayout = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);
        textInput = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
        AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
        AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
        textInput.setOnFocusChangeListener(this);
        textInputLayout.setHint(getResources().getString(R.string.remark));
        if (Constants.UNDELIVERED_TITLE.equalsIgnoreCase(getResources().getString(R.string.remark)) &&
                !undeliverConfirm.getRemarks().equals("")) {
            textInput.setText(undeliverConfirm.getRemarks());

        }

        elAlertdialog.setCanceledOnTouchOutside(false);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remarks = textInput.getText().toString();
                remarks = remarks.replace("'", "");
                if (TextUtils.isEmpty(remarks.trim())) {
//                    Log.v("isremark_empty", "yes");
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(getResources().getString(R.string.unDelReason));
                    textInputLayout.requestFocus();
                } else {
//                    Log.v("isremark_empty", "no");
                    undeliverConfirm.setRemarks(remarks);
                    completeRoot.setVisibility(View.VISIBLE);
                    if (!undeliverConfirm.getRemarks().equals("")) {
                        String deliveryDetailsupdate = "UPDATE UndeliveredConfirmation set remarks = '" + undeliverConfirm.getRemarks() + "' where shipmentnumber = '" + shipmentNumber + "' ";
                        database.execSQL(deliveryDetailsupdate);
                    }
                    completeRoot.setBackgroundColor(ContextCompat.getColor(UndeliveryActivity.this, R.color.bg_main));
                    elAlertdialog.dismiss();
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elAlertdialog.dismiss();
            }
        });
    }

    public static Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(18);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);
        canvas.drawText(watermark, 20, 25, paint);
        return result;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.name:
                TextFieldOnFocus(hasFocus);
                break;
            case R.id.input_undel_address:
                TextAddressOnFocus(hasFocus);
                break;

        }
    }

    /**
     * set on Focus on Phone Edit text
     *
     * @param hasFocus focus of view
     */
    private void TextFieldOnFocus(boolean hasFocus) {
        if (!hasFocus && !TextUtils.isEmpty(textInput.getText().toString())) {
            textInput.setText(strop.titleize(textInput.getText().toString()));
            textInputLayout.setErrorEnabled(false);
        }
    }

    /**
     * set on Focus on Phone Edit text
     *
     * @param hasFocus focus of view
     */
    private void TextAddressOnFocus(boolean hasFocus) {
        if (!hasFocus && !TextUtils.isEmpty(txtAdd.getText().toString())) {
            txtAdd.setText(strop.titleize(txtAdd.getText().toString()));
            inputAdd.setErrorEnabled(false);
        }
    }

    public Boolean insertUndeliveryInfo() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTimeString = format.format(new Date());
        Cursor count = database.rawQuery("select * from UndeliveredConfirmation where shipmentnumber='"
                + shipmentNumber + "'", null);
        if (count.getCount() == 0) {
//            Log.v("get_reason_insert", undeliverConfirm.getReason());
            String deliverDetailsInsert = "Insert into UndeliveredConfirmation (shipmentnumber,remarks," +
                    "proof_photo," +
                    "reason,sync_status, latitude, longitude,shipment_address,redirect, created_at,reason_id)" + " VALUES ('" + shipmentNumber +
                    "','" + undeliverConfirm.getRemarks() + "','" +
                    undeliverConfirm.getProofPhoto() + "','" + undeliverConfirm.getReason() + "' ,'"
                    + "P" + "','" + undeliverConfirm.getLatitude() + "','" + undeliverConfirm.getLongtitude() + "','" +
                    undeliverConfirm.getAddress
                            () + "','" +
                    undeliverConfirm.getRedirect() + "','" + formattedDate + "',"+rid+")";
            database.execSQL(deliverDetailsInsert);
        } else if (count.getCount() > 0) {
//            Log.v("onBackPressed",deliveryConfirm.getIdProff()+"-"+deliveryConfirm.getDeliveryProof()+"-"+deliveryConfirm.getInvoiceProof());
            String deliveryDetailsupdate = "UPDATE UndeliveredConfirmation set remarks='" + undeliverConfirm.getRemarks() +
                    "'," + "proof_photo='" + undeliverConfirm.getProofPhoto() + "',reason='"
                    + undeliverConfirm.getReason() + "', shipment_address='"
                    + undeliverConfirm.getAddress() + "',redirect='"
                    + undeliverConfirm.getRedirect() + "', " + "sync_status='" + "P"
                    + "',latitude='" + undeliverConfirm.getLatitude() + "',longitude='" + undeliverConfirm.getLongtitude() + "'" +
                    ", created_at = '" + formattedDate + "', reason_id = "+rid+" " +
                    "where shipmentnumber ='" + shipmentNumber + "' ";
            database.execSQL(deliveryDetailsupdate);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTimeString = format.format(new Date());
        Cursor count = database.rawQuery("select * from UndeliveredConfirmation where shipmentnumber='"
                + shipmentNumber + "'", null);
        if (count.getCount() == 0) {
            String deliverDetailsInsert = "Insert into UndeliveredConfirmation (shipmentnumber,remarks," +
                    "proof_photo," +
                    "reason,sync_status, latitude, longitude,shipment_address,redirect,created_at)" + " VALUES ('" + shipmentNumber +
                    "','" + undeliverConfirm.getRemarks() + "','" +
                    undeliverConfirm.getProofPhoto() + "','" + undeliverConfirm.getReason() + "' ,'"
                    + "P" + "','" + undeliverConfirm.getLatitude() + "','" + undeliverConfirm.getLongtitude() + "','" +
                    undeliverConfirm.getAddress
                            () + "'," +
                    "'" +
                    undeliverConfirm.getRedirect() + "', '" + currentDateTimeString + "')";
            database.execSQL(deliverDetailsInsert);
        } else if (count.getCount() > 0) {
//            Log.v("onBackPressed",deliveryConfirm.getIdProff()+"-"+deliveryConfirm.getDeliveryProof()+"-"+deliveryConfirm.getInvoiceProof());
            String deliveryDetailsupdate = "UPDATE UndeliveredConfirmation set remarks='" + undeliverConfirm.getRemarks() +
                    "'," + "proof_photo='" + undeliverConfirm.getProofPhoto() + "',reason='"
                    + undeliverConfirm.getReason() + "', shipment_address='"
                    + undeliverConfirm.getAddress() + "',redirect='"
                    + undeliverConfirm.getRedirect() + "', " + "sync_status='" + "P"
                    + "',latitude='" + undeliverConfirm.getLatitude() + "',longitude='" + undeliverConfirm.getLongtitude() +
                    "', created_at = '" + currentDateTimeString + "' " + "where shipmentnumber ='" + shipmentNumber + "' ";
            database.execSQL(deliveryDetailsupdate);
        }
    }

    public void getCustValue() {
        Cursor count = database.rawQuery("select * from UndeliveredConfirmation where shipmentnumber='"
                + shipmentNumber + "'", null);
        if (count.getCount() > 0) {
            count.moveToFirst();

//            Log.v("getphno", "--" + count.getString(count.getColumnIndex("reason")));
//            Log.v("getphno", "--" + count.getString(count.getColumnIndex("proof_photo")));
        }
    }

   /* public void schemeDetailAlert(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.undelivered_alert, null);
        dialogBuilder.setView(dialogView);
        sp_reason = (Spinner) dialogView.findViewById(R.id.sp_reason);
        bt_back = (AppCompatButton) dialogView.findViewById(R.id.bt_back);
        bt_submit = (AppCompatButton) dialogView.findViewById(R.id.bt_submit);
        ArrayAdapter<String> adapterBusinessType = null;

        Log.v("getReason:",Constants.UNDELIVERED_TITLE);

         my_Adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                my_array);
        sp_reason.setAdapter(my_Adapter);

        if (Constants.UNDELIVERED_TITLE.equalsIgnoreCase(getResources().getString(R.string.reason)) &&
                !undeliverConfirm.getReason().equals("")) {
            Log.v("getReason:",undeliverConfirm.getReason());
//            textInput.setText(undeliverConfirm.getReason());
            int position = my_Adapter.getPosition(undeliverConfirm.getReason());
            sp_reason.setSelection(position);
        }

//        int position = my_Adapter.getPosition("Delay in Delivery");
//        sp_reason.setSelection(position);
   *//*     Cursor getSchemeValue = database.rawQuery("select * from UndeliveredReasonMaster  ",null);
        getSchemeValue.moveToFirst();
        if(getSchemeValue.getCount() > 0){
            getSchemeValue.moveToFirst();
            while (!getSchemeValue.isAfterLast()) {
                str_reason = getSchemeValue.getString(getSchemeValue.getColumnIndex("reason"));
//                Log.v("schemeid", str_reason);
//                my_array.add(str_reason);
//                ArrayAdapter<String> ad = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, str_reason);  // pass List to ArrayAdapter
////                my_array.add();
//                sp_reason.setAdapter(ad);
//                sp_reason.setAdapter(adapterqty);
                getSchemeValue.moveToNext();
            }

        }*//*

        sp_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {

                String item = adapter.getItemAtPosition(position).toString();

                sp_reason.setSelection(position);
//                undeliverConfirm.setReason(item);
                undeliverConfirm.setReason(item);

                Toast.makeText(getApplicationContext(),
                        "Selected Spinner : " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        bt_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.image);
                imageCapture();
                alertDialog.dismiss();
            }
        });
        bt_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }*/

    public void reasonAlertBox() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.undelivered_alert, null);
        dialogBuilder.setView(dialogView);
        sp_reason = (Spinner) dialogView.findViewById(R.id.sp_reason);
        bt_back = (AppCompatButton) dialogView.findViewById(R.id.bt_back);
        bt_submit = (AppCompatButton) dialogView.findViewById(R.id.bt_submit);
        mapRoot = (LinearLayout) dialogView.findViewById(R.id.map_layout);

        /*for get the current location*/
        applyLocAdd = (AppCompatButton) dialogView.findViewById(R.id.loc_address);
        gMap = (AppCompatImageView) dialogView.findViewById(R.id.google_map);
        locationFindAddress = (TextView) dialogView.findViewById(R.id.address);


        txtAdd = (AppCompatEditText) dialogView.findViewById(R.id.input_undel_address);
        inputAdd = (TextInputLayout) dialogView.findViewById(R.id.txt_undel_add);
        redirect = (AppCompatCheckBox) dialogView.findViewById(R.id.redirect);
        txtAdd.setOnFocusChangeListener(this);

        dialogBuilder.setCancelable(false);

        gMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View hideView = getCurrentFocus();
                if (hideView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if (locationFindAddress.equals(getResources().getString(R.string.get_location)) || !Utils
                        .checkNetConnection(view.getContext())) {
                    Logger.showShortMessage(view.getContext(), "Satellite singals are poor please turn on the internet " +
                            "connection");

                } else {
                    Intent intent = new Intent(UndeliveryActivity.this, MapsActivity.class);
                    intent.putExtra("MapshipAddValue", "undelivery");
                    startActivityForResult(intent, LOCATION_ADDRESS_CODE);
                }


            }
        });

        applyLocAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentLocation != null || postalCode != null) {


                    txtAdd.setText("");


                    txtAdd.setText(currentLocation);
                } else {
                    Logger.showShortMessage(UndeliveryActivity.this, "Location is empty");
                }
            }
        });

        /**
         * Get the location of current addresskyc
         */
        lat = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_LATITUDE, -1));
        lang = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_LONGITUDE, -1));
        String lati = String.valueOf(lat);
        String longi = String.valueOf(lang);


        if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lang)) {
            getAddresss(lat, lang);
        } else {
            //showToast("Couldn't get the location. Make sure location is enabled on the device");
        }

        if (currentLocation != null)
            locationFindAddress.setText(currentLocation);


        redirect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                                    if (redirect.isChecked()) {
                                                        inputAdd.setVisibility(View.VISIBLE);
                                                        mapRoot.setVisibility(View.VISIBLE);
                                                        if (currentLocation != null)
                                                            locationFindAddress.setText(currentLocation);
                                                        undeliverConfirm.setRedirect("1");
                                                    } else {
                                                        inputAdd.setVisibility(View.GONE);
                                                        mapRoot.setVisibility(View.GONE);
                                                        undeliverConfirm.setRedirect("0");
                                                    }
                                                }
                                            }
        );


//        Log.v("getReason",Constants.UNDELIVERED_TITLE);
        my_Adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item,
                my_array);
        sp_reason.setAdapter(my_Adapter);

        if (Constants.UNDELIVERED_TITLE.equalsIgnoreCase(getResources().getString(R.string.reason)) &&
                !undeliverConfirm.getReason().equals("")) {
//            Log.v("getReason", "--" + undeliverConfirm.getReason());
            int position = my_Adapter.getPosition(undeliverConfirm.getReason());
            sp_reason.setSelection(position);
        }

        if (undeliverConfirm.getRedirect() != null) {
            if (undeliverConfirm.getRedirect().equals("1")) {
                inputAdd.setVisibility(View.VISIBLE);
                redirect.setChecked(true);
                if (undeliverConfirm.getAddress() != null || !undeliverConfirm.getAddress().equals(""))
                    txtAdd.setText(undeliverConfirm.getAddress());

            } else if (undeliverConfirm.getRedirect().equals("0")) {
                inputAdd.setVisibility(View.GONE);
                redirect.setChecked(false);
            }
        }

//        int position = my_Adapter.getPosition("Delay in Delivery");
//        sp_reason.setSelection(position);
      /*  Cursor getSchemeValue = database.rawQuery("select * from UndeliveredReasonMaster  ",null);
        getSchemeValue.moveToFirst();
        if(getSchemeValue.getCount() > 0){
            getSchemeValue.moveToFirst();
            while (!getSchemeValue.isAfterLast()) {
                str_reason = getSchemeValue.getString(getSchemeValue.getColumnIndex("reason"));
//                Log.v("schemeid", str_reason);
//                my_array.add(str_reason);
//                ArrayAdapter<String> ad = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, str_reason);  // pass List to ArrayAdapter
////                my_array.add();
//                sp_reason.setAdapter(ad);
//                sp_reason.setAdapter(adapterqty);
                getSchemeValue.moveToNext();
            }

        }*/

        sp_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {


                String item = adapter.getItemAtPosition(position).toString();

                sp_reason.setSelection(position);
//                if (!item.equals("Select Reason")) {
                if (!item.equals(getString(R.string.select_reason_def))) {

                    Cursor getreasondetails = database.rawQuery("SELECT * FROM ReasonMaster where reason = '"+item+"' AND reason_for = 2  ", null);
                    getreasondetails.moveToFirst();
                    if(getreasondetails.getCount() > 0){
                        rid = getreasondetails.getString(getreasondetails.getColumnIndex("rid"));
                        Log.v("getreasondetails","- "+ rid);
                    }

                    undeliverConfirm.setReason(item);
                    bt_submit.setAlpha(1);
                    bt_submit.setEnabled(true);

                } else {
                    bt_submit.setAlpha(0.4F);
                    bt_submit.setEnabled(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.undeliver_Reason), Toast.LENGTH_LONG).show();
                }

//                Log.v("Selected_Spinner", item);

//                Toast.makeText(getApplicationContext(),
//                        "Selected Spinner : " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.UNDELIVERED_TITLE = getResources().getString(R.string.image);
                strAdd = txtAdd.getText().toString();


                if (!undeliverConfirm.getReason().equals("")) {
                    reasonRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                    String deliveryDetailsupdate = "UPDATE UndeliveredConfirmation set reason = '" + undeliverConfirm.getReason() + "', reason_id = "+rid+" where shipmentnumber = '" + shipmentNumber + "' ";
                    database.execSQL(deliveryDetailsupdate);
                    Log.v("deliveryDetails","- "+rid+"- "+ deliveryDetailsupdate);
                }

                if (!undeliverConfirm.getRedirect().equals("")) {
                    if (undeliverConfirm.getRedirect().equals("1")) {
                        if (TextUtils.isEmpty(strAdd.trim())) {
                            inputAdd.setErrorEnabled(true);
                            inputAdd.setError(getResources().getString(R.string.valid_add));
                            inputAdd.requestFocus();
                        } else {
                            undeliverConfirm.setAddress(strAdd);
                            if (undeliverConfirm.getProofPhoto().equals("")) {
                                imageRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                                imageRoot.setEnabled(true);
                                imageRoot.setClickable(true);
                            }
                            imageCapture();
                            alertDialog.dismiss();
                        }

                    } else if (undeliverConfirm.getRedirect().equals("0")) {
//                        imageRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                        if (undeliverConfirm.getProofPhoto().equals("")) {
                            imageRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                            imageRoot.setEnabled(true);
                            imageRoot.setClickable(true);
                        }
                        imageCapture();
                        alertDialog.dismiss();
                    }
                } else {
//                    imageRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                    if (undeliverConfirm.getProofPhoto().equals("")) {
                        imageRoot.setBackgroundColor(getResources().getColor(R.color.main_bg));
                        imageRoot.setEnabled(true);
                        imageRoot.setClickable(true);

                    }
                    imageCapture();
                    alertDialog.dismiss();
                }


            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                undeliverConfirm.setRedirect("0");
            }
        });
        alertDialog.show();

    }


    public ArrayList<String> getTableValues() {
//        my_array.add("Select Reason");
        my_array.add(getString(R.string.select_reason_def));
        try {
            Cursor getSchemeValue;
//            Cursor getSchemeValue = database.rawQuery("select * from UndeliveredReasonMaster  ", null);
            if(!aadhaarEnabled.equals("0")){
                getSchemeValue = database.rawQuery("select * from ReasonMaster where reason_type = '8'  ", null);
            }else{
            getSchemeValue = database.rawQuery("select * from ReasonMaster where reason_for = '2' AND reason_type != '8' ", null);
            }
            System.out.println("COUNT : " + getSchemeValue.getCount());
            if (getSchemeValue.moveToFirst()) {
                do {
                    String NAME = getSchemeValue.getString(getSchemeValue.getColumnIndex("reason"));
//                    Log.v("getSchemeValue",NAME);
//                    my_array.add(NAME);
                   if(user_language.equals("english")){
                       my_array.add(NAME);
                   }
                   else if(user_language.equals("tamil")) {
                       String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("tamil"));
                       if (language_json != null && !language_json.equals("null")){
                           Log.v("language_reason","-- "+language_json);
                           try {
                               JSONObject jObject = new JSONObject(language_json);
                               String reason = jObject.getString("reason");

                               my_array.add(reason);
                           } catch (JSONException e) {
                               e.getStackTrace();
                           }
                   }else{
                           Log.v("language_reason1","-- "+language_json);
                           my_array.add(NAME);
                       }
                    }  else if(user_language.equals("hindi")){
                        String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("hindi"));
                       if (language_json != null && !language_json.equals("null")){
                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String reason = jObject.getString("reason");

                            my_array.add(reason);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                       }else{
                           my_array.add(NAME);
                       }
                    }else if(user_language.equals("bengali")) {
                       String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("bengali"));
                       if (language_json != null && !language_json.equals("null")){
                           try {
                               JSONObject jObject = new JSONObject(language_json);
                               String reason = jObject.getString("reason");

                               my_array.add(reason);
                           } catch (JSONException e) {
                               e.getStackTrace();
                           }
                   }else{
                           my_array.add(NAME);
                       }
                    }
                    else if(user_language.equals("marathi")) {
                       String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("marathi"));
                       if (language_json != null && !language_json.equals("null")){
                           try {
                               JSONObject jObject = new JSONObject(language_json);
                               String reason = jObject.getString("reason");

                               my_array.add(reason);
                           } catch (JSONException e) {
                               e.getStackTrace();
                           }
                   }else{
                           my_array.add(NAME);
                       }
                    }
                    else if(user_language.equals("punjabi")) {
                       String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("punjabi"));
                       if (language_json != null && !language_json.equals("null")){
                           try {
                               JSONObject jObject = new JSONObject(language_json);
                               String reason = jObject.getString("reason");

                               my_array.add(reason);
                           } catch (JSONException e) {
                               e.getStackTrace();
                           }
                   }else{
                           my_array.add(NAME);
                       }
                    }else if(user_language.equals("odia")) {
                       String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("orissa"));
                       if (language_json != null && !language_json.equals("null")){
                           try {
                               JSONObject jObject = new JSONObject(language_json);
                               String reason = jObject.getString("reason");

                               my_array.add(reason);
                           } catch (JSONException e) {
                               e.getStackTrace();
                           }
                   }else{
                           my_array.add(NAME);
                       }
                    }else if(user_language.equals("telugu")) {
                       String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("telugu"));
                       if (language_json != null && !language_json.equals("null")){
                           try {
                               JSONObject jObject = new JSONObject(language_json);
                               String reason = jObject.getString("reason");

                               my_array.add(reason);
                           } catch (JSONException e) {
                               e.getStackTrace();
                           }
                   }else{
                           my_array.add(NAME);
                       }
                    }else if(user_language.equals("kannada")) {
                       String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("kannada"));
                       Log.v("kannada","-- "+language_json);
                       if (language_json != null && !language_json.equals("null")){
                           try {
                               JSONObject jObject = new JSONObject(language_json);
                               String reason = jObject.getString("reason");

                               my_array.add(reason);
                           } catch (JSONException e) {
                               e.getStackTrace();
                           }
                   }else{
                           my_array.add(NAME);
                       }
                    }else if(user_language.equals("assamese")) {
                       String language_json = getSchemeValue.getString(getSchemeValue.getColumnIndex("assam"));
                       if (language_json != null && !language_json.equals("null")){
                           try {
                               JSONObject jObject = new JSONObject(language_json);
                               String reason = jObject.getString("reason");

                               my_array.add(reason);
                           } catch (JSONException e) {
                               e.getStackTrace();
                           }
                   }else{
                           my_array.add(NAME);
                       }
                    }else{
                       my_array.add(NAME);
                   }



                } while (getSchemeValue.moveToNext());
            }
            getSchemeValue.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error encountered.",
                    Toast.LENGTH_LONG);
        }
        return my_array;
    }


    /**
     * Gps will be enable automatically
     */
    private void EnableGPSAutoMatically() {
        googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(UndeliveryActivity.this)
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

                            int permissionLocation = ContextCompat
                                    .checkSelfPermission(UndeliveryActivity.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

                                mLastLocation = LocationServices.FusedLocationApi
                                        .getLastLocation(googleApiClient);
                            }
                            buildGoogleApiClient();
                            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            createLocationRequest();
                            getLocation();
//                            undeliverySuccessMsg();
                            //toast("Success");
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                // Ask to turn on GPS automatically
                                status.startResolutionForResult(UndeliveryActivity.this,
                                        REQUEST_CHECK_SETTINGS_GPS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            Logger.logInfo("GPS is not on");
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.

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


    public void undeliverySuccessMsg() {
        if (latitude_user != 0.0 && longitude_user != 0.0) {
            latitude = String.valueOf(latitude_user);
            longitude = String.valueOf(longitude_user);
            undeliverConfirm.setLatitude(latitude);
            undeliverConfirm.setLongtitude(longitude);
        }
        if (Utils.checkNetworkAndShowDialog(UndeliveryActivity.this)) {

            lat = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_CUR_LATITUDE, -1));
            lang = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_CUR_LONGITUDE, -1));


            unDelat = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_LATITUDE, -1));
            unDelang = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_LONGITUDE, -1));
            String lati = String.valueOf(lat);
            String longi = String.valueOf(lang);
//            Log.v("latlong", lati + " " + longi);


            if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lang)) {
                if (updateLocation(lati, longi)) {
                    uploadUndelivered();
//                    uploadImage();
                } else {
                    Utils.AlertDialogCancel(UndeliveryActivity.this, "Location Service Warning", "Location Not Updated", "OK", "Cancel");
                }
            } else if (String.valueOf(unDelat) != null && String.valueOf(unDelat) != null && !Double.isNaN
                    (unDelat) && !Double.isNaN(unDelang)) {
                if (updateLocation(String.valueOf(unDelat), String.valueOf(unDelang))) {
                    uploadUndelivered();
//                    uploadImage();
                } else {
                    Utils.AlertDialogCancel(UndeliveryActivity.this, "Location Service Warning", "Location Not Updated", "OK", "Cancel");
                }
            } else {
                Utils.AlertDialogCancel(UndeliveryActivity.this, "Location Serivce Warning", "Couldn't get the location at the moment, Please try again later ", "OK", "Cancle");
            }

        } else {


            lat = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this,
                    Constants
                            .UN_DEL_CUR_LATITUDE, -1));
            lang = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_CUR_LONGITUDE, -1));


            unDelat = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_LATITUDE, -1));
            unDelang = Double.longBitsToDouble(AppController.getLongPreference(UndeliveryActivity.this, Constants.UN_DEL_LONGITUDE, -1));
            String lati = String.valueOf(lat);
            String longi = String.valueOf(lang);
//            Log.v("latlong", lati + " " + longi);

            if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lang)) {
                if (updateLocation(lati, longi)) {
                    database.execSQL("UPDATE orderheader set sync_status = 'C' where Shipment_Number ='" +
                            shipmentNumber + "' ");

                    alertDialogMsgOffline(UndeliveryActivity.this, getResources().getString(R.string.undeli_title), UndeliveryActivity.this.getString(R.string.delivery_offline), getResources().getString(R.string.ok));
                } else {
                    Utils.AlertDialogCancel(UndeliveryActivity.this, getResources().getString(R.string.Location_warning),  getResources().getString(R.string.Location_not_up), getResources().getString(R.string.ok), getResources().getString(R.string.cancel_label));
                }
            } else if (String.valueOf(unDelat) != null && String.valueOf(unDelat) != null && !Double.isNaN
                    (unDelat) && !Double.isNaN(unDelang)) {
                if (updateLocation(String.valueOf(unDelat), String.valueOf(unDelang))) {
                    database.execSQL("UPDATE orderheader set sync_status = 'C' where Shipment_Number ='" +
                            shipmentNumber + "' ");
                    alertDialogMsgOffline(UndeliveryActivity.this, getResources().getString(R.string.undeli_title), UndeliveryActivity.this.getString(R.string.delivery_offline), getResources().getString(R.string.ok));
                } else {
                    Utils.AlertDialogCancel(UndeliveryActivity.this, getResources().getString(R.string.Location_warning), getResources().getString(R.string.Location_not_up), getResources().getString(R.string.ok),  getResources().getString(R.string.cancel_label));
                }
            } else {
                Utils.AlertDialogCancel(UndeliveryActivity.this, getResources().getString(R.string.Location_warning), getResources().getString(R.string.Location_warning_error),  getResources().getString(R.string.ok),  getResources().getString(R.string.cancel_label));
            }
        }


    }


    /**
     * Method to display the location on UI
     */

    private void getLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }


    /**
     * Creating google api client object
     */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(UndeliveryActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(UndeliveryActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }


    /**
     * Method to verify google play services on the device
     */

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.device_not_supp), Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }

    /**
     * Get the current location of the addresskyc initialization
     */
    public void getAddresss(double latitude, double longitude) {
        android.location.Address locationAddress;
        locationAddress = getAddress(latitude, longitude);
        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            postalCode = locationAddress.getPostalCode();


            if (!TextUtils.isEmpty(address.trim())) {
                currentLocation = address;

                if (locationFindAddress != null)
                    locationFindAddress.setText(currentLocation);

                String add = locationAddress.getAddressLine(0);


            }
        } else {
//            Logger.showShortMessage(this, "There is no internet connection");
        }
    }

    public android.location.Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<android.location.Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() > 0) {
                return addresses.get(0);
            } else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle arg0) {

//        Log.d("ACTIVITY", "ApiClient: OnConnected");

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

        //
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
            AppController.setLongPreference(this, Constants.UN_DEL_CUR_LATITUDE, Double.doubleToRawLongBits(mLastLocation
                    .getLatitude()));
            AppController.setLongPreference(this, Constants.UN_DEL_CUR_LONGITUDE, Double.doubleToRawLongBits(mLastLocation
                    .getLongitude()));
        }

        /**
         * Get the location of activity
         */
        lat = Double.longBitsToDouble(AppController.getLongPreference(this, Constants.UN_DEL_LATITUDE, -1));
        lang = Double.longBitsToDouble(AppController.getLongPreference(this, Constants.UN_DEL_LONGITUDE, -1));
        String lati = String.valueOf(lat);
        String longi = String.valueOf(lang);
        if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lang)) {
//            Log.e("pref", "pref");
            getAddresss(lat, lang);
            latCheck = lat;
            lonCheck = lang;
            undeliverConfirm.setLatitude(String.valueOf(lat));
            undeliverConfirm.setLatitude(String.valueOf(lang));


        } else {
//            Log.e("natural", "natural");
            if (latitude != 0.0 && langitidue != 0.0) {
                getAddresss(latitude, langitidue);
            } else {
                //Logger.showShortMessage(this, getResources().getString(R.string.gps_signal));
            }
            latCheck = latitude;
            lonCheck = langitidue;

            undeliverConfirm.setLatitude(String.valueOf(latitude));
            undeliverConfirm.setLatitude(String.valueOf(langitidue));
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
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
    public void onLocationChanged(Location location) {

        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            } else if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(20);
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        checkPlayServices();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void updateSyncStatus() {
        if (null != statusSync || statusSync.equalsIgnoreCase("undelivered"))
            database.execSQL("UPDATE orderheader set delivery_status  = 'undelivered' where Shipment_Number ='" +
                    shipmentNumber + "' ");
    }

    public Boolean updateComplete() {

        database.execSQL("UPDATE orderheader set sync_status  = 'C' where Shipment_Number ='" +
                shipmentNumber + "' ");
  /*      database.execSQL("UPDATE orderheader set delivery_status = 'U' where shipmentnumber IN " +
                "((SELECT Shipment_Number FROM orderheader WHERE Shipment_Number = '" + shipmentNumber + "')) ");*/

        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_complete:
                requestLocation();
//                buildGoogleApiClient();
                manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                createLocationRequest();
                undeliverySuccessMsg();
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        Log.e("Onstart", "MainonStart");

        /**
         * This is for tracking the classes when user working onit an app
         */

        /*Get the current activity name*/
        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "1", shipmentNumber);
    }


    @Override
    protected void onStop() {
        super.onStop();
//        Log.e("Onstop", "MainonStop");
        /*Get the current activity name*/
        if(this.mBatInfoReceiver != null) {
            unregisterReceiver(this.mBatInfoReceiver);
        }


        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "0", shipmentNumber);
    }

    private OkHttpClient getRequestHeader() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(8, TimeUnit.SECONDS)
                .connectTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .build();

        return okHttpClient;
    }


    public void uploadUndelivered() {
        // Delete from DeliveryConfirmation table to avoid possible duplicate entry.
        database.execSQL("DELETE FROM DeliveryConfirmation where shipmentnumber='" + shipmentNumber + "'");

        dialogLoading = new ProgressIndicatorActivity(UndeliveryActivity.this);
        dialogLoading.showProgress();
        /*SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTimeString = format.format(new Date());*/
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en","US"));
        String currentDateTimeString = format.format(new Date());
        if (updateComplete()) {

//            Log.v("shipmentNumber", shipmentNumber);
            Cursor getUndeliveryValue = database.rawQuery("select  O.order_type,O.delivery_status, O.order_number, U.sno, " +
                    "IFNULL(U.shipmentnumber,0) as shipmentnumber, IFNULL(U.remarks,0) as remarks, " +
                    "IFNULL(U.proof_photo, 0) as proof_photo, IFNULL(U.reason,0) as reason, IFNULL(U.sync_status, 0) as sync_status, " +
                    "IFNULL(U.latitude, 0) as latitude, IFNULL(U.longitude, 0) as longitude, IFNULL(U.created_at, 0) as created_at,IFNULL(O.invoice_amount,0) as invoice_amount," +
                    " IFNULL(U.redirect, 0) as redirect,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O" +
                    ".payment_mode,0) as payment_mode,IFNULL(O.attempt_count, 0) as attempt_count," +

                    "IFNULL(U.shipment_address,0) as shipment_address, IFNULL(U.reason_id,0) as reason_id  from orderheader O LEFT JOIN UndeliveredConfirmation U on U" +

                    ".shipmentnumber = O.Shipment_Number where " +
                    " U.shipmentnumber = '" + shipmentNumber + "' ", null);
//            Log.v("get_attempt_count", String.valueOf(getUndeliveryValue.getCount()));

            if (getUndeliveryValue.getCount() > 0) {
                getUndeliveryValue.moveToFirst();
//                Log.v("get_attempt_count", String.valueOf(getUndeliveryValue.getInt(getUndeliveryValue.getColumnIndex("attempt_count"))));
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
                Log.v("undeliveredupload",getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("reason_id")));
//                Log.v("USER_ID", "--" + Constants.USER_ID);
//                Log.v("SAMP_USER_ID", AppController.getStringPreference(Constants.USER_ID, ""));
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
                //un_attempt_count++;
//                Log.v("attempt_count", String.valueOf(attempt_count));
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
                    paramObject.put("attemptCount", un_attempt_count);
                    paramObject.put("jobType", undeliveryReq.getJobType());
                    paramObject.put("employeeCode", undeliveryReq.getEmployeeCode());
                    paramObject.put("hubCode", undeliveryReq.getHubCode());
                    paramObject.put("status", undeliveryReq.getStatus());
                    paramObject.put("trackHalt", undeliveryReq.getTrackHalt());
                    paramObject.put("trackKm", undeliveryReq.getTrackKm());
                    paramObject.put("trackTransactionTimeSpent", undeliveryReq.getTrackTransactionTimeSpent());
                    paramObject.put("merchantCode", undeliveryReq.getMerchantCode());
                    paramObject.put("transactionDate", undeliveryReq.getMoneyTransactionType());
                    paramObject.put("erpPushTime", undeliveryReq.getErpPushTime());
                    paramObject.put("lastTransactionTime", undeliveryReq.getLastTransactionTime());
                    paramObject.put("created_at", getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("created_at")));
                    paramObject.put("battery", undeliveryReq.getBattery());
                    paramObject.put("deviceInfo", AppController.getdevice());
                    paramObject.put("redirect", undeliveryReq.getRedirect());
                    paramObject.put("order_type", getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("order_type")));
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

                Log.v("uploadUndelivered",paramObject.toString());

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
                        Log.v("undeliveredupload","- "+value.getRes_msg()+"-- "+ value.getRes_code());
//                        uploadImage();
                        if (value.getRes_msg().equalsIgnoreCase("undelivered success")) {

                            database.execSQL("UPDATE orderheader set image_status = 'C' where Shipment_Number ='" + shipmentNumber + "' ");


                            startService(new Intent(UndeliveryActivity.this, SyncService.class));
                            alertDialogMsg(UndeliveryActivity.this,  getResources().getString(R.string.undeli_title), getResources().getString(R.string.undeli_success_msg),  getResources().getString(R.string.ok));
                            dialogLoading.dismiss();
                            updateOrderStatus();

                            AppController.clearKey(Constants.UN_DEL_CUR_LATITUDE);
                            AppController.clearKey(Constants.UN_DEL_CUR_LONGITUDE);
                            AppController.clearKey(Constants.UN_DEL_LATITUDE);
                            AppController.clearKey(Constants.UN_DEL_LONGITUDE);



                            //  uploadImage();
                        } else if (value.getRes_msg().equalsIgnoreCase("undelivered updated")) {
                            alertDialogMsg(UndeliveryActivity.this, getResources().getString(R.string.undeli_title),  getResources().getString(R.string.undeli_success_msg), getResources().getString(R.string.ok));
                            dialogLoading.dismiss();
                            updateOrderStatus();
                        } else {
                            Logger.showShortMessage(UndeliveryActivity.this, getResources().getString(R.string.undeli_notsuccess_msg));
                            dialogLoading.dismiss();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        dialogLoading.dismiss();

                        alertDialogMsg(UndeliveryActivity.this, getResources().getString(R.string.undeli_title),  "Due to slow network connectivity the order will be uploaded in offline mode", getResources().getString(R.string.ok));

                        Log.v("uploadUndelivered","Error"+ e.toString());
                        Logger.showShortMessage(UndeliveryActivity.this, "Network dealyed so stored in offline");
                    }

                    @Override
                    public void onComplete() {

                        // dialogLoading.dismiss();
                        Log.v("uploadUndelivered", "oncomplete");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
            }
        }


    }


    public void uploadImage() {
//        Log.v("uploadImage", "uploadImageun");
//        if (updateComplete()) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getRequestHeader())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);


        File fileProofPhoto = new File(file_proofPhoto);
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
Log.v("undeliveredupload","image");
                  /*  if (value.getRes_msg().equalsIgnoreCase(getResources().getString(R.string.undeliver_res))){
                     uploadUndelivered();
                    } else {
                        Logger.showShortMessage(UndeliveryActivity.this, "Undelivered hasn't successfully uploaded");
                        dialogLoading.dismiss();
                    }*/

                undeliveryList = new ArrayList<>();
                List<UndeliveryResp> orderVal = value.getUndeliveredResp();
                if (value.getRes_msg().equalsIgnoreCase("undelivered success")) {

                    alertDialogMsg(UndeliveryActivity.this,  getResources().getString(R.string.undeli_title), getResources().getString(R.string.undeli_success_msg),  getResources().getString(R.string.ok));


                    dialogLoading.dismiss();
                    updateOrderStatus();

                    AppController.clearKey(Constants.UN_DEL_CUR_LATITUDE);
                    AppController.clearKey(Constants.UN_DEL_CUR_LONGITUDE);
                    AppController.clearKey(Constants.UN_DEL_LATITUDE);
                    AppController.clearKey(Constants.UN_DEL_LONGITUDE);
                } else if (value.getRes_msg().equalsIgnoreCase("undelivered updated")) {
                    alertDialogMsg(UndeliveryActivity.this, getResources().getString(R.string.undeli_title),  getResources().getString(R.string.undeli_success_msg), getResources().getString(R.string.ok));
                    dialogLoading.dismiss();
                    updateOrderStatus();
                } else {
                    Logger.showShortMessage(UndeliveryActivity.this, getResources().getString(R.string.undeli_notsuccess_msg));
                    dialogLoading.dismiss();
                }
            }

            @Override
            public void onError(Throwable e) {
//                    Log.d("error", e.toString());
                dialogLoading.dismiss();
            }

            @Override
            public void onComplete() {
//                    Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });


//        }
    }


    /**
     * Change the status of order once it has been uploaded successfully
     */
    private void updateOrderStatus() {

//        database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
//                shipmentNumber + "' ");
        un_attempt_count++;
        database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                shipmentNumber + "' ");
    }


    /*
     * Broadcast receiver to get battery level
     * */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            battery_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//            Log.v("get_battery", String.valueOf(battery_level));
        }
    };


    /**
     * Alert dialog for once get the response from the webservice
     *
     * @param context Get the cont+
     *                ext of an activity
     * @param content Get the content
     * @param okmsg   Get the  ok message of text
     *                //     * @param canmessage Get the cancel message
     */
 /*   public void alertDialogMsg(Context context, String title, String content, String okmsg, String
            canmessage) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
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

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }
                })
                .show();
    }*/
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

    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestCameraStoragePermission() {
        Permissions.check(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                getResources().getString(R.string.camera_permission), new Permissions
                        .Options()
                        .setSettingsDialogTitle(  getResources().getString(R.string.warning)).setRationaleDialogTitle( getResources().getString(R.string.info)),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                       /* Toast.makeText(UndeliveryActivity.this, "Camera+Storage granted",
                                Toast.LENGTH_SHORT).show();
*/

                        //do your task
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Camera+Storage Denied:\n",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }


    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestLocation() {
        Permissions.check(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                getResources().getString(R.string.permission), new
                        Permissions
                                .Options()
                        .setSettingsDialogTitle(getResources().getString(R.string.warning)).setRationaleDialogTitle(getResources().getString(R.string.location_permission)),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        EnableGPSAutoMatically();
                        //do your task
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, getResources().getString(R.string.location_per_deni),
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private boolean updateLocation(String latitude, String longitude) {
        database.execSQL("UPDATE UndeliveredConfirmation set latitude = '" + latitude + "', longitude = '" + longitude + "' where shipmentnumber ='" +
                shipmentNumber + "' ");
        return true;
    }

    private void setLocale(String lang){

        myLocale =new Locale(lang);
        Resources res=getResources();
        DisplayMetrics dm=res.getDisplayMetrics();
        Configuration cf =res.getConfiguration();
        cf.locale=myLocale;
        res.updateConfiguration(cf, dm);

        super.onRestart();
//        Intent intent =new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

}
