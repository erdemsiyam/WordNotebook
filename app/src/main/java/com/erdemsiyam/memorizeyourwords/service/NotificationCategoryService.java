package com.erdemsiyam.memorizeyourwords.service;

import android.content.Context;

import com.erdemsiyam.memorizeyourwords.database.MyDatabase;
import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;

import java.util.List;
import java.util.Random;

public final class NotificationCategoryService {
    public static NotificationCategory addNotificationCategory(Context context,Long categoryId, int wordType, int hour, int minute){
        boolean isAlreadyHave = MyDatabase.getMyDatabase(context).getNotificationCategoryDAO().getByCategory(categoryId) != null;
        if(!isAlreadyHave){
            NotificationCategory notificationCategory = new NotificationCategory();
            notificationCategory.setCategoryId(categoryId);
            notificationCategory.setWordType(wordType);
            notificationCategory.setNotificationId((new Random().nextInt(999999))); // We want, All "CategoryNotification"'s come in different notification BOXES, then we make this id's random each "CategoryNotification".
            notificationCategory.setHour(hour);
            notificationCategory.setMinute(minute);
            notificationCategory.setId(MyDatabase.getMyDatabase(context).getNotificationCategoryDAO().insert(notificationCategory));
            return notificationCategory;
        }
        return null;
    }
    public static NotificationCategory getByCategory(Context context, Long categoryId){
        return MyDatabase.getMyDatabase(context).getNotificationCategoryDAO().getByCategory(categoryId);
    }
    public static void delete(Context context, NotificationCategory notificationCategory){
        MyDatabase.getMyDatabase(context).getNotificationCategoryDAO().delete(notificationCategory);
    }
    public static void deleteAll(Context context){
        MyDatabase.getMyDatabase(context).getNotificationCategoryDAO().deleteAll();
    }
    public static List<NotificationCategory> getAll(Context context){
        return MyDatabase.getMyDatabase(context).getNotificationCategoryDAO().getAll();
    }
}
