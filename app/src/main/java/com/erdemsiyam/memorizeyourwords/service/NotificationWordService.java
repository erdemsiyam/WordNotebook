package com.erdemsiyam.memorizeyourwords.service;

import android.content.Context;

import com.erdemsiyam.memorizeyourwords.database.MyDatabase;
import com.erdemsiyam.memorizeyourwords.entity.NotificationWord;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.util.WordGroupType;

import java.util.List;

public final class NotificationWordService {
    public static List<NotificationWord> getAll(Context context){
        return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getAll();
    }
    public static void addNotificationWord(Context context, Long categoryId, int wordType){
        boolean isAlreadyHave = MyDatabase.getMyDatabase(context).getNotificationWordDAO().getByCategory(categoryId) != null;
        if(!isAlreadyHave){
            NotificationWord notificationWord = new NotificationWord();
            notificationWord.setCategoryId(categoryId);
            notificationWord.setWordType(wordType);
            MyDatabase.getMyDatabase(context).getNotificationWordDAO().insert(notificationWord);
        }
    }
    public static NotificationWord getByCategory(Context context, Long categoryId){
        return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getByCategory(categoryId);
    }
    public static int getHowManyNotificationWord(Context context){
        return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getHowManyNotificationWord();
    }
    public static int getWordsCountByCategoryAndWordType(Context context, Long categoryId, WordGroupType wordType){
        switch (wordType) {
            case All:
                return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getWordsCountFromCategoryByAll(categoryId);
            case Learned:
                return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getWordsCountFromCategoryByLearned(categoryId);
            case Marked:
                return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getWordsCountFromCategoryByMarked(categoryId);
            case NotLearned:
                return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getWordsCountFromCategoryByNotLearned(categoryId);
        }
        return -1;
    }
    public static Word getWordAtIndexFromCategoryByWordType(Context context, Long categoryId, WordGroupType wordType, int index ){
        switch (wordType) {
            case All:
                return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getWordAtIndexFromCategoryByAll(categoryId,index);
            case Learned:
                return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getWordAtIndexFromCategoryByLearned(categoryId,index);
            case Marked:
                return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getWordAtIndexFromCategoryByMarked(categoryId,index);
            case NotLearned:
                return MyDatabase.getMyDatabase(context).getNotificationWordDAO().getWordAtIndexFromCategoryByNotLearned(categoryId,index);
        }
        return null;
    }
    public static void delete(Context context, NotificationWord notificationWord){
        MyDatabase.getMyDatabase(context).getNotificationWordDAO().delete(notificationWord);
    }
}
