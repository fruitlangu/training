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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.activity.OrderDetails;
import com.inthree.boon.deliveryapp.model.ProductShowModel;


import java.util.List;

/**
 * Created by karthika on 12-Jan-18.
 */

public class ProductShowAdapter extends RecyclerView.Adapter<ProductShowAdapter.MyViewHolder> {


    public List<ProductShowModel> mSqliteBeanses= null;
    private List<ProductShowModel> filteredList;
    Context context;
    //    Context ctx;

    Activity activity;

    //    Context mContext;
    SQLiteDatabase db;

    String delivery_status;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView p_deli_qty;
        private final View del_view;
        private final LinearLayout delQtyRoot;
        TextView p_prod_code;
        TextView p_prod_name;
        TextView p_prod_qty;
       // TextView p_prod_amt;
        ImageView iv_pickup_icon;
        TextView tv_delivered_qty;

        public MyViewHolder(View itemView) {
            super(itemView);
            p_prod_code = (TextView)  itemView.findViewById(R.id.ps_product_code);
            p_prod_name = (TextView)  itemView.findViewById(R.id.ps_product_name);
            p_prod_qty = (TextView)  itemView.findViewById(R.id.ps_product_qty);
           // p_prod_amt = (TextView)  itemView.findViewById(R.id.ps_product_amt);
            p_deli_qty = (TextView)  itemView.findViewById(R.id.ps_delivery_qty);
            del_view = (View)  itemView.findViewById(R.id.deli_view);
            delQtyRoot = (LinearLayout)  itemView.findViewById(R.id.deli_view_root);
            iv_pickup_icon = (ImageView) itemView.findViewById(R.id.iv_pickup_icon);
            tv_delivered_qty = itemView.findViewById(R.id.tv_delivered_qty);
        }

    }

    public ProductShowAdapter(Context context, List<ProductShowModel> mSqliteBeanses, SQLiteDatabase db, Activity activity, String delivery_status) {
        this.context = context;
        this.mSqliteBeanses = mSqliteBeanses;
        this.db = db;
        this.activity = activity;
        this.delivery_status=delivery_status;
        //     mFilter = new CustomFilter(PendingDeliveryAdapter.this);
    }


    @Override
    public ProductShowAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        context =  parent.getContext();
        final View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_show, parent, false);

//        return new schemeViewAdapter.MyViewHolder(itemView);
        ProductShowAdapter.MyViewHolder holder = new ProductShowAdapter.MyViewHolder(itemView);
        return holder;
    }



    @Override
    public void onBindViewHolder(final ProductShowAdapter.MyViewHolder holder, int position) {
        final ProductShowModel sqliteMod = mSqliteBeanses.get(position);

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Intent dash = new Intent(context, OrderDetails.class);
                dash.putExtra("order_num", sqliteMod.getpcode() );
                context.startActivity(dash);
            }
        });*/

        holder.p_prod_code.setText(sqliteMod.getpcode());
        holder.p_prod_name.setText(sqliteMod.getpname());
        holder.p_prod_qty.setText(sqliteMod.getpqty());
//        Log.v("delivery_qty",sqliteMod.getDeliveryQty());
       // holder.p_prod_amt.setText(sqliteMod.getpamt());
        if(delivery_status.equalsIgnoreCase("Success")){
           /*if(sqliteMod.getDeliveryQty().equalsIgnoreCase("0")){
               holder.del_view.setVisibility(View.GONE);
               holder.delQtyRoot.setVisibility(View.GONE);
           }else {*/
               holder.del_view.setVisibility(View.VISIBLE);
               holder.delQtyRoot.setVisibility(View.VISIBLE);
               if(sqliteMod.getOrder_type().equals("2")){
                   holder.p_deli_qty.setText(sqliteMod.getpqty());
               }else{
                   holder.p_deli_qty.setText(sqliteMod.getDeliveryQty());
               }

          /* }*/
        }else{
            holder.del_view.setVisibility(View.GONE);
            holder.delQtyRoot.setVisibility(View.GONE);
        }

        if(sqliteMod.getPickupStatus() == 1){
            holder.iv_pickup_icon.setVisibility(View.VISIBLE);
            if(holder.delQtyRoot.getVisibility() == View.VISIBLE){
                holder.tv_delivered_qty.setText("Picked Up Qty");
            }
        }



    }

    @Override
    public int getItemCount() {
        return mSqliteBeanses.size();
    }

}
