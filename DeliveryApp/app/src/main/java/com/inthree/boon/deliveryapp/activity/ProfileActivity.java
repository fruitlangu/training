package com.inthree.boon.deliveryapp.activity;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;

import java.io.File;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity {

ImageView img_email;

    Button btn_re_password;

    TextView txt_name;
    TextView txt_phone;
    TextView txt_email;
    SQLiteDatabase database;

    /**
     * Get the user id from the preference
     */
    private String userID;


    /**
     * Get the activity name
     */
    String activityName;

    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;
    String user_language;
    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*******   Database Open *************/
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        txt_name = (TextView)findViewById(R.id.txt_name) ;
        txt_phone = (TextView)findViewById(R.id.txt_phone) ;
        txt_email = (TextView)findViewById(R.id.txt_email) ;
        img_email = (ImageView)findViewById(R.id.img_email) ;

        getprofile();

        btn_re_password = (Button)findViewById(R.id.btn_pass);

        String uname = AppController.getStringPreference(Constants.LOGIN_USER_ID,"");
        userID=uname;
        getUsername();
//        showOlderRecords1();
        btn_re_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showalert(getString(R.string.under));
//                deleteOlderRecords();
//                showOlderRecords();

               /* Intent repass = new Intent(ProfileActivity.this, ResetPasswordActivity.class);
                startActivity(repass);*/
            }
        });

    }



    // ************************** Display Username *******************
    public void getprofile() {

        Cursor uname = database.rawQuery("Select IFNULL(firstname,0) AS firstname, IFNULL(lastname,0) AS lastname,IFNULL(mobile,0) AS mobile,IFNULL(email,0) AS email from UserMaster ", null);
        if (uname.getCount() > 0) {
            uname.moveToFirst();
            String UName = uname.getString(uname.getColumnIndex("firstname"));
            String FName = uname.getString(uname.getColumnIndex("lastname"));
//            Log.v("FName",FName);
            txt_name.setText(UName+" "+FName);
            String mobile = uname.getString(uname.getColumnIndex("mobile"));
            txt_phone.setText(mobile);
            String email = uname.getString(uname.getColumnIndex("email"));
            txt_email.setText(email);

        }
        uname.close();
    }


    public void getUsername() {

        Cursor uname = database.rawQuery("Select * from UserMaster where user_id = '"+ userID +"' ", null);

        if (uname.getCount() > 0) {
            uname.moveToFirst();
//            Log.v("clicked", String.valueOf(uname.getCount()));
            String get_uname = uname.getString(uname.getColumnIndex("username"));
            String get_pass = uname.getString(uname.getColumnIndex("password"));
            String FName = uname.getString(uname.getColumnIndex("lastname"));
            txt_name.setText(uname.getString(uname.getColumnIndex("firstname"))+" "+FName);
            txt_phone.setText(uname.getString(uname.getColumnIndex("Mobile")));
            txt_email.setText(uname.getString(uname.getColumnIndex("Email")));

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


    private void showalert(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage(message);
        // Alert dialog button
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();// use dismiss to cancel alert dialog
                    }
                });
        alertDialog.show();
    }


    public void deleteOlderRecords() {
        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.valid, D.delivery_proof, D.signature_proof, D.id_proof, D.Invoice_proof FROM orderheader O  LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number WHERE O.sync_status = 'U' AND (O.valid <= datetime('now','-1 day') OR O.valid > datetime('now')) ", null);
//    String deleteOrder = "SELECT FROM orderheader WHERE sync_status = 'U' AND (valid <= datetime('now','-1 day') OR valid > datetime('now'))";
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("order_number")));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("valid")));
                File fileImgDeliverProof = new File("/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
                if (fileImgDeliverProof.exists()) {
                    fileImgDeliverProof.delete();
                }
                File fileImgSignProof = new File("/data/data/com.inthree.boon.deliveryapp/files/UserSignature/" + deleteOrder.getString(deleteOrder.getColumnIndex("signature_proof")));
                if (fileImgSignProof.exists()) {
                    fileImgSignProof.delete();
                }
                File fileImgIDProof = new File("/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("id_proof")));
                if (fileImgIDProof.exists()) {
                    fileImgIDProof.delete();
                }
                File fileImgInvoiceProof = new File("/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("Invoice_proof")));
                if (fileImgInvoiceProof.exists()) {
                    fileImgInvoiceProof.delete();
                }
                deleteOrder.moveToNext();
            }
            String deleteOrderDetails = "DELETE FROM orderheader WHERE sync_status = 'U' AND ( valid <= datetime('now','-1 day') OR valid > datetime('now'))";
//        String deleteOrderDetails = "DELETE FROM orderheader WHERE sync_status = 'U' AND (valid <= datetime('now','-1 day') OR valid > datetime('now'))";
            String deleteDeliveryDetails = "DELETE FROM DeliveryConfirmation WHERE sync_status = 'U' AND ( valid <= datetime('now','-1 day') OR valid > datetime('now'))";
            database.execSQL(deleteOrderDetails);


        }
    }


    public void showOlderRecords1() {
        String file_path = String.valueOf(this.getFilesDir());
        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.signature_proof,0) as signature_proof, IFNULL(D.id_proof,0) as id_proof, IFNULL(D.Invoice_proof,0) as Invoice_proof FROM orderheader O  LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where O.sync_status = 'U'  AND (O.valid < datetime('now') OR O.valid <= datetime('now','-1 day'))  ", null);
//        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, D.delivery_proof, D.signature_proof, D.id_proof, D.Invoice_proof FROM orderheader O  LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number WHERE O.sync_status = 'U' AND (O.valid <= datetime('now','-1 day') OR O.valid > datetime('now')) ", null);
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {
//                String ship_id = deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof"));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("order_number")));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("valid")));
                File fileImgDeliverProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
                Bitmap myBitmap = BitmapFactory.decodeFile(fileImgDeliverProof.getAbsolutePath());
                img_email.setImageBitmap(myBitmap);

                deleteOrder.moveToNext();
            }

        }
    }

    public void showOlderRecords() {
        String file_path = String.valueOf(this.getFilesDir());
        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, IFNULL(D.delivery_proof, 0) as delivery_proof, IFNULL(D.signature_proof,0) as signature_proof, IFNULL(D.id_proof,0) as id_proof, IFNULL(D.Invoice_proof,0) as Invoice_proof FROM orderheader O  LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number where O.sync_status = 'U'  AND (O.valid < datetime('now') OR O.valid <= datetime('now','-1 day'))  ", null);
//        Cursor deleteOrder = database.rawQuery("SELECT O.order_number, O.Shipment_Number, O.valid, D.delivery_proof, D.signature_proof, D.id_proof, D.Invoice_proof FROM orderheader O  LEFT JOIN DeliveryConfirmation D on D.shipmentnumber = O.Shipment_Number WHERE O.sync_status = 'U' AND (O.valid <= datetime('now','-1 day') OR O.valid > datetime('now')) ", null);
        deleteOrder.moveToFirst();
        if (deleteOrder.getCount() > 0) {
            while (!deleteOrder.isAfterLast()) {
               String ship_id = deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof"));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("order_number")));
//                Log.v("fileImgDeliverProof", deleteOrder.getString(deleteOrder.getColumnIndex("valid")));
                File fileImgDeliverProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
                Bitmap myBitmap = BitmapFactory.decodeFile(fileImgDeliverProof.getAbsolutePath());
                img_email.setImageBitmap(myBitmap);

                /* Delete from orderHeader which are older than one day */
                String deleteOrderDetails = "DELETE FROM orderheader WHERE sync_status = 'U' AND Shipment_Number = '"+ship_id+"'";
                database.execSQL(deleteOrderDetails);

                /* Delete from DeliveryConfirmation which are older than one day */
                String deleteDeliveryDetails = "DELETE FROM DeliveryConfirmation WHERE '"+ship_id+"'";
                database.execSQL(deleteDeliveryDetails);

                File deleteImgDeliverProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("delivery_proof")));
                if (deleteImgDeliverProof.exists()) {
                    deleteImgDeliverProof.delete();
                }
                File deleteImgSignProof = new File(file_path+"/UserSignature/" + deleteOrder.getString(deleteOrder.getColumnIndex("signature_proof")));
                if (deleteImgSignProof.exists()) {
                    deleteImgSignProof.delete();
                }
                File deleteImgIDProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("id_proof")));
                if (deleteImgIDProof.exists()) {
                    deleteImgIDProof.delete();
                }
                File deleteImgInvoiceProof = new File(file_path+"/DeliveryApp/" + deleteOrder.getString(deleteOrder.getColumnIndex("Invoice_proof")));
                if (deleteImgInvoiceProof.exists()) {
                    deleteImgInvoiceProof.delete();
                }

                deleteOrder.moveToNext();
            }

        }
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

}
