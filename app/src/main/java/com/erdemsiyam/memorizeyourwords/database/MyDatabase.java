package com.erdemsiyam.memorizeyourwords.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.erdemsiyam.memorizeyourwords.entity.List;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.repository.IListDAO;
import com.erdemsiyam.memorizeyourwords.repository.IWordDAO;

@Database(entities = {Word.class, List.class}, version = 1, exportSchema= false)
public abstract class MyDatabase extends RoomDatabase {

    private static final String DB_NAME = "MemorizeYourWords.db";
    public abstract IListDAO getListDAO();
    public abstract IWordDAO getWordDAO();

    private static MyDatabase myDatabase;
    public static MyDatabase getAppDatabase(Context context) {
        if (myDatabase == null)
            myDatabase =  Room.databaseBuilder(context, MyDatabase.class, DB_NAME).allowMainThreadQueries().build();
        return myDatabase;
    }
    public static void destroyInstance() { myDatabase = null; }
}