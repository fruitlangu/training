package com.inthree.boon.deliveryapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.NetTime.TrueTimeRx;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.ProductShowAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.model.ProductShowModel;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.view.View.GONE;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener {


    private static final int REQUEST = 100;
    private static final int REQUESTBFIL = 456;
    ProductShowAdapter ProdshowAdapter;

    public List<ProductShowModel> ProdshowArraylist;
    private ArrayList<ProductShowModel> msqlitebeans = new ArrayList<>();
    ProductShowModel item;
    Context mContext;
    Activity activity;

    RecyclerView product_show_adapter;

    LinearLayout ll_branch_address;
    TextView tv_reference_number;
    TextView tv_deliveryType;
    TextView tv_shipping_num;
    TextView tv_order_number;
    TextView tv_orderId;
    TextView tv_customer_name;
    TextView tv_deliveryDate;
    TextView tv_shippingAddress;
    TextView tv_shippingCity;
    TextView tv_shipping_pincode;
    TextView tv_ship_invoice_num;
    TextView tv_ship_invoice_amt;
    TextView tv_ship_pay_mode;
    TextView tv_ship_itemcode;
    TextView tv_ship_client_branch_name;
    TextView tv_ship_branch_add;
    TextView tv_ship_branch_pincode;
    TextView tv_ship_attempt_count;
    TextView tv_amount_collected;
    LinearLayout ll_amount_collected;
    TextView tv_to_be_delivered;
    String invoice_amt;


    LinearLayout ll_delivered;
    LinearLayout ll_par_delivered;
    LinearLayout ll_undelivered;
    LinearLayout ll_call_user;
    LinearLayout ll_location;
    LinearLayout ll_show_details;
    LinearLayout ll_hide_details;
    LinearLayout ll_attempt_date;

    LinearLayout lay_delivery;

  //  NavigationView lay_delivery;

    AlertDialog levelDialog;
    RadioGroup radiolow;
    RadioGroup rg;
    Button procd;
    RadioButton rb_customer;
    RadioButton rb_alternate;
    RadioButton rb_shipping;
    RadioButton rb_branch;
    RadioButton rb_groupleader;
    Dialog mBottomSheetDialog;
    int checkId;

    String order_num;

    String getCustomerNo;
    String getAlternateNo;
    String getShippingNo;
    String getBranchNo;
    String getGroupLeaderNo;

    String getmaxqty;

    int maxget;

    private static final String DB_NAME = "boonboxdelivery.sqlite";
    SQLiteDatabase database;
    public static final String MyPREFERENCES = "MyPrefs";

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    private  boolean hideshow = true;

    /**
     * Get the shipment number from the order details
     */
    String shipmentNumber;

    /*
    * Get Reference Number
    **/
    private String reference_no;

    /**
     * Get the details of address
     */
    private Spinner sp_route;

    /****
     * Get back to previous activity
     */
    private AppCompatButton bt_back;

    /**
     * Get back to submit button
     */
    private AppCompatButton bt_submit;

    /**
     * Get te details of order address
     */
    private ArrayAdapter my_Adapter;

    /**
     * Get the spinner
     */
    private AbsSpinner sp_reason;

    /**
     * Get the details of array
     */
    ArrayList<String> my_array = new ArrayList<String>();

    /**
     * Get the ship id
     */
    private String shipId;

    /**
     *
     * private String orderId;
     */
    private String orderID;

    /**
     * Get the activity name
     */
    String activityName;

    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;
    private String delivery_status;
    String undelivery_status;
    String payment_mode;
    private View viewCall;
    TextView tv_attempt_label;
    TextView tv_attempt_date;
    String user_language;
    Locale myLocale;

    LinearLayout ll_pickup;
    LinearLayout ll_buttonlay;
    String order_type;
    String branch_code;
    LinearLayout ll_pickupfailed;

    LinearLayout ll_bran_name;
    LinearLayout ll_bran_pincode;
    View view_gap_brname;
    View view_gap_brpin;
    View view_gap_braddr;
    String aadhaarEnabled;
    String getBfilCustomerNo;
    String getBfilAlternateNo;
    String getBfilVirtualNo;
    LinearLayout ll_invoiceamount;
    LinearLayout ll_paymentmode;
    View v_invoiceamt;
    View v_paymentmode;
    View v_amountcollected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tv_deliveryType = (TextView) findViewById(R.id.tv_deliveryType);
        tv_reference_number= (TextView) findViewById(R.id.tv_reference_number);
//        tv_shipping_num = (TextView) findViewById(R.id.tv_shipping_num);
//        tv_order_number = (TextView) findViewById(R.id.tv_order_number);
        tv_orderId = (TextView) findViewById(R.id.tv_orderId);
        tv_customer_name = (TextView) findViewById(R.id.tv_customer_name);
        tv_deliveryDate = (TextView) findViewById(R.id.tv_deliveryDate);
        tv_shippingAddress = (TextView) findViewById(R.id.tv_shippingAddress);
        tv_shippingCity = (TextView) findViewById(R.id.tv_shippingCity);
        tv_shipping_pincode = (TextView) findViewById(R.id.tv_shippingpincode);
        tv_ship_invoice_num = (TextView) findViewById(R.id.tv_shippinginvoice);
        tv_ship_invoice_amt = (TextView) findViewById(R.id.tv_shippinginvoiceamt);
        tv_ship_pay_mode = (TextView) findViewById(R.id.tv_shippingpaymode);
        tv_ship_itemcode = (TextView) findViewById(R.id.tv_shippingitemcode);
        tv_ship_client_branch_name = (TextView) findViewById(R.id.tv_shipping_bran_name);
        tv_ship_branch_add = (TextView) findViewById(R.id.tv_shipping_bran_add);
        tv_ship_branch_pincode = (TextView) findViewById(R.id.tv_shipping_bran_pin);
        tv_ship_attempt_count = (TextView) findViewById(R.id.tv_shipping_att_count);
        tv_amount_collected = (TextView) findViewById(R.id.tv_amount_collected);
        ll_amount_collected = (LinearLayout) findViewById(R.id.ll_amount_collected);
        tv_to_be_delivered = (TextView) findViewById(R.id.tv_to_be_delivered);
        ll_branch_address = (LinearLayout) findViewById(R.id.ll_branch_address);

        ll_invoiceamount = findViewById(R.id.ll_invoiceamount);
        ll_paymentmode = findViewById(R.id.ll_paymentmode);
        v_invoiceamt =  findViewById(R.id.v_invoiceamt);
        v_paymentmode =  findViewById(R.id.v_paymentmode);
        v_amountcollected =  findViewById(R.id.v_amountcollected);


        ll_pickup = (LinearLayout) findViewById(R.id.ll_pickup);
        ll_buttonlay = (LinearLayout) findViewById(R.id.ll_buttonlay);


        ll_bran_name = (LinearLayout) findViewById(R.id.ll_bran_name);
        ll_bran_pincode = (LinearLayout) findViewById(R.id.ll_bran_pincode);
        view_gap_brname = (View) findViewById(R.id.view_gap_brname);
        view_gap_brpin = (View) findViewById(R.id.view_gap_brpin);
        view_gap_braddr = (View) findViewById(R.id.view_gap_braddr);


        product_show_adapter =(RecyclerView)findViewById(R.id.product_details);
        tv_attempt_label = (TextView) findViewById(R.id.tv_attempt_label);
        tv_attempt_date = (TextView) findViewById(R.id.tv_attempt_date);
        ll_attempt_date = (LinearLayout) findViewById(R.id.ll_attempt_date);

        lay_delivery = (LinearLayout) findViewById(R.id.lay_del);
        ll_delivered = (LinearLayout) findViewById(R.id.ll_delivered);
        ll_par_delivered = (LinearLayout) findViewById(R.id.ll_partial_delivered);
        ll_undelivered = (LinearLayout) findViewById(R.id.ll_undelivered);
        ll_call_user = (LinearLayout) findViewById(R.id.ll_call_user);
        ll_location = (LinearLayout) findViewById(R.id.ll_location);
        ll_pickupfailed = (LinearLayout) findViewById(R.id.ll_pickupfailed);

        ll_show_details = (LinearLayout)findViewById(R.id.show_deteils);
        ll_hide_details =(LinearLayout)findViewById(R.id.view_lay);
        Intent dash = getIntent();
        if (null != dash) {
            order_num = dash.getStringExtra("order_num");
            shipId = dash.getStringExtra("ship_id");
            order_type = dash.getStringExtra("order_type");
        } else {
            order_num = "";
            shipId="";
        }


        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();

        GridLayoutManager glm = new GridLayoutManager(this, 1);

        product_show_adapter.setLayoutManager(glm);
        product_show_adapter.setNestedScrollingEnabled(false);

        getOrderDetails(order_num,shipId);

//        getOrderDetails(order_num);

        productshowdetails();
        getMapAddress();

        productcount();


        if(order_type.equals("2")){
            ll_pickup.setVisibility(View.VISIBLE);
            ll_buttonlay.setVisibility(GONE);
            ll_invoiceamount.setVisibility(GONE);
            v_invoiceamt.setVisibility(GONE);
            v_paymentmode.setVisibility(GONE);
            ll_paymentmode.setVisibility(GONE);
        }else{
            ll_buttonlay.setVisibility(View.VISIBLE);
            ll_pickup.setVisibility(GONE);
        }


        ll_delivered.setOnClickListener(this);
        ll_par_delivered.setOnClickListener(this);
        ll_undelivered.setOnClickListener(this);
        ll_call_user.setOnClickListener(this);
        ll_hide_details.setOnClickListener(this);
        tv_ship_itemcode.setText(R.string.view_details);
        ll_pickup.setOnClickListener(this);
        getValues();
        /*ll_hide_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hideshow==true) {
                    ll_show_details.setVisibility(View.VISIBLE);
                    tv_ship_itemcode.setText("Hide Details");
                    tv_ship_itemcode.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.uparrow, 0);
                    hideshow = false;
                }else if(hideshow == false){
                    ll_show_details.setVisibility(View.GONE);
                    tv_ship_itemcode.setText("View Details");
                    tv_ship_itemcode.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.downarrow, 0);
                    hideshow = true;
                }
            }
        });*/


        ll_location.setOnClickListener(this);

    }


    public void getOrderDetails(String ordernumber,String shipno) {

//        Cursor getOrders = database.rawQuery("Select * from orderheader where order_number = '" + ordernumber + "' ", null);
        Cursor getOrders = database.rawQuery("Select * from orderheader where Shipment_Number = '" + shipno + "' ", null);

        if (getOrders.getCount() > 0) {
            getOrders.moveToFirst();

            delivery_status = getOrders.getString(getOrders.getColumnIndex("sync_status"));
            undelivery_status = getOrders.getString(getOrders.getColumnIndex("delivery_status"));
            Log.v("undelivery_status"," - "+ undelivery_status);
            payment_mode = getOrders.getString(getOrders.getColumnIndex("payment_mode"));

            order_type = getOrders.getString(getOrders.getColumnIndex("order_type"));

            branch_code = getOrders.getString(getOrders.getColumnIndex("delivery_to"));

            if(branch_code.equals("1")){

                ll_delivered.setEnabled(false);
                ll_delivered.setClickable(false);
                ll_delivered.setAlpha(0.6f);

                ll_par_delivered.setEnabled(false);
                ll_par_delivered.setClickable(false);
                ll_par_delivered.setAlpha(0.6f);
            }

            if(order_type.equals("3")){
                ll_par_delivered.setEnabled(false);
                ll_par_delivered.setClickable(false);
                ll_par_delivered.setAlpha(0.6f);
            }

            aadhaarEnabled = getOrders.getString(getOrders.getColumnIndex("delivery_aadhar_required"));


            Log.v("undelivery_status","--"+order_type);
            if (delivery_status.equals("P")) {
                delivery_status = "Pending";
                ll_attempt_date.setVisibility(GONE);
            }
//            else if (delivery_status.equals("S")|| delivery_status.equals("U") || delivery_status.equals("C")) {
//            else if (!undelivery_status.equals("undelivered") && (delivery_status.equals("S")|| delivery_status.equals("U") || delivery_status.equals("C") )) {
            else if (!undelivery_status.equals("undelivered") && (delivery_status.equals("S")|| delivery_status.equals("U") || delivery_status.equals("C") || delivery_status.equals("E") )) {
                delivery_status = "Success";
                lay_delivery.setVisibility(GONE);
//                tv_attempt_label.setText("Delivered Date");
                tv_attempt_label.setText(R.string.delivered_date_new);
                if(payment_mode.equals("COD")){
                    ll_amount_collected.setVisibility(View.VISIBLE);
                    v_amountcollected.setVisibility(View.VISIBLE);
                }

                if(order_type.equals("2")){
                    tv_attempt_label.setText("Pickedup Date");
                }

//                tv_to_be_delivered.setText("Delivered On");


            }
//            else if (delivery_status.equals("F"))
            else if (undelivery_status.equals("undelivered") && (delivery_status.equals("S")|| delivery_status.equals("U") || delivery_status.equals("C") || delivery_status.equals("E")))
            {

                delivery_status = "Failed";
//                tv_to_be_delivered.setText("Date");
//                lay_delivery.setVisibility(View.GONE);
            } else {

                delivery_status = "";
            }
//            Log.v("undelivery_status"," - "+ undelivery_status);
            tv_deliveryType.setText(delivery_status);
            if(delivery_status.equals("Failed")){
                tv_deliveryType.setText(R.string.faild);
            } else if(delivery_status.equals("Pending")){
                tv_deliveryType.setText(R.string.pending);
            }else if(delivery_status.equals("Success")){
                tv_deliveryType.setText(R.string.success);
            }

            if(order_type.equals("3") || order_type.equals("2") ){
                Log.v("getDeliveryConfirm","- "+ order_type+"- "+ shipmentNumber);
                tv_to_be_delivered.setText("To Be Picked Up");
                Cursor getDeliveryConfirm = database.rawQuery("Select * from PickupConfirmation where shipmentno = '" + shipId + "' ", null);
                if (getDeliveryConfirm.getCount() > 0){
                    getDeliveryConfirm.moveToFirst();
                    String delivered_date = getDeliveryConfirm.getString(getDeliveryConfirm.getColumnIndex("createdate"));
                    String pickupstatus = getDeliveryConfirm.getString(getDeliveryConfirm.getColumnIndex("pickupstatus"));
                    if(pickupstatus.equals("Failed")){
                       lay_delivery.setVisibility(View.VISIBLE);
                       ll_pickupfailed.setVisibility(View.VISIBLE);
                    }else{
                        lay_delivery.setVisibility(View.GONE);
                        ll_pickupfailed.setVisibility(View.GONE);
                    }
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date date = null;
                    try {
                        date = inputFormat.parse(delivered_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(date);

                    tv_attempt_date.setText(outputDateStr);
//                    tv_attempt_date.setText(getDeliveryConfirm.getString(getDeliveryConfirm.getColumnIndex("created_at")));
                }
                getDeliveryConfirm.close();
            }


            if(delivery_status.equals("Failed")){
                tv_deliveryType.setText(R.string.faild);
            } else if(delivery_status.equals("Pending")){
                tv_deliveryType.setText(R.string.pending);
            }else if(delivery_status.equals("Success")){
                tv_deliveryType.setText(R.string.success);
            }



//            tv_shipping_num.setText(getOrders.getString(getOrders.getColumnIndex("Shipment_Number")));
            shipmentNumber = getOrders.getString(getOrders.getColumnIndex("Shipment_Number"));
            Log.v("getDeliveryConfirm1","- "+  getOrders.getString(getOrders.getColumnIndex("to_be_delivered_by")));
            tv_reference_number.setText(getOrders.getString(getOrders.getColumnIndex("referenceNumber")));
            reference_no = getOrders.getString(getOrders.getColumnIndex("referenceNumber"));
//            tv_order_number.setText(getOrders.getString(getOrders.getColumnIndex("order_number")));
            tv_orderId.setText(getOrders.getString(getOrders.getColumnIndex("order_number")));
            tv_customer_name.setText(getOrders.getString(getOrders.getColumnIndex("customer_name")));
            tv_deliveryDate.setText(getOrders.getString(getOrders.getColumnIndex("to_be_delivered_by")));
            tv_shippingAddress.setText(getOrders.getString(getOrders.getColumnIndex("shipping_address")));
            tv_shippingCity.setText(getOrders.getString(getOrders.getColumnIndex("shipping_city")));
            tv_shipping_pincode.setText(getOrders.getString(getOrders.getColumnIndex("shipping_pincode")));
            tv_ship_invoice_num.setText(getOrders.getString(getOrders.getColumnIndex("Shipment_Number")));
            tv_ship_invoice_amt.setText(getOrders.getString(getOrders.getColumnIndex("invoice_amount")));
            invoice_amt = getOrders.getString(getOrders.getColumnIndex("invoice_amount"));
            tv_ship_pay_mode.setText(getOrders.getString(getOrders.getColumnIndex("payment_mode")));
            if(!getOrders.getString(getOrders.getColumnIndex("client_branch_name")).equals("")){
            tv_ship_client_branch_name.setText(getOrders.getString(getOrders.getColumnIndex("client_branch_name")));
            }else{
                ll_bran_name.setVisibility(GONE);
                view_gap_brname.setVisibility(GONE);
            }
//            tv_ship_branch_add.setText(getOrders.getString(getOrders.getColumnIndex("branch_address")));
            if(!getOrders.getString(getOrders.getColumnIndex("branch_address")).equals("")){
            tv_ship_branch_add.setText(getOrders.getString(getOrders.getColumnIndex("branch_address")));
            }else{
                ll_branch_address.setVisibility(GONE);
                view_gap_braddr.setVisibility(GONE);
            }
            if(!getOrders.getString(getOrders.getColumnIndex("branch_pincode")).equals("")) {
                tv_ship_branch_pincode.setText(getOrders.getString(getOrders.getColumnIndex("branch_pincode")));
            }else{
                ll_bran_pincode.setVisibility(GONE);
                view_gap_brpin.setVisibility(GONE);
            }

            if(delivery_status.equals("Success")){
                tv_deliveryDate.setText(getOrders.getString(getOrders.getColumnIndex("to_be_delivered_by")));
                Cursor getDeliveryConfirm = database.rawQuery("Select * from DeliveryConfirmation where shipmentnumber = '" + shipmentNumber + "' ", null);
                if (getDeliveryConfirm.getCount() > 0){
                    getDeliveryConfirm.moveToFirst();
                    String delivered_date = getDeliveryConfirm.getString(getDeliveryConfirm.getColumnIndex("created_at"));

                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date date = null;
                    try {
                        date = inputFormat.parse(delivered_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(date);

                    tv_attempt_date.setText(outputDateStr);
//                    tv_attempt_date.setText(getDeliveryConfirm.getString(getDeliveryConfirm.getColumnIndex("created_at")));
                }
                getDeliveryConfirm.close();
            } else if(delivery_status.equals("Failed")){
                tv_deliveryDate.setText(getOrders.getString(getOrders.getColumnIndex("to_be_delivered_by")));
                Cursor getUnDeliveryConfirm = database.rawQuery("Select * from UndeliveredConfirmation where shipmentnumber = '" + shipmentNumber + "' ", null);
                if (getUnDeliveryConfirm.getCount() > 0){
                    getUnDeliveryConfirm.moveToFirst();
                    String attempt_date = getUnDeliveryConfirm.getString(getUnDeliveryConfirm.getColumnIndex("created_at"));

                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date date = null;
                    try {
                        date = inputFormat.parse(attempt_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(date);

                    tv_attempt_date.setText(outputDateStr);
                    String isRedirect = getUnDeliveryConfirm.getString(getUnDeliveryConfirm.getColumnIndex("redirect"));
                    if(isRedirect.equals("1")){
                    tv_shippingAddress.setText(getUnDeliveryConfirm.getString(getUnDeliveryConfirm.getColumnIndex("shipment_address")));
                    }
                    }
                getUnDeliveryConfirm.close();
            }/*else if(delivery_status.equals("pickup")){
                Cursor getDeliveryConfirm = database.rawQuery("Select * from PickupConfirmation where shipmentno = '" + shipmentNumber + "' ", null);
                if (getDeliveryConfirm.getCount() > 0){
                    getDeliveryConfirm.moveToFirst();
                    String delivered_date = getDeliveryConfirm.getString(getDeliveryConfirm.getColumnIndex("createdate"));

                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date date = null;
                    try {
                        date = inputFormat.parse(delivered_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(date);

                    tv_attempt_date.setText(outputDateStr);
//                    tv_attempt_date.setText(getDeliveryConfirm.getString(getDeliveryConfirm.getColumnIndex("created_at")));
                }
                getDeliveryConfirm.close();
            }*/
//            Log.v("ship_attempt_count",getOrders.getString(getOrders.getColumnIndex("attempt_count")));
            tv_ship_attempt_count.setText(getOrders.getString(getOrders.getColumnIndex("attempt_count")));

            shipmentNumber = getOrders.getString(getOrders.getColumnIndex("Shipment_Number"));

            if(payment_mode.equals("COD")){
                getTotalOrderAmount();
            }

            if(order_type.equals("2") ){
                tv_deliveryDate.setText("-");
            }
        }

        if(user_language.equals("tamil")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("tamil"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                } if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                } if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                } if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                } if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }

            }catch(JSONException e){
                e.getStackTrace();
            }
        }  else if(user_language.equals("hindi")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("hindi"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                }if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                }if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                }if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                }if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }

            }catch(JSONException e){
                e.getStackTrace();
            }
        }else if(user_language.equals("bengali")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("bengali"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                }if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                }if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                }if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                }if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }
            }catch(JSONException e){
                e.getStackTrace();
            }
        }
        else if(user_language.equals("marathi")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("marathi"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                }if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                }if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                }if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                }if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }
            }catch(JSONException e){
                e.getStackTrace();
            }
        }
        else if(user_language.equals("punjabi")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("punjabi"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                }if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                }if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                }if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                }if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }
            }catch(JSONException e){
                e.getStackTrace();
            }
        }else if(user_language.equals("odia")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("orissa"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                }if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                }if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                }if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                }if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }
            }catch(JSONException e){
                e.getStackTrace();
            }
        }else if(user_language.equals("telugu")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("telugu"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                }if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                }if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                }if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                }if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }
            }catch(JSONException e){
                e.getStackTrace();
            }
        }else if(user_language.equals("kannada")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("kannada"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                }if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                }if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                }if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                }if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }
            }catch(JSONException e){
                e.getStackTrace();
            }
        }else if(user_language.equals("assamese")){
            String language_json = getOrders.getString(getOrders.getColumnIndex("assam"));

            try {
                JSONObject jObject = new JSONObject(language_json);
                if (jObject.has("customer_name")) {
                    String customer_name = jObject.getString("customer_name");
                    tv_customer_name.setText(customer_name);
                }if (jObject.has("branch_name")) {
                    String branch_name = jObject.getString("branch_name");
                    tv_ship_client_branch_name.setText(branch_name);
                }if (jObject.has("branch_address")) {
                    String branch_addr = jObject.getString("branch_address");
                    tv_ship_branch_add.setText(branch_addr);
                }if (jObject.has("city")) {
                    String city = jObject.getString("city");
                    tv_shippingCity.setText(city);
                }if (jObject.has("delivery_address")) {
                    String ship_addr = jObject.getString("delivery_address");
                    tv_shippingAddress.setText(ship_addr);
                }
            }catch(JSONException e){
                e.getStackTrace();
            }
        }

        getOrders.close();
    }



    public void productshowdetails(){

        getqtymax();

        Cursor getshowdetails = database.rawQuery("SELECT product_code,product_name,quantity,IFNULL(delivery_qty,0) as delivery_qty, amount,IFNULL(tamil,'') as tamil ,IFNULL(telugu,'') as telugu" +
                ",IFNULL(punjabi,'') as punjabi,IFNULL(hindi,'') as hindi,IFNULL(bengali,'') as bengali,IFNULL(kannada,'') as kannada,IFNULL(assam,'') as assam" +
                ",IFNULL(orissa,'') as orissa ,IFNULL(marathi,'') as marathi, pickup_type FROM ProductDetails where shipmentnumber = '" + shipmentNumber + "' ", null);

        ProductShowModel sqlitebeans_child ;

            getshowdetails.moveToFirst();

            if(getshowdetails.getCount() > 0) {
                while (!getshowdetails.isAfterLast()) {
                    sqlitebeans_child = new ProductShowModel();
//                    Log.v("getshowproduct_count", String.valueOf(getshowdetails.getCount()));
                    sqlitebeans_child.setpcode(getshowdetails.getString(getshowdetails.getColumnIndex("product_code")));
                    sqlitebeans_child.setpname(getshowdetails.getString(getshowdetails.getColumnIndex("product_name")));
                    sqlitebeans_child.setpqty(getshowdetails.getString(getshowdetails.getColumnIndex("quantity")));
//                    Log.v("getshowquantity", getshowdetails.getString(getshowdetails.getColumnIndex("quantity")));
                    sqlitebeans_child.setpamt(getshowdetails.getString(getshowdetails.getColumnIndex("amount")));
                    sqlitebeans_child.setPickupStatus(getshowdetails.getInt(getshowdetails.getColumnIndex("pickup_type")));
                    sqlitebeans_child.setOrder_type(order_type);

                    if(delivery_status.equalsIgnoreCase("Success")){
                        sqlitebeans_child.setDeliveryQty(getshowdetails.getString(getshowdetails.getColumnIndex("delivery_qty")));
                    }

                    if(undelivery_status.equalsIgnoreCase("delivered")){
                        sqlitebeans_child.setDeliveryQty(getshowdetails.getString(getshowdetails.getColumnIndex("quantity")));
                    }

                    if(user_language.equals("tamil")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("tamil"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    } else if(user_language.equals("telugu")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("telugu"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    } else if(user_language.equals("punjabi")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("punjabi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    }else if(user_language.equals("hindi")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("hindi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    }else if(user_language.equals("bengali")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("bengali"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    }else if(user_language.equals("kannada")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("kannada"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    }else if(user_language.equals("assamese")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("assam"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    }else if(user_language.equals("odia")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("orissa"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    }else if(user_language.equals("marathi")){
                        String language_json = getshowdetails.getString(getshowdetails.getColumnIndex("marathi"));

                        try {
                            JSONObject jObject = new JSONObject(language_json);
                            String product_name = jObject.getString("product_name");

                            sqlitebeans_child.setpname(product_name);
                        }catch(JSONException e){
                            e.getStackTrace();
                        }
                    }

                    getshowdetails.moveToNext();
                    msqlitebeans.add(sqlitebeans_child);
                    ProdshowAdapter = new ProductShowAdapter(mContext, msqlitebeans, database, activity,delivery_status );
                    product_show_adapter.setAdapter(ProdshowAdapter);

                    ProdshowAdapter.notifyDataSetChanged();
                    product_show_adapter.invalidate();
                }
            }else{
                product_show_adapter.setAdapter(null);
            }



        getshowdetails.close();

    }

    public void getqtymax(){

        Cursor getqty = database.rawQuery("SELECT SUM(quantity)AS quantity FROM ProductDetails where shipmentnumber = '" + shipmentNumber + "' AND pickup_type = 0 ", null);
        if (getqty.getCount() > 0) {
            getqty.moveToFirst();
           //  getmaxqty = getqty.getString(getqty.getColumnIndex("quantity"));

             maxget = getqty.getInt(getqty.getColumnIndex("quantity"));
//            Log.e("Getqty", String.valueOf(maxget));
            //   username.setText(UName);

        }
        getqty.close();

    }

    public void getTotalOrderAmount(){

//        Cursor getqty = database.rawQuery("SELECT SUM(total_amount)AS total_amount FROM ProductDetails where shipmentnumber = '" + shipmentNumber + "' ", null);
        Cursor getqty = database.rawQuery("SELECT SUM(amount_collected)AS total_amount FROM ProductDetails where shipmentnumber = '" + shipmentNumber + "' AND pickup_type = 0 ", null);
        if (getqty.getCount() > 0) {
            getqty.moveToFirst();

            int tot_amount_collected = getqty.getInt(getqty.getColumnIndex("total_amount"));
//            Log.e("get_total_order", String.valueOf(tot_amount_collected));
//            tv_amount_collected.setText(String.valueOf(tot_amount_collected));
            if(undelivery_status.equals("delivered")){
                tv_amount_collected.setText(invoice_amt);
            }else{
                tv_amount_collected.setText(String.valueOf(tot_amount_collected));
            }
        }
        getqty.close();
    }



    public void getsampprod(){


        Cursor getshowdetails = database.rawQuery("SELECT * FROM UndeliveredConfirmation ", null);

        getshowdetails.moveToFirst();

        if(getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {
                Log.v("getsampprod", getshowdetails.getString(getshowdetails.getColumnIndex("shipmentnumber")));
//                Log.v("prod_name", getshowdetails.getString(getshowdetails.getColumnIndex("product_name")));
//                Log.v("prod_qty", getshowdetails.getString(getshowdetails.getColumnIndex("quantity")));
//                Log.v("prod_amount", getshowdetails.getString(getshowdetails.getColumnIndex("amount")));
//                Log.v("prod_ship", getshowdetails.getString(getshowdetails.getColumnIndex("shipmentnumber")));
                getshowdetails.moveToNext();
            }
        }
        getshowdetails.close();

    }

    @Override
    public void onClick(View view) {


        if (view == ll_delivered) {

            Cursor isOrderOffline = database.rawQuery("Select * FROM orderheader O INNER JOIN  UndeliveredConfirmation U ON U.shipmentnumber = O.Shipment_Number where  O.sync_status = 'C' ", null);
            isOrderOffline.moveToFirst();
            if(isOrderOffline.getCount() > 0){
                alertDialogMsgOffline(OrderDetails.this, "Warning", "Shipment Offline!!", "Ok");
            }else{
                Cursor getUndeliverReason = database.rawQuery("Select * FROM orderheader O INNER JOIN  UndeliveredConfirmation U ON U.shipmentnumber = O.Shipment_Number INNER JOIN ReasonMaster R ON R.rid = U.reason_id where O.delivery_status = 'undelivered' AND O.sync_status = 'U' AND O.Shipment_Number = '"+shipmentNumber+"' AND R.reason_type = 1 ", null);

                if (getUndeliverReason.getCount() > 0) {

                    alertDialogMsgOffline(OrderDetails.this, "Warning", "You cannot attempt to Deliver this Order.", "Ok");
                }else{

//                    Cursor getMaxThreecount = database.rawQuery("Select max_attempt,attempt_count FROM orderheader O where O.Shipment_Number = '"+shipmentNumber+"' ", null);
//                    getMaxThreecount.moveToFirst();
//                    if(getMaxThreecount.getCount() > 0){

//                        int maxThreeCount = getMaxThreecount.getInt(getMaxThreecount.getColumnIndex("max_attempt"));
//                        int attemptCount = getMaxThreecount.getInt(getMaxThreecount.getColumnIndex("attempt_count"));
//                        Log.v("getMaxThreecount"," - "+ maxThreeCount + "- "+ attemptCount);
//                        if(attemptCount >= maxThreeCount ){
//                            alertDialogMsgOffline(OrderDetails.this, "Warning", "You have already made 3 attempts.", "Ok");
//                        }else{
                            Cursor uname = database.rawQuery("Select * from orderheader where delivery_status = 'undelivered' AND sync_status = 'U' AND Shipment_Number = '" + shipmentNumber + "' ", null);

                            if (uname.getCount() > 0) {

                                AlertDialogMsgWarning(OrderDetails.this, "Warning", "Are you sure you want make a delivery?", "Ok","No");
                            }else{

                                Intent delivery = new Intent(OrderDetails.this, DeliveryActivity.class);
                                delivery.putExtra(Constants.SHIPMENT_NUMBER, shipmentNumber);
                                delivery.putExtra(Constants.ORDER_ID, order_num);
                                delivery.putExtra("order_type", order_type);
                                startActivity(delivery);

                            }
//                        }

//                    }


//                Cursor uname = database.rawQuery("Select * from orderheader where delivery_status = 'undelivered' AND sync_status = 'U' AND Shipment_Number = '" + shipmentNumber + "' ", null);
//
//                if (uname.getCount() > 0) {
//
//                    AlertDialogMsgWarning(OrderDetails.this, "Warning", "Are you sure you want make a delivery?", "Ok","No");
//                }else{
//
////            if(updatDate()){  // removed 15-11-2018 4.23PM
//                    Intent delivery = new Intent(OrderDetails.this, DeliveryActivity.class);
//                    delivery.putExtra(Constants.SHIPMENT_NUMBER, shipmentNumber);
//                    delivery.putExtra(Constants.ORDER_ID, order_num);
//                    delivery.putExtra("order_type", order_type);
//                    startActivity(delivery);
////            }
//                }

//                Intent delivery = new Intent(OrderDetails.this, DeliveryActivity.class);
//                delivery.putExtra(Constants.SHIPMENT_NUMBER, shipmentNumber);
//                delivery.putExtra(Constants.ORDER_ID, order_num);
//                startActivity(delivery);





                }
            }

        }else if(view == ll_par_delivered){

            Cursor isOrderOffline = database.rawQuery("Select * FROM orderheader O INNER JOIN  UndeliveredConfirmation U ON U.shipmentnumber = O.Shipment_Number where  O.sync_status = 'C' ", null);
            isOrderOffline.moveToFirst();
            if(isOrderOffline.getCount() > 0){
                alertDialogMsgOffline(OrderDetails.this, "Warning", "Shipment Offline!!", "Ok");
            }else{
                //            if(updatDate()) { // removed 15-11-2018 4.23PM
                if(order_type.equals("3")){
                    alertDialogMsgOffline(OrderDetails.this, "Warning", "You cannot attempt to Partial Deliver.", "Ok");
                }else
                {
                    if (maxget <= 1) {


                        showalert(getString(R.string.maxqty));
                        return;
                    } else {

                 /*   Intent partdelivery = new Intent(OrderDetails.this, PartialDelivery.class);
                    partdelivery.putExtra("ship_num", shipmentNumber);
                    partdelivery.putExtra(Constants.ORDER_ID, order_num);
                    partdelivery.putExtra("order_type", order_type);
                    startActivity(partdelivery);*/
                        Cursor uname = database.rawQuery("Select * FROM orderheader O INNER JOIN  UndeliveredConfirmation U ON U.shipmentnumber = O.Shipment_Number INNER JOIN ReasonMaster R ON R.rid = U.reason_id where O.delivery_status = 'undelivered' AND O.sync_status = 'U' AND O.Shipment_Number = '"+shipmentNumber+"' AND R.reason_type = 1 ", null);

                        if (uname.getCount() > 0) {

                            alertDialogMsgOffline(OrderDetails.this, "Warning", "You cannot attempt to Partial Deliver.", "Ok");
                        }else{


//                            Cursor getMaxThreecount = database.rawQuery("Select max_attempt,attempt_count FROM orderheader O where O.Shipment_Number = '"+shipmentNumber+"' ", null);
//                            getMaxThreecount.moveToFirst();
//                            if(getMaxThreecount.getCount() > 0){

//                                int maxThreeCount = getMaxThreecount.getInt(getMaxThreecount.getColumnIndex("max_attempt"));
//                                int attemptCount = getMaxThreecount.getInt(getMaxThreecount.getColumnIndex("attempt_count"));
//                                if(attemptCount >= maxThreeCount){
//                                    alertDialogMsgOffline(OrderDetails.this, "Warning", "You have already made 3 attempts.", "Ok");
//                                }else{
                                    Intent partdelivery = new Intent(OrderDetails.this, PartialDelivery.class);
                                    partdelivery.putExtra("ship_num", shipmentNumber);
                                    partdelivery.putExtra(Constants.ORDER_ID, order_num);
                                    partdelivery.putExtra("order_type", order_type);
                                    startActivity(partdelivery);
//                                }

//                            }

//                        Intent partdelivery = new Intent(OrderDetails.this, PartialDelivery.class);
//                        partdelivery.putExtra("ship_num", shipmentNumber);
//                        partdelivery.putExtra(Constants.ORDER_ID, order_num);
//                        partdelivery.putExtra("order_type", order_type);
//                        startActivity(partdelivery);
                        }

//                    Intent partdelivery = new Intent(OrderDetails.this, PartialDelivery.class);
//                    partdelivery.putExtra("ship_num", shipmentNumber);
//                    partdelivery.putExtra(Constants.ORDER_ID, order_num);
//                    startActivity(partdelivery);


                    }

                }
            }

        }else if (view == ll_undelivered) {

            Cursor isOrderOffline = database.rawQuery("Select * FROM orderheader O INNER JOIN  UndeliveredConfirmation U ON U.shipmentnumber = O.Shipment_Number where  O.sync_status = 'C' ", null);
            isOrderOffline.moveToFirst();
            if(isOrderOffline.getCount() > 0){
                alertDialogMsgOffline(OrderDetails.this, "Warning", "Shipment Offline!!", "Ok");
            }else{
                Cursor uname = database.rawQuery("Select * FROM orderheader O INNER JOIN  UndeliveredConfirmation U ON U.shipmentnumber = O.Shipment_Number INNER JOIN ReasonMaster R ON R.rid = U.reason_id where O.delivery_status = 'undelivered' AND O.sync_status = 'U' AND O.Shipment_Number = '"+shipmentNumber+"' AND (R.reason_type = 1 OR R.reason = 'Consignee refused to accept the product') ", null);

                if (uname.getCount() > 0) {

                    alertDialogMsgOffline(OrderDetails.this, "Warning", "You cannot attempt to Undeliver again.", "Ok");
                }else{

                    if(!aadhaarEnabled.equals("0")){
//                if(aadhaarEnabled.equals("0") || aadhaarEnabled.equals("1")){
//                        Cursor getMaxcount = database.rawQuery("Select attempt_count FROM orderheader O where O.Shipment_Number = '"+shipmentNumber+"' ", null);
//                        getMaxcount.moveToFirst();
//                        if(getMaxcount.getCount() > 0){
//                            int maxCount = getMaxcount.getInt(getMaxcount.getColumnIndex("attempt_count"));
//                            if(maxCount == 3 || maxCount > 3){
//                                alertDialogMsgOffline(OrderDetails.this, "Warning", "You have already made 3 attempts.", "Ok");
//                            }else{
                                Intent delivery = new Intent(OrderDetails.this, UndeliveryActivity.class);
                                delivery.putExtra(Constants.SHIPMENT_NUMBER, shipmentNumber);
                                delivery.putExtra("undelivered", "undelivered");
                                delivery.putExtra("", shipmentNumber);
                                startActivity(delivery);
//                            }
//                        }
//                        getMaxcount.close();
                    }else{

//                        Cursor getMaxThreecount = database.rawQuery("Select max_attempt,attempt_count FROM orderheader O where O.Shipment_Number = '"+shipmentNumber+"' ", null);
//                        getMaxThreecount.moveToFirst();
//                        if(getMaxThreecount.getCount() > 0){

//                            int maxThreeCount = getMaxThreecount.getInt(getMaxThreecount.getColumnIndex("max_attempt"));
//                            int attemptCount = getMaxThreecount.getInt(getMaxThreecount.getColumnIndex("attempt_count"));
//                            if(attemptCount >= maxThreeCount ){
//                                alertDialogMsgOffline(OrderDetails.this, "Warning", "You have already made 3 attempts.", "Ok");
//                            }else{
                                Intent delivery = new Intent(OrderDetails.this, UndeliveryActivity.class);
                                delivery.putExtra(Constants.SHIPMENT_NUMBER, shipmentNumber);
                                delivery.putExtra("undelivered", "undelivered");
                                delivery.putExtra("", shipmentNumber);
                                startActivity(delivery);
//                            }

//                        }


//                    Intent delivery = new Intent(OrderDetails.this, UndeliveryActivity.class);
//                    delivery.putExtra(Constants.SHIPMENT_NUMBER, shipmentNumber);
//                    delivery.putExtra("undelivered", "undelivered");
//                    delivery.putExtra("", shipmentNumber);
//                    startActivity(delivery);
                    }

                }

//            if(updatDate()) {  // removed 15-11-2018 4.23PM
//                Intent delivery = new Intent(OrderDetails.this, UndeliveryActivity.class);
//                delivery.putExtra(Constants.SHIPMENT_NUMBER, shipmentNumber);
//                delivery.putExtra("undelivered", "undelivered");
//                delivery.putExtra("", shipmentNumber);
//                startActivity(delivery);
//            }
            }



        } else if (view == ll_call_user) {

            if (!aadhaarEnabled.equals("0")) {
             callBfilUser();
            }else{
            viewCall = view;

            if (Build.VERSION.SDK_INT >= 23) {
                String[] permissions = {android.Manifest.permission.CALL_PHONE};
                if (!hasPermissions(OrderDetails.this, permissions)) {
                    ActivityCompat.requestPermissions((Activity) OrderDetails.this, permissions, REQUEST);
                } else {
                    openBottomSheet(view);
                }
            } else {
                openBottomSheet(view);
            }
        }
        } else if (view == ll_location) {
            mapAddressAlert();
        }else if(view == ll_hide_details ){

            if(hideshow==true) {
                ll_show_details.setVisibility(View.VISIBLE);
                tv_ship_itemcode.setText(R.string.hide_details);
                tv_ship_itemcode.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.uparrow, 0);
                hideshow = false;
            }else if(hideshow == false){
                ll_show_details.setVisibility(GONE);
                tv_ship_itemcode.setText(R.string.view_details);
                tv_ship_itemcode.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.downarrow, 0);
                hideshow = true;
            }

        }else if(view == ll_pickup){


//            Cursor getMaxThreecount = database.rawQuery("Select max_attempt,attempt_count FROM orderheader O where O.Shipment_Number = '"+shipmentNumber+"' ", null);
//            getMaxThreecount.moveToFirst();
//            if(getMaxThreecount.getCount() > 0){

//                int maxThreeCount = getMaxThreecount.getInt(getMaxThreecount.getColumnIndex("max_attempt"));
//                int attemptCount = getMaxThreecount.getInt(getMaxThreecount.getColumnIndex("attempt_count"));
//                if(attemptCount >= maxThreeCount ){
//                    alertDialogMsgOffline(OrderDetails.this, "Warning", "You have already made 3 attempts.", "Ok");
//                }else{
                    Intent pickdelivery = new Intent(OrderDetails.this, PickupDelivery.class);
                    pickdelivery.putExtra("order_num", order_num );
                    pickdelivery.putExtra("ship_num", shipmentNumber);
                    pickdelivery.putExtra("order_type", order_type);
                    startActivity(pickdelivery);
//                }

//            }

//            Intent pickdelivery = new Intent(OrderDetails.this, PickupDelivery.class);
//            pickdelivery.putExtra("order_num", order_num );
//            pickdelivery.putExtra("ship_num", shipmentNumber);
//            pickdelivery.putExtra("order_type", order_type);
//            startActivity(pickdelivery);

        }
    }



    /**
     * Check the device whether user has changed the date or not if user changed the date throw the alert to change the current
     * date
     *
     */
    private boolean updatDate() {
//        if (!TrueTimeRx.isInitialized()) {
        if (!TrueTimeRx.isInitialized()) {
            Log.d("Truetime not intialized", "Sorry TrueTime not yet initialized.");
            return true;
        }else {
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


                return true;

            } else {
                Utils.AlertDialogCancel(this, getResources().getString(R.string.exactdate), getResources().getString(R.string.get_date_warn), getResources()
                        .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel));
            }
        }
        return false;
        }

    private String _formatDate(Date date, String pattern, TimeZone timeZone) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(timeZone);
        return format.format(date);
    }


    /**
     * Get the permission for marshmallow
     * @param context Get the context of an activity
     * @param permissions GEt the permission of phone call
     * @return the permission
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openBottomSheet(viewCall);
                } else {
                    Toast.makeText(OrderDetails.this, "The app was not allowed to call.", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case REQUESTBFIL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callBfilUser();
                } else {
                    Toast.makeText(OrderDetails.this, "The app was not allowed to call.", Toast.LENGTH_LONG).show();
                }
            }

        }

    }


    /**
     * Get the address from the sqlite
     */
    private void getMapAddress(){
        try {
            Cursor getMapValue = database.rawQuery("select  IFNULL(billing_address,0) as billing_address," +
                    "IFNULL(billing_pincode,0) as billing_pincode," +
                    "IFNULL(shipping_address,0) as shipping_address ,IFNULL(shipping_pincode,0) as shipping_pincode" +
                            " from " + "orderheader where order_number='"
                            + order_num+ "'",null);
            System.out.println("COUNT : " + getMapValue.getCount());

            if (getMapValue.moveToFirst()) {
                do {
                    String billingAddress = getMapValue.getString(getMapValue.getColumnIndex("billing_address"));
                    String billing_pincode = getMapValue.getString(getMapValue.getColumnIndex("billing_pincode"));
                    String shippingAddress = getMapValue.getString(getMapValue.getColumnIndex("shipping_address"));
                    String shippingPincode = getMapValue.getString(getMapValue.getColumnIndex("shipping_pincode"));
//                    Log.v("getSchemeValue",NAME);
                   // my_array.add(billingAddress + "\n" + billing_pincode);
                    my_array.add( shippingAddress + "\n" + shippingPincode );

                } while (getMapValue.moveToNext());
            }
            getMapValue.close();
        } catch (Exception e) {
            Logger.showShortMessage(OrderDetails.this, "Error encountered.");
        }
    }

    /**
     * Display the to address and brach address for disaply the route map
     */
    private void mapAddressAlert(){
        ListAdapter adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.map_alert, my_array) {

            ViewHolder holder;
            Drawable icon;

            class ViewHolder {
                ImageView icon;
                TextView title;
                TextView tv_Alert_Title;
            }

            public View getView(int position, View convertView,
                                ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {
                    convertView = inflater.inflate(
                            R.layout.map_alert, null);

                    holder = new ViewHolder();
                    holder.icon = (ImageView) convertView
                            .findViewById(R.id.icon);
                    holder.title = (TextView) convertView
                            .findViewById(R.id.title);
//                    holder.tv_Alert_Title = (TextView) convertView
//                            .findViewById(R.id.tv_Alert_Title);
                    convertView.setTag(holder);
                } else {
                    // view already defined, retrieve view holder
                    holder = (ViewHolder) convertView.getTag();
                }

                Drawable drawable = getResources().getDrawable(R.drawable.map_icon); //this is an image from the drawables
                // folder


                holder.title.setText(my_array.get(position));
                holder.icon.setImageDrawable(drawable);
//                holder.tv_Alert_Title.setText("Shipping Address");

                return convertView;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.select_location));
        builder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int item) {
                        Logger.showShortMessage(OrderDetails.this, "You selected: " + my_array.get(item));
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData( Uri.parse("google.navigation:q=" + my_array.get(item)));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void openBottomSheet(View v) {

        Context context = v.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.call_alertbox, null);


        rb_customer = (RadioButton) view.findViewById(R.id.rb_customer);
        rb_alternate = (RadioButton) view.findViewById(R.id.rb_alternate);
        rb_shipping = (RadioButton) view.findViewById(R.id.rb_shipping);
        rb_branch = (RadioButton) view.findViewById(R.id.rb_branch);
        rb_groupleader = (RadioButton) view.findViewById(R.id.rb_groupleader);
        rg = (RadioGroup) view.findViewById(R.id.rg);
        procd = (Button) view.findViewById(R.id.proceed);

        Cursor getPhone = database.rawQuery("Select IFNULL(customer_contact_number, '') as customer_contact, IFNULL(alternate_contact_number, '') as alternate_contact, IFNULL(shipping_telephone, '') as shipping_contact, " +
                "IFNULL(branch_contact_number, '') as branch_contact, IFNULL(group_leader_contact_number, '') as groupleader_contact from orderheader where order_number = '" + order_num + "' ", null);

        if (getPhone.getCount() > 0) {
            getPhone.moveToFirst();

            getCustomerNo = getPhone.getString(getPhone.getColumnIndex("customer_contact"));
            getAlternateNo = getPhone.getString(getPhone.getColumnIndex("alternate_contact"));
            getShippingNo = getPhone.getString(getPhone.getColumnIndex("shipping_contact"));
            getBranchNo = getPhone.getString(getPhone.getColumnIndex("branch_contact"));
            getGroupLeaderNo = getPhone.getString(getPhone.getColumnIndex("groupleader_contact"));

            if(getCustomerNo.equalsIgnoreCase(getAlternateNo)){
                rb_customer.setVisibility(GONE);
                rb_alternate.setVisibility(View.VISIBLE);
            }
            if(getCustomerNo.equalsIgnoreCase(getShippingNo) ){
                rb_customer.setVisibility(GONE);
                rb_shipping.setVisibility(View.VISIBLE);

            }
            if(getCustomerNo.equalsIgnoreCase(getBranchNo)){
                rb_customer.setVisibility(GONE);
                rb_branch.setVisibility(View.VISIBLE);

            }
            if(getCustomerNo.equalsIgnoreCase(getGroupLeaderNo)){
                rb_customer.setVisibility(GONE);
                rb_groupleader.setVisibility(View.VISIBLE);

            }
            if(getAlternateNo.equalsIgnoreCase(getShippingNo)){
                rb_shipping.setVisibility(View.VISIBLE);
                rb_alternate.setVisibility(GONE);

            }

            if(getAlternateNo.equalsIgnoreCase(getBranchNo)){
                rb_alternate.setVisibility(GONE);
                rb_branch.setVisibility(View.VISIBLE);

            }

            if(getAlternateNo.equalsIgnoreCase(getGroupLeaderNo)){
                rb_alternate.setVisibility(GONE);
                rb_groupleader.setVisibility(View.VISIBLE);

            }

            if(getShippingNo.equalsIgnoreCase(getBranchNo)){
                rb_shipping.setVisibility(GONE);
                rb_branch.setVisibility(View.VISIBLE);

            }

            if(getShippingNo.equalsIgnoreCase(getGroupLeaderNo)){
                rb_shipping.setVisibility(GONE);
                rb_groupleader.setVisibility(View.VISIBLE);


            }

            if(getBranchNo.equalsIgnoreCase(getGroupLeaderNo)){
                rb_branch.setVisibility(GONE);
                rb_groupleader.setVisibility(View.VISIBLE);

            }

            if (getCustomerNo.equals("")) {
                rb_customer.setVisibility(GONE);

            }
            if (getAlternateNo.equals("")) {
                rb_alternate.setVisibility(GONE);
            }
            if (getShippingNo.equals("")) {
                rb_shipping.setVisibility(GONE);
            }
            if (getBranchNo.equals("")) {
                rb_branch.setVisibility(GONE);
            }
            if (getGroupLeaderNo.equals("")) {
                rb_groupleader.setVisibility(GONE);
            }

//            Log.v("getPhoneNum", getCustomerNo + "-" + getAlternateNo + "" +
//                    "" + getShippingNo + "" + getBranchNo + "" + getGroupLeaderNo);




        }
        getPhone.close();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    checkId = checkedId;
                }
                mBottomSheetDialog.dismiss();
                if (checkId == R.id.rb_customer) {
                    callUser(getCustomerNo);
                } else if (checkId == R.id.rb_alternate) {
                    callUser(getAlternateNo);
                } else if (checkId == R.id.rb_shipping) {
                    callUser(getShippingNo);
                } else if (checkId == R.id.rb_branch) {
                    callUser(getBranchNo);
                } else if (checkId == R.id.rb_groupleader) {
                    callUser(getGroupLeaderNo);
                }

            }
        });


        procd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                if (checkId == R.id.rb_customer) {
                    callUser(getCustomerNo);
                } else if (checkId == R.id.rb_alternate) {
                    callUser(getAlternateNo);
                } else if (checkId == R.id.rb_shipping) {
                    callUser(getShippingNo);
                } else if (checkId == R.id.rb_branch) {
                    callUser(getBranchNo);
                } else if (checkId == R.id.rb_groupleader) {
                    callUser(getGroupLeaderNo);
                }
            }
        });


        mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
    }






    public void showDialog(Context context, String title, String[] btnText,
                           DialogInterface.OnClickListener listener) {
        String[] items = {"One", "Two"};


        Cursor getPhone = database.rawQuery("Select IFNULL(customer_contact_number, '') as customer_contact, IFNULL(alternate_contact_number, '') as alternate_contact, IFNULL(shipping_telephone, '') as shipping_contact, " +
                "IFNULL(branch_contact_number, '') as branch_contact, IFNULL(group_leader_contact_number, '') as groupleader_contact from orderheader where order_number = '" + order_num + "' ", null);

        if (getPhone.getCount() > 0) {
            getPhone.moveToFirst();
        }
        getPhone.close();

        if (listener == null)
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface,
                                    int paramInt) {
                    paramDialogInterface.dismiss();
                }
            };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                    }
                });

        builder.setPositiveButton(btnText[0], listener);
        if (btnText.length != 1) {
            builder.setNegativeButton(btnText[1], listener);
        }
        builder.show();
    }


    public void callUser(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    public void callBfilUser() {

        Cursor getBfilPhone = database.rawQuery("Select IFNULL(customer_contact_number, '') as customer_contact, IFNULL(alternate_contact_number, '') as alternate_contact, IFNULL(shipping_telephone, '') as shipping_contact, " +
                "IFNULL(branch_contact_number, '') as branch_contact, IFNULL(group_leader_contact_number, '') as groupleader_contact, IFNULL(virtual_id,0 ) as virtual_id from orderheader where order_number = '" + order_num + "' ", null);
        getBfilPhone.moveToFirst();
        if(getBfilPhone.getCount() > 0){

            getBfilCustomerNo = getBfilPhone.getString(getBfilPhone.getColumnIndex("customer_contact"));
            getBfilAlternateNo = getBfilPhone.getString(getBfilPhone.getColumnIndex("alternate_contact"));
            getBfilVirtualNo = getBfilPhone.getString(getBfilPhone.getColumnIndex("virtual_id"));
            String phoneNo = getBfilVirtualNo +","+getBfilCustomerNo+"#";
//            phoneNo = "04448133899,,,10052#";
//            String phonenumber = "04448133899,10050#"; // , = pauses
            String encodedPhonenumber = null;
            try {
                encodedPhonenumber = URLEncoder.encode(phoneNo, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//            Log.v("callBfilUser","- "+encodedPhonenumber);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + encodedPhonenumber));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                if (Build.VERSION.SDK_INT >= 23) {
                    String[] permissions = {android.Manifest.permission.CALL_PHONE};
                    if (!hasPermissions(OrderDetails.this, permissions)) {
                        ActivityCompat.requestPermissions((Activity) OrderDetails.this, permissions, REQUESTBFIL);
                    }
                }
                return;
            }
            startActivity(intent);
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
        navigationTracker.trackingClasses(activityName, "1", shipId);
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

    public void getValues(){
//        Cursor getValues = database.rawQuery("Select * from ProductDetails where shipmentnumber = '"+ shipmentNumber +"'", null);
        Cursor getValues = database.rawQuery("Select * from orderheader ", null);
        getValues.moveToFirst();
        if(getValues.getColumnCount() > 0){
            while(!getValues.isAfterLast()){
                Log.v("getallvalues",getValues.getString(getValues.getColumnIndex("Shipment_Number"))
                +"--"+getValues.getString(getValues.getColumnIndex("sync_status"))
                +"--"+getValues.getString(getValues.getColumnIndex("order_number")) );
                getValues.moveToNext();
            }
        }
    }


    public void productcount(){
        Cursor getshowdetails = database.rawQuery("SELECT * FROM ProductDetails  ", null);
        getshowdetails.moveToFirst();
        if(getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {
//       Log.v("Count_product_name", getshowdetails.getString(getshowdetails.getColumnIndex("product_name")));
//                Log.v("Count_product_id", getshowdetails.getString(getshowdetails.getColumnIndex("shipmentnumber")));
                getshowdetails.moveToNext();

            }
        }else{
            product_show_adapter.setAdapter(null);
        }
        getshowdetails.close();
    }


    private void showalert(String message) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(OrderDetails.this).create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage(message);
        // Alert dialog button
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();// use dismiss to cancel alert dialog
                    }
                });
        alertDialog.show();
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


    public  void AlertDialogMsgWarning(Context context, String title, String content, String okmsg, String
            canmessage) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
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

                        database.execSQL("UPDATE orderheader set delivery_status = 'delivered', sync_status = 'P' where Shipment_Number ='" + shipmentNumber + "' ");
                        Intent delivery = new Intent(OrderDetails.this, DeliveryActivity.class);
                        delivery.putExtra(Constants.SHIPMENT_NUMBER, shipmentNumber);
                        delivery.putExtra(Constants.ORDER_ID, order_num);
                        delivery.putExtra("order_type", order_type);
                        startActivity(delivery);
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
    public void alertDialogMsgOffline(Context context, String title, String content, String okmsg) {
        boolean setSampFlag = false;
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






}
