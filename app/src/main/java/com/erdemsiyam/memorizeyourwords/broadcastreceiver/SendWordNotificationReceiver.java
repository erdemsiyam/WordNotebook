package com.erdemsiyam.memorizeyourwords.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import com.erdemsiyam.memorizeyourwords.activity.SettingActivity;
import com.erdemsiyam.memorizeyourwords.entity.NotificationWord;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.NotificationWordService;
import com.erdemsiyam.memorizeyourwords.util.NotificationHelper;
import com.erdemsiyam.memorizeyourwords.util.WordGroupType;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SendWordNotificationReceiver extends BroadcastReceiver {

    /*  This BroadcastReceiver works, throwing "WordNotification" whether or not the app closed.
        Random words are selected to notify from selected words(WordGroupType) of all categories in table "NotificationWord".
        Throw word notify at specified period time intervals from settings.
        Within the time interval specified in the settings.
        Person determines the time of sleep and then does not throws word notify. */

    /* Properties. */
    private static Handler handler = new Handler(); // The handler for processing what we will do again and again.

    /* Override method of "BroadcastReceiver". */
    @Override
    public void onReceive(Context context, Intent intent) {
        /* Creating a process to "NotifyWord" for again and again. */
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                /* Get current time. */
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                int now = calendar.get(Calendar.MINUTE) + (60 * calendar.get(Calendar.HOUR_OF_DAY));

                /* Get "WordNotification" works time interval. As specified at setting (StartTime / EndTime). */
                int startMinute = getStartMinute(context) + (60 * getStartHour(context));
                int endMinute = getEndMinute(context) + (60 * getEndHour(context));

                /* My algorithm : Are we in time interval? Yes : Start showing notify. No : So, wait until when we reach in time interval. */

                if( startMinute > endMinute ){
                    if(!(now >= startMinute || now <= endMinute)){
                        handler.postDelayed(this,(startMinute - now)*60*1000);
                        return;
                    }
                }
                else if( endMinute > startMinute ) {
                    if(!(now <= endMinute && now >= startMinute)){
                        if (now > endMinute){
                            handler.postDelayed(this,((24*60-now) + startMinute)*60*1000);
                        }
                        else {
                            handler.postDelayed(this,(startMinute - now) * 60 * 1000);
                        }
                        return;
                    }
                } else { // If they are equals each other. Then stop notification.
                    handler.removeCallbacksAndMessages(null); // If services are closing then remove all process.
                    return;
                }

                /* Get a random word from "NotificationWord" table. */
                Word randomWord = getRandomWord(context);

                /* If there is no word, that's mean no record for notification. So we will stop the service. */
                if(randomWord == null) {
                    handler.removeCallbacksAndMessages(null); // If services are closing then remove all process.
                    return;
                }

                /* Send Notification if word has come. */
                new NotificationHelper(context,NotificationHelper.Type.Word).showNotification(randomWord.getStrange(),randomWord.getExplain());

                /* Same process calling with specified delay. */
                handler.postDelayed(this, getDelayMS(context));
            }
        };

        /* Starting process for first time. But delaying as specified at settings (Notification Word Period). */
        handler.postDelayed(runnable, getDelayMS(context));
    }

    /* The method : Stop BroadcastReceiver. */
    public static void stop(Context context){
        /* Delete all posted process if BroadcastReceiver is stopped. */
        handler.removeCallbacksAndMessages(null);

        /* Closing stuffs. */
        ComponentName receiver = new ComponentName(context, SendWordNotificationReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /* Util Methods. */
    public  Word getRandomWord(Context context){

        /* Get all "NotificationWord" records from DB. */
        List<NotificationWord> notificationWordList = NotificationWordService.getAll(context);

        /* If there is no record then return null. */
        if(notificationWordList.size()==0) return null;

        /* Collect count of categories words at "NotificationWord" table. */
        int totalWordsCount=0;
        int[] categoriesWordsCount = new int[notificationWordList.size()];
        for(int i=0;i<notificationWordList.size();i++){
            NotificationWord nw = notificationWordList.get(i);
            int count = NotificationWordService.getWordsCountByCategoryAndWordType(context,nw.getCategoryId(), WordGroupType.getTypeByKey(nw.getWordType()));
            totalWordsCount += count;
            categoriesWordsCount[i] = count;
        }

        /* If total word count is zero then return null. */
        if(totalWordsCount==0) return null;

        /* Get random word from this collected words. */
        Random r = new Random();
        long lastNotifyWordId = context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getLong(SettingActivity.NOTIFICATION_LAST_WORD_ID,0L);
        Word randomWord;
        if(totalWordsCount > 1) { //  Control, is it same word last notify? if there is more than 1 words. We don't want repeat same words.
            do {
                randomWord = getWordFromDB(context,r,totalWordsCount,categoriesWordsCount,notificationWordList);
                /* Get random word again if this is same word last notify. */
            } while (lastNotifyWordId == randomWord.getId() );
        } else {
            randomWord = getWordFromDB(context,r,totalWordsCount,categoriesWordsCount,notificationWordList);
        }

        /* Save this word's ID as last notify word ID. */
        context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).edit().putLong(SettingActivity.NOTIFICATION_LAST_WORD_ID,randomWord.getId()).apply();
        return randomWord;
    }
    private Word getWordFromDB(Context context,Random random,int totalWordsCount, int[] categoriesWordsCount,List<NotificationWord> notificationWordList){
        int randomCount = random.nextInt(totalWordsCount)+1;
        for(int i=0;i<categoriesWordsCount.length;i++){
            if(categoriesWordsCount[i]-randomCount >=0){
                NotificationWord nw = notificationWordList.get(i);
                return NotificationWordService.getWordAtIndexFromCategoryByWordType(context,nw.getCategoryId(), WordGroupType.getTypeByKey(nw.getWordType()),randomCount);
            }
            randomCount -= categoriesWordsCount[i];
        }
        return null;
    }
    public  int getDelayMS(Context context){
        /* Get "WordNotification" loop time at setting. */
        return 60*1000*(context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_PERIOD,30));
    }
    public  int getStartHour(Context context){
        return context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_START_TIME_HOUR,9);
    }
    public  int getStartMinute(Context context){
        return context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_START_TIME_MINUTE,0);
    }
    public  int getEndHour(Context context){
        return context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_END_TIME_HOUR,23);
    }
    public  int getEndMinute(Context context){
        return context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_END_TIME_MINUTE,59);
    }
}
