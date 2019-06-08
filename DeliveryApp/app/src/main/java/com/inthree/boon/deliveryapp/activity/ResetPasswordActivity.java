package com.inthree.boon.deliveryapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.request.ResetPasswordReq;
import com.inthree.boon.deliveryapp.response.ResetPasswordResp;
import com.inthree.boon.deliveryapp.server.rest.InthreeApi;
import com.inthree.boon.deliveryapp.server.rest.RestClient;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_oldPassword;
    EditText et_newPassword;
    EditText et_confirmPassword;
    Button bt_resetPass;
    private ArrayList<ResetPasswordReq> changePassList;
    ProgressDialog progressDoalog;
    InthreeApi apiService;

    /**
     * Get the activity name
     */
    String activityName;

    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;

    /**
     * Get the lat and lagititude
     */
    String lat;

    /**
     * Get the langtitude
     */
    String lang;

    /**
     * Get the battery level
     */
    private int batteryLevel;
    private double latitud;
    private double langitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        et_oldPassword = (EditText) findViewById(R.id.et_oldPassword);
        et_newPassword = (EditText) findViewById(R.id.et_newPassword);
        et_confirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
        bt_resetPass = (Button) findViewById(R.id.bt_resetPass);
        bt_resetPass.setOnClickListener(this);

        latitud =Double.longBitsToDouble( AppController.getLongPreference(this,"storelatitude", -1));
        langitude = Double.longBitsToDouble(AppController.getLongPreference(this,"storeLongitude", -1));

        lat = String.valueOf(latitud);
        lang = String.valueOf(langitude);

        if(lat==null){
            lat="0.0";
        }
        if(lang==null){
            lang="0.0";
        }

        batteryLevel=AppController.getIntegerPreferences("BatterLevel",0);
    }


    private void changePassword() {
        progressDoalog = new ProgressDialog(ResetPasswordActivity.this);
//        progressDoalog.setMax(100);
        progressDoalog.setMessage("Resetting Password in progress");
        progressDoalog.setTitle("Delivery App");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();

        String txt_newPass = et_oldPassword.getText().toString();
        String txt_pass_one = et_newPassword.getText().toString();
        String txt_pass_two = et_confirmPassword.getText().toString();
        RequestBody otp = RequestBody.create(MediaType.parse("text/plain"), txt_newPass);
        RequestBody passOne = RequestBody.create(MediaType.parse("text/plain"), txt_pass_one);
        RequestBody passTwo = RequestBody.create(MediaType.parse("text/plain"), txt_pass_two);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "");

        RequestBody runnerId = RequestBody.create(MediaType.parse("text/plain"), Constants.USER_ID);
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), lat);
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), lang);
        RequestBody batterLevel = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(batteryLevel));
        RequestBody deviceInfo = RequestBody.create(MediaType.parse("text/plain"), AppController.getdevice());

        InthreeApi apiService =
                RestClient.getRetroService().create(InthreeApi.class);

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(getResources().getString(R.string.base_url)).
//        addConverterFactory(GsonConverterFactory.create())
//                .build();
//        apiService = retrofit.create(InthreeApi.class);


//        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
//        Call<RegisterResp> call = apiService.setRegistrationDetails(fileToUpload, filename,uname, pass, fname, lname);
        Call<ResetPasswordResp> call = apiService.setChangePassword(userId, otp, passOne, passTwo,latitude,longitude,
                deviceInfo,batterLevel,runnerId);
//        Call<RegisterResp> call = apiService.setRegistrationDetails1(reg.getUsername(), reg.getPassword(), reg.getPhoneNumber(), reg.getEmailId());
        call.enqueue(new Callback<ResetPasswordResp>() {
            @Override
            public void onResponse(Call<ResetPasswordResp> call, Response<ResetPasswordResp> response) {
//                Log.v("msg", "Response message: " + "dgdfgdfg");
                ResetPasswordResp serverResponse = response.body();
                if (serverResponse != null) {
                    if (response.isSuccessful()) {
                        changePassList = response.body().getChangePass();
                        for (int i = 0; i < changePassList.size(); i++) {
                            String res_msg = changePassList.get(i).getRes_msg();
                            String res_code = changePassList.get(i).getRes_code();
                            String user_id = changePassList.get(i).getUser_id();
                            progressDoalog.dismiss();
                            if (res_msg.equals("")) {
                                progressDoalog.dismiss();
                                Toast.makeText(ResetPasswordActivity.this, res_msg, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                                intent.putExtra("user_id", user_id);
                                startActivity(intent);
                            } else if (res_msg.equals("")) {
                                progressDoalog.dismiss();
                                Toast.makeText(ResetPasswordActivity.this, res_msg, Toast.LENGTH_SHORT).show();
                            }
//                            Log.i("youngresponse", "Response message: " + res_msg);
                            Toast.makeText(ResetPasswordActivity.this, res_msg, Toast.LENGTH_SHORT).show();
                        }
//                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                    } else {
                        changePassList = response.body().getChangePass();
                        for (int i = 0; i < changePassList.size(); i++) {
                            String res_msg = changePassList.get(i).getRes_msg();
                            String res_code = changePassList.get(i).getRes_code();
//                            Log.i("youngresponse", "Response message: " + res_msg);
                            Toast.makeText(ResetPasswordActivity.this, res_msg, Toast.LENGTH_SHORT).show();
                        }
//                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    assert serverResponse != null;
//                    Log.v("youngresponse", serverResponse.toString());
                }

            }

            @Override
            public void onFailure(Call<ResetPasswordResp> call, Throwable t) {
//                Log.i("youngresponse", "Response message: " + "fail");
                String message = t.getMessage();
//                Log.d("failure", message);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == bt_resetPass) {
            changePassword();
        }
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
}
