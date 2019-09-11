package com.erdemsiyam.memorizeyourwords.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;

@Dao
public interface INotificationCategoryDAO {
    @Insert
    Long insert(NotificationCategory notificationCategory);
    @Update
    void update(NotificationCategory notificationCategory);
    @Delete
    void delete(NotificationCategory notificationCategory);

    @Query("SELECT * FROM NotificationCategory WHERE category_id=:categoryId")
    NotificationCategory getByCategory(Long categoryId);

    @Query("Delete FROM NotificationCategory")
    void deleteAll();
}
