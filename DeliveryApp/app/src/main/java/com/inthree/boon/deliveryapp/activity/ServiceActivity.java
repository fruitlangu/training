package com.inthree.boon.deliveryapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.AttributeShowAdapter;
import com.inthree.boon.deliveryapp.adapter.DocumentAttributeAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.model.AttributeModel;
import com.inthree.boon.deliveryapp.model.CheckListModel;
import com.inthree.boon.deliveryapp.model.ServiceConfirm;
import com.inthree.boon.deliveryapp.request.DeliveryConfirmReq;
import com.inthree.boon.deliveryapp.request.PartialReq;
import com.inthree.boon.deliveryapp.request.ServiceConfirmReq;
import com.inthree.boon.deliveryapp.response.DeliveryConfirmResp;
import com.inthree.boon.deliveryapp.response.PartialResp;
import com.inthree.boon.deliveryapp.response.ServiceResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;
import static com.inthree.boon.deliveryapp.app.Constants.DB_NAME;

public class ServiceActivity extends AppCompatActivity {

    LinearLayout info;
    ExternalDbOpenHelper dbOpenHelper;
    SQLiteDatabase database;
    final List<AttributeModel> item_atributes = new ArrayList<>();
    final List<AttributeModel> document_item_atributes = new ArrayList<>();
    AttributeModel attrimodel;
    RecyclerView rv_attributes;
    RecyclerView rv_document_attributes;
    AttributeShowAdapter adapter;
    DocumentAttributeAdapter document_adapter;
    String shipmentNumber;
    Button bt_post_service;
    TextView tv_shipid;
    private RadioGroup rg_appointment;

    TextView tv_appointment;
    TextView tv_simple_manner;
    RadioGroup rg_simple_manner;
    TextView tv_safety_measures;
    RadioGroup rg_safety_measures;
    TextView tv_pleasing;
    RadioGroup rg_pleasing;
    RadioButton rb_appointment_yes;
    RadioButton rb_appointment_no;
    RadioButton rb_simple_manner_yes;
    RadioButton rb_simple_manner_no;
    RadioButton rb_safety_measures_yes;
    RadioButton rb_safety_measures_no;
    RadioButton rb_pleasing_yes;
    RadioButton rb_pleasing_no;

    RadioButton rb_appointment;
    RadioButton rb_simple_manner;
    RadioButton rb_safety_measures;
    RadioButton rb_pleasing;
    int selectedId;
    int selectedId1;
    int selectedId2;
    int selectedId3;
//    ArrayList<CheckListModel> list = new ArrayList<CheckListModel>();
    private SweetAlertDialog pDialog;
    ServiceConfirm mServiceConfirm;
    String serviceShipNo;
    int attempt_count;
    ProgressIndicatorActivity dialogLoading;
    String product_name;
    ImageView iv_sign;
    TextView tv_pls_sign_here;
    String sign_path;
    String image_url;
    boolean upload_status = false;
    boolean textContent = true;
    boolean checkContent = true;
    String editvalue = null;
    String fetchVal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        info = (LinearLayout) findViewById(R.id.info);

        dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.btn_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        sign_path = String.valueOf(this.getFilesDir()) + "/ServiceSignApp/";
        image_url = getResources().getString(R.string.delivery_url) + "/media/";
        Intent shipNum = getIntent();
        shipmentNumber = shipNum.getStringExtra("ship_id");
        product_name = shipNum.getStringExtra("product_name");
        Log.v("shipmentNumber","- "+shipmentNumber);
//        ListView listView = (ListView) findViewById(R.id.listview);
//        listView.setVisibility(View.INVISIBLE);
        mServiceConfirm = new ServiceConfirm();
        rg_appointment = (RadioGroup) findViewById(R.id.rg_appointment);
        rg_simple_manner = (RadioGroup) findViewById(R.id.rg_simple_manner);
        rg_safety_measures = (RadioGroup) findViewById(R.id.rg_safety_measures);
        rg_pleasing = (RadioGroup) findViewById(R.id.rg_pleasing);
        rv_attributes= (RecyclerView) findViewById(R.id.rv_attributes);
        rv_document_attributes= (RecyclerView) findViewById(R.id.rv_document_attributes);
        bt_post_service = (Button) findViewById(R.id.bt_post_service);
        tv_shipid = (TextView) findViewById(R.id.tv_shipid);
        tv_appointment = (TextView) findViewById(R.id.tv_appointment);
        tv_simple_manner = (TextView) findViewById(R.id.tv_simple_manner);
        tv_safety_measures = (TextView) findViewById(R.id.tv_safety_measures);
        tv_pleasing = (TextView) findViewById(R.id.tv_pleasing);
        tv_pls_sign_here = (TextView ) findViewById(R.id.tv_pls_sign_here);

        rb_appointment_yes = (RadioButton) findViewById(R.id.rb_appointment_yes);
        rb_appointment_no= (RadioButton) findViewById(R.id.rb_appointment_no);
        rb_simple_manner_yes= (RadioButton) findViewById(R.id.rb_simple_manner_yes);
        rb_simple_manner_no= (RadioButton) findViewById(R.id.rb_simple_manner_no);
        rb_safety_measures_yes= (RadioButton) findViewById(R.id.rb_safety_measures_yes);
        rb_safety_measures_no= (RadioButton) findViewById(R.id.rb_safety_measures_no);
        rb_pleasing_yes= (RadioButton) findViewById(R.id.rb_pleasing_yes);
        rb_pleasing_no= (RadioButton) findViewById(R.id.rb_pleasing_no);

        iv_sign = (ImageView) findViewById(R.id.iv_sign);

        tv_shipid.setText(shipmentNumber +" - "+ product_name);
        GridLayoutManager glm = new GridLayoutManager(this, 1);
        GridLayoutManager glm1 = new GridLayoutManager(this, 1);

//        database.execSQL("UPDATE serviceMaster set delivery_status = 'complete' where shipment_id ='" +
//                shipmentNumber + "' AND sync_status = 'P' ");

        /*Cursor uname = database.rawQuery("Select * from serviceMaster where delivery_status = 'incomplete' AND sync_status = 'U' AND shipment_id = '"+shipmentNumber+"' ", null);

        if (uname.getCount() > 0){
            database.execSQL("UPDATE serviceMaster set delivery_status = 'complete', sync_status = 'P' where shipment_id ='" + shipmentNumber + "' ");
        }else{
            database.execSQL("UPDATE serviceMaster set delivery_status = 'complete' where shipment_id ='" + shipmentNumber + "' AND sync_status = 'P' ");
        }*/

        Cursor uname = database.rawQuery("Select * from serviceMaster where delivery_status = 'incomplete' AND sync_status = 'U' AND shipment_id = '"+shipmentNumber+"' ", null);

        if (uname.getCount() > 0){
            database.execSQL("UPDATE serviceMaster set delivery_status = 'complete', sync_status = 'P' where shipment_id ='" + shipmentNumber + "' ");
        }else{
            Cursor unameOffline = database.rawQuery("Select * from serviceMaster where delivery_status = 'incomplete' AND sync_status = 'C' AND shipment_id = '"+shipmentNumber+"' ", null);

            if (unameOffline.getCount() > 0){
                alertDialogMsgOffline(ServiceActivity.this, "Error", "Cannot Process Servicing at the moment", "Ok");
            }else{
                database.execSQL("UPDATE serviceMaster set delivery_status = 'complete' where shipment_id ='" + shipmentNumber + "' AND sync_status = 'P' ");
            }
        }

        iv_sign.setEnabled(false);
        iv_sign.setClickable(false);
//        getDetails();
        rv_attributes.setLayoutManager(glm);
//        productAttributeValues();
//        productDocumentAttributeValues();
        adapter = new AttributeShowAdapter(this, item_atributes,database);
        rv_attributes.setAdapter(adapter);
        getDetails();
        document_adapter = new DocumentAttributeAdapter(this, document_item_atributes,database);
        rv_document_attributes.setLayoutManager(glm1);
        rv_document_attributes.setAdapter(document_adapter);

        selectedId = rg_appointment.getCheckedRadioButtonId();
        selectedId1 = rg_simple_manner.getCheckedRadioButtonId();
        selectedId2 = rg_safety_measures.getCheckedRadioButtonId();
        selectedId3 = rg_pleasing.getCheckedRadioButtonId();

        rb_appointment = (RadioButton) findViewById(selectedId);
        rb_simple_manner = (RadioButton) findViewById(selectedId1);
        rb_safety_measures = (RadioButton) findViewById(selectedId2);
        rb_pleasing = (RadioButton) findViewById(selectedId3);


        if(selectedId != -1 && selectedId1 !=-1 && selectedId2 !=-1 && selectedId3 !=-1 ){
            bt_post_service.setEnabled(true);
            bt_post_service.setAlpha(1);
        if (rb_appointment.getText().toString().equalsIgnoreCase("Yes")) {
//            Toast.makeText(ServiceActivity.this,
//                    "Aha..! Thank you very much..!!", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(ServiceActivity.this,
//                    "Ohh...What is the problem?", Toast.LENGTH_SHORT).show();
        }
        }else{
//            Toast.makeText(ServiceActivity.this,
//                    "Nothing Selected", Toast.LENGTH_SHORT).show();
        }

        rg_appointment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.v("rg_appointment", String.valueOf(checkedId));
                selectedId = group.getCheckedRadioButtonId();

                rb_appointment = (RadioButton) findViewById(selectedId);
                String get_appointment_str = tv_appointment.getText().toString();
                String get_appointment_check = rb_appointment.getText().toString();

                CheckListModel arr_appointment = new CheckListModel(get_appointment_str,get_appointment_check);
//                list.add(arr_appointment);
                Cursor uname = database.rawQuery("Select * from serviceFeedbackItems where feedback_id = 1 AND shipment_no = '"+shipmentNumber+"'  ", null);

                if (uname.getCount() == 0){
                    String insertProduct = "Insert into serviceFeedbackItems(feedback_id,feedback,shipment_no,feedback_status)" +
                            " Values (1, '" + get_appointment_str + "', '" + shipmentNumber + "', '" + get_appointment_check + "')";
                    database.execSQL(insertProduct);
                }else{
                    database.execSQL("UPDATE serviceFeedbackItems set feedback_status = '"+get_appointment_check+"' where shipment_no ='" +
                            shipmentNumber + "' AND feedback_id = 1 ");
                }
                uname.close();

                if(selectedId != -1 && selectedId1 !=-1 && selectedId2 !=-1 && selectedId3 !=-1 ){
//                    bt_post_service.setEnabled(true);
//                    bt_post_service.setAlpha(1);
                    iv_sign.setEnabled(true);
                    iv_sign.setClickable(true);
                }
            }
        });

        rg_simple_manner.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.v("rg_appointment", String.valueOf(checkedId));
                selectedId1 = group.getCheckedRadioButtonId();

                rb_simple_manner = (RadioButton) findViewById(selectedId1);
                String get_simple_manner_str = tv_simple_manner.getText().toString();
                String get_simple_manner_check = rb_simple_manner.getText().toString();
                CheckListModel arr_appointment = new CheckListModel( get_simple_manner_str,get_simple_manner_check);
//                list.add(arr_appointment);
                Cursor uname = database.rawQuery("Select * from serviceFeedbackItems where feedback_id = 2 AND shipment_no = '"+shipmentNumber+"'  ", null);

                if (uname.getCount() == 0){
                    String insertProduct = "Insert into serviceFeedbackItems(feedback_id,feedback,shipment_no,feedback_status)" +
                            " Values (2, '" + get_simple_manner_str + "', '" + shipmentNumber + "', '" + get_simple_manner_check + "')";
                    database.execSQL(insertProduct);
                }else{
                    database.execSQL("UPDATE serviceFeedbackItems set feedback_status = '"+get_simple_manner_check+"' where shipment_no ='" +
                            shipmentNumber + "' AND feedback_id = 2 ");
                }
                uname.close();
//                list.set(1,arr_appointment);
                if(selectedId != -1 && selectedId1 !=-1 && selectedId2 !=-1 && selectedId3 !=-1 ){
//                    bt_post_service.setEnabled(true);
//                    bt_post_service.setAlpha(1);
                    iv_sign.setEnabled(true);
                    iv_sign.setClickable(true);
                }
            }
        });

        rg_safety_measures.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.v("rg_appointment", String.valueOf(checkedId));
                selectedId2 = group.getCheckedRadioButtonId();

                rb_safety_measures = (RadioButton) findViewById(selectedId2);

                String get_safety_measures_str = tv_safety_measures.getText().toString();
                String get_safety_measures_check = rb_safety_measures.getText().toString();
                CheckListModel arr_appointment = new CheckListModel( get_safety_measures_str,get_safety_measures_check);
//                list.add(arr_appointment);
//                list.set(2,arr_appointment);
                Cursor uname = database.rawQuery("Select * from serviceFeedbackItems where feedback_id = 3 AND shipment_no = '"+shipmentNumber+"'  ", null);

                if (uname.getCount() == 0){
                    String insertProduct = "Insert into serviceFeedbackItems(feedback_id,feedback,shipment_no,feedback_status)" +
                            " Values (3, '" + get_safety_measures_str + "', '" + shipmentNumber + "', '" + get_safety_measures_check + "')";
                    database.execSQL(insertProduct);
                }else{
                    database.execSQL("UPDATE serviceFeedbackItems set feedback_status = '"+get_safety_measures_check+"' where shipment_no ='" +
                            shipmentNumber + "' AND feedback_id = 3 ");
                }
                uname.close();
                if(selectedId != -1 && selectedId1 !=-1 && selectedId2 !=-1 && selectedId3 !=-1 ){
//                    bt_post_service.setEnabled(true);
//                    bt_post_service.setAlpha(1);
                    iv_sign.setEnabled(true);
                    iv_sign.setClickable(true);
                }
            }
        });

        rg_pleasing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.v("rg_appointment", String.valueOf(checkedId));
                selectedId3 = group.getCheckedRadioButtonId();

                rb_pleasing = (RadioButton) findViewById(selectedId3);
                String get_pleasing_str = tv_pleasing.getText().toString();
                String get_pleasing_check = rb_pleasing.getText().toString();
                CheckListModel arr_appointment = new CheckListModel( get_pleasing_str,get_pleasing_check);
//                list.add(arr_appointment);
//                list.set(3,arr_appointment);

                Cursor uname = database.rawQuery("Select * from serviceFeedbackItems where feedback_id = 4 AND shipment_no = '"+shipmentNumber+"'  ", null);

                if (uname.getCount() == 0){
                    String insertProduct = "Insert into serviceFeedbackItems(feedback_id,feedback,shipment_no,feedback_status)" +
                            " Values (4, '" + get_pleasing_str + "', '" + shipmentNumber + "', '" + get_pleasing_check + "')";
                    database.execSQL(insertProduct);
                }else{
                    database.execSQL("UPDATE serviceFeedbackItems set feedback_status = '"+get_pleasing_check+"' where shipment_no ='" +
                            shipmentNumber + "' AND feedback_id = 4 ");
                }
                uname.close();
//                CheckListModel a = list.get(1);

//                Log.v("CheckListModel",a.getFeedback());
                if(selectedId != -1 && selectedId1 !=-1 && selectedId2 !=-1 && selectedId3 !=-1 ){
                    bt_post_service.setEnabled(true);
                    bt_post_service.setAlpha(1);
                    iv_sign.setEnabled(true);
                    iv_sign.setClickable(true);
                }
                insertServiceInfo();
            }
        });


        bt_post_service.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {

                textContent = true;
                checkContent = true;
                String sign_status = "";
                int selectedId = rg_appointment.getCheckedRadioButtonId();
//                Log.v("rb_appointment","--"+shipmentNumber);
                rb_appointment = (RadioButton) findViewById(selectedId);
                Cursor customerName = database.rawQuery("select SC.ship_num,SC.customer_fname,SC.customer_cnum,SC.ship_address,SC.ship_city,IFNULL(SC.signProof, '') as signProof ,SC.ship_phone,SC.customer_feedback,SC.created_date,IFNULL(SC.function,'') as function,IFNULL(SC.documents,'') as documents  ,SC.feedback ,S.sync_status,IFNULL(S.attempt,0 ) as attempt,S.order_id,S.shipping_pincode from serviceMaster S  INNER JOIN serviceConfirmation SC on SC.ship_num = S.shipment_id  where SC.ship_num = '" + shipmentNumber + "' ", null);
                customerName.moveToFirst();
                if (customerName.getCount() > 0){

                   String function_status  = customerName.getString(customerName.getColumnIndex("function"));
                   String document_status  = customerName.getString(customerName.getColumnIndex("documents"));
                   sign_status  = customerName.getString(customerName.getColumnIndex("signProof"));
/*if(!function_status.equals("") && function_status.equals("Yes") && function_status != null && !document_status.equals("") &&  document_status.equals("Yes") && document_status != null){
    upload_status = true;
}*/
                    if(!function_status.equals("") && function_status.equals("Yes") && function_status != null && !document_status.equals("") &&  document_status.equals("Yes") && document_status != null &&  !sign_status.equals(""))
                    {
                        upload_status = true;
                    }

                }
                customerName.close();

                Cursor getshowdetails = database.rawQuery("SELECT * FROM serviceProductAttributes where shipment_no ='"+shipmentNumber+"' ", null);
                getshowdetails.moveToFirst();
                if(getshowdetails.getCount() > 0) {
                    while (!getshowdetails.isAfterLast()){
                        String input_field_type = getshowdetails.getString(getshowdetails.getColumnIndex("input_field_type"));
                    String text_content = getshowdetails.getString(getshowdetails.getColumnIndex("text_content"));
                    int check_content = getshowdetails.getInt(getshowdetails.getColumnIndex("checked"));

                    if (input_field_type.equals("text")) {
                        if ( text_content == null || text_content.equals("null")) {
                            textContent = false;
                        }else{
                            textContent = true;
                        }
                    } else if (input_field_type.equals("checkbox")) {

                            if ( check_content == 0 ) {
                                Log.v("check_content"," - "+ check_content);
                                checkContent = false;
                            }/*else{
                                Log.v("check_content1"," - "+ check_content);
                                checkContent = true;
                            }*/
                        }
                        getshowdetails.moveToNext();
                }
                }

if(upload_status && textContent && checkContent){
if(Utils.checkNetworkAndShowDialog(ServiceActivity.this)) {
    if (updateComplete()) {
        uploadImage(shipmentNumber);
    }
}else{
    database.execSQL("UPDATE serviceMaster set sync_status = 'C',image_status = 'C' where shipment_id ='" +
            shipmentNumber + "' ");
    alertDialogMsgOffline(ServiceActivity.this, "Success", "Stored Offline", "Ok");
}
}else if(textContent == false){
    checkAlert(ServiceActivity.this, "Check Error", "Some texts are not entered", "Ok");
}else if(checkContent == false){
    checkAlert(ServiceActivity.this, "Check Error", "Some checkboxes are not Checked", "Ok");
}else if(sign_status.equals("")){
    checkAlert(ServiceActivity.this, "Check Error", "Please sign the Form", "Ok");
}else{
    checkAlert(ServiceActivity.this, "Check Error", "Fill in the Missing Features", "Ok");
}
            }});


        iv_sign.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
            signaturePad();
            }});

    }

    public Boolean updateComplete() {
        dialogLoading = new ProgressIndicatorActivity(ServiceActivity.this);
        dialogLoading.showProgress();
        database.execSQL("UPDATE serviceMaster set sync_status = 'C',image_status = 'C' where shipment_id ='" +
                shipmentNumber + "' ");
  /*      database.execSQL("UPDATE orderheader set delivery_status = 'U' where shipmentnumber IN " +
                "((SELECT Shipment_Number FROM orderheader WHERE Shipment_Number = '" + shipmentNumber + "')) ");*/

        return true;
    }

    public  void onBackPressed() {
//        finish();
        backAlert(this, getResources()
                .getString(R.string.dl_back_pressed), getResources().getString(R.string.back_pressed), getResources()
                .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel));
    }

  public void getDetails(){
      Cursor getDetailsOrder = database.rawQuery("SELECT sm.*,sc.* FROM serviceMaster sm INNER JOIN serviceConfirmation sc ON sc.ship_num = sm.shipment_id where sm.shipment_id ='"+shipmentNumber+"'  ", null);
      getDetailsOrder.moveToFirst();
      if(getDetailsOrder.getCount() > 0){


          AlertDialogCancel(this, getResources().getString(R.string.sure), getResources().getString(R.string.getwarning), getResources()
                  .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel));


      }else{
          Cursor getDetailsOrderNew = database.rawQuery("SELECT * FROM serviceMaster where shipment_id ='"+shipmentNumber+"'  ", null);
          getDetailsOrderNew.moveToFirst();
          if(getDetailsOrderNew.getCount() > 0){

              mServiceConfirm.setShip_num(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipment_id")));
              mServiceConfirm.setCustomer_fname(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("customer_name")));
              mServiceConfirm.setShip_address(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipping_address")));
              mServiceConfirm.setShip_pincode(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipping_pincode")));
              Log.v("serviceMaster"," - "+mServiceConfirm.getShip_num());
              Cursor getDetailsProducts = database.rawQuery("SELECT * FROM serviceItems where shipment_number ='"+mServiceConfirm.getShip_num()+"' ", null);
              getDetailsProducts.moveToFirst();
              if(getDetailsProducts.getCount() > 0){
                  mServiceConfirm.setProduct_name(getDetailsProducts.getString(getDetailsProducts.getColumnIndex("name")));
                  mServiceConfirm.setProduct_sku(getDetailsProducts.getString(getDetailsProducts.getColumnIndex("sku")));
                  Log.v("serviceMaster"," - "+mServiceConfirm.getProduct_name());
                  productAttribute(mServiceConfirm.getShip_num());
                  productDocumentAttribute(mServiceConfirm.getShip_num());
                  productFeedbackItems(mServiceConfirm.getShip_num());
              }
          }
          getDetailsOrderNew.close();
      }

      getDetailsOrder.close();
  }

    public void productAttribute(String sh){
        item_atributes.clear();
        Cursor getshowdetails = database.rawQuery("SELECT * FROM serviceProductAttributes where shipment_no ='"+sh+"' AND attribute_type = 'service_function' ", null);
        getshowdetails.moveToFirst();
        if(getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {

                attrimodel = new AttributeModel();
                Log.v("Count_product_name", getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
//                Log.v("Count_product_name", getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
                String input_type = getshowdetails.getString(getshowdetails.getColumnIndex("input_field_type"));
                Log.v("input_field_type"," -- "+input_type);
                String d = null;
                if(input_type.equals("checkbox")){
                    d = "checkbox";
                }else if(input_type.equals("text")){
                    d = "text";
                }
                int c_flag = getshowdetails.getInt(getshowdetails.getColumnIndex("checked"));
                boolean c =false;
                Log.v("checked_cflag", String.valueOf(c_flag));
                if(c_flag == 1){
                    c = true;
                }else if(c_flag == 0){
                    c = false;
                }
               /* else{
                    c = false;
                }*/
                attrimodel.setAttribute_name(getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
                attrimodel.setAttribute_id(getshowdetails.getString(getshowdetails.getColumnIndex("attribute_id")));
                attrimodel.setShipment_no(getshowdetails.getString(getshowdetails.getColumnIndex("shipment_no")));
                attrimodel.setText_content(getshowdetails.getString(getshowdetails.getColumnIndex("text_content")));
                attrimodel.setSelected(c);
                attrimodel.setInput_field_type(d);

                item_atributes.add(attrimodel);
//                item_atributes.add(new AttributeModel(c,getshowdetails.getString(getshowdetails.getColumnIndex("shipment_no")), getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")),getshowdetails.getString(getshowdetails.getColumnIndex("attribute_id")),
//                        d));

                getshowdetails.moveToNext();

            }
            adapter = new AttributeShowAdapter(this, item_atributes,database);
            rv_attributes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            rv_attributes.invalidate();
        }
        getshowdetails.close();
    }
    /*public void productAttributeValues(){

        Cursor getshowdetails = database.rawQuery("SELECT * FROM serviceProductAttributes where shipment_no ='"+shipmentNumber+"' AND attribute_type = 'service_function' ", null);
        getshowdetails.moveToFirst();
        if(getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {

                attrimodel = new AttributeModel();
                Log.v("Count_product_name", getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
                String input_type = getshowdetails.getString(getshowdetails.getColumnIndex("input_field_type"));
                Log.v("input_field_type"," -- "+input_type);
                String d = null;
                if(input_type.equals("checkbox")){
                    d = "checkbox";
                }else if(input_type.equals("text")){
                    d = "text";
                }
                int c_flag = getshowdetails.getInt(getshowdetails.getColumnIndex("checked"));
                boolean c =false;

                if(c_flag == 1){
                    c = true;
                }else if(c_flag == 0 || c_flag == 0 ){
                    c = false;
                }
                attrimodel.setAttribute_name(getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
                attrimodel.setAttribute_id(getshowdetails.getString(getshowdetails.getColumnIndex("attribute_id")));
                attrimodel.setShipment_no(getshowdetails.getString(getshowdetails.getColumnIndex("shipment_no")));
                attrimodel.setSelected(c);
                attrimodel.setInput_field_type(d);
                Log.v("c_flag", String.valueOf(c));
                item_atributes.add(attrimodel);
//                item_atributes.add(new AttributeModel(c,getshowdetails.getString(getshowdetails.getColumnIndex("shipment_no")), getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")),getshowdetails.getString(getshowdetails.getColumnIndex("attribute_id")),
//                        d));

                getshowdetails.moveToNext();

            }
            adapter = new AttributeShowAdapter(this, item_atributes,database);
            rv_attributes.setAdapter(adapter);
        }
        getshowdetails.close();
    }*/

    public void productDocumentAttribute(String sh){
        document_item_atributes.clear();
        Cursor getshowdetails = database.rawQuery("SELECT * FROM serviceProductAttributes where shipment_no ='"+sh+"' AND attribute_type = 'service_document' ", null);
        getshowdetails.moveToFirst();
        if(getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {

                attrimodel = new AttributeModel();
//                Log.v("Count_product_name", getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
                String input_type = getshowdetails.getString(getshowdetails.getColumnIndex("input_field_type"));
//                Log.v("input_field_type"," -- "+input_type);
                String d = null;
                if(input_type.equals("checkbox")){
                    d = "checkbox";
                }else if(input_type.equals("text")){
                    d = "text";
                }
                int c_flag = getshowdetails.getInt(getshowdetails.getColumnIndex("checked"));
                boolean c =false;

                if(c_flag == 1){
                    c = true;
                }else if(c_flag == 0 || c_flag == 0 ){
                    c = false;
                }
                attrimodel.setAttribute_name(getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
                attrimodel.setAttribute_id(getshowdetails.getString(getshowdetails.getColumnIndex("attribute_id")));
                attrimodel.setShipment_no(getshowdetails.getString(getshowdetails.getColumnIndex("shipment_no")));
                attrimodel.setText_content(getshowdetails.getString(getshowdetails.getColumnIndex("text_content")));
                attrimodel.setSelected(c);
                attrimodel.setInput_field_type(d);
//                Log.v("c_flag", String.valueOf(c));
                document_item_atributes.add(attrimodel);
//                item_atributes.add(new AttributeModel(c,getshowdetails.getString(getshowdetails.getColumnIndex("shipment_no")), getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")),getshowdetails.getString(getshowdetails.getColumnIndex("attribute_id")),
//                        d));

                getshowdetails.moveToNext();

            }
            document_adapter = new DocumentAttributeAdapter(this, document_item_atributes,database);
            rv_document_attributes.setAdapter(document_adapter);
        }
        getshowdetails.close();
    }

    /*public void productDocumentAttributeValues(){

        Cursor getshowdetails = database.rawQuery("SELECT * FROM serviceProductAttributes where shipment_no ='32000002' AND attribute_type = 'service_document' ", null);
        getshowdetails.moveToFirst();
        if(getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {

                attrimodel = new AttributeModel();
                Log.v("Count_product_name", getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
                String input_type = getshowdetails.getString(getshowdetails.getColumnIndex("input_field_type"));
                Log.v("input_field_type"," -- "+input_type);
                String d = null;
                if(input_type.equals("checkbox")){
                    d = "checkbox";
                }else if(input_type.equals("text")){
                    d = "text";
                }
                int c_flag = getshowdetails.getInt(getshowdetails.getColumnIndex("checked"));
                boolean c =false;

                if(c_flag == 1){
                    c = true;
                }else if(c_flag == 0 || c_flag == 0 ){
                    c = false;
                }
                attrimodel.setAttribute_name(getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")));
                attrimodel.setAttribute_id(getshowdetails.getString(getshowdetails.getColumnIndex("attribute_id")));
                attrimodel.setShipment_no(getshowdetails.getString(getshowdetails.getColumnIndex("shipment_no")));
                attrimodel.setSelected(c);
                attrimodel.setInput_field_type(d);
                Log.v("c_flag", String.valueOf(c));
                document_item_atributes.add(attrimodel);
//                item_atributes.add(new AttributeModel(c,getshowdetails.getString(getshowdetails.getColumnIndex("shipment_no")), getshowdetails.getString(getshowdetails.getColumnIndex("attribute_name")),getshowdetails.getString(getshowdetails.getColumnIndex("attribute_id")),
//                        d));

                getshowdetails.moveToNext();

            }
            document_adapter = new DocumentAttributeAdapter(this, document_item_atributes,database);
            rv_document_attributes.setAdapter(document_adapter);
        }
        getshowdetails.close();
    }*/

    public void dummyUpdate(){
        ArrayList<String> arr = new ArrayList<>();
        arr.add("one");
        arr.add("two");
        arr.add("three");
for(String w:arr)
Log.v("dummyUpdate before",w);
        arr.set(0,"four");
        for(String w:arr)
        Log.v("dummyUpdate before",w);
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

                        Cursor count = database.rawQuery("select * from serviceConfirmation where ship_num='"
                                + shipmentNumber + "'", null);
                        if (count.getCount() == 0) {
                            String deliverDetailsInsert = "Insert into serviceConfirmation (ship_num,customer_fname,customer_cnum,ship_address,ship_city,ship_phone,customer_feedback,created_date)" + " VALUES ('" +
                                    shipmentNumber +
                                    "','" + mServiceConfirm.getCustomer_fname() + "','" +
                                    mServiceConfirm.getCustomer_cnum() + "','" + mServiceConfirm.getShip_address() + "', " +
                                    "'" + mServiceConfirm.getShip_city() + "','" + mServiceConfirm.getShip_phone() + "','" + mServiceConfirm
                                    .getCustomer_feedback() + "', '" +
                                    mServiceConfirm.getCreated_date() + "' )";
                            database.execSQL(deliverDetailsInsert);
                        } else if (count.getCount() > 0) {

                            String deliveryDetailsupdate = "UPDATE serviceConfirmation set customer_fname='" + mServiceConfirm.getCustomer_fname() +
                                    "'," + "customer_cnum='" + mServiceConfirm.getCustomer_cnum() + "',ship_address='"
                                    + mServiceConfirm.getShip_address() + "',ship_city='" + mServiceConfirm.getShip_city() + "',ship_phone='" + mServiceConfirm.getShip_phone() + "',customer_feedback='" + mServiceConfirm
                                    .getCustomer_feedback() + "',created_date='" + mServiceConfirm.getCreated_date() + "' where ship_num ='" + shipmentNumber + "' ";
                            database.execSQL(deliveryDetailsupdate);
                        }
                        count.close();
                        finish();
                    }
                })
                .show();
    }


    public Boolean insertServiceInfo() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
        String currentDateTimeString = format.format(new Date());
        mServiceConfirm.setCreated_date(currentDateTimeString);
        String formattedDate;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());

        Log.v("redirect_val", "-" + "insertDeliveryInfo");
//        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
//        String currentDateTimeString = format.format(new Date());

        Cursor count = database.rawQuery("select * from serviceConfirmation where ship_num='"
                + shipmentNumber + "'", null);
        if (count.getCount() == 0) {
            Log.v("redirect_valc", "-" + count.getCount());
            String deliverDetailsInsert = "Insert into serviceConfirmation (ship_num,customer_fname,customer_cnum,ship_address,ship_city,ship_phone,customer_feedback,created_date)" + " VALUES ('" +
                    shipmentNumber +
                    "','" + mServiceConfirm.getCustomer_fname() + "','" +
                    mServiceConfirm.getCustomer_cnum() + "','" + mServiceConfirm.getShip_address() + "', " +
                    "'" + mServiceConfirm.getShip_city() + "','" + mServiceConfirm.getShip_phone() + "','" + mServiceConfirm
                    .getCustomer_feedback() + "', '" +
                    mServiceConfirm.getCreated_date() + "')";
            database.execSQL(deliverDetailsInsert);
        } else if (count.getCount() > 0) {
            Log.v("redirect_vale", "-" + count.getCount());
            String deliveryDetailsupdate = "UPDATE serviceConfirmation set customer_fname='" + mServiceConfirm.getCustomer_fname() +
                    "'," + "customer_cnum='" + mServiceConfirm.getCustomer_cnum() + "',ship_address='"
                    + mServiceConfirm.getShip_address() + "',ship_city='" + mServiceConfirm.getShip_city() + "',ship_phone='" + mServiceConfirm.getShip_phone() + "',customer_feedback='" + mServiceConfirm
                    .getCustomer_feedback() + "',created_date='" + mServiceConfirm.getCreated_date() + "' where ship_num ='" + shipmentNumber + "' ";
            database.execSQL(deliveryDetailsupdate);
        }
        count.close();
        return true;
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
                String checkFlag = null;
                database.execSQL("DELETE FROM serviceConfirmation where ship_num='" + shipmentNumber + "'");
                database.execSQL("DELETE FROM serviceFeedbackItems where shipment_no='" + shipmentNumber + "'");
                database.execSQL("UPDATE serviceProductAttributes set checked = "+checkFlag+", text_content  = null where shipment_no ='" +
                        shipmentNumber + "' ");
                Cursor getDetailsOrderNew = database.rawQuery("SELECT * FROM serviceMaster where shipment_id ='"+shipmentNumber+"'  ", null);
                getDetailsOrderNew.moveToFirst();
                if(getDetailsOrderNew.getCount() > 0){

                    mServiceConfirm.setShip_num(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipment_id")));
                    mServiceConfirm.setCustomer_fname(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("customer_name")));
                    mServiceConfirm.setShip_address(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipping_address")));
                    mServiceConfirm.setShip_pincode(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipping_pincode")));
                    Log.v("serviceMaster"," - "+mServiceConfirm.getShip_num());
                    Cursor getDetailsProducts = database.rawQuery("SELECT * FROM serviceItems where shipment_number ='"+mServiceConfirm.getShip_num()+"' ", null);
                    getDetailsProducts.moveToFirst();
                    if(getDetailsProducts.getCount() > 0){
                        mServiceConfirm.setProduct_name(getDetailsProducts.getString(getDetailsProducts.getColumnIndex("name")));
                        mServiceConfirm.setProduct_sku(getDetailsProducts.getString(getDetailsProducts.getColumnIndex("sku")));
                        Log.v("serviceMaster"," - "+mServiceConfirm.getProduct_name());
                        productAttribute(mServiceConfirm.getShip_num());
                        productDocumentAttribute(mServiceConfirm.getShip_num());
                    }
                }
                getDetailsOrderNew.close();
            }
        })
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
//                        Cursor getDetailsOrderNew = database.rawQuery("SELECT * FROM serviceMaster where shipment_id ='"+shipmentNumber+"'  ", null);
                        Cursor getDetailsOrderNew = database.rawQuery("SELECT SM.shipment_id,SM.customer_name,SM.shipping_address,SM.shipping_pincode, IFNULL(SC.signProof,null) as signProof   FROM serviceMaster SM INNER JOIN serviceConfirmation SC ON SC.ship_num = SM.shipment_id where SM.shipment_id ='"+shipmentNumber+"'  ", null);
                        getDetailsOrderNew.moveToFirst();
                        if(getDetailsOrderNew.getCount() > 0){

                            mServiceConfirm.setShip_num(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipment_id")));
                            mServiceConfirm.setCustomer_fname(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("customer_name")));
                            mServiceConfirm.setSignProof(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("signProof")));
                            mServiceConfirm.setShip_address(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipping_address")));
                            mServiceConfirm.setShip_pincode(getDetailsOrderNew.getString(getDetailsOrderNew.getColumnIndex("shipping_pincode")));
                            Log.v("getSignProof","= "+mServiceConfirm.getSignProof());
                            if(mServiceConfirm.getSignProof() != null ){
//                            if(!mServiceConfirm.getSignProof().equals("") ){
                                Log.v("getSignProof1","= "+mServiceConfirm.getSignProof());
                                File f = new File(sign_path, mServiceConfirm.getSignProof().toString());
                                Bitmap b = null;
                                try {
                                    b = BitmapFactory.decodeStream(new FileInputStream(f));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                BitmapDrawable background = new BitmapDrawable(b);
                                iv_sign.setBackgroundDrawable(background);
                                iv_sign.setEnabled(true);
                                iv_sign.setClickable(true);
                                bt_post_service.setAlpha(1);
                                bt_post_service.setEnabled(true);
                                tv_pls_sign_here.setVisibility(GONE);
                            }else{
//                                Log.v("getSignProof","= "+"else");
//                                iv_sign.setEnabled(true);
//                                iv_sign.setClickable(true);
                            }
//                            Log.v("serviceMaster"," - "+mServiceConfirm.getShip_num());
                            Cursor getDetailsProducts = database.rawQuery("SELECT * FROM serviceItems where shipment_number ='"+mServiceConfirm.getShip_num()+"' ", null);
                            getDetailsProducts.moveToFirst();
                            if(getDetailsProducts.getCount() > 0){
                                mServiceConfirm.setProduct_name(getDetailsProducts.getString(getDetailsProducts.getColumnIndex("name")));
                                mServiceConfirm.setProduct_sku(getDetailsProducts.getString(getDetailsProducts.getColumnIndex("sku")));
//                                Log.v("serviceMaster"," - "+mServiceConfirm.getProduct_name());
                                productAttribute(mServiceConfirm.getShip_num());
                                productDocumentAttribute(mServiceConfirm.getShip_num());
                                productFeedbackItems(mServiceConfirm.getShip_num());
                            }
                        }
                        getDetailsOrderNew.close();
                    }
                })
                .show();
    }

    public void productFeedbackItems(String sh){

        Cursor getshowdetails = database.rawQuery("SELECT * FROM serviceFeedbackItems where shipment_no ='"+sh+"' ", null);
        getshowdetails.moveToFirst();
        if(getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {

int feedback_id = getshowdetails.getInt(getshowdetails.getColumnIndex("feedback_id"));
String feedback_status = getshowdetails.getString(getshowdetails.getColumnIndex("feedback_status"));
                Log.v("productFeedbackItems","-- "+feedback_id);
if(feedback_id == 1){
    if(feedback_status.equals("Yes")){
        rb_appointment_yes.setChecked(true);
    }else if(feedback_status.equals("No")){
        rb_appointment_no.setChecked(true);
    }
}
else if(feedback_id == 2){
                    if(feedback_status.equals("Yes")){
                        rb_simple_manner_yes.setChecked(true);

                    }else if(feedback_status.equals("No")){
                        rb_simple_manner_no.setChecked(true);
                    }
                }
else if(feedback_id == 3){
    if(feedback_status.equals("Yes")){
        rb_safety_measures_yes.setChecked(true);

    }else if(feedback_status.equals("No")){
        rb_safety_measures_no.setChecked(true);
    }
}
else if(feedback_id == 4){
    if(feedback_status.equals("Yes")){
        rb_pleasing_yes.setChecked(true);

    }else if(feedback_status.equals("No")){
        rb_pleasing_no.setChecked(true);
    }
}

                getshowdetails.moveToNext();

            }

        }
        getshowdetails.close();
    }


   public void uploadComplete(String ship_no) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
        String currentDateTimeString = format.format(new Date());
//        Log.v("currentDateTimeString22", currentDateTimeString);
//        Log.v("currentDateTimeString22", ship_no);

        Cursor customerName = database.rawQuery("select SC.ship_num,SC.customer_fname,SC.customer_cnum,SC.ship_address,SC.ship_city,SC.signProof,SC.ship_phone,SC.customer_feedback,SC.created_date,IFNULL(SC.function, null) as function,IFNULL(SC.documents, null) as documents ,IFNULL(SC.feedback ,null) as feedback ,S.sync_status,IFNULL(S.attempt,0 ) as attempt,S.order_id,S.reference,S.shipping_pincode,S.attempt from serviceMaster S  INNER JOIN serviceConfirmation SC on SC.ship_num = S.shipment_id  where S.sync_status='C' AND SC.ship_num = '" + ship_no + "' ", null);

        if (customerName.getCount() > 0) {
            customerName.moveToFirst();
//            Log.v("get_reason", "--" + customerName.getString(customerName.getColumnIndex("ship_num")));
            serviceShipNo = customerName.getString(customerName.getColumnIndex("ship_num"));
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
//            Log.v("upload_custname", customerName.getString(customerName.getColumnIndex("customer_fname")));
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
            servicePelivery.setReferenceNo(customerName.getString(customerName.getColumnIndex("reference")));

            attempt_count = servicePelivery.getAttempt_count();
            attempt_count++;


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
                paramObject.put("referenceNo", servicePelivery.getReferenceNo());
                paramObject.put("shipment_address", servicePelivery.getShip_address());
                paramObject.put("shipment_city", servicePelivery.getShip_city());
                paramObject.put("shipment_phone", servicePelivery.getShip_phone());
                paramObject.put("created_at", servicePelivery.getCreated_date());
                paramObject.put("sign_proof", image_url + servicePelivery.getSignProof());
                paramObject.put("pincode", servicePelivery.getShippincode());

                paramObject.put("attemptCount", attempt_count);


                    Cursor getOrders = database.rawQuery("Select attribute_id,attribute_name,product_type,shipment_no,IFNULL(checked, 0) as checked ,input_field_type,is_require,attribute_type,IFNULL(text_content,null) as text_content from serviceProductAttributes where shipment_no = '" + shipmentNumber + "' ", null);
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

                Cursor getAttributeOrders = database.rawQuery("Select feedback,feedback_status,feedback_id,shipment_no from serviceFeedbackItems where shipment_no = '" + shipmentNumber + "' ", null);
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

                Cursor getProducts = database.rawQuery("Select service_id,sku,name,qty,item_received,qty_demo_completed,order_item_id,product_serial_no,product_type,created_at,shipment_number from serviceItems where shipment_number = '" + shipmentNumber + "' ", null);
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

if(value.getRes_msg().equals("service upload success")){
    Log.v("getRes_msg","- "+ value.getRes_msg());
    database.execSQL("UPDATE serviceMaster set sync_status = 'U', attempt = '" + attempt_count + "',image_status = 'U' where shipment_id ='" +
            shipmentNumber + "' ");
    attempt_count = 0;
    alertDialogMsg(ServiceActivity.this, "Success", "Service Complete", "Ok");
    dialogLoading.dismiss();
}else if(value.getRes_msg().equals("service upload duplicate")){
    database.execSQL("UPDATE serviceMaster set sync_status = 'E',image_status = 'C' where shipment_id ='" +
            shipmentNumber + "' ");
    alertDialogMsgOffline(ServiceActivity.this, "Failed", "Already Serviced", "Ok");
                        dialogLoading.dismiss();
                    }
else if(value.getRes_msg().equals("service upload failed")){
    database.execSQL("UPDATE serviceMaster set sync_status = 'E',image_status = 'C' where shipment_id ='" +
            shipmentNumber + "' ");
    alertDialogMsgOffline(ServiceActivity.this, "Failed", "Servicing Failed", "Ok");
    dialogLoading.dismiss();
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
                        Intent goToMain = new Intent(ServiceActivity.this, MainActivity.class);
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
                        Intent goToMain = new Intent(ServiceActivity.this, MainActivity.class);
                        startActivity(goToMain);
                        finish();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
        sweetAlertDialog.setCancelable(false);
    }

    public void signaturePad() {
        Intent i = new Intent(this, ServiceSignActivity.class);
        i.putExtra("shipment_num", shipmentNumber);
        startActivityForResult(i, 50);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


         if (requestCode == 50) {
            if (resultCode == Activity.RESULT_OK) {
                String imagePath = data.getStringExtra("imagePath");

                if (imagePath != null) {
                    String[] parts = String.valueOf(imagePath).split("/");
//                    Log.v("imagePath", imagePath);
                    String lastOne = parts[parts.length - 1];
                    mServiceConfirm.setSignProof(lastOne);
                    Log.v("requestCode","- "+ lastOne);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    tv_pls_sign_here.setVisibility(GONE);
                    iv_sign.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    bt_post_service.setEnabled(true);
                    bt_post_service.setAlpha(1);

                    if (!mServiceConfirm.getSignProof().equals("")) {
                        String deliveryDetailsupdate = "UPDATE serviceConfirmation set signProof = '" + mServiceConfirm.getSignProof() + "' where ship_num = '" + shipmentNumber + "' ";
                        database.execSQL(deliveryDetailsupdate);
                    }

                    new CountDownTimer(500, 1) {
                        public void onTick(long millisUntilFinished) {
//                            scroll.scrollTo(0, R.id.scroll);
                        }

                        public void onFinish() {
                        }
                    }.start();


                } else {
                    iv_sign.setImageResource(R.drawable.camera);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                signRoot.setBackgroundDrawable(R.drawable.camera);
//                iv_sign.setImageResource(R.drawable.camera);
                //Write your code if there's no result
            }

        }
    }

    public void uploadImage(String s) {
//        dialogLoading = new ProgressIndicatorActivity(ServiceActivity.this);
//        dialogLoading.showProgress();



            Cursor customerName = database.rawQuery("select SC.ship_num,SC.signProof from serviceMaster S  INNER JOIN serviceConfirmation SC on SC.ship_num = S.shipment_id  where S.sync_status='C' AND SC.ship_num = '" + s + "' ", null);

            if (customerName.getCount() > 0) {
                customerName.moveToFirst();
               String partial_shipAddress = customerName.getString(customerName.getColumnIndex("ship_num"));

                String file_signature = sign_path + customerName.getString(customerName.getColumnIndex("signProof"));
//                Log.v("delivery_proof", customerName.getString(customerName.getColumnIndex("signProof")));


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                InthreeApi apiService = retrofit.create(InthreeApi.class);

                DeliveryConfirmReq delivery = new DeliveryConfirmReq();
                JSONObject paramObject = null;


                /**** Get Signature Proof Image****/
                File fileSignProof = new File(file_signature);
                RequestBody requestBodySign = RequestBody.create(MediaType.parse("*/*"), fileSignProof);
                MultipartBody.Part fileToSign = MultipartBody.Part.createFormData("sign_file", fileSignProof.getName(), requestBodySign);

                final Observable<DeliveryConfirmResp> observable = apiService.getSignatureProof(fileToSign)
                        .subscribeOn
                                (Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                observable.subscribe(new Observer<DeliveryConfirmResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeliveryConfirmResp deliveryVal) {

                        List<DeliveryConfirmResp> orderVal = deliveryVal.getDelivery();

                        Log.v("DeliveryConfirmResp", deliveryVal.getRes_msg());

                        if (deliveryVal.getRes_msg().equals("image success")) {


                            uploadComplete(shipmentNumber);


                        } else if (deliveryVal.getRes_msg().equals("image failed")) {
//                            dialogLoading.dismiss();

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("upload_image_error", e.toString());
                        dialogLoading.dismiss();
                        alertDialogMsgOffline(ServiceActivity.this, "Success", getString(R.string.delivery_offline), "Ok");
                    }

                    @Override
                    public void onComplete() {
//                        dialogLoading.dismiss();
//                        Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
                    }


                });
            }



    }

    public void checkAlert(Context context, String title, String content, String okmsg) {
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

    public void editBoxAlert(final String ship, final String attri_id){

        final Dialog elAlertdialog = new Dialog(this);
        elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        elAlertdialog.setContentView(R.layout.service_textalert);
        elAlertdialog.show();

        final AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
        final AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
        final AppCompatEditText name = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
        final TextInputLayout txt_cust_name = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);

        Cursor getReturnValue = database.rawQuery("Select IFNULL(text_content,null) as text_content from serviceProductAttributes where shipment_no ='" + ship + "' AND attribute_id = " + attri_id + "  ", null);
        getReturnValue.moveToFirst();
        if (getReturnValue.getCount() > 0) {
            fetchVal = getReturnValue.getString(getReturnValue.getColumnIndex("text_content"));
            name.setText(fetchVal);
            editvalue = fetchVal;
        }
        getReturnValue.close();
//                Log.v("fetchVal","- "+fetchVal+" = "+ editvalue);

        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence qty, int i, int i1, int i2) {
                String text_val = qty.toString();
                if(text_val.equals("")){
                    text_val = null;
                }
                editvalue = text_val;
//                Log.v("fetchVal1", "- " + text_val);
//                        name.setText(text_val);
//                        db.execSQL("UPDATE serviceProductAttributes set text_content = '"+text_val+"' where shipment_no ='" +
//                                sqliteMod.getShipment_no() + "' AND attribute_id = "+sqliteMod.getAttribute_id()+" ");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

//                Log.v("editvalue", " = " + editvalue);
                if (editvalue == null || editvalue.equals("null")) {

                    txt_cust_name.setErrorEnabled(true);
                    txt_cust_name.setError("Enter comments");
                    txt_cust_name.requestFocus();
                } else {

                    database.execSQL("UPDATE serviceProductAttributes set text_content = '" + editvalue + "' where shipment_no ='" +
                            ship + "' AND attribute_id = " + attri_id + " ");
                    elAlertdialog.dismiss();
                    productAttribute(shipmentNumber);
                }
                insertServiceInfo();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                elAlertdialog.dismiss();

            }
        });

    }

    public void editBoxDocumentAlert(final String ship, final String attri_id){

        final Dialog elAlertdialog = new Dialog(this);
        elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        elAlertdialog.setContentView(R.layout.service_textalert);
        elAlertdialog.show();

        final AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
        final AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
        final AppCompatEditText name = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
        final TextInputLayout txt_cust_name = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);

        Cursor getReturnValue = database.rawQuery("Select IFNULL(text_content, null ) as text_content from serviceProductAttributes where shipment_no ='" + ship + "' AND attribute_id = " + attri_id + "  ", null);
        getReturnValue.moveToFirst();
        if (getReturnValue.getCount() > 0) {
            fetchVal = getReturnValue.getString(getReturnValue.getColumnIndex("text_content"));
            name.setText(fetchVal);
            editvalue = fetchVal;
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
                if(text_val.equals("")){
                    text_val = null;
                }
                editvalue = text_val;
//                Log.v("fetchVal", "- " + fetchVal);
//                        name.setText(text_val);
//                        db.execSQL("UPDATE serviceProductAttributes set text_content = '"+text_val+"' where shipment_no ='" +
//                                sqliteMod.getShipment_no() + "' AND attribute_id = "+sqliteMod.getAttribute_id()+" ");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

//                Log.v("editvalue", " = " + editvalue);
                if (editvalue == null || editvalue.equals("null")) {

                    txt_cust_name.setErrorEnabled(true);
                    txt_cust_name.setError("Enter comments");
                    txt_cust_name.requestFocus();
                } else {

                    database.execSQL("UPDATE serviceProductAttributes set text_content = '" + editvalue + "' where shipment_no ='" +
                            ship + "' AND attribute_id = " + attri_id + " ");
                    elAlertdialog.dismiss();
                    productDocumentAttribute(shipmentNumber);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                elAlertdialog.dismiss();

            }
        });

    }
}
