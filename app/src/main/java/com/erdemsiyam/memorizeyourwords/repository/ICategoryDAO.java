package com.erdemsiyam.memorizeyourwords.repository;

import androidx.room.*;

import com.erdemsiyam.memorizeyourwords.entity.Category;

@Dao
public interface ICategoryDAO {
    @Insert
    Long insertCategory(Category category);
    @Update
    void updateCategory(Category category);
    @Delete
    void deleteCategory(Category category);
    @Query("SELECT * FROM Category")
    java.util.List<Category> getAllCategory();
}
