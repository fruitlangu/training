package com.inthree.boon.deliveryapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.adapter.PagerAdapter;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;

import java.util.Locale;

public class MenuStartActviity extends AppCompatActivity {
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.pending,
            R.drawable.success,
            R.drawable.faild
    };

    /**
     * Get the activity name
     */
    String activityName;

    /**
     * Get the status from while click on chart activity
     */
    Intent status;

    /**
     * Get the order status of product
     */
    String orderStatus;

    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;
    String user_language;
    Locale myLocale;


    private  ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_start);

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

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);




        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        status=getIntent();
        orderStatus=status.getStringExtra("status");


            tabLayout.addTab(tabLayout.newTab().setText(R.string.pending));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.sucess));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.faild));





        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        setupTabIcons();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if(orderStatus!=null) {
            if (orderStatus.equals("Success")) {
                viewPager.setCurrentItem(1);
            } else if (orderStatus.equals("Failed")) {
                viewPager.setCurrentItem(2);
            } else if (orderStatus.equals("Pending")) {
                viewPager.setCurrentItem(0);
            }
        }
        tabLayout.setTabTextColors(
                ContextCompat.getColor(this, R.color.tab_select ),
                ContextCompat.getColor(this, R.color.tab_unselect)
        );

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());


               // tabLayout.getChildAt(tab.getPosition()).setBackgroundColor(getResources().getColor(R.color.pending));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //tabLayout.getChildAt(tab.getPosition()).setBackgroundColor(getResources().getColor(R.color.sucess));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void setupTabIcons() {

            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);

    }


    private void setTabs(){
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 0) {
//                    tabOne1.setTextColor(getResources().getDrawable(R.id.tabs));
//

                } else if (position == 1) {

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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


    private void setLocale(String lang){

        myLocale =new Locale(lang);
        Resources res=getResources();
        DisplayMetrics dm=res.getDisplayMetrics();
        Configuration cf =res.getConfiguration();
        cf.locale=myLocale;
        res.updateConfiguration(cf, dm);

        super.onRestart();
//        Intent intent =new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

}
