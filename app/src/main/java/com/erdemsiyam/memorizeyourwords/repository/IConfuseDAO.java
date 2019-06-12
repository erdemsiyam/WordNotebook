package com.erdemsiyam.memorizeyourwords.repository;

import androidx.room.*;
import com.erdemsiyam.memorizeyourwords.entity.Confuse;
import java.util.List;

@Dao
public interface IConfuseDAO {
    @Insert
    Long insertConfuse(Confuse confuse);
    @Update
    void updateConfuse(Confuse confuse);
    @Delete
    void deleteConfuse(Confuse confuse);
    @Query("SELECT * FROM Confuse WHERE word_id=:id")
    List<Confuse> getWrongsInsteadOfThisWord(Long id);
    @Query("SELECT * FROM Confuse WHERE wrong_word_id=:id")
    List<Confuse> getThisWordInsteadOfOthers(Long id);
}
