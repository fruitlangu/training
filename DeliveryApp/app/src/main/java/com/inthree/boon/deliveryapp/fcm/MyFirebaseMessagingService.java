package com.inthree.boon.deliveryapp.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.inthree.boon.deliveryapp.MainActivity;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.Config;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.NotificationUtils;
import com.inthree.boon.deliveryapp.utils.OrderSyncService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    private NotificationUtils notificationUtils;
    String p_title, p_msg;
    String msg_id;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private MainActivity mainActivity;
    String push_scheme_status;
    String push_usertype;
    String push_username;
    private SQLiteDatabase database;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.e(TAG, "From: " + remoteMessage.getFrom());
//        msg_id = remoteMessage.getData();
//        Log.e(TAG, "Message ID: " + remoteMessage.getMessageType());
//        msg_id = remoteMessage.getMessageId();

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
//            Log.e(TAG, "Message ID: " + remoteMessage.getMessageId());
            msg_id = remoteMessage.getMessageId();
//            Log.d(TAG, "FCM Message ID " + remoteMessage.getMessageId());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
//            Log.v(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
//                Log.e(TAG, "Message ID: " + remoteMessage.getMessageId());
                msg_id = remoteMessage.getMessageId();
            } catch (Exception e) {
//                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }


    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            //    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //     notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.v(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

          /*  SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            push_scheme_status= prefs.getString("scheme_status", constants.SCHEME_STATUS);
            push_usertype= prefs.getString("usertype", constants.usertype);
            push_username= prefs.getString("username", "");*/

            String title = data.getString("title");
            String message = data.getString("message");
            String order_id = null;
//            String order_id = data.getString("image");
            if (data.has("image")) {
                order_id = data.getString("image");
            }
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

//            Log.v("order_id","--"+ order_id);
//            Log.v("push_notification1",title);
            /* Notification Unassigned Message */
            if (!order_id.equals("") || order_id != null) {
                if (order_id.contains(",")) {
                    String[] splitOrderId = order_id.split(",");
                    for (int i = 0; i < splitOrderId.length; i++) {
                        unassignOrders(splitOrderId[i]);
                    }
                } else {
                    unassignOrders(order_id);
                }


            }


            /*
             * Order Sync service after push notification
             * */
            if (title.contains("Order(s) Assigned")) {
//Log.v("push_Assigned",title);
                startService(new Intent(getApplicationContext(), OrderSyncService.class));
            }
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
//                Log.v("here_or_there", "foreground");
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                pushNotification.putExtra("title", title);

                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//                Intent resultIntent = new Intent(getApplicationContext(), com.boon.inthree.MainActivity.class);
//                Intent resultIntent = new Intent(getApplicationContext(), com.bba.nat.Login.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("uname", push_username);


//                Log.v("TextUtils1", "null");
                showNotificationMessage(getApplicationContext(), title, message, resultIntent);


                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            } else {

                Intent resultIntent;
//                Intent resultIntent = new Intent(Config.PUSH_NOTIFICATION);


//                if(push_scheme_status.equals("1") && push_usertype.equals("distributor") || push_usertype.equals("retailor")){
                resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//                    resultIntent = new Intent(getApplicationContext(), schemeActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("title", title);
                showNotificationMessage(getApplicationContext(), title, message, resultIntent);
            }
        } catch (JSONException e) {
//            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
        notificationUtils.showNotificationMessage(title, message, "stamp", intent);
    }

    /*  public void unassignOrders(String ship_id){

          String deleteOrderDetails = "DELETE FROM orderheader WHERE sync_status = 'P' AND Shipment_Number = '"+ship_id+"'";
          database.execSQL(deleteOrderDetails);

      }*/
    public void unassignOrders(String ship_id) {
//        String deleteOrderDetails = "DELETE FROM orderheader WHERE Shipment_Number = '" + ship_id + "' AND sync_status='P' ";
//        String deleteOrderDetails = "DELETE FROM orderheader WHERE Shipment_Number = '" + ship_id + "' AND (sync_status='P') OR ((sync_status = 'U' OR sync_status = 'C') AND delivery_status = 'undelivered') ";
        String deleteOrderDetails = "DELETE FROM orderheader WHERE Shipment_Number = '" + ship_id + "' AND ((sync_status='P') OR ((sync_status = 'U' OR sync_status = 'C') AND delivery_status = 'undelivered')) ";
        database.execSQL(deleteOrderDetails);

    }
}