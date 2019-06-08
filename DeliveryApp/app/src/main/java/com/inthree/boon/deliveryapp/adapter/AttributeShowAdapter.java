package com.inthree.boon.deliveryapp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.activity.ServiceActivity;
import com.inthree.boon.deliveryapp.activity.UndeliveryActivity;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.model.AttributeModel;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class AttributeShowAdapter extends RecyclerView.Adapter<AttributeShowAdapter.MyViewHolder> {


    public List<AttributeModel> mSqliteBeanses= null;
    private List<AttributeModel> filteredList;
    Context context;
    //    Context ctx;

    Activity activity;

    //    Context mContext;
    SQLiteDatabase db;

    String delivery_status;
    String editvalue = "";
    String fetchVal = "";


    public class MyViewHolder extends RecyclerView.ViewHolder  {

        TextView tv_attribute;
        TextView et_enterval;
        ImageView ivCheckBox;
        CheckBox checked_text_view;
//        public AttributeModel sqliteMod;
        public MyViewHolder(View itemView) {
            super(itemView);
//            p_prod_code = (TextView)  itemView.findViewById(R.id.ps_product_code);
//            p_prod_name = (TextView)  itemView.findViewById(R.id.ps_product_name);
//            p_prod_qty = (TextView)  itemView.findViewById(R.id.ps_product_qty);


            tv_attribute = (TextView) itemView.findViewById(R.id.tv_attribute);
            ivCheckBox = (ImageView) itemView.findViewById(R.id.iv_check_box);
            et_enterval  = (TextView) itemView.findViewById(R.id.et_enterval);
            checked_text_view = (CheckBox) itemView.findViewById(R.id.checked_text_view);
//            qtyTextChanged(this);
        }


    }

    public AttributeShowAdapter(Context context, List<AttributeModel> mSqliteBeanses, SQLiteDatabase db) {
        this.context = context;
        this.mSqliteBeanses = mSqliteBeanses;
        this.db = db;
    }


    @Override
    public AttributeShowAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        context =  parent.getContext();
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_attribute_adapter, parent, false);

        AttributeShowAdapter.MyViewHolder holder = new AttributeShowAdapter.MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AttributeShowAdapter.MyViewHolder holder, int position) {
       final AttributeModel sqliteMod = mSqliteBeanses.get(position);

        holder.tv_attribute.setText(sqliteMod.getAttribute_name());
        holder.ivCheckBox.setVisibility(View.GONE);

        if (sqliteMod.isSelected()) {
            holder.checked_text_view.setChecked(true);
        } else {
            holder.checked_text_view.setChecked(false);
        }
        Log.v("checked_text_view", "-- " + sqliteMod.getAttribute_name() + "- " + sqliteMod.isSelected());
        if (sqliteMod.isSelected()) {
            sqliteMod.setSelected(true);
        } else {
            sqliteMod.setSelected(false);
        }
       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
Log.v("setOnClickListener","-"+ sqliteMod.getAttribute_name());

            }
        });*/
        holder.checked_text_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                @Override
                                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            Log.v("onCheckedChanged", String.valueOf(isChecked));
                                                                    Log.v("onCheckedChanged", "-- " + sqliteMod.getShipment_no() + " - " + sqliteMod.getAttribute_name());
                                                                    int checkFlag = 0;
                                                                    if (isChecked) {
                                                                        checkFlag = 1;
                                                                    } else {
                                                                        checkFlag = 0;
                                                                    }
                                                                    db.execSQL("UPDATE serviceProductAttributes set checked = " + checkFlag + " where shipment_no ='" +
                                                                            sqliteMod.getShipment_no() + "' AND attribute_id = " + sqliteMod.getAttribute_id() + " ");
                                                                    Log.v("setflag", "-- " + sqliteMod.getShipment_no() + " - " + sqliteMod.getAttribute_name() + "- " + checkFlag);
                                                                    Cursor getshowdetails = db.rawQuery("SELECT * FROM serviceProductAttributes where shipment_no ='" + sqliteMod.getShipment_no() + "' AND attribute_type = 'service_function' AND checked = 1 ", null);
                                                                    getshowdetails.moveToFirst();
                                                                    if (getshowdetails.getCount() > 9) {
                                                                        Log.v("getshowdetails", "-- " + sqliteMod.getShipment_no() + " - " + sqliteMod.getAttribute_name());
                                                                        db.execSQL("UPDATE serviceConfirmation set function = 'Yes' where ship_num ='" +
                                                                                sqliteMod.getShipment_no() + "' ");
                                                                    } else {
                                                                        Log.v("getshowdetails1", "-- " + sqliteMod.getShipment_no() + " - " + sqliteMod.getAttribute_name());
                                                                        db.execSQL("UPDATE serviceConfirmation set function = 'No' where ship_num ='" +
                                                                                sqliteMod.getShipment_no() + "' ");
                                                                    }
                                                                    getshowdetails.close();
                                                                    if (context instanceof ServiceActivity) {
                                                                        ((ServiceActivity) context).insertServiceInfo();
                                                                    }
                                                                }
                                                            }
        );




        if (sqliteMod.getInput_field_type().equals("checkbox")) {
            holder.et_enterval.setVisibility(View.GONE);
        } else if (sqliteMod.getInput_field_type().equals("text")) {
            holder.checked_text_view.setVisibility(View.GONE);
            holder.et_enterval.setText(sqliteMod.getText_content());
        }



       holder.et_enterval.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (context instanceof ServiceActivity) {
                    ((ServiceActivity) context).editBoxAlert(sqliteMod.getShipment_no(),sqliteMod.getAttribute_id());
                }
            }


        });

   /*     holder.et_enterval.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog elAlertdialog = new Dialog(context);
                elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                elAlertdialog.setContentView(R.layout.service_textalert);
                elAlertdialog.show();

                final AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
                final AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
                final AppCompatEditText name = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
                final TextInputLayout txt_cust_name = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);

                Cursor getReturnValue = db.rawQuery("Select * from serviceProductAttributes where shipment_no ='" + sqliteMod.getShipment_no() + "' AND attribute_id = " + sqliteMod.getAttribute_id() + "  ", null);
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
                        editvalue = text_val;
                        Log.v("fetchVal", "- " + fetchVal);
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

                        Log.v("editvalue", " = " + editvalue);
                        if (editvalue == null || editvalue.equals("null")) {

                            txt_cust_name.setErrorEnabled(true);
                            txt_cust_name.setError("Enter comments");
                            txt_cust_name.requestFocus();
                        } else {
                            Log.v("serviceProductAttri","- "+ sqliteMod.getAttribute_name());
                            db.execSQL("UPDATE serviceProductAttributes set text_content = '" + editvalue + "' where shipment_no ='" +
                                    sqliteMod.getShipment_no() + "' AND attribute_id = " + sqliteMod.getAttribute_id() + " ");

                            holder.et_enterval.setText(editvalue);
                            elAlertdialog.dismiss();
//                            notifyDataSetChanged();
                        }
                    }
                });

                back.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {
                        elAlertdialog.dismiss();

                    }
                });
            }


        });*/

    }



    @Override
    public int getItemCount() {
        return mSqliteBeanses.size();
    }


    /*public void qtyTextChanged ( final AttributeShowAdapter.MyViewHolder holder){
        holder.et_enterval.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog elAlertdialog = new Dialog(context);
                elAlertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                elAlertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                elAlertdialog.setContentView(R.layout.service_textalert);
                elAlertdialog.show();

                AppCompatButton back = (AppCompatButton) elAlertdialog.findViewById(R.id.back);
                AppCompatButton submit = (AppCompatButton) elAlertdialog.findViewById(R.id.submit);
                final AppCompatEditText name = (AppCompatEditText) elAlertdialog.findViewById(R.id.name);
                final TextInputLayout txt_cust_name = (TextInputLayout) elAlertdialog.findViewById(R.id.txt_cust_name);

                Cursor getReturnValue = db.rawQuery("Select * from serviceProductAttributes where shipment_no ='" + holder.sqliteMod.getShipment_no() + "' AND attribute_id = " + holder.sqliteMod.getAttribute_id() + "  ", null);
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
                        editvalue = text_val;
                        Log.v("fetchVal", "- " + fetchVal);
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

                        Log.v("editvalue", " = " + editvalue);
                        if (editvalue == null || editvalue.equals("null")) {

                            txt_cust_name.setErrorEnabled(true);
                            txt_cust_name.setError("Enter comments");
                            txt_cust_name.requestFocus();
                        } else {

                            db.execSQL("UPDATE serviceProductAttributes set text_content = '" + editvalue + "' where shipment_no ='" +
                                    holder.sqliteMod.getShipment_no() + "' AND attribute_id = " + holder.sqliteMod.getAttribute_id() + " ");
                            elAlertdialog.dismiss();
                            holder.et_enterval.setText(editvalue);
                        }
                    }
                });

                back.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {
                        elAlertdialog.dismiss();

                    }
                });
            }


        });
    }*/


       /* holder.et_enterval.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence qty, int i, int i1, int i2) {
                String text_val = qty.toString();
                Log.v("onTextChanged","- "+text_val);
                holder.et_enterval.setText(text_val);
                db.execSQL("UPDATE serviceProductAttributes set text_content = '"+text_val+"' where shipment_no ='" +
                        sqliteMod.getShipment_no() + "' AND attribute_id = "+sqliteMod.getAttribute_id()+" ");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
}
