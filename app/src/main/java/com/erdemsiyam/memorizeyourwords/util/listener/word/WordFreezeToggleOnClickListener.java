package com.erdemsiyam.memorizeyourwords.util.listener.word;

import android.view.View;

import com.erdemsiyam.memorizeyourwords.WordActivity;

public class WordFreezeToggleOnClickListener implements View.OnClickListener {
    private WordActivity wordActivity;
    public WordFreezeToggleOnClickListener(WordActivity wordActivity) {this.wordActivity = wordActivity;}
    @Override
    public void onClick(View v) {
        wordActivity.getAdapter().toggleFreeze();
    }
}
