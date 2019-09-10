package com.erdemsiyam.memorizeyourwords.androidservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.SettingActivity;
import com.erdemsiyam.memorizeyourwords.broadcastreceiver.StopWordNotificationReceiver;
import com.erdemsiyam.memorizeyourwords.entity.NotificationWord;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.NotificationWordService;
import com.erdemsiyam.memorizeyourwords.util.WordGroupType;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WordNotificationService extends Service {

    /*  This service works, throwing "WordNotification" whether or not the app closed.
        Random words are selected to notify from selected words(WordGroupType) of all categories in table "NotificationWord".
        Throw word notify at specified period time intervals from settings.
        Within the time interval specified in the settings.
        Person determines the time of sleep and then does not throws word notify. */

    /* Constants. */
    public static final int WORD_NOTIFICATION_CHANNEL_ID = 1;

    /* Variables. */
    private Handler handler = new Handler(); // The handler for processing what we will do again and again.

    /* Override methods of "Service". */
    @Override
    public IBinder onBind(Intent intent) { return null; }
    @Override
    public void onCreate() {
        super.onCreate();

        /* Creating a process to "NotifyWord" for again and again. */
        Runnable runnable = new Runnable(){
            @Override
            public void run() {

                /* Get current time. */
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                int now = calendar.get(Calendar.MINUTE) + (60 * calendar.get(Calendar.HOUR_OF_DAY));

                /* Get "WordNotification" works time interval. As specified at setting (StartTime / EndTime). */
                int startMinute = getStartMinute() + (60 * getStartHour());
                int endMinute = getEndMinute() + (60 * getEndHour());

                /* My algorithm : Are we in time interval? Yes : Start showing notify. No : So, wait until when we reach in time interval. */
                try{
                    if( startMinute > endMinute ){
                        if(!(now >= startMinute || now <= endMinute))
                            Thread.sleep((startMinute - now)*60*1000);
                    }
                    else if( endMinute > startMinute ) {
                        if(!(now <= endMinute && now >= startMinute)){
                            if (now > endMinute)
                                Thread.sleep(((24*60-now) + startMinute)*60*1000);
                            else
                                Thread.sleep((startMinute - now)*60*1000);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /* Get a random word from "NotificationWord" table. */
                Word randomWord = getRandomWord();

                /* If there is no word, thats mean no record for notification. So we will stop the service. */
                if(randomWord == null) {
                    handler.postDelayed(this,getDelayMSfromSharedPreferences());
                    stopSelf();
                    return;
                }

                /* If word are come, the push to notification. */
                sendNotification(randomWord.getStrange(),randomWord.getExplain());

                /* Same process calling with specified delay. */
                handler.postDelayed(this,getDelayMSfromSharedPreferences());
            }
        };

        /* Starting process for first time. But delaying as specified at settings (Notification Word Period). */
        handler.postDelayed(runnable,getDelayMSfromSharedPreferences());
    }
    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null); // If services are closing then remove all process.
        super.onDestroy();
    }

    /* Util Methods. */
    public void sendNotification(String title,String message){
        /* An intent created for WordNotification's "Stop" Button. To stop Notification. */
        Intent intent = new Intent(this, StopWordNotificationReceiver.class); // This "BroadcastReceiver" works to stop this "WordkNotificationService".
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, WORD_NOTIFICATION_CHANNEL_ID , intent, PendingIntent.FLAG_CANCEL_CURRENT);

        /* Prepared notification.*/
        Notification notification = new NotificationCompat.Builder(this, WORD_NOTIFICATION_CHANNEL_ID+"")
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0)) // It's works to hide notification.
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .addAction(R.drawable.ic_notification_close,getResources().getString(R.string.words_notification_stop_button),pendingIntent)
                .build();

        /* Notification showing. */
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(WORD_NOTIFICATION_CHANNEL_ID, notification);
    }
    public Word getRandomWord(){

        /* Get all "NotificationWord" records from DB. */
        List<NotificationWord> notificationWordList = NotificationWordService.getAll(this);

        /* If there is no record then return null. */
        if(notificationWordList.size()==0) return null;

        /* Collect count of categories words at "NotificationWord" table. */
        int totalWordsCount=0;
        int[] categoriesWordsCount = new int[notificationWordList.size()];
        for(int i=0;i<notificationWordList.size();i++){
            NotificationWord nw = notificationWordList.get(i);
            int count = NotificationWordService.getWordsCountByCategoryAndWordType(this,nw.getCategoryId(), WordGroupType.getTypeByKey(nw.getWordType()));
            totalWordsCount += count;
            categoriesWordsCount[i] = count;
        }

        /* If total word count is zero then return null. */
        if(totalWordsCount==0) return null;

        /* Get random word from this collected words. */
        Random random = new Random();
        int randomCount = random.nextInt(totalWordsCount)+1;
        for(int i=0;i<categoriesWordsCount.length;i++){
            if(categoriesWordsCount[i]-randomCount >=0){
                NotificationWord nw = notificationWordList.get(i);
                return NotificationWordService.getWordAtIndexFromCategoryByWordType(this,nw.getCategoryId(), WordGroupType.getTypeByKey(nw.getWordType()),randomCount);
            }
            randomCount -= categoriesWordsCount[i];
        }
        return null;
    }
    public int getDelayMSfromSharedPreferences(){
        /* Get setted "WordNotification" Period at setting. */
        return 1000*(getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_PERIOD,30));
    }
    public int getStartHour(){
        return getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_START_TIME_HOUR,9);
    }
    public int getStartMinute(){
        return getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_START_TIME_MINUTE,0);
    }
    public int getEndHour(){
        return getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_END_TIME_HOUR,23);
    }
    public int getEndMinute(){
        return getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_END_TIME_MINUTE,59);
    }
}
