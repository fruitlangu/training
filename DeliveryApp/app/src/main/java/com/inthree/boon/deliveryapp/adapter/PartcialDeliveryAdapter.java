package com.inthree.boon.deliveryapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.activity.OrderDetails;
import com.inthree.boon.deliveryapp.activity.PartialDelivery;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.model.ParcialShowModel;
import com.inthree.boon.deliveryapp.model.ProductShowModel;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karthika on 12-Jan-18.
 */

public class PartcialDeliveryAdapter extends RecyclerView.Adapter<PartcialDeliveryAdapter.MyViewHolder> {


    public List<ParcialShowModel> mSqliteBeanses = null;
    private List<ParcialShowModel> filteredList;
    Context context;
    //    Context ctx;

    Activity activity;

    //    Context mContext;
    SQLiteDatabase db;


    /**
     * Intiliaze the database
     */
    private SQLiteDatabase database;
    //ProductShowModel sqliteMod;
    ArrayList<String> my_array = new ArrayList<String>();
    String user_language;
    ArrayAdapter my_Adapter;
    String editvalue = "";
    String fetchVal = "";
    String rid;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pa_prod_code;
        TextView pa_prod_name;
        TextView pa_prod_qty;
        TextView pa_prod_amt;
        TextView pa_amt_collet;
        TextView pa_total_amt;
        EditText pa_etr_qty;
        Spinner sp_select_reason;
        LinearLayout ll_select_reason;

        public ParcialShowModel sqliteMod;




        public MyViewHolder(View itemView) {
            super(itemView);
            pa_prod_code = itemView.findViewById(R.id.pa_product_code);
            pa_prod_name = itemView.findViewById(R.id.pa_product_name);
            pa_prod_qty = itemView.findViewById(R.id.pa_product_qty);
            pa_prod_amt = itemView.findViewById(R.id.pa_product_amt);
            pa_amt_collet = itemView.findViewById(R.id.pa_cal);
            pa_total_amt = itemView.findViewById(R.id.pa_total);
            pa_etr_qty = itemView.findViewById(R.id.pa_etr_qty);
            sp_select_reason = itemView.findViewById(R.id.sp_select_reason);
            ll_select_reason = itemView.findViewById(R.id.ll_select_reason);
//            getTableValues();
            qtyTextChanged(this);
//            spinnerListener(this);
        }

    }

    public PartcialDeliveryAdapter(Context context, List<ParcialShowModel> mSqliteBeanses, Activity activity) {
        this.context = context;
//        this.mSqliteBeanses = new ArrayList<>(mSqliteBeanses);
        this.mSqliteBeanses = mSqliteBeanses;
       // this.db = db;
        this.activity = activity;
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this.context, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();
        //     mFilter = new CustomFilter(PendingDeliveryAdapter.this);
        user_language = AppController.getStringPreference(Constants.USER_LANGUAGE,"");
        getTableValues();

    }


    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        context = parent.getContext();
        final View itemView =
                LayoutInflater.from(parent.  getContext()).inflate(R.layout.activity_partial_adapter, parent, false);

//        return new schemeViewAdapter.MyViewHolder(itemView);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }


    @Override

    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.setIsRecyclable(true);
//       final ParcialShowModel sqliteMod = mSqliteBeanses.get(position);
       holder.sqliteMod = mSqliteBeanses.get(position);

//        my_Adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item,my_array);
        /*my_Adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1,my_array);
        holder.sp_select_reason.setAdapter(my_Adapter);*/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, my_array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.sp_select_reason.setAdapter(dataAdapter);
    /*    holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           Log.v("onBindViewHolder", holder.sqliteMod.getDeliveryQty()+" -- "+  holder.sqliteMod.getpqty());
            }
        });*/

        holder.pa_prod_code.setText(context.getResources().getString(R.string.ord_product_code) + holder.sqliteMod.getpcode());
        holder.ll_select_reason.setVisibility(View.GONE);
        holder.pa_prod_name.setText( holder.sqliteMod.getpname());
        holder.pa_prod_qty.setText( holder.sqliteMod.getpqty());
        holder.pa_prod_amt.setText("₹ "+ holder.sqliteMod.getpamt());
        holder.pa_etr_qty.setText( holder.sqliteMod.getDeliveryQty());
        holder.pa_total_amt.setText("₹ "+  holder.sqliteMod.getTotal());


        holder.pa_prod_name.setText(holder.sqliteMod.getpname());
        holder.pa_prod_qty.setText(holder.sqliteMod.getpqty());
        holder.pa_prod_amt.setText("₹ "+holder.sqliteMod.getpamt());
        holder.pa_etr_qty.setText(holder.sqliteMod.getDeliveryQty());
        holder.pa_total_amt.setText("₹ "+ holder.sqliteMod.getTotal());
//        int spin_pos = dataAdapter.getPosition(my_array);
        if(holder.sqliteMod.getPartial_reason() != null && !holder.sqliteMod.getPartial_reason().equals("Others")) {
           int spin_pos = dataAdapter.getPosition(holder.sqliteMod.getPartial_reason());
            holder.sp_select_reason.setSelection(spin_pos);
        }else if(holder.sqliteMod.getPartial_reason() != null && holder.sqliteMod.getPartial_reason().equals("Others")){
            int spin_pos = dataAdapter.getPosition("Others");
            holder.sp_select_reason.setSelection(spin_pos);
            holder.sqliteMod.setOther_partial_reason(true);


        }
        Log.v("onBindViewHolder"," - "+holder.sqliteMod.getPartial_reason());


        holder.sp_select_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                final String item = parent.getItemAtPosition(position).toString();
                Log.v("getItemAtPosition"," - "+item);
                if (!item.equals("Select Reason") && !item.equals("Others")) {
                    String str_lang = item;
                    holder.sqliteMod.setOther_partial_reason(false);
                    String deliveryDetailsupdate = "UPDATE ProductDetails set partial_reason = '" +  str_lang+ "' where " + "shipmentnumber " + "= '" + holder.sqliteMod.getShip_no() + "' AND " +
                            "product_code = '" + holder.sqliteMod.getpcode() + "' ";
                    database.execSQL(deliveryDetailsupdate);
                    holder.sqliteMod.setPartial_reason(str_lang);
//                    ((PartialDelivery) context).productshowdetails(holder.sqliteMod.getShip_no());



                    Cursor getreasondetails = database.rawQuery("SELECT * FROM ReasonMaster where reason = '"+str_lang+"' AND reason_for = 1  ", null);
                    getreasondetails.moveToFirst();
                    if(getreasondetails.getCount() > 0){
                        rid = getreasondetails.getString(getreasondetails.getColumnIndex("rid"));
                        Log.v("getreasondetails","- "+ rid);
                    }

                    holder.sqliteMod.setOther_partial_reason(false);

//                    ((PartialDelivery) context).productshowdetails(holder.sqliteMod.getShip_no());

//                } else if(item.equals("Others")){
                } else if(item.equals("Others") && holder.sqliteMod.isOther_partial_reason() == false){
//                    Log.v("getItemAtPosition1","- "+ item);
                    holder.sqliteMod.setPartial_reason(item);

                    final Dialog elAlertdialog = new Dialog(context);
                    elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    elAlertdialog.setContentView(R.layout.partial_textalert);
                    elAlertdialog.show();

                    AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
                    AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
                    final AppCompatEditText name = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
                    final TextInputLayout txt_cust_name = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);

                  /*  Cursor getReturnValue = database.rawQuery("Select IFNULL(partial_reason, null ) as partial_reason,IFNULL(other_partial_reason, null ) as other_partial_reason from ProductDetails where shipmentnumber ='" + holder.sqliteMod.getShip_no() + "' AND product_code = " + holder.sqliteMod.getpcode() + "  ", null);
                    getReturnValue.moveToFirst();
                    if (getReturnValue.getCount() > 0) {
                        fetchVal = getReturnValue.getString(getReturnValue.getColumnIndex("other_partial_reason"));
                        name.setText(fetchVal);
                        editvalue = fetchVal;
                    }
                    getReturnValue.close();*/

                    name.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence qty, int i, int i1, int i2) {
                            String text_val = qty.toString();
                            editvalue = text_val;
//                            Log.v("fetchVal","- "+fetchVal);
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

                            Log.v("editvalue"," = "+ editvalue);
                            if ( editvalue == null || editvalue.equals("null") || editvalue.equals("")) {

                                txt_cust_name.setErrorEnabled(true);
                                txt_cust_name.setError("Enter Reason");
                                txt_cust_name.requestFocus();
                            }else {

                                database.execSQL("UPDATE ProductDetails set partial_reason = '" + editvalue + "' where shipmentnumber ='" +holder.sqliteMod.getShip_no() + "' AND product_code = "+holder.sqliteMod.getpcode()+" AND pickup_type = 0 ");
                                elAlertdialog.dismiss();
                            }
                        }
                    });

                    back.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View arg0) {
                            elAlertdialog.dismiss();

                        }
                    });
                }else {

//                    Toast.makeText(context, "Please Select a Valid Reason", Toast.LENGTH_LONG).show();
//                    String deliveryDetailsupdate = "UPDATE ProductDetails set partial_reason = null where " + "shipmentnumber " + "= '" + holder.sqliteMod.getShip_no() + "' AND " +
//                            "product_code = '" + holder.sqliteMod.getpcode() + "' ";
//                    database.execSQL(deliveryDetailsupdate);

                    Cursor getReturnValue = database.rawQuery("Select IFNULL(partial_reason, null ) as partial_reason,IFNULL(other_partial_reason, null ) as other_partial_reason from ProductDetails where shipmentnumber ='" + holder.sqliteMod.getShip_no() + "' AND product_code = " + holder.sqliteMod.getpcode() + " AND pickup_type = 0  ", null);
                    getReturnValue.moveToFirst();
                    if (getReturnValue.getCount() > 0) {
                        String one = getReturnValue.getString(getReturnValue.getColumnIndex("partial_reason"));
                       Log.v("getReturnValue"," - "+ one);

                    }else{
                        Toast.makeText(context, "Please Select a Valid Reason", Toast.LENGTH_LONG).show();
                    }
                    getReturnValue.close();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.v("onNothingSelected","- "+ "qwerty");
            }
        });
//        Log.v("onBindViewHolder","- "+ holder.sqliteMod.getPartial_reason());

        /*holder.pa_etr_qty.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence qty, int i, int i1, int i2) {


                if(qty.equals("")){
                    holder.pa_total_amt.setText("0");
                }else {
                    String etrqty = qty.toString();

                        if (etrqty.equals("")) {
                            holder.pa_total_amt.setText("");
                           *//* holder.pa_amt_collet.setText("");*//*
                            sqliteMod.setDeliveryQty("0");
//                            sqliteMod.setTotal("");
                            sqliteMod.setTotal("0");
                            holder.pa_total_amt.setText("₹ "+ sqliteMod.getTotal());
                        } else {
                            if (Integer.parseInt(sqliteMod.getpqty().toString())>= Integer.parseInt(etrqty)) {
                                int input = Integer.parseInt(etrqty);
                                int rate = Integer.parseInt(sqliteMod.getpamt().toString());
                                int total = rate * input;

                                holder.pa_total_amt.setText("₹ "+ Integer.toString(total));
                              *//*  holder.pa_amt_collet.setText(etrqty + "*" + sqliteMod.getpamt().toString());*//*



                                int totals=Integer.parseInt(etrqty ) * Integer.parseInt( sqliteMod.getpamt().toString());
                                String amountCollected= String.valueOf(totals);

                                if(!etrqty.equalsIgnoreCase(""))
                                    sqliteMod.setDeliveryQty(etrqty);

                                sqliteMod.setTotal(amountCollected);

//                                ((PartialDelivery)context).updateProductDetails(mSqliteBeanses);
                            }else{
                                holder.pa_etr_qty.setText("");
//                                holder.pa_etr_qty.setText("0");
                                Toast.makeText(context, context.getResources().getString(R.string.entervalid), Toast.LENGTH_SHORT).show();
                            }
                        }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

    }

    public void qtyTextChanged(final PartcialDeliveryAdapter.MyViewHolder holder) {
        holder.pa_etr_qty.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence qty, int i, int i1, int i2) {


                if(qty.equals("")){
                    holder.pa_total_amt.setText("0");
                }else {
                    String etrqty = qty.toString();

                    if (etrqty.equals("")) {
                        holder.pa_total_amt.setText("");
                        /* holder.pa_amt_collet.setText("");*/
                        holder.sqliteMod.setDeliveryQty("0");
//                            sqliteMod.setTotal("");

                        holder.sqliteMod.setTotal("0");
                        holder.pa_total_amt.setText("₹ "+  holder.sqliteMod.getTotal());
                        holder.ll_select_reason.setVisibility(View.VISIBLE);

                    } else {

                        if(Integer.parseInt(holder.sqliteMod.getpqty()) > Integer.parseInt(etrqty)){
                            holder.ll_select_reason.setVisibility(View.VISIBLE);
                        }else{
                            holder.ll_select_reason.setVisibility(View.GONE);
                            String deliveryDetailsupdate = "UPDATE ProductDetails set partial_reason = null where " + "shipmentnumber " + "= '" + holder.sqliteMod.getShip_no() + "' AND " +
                                    "product_code = '" + holder.sqliteMod.getpcode() + "' AND pickup_type = 0 ";
                            database.execSQL(deliveryDetailsupdate);


                        }

                        if (Integer.parseInt( holder.sqliteMod.getpqty().toString())>= Integer.parseInt(etrqty)) {

//                            int input = Integer.parseInt(etrqty);
                            double input = Double.parseDouble(etrqty);

//                            int rate = Integer.parseInt( holder.sqliteMod.getpamt().toString());
                            double rate = Double.parseDouble( holder.sqliteMod.getpamt().toString());

//                            int total = rate * input;
                            double total = rate * input;

//                            holder.pa_total_amt.setText("₹ "+ Integer.toString(total));
                            holder.pa_total_amt.setText("₹ "+ Double.toString(total));

                            int totals=Integer.parseInt(etrqty ) * Integer.parseInt(  holder.sqliteMod.getpamt().toString());

                            String amountCollected= String.valueOf(totals);

                            if(!etrqty.equalsIgnoreCase(""))
                                holder.sqliteMod.setDeliveryQty(etrqty);

                            holder.sqliteMod.setTotal(amountCollected);

//                                ((PartialDelivery)context).updateProductDetails(mSqliteBeanses);
                        }else{
                            holder.pa_etr_qty.setText("");
//                                holder.pa_etr_qty.setText("0");
                            Toast.makeText(context, context.getResources().getString(R.string.entervalid), Toast.LENGTH_SHORT).show();
                            holder.ll_select_reason.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mSqliteBeanses.size();
    }

    public void spinnerListener(final PartcialDeliveryAdapter.MyViewHolder holder) {
        holder.sp_select_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String item = parent.getItemAtPosition(position).toString();
                Log.v("getItemAtPosition"," - "+item);
                if (!item.equals("Select Reason") && !item.equals("Others")) {
                   String str_lang = item;


                    String deliveryDetailsupdate = "UPDATE ProductDetails set partial_reason = '" +  str_lang+ "' where " + "shipmentnumber " + "= '" + holder.sqliteMod.getShip_no() + "' AND " +
                            "product_code = '" + holder.sqliteMod.getpcode() + "' AND pickup_type = 0 ";
                    database.execSQL(deliveryDetailsupdate);
                    holder.sqliteMod.setPartial_reason(str_lang);
//                    ((PartialDelivery) context).productshowdetails(holder.sqliteMod.getShip_no());

                    Cursor getreasondetails = database.rawQuery("SELECT * FROM ReasonMaster where reason = '"+str_lang+"' AND reason_for = 1  ", null);
                    getreasondetails.moveToFirst();
                    if(getreasondetails.getCount() > 0){
                        rid = getreasondetails.getString(getreasondetails.getColumnIndex("rid"));
                        Log.v("getreasondetails","- "+ rid);
                    }


                    holder.sqliteMod.setOther_partial_reason(false);


//                } else if(item.equals("Others")){
                } else if(item.equals("Others")){
//                    Log.v("getItemAtPosition1","- "+ item);
                    holder.sqliteMod.setPartial_reason(item);
                    final Dialog elAlertdialog = new Dialog(context);
                    elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    elAlertdialog.setContentView(R.layout.partial_textalert);
                    elAlertdialog.show();

                    AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
                    AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
                    final AppCompatEditText name = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
                    final TextInputLayout txt_cust_name = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);

                    name.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence qty, int i, int i1, int i2) {
                            String text_val = qty.toString();
                            editvalue = text_val;
//                            Log.v("fetchVal","- "+fetchVal);
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

                            Log.v("editvalue"," = "+ editvalue);
                            if ( editvalue == null || editvalue.equals("null") || editvalue.equals("")) {

                                txt_cust_name.setErrorEnabled(true);
                                txt_cust_name.setError("Enter Reason");
                                txt_cust_name.requestFocus();
                            }else {

                                database.execSQL("UPDATE ProductDetails set partial_reason = '" + editvalue + "' where shipmentnumber ='" +holder.sqliteMod.getShip_no() + "' AND product_code = "+holder.sqliteMod.getpcode()+" ");
                                elAlertdialog.dismiss();
                            }
                        }
                    });

                    back.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View arg0) {
                            elAlertdialog.dismiss();

                        }
                    });
                }else {

                    Toast.makeText(context, "Please Select a Valid Reason", Toast.LENGTH_LONG).show();
                    String deliveryDetailsupdate = "UPDATE ProductDetails set partial_reason = null where " + "shipmentnumber " + "= '" + holder.sqliteMod.getShip_no() + "' AND " +
                            "product_code = '" + holder.sqliteMod.getpcode() + "' ";
                    database.execSQL(deliveryDetailsupdate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.v("getTableValues","nothing");
            }
        });
    }

    public ArrayList<String> getTableValues() {
        Log.v("getTableValues","- ");
//        my_array.add("Select Reason");
        my_array.add("Select Reason");
        try {
//            Cursor getSchemeValue = database.rawQuery("select * from UndeliveredReasonMaster  ", null);
            Cursor getSchemeValue = database.rawQuery("select * from ReasonMaster where reason_for = '1'  ", null);
//            System.out.println("COUNT : " + getSchemeValue.getCount());
//            Log.v("getTableValues"," - "+getSchemeValue.getCount());
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
                        Log.v("kannada","-- "+language_json);
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
//            Toast.makeText(getApplicationContext(), "Error encountered.",
//                    Toast.LENGTH_LONG);
        }
        return my_array;
    }



}
