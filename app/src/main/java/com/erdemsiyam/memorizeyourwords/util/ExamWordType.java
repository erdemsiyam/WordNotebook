package com.erdemsiyam.memorizeyourwords.util;

import android.content.Context;

import androidx.annotation.StringRes;

import com.erdemsiyam.memorizeyourwords.R;

public enum ExamWordType {
    All(0,R.string.type_exam_word_all),
    Learned(1,R.string.type_exam_word_learned),
    Marked(2,R.string.type_exam_word_marked),
    NotLearned(3,R.string.type_exam_word_notlearned);

    public int key; // It is an identity
    @StringRes
    public int value; // It is a string resource. Equals multiple language texts.

    ExamWordType(int key,@StringRes int value){this.key = key;this.value = value;}
    public static String[] getValuesAsStringArray(Context context){
        String[] values = new String[ExamWordType.values().length];
        for(int i = 0; i < ExamWordType.values().length; i++){
            values[i] = context.getResources().getString(ExamWordType.values()[i].value);
        }
        return values;
    }
    public static ExamWordType getTypeByKey(int key){
        for (ExamWordType type : values()) {
            if (type.key == key)
                return type;
        }
        return null;
    }
}
