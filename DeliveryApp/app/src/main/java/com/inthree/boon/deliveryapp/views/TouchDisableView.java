/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * This is the Touch Disable used to track the touch event of the view and disable it.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class TouchDisableView extends ViewGroup {

    /**
     * View
     */
    private View mContent;

    /**
     * Touch disabled default value
     */
    private boolean mTouchDisabled = false;

    /**
     * Constructor class used to call this class from other class
     *
     * @param context Activity context
     */
    public TouchDisableView(Context context) {
        this(context, null);
    }

    /**
     * Constructor class used to call this class from other class
     *
     * @param context Activity context
     * @param attrs   Attribute set
     */
    public TouchDisableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Method used to get the content of the View
     *
     * @return View Content
     */
    public View getContent() {
        return mContent;
    }

    /**
     * Method used to set the content for the view either by adding or removing the view
     *
     * @param v View
     */
    public void setContent(View v) {
        if (mContent != null) {
            this.removeView(mContent);
        }
        mContent = v;
        addView(mContent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);

        final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
        final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
        mContent.measure(contentWidth, contentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        mContent.layout(0, 0, width, height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mTouchDisabled;
    }

    /**
     * Method used to disable the touch event
     *
     * @param disableTouch True or False
     */
    public void setTouchDisable(boolean disableTouch) {
        mTouchDisabled = disableTouch;
    }
}
