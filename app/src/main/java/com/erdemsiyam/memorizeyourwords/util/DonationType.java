package com.erdemsiyam.memorizeyourwords.util;

import android.content.Context;

import androidx.annotation.StringRes;

import com.erdemsiyam.memorizeyourwords.R;

import java.util.Random;

public enum DonationType {
    TWO(0,"two","2 $", R.string.donation_two_dolar_joke_1),
    FIVE(1,"five","5 $",R.string.donation_five_dolar_joke_1),
    TWENTY(2,"twenty","20 $",R.string.donation_twenty_dolar_joke_1),
    FIFTY(3,"fifty","50 $",R.string.donation_fifty_dolar_joke_1);

    public int key; // It is an identity
    public String value;
    public String displayValue; //
    @StringRes
    public int[] jokes; // It is a string resource. Equals multiple language texts.

    private static Random random = new Random();

    DonationType(int key, String value, String displayValue, @StringRes int... jokes){
        this.key = key;
        this.value = value;
        this.displayValue = displayValue;
        this.jokes = jokes;
    }
    public static String[] getValuesAsStringArray(){
        String[] values = new String[DonationType.values().length];
        for(int i = 0; i < DonationType.values().length; i++){
            values[i] = DonationType.values()[i].displayValue;
        }
        return values;
    }
    public static DonationType getTypeByKey(int key){
        for (DonationType type : values()) {
            if (type.key == key)
                return type;
        }
        return null;
    }
    public static String getRandomJokeByKey(Context context, int key){
        DonationType dt = getTypeByKey(key);
        int jokeSize = dt.jokes.length;
        return context.getString(dt.jokes[random.nextInt(jokeSize)]);
    }

    public enum Fault {
        Joke1(R.string.donation_fault_joke_1),
        Joke2(R.string.donation_fault_joke_2);

        @StringRes
        public int joke;
        Fault(@StringRes int joke){
            this.joke = joke;
        }
        public static String getRandomJoke(Context context){
            int jokeSize = Fault.values().length;
            return context.getString(Fault.values()[random.nextInt(jokeSize)].joke);
        }
    }
}
