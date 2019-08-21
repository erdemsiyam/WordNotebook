package com.erdemsiyam.memorizeyourwords.util.listener.word;

import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.erdemsiyam.memorizeyourwords.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.util.adapter.WordRecyclerViewAdapter;

public class WordOnClickListener implements View.OnClickListener {
    private WordActivity wordActivity;
    private Word word;
    public WordOnClickListener(WordActivity wordActivity, Word word) {this.wordActivity = wordActivity; this.word = word;}
    @Override
    public void onClick(View v) {
        WordRecyclerViewAdapter adapter = wordActivity.getAdapter();
        switch (adapter.freezeType){
            case NONE:
                    AlertDialog.Builder builder = new AlertDialog.Builder(wordActivity);
                    builder.setTitle("Bilgi");
                    String message = word.getStrange()+" : "+ word.getExplain();
                    long trueSelect = Long.valueOf(word.getTrueSelect());
                    long falseSelect = Long.valueOf(word.getFalseSelect());
                    long totalSelect = trueSelect+falseSelect;
                    message+="\nDoğru Seçim : "+trueSelect;
                    message+="\nYanlış Seçim : "+falseSelect;
                    message+="\nSeçim Süresi Avarajı: "+word.getSpendTime()/((totalSelect==0)?1:totalSelect);
                    builder.setMessage(message);

                    builder.setNeutralButton("Ok",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                break;
            case STRANGE:
                    adapter.unFreezeTheClickedItem(word);
                break;
            case EXPLAIN:
                    adapter.unFreezeTheClickedItem(word);
                break;
        }

    }
}
