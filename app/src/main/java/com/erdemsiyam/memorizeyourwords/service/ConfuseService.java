package com.erdemsiyam.memorizeyourwords.service;

import android.content.Context;

import com.erdemsiyam.memorizeyourwords.database.MyDatabase;
import com.erdemsiyam.memorizeyourwords.entity.Confuse;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import java.util.List;

public class ConfuseService {
    public static List<Confuse> getWrongsInsteadOfThisWord(Context context, Word word){
        return MyDatabase.getMyDatabase(context).getConfuseDAO().getWrongsInsteadOfThisWord(word.getId());
    }
    public static List<Confuse> getThisWordInsteadOfOthers(Context context, Word word){
        return MyDatabase.getMyDatabase(context).getConfuseDAO().getThisWordInsteadOfOthers(word.getId());
    }
}
