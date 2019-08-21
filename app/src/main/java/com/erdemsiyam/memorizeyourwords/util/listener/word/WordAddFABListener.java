package com.erdemsiyam.memorizeyourwords.util.listener.word;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.erdemsiyam.memorizeyourwords.util.fragment.WordAddModalBottomSheetDialog;

public class WordAddFABListener implements View.OnClickListener {
    private AppCompatActivity context;
    public WordAddFABListener(AppCompatActivity ctx){this.context = ctx;}
    @Override
    public void onClick(View v) {
        WordAddModalBottomSheetDialog bottomSheetDialog = new WordAddModalBottomSheetDialog(context);
        bottomSheetDialog.show(context.getSupportFragmentManager(),"wordAddMBS");
    }
}
