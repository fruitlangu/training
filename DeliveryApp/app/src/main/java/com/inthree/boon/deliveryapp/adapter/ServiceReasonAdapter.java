package com.inthree.boon.deliveryapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.response.ReasonVal;

import java.util.ArrayList;

public class ServiceReasonAdapter extends BaseAdapter {
    Context context;
    ArrayList<ReasonVal> reasonValList;
    LayoutInflater inflter;

    public ServiceReasonAdapter(Context applicationContext, ArrayList<ReasonVal> reasonValList) {
        this.context = applicationContext;
        this.reasonValList = reasonValList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return reasonValList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.servicereason_spinner, null);
        TextView names = (TextView) view.findViewById(R.id.textView);

        ReasonVal data = reasonValList.get(i);
        names.setText(data.getReason());
        Log.v("incomplete_reason", " - " + reasonValList.get(i));
        return view;
    }
}