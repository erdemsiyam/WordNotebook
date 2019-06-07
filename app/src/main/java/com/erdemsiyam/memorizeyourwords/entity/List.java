package com.erdemsiyam.memorizeyourwords.entity;

import android.graphics.drawable.ColorDrawable;

import androidx.room.*;

@Entity(tableName = "List")
public class List {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private ColorDrawable color;
    @Ignore
    private java.util.List<Word> words;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColorDrawable getColor() {
        return color;
    }

    public void setColor(ColorDrawable color) {
        this.color = color;
    }

    public java.util.List<Word> getWords() {
        return words;
    }

    public void setWords(java.util.List<Word> words) {
        this.words = words;
    }
}
