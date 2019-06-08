package com.inthree.boon.deliveryapp.forceupdateapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class ForceUpdateChecker {

    private static final String TAG = ForceUpdateChecker.class.getSimpleName();

    public static final String KEY_UPDATE_REQUIRED = "del_force_update_required";
    public static final String KEY_CURRENT_VERSION = "del_force_update_current_version";
    public static final String KEY_UPDATE_URL = "del_force_update_store_url";




    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl, boolean updateStaBool);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public void check() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        //Log.v("update_required","false");



        if (isInternetAvailable()) {






            if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {

                Log.v("update_required", "true");
                String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
                double currenVer = Double.parseDouble(currentVersion);
                String appVersion = getAppVersion(context);
                double appVer = Double.parseDouble(getAppVersion(context));
                String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);
                /*if (!TextUtils.equals(currentVersion, appVersion)*/
                if (appVersion.compareTo(currentVersion) < 0 && onUpdateNeededListener != null) {
                    Log.v("update_required", "update");
                    Log.v("update_required", currentVersion);
                    Log.v("update_required", appVersion);
                    onUpdateNeededListener.onUpdateNeeded(updateUrl, true);
                } else {
                    onUpdateNeededListener.onUpdateNeeded(updateUrl, false);
                    Log.v("update_required", "not update");
                    Log.v("update_required", currentVersion);
                    Log.v("update_required", appVersion);
                       /* Intent obj = new Intent(context, MainActivity.class);
                        context.startActivity(obj);*/
                }
            } else {

                onUpdateNeededListener.onUpdateNeeded("updateURL", false);
                Log.v("update_required", "false");
            }
        } else {

            Log.v("update_required", "no internet");
            onUpdateNeededListener.onUpdateNeeded("updateURL", false);
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();

            return forceUpdateChecker;
        }
    }
}
