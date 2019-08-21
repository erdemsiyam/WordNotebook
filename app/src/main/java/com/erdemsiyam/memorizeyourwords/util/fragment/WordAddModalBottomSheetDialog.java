package com.erdemsiyam.memorizeyourwords.util.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.exception.MyException;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class WordAddModalBottomSheetDialog  extends BottomSheetDialogFragment {
    private WordActivity context;
    public WordAddModalBottomSheetDialog(AppCompatActivity context){this.context = (WordActivity)context;}
    EditText txtNewWordStrange,txtNewWordExplain;
    Button btnCreateWord;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mbs_add_word,container,false);
        txtNewWordStrange = v.findViewById(R.id.txtNewWordStrange);
        txtNewWordExplain = v.findViewById(R.id.txtNewWordExplain);
        btnCreateWord = v.findViewById(R.id.btnCreateWord);
        btnCreateWord.setOnClickListener(new CreateWordListener());
        return v;
    }
    public class CreateWordListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                String strange = txtNewWordStrange.getText().toString().trim();
                String explain = txtNewWordExplain.getText().toString().trim();
                if(strange.equals("") || explain.equals("")  )
                    throw new MyException(MyException.NO_CONTENT);
                Word newWord = WordService.addWord(context,context.selectedCategoryId,strange,explain);
                context.getAdapter().addWord(newWord); // add to view
            }
            catch (MyException e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            finally {
                dismiss(); // done after -> Closing modal bottom sheet.
            }
        }
    }
}