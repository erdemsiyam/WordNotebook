package com.erdemsiyam.memorizeyourwords.util.listener;

import com.erdemsiyam.memorizeyourwords.entity.Category;

import co.dift.ui.SwipeToAction;

public class CategorySwipeListener implements SwipeToAction.SwipeListener<Category> {
    @Override
    public boolean swipeLeft(Category itemData) {
        return false;
    }

    @Override
    public boolean swipeRight(Category itemData) {
        return false;
    }

    @Override
    public void onClick(Category itemData) {

    }

    @Override
    public void onLongClick(Category itemData) {

    }
}
