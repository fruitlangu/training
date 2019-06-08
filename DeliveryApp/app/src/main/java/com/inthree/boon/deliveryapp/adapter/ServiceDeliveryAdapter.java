package com.inthree.boon.deliveryapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.activity.ServiceActivity;
import com.inthree.boon.deliveryapp.activity.ServiceDetails;
import com.inthree.boon.deliveryapp.model.ServiceModel;
import com.inthree.boon.deliveryapp.activity.OrderDetails;
//import com.inthree.boon.deliveryapp.activity.ServiceDetails;
import com.inthree.boon.deliveryapp.app.Constants;

import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thulasiram on 14-07-2017.
 */

public class ServiceDeliveryAdapter extends RecyclerView.Adapter<ServiceDeliveryAdapter.MyViewHolder> {
    public List<ServiceModel> mSqliteBeanses= null;
    private List<ServiceModel> filteredList;
    Context context;
    //    Context ctx;

    Activity activity;

//    Context mContext;
    SQLiteDatabase db;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_order_id;
        TextView tv_shipment_id;
        TextView tv_customer_name;
        CircleImageView im_deli;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv_order_id = (TextView)  itemView.findViewById(R.id.tv_order_id);
            tv_shipment_id = (TextView)  itemView.findViewById(R.id.tv_shipment_id);
            tv_customer_name = (TextView)  itemView.findViewById(R.id.tv_customer_name);
            im_deli = itemView.findViewById(R.id.cv_profile_image);

        }

    }

    public ServiceDeliveryAdapter(Context context, List<ServiceModel> mSqliteBeanses, SQLiteDatabase db, Activity activity) {
        this.context = context;
        this.mSqliteBeanses = mSqliteBeanses;
        this.db = db;
        this.activity = activity;
   //     mFilter = new CustomFilter(PendingDeliveryAdapter.this);





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
        final ServiceModel sqliteMod = mSqliteBeanses.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v){

             Intent dash = new Intent(context, ServiceDetails.class);
             dash.putExtra("order_num", sqliteMod.getOrderId() );
             dash.putExtra("ship_id", sqliteMod.getShipId() );
             context.startActivity(dash);
         }
         });

        holder.tv_order_id.setText(sqliteMod.getOrderId());
        holder.tv_shipment_id.setText(sqliteMod.getShipId());
        holder.tv_customer_name.setText(sqliteMod.getCustomerName());

       /* Log.e("sqlorderpage",sqliteMod.getOrderpage());
        if(sqliteMod.getOrderpage().equals("success")){
            holder.im_deli.setImageResource(R.drawable.deliver);
        }*/

        if(sqliteMod.getOrderpage().equals("pending")){
            holder.im_deli.setImageResource(R.drawable.pend);
        }
        if(sqliteMod.getOrderpage().equals("complete") && !sqliteMod.isOffline()){
            holder.im_deli.setImageResource(R.drawable.deliver);
        }else if(sqliteMod.getOrderpage().equals("complete") && sqliteMod.isOffline()){
            holder.im_deli.setImageResource(R.drawable.ol_deliver);
        }
        if(sqliteMod.getOrderpage().equals("Incomplete")){
            holder.im_deli.setImageResource(R.drawable.fail);
        }
    }

    @Override
    public int getItemCount() {
        return mSqliteBeanses.size();
    }


}

