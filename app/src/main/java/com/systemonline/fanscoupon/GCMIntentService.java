package com.systemonline.fanscoupon;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.systemonline.fanscoupon.GCM.ServerUtilities;
import com.systemonline.fanscoupon.Helpers.Utility;

import static com.systemonline.fanscoupon.GCM.CommonUtilities.SENDER_ID;

public class GCMIntentService extends GCMBaseIntentService {


    static NotificationManager notificationManager;

    public GCMIntentService() {
        super(SENDER_ID);
    }

    public static void showNotification(Context c, String message) {
        Log.e("GCMIntentService", "fn - showNotification " + message);

        Utility _utility = new Utility(c);

        MediaPlayer mp = MediaPlayer.create(c, R.raw.sounds);
        mp.start();

        notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        Notification n;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            n = new Notification.Builder(c)
                    .setContentTitle(c.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setSmallIcon(R.drawable.fans_coupons)
                    .setAutoCancel(true)
//                    .setStyle(new Notification.BigTextStyle().bigText("aaa"))
                    .build();

        } else {
            n = new Notification.Builder(c)
                    .setContentTitle(c.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setSmallIcon(R.drawable.fans_coupons)
                    .setAutoCancel(true)
                    .getNotification();
        }
        _utility.showHover("add");

        notificationManager.notify(0, n);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.e("GCMIntentService", "fn - onRegistered");

        Log.e(TAG, "Device registered: regId = " + registrationId);
        if (MainActivity.currentFan != null)
            ServerUtilities.register(context, registrationId, MainActivity.currentFan.getFanID(), MainActivity.IMEI);
    }

    /**
     * Method called on device un registred
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.e("GCMIntentService", "fn - onUnregistered");
        ServerUtilities.unregister(context);
    }

    /**
     * Method called on Receiving a new couponImg
     */
    @Override
    protected void onMessage(Context context, Intent intent) {

        Log.e("GCMIntentService", "0fn - onMessage");
        String message = intent.getExtras().getString("price");
//        Log.e("GCMIntentServ Message", couponImg);
        try {
            Log.e("GCMIntentServ userID", intent.getExtras().getString("user_id") + "");

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            if (sharedPreferences.getBoolean("stop_mobile_notification", false))
                return;
            if (message != null && sharedPreferences.getInt("admin_id", -1) == Integer.parseInt(intent.getExtras().getString("user_id")))
                showNotification(context, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called on Error
     */
    @Override
    public void onError(Context context, String errorId) {
        Log.e("GCMIntentService", "fn - onError");
    }
}