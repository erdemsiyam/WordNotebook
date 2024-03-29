package com.erdemsiyam.memorizeyourwords.activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.androidservice.WordNotificationService;
import com.erdemsiyam.memorizeyourwords.util.TimePrintHelper;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    /*  Setting Page.
        Using SharedPreferences. */

    /* Constants : for "SharedPreferences". */
    public static final String  PREFERENCE_NAME = "setting_preference";
    public static final int     PREFERENCE_MODE = MODE_PRIVATE;
    public static final String  WORD_NOTIFICATION_PERIOD = "word_notification_period";
    public static final String  WORD_NOTIFICATION_START_TIME_HOUR = "word_notification_start_time_hour";
    public static final String  WORD_NOTIFICATION_START_TIME_MINUTE = "word_notification_start_time_minute";
    public static final String  WORD_NOTIFICATION_END_TIME_HOUR = "word_notification_end_time_hour";
    public static final String  WORD_NOTIFICATION_END_TIME_MINUTE = "word_notification_end_time_minute";
    public static final String  NOTIFICATION_SOUND = "notification_sound";
    public static final String  NOTIFICATION_VIBRATE = "notification_vibrate";
    public static final String  NOTIFICATION_HEADS_UP = "notification_heads_up";
    public static final String  NOTIFICATION_IS_CHANGE = "notification_is_change"; // Background value holder.
    public static final String  NOTIFICATION_GENERAL_CHANNEL_ID = "notification_general_channel_id"; // Background value holder.
    public static final String  NOTIFICATION_LAST_WORD_ID = "notification_last_word_id"; // Background value holder.
    public static final String  FONT = "font";

    /* UI Components. */
    private LinearLayout         lytWordNotificationPeriod;
    private LinearLayout         lytWordNotificationStartTime;
    private LinearLayout         lytWordNotificationEndTime;
    private LinearLayout         lytNotificationSound;
    private LinearLayout         lytNotificationVibrate;
    private LinearLayout         lytNotificationHeadsUp;
    private LinearLayout         lytFont;
    private AppCompatTextView    txtWordNotificationPeriodValue;
    private AppCompatTextView    txtWordNotificationStartTimeValue;
    private AppCompatTextView    txtWordNotificationEndTimeValue;
    private Switch               swNotificationSoundValue;
    private Switch               swNotificationVibrateValue;
    private Switch               swNotificationHeadsUpValue;
    private AppCompatTextView    txtFontValue;
    private AppCompatImageButton btnBackToCategoryFromSetting; // Back button to "CategoryActivity".

    /* Variable. */
    private SharedPreferences sharedPreferences;

    /* Override Method. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initComponents(); // UI components are installed.
        loadData(); // Data is loaded into UI components.
        loadFontSizes();
    }

    /* Initial Methods. */
    private void initComponents() {
        lytWordNotificationPeriod = findViewById(R.id.lytWordNotificationPeriod);
        lytWordNotificationStartTime = findViewById(R.id.lytWordNotificationStartTime);
        lytWordNotificationEndTime = findViewById(R.id.lytWordNotificationEndTime);
        lytNotificationSound = findViewById(R.id.lytNotificationSound);
        lytNotificationVibrate = findViewById(R.id.lytNotificationVibrate);
        lytNotificationHeadsUp = findViewById(R.id.lytNotificationHeadsUp);
        lytFont = findViewById(R.id.lytFont);
        txtWordNotificationPeriodValue = findViewById(R.id.txtWordNotificationPeriodValue);
        txtWordNotificationStartTimeValue = findViewById(R.id.txtWordNotificationStartTimeValue);
        txtWordNotificationEndTimeValue = findViewById(R.id.txtWordNotificationEndTimeValue);
        swNotificationSoundValue = findViewById(R.id.swNotificationSoundValue);
        swNotificationVibrateValue = findViewById(R.id.swNotificationVibrateValue);
        swNotificationHeadsUpValue = findViewById(R.id.swNotificationHeadsUpValue);
        swNotificationSoundValue.setClickable(false);
        swNotificationVibrateValue.setClickable(false);
        swNotificationHeadsUpValue.setClickable(false);
        txtFontValue = findViewById(R.id.txtFontValue);
        btnBackToCategoryFromSetting = findViewById(R.id.btnBackToCategoryFromSetting);
    }
    private void loadData() {
        /* Obtaining the "SharedPreferences" object. */
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME,PREFERENCE_MODE);

        /* Filling UI values. */
        txtWordNotificationPeriodValue.setText(sharedPreferences.getInt(WORD_NOTIFICATION_PERIOD,30)+" "+getResources().getString(R.string.minute));
        int startHourInt = sharedPreferences.getInt(WORD_NOTIFICATION_START_TIME_HOUR,9);
        int startMinuteInt = sharedPreferences.getInt(WORD_NOTIFICATION_START_TIME_MINUTE,0);
        txtWordNotificationStartTimeValue.setText(TimePrintHelper.getTime(this,startHourInt,startMinuteInt));
        int endHourInt = sharedPreferences.getInt(WORD_NOTIFICATION_END_TIME_HOUR,23);
        int endMinuteInt = sharedPreferences.getInt(WORD_NOTIFICATION_END_TIME_MINUTE,59);
        txtWordNotificationEndTimeValue.setText(TimePrintHelper.getTime(this,endHourInt,endMinuteInt));
        swNotificationSoundValue.setChecked(sharedPreferences.getBoolean(NOTIFICATION_SOUND,true));
        swNotificationVibrateValue.setChecked(sharedPreferences.getBoolean(NOTIFICATION_VIBRATE,true));
        swNotificationHeadsUpValue.setChecked(sharedPreferences.getBoolean(NOTIFICATION_HEADS_UP,true));
        txtFontValue.setText((12 + 2*sharedPreferences.getInt(FONT,2))+" sp");

        /* Listeners for settings. */
        lytWordNotificationPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new Dialog(SettingActivity.this); // Our custom dialog created.
                d.setTitle(R.string.setting_word_notification_alert_title_loop_time);
                d.setContentView(R.layout.dialog_setting_number_pick); // Including custom layout to custom dialog.
                Button btnSettingNumberPickAccept = d.findViewById(R.id.btnSettingNumberPickAccept);
                NumberPicker np = d.findViewById(R.id.npWordNotificationPeriodValue); // A NumberPicker created.
                List<String> values =  Arrays.asList(new String[] {"5","10","15","20","30","45","60","90","120","160","180","300","480","600"});
                np.setMinValue(0);
                np.setMaxValue(values.size()-1);
                np.setDisplayedValues(values.toArray(new String[0]));
                np.setValue(values.indexOf(sharedPreferences.getInt(WORD_NOTIFICATION_PERIOD,30)+""));
                np.setWrapSelectorWheel(false);
                btnSettingNumberPickAccept.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        sharedPreferences.edit().putInt(WORD_NOTIFICATION_PERIOD,Integer.valueOf(values.get(np.getValue()))).apply(); // WordNotification "CycleTime" changed.
                        txtWordNotificationPeriodValue.setText(values.get(np.getValue()) + " " + getResources().getString(R.string.minute)); // UI refreshed.
                        restartWordNotificationReceiver(); // Restarting "WordNotification BroadcastReceiver".
                        d.dismiss(); // Dialog closed.
                    }
                });
                d.show();
            }
        });
        lytWordNotificationStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Showing "TimePickerDialog" to taking start time of "WordNotification". */
                class TimeHandler implements TimePickerDialog.OnTimeSetListener {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sharedPreferences.edit()
                                .putInt(WORD_NOTIFICATION_START_TIME_HOUR,hourOfDay)
                                .putInt(WORD_NOTIFICATION_START_TIME_MINUTE,minute)
                                .apply(); // Start time value changed.
                        txtWordNotificationStartTimeValue.setText(TimePrintHelper.getTime(SettingActivity.this,hourOfDay,minute)); // UI refreshed.
                        restartWordNotificationReceiver(); // Restarting "WordNotification BroadcastReceiver".
                    }
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingActivity.this, new TimeHandler(), 9, 0, DateFormat.is24HourFormat(SettingActivity.this));
                timePickerDialog.setTitle(R.string.setting_word_notification_alert_title_set_time);
                timePickerDialog.show();
            }
        });
        lytWordNotificationEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Showing "TimePickerDialog" to taking end time of "WordNotification". */
                class TimeHandler implements TimePickerDialog.OnTimeSetListener {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sharedPreferences.edit()
                                .putInt(WORD_NOTIFICATION_END_TIME_HOUR,hourOfDay)
                                .putInt(WORD_NOTIFICATION_END_TIME_MINUTE,minute)
                                .apply(); // End time value changed.
                        txtWordNotificationEndTimeValue.setText(TimePrintHelper.getTime(SettingActivity.this,hourOfDay,minute)); // UI refreshed.
                        restartWordNotificationReceiver(); // Restarting "WordNotification BroadcastReceiver".
                    }
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingActivity.this, new TimeHandler(), 23, 59, DateFormat.is24HourFormat(SettingActivity.this));
                timePickerDialog.setTitle(R.string.setting_word_notification_alert_title_set_time);
                timePickerDialog.show();
            }
        });
        lytNotificationSound.setOnClickListener(v -> {
            boolean newStatus = !swNotificationSoundValue.isChecked();
            swNotificationSoundValue.setChecked(newStatus);
            sharedPreferences.edit()
                    .putBoolean(NOTIFICATION_SOUND,newStatus)
                    .putBoolean(NOTIFICATION_IS_CHANGE,true)
                    .apply();
        });
        lytNotificationVibrate.setOnClickListener(v -> {
            boolean newStatus = !swNotificationVibrateValue.isChecked();
            swNotificationVibrateValue.setChecked(newStatus);
            sharedPreferences.edit()
                    .putBoolean(NOTIFICATION_VIBRATE,newStatus)
                    .putBoolean(NOTIFICATION_IS_CHANGE,true)
                    .apply();
        });
        lytNotificationHeadsUp.setOnClickListener(v -> {
            /* Version control. */
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                Toast.makeText(SettingActivity.this,R.string.excel_message_version_error,Toast.LENGTH_LONG).show();
            } else {
                boolean newStatus = !swNotificationHeadsUpValue.isChecked();
                swNotificationHeadsUpValue.setChecked(newStatus);
                sharedPreferences.edit()
                        .putBoolean(NOTIFICATION_HEADS_UP, newStatus)
                        .putBoolean(NOTIFICATION_IS_CHANGE, true)
                        .apply();
            }
        });
        lytFont.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Dialog d = new Dialog(SettingActivity.this); // Our custom dialog created.
                d.setTitle(R.string.setting_word_notification_alert_title_font_size);
                d.setContentView(R.layout.dialog_setting_number_pick); // Including custom layout to custom dialog.
                Button btnSettingNumberPickAccept = d.findViewById(R.id.btnSettingNumberPickAccept);
                NumberPicker np = d.findViewById(R.id.npWordNotificationPeriodValue);
                String[] values = new String[]{"12","14","16","18","20"};
                np.setMinValue(0);
                np.setMaxValue(values.length-1);
                np.setDisplayedValues(values);
                np.setValue(sharedPreferences.getInt(FONT,2));
                np.setWrapSelectorWheel(false);
                btnSettingNumberPickAccept.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        sharedPreferences.edit().putInt(FONT,np.getValue()).apply(); // "FontSize" changed.
                        txtFontValue.setText(values[np.getValue()] + " sp"); // UI refreshed.
                        loadFontSizes();// All page refreshed.
                        d.dismiss(); // Dialog closed.
                    }
                });
                d.show();
            }
        });

        /* Listener to BackButton. */
        btnBackToCategoryFromSetting.setOnClickListener(v -> {
            finish();
        });
    }
    private void loadFontSizes(){
        float fontSize = getFont(this);
        /* Section Titles. */
        ((AppCompatTextView)findViewById(R.id.txtSettingsWordNotificationSectionTitle)).setTextSize(fontSize-4);
        ((AppCompatTextView)findViewById(R.id.txtSettingsNotificationSectionTitle)).setTextSize(fontSize-4);
        ((AppCompatTextView)findViewById(R.id.txtSettingsFontSectionTitle)).setTextSize(fontSize-4);

        /* Word Notification Section's. */
        ((AppCompatTextView)findViewById(R.id.txtWordNotificationPeriod)).setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtWordNotificationPeriodSummary)).setTextSize(fontSize-4);
        txtWordNotificationPeriodValue.setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtWordNotificationStartTime)).setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtWordNotificationStartTimeSummary)).setTextSize(fontSize-4);
        txtWordNotificationStartTimeValue.setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtWordNotificationStartTime)).setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtWordNotificationStartTimeSummary)).setTextSize(fontSize-4);
        txtWordNotificationStartTimeValue.setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtWordNotificationEndTime)).setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtWordNotificationEndTimeSummary)).setTextSize(fontSize-4);
        txtWordNotificationEndTimeValue.setTextSize(fontSize-2);

        /* Notification Section's. */
        ((AppCompatTextView)findViewById(R.id.txtNotificationSound)).setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtNotificationSoundSummary)).setTextSize(fontSize-4);
        ((AppCompatTextView)findViewById(R.id.txtNotificationVibrate)).setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtNotificationVibrateSummary)).setTextSize(fontSize-4);
        ((AppCompatTextView)findViewById(R.id.txtNotificationHeadsUp)).setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtNotificationHeadsUpSummary)).setTextSize(fontSize-4);

        /* Font Section's. */
        ((AppCompatTextView)findViewById(R.id.txtFont)).setTextSize(fontSize-2);
        ((AppCompatTextView)findViewById(R.id.txtFontSummary)).setTextSize(fontSize-4);
        txtFontValue.setTextSize(fontSize-2);
    }

    /* Util Method. */
    private void restartWordNotificationReceiver(){
        /* "WordNotification" restarting after changed settings. Because last alarm may be left. */

        /* Stop. */
        WordNotificationService.stop(this);

        /* Start. */
        WordNotificationService.start(this);
    }

    /* Preference Values Service. */
    public static int getFont(Context context){
        return getFont(context,0);
    }
    public static int getFont(Context context,int increaseSize){
        return increaseSize+(12)+2*(context.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.FONT,2));
    }
}
