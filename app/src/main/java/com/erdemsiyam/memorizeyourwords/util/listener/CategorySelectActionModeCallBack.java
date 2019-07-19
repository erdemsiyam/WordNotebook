package com.erdemsiyam.memorizeyourwords.util.listener;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import com.erdemsiyam.memorizeyourwords.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.util.adapter.CategoryRecyclerViewAdapter;

public class CategorySelectActionModeCallBack implements ActionMode.Callback {
    public CategoryRecyclerViewAdapter adapter;
    public AppCompatActivity context;
    public CategorySelectActionModeCallBack(CategoryRecyclerViewAdapter adapter, AppCompatActivity context){
        this.adapter = adapter;
        this.context = context;
    }
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.category_selecting,menu);
        mode.setTitle("Kategori Seç");
        return true; // Do Not Forget This.
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()){
            case R.id.categoriesSelectedForExam:
                Log.i("erdem", "sinav gir tiklandi");
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.selectingCategoriesActionMode = null;
        adapter.clearSelections(); // selected categories are freedom.

    }
}
