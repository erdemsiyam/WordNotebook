package com.erdemsiyam.memorizeyourwords.entity;

import androidx.room.*;

@Entity(tableName = "List")
public class List {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public java.util.List<Word> getWords() {
        return words;
    }

    public void setWords(java.util.List<Word> words) {
        this.words = words;
    }
}
