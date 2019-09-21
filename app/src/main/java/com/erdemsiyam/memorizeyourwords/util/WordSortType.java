package com.erdemsiyam.memorizeyourwords.util;

import android.content.Context;
import androidx.annotation.StringRes;
import com.erdemsiyam.memorizeyourwords.R;

public enum WordSortType {
    MostCorrectlySelected(0, R.string.type_word_sort_mostcorrectlyselected),
    MostIncorrectlySelected(1, R.string.type_word_sort_mostincorrectlyselected),
    StrangeAZ(2, R.string.type_word_sort_strangeaz),
    StrangeZA(3, R.string.type_word_sort_strangeza),
    ExplainAZ(4, R.string.type_word_sort_explainaz),
    ExplainZA(5, R.string.type_word_sort_Explainza);

    public int key; // It is an identity
    @StringRes
    public int value; // It is a string resource. Equals multiple language texts.
    WordSortType(int key,@StringRes int value){this.key = key;this.value = value;}

    public static String[] getValuesAsStringArray(Context context){
        String[] values = new String[WordSortType.values().length];
        for(int i = 0; i < WordSortType.values().length; i++){
            values[i] =  context.getResources().getString(WordSortType.values()[i].value);
        }
        return values;
    }
    public static WordSortType getTypeByKey(int key){
        for (WordSortType type : values()) {
            if (type.key == key)
                return type;
        }
        return null;
    }
}
