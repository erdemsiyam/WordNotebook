package com.erdemsiyam.memorizeyourwords.fragment;

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
import com.erdemsiyam.memorizeyourwords.activity.SettingActivity;
import com.erdemsiyam.memorizeyourwords.activity.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.exception.MyException;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class WordEditModalBottomSheetDialog extends BottomSheetDialogFragment {

    /*  When long click to word then click edit,
        a pane appears from below, this pane is "BottomSheetDialogFragment",
        in this pane, selected word is edited. */

    /* Constants. */
    public static final String TAG = "mbsd_word_edit";

    /* Property. */
    private WordActivity    wordActivity;
    private Word            word;   // Selected word. (To edit.)
    private int             index;  // Selected word index.

    /* UI components. */
    private Button   btnEditWord;
    private EditText txtEditWordStrange;
    private EditText txtEditWordExplain;

    /* Constructor. */
    public WordEditModalBottomSheetDialog(WordActivity wordActivity, Word word, int index){
        this.wordActivity = wordActivity;
        this.word = word;
        this.index = index;
    }

    /* Override method of BottomSheetDialogFragment. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* Including layout of ModelBottomSheet's. */
        View view = inflater.inflate(R.layout.modal_bottom_sheet_word_edit,container,false);

        initComponents(view); // Loading UI items.
        loadData(); // Loading Listeners.
        loadFontSizes(); // Loading Font Sizes.

        return view; // Prepared layout return.
    }

    /* Initial Methods. */
    private void initComponents(View view) {
        btnEditWord  = view.findViewById(R.id.btnEditWord);
        txtEditWordStrange = view.findViewById(R.id.txtEditWordStrange);
        txtEditWordExplain = view.findViewById(R.id.txtEditWordExplain);
    }
    private void loadData() {
        /* Shows word's old values. */
        txtEditWordStrange.setText(word.getStrange());
        txtEditWordExplain.setText(word.getExplain());

        /* Listener giving to "EditDoneButton". */
        btnEditWord.setOnClickListener(new EditWordListener()); // The listener at below.
    }
    private void loadFontSizes() {
        txtEditWordStrange.setTextSize(SettingActivity.getFont(wordActivity));
        txtEditWordExplain.setTextSize(SettingActivity.getFont(wordActivity));
        btnEditWord.setTextSize(SettingActivity.getFont(wordActivity));
    }

    /* Listener of "EditWordButton" on UI. (inner class) */
    public class EditWordListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try{
                String strange = txtEditWordStrange.getText().toString().trim(); // Get word new strange.
                String explain = txtEditWordExplain.getText().toString().trim(); // Get word new explain.
                if(strange.equals("") || explain.equals("") ) // If empty.
                    throw new MyException(wordActivity.getResources().getString(R.string.exception_no_content)); // Throw exception.
                if(strange.length() > 30 || explain.length() > 30) // If more than 30 characters.
                    throw new MyException(wordActivity.getResources().getString(R.string.exception_content_limit_exceeded)); // Throw exception.
                word.setStrange(strange);   // Set word's new strange.
                word.setExplain(explain);   // Set word's new explain.
                WordService.changeWordStrange(wordActivity,word.getId(),strange);// Update at DB side.
                WordService.changeWordExplain(wordActivity,word.getId(),explain);// Update at DB side.
                wordActivity.getAdapter().updateWord(index,word); // Refreshing at UI(RecyclerView).
                dismiss();  // Close the "ModalBottomSheetDialog".
            }
            catch (MyException e){
                /* Send a message in case of error. */
                Toast.makeText(wordActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}