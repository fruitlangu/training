package com.inthree.boon.deliveryapp.adapter;

/**
 * Created by Kanimozhi on 19-12-2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.inthree.boon.deliveryapp.fragments.ServiceComplete;
import com.inthree.boon.deliveryapp.fragments.ServicePending;
import com.inthree.boon.deliveryapp.fragments.ServiceUncomplete;

public class ServiceAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ServiceAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ServicePending tab1 = new ServicePending();
                return tab1;
            case 1:
                ServiceComplete tab2 = new ServiceComplete();
                return tab2;
            case 2:
                ServiceUncomplete tab3 = new ServiceUncomplete();
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
