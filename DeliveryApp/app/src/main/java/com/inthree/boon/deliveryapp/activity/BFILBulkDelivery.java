package com.inthree.boon.deliveryapp.activity;


import android.Manifest;
import android.annotation.SuppressLint;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inthree.boon.deliveryapp.LocationUtils.LocationHelper;
import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.NetTime.TrueTimeRx;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.model.Barcode;
import com.inthree.boon.deliveryapp.model.BulkDeliveryConfirm;
import com.inthree.boon.deliveryapp.model.DeliveryConfirm;
import com.inthree.boon.deliveryapp.newcamera.CameraFragmentMainActivity;
import com.inthree.boon.deliveryapp.newcamera.PreviewActivity;
import com.inthree.boon.deliveryapp.request.DeliveryConfirmReq;
import com.inthree.boon.deliveryapp.request.OrderReq;
import com.inthree.boon.deliveryapp.request.PartialReq;
import com.inthree.boon.deliveryapp.response.AttemptResp;
import com.inthree.boon.deliveryapp.response.DeliveryConfirmResp;
import com.inthree.boon.deliveryapp.response.OtpResp;
import com.inthree.boon.deliveryapp.response.PartialResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.GPSTracker;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;
import com.inthree.boon.deliveryapp.utils.StringOperationsUtils;
import com.inthree.boon.deliveryapp.utils.SyncService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;


public class BFILBulkDelivery extends AppCompatActivity implements View.OnClickListener, LocationListener, View
        .OnFocusChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback, PopupMenu.OnMenuItemClickListener {

    String partial_shipAddress;
    private ArrayList<AttemptResp> attemptList;
    String upload_status = "";
    String image_url;
    ScrollView scroll;

    long time_wait = 30000;
    private ArrayList<DeliveryConfirmResp> deliveryList;
    String cam_title;

    private String shipmentAppend = "";


    String file_path;
    String file_path_old = "/storage/emulated/0/Pictures/DeliveryApp/";
    String file_path_very_old = "/storage/emulated/0/DCIM/Camera/";
    String sign_path;
    String payment_mode = "";


    private static final int CUSTOMER_DELIVERY_CODE = 100;
    private static final int INVOICE_CODE = 101;
    private static final int ADDRESS_PROOF_CODE = 102;

    public final static int MEMBER_PIC_CODE = 111;

    private static final int RELATION_PROOF_CODE = 103;
    private static final int VOTER_OCR_CODE = 333;


    String img_path;
    int battery_level;
    int attempt_count;

    private static Context context;


    /***
     * Response code argument
     */
    private static final String RESPONSE_CODE_ARG = "response_code_arg";


    private boolean successFlag = false;


    ProgressIndicatorActivity dialogLoading;


    final File mypicturedirectory = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES);


    AppCompatCheckBox cb_amount;
    AppCompatCheckBox cb_pickup;
    AppCompatEditText input_landmark;
    String currentDateandTime;

    TextView tv_custname;
    TextView tv_amountcoll;
    TextView tv_phno;
    TextView tv_pincode;
    private LinearLayout completeRoot;
    private LinearLayout completeMainRoot;

    /**
     * Google client to interact with Google API
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Enable and disable the name layout
     */
    private LinearLayout nameRoot;

    /**
     * Enable and disable the name layout
     */
    private LinearLayout amountRoot;

    /**
     * Enable and disable the phone layout
     */
    private LinearLayout phoneRoot;

    /**
     * Enable and disable the pin layout
     */
    private LinearLayout pinRoot;

    /**
     * Enable and disable the customer layout
     */
    private LinearLayout customerRoot;

    /**
     * Enable and disable the invoice layout
     */
    private LinearLayout invoiceRoot;


    /**
     * Enable and disable the submit layout
     */
    private LinearLayout submitRoot;

    /**
     * Text input layout has been displayed
     */
    AppCompatEditText textInput;

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
    TextInputLayout textpincode;

    /**
     * Enable and disable the error in edit text
     */
    TextInputLayout textInputLayout;


    /**
     * Otp enable
     */

    String generateotp;
    /**
     * Request code path
     */
    private final static String FILE_PATH_ARG = "file_path_arg";


    /**
     * The strop.
     */
    private StringOperationsUtils strop;

    ImageView imag_cust_address;

    ImageView imag_relation;

    LinearLayout ll_cust_addr;
    LinearLayout ll_relation;
    LinearLayout ll_other_name;
    ImageView imag_cust_delivery;
    LinearLayout ll_imag_invoice;
    LinearLayout relationRoot;
    LinearLayout signRoot;


    String imageCapture = "";
    ImageView imag_invoice;
    LinearLayout ll_cust_deli;

    /**
     * Get the string of fiels
     */
    String name;

    String customershipaddress;
    String phoneNumber;
    String Amount;
    String pincode;
    String land_mark;
    String getURN;
    String getOTP;
    String getgeOTP = "";
    String getNeft;
    String getVodeID;
    String getothername;
    String CustomerDeiPhoto1;
    String CustomerDeiPhoto2;
    String CustomerDeiPhoto3;
    String invoice;


    /**
     * Intiliaze the database
     */
    private SQLiteDatabase database;

    /**
     * set the model for all details values
     */
    BulkDeliveryConfirm deliveryConfirm;

    /**
     * Get the alert by  using util
     */
    Utils alertDialog;

    /**
     * Get the shipment number by using get intent
     */
    String shipmentNumber;

    /**
     * Check whether the status is partial or not
     */
    String statusSync;

    ArrayList<DeliveryConfirm> deliveryConfirmsArrayList = new ArrayList<>();

    boolean boolName = false;


    /**
     * Get the image of signature
     */
    private ImageView relationImage;


    /**
     * Get the image of signature
     */
    private ImageView signatureImage;


    /**
     * Set the text name to layout
     */
    private TextView txtName;

    /**
     * Set the text name to layout
     */
    private TextView txtAmt;

    /**
     * Set the text name to layout
     */
    private TextView txtNumber;

    /**
     * Set the text name to layout
     */
    private TextView txtPincode;

    /**
     * Check the pincode by using regex
     */
    private String pincodeRegex = "[0-9]{6}";

    /**
     * Check the regex pattern
     */
    private Pattern p;

    /**
     * check pattern matcher
     */
    private Matcher m;

    /**
     * Get the latitude and longitude
     */
    private GPSTracker gps;

    /**
     * Get the latitude
     */
    private double latitude_user;


    /**
     * Get the class name
     */
    private static final String TAG = BFILBulkDelivery.class.getSimpleName();

    /**
     * Get the longitude
     */
    private double longitude_user;
    private String latitude;
    private String longitude;


    Spinner selectrelation;

    String[] selrelation = {"Self", "Other"};

    /**
     * Get the location of latitude and longitude
     */
    private LocationHelper locationHelper;

    private LocationManager manager;
    private LocationRequest mLocationRequest;

    /**
     * Get the activity name
     */
    String activityName;

    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;

    private String orderID;

    private String referenceID;

    /**
     * Get the order details
     */
    private Cursor getOrderDetails;

    /**
     * Get the image address
     */
    private ImageView imagShipAddress;
    private LinearLayout shipAddress;
    private TextView txtShipadd;
    private TextInputLayout textAmount;
    private AppCompatEditText custAmount;
    private AppCompatCheckBox redirect;

    /**
     * Get the map current address
     */
    AppCompatButton applyLocAdd;

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
     * Get the current latitude and longititude
     */
    double comLat;
    double comLang;

    public static final int LOCATION_ADDRESS_CODE = 201;

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private final static int REQUEST_FIRST_CHECK_SETTINGS = 2500;

    /**
     * Check the latitude and longtitude
     */
    double latCheck;

    double lonCheck;
    public Location mLastLocation;

    /**
     * Get the current location addres of the map store into string
     */
    private String currentLocation;

    /**
     * Get the pincode of address store into string
     */
    private String postalCode;

    /***
     * Get the Adhaar information
     */
    private LinearLayout ll_custAdhaar;

    /**
     * Click the trigger onclick listener
     */
    private ImageView adhaaImg;

    /**
     * The contents.
     */
    private String contents;

    /**
     * Initialize the xml
     */
    XmlToJson xmlToJson;

    /**
     * Set the adhaar UID
     */
    private TextView adhaaTxt;

    /**
     * need to show the alertdialog
     */
    private SweetAlertDialog pDialog;


    private final static String DELIVERY = "delivery";
    private final static String ADDRESS = "address";
    private final static String INVOICE = "invoice";
    private final static String RELATION = "relation";

    /**
     * check the payment mode
     */
    private String paymentMode;

    /**
     * Get the total amount from partial delivery
     */
    private String amountCollected;
    private String partial_reason;
    private String order_type;
    private GoogleApiClient googleApiClient;

    private final static int REQUEST_CHECK_SETTINGS_GPS = 2000;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private ScrollView scrollView;
    String aadhaarEnabled;

    /**
     * Get the date
     */
    private String formattedDate;

    /**
     * Get the feedback
     */
    String feedBack;
    String user_language;

    /**
     * Enter the otp in edittext
     */
    private TextInputLayout txtOtp;

    /**
     * Enter the otp in Textview
     */
    private Button txtgetOtp;

    /**
     * Enter the otp in Textview
     */
    private Button txtresendOtp;

    /**
     * Enter the urn in edittext
     */
    private TextInputLayout txtURN;

    /**
     * Get the otp from user
     */
    private AppCompatEditText input_otp;

    /**
     * Get the URN from user
     */
    private AppCompatEditText input_URN;

    /**
     * Get the otp from string
     */
    private String otp;

    /**
     * Get the otp from string
     */
    private String bfilotp;

    /**
     * Get the otp from string
     */
    private String Contact_number;

    /**
     * Get the otp from string
     */
    private String urn;


    boolean cameraAlert = false;

    /**
     * check whether urn and otp number matched or not
     */
    private String verify;
    Locale myLocale;

    boolean checked_or_not;
    boolean pickupBoolean = false;
    LinearLayout ll_camera;
    AppCompatButton bt_back;
    AppCompatButton bt_submit;
    LinearLayout ll_pickup_layout;
    RadioGroup radioGroup;
    int selected_rid;
    RadioButton rdsuccess;
    RadioButton rdfailed;
    LinearLayout sub_lay4;
    LinearLayout ll_checkbox;
    CheckBox cb_pickup_complete;
    String pick_success_fail;
    String storeFilename, partFilename;
    private final static String MEMBER_PIC_NAME = "pickupimage";
    ImageView imag_name;
    String pick_completed;

    String editvalue = null;
    String fetchVal = null;


    /*
     * Textview label for differentiating between Aadhar and Voter ID
     * */
    TextView tv_proofLabel;

    /**
     * Enter the NEFT number in edittext
     */
    TextInputLayout txt_neft;

    /**
     * Get the NEFT from user
     */
    AppCompatEditText input_neft;

    AppCompatCheckBox cb_neft;
    TextInputLayout txt_voterid;
    TextInputLayout txt_othername;
    AppCompatEditText input_voterid;
    AppCompatEditText input_other_name;
    LinearLayout ll_voter_Aadhar;
    ImageView iv_delAadharScan;
    TextView sp_proof;
    TextView sr_proof;
    View ll_relation_view;
    ImageView iv_delVoterOcr;
    private String blockCharacterSet;
    String pickupImageProof = "";
    ScrollView scroll_alert;

    /**
     * update the orders by using shipment id
     */
    private String updateBulkShipAppend;

    /**
     * update the shipment update append
     */
    private String shipmentupdateAppend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_delivery);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.btn_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        user_language = AppController.getStringPreference(Constants.USER_LANGUAGE, "");
        if (user_language.equals("tamil")) {
            AppController.setLocale("ta");
        } else if (user_language.equals("telugu")) {
            AppController.setLocale("te");
        } else if (user_language.equals("marathi")) {
            AppController.setLocale("mr");
        } else if (user_language.equals("hindi")) {
            AppController.setLocale("hi");
        } else if (user_language.equals("punjabi")) {
            AppController.setLocale("pa");
        } else if (user_language.equals("odia")) {
            AppController.setLocale("or");
        } else if (user_language.equals("bengali")) {
            AppController.setLocale("be");
        } else if (user_language.equals("kannada")) {
            AppController.setLocale("kn");
        } else if (user_language.equals("assamese")) {
            AppController.setLocale("as");
        } else {
            AppController.setLocale("en");
        }
//        Log.v("deli_user_language", "-- " + user_language);
        imag_cust_address = (ImageView) findViewById(R.id.imag_cust_address);
        imag_relation = (ImageView) findViewById(R.id.image_relation);
        imagShipAddress = (ImageView) findViewById(R.id.imag_ship_address);
        imag_cust_delivery = (ImageView) findViewById(R.id.imag_cust_delivery);
        imag_invoice = (ImageView) findViewById(R.id.imag_invoice);
        ll_imag_invoice = (LinearLayout) findViewById(R.id.ll_imag_invoice);
        ll_cust_addr = (LinearLayout) findViewById(R.id.ll_cust_addr);


        ll_cust_deli = (LinearLayout) findViewById(R.id.ll_cust_deli);
        ll_relation_view = (View) findViewById(R.id.ll_relation_view);
//        tv_proofLabel = (TextView) findViewById(R.id.tv_proofLabel);
//        scrollView = (ScrollView) findViewById(R.id.scroll);

        image_url = getResources().getString(R.string.delivery_url) + "/media/";
        file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
//        Log.v("file_path",String.valueOf(this.getFilesDir()));
//        sign_path = String.valueOf(this.getFilesDir()) + "/UserSignature/";
        sign_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
        partFilename = currentDateFormat();
        storeFilename = "";

        locationHelper = new LocationHelper(this);
        locationHelper.checkpermission();
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            createLocationRequest();

        }

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());

        getLocation();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        initViews();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());
        Log.v("onActivityResultCD", "rtrtr");
        if (requestCode == CUSTOMER_DELIVERY_CODE && imageCapture.equals("CD")) {
            if (resultCode == Activity.RESULT_OK) {


                Intent i = getIntent();
                String responseCode = data.getStringExtra(RESPONSE_CODE_ARG);
                String retakes = data.getStringExtra("retake");
                if (responseCode.equalsIgnoreCase("900")) {

                    // String imagePathuri = data.getStringExtra(FILE_PATH_ARG);
                    //  String imagePath = compressImage(imagePathuri);
                    String imagePath = data.getStringExtra(FILE_PATH_ARG);
                    Log.v("onActivityResultCD", imagePath);
                    // String imagePath = data.getStringExtra("camera_data");

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
                                        customerRoot.setBackgroundDrawable(dr);
                                    }
                                });

//                    ll_cust_deli.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        String lastOne = parts[parts.length - 1];
                        deliveryConfirm.setDeliveryProof(lastOne);
//                    Log.v("setDeliveryProof", lastOne);
                        imag_cust_delivery.setVisibility(View.INVISIBLE);
                        invoiceRoot.setEnabled(true);
                        invoiceRoot.setClickable(true);
                        if (deliveryConfirm.getInvoiceProof().equals("")) {
                            // Constants.DELIVERED_TITLE = getResources().getString(R.string.invoice);
                            invoiceRoot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.main_bg));
                        }
                        if (!deliveryConfirm.getDeliveryProof().equals("")) {
                            String deliveryDetailsupdate = "UPDATE DeliveryConfirmation set delivery_proof = '" + deliveryConfirm.getDeliveryProof() + "' where shipmentnumber = '" + shipmentNumber + "' ";
                            database.execSQL(deliveryDetailsupdate);
                        }

                        if (retakes != null) {
                            if (retakes.equalsIgnoreCase("emptyRetake")) {
                                imageCapturePopup();
                            } else if (retakes.equalsIgnoreCase("picRetake")) {

                            }
                        } else {
                            imageCapturePopup();
                        }

                    } else {
//                    signatureImage.setImageResource(R.drawable.camera);
                    }
                } else if (responseCode.equalsIgnoreCase("901")) {
                    Log.v("onActivityResultCD", "789");
                    if (deliveryConfirm.getDeliveryProof().equals(""))
                        customerActivity(CUSTOMER_DELIVERY_CODE, "emptyRetake", "Delivery Proof", DELIVERY);
                    else
                        customerActivity(CUSTOMER_DELIVERY_CODE, "picRetake", "Delivery Proof", DELIVERY);

                } else if (responseCode.equalsIgnoreCase("902")) {
                    Log.v("onActivityResultCD", "345");
                    if (deliveryConfirm.getDeliveryProof().equals(""))
                        customerActivity(CUSTOMER_DELIVERY_CODE, "emptyRetake", "Delivery Proof", DELIVERY);
                    else
                        customerActivity(CUSTOMER_DELIVERY_CODE, "picRetake", "Delivery Proof", DELIVERY);

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                signRoot.setBackgroundDrawable(R.drawable.camera);
//                signatureImage.setImageResource(R.drawable.camera);
                //Write your code if there's no result
            }
        } else if (requestCode == ADDRESS_PROOF_CODE && imageCapture.equals("CA")) {
            if (resultCode == Activity.RESULT_OK) {


                String responseCode = data.getStringExtra(RESPONSE_CODE_ARG);
                String retakes = data.getStringExtra("retake");
                if (responseCode.equalsIgnoreCase("900")) {

                    String imagePath = data.getStringExtra(FILE_PATH_ARG);
                    Log.v("onActivityResultCA", imagePath);
                    String[] parts = imagePath.split("/");
                    if (imagePath != null) {
//                    Log.v("out_here", "ADDRESS_PROOF_CODE:" + Constants.DELIVERED_TITLE);
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
                                        ll_cust_addr.setBackgroundDrawable(dr);
                                    }
                                });


                        String lastOne = parts[parts.length - 1];


                        deliveryConfirm.setIdProff(lastOne);
                        imag_cust_address.setVisibility(View.INVISIBLE);
                        signRoot.setEnabled(true);
                        signRoot.setClickable(true);
                        if (deliveryConfirm.getSignatureProof().equals("")) {
                            signRoot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.main_bg));
                        }

                        if (!deliveryConfirm.getIdProff().equals("")) {
                            String deliveryDetailsupdate = "UPDATE DeliveryConfirmation set id_proof = '" + deliveryConfirm.getIdProff() + "' where shipmentnumber = '" + shipmentNumber + "' ";
                            database.execSQL(deliveryDetailsupdate);
                        }

                        if (retakes != null) {
                            if (retakes.equalsIgnoreCase("emptyRetake")) {
                                imageCapturePopup();
                            } else if (retakes.equalsIgnoreCase("picRetake")) {

                            }
                        } else {
                            imageCapturePopup();
                        }
                    } else {
//                    signatureImage.setImageResource(R.drawable.camera);

                    }
                } else if (responseCode.equalsIgnoreCase("901")) {
                    if (deliveryConfirm.getIdProff().equals("")) {
                        customerActivity(ADDRESS_PROOF_CODE, "emptyRetake", "Address Proof", ADDRESS);
                    } else {
                        customerActivity(ADDRESS_PROOF_CODE, "picRetake", "Address Proof", ADDRESS);
                    }


                } else if (responseCode.equalsIgnoreCase("902")) {
                    if (deliveryConfirm.getIdProff().equals("")) {
                        customerActivity(ADDRESS_PROOF_CODE, "emptyRetake", "Address Proof", ADDRESS);
                    } else {
                        customerActivity(ADDRESS_PROOF_CODE, "picRetake", "Address Proof", ADDRESS);
                    }
                }

                if (resultCode == Activity.RESULT_CANCELED) {
                }
            }
        } else if (requestCode == INVOICE_CODE && imageCapture.equals("IN")) {
            if (resultCode == Activity.RESULT_OK) {


                String responseCode = data.getStringExtra(RESPONSE_CODE_ARG);
                String retakes = data.getStringExtra("retake");
                if (responseCode.equalsIgnoreCase("900")) {
                    String imagePath = data.getStringExtra(FILE_PATH_ARG);
                    Log.v("onActivityResultIN", imagePath);
                    String[] parts = imagePath.split("/");
                    if (imagePath != null) {
//                    Log.v("out_here", "INVOICE_CODE:" + Constants.DELIVERED_TITLE);
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
                                        ll_imag_invoice.setBackgroundDrawable(dr);
                                    }
                                });

//                    ll_imag_invoice.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        String lastOne = parts[parts.length - 1];
                        deliveryConfirm.setInvoiceProof(lastOne);


                        imag_invoice.setVisibility(View.INVISIBLE);
                        ll_cust_addr.setEnabled(true);
                        ll_cust_addr.setClickable(true);
                        if (deliveryConfirm.getIdProff().equals("")) {
                            ll_cust_addr.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.main_bg));
                        }


                        if (!deliveryConfirm.getInvoiceProof().equals("")) {
                            String deliveryDetailsupdate = "UPDATE DeliveryConfirmation set Invoice_proof = '" + deliveryConfirm.getInvoiceProof() + "' where shipmentnumber = '" + shipmentNumber + "' ";
                            database.execSQL(deliveryDetailsupdate);
                        }

                        if (retakes != null) {
                            if (retakes.equalsIgnoreCase("emptyRetake")) {
                                imageCapturePopup();
                            } else if (retakes.equalsIgnoreCase("picRetake")) {

                            }
                        } else {
                            imageCapturePopup();
                        }
                    } else {
//                    signatureImage.setImageResource(R.drawable.camera);
                    }
                } else if (responseCode.equalsIgnoreCase("901")) {
                    if (deliveryConfirm.getInvoiceProof().equals(""))
                        customerActivity(INVOICE_CODE, "emptyRetake", "Invoice Proof", INVOICE);
                    else
                        customerActivity(INVOICE_CODE, "picRetake", "Invoice Proof", INVOICE);

                } else if (responseCode.equalsIgnoreCase("902")) {
                    if (deliveryConfirm.getInvoiceProof().equals(""))
                        customerActivity(INVOICE_CODE, "emptyRetake", "Invoice Proof", INVOICE);
                    else
                        customerActivity(INVOICE_CODE, "picRetake", "Invoice Proof", INVOICE);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String imagePath = data.getStringExtra("imagePath");
                feedBack = data.getStringExtra("Feedback");
                if (imagePath != null) {
                    String[] parts = String.valueOf(imagePath).split("/");
//                    Log.v("imagePath", imagePath);
                    String lastOne = parts[parts.length - 1];
                    deliveryConfirm.setSignatureProof(lastOne);
                    deliveryConfirm.setFeed_back(feedBack);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    signRoot.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    insertDeliveryInfo();
                    signatureImage.setVisibility(View.INVISIBLE);
                  /*  if (!aadhaarEnabled.equals("0")) {
                        if (!deliveryConfirm.getAdhaarDetails().equals("")) {
                            completeRoot.setVisibility(VISIBLE);
                            completeRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_main));
                        }
                    } else {*/
                    completeRoot.setVisibility(VISIBLE);
                    completeRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_main));
                    /*}*/
                    if (!deliveryConfirm.getSignatureProof().equals("")) {
                        String deliveryDetailsupdate = "UPDATE DeliveryConfirmation set signature_proof = '" + deliveryConfirm.getSignatureProof() + "' where shipmentnumber = '" + shipmentNumber + "' ";
                        database.execSQL(deliveryDetailsupdate);
                    }

                    new CountDownTimer(500, 1) {
                        public void onTick(long millisUntilFinished) {
                            scroll.scrollTo(0, R.id.scroll);
                        }

                        public void onFinish() {
                        }
                    }.start();


                } else {
                    signatureImage.setImageResource(R.drawable.camera);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                signRoot.setBackgroundDrawable(R.drawable.camera);
                signatureImage.setImageResource(R.drawable.camera);
                //Write your code if there's no result
            }

        } else if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            Constants.aadharCode = resultCode == RESULT_OK
                    ? data.getExtras().getString(QrScannerActivity.QR_RESULT_STR)
                    : "Scanned Nothing!";
            String scanResult = Constants.aadharCode;

            if (scanResult != null)
//                ll_custAdhaar.setBackgroundColor(ContextCompat.getColor(this, R.color.main_bg));
                Log.v("adhaar_xml", scanResult);
            deliveryConfirm.setAdhaarDetails(scanResult);
//            qrscanner(scanResult);
            qrscannerAlert(scanResult);
//            if (Constants.printLetterBarcodeData.getPrintLetterBarcodeData().getUid() != null) {
//                ll_custAdhaar.setBackgroundColor(ContextCompat.getColor(this, R.color.main_bg));
//            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    buildGoogleApiClient();
                    manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    createLocationRequest();
                    getLocation();
                    successFlag = true;

                    break;
                case Activity.RESULT_CANCELED:
                    Utils.AlertDialogCancel(BFILBulkDelivery.this, "Location Service Warning", "Couldn't get the location at the moment, Make sure location is enabled on the device", "OK", "Cancel");
                    break;
            }
        } else if (requestCode == REQUEST_FIRST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Utils.AlertDialogCancel(BFILBulkDelivery.this, "Location Service Warning", "Couldn't get the location at the moment, Make sure location is enabled on the device", "OK", "Cancel");
                    break;
            }

        } else if (requestCode == MEMBER_PIC_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                String responseCode = data.getStringExtra(RESPONSE_CODE_ARG);
                // String responseCode = "111";
                Log.v("responseCode", responseCode);
                if (responseCode.equalsIgnoreCase("900")) {

                    String imagePath = data.getStringExtra(FILE_PATH_ARG);
//                    Log.v("onActivityResult", imagePath);
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
                                        sub_lay4.setBackgroundDrawable(dr);
                                    }
                                });

                        String lastOne = parts[parts.length - 1];
                        storeFilename = lastOne;
                        Log.v("onActivityResult", storeFilename);
                        deliveryConfirm.setPickup_image(storeFilename);
                        imag_name.setVisibility(View.INVISIBLE);
                        getpickupdata();
                    } else {
//                    signatureImage.setImageResource(R.drawable.camera);
                    }
                } else if (responseCode.equalsIgnoreCase("901")) {


                    if (storeFilename.equals(""))
//                        customerActivity(MEMBER_PIC_CODE, "emptyRetake", "Member Card", responseCode);
                        customerActivity(MEMBER_PIC_CODE, "emptyRetake", "Pickup Proof", responseCode);
                    else
//                        customerActivity(MEMBER_PIC_CODE, "picRetake", "Member Card", responseCode);
                        customerActivity(MEMBER_PIC_CODE, "picRetake", "Pickup Proof", responseCode);


                } else if (responseCode.equalsIgnoreCase("902")) {

                    if (storeFilename.equals(""))
//                        customerActivity(MEMBER_PIC_CODE, "emptyRetake", "Member Card", responseCode);
                        customerActivity(MEMBER_PIC_CODE, "emptyRetake", "Pickup Proof", responseCode);
                    else
//                        customerActivity(MEMBER_PIC_CODE, "picRetake", "Member Card", responseCode);
                        customerActivity(MEMBER_PIC_CODE, "picRetake", "Pickup Proof", responseCode);

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }

        } else if (requestCode == VOTER_OCR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String ocrPath = data.getStringExtra("ocrPath");
                if (ocrPath != null) {
//            Log.v("ocrPath"," - "+ ocrPath);
                    input_voterid.setText(ocrPath);
                }
            }

        }
    }


    private void successUploadService() {

        getLocation();
        if (mLastLocation != null) {
            AppController.setLongPreference(this, Constants.CUR_LATITUDE, Double.doubleToRawLongBits(mLastLocation
                    .getLatitude()));
            AppController.setLongPreference(this, Constants.CUR_LONGITUDE, Double.doubleToRawLongBits(mLastLocation
                    .getLongitude()));
        }

        if (latitude_user != 0.0 && longitude_user != 0.0) {
            latitude = String.valueOf(latitude_user);
            longitude = String.valueOf(longitude_user);
            deliveryConfirm.setLatitude(latitude);
            deliveryConfirm.setLongitude(longitude);
        }

        if (Utils.checkNetworkAndShowDialog(BFILBulkDelivery.this)) {
            double lat = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.LATITUDE,
                    -1));
            double lang = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.LONGITUDE,
                    -1));

//            Log.v("latlonge", lat + " " + lang);
            comLat = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.CUR_LATITUDE, -1));
            comLang = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.CUR_LONGITUDE, -1));
            String lati = String.valueOf(comLat);
            String longi = String.valueOf(comLang);
//            Log.v("latlong", lati + " " + longi);

            if (lati != null && longi != null && !Double.isNaN(comLat) && !Double.isNaN(comLang)) {

                if (updateLocation(lati, longi)) {
                    Log.v("updateLocation", "here");
                    uploadComplete();
                    //  uploadImage();
                } else {
                    Utils.AlertDialogCancel(BFILBulkDelivery.this, "Location Service Warning", "Location Not Updated", "OK", "Cancel");
                }


            } else if (String.valueOf(lat) != null && String.valueOf(lang) != null && !Double.isNaN(lat) &&
                    !Double.isNaN(lang)) {

                if (updateLocation(String.valueOf(lat), String.valueOf(lang))) {
                    Log.v("updateLocation", "there");
                    uploadComplete();
                    //uploadImage();
                } else {
                    Utils.AlertDialogCancel(BFILBulkDelivery.this, "Location Service Warning", "Location Not Updated", "OK", "Cancel");
                }
            } else {
                Utils.AlertDialogCancel(BFILBulkDelivery.this, "Location Service Warning", "Couldn't get the location at the moment, Please try again later", "OK", "Cancel");
            }


        } else {
//                    Logger.showShortMessage(this, this.getString(R.string.delivery_offline));
            double lat = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.LATITUDE,
                    -1));
            double lang = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.LONGITUDE,
                    -1));


            comLat = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.CUR_LATITUDE, -1));
            comLang = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.CUR_LONGITUDE, -1));
            String lati = String.valueOf(comLat);
            String longi = String.valueOf(comLang);

            if (lati != null && longi != null && !Double.isNaN(comLat) && !Double.isNaN(comLang) && comLat != 0.0 &&
                    comLang != 0.0) {

                if (updateLocation(lati, longi)) {

                    /*Cursor checkOrder = database.rawQuery("Select IFNULL(Shipment_Number,0) as Shipment_Number from orderheader where check_bfil_order_status = '2' AND delivery_to = 1",
                            null);
                    if (checkOrder != null && checkOrder.getCount() > 0) {
                        checkOrder.moveToFirst();

                        while (!checkOrder.isAfterLast()) {
                           String shipmentNumber=checkOrder.getString(checkOrder.getColumnIndex("Shipment_Number"));*/

                    database.execSQL("UPDATE orderheader set sync_status = 'C' where Shipment_Number IN (" + updateBulkOrders() + ") AND delivery_to='1' ");
                       /*     checkOrder.moveToNext();
                        }

                        checkOrder.close();
                    }*/
                    alertDialogMsgOffline(BFILBulkDelivery.this, "Success", getString(R.string.delivery_offline), "Ok");
                } else {
                    Utils.AlertDialogCancel(BFILBulkDelivery.this, "Location Service Warning", "Location Not Updated", "OK", "Cancel");
                }

            } else if (String.valueOf(lat) != null && String.valueOf(lang) != null && !Double.isNaN(lat) &&
                    !Double.isNaN(lang) && lat != 0.0 && lang != 0.0) {
                if (updateLocation(String.valueOf(lat), String.valueOf(lang))) {

                    database.execSQL("UPDATE orderheader set sync_status = 'C' where Shipment_Number IN (" + updateBulkOrders() + ") AND delivery_to='1' ");

                    alertDialogMsgOffline(BFILBulkDelivery.this, "Success", getString(R.string.delivery_offline), "Ok");
                } else {
                    Utils.AlertDialogCancel(BFILBulkDelivery.this, "Location Service Warning", "Location Not Updated", "OK", "Cancel");
                }
            } else {

                Utils.AlertDialogCancel(BFILBulkDelivery.this, "Location Service Warning", "Couldn't get the location at the moment", "OK", "Cancel");

            }
        }
    }


    /**
     * Scan the web url then open the web link.
     *
     * @param result the result
     */
    private void qrscanner(String result) {
        contents = result;

        JSONObject jsonObj = null;
        // String errorContent= String.valueOf(contents.indexOf(1));
        String errorContent = String.valueOf(contents.charAt(1));
        if (errorContent.contains("/")) {
            contents = contents.substring(0, 1) + "" + contents.substring(2);
        }
        xmlToJson = new XmlToJson.Builder(contents).build();
        jsonObj = xmlToJson.toJson();
        String adhaarCode = jsonObj.toString();
        Log.v("adhaar", "--" + adhaarCode);
//        deliveryConfirm.setAdhaarDetails(adhaarCode);
        Gson gson = new GsonBuilder().create();
        Constants.printLetterBarcodeData = gson.fromJson(jsonObj.toString(), Barcode.class);

        if (Constants.printLetterBarcodeData.getPrintLetterBarcodeData() != null)
            if (Constants.printLetterBarcodeData.getPrintLetterBarcodeData().getUid() != null) {

                String aadharUpdate = "UPDATE DeliveryConfirmation set adhaar_details='" + deliveryConfirm.getAdhaarDetails() + "' where shipmentnumber ='" + shipmentNumber + "' ";
                database.execSQL(aadharUpdate);
                adhaaTxt.setVisibility(View.VISIBLE);
                adhaaTxt.setText(Constants.printLetterBarcodeData.getPrintLetterBarcodeData().getUid());
                if (Constants.printLetterBarcodeData.getPrintLetterBarcodeData().getUid() != null) {
                    ll_custAdhaar.setBackgroundColor(ContextCompat.getColor(this, R.color.main_bg));
                    tv_proofLabel.setText("Aadhar");
                    completeMainRoot.setVisibility(View.VISIBLE);
                    completeRoot.setVisibility(VISIBLE);
                    completeRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_main));
                    new CountDownTimer(500, 1) {
                        public void onTick(long millisUntilFinished) {
                            scroll.scrollTo(0, R.id.scroll);
                        }

                        public void onFinish() {
                        }
                    }.start();

                }
            } else {
//                adhaaTxt.setVisibility(View.GONE);  //removed 10-02-2019 9.17PM
            }

    }

    private void qrscannerAlert(String result) {
        contents = result;

        JSONObject jsonObj = null;
        // String errorContent= String.valueOf(contents.indexOf(1));
        String errorContent = String.valueOf(contents.charAt(1));
        if (errorContent.contains("/")) {
            contents = contents.substring(0, 1) + "" + contents.substring(2);
        }
        xmlToJson = new XmlToJson.Builder(contents).build();
        jsonObj = xmlToJson.toJson();
        String adhaarCode = jsonObj.toString();
        Log.v("adhaar", "--" + adhaarCode);
//        deliveryConfirm.setAdhaarDetails(adhaarCode);
        Gson gson = new GsonBuilder().create();
        Constants.printLetterBarcodeData = gson.fromJson(jsonObj.toString(), Barcode.class);

        if (Constants.printLetterBarcodeData.getPrintLetterBarcodeData() != null)
            if (Constants.printLetterBarcodeData.getPrintLetterBarcodeData().getUid() != null) {
                String aadharUpdate = "UPDATE DeliveryConfirmation set adhaar_details='" + deliveryConfirm.getAdhaarDetails() + "' where shipmentnumber ='" + shipmentNumber + "' ";
                database.execSQL(aadharUpdate);

                input_voterid.setText(Constants.printLetterBarcodeData.getPrintLetterBarcodeData().getUid());
                if (Constants.printLetterBarcodeData.getPrintLetterBarcodeData().getUid() != null) {
                }
            } else {
            }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        camera.deleteImage();
    }


    /**
     * Declare all the id of layout attributes
     */
    private void initViews() {
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();

        Cursor getAadhaarStatus = database.rawQuery("select aadhaar from UserMaster ", null);
        if (getAadhaarStatus.getCount() > 0) {
            getAadhaarStatus.moveToFirst();

        }

        context = this;
        deliveryConfirm = new BulkDeliveryConfirm();
        Intent shipNum = getIntent();
        shipmentNumber = shipNum.getStringExtra(Constants.SHIPMENT_NUMBER);
        statusSync = shipNum.getStringExtra("partial");
        amountCollected = shipNum.getStringExtra("amountCollected");

        partial_reason = shipNum.getStringExtra("partial_reason");
        order_type = shipNum.getStringExtra("order_type");
        Log.v("new_amount", "--" + amountCollected + " " + order_type);
        orderID = shipNum.getStringExtra(Constants.ORDER_ID);
        referenceID = shipNum.getStringExtra(Constants.REFERENCE_NO);
//        Log.v("shipnum", "--" + shipmentNumber + "-" + partial_reason);
        alertDialog = new Utils();
        strop = new StringOperationsUtils();

        Cursor getUndeliveredStatus = database.rawQuery("Select * from orderheader where Shipment_Number = '" + shipmentNumber + "' AND delivery_to='1' ", null);
        if (getUndeliveredStatus.getCount() > 0) {
            getUndeliveredStatus.moveToFirst();
            String getUndeliveredVal = getUndeliveredStatus.getString(getUndeliveredStatus.getColumnIndex("sync_status"));

            aadhaarEnabled = getUndeliveredStatus.getString(getUndeliveredStatus.getColumnIndex("delivery_aadhar_required"));
            Log.v("getUndelivered", getUndeliveredVal);

            if (getUndeliveredVal.equals("U")) {
                Log.v("getUndelivered", " - " + getUndeliveredVal);
                database.execSQL("DELETE FROM DeliveryConfirmation where shipmentnumber = '" + shipmentNumber + "' ");
            }
        }

        nameRoot = (LinearLayout) findViewById(R.id.sub_lay1);
        phoneRoot = (LinearLayout) findViewById(R.id.sub_lay3);
        pinRoot = (LinearLayout) findViewById(R.id.sub_lay4);
        customerRoot = (LinearLayout) findViewById(R.id.ll_cust_deli);
        invoiceRoot = (LinearLayout) findViewById(R.id.ll_imag_invoice);
        signRoot = (LinearLayout) findViewById(R.id.ll_sign);

        shipAddress = (LinearLayout) findViewById(R.id.sub_address);
        ll_cust_addr = (LinearLayout) findViewById(R.id.ll_cust_addr);
        signatureImage = (ImageView) findViewById(R.id.image_sign);


        tv_custname = (TextView) findViewById(R.id.tv_custname);
        tv_phno = (TextView) findViewById(R.id.tv_phno);
        tv_pincode = (TextView) findViewById(R.id.tv_pincode);

        scroll = (ScrollView) findViewById(R.id.scroll);
        /**
         * Set the text in layout
         */
        txtName = (TextView) findViewById(R.id.txt_name);
        txtNumber = (TextView) findViewById(R.id.txt_number);
        txtPincode = (TextView) findViewById(R.id.txt_pincode);
        txtShipadd = (TextView) findViewById(R.id.txt_address);
        txtShipadd.setSelected(true);

        completeRoot = (LinearLayout) findViewById(R.id.ll_complete);
        completeMainRoot = (LinearLayout) findViewById(R.id.ll_main_complete);


        nameRoot.setOnClickListener(this);
        phoneRoot.setOnClickListener(this);
        pinRoot.setOnClickListener(this);
        customerRoot.setOnClickListener(this);
        invoiceRoot.setOnClickListener(this);
        signRoot.setOnClickListener(this);
        ll_cust_addr.setOnClickListener(this);
        ll_cust_deli.setOnClickListener(this);
        completeRoot.setOnClickListener(this);
        shipAddress.setOnClickListener(this);

        if (updateSyncStatus()) {
            getDetails();
        }

    }


    /**
     * Fetch the details from delivery details
     */
    private void getDetails() {
        Log.v("otp_and_urn", "-- " + "getdetails");
      /*  Cursor customerName = database.rawQuery("select  O.delivery_status, O.referenceNumber," +
                " D.sno as sno " +
                ",IFNULL(D.customer_name,0) as customer_name," +
                "IFNULL(D.verify,0) as verify,IFNULL(D.customer_contact_number,0) as customer_contact_number, IFNULL(O.branch_contact_number,0) as branch_contact_number," +
                "IFNULL(O.delivery_to,0) as delivery_to, IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                ", IFNULL(D.delivery_proof, 0) as delivery_proof,IFNULL(D.id_proof,0)as" +
                " " + "id_proof,IFNULL(D" + ".signature_proof," +
                "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D" +
                ".latitude,0) as " + "latitude," + "IFNULL" + "(D.longitude,0) as longitude" + " from orderheader O LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where D" +
                ".shipmentnumber='" + shipmentNumber + "' AND O.delivery_to='1'", null);*/

        Cursor customerName = database.rawQuery("select  O.delivery_status, O.referenceNumber," +
                " D.sno as sno " +
                ",IFNULL(D.customer_name,0) as customer_name," +
                "IFNULL(D.verify,0) as verify,IFNULL(D.customer_contact_number,0) as customer_contact_number, IFNULL(O.branch_contact_number,0) as branch_contact_number," +
                "IFNULL(O.delivery_to,0) as delivery_to, IFNULL(O.branch_address,0) as branch_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                ", IFNULL(D.delivery_proof, 0) as delivery_proof,IFNULL(D.id_proof,0)as" +
                " " + "id_proof,IFNULL(D" + ".signature_proof," +
                "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D" +
                ".latitude,0) as " + "latitude," + "IFNULL" + "(D.longitude,0) as longitude" + " from orderheader O LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where D" +
                ".shipmentnumber='" + shipmentNumber + "' AND O.delivery_to='1'", null);

        //customerName.moveToFirst();

        if (customerName != null) {
            if (customerName.getCount() > 0) {

                customerName.moveToFirst();

                bfilotp = customerName.getString(customerName.getColumnIndex("delivery_to"));

                Contact_number = customerName.getString(customerName.getColumnIndex("branch_contact_number"));

                nameRoot.setEnabled(false);
                nameRoot.setClickable(false);
                phoneRoot.setEnabled(false);
                phoneRoot.setClickable(false);
                pinRoot.setEnabled(false);
                pinRoot.setClickable(false);
                customerRoot.setEnabled(false);
                customerRoot.setEnabled(false);
                invoiceRoot.setEnabled(false);
                invoiceRoot.setClickable(false);
                signRoot.setEnabled(false);
                signRoot.setClickable(false);
                ll_cust_addr.setEnabled(false);
                ll_cust_addr.setClickable(false);

                AlertDialogCancel(this, getResources().getString(R.string.sure), getResources().getString(R.string.getwarning), getResources()
                        .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel), customerName);
            } else {
                nameRoot.setEnabled(false);
                nameRoot.setClickable(false);
                phoneRoot.setEnabled(false);
                phoneRoot.setClickable(false);
                pinRoot.setEnabled(false);
                pinRoot.setClickable(false);
                customerRoot.setEnabled(false);
                customerRoot.setEnabled(false);
                invoiceRoot.setEnabled(false);
                invoiceRoot.setClickable(false);
                signRoot.setEnabled(false);
                signRoot.setClickable(false);
                ll_cust_addr.setEnabled(false);
                ll_cust_addr.setClickable(false);


                getOrderDetails = database.rawQuery("select  IFNULL(customer_name,0) as customer_name," +
                        "IFNULL(branch_address,0) as branch_address,IFNULL(delivery_to, 0) as delivery_to," +
                        "IFNULL(branch_contact_number,0) as branch_contact_number,IFNULL(branch_pincode,0) as branch_pincode ,IFNULL(customer_contact_number,0) as customer_contact_number," +
                        "tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi" +
                        " from " + "orderheader where order_number='" + orderID + "' AND  delivery_to = '1'", null);
                System.out.println("COUNT : " + getOrderDetails.getCount());


                if (getOrderDetails.getCount() > 0) {


                    getOrderDetails.moveToFirst();
                    bfilotp = getOrderDetails.getString(getOrderDetails.getColumnIndex("delivery_to"));
                    Contact_number = getOrderDetails.getString(getOrderDetails.getColumnIndex("branch_contact_number"));
                    deliveryConfirm.setPickup_image(pickupImageProof);

                    deliveryConfirm.setCustomerName(getOrderDetails.getString(getOrderDetails.getColumnIndex("customer_name")));
                    deliveryConfirm.setPincode(getOrderDetails.getString(getOrderDetails.getColumnIndex("branch_pincode")));
                    deliveryConfirm.setCustomerContactNumber(getOrderDetails.getString(getOrderDetails.getColumnIndex("branch_contact_number")));
                    deliveryConfirm.setShipAddress(getOrderDetails.getString(getOrderDetails.getColumnIndex
                            ("branch_address")));

                    if (user_language.equals("tamil")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("tamil"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("hindi")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("hindi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("bengali")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("bengali"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("marathi")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("marathi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("punjabi")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("punjabi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("odia")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("orissa"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("telugu")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("telugu"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("kannada")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("kannada"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("assamese")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("assam"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    }

                    delAlertBox();
                }
                getOrderDetails.close();
            }
        }

    }


    /**
     * This is for alert box
     */
    private void delAlertBox() {

        final Dialog elAlertdialog = new Dialog(BFILBulkDelivery.this);
        elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        elAlertdialog.setContentView(R.layout.bulk_delivery_alert);
        elAlertdialog.show();
        elAlertdialog.setCancelable(false);

        textName = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);
        textPhoneNumber = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_phone);
        textpincode = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_pincode);
        textAddress = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_add);
        txtOtp = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_opt);

        txtgetOtp = elAlertdialog.findViewById(R.id.txt_get_otp);
        txtresendOtp = elAlertdialog.findViewById(R.id.txt_resend_otp);

        applyLocAdd = (AppCompatButton) elAlertdialog.findViewById(R.id.loc_address);
        gMap = (AppCompatImageView) elAlertdialog.findViewById(R.id.google_map);
        locationFindAddress = (TextView) elAlertdialog.findViewById(R.id.address);


        custName = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
        custPhoneNo = (AppCompatEditText) elAlertdialog.findViewById(R.id.input_phone);
        shippingAddress = (AppCompatEditText) elAlertdialog.findViewById(R.id.add);
        shipPincode = (AppCompatEditText) elAlertdialog.findViewById(R.id.input_pincode);
        input_landmark = (AppCompatEditText) elAlertdialog.findViewById(R.id.input_landmark);
        input_otp = (AppCompatEditText) elAlertdialog.findViewById(R.id.input_otp);

        AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
        final AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
        custName.setOnFocusChangeListener(this);
        custPhoneNo.setOnFocusChangeListener(this);
        shipPincode.setOnFocusChangeListener(this);
        input_otp.setOnFocusChangeListener(this);


        nameRoot.setEnabled(true);
        nameRoot.setClickable(true);

        Log.v("paymentMode", "--" + paymentMode);
        applyLocAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentLocation != null || postalCode != null) {

                    shipPincode.setText("");
                    shippingAddress.setText("");

                    shipPincode.setText(postalCode);
                    shippingAddress.setText(currentLocation);
                } else {
                    Logger.showShortMessage(BFILBulkDelivery.this, "Location is empty");
                }
            }
        });


        if (bfilotp.equalsIgnoreCase("1")) {
            input_otp.setVisibility(VISIBLE);
            txtOtp.setVisibility(VISIBLE);
            txtgetOtp.setVisibility(VISIBLE);


        } else {
            input_otp.setVisibility(GONE);
            txtOtp.setVisibility(GONE);

        }

        /**
         * Get the location of current addresskyc
         */
        lat = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.LATITUDE, -1));
        lang = Double.longBitsToDouble(AppController.getLongPreference(BFILBulkDelivery.this, Constants.LONGITUDE,
                -1));
        String lati = String.valueOf(lat);
        String longi = String.valueOf(lang);
//        Log.v("former_val", Constants.LATITUDE + "--" + Constants.LONGITUDE);

        if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lang)) {
            getAddresss(lat, lang);

        } else {
            //showToast("Couldn't get the location. Make sure location is enabled on the device");
        }


        txtgetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    txtgetOtp.setVisibility(GONE);
                    txtresendOtp.setVisibility(VISIBLE);
                    generateotp();
                } else {
                    showalert(getString(R.string.nointernet));
                }

            }
        });

        txtresendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    generateotp();
                } else {
                    showalert(getString(R.string.nointernet));
                }
            }
        });


        input_otp.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (input_otp.getText().length() > 0) {
                    txtOtp.setErrorEnabled(false);
                }
            }
        });

        gMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View hideView = getCurrentFocus();
                if (hideView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                double lat = Double.longBitsToDouble(AppController.getLongPreference(view.getContext(), Constants.LATITUDE,
                        -1));
                double lang = Double.longBitsToDouble(AppController.getLongPreference(view.getContext(), Constants.LONGITUDE,
                        -1));


                comLat = Double.longBitsToDouble(AppController.getLongPreference(view.getContext(), Constants.CUR_LATITUDE, -1));
                comLang = Double.longBitsToDouble(AppController.getLongPreference(view.getContext(), Constants.CUR_LONGITUDE, -1));
                String lati = String.valueOf(comLat);
                String longi = String.valueOf(comLang);
//                Log.v("latlong", lati + " " + longi);

                if (currentLocation != null)
                    locationFindAddress.setText(currentLocation);

                if (lati != null && longi != null && !Double.isNaN(comLat) && !Double.isNaN(comLang)) {
                    Intent intent = new Intent(BFILBulkDelivery.this, MapsActivity.class);
                    intent.putExtra("MapshipAddValue", "delivery");
                    startActivityForResult(intent, LOCATION_ADDRESS_CODE);
                } else if (String.valueOf(lat) != null && String.valueOf(lang) != null && !Double.isNaN(lat) &&
                        !Double.isNaN(lang)) {
                    Intent intent = new Intent(BFILBulkDelivery.this, MapsActivity.class);
                    intent.putExtra("MapshipAddValue", "delivery");
                    startActivityForResult(intent, LOCATION_ADDRESS_CODE);

                } else {
                    Logger.showShortMessage(view.getContext(), "Satellite singals are poor please turn on the GPS " +
                            "location");
                }


            }
        });


        if (boolName) {
            if (!deliveryConfirm.getCustomerName().equals("")) {
                custName.setText(deliveryConfirm.getCustomerName());
            }

            if (!deliveryConfirm.getCustomerContactNumber().equals("")) {
                custPhoneNo.setText(deliveryConfirm.getCustomerContactNumber());
            }
            if (!deliveryConfirm.getShipAddress().equals("")) {
                shippingAddress.setText(deliveryConfirm.getShipAddress());
            }
            if (!deliveryConfirm.getPincode().equals("")) {
                shipPincode.setText(deliveryConfirm.getPincode());
            }
            if (!deliveryConfirm.getOtp().equals("")) {
                input_otp.setText(deliveryConfirm.getOtp());
            }

        } else {
            if (!deliveryConfirm.getCustomerName().equals("")) {
                custName.setText(deliveryConfirm.getCustomerName());
            }
            if (!deliveryConfirm.getCustomerContactNumber().equals("")) {
                custPhoneNo.setText(deliveryConfirm.getCustomerContactNumber());
            }
            if (!deliveryConfirm.getShipAddress().equals("")) {
                shippingAddress.setText(deliveryConfirm.getShipAddress());
            }
            if (!deliveryConfirm.getPincode().equals("")) {
                shipPincode.setText(deliveryConfirm.getPincode());
            }
            if (!deliveryConfirm.getOtp().equals("")) {
                input_otp.setText(deliveryConfirm.getOtp());
            }

        }
        elAlertdialog.setCanceledOnTouchOutside(false);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern mPattern = Pattern.compile("/^([a-zA-Z0-9]+)$/");


                name = custName.getText().toString();
                name = name.replaceAll("[-+.^:',_]", "");
                name = name.trim();
                customershipaddress = shippingAddress.getText().toString();
                phoneNumber = custPhoneNo.getText().toString();
                pincode = shipPincode.getText().toString();
                land_mark = input_landmark.getText().toString();
                getOTP = input_otp.getText().toString();
                deliveryConfirm.setNeft(getNeft);
                p = Pattern.compile(pincodeRegex);
                m = p.matcher(pincode);
                Log.v("getPickup_image", " - " + deliveryConfirm.getPickup_image() + " + ");

                if (TextUtils.isEmpty(name.trim())) {
                    textName.setErrorEnabled(true);
                    textName.setError(getResources().getString(R.string.pername));
                    textName.requestFocus();
                } else if (TextUtils.isEmpty(phoneNumber.trim())) {
                    textPhoneNumber.setErrorEnabled(true);
                    textPhoneNumber.setError(getResources().getString(R.string.error_phone));
                    textPhoneNumber.requestFocus();
//                } else if (phoneNumber.length() < 10 || phoneNumber.length() > 10 || !android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
                } else if ((phoneNumber.length() < 10 || phoneNumber.length() > 10 || !android.util.Patterns.PHONE.matcher(phoneNumber).matches()) && aadhaarEnabled.equals("0")) {
                    textPhoneNumber.setErrorEnabled(true);
                    textPhoneNumber.setError(getResources().getString(R.string.valid_phone_number));
                    textPhoneNumber.requestFocus();
                } else if (TextUtils.isEmpty(customershipaddress.trim())) {  //&& customershipaddress.trim().length() < 6
                    textAddress.setErrorEnabled(true);
                    textAddress.setError(getResources().getString(R.string.error_ship_address));
                    textAddress.requestFocus();

                } else if (TextUtils.isEmpty(pincode.trim())) {

                    textpincode.setErrorEnabled(true);
                    textpincode.setError(getResources().getString(R.string.error_pincode));
                    textpincode.requestFocus();
                } else if (pincode.length() < 6 || pincode.length() > 6 || !m.matches()) {
                    textpincode.setErrorEnabled(true);
                    textpincode.setError(getResources().getString(R.string.valid_pincode));
                    textpincode.requestFocus();

                } else if (pickupBoolean == false && order_type.equals("3")) {
                    cb_pickup_complete.setError("Error Please check");
                } else if (deliveryConfirm.getPickup_image().equals("") && order_type.equals("3")) {
                    alertDialogPickupProof(BFILBulkDelivery.this, "Error", "Pickup Image Proof Missing", "Ok");
                } else if (input_otp.getVisibility() == VISIBLE) {

                    if (TextUtils.isEmpty(getOTP.trim())) {
                        txtOtp.setErrorEnabled(true);
                        txtOtp.setError(getResources().getString(R.string.press_otp_match));
                        txtOtp.requestFocus();
                    } else if (getOTP.equalsIgnoreCase("")) {
                        txtOtp.setErrorEnabled(true);
                        txtOtp.setError(getResources().getString(R.string.error_otp_match));
                        txtOtp.requestFocus();
                    } else if (getgeOTP.equalsIgnoreCase("")) {
                        txtOtp.setErrorEnabled(true);
                        txtOtp.setError(getResources().getString(R.string.press_otp_match));
                        txtOtp.requestFocus();
                    } else {
                        if (!getgeOTP.equalsIgnoreCase("0") && getgeOTP != null && !getgeOTP.equalsIgnoreCase("")) {
                            if (getOTP.equalsIgnoreCase(getgeOTP)) {
                                verify = "yes";
                                deliveryConfirm.setOtp(getgeOTP);
                                deliveryConfirm.setVerify(verify);
                            } else {
                                verify = "no";
                                deliveryConfirm.setVerify(verify);
                            }
                        }
                        nameRoot.setEnabled(true);
                        nameRoot.setClickable(true);
                        nameRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));

                        phoneRoot.setEnabled(true);
                        phoneRoot.setClickable(true);
                        phoneRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));

                        txtName.setVisibility(VISIBLE);
                        txtName.setText(name);

                        txtNumber.setVisibility(VISIBLE);
                        txtNumber.setText(phoneNumber);

                        txtPincode.setVisibility(VISIBLE);
                        txtPincode.setText(pincode);

                        txtShipadd.setVisibility(VISIBLE);
                        txtShipadd.setText(customershipaddress);

                        pinRoot.setEnabled(true);
                        pinRoot.setClickable(true);
                        pinRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));

                        shipAddress.setEnabled(true);
                        shipAddress.setClickable(true);
                        shipAddress.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));

                        deliveryConfirm.setCustomerName(name);
                        deliveryConfirm.setShipAddress(customershipaddress);
                        deliveryConfirm.setPincode(pincode);
                        deliveryConfirm.setCustomerContactNumber(phoneNumber);
                        deliveryConfirm.setAmountCollected(Amount);
                        deliveryConfirm.setLandMark(land_mark);

                        if (deliveryConfirm.getDeliveryProof().equals("")) {
                            customerRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));
                            Constants.DELIVERED_TITLE = getResources().getString(R.string.custdevelivery);
                        } else if (!deliveryConfirm.getDeliveryProof().equals("") && deliveryConfirm.getInvoiceProof().equals("")) {
                            invoiceRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));
                            Constants.DELIVERED_TITLE = getResources().getString(R.string.invoice);
                        } else if (!deliveryConfirm.getDeliveryProof().equals("") && !deliveryConfirm.getInvoiceProof().equals("") && deliveryConfirm.getIdProff().equals("")) {
                            ll_cust_addr.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));
                            Constants.DELIVERED_TITLE = getResources().getString(R.string.addressprof);
                        }
                        elAlertdialog.cancel();
                        customerRoot.setClickable(true);
                        customerRoot.setEnabled(true);
                        requestCameraStoragePermission();
                    }

                } else {
                    if (!getgeOTP.equalsIgnoreCase("0") && getgeOTP != null && !getgeOTP.equalsIgnoreCase("")) {
                        if (getOTP.equalsIgnoreCase(getgeOTP)) {
                            verify = "yes";
                            deliveryConfirm.setOtp(getgeOTP);
                            deliveryConfirm.setVerify(verify);
                        } else {
                            verify = "no";
                            deliveryConfirm.setVerify(verify);
                        }

                    }

                    nameRoot.setEnabled(true);
                    nameRoot.setClickable(true);
                    nameRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));

                    phoneRoot.setEnabled(true);
                    phoneRoot.setClickable(true);
                    phoneRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));

                    txtName.setVisibility(VISIBLE);
                    txtName.setText(name);

                    txtNumber.setVisibility(VISIBLE);
                    txtNumber.setText(phoneNumber);

                    txtPincode.setVisibility(VISIBLE);
                    txtPincode.setText(pincode);

                    txtShipadd.setVisibility(VISIBLE);
                    txtShipadd.setText(customershipaddress);

                    pinRoot.setEnabled(true);
                    pinRoot.setClickable(true);
                    pinRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));

                    shipAddress.setEnabled(true);
                    shipAddress.setClickable(true);
                    shipAddress.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));

                    deliveryConfirm.setCustomerName(name);
                    deliveryConfirm.setShipAddress(customershipaddress);
                    deliveryConfirm.setPincode(pincode);
                    deliveryConfirm.setCustomerContactNumber(phoneNumber);
                    deliveryConfirm.setAmountCollected(Amount);
                    deliveryConfirm.setLandMark(land_mark);

                    if (deliveryConfirm.getDeliveryProof().equals("")) {
                        customerRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));
                        Constants.DELIVERED_TITLE = getResources().getString(R.string.custdevelivery);
                    } else if (!deliveryConfirm.getDeliveryProof().equals("") && deliveryConfirm.getInvoiceProof().equals("")) {
                        invoiceRoot.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));
                        Constants.DELIVERED_TITLE = getResources().getString(R.string.invoice);
                    } else if (!deliveryConfirm.getDeliveryProof().equals("") && !deliveryConfirm.getInvoiceProof().equals("") && deliveryConfirm.getIdProff().equals("")) {
                        ll_cust_addr.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.main_bg));
                        Constants.DELIVERED_TITLE = getResources().getString(R.string.addressprof);
                    }
                    elAlertdialog.cancel();
                    customerRoot.setClickable(true);
                    customerRoot.setEnabled(true);
                    requestCameraStoragePermission();

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


    /**
     * This id for OTP genetaor
     */
    private void generateotp() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);

        String phone = Contact_number;
        JSONObject json = new JSONObject();
        try {
            json.put("mobile_number", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString().toString());
        final Observable<OtpResp> observable = apiService.getOtp(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<OtpResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(OtpResp value) {

                if (value.getResMsg().equalsIgnoreCase("success")) {
                    getgeOTP = value.getOtp();
                    Toast.makeText(context, "OTP is Send to your Branch Contact number",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, value.getResMsg(),
                            Toast.LENGTH_SHORT).show();
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


    /**
     * Net avaliable Check
     */
    public static boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    /**
     * This is for signature pad  for when user delivered the product to sign in
     */
    public void signaturePad() {
        Intent i = new Intent(this, SignatureActivity.class);
        i.putExtra("shipment_num", shipmentNumber);
        startActivityForResult(i, 1);
    }

    public void cameraImageCapture(final int req_code, final String headName, final String fileName, final String
            prooff,
                                   final String
                                           filePath) {

        Permissions.check(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                getResources().getString(R.string.camera_permission), new Permissions
                        .Options()
                        .setSettingsDialogTitle(getResources().getString(R.string.warning)).setRationaleDialogTitle(getResources().getString(R.string.info)),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        Log.v("onGranted", "- " + prooff);
                        if (!prooff.equals("")) {
                            Intent intent = new Intent(BFILBulkDelivery.this, PreviewActivity.class);
                            intent.putExtra(FILE_PATH_ARG, filePath);
                            intent.putExtra("code", String.valueOf(req_code));
                            intent.putExtra("PreviewActivity", "PreviewStatus");
                            intent.putExtra("heading", headName);
                            startActivityForResult(intent, req_code);
                        } else {
                            Intent intent = new Intent(BFILBulkDelivery.this, CameraFragmentMainActivity.class);
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


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sub_lay1:
                if (deliveryConfirm.getCustomerName() != null) {
                    boolName = true;
                    delAlertBox();
                }
                break;

            case R.id.sub_address:
                if (deliveryConfirm.getShipAddress() != null) {
                    // Constants.DELIVERED_TITLE = getResources().getString(R.string.name);
                    boolName = true;
                    delAlertBox();
                }

                break;

            case R.id.sub_lay2:
                if (deliveryConfirm.getAmountCollected() != null) {
                    // Constants.DELIVERED_TITLE = getResources().getString(R.string.amountcolled);
                    boolName = true;
                    delAlertBox();
                }
                break;
            case R.id.sub_lay3:

                if (deliveryConfirm.getCustomerContactNumber() != null) {
                    //  Constants.DELIVERED_TITLE = getResources().getString(R.string.phnumber);
                    boolName = true;
                    delAlertBox();
                }
                break;
            case R.id.sub_lay4:
                if (deliveryConfirm.getPincode() != null) {
                    //  Constants.DELIVERED_TITLE = getResources().getString(R.string.pincode);
                    boolName = true;
                    delAlertBox();
                }
                break;

            case R.id.ll_cust_deli:
                imageCapture = "CD";
                cameraImageCapture(CUSTOMER_DELIVERY_CODE, getString(R.string.delivery_proof), DELIVERY, deliveryConfirm.getDeliveryProof(),
                        file_path + deliveryConfirm.getDeliveryProof());
                break;
            case R.id.ll_imag_invoice:
                imageCapture = "IN";

                cameraImageCapture(INVOICE_CODE, getString(R.string.invoice_proof), INVOICE, deliveryConfirm.getInvoiceProof(),
                        file_path + deliveryConfirm
                                .getInvoiceProof
                                        ());
                break;
            case R.id.ll_cust_addr:
                imageCapture = "CA";

                cameraImageCapture(ADDRESS_PROOF_CODE, getString(R.string.addressprof), ADDRESS, deliveryConfirm.getIdProff(),
                        file_path + deliveryConfirm
                                .getIdProff());

                break;

            case R.id.ll_sign:
                signaturePad();
                break;

            case R.id.ll_complete:
                requestLocation();
                manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                successUploadService();
                break;
        }

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.name:
                textNameOnFocus(hasFocus);
                break;

            case R.id.add:
                textnumberOnFocus(hasFocus);
                break;

            case R.id.input_pincode:
                textAddressOnFocus(hasFocus);
                break;

            case R.id.input_phone:
                textPincodeOnFocus(hasFocus);
                break;


            case R.id.input_otp:
                textOtpOnFocus(hasFocus);
                break;
        }
    }

    /**
     * set on Focus on Phone Edit text
     *
     * @param hasFocus focus of view
     */
    private void textNameOnFocus(boolean hasFocus) {
        if (!hasFocus && !TextUtils.isEmpty(custName.getText().toString())) {
            custName.setText(strop.titleize(custName.getText().toString()));
            textName.setErrorEnabled(false);
        }

    }

    /**
     * set on Focus on Phone Edit text
     *
     * @param hasFocus focus of view
     */
    private void textnumberOnFocus(boolean hasFocus) {
        if (!hasFocus && !TextUtils.isEmpty(custPhoneNo.getText().toString())) {
            custPhoneNo.setText(strop.titleize(custPhoneNo.getText().toString()));
            textPhoneNumber.setErrorEnabled(false);
        }

    }

    /**
     * set on Focus on Phone Edit text
     *
     * @param hasFocus focus of view
     */
    private void textAddressOnFocus(boolean hasFocus) {
        if (!hasFocus && !TextUtils.isEmpty(shippingAddress.getText().toString())) {
            shippingAddress.setText(strop.titleize(shippingAddress.getText().toString()));
            textAddress.setErrorEnabled(false);
        }

    }

    /**
     * set on Focus on Phone Edit text
     *
     * @param hasFocus focus of view
     */
    private void textPincodeOnFocus(boolean hasFocus) {
        if (!hasFocus && !TextUtils.isEmpty(shipPincode.getText().toString())) {
            shipPincode.setText(strop.titleize(shipPincode.getText().toString()));
            textpincode.setErrorEnabled(false);
        }

    }


    /**
     * set on Focus on otp Edit text
     *
     * @param hasFocus focus of view
     */
    private void textOtpOnFocus(boolean hasFocus) {
        if (!hasFocus && !TextUtils.isEmpty(input_otp.getText().toString())) {
            input_otp.setText(strop.titleize(input_otp.getText().toString()));
            txtOtp.setErrorEnabled(false);
        }

    }


    /**
     * Alert dialog for get the cancel
     *
     * @param context    Get the context of an activity
     * @param content    Get the content
     * @param okmsg      Get the  ok message of text
     * @param canmessage Get the cancel message
     */
    public void AlertDialogCancel(final Context context, String title, String content, String okmsg, String
            canmessage, final Cursor customerName) {

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setCancelable(false);

        pDialog.setTitleText(title)
                .setContentText(content)
                .setCancelText(canmessage).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                database.execSQL("DELETE FROM DeliveryConfirmation where shipmentnumber='" + shipmentNumber + "'");
                database.execSQL("DELETE FROM PickupConfirmation where shipmentno='" + shipmentNumber + "'");

                getOrderDetails = database.rawQuery("select  IFNULL(customer_name,0) as customer_name," +
                        "IFNULL(shipping_address,0) as shipping_address," +
                        "IFNULL(shipping_pincode,0) as shipping_pincode ,IFNULL(customer_contact_number,0) as customer_contact_number,IFNULL(invoice_amount,0) as invoice_amount," +
                        "tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi" +
                        " from " + "orderheader where order_number='"
                        + orderID + "'", null);

                if (getOrderDetails.getCount() > 0) {
                    getOrderDetails.moveToFirst();
                    deliveryConfirm.setCustomerName(getOrderDetails.getString(getOrderDetails.getColumnIndex("customer_name")));
                    deliveryConfirm.setPincode(getOrderDetails.getString(getOrderDetails.getColumnIndex("shipping_pincode")));
                    deliveryConfirm.setCustomerContactNumber(getOrderDetails.getString(getOrderDetails.getColumnIndex("customer_contact_number")));
                    deliveryConfirm.setShipAddress(getOrderDetails.getString(getOrderDetails.getColumnIndex
                            ("shipping_address")));
                    if (null == statusSync || !statusSync.equalsIgnoreCase("partial")) {
                        deliveryConfirm.setAmountCollected(getOrderDetails.getString(getOrderDetails.getColumnIndex("invoice_amount")));
                    } else {
                        deliveryConfirm.setAmountCollected(amountCollected);
                    }
                    if (user_language.equals("tamil")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("tamil"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("hindi")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("hindi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("bengali")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("bengali"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("marathi")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("marathi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("punjabi")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("punjabi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("odia")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("orissa"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("telugu")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("telugu"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("kannada")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("kannada"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    } else if (user_language.equals("assam")) {
                        String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("assamese"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String customer_name = jObject.getString("customer_name");
                            String ship_addr = jObject.getString("delivery_address");

                            deliveryConfirm.setCustomerName(customer_name);
                            deliveryConfirm.setShipAddress(ship_addr);
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    }

                }
                getOrderDetails.close();
                delAlertBox();
//                Log.v("get_alert_state", "okay");
            }
        })
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        database.execSQL("DELETE FROM DeliveryConfirmation where shipmentnumber='" + shipmentNumber + "'");
                        database.execSQL("DELETE FROM PickupConfirmation where shipmentno='" + shipmentNumber + "'");

                        getOrderDetails = database.rawQuery("select  IFNULL(customer_name,0) as customer_name," +
                                "IFNULL(shipping_address,0) as shipping_address," +
                                "IFNULL(shipping_pincode,0) as shipping_pincode ,IFNULL(customer_contact_number,0) as customer_contact_number,IFNULL(invoice_amount,0) as invoice_amount," +
                                "tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi" +
                                " from " + "orderheader where order_number='"
                                + orderID + "'", null);

                        if (getOrderDetails.getCount() > 0) {
                            getOrderDetails.moveToFirst();
                            deliveryConfirm.setCustomerName(getOrderDetails.getString(getOrderDetails.getColumnIndex("customer_name")));
                            deliveryConfirm.setPincode(getOrderDetails.getString(getOrderDetails.getColumnIndex("shipping_pincode")));
                            deliveryConfirm.setCustomerContactNumber(getOrderDetails.getString(getOrderDetails.getColumnIndex("customer_contact_number")));
                            deliveryConfirm.setShipAddress(getOrderDetails.getString(getOrderDetails.getColumnIndex
                                    ("shipping_address")));
                            if (null == statusSync || !statusSync.equalsIgnoreCase("partial")) {
                                deliveryConfirm.setAmountCollected(getOrderDetails.getString(getOrderDetails.getColumnIndex("invoice_amount")));
                            } else {
                                deliveryConfirm.setAmountCollected(amountCollected);
                            }
                            if (user_language.equals("tamil")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("tamil"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            } else if (user_language.equals("hindi")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("hindi"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            } else if (user_language.equals("bengali")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("bengali"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            } else if (user_language.equals("marathi")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("marathi"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            } else if (user_language.equals("punjabi")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("punjabi"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            } else if (user_language.equals("odia")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("orissa"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            } else if (user_language.equals("telugu")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("telugu"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            } else if (user_language.equals("kannada")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("kannada"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            } else if (user_language.equals("assam")) {
                                String language_json = getOrderDetails.getString(getOrderDetails.getColumnIndex("assamese"));

                                try {
                                    JSONObject jObject = new JSONObject(language_json);
                                    String customer_name = jObject.getString("customer_name");
                                    String ship_addr = jObject.getString("delivery_address");

                                    deliveryConfirm.setCustomerName(customer_name);
                                    deliveryConfirm.setShipAddress(ship_addr);
                                } catch (JSONException e) {
                                    e.getStackTrace();
                                }
                            }

                        }
                        getOrderDetails.close();
                        delAlertBox();

                    }
                }).show();

    }


    /**
     * Insert  the delivery information
     *
     * @return
     */
    public Boolean insertDeliveryInfo() {

        String shipmentAppend = getBulkOrders();


//        Log.v("redirect_val", "-" + "insertDeliveryInfo");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTimeString = format.format(new Date());
        Log.v("redirect_valc", "-" + deliveryConfirm.getVoterOrAadharType());
        Cursor count = database.rawQuery("select * from DeliveryConfirmation where shipmentnumber='"
                + shipmentNumber + "'", null);
        if (count.getCount() == 0) {
            Log.v("redirect_valc", "-" + count.getCount());
            String deliverDetailsInsert = "Insert into DeliveryConfirmation (shipmentnumber,customer_name," +
                    "customer_contact_number," +
                    "Invoice_proof,delivery_proof,shipping_address, id_proof, signature_proof" +
                    ",amount_collected,sync_status,latitude,longitude,pin_code,adhaar_details,redirect,reason, landmark, created_at,feed_back,verify,neft,relation_proof,received_by,aadhar_voter_type,bulk_Shipment_append)" + " VALUES (" +
                    "'" + shipmentNumber + "'," +
                    "'" + deliveryConfirm.getCustomerName() + "','" +
                    deliveryConfirm.getCustomerContactNumber() + "','" + deliveryConfirm.getInvoiceProof() + "', " +
                    "'" + deliveryConfirm.getDeliveryProof() + "','" + deliveryConfirm.getShipAddress() + "','" + deliveryConfirm
                    .getIdProff() + "', '" +
                    deliveryConfirm.getSignatureProof() + "' ,'" + deliveryConfirm.getAmountCollected() + "','"
                    + "P" + "','" + deliveryConfirm.getLatitude() + "','" + deliveryConfirm.getLongitude() + "','" +
                    deliveryConfirm.getPincode()
                    + "', '" +
                    deliveryConfirm.getAdhaarDetails() + "'," +
                    "'" + deliveryConfirm.getRedirect() + "', " +
                    "'" + partial_reason + "', '" + deliveryConfirm.getLandMark() + "'," +
                    "'" + formattedDate + "','" + deliveryConfirm.getFeed_back() + "','" + deliveryConfirm.getVerify() + "','" + deliveryConfirm.getNeft() + "','" + deliveryConfirm.getRelationProof() + "','" + deliveryConfirm.getOtherSelfType() + "','" + deliveryConfirm.getVoterOrAadharType() + "','" + shipmentAppend + "')";
            database.execSQL(deliverDetailsInsert);
        } else if (count.getCount() > 0) {
            Log.v("redirect_vale", "-" + count.getCount());
            String deliveryDetailsupdate = "UPDATE DeliveryConfirmation set customer_name='" + deliveryConfirm.getCustomerName() +
                    "'," + "customer_contact_number='" + deliveryConfirm.getCustomerContactNumber() + "',adhaar_details='"
                    + deliveryConfirm.getAdhaarDetails() + "',shipping_address='" + deliveryConfirm.getShipAddress() + "'," +
                    "amount_collected='"
                    + deliveryConfirm.getAmountCollected() + "'," +
                    "pin_code='" + deliveryConfirm.getPincode() + "',redirect='" + deliveryConfirm.getRedirect() + "',id_proof='" +
                    deliveryConfirm.getIdProff() + "'," +
                    "delivery_proof='" + deliveryConfirm.getDeliveryProof() + "'," +
                    "Invoice_proof='" + deliveryConfirm.getInvoiceProof() + "'," +
                    "signature_proof='" + deliveryConfirm.getSignatureProof() + "'," +
                    "sync_status='" + "P" + "'," +
                    "latitude='" + deliveryConfirm.getLatitude() + "'," +
                    "bulk_Shipment_append='" + shipmentAppend + "'," +
                    "relation_proof='" + deliveryConfirm.getRelationProof() + "'," +
                    "received_by='" + deliveryConfirm.getOtherSelfType() + "'," +
                    "longitude='" + deliveryConfirm.getLongitude() + "', " +
                    "landmark = '" + deliveryConfirm.getLandMark() + "', reason='" + partial_reason + "'," + "" +
                    "created_at = '" + formattedDate + "',feed_back='" + deliveryConfirm.getFeed_back() + "', verify='" + deliveryConfirm.getVerify() + "', neft = '" + deliveryConfirm.getNeft() + "',aadhar_voter_type = '" + deliveryConfirm.getVoterOrAadharType() + "' " +
                    "where shipmentnumber ='" + shipmentNumber + "' ";
            database.execSQL(deliveryDetailsupdate);
        }
        count.close();
        return true;
    }


    public String getBulkOrders() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        Cursor checkOrder = database.rawQuery("Select IFNULL(Shipment_Number,0) as Shipment_Number,IFNULL(invoice_id,0) as invoice_id from orderheader where check_bfil_order_status = '2' AND delivery_to = 1 AND sync_status='P'",
                null);
        if (checkOrder != null && checkOrder.getCount() > 0) {
            checkOrder.moveToFirst();
            while (!checkOrder.isAfterLast()) {
                sb.append('\'').append('\'').append(checkOrder.getString(checkOrder.getColumnIndex("Shipment_Number"))).append('\'').append('\'').append(',');
                checkOrder.moveToNext();
            }
            shipmentAppend = sb.toString();
            if (shipmentAppend.endsWith(",")) {
                shipmentAppend = shipmentAppend.substring(0, shipmentAppend.length() - 1);
            }
            checkOrder.close();
        }

        return shipmentAppend;
    }


    public String updateBulkOrders() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        Cursor checkOrder = database.rawQuery("Select IFNULL(Shipment_Number,0) as Shipment_Number,IFNULL(invoice_id,0) as invoice_id from orderheader where check_bfil_order_status = '2' AND delivery_to = 1 AND sync_status='P'",
                null);
        if (checkOrder != null && checkOrder.getCount() > 0) {
            checkOrder.moveToFirst();
            while (!checkOrder.isAfterLast()) {
                sb.append('\'').append(checkOrder.getString(checkOrder.getColumnIndex("Shipment_Number"))).append('\'').append(',');
                checkOrder.moveToNext();
            }
            shipmentupdateAppend = sb.toString();
            if (shipmentupdateAppend.endsWith(",")) {
                shipmentupdateAppend = shipmentupdateAppend.substring(0, shipmentupdateAppend.length() - 1);
            }
            checkOrder.close();
        }

        return shipmentupdateAppend;
    }


    @Override
    public void onBackPressed() {
        backAlert(this, getResources()
                .getString(R.string.dl_back_pressed), getResources().getString(R.string.back_pressed), getResources()
                .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel));

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


    public void imageCapturePopup() {
        Log.v("imageCapturePopup", Constants.DELIVERED_TITLE);
        Log.v("requestCamera", " --th " + "requestCameraStoragePermission");
        if (ll_cust_deli.getContentDescription().toString().equalsIgnoreCase(getResources().getString(R.string
                .custdevelivery)) && Constants.DELIVERED_TITLE.equals(getResources().getString(R.string.custdevelivery))) {
            Log.v("imagecapture", "delivery proof" + "-" + Constants.DELIVERED_TITLE);
            imageCapture = "CD";
            cameraImageCapture(CUSTOMER_DELIVERY_CODE, getString(R.string.delivery_proof), DELIVERY, deliveryConfirm.getDeliveryProof(), file_path +
                    deliveryConfirm.getDeliveryProof());
            Constants.DELIVERED_TITLE = getResources().getString(R.string.invoice);

        } else if (ll_imag_invoice.getContentDescription().toString().equalsIgnoreCase(getResources().getString(R.string
                .invoice)) && Constants.DELIVERED_TITLE.equals(getResources().getString(R.string.invoice))) {
            imageCapture = "IN";
            Log.v("imagecapture", "invoice" + "-" + Constants.DELIVERED_TITLE);
            cameraImageCapture(INVOICE_CODE, getString(R.string.invoice_proof), INVOICE, deliveryConfirm.getInvoiceProof(),
                    file_path + deliveryConfirm.getInvoiceProof());
            Constants.DELIVERED_TITLE = getResources().getString(R.string.addressprof); //removed-->

        } else if (ll_cust_addr.getContentDescription().toString().equalsIgnoreCase(getResources().getString(R.string
                .addressprof)) && Constants.DELIVERED_TITLE.equals(getResources().getString(R.string.addressprof))) {
            imageCapture = "CA";
            cameraImageCapture(ADDRESS_PROOF_CODE, getString(R.string.addressprof), ADDRESS, deliveryConfirm.getIdProff(),
                    file_path + deliveryConfirm.getIdProff
                            ());
            Constants.DELIVERED_TITLE = getResources().getString(R.string.sign);

        } else if (signRoot.getContentDescription().toString().equalsIgnoreCase(getResources().getString(R.string
                .sign)) && Constants.DELIVERED_TITLE.equals(getResources().getString(R.string.sign))) {
            Log.v("in_here", "signaturePad");
            if (deliveryConfirm.getSignatureProof().equals("")) {
                signaturePad();
            }
        }


    }


    public Bitmap applyWaterMarkEffect(Bitmap src, String watermark, int x, int y, int color, int alpha, int size, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(watermark, x, y, paint);
        return result;
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

    public void getCustValue() {
        Cursor count = database.rawQuery("select * from DeliveryConfirmation where shipmentnumber='"
                + shipmentNumber + "'", null);
        if (count.getCount() > 0) {
            count.moveToFirst();
//            Log.v("getphno", "--" + count.getString(count.getColumnIndex("customer_contact_number")));
//            Log.v("getphno", "--" + count.getString(count.getColumnIndex("customer_name")));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

        }

//        Log.e("Onstart", "MainonStart");

        /**
         * This is for tracking the classes when user working onit an app
         */

        /*Get the current activity name*/
        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "1", shipmentNumber);
    }


    public Boolean updateComplete() {
        Log.v("updateComplete", shipmentNumber);
       /* database.execSQL("UPDATE orderheader set sync_status = 'C' where Shipment_Number ='" +
                shipmentNumber + "' ");*/

        String bulkOrderStr = updateBulkOrders();

        database.execSQL("UPDATE orderheader set sync_status = 'C' where Shipment_Number IN (" + bulkOrderStr + ") AND delivery_to='1'");


  /*      database.execSQL("UPDATE orderheader set delivery_status = 'U' where shipmentnumber IN " +
                "((SELECT Shipment_Number FROM orderheader WHERE Shipment_Number = '" + shipmentNumber + "')) ");*/

        return true;
    }

    public void uploadComplete() {


//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
        String currentDateTimeString = format.format(new Date());
        Log.v("currentDateTimeString22", currentDateTimeString);

        dialogLoading = new ProgressIndicatorActivity(BFILBulkDelivery.this);
        dialogLoading.showProgress();
//        Log.v("uploadImage","uploadImage"+ ship_number);
        if (updateComplete()) {

            Cursor customerName = database.rawQuery("select IFNULL(D.bulk_Shipment_append,0) as bulk_Shipment_append,O.sync_status,O.delivery_status, O.order_number, O" +
                    ".valid, O.attempt_count,O.order_type,IFNULL(O.payment_mode,0) as payment_mode,  D.sno as sno,IFNULL(D.redirect,0) as redirect,IFNULL(D.reason,0) as reason,IFNULL(D.shipmentnumber,0) as " +
                    "shipmentnumber, " + "IFNULL" + "(D" + ".customer_name,0) as customer_name,IFNULL(O" +
                    ".referenceNumber,0) as referenceNumber,IFNULL(D.amount_collected,0) as amount_collected,IFNULL(O" +
                    ".invoice_amount,0) as invoice_amount,IFNULL(D.customer_contact_number  ,0) as " +
                    "customer_contact_number,IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city," +
                    "0) as shipping_city,IFNULL(D.Invoice_proof,0) as Invoice_proof,IFNULL(D.delivery_proof, 0) as " +
                    "delivery_proof, IFNULL(D.relation_proof, 0) as relation_proof, IFNULL(D.received_by, 0) as received_by, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof,0) as signature_proof," +
                    "IFNULL(D.sync_status,0) as sync_status,IFNULL(D.latitude,0) as latitude,IFNULL(D.longitude,0) as" +
                    " longitude, IFNULL(D.pin_code,0) as pin_code,IFNULL(D.adhaar_details,0) as adhaar_details, " +
                    "IFNULL(D.landmark,'') as landmark, IFNULL(D.customer_contact_number,0) as phone, IFNULL(D.created_at,0) as created_at,IFNULL(D.feed_back,0) as feed_back,IFNULL(D.verify,0) as verify, IFNULL(D.neft,'') as neft,IFNULL(D.aadhar_voter_type,'') as proof_type  from orderheader O  LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number  where O.sync_status='C' AND O.delivery_to='1' AND D.shipmentnumber = '" + shipmentNumber + "' ", null);

            if (customerName.getCount() > 0) {
                customerName.moveToFirst();
                Log.v("get_reason", "--" + customerName.getString(customerName.getColumnIndex("shipmentnumber")));
                partial_shipAddress = customerName.getString(customerName.getColumnIndex("shipmentnumber"));
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                PartialReq partialPelivery = new PartialReq();
                PartialReq.FieldData fieldData = new PartialReq.FieldData();

                JSONObject paramObject = null; // Main JSON Object
                JSONObject jsonFieldObj; // FieldData JSON Object
                JSONArray jsonDetailsArray; // Details JSON Array
                JSONObject jsonAmountCollected; // Amount Collected JSON Object
                JSONObject jsonDummy;
                JSONArray jsonItemCodeArray; // Itemcode JSON Array
                JSONObject jsonItemCodeObject; // Itemcode JSON Object
                JSONObject jsonProofFieldObj;
                JSONObject jsonPickObj; // PickData JSON Object
                Log.v("upload_custname", customerName.getString(customerName.getColumnIndex("customer_name")));
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


                // partialPelivery.setShipmentnumber(customerName.getString(customerName.getColumnIndex("shipmentnumber")));

                updateBulkShipAppend = customerName.getString(customerName.getColumnIndex("bulk_Shipment_append"));
                partialPelivery.setShipmentnumber(customerName.getString(customerName.getColumnIndex("bulk_Shipment_append")));


                partialPelivery.setOrderNo(customerName.getString(customerName.getColumnIndex("order_number")));
                partialPelivery.setAadhaarDetails(customerName.getString(customerName.getColumnIndex("adhaar_details")));
                partialPelivery.setAttempt(customerName.getInt(customerName.getColumnIndex("attempt_count")));
                partialPelivery.setRedirect(customerName.getString(customerName.getColumnIndex("redirect")));
                partialPelivery.setReason(customerName.getString(customerName.getColumnIndex("reason")));
                payment_mode = customerName.getString(customerName.getColumnIndex("payment_mode"));
                partialPelivery.setFeedback(customerName.getString(customerName.getColumnIndex("feed_back")));
                partialPelivery.setVerify(customerName.getString(customerName.getColumnIndex("verify")));
                partialPelivery.setProof_type(customerName.getString(customerName.getColumnIndex("proof_type")));
                partialPelivery.setReceived_by(customerName.getString(customerName.getColumnIndex("received_by")));
                attempt_count = partialPelivery.getAttempt();
                attempt_count++;
//                Log.v("delivery_status", customerName.getString(customerName.getColumnIndex("delivery_proof")));

                if (customerName.getString(customerName.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {
                    fieldData.setAmountCollected(customerName.getString(customerName.getColumnIndex("amount_collected")));
                    partialPelivery.setModeType("Cash");
                    partialPelivery.setAmount_tot(customerName.getString(customerName.getColumnIndex("order_number")));
                    partialPelivery.setTransactionNum("N/A");
                    partialPelivery.setRemarks("N/A");
                    partialPelivery.setReceipt("N/A");
                    partialPelivery.setOriginalAmount(customerName.getString(customerName.getColumnIndex("invoice_amount")));
//                    getsm();

                    partialPelivery.setActualAmount(customerName.getString(customerName.getColumnIndex("amount_collected")));
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
                    paramObject.put("lastTransactionTime", currentDateTimeString);
                    paramObject.put("erpPushTime", customerName.getString(customerName.getColumnIndex("valid")));
                    paramObject.put("transactionDate", currentDateTimeString);
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
                    paramObject.put("order_type", customerName.getString(customerName.getColumnIndex("order_type")));
                    paramObject.put("proof_type", partialPelivery.getProof_type());
                    paramObject.put("received_by", partialPelivery.getReceived_by());
//                    Log.v("", partialPelivery.getRedirect());

                    if (!customerName.getString(customerName.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {

                        jsonFieldObj.put("customer_delivery_proof", image_url + partialPelivery.getDeliveryproof());
                        jsonFieldObj.put("invoice", image_url + partialPelivery.getInvoiceproof());
                        jsonDummy.put("relation", image_url + partialPelivery.getRelationproof());
                        jsonFieldObj.put("signproof", image_url + partialPelivery.getSignproof());
                        jsonFieldObj.put("govt_id_proof", image_url + partialPelivery.getAddressproof());
                        jsonFieldObj.put("phone_no", partialPelivery.getPhone());
                        jsonFieldObj.put("aadhaar_card", partialPelivery.getAadhaarDetails());
                        jsonFieldObj.put("name", partialPelivery.getCustomer());
                        jsonFieldObj.put("pincode", partialPelivery.getPincode());
//                        jsonFieldObj.put("amount_collected", fieldData.getAmountCollected());

                        jsonAmountCollected.put("Original_Amount", customerName.getString(customerName.getColumnIndex("invoice_amount")));
                        jsonAmountCollected.put("Actual_Amount", customerName.getString(customerName.getColumnIndex("amount_collected")));
                        jsonFieldObj.put("amount_collected", jsonAmountCollected);
//                        jsonFieldObj.put("amount_collected", fieldData.getAmountCollected());
                        paramObject.put("fieldData", jsonFieldObj);
                    }


                    if (customerName.getString(customerName.getColumnIndex("order_type")).equalsIgnoreCase("3")) {

                        Cursor getPickup = database.rawQuery("Select orderno,shipmentno,customername,customerphone,customeraddress,customerphoto,pickup_completed,pickupstatus,createdate,IFNULL(latitude,0 ) as latitude, IFNULL(longitude,0) as longitude  from PickupConfirmation where shipmentno = '" + shipmentNumber + "' ", null);
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
                                            "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected, IFNULL(partial_reason, '') as partial_reason, IFNULL(r_id, 0) as r_id   from ProductDetails where shipmentnumber = '" + shipmentNumber + "' AND pickup_type = 1  ", null);
                                    JSONArray array = new JSONArray();

                                    if (getOrders.getCount() > 0) {
                                        getOrders.moveToFirst();
                                        ArrayList<String> list1 = new ArrayList<String>();
                                        while (!getOrders.isAfterLast()) {

                                            JSONObject list2 = new JSONObject();

                                            try {

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


//                                arrayPick.put(picklist);
//                                jsonDummy.put("pickup", arrayPick);

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
                        jsonDummy.put("signproof", image_url + partialPelivery.getSignproof());
                        jsonDummy.put("govt_id_proof", image_url + partialPelivery.getAddressproof());
                        jsonDummy.put("name", partialPelivery.getCustomer());
                        jsonDummy.put("reason", partialPelivery.getReason());
//                        paramObject.put("fieldData", jsonProofFieldObj);
                        Cursor getOrders = database.rawQuery("Select IFNULL(delivery_qty,0) as delivery_qty, IFNULL(product_code, 0) as product_code, IFNULL(product_name, '') as product_name," +

                                "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected, IFNULL(partial_reason, '') as partial_reason, IFNULL(r_id, 0) as r_id   from ProductDetails where shipmentnumber = '" + shipmentNumber + "' AND pickup_type = 0 ", null);

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


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v("fielddate", paramObject.toString());
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                final Observable<PartialResp> observable = apiService.getBulkBfilDeliverySync(requestBody)
                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<PartialResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PartialResp value) {

                        List<PartialResp> orderVal = value.getPartialDelivery();
//                        Log.v("upload_response", "--" + "response");
                        Log.v("delact_response", value.getRes_msg() + " -" + value.getRes_code());
//                        uploadImage();
                        if (value.getRes_msg().equals("upload success")) {
                       /* database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number ='" +
                                shipmentNumber + "' ");*/
                            /**** added on 10-08-2018 13.32 hr ****/


                            database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '" + attempt_count + "' where Shipment_Number IN ("+ updateBulkShipAppend +") AND delivery_to='1' ");
                            attempt_count = 0;
                            deleteOlderRecords(shipmentNumber);
//                        alertDialogMsg(BFILBulkDelivery.this, "Success", "Shipment Delivered successfully", "Ok");
                            alertDialogMsg(BFILBulkDelivery.this, "Success", getResources().getString(R.string
                                    .delivery_success_msg), "Ok");

                            AppController.clearKey(Constants.CUR_LATITUDE);
                            AppController.clearKey(Constants.CUR_LONGITUDE);
                            AppController.clearKey(Constants.LATITUDE);
                            AppController.clearKey(Constants.LONGITUDE);
                            startService(new Intent(BFILBulkDelivery.this, SyncService.class));
                            dialogLoading.dismiss();
//                            uploadImage(partial_shipAddress);
//                            Logger.showShortMessage(BFILBulkDelivery.this,value.getRes_msg());
                        } else if (value.getRes_msg().equals("already delivered")) {
                            Logger.showShortMessage(BFILBulkDelivery.this, getResources().getString(R.string
                                    .upload_failed));
                            database.execSQL("UPDATE orderheader set sync_status = 'E' where Shipment_Number ='" +
                                    shipmentNumber + "' ");
                            alertDialogMsgOffline(BFILBulkDelivery.this, "Failed", getResources().getString(R.string
                                    .shipment_already_delivered), "Ok");
                            dialogLoading.dismiss();


                        }

//                        uploadImage();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("upload_response", "error" + e.toString());
                        dialogLoading.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        dialogLoading.dismiss();
                        Log.v("upload_response", "--" + "in here");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
            } else {
                //dialogLoading.dismiss();
            }
            customerName.close();
        }
//        }
    }


    public void uploadImage() {
        dialogLoading = new ProgressIndicatorActivity(BFILBulkDelivery.this);
        dialogLoading.showProgress();
//        Log.v("uploadImage","uploadImage"+ ship_number);
        if (updateComplete()) {

            Cursor customerName = database.rawQuery("select  O.delivery_status,O.order_type, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber ,IFNULL(D.customer_name,0) as customer_name,IFNULL" +
                    "(D.amount_collected,0) as amount_collected,IFNULL(D.customer_contact_number,0) as customer_contact_number," +
                    "IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                    ", IFNULL(D.delivery_proof, 0) as delivery_proof,IFNULL(D.relation_proof, '') as relation_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof," +
                    "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D.latitude,0) as latitude,IFNULL" +
                    "(D.longitude,0) as longitude" + ",IFNULL(D.received_by, 0) as received_by from orderheader O LEFT JOIN DeliveryConfirmation D on D" +
                    ".shipmentnumber = O.Shipment_Number where O" +
                    ".sync_status='C' AND D.shipmentnumber = '" + shipmentNumber + "' ", null);

            if (customerName.getCount() > 0) {
                customerName.moveToFirst();
                partial_shipAddress = customerName.getString(customerName.getColumnIndex("shipmentnumber"));
                String file_deliveryProof = file_path + customerName.getString(customerName.getColumnIndex("delivery_proof"));
                String file_addressProof = file_path + customerName.getString(customerName.getColumnIndex("id_proof"));
                String file_invoiceProof = file_path + customerName.getString(customerName.getColumnIndex("Invoice_proof"));
                String file_relationproof = file_path + customerName.getString(customerName.getColumnIndex("relation_proof"));
                String file_signature = sign_path + customerName.getString(customerName.getColumnIndex("signature_proof"));
                String received_by = sign_path + customerName.getString(customerName.getColumnIndex("received_by"));
                Log.v("delivery_proof", file_invoiceProof);
                String file_pickup = null;

                if (customerName.getString(customerName.getColumnIndex("order_type")).equalsIgnoreCase("3")) {

                    Cursor getPickup = database.rawQuery("Select customerphoto  from PickupConfirmation where shipmentno = '" + shipmentNumber + "' ", null);


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


                /**** Get Signature Proof Image****/
                File fileSignProof = new File(file_signature);
                RequestBody requestBodySign = RequestBody.create(MediaType.parse("*/*"), fileSignProof);
                MultipartBody.Part fileToSign = MultipartBody.Part.createFormData("sign_file", fileSignProof.getName(), requestBodySign);
                MultipartBody.Part fileToPickup;
                if (order_type.equals("3")) {
                    /**** Get Pickup Proof Image****/
                    File filePickupProof = new File(file_pickup);
                    RequestBody requestBodyPickup = RequestBody.create(MediaType.parse("*/*"), filePickupProof);
//                    MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                    fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                } else {
                    File filePickupProof = new File(file_signature);
                    RequestBody requestBodyPickup = RequestBody.create(MediaType.parse("*/*"), filePickupProof);
//                    MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                    fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);
                }
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


                final Observable<DeliveryConfirmResp> observable = apiService.getDeliveryImage(fileToDelivery, fileToInvoice, fileToAddress, fileToRelation, fileToSign, fileToPickup)


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
                        Log.v("DeliveryConfirmResp", deliveryVal.getRes_msg());
                        if (deliveryVal.getRes_msg().equals("image success")) {

                            database.execSQL("UPDATE orderheader set image_status = 'U' where Shipment_Number ='" +
                                    shipmentNumber + "' ");
                            //   uploadComplete(shipmentNumber); /*Removed 28-5-19*/

//                            dialogLoading.dismiss();

                        } else if (deliveryVal.getRes_msg().equals("image failed")) {
//                            dialogLoading.dismiss();

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("upload_image_error", e.toString());
                        dialogLoading.dismiss();
                        alertDialogMsgOffline(BFILBulkDelivery.this, "Success", getString(R.string.delivery_offline), "Ok");
                    }

                    @Override
                    public void onComplete() {
                    }


                });
            }


        }
    }


    public void getAmountCollected(String ship_no) {


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
            paramObject.put("shipment_id", ship_no);

            Log.v("paramObject", paramObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());


        final Observable<AttemptResp> observable = apiService.getAmountCollected(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<AttemptResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AttemptResp value) {
                attemptList = new ArrayList<>();
                List<AttemptResp> detailVal = value.getAttemptVal();
                List<AttemptResp> totalAmountArray = value.getTotalAmount();
                Log.v("attempt_succ", value.getResMsg());
                if (value.getResMsg().equals("attempt success")) {

                    for (int i = 0; i < detailVal.size(); i++) {
                        Cursor checkOrder = database.rawQuery("Select * from orderheader where Shipment_Number = '" +
                                        detailVal.get(i).getShipmentid() + "'",
                                null);

                        for (int j = 0; j < detailVal.get(i).getTotalAmount().size(); j++) {

                            Log.v("get_getRow_total", "" + detailVal.get(i).getTotalAmount().get(j).getRow_total());
                            Log.v("get_getRow_prod", detailVal.get(i).getTotalAmount().get(j).getSku());
                            String updateProducts = "UPDATE ProductDetails set total_amount = '" + detailVal.get(i).getTotalAmount().get(j).getRow_total() + "' where shipmentnumber = '" + detailVal.get(i).getShipmentid() + "' AND product_code = '" + detailVal.get(i).getTotalAmount().get(j).getSku() + "' ";
                            database.execSQL(updateProducts);

                        }
                        checkOrder.close();
                    }


                } else if (value.getResMsg().equals("attempt failed")) {

                } else {

                }

            }

            @Override
            public void onError(Throwable e) {
                Log.d("error", e.toString());
            }

            @Override
            public void onComplete() {
                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }


    /**
     * Gps will be enable automatically
     */
    private void EnableGPSAutoMatically() {
        googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(BFILBulkDelivery.this)
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


                @SuppressLint("MissingPermission")
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                            int permissionLocation = ContextCompat
                                    .checkSelfPermission(BFILBulkDelivery.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

                                mLastLocation = LocationServices.FusedLocationApi
                                        .getLastLocation(googleApiClient);
                            }

                            buildGoogleApiClient();
                            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            createLocationRequest();
                            getLocation();
                            successFlag = true;
                            break;


                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                // Ask to turn on GPS automatically
                                status.startResolutionForResult(BFILBulkDelivery.this,
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


    /**
     * Method to display the location on UI
     */

    @SuppressLint("MissingPermission")
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
        mGoogleApiClient = new GoogleApiClient.Builder(BFILBulkDelivery.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
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
                            status.startResolutionForResult(BFILBulkDelivery.this, REQUEST_FIRST_CHECK_SETTINGS);

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
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("connection failed", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }


    @SuppressLint("MissingPermission")
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

        if (mLastLocation == null) {
            // startLocationUpdates(); // bind interface if your are not getting the lastlocation. or bind as per your
            // requirement.
        }

        if (mLastLocation != null) {
            while (latitude_user == 0 || longitude_user == 0) {
                latitude_user = mLastLocation.getLatitude();
                longitude_user = mLastLocation.getLongitude();
//Log.v("mLastLocation","--"+ mLastLocation.getLatitude()+"-"+mLastLocation.getLongitude());
//                Toast.makeText(BFILBulkDelivery.this, ""+mLastLocation.getLatitude()+"-"+mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();
                if (latitude_user != 0 && longitude_user != 0) {
                    //  stopLocationUpdates(); // unbind the locationlistner here or wherever you want as per your requirement.
                    // location data received, dismiss dialog, call your method or perform your task.
                }
            }
        }


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


        /**
         * Get the location of activity
         */
        lat = Double.longBitsToDouble(AppController.getLongPreference(this, Constants.LATITUDE, -1));
        lang = Double.longBitsToDouble(AppController.getLongPreference(this, Constants.LONGITUDE, -1));
        String lati = String.valueOf(lat);
        String longi = String.valueOf(lang);
        if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lang) && lat != 0.0 && lang != 0.0) {
//            Log.e("pref", "pref");
            getAddresss(lat, lang);

            deliveryConfirm.setLatitude(lati);
            deliveryConfirm.setLongitude(longi);

            latCheck = lat;
            lonCheck = lang;


        } else {
//            Log.e("natural", "natural");
            // Logger.showShortMessage(this, getResources().getString(R.string.gps_signal));


            latCheck = latitude_user;
            lonCheck = longitude_user;
            if (String.valueOf(latCheck) != null && String.valueOf(lonCheck) != null && !Double.isNaN(latCheck) &&
                    !Double.isNaN(lonCheck) && latCheck != 0.0 && lonCheck != 0.0) {
                deliveryConfirm.setLatitude(String.valueOf(latCheck));
                deliveryConfirm.setLongitude(String.valueOf(lonCheck));
                getAddresss(latCheck, lonCheck);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @SuppressLint("MissingPermission")
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

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (locationFindAddress != null)
                    locationFindAddress.setText(currentLocation);

                String add = locationAddress.getAddressLine(0);
            }
        } else {
            //  Logger.showShortMessage(this, "Location Not Avaliable");
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

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(20);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.mBatInfoReceiver);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
//        Log.e("Onstop", "MainonStop");
        /*Get the current activity name*/


        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "0", "0");
    }

    public void getsm() {
        JSONObject jsonObject = new JSONObject();
        Cursor getOrders = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" + shipmentNumber + "' ", null);

        if (getOrders.getCount() > 0) {
            getOrders.moveToFirst();
            while (!getOrders.isAfterLast()) {
//                    Log.v("getOrdersprods", getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
//
                for (int i = 0; i < getOrders.getCount(); i++) {
//                    for (int j = 0; j < getOrders.getCount(); j++)

                }
                getOrders.moveToNext();
            }
            while (!getOrders.isAfterLast()) {
                List<String> list = new ArrayList<String>();
                list.add(getOrders.getString(getOrders.getColumnIndex("quantity")));
                list.add(getOrders.getString(getOrders.getColumnIndex("product_code")));
                list.add(getOrders.getString(getOrders.getColumnIndex("product_name")));
                list.add(getOrders.getString(getOrders.getColumnIndex("delivery_qty")));
                list.add(getOrders.getString(getOrders.getColumnIndex("amount_collected")));
                for (int i = 0; i < list.size(); i++) {
//                    for (int j = 0; j < getOrders.getCount(); j++)
//                    Log.v("example_test", "--" + list.get(i));

                }
                getOrders.moveToNext();
            }


        }
        getOrders.close();
    }


    public String[] getContacts(String ship) {
        Cursor cursor = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" + ship + "' ", null);
//        Cursor cursor = getReadableDatabase().rawQuery("SELECT name FROM contacts", null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            names.add(cursor.getString(cursor.getColumnIndex("product_name")));
//            Log.v("getContacts", cursor.getString(cursor.getColumnIndex("product_name")));
            cursor.moveToNext();
        }
//        Log.v("getContacts1", String.valueOf(names));
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public String[] getrest() {
        Cursor cursor = database.rawQuery("Select * from ProductDetails where shipmentnumber = '" + shipmentNumber + "' ", null);
//        Cursor cursor = getReadableDatabase().rawQuery("SELECT name FROM contacts", null);
        String[] names = {""};
        for (int i = 0; i < cursor.getCount(); i++) {
            names[i] = cursor.getString(i);
        }
        cursor.close();
        return names;
    }

    public void getsampjson() {

        JSONArray obj = new JSONArray();
        try {
            for (int i = 0; i < 3; i++) {
                // 1st object
                JSONObject list1 = new JSONObject();
                list1.put("val1", i + 1);
                list1.put("val2", i + 2);
                list1.put("val3", i + 3);
                obj.put(list1);
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


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


    public void exitAlert(String title, String msg) {

        final Dialog dialog1 = new Dialog(BFILBulkDelivery.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.alertbox);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;


        Button yes = (Button) dialog1.findViewById(R.id.proceed);
        Button no = (Button) dialog1.findViewById(R.id.close);
        TextView txt_ale = (TextView) dialog1.findViewById(R.id.txt_title);
        TextView txt_msg = (TextView) dialog1.findViewById(R.id.txt_message);

        txt_ale.setText(title);
        yes.setText(R.string.yes);
        no.setText(R.string.no);
        txt_msg.setText(msg);


        ImageView imag_icon = (ImageView) dialog1.findViewById(R.id.boon);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                String currentDateTimeString = format.format(new Date());
                Constants.DELIVERED_TITLE = "name";
                Cursor count = database.rawQuery("select * from DeliveryConfirmation where shipmentnumber='"
                        + shipmentNumber + "'", null);
                if (count.getCount() == 0) {
                    if (!deliveryConfirm.getCustomerName().equalsIgnoreCase("") && !deliveryConfirm.getShipAddress()
                            .equalsIgnoreCase("") && !deliveryConfirm
                            .getPincode().equalsIgnoreCase("") && !deliveryConfirm.getCustomerContactNumber().equalsIgnoreCase("")) {
                        String deliverDetailsInsert = "Insert into DeliveryConfirmation (shipmentnumber,shipping_address,customer_name," +
                                "customer_contact_number," +
                                "Invoice_proof,delivery_proof, id_proof, signature_proof" +
                                ",amount_collected,sync_status,latitude,longitude,pin_code,redirect,adhaar_details, created_at,feed_back,verify)" + " VALUES ('" +
                                shipmentNumber + "','" +
                                deliveryConfirm.getShipAddress() + "','" + deliveryConfirm.getCustomerName() + "','" + deliveryConfirm
                                .getCustomerContactNumber() + "','" + deliveryConfirm.getInvoiceProof() + "', " +
                                "'" + deliveryConfirm.getDeliveryProof() + "','" + deliveryConfirm.getIdProff() + "', '" + deliveryConfirm.getSignatureProof() + "' ,'" + deliveryConfirm.getAmountCollected() + "','"
                                + "P" + "','" + deliveryConfirm.getLatitude() + "','" + deliveryConfirm.getLongitude() + "','" +
                                deliveryConfirm.getPincode
                                        () +
                                "','" + deliveryConfirm
                                .getRedirect() + "','" + deliveryConfirm
                                .getAdhaarDetails() + "', datetime('now'),'" + deliveryConfirm.getFeed_back() + "','" + deliveryConfirm.getVerify() + "')";
                        database.execSQL(deliverDetailsInsert);
                    }
                } else if (count.getCount() > 0) {

                    String deliveryDetailsupdate = "UPDATE DeliveryConfirmation set customer_name='" + deliveryConfirm.getCustomerName() +
                            "'," + "customer_contact_number='" + deliveryConfirm.getCustomerContactNumber() + "'," +
                            "shipping_address='" + deliveryConfirm.getShipAddress() + "',amount_collected='"
                            + deliveryConfirm.getAmountCollected() + "',adhaar_details='"
                            + deliveryConfirm.getAdhaarDetails() + "'," +
                            "pin_code='" + deliveryConfirm.getPincode() + "',id_proof='" + deliveryConfirm.getIdProff() + "',delivery_proof='" + deliveryConfirm.getDeliveryProof() + "'," +
                            "Invoice_proof='" + deliveryConfirm.getInvoiceProof() + "',signature_proof='" + deliveryConfirm
                            .getSignatureProof() + "',redirect='" + deliveryConfirm.getRedirect() + "',sync_status='" + "P" + "'," +
                            "latitude='" + deliveryConfirm.getLatitude() + "',longitude='" + deliveryConfirm.getLongitude() + "', created_at = datetime('now'),feed_back='" + deliveryConfirm.getFeed_back() + "',verify='" + deliveryConfirm.getVerify() + "'" +
                            " where shipmentnumber ='" + shipmentNumber + "' ";
                    database.execSQL(deliveryDetailsupdate);
                }
                count.close();
//                dialog1.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                return;
            }
        });
        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                        Constants.DELIVERED_TITLE = "name";
                        deliveryConfirm.setVerify("");

                        String bulkOrders = getBulkOrders();
                        Cursor count = database.rawQuery("select * from DeliveryConfirmation where shipmentnumber='"
                                + shipmentNumber + "'", null);
                        if (count.getCount() == 0) {
                            if (!deliveryConfirm.getCustomerName().equalsIgnoreCase("") && !deliveryConfirm.getShipAddress()
                                    .equalsIgnoreCase("") && !deliveryConfirm
                                    .getPincode().equalsIgnoreCase("") && !deliveryConfirm.getCustomerContactNumber().equalsIgnoreCase("")) {
                                String deliverDetailsInsert = "Insert into DeliveryConfirmation (bulk_Shipment_append,shipmentnumber,shipping_address,customer_name," +
                                        "customer_contact_number," +
                                        "Invoice_proof,delivery_proof, id_proof, signature_proof" +
                                        ",amount_collected,sync_status,latitude,longitude,pin_code,redirect,adhaar_details, created_at,feed_back,verify,neft,relation_proof,received_by,aadhar_voter_type)" + " VALUES ('" + bulkOrders + "','" +
                                        shipmentNumber + "','" +
                                        deliveryConfirm.getShipAddress() + "','" + deliveryConfirm.getCustomerName() + "','" + deliveryConfirm
                                        .getCustomerContactNumber() + "','" + deliveryConfirm.getInvoiceProof() + "', " +
                                        "'" + deliveryConfirm.getDeliveryProof() + "','" + deliveryConfirm.getIdProff() + "', '" + deliveryConfirm.getSignatureProof() + "' ,'" + deliveryConfirm.getAmountCollected() + "','"
                                        + "P" + "','" + deliveryConfirm.getLatitude() + "','" + deliveryConfirm.getLongitude() + "','" +
                                        deliveryConfirm.getPincode
                                                () +
                                        "','" + deliveryConfirm
                                        .getRedirect() + "','" + deliveryConfirm
                                        .getAdhaarDetails() + "', datetime('now'),'" + deliveryConfirm.getFeed_back() + "','" + deliveryConfirm.getVerify() + "','" + deliveryConfirm.getNeft() + "','" + deliveryConfirm.getRelationProof() + "','" + deliveryConfirm.getOtherSelfType() + "','" + deliveryConfirm.getVoterOrAadharType() + "')";
                                database.execSQL(deliverDetailsInsert);
                            }
                        } else if (count.getCount() > 0) {

                            String deliveryDetailsupdate = "UPDATE DeliveryConfirmation set customer_name='" + deliveryConfirm.getCustomerName() +
                                    "'," + "customer_contact_number='" + deliveryConfirm.getCustomerContactNumber() + "'," +
                                    "shipping_address='" + deliveryConfirm.getShipAddress() + "',amount_collected='"
                                    + deliveryConfirm.getAmountCollected() + "',adhaar_details='"
                                    + deliveryConfirm.getAdhaarDetails() + "'," +
                                    "pin_code='" + deliveryConfirm.getPincode() + "'," +
                                    "bulk_Shipment_append='" + bulkOrders + "'," +
                                    "id_proof='" + deliveryConfirm.getIdProff() + "'," +
                                    "relation_proof='" + deliveryConfirm.getRelationProof() + "'," +
                                    "received_by='" + deliveryConfirm.getOtherSelfType() + "'," +
                                    "delivery_proof='" + deliveryConfirm.getDeliveryProof() + "'," +
                                    "Invoice_proof='" + deliveryConfirm.getInvoiceProof() + "',signature_proof='" + deliveryConfirm
                                    .getSignatureProof() + "',redirect='" + deliveryConfirm.getRedirect() + "',sync_status='" + "P" + "'," +
                                    "latitude='" + deliveryConfirm.getLatitude() + "',longitude='" + deliveryConfirm.getLongitude() + "' , created_at = datetime('now'),feed_back='" + deliveryConfirm.getFeed_back() + "',verify='" + deliveryConfirm.getVerify() + "',neft = '" + deliveryConfirm.getNeft() + "',aadhar_voter_type = '" + deliveryConfirm.getVoterOrAadharType() + "'" +
                                    " where shipmentnumber ='" + shipmentNumber + "' ";
                            database.execSQL(deliveryDetailsupdate);
                        }
                        count.close();
                        finish();
                    }
                })
                .show();
    }


    public void alertDialogMsg(Context context, String title, String content, String okmsg) {
        boolean setSampFlag = false;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
//        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent goToMain = new Intent(BFILBulkDelivery.this, MainActivity.class);
                        startActivity(goToMain);
                        finish();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
        sweetAlertDialog.setCancelable(false);
    }

    public void alertDialogMsgOffline(Context context, String title, String content, String okmsg) {
        boolean setSampFlag = false;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
//        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent goToMain = new Intent(BFILBulkDelivery.this, MainActivity.class);
                        startActivity(goToMain);
                        finish();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
        sweetAlertDialog.setCancelable(false);
    }

    public void alertDialogPickupProof(Context context, String title, String content, String okmsg) {
        boolean setSampFlag = false;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
//        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
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
        Log.v("requestCamera", " --th " + "requestCameraStoragePermission");
        Permissions.check(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                getResources().getString(R.string.camera_permission), new Permissions
                        .Options()
                        .setSettingsDialogTitle(getResources().getString(R.string.warning)).setRationaleDialogTitle(getResources().getString(R.string.info)),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        imageCapturePopup();
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
                        .setSettingsDialogTitle(getResources().getString(R.string.warning)).setRationaleDialogTitle("Location Permission"),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        EnableGPSAutoMatically();
                        //do your task
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "Camera+Storage Denied:\n",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }


    /***  Update Location after turning on GPS ****/
    private boolean updateLocation(String latitude, String longitude) {
        database.execSQL("UPDATE DeliveryConfirmation set latitude = '" + latitude + "', longitude = '" + longitude + "' where shipmentnumber ='" +
                shipmentNumber + "' ");
        return true;
    }


    private void customerActivity(int customerDeliveryCode, String retake, String headingName, String fileName) {
        Intent intent = new Intent(BFILBulkDelivery.this, CameraFragmentMainActivity.class);
        intent.putExtra("fileName", fileName);
        intent.putExtra("shipmentId", shipmentNumber);
        intent.putExtra("retake", retake);
        intent.putExtra("heading", headingName);
        startActivityForResult(intent, customerDeliveryCode);
    }


    private OkHttpClient getRequestHeader() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.MINUTES)
                .connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();

        return okHttpClient;
    }


    public void deleteOlderRecords(String shipno) {
        String file_path = String.valueOf(this.getFilesDir());

        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.signature_proof,0) as signature_proof, IFNULL(D.id_proof,0) as id_proof, IFNULL(D.Invoice_proof,0) as Invoice_proof, IFNULL(D.created_at,0) as created_at FROM orderheader O   JOIN DeliveryConfirmation D on O.Shipment_Number = D.shipmentnumber where O.Shipment_Number = '" + shipno + "'  AND O.sync_status = 'U' AND O.image_status = 'U' ", null);
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {

                Log.v("deleteOlderRecords", deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));

                File deleteImgDeliverProof = new File(file_path + "/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
                if (deleteImgDeliverProof.exists()) {
                    deleteImgDeliverProof.delete();
                }
//                File deleteImgSignProof = new File(file_path+"/UserSignature/" + deleteOrder.getString(deleteOrder.getColumnIndex("signature_proof")));
                File deleteImgSignProof = new File(file_path + "/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("signature_proof")));
                if (deleteImgSignProof.exists()) {
                    deleteImgSignProof.delete();
                }
                File deleteImgIDProof = new File(file_path + "/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("id_proof")));
                if (deleteImgIDProof.exists()) {
                    deleteImgIDProof.delete();
                }
                File deleteImgInvoiceProof = new File(file_path + "/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("Invoice_proof")));
                if (deleteImgInvoiceProof.exists()) {
                    deleteImgInvoiceProof.delete();
                }

                deleteOrder.moveToNext();
            }

        }
    }


    /**
     * Check the device whether user has changed the date or not if user changed the date throw the alert to change the current
     * date
     */
    private void updatDate() {
//        if (!TrueTimeRx.isInitialized()) {
        if (!TrueTimeRx.isInitialized()) {
            //Toast.makeText(this, "Sorry TrueTime not yet initialized.", Toast.LENGTH_SHORT).show();
            requestLocation();
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            successUploadService();
            return;
        }
        Date trueTime = TrueTimeRx.now();
        Date deviceTime = new Date();

        Log.d("kg",
                String.format(" [trueTime: %d] [devicetime: %d] [drift_sec: %f]",
                        trueTime.getTime(),
                        deviceTime.getTime(),
                        (trueTime.getTime() - deviceTime.getTime()) / 1000F));


        String deviceDate = _formatDate(deviceTime, "yyyy-MM-dd", TimeZone.getTimeZone("GMT+05:30"));
        String networkDate = _formatDate(trueTime, "yyyy-MM-dd", TimeZone.getTimeZone("GMT+05:30"));

        if (deviceDate.equalsIgnoreCase(networkDate)) {
            requestLocation();
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            successUploadService();
        } else {
            Utils.AlertDialogCancel(this, getResources().getString(R.string.exactdate), getResources().getString(R.string.get_date_warn), getResources()
                    .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel));
        }


    }

    private String _formatDate(Date date, String pattern, TimeZone timeZone) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(timeZone);
        return format.format(date);
    }

    private void setLocale(String lang) {

        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration cf = res.getConfiguration();
        cf.locale = myLocale;
        res.updateConfiguration(cf, dm);

        super.onRestart();
//        Intent intent =new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }


    public void getpickupdata() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());
        Log.v("getpickupdata", "- " + storeFilename);

        Cursor count = database.rawQuery("select * from PickupConfirmation where shipmentno='"
                + shipmentNumber + "'", null);

        if (count.getCount() == 0) {
            // String Pickup = "INSERT INTO PickupConfirmation(orderno, shipmentno,customername,customerphone,customeraddress, customerphoto, pickup_completed,pickupstatus) VALUES"

            String Pickupinsert = "Insert into PickupConfirmation (orderno,shipmentno," +
                    "customername," +
                    "customerphone,customeraddress, customerphoto, pickupstatus,pickup_completed,createdate)" + " VALUES ('" + orderID +
                    "','" + shipmentNumber + "','" +
                    deliveryConfirm.getCustomerName() + "','" + deliveryConfirm.getCustomerContactNumber() + "' ,'"
                    + deliveryConfirm.getShipAddress() + "','" + deliveryConfirm.getPickup_image() + "', 'Success','" + pick_completed + "','" + formattedDate + "')";
            database.execSQL(Pickupinsert);

            Log.e("InserData", Pickupinsert);

        } else if (count.getCount() > 0) {

            String PickupUpdate = "UPDATE PickupConfirmation set orderno='" + orderID +
                    "'," + "shipmentno='" + shipmentNumber + "',customername='"
                    + deliveryConfirm.getCustomerName() + "', customerphone='"
                    + deliveryConfirm.getCustomerContactNumber() + "',customeraddress='"
                    + deliveryConfirm.getShipAddress() + "', customerphoto='"
                    + deliveryConfirm.getPickup_image() + "' , pickupstatus ='Success', pickup_completed = '" + pick_completed + "',createdate = '" + formattedDate + "' " +
                    "where shipmentno ='" + shipmentNumber + "' ";
            database.execSQL(PickupUpdate);

            Log.e("InserDate", PickupUpdate);
        }

    }

    @SuppressLint("SimpleDateFormat")
    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void showalert(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(BFILBulkDelivery.this).create();
        alertDialog.setTitle("Boonbox");
        alertDialog.setIcon(R.drawable.icon);
        alertDialog.setMessage(message);
        // Alert dialog button
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();// use dismiss to cancel alert dialog
                    }
                });
        alertDialog.show();
    }

    private boolean updateSyncStatus() {
        if (null == statusSync || !statusSync.equalsIgnoreCase("partial")) {
            /*database.execSQL("UPDATE orderheader set delivery_status = 'delivered' where Shipment_Number ='" +
                    shipmentNumber + "' ");*/
            Cursor uname = database.rawQuery("Select * from orderheader where delivery_status = 'undelivered' AND sync_status = 'U' AND Shipment_Number = '" + shipmentNumber + "' ", null);

            if (uname.getCount() > 0) {
                database.execSQL("UPDATE orderheader set delivery_status = 'delivered', sync_status = 'P' where Shipment_Number ='" + shipmentNumber + "' ");
                return true;
            } else {
                Cursor unameOffline = database.rawQuery("Select * from orderheader where delivery_status = 'undelivered' AND sync_status = 'C' AND Shipment_Number = '" + shipmentNumber + "' ", null);

                if (unameOffline.getCount() > 0) {
                    alertDialogMsgOffline(BFILBulkDelivery.this, "Error", "Delivery cannot be made at this moment", "Ok");
                    return false;
                } else {
                    database.execSQL("UPDATE orderheader set delivery_status = 'delivered' where Shipment_Number ='" +
                            shipmentNumber + "' ");
                    return true;
                }
            }
        }
        return true;
    }


    public void editBoxDocumentAlert() {

        final Dialog elAlertdialog = new Dialog(this);
        elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        elAlertdialog.setContentView(R.layout.voterid_alert);
        elAlertdialog.show();

        final AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
        final AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
        final AppCompatEditText name = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
        final TextInputLayout txt_cust_name = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);

        Cursor getReturnValue = database.rawQuery("Select IFNULL(adhaar_details, null ) as adhaar_details from DeliveryConfirmation where shipmentnumber ='" + shipmentNumber + "' ", null);
        getReturnValue.moveToFirst();
        if (getReturnValue.getCount() > 0) {
            fetchVal = getReturnValue.getString(getReturnValue.getColumnIndex("adhaar_details"));
            if (fetchVal.contains("<")) {
                name.setText("");
                editvalue = "";
            } else {
                name.setText(fetchVal);
                editvalue = fetchVal;
            }

        }
        getReturnValue.close();
//                Log.v("fetchVal","- "+fetchVal);

        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence qty, int i, int i1, int i2) {
                String text_val = qty.toString();
                if (text_val.equals("")) {
                    text_val = null;
                }
                editvalue = text_val;


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Log.v("editvalue", " = " + editvalue + "- " + shipmentNumber);
                if (editvalue == null || editvalue.equals("null")) {

                    txt_cust_name.setErrorEnabled(true);
                    txt_cust_name.setError("Enter Valid Voter ID No.");
                    txt_cust_name.requestFocus();
                } else if (editvalue.length() < 10) {
                    txt_cust_name.setErrorEnabled(true);
                    txt_cust_name.setError("Enter Valid Voter ID No.");
                    txt_cust_name.requestFocus();
                } else {
                    deliveryConfirm.setAdhaarDetails(editvalue);
                    database.execSQL("UPDATE DeliveryConfirmation set adhaar_details = '" + deliveryConfirm.getAdhaarDetails() + "' where shipmentnumber ='" +
                            shipmentNumber + "'  ");
                    adhaaTxt.setText(deliveryConfirm.getAdhaarDetails());
                    tv_proofLabel.setText("Voter ID No.");
                    if (!deliveryConfirm.getAdhaarDetails().equals("")) {
                        completeMainRoot.setVisibility(View.VISIBLE); // complete parent layout commented out
                        completeRoot.setVisibility(VISIBLE);
                        completeRoot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_main));
                    }
                    new CountDownTimer(500, 1) {
                        public void onTick(long millisUntilFinished) {
                            scroll.scrollTo(0, R.id.scroll);
                        }

                        public void onFinish() {
                        }
                    }.start();
                    elAlertdialog.dismiss();

                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                elAlertdialog.dismiss();

            }
        });

    }

    public boolean isAlphaNumeric(String s) {
        String pattern = "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_aadhar:
                sp_proof.setText("Aadhar");
                deliveryConfirm.setVoterOrAadharType("Aadhar");
                iv_delAadharScan.setVisibility(VISIBLE);
                iv_delVoterOcr.setVisibility(GONE);
                input_voterid.setText("");
                input_voterid.setInputType(2);
                int maxLength = 12;
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                input_voterid.setFilters(FilterArray);
                return true;
            case R.id.menu_voter:


                sp_proof.setText("Voter ID");
                deliveryConfirm.setVoterOrAadharType("Voter ID");
                iv_delAadharScan.setVisibility(GONE);
                iv_delVoterOcr.setVisibility(VISIBLE);
                input_voterid.setText("");
                input_voterid.setInputType(InputType.TYPE_CLASS_TEXT);

//                input_voterid.setKeyListener(new DigitsKeyListener().getInstance("qwertzuiopasdfghjklyxcvbnm*"));
                int maxLength1 = 10;
                InputFilter[] FilterArray1 = new InputFilter[1];
                FilterArray1[0] = new InputFilter.LengthFilter(maxLength1);
                input_voterid.setFilters(FilterArray1);
                return true;

            case R.id.menu_urn:
                sp_proof.setText("Unique ID ");
                deliveryConfirm.setVoterOrAadharType("Unique ID");
                iv_delAadharScan.setVisibility(GONE);
                iv_delVoterOcr.setVisibility(GONE);
                input_voterid.setText("");
                input_voterid.setInputType(InputType.TYPE_CLASS_TEXT);
                int maxLength2 = 15;
                InputFilter[] FilterArray2 = new InputFilter[1];
                FilterArray2[0] = new InputFilter.LengthFilter(maxLength2);
                input_voterid.setFilters(FilterArray2);
                return true;


            case R.id.menu_self:
                sr_proof.setText("Self");


                ll_relation_view.setVisibility(GONE);
                deliveryConfirm.setOtherSelfType("Self");

                ll_other_name.setVisibility(GONE);


                return true;
            case R.id.menu_other:
                sr_proof.setText("Other");
                Log.v("ll_other_name", " - " + ll_other_name.getVisibility());
                ll_other_name.setVisibility(VISIBLE);

                ll_relation_view.setVisibility(VISIBLE);

                deliveryConfirm.setOtherSelfType("Other");

                return true;

        }
        return false;
    }

    public void callOcrActivity() {
        Intent i = new Intent(this, VoterOcrActivity.class);
        i.putExtra("shipment_num", shipmentNumber);
        startActivityForResult(i, 333);
    }


}
