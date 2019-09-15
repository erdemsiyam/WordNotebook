package com.erdemsiyam.memorizeyourwords.util;

public class ExcelImportModel {

    private String pathExcel;
    private int strangeCellIndex;
    private int explainCellIndex;
    private int selectedSheetIndex;
    private long categoryId;

    public String getPathExcel() { return pathExcel; }
    public void setPathExcel(String pathExcel) { this.pathExcel = pathExcel; }
    public int  getStrangeCellIndex() { return strangeCellIndex; }
    public void setStrangeCellIndex(int strangeCellIndex) { this.strangeCellIndex = strangeCellIndex; }
    public int  getExplainCellIndex() { return explainCellIndex; }
    public void setExplainCellIndex(int explainCellIndex) { this.explainCellIndex = explainCellIndex; }
    public int  getSelectedSheetIndex() { return selectedSheetIndex; }
    public void setSelectedSheetIndex(int selectedSheetIndex) { this.selectedSheetIndex = selectedSheetIndex; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
}
