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
}
