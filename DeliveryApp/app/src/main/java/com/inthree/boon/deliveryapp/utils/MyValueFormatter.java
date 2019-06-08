package com.inthree.boon.deliveryapp.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;


/**
 * float format converted to decimal value for PieChart
 */
public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0"); // use one decimal if needed
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
        if(value > 0) {
            return mFormat.format(value) + "";
        }else{
            return "";
            // e.g. append a dollar-sign
        }
    }
}
