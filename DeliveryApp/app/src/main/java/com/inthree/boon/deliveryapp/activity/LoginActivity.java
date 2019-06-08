package com.inthree.boon.deliveryapp.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.NetTime.TimeSingleton;
import com.inthree.boon.deliveryapp.NetTime.TrueTimeRx;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Logger;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.fcm.MyFirebaseInstanceIDService;
import com.inthree.boon.deliveryapp.request.LoginReq;
import com.inthree.boon.deliveryapp.response.LoginResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.StringOperationsUtils;
import com.inthree.boon.deliveryapp.utils.SyncService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inthree.boon.deliveryapp.app.Constants.ApiHeaders.BASE_URL;

public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase database;
    public static final String MyPREFERENCES = "MyPrefs";
    Context mContext;

//    EditText et_username;
AppCompatEditText et_username;
//    EditText et_password;
AppCompatEditText et_password;
    Button bt_login;
    MyFirebaseInstanceIDService myfirebaseId;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private ArrayList<LoginResp> loginList;
    private ArrayList<LoginResp> languageList;

    Button btn_login, btn_qrcode;


    private StringOperationsUtils strop;

    /**
     * Get the user name by using shared preference
     */
    private String getUserName;


    ProgressIndicatorActivity dialogLoading;
    public static boolean trueTime;
    String user_language;
    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        user_language = AppController.getStringPreference(Constants.USER_LANGUAGE,"");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.btn_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.boonbox_tit);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        et_username = (AppCompatEditText) findViewById(R.id.et_username);
        et_password = (AppCompatEditText) findViewById(R.id.et_password);


//        Log.v("data_loc",""+ String.valueOf(this.getApplicationInfo().dataDir));
//        Log.v("data_loc",""+ String.valueOf(this.getFilesDir()));
        strop = new StringOperationsUtils();


        btn_login = (Button) findViewById(R.id.btnLogin);

        if (getCurProcessName(getApplicationContext()).equals("com.inthree.boon.deliveryapp")) {
            ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);

            database = dbOpenHelper.openDataBase();
//            database.rawQuery("PRAGMA journal_mode=PERSIST", null);
//            database.close();
//            database = dbOpenHelper.openDataBase();
//            database.endTransaction();
//            database = dbOpenHelper.openDataBase();
//            database = dbOpenHelper.getWritableDatabase();

//            database.enableWriteAheadLogging();
            database.disableWriteAheadLogging();

        }

       /* if (android.os.Build.VERSION.SDK_INT >= 28) {
            ExternalDbOpenHelper helper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
            database = helper.getReadableDatabase();
            Log.v("CODENAME","- "+ Build.VERSION.SDK_INT);
        } else {
            ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);

            database = dbOpenHelper.openDataBase();
        }*/

       /* ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();*/
        getVersionInfo();


        getUserName = AppController.getStringPreference(Constants.USER_NAME, "");
//Log.v("getUserName","--"+getUserName);
        if (!getUserName.isEmpty()) {
            et_username.setText(getUserName);
        }

        /**
         * Store the app device and model for push notification
         */
        String deviceinfo = android.os.Build.BRAND + "," + android.os.Build.MODEL + "," + android.os.Build.SERIAL;
        AppController.storeStringPreferences(Constants.DEVICE, deviceinfo);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uname = et_username.getText().toString();
                String pass = et_password.getText().toString();

                /**
                 * Intialize the  network time  in devices  //commented kani
                 */

                trueTime = TrueTimeRx.isInitialized();


//                Log.v("clicked", "username: " + uname + "password: " + pass);
                if (TextUtils.isEmpty(uname)) {
                    Logger.showShortMessage(LoginActivity.this, getResources().getString(R.string.valid_user_name));
                } else if (TextUtils.isEmpty(pass)) {
                    Logger.showShortMessage(LoginActivity.this, getResources().getString(R.string.valid_pass));
                } else {
                    getUsername(uname, pass);
                }
            }
        });


        autoLogin();
//        getu();
    }


    /**
     * Call the webservice for check the username and password for login
     *
     * @param user_name
     * @param pass_word
     */
    public void getUserAccess(String user_name, String pass_word) {
        String getFirebaseId = AppController.getStringPreference(Constants.DEVICE_TOKEN_REGID, "");
        String getDeviceModel = AppController.getStringPreference(Constants.DEVICE, "");
        dialogLoading = new ProgressIndicatorActivity(LoginActivity.this);
        dialogLoading.showProgress();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        InthreeApi apiService = retrofit.create(InthreeApi.class);
        LoginReq login = new LoginReq();
        JSONObject paramObject = null;
        login.setUsername(user_name);
        login.setPassword(pass_word);
        try {
            paramObject = new JSONObject();
            paramObject.put("username", login.getUsername());
            paramObject.put("password", login.getPassword());
            paramObject.put("firebase_id", getFirebaseId); // updating firebase regid during login
            paramObject.put("device_info", getDeviceModel); // updating device model during login


            Log.v("getUserAccess", getFirebaseId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

        final Observable<LoginResp> observable = apiService.getLogin(requestBody).subscribeOn
                (Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<LoginResp>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginResp value) {
                loginList = value.getLogin();
                languageList = value.getLanguageArray();

                if (value.getResMsg().equals("login success")) {
//                    Log.v("get_response", value.getResMsg());
                    for (int i = 0; i < loginList.size(); i++) {
//                        Log.v("login_user", loginList.get(i).getId());
                        database.rawQuery("Delete from UserMaster ", null);

                        String query = "INSERT INTO UserMaster (user_id, username, password,firstname,lastname,Email,Mobile,status,aadhaar,user_role ) " +
                                "VALUES ('" + loginList.get(i).getId() + "','" + loginList.get(i).getUsername() + "','" +
                                loginList.get(i).getPassword() + "','" + loginList.get(i).getFirstname() + "'," +
                                "'" + loginList.get(i).getLastname() + "','" + loginList.get(i).getEmail() + "','" +
                                loginList.get(i).getMobileNo() + "','O'," + loginList.get(i).getAadhaar_feature() + "," + loginList.get(i).getRole() + ")";
                        database.execSQL(query);

                        for(int j = 0; j < loginList.get(i).getLanguageArray().size(); j++){
                            Log.v("getLanguageArray",loginList.get(i).getLanguageArray().get(j).getLanguage());

                            Cursor uname = database.rawQuery("Select * from LanguageMaster where language_id = '" + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "' ", null);

                            if (uname.getCount() == 0) {
                                uname.moveToFirst();
                                String insertUndeliveredReason = "Insert into LanguageMaster (language ,language_id,is_active) Values('" + loginList.get(i).getLanguageArray().get(j).getLanguage() + "', " + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "" +
                                        "," + loginList.get(i).getLanguageArray().get(j).getLanguage_active() + ")";
                                database.execSQL(insertUndeliveredReason);
                            }else {
                                String updateUndeliveredReason = "Update LanguageMaster set language_id = " + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "," +
                                        "language = '" + loginList.get(i).getLanguageArray().get(j).getLanguage() + "', is_active ='" + loginList.get(i).getLanguageArray().get(j).getLanguage_active() + "' where language_id = '" + loginList.get(i).getLanguageArray().get(j).getLanguageId() + "'";
                                database.execSQL(updateUndeliveredReason);
                            }
                        }

                        AppController.storeStringPreferences(Constants.LOGIN_USER_ID, loginList.get(i).getId());
                        AppController.storeStringPreferences(Constants.LOGIN_USER_EMAIL, loginList.get(i).getEmail());
                        AppController.storeStringPreferences(Constants.USER_NAME, loginList.get(i).getUsername());
                        AppController.storeStringPreferences(Constants.ROLE_ID, loginList.get(i).getRole());
                        AppController.setLocale("en");

                        startService(new Intent(LoginActivity.this, SyncService.class));
                        Intent startMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(startMainActivity);
                        finish();
                        dialogLoading.dismiss();
//                        finish();

                    }
                } else if (value.getResMsg().equals("login failed")) {
                    Logger.showShortMessage(LoginActivity.this, getResources().getString(R.string.invalid_login));
                    dialogLoading.dismiss();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("loginerror", e.toString());
                Logger.showShortMessage(LoginActivity.this, "Network Error");
                dialogLoading.dismiss();
            }

            @Override
            public void onComplete() {
//                Log.v("inhere", "--");
//                observable.unsubscribeOn(Schedulers.newThread());
            }


        });
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

    public void autoLogin() {
        String get_uname = null;
        String get_pass = null;

        Cursor uname = database.rawQuery("Select * from UserMaster ", null);
//        Log.v("clicked", " - "+uname);
        if (uname.getCount() > 0) {
            uname.moveToFirst();
//            Log.v("clicked", String.valueOf(uname.getCount()));

            get_uname = uname.getString(uname.getColumnIndex("username"));
            get_pass = uname.getString(uname.getColumnIndex("password"));


            if (get_uname.equals(get_uname) && get_pass.equals(get_pass)) {
                startService(new Intent(LoginActivity.this, SyncService.class));
                Intent startMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                AppController.storeStringPreferences(Constants.USER_NAME, get_uname);
                startActivity(startMainActivity);
                finish();
            } else if (!get_uname.equals(get_uname)) {
                AlertDialogCancel(this, getResources().getString(R.string.btn_login), getResources().getString(R
                        .string.wrong_user), getResources().getString(R.string.yes), getResources().getString(R.string
                        .no), get_uname, get_pass);
            } else if (!get_pass.equals(get_pass)) {
                Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void getUsername(String user_name, String pass_word) {

        Cursor uname = database.rawQuery("Select * from UserMaster ", null);

        if (uname.getCount() > 0) {
            uname.moveToFirst();
//            Log.v("clicked", String.valueOf(uname.getCount()));

            String get_uname = uname.getString(uname.getColumnIndex("username"));
            String get_pass = uname.getString(uname.getColumnIndex("password"));


            if (user_name.equals(get_uname) && pass_word.equals(get_pass)) {
                startService(new Intent(LoginActivity.this, SyncService.class));
                Intent startMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                AppController.storeStringPreferences(Constants.USER_NAME, get_uname);
                startActivity(startMainActivity);
                finish();
            } else if (!user_name.equals(get_uname)) {
                AlertDialogCancel(this, getResources().getString(R.string.btn_login), getResources().getString(R
                        .string.wrong_user), getResources().getString(R.string.yes), getResources().getString(R.string
                        .no), user_name, pass_word);
            } else if (!pass_word.equals(get_pass)) {
                Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
            }

        } else {
            if (Utils.checkNetworkAndShowDialog(this)) {
                getUserAccess(user_name, pass_word);
            } else {
                Logger.showShortMessage(this, this.getString(R.string.check_internet));
            }
        }
    }


    public void onBackPressed() {
        AppExitAlert();
    }


    /**
     * Alert dialog for get the cancel
     *
     * @param context    Get the context of an activity
     * @param content    Get the content
     * @param okmsg      Get the  ok message of text
     * @param canmessage Get the cancel message
     */
    public void AlertDialogCancel(final Context context, String title, String content, String okmsg, String
            canmessage, final String customerName, final String password) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setCancelText(canmessage).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                sweetAlertDialog.dismissWithAnimation();
            }
        })
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        database.rawQuery("Delete from UserMaster ", null);
                        database.rawQuery("Delete from orderheader ", null);
                        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getApplicationContext(), Constants.DB_NAME);
                        database = dbOpenHelper.deleteDatabase();
                        getUserAccess(customerName, password);
                        sDialog.dismissWithAnimation();

                    }
                })
                .show();
    }


    public void AppExitAlert() {

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.alertbox);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;

        Button yes = (Button) dialog1.findViewById(R.id.proceed);
        Button no = (Button) dialog1.findViewById(R.id.close);
        TextView txt_ale = (TextView) dialog1.findViewById(R.id.txt_title);
        TextView txt_msg = (TextView) dialog1.findViewById(R.id.txt_message);

        txt_ale.setText(R.string.app_name);
        yes.setText(R.string.yes);
        no.setText(R.string.no);
        txt_msg.setText(R.string.showexit);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(LoginActivity.this,
                        FirstActivity.class);
                startActivity(intent);*/
                finish();

                dialog1.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }


    private void getVersionInfo() {
        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textViewVersionInfo = (TextView) findViewById(R.id.txt_version);
        textViewVersionInfo.setText(String.format("Version. %s ", versionName));
    }


    public void getu() {
        Cursor getu = database.rawQuery("Select * from UserMaster ", null);
        if (getu.getCount() > 0) {
            while (!getu.isAfterLast()) {
                getu.moveToFirst();
                Log.v("getuvals", getu.getString(getu.getColumnIndex("password")) + "--"
                        + getu.getString(getu.getColumnIndex("username")));
                getu.moveToNext();
            }
        }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_icon, menu);

        return super.onCreateOptionsMenu(menu);
    }*/

    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

}
