package com.inthree.boon.deliveryapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.PendingDeliveryAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.pendingModel;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Failed extends Fragment {

    PendingDeliveryAdapter failureAdapter;
    public List<pendingModel> pendingArraylist;
    private ArrayList<pendingModel> msqlitebeans = new ArrayList<>();
    pendingModel item;
    Context mContext;
    Activity activity;

    RecyclerView rv_failure_adapter;

    SearchView ser_edit_failed;
    String edit;

    private static final String DB_NAME = "boonboxdelivery.sqlite";
    SQLiteDatabase database;
    String user_language;
    Locale myLocale;
    private Context fragContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_failed, container, false);
        rv_failure_adapter =(RecyclerView) rootView.findViewById(R.id.rv_failure_adapter);
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
        ser_edit_failed = (SearchView)rootView.findViewById(R.id.faild_search) ;

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getActivity(), DB_NAME);
        database = dbOpenHelper.openDataBase();
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1);

        rv_failure_adapter.setLayoutManager(glm);
        populateFailedList();
        return rootView;
    }

    public void populateFailedList(){

        Cursor cursor = database.rawQuery("select O.order_number, O.Shipment_Number,O.sync_status, O.customer_name,O.order_type,O.tamil ,O.telugu, O.punjabi,O. hindi, O.bengali ,O.kannada,O.assam,O.orissa,O.marathi from orderheader O LEFT JOIN UndeliveredConfirmation U on U.shipmentnumber = O.Shipment_Number where O.delivery_status = 'undelivered' AND (O.sync_status = 'C' OR O.sync_status = 'U' OR O.sync_status = 'E') " +
                "ORDER BY U.created_at DESC", null);


        pendingModel sqlitebeans_child ;
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                sqlitebeans_child = new pendingModel();
//                Log.v("getOrderHeader_count", String.valueOf(cursor.getCount()));
                sqlitebeans_child.setOrderId(cursor.getString(cursor.getColumnIndex("order_number")));
                sqlitebeans_child.setShipId(cursor.getString(cursor.getColumnIndex("Shipment_Number")));
                sqlitebeans_child.setCustomerName(cursor.getString(cursor.getColumnIndex("customer_name")));
                sqlitebeans_child.setOrder_type(cursor.getInt(cursor.getColumnIndex("order_type")));
                sqlitebeans_child.setOrderpage("failed");

                if(cursor.getString(cursor.getColumnIndex("sync_status")).equals("C")){
                    sqlitebeans_child.setOffline(true);
                }else if(cursor.getString(cursor.getColumnIndex("sync_status")).equals("E")){
                    sqlitebeans_child.setOffline(true);

                }else{
                    sqlitebeans_child.setOffline(false);
                }

                if(user_language.equals("tamil")){
                    String language_json = cursor.getString(cursor.getColumnIndex("tamil"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                } else if(user_language.equals("telugu")){
                    String language_json = cursor.getString(cursor.getColumnIndex("telugu"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                } else if(user_language.equals("punjabi")){
                    String language_json = cursor.getString(cursor.getColumnIndex("punjabi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("hindi")){
                    String language_json = cursor.getString(cursor.getColumnIndex("hindi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("bengali")){
                    String language_json = cursor.getString(cursor.getColumnIndex("bengali"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("kannada")){
                    String language_json = cursor.getString(cursor.getColumnIndex("kannada"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("assamese")){
                    String language_json = cursor.getString(cursor.getColumnIndex("assam"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("odia")){
                    String language_json = cursor.getString(cursor.getColumnIndex("orissa"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("marathi")){
                    String language_json = cursor.getString(cursor.getColumnIndex("marathi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }

                cursor.moveToNext();
                msqlitebeans.add(sqlitebeans_child);
                failureAdapter = new PendingDeliveryAdapter(mContext, msqlitebeans, database, fragContext);
                rv_failure_adapter.setAdapter(failureAdapter);
                editfailedsearch();
                failureAdapter.notifyDataSetChanged();
                rv_failure_adapter.invalidate();
            }
            cursor.close();
        }else{
            rv_failure_adapter.setAdapter(null);
        }
    }

    public  void editfailedsearch(){

        ser_edit_failed.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                msqlitebeans.clear();
                edit = query;

                if(query.equals("")){
//                    Log.e("Serchview",query);
                    populateFailedList();
                }else {
//                    Log.e("Serchview",query);
                    searchviewfailed (query);
                }
                return false;
            }
        });

    }


    public void searchviewfailed (String query){

        Cursor cursor = database.rawQuery("select O.order_number, O.Shipment_Number, O.customer_name,O.order_type,O.tamil ,O.telugu, O.punjabi,O. hindi, O.bengali ,O.kannada,O.assam,O.orissa,O.marathi from orderheader O LEFT JOIN UndeliveredConfirmation U on U.shipmentnumber = O.Shipment_Number where O.delivery_status = 'undelivered' AND (O.sync_status = 'C' OR O.sync_status = 'U' OR O.sync_status = 'E') AND( order_number LIKE '%"+ query+"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' ) ", null);
//        Cursor cursor = database.rawQuery("select order_number, customer_name,shipment_number,tamil,hindi,marathi,bengali,punjabi,orissa,kannada,telugu,assam from orderheader where delivery_status = 'undelivered' AND (sync_status = 'C' OR sync_status = 'U') AND( order_number LIKE '%"+ query+"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' ) ", null);
//        Cursor cursor = database.rawQuery("select order_number, customer_name,shipment_number from orderheader where delivery_status = 'F' AND( order_number LIKE '%"+ query+"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' ) ", null);
//        Cursor cursor = database.rawQuery("select order_number, customer_name,shipment_number from orderheader where delivery_status = 'F' AND( order_number LIKE '%"+ query+"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' ) ", null);
        pendingModel sqlitebeans_child ;
        msqlitebeans.clear();
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                sqlitebeans_child = new pendingModel();
//                Log.v("getOrderHeader_count_f", String.valueOf(cursor.getString(cursor.getColumnIndex("order_number"))));
                sqlitebeans_child.setOrderId(cursor.getString(cursor.getColumnIndex("order_number")));
                sqlitebeans_child.setShipId(cursor.getString(cursor.getColumnIndex("Shipment_Number")));
                sqlitebeans_child.setCustomerName(cursor.getString(cursor.getColumnIndex("customer_name")));
                sqlitebeans_child.setOrder_type(cursor.getInt(cursor.getColumnIndex("order_type")));
                sqlitebeans_child.setOrderpage("failed");

                if(user_language.equals("tamil")){
                    String language_json = cursor.getString(cursor.getColumnIndex("tamil"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                } else if(user_language.equals("telugu")){
                    String language_json = cursor.getString(cursor.getColumnIndex("telugu"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                } else if(user_language.equals("punjabi")){
                    String language_json = cursor.getString(cursor.getColumnIndex("punjabi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("hindi")){
                    String language_json = cursor.getString(cursor.getColumnIndex("hindi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("bengali")){
                    String language_json = cursor.getString(cursor.getColumnIndex("bengali"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("kannada")){
                    String language_json = cursor.getString(cursor.getColumnIndex("kannada"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("assamese")){
                    String language_json = cursor.getString(cursor.getColumnIndex("assam"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("odia")){
                    String language_json = cursor.getString(cursor.getColumnIndex("orissa"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }else if(user_language.equals("marathi")){
                    String language_json = cursor.getString(cursor.getColumnIndex("marathi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    }catch(JSONException e){
                        e.getStackTrace();
                    }
                }

                msqlitebeans.add(sqlitebeans_child);
                cursor.moveToNext();
                failureAdapter = new PendingDeliveryAdapter(mContext, msqlitebeans, database, fragContext );
                rv_failure_adapter.setAdapter(failureAdapter);
                failureAdapter.notifyDataSetChanged();
                rv_failure_adapter.invalidate();
            }
        }else{
              rv_failure_adapter.setAdapter(null);
        }
    }

    private void setLocale(String lang){

        myLocale =new Locale(lang);
        Resources res=getResources();
        DisplayMetrics dm=res.getDisplayMetrics();
        Configuration cf =res.getConfiguration();
        cf.locale=myLocale;
        res.updateConfiguration(cf, dm);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragContext = context;
    }

}