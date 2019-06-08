package com.inthree.boon.deliveryapp.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.SummaryViewPageAdapter;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.fragments.DeliverSummaryFragments;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;

public class SummaryActivity extends AppCompatActivity {


    /**
     * View pager for display the number of tabs
     */
    private static ViewPager viewPager;

    /**
     * Each tablayout to display the fragment
     */
    private static TabLayout tabLayout;

    /**
     * Set the icons
     */
    private int[] tabIcons = {
            R.drawable.schedule
    };

    /**
     * Get the activity name
     */
    String activityName;

    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);//setting tab over viewpager

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);

        //Implementing tab selected listener over tablayout
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager
                switch (tab.getPosition()) {
                    case 0:
                        Logger.logInfo("TAB1");
                        break;
                    case 1:
                        Logger.logInfo("TAB2");
                        break;
                    case 2:
                        Logger.logInfo("TAB3");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * Setting the view pager
     *
     * @param viewPager Get the fragment of tabs
     */
    private void setupViewPager(ViewPager viewPager) {
        SummaryViewPageAdapter adapter = new SummaryViewPageAdapter(getSupportFragmentManager());
        adapter.addFrag(new DeliverSummaryFragments("SUMMARY"), "SUMMARY");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.e("Onstart", "MainonStart");

        /**
         * This is for tracking the classes when user working onit an app
         */

        /*Get the current activity name*/
        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "1", "0");
    }


    @Override
    protected void onStop() {
        super.onStop();
//        Log.e("Onstop", "MainonStop");
         /*Get the current activity name*/
        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "0", "0");
    }
}
