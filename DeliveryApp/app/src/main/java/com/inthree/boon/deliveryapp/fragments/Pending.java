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


public class Pending extends Fragment {

    PendingDeliveryAdapter pendingAdapter;
    public List<pendingModel> pendingArraylist;
    private ArrayList<pendingModel> msqlitebeans = new ArrayList<>();
    pendingModel item;
    Context mContext;
    Context fragContext;
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
        View rootView = inflater.inflate(R.layout.fragement_pending, container, false);
        rv_pending_adapter = (RecyclerView) rootView.findViewById(R.id.rv_pending_adapter);
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

        // edit_search = (EditText)rootView.findViewById(R.id.txt_search) ;

        ser_edit_pending = (SearchView) rootView.findViewById(R.id.search);

        mContext = getActivity();


        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(mContext, DB_NAME);
        database = dbOpenHelper.openDataBase();
        //  getOrderHeader();
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1);

        rv_pending_adapter.setLayoutManager(glm);
        populatePendingList();
        return rootView;


    }


    public void getOrderHeader() {
        Cursor cursor = database.rawQuery("select * from orderheader where delivery_status = 'P' ", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
//                Log.v("getOrderHeader_count", String.valueOf(cursor.getCount()));

                cursor.moveToNext();
            }
        } else {

        }
    }

    public void populatePendingList() {
//        Cursor pendinglist = database.rawQuery("select * from orderheader where sync_status = 'P' ", null);
//        Cursor pendinglist = database.rawQuery("select * from orderheader where sync_status = 'P' ORDER BY valid DESC ", null);
        Cursor pendinglist = database.rawQuery("select * from orderheader where sync_status = 'P' ORDER BY valid ASC ", null);

        pendingModel sqlitebeans_child;
        pendinglist.moveToFirst();
        if (pendinglist.getCount() > 0) {
            while (!pendinglist.isAfterLast()) {
                sqlitebeans_child = new pendingModel();
//                Log.v("getOrderHeader_pending", pendinglist.getString(pendinglist.getColumnIndex("valid")));
                sqlitebeans_child.setOrderId(pendinglist.getString(pendinglist.getColumnIndex("order_number")));
                sqlitebeans_child.setShipId(pendinglist.getString(pendinglist.getColumnIndex("Shipment_Number")));
                sqlitebeans_child.setCustomerName(pendinglist.getString(pendinglist.getColumnIndex("customer_name")));
                sqlitebeans_child.setOrder_type(pendinglist.getInt(pendinglist.getColumnIndex("order_type")));
                sqlitebeans_child.setOrderpage("pending");

                if (user_language.equals("tamil")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("tamil"));
//                     Log.v("user_language","- "+ language_json);
                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("telugu")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("telugu"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("punjabi")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("punjabi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("hindi")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("hindi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("bengali")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("bengali"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("kannada")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("kannada"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("assamese")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("assam"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("odia")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("orissa"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("marathi")) {
                    String language_json = pendinglist.getString(pendinglist.getColumnIndex("marathi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                }

                pendinglist.moveToNext();
                msqlitebeans.add(sqlitebeans_child);
                pendingAdapter = new PendingDeliveryAdapter(mContext, msqlitebeans, database, fragContext);
                rv_pending_adapter.setAdapter(pendingAdapter);
                editpendingsearch();
                pendingAdapter.notifyDataSetChanged();
                rv_pending_adapter.invalidate();
            }
            pendinglist.close();
        } else {
            rv_pending_adapter.setAdapter(null);
        }
    }


    public void editpendingsearch() {

        ser_edit_pending.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                msqlitebeans.clear();
                edit = query;
                if (query.equals("")) {
//                    Log.e("Serchview",query);
                    populatePendingList();
                } else {
//                    Log.e("Serchview",query);
                    searchviewpending(query);
                }
                return false;
            }
        });

    }


    public void searchviewpending(String query) {
        Cursor cursorpending = database.rawQuery("select order_number,Shipment_Number,customer_name,tamil,hindi,marathi,bengali,punjabi,orissa,kannada,telugu,assam,order_type from orderheader where sync_status = 'P'  AND  (order_number LIKE '%" + query + "%' OR customer_name  LIKE '%" + query + "%' OR shipment_number  LIKE '%" + query + "%' ) ", null);
//        Cursor cursorpending = database.rawQuery("select order_number,Shipment_Number,customer_name from orderheader where delivery_status = 'P'  AND  (order_number LIKE '%"+ query +"%' OR customer_name  LIKE '%"+ query +"%' OR shipment_number  LIKE '%"+ query +"%' ) ", null);
        pendingModel sqlitebeans_child;
        msqlitebeans.clear();
        cursorpending.moveToFirst();
        if (cursorpending.getCount() > 0) {


            while (!cursorpending.isAfterLast()) {
                sqlitebeans_child = new pendingModel();
//                Log.v("getOrderHeader_count_p", cursorpending.getString(cursorpending.getColumnIndex("customer_name")));
                sqlitebeans_child.setOrderId(cursorpending.getString(cursorpending.getColumnIndex("order_number")));
                sqlitebeans_child.setShipId(cursorpending.getString(cursorpending.getColumnIndex("Shipment_Number")));
                sqlitebeans_child.setCustomerName(cursorpending.getString(cursorpending.getColumnIndex("customer_name")));
                sqlitebeans_child.setOrder_type(cursorpending.getInt(cursorpending.getColumnIndex("order_type")));
                if (user_language.equals("tamil")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("tamil"));
                    Log.v("user_language", "- " + language_json);
                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("telugu")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("telugu"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("punjabi")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("punjabi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("hindi")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("hindi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("bengali")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("bengali"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("kannada")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("kannada"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("assamese")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("assam"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("odia")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("orissa"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                } else if (user_language.equals("marathi")) {
                    String language_json = cursorpending.getString(cursorpending.getColumnIndex("marathi"));

                    try {
                        JSONObject jObject = new JSONObject(language_json);
                        String customer_name = jObject.getString("customer_name");

                        sqlitebeans_child.setCustomerName(customer_name);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                }

                sqlitebeans_child.setOrderpage("pending");
                msqlitebeans.add(sqlitebeans_child);
                cursorpending.moveToNext();


                pendingAdapter = new PendingDeliveryAdapter(mContext, msqlitebeans, database, fragContext);
                rv_pending_adapter.setAdapter(pendingAdapter);
                pendingAdapter.notifyDataSetChanged();
                rv_pending_adapter.invalidate();
            }
        } else {
            rv_pending_adapter.setAdapter(null);
        }

    }

    private void setLocale(String lang) {

        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration cf = res.getConfiguration();
        cf.locale = myLocale;
        res.updateConfiguration(cf, dm);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragContext = context;
    }
}