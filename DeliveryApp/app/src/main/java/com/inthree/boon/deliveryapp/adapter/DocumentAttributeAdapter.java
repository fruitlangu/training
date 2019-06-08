package com.inthree.boon.deliveryapp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.activity.ServiceActivity;
import com.inthree.boon.deliveryapp.model.AttributeModel;

import java.util.List;


public class DocumentAttributeAdapter extends RecyclerView.Adapter<DocumentAttributeAdapter.MyViewHolder> {


    public List<AttributeModel> mSqliteBeanses= null;
    private List<AttributeModel> filteredList;
    Context context;
    //    Context ctx;

    Activity activity;

    //    Context mContext;
    SQLiteDatabase db;

    String delivery_status;
    String editvalue = "";
    String fetchVal;



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_attribute;
        TextView et_enterval;
        ImageView ivCheckBox;
        CheckBox checked_text_view;

        public MyViewHolder(View itemView) {
            super(itemView);
//            p_prod_code = (TextView)  itemView.findViewById(R.id.ps_product_code);
//            p_prod_name = (TextView)  itemView.findViewById(R.id.ps_product_name);
//            p_prod_qty = (TextView)  itemView.findViewById(R.id.ps_product_qty);


            tv_attribute = (TextView) itemView.findViewById(R.id.tv_attribute);
            ivCheckBox = (ImageView) itemView.findViewById(R.id.iv_check_box);
            et_enterval  = (TextView) itemView.findViewById(R.id.et_enterval);
            checked_text_view = (CheckBox) itemView.findViewById(R.id.checked_text_view);
        }

    }

    public DocumentAttributeAdapter(Context context, List<AttributeModel> mSqliteBeanses, SQLiteDatabase db) {
        this.context = context;
        this.mSqliteBeanses = mSqliteBeanses;
        for(int i =0;i<mSqliteBeanses.size();i++)

        this.db = db;
    }


    @Override
    public DocumentAttributeAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        context =  parent.getContext();
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_attribute_adapter, parent, false);

//        return new schemeViewAdapter.MyViewHolder(itemView);
        DocumentAttributeAdapter.MyViewHolder holder = new DocumentAttributeAdapter.MyViewHolder(itemView);
        return holder;
    }



    @Override
    public void onBindViewHolder(final DocumentAttributeAdapter.MyViewHolder holder, int position) {
        final AttributeModel sqliteMod = mSqliteBeanses.get(position);

        holder.tv_attribute.setText(sqliteMod.getAttribute_name());
        holder.ivCheckBox.setVisibility(View.INVISIBLE);
      /*  holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){


            }
        });*/

        if (sqliteMod.isSelected()) {
            holder.checked_text_view.setChecked(true);
        }
        else {
            holder.checked_text_view.setChecked(false);
        }

        if (sqliteMod.isSelected()){
            sqliteMod.setSelected(true);
        }else{
            sqliteMod.setSelected(false);
        }

        holder.checked_text_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                @Override
                                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                                                    Log.v("onCheckedChanged", String.valueOf(isChecked));
                                                                    int checkFlag = 0;
                                                                    if(isChecked){
                                                                        checkFlag = 1;
                                                                    }else{
                                                                        checkFlag = 0;
                                                                    }
                                                                    db.execSQL("UPDATE serviceProductAttributes set checked = "+checkFlag+" where shipment_no ='" +
                                                                            sqliteMod.getShipment_no() + "' AND attribute_id = "+sqliteMod.getAttribute_id()+" ");

                                                                    Cursor getshowdetails = db.rawQuery("SELECT * FROM serviceProductAttributes where shipment_no ='"+sqliteMod.getShipment_no()+"' AND attribute_type = 'service_document' AND checked = 1 ", null);
                                                                    if(getshowdetails.getCount()> 3){
//                                                                        Log.v("getcount_of", String.valueOf(getshowdetails.getCount()));
                                                                        db.execSQL("UPDATE serviceConfirmation set documents = 'Yes' where ship_num ='" +
                                                                                sqliteMod.getShipment_no() + "' ");
                                                                    }else{
                                                                        db.execSQL("UPDATE serviceConfirmation set documents = 'No' where ship_num ='" +
                                                                                sqliteMod.getShipment_no() + "' ");
                                                                    }
                                                                    getshowdetails.close();
                                                                }
                                                            }
        );
        /*holder.ivCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int checkFlag = 0;

                if (sqliteMod.isSelected()) {
                    checkFlag =1;

                    sqliteMod.setSelected(false);
//                    Log.v("AttributeModel","  --"+checkFlag);
                    holder.ivCheckBox.setBackgroundResource(R.drawable.checked);
                    db.execSQL("UPDATE serviceProductAttributes set checked = "+checkFlag+" where shipment_no ='" +
                            sqliteMod.getShipment_no() + "' AND attribute_id = "+sqliteMod.getAttribute_id()+" ");
                }
                else{
                    checkFlag =0;
//                    Log.v("AttributeModel2","  --"+sqliteMod.isSelected()+"-- "+ sqliteMod.getAttribute_name()+"-- "+sqliteMod.getShipment_no() );
                    sqliteMod.setSelected(true);
                    holder.ivCheckBox.setBackgroundResource(R.drawable.check);
                    db.execSQL("UPDATE serviceProductAttributes set checked = "+checkFlag+" where shipment_no ='" +
                            sqliteMod.getShipment_no() + "' AND attribute_id = "+sqliteMod.getAttribute_id()+" ");
                }

                Cursor getshowdetails = db.rawQuery("SELECT * FROM serviceProductAttributes where shipment_no ='"+sqliteMod.getShipment_no()+"' AND attribute_type = 'service_document' AND checked = 1 ", null);
                if(getshowdetails.getCount()> 3){
                    Log.v("getcount_of", String.valueOf(getshowdetails.getCount()));
                    db.execSQL("UPDATE serviceConfirmation set documents = 'Yes' where ship_num ='" +
                            sqliteMod.getShipment_no() + "' ");
                }else{
                    db.execSQL("UPDATE serviceConfirmation set documents = 'No' where ship_num ='" +
                            sqliteMod.getShipment_no() + "' ");
                }
            }
        });*/
//        Log.v("AttributeModel1","  --"+sqliteMod.getInput_field_type());
   /*     if(sqliteMod.getInput_field_type().equals("checkbox")){
            holder.et_enterval.setVisibility(View.GONE);
        }else if(sqliteMod.getInput_field_type().equals("text")){
            holder.ivCheckBox.setVisibility(View.GONE);
        }*/

        if(sqliteMod.getInput_field_type().equals("checkbox")){
            holder.et_enterval.setVisibility(View.GONE);
        }else if(sqliteMod.getInput_field_type().equals("text")){
            holder.checked_text_view.setVisibility(View.GONE);
            holder.et_enterval.setText(sqliteMod.getText_content());
        }
        if (sqliteMod.isSelected()) {
            holder.ivCheckBox.setBackgroundResource(R.drawable.checked);


        }
        else{
            holder.ivCheckBox.setBackgroundResource(R.drawable.check);

        }


        holder.et_enterval.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (context instanceof ServiceActivity) {
                    ((ServiceActivity) context).editBoxDocumentAlert(sqliteMod.getShipment_no(),sqliteMod.getAttribute_id());
                }
            }


        });

       /* holder.et_enterval.setOnClickListener(new View.OnClickListener() {

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

                Cursor getReturnValue = db.rawQuery("Select * from serviceProductAttributes where shipment_no ='" +sqliteMod.getShipment_no() + "' AND attribute_id = "+sqliteMod.getAttribute_id()+"  ", null);
                getReturnValue.moveToFirst();
                if (getReturnValue.getCount() > 0){
                    fetchVal = getReturnValue.getString(getReturnValue.getColumnIndex("text_content"));
                    name.setText(fetchVal);
                    editvalue = fetchVal;
                }


                name.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence qty, int i, int i1, int i2) {
                        String text_val = qty.toString();
                        editvalue = text_val;
                        Log.v("onTextChanged","- "+text_val);
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

                        db.execSQL("UPDATE serviceProductAttributes set text_content = '"+editvalue+"' where shipment_no ='" +
                                sqliteMod.getShipment_no() + "' AND attribute_id = "+sqliteMod.getAttribute_id()+" ");
                        elAlertdialog.dismiss();
                        holder.et_enterval.setText(editvalue);
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

}
