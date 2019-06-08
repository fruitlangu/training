package com.inthree.boon.deliveryapp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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
import com.inthree.boon.deliveryapp.activity.DeliveryActivity;
import com.inthree.boon.deliveryapp.activity.LoginActivity;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.forceupdateapp.ForceUpdateChecker;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.MyLocation;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.inthree.boon.deliveryapp.R.layout.activity_splash;



public class SplashActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ForceUpdateChecker.OnUpdateNeededListener {

    Animation anim_fade;
    RelativeLayout relativFirst;
    MediaPlayer mp;
    String user_language;
    private final static int REQUEST_CHECK_SETTINGS_GPS=2000;



    /**
     * Set the permission access
     */
    private String[] permissions = new String[]
            {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA, android.Manifest.permission
                    .ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                    .RECEIVE_SMS, android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS};
    private static Location loc;

    /**
     * Get the location of latitude and langitude
     */
    private GoogleApiClient googleApiClient;
    private Location mylocation;
    Locale myLocale;
    ExternalDbOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        //getWindow().setSoftInputMode(LayoutParams.SOFT_INFPUT_STATE_ALWAYS_HIDDEN);
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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        setContentView(activity_splash);
//        dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        //isStoragePermissionGranted();


       /* *
         * Get the latitude and langitude*/

        /*Get the latitude and langtitude*/
        // to Find the Location

        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    /*
   * Broadcast receiver to get battery level
   * */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            AppController.storeIntegerPreference("BatterLevel",level);
            Log.v("get_battery", String.valueOf(level));
        }
    };

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
    /**
     * Set the permission above 23
     *
     * @return the Boolean value
     */
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissions(SplashActivity.this, 2909)) {
                Log.v("Permission is granted1", "Permission is granted2");
                return true;
            } else {
                Log.v("Permission is revoked", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission is granted3", "Permission is granted");
            EnableGPSAutoMatically();
            return true;
        }
    }

    /**
     * Check the permission for higher version device for camera and gallery
     *
     * @param employeeDetailActivity Get the activity
     * @param code                   Get the code using this permission for call back
     * @return The value
     */
    private boolean checkPermissions(Activity employeeDetailActivity, int code) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(employeeDetailActivity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(employeeDetailActivity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), code);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2909)
            splashPermission(grantResults);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        try {
            if (mBatInfoReceiver != null)
                unregisterReceiver(mBatInfoReceiver);


        } catch (Exception e) {

        }
        super.onDestroy();

    }

    /**
     * Granted permission for above code onRequestPermission
     * check if the permission is granted or denied
     * * @param grantResults
     */
    public void splashPermission(int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("Permission", "Granted");
            EnableGPSAutoMatically();
        } else {
            Log.e("Permission", "Denied");
            EnableGPSAutoMatically();
        }
    }

    /**
     * Get the current location by using lat and longitude
     */
    private void getLocation(){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(final Location location){
                if(location!=null) {
                    loc = location;

                    Log.e("Latitude: ", String.valueOf(loc.getLatitude()));
                    Log.e("Longitude: ", String.valueOf(loc.getLongitude()));
                   /* AppController.storeStringPreferences("latitude", String.valueOf(loc.getLatitude()));
                    AppController.storeStringPreferences("Longitude",String.valueOf(loc.getLongitude()));*/


                    Log.e("storelatitude: ", String.valueOf(loc.getLatitude()));
                    Log.e("storeLongitude: ", String.valueOf(loc.getLongitude()));
                    AppController.setLongPreference(SplashActivity.this,"storelatitude",Double.doubleToRawLongBits(loc
                            .getLatitude()));
                    AppController.setLongPreference(SplashActivity.this,"storeLongitude",Double.doubleToRawLongBits(loc
                            .getLongitude()));

                }
            }
        };
        MyLocation myLocation = new MyLocation();
        if(myLocation!=null) {
            myLocation.getLocation(SplashActivity.this, locationResult);
        }
    }

    /**
     * This startHomeActivity method is used for navigate from splash activity to main activity
     */
    private void startHomeActivity() {



        TimerTask tmrtsk = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent obj = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(obj);
                SplashActivity.this.finish();
            }
        };
        Timer task = new Timer();
        task.schedule(tmrtsk, 1000);  //2500
    }



    /**
     * Gps will be enable automatically
     */
    private void EnableGPSAutoMatically() {
         googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
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

                            int permissionLocation = ContextCompat
                                    .checkSelfPermission(SplashActivity.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                mylocation = LocationServices.FusedLocationApi
                                        .getLastLocation(googleApiClient);
                            }
                            startHomeActivity();
                            //toast("Success");
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Logger.logInfo("GPS is not on");


                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                // Ask to turn on GPS automatically
                                status.startResolutionForResult(SplashActivity.this,
                                        REQUEST_CHECK_SETTINGS_GPS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Logger.logInfo("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
               // String result=data.getStringExtra("result");
                startHomeActivity();
                getLocation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                //startHomeActivity();
            }
        }else if(requestCode== REQUEST_CHECK_SETTINGS_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    startHomeActivity();
                    getLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    startHomeActivity();
                    break;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.showShortMessage(this,"suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.showShortMessage(this,"failed");
    }


    /**
     * Force update to call the method
     *
     * @param updateUrl
     * @param updateStaBool
     */
    @Override
    public void onUpdateNeeded(final String updateUrl, boolean updateStaBool) {
        if(updateStaBool==true) {

            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
//        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.setTitleText(getResources().getString(R.string.force_update))
                    .setContentText(getResources().getString(R.string.force_update_name))
                    .setConfirmText(getResources().getString(R.string.force_update_confirm))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            redirectStore(updateUrl);
                            sDialog.dismissWithAnimation();
                        }
                    }).setCancelText(getResources().getString(R.string.force_update_ok)).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                    SplashActivity.this.finish();
                }
            })
                    .show();
            sweetAlertDialog.setCancelable(false);



          /*  AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("New version available")
                    .setMessage("Please, update app to new version to continue reposting.")
                    .setPositiveButton("Update",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    redirectStore(updateUrl);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("No, thanks",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    SplashActivity.this.finish();

                                }
                            }).create();

            dialog.show();
            dialog.setCancelable(false);*/
        }else if(updateStaBool==false){
            isStoragePermissionGranted();
        }
    }


    /**
     * Redirect to playstore directly whether if it is not update properly
     * @param updateUrl
     */
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}