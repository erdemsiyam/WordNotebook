package com.erdemsiyam.memorizeyourwords.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatTextView;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Confuse;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.ConfuseService;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.ConfuseTempModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordDetailDialogFragment extends AppCompatDialogFragment {

    /* A Pop-up show up for word detail when click the word at "WordActivity".
       This Pop-up is "DialogFragment". */

    /* Constants. */
    public static final String TAG = "df_word_detail";

    /* Veriables.*/
    private WordActivity wordActivity;
    private Word word;

    /* UI components. */
    private AppCompatTextView txtWordDetailTrueCount;
    private AppCompatTextView txtWordDetailFalseCount;
    private AppCompatTextView txtWordDetailConfuses;

    /* Constructor. */
    public WordDetailDialogFragment(WordActivity wordActivity, Word word){
        this.wordActivity = wordActivity;
        this.word = word;
    }

    /* Override method of AppCompatDialogFragment.
     * Creating Our CustomizeDialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /* "AlertDialog" Design will be used to our "CustomizeDialog". */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        /* Including layout of DialogFragment's. */
        View view = inflater.inflate(R.layout.dialog_detail_word,null);

        /* UI components are installed. */
        txtWordDetailTrueCount = view.findViewById(R.id.txtWordDetailTrueCount);
        txtWordDetailFalseCount = view.findViewById(R.id.txtWordDetailFalseCount);
        txtWordDetailConfuses = view.findViewById(R.id.txtWordDetailConfuses);

        /* "AlertDialog" building. */
        builder.setView(view);
        builder.setTitle("Başlık");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        /* Giving values to UI components. */
        txtWordDetailTrueCount.setText(word.getTrueSelect()+"");
        txtWordDetailFalseCount.setText(word.getFalseSelect()+"");
        String message = "Karıştırdıkların : \n";
        for(ConfuseTempModel cm : getConfuses()){
            message +="\t"+cm.times+ " Kez : "+ WordService.getWordById(wordActivity,cm.strangeId).getStrange()+"\n";
        }
        txtWordDetailConfuses.setText(message);

        return builder.create();  // Prepared Customize "AlertDialog" return.
    }

    /* Util method.*/
    private ArrayList<ConfuseTempModel> getConfuses() {
        List<Confuse> confuses1 = ConfuseService.getConfusesWordsByFirstId(wordActivity,word.getId());
        List<Confuse> confuses2 = ConfuseService.getConfusesWordsBySecondId(wordActivity,word.getId());
        ArrayList<ConfuseTempModel> list = new ArrayList<>();
        for (Confuse c : confuses1) {
            list.add(new ConfuseTempModel(c.getWrongWordId(),c.getTimes()));
        }
        for(Confuse c :  confuses2){
            boolean isHave = false;
            for (ConfuseTempModel cm : list){
                if(c.getWordId().longValue()==cm.strangeId){
                    cm.times += c.getTimes();
                    isHave=true;
                    break;
                }
            }
            if(!isHave) list.add(new ConfuseTempModel(c.getWordId(),c.getTimes()));
        }
        Collections.sort(list);
        return new ArrayList<>(list.subList(0,(list.size()>3)?3:list.size()));// ilk 3 item alınır
    }
}
