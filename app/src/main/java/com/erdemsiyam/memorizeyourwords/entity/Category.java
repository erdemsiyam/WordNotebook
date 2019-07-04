package com.erdemsiyam.memorizeyourwords.entity;

import androidx.room.*;

import java.util.List;

@Entity(tableName = "Category")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String color;
    @ColumnInfo()
    private long alarm;
    @Ignore
    private List<Word> words;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public long getAlarm() { return alarm; }

    public void setAlarm(long alarm) { this.alarm = alarm; }
}
