package com.erdemsiyam.memorizeyourwords.service;

import android.content.Context;

import com.erdemsiyam.memorizeyourwords.database.MyDatabase;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.Word;

import java.util.List;

public final class WordService {

    public static List<Word> getWordsByCategoryId(Context context, Category category){
        if(category.getWords() == null) // if there allready words puted. we dont waiting client, we services the words being fast.
            category.setWords(MyDatabase.getMyDatabase(context).getWordDAO().getWordsByCategoryId(category.getId())); // when we put words. We also put who owner category.
        return category.getWords();
    }
    public static void updateWord(Context context, Category category, Word updatedWord){
        Word existsWord = findWordInCategoryWordList(category,updatedWord); // buna gerek var mı?
        existsWord.setExplain(updatedWord.getExplain());
        existsWord.setStrange(updatedWord.getStrange());
        existsWord.setActive(updatedWord.getActive());
        existsWord.setCategoryId(updatedWord.getCategoryId());
        existsWord.setDensity(updatedWord.getDensity());
        existsWord.setFalseSelect(updatedWord.getFalseSelect());
        existsWord.setTrueSelect(updatedWord.getTrueSelect()); // buna gerek var mı?
        MyDatabase.getMyDatabase(context).getWordDAO().updateWord(updatedWord);
    }
    public static void deleteWord(Context context, Category category, Word willRemoveWord){
        Word existsWord = findWordInCategoryWordList(category,willRemoveWord); // buna gerek var mı?
        List<Word> words = category.getWords();
        words.remove(existsWord);
        category.setWords(words); // buna gerek var mı?
        MyDatabase.getMyDatabase(context).getWordDAO().deleteWord(willRemoveWord);
    }
    public static void addWord(Context context, Category category, Word word){
        word.setCategoryId(category.getId());
        word.setId(MyDatabase.getMyDatabase(context).getWordDAO().insertWord(word));
        List<Word> words = category.getWords();
        if(words != null){
            words.add(word);
            category.setWords(words);
        }
    }
    //buna gerek var mı?
    private static Word findWordInCategoryWordList(Category category,Word searchWord){
        Word existsWord = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            existsWord = category.getWords().stream().filter(x -> searchWord.getId().equals(x.getId())).findAny().orElse(null);
        }
        else {
            for (Word w : category.getWords()) {
                if(w.getId().equals(searchWord.getId())){
                    existsWord = w;
                    break;
                }
            }
        }
        return existsWord;
    }
}
