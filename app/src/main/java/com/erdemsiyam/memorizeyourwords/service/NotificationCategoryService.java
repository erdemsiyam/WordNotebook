package com.erdemsiyam.memorizeyourwords.service;

import android.content.Context;

import com.erdemsiyam.memorizeyourwords.database.MyDatabase;
import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;

public final class NotificationCategoryService {
    public static NotificationCategory addNotificationCategory(Context context,Long categoryId, int wordType){
        boolean isAlreadyHave = MyDatabase.getMyDatabase(context).getNotificationCategoryDAO().getByCategory(categoryId) != null;
        if(!isAlreadyHave){
            NotificationCategory notificationCategory = new NotificationCategory();
            notificationCategory.setCategoryId(categoryId);
            notificationCategory.setWordType(wordType);
            notificationCategory.setNotificationId((int)(System.currentTimeMillis()/1000));
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
}
