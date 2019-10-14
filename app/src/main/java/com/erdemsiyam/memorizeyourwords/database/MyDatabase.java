package com.erdemsiyam.memorizeyourwords.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.Confuse;
import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;
import com.erdemsiyam.memorizeyourwords.entity.NotificationWord;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.repository.ICategoryDAO;
import com.erdemsiyam.memorizeyourwords.repository.IConfuseDAO;
import com.erdemsiyam.memorizeyourwords.repository.INotificationCategoryDAO;
import com.erdemsiyam.memorizeyourwords.repository.INotificationWordDAO;
import com.erdemsiyam.memorizeyourwords.repository.IWordDAO;

@Database(entities = {Word.class, Category.class, Confuse.class, NotificationWord.class, NotificationCategory.class}, version = 2, exportSchema= false)
public abstract class MyDatabase extends RoomDatabase {

    private static final String DB_NAME = "WordNotebook.db";
    public abstract ICategoryDAO getCategoryDAO();
    public abstract IWordDAO getWordDAO();
    public abstract IConfuseDAO getConfuseDAO();
    public abstract INotificationWordDAO getNotificationWordDAO();
    public abstract INotificationCategoryDAO getNotificationCategoryDAO();

    private static MyDatabase myDatabase;
    public static MyDatabase getMyDatabase(Context context) {
        if (myDatabase == null)
            myDatabase = Room.databaseBuilder(context, MyDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries().build();
        return myDatabase;
    }
    public static void destroyInstance() { myDatabase = null; }

    /* Migrations */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            /* Deleting "Color" column to "Category" table. */
            database.execSQL("CREATE TEMPORARY TABLE Category_backup(id INTEGER PRIMARY KEY,name TEXT)");
            database.execSQL("INSERT INTO Category_backup SELECT id,name FROM Category");
            database.execSQL("DROP TABLE Category");
            database.execSQL("CREATE TABLE Category(id INTEGER PRIMARY KEY,name TEXT)");
            database.execSQL("INSERT INTO Category SELECT id,name FROM Category_backup");
            database.execSQL("DROP TABLE Category_backup");

            /* Adding "visibilityWordGroupType" column to "Category" table. */
            database.execSQL("ALTER TABLE Category ADD COLUMN visibilityWordGroupType INTEGER NOT NULL Default 0");
        }
    };
}