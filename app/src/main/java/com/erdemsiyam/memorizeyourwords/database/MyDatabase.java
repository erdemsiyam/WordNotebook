package com.erdemsiyam.memorizeyourwords.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.Confuse;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.repository.ICategoryDAO;
import com.erdemsiyam.memorizeyourwords.repository.IConfuseDAO;
import com.erdemsiyam.memorizeyourwords.repository.IWordDAO;

@Database(entities = {Word.class, Category.class, Confuse.class}, version = 1, exportSchema= false)
public abstract class MyDatabase extends RoomDatabase {

    private static final String DB_NAME = "MemorizeYourWords2.db";
    public abstract ICategoryDAO getCategoryDAO();
    public abstract IWordDAO getWordDAO();
    public abstract IConfuseDAO getConfuseDAO();

    private static MyDatabase myDatabase;
    public static MyDatabase getMyDatabase(Context context) {
        if (myDatabase == null)
            myDatabase =  Room.databaseBuilder(context, MyDatabase.class, DB_NAME).allowMainThreadQueries().build();
        return myDatabase;
    }
    public static void destroyInstance() { myDatabase = null; }
}