package com.inthree.boon.deliveryapp.adapter;

/**
 * Created by Kanimozhi on 19-12-2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.inthree.boon.deliveryapp.fragments.Failed;
import com.inthree.boon.deliveryapp.fragments.Pending;
import com.inthree.boon.deliveryapp.fragments.Sucess;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Pending tab1 = new Pending();
                return tab1;
            case 1:
                Sucess tab2 = new Sucess();
                return tab2;
            case 2:
                Failed tab3 = new Failed();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
