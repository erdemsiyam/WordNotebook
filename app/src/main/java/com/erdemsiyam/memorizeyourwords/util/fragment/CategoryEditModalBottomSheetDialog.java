package com.erdemsiyam.memorizeyourwords.util.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.erdemsiyam.memorizeyourwords.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.exception.MyException;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CategoryEditModalBottomSheetDialog extends BottomSheetDialogFragment {
    private CategoryActivity context;
    private Category category;
    public CategoryEditModalBottomSheetDialog(AppCompatActivity context, Category category){this.context = (CategoryActivity)context; this.category = category;}
    private Button btnEditCategory, btnDeleteCategory;
    private EditText txtEditCategory;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mbs_edit_category,container,false);
        txtEditCategory = v.findViewById(R.id.txtEditCategoryName);
        btnEditCategory = v.findViewById(R.id.btnEditCategory);
        btnDeleteCategory  = v.findViewById(R.id.btnDeleteCategory);

        txtEditCategory.setText(category.getName());
        btnEditCategory.setOnClickListener(new EditCategoryListener());
        btnDeleteCategory.setOnClickListener(new DeleteCategoryListener());

        return v; // our mbs returned.
    }
    public class EditCategoryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try{
                String categoryNewName = txtEditCategory.getText().toString().trim();
                if(categoryNewName.equals(""))
                    throw new MyException(MyException.NO_CONTENT);
                category.setName(categoryNewName);
                CategoryService.updateCategory(context,category); // update at backend
                context.getAdapter().updateCategory(category); // category get refresh at RecyclerView
            }
            catch (MyException e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            finally {
                dismiss();
            }
        }
    }

    public class DeleteCategoryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Silmek İstediğinize Emin Misiniz?");
            builder.setMessage("Kategori : "+category.getName());
            builder.setPositiveButton("Evet",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CategoryService.deleteCategory(context,category); // category deleting from backend
                    context.getAdapter().deleteCategory(category); // category deleting from frontend
                }
            });
            builder.setNegativeButton("Hayır",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
