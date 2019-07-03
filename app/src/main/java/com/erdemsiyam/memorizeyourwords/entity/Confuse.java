package com.erdemsiyam.memorizeyourwords.entity;

import androidx.room.*;

@Entity(tableName = "Confuse",
        foreignKeys = {
                @ForeignKey(
                        entity = Word.class,
                        parentColumns = "id",
                        childColumns = "word_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Word.class,
                        parentColumns = "id",
                        childColumns = "wrong_word_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class Confuse {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "word_id",index = true)
    private Long wordId;
    @ColumnInfo(name = "wrong_word_id",index = true)
    private Long wrongWordId;
    @ColumnInfo(name = "times")
    private int times;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }
    public Long getWrongWordId() { return wrongWordId; }
    public void setWrongWordId(Long wrongWordId) { this.wrongWordId = wrongWordId; }
    public int getTimes() { return times; }
    public void setTimes(int times) { this.times = times; }
}
