package com.erdemsiyam.memorizeyourwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.erdemsiyam.memorizeyourwords.util.listener.category.CategorySelectActionModeCallBack;

public class ExamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        initComponents();
        loadData();
    }

    private void initComponents() {

    }

    private void loadData() {

        Intent intent = getIntent();
        int examSelectIndex = intent.getIntExtra(CategoryActivity.INTENT_EXAM_SELECT_INDEX,0);
        long[] selectedCategoryIds = intent.getLongArrayExtra(CategoryActivity.INTENT_SELECTED_CATEGORY_IDS);

        Toast.makeText(this,selectedCategoryIds[0]+" "+selectedCategoryIds[1],Toast.LENGTH_SHORT).show();


    }
}
