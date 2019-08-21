package com.erdemsiyam.memorizeyourwords.util.listener.word;

import android.view.View;

import com.erdemsiyam.memorizeyourwords.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.WordService;

public class WordCheckButtonOnClickListener implements View.OnClickListener {
    private WordActivity context;
    private Word word;
    public WordCheckButtonOnClickListener(WordActivity context, Word word) {this.context = context; this.word = word;}
    @Override
    public void onClick(View v) {
        WordService.changeLearned(context,word.getId(),!word.isLearned());
        word.setLearned(!word.isLearned());
        context.getAdapter().refreshWord(word);
    }
}
