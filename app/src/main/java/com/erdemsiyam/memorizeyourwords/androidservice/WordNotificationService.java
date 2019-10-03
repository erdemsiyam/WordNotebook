package com.erdemsiyam.memorizeyourwords.androidservice;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationManagerCompat;
import com.erdemsiyam.memorizeyourwords.activity.SettingActivity;
import com.erdemsiyam.memorizeyourwords.entity.NotificationWord;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.NotificationWordService;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.NotificationHelper;
import com.erdemsiyam.memorizeyourwords.util.WakeLockerHelper;
import com.erdemsiyam.memorizeyourwords.util.WordGroupType;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WordNotificationService extends IntentService {

    /*  This Service works, throwing "WordNotification" whether or not the app closed.
        Random words are selected to notify from selected words(WordGroupType) of all categories in table "NotificationWord".
        Throw word notify at specified period time intervals from settings.
        Within the time interval specified in the settings.
        Person determines the time of sleep and then does not throws word notify. */

    /* Constant. */
    private static final int ALARM_REQUEST_CODE = 0;
    public static final String KEY_SET_LEARNED_WORD_ID = "SET_LEARNED_WORD_ID"; // A intent key for set word learned from notification "Learned" button.

    /* Override method of "IntentService". */
    @Override
    protected void onHandleIntent(Intent intent) {

        /* Get current time. */
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int now = calendar.get(Calendar.MINUTE) + (60 * calendar.get(Calendar.HOUR_OF_DAY));

        /* Get "WordNotification" works time interval. As specified at setting (StartTime / EndTime). */
        int startMinute = getStartMinute(this) + (60 * getStartHour(this));
        int endMinute = getEndMinute(this) + (60 * getEndHour(this));

        /* My algorithm : Are we in time interval? Yes : Start showing notify. No : So, wait until when we reach in time interval. */
        if( startMinute > endMinute ){
            if(!(now >= startMinute || now <= endMinute)){
                setAlarm(this,(startMinute - now)*60*1000);
                return;
            }
        }
        else if( endMinute > startMinute ) {
            if(!(now <= endMinute && now >= startMinute)){
                if (now > endMinute){
                    setAlarm(this,((24*60-now) + startMinute)*60*1000);
                }
                else {
                    setAlarm(this,(startMinute - now) * 60 * 1000);
                }
                return;
            }
        } else { // If they are equals each other. Then stop notification.
            stop(this);
            return;
        }

        /* Get a random word from "NotificationWord" table. */
        Word randomWord = getRandomWord(this);

        /* If there is no word, that's mean no record for notification. So we will stop the service. */
        if(randomWord == null) {
            stop(this);
            return;
        }

        /* Wake up device to show notification for not to bother the user. */
        WakeLockerHelper.acquire(this);

        /* Send Notification if word has come. */
        new NotificationHelper(WordNotificationService.this,NotificationHelper.Type.Word).showNotification(randomWord.getStrange(),randomWord.getExplain(),randomWord.getId(),randomWord.isLearned());

        /* Repeat Alarm. */
        setAlarm(this,getDelayMS(this));
    }

    /* Needs Methods. */
    private static void setAlarm(Context context, long millisAfter){
        /* Creating Alarm to call this class "WordNotificationService". */
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        /* Creating "PendingIntent" for what to do when the alarm time comes. */
        Intent i = new Intent(context, WordNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, ALARM_REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Specified the time to alarm. */
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.add(Calendar.MILLISECOND, (int)millisAfter);

        /* Set alarm by API Versions.*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);
        }else{
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
    public  static void start(Context context){
        /* Creating "WordNotificationService" alarm. */
        setAlarm(context,getDelayMS(context));
    }
    public  static void stop(Context context){
        /* Removing all "WordNotificationService" alarms. */
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WordNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, ALARM_REQUEST_CODE, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    /* Util Methods. */
    public  WordNotificationService() {
        super("WordNotificationService");
    }
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
    private static int getDelayMS(Context context){
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

    /* Inner IntentServices for Notification Buttons. */
    public static class StopWordNotificationService extends IntentService {
        /* Button : Stop all word notifications. */
        /*  This Service for removing all WordNotifications from DB.
            It's calls from "Notification"' content's "STOP" button. */
        @Override
        protected void onHandleIntent(Intent intent) {
            NotificationManagerCompat.from(this).cancel(NotificationHelper.WORD_NOTIFICATION_ID);
            NotificationWordService.deleteAll(this); // All notification records deleted at DB.

            /* Closing "WordNotificationService". */
            WordNotificationService.stop(this);
        }
        public StopWordNotificationService() {
            super("StopWordNotificationService");
        }
    }
    public static class SetWordAsLearnedService extends IntentService {
        /* Button : Set as learned the word, which are notified. */

        @Override
        protected void onHandleIntent(Intent intent) {
            NotificationManagerCompat.from(this).cancel(NotificationHelper.WORD_NOTIFICATION_ID);

            /* Get word id which are clicked as learned. */
            long wordId = intent.getLongExtra(KEY_SET_LEARNED_WORD_ID,0L);

            /* Set learned if exists in db.*/
            if(wordId > 0){
                WordService.changeLearned(this,wordId,true);
            }
        }
        public SetWordAsLearnedService() {
            super("SetWordAsLearnedService");
        }
    }
}
