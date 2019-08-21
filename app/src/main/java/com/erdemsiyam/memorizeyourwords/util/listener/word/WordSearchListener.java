package com.erdemsiyam.memorizeyourwords.util.listener.word;

import androidx.appcompat.widget.SearchView;

import com.erdemsiyam.memorizeyourwords.WordActivity;

public class WordSearchListener implements SearchView.OnQueryTextListener {
    private WordActivity wordActivity;
    public WordSearchListener(WordActivity activity){this.wordActivity = activity;}
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        wordActivity.getAdapter().getFilter().filter(newText); // the word list when filtering, this func will be using
        return false;
    }
}