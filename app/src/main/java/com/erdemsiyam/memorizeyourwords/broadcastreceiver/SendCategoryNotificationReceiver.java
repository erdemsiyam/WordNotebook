package com.erdemsiyam.memorizeyourwords.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.activity.ExamActivity;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.service.NotificationCategoryService;
import com.erdemsiyam.memorizeyourwords.util.NotificationHelper;

public class SendCategoryNotificationReceiver extends BroadcastReceiver {

    /*  When "CategoryNotification" alarm time comes.
        An Intent comes to here to,
        Sending Notification the "CategoryNotification" for starting exam. */

    /* Constant for intent key. */
    public final static String  KEY_NOTIFICATION_CATEGORY_ID = "notification_category_id";

    /* Override Method. */
    @Override
    public void onReceive(Context context, Intent intent) {
        /* When alarm comes, create and send Notification. */

        /*  If this "NotificationCategory" deleted at DB. Then return without doing anything. */
        NotificationCategory notificationCategory = NotificationCategoryService.getByCategory(context,intent.getLongExtra(KEY_NOTIFICATION_CATEGORY_ID,-1));
        if(notificationCategory == null) return;

        /*  If this "NotificationCategory" deleted at DB. Then return without doing anything. */
        Category category = CategoryService.getCategoryById(context,notificationCategory.getCategoryId());
        if(category == null) return;

        /* An intent created for start Exam with this notified "Category". */
        Intent intentToExam = new Intent(context, ExamActivity.class);
        intentToExam.putExtra(CategoryActivity.INTENT_EXAM_SELECT_INDEX,notificationCategory.getWordType());
        intentToExam.putExtra(CategoryActivity.INTENT_SELECTED_CATEGORY_IDS, new long[]{notificationCategory.getCategoryId()}); // long array

        /* Show Notification. */
        String title = context.getResources().getString(R.string.category_notification_title);
        String message = context.getResources().getString(R.string.category_notification_content)+"\"" + category.getName()+"\"";
        int specialNotificationId = notificationCategory.getNotificationId();
        new NotificationHelper(context,NotificationHelper.Type.Category).showNotification(title,message,intentToExam,specialNotificationId);

        /* After Notification : Delete "NotificationCategory" on DB. */
        NotificationCategoryService.delete(context,notificationCategory);
    }
}
