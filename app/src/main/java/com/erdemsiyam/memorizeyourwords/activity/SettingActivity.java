package com.erdemsiyam.memorizeyourwords.activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.androidservice.WordNotificationService;

public class SettingActivity extends AppCompatActivity {
    /*  Setting Page.
        Using SharedPreferences. */

    /* Constants : for "SharedPreferences". */
    public static final String  PREFERENCE_NAME = "setting_preference";
    public static final int     PREFERENCE_MODE = MODE_PRIVATE;
    public static final String  WORD_NOTIFICATION_PERIOD = "word_notification_periot";
    public static final String  WORD_NOTIFICATION_START_TIME_HOUR = "word_notification_start_time_hour";
    public static final String  WORD_NOTIFICATION_START_TIME_MINUTE = "word_notification_start_time_minute";
    public static final String  WORD_NOTIFICATION_END_TIME_HOUR = "word_notification_end_time_hour";
    public static final String  WORD_NOTIFICATION_END_TIME_MINUTE = "word_notification_end_time_minute";
    public static final String  FONT = "font";

    /* UI Components. */
    private LinearLayout lytWordNotificationPeriod;
    private LinearLayout lytWordNotificationStartTime;
    private LinearLayout lytWordNotificationEndTime;
    private LinearLayout lytFont;
    private AppCompatTextView txtWordNotificationPeriodValue;
    private AppCompatTextView txtWordNotificationStartTimeValue;
    private AppCompatTextView txtWordNotificationEndTimeValue;
    private AppCompatTextView txtFontValue;

    /* Variable. */
    private SharedPreferences sharedPreferences;

    /* Override Method. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initComponents(); // UI components are installed.
        loadData(); // Data is loaded into UI components.
    }

    /* Initial Methods. */
    private void initComponents() {
        lytWordNotificationPeriod = findViewById(R.id.lytWordNotificationPeriod);
        lytWordNotificationStartTime = findViewById(R.id.lytWordNotificationStartTime);
        lytWordNotificationEndTime = findViewById(R.id.lytWordNotificationEndTime);
        lytFont = findViewById(R.id.lytFont);
        txtWordNotificationPeriodValue = findViewById(R.id.txtWordNotificationPeriodValue);
        txtWordNotificationStartTimeValue = findViewById(R.id.txtWordNotificationStartTimeValue);
        txtWordNotificationEndTimeValue = findViewById(R.id.txtWordNotificationEndTimeValue);
        txtFontValue = findViewById(R.id.txtFontValue);
    }
    private void loadData() {
        /* Obtaining the "SharedPreferences" object. */
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME,PREFERENCE_MODE);

        /* Filling UI values. */
        txtWordNotificationPeriodValue.setText(sharedPreferences.getInt(WORD_NOTIFICATION_PERIOD,30)+" dakika"); // todo string res at dakikaya
        int startHourInt = sharedPreferences.getInt(WORD_NOTIFICATION_START_TIME_HOUR,9);
        int startMinuteInt = sharedPreferences.getInt(WORD_NOTIFICATION_START_TIME_MINUTE,0);
        txtWordNotificationStartTimeValue.setText(((startHourInt<10)?"0"+startHourInt:""+startHourInt)+" : "+((startMinuteInt<10)?"0"+startMinuteInt:""+startMinuteInt));
        int endHourInt = sharedPreferences.getInt(WORD_NOTIFICATION_END_TIME_HOUR,23);
        int endMinuteInt = sharedPreferences.getInt(WORD_NOTIFICATION_END_TIME_MINUTE,59);
        txtWordNotificationEndTimeValue.setText(((endHourInt<10)?"0"+endHourInt:""+endHourInt)+" : "+((endMinuteInt<10)?"0"+endMinuteInt:""+endMinuteInt));
        txtFontValue.setText((12 + 2*sharedPreferences.getInt(FONT,2))+" sp");

        /* Listeners for settings. */
        lytWordNotificationPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new Dialog(SettingActivity.this); // Our custom dialog created.
                d.setTitle(R.string.setting_word_notification_alert_title_cycle_time);
                d.setContentView(R.layout.dialog_setting_number_pick); // Including custom layout to custom dialog.
                Button btnSettingNumberPickAccept = d.findViewById(R.id.btnSettingNumberPickAccept);
                NumberPicker np = d.findViewById(R.id.npWordNotificationPeriodValue); // A NumberPicker created.
                np.setMaxValue(720);
                np.setMinValue(10);
                np.setValue(sharedPreferences.getInt(WORD_NOTIFICATION_PERIOD,30));
                np.setWrapSelectorWheel(false);
                btnSettingNumberPickAccept.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        sharedPreferences.edit().putInt(WORD_NOTIFICATION_PERIOD,np.getValue()).commit(); // WordNotification "CycleTime" changed.
                        txtWordNotificationPeriodValue.setText(np.getValue() + " " + getResources().getString(R.string.minute)); // UI refreshed.
                        restartWordNotificationService(); // Restarting "WordNotificationService".
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
                                .commit(); // Start time value changed.
                        txtWordNotificationStartTimeValue.setText(((hourOfDay<10)?"0"+hourOfDay:""+hourOfDay)+" : "+((minute<10)?"0"+minute:""+minute)); // UI refreshed.
                        restartWordNotificationService(); // Restarting "WordNotificationService".
                    }
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingActivity.this, new TimeHandler(), 9, 0, true);
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
                                .commit(); // End time value changed.
                        txtWordNotificationEndTimeValue.setText(((hourOfDay<10)?"0"+hourOfDay:""+hourOfDay)+" : "+((minute<10)?"0"+minute:""+minute)); // UI refreshed.
                        restartWordNotificationService(); // Restarting "WordNotificationService".
                    }
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingActivity.this, new TimeHandler(), 23, 59, true);
                timePickerDialog.setTitle(R.string.setting_word_notification_alert_title_set_time);
                timePickerDialog.show();
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
                np.setMinValue(0);
                np.setMaxValue(4);
                np.setDisplayedValues(new String[]{"12","14","16","18","20"});
                np.setValue(sharedPreferences.getInt(FONT,2));
                np.setWrapSelectorWheel(false);
                btnSettingNumberPickAccept.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        sharedPreferences.edit().putInt(FONT,np.getValue()).commit(); // "FontSize" changed.
                        txtFontValue.setText((12 + 2*np.getValue()) + " sp"); // UI refreshed.
                        d.dismiss(); // Dialog closed.
                    }
                });
                d.show();
            }
        });
    }

    /* Util Method. */
    private void restartWordNotificationService(){
        /* "WordNotification" restarting after changed settings. Because last process may be left. */
        stopService(new Intent(getApplicationContext(),WordNotificationService.class));
        startService(new Intent(getApplicationContext(),WordNotificationService.class));
    }
}
