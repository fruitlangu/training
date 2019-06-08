package com.inthree.boon.deliveryapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.PartcialDeliveryAdapter;
import com.inthree.boon.deliveryapp.adapter.PickupPartialAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.model.ParcialShowModel;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PartialPickupProduct extends AppCompatActivity {

    private static final String DB_NAME = "boonboxdelivery.sqlite";
    SQLiteDatabase database;
    PickupPartialAdapter partcialDeliveryAdapter;
    ArrayAdapter my_Adapter;
    AlertDialog alertDialog1;
    Spinner sp_reason;
    AppCompatButton bt_back;
    AppCompatButton bt_submit;
    String str_other_reason = "";

    public ArrayList<ParcialShowModel> msqlitebeans = new ArrayList<>();

    RecyclerView part_rey_deliver;

    Context mContext;
    Activity activity;

    String ship_num;
    String order_num;

    Button btn_par_delivery;

    TextView tv_order_number;
    TextView tv_shipping_num;
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
    String order_type;
    int maxget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_product);

        part_rey_deliver = findViewById(R.id.part_del);
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

        btn_par_delivery = findViewById(R.id.btn_parcial_delivery);

        Intent partdelivery = getIntent();
        if (null != partdelivery) {
            order_num = partdelivery.getStringExtra(Constants.ORDER_ID);
            ship_num = partdelivery.getStringExtra("ship_num");
            order_type = partdelivery.getStringExtra("order_type");

        } else {
            order_num = "";
            ship_num = "";
        }

//        Log.v("ship_num1", ship_num);
        dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        GridLayoutManager glm = new GridLayoutManager(this, 1);
        part_rey_deliver.setLayoutManager(glm);
        partcialDeliveryAdapter = new PickupPartialAdapter(this, msqlitebeans, activity);

        part_rey_deliver.setAdapter(partcialDeliveryAdapter);

        database.execSQL("DELETE FROM DeliveryConfirmation where shipmentnumber='" + ship_num + "'");
        getOrderDetails(order_num);
        productshowdetails(ship_num);
        getTableValues();
        getqtymax();
        database.execSQL("UPDATE orderheader set delivery_status = 'partial' where Shipment_Number ='" +
                ship_num + "' ");
//        getsm(ship_num);
        btn_par_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             /*   Cursor getshowdetails = database.rawQuery("SELECT shipmentnumber,product_code,product_name,quantity,IFNULL(delivery_qty, 0) as delivery_qty ," +
                        "IFNULL(amount_collected, 0) as amount_collected , IFNULL(amount, 0) as amount, partial_reason FROM ProductDetails where shipmentnumber = '" + ship_num + "' ", null);
                getshowdetails.moveToFirst();
                if(getshowdetails.getCount() > 0){
                    while(!getshowdetails.isAfterLast()){
                        String qty = getshowdetails.getString(getshowdetails.getColumnIndex("quantity"));
                        String delivery_qty = getshowdetails.getString(getshowdetails.getColumnIndex("delivery_qty"));
                        String partial_reason = getshowdetails.getString(getshowdetails.getColumnIndex("partial_reason"));
                        if(Integer.parseInt(qty) > Integer.parseInt(delivery_qty) ){
                            if(partial_reason != null){
                                partial_bool = true;
                            }else{
                                partial_bool = false;
                            }
                        }
                    Log.v("getshowdetails1","- "+getshowdetails.getString(getshowdetails.getColumnIndex("partial_reason")));
                    Log.v("getshowdetails2","- "+getshowdetails.getString(getshowdetails.getColumnIndex("delivery_qty")));
                    Log.v("getshowdetails3","- "+getshowdetails.getString(getshowdetails.getColumnIndex("quantity")));
                        getshowdetails.moveToNext();
                    }
                }
                getshowdetails.close();*/


                boolean samp = getOne(((PickupPartialAdapter) partcialDeliveryAdapter).mSqliteBeanses);

//                partialReasonAlertBox();
                if(samp){
                    updateProductDetails(((PickupPartialAdapter) partcialDeliveryAdapter).mSqliteBeanses);
                }else{
                    Utils.AlertDialog(PartialPickupProduct.this, "Reasons Missing in Partial Delivered Products", getResources().getString(R.string.ok));
                }
//                updateProductDetails(((PartcialDeliveryAdapter) partcialDeliveryAdapter).mSqliteBeanses);
            }
        });
    }


    public void getOrderDetails(String ordernumber) {

        Cursor getOrders = database.rawQuery("Select * from orderheader where order_number = '" + ordernumber + "' ", null);

        if (getOrders.getCount() > 0) {
            getOrders.moveToFirst();
            tv_shipping_num.setText(getOrders.getString(getOrders.getColumnIndex("Shipment_Number")));
            ship_num = getOrders.getString(getOrders.getColumnIndex("Shipment_Number"));
            paymentMode = getOrders.getString(getOrders.getColumnIndex("payment_mode"));
            tv_order_number.setText(getOrders.getString(getOrders.getColumnIndex("order_number")));
//            Log.v("ship_num", ship_num);
        }
        getOrders.close();
    }


    public void productshowdetails(String shipnumber) {
        Cursor getshowdetails = database.rawQuery("SELECT shipmentnumber,product_code,product_name,quantity,IFNULL(delivery_qty, 0) as delivery_qty ," +
                "IFNULL(amount_collected, 0) as amount_collected , IFNULL(amount, 0) as amount,IFNULL(tamil,'') as tamil ,IFNULL(telugu,'') as telugu"  +
                ",IFNULL(punjabi,'') as punjabi,IFNULL(hindi,'') as hindi,IFNULL(bengali,'') as bengali,IFNULL(kannada,'') as kannada,IFNULL(assam,'') as assam" +
                ",IFNULL(orissa,'') as orissa ,IFNULL(marathi,'') as marathi, partial_reason FROM ProductDetails where shipmentnumber = '" + shipnumber + "' ", null);
        ParcialShowModel sqlitebeans_child;
        getshowdetails.moveToFirst();
        // msqlitebeans.clear();
        if (getshowdetails.getCount() > 0) {
            msqlitebeans.clear();
            while (!getshowdetails.isAfterLast()) {

                sqlitebeans_child = new ParcialShowModel();
//                Log.v("getparcial_count", String.valueOf(getshowdetails.getCount()));
                sqlitebeans_child.setpcode(getshowdetails.getString(getshowdetails.getColumnIndex("product_code")));
                sqlitebeans_child.setpname(getshowdetails.getString(getshowdetails.getColumnIndex("product_name")));
                sqlitebeans_child.setpqty(getshowdetails.getString(getshowdetails.getColumnIndex("quantity")));
                sqlitebeans_child.setpamt(getshowdetails.getString(getshowdetails.getColumnIndex("amount")));
//                sqlitebeans_child.setDeliveryQty(getshowdetails.getString(getshowdetails.getColumnIndex("delivery_qty")));
                sqlitebeans_child.setDeliveryQty(getshowdetails.getString(getshowdetails.getColumnIndex("quantity")));
                sqlitebeans_child.setPartial_reason(getshowdetails.getString(getshowdetails.getColumnIndex("partial_reason")));
                sqlitebeans_child.setShip_no(getshowdetails.getString(getshowdetails.getColumnIndex("shipmentnumber")));


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
               /* if(getshowdetails.getString(getshowdetails.getColumnIndex("delivery_qty")).equals("") ||
                        getshowdetails.getString(getshowdetails.getColumnIndex("delivery_qty"))== null ||
                        getshowdetails.getString(getshowdetails.getColumnIndex("delivery_qty")).equals("null")){
                    sqlitebeans_child.setDeliveryQty("0");
                }else{
                    sqlitebeans_child.setDeliveryQty(getshowdetails.getString(getshowdetails.getColumnIndex("delivery_qty")));
                }*/
               int tot_amt = Integer.parseInt(getshowdetails.getString(getshowdetails.getColumnIndex("quantity"))) * Integer.parseInt(getshowdetails.getString(getshowdetails.getColumnIndex("amount")));
                sqlitebeans_child.setTotal(String.valueOf(tot_amt));
//                               sqlitebeans_child.setTotal(getshowdetails.getString(getshowdetails.getColumnIndex("amount_collected")));

                Log.v("productshowdetails",getshowdetails.getString(getshowdetails.getColumnIndex("product_code")));
                msqlitebeans.add(sqlitebeans_child);
                getshowdetails.moveToNext();


            }
            partcialDeliveryAdapter = new PickupPartialAdapter(this, msqlitebeans, activity);
            part_rey_deliver.setAdapter(partcialDeliveryAdapter);
            partcialDeliveryAdapter.notifyDataSetChanged();
            part_rey_deliver.invalidate();
        } else {
            part_rey_deliver.setAdapter(null);
        }

        getshowdetails.close();
    }


    /**
     * Update the qty and amount collected in sqlite product table
     *
     * @param sqliteMod Get the array of product details model
     */
    public void updateProductDetails(List<ParcialShowModel> sqliteMod) {

        sumDelQty=0;
        sumQty=0;
        total=0;

        if (sqliteMod.size() > 0) {
            for (int i = 0; i < sqliteMod.size(); i++) {
                productShowModel = sqliteMod.get(i);
                sumQty = sumQty+Integer.parseInt(productShowModel.getpqty());
                Log.v("productShowModel",productShowModel.getpcode());

                if (productShowModel.getDeliveryQty() != null && !productShowModel.getDeliveryQty().isEmpty() &&
                        !productShowModel.getDeliveryQty().equalsIgnoreCase("") && !productShowModel
                        .getDeliveryQty().equalsIgnoreCase("null")) {
                    sumDelQty = sumDelQty+Integer.parseInt(productShowModel.getDeliveryQty());
//                    total = total+Integer.parseInt(productShowModel.getTotal());
                    try{

                        total= total+Integer.parseInt(productShowModel.getTotal());
                    }catch(NumberFormatException ex){ // handle your exception

                    }
                    alertValue=true;
                    String deliveryDetailsupdate = "UPDATE ProductDetails set delivery_qty = '" + productShowModel.getDeliveryQty() +
                            "',amount_collected='" + productShowModel.getTotal() + "' where " + "shipmentnumber " + "= '" + ship_num + "' AND " +
                            "product_code = '" + productShowModel.getpcode() + "' ";
                    database.execSQL(deliveryDetailsupdate);

                } else {
                    Utils.AlertDialog(this, getResources().getString(R.string.enter_qty), getResources().getString(R.string.ok));
                    alertValue=false;
                    break;
                }
            }



            /*Calculate the qty*/

            if(alertValue) {

                if (sumDelQty == sumQty) {
                    Utils.AlertDialog(this, getResources().getString(R.string.enter_qty_error), getResources().getString(R.string.ok));
                } else if (sumDelQty == 0) {
                    Utils.AlertDialog(this,  getResources().getString(R.string.enter_qtyno_error), getResources().getString(R.string.ok));
                } else {
//                    Log.v("getDeliveryQty", productShowModel.getDeliveryQty());
                    Cursor productCount = database.rawQuery("select delivery_qty,amount_collected from " +
                            "ProductDetails", null);
                    Logger.logInfo(String.valueOf(productCount.getCount()));
                    productCount.close();
                    sumtotal= String.valueOf(total);
                    Intent delivery = new Intent(PartialPickupProduct.this, DeliveryActivity.class);
                    delivery.putExtra(Constants.SHIPMENT_NUMBER, ship_num);
                    delivery.putExtra("partial", "partial");
                    delivery.putExtra("amountCollected", sumtotal);
                    delivery.putExtra("partial_reason", str_other_reason);
                    delivery.putExtra("order_type", order_type);
                    delivery.putExtra(Constants.ORDER_ID, order_num);
                    startActivity(delivery);
                }
            }


        }


    }


    public void getsm(String ship) {

        Cursor getOrders = database.rawQuery("Select * from orderheader where Shipment_Number = '" + ship + "' ", null);

        if (getOrders.getCount() > 0) {
            getOrders.moveToFirst();

//            Log.v("getdeliverydetails", getOrders.getString(getOrders.getColumnIndex("sync_status")));
        }
        getOrders.close();
    }

    public ArrayList<String> getTableValues() {
//        my_array.add("Select Reason");
        my_array.add(getString(R.string.select_reason_def));
        try {
//            Cursor getSchemeValue = database.rawQuery("select * from PartialReasonMaster  ", null);
            Cursor getSchemeValue = database.rawQuery("select * from ReasonMaster where reason_for = '1'  ", null);
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

    public void partialReasonAlertBox() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.partail_reason_alert, null);
        dialogBuilder.setView(dialogView);
        sp_reason = (Spinner) dialogView.findViewById(R.id.sp_reason);
        bt_back = (AppCompatButton) dialogView.findViewById(R.id.bt_back);
        bt_submit = (AppCompatButton) dialogView.findViewById(R.id.bt_submit);
        final AlertDialog alertDialog = dialogBuilder.create();


        dialogBuilder.setCancelable(false);

        my_Adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item,
                my_array);

//        my_Adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
//                my_array);
        sp_reason.setAdapter(my_Adapter);


        sp_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {


                String item = adapter.getItemAtPosition(position).toString();

                sp_reason.setSelection(position);
//                undeliverConfirm.setReason(item);
//                if(!item.equals("Select Reason")) {
                if(!item.equals(getString(R.string.select_reason_def))) {
                    str_other_reason = item;
                    bt_submit.setAlpha(1);
                    bt_submit.setEnabled(true);
//                    Log.v("Selected_Spinner", item);
                    if (item.equals("Others")) {
                        alertDialog.dismiss();
                        partialOtherReason();
                    }
                }else{
                    bt_submit.setAlpha(0.4F);
                    bt_submit.setEnabled(false);
                    Toast.makeText(getApplicationContext(),  getResources().getString(R.string.undeliver_Reason), Toast.LENGTH_LONG).show();
                }
//                Toast.makeText(getApplicationContext(),
//                        "Selected Spinner : " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

//        final AlertDialog alertDialog = dialogBuilder.create();
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                updateProductDetails(((PickupPartialAdapter) partcialDeliveryAdapter).mSqliteBeanses);
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

    public void partialOtherReason() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.other_partial_reason, null);
        dialogBuilder.setView(dialogView);
        final EditText et_other_reason;
        AppCompatButton close;
        final AppCompatButton proceed;
        et_other_reason = (EditText) dialogView.findViewById(R.id.et_other_reason);
        close = (AppCompatButton) dialogView.findViewById(R.id.close);
        proceed = (AppCompatButton) dialogView.findViewById(R.id.proceed);

        dialogBuilder.setCancelable(false);

        final AlertDialog alertDialog = dialogBuilder.create();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                str_other_reason = et_other_reason.getText().toString();
                if(!TextUtils.isEmpty(str_other_reason.trim())) {
                    updateProductDetails(((PickupPartialAdapter) partcialDeliveryAdapter).mSqliteBeanses);
                }else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.undeliver_Reason),
                            Toast.LENGTH_LONG);
                }

             /*   et_other_reason.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {}

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if(s.length() != 0)
                            Log.v("sample_text", (String) s);
                    }
                });*/
           /*     if (!TextUtils.isEmpty(str_other_reason.trim())){
                    et_other_reason.setEnabled(true);
                    proceed.setEnabled(true);
                    et_other_reason.setAlpha(1);
                    updateProductDetails(((PartcialDeliveryAdapter) partcialDeliveryAdapter).mSqliteBeanses);
                }else{
                    et_other_reason.setEnabled(false);
                    proceed.setEnabled(false);
                    et_other_reason.setAlpha(0.4F);
                }*/
//                updateProductDetails(((PartcialDeliveryAdapter) partcialDeliveryAdapter).mSqliteBeanses);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

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

    public boolean getOne(List<ParcialShowModel> sqliteMod){

        if (sqliteMod.size() > 0) {
            for (int i = 0; i < sqliteMod.size(); i++){
                productShowModel = sqliteMod.get(i);
                Cursor getshowdetails = database.rawQuery("SELECT shipmentnumber,product_code,product_name,quantity,IFNULL(delivery_qty, 0) as delivery_qty ," +
                        "IFNULL(amount_collected, 0) as amount_collected , IFNULL(amount, 0) as amount, partial_reason FROM ProductDetails where shipmentnumber = '" + productShowModel.getShip_no() + "' AND product_code = "+productShowModel.getpcode()+" ", null);
                getshowdetails.moveToFirst();
                if(getshowdetails.getCount() > 0){
//                    while(!getshowdetails.isAfterLast()){
                        String qty = productShowModel.getpqty();
                        String delivery_qty = productShowModel.getDeliveryQty();
                        String partial_reason = getshowdetails.getString(getshowdetails.getColumnIndex("partial_reason"));
                        Log.v("getOne","- "+ partial_reason);
                        if(Integer.parseInt(qty) > Integer.parseInt(delivery_qty) ){

                            if(partial_reason != null){

                                partial_bool = true;
//                                return true;
                            }else{
                                partial_bool = false;
                                return false;
                            }
                        }else{
                            partial_bool = true;
//                            return true;
                        }
//                        Log.v("getshowdetails1","- "+qty);
//                        Log.v("getshowdetails2","- "+delivery_qty);
//                        Log.v("getshowdetails3","- "+partial_reason);
//                        getshowdetails.moveToNext();
//                    }
                }
                getshowdetails.close();
            }
        }

return true;
    }

    @Override
    public void onBackPressed(){
        database.execSQL("UPDATE ProductDetails set partial_reason = null where shipmentnumber ='" +
                ship_num + "' ");
        finish();
    }

    public void getqtymax(){

        Cursor getqty = database.rawQuery("SELECT SUM(quantity)AS quantity FROM ProductDetails where shipmentnumber = '" + ship_num + "' ", null);
        if (getqty.getCount() > 0) {
            getqty.moveToFirst();
            maxget = getqty.getInt(getqty.getColumnIndex("quantity"));

        }
        getqty.close();

    }

}
