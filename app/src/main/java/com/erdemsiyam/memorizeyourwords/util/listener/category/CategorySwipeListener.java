package com.erdemsiyam.memorizeyourwords.util.listener.category;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.erdemsiyam.memorizeyourwords.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.util.adapter.CategoryRecyclerViewAdapter;
import com.erdemsiyam.memorizeyourwords.util.fragment.CategoryEditModalBottomSheetDialog;

import co.dift.ui.SwipeToAction;

public class CategorySwipeListener implements SwipeToAction.SwipeListener<Category> {
    private CategoryRecyclerViewAdapter adapter;
    private AppCompatActivity context;
    public CategorySwipeListener(AppCompatActivity context,CategoryRecyclerViewAdapter adapter){
        this.context = context;
        this.adapter = adapter;
    }
    @Override
    public boolean swipeLeft(Category itemData) {

        CategoryEditModalBottomSheetDialog bottomSheetDialog = new CategoryEditModalBottomSheetDialog(context,itemData);
        bottomSheetDialog.show(context.getSupportFragmentManager(),"categoryAddMBS");

        return true;
    }

    @Override
    public boolean swipeRight(Category itemData) {
        Intent intent = new Intent(context, WordActivity.class);
        intent.putExtra(CategoryActivity.INTENT_CATEGORY_ID,itemData.getId());
        context.startActivity(intent);
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
