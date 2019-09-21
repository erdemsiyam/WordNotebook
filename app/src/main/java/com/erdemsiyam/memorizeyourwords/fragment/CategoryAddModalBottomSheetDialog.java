package com.erdemsiyam.memorizeyourwords.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import com.erdemsiyam.memorizeyourwords.activity.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.exception.MyException;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CategoryAddModalBottomSheetDialog extends BottomSheetDialogFragment {

    /*  If click the "FloatingActionButton" at "CategoryActivity" to add category,
        a pane appears from below, this pane is "BottomSheetDialogFragment",
        in this pane, new category is adding here. */

    /* Constants. */
    public static final String TAG = "mbsd_category_add";

    /* Veriables.*/
    private CategoryActivity categoryActivity;

    /* UI components. */
    private AppCompatImageButton    btnCreateCateogry;
    private EditText                txtNewCategoryName;

    /* Constructor. */
    public CategoryAddModalBottomSheetDialog(CategoryActivity categoryActivity) {
        this.categoryActivity = categoryActivity;
    }

    /* Override method of BottomSheetDialogFragment. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* Including layout of ModelBottomSheet's. */
        View v = inflater.inflate(R.layout.modal_bottom_sheet_category_add,container,false);

        /* Loading UI items. */
        btnCreateCateogry = v.findViewById(R.id.btnCreateCategory);
        txtNewCategoryName = v.findViewById(R.id.newCategoryName);

        /* Listener giving to "CreateCategoryButton". */
        btnCreateCateogry.setOnClickListener(new CreateCategoryListener()); // The listener at below.

        return v; // Prepared layout return.
    }

    /* Listener of the button create category. (inner class) */
    public class CreateCategoryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                String categoryName = txtNewCategoryName.getText().toString().trim(); // Fetch new category's name.
                if(categoryName.equals("")) // If empty.
                    throw new MyException(categoryActivity.getResources().getString(R.string.exception_no_content)); // Throw exception.
                if(categoryName.length() > 30) // If more than 30 characters.
                    throw new MyException(categoryActivity.getResources().getString(R.string.exception_content_limit_exceeded)); // Throw exception.
                Category newCategory = CategoryService.addCategory(categoryActivity,categoryName); // Create the new category at DB side.
                categoryActivity.getAdapter().addCategory(newCategory); // Add the new category to UI ListView.
                dismiss(); // Close the "ModalBottomSheetDialog".
            }
            catch (MyException e){
                /* Send a message in case of error. */
                Toast.makeText(categoryActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
