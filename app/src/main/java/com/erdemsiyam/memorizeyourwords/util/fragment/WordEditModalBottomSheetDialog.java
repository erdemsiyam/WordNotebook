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

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.exception.MyException;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class WordEditModalBottomSheetDialog extends BottomSheetDialogFragment {
    private WordActivity wordActivity;
    private Word word;
    private int index;
    public WordEditModalBottomSheetDialog(WordActivity wordActivity, Word word, int index){this.wordActivity = wordActivity; this.word = word;this.index = index;}
    private Button btnEditWord;
    private EditText txtEditWordStrange,txtEditWordExplain;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mbs_edit_word,container,false);
        btnEditWord  = v.findViewById(R.id.btnEditWord);
        txtEditWordStrange = v.findViewById(R.id.txtEditWordStrange);
        txtEditWordExplain = v.findViewById(R.id.txtEditWordExplain);
        txtEditWordStrange.setText(word.getStrange());
        txtEditWordExplain.setText(word.getExplain());
        btnEditWord.setOnClickListener(new EditWordListener());

        return v; // our mbs returned.
    }
    public class EditWordListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try{
                String strange = txtEditWordStrange.getText().toString().trim();
                String explain = txtEditWordExplain.getText().toString().trim();
                if(strange.equals("") || explain.equals("") )
                    throw new MyException(MyException.NO_CONTENT);
                word.setStrange(strange);
                word.setExplain(explain);
                WordService.changeWordStrange(wordActivity,word.getId(),strange);// update at backend
                WordService.changeWordExplain(wordActivity,word.getId(),explain);// update at backend
                wordActivity.getAdapter().updateWord(index,word); // word get refresh at RecyclerView
            }
            catch (MyException e){
                Toast.makeText(wordActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            finally {
                dismiss();
            }
        }
    }
}