package com.erdemsiyam.memorizeyourwords.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Vibrator;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.SettingActivity;
import com.erdemsiyam.memorizeyourwords.androidservice.WordNotificationService;
import java.nio.charset.Charset;
import java.util.Random;

public class NotificationHelper {

    /* This class help us preparing "Notification".
       Using like :
            new NotificationHelper(context,type).showNotification(title,message); */

    /* Constants. */
    public  static final int    WORD_NOTIFICATION_ID = 1; // This is constant because, We don't want clutter of "WordNottification". Every newcomer will crush the old.
    private static final String WORD_CHANNEL_NAME = "Word Notification";
    private static final String CATEGORY_CHANNEL_NAME = "List Exam Notification";

    /* Variable. */
    private Context context;
    private String  channelId;
    private Type    type;

    /* Constructor. */
    public NotificationHelper (Context context, Type type) { this.context = context; this.type = type; }

    /* Enum class for define notification type. */
    public enum Type { Word, Category }

    /* Main Methods. */
    public void showNotification(String title, String message, long specialWordId, boolean isWordLearned) {
        showNotification(title,message,null,0,specialWordId,isWordLearned);
    }
    public void showNotification(String title, String message, Intent intent, int specialNotificationId){
        showNotification(title,message,intent,specialNotificationId,0L,false);
    }
    public void showNotification(String title, String message, Intent intent, int specialNotificationId,long specialWordId,boolean isWordLearned){
        /* "Intent Param" : may you need this to using in notification. For example : "CategoryNotification" need to this for start exam as category. */
        channelId = getChannelId();

        /*  We want, All "CategoryNotification"'s come in different notification BOXES.
            Then we make this id's random each "CategoryNotification" at EntityService.
            After took to here to notification. */
        int CATEGORY_NOTIFICATION_ID = specialNotificationId;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* NotificationChannel Part. For >= API 26. */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);

            /* If this is first notification?
               YES : then create NotificationChannel. No : Go with old one. */
            if(notificationChannel == null) {
                notificationChannel = prepareNotificationChannel(); // Created new channel at the "Function" at blow.
                notificationManager.createNotificationChannel(notificationChannel); // Created done.
            } else {
                /* Control : Is setting flag up? : Mean, is there any changing at setting about "Notification".*/
                if(isNotificationSettingChange()){
                    /* If yes then delete old, we can not change at API 26 and above, we must create new with new id.*/
                    notificationManager.deleteNotificationChannel(channelId); // Delete old one
                    channelId = getNewChannelId(); // Channel ID replace with new value.
                    notificationManager.createNotificationChannel(prepareNotificationChannel()); // Created New as User's preferences.
                    setNotificationChangeHandled(); // Setting's changes flag down.
                }
            }
        }

        /* NotificationBuilder Part. For Every API. */
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        if(type == Type.Word){
            /* If this is "WordNotification", add content click ; disappear when click the content. */
            notificationBuilder.setContentIntent(PendingIntent.getActivity(context, WORD_NOTIFICATION_ID, new Intent(), 0));

            /* Add Button to stop "WordNotification" with intent. When click "Stop" in Notification. */
            Intent stopWordNotifyIntent = new Intent(context, WordNotificationService.StopWordNotificationService.class); // This inner class "Service" works to stop this "WordNotificationService".
            PendingIntent pendingIntent = PendingIntent.getService(context, WORD_NOTIFICATION_ID, stopWordNotifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.ic_notification_close,context.getResources().getString(R.string.words_notification_stop_button),pendingIntent);

            /* Add Button to set learned "The Word", with intent. When click "Learned" in Notification. If word is not learned */
            if(!isWordLearned) {
                Intent setWordAsLearnedService = new Intent(context, WordNotificationService.SetWordAsLearnedService.class); // This inner class "Service" works to stop this "WordNotificationService".
                setWordAsLearnedService.putExtra(WordNotificationService.KEY_SET_LEARNED_WORD_ID, specialWordId); // Word's id sending to process.
                PendingIntent pendingIntent2 = PendingIntent.getService(context, WORD_NOTIFICATION_ID, setWordAsLearnedService, PendingIntent.FLAG_CANCEL_CURRENT);
                notificationBuilder.addAction(R.drawable.ic_word_add, context.getResources().getString(R.string.words_notification_learned_button), pendingIntent2);
            }
        }
        else if (type == Type.Category){
            /* If this is "WordNotification", add content click ; Start "ExamActivity" to Exam. */
            notificationBuilder.setContentIntent(PendingIntent.getActivity(context, CATEGORY_NOTIFICATION_ID,intent, 0));
        }

        /* NotificationBuilder Customize Part. For <= API 25. */
        Notification notification ;
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {

            /* Set "Heads-Up" setting preference. */
            int priority = (getHeadsUpEnable())?NotificationCompat.PRIORITY_MAX:NotificationCompat.PRIORITY_DEFAULT;
            notificationBuilder.setPriority(priority);

            /* Set "Sound" setting preference. */
            if(getSoundEnable())
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            /* Set "Vibrate" setting preference. */
            if(getVibrateEnable()) {
               ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
                notificationBuilder.setVibrate(new long[]{200});
            }
        }

        notification = notificationBuilder.build(); // Building here.

        /* Wake up device before notification call to show notification for not to bother the user. */
        WakeLockerHelper.acquire(context);

        /* Notification showing. */
        notificationManager.notify((type==Type.Word)?WORD_NOTIFICATION_ID:CATEGORY_NOTIFICATION_ID, notification);

    }

    /* Prepare Method. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel prepareNotificationChannel(){
        /* Preparing "NotificationChannel" for API 26 and above. */

        /* Set "Heads-Up" setting preference. */
        int importance = (getHeadsUpEnable())?NotificationManager.IMPORTANCE_HIGH:NotificationManager.IMPORTANCE_DEFAULT;

        /* Select which Notification. */
        String channelName = (type == Type.Word)? WORD_CHANNEL_NAME : CATEGORY_CHANNEL_NAME;

        NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName , importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);

        /* Set "Vibrate" setting preference. */
        if(getVibrateEnable()) {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[] {200});
        }

        /* Set "Sound" setting preference. */
        if(getSoundEnable()) // Looking settings. Is this enabled?.
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_ALARM).build());

        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        return notificationChannel;
    }

    /* Get Settings Value Methods.(from "SharedPreferences"). */
    private boolean getSoundEnable(){
        return context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getBoolean(SettingActivity.NOTIFICATION_SOUND,true);
    }
    private boolean getVibrateEnable(){
        return context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getBoolean(SettingActivity.NOTIFICATION_VIBRATE,true);
    }
    private boolean getHeadsUpEnable(){
        return context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getBoolean(SettingActivity.NOTIFICATION_HEADS_UP,true);
    }

    /* Util Methods. */
    private String  getRandomString(){
        /*  When user change notification settings, then needs to create new channel_id than needs to a unique string.
            That's because we created this method. */
        byte[] array = new byte[8];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
    private String  getChannelId(){
        /* Gets last Channel_Id from SettingActivity's SharedPreferences. */

        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE);

        /* Check is it first time? If it is first time, we must put manually, if we not then its makes every time random channel id. */
        if(!sharedPreferences.contains(SettingActivity.NOTIFICATION_GENERAL_CHANNEL_ID)){
            String newValue = getRandomString();
            sharedPreferences.edit().putString(SettingActivity.NOTIFICATION_GENERAL_CHANNEL_ID,newValue).apply();
            return newValue;
        }
        /* Return value if already exists. */
        return sharedPreferences.getString(SettingActivity.NOTIFICATION_GENERAL_CHANNEL_ID,"");
    }
    private String  getNewChannelId(){
        String newValue = getRandomString();
        context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).edit().putString(SettingActivity.NOTIFICATION_GENERAL_CHANNEL_ID,newValue).apply();
        return newValue;
    }
    private boolean isNotificationSettingChange(){
        /* If there is some changes at "SettingActivity" then we will flags up. This is the flag. */
        return context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getBoolean(SettingActivity.NOTIFICATION_IS_CHANGE,false);
    }
    private void    setNotificationChangeHandled(){
        /* After flag up we did do everything about it, then flag down. */
        context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).edit().putBoolean(SettingActivity.NOTIFICATION_IS_CHANGE,false).apply();
    }
}
