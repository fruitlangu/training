package com.inthree.boon.deliveryapp.fragments;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.MyValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class DeliverSummaryFragments extends Fragment {
    private View view;

    private String title;//String for tab title

    /**
     * Initialize the database
     */
    SQLiteDatabase database;

    /**
     * Display the piechart based on status
     */
    PieChart pieChart;

    /**
     * Get the values of y axis
     */
    PieDataSet dataSet;

    /**
     * Get the value of y axis
     */
    ArrayList<String> xVals;

    /**
     * Join the data of x and y values
     */
    PieData data;

    /**
     * Added all the y values
     */
    ArrayList<Entry> yvalues;

    /**
     * Display the pending count of order status
     */
    private TextView pendingCount;

    /**
     * Display the pending count of order status
     */
    private TextView pendCount;

    /**
     * Display the Success count of order status
     */
    private TextView successCount;

    /**
     * Display the Success count of order status
     */
    private TextView succCount;

    /**
     * Display the failed count of order status
     */
    private TextView failCount;

    /**
     * Display the failed count of order status
     */
    private TextView failedCount;

    /**
     * Set the color for different piechart
     */
    int[] colors = {};


    /**
     * Constructor of deliver summary fragments
     */
    public DeliverSummaryFragments() {
    }

    @SuppressLint("ValidFragment")
    public DeliverSummaryFragments(String title) {
        this.title = title;//Setting tab title
    }

    String user_language;
    Locale myLocale;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_deliver_summary_fragments, container, false);

        user_language = AppController.getStringPreference(Constants.USER_LANGUAGE,"");

        if(user_language.equals("tamil")){
            AppController.setLocale("ta");
        }else if(user_language.equals("telugu")){
            AppController.setLocale("te");
        }else if(user_language.equals("marathi")){
            AppController.setLocale("mr");
        }else if(user_language.equals("hindi")){
            AppController.setLocale("hi");
        }else if(user_language.equals("punjabi")){
            AppController.setLocale("pa");
        }else if(user_language.equals("odia")){
            AppController.setLocale("or");
        }else if(user_language.equals("bengali")){
            AppController.setLocale("be");
        }else if(user_language.equals("kannada")){
            AppController.setLocale("kn");
        }else if(user_language.equals("assamese")){
            AppController.setLocale("as");
        }else{
            AppController.setLocale("en");
        }
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getActivity(), Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();
        initView(view);
        return view;
    }


    /**
     * Initialze  all the attributes
     *
     * @param view Get the view of an activity
     */
    public void initView(View view) {
        pieChart = (PieChart) view.findViewById(R.id.piechart);
        //pendingCount = (TextView) view.findViewById(R.id.pending_count);
        pendCount = (TextView) view.findViewById(R.id.pend_count);
//        successCount = (TextView) view.findViewById(R.id.successful_count);
        succCount = (TextView) view.findViewById(R.id.success_count);
     //   failedCount = (TextView) view.findViewById(R.id.failed_count);
        failCount = (TextView) view.findViewById(R.id.fail_count);


        yvalues = new ArrayList<Entry>();
        ordercount();
        statusSummarDeliver();
        pieChart.setHighlightPerTapEnabled(true);
    }


    /**
     * Get the no. of order
     */
    public void ordercount() {
        Cursor getcount = database.rawQuery("SELECT count(order_number) as Count FROM orderheader", null);
        if (getcount.getCount() > 0) {
            getcount.moveToFirst();

            if (!getcount.isAfterLast()) {
                do {
                    String ordercount = getcount.getString(getcount.getColumnIndex("Count"));
                    pieChart.setCenterText(getResources().getString(R.string.dash_order) + ordercount);
                    pieChart.setCenterTextSize(24f);
                    pieChart.setCenterTextColor(Color.BLUE);
                } while (getcount.moveToNext());
            }
            getcount.close();
        }
    }

    /**
     * Added the array list to integer element
     *
     * @param a Get the integer of an element
     * @param e Get the  element of added list
     * @return e
     */
    static int[] addElement(int[] a, int e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    /**
     /**
     * Display the piechart for dash board order status
     */
    public void statusSummarDeliver() {
        yvalues.clear();
        Cursor needSuccess = database.rawQuery("Select Count(order_number)  from orderheader  where " +
                "(sync_status = " + "'U' OR sync_status ='C') AND (delivery_status='delivered' OR " +
                "delivery_status='partial') ", null);

        /*Success*/
        if (needSuccess.getCount() > 0) {
            needSuccess.moveToFirst();

            final int nesy = Integer.parseInt(needSuccess.getString(0));
            Log.e("S", String.valueOf(nesy));
            if (nesy > 0) {
//                successCount.setText(String.valueOf(nesy));
                succCount.setText(String.valueOf(nesy));
                yvalues.add(new Entry((float) nesy, 0));
            } else {
//                successCount.setText("0");
                succCount.setText("0");
                yvalues.add(new Entry((float) 0, 0));
            }

        }


        /*Failed*/
        Cursor needFailed = database.rawQuery("Select  Count(order_number)  from orderheader  where delivery_status = " +
                "'undelivered' AND  (sync_status = 'U' OR sync_status ='C') ", null);
        if (needFailed.getCount() > 0) {
            needFailed.moveToFirst();
            final int nesy = Integer.parseInt(needFailed.getString(0));
            Log.e("F", String.valueOf(nesy));
            if (nesy > 0) {
//                failedCount.setText(String.valueOf(nesy));
                failCount.setText(String.valueOf(nesy));
                yvalues.add(new Entry((float) nesy, 1));
            } else {
//                failedCount.setText("0");
                failCount.setText("0");
                yvalues.add(new Entry((float) 0, 1));
            }
        }

          /*Pending*/
        Cursor needpend = database.rawQuery("Select  Count(order_number)  from orderheader  where sync_status = " +
                "'P'", null);
        if (needpend.getCount() > 0) {
            needpend.moveToFirst();
            final int nesy = Integer.parseInt(needpend.getString(0));
            Log.e("P", String.valueOf(nesy));
            if (nesy > 0) {
                yvalues.add(new Entry((float) nesy, 2));
//                pendingCount.setText(String.valueOf(nesy));
                pendCount.setText(String.valueOf(nesy));
            } else {
//                pendingCount.setText("0");
                pendCount.setText("0");
                yvalues.add(new Entry((float) 0, 2));
            }
        }

        needpend.close();
        needSuccess.close();
        needFailed.close();


        dataSet = new PieDataSet(yvalues, "");


        xVals = new ArrayList<String>();
        for (int i = 0; i < yvalues.size(); i++) {
            if (yvalues.get(i).getXIndex() == 0) {
                colors = addElement(colors, Color.parseColor("#e7a528"));
                xVals.add("Total number of Shipped");
            }
            if (yvalues.get(i).getXIndex() == 1) {
                colors = addElement(colors, Color.parseColor("#c11e0c"));
                xVals.add("Total number of Pending");
            }
            if (yvalues.get(i).getXIndex() == 2) {
                colors = addElement(colors, Color.parseColor("#166b94"));
                xVals.add("Total number of Delivered");
            }
        }

        dataSet.setColors(colors);
        data = new PieData(xVals, dataSet);
        data.setValueFormatter(new MyValueFormatter());
        pieChart.setTransparentCircleRadius(10);
        pieChart.setData(data);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        data.setValueTextSize(22f);
        data.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.dark_white));
        pieChart.setDrawSliceText(false);
        pieChart.highlightValues(null);
        dataSet.setSliceSpace(2f);
        pieChart.setDescription("");
        pieChart.setMaxAngle(900);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.invalidate();
        pieChart.animateXY(1400, 1400);
    }

    private void setLocale(String lang){

        myLocale =new Locale(lang);
        Resources res=getResources();
        DisplayMetrics dm=res.getDisplayMetrics();
        Configuration cf =res.getConfiguration();
        cf.locale=myLocale;
        res.updateConfiguration(cf, dm);

    }

}
