package com.erdemsiyam.memorizeyourwords.util.listener.word;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.erdemsiyam.memorizeyourwords.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Confuse;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.ConfuseService;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.adapter.WordRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
                    message+="\nOrtalama Seçim Süresi : "+word.getSpendTime()/((totalSelect==0)?1:totalSelect);
                    message+="\nBu Kelimeyle Karıştırdıkların : "+"\n";
                    for(ConfuseModel cm : getConfuses()){
                        message +="\t"+cm.times+ " Kez : "+WordService.getWordById(wordActivity,cm.strangeId).getStrange()+"\n";
                    }
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

    private ArrayList<ConfuseModel> getConfuses() {
        List<Confuse> confuses1 = ConfuseService.getConfusesWordsByFirstId(wordActivity,word.getId());
        List<Confuse> confuses2 = ConfuseService.getConfusesWordsBySecondId(wordActivity,word.getId());
        ArrayList<ConfuseModel> list = new ArrayList<>();
        for (Confuse c : confuses1) {
            list.add(new ConfuseModel(c.getWrongWordId(),c.getTimes()));
        }
        for(Confuse c :  confuses2){
            boolean isHave = false;
            for (ConfuseModel cm : list){
                if(c.getWordId().longValue()==cm.strangeId){
                    cm.times += c.getTimes();
                    isHave=true;
                    break;
                }
            }
            if(!isHave) list.add(new ConfuseModel(c.getWordId(),c.getTimes()));
        }
        Collections.sort(list);
        return new ArrayList<ConfuseModel>(list.subList(0,(list.size()>3)?3:list.size()));// ilk 3 item alınır
    }

    private static class ConfuseModel implements Comparable<ConfuseModel>{
        public long strangeId;
        public int times;
        public ConfuseModel(long strangeId,int times){this.strangeId=strangeId;this.times=times;}
        @Override
        public int compareTo(ConfuseModel o) {
            return (this.times>o.times)?-1:(this.times<o.times)?1:0;
        }
    }
}
