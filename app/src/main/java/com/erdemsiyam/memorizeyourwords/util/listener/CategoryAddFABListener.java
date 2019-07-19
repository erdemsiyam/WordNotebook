package com.erdemsiyam.memorizeyourwords.util.listener;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.erdemsiyam.memorizeyourwords.util.fragment.CategoryAddModalBottomSheetDialog;

public class CategoryAddFABListener implements View.OnClickListener {
    private AppCompatActivity context;
    public CategoryAddFABListener(AppCompatActivity ctx){this.context = ctx;}
    @Override
    public void onClick(View v) {
        CategoryAddModalBottomSheetDialog bottomSheetDialog = new CategoryAddModalBottomSheetDialog(context);
        bottomSheetDialog.show(context.getSupportFragmentManager(),"categoryAddMBS");
    }
}
