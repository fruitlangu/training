/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.inthree.boon.deliveryapp.BuildConfig;
import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.NetTime.TrueTimeRx;
import com.inthree.boon.deliveryapp.forceupdateapp.ForceUpdateChecker;
import com.inthree.boon.deliveryapp.server.rest.RestClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.inthree.boon.deliveryapp.app.Constants.CUR_LATITUDE;
import static com.inthree.boon.deliveryapp.app.Constants.CUR_LONGITUDE;
import static com.inthree.boon.deliveryapp.app.Constants.DEVICE;
import static com.inthree.boon.deliveryapp.app.Constants.LATITUDE;
import static com.inthree.boon.deliveryapp.app.Constants.LOGIN_USER_EMAIL;
import static com.inthree.boon.deliveryapp.app.Constants.LOGIN_USER_ID;
import static com.inthree.boon.deliveryapp.app.Constants.LONGITUDE;
import static com.inthree.boon.deliveryapp.app.Constants.LastSyncTime;
import static com.inthree.boon.deliveryapp.app.Constants.ROLE_ID;
import static com.inthree.boon.deliveryapp.app.Constants.UN_DEL_CUR_LATITUDE;
import static com.inthree.boon.deliveryapp.app.Constants.UN_DEL_CUR_LONGITUDE;
import static com.inthree.boon.deliveryapp.app.Constants.UN_DEL_LATITUDE;
import static com.inthree.boon.deliveryapp.app.Constants.UN_DEL_LONGITUDE;
import static com.inthree.boon.deliveryapp.app.Constants.USER_ID;
import static com.inthree.boon.deliveryapp.app.Constants.USER_LANGUAGE;
import static com.inthree.boon.deliveryapp.app.Constants.USER_NAME;


/**
 * This is the singleton class of the app. It will be called once at the time of app installation.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class AppController extends MultiDexApplication {
    private static Context mContext;
    /**
     * Rest client instance
     */
    private static RestClient restClient;

    /**
     * SharedPreference instance
     */
    private static SharedPreferences preferences;

    /**
     * Player user agent
     */
    protected String userAgent;

    /**
     * Map to store Font
     */
    private HashMap<String, Typeface> fonts = new HashMap<>();

    /*
    * Language Method
    * */
    public static Locale myLocale;

    /**
     * Initialising preference
     *
     * @param preferences Object of shared preference
     */
    private static void setPreferences(SharedPreferences preferences) {
        AppController.preferences = preferences;
    }

    /**
     * GetPreference method to get the string preference value
     *
     * @param key          Identify the values stored
     * @param defaultValue If preference is empty
     * @return String Preference
     */
    public static String getStringPreference(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    /**
     * StorePreference method to store the boolean preference value
     *
     * @param key   Used to identify the values stored
     * @param value Used to map with the key
     */
    public static void storeBooleanPreferences(String key, Boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    /**
     * Helper method to retrieve a long value from {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static long getLongPreference(Context context, String key, long defaultValue) {
        long value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getLong(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a long value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setLongPreference(Context context, String key, long value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * StorePreference method to store the string preference value
     *
     * @param key   Used to identify the values stored
     * @param value Used to map with the key
     */
    public static void storeStringPreferences(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * GetPreference method to get the boolean preference value
     *
     * @param key          Used to identify the values stored
     * @param defaultValue Used if preference is empty
     * @return boolean Preference boolean value
     */
    public static Boolean getBooleanPreference(String key, Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * Get preference method to get the Integer preference value
     *
     * @param key          Used to identify the values stored
     * @param defaultValue Used if preference is empty
     * @return int Preference integer value
     */
    public static int getIntegerPreferences(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Get the cache expiration
     */
    private long cacheExpiration=3600;

    /**
     * Tag the application
     */
    private static final String TAG = AppController.class.getSimpleName();

    /**
     * Store preference value to store the integer preference value
     *
     * @param key   Used to identify the values stored
     * @param value Used to map with the key
     */
    public static void storeIntegerPreference(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Getting the rest client access through the context
     *
     * @return RestClient
     */
    public static RestClient getRestClient() {
        if (restClient == null) {
            restClient = new RestClient();
        }
        return restClient;
    }

    /**
     * Initializing the rest client in this application class
     *
     * @param restClient Object of the restClient
     */
    private static void setRestClient(RestClient restClient) {
        AppController.restClient = restClient;
    }


    public static void clearKey(String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(value);
        editor.apply();

    }

    /**
     * This is the method used to clear the whole preference
     */
    public static void clearPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void removePreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(LOGIN_USER_ID);
        editor.remove(USER_NAME);
        editor.remove(USER_ID);
        editor.remove(LATITUDE);
        editor.remove(LONGITUDE);
        editor.remove(CUR_LATITUDE);
        editor.remove(CUR_LONGITUDE);
        editor.remove(LOGIN_USER_EMAIL);
        editor.remove(UN_DEL_LATITUDE);
        editor.remove(UN_DEL_LONGITUDE);
        editor.remove(UN_DEL_CUR_LATITUDE);
        editor.remove(UN_DEL_CUR_LONGITUDE);
        editor.remove(DEVICE);
        editor.remove(LastSyncTime);
        editor.remove(USER_LANGUAGE);
        editor.remove(ROLE_ID);
        editor.apply();
    }


    /**
     * Method used to get the font details
     *
     * @param font Font name
     * @return Typeface
     */
    public Typeface getTypeface(String font) {
        if (!fonts.containsKey(font)) {
            Typeface tf = Typeface.createFromAsset(getAssets(), "Font/" + font);
            fonts.put(font, tf);
        }
        return fonts.get(font);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mContext = this;
        setRestClient(new RestClient());
        setPreferences(PreferenceManager.getDefaultSharedPreferences(this));



        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        int versionCode = BuildConfig.VERSION_CODE;
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.inthree.boon.deliveryapp");

        remoteConfigDefaults.put(MainActivity.KEY_UPDATE_BACKUP, "true");
        remoteConfigDefaults.put(MainActivity.KEY_UPDATE_USERNAME, "xxx");

        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);


        if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;;
        }


        firebaseRemoteConfig.fetch(cacheExpiration) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });

        initRxTrueTime();

    }


    /**
     * Initialize the TrueTime using RxJava. kani comments
     */
    private void initRxTrueTime() {
        DisposableSingleObserver<Date> disposable = TrueTimeRx.build()
                .withConnectionTimeout(31_428)
                .withRetryCount(100)
                .withSharedPreferencesCache(this)
                .withLoggingEnabled(true)
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Date>() {
                    @Override
                    public void onSuccess(Date date) {
                        Log.d(TAG, "Success initialized TrueTime :" + date.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "something went wrong when trying to initializeRx TrueTime", e);
                    }
                });
    }


    public static String getdevice(){
        String myDeviceModel = android.os.Build.MODEL;
        return  myDeviceModel;
    }

    public static void setLocale(String lang){

        myLocale =new Locale(lang);
        Resources res = mContext.getResources();
        DisplayMetrics dm=res.getDisplayMetrics();
        Configuration cf =res.getConfiguration();
        cf.locale=myLocale;
        res.updateConfiguration(cf, dm);

    }


}