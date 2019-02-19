package com.systemonline.fanscoupon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by SystemOnline1 on 7/6/2017.
 */


public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, CampaignLocationsService.class);
            context.startService(pushIntent);
        }
    }
}