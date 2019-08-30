package com.erdemsiyam.memorizeyourwords.listener;

import android.app.TimePickerDialog;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.adapter.CategoryRecyclerViewAdapter;

public class CategoryNotificationButtonOnClickListener implements View.OnClickListener {
    public AppCompatActivity context;
    public CategoryRecyclerViewAdapter adapter;
    public Category category;
    public CategoryNotificationButtonOnClickListener(AppCompatActivity context,CategoryRecyclerViewAdapter adapter, Category category){this.context=context;this.adapter=adapter;this.category=category;}
    @Override
    public void onClick(View v) {
        if(category.getAlarm() <= 0) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, new MyTimeHandler(), 0, 0, true);
            timePickerDialog.setTitle("Her Gün Saat Kaçta Bildirilsin?");
            timePickerDialog.show();
        }
        else
        {
            category.setAlarm(0); // alarm turnoff
            // servise ve db'ye yüklenir ?
            adapter.updateCategory(category); // görünürdeki alarm simgesi yenilenmesi için.
        }
    }

    public class MyTimeHandler implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Toast.makeText(context, R.string.soon, Toast.LENGTH_SHORT).show();
            /*
            category.setAlarm(hourOfDay+minute); // gerekeni yap
            // servise ve db'ye yüklenir ?
            adapter.updateCategory(category); // görünürdeki alarm simgesi yenilenmesi için.
            */
        }
    }
}
