package com.inthree.boon.deliveryapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.pendingModel;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class BrachDeliveryAdapter extends RecyclerView.Adapter<BrachDeliveryAdapter.MyViewHolder> {


    /**
     * QRCODE SCAN
     */
    public static final int QR_REQUEST_CODE = 200;

    public List<pendingModel> mSqliteBeanses = null;


    public ArrayList<pendingModel> invoiceArray;

    private List<pendingModel> filteredList;
    Context context;
    //    Context ctx;

    Activity activity;

    //    Context mContext;
    SQLiteDatabase database;

    /**
     * Get context of an activity
     */
    Context pendingContext;

    CheckBox chkAll;
    private boolean isSelectedAll;
    private boolean singleSelectChk = false;


    MyViewHolder holder;

    String invoicenu;

    String shipmentNumber;

    int qrPosition;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_order_id;
        TextView tv_shipment_id;
        TextView tv_customer_name;
        EditText tv_invoice_id;
        ImageView img_qrcode;
        CircleImageView im_deli;
        ImageView iv_pickup_icon;
        CheckBox chkSingle;
        public pendingModel sqliteMod;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_order_id = (TextView) itemView.findViewById(R.id.tv_order_id);
            tv_shipment_id = (TextView) itemView.findViewById(R.id.tv_shipment_id);
            tv_customer_name = (TextView) itemView.findViewById(R.id.tv_customer_name);
            im_deli = itemView.findViewById(R.id.cv_profile_image);
            chkSingle = (CheckBox) itemView.findViewById(R.id.chk_single);
            //   iv_pickup_icon = itemView.findViewById(R.id.iv_pickup_icon);

        }

    }

    public BrachDeliveryAdapter(Context context, List<pendingModel> mSqliteBeanses, SQLiteDatabase db, Context context1) {
        this.context = context;
        this.pendingContext = context1;
        this.mSqliteBeanses = mSqliteBeanses;
        this.database = db;
        this.activity = activity;
        //     mFilter = new CustomFilter(PendingDeliveryAdapter.this);
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this.pendingContext, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();

        invoiceArray = new ArrayList<>();


    }


    @Override
    public BrachDeliveryAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        context = parent.getContext();
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.branch_row, parent, false);

//        return new schemeViewAdapter.MyViewHolder(itemView);
        BrachDeliveryAdapter.MyViewHolder holder = new BrachDeliveryAdapter.MyViewHolder(itemView);
        return holder;
    }


    @Override
    public void onBindViewHolder(final BrachDeliveryAdapter.MyViewHolder holder, final int position) {
        holder.sqliteMod = mSqliteBeanses.get(position);
        this.holder = holder;
//        holder.sqliteMod = mSqliteBeanses.get(position);
        holder.setIsRecyclable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor getShipmentExist = database.rawQuery("SELECT * FROM orderheader where Shipment_Number = '" + holder.sqliteMod.getShipId() + "'  ", null);
                getShipmentExist.moveToFirst();
                if (getShipmentExist.getCount() > 0) {
                    /*Intent dash = new Intent(context, OrderDetails.class);
                    dash.putExtra("order_num", sqliteMod.getOrderId());
                    dash.putExtra("ship_id", sqliteMod.getShipId());
                    dash.putExtra("order_type", sqliteMod.getOrder_type());
                    context.startActivity(dash);*/
                }
                getShipmentExist.close();


            }
        });


        holder.chkSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final pendingModel pending;
                if (((CheckBox) v).isChecked()) {
                    singleSelectChk = true;
                    isSelectedAll = false;
                    holder.chkSingle.setTag(position);

                    if (holder.sqliteMod.getCheck()) {
                        holder.sqliteMod.setCheck(false);
                    } else {
                        holder.sqliteMod.setCheck(true);
                    }

                    String deliveryDetailsupdate = "UPDATE orderheader set check_bfil_order_status = '1'  where Shipment_Number= '" + holder.sqliteMod.getShipId() + "' AND delivery_to = '1' AND sync_status='P' ";
                    database.execSQL(deliveryDetailsupdate);

                    /*pending = new pendingModel();
                    pending.setShipId(holder.sqliteMod.getShipId());
                    pending.setShipId(holder.sqliteMod.getShipId());
                    invoiceArray.add(pending);*/
                } else {

                    String deliveryDetailsupdate = "UPDATE orderheader set check_bfil_order_status = '0' where Shipment_Number= '" + holder.sqliteMod.getShipId() + "' AND delivery_to = '1' AND sync_status='P' ";
                    database.execSQL(deliveryDetailsupdate);
                   /* for (int i = 0; i < invoiceArray.size(); i++) {
                        String ship = mSqliteBeanses.get(position).getShipId();
                        if (ship.equals(invoiceArray.get(i).getShipId()))
                            invoiceArray.remove(i);
                        holder.sqliteMod.setCheck(false);
                    }*/

                }
            }
        });

        if (!singleSelectChk) {
            for (int i = 0; i < mSqliteBeanses.size(); i++) {
                if (!isSelectedAll) {
                    holder.sqliteMod.setCheck(false);
                } else {
                    invoiceArray.clear();
                    holder.sqliteMod.setCheck(true);
                    holder.chkSingle.setActivated(true);
                    String deliveryDetailsupdate = "UPDATE orderheader set check_bfil_order_status = '1' where  Shipment_Number='" + holder.sqliteMod.getShipId() + "' AND delivery_to = '1'  AND sync_status='P'";
                    database.execSQL(deliveryDetailsupdate);
                    System.out.println("ALL CHECKED INSIDE AVA");
                }
            }
        }


        holder.tv_order_id.setText(holder.sqliteMod.getOrderId());
        holder.tv_shipment_id.setText(holder.sqliteMod.getShipId());
        holder.tv_customer_name.setText(holder.sqliteMod.getCustomerName());
        holder.chkSingle.setChecked(mSqliteBeanses.get(position).getCheck());

       /* if (holder.sqliteMod.getInvoice() != null) {
            holder.tv_invoice_id.setText(holder.sqliteMod.getInvoice());
        }else {
            holder.tv_invoice_id.setText("");
        }
*/




      /*  holder.img_qrcode.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, QrScannerActivity.class);
                intent.putExtra("shipmentid", holder.sqliteMod.getShipId());
                ((Activity) context).startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);

                *//*if (context instanceof BFILBranchActivity) {
                    ((BFILBranchActivity) context).qrcode(sqliteMod.getShipId());
                }*//*

            }
        });*/


    }

    @Override
    public int getItemCount() {
        return mSqliteBeanses.size();
    }


    public void selectAll(boolean checkBool) {
        Log.e("onClickSelectAll", "yes");
        isSelectedAll = checkBool;
        singleSelectChk = false;
        notifyDataSetChanged();

    }


    /*public ArrayList<pendingModel> getArrayList() {
        return invoiceArray;
    }*/

}
