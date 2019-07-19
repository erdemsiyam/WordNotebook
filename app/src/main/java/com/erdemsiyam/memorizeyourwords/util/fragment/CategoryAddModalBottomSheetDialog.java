package com.erdemsiyam.memorizeyourwords.util.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.erdemsiyam.memorizeyourwords.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CategoryAddModalBottomSheetDialog extends BottomSheetDialogFragment {
    private CategoryActivity context;
    public CategoryAddModalBottomSheetDialog(AppCompatActivity context){this.context = (CategoryActivity)context;}
    Button btnCreateCateogry;
    EditText txtNewCategoryName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mbs_add_category,container,false);
        btnCreateCateogry = v.findViewById(R.id.btnCreateCategory); // the button in modal bottom sheet dialog.
        txtNewCategoryName = v.findViewById(R.id.newCategoryName);

        btnCreateCateogry.setOnClickListener(new CreateCategoryListener()); // when click this button , start to action in this listener. which at below there.
        return v; // our mbs returned.
    }
    public class CreateCategoryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Category newCategory = new Category();
            newCategory.setId(10L);
            newCategory.setName(txtNewCategoryName.getText().toString());
            newCategory.setAlarm(0l);
            newCategory.setColor("");
            context.getAdapter().addCategory(newCategory);
            dismiss(); // done after -> Closing modal bottom sheet.
        }
    }
}
