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

public class WordAddModalBottomSheetDialog  extends BottomSheetDialogFragment {

    /*  If click the "FloatingActionButton" at "WordActivity" to add word,
        a pane appears from below, this pane is "BottomSheetDialogFragment",
        in this pane, new word is adding here. */

    /* Constants. */
    public static final String TAG = "mbsd_word_add";

    /* Property. */
    private WordActivity wordActivity;

    /* UI components. */
    private EditText txtNewWordStrange;
    private EditText txtNewWordExplain;
    private Button   btnCreateWord;

    /* Constructor. */
    public WordAddModalBottomSheetDialog(WordActivity wordActivity) {
        this.wordActivity = wordActivity;
    }

    /* Override method of BottomSheetDialogFragment. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* Including layout of ModelBottomSheet's. */
        View view = inflater.inflate(R.layout.modal_bottom_sheet_word_add,container,false);

        initComponents(view); // Loading UI items.
        loadData(); // Loading Listeners.
        loadFontSizes(); // Loading Font Sizes.

        return view; // Prepared layout return.
    }

    /* Initial Methods. */
    private void initComponents(View view) {
        txtNewWordStrange = view.findViewById(R.id.txtNewWordStrange);
        txtNewWordExplain = view.findViewById(R.id.txtNewWordExplain);
        btnCreateWord = view.findViewById(R.id.btnCreateWord);
    }
    private void loadData() {
        /* Listener giving to "CreateWordButton". */
        btnCreateWord.setOnClickListener(new CreateWordListener()); // The listener at below.
    }
    private void loadFontSizes() {
        txtNewWordStrange.setTextSize(SettingActivity.getFont(wordActivity));
        txtNewWordExplain.setTextSize(SettingActivity.getFont(wordActivity));
        btnCreateWord.setTextSize(SettingActivity.getFont(wordActivity));
    }

    /* Listener of the button create word. (inner class) */
    public class CreateWordListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                String strange = txtNewWordStrange.getText().toString().trim(); // Get new word's strange.
                String explain = txtNewWordExplain.getText().toString().trim(); // Get new word's explain.
                if(strange.equals("") || explain.equals("")) // If empty.
                    throw new MyException(wordActivity.getResources().getString(R.string.exception_no_content)); // Throw exception.
                if(strange.length() > 30|| explain.length() > 30) // If more than 30 characters.
                    throw new MyException(wordActivity.getResources().getString(R.string.exception_content_limit_exceeded)); // Throw exception.
                Word newWord = WordService.addWord(wordActivity, wordActivity.selectedCategory.getId(),strange,explain); // Create the new word at DB side.
                wordActivity.getAdapter().addWord(newWord); // Add the new word to UI ListView.
                dismiss(); // Close the "ModalBottomSheetDialog".
            }
            catch (MyException e){
                /* Send a message in case of error. */
                Toast.makeText(wordActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}