package com.inthree.boon.deliveryapp.interfaces;


import com.inthree.boon.deliveryapp.model.SavedAddress;

import java.util.ArrayList;

/**
 * This is the interface for placed save dapter
 */
public interface SavedPlaceListener {

    public void onSavedPlaceClick(ArrayList<SavedAddress> mResultList, int position);
}
