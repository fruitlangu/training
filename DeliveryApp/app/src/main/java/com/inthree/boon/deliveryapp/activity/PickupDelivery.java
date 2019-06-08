package com.inthree.boon.deliveryapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.PartcialDeliveryAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.model.ParcialShowModel;
import com.inthree.boon.deliveryapp.newcamera.CameraFragmentMainActivity;
import com.inthree.boon.deliveryapp.newcamera.PreviewActivity;
import com.inthree.boon.deliveryapp.request.UndeliveryReq;
import com.inthree.boon.deliveryapp.response.UndeliveryResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;

public class PickupDelivery extends AppCompatActivity {

    private static final String DB_NAME = "boonboxdelivery.sqlite";
    SQLiteDatabase database;

    AlertDialog alertDialog1;


    RecyclerView part_rey_deliver;

    Context mContext;
    Activity activity;

    String ship_num;
    String order_num;
    String order_type;

    Button btn_par_delivery;

    TextView tv_order_number;
    TextView tv_shipping_num;
    TextView tv_deliveryType;

    TextView tx_name;
    TextView tx_phone;
    TextView tx_address;
    ImageView im_picup;
    LinearLayout sub_lay4;

    private Cursor getProductDetails;
    ExternalDbOpenHelper dbOpenHelper;

    /**
     * Check the paymode COD or preparid
     */
    private String paymentMode;
    private int sumDelQty;
    private int sumQty;
    private ParcialShowModel productShowModel;
    private int total = 0;
    private boolean alertValue=true;
    private String sumtotal;
    ArrayList<String> my_array = new ArrayList<String>();
    String user_language;
    Locale myLocale;
    boolean partial_bool = true;
    String customerName;
    String customerPhone;
    String customerAddress;
    String customerImage;
    String orderType;

    RadioButton picsuccess , picfailed;
    RadioGroup picsufailed;



    LinearLayout cam_lay;

    /**
     * Check the online payment for further ptrasaction
     */
    Integer checkId = 0;

    /***
     * Response code argument
     */
    private static final String RESPONSE_CODE_ARG = "response_code_arg";


    /**
     * Request code path
     */
    private final static String FILE_PATH_ARG = "file_path_arg";


    String storeFilename, partFilename;


    public final static int MEMBER_PIC_CODE = 100;

    String piccheck;


    String file_path = "/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/";


    private final static String MEMBER_PIC_NAME = "pickupimage";
    ProgressIndicatorActivity dialogLoading;
    private int battery_level;
    String image_url;
    private String file_proofPhoto;
    private String formattedDate;
    int pick_attempt_count;
    String delivery_status;
    ArrayAdapter my_Adapter;
    Spinner sp_reason;
    AppCompatButton bt_back;
    AppCompatButton bt_submit;
    String str_other_reason = "";
    Spinner sp_select_reason;
    String str_lang = "";
    AppCompatCheckBox cb_pickupfailed;
    String rid;
    /*
    *  Scrollview for data layout
    * */
    ScrollView scroll;

    /*
    * Partial Products Click Textview
    * */
    AppCompatTextView tv_partial_pickup;

    int maxget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_delivery);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.btn_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        part_rey_deliver = findViewById(R.id.part_del);
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
        tv_shipping_num = (TextView) findViewById(R.id.tv_shipping_num);
        tv_order_number = (TextView) findViewById(R.id.tv_order_number);
        tv_deliveryType = (TextView) findViewById(R.id.tv_deliveryType);
        sp_select_reason = (Spinner) findViewById(R.id.sp_select_reason);
        cb_pickupfailed = (AppCompatCheckBox) findViewById(R.id.cb_pickupfailed);
        scroll = (ScrollView) findViewById(R.id.scroll);
        image_url = getResources().getString(R.string.delivery_url) + "/media/";
        tx_name = findViewById(R.id.tv_custname);
        tx_phone = findViewById(R.id.tv_cust_phno);
        tx_address = findViewById(R.id.tv_place);
        tv_partial_pickup =  findViewById(R.id.tv_partial_pickup);

        picsufailed = findViewById(R.id.radioGroup);
        picsuccess = findViewById(R.id.rdsuccess);
        picfailed = findViewById(R.id.rdfailed);
        picsuccess.setVisibility(GONE);
        cam_lay = findViewById(R.id.sub_lay4);

        im_picup = findViewById(R.id.imag_name);
        sub_lay4 = findViewById(R.id.sub_lay4);

        file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";

        picsufailed.setVisibility(GONE);
        btn_par_delivery = findViewById(R.id.btn_parcial_delivery);

        Intent partdelivery = getIntent();
        if (null != partdelivery) {
            order_num = partdelivery.getStringExtra("order_num");
            ship_num = partdelivery.getStringExtra("ship_num");
            order_type = partdelivery.getStringExtra("order_type");
            Log.v("ship_num1", "- "+ship_num);
        } else {
            order_num = "";
            ship_num = "";
        }
        piccheck = "Success";
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(order_type.equals("2")){
            tv_deliveryType.setText("Pickup");
            btn_par_delivery.setText("Submit");
        }else if(order_type.equals("3")){
            tv_deliveryType.setText("Pickup and Delivery");
        }
//        Log.v("ship_num1", ship_num);
        dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        updateSyncStatus();  // removed 17-04-2019 5.42PM
        getOrderDetails(order_num,ship_num);
        getTableValues();
        getqtymax();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, my_array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_select_reason.setAdapter(dataAdapter);


        if(maxget <= 1){
            tv_partial_pickup.setVisibility(View.GONE);
        }
        partFilename = currentDateFormat();
        storeFilename = "";

        picsufailed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkId = checkedId;

                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);

                piccheck = radioButton.getText().toString();

                if(storeFilename.equals("")){
                    alertDialogMsgError(PickupDelivery.this, "Error", "Please Take Pickup Image", "Ok");
//                    showalert("Please Take Pickup Image");
//                    picsuccess.setChecked(false);
                    picfailed.setChecked(false);
                    return;

                }else {
                    /*if(checkedId == R.id.rdsuccess){
                        piccheck= "Success";
                        if(updatePickupStatus(piccheck)) {
                            getpickupdata();
                        }
                        Log.e("Suce", piccheck);

                    }*/
                    if(checkedId == R.id.rdfailed){
                        piccheck= "Failed";
                        picfailed.setChecked(true);
                        sp_select_reason.setVisibility(View.VISIBLE);
                        if(updatePickupStatus(piccheck)) {
                            getpickupdata();
                        }
                        Log.e("Suce1", piccheck);
                    }
                }



            }
        });



        tv_partial_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_partial_pickup.setTextColor(getResources().getColor(R.color.colorAccent));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_partial_pickup.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                }, 100);

                Intent partdelivery = new Intent(PickupDelivery.this, PartialPickupProduct.class);
                partdelivery.putExtra("ship_num", ship_num);
                partdelivery.putExtra(Constants.ORDER_ID, order_num);
                partdelivery.putExtra("order_type", order_type);
                startActivity(partdelivery);
            }
        });

        btn_par_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternetAvailable()){

                    if(piccheck.equals("Failed")){
                        Log.v("btn_par_delivery","- "+ str_lang);
                        if(str_lang.equals("Select Reason") || str_lang.equals("") ){

                            TextView errorText = (TextView) sp_select_reason.getSelectedView();
                            errorText.setError("");
                            errorText.setTextColor(Color.RED);//just to highlight that this is an error
                            errorText.setText("Select a valid Reason");
                        }else{
                            uploadImage();
                        }
                    }else{
                        if(storeFilename.equals("")){
                            alertDialogMsgError(PickupDelivery.this, "Error", "Please Take Pickup Image", "Ok");
//                        showalert("Please Take Pickup Image");
                        }else{
                            uploadImage();
                        }

                    }


                }else{


                    database.execSQL("UPDATE orderheader set sync_status = 'C' where Shipment_Number ='" +
                            ship_num + "' ");
                    alertDialogMsgOffline(PickupDelivery.this, "Success", "Pickup Stored Offline", "Ok");
                }


            }
        });

        cb_pickupfailed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                               @Override
                                               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
if(isChecked){

    if(storeFilename.equals("")){
        alertDialogMsgError(PickupDelivery.this, "Error", "Please Take Pickup Image", "Ok");
//        showalert("Please Take Pickup Image");
        cb_pickupfailed.setChecked(false);
        return;

    }else{
        scroll.fullScroll(View.FOCUS_DOWN);
    piccheck= "Failed";
    picfailed.setChecked(true);
        cb_pickupfailed.setChecked(true);

    sp_select_reason.setVisibility(View.VISIBLE);
    if(updatePickupStatus(piccheck)) {
        getpickupdata();
    }
    }
}else{
    piccheck= "Success";
    cb_pickupfailed.setChecked(false);
    sp_select_reason.setVisibility(GONE);
    if(updatePickupStatus(piccheck)) {
        getpickupdata();
    }
}
                                               }
                                           }
        );


        sub_lay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*   cameraImageCapture(MEMBER_PIC_CODE, "Member Card", MEMBER_PIC_NAME+ partFilename,
                        storeFilename,
                        file_path + storeFilename);*/
                cameraImageCapture(MEMBER_PIC_CODE, "Pickup Proof", MEMBER_PIC_NAME+ partFilename,
                        storeFilename,
                        file_path + storeFilename);


            }
        });

        sp_select_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {


                String item = adapter.getItemAtPosition(position).toString();

                sp_select_reason.setSelection(position);
                if (!item.equals("Select Reason")) {
                    str_lang = item;

                    Cursor getreasondetails = database.rawQuery("SELECT * FROM ReasonMaster where reason = '"+str_lang+"' AND reason_for = 3  ", null);
                    getreasondetails.moveToFirst();
                    if(getreasondetails.getCount() > 0){
                       rid = getreasondetails.getString(getreasondetails.getColumnIndex("rid"));
                       Log.v("getreasondetails","- "+ rid);
                    }
                    getpickupdata();
                } else {
                    str_lang = "";
                    Toast.makeText(getApplicationContext(), "Please Select a Valid Reason", Toast.LENGTH_LONG).show();
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

    }





    private void showalert(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(PickupDelivery.this).create();
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


    public void getOrderDetails(String ordernumber,String sn) {

//        Cursor getOrders = database.rawQuery("Select * from orderheader where order_number = '" + ordernumber + "' ", null);
        Cursor getOrders = database.rawQuery("Select * from orderheader where Shipment_Number = '" + sn + "' ", null);

        if (getOrders.getCount() > 0) {
            getOrders.moveToFirst();
            tv_shipping_num.setText(getOrders.getString(getOrders.getColumnIndex("Shipment_Number")));
            ship_num = getOrders.getString(getOrders.getColumnIndex("Shipment_Number"));
            paymentMode = getOrders.getString(getOrders.getColumnIndex("payment_mode"));
            tv_order_number.setText(getOrders.getString(getOrders.getColumnIndex("order_number")));
//            Log.v("ship_num", ship_num);
            customerName = getOrders.getString(getOrders.getColumnIndex("customer_name"));
            tx_name.setText(customerName);
            customerPhone  = getOrders.getString(getOrders.getColumnIndex("customer_contact_number"));
            tx_phone.setText(customerPhone);
            customerAddress  = getOrders.getString(getOrders.getColumnIndex("shipping_address"));
            tx_address.setText(customerAddress);
            orderType = getOrders.getString(getOrders.getColumnIndex("order_type"));
        }
        getOrders.close();
    }


    public  void  getpickupdata(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());
        Log.v("getpickupdata","- "+ formattedDate);
        Cursor count = database.rawQuery("select * from PickupConfirmation where shipmentno='"
                + ship_num + "' ORDER BY createdate DESC ", null);

        if (count.getCount() == 0) {

            String Pickupinsert = "Insert into PickupConfirmation (orderno,shipmentno," +
                    "customername," +
                    "customerphone,customeraddress, customerphoto, pickupstatus,createdate,latitude,longitude,reason,reason_id)" + " VALUES ('" + order_num +
                    "','" + ship_num + "','" +
                    customerName + "','" + customerPhone + "' ,'"
                    + customerAddress + "','" + storeFilename + "', '"+piccheck+"','"+formattedDate+"','','','"+str_lang+"',"+rid+")";
            database.execSQL(Pickupinsert);

            Log.e("InserData",Pickupinsert );

        }else if (count.getCount() > 0) {

            String PickupUpdate = "UPDATE PickupConfirmation set orderno='" + order_num +
                    "'," + "shipmentno='" + ship_num + "',customername='"
                    + customerName + "', customerphone='"
                    + customerPhone + "',customeraddress='"
                    + customerAddress + "', customerphoto='"
                    + storeFilename +"' , pickupstatus ='"
                    + piccheck +"',createdate = '"+formattedDate+"',latitude= '',longitude = '' , reason = '"+str_lang+"' , reason_id = '"+rid+"' "+
                    "where shipmentno ='" + ship_num + "'  ";
            database.execSQL(PickupUpdate);

            Log.e("InserDate", PickupUpdate);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MEMBER_PIC_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                String responseCode = data.getStringExtra(RESPONSE_CODE_ARG);
                if (responseCode.equalsIgnoreCase("900")) {

                    String imagePath = data.getStringExtra(FILE_PATH_ARG);
                    Log.v("onActivityResult", imagePath);
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
                                        cam_lay.setBackgroundDrawable(dr);
                                    }
                                });

                        String lastOne = parts[parts.length - 1];
                        storeFilename = lastOne;
                        im_picup.setVisibility(View.INVISIBLE);
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
        }

    }



    @SuppressLint("SimpleDateFormat")
    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }


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
                        Log.v("onGranted","- "+ prooff);
                        if (!prooff.equals("")) {
                            Intent intent = new Intent(PickupDelivery.this, PreviewActivity.class);
                            intent.putExtra(FILE_PATH_ARG, filePath);
                            intent.putExtra("code", String.valueOf(req_code));
                            intent.putExtra("PreviewActivity", "PreviewStatus");
                            intent.putExtra("heading", headName);
                            startActivityForResult(intent, req_code);
                        } else {
                            Intent intent = new Intent(PickupDelivery.this, CameraFragmentMainActivity.class);
                            intent.putExtra("fileName", fileName);
                            intent.putExtra("heading", headName);
                            intent.putExtra("shipmentId", ship_num);
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


    private void customerActivity(int customerUnDeliveryCode, String emptyRetake, String headingName, String fileName) {
        Intent intent = new Intent(PickupDelivery.this, CameraFragmentMainActivity.class);
        intent.putExtra("fileName", fileName);
        intent.putExtra("retake", emptyRetake);
        intent.putExtra("shipmentId", ship_num);
        intent.putExtra("heading", headingName);
        startActivityForResult(intent, customerUnDeliveryCode);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    public void uploadPickupComplete(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en","US"));
        String currentDateTimeString = format.format(new Date());

//        if(updateComplete()){
        Cursor getPickupDetails = database.rawQuery("select IFNULL(O.return_id,0) as return_id,O.delivery_status, O.customer_name, O.order_number, " +
                "IFNULL(P.shipmentno,0) as shipmentno, IFNULL(P.customerphoto, 0) as customerphoto, IFNULL(P.latitude, 0) as latitude, IFNULL(P.longitude, 0) as longitude, " +
                "IFNULL(P.createdate, 0) as createdate,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O.attempt_count, 0) as attempt_count," +
                "IFNULL(P.customeraddress,0) as shipment_address, IFNULL(P.customerphone,0) as customerphone,IFNULL(P.reason, 0) as reason,IFNULL(P.reason_id, 0) as reason_id, IFNULL(O.pickup_status, 'Success') as pickup_status, IFNULL(order_type, 0) as order_type from orderheader O INNER JOIN PickupConfirmation P on P" +
                ".shipmentno = O.Shipment_Number where " +
                " P.shipmentno = '" + ship_num + "' ORDER BY P.createdate DESC ", null);

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
            final String pickup_status = getPickupDetails.getString(getPickupDetails.getColumnIndex("pickup_status"));

            try {
                paramObject.put("runsheetNo", AppController.getStringPreference(Constants.USER_ID, ""));
                paramObject.put("referenceNumber", getPickupDetails.getString(getPickupDetails.getColumnIndex("referenceNumber")));
                paramObject.put("latitude", "0.0");
                paramObject.put("longitude", "0.0");
                paramObject.put("return_id", getPickupDetails.getString(getPickupDetails.getColumnIndex("return_id")));
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
                paramObject.put("pickup_status", getPickupDetails.getString(getPickupDetails.getColumnIndex("pickup_status")));
                paramObject.put("order_type", getPickupDetails.getString(getPickupDetails.getColumnIndex("order_type")));

                jsonFieldObj.put("image", image_url + getPickupDetails.getString(getPickupDetails.getColumnIndex("customerphoto")));
                jsonFieldObj.put("address", getPickupDetails.getString(getPickupDetails.getColumnIndex("shipment_address")));
                jsonFieldObj.put("phone", getPickupDetails.getString(getPickupDetails.getColumnIndex("customerphone")));
                if(!pickup_status.equals("Success")){
                jsonFieldObj.put("reason", getPickupDetails.getString(getPickupDetails.getColumnIndex("reason")));
                jsonFieldObj.put("reason_id", getPickupDetails.getString(getPickupDetails.getColumnIndex("reason_id")));
                }else{
                    jsonFieldObj.put("reason", "");
                    jsonFieldObj.put("reason_id", 0);
                }
                Cursor getOrders = database.rawQuery("Select IFNULL(delivery_qty,0) as delivery_qty, IFNULL(product_code, 0) as product_code, IFNULL(product_name, '') as product_name," +
                        "IFNULL(quantity, 0) as quantity, IFNULL(amount_collected, 0) as amount_collected, IFNULL(partial_reason, null) as partial_reason, IFNULL(r_id, null) as r_id   from ProductDetails where shipmentnumber = '" + ship_num + "'  ", null);
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


            final String pickupStatus=pickup_status;

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
                        if(!pickupStatus.equals("Success")) {
                            alertDialogMsg(PickupDelivery.this, "Pickup", "Pickup Failed", getResources().getString(R.string.ok));
                        }else{
                            alertDialogMsg(PickupDelivery.this, "Pickup", "Pickup Successful", getResources().getString(R.string.ok));
                        }
                        dialogLoading.dismiss();
                        updateOrderStatus();
                    } else if (value.getRes_msg().equalsIgnoreCase("pickup updated")) {
                        alertDialogMsg(PickupDelivery.this, "Pickup",  "Pickup Successful", getResources().getString(R.string.ok));
                        dialogLoading.dismiss();

                    } else {
                        Logger.showShortMessage(PickupDelivery.this, "Pickup Not Successful");
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
                }


            });
        }
//        }
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

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.e("Onstop", "MainonStop");
        /*Get the current activity name*/
        if(this.mBatInfoReceiver != null) {
            unregisterReceiver(this.mBatInfoReceiver);
        }

    }

    private void updateSyncStatus() {

            database.execSQL("UPDATE orderheader set delivery_status = 'pickup' where Shipment_Number ='" +
                    ship_num + "' ");
    }

    public boolean updateComplete() {

        database.execSQL("UPDATE orderheader set sync_status  = 'C', pickup_status = '"+piccheck+"' where Shipment_Number ='" + ship_num + "' ");
        return true;
    }
    public void uploadImage() {
//        Log.v("uploadImage", "uploadImageun");
        dialogLoading = new ProgressIndicatorActivity(PickupDelivery.this);
        dialogLoading.showProgress();
        if (updateComplete()) {
        Cursor getPickupImage = database.rawQuery("select  O.delivery_status, O.order_number, " +
                "IFNULL(P.shipmentno,0) as shipmentno, IFNULL(P.customerphoto, 0) as customerphoto, IFNULL(P.latitude, 0) as latitude, IFNULL(P.longitude, 0) as longitude, " +
                "IFNULL(P.createdate, 0) as createdate,IFNULL(O.referenceNumber,0) as referenceNumber,IFNULL(O.attempt_count, 0) as attempt_count," +
                "IFNULL(P.customeraddress,0) as shipment_address, IFNULL(P.customerphone,0) as customerphone from orderheader O INNER JOIN PickupConfirmation P on P" +
                ".shipmentno = O.Shipment_Number where " +
                " P.shipmentno = '" + ship_num + "' ", null);
        if (getPickupImage.getCount() > 0) {
            getPickupImage.moveToFirst();
            file_proofPhoto = file_path + getPickupImage.getString(getPickupImage.getColumnIndex("customerphoto"));
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            InthreeApi apiService = retrofit.create(InthreeApi.class);


            File fileProofPhoto = new File(file_proofPhoto);
            RequestBody requestBodyDelivery = RequestBody.create(MediaType.parse("*/*"), fileProofPhoto);
            MultipartBody.Part fileToDelivery = MultipartBody.Part.createFormData("image", fileProofPhoto.getName(), requestBodyDelivery);

            final Observable<UndeliveryResp> observable = apiService.getUnDeliveryImage(fileToDelivery)
                    .subscribeOn
                            (Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            observable.subscribe(new Observer<UndeliveryResp>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(UndeliveryResp value) {
                    Log.v("undeliveredupload", "image");

                    List<UndeliveryResp> orderVal = value.getUndeliveredResp();
                    if (value.getRes_msg().equalsIgnoreCase("undelivered success")) {

                       /* alertDialogMsg(PickupDelivery.this, getResources().getString(R.string.undeli_title), getResources().getString(R.string.undeli_success_msg), getResources().getString(R.string.ok));
                        dialogLoading.dismiss();*/
                       uploadPickupComplete();

                    } else if (value.getRes_msg().equalsIgnoreCase("undelivered failed")) {
                        alertDialogMsg(PickupDelivery.this, getResources().getString(R.string.undeli_title), getResources().getString(R.string.undeli_success_msg), getResources().getString(R.string.ok));
                        dialogLoading.dismiss();
                    } else {
                        Logger.showShortMessage(PickupDelivery.this, getResources().getString(R.string.undeli_notsuccess_msg));
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
        }

        }
    }

    private void updateOrderStatus() {

        pick_attempt_count++;
        database.execSQL("UPDATE orderheader set sync_status = 'U', attempt_count = " + pick_attempt_count + " where Shipment_Number ='" +
                ship_num + "' ");
    }

    private boolean updatePickupStatus(String pickupstatus) {

      if(pickupstatus.equals("Success")){
            delivery_status = "delivered";
//            delivery_status = "pickup";
      }else if(pickupstatus.equals("Failed")){
            delivery_status = "undelivered";
//            delivery_status = "pickup";
       }
        database.execSQL("UPDATE orderheader set pickup_status = '"+pickupstatus+"', delivery_status = '"+delivery_status+"' where Shipment_Number ='" +
                ship_num + "' ");
        return true;
    }

    public ArrayList<String> getTableValues() {
//        my_array.add("Select Reason");
        my_array.add(getString(R.string.select_reason_def));
        try {
//            Cursor getSchemeValue = database.rawQuery("select * from PartialReasonMaster  ", null);
            Cursor getSchemeValue = database.rawQuery("select * from ReasonMaster where reason_for = '3'  ", null);
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

    public void alertDialogMsgError(Context context, String title, String content, String okmsg) {
        boolean setSampFlag = false;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
//        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
//                        Intent goToMain = new Intent(PickupDelivery.this, MainActivity.class);
//                        startActivity(goToMain);
//                        finish();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
        sweetAlertDialog.setCancelable(false);
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
                        Intent goToMain = new Intent(PickupDelivery.this, MainActivity.class);
                        startActivity(goToMain);
                        finish();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
        sweetAlertDialog.setCancelable(false);
    }



    public void getqtymax(){

        Cursor getqty = database.rawQuery("SELECT SUM(quantity)AS quantity FROM ProductDetails where shipmentnumber = '" + ship_num + "' ", null);
        if (getqty.getCount() > 0) {
            getqty.moveToFirst();
            maxget = getqty.getInt(getqty.getColumnIndex("quantity"));

        }
        getqty.close();

    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) PickupDelivery.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
