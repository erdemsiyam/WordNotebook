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
    private int visibilityWordGroupType;
    @Ignore
    private List<Word> words;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getVisibilityWordGroupType() { return visibilityWordGroupType; }
    public void setVisibilityWordGroupType(int visibilityWordGroupType) { this.visibilityWordGroupType = visibilityWordGroupType; }
}
