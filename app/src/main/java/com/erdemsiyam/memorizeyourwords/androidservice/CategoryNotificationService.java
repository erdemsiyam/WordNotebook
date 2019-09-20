package com.erdemsiyam.memorizeyourwords.androidservice;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.activity.ExamActivity;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.service.NotificationCategoryService;
import com.erdemsiyam.memorizeyourwords.util.NotificationHelper;

import java.util.Calendar;

public class CategoryNotificationService extends IntentService {

    /*  When "CategoryNotification" alarm time comes.
        An Intent comes to here to,
        Sending Notification the "CategoryNotification" for starting exam. */

    /* Constant for intent key. */
    public final static String  KEY_NOTIFICATION_CATEGORY_ID = "notification_category_id";

    /* Override method of "IntentService". */
    @Override
    protected void onHandleIntent(Intent intent) {
        /* When alarm comes, create and send Notification. */

        /*  If this "NotificationCategory" deleted at DB. Then return without doing anything. */
        NotificationCategory notificationCategory = NotificationCategoryService.getByCategory(this,intent.getLongExtra(KEY_NOTIFICATION_CATEGORY_ID,-1));
        if(notificationCategory == null) return;

        /*  If this "NotificationCategory" deleted at DB. Then return without doing anything. */
        Category category = CategoryService.getCategoryById(this,notificationCategory.getCategoryId());
        if(category == null) return;

        /* An intent created for start Exam with this notified "Category". */
        Intent intentToExam = new Intent(this, ExamActivity.class);
        intentToExam.putExtra(CategoryActivity.INTENT_EXAM_SELECT_INDEX,notificationCategory.getWordType());
        intentToExam.putExtra(CategoryActivity.INTENT_SELECTED_CATEGORY_IDS, new long[]{notificationCategory.getCategoryId()}); // long array

        /* Show Notification. */
        String title = this.getResources().getString(R.string.category_notification_title);
        String message = this.getResources().getString(R.string.category_notification_content)+"\"" + category.getName()+"\"";
        int specialNotificationId = notificationCategory.getNotificationId();
        new NotificationHelper(this,NotificationHelper.Type.Category).showNotification(title,message,intentToExam,specialNotificationId);

        /* After Notification : Delete "NotificationCategory" on DB. */
        NotificationCategoryService.delete(this,notificationCategory);
    }
    public CategoryNotificationService() {
        super("CategoryNotificationService");
    }

    /* Util Method for Other classes. */
    public static void setAlarm(Context context, NotificationCategory nc){
        /* Set "Alarm" to specified time. */
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        /* Creating "PendingIntent" for what to do when the alarm time comes. */
        Intent intent = new Intent(context, CategoryNotificationService.class); // When alarm time comes. Go to this "CategoryNotificationService" to send "CategoryNotification".
        intent.putExtra(CategoryNotificationService.KEY_NOTIFICATION_CATEGORY_ID,nc.getCategoryId());
        PendingIntent pendingIntent = PendingIntent.getService(context, nc.getNotificationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Get "NotificationTime" to alarm as "Calendar". Because this is need to Alarm. */
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, nc.getHour());
        c.set(Calendar.MINUTE, nc.getMinute());
        c.set(Calendar.SECOND, 0);

        /*  A setting for 12-Hour-Format users...
            Example Problem : Suppose, now time 05 am, we set alarm 01 am (but for tomorrow) but alarms get calls immediately.
            Because "AlarmManager" thinks time passed. no we set up this is for tomorrow.
            Then the solution : if this is early time then add 1 day to alarm time.*/
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        /* Set alarm by API Versions.*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pendingIntent);
        }else{
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }
}
