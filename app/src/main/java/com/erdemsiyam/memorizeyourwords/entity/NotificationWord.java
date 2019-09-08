package com.erdemsiyam.memorizeyourwords.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "NotificationWord",
        foreignKeys = {
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class NotificationWord {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "category_id",index = true)
    private Long categoryId;
    @ColumnInfo(name = "times")
    private int wordType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public int getWordType() { return wordType; }
    public void setWordType(int wordType) { this.wordType = wordType; }
}
