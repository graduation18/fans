package com.systemonline.fanscoupon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.systemonline.fanscoupon.Interfaces.IServiceCallBack;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SystemOnline1 on 10/5/2015.
 */
public class TrackingReceiver extends BroadcastReceiver implements IServiceCallBack {
    private static final String PLAY_STORE_REFERRER_KEY = "referrer";
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("0Receiver-TrackingRec", "0fn - onReceive");

        try {
            this.context = context;
            sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            String referrer = intent.getStringExtra(PLAY_STORE_REFERRER_KEY);


            String[] splitter = referrer.split("%26");
            Log.e("referrer>>>>>", referrer);
            referrer = splitter[0].split("=")[1];
            editor.putString("referrer", referrer);
            editor.putInt("referrer_status", 0);
            editor.commit();
//            Toast.makeText(context, referrer, Toast.LENGTH_SHORT).show();
            try {
                TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = mngr.getDeviceId();
//                Toast.makeText(context, IMEI, Toast.LENGTH_SHORT).show();
                sendReferrerID(referrer, IMEI);
            } catch (Exception e) {
//                Log.e("tracking", "Error getting IMEI");
                Toast.makeText(context, "cannot get IMEI", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendReferrerID(String referrer, String IMEI) {

        JSONWebServices service = new JSONWebServices(TrackingReceiver.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("refid", referrer));
        nameValuePairs.add(new BasicNameValuePair("imei", IMEI));
        service.sendReferrerReceiver(nameValuePairs);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
//            Toast.makeText(context, Result.toString(), Toast.LENGTH_SHORT).show();
            if (ParseData.parseActionsResult(Result)) {
                editor.putInt("referrer_status", 1);
                Toast.makeText(context, "referrer is sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                editor.putInt("referrer_status", 0);
                Toast.makeText(context, "failed to send referrer", Toast.LENGTH_SHORT).show();
            }
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * referrer=utm_source%3Dfans_coupon%26utm_medium%3Dmarketing_medium%26utm_term%3Dcamp_term%26utm_content%3Dcamp_content%26utm_campaign%3Dcamp_name%26anid%3Dadmob
     */


}