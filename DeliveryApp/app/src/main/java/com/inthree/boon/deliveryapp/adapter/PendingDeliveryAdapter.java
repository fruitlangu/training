package com.inthree.boon.deliveryapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.activity.OrderDetails;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.pendingModel;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thulasiram on 14-07-2017.
 */

public class PendingDeliveryAdapter extends RecyclerView.Adapter<PendingDeliveryAdapter.MyViewHolder> {
    public List<pendingModel> mSqliteBeanses= null;
    private List<pendingModel> filteredList;
    Context context;
    //    Context ctx;

    Activity activity;

//    Context mContext;
    SQLiteDatabase database;

    Context pendingContext;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_order_id;
        TextView tv_shipment_id;
        TextView tv_customer_name;
        CircleImageView im_deli;
        ImageView iv_pickup_icon;
        public pendingModel sqliteMod;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_order_id = (TextView)  itemView.findViewById(R.id.tv_order_id);
            tv_shipment_id = (TextView)  itemView.findViewById(R.id.tv_shipment_id);
            tv_customer_name = (TextView)  itemView.findViewById(R.id.tv_customer_name);
            im_deli = itemView.findViewById(R.id.cv_profile_image);
            iv_pickup_icon = itemView.findViewById(R.id.iv_pickup_icon);

        }

    }

    public PendingDeliveryAdapter(Context context, List<pendingModel> mSqliteBeanses, SQLiteDatabase db, Context context1) {
        this.context = context;
        this.pendingContext = context1;
        this.mSqliteBeanses = mSqliteBeanses;
        this.database = db;
        this.activity = activity;
   //     mFilter = new CustomFilter(PendingDeliveryAdapter.this);
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this.pendingContext, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();

    }


    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        context =  parent.getContext();
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_adapter, parent, false);

//        return new schemeViewAdapter.MyViewHolder(itemView);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final pendingModel sqliteMod = mSqliteBeanses.get(position);
//        holder.sqliteMod = mSqliteBeanses.get(position);
        holder.setIsRecyclable(false);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v){

             Cursor getShipmentExist = database.rawQuery("SELECT * FROM orderheader where Shipment_Number = '"+sqliteMod.getShipId()+"'  ", null);
             getShipmentExist.moveToFirst();
             if(getShipmentExist.getCount() > 0){
                Intent dash = new Intent(context, OrderDetails.class);
                dash.putExtra("order_num", sqliteMod.getOrderId() );
                dash.putExtra("ship_id", sqliteMod.getShipId() );
                dash.putExtra("order_type", sqliteMod.getOrder_type() );
                context.startActivity(dash);
             }
             getShipmentExist.close();


          /*  if(sqliteMod.getOrder_type() == 3 || sqliteMod.getOrder_type() == 2 ){


                Intent pickdelivery = new Intent(context, PickupDelivery.class);
                pickdelivery.putExtra("order_num", sqliteMod.getOrderId() );
                pickdelivery.putExtra("ship_num", sqliteMod.getShipId());
                pickdelivery.putExtra("order_type", String.valueOf(sqliteMod.getOrder_type()));
                Log.e("sqlorderpage",String.valueOf(sqliteMod.getOrder_type()));
                context.startActivity(pickdelivery);
             }else{
             Intent dash = new Intent(context, OrderDetails.class);
             dash.putExtra("order_num", sqliteMod.getOrderId() );
             dash.putExtra("ship_id", sqliteMod.getShipId() );
             context.startActivity(dash);
            }*/

         }});

        holder.tv_order_id.setText(sqliteMod.getOrderId());
        holder.tv_shipment_id.setText(sqliteMod.getShipId());
        holder.tv_customer_name.setText(sqliteMod.getCustomerName());

//        if(sqliteMod.getOrder_type() == 3 || sqliteMod.getOrder_type() == 2 ){
        if(sqliteMod.getOrder_type() != 1 ){
            holder.iv_pickup_icon.setVisibility(View.VISIBLE);
        }
       /* Log.e("sqlorderpage",sqliteMod.getOrderpage());
        if(sqliteMod.getOrderpage().equals("success")){
            holder.im_deli.setImageResource(R.drawable.deliver);
        }*/

        if(sqliteMod.getOrderpage().equals("pending")){
//            holder.im_deli.setImageResource(R.drawable.pend);
            if(sqliteMod.getOrder_type() == 1 ){
                holder.im_deli.setImageResource(R.drawable.pend);
            }else{
                holder.im_deli.setImageResource(R.drawable.pending_pickup);
            }
        }

        if(sqliteMod.getOrderpage().equals("success") && !sqliteMod.isOffline()){
            if(sqliteMod.getOrder_type() != 1 ){
                holder.im_deli.setImageResource(R.drawable.pickup_success);
            }else{
            holder.im_deli.setImageResource(R.drawable.deliver);
            }
        }else if(sqliteMod.getOrderpage().equals("success") && sqliteMod.isOffline()){
            holder.im_deli.setImageResource(R.drawable.ol_deliver);
        }

        if(sqliteMod.getOrderpage().equals("failed") && !sqliteMod.isOffline()){
            if(sqliteMod.getOrder_type() != 1 ){
                holder.im_deli.setImageResource(R.drawable.pickup_failed);
            }else{
                holder.im_deli.setImageResource(R.drawable.fail);
            }
        }else if(sqliteMod.getOrderpage().equals("failed") && sqliteMod.isOffline()){
            holder.im_deli.setImageResource(R.drawable.ol_failed);
        }


       /* if(sqliteMod.getOrderpage().equals("failed")){
            if(sqliteMod.getOrder_type() != 1 ){
                holder.im_deli.setImageResource(R.drawable.pickup_failed);
            }else{
            holder.im_deli.setImageResource(R.drawable.fail);
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return mSqliteBeanses.size();
    }


    /*@Override
    public android.widget.Filter getFilter() {
        return  mFilter;
    }

    public class CustomFilter extends android.widget.Filter {
        private PendingDeliveryAdapter mAdapter;
        private CustomFilter(PendingDeliveryAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(mSqliteBeanses);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final pendingModel mWords : mSqliteBeanses) {
                    if (mWords.getCustomerName().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(mWords);
                    }
                }
            }
            System.out.println("Count Number " + filteredList.size());
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((List<pendingModel>) results.values).addAll(filteredList));
            this.mAdapter.notifyDataSetChanged();
        }
    }*/




}

