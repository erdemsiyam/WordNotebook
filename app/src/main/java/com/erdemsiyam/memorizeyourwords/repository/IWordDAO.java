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
    @Query("SELECT * FROM Word WHERE id=:id")
    Word getWordById(Long id);
    @Query("SELECT * FROM Word WHERE category_id=:id")
    List<Word> getWordsByCategoryId(Long id);
    @Query("SELECT * FROM Word WHERE category_id=:id AND learned=1")
    List<Word> getLearnedWordsByCategoryId(Long id);
    @Query("SELECT * FROM Word WHERE category_id=:id AND mark=1")
    List<Word> getMarkedWordsByCategoryId(Long id);
    @Query("SELECT * FROM Word WHERE category_id=:id AND learned=0")
    List<Word> getNotLearnedWordsByCategoryId(Long id);
    @Query("SELECT * FROM Word")
    List<Word> getAllWord();
    @Query("SELECT Count(*)>0 FROM Word WHERE category_id=:categoryId AND strange=:strange")
    boolean isThisWordExistsOnThisCategory(Long categoryId, String strange);
    @Query("UPDATE Word SET trueSelect = trueSelect + 1 WHERE id =:id")
    void trueSelectIncrease(Long id);
    @Query("UPDATE Word SET falseSelect = falseSelect + 1 WHERE id =:id")
    void falseSelectIncrease(Long id);
    @Query("UPDATE Word SET learned = :status WHERE id =:id")
    void changeLearned(Long id, boolean status);
    @Query("UPDATE Word SET mark = :status WHERE id =:id")
    void changeMark(Long id, boolean status);
    @Query("UPDATE Word SET spendTime = spendTime+:spendTime WHERE id =:id")
    void addSpendTime(Long id,long spendTime);
    @Query("UPDATE Word SET strange = :newStrange WHERE id =:id")
    void changeWordStrange(Long id, String newStrange);
    @Query("UPDATE Word SET 'explain' = :newExplain WHERE id =:id")
    void changeWordExplain(Long id, String newExplain);
    @Query("DELETE FROM Word WHERE category_id=:categoryId AND learned=1")
    void deleteAllLearnedByCategoryId(Long categoryId);
}
