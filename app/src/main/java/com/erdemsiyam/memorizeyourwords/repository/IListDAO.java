package com.erdemsiyam.memorizeyourwords.repository;

import androidx.room.*;
import com.erdemsiyam.memorizeyourwords.entity.List;

@Dao
public interface IListDAO {
    @Insert
    Long insertList(List list);
    @Update
    void updateList(List list);
    @Delete
    void deleteList(List list);
    @Query("SELECT * FROM List")
    java.util.List<List> getAllList();
}
