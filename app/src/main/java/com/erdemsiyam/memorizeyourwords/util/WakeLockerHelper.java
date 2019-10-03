package com.erdemsiyam.memorizeyourwords.util;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

public class WakeLockerHelper {
    /*  This helper class works open wake the screen on notifications.
        "WordNotification", "CategoryNotification" uses this.
        Needs "<uses-permission android:name="android.permission.WAKE_LOCK" />" at Manifest.xml. */

    /* Constant. */
    private static final String TAG = "wordnotification:wakelock";

    /* Static Method. */
    public static void acquire(Context ctx){
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn(); // check if screen is on
        if (!isScreenOn) {
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
            wakeLock.acquire(1000); //set your time in milliseconds
        }
    }

}
