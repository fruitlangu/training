package com.inthree.boon.deliveryapp.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.R;

public class FirstActivity extends AppCompatActivity {

    Button btn_login, btn_qrcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        getVersionInfo();

        btn_login = (Button)findViewById(R.id.btnLogin);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
            }
        });
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


    public  void onBackPressed() {
        finish();
    }
}
