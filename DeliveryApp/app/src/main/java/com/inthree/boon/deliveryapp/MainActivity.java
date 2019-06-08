package com.inthree.boon.deliveryapp;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Matrix;

import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.messaging.FirebaseMessaging;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.inthree.boon.deliveryapp.TrackingService.TrackerService;
import com.inthree.boon.deliveryapp.activity.BFILBranchActivity;
import com.inthree.boon.deliveryapp.activity.LoginActivity;
import com.inthree.boon.deliveryapp.activity.MenuServiceActvity;
import com.inthree.boon.deliveryapp.activity.MenuStartActviity;
import com.inthree.boon.deliveryapp.activity.ProfileActivity;
import com.inthree.boon.deliveryapp.activity.ServiceActivity;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Config;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.fcm.MyFirebaseInstanceIDService;
import com.inthree.boon.deliveryapp.model.BranchVal;
import com.inthree.boon.deliveryapp.model.ServiceOrderResp;
import com.inthree.boon.deliveryapp.request.DeliveryConfirmReq;
import com.inthree.boon.deliveryapp.request.LoginReq;
import com.inthree.boon.deliveryapp.request.OrderReq;
import com.inthree.boon.deliveryapp.request.OrderStatusReq;
import com.inthree.boon.deliveryapp.request.PartialReq;
import com.inthree.boon.deliveryapp.request.ServiceConfirmReq;
import com.inthree.boon.deliveryapp.request.UndeliveryReq;
import com.inthree.boon.deliveryapp.response.AttemptResp;
import com.inthree.boon.deliveryapp.response.BranchResp;
import com.inthree.boon.deliveryapp.response.DeliveryConfirmResp;
import com.inthree.boon.deliveryapp.response.FeedbackVal;
import com.inthree.boon.deliveryapp.response.ImageSyncResp;
import com.inthree.boon.deliveryapp.response.LoginResp;
import com.inthree.boon.deliveryapp.response.OrderChangeResp;
import com.inthree.boon.deliveryapp.response.OrderResp;
import com.inthree.boon.deliveryapp.response.PartialResp;
import com.inthree.boon.deliveryapp.response.ReasonResp;
import com.inthree.boon.deliveryapp.response.ReasonVal;
import com.inthree.boon.deliveryapp.response.ServiceIncompleteResp;
import com.inthree.boon.deliveryapp.response.ServiceResp;
import com.inthree.boon.deliveryapp.response.UndeliveredReasonResp;
import com.inthree.boon.deliveryapp.response.UndeliveryResp;
import com.inthree.boon.deliveryapp.response.feedBackResp;
import com.inthree.boon.deliveryapp.response.pushNotiResponse;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.MyValueFormatter;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;
import com.inthree.boon.deliveryapp.utils.NotificationUtils;
import com.inthree.boon.deliveryapp.utils.SyncService;
import com.inthree.boon.deliveryapp.utils.TLSSocketFactory;
import com.jakewharton.retrofit2.adapter.rxjava2.Result;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inthree.boon.deliveryapp.app.AppController.removePreferences;
import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;


public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener, PopupMenu.OnMenuItemClickListener, View.OnClickListener {


    private static final int BUFFER = 2048;
    private CompressFiles mCompressFiles;
    CompressDelivery mCompressdelivery;
    private ArrayList<String> mFilePathList = new ArrayList<>();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Context mcontext;
    int logoutFlag = 1;
    String lastSyncTime;
    private static Context context;
    String regId;
    //    String partial_shipAddress;
    String undelivered_shipAddress;
    private String file_proofPhoto;
    private ArrayList<UndeliveryResp> undeliveryList;
    String image_url;
    String file_path = "/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/";
    String sign_path = "/data/data/com.inthree.boon.deliveryapp/files/UserSignature/";
    private ArrayList<DeliveryConfirmResp> deliveryList;

    private BroadcastReceiver mSyncserviceReceiver;

    static ImageView img_start, img_bulk_start, img_sync, img_summary, img_profile, img_backup, img_logout;
    static TextView tv_lastsync;
    static TextView tv_lastsync_offline;

    /**
     * This is for selecte the dialog
     */
    AlertDialog alertDialog1;

    /**
     * To change the backround enable and disable
     */
    private LinearLayout sumRoot;

    MyFirebaseInstanceIDService myfirebaseId;
    /**
     * Display the item in alert box
     */
    CharSequence[] values = {" First Item ", " Second Item ", " Third Item "};

    LinearLayout ll_success;
    LinearLayout ll_failed;
    LinearLayout ll_pending;


    String aadhaarEnabled;

    /**
     * Display the piechart based on status
     */
    static PieChart pieChart;

    /**
     * Get the values of y axis
     */
    static PieDataSet dataSet;

    MyBroadcastReceiver receiver;

    /**
     * Get the value of y axis
     */
    static ArrayList<String> xVals;

    /**
     * Join the data of x and y values
     */
    static PieData data;

    /**
     * Added all the y values
     */
    static ArrayList<Entry> yvalues;

    /**
     * Display the pending count of order status
     */
    private static TextView pendingCount;

    /**
     * Display the Success count of order status
     */
    private static TextView successCount;

    /**
     * Display the failed count of order status
     */
    private static TextView failedCount;

    /**
     * Set the color for different piechart
     */
    static int[] colors = {};

    /**
     * Initialize the database
     */
    static SQLiteDatabase database;

    /**
     * Order list
     */
    private ArrayList<OrderResp> orderList;

    private ArrayList<AttemptResp> attemptList;

    private ArrayList<UndeliveredReasonResp> reasonList;

    private ArrayList<ReasonResp> reasonMasterList;

    private ArrayList<BranchVal> branchMasterList;

    /**
     * Get the activity name
     */
    String activityName;

    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;

    /**
     * This is for foreground for get the push notification for deliver people
     */
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /**
     * Get the device token id from push notification
     */
    private String pushRegId;

    /**
     * Get the device name from push notification
     */
    private String deviceModel;

    /**
     * Get the user id
     */
    private String userId;

    /**
     * Get the user name
     */
    private String userName;

    public static Location loc;

    static String ordercount;
    static String getorders;
    static String getOfflineOrders;
    private static final String DB_NAME = "boonboxdelivery.sqlite";
    private Matrix matrix;
    private DoubleAccumulator savedMatrix;

    /**
     * Get the time from background service
     */
    static String get_currentTime;
    static String sync;
    private String battery_level;

    String lat;
    String lang;
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
    private String page;

    private LinearLayout backup_linear;

    int attempt_count;
    /**
     * Trigger the click listener to perform the action
     */
    private LinearLayout mainRoot;

    /**
     * Trigger the click listener to perform the action
     */
    private LinearLayout bulkRoot;

    /**
     * Trigger the click listener to perform the action
     */
    private LinearLayout syncRoot;

    /**
     * Trigger the click listener to perform the action
     */
    private LinearLayout summaryRoot;

    /**
     * Trigger the click listener to perform the action
     */
    private LinearLayout profiltRoot;

    /**
     * Trigger the click listener to perform the action
     */
    private LinearLayout logoutRoot;
    private double latitud;
    private double langitude;
    Toolbar toolbarTop;
    Context mContext;
    LocationManager locationManager;
    String provider;
    ExternalDbOpenHelper dbOpenHelper;
    String compressDate;
    String compressedFilename;
    int getUnsyncedCount = 0;
    ArrayList filesToAdd = new ArrayList();
    File[] filessign;
    File[] files;
    String pathsign;
    ProgressDialog dialog;
    String zipfile_name;
    OrderDataAsync orderdatadsync;
    UploadImageAsync uploadimageasync;
    ReasonAsync reasonasync;
    UndeliveredDataAsync undelivereddatadsync;
    ModifyRecordDataAsync modifyrecorddataAsync;
    ChangeStatusAsync changestatusssync;
    int un_attempt_count;
    TextView tv_versionno;
    Toolbar toolbar;
    String nonDelivered_shipno;
    ArrayList filePaths = new ArrayList();
    ArrayList filePaths2 = new ArrayList();
    String currentVersion;
    TextView tv_update_app;
    Locale myLocale;
    ArrayList filePaths3 = new ArrayList();

    /********************Implemented by kani *******/


    //NTP server list: http://tf.nist.gov/tf-cgi/servers.cgi
    public static final String TIME_SERVER = "time-a.nist.gov";


    /*Tracking service*/
    String transportId;
    String tuserName;
    String tpassword;

    /**
     * Tracking permission
     */
    private static final int PERMISSIONS_REQUEST = 1;
    private static String[] PERMISSIONS_REQUIRED = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * Tracking shared preference
     */
    private SharedPreferences mPrefs;

    /**
     * firebase configuration
     */
    public static final String KEY_UPDATE_BACKUP = "del_backup_bool";

    /**
     * firebase configuration
     */
    public static final String KEY_UPDATE_USERNAME = "del_username";

    LinearLayout linearRoot;

    private Snackbar mSnackbarPermissions;
    private Snackbar mSnackbarGps;
    private String format;
    private List<FeedbackVal> feedBackVal;

    Spinner sp_reason;
    AppCompatButton bt_back;
    AppCompatButton bt_submit;
    ArrayList<String> my_array = new ArrayList<String>();

    List<String> list = new ArrayList<String>();
    CharSequence[] cs;
    ArrayAdapter my_Adapter;
    //    TextView tv_change_lang;
    String str_lang;
    String user_language;
    FloatingActionButton fab_language;
    private ArrayList<LoginResp> loginList;
    private ArrayList<LoginResp> languageList;
    boolean snackbar_flag = false;
    Snackbar gps_snackbar;
    /**
     * Check whether rold for service or delivery
     */
    private String roleId;

    /**
     * Get unincome count
     */
    private int unIncomAttemptCount;

    TextView tv_start;

    /*
     * Piechart for Service Orders
     * */
    static PieChart pc_service_orders;

    static ArrayList<Entry> Yservice_values;
    static ArrayList<String> Xservice_values;
    static String orderServicecount;

    static TextView dash_complete;
    static TextView dash_incomplete;
    static TextView dash_service_pend;
    LinearLayout ll_complete;
    LinearLayout ll_incomplete;
    LinearLayout ll_service_pend;
    LinearLayout ll_piechart_service;
    LinearLayout ll_piechart_order;
    LinearLayout service_root;
    int service_attempt_count;
    int pick_attempt_count;

    /**
     * Check the delivery_to whether it is delivery or bulk delivery
     */
    private String bfilBulkCheck;


    /********************Implemented by kani  end*******/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_language = AppController.getStringPreference(Constants.USER_LANGUAGE, "");
//        setLocale("en");
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
        dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
       /* getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);*/
        this.mcontext = mcontext;
        mcontext = this.getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbar.setLogo(R.drawable.delivery_truck);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        feedBackVal = new ArrayList<>();

      /*  toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupAlert();

            }
        });*/

        ll_complete = (LinearLayout) findViewById(R.id.ll_complete);
        ll_incomplete = (LinearLayout) findViewById(R.id.ll_incomplete);
        ll_service_pend = (LinearLayout) findViewById(R.id.ll_service_pend);
        ll_piechart_service = (LinearLayout) findViewById(R.id.ll_piechart_service);
        ll_piechart_order = (LinearLayout) findViewById(R.id.ll_piechart_order);
        service_root = (LinearLayout) findViewById(R.id.service_root);
        ll_complete.setOnClickListener(this);
        ll_incomplete.setOnClickListener(this);
        ll_service_pend.setOnClickListener(this);
        service_root.setOnClickListener(this);
        fab_language = (FloatingActionButton) findViewById(R.id.fab_language);
//        fab_language = (Spinner) findViewById(R.id.fab_language);
//        fab_language.setOnItemSelectedListener(this);


        fab_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                int[] lang_arr = {1,2,3,4,5,6,7,8,9};
                List<Integer> lang_arr = new ArrayList<>();
                Cursor getLangId = database.rawQuery("SELECT language_id FROM LanguageMaster  ", null);
                getLangId.moveToFirst();
                if (getLangId.getCount() > 0) {
                    while (!getLangId.isAfterLast()) {
                        int language_id = getLangId.getInt(getLangId.getColumnIndex("language_id"));
                        lang_arr.add(language_id);
                        getLangId.moveToNext();

                    }
                }
                /*for (int a : lang_arr) {
                    Log.v("lang_present", String.valueOf(a));
                }*/
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                popupMenu.setOnMenuItemClickListener(MainActivity.this);
                popupMenu.inflate(R.menu.poupup_menu);
                if (!lang_arr.contains(10)) {
                    popupMenu.getMenu().findItem(R.id.menu_punjabi).setVisible(false);
                }
                if (!lang_arr.contains(9)) {
                    popupMenu.getMenu().findItem(R.id.menu_kannada).setVisible(false);
                }
                if (!lang_arr.contains(8)) {
                    popupMenu.getMenu().findItem(R.id.menu_assamese).setVisible(false);
                }
                if (!lang_arr.contains(7)) {
                    popupMenu.getMenu().findItem(R.id.menu_odia).setVisible(false);
                }
                if (!lang_arr.contains(6)) {
                    popupMenu.getMenu().findItem(R.id.menu_marathi).setVisible(false);
                }
                if (!lang_arr.contains(5)) {
                    popupMenu.getMenu().findItem(R.id.menu_telugu).setVisible(false);
                }
                if (!lang_arr.contains(4)) {
                    popupMenu.getMenu().findItem(R.id.menu_bengali).setVisible(false);
                }
                if (!lang_arr.contains(3)) {
                    popupMenu.getMenu().findItem(R.id.menu_hindi).setVisible(false);
                }
                if (!lang_arr.contains(2)) {
                    popupMenu.getMenu().findItem(R.id.menu_tamil).setVisible(false);
                }
                popupMenu.show();

            }
        });

        toolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                backUpdate();



             /*   Date date = new Date(Location);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String myDate= sdf.format(date);

                System.out.println(myDate);*/

                return true;
            }
        });
           /* @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
            }*/

       /* toolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
                backupAlert();
                return false;
            }

           *//* @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
            }*//*
        });*/

        /*offline time commented until get the correct one*/
        /* TrueTimeRx.isInitialized();*/


//        toolbarTop= (Toolbar) findViewById(R.id.toolbar_top);
//        setSupportActionBar(toolbarTop);
        database = dbOpenHelper.openDataBase();
//        database.enableWriteAheadLogging();
        context = this;
        matrix = new Matrix();
        img_start = (ImageView) findViewById(R.id.imag_start);
        img_bulk_start = (ImageView) findViewById(R.id.img_bulk);
        //  img_bulk_start = (ImageView)findViewById(R.id.imag_bluk_start);
        img_sync = (ImageView) findViewById(R.id.imag_sync);
        // img_summary = (ImageView) findViewById(R.id.imag_summary);
        img_profile = (ImageView) findViewById(R.id.imag_profile);
        // img_backup = (ImageView)findViewById(R.id.imag_backup);
        img_logout = (ImageView) findViewById(R.id.imag_logout);
        tv_lastsync = (TextView) findViewById(R.id.tv_lastsync);
        tv_lastsync_offline = (TextView) findViewById(R.id.tv_lastsync_offline);
        mainRoot = (LinearLayout) findViewById(R.id.start_root);
        bulkRoot = (LinearLayout) findViewById(R.id.bulk_root);
        syncRoot = (LinearLayout) findViewById(R.id.sync_root);
        //summaryRoot=(LinearLayout) findViewById(R.id.summar_root);
        profiltRoot = (LinearLayout) findViewById(R.id.profile_root);
        logoutRoot = (LinearLayout) findViewById(R.id.logout_root);
        tv_update_app = (TextView) findViewById(R.id.tv_update_app);
        ll_success = (LinearLayout) findViewById(R.id.ll_success);
        ll_failed = (LinearLayout) findViewById(R.id.ll_failed);
        ll_pending = (LinearLayout) findViewById(R.id.ll_pending);
        backup_linear = (LinearLayout) findViewById(R.id.sub_lay6);
        tv_versionno = (TextView) findViewById(R.id.tv_versionno);
        tv_start = (TextView) findViewById(R.id.tv_start);
//        tv_change_lang= (TextView) findViewById(R.id.tv_change_lang);

        orderList = new ArrayList<>();
        // getorder();

        //call broadcast receiver for battery level
        image_url = getResources().getString(R.string.delivery_url) + "/media/";
        file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
//        sign_path = String.valueOf(this.getFilesDir()) + "/UserSignature/";
        sign_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
        battery_level = String.valueOf(AppController.getIntegerPreferences("BatterLevel", 0));
//        Log.e("batter_level", battery_level);

        latitud = Double.longBitsToDouble(AppController.getLongPreference(this, "storelatitude", -1));
        langitude = Double.longBitsToDouble(AppController.getLongPreference(this, "storeLongitude", -1));

        lat = String.valueOf(latitud);
        lang = String.valueOf(langitude);


        if (lat == null) {
            lat = "0.0";
        }

        if (lang == null) {
            lang = "0.0";
        }

//        Log.v("login1user1",AppController.getStringPreference(Constants.USER_ID, ""));
//        Log.v("login1user2",AppController.getStringPreference(Constants.LOGIN_USER_ID, ""));

     /*   new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true)
                .setCancelable(false)
                .setButtonDoNotShowAgain(null)
                .setButtonDismiss(null)
                .start();*/
        GetVersionCode vcode = new GetVersionCode();
        vcode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


//        setLocale("ta");

        getValues();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

//					displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    // new pushMessageSuccess().execute();
                 /*   p_message = intent.getStringExtra("message");
                    p_title = intent.getStringExtra("title");
                    msg_id = intent.getStringExtra("message_id");
                    if(p_title != null && !p_title.equals("null"))
                        new pushMessageSuccess().execute();*/
                    //   String message = intent.getStringExtra("message");

//                    Toast.makeText(getApplicationContext(), "Push notification: ", Toast.LENGTH_LONG).show();

                    // txtMessage.setText(message);
                }
            }
        };


//        getData();
//        getFeedBack();



      /*  mSyncserviceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.hasExtra("SyncServiceAction"))
                if (intent.getAction().equals("SyncServiceAction")) {
                    Bundle b = intent.getExtras();
                    String yourValue = b.getString("current_time");
                 Log.v("getSyncservice", yourValue);
                    tv_lastsync.setText(yourValue);
                }
            }
        };*/


        /************* Get the push notification details ************/

        pushRegId = AppController.getStringPreference(Constants.DEVICE_TOKEN_REGID, "0");
        deviceModel = AppController.getStringPreference(Constants.DEVICE, "");
        userId = AppController.getStringPreference(Constants.USER_ID, "");
        roleId = AppController.getStringPreference(Constants.ROLE_ID, "");
        if (roleId.equalsIgnoreCase("3")) {
            tv_start.setText(getResources().getString(R.string.tv_start_del));
            new MyAsyncTask().execute();


            //  getData(); removed on 8-5-19
            getFeedBack();
        } else if (roleId.equalsIgnoreCase("4")) {
            tv_start.setText(getResources().getString(R.string.tv_start_ser));
            getServiceData();
            getServiceIncomlpeteReasonData();
        } else {
            new MyAsyncTask().execute();
            // getData();   removed on 8-5-19
            getServiceData();
            getServiceIncomlpeteReasonData();
            syncUploadSer();
            getFeedBack();
           /* uploadimageasync = new UploadImageAsync();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                uploadimageasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                uploadimageasync.execute();
            }*/ // put this common for all 21-5-19
        }
        uploadimageasync = new UploadImageAsync();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            uploadimageasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            uploadimageasync.execute();
        }
        userName = AppController.getStringPreference(Constants.USER_NAME, "");


        /***************    sync animation ***********/

        // sumRoot = (LinearLayout) findViewById(R.id.summar_root);

        //  ordercount();

        /***************   end  sync animation ***********/

        /* *********************************  Menu Start ****************************/

        mainRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (roleId.equals("3")) {
                    if (getorders.equals("") || getorders.equals(null) || getorders.equals("0")) {
                        showalert(getString(R.string.start_alert));
                        return;
                    } else {

                        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                        img_start.startAnimation(myAnim);
                        Intent menu = new Intent(MainActivity.this, MenuStartActviity.class);
                        startActivity(menu);

                    }
                } else if (roleId.equals("4")) {
                    final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                    img_start.startAnimation(myAnim);
                    Intent menu = new Intent(MainActivity.this, MenuServiceActvity.class);
                    startActivity(menu);
                } else {
                    final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                    img_start.startAnimation(myAnim);
                    Intent menu = new Intent(MainActivity.this, MenuStartActviity.class);
                    startActivity(menu);
                }
            }
        });

        bulkRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isInternetAvailable()) {
                    if (roleId.equals("3")) {
                        if (getorders.equals("") || getorders.equals(null) || getorders.equals("0")) {
                            showalert(getString(R.string.start_alert));
                            return;
                        } else {

                            final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                            img_bulk_start.startAnimation(myAnim);
                            Intent menu = new Intent(MainActivity.this, BFILBranchActivity.class);
                            startActivity(menu);
                        }
                    } else if (roleId.equals("4")) {
                        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                        img_bulk_start.startAnimation(myAnim);
                        Intent menu = new Intent(MainActivity.this, MenuServiceActvity.class);
                        startActivity(menu);
                    } else {
                        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                        img_bulk_start.startAnimation(myAnim);
                        Intent menu = new Intent(MainActivity.this, MenuStartActviity.class);
                        startActivity(menu);
                    }
                } else {
                    showalert(getString(R.string.nointernet));
                }

            }
        });


 /*       tv_change_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageAlertBox();
            }
        });*/

        /**********************************  End Menu Start ****************************/

        /**********************************  Bulk  Start ****************************/

       /* img_bulk_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              *//*  Intent delivery = new Intent(MainActivity.this, DeliveryActivity.class);
                startActivity(delivery);*//*
            }
        });*/


        /* *********************************  End Bulk Start ****************************/

        /* *********************************  Sync   ****************************/
        syncRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isInternetAvailable()) {
                    RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
                            Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setRepeatCount(1);
                    anim.setDuration(2000);
                    img_sync.setAnimation(anim);
                    img_sync.startAnimation(anim);

                    /*uploadimageasync = new UploadImageAsync();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        uploadimageasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        uploadimageasync.execute();
                    }


                    getServiceData();
                    getServiceIncomlpeteReasonData();*/
                    syncUploadSer();

                    pageTrackerService();
                    getOfflineSyncOrders();


                } else {
                    String lsttime = AppController.getStringPreference(Constants.LastSyncTime, "");
                    tv_lastsync.setText(lsttime);
                    showalert(getString(R.string.nointernet));
                }
//              userLogin();

            }
        });

        /* *********************************  End Sync   ****************************/


        ll_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                ll_success.startAnimation(myAnim);

                Intent i = new Intent(MainActivity.this, MenuStartActviity.class);
                i.putExtra("status", "Success");
                startActivity(i);

            }
        });

        ll_failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                ll_failed.startAnimation(myAnim);

                Intent i = new Intent(MainActivity.this, MenuStartActviity.class);
                i.putExtra("status", "Failed");
                startActivity(i);
            }
        });

        ll_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                ll_pending.startAnimation(myAnim);

                Intent i = new Intent(MainActivity.this, MenuStartActviity.class);
                i.putExtra("status", "Pending");
                startActivity(i);
            }
        });


        tv_update_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.inthree.boon.deliveryapp&hl=en")));
            }
        });


        backup_linear.setClickable(false);
        backup_linear.setEnabled(false);
        /* *********************************  Summary  Start ****************************/

        /*summaryRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getorders.equals("") || getorders.equals(null) || getorders.equals("0")) {
                    showalert(getString(R.string.noordersum));
                    return;
                } else {
                    final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                    img_summary.startAnimation(myAnim);

                    Intent summary = new Intent(MainActivity.this, SummaryActivity.class);
                    startActivity(summary);
                }
                // CreateAlertDialogWithRadioButtonGroup();
            }
        });*/


        /* *********************************  End Summary ****************************/

        /* *********************************  Profile  Start ****************************/

        getTableValues();


        Cursor getValues = database.rawQuery("Select * from orderheader where sync_status = 'C' OR sync_status = 'E'", null);
        getValues.moveToFirst();

        if (getValues.getCount() > 0) {

            getUnsyncedCount = getValues.getInt(getValues.getColumnIndex("order_number"));
        } else {
            getUnsyncedCount = 0;
        }
        Log.v("getValues", String.valueOf(getUnsyncedCount));
        if (getUnsyncedCount > 0) {
            backup_linear.setVisibility(View.GONE);
            backup_linear.setEnabled(true);
            backup_linear.setAlpha(1);
            backup_linear.setClickable(true);
            toolbar.setLongClickable(true);
        } else if (getUnsyncedCount == 0) {
            backup_linear.setVisibility(View.GONE);
            backup_linear.setEnabled(false);
            backup_linear.setAlpha(0.7f);
            backup_linear.setClickable(false);
            toolbar.setLongClickable(false);
        }


        backup_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.show();
                dialog.setMessage("Backup In Progress");
                dialog.setCancelable(false);
                File f = new File(Environment.getExternalStorageDirectory(), mcontext.getResources().getString(R.string.app_name));
                if (!f.exists()) {
                    f.mkdirs();
                }
                mCompressdelivery = new CompressDelivery();
                mCompressdelivery.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                /*SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
                String currentDateTimeString = format.format(new Date());
                compressDate = currentDateTimeString;

                String inFileName = context.getDatabasePath(DB_NAME).toString();
                mFilePathList.add(inFileName);
                String path = String.valueOf(MainActivity.this.getFilesDir()+ "/DeliveryApp");

                File directory = new File(path);
                File[] files = directory.listFiles();
                Log.d("Files", "Size: "+ files.length);

                for (int i = 0; i < files.length; i++)
                {
//                    Log.d("backup_files", "FileName:" + files[i].getName());
                    mFilePathList.add(path+"/"+files[i].getName());
                    filesToAdd.add(new File(path+"/"+files[i].getName()));

                }

                String pathsign = String.valueOf(MainActivity.this.getFilesDir()+ "/UserSignature");
                File directorysign = new File(pathsign);
                File[] filessign = directorysign.listFiles();
                for (int i = 0; i < filessign.length; i++)
                {
                    mFilePathList.add(pathsign+"/"+filessign[i].getName());
                    ZipFile zipFile = null;
                    try {
                        String sqlitePath = String.valueOf(context.getDatabasePath(DB_NAME));
                        filesToAdd.add(new File(sqlitePath));
                        zipFile = new ZipFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mcontext.getResources().getString(R.string.app_name), "Lastmile_"+compressDate+".zip"));
                        filesToAdd.add(new File(pathsign+"/"+filessign[i].getName()));
                        ZipParameters parameters = new ZipParameters();
                        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
                        parameters.setEncryptFiles(true);
                        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                        parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                        parameters.setPassword("inthree123!@#");
                        zipFile.addFiles(filesToAdd, parameters);
                        Toast.makeText(getApplicationContext(), "Backup Completed", Toast.LENGTH_SHORT).show();
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }

                }*/

            }
        });


        profiltRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutRoot.setEnabled(false);
                mainRoot.setEnabled(false);
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                img_profile.startAnimation(myAnim);

//                getUserProfile();
                Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profile);
                logoutRoot.setEnabled(true);
                mainRoot.setEnabled(true);

                /*Intent profile = new Intent(MainActivity.this, MenuServiceActvity.class);
                startActivity(profile);
                logoutRoot.setEnabled(true);
                mainRoot.setEnabled(true);*/
            }
        });


        /* *********************************  End Profile ****************************/


        /* *********************************  Backup  Start ****************************/

   /*     img_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/


        /* *********************************  End Backup ****************************/

        /* *********************************  Logout  Start ****************************/

        logoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //isTimeAutomatic(view.getContext());

                profiltRoot.setEnabled(false);
                mainRoot.setEnabled(false);
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                img_logout.startAnimation(myAnim);
                Cursor uname = database.rawQuery("Select * from orderheader where sync_status = 'C' ", null);

                if (uname.getCount() > 0) {
                    uname.moveToFirst();
                    AppExitAlert();

                } else {
                    logout();
                }
                uname.close();
            }
        });





        /* *********************************  End Logout ****************************/

        initView();
        uploadPushNotification();
        getVersionInfo();

        /*Tracking the service from background service*/
        trckingService();
    }


    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }


    /**
     * Get the network time from the internet
     *
     * @return the format
     */
    public String getCurrentNetworkTime() {
        NTPUDPClient lNTPUDPClient = new NTPUDPClient();
        lNTPUDPClient.setDefaultTimeout(3000);
        Date returnTime;
        String format = null;
        try {
            lNTPUDPClient.open();
            InetAddress lInetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo lTimeInfo = lNTPUDPClient.getTime(lInetAddress);
            // returnTime =  lTimeInfo.getReturnTime(); // local time
            returnTime = lTimeInfo.getMessage().getTransmitTimeStamp().getDate();   //server time

            format = formatDate(returnTime, "yyyy-MM-dd", TimeZone.getTimeZone("GMT+05:30"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lNTPUDPClient.close();
        }

        return format;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_english:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "");
                snackbar_flag = false;
                AppController.setLocale("en");
                restartActivity();
                return true;
            case R.id.menu_hindi:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "hindi");
                snackbar_flag = false;
                AppController.setLocale("hi");
                restartActivity();
                return true;
            case R.id.menu_telugu:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "telugu");
                snackbar_flag = false;
                AppController.setLocale("te");
                restartActivity();
                return true;
            case R.id.menu_tamil:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "tamil");
                snackbar_flag = false;
                AppController.setLocale("ta");
                restartActivity();
                return true;
            case R.id.menu_bengali:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "bengali");
                snackbar_flag = false;
                AppController.setLocale("be");
                restartActivity();
                return true;
            case R.id.menu_marathi:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "marathi");
                snackbar_flag = false;
                AppController.setLocale("mr");
                restartActivity();
                return true;
            case R.id.menu_punjabi:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "punjabi");
                snackbar_flag = false;
                AppController.setLocale("pa");
                restartActivity();
                return true;
            case R.id.menu_assamese:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "assamese");
                snackbar_flag = false;
                AppController.setLocale("as");
                restartActivity();
                return true;
            case R.id.menu_kannada:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "kannada");
                snackbar_flag = false;
                AppController.setLocale("kn");
                restartActivity();
                return true;
            case R.id.menu_odia:
                AppController.storeStringPreferences(Constants.USER_LANGUAGE, "odia");
                snackbar_flag = false;
                AppController.setLocale("or");
                restartActivity();
                return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_complete:
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
//                ll_success.startAnimation(myAnim);
                ll_complete.startAnimation(myAnim);
                Intent startComplete = new Intent(this, MenuServiceActvity.class);
                startComplete.putExtra("status", "complete");
                startActivity(startComplete);
                break;
            case R.id.ll_incomplete:
                final Animation myAnim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
//                ll_success.startAnimation(myAnim1);
                ll_incomplete.startAnimation(myAnim1);
                Intent startInComplete = new Intent(this, MenuServiceActvity.class);
                startInComplete.putExtra("status", "incomplete");
                startActivity(startInComplete);
                break;
            case R.id.ll_service_pend:
                final Animation myAnim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
//                ll_success.startAnimation(myAnim2);
                ll_service_pend.startAnimation(myAnim2);
                Intent startServicePending = new Intent(this, MenuServiceActvity.class);
                startServicePending.putExtra("status", "pending");
                startActivity(startServicePending);
                break;
            case R.id.service_root:
                final Animation myAnim3 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
//                ll_success.startAnimation(myAnim2);
                service_root.startAnimation(myAnim3);
                Intent startService = new Intent(this, MenuServiceActvity.class);
                startService.putExtra("status", "pending");
                startActivity(startService);
                break;
        }
    }

  /*  @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

//        sp_reason.setSelection(position);
        if (!item.equals("Select Language")) {
            str_lang = item;
            str_lang = str_lang.substring(0,1).toLowerCase() + str_lang.substring(1);
            String store_lang = AppController.getStringPreference(Constants.USER_LANGUAGE,null);
            Log.v("onItemSelected","-- "+item+"-- "+store_lang+" -"+ str_lang);
            AppController.storeStringPreferences(Constants.USER_LANGUAGE, str_lang);
            if(str_lang.equals("tamil")){
                snackbar_flag = false;
                AppController.setLocale("ta");
                restartActivity();
            }else if(str_lang.equals("telugu")){
                snackbar_flag = false;
                AppController.setLocale("te");
                restartActivity();
            }else if(str_lang.equals("marathi")){
                snackbar_flag = false;
                AppController.setLocale("mr");
                restartActivity();
            }else if(str_lang.equals("hindi")){
                snackbar_flag = false;
                AppController.setLocale("hi");
                restartActivity();
            }else if(str_lang.equals("punjabi")){
                snackbar_flag = false;
                AppController.setLocale("pa");
                restartActivity();
            }else if(str_lang.equals("odia")){
                snackbar_flag = false;
                AppController.setLocale("or");
                restartActivity();
            }else if(str_lang.equals("bengali")){
                snackbar_flag = false;
                AppController.setLocale("be");
                restartActivity();
            }else if(str_lang.equals("kannada")){
                snackbar_flag = false;
                AppController.setLocale("kn");
                restartActivity();
            }else if(str_lang.equals("assamese")){
                snackbar_flag = false;
                AppController.setLocale("as");
                restartActivity();
            }else{
                snackbar_flag = false;
                AppController.setLocale("en");
                restartActivity();
            }
//                    Toast.makeText(getApplicationContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(), "Please Select a Valid Language", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/


    class getCurrentNetworkTime extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            NTPUDPClient timeClient = new NTPUDPClient();
            timeClient.setDefaultTimeout(3000);
            InetAddress inetAddress = null;
            boolean is_locale_date = false;
            String networkDate = null;
            try {
                inetAddress = InetAddress.getByName(TIME_SERVER);
                TimeInfo timeInfo = null;
                timeInfo = timeClient.getTime(inetAddress);
                long localTime = timeInfo.getReturnTime();
                long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                Date time = new Date(serverTime);
                format = formatDate(time, "yyyy-MM-dd", TimeZone.getTimeZone("GMT+05:30"));
                networkDate = format;


              /*  if (new Date(localTime) != new Date(serverTime))
                    is_locale_date = true;*/

            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e("UnknownHostException: ", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException: ", e.getMessage());
            }
            return networkDate;
        }

        protected void onPostExecute(String networkDate) {

          /*  Date checkDate = Calendar.getInstance().getTime();
            System.out.println("Current time => " + checkDate);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
            String formattedDate = df.format(checkDate);
*/

            AppController.getStringPreference("network_date", networkDate);

            getTimeComparison();






            /*if(!local_date) {
                Log.e("Check ", "dates not equal" + local_date);
            }*/
        }
    }

    private void getTimeComparison() {

        String networkStatus = AppController.getStringPreference("network_date", "");

        if (networkStatus != null) {

            try {
                String date = getCurrentNetworkTime();
                String incDate = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(date));
                int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int co = 0; co < maxDay; co++) {
                    c.add(Calendar.DATE, 1);
                    incDate = sdf.format(c.getTime());
                }

                Date checkDate = Calendar.getInstance().getTime();
                System.out.println("Current time => " + checkDate);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String formattedDate = df.format(checkDate);

                if (formattedDate.equalsIgnoreCase(incDate)) {
                    Log.v("Date match", formattedDate + "" + incDate);

                } else {
                    Log.v("Date  match not matched", formattedDate + "" + incDate);
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    private String formatDate(Date date, String pattern, TimeZone timeZone) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(timeZone);
        return format.format(date);
    }

    /**
     * Configure the backup whether should be on or off
     */
    private void backUpdate() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        String backupUpdate = remoteConfig.getString(KEY_UPDATE_BACKUP);
        String backUpUsernmae = remoteConfig.getString(KEY_UPDATE_USERNAME);

        if (isInternetAvailable()) {
            if (backupUpdate.equalsIgnoreCase("yes") && backUpUsernmae.equalsIgnoreCase(userName)) {
                backupAlert();
            } else {
                alertDialogBackUp(this, "BACKUP", getResources().getString(R.string.backupalert), "OK");
            }
        } else {
            alertDialogBackUp(this, "BACKUP", getResources().getString(R.string.back_internet), "OK");
        }


    }


    /************************** Tracking service   *****************************/

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Track the vehicle while running to delivery the products to user  // kani commented
     */
    public void trckingService() {


        mPrefs = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        String transportID = mPrefs.getString(getString(R.string.transport_id), "");
        String email = mPrefs.getString(getString(R.string.email), "");
        String password = mPrefs.getString(getString(R.string.password), "");


        checkInputFields();

        transportId = transportID;
        tuserName = email;
        tpassword = password;


        if (isServiceRunning(TrackerService.class)) {
            // If service already running, simply update UI.
            setTrackingStatus(R.string.tracking);
        } else if (transportID.length() > 0 && email.length() > 0 && password.length() > 0) {
            // Inputs have previously been stored, start validation.
            checkLocationPermission();
        } else {
            // First time running - check for inputs pre-populated from build.
            // transportId=getString(R.string.build_transport_id);
            // tuserName=getString(R.string.build_email);
            // tpassword=getString(R.string.build_password);
        }

    }


    /**
     * Second validation check - ensures the app has location permissions, and
     * if not, requests them, otherwise runs the next check.
     */
    private void checkLocationPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (locationPermission != PackageManager.PERMISSION_GRANTED
                || storagePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST);
        } else {
            checkGpsEnabled();
        }
    }


    private void setTrackingStatus(int status) {
        boolean tracking = status == R.string.tracking;
        // mTransportIdEditText.setEnabled(!tracking);
        //  mEmailEditText.setEnabled(!tracking);
        //   mPasswordEditText.setEnabled(!tracking);


        //  mStartButton.setVisibility(tracking ? View.INVISIBLE : View.VISIBLE);


       /* if (mSwitch != null) {
            // Initial broadcast may come before menu has been initialized.
            mSwitch.setChecked(tracking);
        }*/
        //((TextView) findViewById(R.id.title)).setText(getString(status));
    }


    /**
     * Receives status messages from the tracking service.
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setTrackingStatus(intent.getIntExtra(getString(R.string.status), 0));
        }
    };


    /**
     * First validation check - ensures that required inputs have been
     * entered, and if so, store them and runs the next check.
     */
    private void checkInputFields() {
        transportId = userName;
        tuserName = "kanimozhi@inthreeaccess.com";
        tpassword = "inthree123";

        if (transportId.length() == 0 || tuserName.length() == 0 ||
                tpassword.length() == 0) {
            Toast.makeText(MainActivity.this, R.string.missing_inputs, Toast.LENGTH_SHORT).show();
        } else {
            // Store values.
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(getString(R.string.transport_id), transportId);
            editor.putString(getString(R.string.email), tuserName);
            editor.putString(getString(R.string.password), tpassword);
            editor.apply();
            // Validate permissions.
            checkTrackingLocationPermission();

            // mSwitch.setEnabled(true);
        }
    }


    /**
     * Second validation check - ensures the app has location permissions, and
     * if not, requests them, otherwise runs the next check.
     */
    private void checkTrackingLocationPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (locationPermission != PackageManager.PERMISSION_GRANTED
                || storagePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST);
        } else {
//            checkGpsEnabled();
        }
    }

    /**
     * Third and final validation check - ensures GPS is enabled, and if not, prompts to
     * enable it, otherwise all checks pass so start the location tracking service.
     */
    private void checkGpsEnabled() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

//            Log.v("checkGpsEnabled","rt- "+ "");
            if (snackbar_flag == false)
//                Log.v("checkGpsEnabled","- "+ snackbar_flag);
                reportGpsError();


        } else {
//            Log.v("checkGpsEnabled","- "+ "resolveGpsError");
            resolveGpsError();
            startLocationService();
        }
    }

    /**
     * Callback for location permission request - if successful, run the GPS check.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            // We request storage perms as well as location perms, but don't care
            // about the storage perms - it's just for debugging.
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        reportPermissionsError();
                        Log.v("checkGpsEnabled", "-- " + "checkGpsEnabled1");
                    } else {
                        resolvePermissionsError();
                        checkGpsEnabled();
                        Log.v("checkGpsEnabled", "-- " + "checkGpsEnabled");
                    }
                }
            }
        }
    }

    private void startLocationService() {
        // Before we start the service, confirm that we have extra power usage privileges.
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

//        Log.v("StartTrackingService", "StartService");
//        startService(new Intent(this, TrackerService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(new Intent(this, TrackerService.class));
        } else {
            startService(new Intent(this, TrackerService.class));
        }
    }

    private void stopLocationService() {
        stopService(new Intent(this, TrackerService.class));
    }


    private void reportPermissionsError() {
       /* if (mSwitch != null) {
            mSwitch.setChecked(false);
        }*/
        Snackbar snackbar = Snackbar
                .make(
                        findViewById(R.id.linear_root),
                        getString(R.string.location_permission_required),
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.enable, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar_flag = false;
                        Intent intent = new Intent(android.provider.Settings
                                .ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(
                android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }


    private void resolvePermissionsError() {
        if (mSnackbarPermissions != null) {
            mSnackbarPermissions.dismiss();
            mSnackbarPermissions = null;
        }
    }

    private void reportGpsError() {
       /* if (mSwitch != null) {
            mSwitch.setChecked(false);
        }*/
        snackbar_flag = true;
//        Snackbar snackbar = Snackbar
        gps_snackbar = Snackbar
                .make(findViewById(R.id.linear_root), getString(R.string
                        .gps_required), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.enable, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar_flag = false;
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        // Changing message text color
        gps_snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = gps_snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.YELLOW);
        gps_snackbar.show();

    }


    private void resolveGpsError() {
       /* if (mSnackbarGps != null) {
            mSnackbarGps.dismiss();
            mSnackbarGps = null;
        }*/
    }


    /******************************************************  end of tracking service  **************************************************/


    public void AppExitAlert() {

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.alertbox);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;

        Button yes = (Button) dialog1.findViewById(R.id.proceed);
        Button no = (Button) dialog1.findViewById(R.id.close);
        TextView txt_ale = (TextView) dialog1.findViewById(R.id.txt_title);
        TextView txt_msg = (TextView) dialog1.findViewById(R.id.txt_message);

        txt_ale.setText(R.string.app_name);
        yes.setText(R.string.ok);
        no.setText(R.string.no);
        no.setVisibility(View.GONE);
        txt_msg.setText("Cannot Logout Offline Orders Not Synced");


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profiltRoot.setEnabled(true);
                mainRoot.setEnabled(true);
                dialog1.dismiss();
                stopLocationService();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    /**
     * Sync service for page tracking
     */
    public void pageTrackerService() {
        final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
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
//                    Log.v("paramObject", paramObject.toString());
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
                        tv_lastsync.setText(currentDateTimeString);
                    } else if (value.getRes_msg().equals("page track failed")) {
                        Logger.logInfo("failed");
//                            uploadUndelivered();
                    }
                    File dir = new File(String.valueOf(MainActivity.this.getFilesDir()) + "/DeliveryApp");
                    if (dir.exists() && dir.isDirectory()) {
                        getImagesToSync();
                    }

                    File dir1 = new File(String.valueOf(MainActivity.this.getFilesDir()) + "/ServiceSignApp");
                    if (dir1.exists() && dir1.isDirectory()) {
                        getServiceSignatureToSync();
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


        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
//        Log.v("Firebase reg id: ", regId);
       /* Log.e(TAG, "Firebase reg id: " + regId);
        Log.e(TAG, "Username: " + constants.username);
        Log.e(TAG, "Device_info: " + constants.deviceinfo);
        Log.e(TAG, "APP_Name: " + getString(R.string.app_name));*/

        if (isInternetAvailable()) {


        } else {

            return;

        }
    }

  /*  private BroadcastReceiver  mSyncserviceReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("SyncServiceAction")) {
                Bundle b = intent.getExtras();
                String yourValue = b.getString("current_time");
                Log.v("getSyncservice", yourValue);
                tv_lastsync.setText(yourValue);
            }
        }
    };*/


    /**
     * Initialze  all the attributes
     */
    public void initView() {
        pieChart = (PieChart) findViewById(R.id.piechart);
        pc_service_orders = (PieChart) findViewById(R.id.pc_service_orders);
        pendingCount = (TextView) findViewById(R.id.dash_pending);
        successCount = (TextView) findViewById(R.id.dash_sucess);
        failedCount = (TextView) findViewById(R.id.dash_faild);

        dash_complete = (TextView) findViewById(R.id.dash_complete);
        dash_incomplete = (TextView) findViewById(R.id.dash_incomplete);
        dash_service_pend = (TextView) findViewById(R.id.dash_service_pend);

        yvalues = new ArrayList<Entry>();
        Yservice_values = new ArrayList<Entry>();

        if (roleId.equals("3")) {
            ll_piechart_service.setVisibility(View.GONE);

            ordercount();
            getorder();
            statusSummarDeliver();
        } else if (roleId.equals("4")) {
            ll_piechart_order.setVisibility(View.GONE);
            serviceDashboard();
            orderServiceCount();
        } else {
            service_root.setVisibility(View.VISIBLE);
            ordercount();
            getorder();
            statusSummarDeliver();
            serviceDashboard();
            orderServiceCount();
        }

        lastOnline();
        getOfflineSyncOrders();
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setOnChartValueSelectedListener(this);
        pc_service_orders.setHighlightPerTapEnabled(true);
        pc_service_orders.setOnChartValueSelectedListener(this);
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;
//        Log.i("VAL SELECTED",
//                "Value: " + e.getVal() + ", index: " + e.getXIndex()
//                        + ", DataSet index: " + e.getData());
        if (roleId.equals("3")) {
            if (e.getXIndex() == 0) {
                Intent i = new Intent(this, MenuStartActviity.class);
                i.putExtra("status", "Success");
                startActivity(i);

            } else if (e.getXIndex() == 1) {
                Intent i = new Intent(this, MenuStartActviity.class);
                i.putExtra("status", "Failed");
                startActivity(i);

            } else if (e.getXIndex() == 2) {

                Intent i = new Intent(this, MenuStartActviity.class);
                i.putExtra("status", "Pending");
                startActivity(i);

            }
        } else if (roleId.equals("4")) {
            if (e.getXIndex() == 2) {
                Intent i = new Intent(this, MenuServiceActvity.class);
                i.putExtra("status", "pending");
                startActivity(i);

            } else if (e.getXIndex() == 0) {
                Intent i = new Intent(this, MenuServiceActvity.class);
                i.putExtra("status", "complete");
                startActivity(i);

            } else if (e.getXIndex() == 1) {

                Intent i = new Intent(this, MenuServiceActvity.class);
                i.putExtra("status", "incomplete");
                startActivity(i);

            }
        } else {
            if (e.getXIndex() == 0) {
                Intent i = new Intent(this, MenuStartActviity.class);
                i.putExtra("status", "Success");
                startActivity(i);

            } else if (e.getXIndex() == 1) {
                Intent i = new Intent(this, MenuStartActviity.class);
                i.putExtra("status", "Failed");
                startActivity(i);

            } else if (e.getXIndex() == 2) {

                Intent i = new Intent(this, MenuStartActviity.class);
                i.putExtra("status", "Pending");
                startActivity(i);

            }
        }

    }

    @Override
    public void onNothingSelected() {
//        Log.i("PieChart", "nothing selected");
    }


    /**
     * Get the no. of order
     */
    public static void ordercount() {
        getorder();
        Cursor getcount = database.rawQuery("SELECT count(order_number) as Count, valid FROM orderheader", null);

        if (getcount.getCount() > 0) {
            getcount.moveToFirst();


            if (!getcount.isAfterLast()) {
                do {
                    ordercount = getcount.getString(getcount.getColumnIndex("Count"));
//                    Log.e("Order_COUNT", ordercount);
                    pieChart.setCenterText(context.getResources().getString(R.string.dash_order) + " " + ordercount);
                    pieChart.setCenterTextSize(24f);
                    pieChart.setCenterTextColor(Color.BLUE);
                } while (getcount.moveToNext());
            }
            getcount.close();
            //img_summary.setClickable(true);
            // img_summary.setEnabled(true);

           /* final Animation myRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_in);
            img_sync.startAnimation(myRotation);*/


          /*  RotateAnimation anim = new RotateAnimation(0.0f, 360.0f ,
                    Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(1000);
            img_sync.setAnimation(anim);
            img_sync.startAnimation(anim);*/


            if (get_currentTime != null && sync != null) {
                RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(1);
                anim.setDuration(2000);
                img_sync.setAnimation(anim);
                img_sync.startAnimation(anim);
            }
              /*  final Animation animation = new RotateAnimation(0f, -90f, 100, 200);
                animation.setRepeatCount(-1);
                animation.setDuration(2000);
                animation.setStartOffset(5000);
                animation.setStartTime(4000);
                animation.setFillEnabled(true);
                img_sync.setAnimation(animation);*/


        } else {
            img_sync.clearAnimation();
            //sumRoot.setBackgroundColor(getResources().getColor(R.color.bg_menu));
            // img_summary.setClickable(false);
            //img_summary.setEnabled(false);

        }
    }


    public void getOfflineSyncOrders() {
        if (roleId.equals("3")) {
            Cursor uname = database.rawQuery("Select IFNULL(Count(order_number),0) as Order_number  from orderheader where sync_status = 'C' OR sync_status = 'E' ", null);
//        Cursor uname = database.rawQuery("Select IFNULL(Count(order_number),0) as Order_number  from orderheader where sync_status = 'C' ", null);
            uname.moveToFirst();
//        Log.v("getOfflineSyncOrders", uname.getString(uname.getColumnIndex("ono")));
//        Log.v("getOfflineSyncOrders", uname.getString(uname.getColumnIndex("sync_status")));
            if (uname.getInt(uname.getColumnIndex("Order_number")) > 0) {

                getOfflineOrders = uname.getString(uname.getColumnIndex("Order_number"));
                tv_lastsync_offline.setText(getApplicationContext().getResources().getString(R.string.sync) + " (" + getOfflineOrders + ")");
            } else {
                tv_lastsync_offline.setText(getApplicationContext().getResources().getString(R.string.sync));
                backup_linear.setEnabled(false);
                backup_linear.setAlpha(0.4f);
                backup_linear.setClickable(false);
                toolbar.setLongClickable(false);
            }
            uname.close();
        } else if (roleId.equals("4")) {
            Cursor uname = database.rawQuery("Select IFNULL(Count(order_id),0) as Order_number  from serviceMaster where sync_status = 'C' OR sync_status = 'E' ", null);

            uname.moveToFirst();

            if (uname.getInt(uname.getColumnIndex("Order_number")) > 0) {

                getOfflineOrders = uname.getString(uname.getColumnIndex("Order_number"));
                tv_lastsync_offline.setText(getApplicationContext().getResources().getString(R.string.sync) + " (" + getOfflineOrders + ")");
            } else {
                tv_lastsync_offline.setText(getApplicationContext().getResources().getString(R.string.sync));
                backup_linear.setEnabled(false);
                backup_linear.setAlpha(0.4f);
                backup_linear.setClickable(false);
                toolbar.setLongClickable(false);
            }
            uname.close();
        }
    }


    public static void getorder() {
//        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        tv_lastsync.setText(currentDateTimeString);
        Cursor uname = database.rawQuery("Select IFNULL(Count(order_number),0)as Order_number  from orderheader ", null);
        if (uname.getCount() > 0) {
            uname.moveToFirst();
            getorders = uname.getString(uname.getColumnIndex("Order_number"));
//            Log.e("GetOrderCount", getorders);
            //   username.setText(UName);

        }
        uname.close();
    }

    /**
     * Added the array list to integer element
     *
     * @param a Get the integer of an element
     * @param e Get the  element of added list
     * @return e
     */
    static int[] addElement(int[] a, int e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    /**
     * Display the piechart for dash board order status
     */
    public static void statusSummarDeliver() {
        yvalues.clear();
        /*Cursor needSuccess = database.rawQuery("Select Count(order_number)  from orderheader  where " +
                "(sync_status = " + "'U' OR sync_status ='C' OR sync_status='E') AND (delivery_status='delivered' OR " +
                "delivery_status='partial') ", null);*/
        Cursor needSuccess = database.rawQuery("Select Count(order_number)  from orderheader  where " +
                "(sync_status = " + "'U' OR sync_status ='C' OR sync_status='E') AND (delivery_status='delivered' OR " +
                "delivery_status='partial' OR delivery_status = 'pickup') ", null);

        /*Success*/
        if (needSuccess.getCount() > 0) {
            needSuccess.moveToFirst();

            final int nesy = Integer.parseInt(needSuccess.getString(0));
//            Log.e("S", String.valueOf(nesy));
            if (nesy > 0) {
                successCount.setText(String.valueOf(nesy));
                yvalues.add(new Entry((float) nesy, 0));
            } else {
                successCount.setText("0");
                yvalues.add(new Entry((float) 0, 0));
            }

        }


        /*Failed*/
        Cursor needFailed = database.rawQuery("Select  Count(order_number)  from orderheader  where delivery_status = " +
                "'undelivered' AND  (sync_status = 'U' OR sync_status ='C' OR sync_status='E') ", null);
        if (needFailed.getCount() > 0) {
            needFailed.moveToFirst();
            final int nesy = Integer.parseInt(needFailed.getString(0));
//            Log.e("F", String.valueOf(nesy));
            if (nesy > 0) {
                failedCount.setText(String.valueOf(nesy));
                yvalues.add(new Entry((float) nesy, 1));
            } else {
                failedCount.setText("0");
                yvalues.add(new Entry((float) 0, 1));
            }
        }

        /*Pending*/
        Cursor needpend = database.rawQuery("Select  Count(order_number)  from orderheader  where sync_status = " +
                "'P'", null);
        if (needpend.getCount() > 0) {
            needpend.moveToFirst();
            final int nesy = Integer.parseInt(needpend.getString(0));
//            Log.v("Pending_summary", String.valueOf(nesy));
            if (nesy > 0) {
                yvalues.add(new Entry((float) nesy, 2));
                pendingCount.setText(String.valueOf(nesy));
//                Log.v("Pending_summary1", String.valueOf(yvalues.get(0)));

            } else {
                pendingCount.setText("0");
                yvalues.add(new Entry((float) 0, 2));
            }
        }

        needpend.close();
        needSuccess.close();
        needFailed.close();


        dataSet = new PieDataSet(yvalues, "");


        xVals = new ArrayList<String>();
        for (int i = 0; i < yvalues.size(); i++) {
            if (yvalues.get(i).getXIndex() == 0) {
                colors = addElement(colors, Color.parseColor("#e7a528"));
                xVals.add("Total number of Shipped");
            }
            if (yvalues.get(i).getXIndex() == 1) {
                colors = addElement(colors, Color.parseColor("#c11e0c"));
                xVals.add("Total number of Pending");
            }
            if (yvalues.get(i).getXIndex() == 2) {
                colors = addElement(colors, Color.parseColor("#166b94"));
                xVals.add("Total number of Delivered");
            }
        }

        dataSet.setColors(colors);
        data = new PieData(xVals, dataSet);
        data.setValueFormatter(new MyValueFormatter());
        pieChart.setTransparentCircleRadius(10);
        pieChart.setData(data);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        data.setValueTextSize(22f);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.dark_white));
        pieChart.setDrawSliceText(false);
        pieChart.highlightValues(null);
        dataSet.setSliceSpace(2f);
        pieChart.setDescription("");
        pieChart.setMaxAngle(900);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.invalidate();
        pieChart.animateXY(1400, 1400);


    }


    /**
     * Need to create the alert for radio button
     */


    public void CreateAlertDialogWithRadioButtonGroup() {


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Select Your Choice");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:

                        Logger.logInfo("First Item Clicked");
                        break;
                    case 1:
                        Logger.logInfo("Second Item Clicked");
                        break;
                    case 2:
                        Logger.logInfo("Third Item Clicked");
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }


    /*************************** lOGOUT ALERT BOX ***********************/


    public void logout() {

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.alertbox);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog1.setCancelable(false);
        Button yes = (Button) dialog1.findViewById(R.id.proceed);
        Button no = (Button) dialog1.findViewById(R.id.close);
        TextView txt_ale = (TextView) dialog1.findViewById(R.id.txt_title);
        TextView txt_msg = (TextView) dialog1.findViewById(R.id.txt_message);

        txt_ale.setText(R.string.app_name);
        yes.setText(R.string.yes);
        no.setText(R.string.no);
        txt_msg.setText(R.string.showSwitch);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profiltRoot.setEnabled(true);
                mainRoot.setEnabled(true);
//                database.execSQL("Delete from UserMaster ", null);
//                database.execSQL("Delete from orderheader ", null);
                database.delete("UserMaster", null, null);
                database.delete("BranchMaster", null, null);
                database.delete("orderheader", null, null);
                database.delete("DeliveryConfirmation", null, null);
                database.delete("PageTracker", null, null);
                database.delete("PartialReasonMaster", null, null);
                database.delete("ProductDetails", null, null);
                database.delete("UndeliveredConfirmation", null, null);
                database.delete("UndeliveredReasonMaster", null, null);
                database.delete("LanguageMaster", null, null);
                database.delete("serviceMaster", null, null);
                database.delete("serviceProductAttributes", null, null);
                database.delete("serviceItems", null, null);
                database.delete("serviceFeedbackItems", null, null);
                database.delete("serviceConfirmation", null, null);
                database.delete("ServiceIncompleteReasonMaster", null, null);
                database.delete("ServiceIncompleteConfirmation", null, null);
//                ((ActivityManager)MainActivity.this.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
//                deleteAppData();
//                restartApp();
              /*  String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("pm clear "+packageName);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
               /* Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        MainActivity.restartApp(mcontext);
                    }
                });*/
              /*  try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
//                    myfirebaseId.onTokenRefresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
//                AppController.clearPreferences();
                removePreferences();
                AppController.setLocale("en");
                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
//                MainActivity.super.onBackPressed();

                dialog1.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profiltRoot.setEnabled(true);
                mainRoot.setEnabled(true);
                dialog1.dismiss();
            }
        });
        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    /*************************** lOGOUT ALERT BOX ***********************/

    public void getData() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final InthreeApi apiService = retrofit.create(InthreeApi.class);
        final OrderReq order = new OrderReq();
        JSONObject paramObject = null;


        order.setId(userId);

        try {
            paramObject = new JSONObject();
            paramObject.put("runner_id", order.getId());
            paramObject.put("token", "123456");
            paramObject.put("latitude", lat);
            paramObject.put("longitude", lang);
            paramObject.put("deviceInfo", AppController.getdevice());
            paramObject.put("battery", battery_level);
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
                if (value.getResMsg().equals("order success")) {
                    Log.v("get_data_msg", value.getResMsg());

                    for (int i = 0; i < detailVal.size(); i++) {
                        Cursor checkOrder = database.rawQuery("Select * from orderheader where Shipment_Number = '" +
                                        detailVal.get(i).getShipmentid() + "'",
                                null);
                        if (checkOrder.getCount() == 0) {

                            Log.v("OrderResp", detailVal.get(i).getShipmentid() + "shipid" + detailVal.get(i).getBranchCode() + "getotp");
                            Log.v("OrderResp_branchCode", detailVal.get(i).getBranchCode());


                            try {
                                date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(date);
                            Log.v("outputDateStr", " - " + detailVal.get(i).getToBeDeliveredBy() + " - " + outputDateStr);

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
                            }
                            /* language parsing json ends*/

                            String insertOrderHearder = "Insert into orderheader(return_id,branch_code,order_number,customer_name," +
                                    "customer_contact_number,alternate_contact_number,to_be_delivered_by,billing_address,billing_city,billing_pincode," +
                                    "billing_telephone,shipping_address,shipping_city,shipping_pincode, shipping_telephone,invoice_amount,payment_mode," +
                                    "client_branch_name,branch_address,branch_pincode,branch_contact_number,group_leader_name,group_leader_contact_number," +

                                    "slot_number,referenceNumber,processDefinitionCode,Shipment_Number,sync_status,delivery_status,valid,attempt_count,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi,otp,urn,order_type,max_attempt,delivery_aadhar_required,virtual_id) " +

                                    "Values('" + detailVal.get(i).getReturnId() + "','" + detailVal.get(i).getBranchCode() + "','" + detailVal.get(i).getOrderid() + "', '" + detailVal.get(i).getCustomerName() + "', '" + customer_contact_no + "'," +
                                    "'" + detailVal.get(i).getAlternateContactNumber() + "', '" + outputDateStr + "', " +
                                    "'" + detailVal.get(i).getBillingAddress() + "', '" + detailVal.get(i).getBillingCity() + "', '" + detailVal.get(i).getBillingPincode() + "', '" + detailVal.get(i).getBillingTelephone() + "', '" + detailVal.get(i).getShippingAddress() + "', '" + detailVal.get(i).getShippingCity() + "'," +

                                    " '" + detailVal.get(i).getShippingPincode() + "', '" + detailVal.get(i).getShippingTelephone() + "', '" + detailVal.get(i).getAmount() + "', '" + detailVal.get(i).getPaymentMode() + "', '" + detailVal.get(i).getClient_branch_name() + "', '" + detailVal.get(i).getBranch_address() + "', '" + detailVal.get(i).getBranch_pincode() + "'" +
                                    ", '" + detailVal.get(i).getBranch_contact_number() + "', '" + detailVal.get(i).getGroup_leader_name() + "', '" + detailVal.get(i).getGroup_leader_contact_number() + "', '" + detailVal.get(i).getSlot_number() + "', '" + detailVal.get(i).getReference() + "', '', '" + detailVal.get(i).getShipmentid() + "', 'P', '', '" + detailVal.get(i).getDownloadSync() + "', " + detailVal.get(i).getAttempt() + ",'" + tamil_val + "','" + telugu_val + "','" + punjabi_val + "','" + hindi_val + "','" + bengali_val + "','" + kannada_val + "','" + assam_val + "','" + orissa_val + "','" + marathi_val + "','" + detailVal.get(i).getOtp() + "','" + detailVal.get(i).getUrn() + "','" + detailVal.get(i).getOrder_type() + "','" + detailVal.get(i).getMax_attempt() + "'," + detailVal.get(i).getDelivery_aadhar_required() + ",'" + value.getVirtual_id() + "')";

                            database.execSQL(insertOrderHearder);

                        } else {
                            try {
                                date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String outputDateStr = outputFormat.format(date);
                            Log.v("outputDateStr1", " - " + detailVal.get(i).getToBeDeliveredBy() + " - " + outputDateStr);
                            Log.v("ordertype1", " - " + detailVal.get(i).getOrder_type());
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
                                        /*String tamil_name = tamilOneObject.getString("customer_name");
                                        String tamil_branch = tamilOneObject.getString("branch_name");
                                        String tamil_branch_deliaddr = tamilOneObject.getString("delivery_address");
                                        String tamil_branch_addr = tamilOneObject.getString("branch_address");
                                        String tamil_branch_city = tamilOneObject.getString("city");
                                        tamilLangParamObject = new JSONObject();
                                        tamilLangParamObject.put("delivery_address", tamil_branch_deliaddr);
                                        tamilLangParamObject.put("city", tamil_branch_city);
                                        tamilLangParamObject.put("branch_name", tamil_branch);
                                        tamilLangParamObject.put("branch_address", tamil_branch_addr);
                                        tamilLangParamObject.put("customer_name", tamil_name);*/
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


                                      /*  String hindi_name = hindiOneObject.getString("customer_name");

                                        String hindi_branch = hindiOneObject.getString("branch_name");
                                        String hindi_branch_deliaddr = hindiOneObject.getString("delivery_address");
                                        String hindi_branch_addr = hindiOneObject.getString("branch_address");
                                        String hindi_branch_city = hindiOneObject.getString("city");
                                        hindiLangParamObject = new JSONObject();
                                        hindiLangParamObject.put("delivery_address", hindi_branch_deliaddr);
                                        hindiLangParamObject.put("city", hindi_branch_city);
                                        hindiLangParamObject.put("branch_name", hindi_branch);
                                        hindiLangParamObject.put("branch_address", hindi_branch_addr);
                                        hindiLangParamObject.put("customer_name", hindi_name);*/
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
                                     /*   String bengali_name = bengaliOneObject.getString("customer_name");

                                        String bengali_branch = bengaliOneObject.getString("branch_name");
                                        String bengali_branch_deliaddr = bengaliOneObject.getString("delivery_address");
                                        String bengali_branch_addr = bengaliOneObject.getString("branch_address");
                                        String bengali_branch_city = bengaliOneObject.getString("city");
                                        bengaliLangParamObject = new JSONObject();
                                        bengaliLangParamObject.put("delivery_address", bengali_branch_deliaddr);
                                        bengaliLangParamObject.put("city", bengali_branch_city);
                                        bengaliLangParamObject.put("branch_name", bengali_branch);
                                        bengaliLangParamObject.put("branch_address", bengali_branch_addr);
                                        bengaliLangParamObject.put("customer_name", bengali_name);*/
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

                                        /*String marathi_name = marathiOneObject.getString("customer_name");

                                        String marathi_branch = marathiOneObject.getString("branch_name");
                                        String marathi_branch_deliaddr = marathiOneObject.getString("delivery_address");
                                        String marathi_branch_addr = marathiOneObject.getString("branch_address");
                                        String marathi_branch_city = marathiOneObject.getString("city");
                                        marathiLangParamObject = new JSONObject();
                                        marathiLangParamObject.put("delivery_address", marathi_branch_deliaddr);
                                        marathiLangParamObject.put("city", marathi_branch_city);
                                        marathiLangParamObject.put("branch_name", marathi_branch);
                                        marathiLangParamObject.put("branch_address", marathi_branch_addr);
                                        marathiLangParamObject.put("customer_name", marathi_name);*/
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


                                       /* String assam_name = assamOneObject.getString("customer_name");

                                        String assam_branch = assamOneObject.getString("branch_name");
                                        String assam_branch_deliaddr = assamOneObject.getString("delivery_address");
                                        String assam_branch_addr = assamOneObject.getString("branch_address");
                                        String assam_branch_city = assamOneObject.getString("city");
                                        assamLangParamObject = new JSONObject();
                                        assamLangParamObject.put("delivery_address", assam_branch_deliaddr);
                                        assamLangParamObject.put("city", assam_branch_city);
                                        assamLangParamObject.put("branch_name", assam_branch);
                                        assamLangParamObject.put("branch_address", assam_branch_addr);
                                        assamLangParamObject.put("customer_name", assam_name);*/
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

                                      /*  String orissa_name = orissaOneObject.getString("customer_name");

                                        String orissa_branch = orissaOneObject.getString("branch_name");
                                        String orissa_branch_deliaddr = orissaOneObject.getString("delivery_address");
                                        String orissa_branch_addr = orissaOneObject.getString("branch_address");
                                        String orissa_branch_city = orissaOneObject.getString("city");
                                        orissaLangParamObject = new JSONObject();
                                        orissaLangParamObject.put("delivery_address", orissa_branch_deliaddr);
                                        orissaLangParamObject.put("city", orissa_branch_city);
                                        orissaLangParamObject.put("branch_name", orissa_branch);
                                        orissaLangParamObject.put("branch_address", orissa_branch_addr);
                                        orissaLangParamObject.put("customer_name", orissa_name);*/
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

                                       /* String telugu_name = teluguOneObject.getString("customer_name");

                                        String telugu_branch = teluguOneObject.getString("branch_name");
                                        String telugu_branch_deliaddr = teluguOneObject.getString("delivery_address");
                                        String telugu_branch_addr = teluguOneObject.getString("branch_address");
                                        String telugu_branch_city = teluguOneObject.getString("city");
                                        teluguLangParamObject = new JSONObject();
                                        teluguLangParamObject.put("delivery_address", telugu_branch_deliaddr);
                                        teluguLangParamObject.put("city", telugu_branch_city);
                                        teluguLangParamObject.put("branch_name", telugu_branch);
                                        teluguLangParamObject.put("branch_address", telugu_branch_addr);
                                        teluguLangParamObject.put("customer_name", telugu_name);*/
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

                                     /*   String kannada_name = kannadaOneObject.getString("customer_name");

                                        String kannada_branch = kannadaOneObject.getString("branch_name");
                                        String kannada_branch_deliaddr = kannadaOneObject.getString("delivery_address");
                                        String kannada_branch_addr = kannadaOneObject.getString("branch_address");
                                        String kannada_branch_city = kannadaOneObject.getString("city");
                                        kannadaLangParamObject = new JSONObject();
                                        kannadaLangParamObject.put("delivery_address", kannada_branch_deliaddr);
                                        kannadaLangParamObject.put("city", kannada_branch_city);
                                        kannadaLangParamObject.put("branch_name", kannada_branch);
                                        kannadaLangParamObject.put("branch_address", kannada_branch_addr);
                                        kannadaLangParamObject.put("customer_name", kannada_name);*/
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

                                       /* String punjab_name = punjabOneObject.getString("customer_name");
                                        String punjab_branch = punjabOneObject.getString("branch_name");
                                        String punjab_branch_deliaddr = punjabOneObject.getString("delivery_address");
                                        String punjab_branch_addr = punjabOneObject.getString("branch_address");
                                        String punjab_branch_city = punjabOneObject.getString("city");
                                        punjabLangParamObject = new JSONObject();
                                        punjabLangParamObject.put("delivery_address", punjab_branch_deliaddr);
                                        punjabLangParamObject.put("city", punjab_branch_city);
                                        punjabLangParamObject.put("branch_name", punjab_branch);
                                        punjabLangParamObject.put("branch_address", punjab_branch_addr);
                                        punjabLangParamObject.put("customer_name", punjab_name);*/
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
                                    "hindi = '" + hindi_val + "', " +
                                    "assam = '" + assam_val + "'," +
                                    "punjabi = '" + punjabi_val + "', " +
                                    "marathi = '" + marathi_val + "'," +
                                    "telugu = '" + telugu_val + "'," +
                                    "kannada = '" + kannada_val + "'," +
                                    "orissa = '" + orissa_val + "'," +

                                    "bengali = '" + bengali_val + "'," +

                                    "virtual_id = '" + value.getVirtual_id() + "'" +


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
                            if (item_json != null && !item_json.equals("null")) {
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
                                    Log.v("tamil_pval", "-- " + tamil_pval);
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
                                        //:Punjabi JSON
                                        JSONObject orissaOneObject = obj.getJSONObject("punjabi");
                                        String punjab_pname = orissaOneObject.getString("product_name");
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

                                } catch (Throwable t) {
                                    Log.e("lang_json", "Could not parse malformed JSON: \"" + bengali_pval + "\"");
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
//                        changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
                        checkOrder.close();
                    }

                    ordercount();
                    statusSummarDeliver();
                } else if (value.getResMsg().equals("order failed")) {

                } else {

                }
             /*   if(value.getRes_msg().equals("order success")) {
                    Log.v("get_response", value.getOrder_number());


                    for (int i = 0; i < orderVal.size(); i++) {
                        OrderResp student = new OrderResp();
                        Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '"+
                                        value.getShipment_number()+"'",
                                null);
                        if (checkProducts.getCount() == 0){
                            String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code)" +
                                    " Values ('"+ value.getShipment_number() +"', '"+ orderVal.get(i).getProduct_name()
                                    +"', '"+ orderVal.get(i).getQuantity() +"', '"+ orderVal.get(i).getAmount() +"', '"+
                                    orderVal.get(i).getProduct_code() +"')";
                            database.execSQL(insertProduct);
                        }else{
                            database.execSQL("DELETE FROM ProductDetails where shipmentnumber = '"+value.getShipment_number()+"'");

                            String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code)" +
                                    " Values ('"+ value.getShipment_number() +"', '"+ orderVal.get(i).getProduct_name()
                                    +"', '"+ orderVal.get(i).getQuantity() +"', '"+ orderVal.get(i).getAmount() +"', '"+
                                    orderVal.get(i).getProduct_code() +"')";
                            database.execSQL(insertProduct);

                        }

                        Log.v("getAddress", orderVal.get(i).getProduct_name());
//                        Log.v("getAddress", "--");
                        orderList.add(student);

                    }
                }else{
                    Log.v("get_response",value.getRes_msg());
                }*/
                changestatusssync = new ChangeStatusAsync();
                changestatusssync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
     * Get the feed back data from download
     */
    private void getFeedBack() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final InthreeApi apiService = retrofit.create(InthreeApi.class);
        final feedBackResp order = new feedBackResp();
        JSONObject paramObject = null;

        // RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

        final Observable<feedBackResp> observable = apiService.getFeedBack().subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<feedBackResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(feedBackResp value) {

                if (value.getResMsg().equalsIgnoreCase("feedback success")) {
                    Cursor checkOrder = database.rawQuery("Select * from FeedBackMaster",
                            null);
                    if (checkOrder.getCount() == 0) {
                        feedBackVal = value.getFeedbackVal();
                        if (feedBackVal.size() > 0) {
                            for (int i = 0; i < feedBackVal.size(); i++) {
//                        Log.v("login_user", loginList.get(i).getId());


                                String query = "INSERT INTO FeedBackMaster ( qid, que_type,question,status) " +
                                        "VALUES ('" + feedBackVal.get(i).getQid() + "','" +
                                        feedBackVal.get(i).getQuestionType() + "','" + feedBackVal.get(i).getQuestion() + "'," +
                                        "'" + feedBackVal.get(i).getStatus() + "')";
                                database.execSQL(query);

                            }
                        }
                    } else {
                        feedBackVal = value.getFeedbackVal();
                        if (feedBackVal != null) {
                            if (feedBackVal.size() > 0) {
                                for (int i = 0; i < feedBackVal.size(); i++) {
//                        Log.v("login_user", loginList.get(i).getId());

                                    String updateFeedBack = "UPDATE FeedBackMaster set qid = '" + feedBackVal.get(i).getQid() + "', " +
                                            "que_type = '" + feedBackVal.get(i).getQuestionType() + "'," +
                                            "status = '" + feedBackVal.get(i).getStatus() + "', " +
                                            "question = '" + feedBackVal.get(i).getQuestion() + "'  " +
                                            " where qid = '" + feedBackVal.get(i).getQid() + "'  ";
                                    database.execSQL(updateFeedBack);

                                }
                            }
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
                orderList = new ArrayList<>();
                OrderChangeResp detailVal = value;
//                Log.v("response_message", detailVal.getRes_msg());
//                uploadComplete();
//                uploadImage();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("changeerror", e.toString());
            }

            @Override
            public void onComplete() {
//                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }


    private void uploadPushNotification() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);
        OrderReq order = new OrderReq();
        JSONObject paramObject = null;
        order.setId("1");
        try {
            paramObject = new JSONObject();
            paramObject.put("username", userName);
            paramObject.put("device_info", deviceModel);
            paramObject.put("reg_id", pushRegId);
            paramObject.put("battery_level", battery_level);
            paramObject.put("latitude", lat);
            paramObject.put("userId", Constants.USER_ID);
            paramObject.put("longitude", lang);
            paramObject.put("deviceInfo", AppController.getdevice());
            paramObject.put("app_name", getResources().getString(R.string.app_name));
            paramObject.put("user_id", userId);
//            Log.v("paramObject", paramObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

//        order.setId("1");

        final Observable<pushNotiResponse> observable = apiService.uploadPushNoti(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<pushNotiResponse>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(pushNotiResponse value) {
                orderList = new ArrayList<>();
                pushNotiResponse detailVal = value;
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



    /*private void userLogin() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).
//                .baseUrl("http://youngninjas.in/").
        addConverterFactory(GsonConverterFactory.create())
                .build();
        InthreeApi  apiService = retrofit.create(InthreeApi.class);
        OrderReq order = new OrderReq();
        JSONObject paramObject = null;
        order.setId("1");
        try {
            paramObject = new JSONObject();
            paramObject.put("id", order.getId());
            Log.v("paramObject",paramObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody =RequestBody.create(MediaType.parse("application/json"),paramObject.toString() );
//        RequestBody uname = RequestBody.create(MediaType.parse("text/plain"), "1");
//        RequestBody pass = RequestBody.create(MediaType.parse("text/plain"), txt_pass);


        Call<OrderResp> call = apiService.setRegistrationDetails1(requestBody);

        call.enqueue(new Callback<OrderResp>() {
            @Override
            public void onResponse(Call<OrderResp> call, Response<OrderResp> response) {
                Log.v("msg", "Response message: " + "dgdfgdfg");

                OrderResp serverResponse = response.body();
                if (serverResponse != null) {
                    if (response.isSuccessful()) {
                        orderList = response.body().getOrder();
                        for (int i = 0; i < orderList.size(); i++) {
                            String res_msg = response.body().getResMsg();
                            Log.v("msg", "Response message: " + res_msg);
                            if(serverResponse.getResMsg().equals("success")) {

                                Log.v("Response_product",  orderList.get(i).getName());
                            }
//                            Toast.makeText(CommonActivity.this, user_id, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    assert serverResponse != null;
                    Log.v("youngresponse", serverResponse.toString());
                }

            }

            @Override
            public void onFailure(Call<OrderResp> call, Throwable t) {
                Log.i("youngresponse", "Response message: " + "fail");
                String message = t.getMessage();
                Log.d("failure", message);
            }
        });
    }*/


    @Override
    protected void onResume() {
        super.onResume();
//        langcount();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

//        final IntentFilter intentFilter = new IntentFilter("SyncServiceAction");
//        LocalBroadcastManager.getInstance(this).registerReceiver(mSyncserviceReceiver, intentFilter);

        IntentFilter filter = new IntentFilter("SyncServiceAction");
        receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, filter);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mSyncserviceReceiver,
//                new IntentFilter("SyncServiceAction"));
//        unregisterReceiver(receiver);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (receiver != null)
            unregisterReceiver(receiver);

        if (mSyncserviceReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mSyncserviceReceiver);
        mSyncserviceReceiver = null;
//        if (mBatInfoReceiver != null)
//        this.unregisterReceiver(mBatInfoReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("Onstart", "MainonStart");
        startService(new Intent(MainActivity.this, SyncService.class));
        /**
         * This is for tracking the classes when user working onit an app
         */

        /*Get the current activity name*/
        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "1", "0");
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (receiver != null)
                unregisterReceiver(receiver);

        } catch (Exception e) {
        }
    }


    @Override
    public void onBackPressed() {
//        logout();
        backpressed();
    }


    public static class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, new Locale("en", "US"));
            String currentDateTimeString = dateFormat.format(new Date());

//            final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            if (intent.getAction().equals("SyncServiceAction")) {
                Bundle b = intent.getExtras();
                get_currentTime = b.getString("current_time");
                sync = b.getString("sync");
                if (isInternetAvailable()) {
                    ordercount();
                    statusSummarDeliver();
                }
//                tv_lastsync.setText(get_currentTime);
                tv_lastsync.setText(currentDateTimeString);
                Log.v("tv_lastsync", "-- " + currentDateTimeString);
                AppController.storeStringPreferences(Constants.LastSyncTime, currentDateTimeString);
            }
        }
    }


    public static boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private void showalert(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setIcon(R.mipmap.ic_launcher);
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


    //    public void uploadComplete(final String ship_no) {  // removed on 10-08-2018
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

                                database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number IN ( " + bulkShipmentAppend + " ) AND delivery_to='1'  ");

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
                        paramObject.put("order_type", customerNameErrOrd.getString(customerNameErrOrd.getColumnIndex("order_type")));

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
        Log.v("sync_service_ship", "-" + "no");
        String file_deliveryProof = null;
        String file_addressProof = null;
        String file_invoiceProof = null;
        String file_relationproof = null;
        String file_signature = null;
        InthreeApi apiService = null;

       /* Cursor customerName = database.rawQuery("select DISTINCT O.delivery_status, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber ,IFNULL(D.customer_name,0) as customer_name,IFNULL" +
                "(D.amount_collected,0) as amount_collected,IFNULL(D.customer_contact_number,0) as customer_contact_number," +
                "IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                ", IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof," +
                "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D.latitude,0) as latitude,IFNULL" +
                "(D.longitude,0) as longitude" + " from orderheader O INNER JOIN DeliveryConfirmation D on D" +
                ".shipmentnumber = O.Shipment_Number where O" +
                ".sync_status='C' ", null);*/

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
            customerName.moveToFirst();
            while (!customerName.isAfterLast()) {

                aadhaarEnabled = customerName.getString(customerName.getColumnIndex("delivery_aadhar_required"));

                file_deliveryProof = file_path + customerName.getString(customerName.getColumnIndex("delivery_proof"));
                file_addressProof = file_path + customerName.getString(customerName.getColumnIndex("id_proof"));
                file_invoiceProof = file_path + customerName.getString(customerName.getColumnIndex("Invoice_proof"));
                file_signature = sign_path + customerName.getString(customerName.getColumnIndex("signature_proof"));
                file_relationproof = file_path + customerName.getString(customerName.getColumnIndex("relation_proof"));

                final String partial_shipAddress = customerName.getString(customerName.getColumnIndex("shipmentnumber"));
                Log.v("main_uploadImage", partial_shipAddress);
//                Log.v("main_uploadImage", file_path +customerName.getString(customerName.getColumnIndex("delivery_proof")));

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

                apiService = retrofit.create(InthreeApi.class);

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

//                File filePickupProof = new File(file_signature);
//                RequestBody requestBodyPickup = RequestBody.create(MediaType.parse("*/*"), filePickupProof);
//                MultipartBody.Part fileToPickup = MultipartBody.Part.createFormData("pickup_file", filePickupProof.getName(), requestBodyPickup);

//                MultipartBody.Part fileToRelation;
//                if (!aadhaarEnabled.equals("0")) {
//                    File filerelationproof = new File(file_relationProof);
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
                        Log.v("del_image_response", deliveryVal.getRes_msg());
                        if (deliveryVal.getRes_msg().equals("image success")) {
//                            database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
//                                    shipno + "' ");
//                            database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '"+attempt_count+"'  where Shipment_Number ='" +
//                                    shipno + "' ");
                            database.execSQL("UPDATE orderheader set image_status = 'U' where Shipment_Number ='" +
                                    partial_shipAddress + "' ");
                        /*    Logger.showShortMessage(SyncService.this, "Delivery has been uploaded successfully");
                            Intent goToMain = new Intent(SyncService.this, MainActivity.class);
                            startActivity(goToMain);*/
//                            pageTrackerService();
//                            uploadComplete(partial_shipAddress);  // removed on 10-08-2018

                        } else if (deliveryVal.getRes_msg().equals("image failed")) {
                            pageTrackerService();
                        }

                        getOfflineSyncOrders();
//                        orderSyncStatus();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mainerror", e.toString());
                    }

                    @Override
                    public void onComplete() {
//                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });

                customerName.moveToNext();
            }
        }
        customerName.close();


    }

    public void uploadUndelivered() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String currentDateTimeString = format.format(new Date());

        Cursor getUndeliveryValue = database.rawQuery("select  O.order_type,O.delivery_status, O.order_number, O.attempt_count, U.sno, " +
                "IFNULL(U.shipmentnumber,0) as shipmentnumber, IFNULL(U.remarks,0) as remarks, " +
                "IFNULL(U.proof_photo, 0) as proof_photo, IFNULL(U.reason,0) as reason, " +
                "IFNULL(U.latitude, 0) as latitude, IFNULL(U.longitude, 0) as longitude, IFNULL(U.created_at, 0) as created_at,IFNULL(O.invoice_amount,0) as invoice_amount," +
                " IFNULL(U.redirect, 0) as redirect,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O" +
                ".payment_mode,0) as payment_mode,O.attempt_count," +
                "IFNULL(U.shipment_address,0) as shipment_address, IFNULL(U.reason_id,0) as reason_id  from orderheader O INNER JOIN UndeliveredConfirmation U on U" +
                ".shipmentnumber = O.Shipment_Number where O.sync_status='C' LIMIT 1", null);

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

                String shipNo = getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("order_number"));
                Log.v("ordernumber", shipNo);

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
                //un_attempt_count++;
                Log.v("un_attempt_count", String.valueOf(un_attempt_count));
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
                    paramObject.put("order_type", getUndeliveryValue.getString(getUndeliveryValue.getColumnIndex("order_type")));
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
                    Log.v("undelivered_uplo", String.valueOf(paramObject));
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

                        Log.v("undeimage_response", value.getRes_msg());
                        // uploadUndeliveredImage(undelivered_shipAddress);

                        if (value.getRes_msg().equalsIgnoreCase("undelivered success")) {
                            Log.v("image_response", "success msgs");

                            un_attempt_count++;
                            database.execSQL("UPDATE orderheader set sync_status = 'U', image_status = 'C' ,attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                    undelivered_shipAddress + "' ");
                        } else if (value.getRes_msg().equalsIgnoreCase("undelivered unassigned")) {
                            database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                    undelivered_shipAddress + "' ");
                        } else if (value.getRes_msg().equalsIgnoreCase("undelivered failed")) {
                            database.execSQL("UPDATE orderheader set sync_status = 'E', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                    undelivered_shipAddress + "' ");
                        } else if (value.getRes_msg().equalsIgnoreCase("already undelivered")) {
                            database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                    undelivered_shipAddress + "' ");
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("upundelerror", e.toString());
                    }

                    @Override
                    public void onComplete() {
//                    Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
                getUndeliveryValue.moveToNext();
            }
        } else {
            Cursor getUndeliveryErrValue = database.rawQuery("select  O.delivery_status, O.order_number, O.attempt_count, U.sno, " +
                    "IFNULL(U.shipmentnumber,0) as shipmentnumber, IFNULL(U.remarks,0) as remarks, " +
                    "IFNULL(U.proof_photo, 0) as proof_photo, IFNULL(U.reason,0) as reason, " +
                    "IFNULL(U.latitude, 0) as latitude, IFNULL(U.longitude, 0) as longitude, IFNULL(U.created_at, 0) as created_at,IFNULL(O.invoice_amount,0) as invoice_amount," +
                    " IFNULL(U.redirect, 0) as redirect,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O" +
                    ".payment_mode,0) as payment_mode,O.attempt_count," +
                    "IFNULL(U.shipment_address,0) as shipment_address, IFNULL(U.reason_id,0) as reason_id  from orderheader O INNER JOIN UndeliveredConfirmation U on U" +
                    ".shipmentnumber = O.Shipment_Number where O.sync_status='E' LIMIT 1", null);

            Log.v("getUndeliveryValue1", String.valueOf(getUndeliveryErrValue.getCount()));
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

                    String shipNo = getUndeliveryErrValue.getString(getUndeliveryErrValue.getColumnIndex("order_number"));
                    Log.v("ordernumber1", shipNo);

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
                    //un_attempt_count++;
                    Log.v("un_attempt_count", String.valueOf(un_attempt_count));
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
                        paramObject.put("attemptCount", un_attempt_count);
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
                        Log.v("undelivered_uplo", String.valueOf(paramObject));
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

                            Log.v("image_response1", value.getRes_msg());
                            //uploadUndeliveredImage(undelivered_shipAddress);

                            if (value.getRes_msg().equalsIgnoreCase("undelivered success")) {
                                //uploadUndeliveredImage(undelivered_shipAddress);
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
                                database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + un_attempt_count + " where Shipment_Number ='" +
                                        undelivered_shipAddress + "' ");
                            }


                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("upundelerror", e.toString());
                        }

                        @Override
                        public void onComplete() {
//                    Log.v("inhere", "--");
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
                ".shipmentnumber = O.Shipment_Number where O.sync_status = 'C' LIMIT 1", null);
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

                        Log.v("loggerimageundeliver", value.getUndeliveredResp().toString());
                        if (value.getRes_msg().equalsIgnoreCase(getResources().getString(R.string.undeliver_res))) {

//                            database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
//                                    Shipment_no + "' ");

                        } else if (value.getRes_msg().equalsIgnoreCase("undelivered updated")) {


                        } else {

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
                getUndeliveryImage.moveToNext();
            }
        }
        getUndeliveryImage.close();


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
                                "assam = '" + assam_rval + "', orissa = '" + orissa_rval + "', marathi = '" + marathi_rval + "', reason_type = " + reasonMasterList.get(i).getReason_type() + " where rid = '" + reasonMasterList.get(i).getRid() + "'";

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


    public void lastOnline() {

        String sync_time = AppController.getStringPreference(Constants.LastSyncTime, "N/A");
//            Log.v("lastSyncTime","--"+sync_time);
        tv_lastsync.setText(sync_time);

    }


    public void showOlderRecords() {
        String file_path = String.valueOf(this.getFilesDir());

//        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.signature_proof,0) as signature_proof, IFNULL(D.id_proof,0) as id_proof, IFNULL(D.Invoice_proof,0) as Invoice_proof, IFNULL(D.created_at,0) as created_at FROM orderheader O   JOIN DeliveryConfirmation D on O.Shipment_Number = D.shipmentnumber where  (D.created_at < datetime('now','-1 day')) AND O.sync_status = 'U' AND O.image_status = 'U' ", null);
        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.signature_proof,0) as signature_proof, IFNULL(D.id_proof,0) as id_proof, IFNULL(D.Invoice_proof,0) as Invoice_proof, IFNULL(D.created_at,0) as created_at FROM orderheader O   JOIN DeliveryConfirmation D on O.Shipment_Number = D.shipmentnumber where  (D.created_at < datetime('now','-1 day')) AND O.sync_status = 'U' AND O.delivery_status != 'undelivered' ", null);
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {
                String ship_id = deleteOrder.getString(deleteOrder.getColumnIndex("Shipment_Number"));
                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("order_number")));
//                Log.v("fileImgDeliverdate", deleteOrder.getString(deleteOrder.getColumnIndex("created_at")));


                String deleteOrderDetails = "DELETE FROM orderheader WHERE sync_status = 'U' AND Shipment_Number = '" + ship_id + "'";
                database.execSQL(deleteOrderDetails);

                String deleteDeliveryDetails = "DELETE FROM DeliveryConfirmation WHERE shipmentnumber = '" + ship_id + "'";
                database.execSQL(deleteDeliveryDetails);

        /*        File deleteImgDeliverProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
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

    public void backpressed() {

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.alertbox);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;

        Button yes = (Button) dialog1.findViewById(R.id.proceed);
        Button no = (Button) dialog1.findViewById(R.id.close);
        TextView txt_ale = (TextView) dialog1.findViewById(R.id.txt_title);
        TextView txt_msg = (TextView) dialog1.findViewById(R.id.txt_message);

        txt_ale.setText(R.string.app_name);
        yes.setText(R.string.yes);
        no.setText(R.string.no);
        txt_msg.setText(R.string.showexit);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                stopService(new Intent(this, TrackerService.class));
//                Intent intent = new Intent(MainActivity.this,
//                        LoginActivity.class);
//                startActivity(intent);
              /*  Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();*/
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
//                MainActivity.super.onBackPressed();

                dialog1.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void deleteAppData() {
        try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);

           /* Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restartApp() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public static void restartApp(Context context) {
        Intent mStartActivity = new Intent(context, MainActivity.class); //Replace StartActivity with the name of the first activity in your app
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }


    public void getValues() {
        Cursor getValues = database.rawQuery("Select O.sync_status, O.Shipment_Number, O.image_status,D.id_proof,D.delivery_proof,D.Invoice_proof from orderheader O LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number ", null);
        getValues.moveToFirst();
        if (getValues.getColumnCount() > 0) {
            while (!getValues.isAfterLast()) {
                Log.v("getallvalues", getValues.getString(getValues.getColumnIndex("sync_status"))
                        + "--" + getValues.getString(getValues.getColumnIndex("delivery_proof"))
                        + "--" + getValues.getString(getValues.getColumnIndex("id_proof"))
                );
                getValues.moveToNext();
            }
        }
    }

    public void getValues1() {
        Cursor getValues = database.rawQuery("select  O.delivery_status, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber ,IFNULL(D.customer_name,0) as customer_name,IFNULL" +
                "(D.amount_collected,0) as amount_collected,IFNULL(D.customer_contact_number,0) as customer_contact_number," +
                "IFNULL(D.shipping_address,0) as shipping_address,IFNULL(D.shipping_city,0) as shipping_city, " + "IFNULL(D.Invoice_proof,0) as Invoice_proof" +
                ", IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof," +
                "0) as signature_proof,IFNULL(D.sync_status,0) as sync_status,IFNULL(D.latitude,0) as latitude,IFNULL" +
                "(D.longitude,0) as longitude" + " from orderheader O LEFT JOIN DeliveryConfirmation D on D" +
                ".shipmentnumber = O.Shipment_Number where O" +
                ".sync_status='C' ", null);
        getValues.moveToFirst();
        if (getValues.getColumnCount() > 0) {
            while (!getValues.isAfterLast()) {
                Log.v("getallvalues", getValues.getString(getValues.getColumnIndex("delivery_proof"))
                        + "--" + getValues.getString(getValues.getColumnIndex("id_proof"))
                        + "--" + getValues.getString(getValues.getColumnIndex("signature_proof")));
                getValues.moveToNext();
            }
        }
    }

    private void performBackup(final ExternalDbOpenHelper db, final String outFileName) {

        verifyStoragePermissions(this);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Lastmile Backup Name");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    String out = outFileName + m_Text + ".sqlite";
                    db.backup(out);

                    File targetLocation = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));
                    File sourceLocation = new File(new File(String.valueOf(MainActivity.this.getFilesDir())) + "/DeliveryApp");
                    copyFileFromDirectory(sourceLocation, targetLocation);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else
            Toast.makeText(this, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show();
    }

    //check permissions.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void copyAndMove() {
        // your sd card
        String sdCard = Environment.getExternalStorageDirectory().toString();
        String card = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name);
        file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/IMG_20180516_155715.jpg";
        // the file to be moved or copied
//        File sourceLocation = new File (sdCard + "/sample.txt");
        File sourceLocation = new File(file_path);

        // make sure your target location folder exists!
//        File targetLocation = new File (sdCard + "/MyNewFolder/sample.txt");
        File targetLocation = new File(card + "/IMG_20180516_155715.jpg");

        // just to take note of the location sources
        Log.v("sourceLocation", "sourceLocation: " + sourceLocation);
        Log.v("targetLocation", "targetLocation: " + targetLocation);

        try {

            // 1 = move the file, 2 = copy the file
            int actionChoice = 2;

            // moving the file to another directory
            if (actionChoice == 1) {

                if (sourceLocation.renameTo(targetLocation)) {
                    Log.v("Move_file", "Move file successful.");
                } else {
                    Log.v("Move_file", "Move file failed.");
                }

            }

            // we will copy the file
            else {

                // make sure the target file exists

                if (sourceLocation.exists()) {

                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                    Log.v("Copy_file", "Copy file successful.");

                } else {
                    Log.v("Copy_file", "Copy file failed. Source file missing.");
                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFileFromDirectory(File sourceLocation, File targetLocation) {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                try {
                    throw new IOException("Directory not creating " + targetLocation.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyFileFromDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            File directory = targetLocation.getParentFile();
            // Check Directory is exist or not.
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                try {
                    throw new IOException("Directory not creating " + directory.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(sourceLocation);
                out = new FileOutputStream(targetLocation);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            byte[] buf = new byte[1024];
            int len;
            try {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File targetLocation1 = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));
//        File sourceLocation1 = new File(new File(String.valueOf(MainActivity.this.getFilesDir())) + "/UserSignature");
        File sourceLocation1 = new File(new File(String.valueOf(MainActivity.this.getFilesDir())) + "/DeliveryApp");
        copyFileFromDirectorySign(sourceLocation1, targetLocation1);
    }

    public void copyFileFromDirectorySign(File sourceLocation, File targetLocation) {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                try {
                    throw new IOException("Directory not creating " + targetLocation.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyFileFromDirectorySign(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            File directory = targetLocation.getParentFile();
            // Check Directory is exist or not.
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                try {
                    throw new IOException("Directory not creating " + directory.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(sourceLocation);
                out = new FileOutputStream(targetLocation);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            byte[] buf = new byte[1024];
            int len;
            try {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public File getOutputZipFile(String fileName) {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), mcontext.getResources().getString(R.string.app_name));

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public void zip(String zipFilePath) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFilePath);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < mFilePathList.size(); i++) {


                setCompressProgress(i + 1);


                FileInputStream fi = new FileInputStream(mFilePathList.get(i));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(mFilePathList.get(i).substring(mFilePathList.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class CompressFiles extends AsyncTask<Void, Integer, Boolean> {
        //        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
//        String currentDateTimeString = format.format(new Date());
//         compressDate = currentDateTimeString;
        @Override
        protected void onPreExecute() {

            try {
//                mProgressView.setText("0% Completed");
            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

            File file = getOutputZipFile("Lastmile_" + compressDate + ".zip");

            String zipFileName;
            if (file != null) {
                zipFileName = file.getAbsolutePath();
                Log.v("zipFileName", zipFileName);
                compressedFilename = zipFileName;

                if (mFilePathList.size() > 0) {
                    zip(zipFileName);
                }

            }


            return true;
        }

        public void publish(int filesCompressionCompleted) {
            int totalNumberOfFiles = mFilePathList.size();
            publishProgress((100 * filesCompressionCompleted) / totalNumberOfFiles);
        }

        protected void onProgressUpdate(Integer... progress) {


            try {
//                mProgressView.setText(Integer.toString(progress[0]) + "% Completed");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        protected void onPostExecute(Boolean flag) {
            Log.d("COMPRESS_TASK", "COMPLETED");
//            mProgressView.setText("100 % Completed");
         /*   try {
                File src = new File(compressedFilename);

                ZipFile zipFile = new ZipFile(src);
                Log.v("compressedFilename1", String.valueOf(zipFile));
                zipFile.setPassword("a");
                if (zipFile.isEncrypted()) {
                    Log.v("compressedFilename",compressedFilename);
                    zipFile.setPassword("a");
                }
//                    String dest = new String("/sdcard/abc");
//                    zipFile.extractAll(dest);
            } catch (ZipException e) {
                e.printStackTrace();
            }*/

            Toast.makeText(getApplicationContext(), "Backup Completed", Toast.LENGTH_SHORT).show();


        }
    }

    public void setCompressProgress(int filesCompressionCompleted) {
        mCompressFiles.publish(filesCompressionCompleted);
    }


    private class CompressDelivery extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {
//                mProgressView.setText("0% Completed");

            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
            String currentDateTimeString = format.format(new Date());
            compressDate = currentDateTimeString;

//            String inFileName = context.getDatabasePath(DB_NAME).toString();
            String inFileName = database.getPath();

            Log.d("database", "path: " + database.getPath());
            mFilePathList.add(inFileName);
            String path = String.valueOf(MainActivity.this.getFilesDir() + "/DeliveryApp");
            Log.d("filepath", "path: " + path);
            File directory = new File(path);
            files = directory.listFiles();
            Log.d("Files", "Size: " + files.length);

            for (int i = 0; i < files.length; i++) {
                Log.d("backup_files", "FileName:" + files[i].getName());
                mFilePathList.add(path + "/" + files[i].getName());
                filesToAdd.add(new File(path + "/" + files[i].getName()));

            }

//            pathsign = String.valueOf(MainActivity.this.getFilesDir()+ "/UserSignature");
            pathsign = String.valueOf(MainActivity.this.getFilesDir() + "/DeliveryApp");
            File directorysign = new File(pathsign);
            filessign = directorysign.listFiles();
            zipfile_name = "Lastmile_" + compressDate + ".zip";
            ZipFile zipFile = null;
            try {
//                String sqlitePath = String.valueOf(context.getDatabasePath(DB_NAME));
                String sqlitePath = database.getPath();

                filesToAdd.add(new File(sqlitePath));
                zipFile = new ZipFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mcontext.getResources().getString(R.string.app_name), "Lastmile_" + compressDate + ".zip"));
                for (int i = 0; i < filessign.length; i++) {
                    Log.d("filessign_files", "FileName:" + filessign[i].getName());
                    filesToAdd.add(new File(pathsign + "/" + filessign[i].getName()));
                }
                ZipParameters parameters = new ZipParameters();
                parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                parameters.setPassword("inthree123!@#");
                zipFile.addFiles(filesToAdd, parameters);
//                Toast.makeText(getApplicationContext(), "Backup Completed", Toast.LENGTH_SHORT).show();
                Log.v("Backup", "Backup Completed");
            } catch (ZipException e) {
                e.printStackTrace();
            }
            filesToAdd.clear();
            return true;
        }


        protected void onProgressUpdate(Integer... progress) {


            try {
//                mProgressView.setText(Integer.toString(progress[0]) + "% Completed");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        protected void onPostExecute(Boolean flag) {
            Log.v("onpostexecute", String.valueOf(flag));

            File main = new File(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()));
            String asdf = String.valueOf(main);
            Log.v("asdf", asdf + "/" + mcontext.getResources().getString(R.string.app_name) + "/" + zipfile_name);
            asdf = asdf + "/" + mcontext.getResources().getString(R.string.app_name) + "/" + zipfile_name;
            uploadZipFile(asdf);
//            Toast.makeText(getApplicationContext(), "Backup Completed", Toast.LENGTH_SHORT).show();
//            dialog.dismiss();

        }
    }

    public void uploadZipFile(final String filep) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);


        File fileProofPhoto = new File(filep);
        RequestBody requestBodyDelivery = RequestBody.create(MediaType.parse("*/*"), fileProofPhoto);
        MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("zip_file", fileProofPhoto.getName(), requestBodyDelivery);

        final Observable<UndeliveryResp> observable = apiService.uploadZipFile(fileToDelivery) //,fileToAddress,fileToInvoice,fileToSign
                .subscribeOn
                        (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<UndeliveryResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UndeliveryResp value) {
                Log.v("res_zip", value.getRes_msg());
                if (value.getRes_msg().equals("zipfile success")) {
                    Toast.makeText(getApplicationContext(), "Backup Successful", Toast.LENGTH_SHORT).show();
                    File file = new File(filep);
                    boolean deleted = file.delete();
                    if (deleted) {
                        ZipAlert("Backup Successful");
                        database.execSQL("UPDATE orderheader set sync_status ='U' where sync_status = 'C' OR sync_status = 'E' ");
                    }
//                   ZipAlert( "Backup Successful");
                    dialog.dismiss();
                } else if (value.getRes_msg().equals("zipfile failed")) {
                    Toast.makeText(getApplicationContext(), "Backup saved but not sent to server", Toast.LENGTH_SHORT).show();
                    ZipAlert("Backup Saved But not sent to server");
                    dialog.dismiss();
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.d("res_zip", e.toString());
                Toast.makeText(getApplicationContext(), "Backup saved but not sent to server", Toast.LENGTH_SHORT).show();
                ZipAlert("Backup Saved But not sent to server");
                dialog.dismiss();
            }

            @Override
            public void onComplete() {
//                    Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });


//        }
    }

    public void ZipAlert(String msg) {

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.alertbox);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;

        Button yes = (Button) dialog1.findViewById(R.id.proceed);
        Button no = (Button) dialog1.findViewById(R.id.close);
        TextView txt_ale = (TextView) dialog1.findViewById(R.id.txt_title);
        TextView txt_msg = (TextView) dialog1.findViewById(R.id.txt_message);

        txt_ale.setText(R.string.app_name);
        yes.setText(R.string.ok);
        no.setText(R.string.no);
        no.setVisibility(View.GONE);
        txt_msg.setText(msg);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOfflineSyncOrders();
                dialog1.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

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

            // getData(); removed on 8-5-19

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
                /*uploadComplete();*/
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
            // reasonasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                reasonasync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                reasonasync.execute();
            }

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
            downloadLanguages();

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
//            modifyrecorddataAsync = new ModifyRecordDataAsync();
//            modifyrecorddataAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            orderdatadsync = new OrderDataAsync();
            orderdatadsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            getorder();
            orderSyncStatus();
//            orderPendingStatus();
//            statusSummarDeliver();

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
                Log.d("ordererror", e.toString());
            }

            @Override
            public void onComplete() {
                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
    }

    private void getVersionInfo() {
        String version = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tv_versionno.setText(String.format(" Version. %s ", version));
    }

    private OkHttpClient getRequestHeader() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.MINUTES)
                .connectTimeout(15, TimeUnit.MINUTES)
                .writeTimeout(15, TimeUnit.MINUTES)
                .build();

        return okHttpClient;
    }

    public static OkHttpClient getHttpClientForFile() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)  //TlsVersion.TLS_1_0,
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA)
                .build();
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.MINUTES)
                .writeTimeout(15, TimeUnit.MINUTES)
                .readTimeout(15, TimeUnit.MINUTES)
                .connectionSpecs(Collections.singletonList(spec))
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .build();
    }


    private OkHttpClient getAnotherSSL() {
        OkHttpClient client = new OkHttpClient();
        try {
            client = new OkHttpClient.Builder()
                    .sslSocketFactory(new TLSSocketFactory())
                    .readTimeout(15, TimeUnit.MINUTES)
                    .connectTimeout(15, TimeUnit.MINUTES)
                    .writeTimeout(15, TimeUnit.MINUTES)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return client;
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


    public void backupAlert() {

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.alertbox);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog1.setCancelable(false);
        Button yes = (Button) dialog1.findViewById(R.id.proceed);
        Button no = (Button) dialog1.findViewById(R.id.close);
        TextView txt_ale = (TextView) dialog1.findViewById(R.id.txt_title);
        TextView txt_msg = (TextView) dialog1.findViewById(R.id.txt_message);

        txt_ale.setText(R.string.app_name);
        yes.setText(R.string.yes);
        no.setText(R.string.no);
        txt_msg.setText("Do you want to Backup?");


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new ProgressDialog(MainActivity.this);
                dialog.show();
                dialog.setMessage("Backup In Progress");
                dialog.setCancelable(false);
                File f = new File(Environment.getExternalStorageDirectory(), mcontext.getResources().getString(R.string.app_name));
                if (!f.exists()) {
                    f.mkdirs();
                }
                mCompressdelivery = new CompressDelivery();
                mCompressdelivery.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                dialog1.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog1.dismiss();
            }
        });
        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }


    public void uploadNonDelivered() {
        Log.v("uploadNonDelivered", "--" + "");

        battery_level = BatteryManager.EXTRA_LEVEL;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String currentDateTimeString = format.format(new Date());

        Cursor customerName = database.rawQuery("select DISTINCT O.sync_status,O.delivery_status, O.order_number, O.valid, O.attempt_count,O.Shipment_Number as Shipment_Number, D.sno as sno,IFNULL(D.shipmentnumber,0) as shipmentnumber, IFNULL(O.customer_name,0) as customer_name,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(D.amount_collected,0) as amount_collected,IFNULL(O.invoice_amount,0) as invoice_amount,IFNULL(O.customer_contact_number  ,0) as phone,IFNULL(O.shipping_address,0) as shipping_address,IFNULL(O.shipping_city,0) as shipping_city,IFNULL(D.Invoice_proof,0) as Invoice_proof,IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.id_proof,0)as id_proof,IFNULL(D.signature_proof,0) as signature_proof,IFNULL(D.latitude,0) as latitude,IFNULL(D.longitude,0) as longitude, IFNULL(O.shipping_pincode,0) as pin_code,IFNULL(D.adhaar_details,0) as adhaar_details, IFNULL(D.landmark,0) as landmark, IFNULL(D.redirect, 0) as redirect,IFNULL(D.reason,0) as reason,IFNULL(D.created_at  ,0) as created_at,IFNULL(P" +
                ".product_name,0) as product_name,IFNULL(P.quantity,0) as quantity,IFNULL(P.amount,0) as amount," +
                "IFNULL(P.product_code,0) as product_code,IFNULL(P.amount_collected,0) as " +
                "product_amount_collected,IFNULL(P.delivery_qty,0) as delivery_qty from orderheader O  LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number LEFT JOIN PRODUCTDETAILS P ON D.shipmentnumber = P.shipmentnumber where O.sync_status='C' LIMIT 1 ", null);

        if (customerName.getCount() > 0) {
            customerName.moveToFirst();
            while (!customerName.isAfterLast()) {
                if (customerName.getString(customerName.getColumnIndex("shipmentnumber")).equals("") || customerName.getString(customerName.getColumnIndex("shipmentnumber")) == null) {
                    nonDelivered_shipno = customerName.getString(customerName.getColumnIndex("shipmentnumber"));
                    Log.v("uploadNonDeli_shipAdd", "--" + nonDelivered_shipno);

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
                    partialPelivery.setAddressproof(customerName.getString(customerName.getColumnIndex("id_proof")));
                    partialPelivery.setSignproof(customerName.getString(customerName.getColumnIndex("signature_proof")));
                    partialPelivery.setShipmentnumber(customerName.getString(customerName.getColumnIndex("Shipment_Number")));
                    partialPelivery.setOrderNo(customerName.getString(customerName.getColumnIndex("order_number")));
                    partialPelivery.setAadhaarDetails(customerName.getString(customerName.getColumnIndex("adhaar_details")));
                    partialPelivery.setAttempt(customerName.getInt(customerName.getColumnIndex("attempt_count")));
                    partialPelivery.setRedirect(customerName.getString(customerName.getColumnIndex("redirect")));
                    partialPelivery.setReason(customerName.getString(customerName.getColumnIndex("reason")));
                    attempt_count = partialPelivery.getAttempt();
                    attempt_count++;


                    if (customerName.getString(customerName.getColumnIndex("delivery_status")).equalsIgnoreCase("partial")) {
                        fieldData.setAmountCollected(customerName.getString(customerName.getColumnIndex("invoice_amount")));
                        partialPelivery.setModeType("Cash");
                        partialPelivery.setAmount_tot(customerName.getString(customerName.getColumnIndex("order_number")));
                        partialPelivery.setTransactionNum("N/A");
                        partialPelivery.setRemarks("N/A");
                        partialPelivery.setReceipt("N/A");
                        partialPelivery.setOriginalAmount(customerName.getString(customerName.getColumnIndex("invoice_amount")));

                        partialPelivery.setActualAmount(customerName.getString(customerName.getColumnIndex("invoice_amount")));
                        fieldData.setSkuActualQty(customerName.getString(customerName.getColumnIndex("quantity")));
                        fieldData.setProductCode(customerName.getString(customerName.getColumnIndex("product_code")));
                        fieldData.setProductName(customerName.getString(customerName.getColumnIndex("product_name")));
                        fieldData.setQuantity(customerName.getString(customerName.getColumnIndex("delivery_qty")));
                        fieldData.setAmount(customerName.getString(customerName.getColumnIndex("invoice_amount")));

                    } else {

                    }

                    try {
                        jsonItemCodeArray = new JSONArray();
                        jsonItemCodeObject = new JSONObject();
                        jsonFieldObj = new JSONObject();
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
                        paramObject.put("battery", "99");
                        paramObject.put("deviceInfo", AppController.getdevice());
                        paramObject.put("lastTransactionTime", currentDateTimeString);
                        paramObject.put("erpPushTime", customerName.getString(customerName.getColumnIndex("valid")));
                        paramObject.put("transactionDate", currentDateTimeString);
//                    paramObject.put("created_at", customerName.getString(customerName.getColumnIndex("created_at")));
                        paramObject.put("created_at", currentDateTimeString);

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
                        paramObject.put("addressproof", image_url + partialPelivery.getAddressproof());
                        paramObject.put("signproof", image_url + partialPelivery.getSignproof());
                        paramObject.put("shipmentnumber", partialPelivery.getShipmentnumber());
                        paramObject.put("order_no", partialPelivery.getOrderNo());
                        paramObject.put("aadhaar_details", partialPelivery.getAadhaarDetails());
                        paramObject.put("attemptCount", attempt_count);
                        paramObject.put("redirect", "0");


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

                            Cursor getOrders = database.rawQuery("Select IFNULL(delivery_qty,0) as delivery_qty, IFNULL(product_code, 0) as product_code, IFNULL(product_name, '') as product_name," +
                                    "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected from ProductDetails where shipmentnumber = '" + nonDelivered_shipno + "' ", null);
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


                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                    final Observable<PartialResp> observable = apiService.getPartialDeliverySync(requestBody)
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
                            Log.v("nonactivity_resmsg", value.getRes_msg() + " -" + value.getRes_code());
                            if (value.getRes_msg().equals("upload success")) {

                                database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = '" + attempt_count + "', image_status = 'C' where Shipment_Number ='" +
                                        nonDelivered_shipno + "' ");

                            } else if (value.getRes_msg().equals("upload failed")) {
                            }
                            pageTrackerService();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("upcomperror", e.toString());
                        }

                        @Override
                        public void onComplete() {

                        }


                    });
                }
                // newly added on 13-08-2018
                customerName.moveToNext();
            }
        } else {

        }
        customerName.close();

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
                        File dir = new File(String.valueOf(MainActivity.this.getFilesDir()) + "/UserSignature");
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
                ".shipmentnumber = O.Shipment_Number where O.image_status='C' LIMIT 1 ", null);
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


    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                currentVersion = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            String newVersion = null;

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    tv_update_app.setVisibility(View.VISIBLE);
                    alertDialogMsg(MainActivity.this, "Update Available", "A Newer version of the Lastmile app is available to download ", "Ok", onlineVersion);
                }
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }

    }

    /**
     * This is for backup alert
     *
     * @param context get the context of an activity
     * @param title   Get the title
     * @param content Get the content
     * @param okmsg   ok msg
     */
    public void alertDialogBackUp(Context context, String title, String content, String okmsg) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
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

    public void alertDialogMsg(Context context, String title, String content, String okmsg, String onlineVersion) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
//        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.inthree.boon.deliveryapp&hl=en")));
                        // sDialog.dismissWithAnimation();
                    }
                })
                .show();

        if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {

        } else {
            sweetAlertDialog.dismissWithAnimation();
        }
        sweetAlertDialog.setCancelable(false);
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

                Log.v("getShipmentID", paramObject.toString());
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

    private void orderPendingStatus() {

        Cursor customerName = database.rawQuery("SELECT Shipment_Number FROM orderheader where sync_status = 'P' ", null);
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

                Log.v("getShipmentID1", paramObject.toString());
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
                        Log.v("testresponse_message1", detailVal.getRes_msg());
                        if (detailVal.getRes_code().equals("1")) {
//                            database.execSQL("UPDATE orderheader set sync_status = 'U' where Shipment_Number ='" +
//                                    detailVal.getRes_msg() + "' ");


                            database.execSQL("DELETE FROM orderheader where Shipment_Number = '" + detailVal.getRes_msg() + "' AND  sync_status = 'P'");


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


    public void languageDropDown() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.language_dropdown, null);
        dialogBuilder.setView(dialogView);
        sp_reason = (Spinner) dialogView.findViewById(R.id.sp_reason);

        dialogBuilder.setTitle(R.string.main_choose_lang);
        dialogBuilder.setCancelable(true);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, my_array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_reason.setAdapter(dataAdapter);
//        my_Adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item,
//                my_array);
//        sp_reason.setAdapter(my_Adapter);

//        int position = my_Adapter.getPosition(my_array);
        String cur_lang = AppController.getStringPreference(Constants.USER_LANGUAGE, "");
        if (!cur_lang.equals("")) {
            str_lang = cur_lang.substring(0, 1).toUpperCase() + cur_lang.substring(1);
        }

//        sp_reason.setSelection(position);
        final AlertDialog alertDialog = dialogBuilder.create();

        sp_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {


                String item = adapter.getItemAtPosition(position).toString();

                sp_reason.setSelection(position);
                if (!item.equals("Select Language")) {
                    str_lang = item;
                    str_lang = str_lang.substring(0, 1).toLowerCase() + str_lang.substring(1);

                    AppController.storeStringPreferences(Constants.USER_LANGUAGE, str_lang);
                    if (str_lang.equals("tamil")) {
                        snackbar_flag = false;
                        AppController.setLocale("ta");
                        alertDialog.dismiss();
                        restartActivity();
                    } else if (str_lang.equals("telugu")) {
                        snackbar_flag = false;
                        AppController.setLocale("te");
                        alertDialog.dismiss();
                        restartActivity();
                    } else if (str_lang.equals("marathi")) {
                        snackbar_flag = false;
                        AppController.setLocale("mr");
                        alertDialog.dismiss();
                        restartActivity();
                    } else if (str_lang.equals("hindi")) {
                        snackbar_flag = false;
                        AppController.setLocale("hi");
                        restartActivity();
                    } else if (str_lang.equals("punjabi")) {
                        snackbar_flag = false;
                        AppController.setLocale("pa");
                        alertDialog.dismiss();
                        restartActivity();
                    } else if (str_lang.equals("odia")) {
                        snackbar_flag = false;
                        AppController.setLocale("or");
                        alertDialog.dismiss();
                        restartActivity();
                    } else if (str_lang.equals("bengali")) {
                        snackbar_flag = false;
                        AppController.setLocale("be");
                        alertDialog.dismiss();
                        restartActivity();
                    } else if (str_lang.equals("kannada")) {
                        snackbar_flag = false;
                        AppController.setLocale("kn");
                        alertDialog.dismiss();
                        restartActivity();
                    } else if (str_lang.equals("assamese")) {
                        snackbar_flag = false;
                        AppController.setLocale("as");
                        alertDialog.dismiss();
                        restartActivity();
                    } else {
                        snackbar_flag = false;
                        AppController.setLocale("en");
                        alertDialog.dismiss();
                        restartActivity();
                    }
//                    Toast.makeText(getApplicationContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "Please Select a Valid Language", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        alertDialog.show();
    }

    public void languageAlertBox() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.language_layout, null);
        dialogBuilder.setView(dialogView);
        sp_reason = (Spinner) dialogView.findViewById(R.id.sp_reason);
        bt_back = (AppCompatButton) dialogView.findViewById(R.id.bt_back);
        bt_submit = (AppCompatButton) dialogView.findViewById(R.id.bt_submit);

        dialogBuilder.setTitle(R.string.main_choose_lang);
        dialogBuilder.setCancelable(false);


//        Log.v("getReason",Constants.UNDELIVERED_TITLE);
//        my_Adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item,
//                my_array);
        my_Adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item,
                my_array);
        sp_reason.setAdapter(my_Adapter);


//            Log.v("getReason", "--" + undeliverConfirm.getReason());
//            int position = my_Adapter.getPosition(undeliverConfirm.getReason());
        int position = my_Adapter.getPosition(my_array);
        String cur_lang = AppController.getStringPreference(Constants.USER_LANGUAGE, "");
        if (!cur_lang.equals("")) {
            str_lang = cur_lang.substring(0, 1).toUpperCase() + cur_lang.substring(1);
        }
//        position = my_Adapter.getPosition(AppController.getStringPreference(Constants.USER_LANGUAGE,""));
        position = my_Adapter.getPosition(str_lang);
        sp_reason.setSelection(position);


        sp_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {


                String item = adapter.getItemAtPosition(position).toString();

                sp_reason.setSelection(position);
                if (!item.equals("Select Language")) {
                    str_lang = item;
                    str_lang = str_lang.substring(0, 1).toLowerCase() + str_lang.substring(1);
//                    undeliverConfirm.setReason(item);
                    bt_submit.setAlpha(1);
                    bt_submit.setEnabled(true);

                } else {
                    bt_submit.setAlpha(0.4F);
                    bt_submit.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Please Select a Valid Language", Toast.LENGTH_LONG).show();
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

                AppController.storeStringPreferences(Constants.USER_LANGUAGE, str_lang);
                if (str_lang.equals("tamil")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("ta");
                    restartActivity();
                } else if (str_lang.equals("telugu")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("te");
                    restartActivity();
                } else if (str_lang.equals("marathi")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("mr");
                    restartActivity();
                } else if (str_lang.equals("hindi")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("hi");
                    restartActivity();
                } else if (str_lang.equals("punjabi")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("pa");
                    restartActivity();
                } else if (str_lang.equals("odia")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("or");
                    restartActivity();
                } else if (str_lang.equals("bengali")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("be");
                    restartActivity();
                } else if (str_lang.equals("kannada")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("kn");
                    restartActivity();
                } else if (str_lang.equals("assamese")) {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("as");
                    restartActivity();
                } else {
                    snackbar_flag = false;
//                    if(gps_snackbar != null) {
//                        gps_snackbar.dismiss();
//                    }
                    AppController.setLocale("en");
                    restartActivity();
                }
                alertDialog.dismiss();

            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public ArrayList<String> getTableValues() {
        my_array.add("Select Language");
        try {
//            Cursor getSchemeValue = database.rawQuery("select * from UndeliveredReasonMaster  ", null);
            Cursor getSchemeValue = database.rawQuery("select * from LanguageMaster where is_active = '1'  ", null);
            System.out.println("COUNT : " + getSchemeValue.getCount());
            if (getSchemeValue.moveToFirst()) {
                do {
                    String NAME = getSchemeValue.getString(getSchemeValue.getColumnIndex("language"));
                    String upperString = NAME.substring(0, 1).toUpperCase() + NAME.substring(1);
//                    Log.v("getSchemeValue",NAME);
                    my_array.add(upperString);

                } while (getSchemeValue.moveToNext());
            }
            getSchemeValue.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error encountered.",
                    Toast.LENGTH_LONG);
        }
        return my_array;
    }

    public void langcount() {
        Cursor getshowdetails = database.rawQuery("SELECT * FROM orderheader  ", null);
        getshowdetails.moveToFirst();
        if (getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {
                Log.v("lang_product_name", getshowdetails.getString(getshowdetails.getColumnIndex("referenceNumber")));
                Log.v("lang_product_ta", getshowdetails.getString(getshowdetails.getColumnIndex("tamil")));
                Log.v("lang_product_hi", getshowdetails.getString(getshowdetails.getColumnIndex("hindi")));
                Log.v("lang_product_be", getshowdetails.getString(getshowdetails.getColumnIndex("bengali")));
                getshowdetails.moveToNext();

            }
        }
        getshowdetails.close();
    }

    public void downloadLanguages() {
        String get_uname = null;
        String get_pass = null;
        String language_flag = null;
        String getFirebaseId = AppController.getStringPreference(Constants.DEVICE_TOKEN_REGID, "");
        String getDeviceModel = AppController.getStringPreference(Constants.DEVICE, "");
        Cursor uname = database.rawQuery("Select * from UserMaster ", null);

        if (uname.getCount() > 0) {
            uname.moveToFirst();
//            Log.v("clicked", String.valueOf(uname.getCount()));

            get_uname = uname.getString(uname.getColumnIndex("username"));
            get_pass = uname.getString(uname.getColumnIndex("password"));
            language_flag = "1";


        }
        uname.close();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);
        LoginReq login = new LoginReq();
        JSONObject paramObject = null;
        login.setUsername(get_uname);
        login.setPassword(get_pass);
        try {
            paramObject = new JSONObject();
            paramObject.put("username", login.getUsername());
            paramObject.put("password", login.getPassword());
            paramObject.put("firebase_id", getFirebaseId); // updating firebase regid during login
            paramObject.put("device_info", getDeviceModel); // updating device model during login
            paramObject.put("language_flag", language_flag);

            Log.v("getUserAccess", getFirebaseId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

        final Observable<LoginResp> observable = apiService.getLogin(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<LoginResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginResp value) {
                loginList = value.getLogin();
                languageList = value.getLanguageArray();

                if (value.getResMsg().equals("login success")) {
//                    Log.v("get_response", value.getResMsg());
                    for (int i = 0; i < loginList.size(); i++) {


                        for (int j = 0; j < loginList.get(i).getLanguageArray().size(); j++) {
                            Log.v("getLanguageArray", loginList.get(i).getLanguageArray().get(j).getLanguage());
                            Cursor uname = database.rawQuery("Select * from LanguageMaster where language_id = '" + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "' ", null);

                            if (uname.getCount() == 0) {
                                uname.moveToFirst();
                                String insertUndeliveredReason = "Insert into LanguageMaster (language ,language_id,is_active) Values('" + loginList.get(i).getLanguageArray().get(j).getLanguage() + "', " + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "" +
                                        "," + loginList.get(i).getLanguageArray().get(j).getLanguage_active() + ")";
                                database.execSQL(insertUndeliveredReason);
                            } else {
                                String updateUndeliveredReason = "Update LanguageMaster set language_id = " + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "," +
                                        "language = '" + loginList.get(i).getLanguageArray().get(j).getLanguage() + "', is_active ='" + loginList.get(i).getLanguageArray().get(j).getLanguage_active() + "' where language_id = '" + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "'";
                                database.execSQL(updateUndeliveredReason);
                            }
                        }


                    }
                } else if (value.getResMsg().equals("login failed")) {

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

    public void getServiceData() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final InthreeApi apiService = retrofit.create(InthreeApi.class);
        final OrderReq order = new OrderReq();
        JSONObject paramObject = null;


        order.setId(userId);

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
                Log.v("get_data_msg1", value.getRes_msg());
                if (value.getRes_msg().equals("service success")) {

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

                orderServiceCount();
                serviceDashboard();
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

    private void syncUploadSer() {

        getImagesToSync();

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
        } else {
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


    public static void serviceDashboard() {
        Yservice_values.clear();
        Cursor needSuccess = database.rawQuery("Select Count(order_id)  from serviceMaster  where " +
                "(sync_status = 'U' OR sync_status ='C' OR sync_status='E') AND (delivery_status='complete' ) ", null);

        /*Success*/
        if (needSuccess.getCount() > 0) {
            needSuccess.moveToFirst();

            final int nesy = Integer.parseInt(needSuccess.getString(0));
//            Log.e("S", String.valueOf(nesy));
            if (nesy > 0) {
                dash_complete.setText(String.valueOf(nesy));
                Yservice_values.add(new Entry((float) nesy, 0));
            } else {
                dash_complete.setText("0");
                Yservice_values.add(new Entry((float) 0, 0));
            }

        }




        /*Failed*/
        Cursor needFailed = database.rawQuery("Select  Count(order_id)  from serviceMaster  where delivery_status = " +
                "'incomplete' AND  (sync_status = 'U' OR sync_status ='C' OR sync_status='E') ", null);
        if (needFailed.getCount() > 0) {
            needFailed.moveToFirst();
            final int nesy = Integer.parseInt(needFailed.getString(0));
//            Log.e("F", String.valueOf(nesy));
            if (nesy > 0) {
                dash_incomplete.setText(String.valueOf(nesy));
                Yservice_values.add(new Entry((float) nesy, 1));
            } else {
                dash_incomplete.setText("0");
                Yservice_values.add(new Entry((float) 0, 1));
            }
        }

        /*Pending*/
        Cursor needpend = database.rawQuery("Select  Count(order_id)  from serviceMaster  where sync_status = " +
                "'P'", null);
        if (needpend.getCount() > 0) {
            needpend.moveToFirst();
            final int nesy = Integer.parseInt(needpend.getString(0));
//            Log.v("Pending_summary", String.valueOf(nesy));
            if (nesy > 0) {
                Yservice_values.add(new Entry((float) nesy, 2));
                dash_service_pend.setText(String.valueOf(nesy));
//                Log.v("Pending_summary1", String.valueOf(yvalues.get(0)));

            } else {
                dash_service_pend.setText("0");
                Yservice_values.add(new Entry((float) 0, 2));
            }
        }

        needpend.close();
        needSuccess.close();
        needFailed.close();


        dataSet = new PieDataSet(Yservice_values, "");


        Xservice_values = new ArrayList<String>();
        for (int i = 0; i < Yservice_values.size(); i++) {
            if (Yservice_values.get(i).getXIndex() == 1) {
                colors = addElement(colors, Color.parseColor("#c11e0c"));
                Xservice_values.add("Total number of complete");
            }
            if (Yservice_values.get(i).getXIndex() == 2) {
                colors = addElement(colors, Color.parseColor("#166b94"));
                Xservice_values.add("Total number of incomplete");
            }
            if (Yservice_values.get(i).getXIndex() == 0) {
                colors = addElement(colors, Color.parseColor("#e7a528"));
                Xservice_values.add("Total number of pending");
            }
        }

        dataSet.setColors(colors);
        data = new PieData(Xservice_values, dataSet);
        data.setValueFormatter(new MyValueFormatter());
        pc_service_orders.setTransparentCircleRadius(10);
        pc_service_orders.setData(data);
        pc_service_orders.setDragDecelerationFrictionCoef(0.95f);
        data.setValueTextSize(22f);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.dark_white));
        pc_service_orders.setDrawSliceText(false);
        pc_service_orders.highlightValues(null);
        dataSet.setSliceSpace(2f);
        pc_service_orders.setDescription("");
        pc_service_orders.setMaxAngle(900);
        pc_service_orders.getLegend().setEnabled(false);
        pc_service_orders.setRotationEnabled(false);
        pc_service_orders.invalidate();
        pc_service_orders.animateXY(1400, 1400);


    }

    public static void orderServiceCount() {

        Cursor getcount = database.rawQuery("SELECT count(order_id) as Count FROM serviceMaster", null);

        if (getcount.getCount() > 0) {
            getcount.moveToFirst();


            if (!getcount.isAfterLast()) {
                do {
                    orderServicecount = getcount.getString(getcount.getColumnIndex("Count"));
                    pc_service_orders.setCenterText("Total Service" + " " + orderServicecount);
                    pc_service_orders.setCenterTextSize(24f);
                    pc_service_orders.setCenterTextColor(Color.BLUE);
                } while (getcount.moveToNext());
            }
            getcount.close();


            if (get_currentTime != null && sync != null) {
                RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(1);
                anim.setDuration(2000);
                img_sync.setAnimation(anim);
                img_sync.startAnimation(anim);
            }


        } else {
            img_sync.clearAnimation();


        }
    }

    public void changeStatusService() {
        Log.v("changeStatusService", "- " + "qwerty");
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

   /* public void uploadPickupComplete(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en","US"));
        String currentDateTimeString = format.format(new Date());

//        if(updateComplete()){
        Cursor getPickupDetails = database.rawQuery("select  O.delivery_status,O.customer_name, O.order_number, " +
                "IFNULL(P.shipmentno,0) as shipmentno, IFNULL(P.customerphoto, 0) as customerphoto, IFNULL(P.latitude, 0) as latitude, IFNULL(P.longitude, 0) as longitude, " +
                "IFNULL(P.createdate, 0) as createdate,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O.attempt_count, 0) as attempt_count," +
                "IFNULL(P.customeraddress,0) as shipment_address, IFNULL(P.customerphone,0) as customerphone,IFNULL(P.reason, 0) as reason,IFNULL(P.reason_id, 0) as reason_id, IFNULL(P.pickupstatus, 0) as pickupstatus, IFNULL(order_type, 0) as order_type from orderheader O INNER JOIN PickupConfirmation P on P" +
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
            String pickup_status = getPickupDetails.getString(getPickupDetails.getColumnIndex("pickupstatus"));
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
                paramObject.put("created_at", getPickupDetails.getString(getPickupDetails.getColumnIndex("createdate")));
                paramObject.put("battery", String.valueOf(battery_level));
                paramObject.put("deviceInfo", AppController.getdevice());
                paramObject.put("order_no", getPickupDetails.getString(getPickupDetails.getColumnIndex("order_number")));
                paramObject.put("shipmentnumber", getPickupDetails.getString(getPickupDetails.getColumnIndex("shipmentno")));
                paramObject.put("pickup_status", getPickupDetails.getString(getPickupDetails.getColumnIndex("pickupstatus")));
                paramObject.put("order_type", getPickupDetails.getString(getPickupDetails.getColumnIndex("order_type")));

                jsonFieldObj.put("image", image_url + getPickupDetails.getString(getPickupDetails.getColumnIndex("customerphoto")));
                jsonFieldObj.put("address", getPickupDetails.getString(getPickupDetails.getColumnIndex("shipment_address")));
                jsonFieldObj.put("phone", getPickupDetails.getString(getPickupDetails.getColumnIndex("customerphone")));
                if(!pickup_status.equals("Success")){
                    jsonFieldObj.put("reason", getPickupDetails.getString(getPickupDetails.getColumnIndex("reason")));
                    jsonFieldObj.put("reason_id", getPickupDetails.getString(getPickupDetails.getColumnIndex("reason_id")));
                }else{
                    jsonFieldObj.put("reason", "");
                    jsonFieldObj.put("reason_id", "");
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
            Log.v("pickup_param",paramObject.toString());
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
                    Log.v("undeliveredupload","- "+value.getRes_msg()+"-- "+ value.getRes_code());
//                        uploadImage();
                    if (value.getRes_msg().equalsIgnoreCase("pickup success")) {

                        database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + pick_attempt_count + " where Shipment_Number ='" +
                                sn + "' ");
                    } else if (value.getRes_msg().equalsIgnoreCase("pickup updated")) {

                        database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + pick_attempt_count + " where Shipment_Number ='" +
                                sn + "' ");
                    } else {
//                        Logger.showShortMessage(MainActivity.this, getResources().getString(R.string.undeli_notsuccess_msg));

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
    }*/


    /**
     * This is for pick up complete
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


            order.setId(userId);

            try {
                paramObject = new JSONObject();
                paramObject.put("runner_id", order.getId());
                paramObject.put("token", "123456");
                paramObject.put("latitude", lat);
                paramObject.put("longitude", lang);
                paramObject.put("deviceInfo", AppController.getdevice());
                paramObject.put("battery", battery_level);
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
                    if (value.getResMsg().equals("order success")) {
                        Log.v("get_data_msg", value.getResMsg());

                        for (int i = 0; i < detailVal.size(); i++) {
                            Cursor checkOrder = database.rawQuery("Select * from orderheader where Shipment_Number = '" +
                                            detailVal.get(i).getShipmentid() + "'",
                                    null);
                            if (checkOrder.getCount() == 0) {

                                Log.v("OrderResp", detailVal.get(i).getShipmentid() + "branch code -" + detailVal.get(i).getClientBranchCode() + "delivery to -" + detailVal.get(i).getDeliveryTo() + "shipid" + detailVal.get(i).getBranchCode() + "getotp" + "invoice_date -" + detailVal.get(i).getInvoiceDate() + "Incoive -" + detailVal.get(i).getInvoiceNumber());
                                // Log.v("OrderResp_branchcode", detailVal.get(i).getClientBranchCode());
                                // Log.v("OrderResp_delivery", detailVal.get(i).getDeliveryTo());

                                try {
                                    date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String outputDateStr = outputFormat.format(date);
                                Log.v("outputDateStr", " - " + detailVal.get(i).getToBeDeliveredBy() + " - " + outputDateStr);

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
                                }
                                /* language parsing json ends*/

                                String insertOrderHearder = "Insert into orderheader(loanrefno,invoice_date,invoice_id,delivery_to,branch_code,order_number,customer_name," +
                                        "customer_contact_number,alternate_contact_number,to_be_delivered_by,billing_address,billing_city,billing_pincode," +
                                        "billing_telephone,shipping_address,shipping_city,shipping_pincode, shipping_telephone,invoice_amount,payment_mode," +
                                        "client_branch_name,branch_address,branch_pincode,branch_contact_number,group_leader_name,group_leader_contact_number," +

                                        "slot_number,referenceNumber,processDefinitionCode,Shipment_Number,sync_status,delivery_status,valid,attempt_count,tamil ,telugu, punjabi, hindi, bengali ,kannada,assam,orissa,marathi,otp,urn,order_type,max_attempt,delivery_aadhar_required,virtual_id) " +

                                        "Values('" + detailVal.get(i).getLoanrefno() + "','" + detailVal.get(i).getInvoiceDate() + "','" + detailVal.get(i).getInvoiceNumber() + "','" + detailVal.get(i).getDeliveryTo() + "','" + detailVal.get(i).getClientBranchCode() + "','" + detailVal.get(i).getOrderid() + "', '" + detailVal.get(i).getCustomerName() + "', '" + customer_contact_no + "'," +
                                        "'" + detailVal.get(i).getAlternateContactNumber() + "', '" + outputDateStr + "', " +
                                        "'" + detailVal.get(i).getBillingAddress() + "', '" + detailVal.get(i).getBillingCity() + "', '" + detailVal.get(i).getBillingPincode() + "', '" + detailVal.get(i).getBillingTelephone() + "', '" + detailVal.get(i).getShippingAddress() + "', '" + detailVal.get(i).getShippingCity() + "'," +

                                        " '" + detailVal.get(i).getShippingPincode() + "', '" + detailVal.get(i).getShippingTelephone() + "', '" + detailVal.get(i).getAmount() + "', '" + detailVal.get(i).getPaymentMode() + "', '" + detailVal.get(i).getClient_branch_name() + "', '" + detailVal.get(i).getBranch_address() + "', '" + detailVal.get(i).getBranch_pincode() + "'" +
                                        ", '" + detailVal.get(i).getBranch_contact_number() + "', '" + detailVal.get(i).getGroup_leader_name() + "', '" + detailVal.get(i).getGroup_leader_contact_number() + "', '" + detailVal.get(i).getSlot_number() + "', '" + detailVal.get(i).getReference() + "', '', '" + detailVal.get(i).getShipmentid() + "', 'P', '', '" + detailVal.get(i).getDownloadSync() + "', " + detailVal.get(i).getAttempt() + ",'" + tamil_val + "','" + telugu_val + "','" + punjabi_val + "','" + hindi_val + "','" + bengali_val + "','" + kannada_val + "','" + assam_val + "','" + orissa_val + "','" + marathi_val + "','" + detailVal.get(i).getOtp() + "','" + detailVal.get(i).getUrn() + "','" + detailVal.get(i).getOrder_type() + "','" + detailVal.get(i).getMax_attempt() + "'," + detailVal.get(i).getDelivery_aadhar_required() + ",'" + value.getVirtual_id() + "')";

                                database.execSQL(insertOrderHearder);

                            } else {
                                try {
                                    date = inputFormat.parse(detailVal.get(i).getToBeDeliveredBy());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String outputDateStr = outputFormat.format(date);
                                Log.v("outputDateStr1", " - " + detailVal.get(i).getToBeDeliveredBy() + " - " + outputDateStr);
                                Log.v("ordertype1", " - " + detailVal.get(i).getOrder_type());
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
                                        /*String tamil_name = tamilOneObject.getString("customer_name");
                                        String tamil_branch = tamilOneObject.getString("branch_name");
                                        String tamil_branch_deliaddr = tamilOneObject.getString("delivery_address");
                                        String tamil_branch_addr = tamilOneObject.getString("branch_address");
                                        String tamil_branch_city = tamilOneObject.getString("city");
                                        tamilLangParamObject = new JSONObject();
                                        tamilLangParamObject.put("delivery_address", tamil_branch_deliaddr);
                                        tamilLangParamObject.put("city", tamil_branch_city);
                                        tamilLangParamObject.put("branch_name", tamil_branch);
                                        tamilLangParamObject.put("branch_address", tamil_branch_addr);
                                        tamilLangParamObject.put("customer_name", tamil_name);*/
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


                                      /*  String hindi_name = hindiOneObject.getString("customer_name");

                                        String hindi_branch = hindiOneObject.getString("branch_name");
                                        String hindi_branch_deliaddr = hindiOneObject.getString("delivery_address");
                                        String hindi_branch_addr = hindiOneObject.getString("branch_address");
                                        String hindi_branch_city = hindiOneObject.getString("city");
                                        hindiLangParamObject = new JSONObject();
                                        hindiLangParamObject.put("delivery_address", hindi_branch_deliaddr);
                                        hindiLangParamObject.put("city", hindi_branch_city);
                                        hindiLangParamObject.put("branch_name", hindi_branch);
                                        hindiLangParamObject.put("branch_address", hindi_branch_addr);
                                        hindiLangParamObject.put("customer_name", hindi_name);*/
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
                                     /*   String bengali_name = bengaliOneObject.getString("customer_name");

                                        String bengali_branch = bengaliOneObject.getString("branch_name");
                                        String bengali_branch_deliaddr = bengaliOneObject.getString("delivery_address");
                                        String bengali_branch_addr = bengaliOneObject.getString("branch_address");
                                        String bengali_branch_city = bengaliOneObject.getString("city");
                                        bengaliLangParamObject = new JSONObject();
                                        bengaliLangParamObject.put("delivery_address", bengali_branch_deliaddr);
                                        bengaliLangParamObject.put("city", bengali_branch_city);
                                        bengaliLangParamObject.put("branch_name", bengali_branch);
                                        bengaliLangParamObject.put("branch_address", bengali_branch_addr);
                                        bengaliLangParamObject.put("customer_name", bengali_name);*/
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

                                        /*String marathi_name = marathiOneObject.getString("customer_name");

                                        String marathi_branch = marathiOneObject.getString("branch_name");
                                        String marathi_branch_deliaddr = marathiOneObject.getString("delivery_address");
                                        String marathi_branch_addr = marathiOneObject.getString("branch_address");
                                        String marathi_branch_city = marathiOneObject.getString("city");
                                        marathiLangParamObject = new JSONObject();
                                        marathiLangParamObject.put("delivery_address", marathi_branch_deliaddr);
                                        marathiLangParamObject.put("city", marathi_branch_city);
                                        marathiLangParamObject.put("branch_name", marathi_branch);
                                        marathiLangParamObject.put("branch_address", marathi_branch_addr);
                                        marathiLangParamObject.put("customer_name", marathi_name);*/
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


                                       /* String assam_name = assamOneObject.getString("customer_name");

                                        String assam_branch = assamOneObject.getString("branch_name");
                                        String assam_branch_deliaddr = assamOneObject.getString("delivery_address");
                                        String assam_branch_addr = assamOneObject.getString("branch_address");
                                        String assam_branch_city = assamOneObject.getString("city");
                                        assamLangParamObject = new JSONObject();
                                        assamLangParamObject.put("delivery_address", assam_branch_deliaddr);
                                        assamLangParamObject.put("city", assam_branch_city);
                                        assamLangParamObject.put("branch_name", assam_branch);
                                        assamLangParamObject.put("branch_address", assam_branch_addr);
                                        assamLangParamObject.put("customer_name", assam_name);*/
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

                                      /*  String orissa_name = orissaOneObject.getString("customer_name");

                                        String orissa_branch = orissaOneObject.getString("branch_name");
                                        String orissa_branch_deliaddr = orissaOneObject.getString("delivery_address");
                                        String orissa_branch_addr = orissaOneObject.getString("branch_address");
                                        String orissa_branch_city = orissaOneObject.getString("city");
                                        orissaLangParamObject = new JSONObject();
                                        orissaLangParamObject.put("delivery_address", orissa_branch_deliaddr);
                                        orissaLangParamObject.put("city", orissa_branch_city);
                                        orissaLangParamObject.put("branch_name", orissa_branch);
                                        orissaLangParamObject.put("branch_address", orissa_branch_addr);
                                        orissaLangParamObject.put("customer_name", orissa_name);*/
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

                                       /* String telugu_name = teluguOneObject.getString("customer_name");

                                        String telugu_branch = teluguOneObject.getString("branch_name");
                                        String telugu_branch_deliaddr = teluguOneObject.getString("delivery_address");
                                        String telugu_branch_addr = teluguOneObject.getString("branch_address");
                                        String telugu_branch_city = teluguOneObject.getString("city");
                                        teluguLangParamObject = new JSONObject();
                                        teluguLangParamObject.put("delivery_address", telugu_branch_deliaddr);
                                        teluguLangParamObject.put("city", telugu_branch_city);
                                        teluguLangParamObject.put("branch_name", telugu_branch);
                                        teluguLangParamObject.put("branch_address", telugu_branch_addr);
                                        teluguLangParamObject.put("customer_name", telugu_name);*/
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

                                     /*   String kannada_name = kannadaOneObject.getString("customer_name");

                                        String kannada_branch = kannadaOneObject.getString("branch_name");
                                        String kannada_branch_deliaddr = kannadaOneObject.getString("delivery_address");
                                        String kannada_branch_addr = kannadaOneObject.getString("branch_address");
                                        String kannada_branch_city = kannadaOneObject.getString("city");
                                        kannadaLangParamObject = new JSONObject();
                                        kannadaLangParamObject.put("delivery_address", kannada_branch_deliaddr);
                                        kannadaLangParamObject.put("city", kannada_branch_city);
                                        kannadaLangParamObject.put("branch_name", kannada_branch);
                                        kannadaLangParamObject.put("branch_address", kannada_branch_addr);
                                        kannadaLangParamObject.put("customer_name", kannada_name);*/
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

                                       /* String punjab_name = punjabOneObject.getString("customer_name");
                                        String punjab_branch = punjabOneObject.getString("branch_name");
                                        String punjab_branch_deliaddr = punjabOneObject.getString("delivery_address");
                                        String punjab_branch_addr = punjabOneObject.getString("branch_address");
                                        String punjab_branch_city = punjabOneObject.getString("city");
                                        punjabLangParamObject = new JSONObject();
                                        punjabLangParamObject.put("delivery_address", punjab_branch_deliaddr);
                                        punjabLangParamObject.put("city", punjab_branch_city);
                                        punjabLangParamObject.put("branch_name", punjab_branch);
                                        punjabLangParamObject.put("branch_address", punjab_branch_addr);
                                        punjabLangParamObject.put("customer_name", punjab_name);*/
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
                                        "hindi = '" + hindi_val + "', " +
                                        "assam = '" + assam_val + "'," +
                                        "punjabi = '" + punjabi_val + "', " +
                                        "marathi = '" + marathi_val + "'," +
                                        "telugu = '" + telugu_val + "'," +
                                        "kannada = '" + kannada_val + "'," +
                                        "orissa = '" + orissa_val + "'," +

                                        "bengali = '" + bengali_val + "'," +

                                        "virtual_id = '" + value.getVirtual_id() + "'" +


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
                                if (item_json != null && !item_json.equals("null")) {
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
                                        Log.v("tamil_pval", "-- " + tamil_pval);
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
                                            //:Punjabi JSON
                                            JSONObject orissaOneObject = obj.getJSONObject("punjabi");
                                            String punjab_pname = orissaOneObject.getString("product_name");
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

                                    } catch (Throwable t) {
                                        Log.e("lang_json", "Could not parse malformed JSON: \"" + bengali_pval + "\"");
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
//                        changeOrderStatus(order.getId(), detailVal.get(i).getShipmentid());
                            checkOrder.close();
                        }

                        ordercount();
                        statusSummarDeliver();
                    } else if (value.getResMsg().equals("order failed")) {

                    } else {

                    }
             /*   if(value.getRes_msg().equals("order success")) {
                    Log.v("get_response", value.getOrder_number());


                    for (int i = 0; i < orderVal.size(); i++) {
                        OrderResp student = new OrderResp();
                        Cursor checkProducts = database.rawQuery("Select * from ProductDetails where shipmentnumber = '"+
                                        value.getShipment_number()+"'",
                                null);
                        if (checkProducts.getCount() == 0){
                            String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code)" +
                                    " Values ('"+ value.getShipment_number() +"', '"+ orderVal.get(i).getProduct_name()
                                    +"', '"+ orderVal.get(i).getQuantity() +"', '"+ orderVal.get(i).getAmount() +"', '"+
                                    orderVal.get(i).getProduct_code() +"')";
                            database.execSQL(insertProduct);
                        }else{
                            database.execSQL("DELETE FROM ProductDetails where shipmentnumber = '"+value.getShipment_number()+"'");

                            String insertProduct = "Insert into ProductDetails(shipmentnumber,product_name,quantity,amount,product_code)" +
                                    " Values ('"+ value.getShipment_number() +"', '"+ orderVal.get(i).getProduct_name()
                                    +"', '"+ orderVal.get(i).getQuantity() +"', '"+ orderVal.get(i).getAmount() +"', '"+
                                    orderVal.get(i).getProduct_code() +"')";
                            database.execSQL(insertProduct);

                        }

                        Log.v("getAddress", orderVal.get(i).getProduct_name());
//                        Log.v("getAddress", "--");
                        orderList.add(student);

                    }
                }else{
                    Log.v("get_response",value.getRes_msg());
                }*/
                    changestatusssync = new ChangeStatusAsync();
                    changestatusssync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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