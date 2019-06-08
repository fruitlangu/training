/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;


/**
 * This Class used to set the custom font in the Text view.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class TypefaceTextView extends TextView {

    /**
     * Typeface text view
     */
    private Typeface typeface;

    /**
     * Constructor to call the textView
     *
     * @param context Activity Context
     */
    public TypefaceTextView(Context context) {
        super(context);
    }

    /**
     * Constructor to set the attribute set.
     *
     * @param context Instance of the class
     * @param attrs   Font attribute set
     */
    public TypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getTypefaceFont(context, attrs);
    }

    /**
     * Constructor to set the attribute  with style attribute
     *
     * @param context      Instance of the class
     * @param attrs        Font attribute set
     * @param defStyleAttr Font attribute style
     */
    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getTypefaceFont(context, attrs);
    }

    /**
     * Constructor to set the attribute along with default style, default resource style and
     * resource id.
     *
     * @param context      Instance of the class
     * @param attrs        Font attribute set
     * @param defStyleAttr Font attribute style
     * @param defStyleRes  Font attribute style with resource id
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getTypefaceFont(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setTypeface(typeface);
    }

    /**
     * Method used to get the font from the assert.
     *
     * @param context Instance of the class
     * @param attrs   Font attribute
     */
    private void getTypefaceFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTextView);

        final int n = a.getIndexCount();
        for (int i = 0; i < n; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.TypefaceTextView_typeface) {
                restricted(context, a, attr);
            }
        }
        a.recycle();
    }

    /**
     * Method used to throw the illegal state exception if the context is restricted
     *
     * @param context Activity Context
     * @param a       Typed Array
     * @param attr    Attributes
     */
    private void restricted(Context context, TypedArray a, int attr) {
        if (context.isRestricted()) {
            throw new IllegalStateException("The " + getClass().getCanonicalName() + ":required attribute" +
                    " cannot " + "be used within a restricted context");
        }
        String fontName = a.getString(attr);
        typeface = ((AppController) getContext().getApplicationContext()).getTypeface(fontName);
    }
}
