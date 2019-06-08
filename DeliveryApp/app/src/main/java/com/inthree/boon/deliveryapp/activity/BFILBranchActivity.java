package com.inthree.boon.deliveryapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.BrachDeliveryAdapter;
import com.inthree.boon.deliveryapp.adapter.CustomAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.model.BranchVal;
import com.inthree.boon.deliveryapp.pendingModel;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.ydcool.lib.qrmodule.activity.QrScannerActivity;

public class BFILBranchActivity extends AppCompatActivity {


    /**
     * QRCODE SCAN
     */
    public static final int QR_REQUEST_CODE = 200;


    /**
     * BFIL branch adapter
     */
    BrachDeliveryAdapter pendingAdapter;

    /**
     * Add the order into model
     */
    public List<pendingModel> pendingArraylist;

    /**
     *
     */
    private ArrayList<pendingModel> msqlitebeans = new ArrayList<>();

    /**
     * set the values into model
     */
    pendingModel item;

    /**
     * Pass the context to adapter
     */
    Context mContext;


    Context fragContext;

    /**
     * Get an activity to pass the model
     */
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

    public CheckBox chkAll;

    /**
     * GEt the branch name into spinner
     */
    ArrayList<BranchVal> branchArray;

    /**
     * Load the master into spinner
     */
    private Spinner spinBranch;

    /**
     * Load the string
     */
    private String spinBranchItem;


    /**
     * By using get the order items in android
     */
    private String branchId = "";

    String invoiceResult;

    Button btn_bulk_delivery;

    String shipmentNumber;
    String order_num;
    String order_type;

    /**
     * check the qr for  shimpment number checking it
     */
    private String Shipment_Number;

    /**
     * check the qr for  shimpment number checking it
     */
    private String Customer_Name;

    /**
     * IF list is empty load the view
     */
    private ImageView emptyView;

    /**
     * Check the invoice id  for corresponding shipment number
     */
    private String invoice_id;
    private String check_invoice_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bfilbranch);

        rv_pending_adapter = (RecyclerView) findViewById(R.id.rv_pending_adapter);
        emptyView = (ImageView) findViewById(R.id.empty_view);
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

        branchArray = new ArrayList<>();

        ser_edit_pending = (SearchView) findViewById(R.id.search);
        spinBranch = (Spinner) findViewById(R.id.branch_spinner);

        btn_bulk_delivery =(Button) findViewById(R.id.btn_bulk_delivery);

        chkAll = (CheckBox) findViewById(R.id.Chk_all);

        mContext = this;


        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(mContext, DB_NAME);
        database = dbOpenHelper.openDataBase();

        String deliveryDetailsupdate = "UPDATE orderheader set check_bfil_order_status = '0', invoice_status ='0' where check_bfil_order_status !=0 AND delivery_to = '1'";
        database.execSQL(deliveryDetailsupdate);

        //  getOrderHeader();
        GridLayoutManager glm = new GridLayoutManager(this, 1);

        rv_pending_adapter.setLayoutManager(glm);
        BranchMaster();
        // populatePendingList();

        chkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectItem();
                if (msqlitebeans.size() > 0) {
                    if (((CheckBox) v).isChecked()) {
                        /*String deliveryDetailsupdate = "UPDATE orderheader set check_bfil_order_status = '1' where  check_bfil_order_status != '2' AND delivery_to = '1'  AND sync_status='P'";
                        database.execSQL(deliveryDetailsupdate);*/
                        pendingAdapter.selectAll(true);

                    } else if (!((CheckBox) v).isChecked()) {
                        String deliveryDetailsupdate = "UPDATE orderheader set check_bfil_order_status = '0' where  delivery_to = '1'  AND sync_status='P'";
                        database.execSQL(deliveryDetailsupdate);
                        pendingAdapter.selectAll(false);
                    }
                } else {
                    Utils.AlertDialogCancel(BFILBranchActivity.this, "Bfil Bulk Orders", getResources().getString(R.string.no_order), "OK", "Cancel");
                    chkAll.setChecked(false);
                }
            }
        });

        //  btn_bulk_delivery.setOnClickListener(BulkOnclick());

        btn_bulk_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BulkOnclick();
            }
        });

    }

    public void BranchMaster() {
        branchArray.clear();
        Cursor branchMaster = database.rawQuery("select branch_id,branch_name,status from BranchMaster where status = '1' ", null);
        BranchVal branchVal;

        if (branchMaster.getCount() > 0) {
            branchMaster.moveToFirst();
            while (!branchMaster.isAfterLast()) {

                branchVal = new BranchVal();

                // branchVal.setName(branchMaster.getString(branchMaster.getColumnIndex("branch_id")));
                branchVal.setName(branchMaster.getString(branchMaster.getColumnIndex("branch_name")));
                branchArray.add(branchVal);
                branchMaster.moveToNext();
            }
            branchMaster.close();
        }


        CustomAdapter adapter = new CustomAdapter(this,              // Use our custom adapter
                android.R.layout.simple_spinner_item, branchArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBranch.setAdapter(adapter);
        spinBranchItem = spinBranch.getSelectedItem().toString();


        if (spinBranchItem != null) {
            int spinnerPosition = adapter.getPosition(spinBranchItem);
            spinBranch.setSelection(spinnerPosition);
            adapter.notifyDataSetChanged();
        } else {

            spinBranch.setSelection(0);

            adapter.notifyDataSetChanged();
        }


        spinBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                CustomAdapter.flag = true;
                spinBranchItem = spinBranch.getSelectedItem().toString();
                System.out.println(spinBranchItem);


                // Set adapter flag that something
                spinBranch.setSelection(pos);

                getSelectItem();

            }
        });


    }


    public static void selectSpinnerItemByValue(Spinner spnr, long value) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItemId(position) == value) {
                spnr.setSelection(position);
                return;
            }
        }
    }


    public void getSelectItem() {
        Cursor branchMaster = database.rawQuery("select branch_id from BranchMaster where branch_name = '" + spinBranchItem + "' ", null);
        BranchVal branchVal;
        branchMaster.moveToFirst();
        if (branchMaster.getCount() > 0) {
            while (!branchMaster.isAfterLast()) {

                branchVal = new BranchVal();
                // branchVal.setName(branchMaster.getString(branchMaster.getColumnIndex("branch_id")));
                branchId = branchMaster.getString(branchMaster.getColumnIndex("branch_id"));

                branchMaster.moveToNext();
            }
            branchMaster.close();
        }

        populatePendingList();
    }


    public void populatePendingList() {
        msqlitebeans.clear();
        Cursor pendinglist = database.rawQuery("select * from orderheader where branch_code='" + branchId + "' AND sync_status = 'P' AND delivery_to='1'   ORDER BY valid ASC ", null);

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
                sqlitebeans_child.setInvoicenumber(invoiceResult);
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
                pendingAdapter = new BrachDeliveryAdapter(mContext, msqlitebeans, database, mContext);
                rv_pending_adapter.setAdapter(pendingAdapter);
                pendingAdapter.notifyDataSetChanged();
                rv_pending_adapter.invalidate();
            }

            if (msqlitebeans.size() > 0) {
                rv_pending_adapter.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                rv_pending_adapter.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }

        } else {
            rv_pending_adapter.setAdapter(null);
            if (msqlitebeans.size() > 0) {
                rv_pending_adapter.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                rv_pending_adapter.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            Constants.aadharCode = resultCode == RESULT_OK
                    ? data.getExtras().getString(QrScannerActivity.QR_RESULT_STR)
                    : "Scanned Nothing!";
            String scanResult = Constants.aadharCode;
            if (scanResult != null) {

                if (!scanResult.equalsIgnoreCase("Scanned Nothing!")) {
                    check_invoice_id = scanResult;
                    checkQr();
                }
            }
        }

    }


    /**
     * Onclick listener for bulk code
     *
     * @return
     */
    private void BulkOnclick() {

        getInvoice();


    }

    private void getInvoice() {
        Cursor pageTracker = database.rawQuery("Select IFNULL(Shipment_Number,0) as Shipment_Number," +
                "IFNULL(customer_name,0) as customer_name, IFNULL(invoice_id,0) as invoice_id," +
                "IFNULL(order_number,0) as order_number," +
                "IFNULL(order_type,0) as order_type from orderheader where check_bfil_order_status='1' AND sync_status='P' AND delivery_to = '1'", null);


        if (pageTracker.getCount() > 0) {
            pageTracker.moveToFirst();
            /* while (!pageTracker.isAfterLast()) {*/
            Shipment_Number = pageTracker.getString(pageTracker.getColumnIndex("Shipment_Number"));
            Customer_Name = pageTracker.getString(pageTracker.getColumnIndex("customer_name"));
            invoice_id = pageTracker.getString(pageTracker.getColumnIndex("invoice_id"));
            order_num = pageTracker.getString(pageTracker.getColumnIndex("order_number"));
            order_type = pageTracker.getString(pageTracker.getColumnIndex("order_type"));
            checkQr();
            pageTracker.close();

        } else {
            Cursor checkOrder = database.rawQuery("Select IFNULL(Shipment_Number,0) as Shipment_Number,IFNULL(order_number,0) as order_number,IFNULL(order_type,0) as order_type,check_bfil_order_status from orderheader where check_bfil_order_status = '2' AND delivery_to = 1",
                    null);
            if (checkOrder != null && checkOrder.getCount() > 0) {
                checkOrder.moveToFirst();

                Shipment_Number = checkOrder.getString(checkOrder.getColumnIndex("Shipment_Number"));
                order_num = checkOrder.getString(checkOrder.getColumnIndex("order_number"));
                order_type = checkOrder.getString(checkOrder.getColumnIndex("order_type"));

                Intent delivery = new Intent(BFILBranchActivity.this, BFILBulkDelivery.class);
                delivery.putExtra(Constants.SHIPMENT_NUMBER, Shipment_Number);
                delivery.putExtra(Constants.ORDER_ID, order_num);
                delivery.putExtra("order_type", order_type);
                startActivity(delivery);


                checkOrder.close();

            } else {
                Toast.makeText(BFILBranchActivity.this, "check order and submit", Toast.LENGTH_LONG).show();
            }

        }
    }


    /**
     * Alert box throw an check the validation for bulk orders QR
     */
    public void checkQr() {

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.qrcode_alert);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog1.setCancelable(false);
        Button submit = (Button) dialog1.findViewById(R.id.btn_submit);
        Button cancel = (Button) dialog1.findViewById(R.id.btn_cancel);
        TextView ship_id = (TextView) dialog1.findViewById(R.id.shipid);
        TextView cust_name = (TextView) dialog1.findViewById(R.id.custname);
        final TextView invoiceId = (TextView) dialog1.findViewById(R.id.tv_invoice_id);
        ImageView qr_code = (ImageView) dialog1.findViewById(R.id.lmg_qrcode);

        ship_id.setText(Shipment_Number);
        cust_name.setText(Customer_Name);

        if (!check_invoice_id.equals(""))
            invoiceId.setText(check_invoice_id);

        qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), QrScannerActivity.class),
                        QrScannerActivity.QR_REQUEST_CODE);
                dialog1.dismiss();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String invoiceCheck = null;
                if (!invoiceId.getText().toString().isEmpty()) {
                    invoiceCheck = invoiceId.getText().toString();
                }

                if (invoiceId.getText().toString().isEmpty()) {
                    invoiceId.setError("Enter the Invoice Id");

                } else if (invoiceCheck.equalsIgnoreCase(invoice_id)) {
                    String deliveryDetailsupdate = "UPDATE orderheader set check_bfil_order_status = '2' where Shipment_Number= '" + Shipment_Number + "' AND delivery_to = 1";
                    database.execSQL(deliveryDetailsupdate);
                    invoiceId.setText("");
                    check_invoice_id = "";
                    getInvoice();
                    dialog1.dismiss();
                } else {
                    invoiceId.setError("Enter the Invoice Id");
                    invoiceId.setText("");
                    Toast.makeText(BFILBranchActivity.this, "you have taken wrong invoice, take invoice again", Toast.LENGTH_LONG).show();
                }


            }
        });

        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String deliveryDetailsupdate = "UPDATE orderheader set check_bfil_order_status = '0', invoice_status ='0' where check_bfil_order_status != 0 AND delivery_to = '1'";
        database.execSQL(deliveryDetailsupdate);


        //  Update orderheader set check status = 0, invoice status =0 where  check status != 0 AND status = 'P'
    }
}
