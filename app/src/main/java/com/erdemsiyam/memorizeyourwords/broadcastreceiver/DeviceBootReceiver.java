package com.erdemsiyam.memorizeyourwords.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.erdemsiyam.memorizeyourwords.androidservice.WordNotificationService;

public class DeviceBootReceiver extends BroadcastReceiver {

    /*  This is help us about start the "Service" to start "Word Notification"
        When the phone startup. */

    /* Override method of "BroadcastReceiver". */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) { // Catch the moment of phone startup.
            context.startService(new Intent(context, WordNotificationService.class)); // Start the our "WordNotification" Service.
        }
    }
}
