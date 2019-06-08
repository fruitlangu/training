package com.inthree.boon.deliveryapp.model;

/**
 * Created by kanimozhi on 23-10-2017.
 */

public class SavedAddress {
    String Latitude, Longitude;

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}