package com.erdemsiyam.memorizeyourwords.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "NotificationCategory",
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
public class NotificationCategory {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "category_id",index = true)
    private Long categoryId;
    @ColumnInfo(name = "word_type")
    private int wordType;
    @ColumnInfo(name = "notification_id")
    private int notificationId;
    @ColumnInfo(name = "hour")
    private int hour;
    @ColumnInfo(name = "minute")
    private int minute;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public int getWordType() { return wordType; }
    public void setWordType(int wordType) { this.wordType = wordType; }
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }
    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }
}
