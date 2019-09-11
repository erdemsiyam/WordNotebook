package com.erdemsiyam.memorizeyourwords.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.activity.ExamActivity;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.service.NotificationCategoryService;

public class SendCategoryNotificationReceiver extends BroadcastReceiver {

    /*  When "CategoryNotification" alarm time comes.
        An Intent comes to here to,
        Sending Notification the "CategoryNotification" for starting exam. */

    /* Constant for intent key. */
    public final static String KEY_NOTIFICATION_CATEGORY_ID = "notification_category_id";

    /* Override Method. */
    @Override
    public void onReceive(Context context, Intent intent) {
        /* When alarm comes, create and send Notification. */

        /*  If this "NotificationCategory" deleted at DB. Then return without doing anything. */
        NotificationCategory notificationCategory = NotificationCategoryService.getByCategory(context,intent.getLongExtra(KEY_NOTIFICATION_CATEGORY_ID,-1));
        if(notificationCategory == null)
            return;

        /* An intent created for start Exam with this notified "Category". */
        Intent intentToExam = new Intent(context, ExamActivity.class);
        intentToExam.putExtra(CategoryActivity.INTENT_EXAM_SELECT_INDEX,notificationCategory.getWordType());
        intentToExam.putExtra(CategoryActivity.INTENT_SELECTED_CATEGORY_IDS, new long[]{notificationCategory.getCategoryId()}); // long array

        /* Prepared notification.*/
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // Create channel in new versions of android. It's need or application is crash.
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(notificationCategory.getNotificationId()+"",notificationCategory.getNotificationId()+"" , importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Category category = CategoryService.getCategoryById(context,notificationCategory.getCategoryId());
        if(category == null) return;
        Notification notification = new NotificationCompat.Builder(context, (notificationCategory.getNotificationId())+"")
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(context.getResources().getString(R.string.category_notification_title))
                .setContentText(context.getResources().getString(R.string.category_notification_content)+"\"" + category.getName()+"\"")
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, notificationCategory.getNotificationId(),intentToExam, 0)) // Start "ExamActivity" when click to notification content.
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        /* Notification showing. */
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationCategory.getNotificationId(), notification);

        /* Delete "NotificationCategory" record on DB. We've done with this. */
        NotificationCategoryService.delete(context,notificationCategory);
    }
}
