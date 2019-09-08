package com.erdemsiyam.memorizeyourwords.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.erdemsiyam.memorizeyourwords.entity.NotificationWord;
import com.erdemsiyam.memorizeyourwords.entity.Word;

import java.util.List;

@Dao
public interface INotificationWordDAO {
    @Insert
    Long insert(NotificationWord notificationWord);
    @Update
    void update(NotificationWord notificationWord);
    @Delete
    void delete(NotificationWord notificationWord);
    @Query("SELECT * FROM NotificationWord")
    List<NotificationWord> getAll();
    @Query("SELECT * FROM NotificationWord WHERE category_id=:categoryId")
    NotificationWord getByCategory(Long categoryId);
    @Query("SELECT Count(*) FROM NotificationWord")
    int getHowManyNotificationWord();

    @Query("SELECT Count(*) FROM Word WHERE category_id=:categoryId")
    int getWordsCountFromCategoryByAll(Long categoryId);
    @Query("SELECT Count(*) FROM Word WHERE category_id=:categoryId AND learned=1")
    int getWordsCountFromCategoryByLearned(Long categoryId);
    @Query("SELECT Count(*) FROM Word WHERE category_id=:categoryId AND mark=1")
    int getWordsCountFromCategoryByMarked(Long categoryId);
    @Query("SELECT Count(*) FROM Word WHERE category_id=:categoryId AND learned=0")
    int getWordsCountFromCategoryByNotLearned(Long categoryId);

    @Query("SELECT * FROM Word WHERE category_id=:categoryId LIMIT :index-1,1")
    Word getWordAtIndexFromCategoryByAll(Long categoryId, int index);
    @Query("SELECT * FROM Word WHERE category_id=:categoryId AND learned=1 LIMIT :index-1,1")
    Word getWordAtIndexFromCategoryByLearned(Long categoryId,int index);
    @Query("SELECT * FROM Word WHERE category_id=:categoryId AND mark=1 LIMIT :index-1,1")
    Word getWordAtIndexFromCategoryByMarked(Long categoryId,int index);
    @Query("SELECT * FROM Word WHERE category_id=:categoryId AND learned=0 LIMIT :index-1,1")
    Word getWordAtIndexFromCategoryByNotLearned(Long categoryId,int index);
}
