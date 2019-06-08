package com.inthree.boon.deliveryapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.model.BranchVal;

import java.util.ArrayList;

public class CustomAdapter  extends ArrayAdapter {
    private Context context;
    private int textViewResourceId;
    private ArrayList<BranchVal> objects;
    public static boolean flag = false;
    public CustomAdapter(Context context, int textViewResourceId,
                         ArrayList<BranchVal> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);
        if (flag != false) {
            TextView tv = (TextView) convertView;
            tv.setText(objects.get(position).getName());
        }
        return convertView;
    }
}
