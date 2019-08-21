package com.erdemsiyam.memorizeyourwords.util.listener.category;

import androidx.appcompat.widget.SearchView;

import com.erdemsiyam.memorizeyourwords.util.adapter.CategoryRecyclerViewAdapter;

public class CategorySearchListener implements SearchView.OnQueryTextListener {
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        CategoryRecyclerViewAdapter.instance.getFilter().filter(newText); // the category list when filtering, this func will be using
        return false;
    }
}
