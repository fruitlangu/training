package com.inthree.boon.deliveryapp.utils;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NavigationTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Activity for context of an activty
     */
    public Context context;

    /**
     * Activity for context of an activty
     */
    public Activity activity;

    /**
     * Get the latitude
     */
    public double latitude=0.0;

    /**
     * Get the longtitude
     */
    public double langitude=0.0;

    /**
     * Get the latitude
     */
    public double lat;

    /**
     * Get the langitude
     */
    public double lang;

    /**
     * set the database
     */
    private SQLiteDatabase database;

    /**
     * Get the app version name
     */
    String version;

    String battery_level;
    String deviceModel;

    public NavigationTracker(Context context) {
        this.context = context;
        activity = (Activity) context;
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(context, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();
    }


    public void trackingClasses(String page, String timeSetUP, String shipmentId) {



        // check if GPS enabled
     /*   GPSTracker gpsTracker = new GPSTracker(context,activity);

        latitude = stringLatitude;

        langitude = stringLongitude;*/

        latitude =Double.longBitsToDouble( AppController.getLongPreference(activity,"storelatitude", -1));
        langitude = Double.longBitsToDouble(AppController.getLongPreference(activity,"storeLongitude", -1));

        battery_level = String.valueOf(AppController.getIntegerPreferences("BatterLevel",0));
        deviceModel = AppController.getStringPreference(Constants.DEVICE, "");

        String stringLatitude = String.valueOf(latitude);
        String stringLongitude =  String.valueOf(langitude);

        if (stringLatitude==null) {
            latitude = 0.0;
        }

        if (stringLongitude == null) {
            langitude = 0.0;
        }

        Log.e("currentLat", String.valueOf(latitude));
        Log.e("currentLang", String.valueOf(langitude));

        String userName=  AppController.getStringPreference(Constants.USER_NAME, "");

        /**
         * Current Date for during tracking the classes
         */
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        /**
         * Get the current time for during tracking the classes
         */
//        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String currentTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());

        /**
         * Get the appversion name for boonbox
         */
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        /**
         * Get the application name of an boon box
         */
        String appName = String.valueOf(context.getApplicationInfo().loadLabel(context.getPackageManager()));

        Log.e("lati", String.valueOf(latitude));
        Log.e("longit", String.valueOf(langitude));
        Log.e("currendate",currentDate);
        Log.e("currenttime",currentTime);
        Log.e("appname",appName);
        Log.e("page",page);
        Log.e("version",version);
        Log.e("username", userName);
        Log.e("timesetup",timeSetUP);

        if(shipmentId!=null)
        Log.e("shipmentId",shipmentId);


        /**
         * Whether check the timesetup common for in and our if there is timesetup 1 to be insert or else update the
         * query
         */
        if (timeSetUP.equalsIgnoreCase("1")) {
            String query = "INSERT INTO pageTracker (page, date, time_in, time_out, lat_in,long_in,lat_out," +
                    "long_out,shipment_id,app_version,app_name,user_name,status,device_info,battery) " +
                    "VALUES ('" + page + "','" + currentDate + "','" + currentTime + "','0','" + stringLatitude + "'," +
                    "'" + stringLongitude + "','0','0','" + shipmentId + "','" + version +
                    "','" +appName  + "','" + userName + "','P','"+deviceModel+"', '"+battery_level+"')";
            database.execSQL(query);

            Cursor count=database.rawQuery("SELECT * from pageTracker",null);

            Log.e("count", String.valueOf(count.getCount()));
        } else if (timeSetUP.equalsIgnoreCase("0")) {
            database.execSQL("UPDATE pageTracker set time_out='" + currentTime + "', lat_out = '" + stringLatitude + "' ," +
                    " long_out ='" + stringLongitude + "' where page ='" + page + "' AND time_out = '0' AND status = 'P' ");
            Cursor count=database.rawQuery("SELECT * from pageTracker",null);
            Log.e("count", String.valueOf(count.getCount()));
        }
    }


    /**
     * Gps will be enable automatically
     */
    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            //toast("Success");
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            toast("GPS is not on");
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(activity, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    /**
     * Toast message for the enable gps
     *
     * @param message
     */
    private void toast(String message) {
        try {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            // log("Window has been closed");
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }


}
