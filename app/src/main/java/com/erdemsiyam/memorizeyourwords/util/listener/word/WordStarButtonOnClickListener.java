package com.erdemsiyam.memorizeyourwords.util.listener.word;

import android.view.View;

import com.erdemsiyam.memorizeyourwords.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.WordService;

public class WordStarButtonOnClickListener implements View.OnClickListener {
    private WordActivity context;
    private Word word;
    public WordStarButtonOnClickListener(WordActivity context, Word word) {this.context = context; this.word = word;}
    @Override
    public void onClick(View v) {
        WordService.changeMark(context,word.getId(),!word.isMark());
        word.setMark(!word.isMark());
        context.getAdapter().refreshWord(word);
    }
}
