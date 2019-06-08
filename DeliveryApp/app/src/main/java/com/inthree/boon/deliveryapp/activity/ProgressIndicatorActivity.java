package com.inthree.boon.deliveryapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.inthree.boon.deliveryapp.R;

/**
 * Created by Kanimozhi on 19-02-2018.
 */

public class ProgressIndicatorActivity  extends Dialog {

    /**
     * The context.
     */
    private Context context;

    /**
     * Default constructor which instantiates a new Loading progress dialog.
     *
     * @param context The context to use.
     */
    public ProgressIndicatorActivity(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Method to display the progress dialog calling this will show the dialog instantly visibility
     * needs to be checked by the super class implementation.
     */
    public void showProgress() {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_progressbar, null);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.MATCH_PARENT);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color
                    .TRANSPARENT));
            this.setCancelable(false);
            this.setContentView(view);
            this.show();

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                    PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
//            Log.e("Error", "Error");
        }

    }
}
