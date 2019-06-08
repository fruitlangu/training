package com.inthree.boon.deliveryapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.Line;
import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.NetTime.TrueTimeRx;
import com.inthree.boon.deliveryapp.R;

import com.inthree.boon.deliveryapp.adapter.PendingDeliveryAdapter;
import com.inthree.boon.deliveryapp.adapter.ProductShowAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;

import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.model.ProductShowModel;
import com.inthree.boon.deliveryapp.pendingModel;

import com.inthree.boon.deliveryapp.app.Logger;

import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ServiceDetails extends AppCompatActivity implements View.OnClickListener {

    private static final String DB_NAME = "boonboxdelivery.sqlite";
    SQLiteDatabase database;
    public static final String MyPREFERENCES = "MyPrefs";

    TextView tv_reference_number;
    TextView tv_order;
    TextView tv_customer_name;
    TextView tv_phone;
    TextView tv_alter_phone;
    TextView tv_shippingAddress;
    TextView tv_shippingCity;
    TextView tv_shippingpincode;
    TextView tv_shipping_att_count;

    TextView tv_item_code;
    TextView tv_product_name;
    private String complete_status;
    private String service_delivery_status;

    LinearLayout lay_completed;


    String order_num;
    String  shipId ;

    String shipmentid;
    String CustomerName;
    String ContactNumber;
    String Alternetnumber;
    String Shippingaddress;
    String Shippingcity;
    String Shippingpincode;
    String Attempt;

    LinearLayout ll_delivered;
    LinearLayout ll_par_delivered;
    TextView tv_deliveryType;
    String product_name;

    /**
            * Get the details of array
 */
    ArrayList<String> my_array = new ArrayList<String>();
    LinearLayout ll_call_user;
    LinearLayout ll_location;

    RadioGroup rg;
    Button procd;
    RadioButton rb_customer;
    RadioButton rb_alternate;
    RadioButton rb_shipping;
    RadioButton rb_branch;
    RadioButton rb_groupleader;
    Dialog mBottomSheetDialog;
    int checkId;
    private View viewCall;
    private static final int REQUEST = 100;
    String getCustomerNo;
    String getAlternateNo;
    String getShippingNo;
    String getBranchNo;
    String getGroupLeaderNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();

        tv_reference_number = findViewById(R.id.tv_reference_number);
        tv_order = findViewById(R.id.tv_orderId);
        tv_customer_name = findViewById(R.id.tv_customer_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_alter_phone = findViewById(R.id.tv_alter_phone);
        tv_shippingAddress = findViewById(R.id.tv_shippingAddress);
        tv_shippingCity = findViewById(R.id.tv_shippingCity);
        tv_shippingpincode = findViewById(R.id.tv_shippingpincode);
        tv_shipping_att_count = findViewById(R.id.tv_shipping_att_count);
        tv_deliveryType = (TextView) findViewById(R.id.tv_deliveryType);

        tv_item_code = findViewById(R.id.tv_item_code);
        tv_product_name = findViewById(R.id.tv_product_name);

        ll_delivered = (LinearLayout) findViewById(R.id.ll_delivered);
        ll_par_delivered = (LinearLayout) findViewById(R.id.ll_partial_delivered);

        lay_completed = (LinearLayout) findViewById(R.id.lay_del);

        Intent dash = getIntent();
        if (null != dash) {
            order_num = dash.getStringExtra("order_num");
            shipId = dash.getStringExtra("ship_id");
        } else {
            order_num = "";
            shipId="";
        }


        getOrderDetails(order_num);
        getproductname();
//        getsampprod();

        ll_delivered.setOnClickListener(this);
        ll_par_delivered.setOnClickListener(this);


        ll_call_user = (LinearLayout) findViewById(R.id.ll_call_user);
        ll_location = (LinearLayout) findViewById(R.id.ll_location);

        ll_call_user.setOnClickListener(this);
        ll_location.setOnClickListener(this);
        getMapAddress();

    }





    public void getOrderDetails(String ordernumber) {

        Cursor getOrders = database.rawQuery("Select * from serviceMaster where order_id = '" + ordernumber + "' ", null);

        if (getOrders.getCount() > 0) {
            getOrders.moveToFirst();


            complete_status = getOrders.getString(getOrders.getColumnIndex("sync_status"));
            service_delivery_status = getOrders.getString(getOrders.getColumnIndex("delivery_status"));


            if (complete_status.equals("P")) {
                complete_status = "Pending";
            }
            else if (!service_delivery_status.equals("incomplete") && (complete_status.equals("S")|| complete_status.equals("U") || complete_status.equals("C") || complete_status.equals("E") )) {
                complete_status = "Complete";
                lay_completed.setVisibility(View.GONE);
                tv_shipping_att_count.setText(R.string.delivered_date_new);


            }else if (service_delivery_status.equals("incomplete") && (complete_status.equals("S")|| complete_status.equals("U") || complete_status.equals("C") || complete_status.equals("E")))
            {

                complete_status = "Incomplete";
//                tv_to_be_delivered.setText("Date");
//                lay_delivery.setVisibility(View.GONE);
            }


            if(complete_status.equals("Incomplete")){
                tv_deliveryType.setText("Incomplete");
            } else if(complete_status.equals("Pending")){
                tv_deliveryType.setText("Pending");
            }else if(complete_status.equals("Complete")){
                tv_deliveryType.setText("Complete");
            }

            tv_reference_number .setText("Order ID: "+getOrders.getString(getOrders.getColumnIndex("order_id")));

            shipmentid = getOrders.getString(getOrders.getColumnIndex("shipment_id"));

            tv_order.setText(getOrders.getString(getOrders.getColumnIndex("shipment_id")));

            tv_customer_name.setText(getOrders.getString(getOrders.getColumnIndex("customer_name")));
            tv_phone.setText(getOrders.getString(getOrders.getColumnIndex("customer_contact_number")));
            tv_alter_phone.setText(getOrders.getString(getOrders.getColumnIndex("alternate_contact_number")));
            tv_shippingAddress.setText(getOrders.getString(getOrders.getColumnIndex("shipping_address")));
            tv_shippingCity.setText(getOrders.getString(getOrders.getColumnIndex("shipping_city")));
            tv_shippingpincode.setText(getOrders.getString(getOrders.getColumnIndex("shipping_pincode")));
            tv_shipping_att_count.setText(getOrders.getString(getOrders.getColumnIndex("attempt")));
            Log.v("tv_shipping_att_count","- "+getOrders.getString(getOrders.getColumnIndex("attempt")));
            if(getOrders.getString(getOrders.getColumnIndex("attempt")).equals("")){

                tv_shipping_att_count.setText("0");
            }

            getOrders.close();

            //    Log.e("Com", complete_status);
//            Log.e("Comun", in_complete_status);
        }


    }


    public void getproductname() {

        Cursor getproduct = database.rawQuery("Select * from serviceitems where shipment_number = '" + shipmentid + "' ", null);

        if (getproduct.getCount() > 0) {
            getproduct.moveToFirst();

            tv_item_code.setText(getproduct.getString(getproduct.getColumnIndex("sku")));
            tv_product_name.setText(getproduct.getString(getproduct.getColumnIndex("name")));
            product_name = getproduct.getString(getproduct.getColumnIndex("name"));

            getproduct.close();
        }
    }


    @Override
    public void onClick(View view) {


        if (view == ll_delivered) {

            if (updatDate()) {
                Intent delivery = new Intent(ServiceDetails.this, ServiceActivity.class);
                delivery.putExtra("ship_id", shipmentid);
                delivery.putExtra("product_name", product_name);
                delivery.putExtra(Constants.ORDER_ID, order_num);
                startActivity(delivery);
            }


        } else if (view == ll_par_delivered) {


            Intent partdelivery = new Intent(ServiceDetails.this, ServiceIncompleteActivity.class);
            partdelivery.putExtra("ship_num", shipmentid);
            partdelivery.putExtra("product_name", product_name);
            partdelivery.putExtra(Constants.ORDER_ID, order_num);
            startActivity(partdelivery);


        }else if (view == ll_call_user) {

            viewCall=view;

            if (Build.VERSION.SDK_INT >= 23) {
                String[] permissions = {android.Manifest.permission.CALL_PHONE};
                if (!hasPermissions(ServiceDetails.this, permissions)) {
                    ActivityCompat.requestPermissions((Activity) ServiceDetails.this, permissions, REQUEST );
                } else {
                    openBottomSheet(view);
                }
            } else {
                openBottomSheet(view);
            }
        } else if (view == ll_location) {
            mapAddressAlert();
        }
    }



    /**
     * Check the device whether user has changed the date or not if user changed the date throw the alert to change the current
     * date
     *
     */
    private boolean updatDate() {
//        if (!TrueTimeRx.isInitialized()) {
        if (!TrueTimeRx.isInitialized()) {
            Log.d("Truetime not intialized", "Sorry TrueTime not yet initialized.");
            return true;
        }else {
            Date trueTime = TrueTimeRx.now();
            Date deviceTime = new Date();

            Log.d("kg",
                    String.format(" [trueTime: %d] [devicetime: %d] [drift_sec: %f]",
                            trueTime.getTime(),
                            deviceTime.getTime(),
                            (trueTime.getTime() - deviceTime.getTime()) / 1000F));


            String deviceDate = _formatDate(deviceTime, "yyyy-MM-dd", TimeZone.getTimeZone("GMT+05:30"));
            String networkDate = _formatDate(trueTime, "yyyy-MM-dd", TimeZone.getTimeZone("GMT+05:30"));

            if (deviceDate.equalsIgnoreCase(networkDate)) {


                return true;

            } else {
                Utils.AlertDialogCancel(this, getResources().getString(R.string.exactdate), getResources().getString(R.string.get_date_warn), getResources()
                        .getString(R.string.dialog_ok), getResources().getString(R.string.dialog_cancel));
            }
        }
        return false;
    }


    private String _formatDate(Date date, String pattern, TimeZone timeZone) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(timeZone);
        return format.format(date);
    }


    public void getsampprod(){


        Cursor getshowdetails = database.rawQuery("SELECT * FROM serviceMaster ", null);

        getshowdetails.moveToFirst();

        if(getshowdetails.getCount() > 0) {
            while (!getshowdetails.isAfterLast()) {
                Log.v("getsampprod", "-"+getshowdetails.getString(getshowdetails.getColumnIndex("delivery_status")));
                Log.v("getsampprod1", getshowdetails.getString(getshowdetails.getColumnIndex("sync_status")));
                Log.v("getsampprod2", getshowdetails.getString(getshowdetails.getColumnIndex("order_id")));
//                Log.v("prod_amount", getshowdetails.getString(getshowdetails.getColumnIndex("amount")));
//                Log.v("prod_ship", getshowdetails.getString(getshowdetails.getColumnIndex("shipmentnumber")));
                getshowdetails.moveToNext();
            }
        }
        getshowdetails.close();

    }

    /**
     * Get the permission for marshmallow
     * @param context Get the context of an activity
     * @param permissions GEt the permission of phone call
     * @return the permission
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openBottomSheet(viewCall);
                } else {
                    Toast.makeText(ServiceDetails.this, "The app was not allowed to call.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    /**
     * Get the address from the sqlite
     */
    private void getMapAddress(){
        try {
            Cursor getMapValue = database.rawQuery("select IFNULL(shipping_address,0) as shipping_address ,IFNULL(shipping_pincode,0) as shipping_pincode" +
                    " from " + "serviceMaster where shipment_id='"
                    + shipmentid + "'",null);
            System.out.println("COUNT_MAP: " + getMapValue.getCount());

            if(getMapValue.getCount()>0) {
                if (getMapValue.moveToFirst()) {
                    do {
                        String shippingAddress = getMapValue.getString(getMapValue.getColumnIndex("shipping_address"));
                        String shippingPincode = getMapValue.getString(getMapValue.getColumnIndex("shipping_pincode"));
//                    Log.v("getSchemeValue",NAME);
                        // my_array.add(billingAddress + "\n" + billing_pincode);
                        my_array.add(shippingAddress + "\n" + shippingPincode);

                    } while (getMapValue.moveToNext());
                }
                getMapValue.close();
            }
        } catch (Exception e) {
            Logger.showShortMessage(ServiceDetails.this, "Error encountered.");
        }
    }
    /**
     * Display the to address and brach address for disaply the route map
     */
    private void mapAddressAlert(){
        ListAdapter adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.map_alert, my_array) {

            ViewHolder holder;
            Drawable icon;

            class ViewHolder {
                ImageView icon;
                TextView title;
            }

            public View getView(int position, View convertView,
                                ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {
                    convertView = inflater.inflate(
                            R.layout.map_alert, null);

                    holder = new ViewHolder();
                    holder.icon = (ImageView) convertView
                            .findViewById(R.id.icon);
                    holder.title = (TextView) convertView
                            .findViewById(R.id.title);
                    convertView.setTag(holder);
                } else {
                    // view already defined, retrieve view holder
                    holder = (ViewHolder) convertView.getTag();
                }

                Drawable drawable = getResources().getDrawable(R.drawable.map_icon); //this is an image from the drawables
                // folder


                holder.title.setText(my_array.get(position));
                holder.icon.setImageDrawable(drawable);

                return convertView;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.select_location));
        builder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int item) {
                        Logger.showShortMessage(ServiceDetails.this, "You selected: " + my_array.get(item));
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData( Uri.parse("google.navigation:q=" + my_array.get(item)));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void openBottomSheet(View v) {

        Context context = v.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.call_alertbox, null);


        rb_customer = (RadioButton) view.findViewById(R.id.rb_customer);
        rb_alternate = (RadioButton) view.findViewById(R.id.rb_alternate);
        rb_shipping = (RadioButton) view.findViewById(R.id.rb_shipping);
        rb_branch = (RadioButton) view.findViewById(R.id.rb_branch);
        rb_groupleader = (RadioButton) view.findViewById(R.id.rb_groupleader);

        rg = (RadioGroup) view.findViewById(R.id.rg);
        procd = (Button) view.findViewById(R.id.proceed);

        Cursor getPhone = database.rawQuery("Select IFNULL(customer_contact_number, '') as customer_contact, IFNULL(alternate_contact_number, '') as alternate_contact, IFNULL(shipping_telephone, '') as shipping_contact" +
                " from serviceMaster where shipment_id = '" + shipmentid + "' ", null);

        if (getPhone.getCount() > 0) {
            getPhone.moveToFirst();

            getCustomerNo = getPhone.getString(getPhone.getColumnIndex("customer_contact"));
            getAlternateNo = getPhone.getString(getPhone.getColumnIndex("alternate_contact"));
            getShippingNo = getPhone.getString(getPhone.getColumnIndex("shipping_contact"));
            rb_branch.setVisibility(View.GONE);
            rb_groupleader.setVisibility(View.GONE);

            if(getCustomerNo.equalsIgnoreCase(getAlternateNo)){
                rb_customer.setVisibility(View.GONE);
                rb_alternate.setVisibility(View.VISIBLE);
            }
            if(getCustomerNo.equalsIgnoreCase(getShippingNo) ){
                rb_customer.setVisibility(View.GONE);
                rb_shipping.setVisibility(View.VISIBLE);

            }

            if(getAlternateNo.equalsIgnoreCase(getShippingNo)){
                rb_shipping.setVisibility(View.VISIBLE);
                rb_alternate.setVisibility(View.GONE);

            }



            if (getCustomerNo.equals("")) {
                rb_customer.setVisibility(View.GONE);

            }
            if (getAlternateNo.equals("")) {
                rb_alternate.setVisibility(View.GONE);
            }
            if (getShippingNo.equals("")) {
                rb_shipping.setVisibility(View.GONE);
            }





        }
        getPhone.close();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    checkId = checkedId;
                }
                mBottomSheetDialog.dismiss();
                if (checkId == R.id.rb_customer) {
                    callUser(getCustomerNo);
                } else if (checkId == R.id.rb_alternate) {
                    callUser(getAlternateNo);
                } else if (checkId == R.id.rb_shipping) {
                    callUser(getShippingNo);
                }

            }
        });


        procd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                if (checkId == R.id.rb_customer) {
                    callUser(getCustomerNo);
                } else if (checkId == R.id.rb_alternate) {
                    callUser(getAlternateNo);
                } else if (checkId == R.id.rb_shipping) {
                    callUser(getShippingNo);
                } else if (checkId == R.id.rb_branch) {
                    callUser(getBranchNo);
                } else if (checkId == R.id.rb_groupleader) {
                    callUser(getGroupLeaderNo);
                }
            }
        });


        mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
    }
    public void callUser(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

}
