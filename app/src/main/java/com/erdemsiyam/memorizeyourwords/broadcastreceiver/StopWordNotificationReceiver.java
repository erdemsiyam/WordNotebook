package com.erdemsiyam.memorizeyourwords.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.erdemsiyam.memorizeyourwords.androidservice.WordNotificationService;
import com.erdemsiyam.memorizeyourwords.service.NotificationWordService;
import com.erdemsiyam.memorizeyourwords.util.NotificationHelper;

public class StopWordNotificationReceiver extends BroadcastReceiver {

    /*  When click "Stop" button on the "WordNotification",
        Redirects here to stop "WordNotificationService". */

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat.from(context).cancel(NotificationHelper.WORD_NOTIFICATION_ID);
        NotificationWordService.deleteAll(context); // All notification records deleted at DB.
        Intent service = new Intent(context, WordNotificationService.class);
        context.stopService(service); // Stopped service.
    }
}
