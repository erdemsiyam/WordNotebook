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
    @Query("SELECT * FROM Confuse WHERE id=:id AND wrong_word_id=:target_id")
    Confuse getConfuse(Long id, Long target_id);
    @Query("UPDATE Confuse SET times = times + 1 WHERE id =:id")
    void increaseTimes(Long id);
    @Query("SELECT * FROM Confuse")
    List<Confuse> getAllConfuse();
    @Query("DELETE FROM Confuse WHERE word_id=:id OR wrong_word_id=:id")
    void deleteWord(Long id);
}
