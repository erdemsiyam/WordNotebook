package com.erdemsiyam.memorizeyourwords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.ConfuseService;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.listener.category.CategorySelectActionModeCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

public class ExamActivity extends AppCompatActivity {

    private List<Word> words;
    private Word lastAskedWord;
    private long timePassing=0;
    private int trueCount=0,falseCount=0;
    private Button selectedButton;
    private boolean isAnswered=false;
    private Thread threadTimeCounter = new TimeCounterThread();
    private Thread threadAutoPass = new AutoPassCounterThread();

    // ui components
    private ConstraintLayout topLayout;
    private TextView txtTimer,txtStrange,txtFalseCount,txtTrueCount;
    private Button btnWord1,btnWord2,btnWord3,btnWord4,btnPass;
    private AppCompatImageButton btnDone;
    private SwitchCompat swAutoPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        initComponents();
        loadData();
    }

    private void initComponents() {
        topLayout = findViewById(R.id.roundedTopLayout);
        txtTimer = findViewById(R.id.txtTimer);
        txtStrange = findViewById(R.id.txtStrange);
        txtFalseCount = findViewById(R.id.txtFalseCount);
        txtTrueCount = findViewById(R.id.txtTrueCount);
        btnWord1 = findViewById(R.id.btnWord1);
        btnWord2 = findViewById(R.id.btnWord2);
        btnWord3 = findViewById(R.id.btnWord3);
        btnWord4 = findViewById(R.id.btnWord4);
        btnPass = findViewById(R.id.btnPass);
        btnDone = findViewById(R.id.btnDone);
        swAutoPass = findViewById(R.id.swAutoPass);

        btnWord1.setOnClickListener(new WordButtonOnClickListener());
        btnWord2.setOnClickListener(new WordButtonOnClickListener());
        btnWord3.setOnClickListener(new WordButtonOnClickListener());
        btnWord4.setOnClickListener(new WordButtonOnClickListener());
        btnPass.setOnClickListener(new PassButtonOnClickListener());

    }
    private void loadData() {
        Intent intent = getIntent();
        int examSelectIndex = intent.getIntExtra(CategoryActivity.INTENT_EXAM_SELECT_INDEX,0);
        long[] selectedCategoryIds = intent.getLongArrayExtra(CategoryActivity.INTENT_SELECTED_CATEGORY_IDS);
        words = new ArrayList<>();
        switch (examSelectIndex){
            case 0:
                    for(long l : selectedCategoryIds){
                        words.addAll(WordService.getWordsByCategoryId(this,l));
                    }
                break;
            case 1:
                    for(long l : selectedCategoryIds){
                        words.addAll(WordService.getLearnedWordsByCategoryId(this,l));
                    }
                break;
            case 2:
                    for(long l : selectedCategoryIds){
                        words.addAll(WordService.getMarkedWordsByCategoryId(this,l));
                    }
                break;
            case 3:
                    for(long l : selectedCategoryIds){
                        words.addAll(WordService.getNotLearnedWordsByCategoryId(this,l));
                    }
                break;
        }
        if(words.size()<4){
            Toast.makeText(this, "En az 4 kelime olmalı", Toast.LENGTH_LONG).show();
            finish();
        }
        startWordExam(getRandom4Word());
    }

    private List<Word> getRandom4Word(){
        List<Word> selectedWords = new ArrayList<>();
        Random rand = new Random();
        int wordSize = words.size();

        Word firstWord;
        do{
            firstWord = words.get(rand.nextInt(wordSize));
        }while(lastAskedWord == firstWord);

        lastAskedWord = firstWord;
        selectedWords.add(firstWord);

        for(int i=0;i<3;i++){
            Word nextWord;
            do{
                nextWord = words.get(rand.nextInt(wordSize));
            }while(selectedWords.contains(nextWord));
            selectedWords.add(nextWord);
        }
        return selectedWords;
    }
    private void startWordExam(List<Word> words){
        txtTimer.setText("00");
        timePassing = 0;
        isAnswered=false;

        topLayout.setBackgroundResource(R.drawable.exam_rounded_top_layout);
        txtStrange.setText(words.get(0).getStrange());

        Random rand = new Random();
        Word w1 = words.get(rand.nextInt(4));
        words.remove(w1);
        btnWord1.setTag(w1);
        btnWord1.setText(w1.getExplain());
        Word w2 = words.get(rand.nextInt(3));
        words.remove(w2);
        btnWord2.setTag(w2);
        btnWord2.setText(w2.getExplain());
        Word w3 = words.get(rand.nextInt(2));
        words.remove(w3);
        btnWord3.setTag(w3);
        btnWord3.setText(w3.getExplain());
        Word w4 = words.get(0);
        words.remove(w4);
        btnWord4.setTag(w4);
        btnWord4.setText(w4.getExplain());

        btnWord1.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        btnWord2.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        btnWord3.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        btnWord4.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        btnDone.setOnClickListener(new DoneButtonOnClickListener());
        swAutoPass.setOnClickListener(new SwitchAutoPassOnClickListener());


        txtFalseCount.setText(falseCount+"");
        txtTrueCount.setText(trueCount+"");

        threadTimeCounter.start();
    }

    private class WordButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(isAnswered)return; else isAnswered=true;

            threadTimeCounter.interrupt();
            selectedButton = (Button) v;
            String trueAnswer = lastAskedWord.getExplain();
            if(trueAnswer.equals(selectedButton.getText())){
                selectedButton.getBackground().setColorFilter(getResources().getColor(R.color.colorEnter), PorterDuff.Mode.SRC_ATOP);
                txtTrueCount.setText(++trueCount +"");

                WordService.trueSelectIncrease(getApplicationContext(),lastAskedWord.getId(),timePassing);
            } else {
                //yanlis
                txtFalseCount.setText(++falseCount +"");

                selectedButton.getBackground().setColorFilter(getResources().getColor(R.color.colorDelete), PorterDuff.Mode.SRC_ATOP);
                if(btnWord1.getText().equals(trueAnswer))
                    btnWord1.getBackground().setColorFilter(getResources().getColor(R.color.colorEnter), PorterDuff.Mode.SRC_ATOP);
                else if(btnWord2.getText().equals(trueAnswer))
                    btnWord2.getBackground().setColorFilter(getResources().getColor(R.color.colorEnter), PorterDuff.Mode.SRC_ATOP);
                else if(btnWord3.getText().equals(trueAnswer))
                    btnWord3.getBackground().setColorFilter(getResources().getColor(R.color.colorEnter), PorterDuff.Mode.SRC_ATOP);
                else if(btnWord4.getText().equals(trueAnswer))
                    btnWord4.getBackground().setColorFilter(getResources().getColor(R.color.colorEnter), PorterDuff.Mode.SRC_ATOP);

                WordService.falseSelectIncrease(getApplicationContext(),lastAskedWord.getId(),timePassing);
                ConfuseService.addConfuse(getApplicationContext(),lastAskedWord.getId(),((Word)selectedButton.getTag()).getId());
            }
            if(swAutoPass.isChecked())
                threadAutoPass.start();
        }
    }
    private class PassButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(swAutoPass.isChecked()) threadAutoPass.interrupt();// auto acik ama kişi manuel gectiyse auto atlamayı geç
            startWordExam(getRandom4Word());
        }
    }
    private class SwitchAutoPassOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!swAutoPass.isChecked()) // kişi son anda tiklarsa switch'e, görevde oto yenileme varsa iptal edilir.
                threadAutoPass.interrupt();
            else
            {
                if(isAnswered) threadAutoPass.start();// cevaptan sonra oto tiklanirsa baslatilir.
            }
        }
    }
    private class DoneButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class TimeCounterThread extends Thread {
        @Override
        public void run() {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    sleep(1000);
                    runOnUiThread(new Runnable() { // ui componente ulasmak icin
                        @Override
                        public void run() {
                            timePassing +=1;
                            txtTimer.setText((timePassing<10)?"0"+timePassing:timePassing+"");
                        }
                    });
                }
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class AutoPassCounterThread extends Thread {
        @Override
        public void run() {
            try {
                sleep(1400);
                if(Thread.currentThread().isInterrupted()) return; // auto pass beklenilmeden gecildiyse görevi iptal et

                runOnUiThread(new Runnable() { // ui componente ulasmak icin
                    @Override
                    public void run() {
                        startWordExam(getRandom4Word());
                    }
                });
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
