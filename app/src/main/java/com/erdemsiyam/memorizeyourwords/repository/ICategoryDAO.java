package com.erdemsiyam.memorizeyourwords.repository;

import androidx.room.*;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import java.util.List;

@Dao
public interface ICategoryDAO {
    @Insert
    Long insertCategory(Category category);
    @Update
    void updateCategory(Category category);
    @Delete
    void deleteCategory(Category category);
    @Query("SELECT * FROM Category")
    List<Category> getAllCategory();
    @Query("SELECT * FROM Category WHERE id =:id")
    Category getCategoryById(Long id);
    @Query("Select COUNT(*) from Word WHERE category_id =:id")
    int getCategoryWordCount(Long id);

}
