package com.erdemsiyam.memorizeyourwords.util.listener;

import android.util.Log;

import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.util.adapter.CategoryRecyclerViewAdapter;

import co.dift.ui.SwipeToAction;

public class CategorySwipeListener implements SwipeToAction.SwipeListener<Category> {
    private CategoryRecyclerViewAdapter adapter;
    public CategorySwipeListener(CategoryRecyclerViewAdapter adapter){
        this.adapter = adapter;
    }
    @Override
    public boolean swipeLeft(Category itemData) {
        return true;
    }

    @Override
    public boolean swipeRight(Category itemData) {
        return true;
    }

    @Override
    public void onClick(Category category) {
        if(adapter.getSelectedCategoryCount() > 0){ // if there is exist selected item, We got it Select Action Active.
            Log.i("erdem","kategori uzun tiklandi");
            selectingCategory(category);
        }
        else{
            // açma işlemi
            Log.i("erdem","kategori tiklandi aç");
        }
    }

    @Override
    public void onLongClick(Category category) {
        Log.i("erdem","kategori uzun tiklandi");
        selectingCategory(category);
    }

    private void selectingCategory(Category category){
        adapter.displaySelectingActionModeIfNotExist(); // if action mod null : then fill it and start.
        adapter.toggleSelection(category); // switch select or non-select.

        int selectedCount = adapter.getSelectedCategoryCount();
        if( selectedCount == 0){ // if all selects are removed, then Action Mode Will Closed.
            adapter.selectingCategoriesActionMode.finish();
        }else{ // if there is selecting items exists, write these count to the title.
            adapter.selectingCategoriesActionMode.setTitle(selectedCount + " Seçildi");
            adapter.selectingCategoriesActionMode.invalidate();
        }
    }
}
