package com.erdemsiyam.memorizeyourwords.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;

import com.erdemsiyam.memorizeyourwords.activity.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.exception.MyException;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CategoryEditModalBottomSheetDialog extends BottomSheetDialogFragment {

    /*  If the selected category is moved to the left,
        a pane appears from below, this pane is "BottomSheetDialogFragment",
        in this pane, selected category is edited. */

    /* Constants. */
    public static final String TAG = "mbsd_category_edit";

    /* Veriables.*/
    private CategoryActivity    categoryActivity;
    private Category            category;

    /* UI components. */
    private AppCompatImageButton        btnEditCategory;
    private AppCompatImageButton        btnDeleteCategory;
    private EditText                    txtEditCategory;

    /* Constructor. */
    public CategoryEditModalBottomSheetDialog(CategoryActivity categoryActivity, Category category) {
        this.categoryActivity = categoryActivity;
        this.category = category;
    }

    /* Override method of BottomSheetDialogFragment. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* Including layout of ModelBottomSheet's. */
        View v = inflater.inflate(R.layout.modal_bottom_sheet_category_edit,container,false);

        /* Loading UI items. */
        txtEditCategory = v.findViewById(R.id.txtEditCategoryName);
        btnEditCategory = v.findViewById(R.id.btnEditCategory);
        btnDeleteCategory  = v.findViewById(R.id.btnDeleteCategory);

        /* Shows which category changed. */
        txtEditCategory.setText(category.getName());

        /* Listeners giving to EditDone and Delete Buttons. */
        btnEditCategory.setOnClickListener(new EditCategoryListener());     // The listener at below.
        btnDeleteCategory.setOnClickListener(new DeleteCategoryListener()); // The listener at below.

        return v; // Prepared layout return.
    }

    /* Listeners of buttons on UI. (inner class) */
    public class EditCategoryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try{
                String categoryNewName = txtEditCategory.getText().toString().trim(); // Fetch new name for category.
                if(categoryNewName.equals("")) // If empty.
                    throw new MyException(categoryActivity.getString(R.string.exception_no_content)); // Throw exception.
                if(categoryNewName.length() > 30) // If more than 30 characters.
                    throw new MyException(categoryActivity.getString(R.string.exception_content_limit_exceeded)); // Throw exception.
                category.setName(categoryNewName); // Set category's new name.
                CategoryService.updateCategory(categoryActivity,category); // Update at DB side.
                categoryActivity.getAdapter().updateCategory(category); // Refreshing at UI(RecyclerView).
                dismiss(); // Close the "ModalBottomSheetDialog".
            }
            catch (MyException e){
                /* Send a message in case of error. */
                Toast.makeText(categoryActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class DeleteCategoryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                /* "AlertDialog" is created to confirm the deletion. */
                AlertDialog.Builder builder = new AlertDialog.Builder(categoryActivity);
                builder.setTitle(R.string.category_delete_alert_title);
                builder.setMessage(categoryActivity.getResources().getString(R.string.category_delete_alert_message)+ " " + category.getName());
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CategoryService.deleteCategory(categoryActivity, category); // Category is deleted from DB.
                        categoryActivity.getAdapter().deleteCategory(category); // Category deleted from frontend.
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss(); // Close the "ModalBottomSheetDialog".
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            catch (Exception e){
                Toast.makeText(categoryActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            finally {
                dismiss(); // Close the "ModalBottomSheetDialog".
            }
        }
    }
}
