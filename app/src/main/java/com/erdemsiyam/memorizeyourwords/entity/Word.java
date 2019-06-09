package com.erdemsiyam.memorizeyourwords.entity;

import androidx.room.*;

@Entity(tableName = "Word",
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
public class Word {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo()
    private String strange;
    @ColumnInfo()
    private String explain;
    @ColumnInfo()
    private Integer trueSelect;
    @ColumnInfo()
    private Integer falseSelect;
    @ColumnInfo()
    private Integer trueSelectTime;
    @ColumnInfo()
    private Integer falseSelectTime;
    @ColumnInfo()
    private Boolean active;
    @ColumnInfo()
    private Integer density;
    @ColumnInfo(name = "category_id",index = true)
    private Long categoryId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStrange() {
        return strange;
    }

    public void setStrange(String strange) {
        this.strange = strange;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public Integer getTrueSelect() {
        return trueSelect;
    }

    public void setTrueSelect(Integer trueSelect) {
        this.trueSelect = trueSelect;
    }

    public Integer getFalseSelect() {
        return falseSelect;
    }

    public void setFalseSelect(Integer falseSelect) {
        this.falseSelect = falseSelect;
    }

    public Integer getTrueSelectTime() {
        return trueSelectTime;
    }

    public void setTrueSelectTime(Integer trueSelectTime) {
        this.trueSelectTime = trueSelectTime;
    }

    public Integer getFalseSelectTime() {
        return falseSelectTime;
    }

    public void setFalseSelectTime(Integer falseSelectTime) {
        this.falseSelectTime = falseSelectTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getDensity() {
        return density;
    }

    public void setDensity(Integer density) {
        this.density = density;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
