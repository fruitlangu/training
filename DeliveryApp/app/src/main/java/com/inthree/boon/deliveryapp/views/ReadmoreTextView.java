/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.views;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.inthree.boon.deliveryapp.R;


/**
 * This class is used to set the view more option in textview
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class ReadmoreTextView extends ClickableSpan {

    /**
     * Instance of the class
     */
    private Activity activity;

    /**
     * Default construtor
     *
     * @param activity Instance of the class
     */
    public ReadmoreTextView(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View widget) {
        /**
         * Unused method
         */
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ContextCompat.getColor(activity, R.color.read_more_text_color));
    }
}
