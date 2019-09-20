package com.erdemsiyam.memorizeyourwords.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.erdemsiyam.memorizeyourwords.androidservice.CategoryNotificationService;
import com.erdemsiyam.memorizeyourwords.androidservice.WordNotificationService;
import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;
import com.erdemsiyam.memorizeyourwords.service.NotificationCategoryService;
import java.util.List;

public class DeviceBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /* If device boot up. */
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            /* Start WordNotificationService. */
            WordNotificationService.start(context);

            /* Start CategoryNotification. */
            List<NotificationCategory> notificationCategoryList = NotificationCategoryService.getAll(context);
            for(NotificationCategory nc : notificationCategoryList){
                CategoryNotificationService.setAlarm(context,nc);
            }
        }
    }
}
