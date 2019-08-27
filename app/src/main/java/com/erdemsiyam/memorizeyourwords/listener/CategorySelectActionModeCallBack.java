package com.erdemsiyam.memorizeyourwords.listener;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.app.AlertDialog;
import com.erdemsiyam.memorizeyourwords.activity.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.activity.ExamActivity;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.util.ExamWordType;
import com.erdemsiyam.memorizeyourwords.adapter.CategoryRecyclerViewAdapter;

import static com.erdemsiyam.memorizeyourwords.activity.CategoryActivity.INTENT_EXAM_SELECT_INDEX;
import static com.erdemsiyam.memorizeyourwords.activity.CategoryActivity.INTENT_SELECTED_CATEGORY_IDS;

public class CategorySelectActionModeCallBack implements ActionMode.Callback {

    /*  Question selection is made on POPUP screen,
        which questions of the selected categories will be taken to be the exam. */

    /* The instances in need. */
    public CategoryRecyclerViewAdapter  adapter;
    public CategoryActivity             categoryActivity;

    /* Indexing Variables */
    private int examSelectIndex=-1;

    /* Constructor (gets instances). */
    public CategorySelectActionModeCallBack(CategoryRecyclerViewAdapter adapter, CategoryActivity categoryActivity){
        this.adapter = adapter;
        this.categoryActivity = categoryActivity;
    }

    /* Override Methods. */
    @Override
    public boolean  onCreateActionMode(ActionMode mode, Menu menu) {
        /*  Menu changes in case of ActionMode,
            The only item at menu is "Exam : Take the exam for selected categories". */
        mode.getMenuInflater().inflate(R.menu.menu_category_selecting,menu); // Including the menu.
        mode.setTitle("Kategori Seç"); // Setting title.

        return true; // This should be "TRUE".
    }
    @Override
    public boolean  onPrepareActionMode(ActionMode mode, Menu menu) { return false; }
    @Override
    public boolean  onActionItemClicked(ActionMode mode, MenuItem item) {
        /* Clicking Item in the Menu. */
        switch (item.getItemId()){
            case R.id.categoriesSelectedForExam: // The only item is "Exam".
                createAlertDialogForSelectingWordTypesToExam(mode).show(); // Selecting with AlertDialog which words to get exam. The Function below there.
                return true;
        }
        return false;
    }
    @Override
    public void     onDestroyActionMode(ActionMode mode) {
        adapter.terminateActionMode();

    }

    /* Util Methods */
    public AlertDialog createAlertDialogForSelectingWordTypesToExam(ActionMode mode){
        /* AlertDialog is prepared which words we want to choose. */
        AlertDialog.Builder builder = new AlertDialog.Builder(categoryActivity);
        builder.setTitle("Sınav Seçiniz");
        String[] options = ExamWordType.getKeysAsStringArray(); // Enum options are taken as Array of String type.
        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                examSelectIndex = i; // The index is keeping at each click to options at AlertDialog.
            }
        });
        builder.setPositiveButton("Başla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(categoryActivity, ExamActivity.class); // if clicks positive then shows ExamAvtivity to Exam.
                intent.putExtra(INTENT_EXAM_SELECT_INDEX,examSelectIndex); // Sending index.
                intent.putExtra(INTENT_SELECTED_CATEGORY_IDS,categoryActivity.getAdapter().getSelectedCategoryIds()); // Sending selected categories.
                mode.finish(); // The ActionMode is terminated.
                categoryActivity.startActivity(intent);
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mode.finish(); // The ActionMode is terminated.
            }
        });
        return builder.create(); // AlertDialog is ready.
    }
}
