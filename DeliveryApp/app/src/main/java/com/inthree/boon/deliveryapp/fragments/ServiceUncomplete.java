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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.model.ServiceModel;
import com.inthree.boon.deliveryapp.adapter.ServiceDeliveryAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.pendingModel;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ServiceUncomplete extends Fragment {

    ServiceDeliveryAdapter pendingAdapter;
    public List<ServiceModel> pendingArraylist;
    private ArrayList<ServiceModel> msqlitebeans = new ArrayList<>();
    ServiceModel item;
    Context mContext;
    Activity activity;

    RecyclerView rv_pending_adapter;
    //************* Seacrh listview ************//
    EditText edit_search;

    SearchView ser_edit_pending;

    private static final String DB_NAME = "boonboxdelivery.sqlite";
    SQLiteDatabase database;

    String edit;
    String user_language;
    Locale myLocale;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_service_uncomplete, container, false);
        rv_pending_adapter = (RecyclerView) rootView.findViewById(R.id.rv_pending_adapter);
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
       // edit_search = (EditText)rootView.findViewById(R.id.txt_search) ;

        ser_edit_pending = (SearchView)rootView.findViewById(R.id.search) ;

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getActivity(), DB_NAME);
        database = dbOpenHelper.openDataBase();
      //  getOrderHeader();
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1);
//        getOrderHeader();
        rv_pending_adapter.setLayoutManager(glm);
        populatePendingList();
        return rootView;




    }


    public void getOrderHeader(){
        Cursor cursor = database.rawQuery("select * from serviceMaster ", null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Log.v("getOrderHeader_count", cursor.getString(cursor.getColumnIndex("sync_status")));

                cursor.moveToNext();
            }
        }else{

        }
    }

    public void populatePendingList( ){
//        Cursor pendinglist = database.rawQuery("select * from orderheader where sync_status = 'P' ", null);
//        Cursor pendinglist = database.rawQuery("select * from orderheader where sync_status = 'P' ORDER BY valid DESC ", null);
//        Cursor pendinglist = database.rawQuery("select * from orderheader where sync_status = 'P' ORDER BY valid ASC ", null);
        Cursor pendinglist = database.rawQuery("select * from serviceMaster S INNER JOIN ServiceIncompleteConfirmation SIC ON SIC.ship_no = S.shipment_id where (sync_status = 'U' OR sync_status = 'C') AND delivery_status = 'incomplete' ", null);

        ServiceModel sqlitebeans_child ;
        pendinglist.moveToFirst();
        if(pendinglist.getCount() > 0) {
            while (!pendinglist.isAfterLast()) {
                sqlitebeans_child = new ServiceModel();
//                Log.v("getOrderHeader_pending", pendinglist.getString(pendinglist.getColumnIndex("valid")));
                sqlitebeans_child.setOrderId(pendinglist.getString(pendinglist.getColumnIndex("order_id")));
                sqlitebeans_child.setShipId(pendinglist.getString(pendinglist.getColumnIndex("shipment_id")));
                sqlitebeans_child.setCustomerName(pendinglist.getString(pendinglist.getColumnIndex("customer_name")));
//                sqlitebeans_child.setOrderpage("pending");
                sqlitebeans_child.setOrderpage("Incomplete");

                pendinglist.moveToNext();
                msqlitebeans.add(sqlitebeans_child);
                pendingAdapter = new ServiceDeliveryAdapter(mContext, msqlitebeans, database, activity );
                rv_pending_adapter.setAdapter(pendingAdapter);
                editpendingsearch();
                pendingAdapter.notifyDataSetChanged();
                rv_pending_adapter.invalidate();
            }
        }else{
            rv_pending_adapter.setAdapter(null);
        }
    }



    public  void editpendingsearch(){

        ser_edit_pending.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                    populatePendingList();
                }else {
//                    Log.e("Serchview",query);
                    searchviewpending (query);
                }
                return false;
            }
        });

    }


    public void searchviewpending (String query){
        Cursor cursorpending = database.rawQuery("select order_id,shipment_id,customer_name  from serviceMaster S INNER JOIN ServiceIncompleteConfirmation SIC ON SIC.ship_no = S.shipment_id where (sync_status = 'U' OR sync_status = 'C') AND delivery_status = 'incomplete'  AND  (order_id LIKE '%"+ query +"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_id  LIKE '%"+ query +"%' ) ", null);
//        Cursor cursorpending = database.rawQuery("select order_number,Shipment_Number,customer_name,tamil,hindi,marathi,bengali,punjabi,orissa,kannada,telugu,assam from orderheader where sync_status = 'P'  AND  (order_number LIKE '%"+ query +"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' ) ", null);
        ServiceModel sqlitebeans_child ;
        msqlitebeans.clear();
        cursorpending.moveToFirst();
        if(cursorpending.getCount() > 0) {


            while (!cursorpending.isAfterLast()) {
                sqlitebeans_child = new ServiceModel();
//                Log.v("getOrderHeader_count_p", cursorpending.getString(cursorpending.getColumnIndex("customer_name")));
                sqlitebeans_child.setOrderId(cursorpending.getString(cursorpending.getColumnIndex("order_id")));
                sqlitebeans_child.setShipId(cursorpending.getString(cursorpending.getColumnIndex("shipment_id")));
                sqlitebeans_child.setCustomerName(cursorpending.getString(cursorpending.getColumnIndex("customer_name")));

                sqlitebeans_child.setOrderpage("Incomplete");
                msqlitebeans.add(sqlitebeans_child);
                cursorpending.moveToNext();



                pendingAdapter = new ServiceDeliveryAdapter(mContext, msqlitebeans, database, activity );
                rv_pending_adapter.setAdapter(pendingAdapter);
                pendingAdapter.notifyDataSetChanged();
                rv_pending_adapter.invalidate();
            }
        }else{
            rv_pending_adapter.setAdapter(null);
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
}