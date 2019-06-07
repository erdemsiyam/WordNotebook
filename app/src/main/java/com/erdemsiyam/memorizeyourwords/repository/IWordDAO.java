package com.erdemsiyam.memorizeyourwords.repository;

import androidx.room.*;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import java.util.List;

@Dao
public interface IWordDAO {
    @Insert
    Long insertWord(Word word);
    @Update
    void updateWord(Word word);
    @Delete
    void deleteWord(Word word);
    @Query("SELECT * FROM Word WHERE list_id=:id")
    List<Word> getWordsByListId(Long id);
    @Query("SELECT * FROM Word")
    List<Word> getAllWord();

}
