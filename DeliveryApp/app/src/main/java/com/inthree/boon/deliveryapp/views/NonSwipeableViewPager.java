/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;


import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.Logger;

import java.lang.reflect.Field;

/**
 * This class is used to set the disable for view pager swipe option.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class NonSwipeableViewPager extends ViewPager {

    /**
     * View pager Constructor with context
     *
     * @param context Activity context
     */
    public NonSwipeableViewPager(Context context) {
        super(context);
        setMyScroller();
    }

    /**
     * View pager Constructor with context and attributes
     *
     * @param context Activity context
     * @param attrs   Attributes
     */
    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        /**
         * Never allow swiping to switch between pages
         */
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /**
         * Never allow swiping to switch between pages
         */
        return false;
    }

    /**
     * This method is responsible for the smooth scrolling
     */
    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField(getResources().getString(R.string.scroll_key));
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            Logger.logInfo(String.valueOf(e));
        }
    }

    /**
     * Scroller class used to implement the scroll action
     */
    public class MyScroller extends Scroller {

        /**
         * Scroller Constructor used to call this class
         *
         * @param context Activity Context
         */
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
        }
    }
}