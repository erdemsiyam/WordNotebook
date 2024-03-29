package com.erdemsiyam.memorizeyourwords.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.ConfuseService;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.WordGroupType;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExamActivity extends AppCompatActivity {

    /* Constants */
    private static final int DELAY_AUTO_PASS = 1400;

    /* Variables. */
    private List<Word>  words;
    private Word        lastAskedWord;
    private long        timePassing=0;
    private int         trueCount=0,falseCount=0;
    private Button      selectedButton;
    private boolean     isAnswered=false;
    private Thread      threadTimeCounter = new TimeCounterThread();
    private Thread      threadAutoPass = new AutoPassCounterThread();

    /* UI Components */
    private ConstraintLayout        topLayout;
    private TextView                txtTimer,txtStrange;
    private Chip                    chipExamFalseCount,chipExamTrueCount;
    private Button                  btnWord1,btnWord2,btnWord3,btnWord4,btnPass;
    private AppCompatImageButton    btnDone;
    private SwitchCompat            swAutoPass;
    private InterstitialAd          interstitialAd; // Fullscreen Ad.

    /* Override Method. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        initComponents(); // UI components are installed.
        loadData();
        loadFontSizes();
    }

    /* Initial Methods. */
    private void initComponents() {
        /* Loading UI items. */
        topLayout = findViewById(R.id.roundedTopLayout);
        txtTimer = findViewById(R.id.txtTimer);
        txtStrange = findViewById(R.id.txtStrange);
        chipExamFalseCount = findViewById(R.id.chipExamFalseCount);
        chipExamTrueCount = findViewById(R.id.chipExamTrueCount);
        btnWord1 = findViewById(R.id.btnWord1);
        btnWord2 = findViewById(R.id.btnWord2);
        btnWord3 = findViewById(R.id.btnWord3);
        btnWord4 = findViewById(R.id.btnWord4);
        btnPass = findViewById(R.id.btnPass);
        btnDone = findViewById(R.id.btnDone);
        swAutoPass = findViewById(R.id.swAutoPass);

        /* Gives "lickListeners" to UI buttons. */
        btnWord1.setOnClickListener(new WordButtonOnClickListener());
        btnWord2.setOnClickListener(new WordButtonOnClickListener());
        btnWord3.setOnClickListener(new WordButtonOnClickListener());
        btnWord4.setOnClickListener(new WordButtonOnClickListener());
        btnPass.setOnClickListener(new PassButtonOnClickListener());
        btnDone.setOnClickListener(new DoneButtonOnClickListener());
        swAutoPass.setOnClickListener(new SwitchAutoPassOnClickListener());

        /* FontSize loading from Setting. */
        float fontSize = SettingActivity.getFont(this,0); // FontSize loading from Setting.
        txtTimer.setTextSize(fontSize);
        txtStrange.setTextSize(fontSize);
        int minFont = (int)fontSize-6;
        int maxFont = (int)fontSize-2;
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(btnWord1, minFont, maxFont, 2, TypedValue.COMPLEX_UNIT_SP);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(btnWord2, minFont, maxFont, 2, TypedValue.COMPLEX_UNIT_SP);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(btnWord3, minFont, maxFont, 2, TypedValue.COMPLEX_UNIT_SP);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(btnWord4, minFont, maxFont, 2, TypedValue.COMPLEX_UNIT_SP);

        /* Start exam. */
        loadData();

        /* Ad preparing. */
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_ad_interstitial_id));
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish(); // Stop this activity after ad.
            }
        });
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }
    private void loadData() {
        /* We will get the word type selected when entering the exam.
        *  Than take the words which as specified. */
        Intent intent = getIntent();
        WordGroupType wordGroupType = WordGroupType.getTypeByKey(intent.getIntExtra(CategoryActivity.INTENT_EXAM_SELECT_INDEX,0));
        long[] selectedCategoryIds = intent.getLongArrayExtra(CategoryActivity.INTENT_SELECTED_CATEGORY_IDS);

        /* Exam finish if no select any category. */
        if(selectedCategoryIds.length <= 0){
            Toast.makeText(this, R.string.exam_category_size_error, Toast.LENGTH_LONG).show();
            finish();
        }

        /* Get words. */
        words = new ArrayList<>();
        switch (wordGroupType){
            case All:
                    for(long l : selectedCategoryIds){
                        words.addAll(WordService.getWordsByCategoryId(this,l));
                    }
                break;
            case Learned:
                    for(long l : selectedCategoryIds){
                        words.addAll(WordService.getLearnedWordsByCategoryId(this,l));
                    }
                break;
            case Marked:
                    for(long l : selectedCategoryIds){
                        words.addAll(WordService.getMarkedWordsByCategoryId(this,l));
                    }
                break;
            case NotLearned:
                    for(long l : selectedCategoryIds){
                        words.addAll(WordService.getNotLearnedWordsByCategoryId(this,l));
                    }
                break;
        }

        /* Exam finish if the number of words is less than 4. */
        if(words.size()<4){
            Toast.makeText(this, R.string.exam_word_size_error, Toast.LENGTH_LONG).show();
            finish();
        }else{
            /* First question comes. */
            startNewWordExam();
        }

    }
    private void loadFontSizes(){
        txtTimer.setTextSize(SettingActivity.getFont(this,2));
        txtStrange.setTextSize(SettingActivity.getFont(this,2));
        btnWord1.setTextSize(SettingActivity.getFont(this,-3));
        btnWord2.setTextSize(SettingActivity.getFont(this,-3));
        btnWord3.setTextSize(SettingActivity.getFont(this,-3));
        btnWord4.setTextSize(SettingActivity.getFont(this,-3));
    }
    /* Util Methods. */
    private List<Word> getRandom4Word(){
        /* Random 4 words selecting from "WordList". */
        List<Word> selectedWords = new ArrayList<>();
        Random rand = new Random();
        int wordSize = words.size();

        Word firstWord;
        do{
            firstWord = words.get(rand.nextInt(wordSize));
        }while(lastAskedWord == firstWord); // Ensures that it is not the same as the previous asked word.

        lastAskedWord = firstWord;
        selectedWords.add(firstWord);

        for(int i=0;i<3;i++){
            Word nextWord;
            do{
                nextWord = words.get(rand.nextInt(wordSize));
            }while(selectedWords.contains(nextWord)); // Ensures that it is all word diffrent from each other.
            selectedWords.add(nextWord);
        }
        return selectedWords;
    }
    private void startNewWordExam(){
        /* UI loadings for next question. */
        List<Word> words = getRandom4Word(); // Gets random 4 words.
        txtTimer.setText("00"); // Timer text refreshed at front.
        timePassing = 0; // Timer text refreshed at back.
        isAnswered=false; // Specified we are at a question.
        txtStrange.setText(words.get(0).getStrange()); // True word is first question on the list. We get these strange.

        /* Loaded rounded top UI. */
        topLayout.setBackgroundResource(R.drawable.exam_rounded_top_layout);

        /* Answers are randomly placed on buttons. */
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

        /* Buttons color refreshed to default color. */
        btnWord1.getBackground().setColorFilter(getResources().getColor(R.color.main_blue_3), PorterDuff.Mode.SRC_ATOP);
        btnWord2.getBackground().setColorFilter(getResources().getColor(R.color.main_blue_3), PorterDuff.Mode.SRC_ATOP);
        btnWord3.getBackground().setColorFilter(getResources().getColor(R.color.main_blue_3), PorterDuff.Mode.SRC_ATOP);
        btnWord4.getBackground().setColorFilter(getResources().getColor(R.color.main_blue_3), PorterDuff.Mode.SRC_ATOP);

        /* Total true and false select count printed. */
        chipExamFalseCount.setText((falseCount<10)?"0"+falseCount:""+falseCount);
        chipExamTrueCount.setText((trueCount<10)?"0"+trueCount:""+trueCount);

        /* The timer starts. */
        if(!threadTimeCounter.isAlive()){
            threadTimeCounter = new TimeCounterThread();
            threadTimeCounter.start();
        }
    }


    /*################# INNER CLASSES SECTION #################*/

    /* Util Listeners. */
    private class WordButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(isAnswered) // Do nothing if already answered this question.
                return;
            else
                isAnswered=true; // Specified, question are over.

            threadTimeCounter.interrupt(); // Timer stopped.
            selectedButton = (Button) v; // Get the button which are clicked.
            String trueAnswer = lastAskedWord.getExplain(); // Get the right word to compare.

            if(trueAnswer.equals(selectedButton.getText())){ // If the answer in the clicked word is correct.
                selectedButton.getBackground().setColorFilter(getResources().getColor(R.color.category_enter), PorterDuff.Mode.SRC_ATOP); // Pressed button turns green.
                chipExamTrueCount.setText((++trueCount<10)?"0"+trueCount:""+trueCount); // True count increase 1 at front and back.
                WordService.trueSelectIncrease(getApplicationContext(),lastAskedWord.getId(),timePassing); // The correct word's "TrueSelect" value is increase at DB.
            } else { // If the answer in the clicked word is not correct.
                chipExamFalseCount.setText((++falseCount<10)?"0"+falseCount:""+falseCount);// False count increase 1 at front and back.
                selectedButton.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP); // Pressed button turns red.

                /* The button turns "green" which are contain correct answer. */
                if(btnWord1.getText().equals(trueAnswer))
                    btnWord1.getBackground().setColorFilter(getResources().getColor(R.color.category_enter), PorterDuff.Mode.SRC_ATOP);
                else if(btnWord2.getText().equals(trueAnswer))
                    btnWord2.getBackground().setColorFilter(getResources().getColor(R.color.category_enter), PorterDuff.Mode.SRC_ATOP);
                else if(btnWord3.getText().equals(trueAnswer))
                    btnWord3.getBackground().setColorFilter(getResources().getColor(R.color.category_enter), PorterDuff.Mode.SRC_ATOP);
                else if(btnWord4.getText().equals(trueAnswer))
                    btnWord4.getBackground().setColorFilter(getResources().getColor(R.color.category_enter), PorterDuff.Mode.SRC_ATOP);

                WordService.falseSelectIncrease(getApplicationContext(),lastAskedWord.getId(),timePassing); // The word's "FalseSelect" value is increase at DB.
                ConfuseService.addConfuse(getApplicationContext(),lastAskedWord.getId(),((Word)selectedButton.getTag()).getId()); // Wrong answer is saved to DB.
            }

            /* Start auto pass timer if the AutoPass "Switch" are on. */
            if(swAutoPass.isChecked()) {
                threadAutoPass = new AutoPassCounterThread();
                threadAutoPass.start();
            }
        }
    }
    private class PassButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            /* Close the "AutoPassTimer" if clicked the pass button manually while "AutoPassTimer" working. */
            if(threadAutoPass.isAlive())
                threadAutoPass.interrupt();

            startNewWordExam();
        }
    }
    private class SwitchAutoPassOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            /* May timer works when question answered. We will control it. */
            if(isAnswered){
                if(swAutoPass.isChecked()){ /* Start  */
                    if(!threadAutoPass.isAlive()) {
                        threadAutoPass = new AutoPassCounterThread();
                        threadAutoPass.start();
                    }
                }else{
                    if(threadAutoPass.isAlive())
                        threadAutoPass.interrupt();
                }
            }
        }
    }
    private class DoneButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            /* Show Ad before closing activity. */
            if(interstitialAd.isLoaded()){
                interstitialAd.show();
            }
            /* Exam finisher, activity terminate.*/
            finish();
        }
    }

    /* Util Timers. */
    private class TimeCounterThread extends Thread {
        @Override
        public void run() {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    sleep(1000);
                    runOnUiThread(new Runnable() { // If you want some changes at UI with thread, then you must use this method and create a "Runnable".
                        @Override
                        public void run() {
                            timePassing +=1; // Time increase at Back.
                            txtTimer.setText((timePassing<10)?"0"+timePassing:timePassing+""); // Time increase at Front.
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
                sleep(DELAY_AUTO_PASS);
                if(Thread.currentThread().isInterrupted()) return; // Cancel the task if unexpectedly "AutoPass" termination.
                runOnUiThread(new Runnable() { // If you want some changes at UI with thread, then you must use this method and create a "Runnable".
                    @Override
                    public void run() {
                        startNewWordExam();
                    }
                });
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
