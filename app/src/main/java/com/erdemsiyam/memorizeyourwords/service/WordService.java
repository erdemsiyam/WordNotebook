package com.erdemsiyam.memorizeyourwords.service;

import android.content.Context;

import com.erdemsiyam.memorizeyourwords.database.MyDatabase;
import com.erdemsiyam.memorizeyourwords.entity.Word;

import java.util.List;

public final class WordService {

    public static List<Word> getWordsByCategoryId(Context context, Long categoryId){
        return MyDatabase.getMyDatabase(context).getWordDAO().getWordsByCategoryId(categoryId);
    }
    public static List<Word> getLearnedWordsByCategoryId(Context context, Long categoryId){
        return MyDatabase.getMyDatabase(context).getWordDAO().getLearnedWordsByCategoryId(categoryId);
    }
    public static List<Word> getMarkedWordsByCategoryId(Context context, Long categoryId){
        return MyDatabase.getMyDatabase(context).getWordDAO().getMarkedWordsByCategoryId(categoryId);
    }
    public static List<Word> getNotLearnedWordsByCategoryId(Context context, Long categoryId){
        return MyDatabase.getMyDatabase(context).getWordDAO().getNotLearnedWordsByCategoryId(categoryId);
    }
    public static List<Word>  getAllWords(Context context){
        return MyDatabase.getMyDatabase(context).getWordDAO().getAllWord();
    }
    public static Word getWordById(Context context, Long wordId){
        Word word = MyDatabase.getMyDatabase(context).getWordDAO().getWordById(wordId);
        if(word == null)
            throw new RuntimeException("Word Not Found.");
        return word;
    }
    public static Word addWord(Context context, Long categoryId, String strange, String explain){
        Word word = new Word();
        word.setStrange(strange.trim());
        word.setExplain(explain.trim());
        word.setWriteTrue(0);
        word.setWriteFalse(0);
        word.setTrueSelect(0);
        word.setFalseSelect(0);
        word.setSpendTime(0);
        word.setLearned(false);
        word.setMark(false);
        word.setCategoryId(categoryId);
        word.setId(MyDatabase.getMyDatabase(context).getWordDAO().insertWord(word));
        return word;
    }
    public static void deleteWord(Context context, Word removeWord){
        MyDatabase.getMyDatabase(context).getWordDAO().deleteWord(removeWord);
        MyDatabase.getMyDatabase(context).getConfuseDAO().deleteConfusesByWordId(removeWord.getId());
    }
    public static void changeWordStrange(Context context, Long wordId, String newStrange){
        Word word = getWordById(context,wordId);
        MyDatabase.getMyDatabase(context).getWordDAO().changeWordStrange(word.getId(),newStrange);
    }
    public static void changeWordExplain(Context context, Long wordId, String newExplain){
        Word word = getWordById(context,wordId);
        MyDatabase.getMyDatabase(context).getWordDAO().changeWordExplain(word.getId(),newExplain);
    }
    public static void trueSelectIncrease(Context context, Long wordId, Long spendTime){
        Word word = getWordById(context,wordId);
        MyDatabase.getMyDatabase(context).getWordDAO().trueSelectIncrease(word.getId());
        MyDatabase.getMyDatabase(context).getWordDAO().addSpendTime(wordId,spendTime);
    }
    public static void falseSelectIncrease(Context context, Long wordId, Long spendTime){
        Word word = getWordById(context,wordId);
        MyDatabase.getMyDatabase(context).getWordDAO().falseSelectIncrease(word.getId());
        MyDatabase.getMyDatabase(context).getWordDAO().addSpendTime(wordId,spendTime);
    }
    public static void changeLearned(Context context, Long wordId, boolean status){
        MyDatabase.getMyDatabase(context).getWordDAO().changeLearned(wordId,status);
    }
    public static void changeMark(Context context, Long wordId, boolean status){
        MyDatabase.getMyDatabase(context).getWordDAO().changeMark(wordId,status);
    }

}
