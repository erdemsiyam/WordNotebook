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
    private int writeTrue;
    @ColumnInfo()
    private int writeFalse;
    @ColumnInfo()
    private int trueSelect;
    @ColumnInfo()
    private int falseSelect;
    @ColumnInfo()
    private long spendTime;
    @ColumnInfo()
    private boolean mark;
    @ColumnInfo()
    private boolean learned;
    @ColumnInfo(name = "category_id",index = true)
    private Long categoryId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStrange() { return strange; }
    public void setStrange(String strange) { this.strange = strange; }
    public String getExplain() { return explain; }
    public void setExplain(String explain) { this.explain = explain; }
    public int getTrueSelect() { return trueSelect; }
    public void setTrueSelect(int trueSelect) { this.trueSelect = trueSelect; }
    public int getFalseSelect() { return falseSelect; }
    public void setFalseSelect(int falseSelect) { this.falseSelect = falseSelect; }
    public long getSpendTime() { return spendTime; }
    public void setSpendTime(long spendTime) { this.spendTime = spendTime; }
    public boolean isLearned() { return learned; }
    public void setLearned(boolean learned) {this.learned = learned;}
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public int getWriteTrue() { return writeTrue; }
    public void setWriteTrue(int writeTrue) { this.writeTrue = writeTrue; }
    public int getWriteFalse() { return writeFalse; }
    public void setWriteFalse(int writeFalse) { this.writeFalse = writeFalse; }
    public boolean isMark() { return mark; }
    public void setMark(boolean mark) { this.mark = mark; }
}
