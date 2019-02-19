package com.systemonline.fanscoupon.Model;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.MyApp;


/**
 * Created by SystemOnline1 on 8/2/2017.
 */

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    Activity activity;

    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                MyApp.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //Restart your app after 2 seconds
        AlarmManager mgr = (AlarmManager) MyApp.getInstance().getBaseContext()
                .getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
        //finishing the activity.
        activity.finish();
        //Stopping application
        System.exit(2);
    }
}
