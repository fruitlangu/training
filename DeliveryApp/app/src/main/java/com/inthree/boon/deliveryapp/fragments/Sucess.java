package com.inthree.boon.deliveryapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class Sucess extends Fragment {

    PendingDeliveryAdapter successAdapter;
    public List<pendingModel> pendingArraylist;
    private ArrayList<pendingModel> msqlitebeans = new ArrayList<>();
    pendingModel item;
    Context mContext;
    Activity activity;

    RecyclerView rv_success_adapter;

    //************* Seacrh listview ************//
    EditText edit_search;

    SearchView ser_edit_sucess;

    private static final String DB_NAME = "boonboxdelivery.sqlite";
    SQLiteDatabase database;

    String edit;
    String user_language;
    Locale myLocale;
    private Context fragContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_sucess, container, false);
        rv_success_adapter = (RecyclerView) rootView.findViewById(R.id.rv_success_adapter);
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

        ser_edit_sucess = (SearchView)rootView.findViewById(R.id.sucess_search) ;

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getActivity(), DB_NAME);
        database = dbOpenHelper.openDataBase();
        getOrderHeader();
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1);

        rv_success_adapter.setLayoutManager(glm);
        populateSucessList();
        return rootView;
    }

    public void getOrderHeader(){
        Cursor cursor = database.rawQuery("select * from orderheader ", null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
//                Log.v("getOrderHeader", cursor.getString(cursor.getColumnIndex("sync_status")));
//                Log.v("getOrderHeader", cursor.getString(cursor.getColumnIndex("delivery_status")));
//                Log.v("getOrderHeader", cursor.getString(cursor.getColumnIndex("Shipment_Number")));
//                Log.v("getOrderHeader", cursor.getString(cursor.getColumnIndex("order_number")));
                cursor.moveToNext();
            }
        }else{

        }
        cursor.close();
    }

    public void populateSucessList(){

        Cursor cursor = database.rawQuery("select O.order_number, O.Shipment_Number,O.customer_name,O.sync_status,O.order_type,D.created_at,IFNULL(O.tamil,'') as tamil ,IFNULL(O.telugu,'') as telugu" +
                ",IFNULL(O.punjabi,'') as punjabi,IFNULL(O.hindi,'') as hindi,IFNULL(O.bengali,'') as bengali,IFNULL(O.kannada,'') as kannada,IFNULL(O.assam,'') as assam"  +
                ",IFNULL(O.orissa,'') as orissa ,IFNULL(O.marathi,'') as marathi from orderheader O LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where (O.sync_status = 'S' OR O.sync_status = " +
                "'C' OR O.sync_status = 'U' OR O.sync_status = 'E') AND O.delivery_status != 'undelivered'  ORDER BY D.created_at DESC  ", null);

       /* Cursor cursor = database.rawQuery("select O.order_number, O.Shipment_Number,O.customer_name,O.sync_status,O.order_type,D.created_at,IFNULL(O.tamil,'') as tamil ,IFNULL(O.telugu,'') as telugu" +
                ",IFNULL(O.punjabi,'') as punjabi,IFNULL(O.hindi,'') as hindi,IFNULL(O.bengali,'') as bengali,IFNULL(O.kannada,'') as kannada,IFNULL(O.assam,'') as assam"  +
                ",IFNULL(O.orissa,'') as orissa ,IFNULL(O.marathi,'') as marathi from orderheader O LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where (O.sync_status = 'S' OR O.sync_status = " +
                "'C' OR O.sync_status = 'U' OR O.sync_status = 'E') AND (O.delivery_status != 'undelivered' AND O.pickup_status != 'Failed')  ORDER BY D.created_at DESC  ", null);*/

        pendingModel sqlitebeans_child ;
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                sqlitebeans_child = new pendingModel();
//                Log.v("getOrderHeader_success", "-"+cursor.getString(cursor.getColumnIndex("created_at")));
                sqlitebeans_child.setOrderId(cursor.getString(cursor.getColumnIndex("order_number")));
                sqlitebeans_child.setShipId(cursor.getString(cursor.getColumnIndex("Shipment_Number")));
                sqlitebeans_child.setCustomerName(cursor.getString(cursor.getColumnIndex("customer_name")));
                sqlitebeans_child.setOrder_type(cursor.getInt(cursor.getColumnIndex("order_type")));

                sqlitebeans_child.setOrderpage("success");
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


                msqlitebeans.add(sqlitebeans_child);
                cursor.moveToNext();
                successAdapter = new PendingDeliveryAdapter(mContext, msqlitebeans, database, fragContext);
                rv_success_adapter.setAdapter(successAdapter);
                editsucesssearch();
                successAdapter.notifyDataSetChanged();
                rv_success_adapter.invalidate();
            }
            cursor.close();
        }else{
            rv_success_adapter.setAdapter(null);
        }
    }


    public  void editsucesssearch(){

        ser_edit_sucess.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                    populateSucessList();
                }else {
//                    Log.e("Serchview",query);
                    searchviewsucess (query);
                }
                return false;
            }
        });

    }


    public void searchviewsucess (String query){

        Cursor cursor = database.rawQuery("select O.order_number, O.Shipment_Number,O.customer_name,O.sync_status,O.order_type,D.created_at,IFNULL(O.tamil,'') as tamil ,IFNULL(O.telugu,'') as telugu" +
                ",IFNULL(O.punjabi,'') as punjabi,IFNULL(O.hindi,'') as hindi,IFNULL(O.bengali,'') as bengali,IFNULL(O.kannada,'') as kannada,IFNULL(O.assam,'') as assam"  +
                ",IFNULL(O.orissa,'') as orissa ,IFNULL(O.marathi,'') as marathi from orderheader O LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where (O.sync_status = 'S' OR O.sync_status = " +
                "'C' OR O.sync_status = 'U' OR O.sync_status = 'E') AND O.delivery_status != 'undelivered' AND (order_number LIKE '%"+ query +"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' )  ", null);

//        Cursor cursor = database.rawQuery("select order_number, customer_name,shipment_number,tamil,hindi,marathi,bengali,punjabi,orissa,kannada,telugu,assam from orderheader where (sync_status = 'S' OR sync_status = 'U' OR sync_status = 'C') AND (order_number LIKE '%"+ query +"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' )", null);
//        Cursor cursor = database.rawQuery("select order_number, customer_name,shipment_number from orderheader where sync_status = 'S' AND (order_number LIKE '%"+ query +"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' )", null);
//        Cursor cursor = database.rawQuery("select order_number, customer_name,shipment_number from orderheader where delivery_status = 'S' AND (order_number LIKE '%"+ query +"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' )", null);
        pendingModel sqlitebeans_child ;
        msqlitebeans.clear();
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                sqlitebeans_child = new pendingModel();
//                Log.v("getOrderHeader_count_s", String.valueOf(cursor.getCount()));
                sqlitebeans_child.setOrderId(cursor.getString(cursor.getColumnIndex("order_number")));
                sqlitebeans_child.setShipId(cursor.getString(cursor.getColumnIndex("Shipment_Number")));
                sqlitebeans_child.setCustomerName(cursor.getString(cursor.getColumnIndex("customer_name")));
                sqlitebeans_child.setOrder_type(cursor.getInt(cursor.getColumnIndex("order_type")));


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

                sqlitebeans_child.setOrderpage("success");
                msqlitebeans.add(sqlitebeans_child);
                cursor.moveToNext();

                successAdapter = new PendingDeliveryAdapter(mContext, msqlitebeans, database, fragContext );
                rv_success_adapter.setAdapter(successAdapter);
                successAdapter.notifyDataSetChanged();
                rv_success_adapter.invalidate();
            }
        }else{
            rv_success_adapter.setAdapter(null);
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