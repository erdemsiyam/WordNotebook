package com.erdemsiyam.memorizeyourwords.util.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BlurMaskFilter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.erdemsiyam.memorizeyourwords.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.listener.word.WordCheckButtonOnClickListener;
import com.erdemsiyam.memorizeyourwords.util.listener.word.WordOnClickListener;
import com.erdemsiyam.memorizeyourwords.util.listener.word.WordStarButtonOnClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class WordRecyclerViewAdapter extends RecyclerView.Adapter<WordRecyclerViewAdapter.WordViewHolder> implements Filterable {

    private AppCompatActivity context;
    private List<Word> words;
    public FreezeType freezeType=FreezeType.NONE;
    private HashSet<Integer> unFreezIndexs = new HashSet<>();


    public WordRecyclerViewAdapter(Context context, List<Word> words){
        this.context = (AppCompatActivity) context;
        this.words = words;
        this.filteredWords= new ArrayList<>(words);
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{ // impl for when long click to the item then show up context menu
        public ImageButton btnStar,btnCheck;
        public TextView txtStrange,txtExplain;
        public FrameLayout frameLayout;
        public WordViewHolder(View layout){
            super(layout);
            btnStar = layout.findViewById(R.id.btnWordStar);
            btnCheck = layout.findViewById(R.id.btnWordCheck);
            txtStrange =  layout.findViewById(R.id.txtWordStrange);
            txtExplain =  layout.findViewById(R.id.txtWordExplain);
            frameLayout = layout.findViewById(R.id.elementWord);
            frameLayout.setOnCreateContextMenuListener(this); // whe have implement to this class at bottom you can see override func.
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), WordActivity.CONTEXT_MENU_DELETE,0,"Delete");
            menu.add(this.getAdapterPosition(), WordActivity.CONTEXT_MENU_EDIT,0,"Edit");
        }
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_word, parent, false);
        return new WordViewHolder(layout);
    }
    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = filteredWords.get(position);
        holder.btnStar.setImageResource((word.isMark())?R.drawable.ic_star_on:R.drawable.ic_star_off);
        holder.btnCheck.setImageResource((word.isLearned())?R.drawable.ic_check_on:R.drawable.ic_check_off);
        holder.txtStrange.setText((freezeType != FreezeType.STRANGE || unFreezIndexs.contains(position))?word.getStrange():"");
        holder.txtExplain.setText((freezeType != FreezeType.EXPLAIN || unFreezIndexs.contains(position))?word.getExplain():"");
        holder.btnStar.setOnClickListener(new WordStarButtonOnClickListener((WordActivity)context,word));
        holder.btnCheck.setOnClickListener(new WordCheckButtonOnClickListener((WordActivity)context,word));
        holder.frameLayout.setOnClickListener(new WordOnClickListener((WordActivity)context,word));
    }
    @Override
    public int getItemCount() { return filteredWords.size(); }
    @Override
    public long getItemId(int position) { return filteredWords.get(position).getId(); } //changed

    /*######### FILTER SECTION ##########*/
    private List<Word> filteredWords;
    @Override
    public Filter getFilter() { return filter; }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Word> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(words);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Word c : words){
                    if(c.getStrange().toLowerCase().contains(filterPattern) || c.getExplain().toLowerCase().contains(filterPattern)){
                        filteredList.add(c);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredWords.clear();
            filteredWords.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    /*######### OTHER ############*/

    public void addWord(Word newWord) {
        words.add(newWord);
        filteredWords.add(newWord);
        notifyDataSetChanged();
    }
    public void deleteWord(int index){
        Word word = filteredWords.get(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Silmek İstediğinize Emin Misiniz?");
        builder.setMessage(word.getStrange()+" : "+ word.getExplain());
        builder.setPositiveButton("Evet",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WordService.deleteWord(context,word); // word deleting from backend
                // word deleting from frontend
                words.remove(word);
                filteredWords.remove(word);
                notifyDataSetChanged();
                //notifyItemInserted(categories.size() - 1);
            }
        });
        builder.setNegativeButton("Hayır",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void updateWord(int index,Word newWord) {
        Word oldWord = filteredWords.get(index);
        oldWord.setStrange(newWord.getStrange());
        oldWord.setExplain(newWord.getExplain());
        notifyItemChanged(index);
    }
    public Word getWordByIndex(int index){
        return filteredWords.get(index);
    }
    public void refreshWord(Word word){
        int index = filteredWords.indexOf(word);
        notifyItemChanged(index);
    }

    /*######### Freeze Events ###########*/
    public enum FreezeType{ NONE, STRANGE, EXPLAIN }
    public void unFreezeTheClickedItem(Word word){
        int index = filteredWords.indexOf(word);
        if(unFreezIndexs.contains(index))
            unFreezIndexs.remove(index);
        else
            unFreezIndexs.add(index);
        notifyItemChanged(index);
    }
    public void toggleFreeze(){
        switch (freezeType){
            case NONE:
                    freezeType = FreezeType.STRANGE;
                break;
            case STRANGE:
                    freezeType = FreezeType.EXPLAIN;
                break;
            case EXPLAIN:
                    freezeType = FreezeType.NONE;
                break;
        }
        notifyDataSetChanged();
        unFreezIndexs = new HashSet<>();
    }

    /*######### SORTING #################*/
    public static class ComparatorMuchSelectedTrue implements Comparator<Word> {
        @Override
        public int compare(Word w1, Word w2) {
            if(w1.getTrueSelect() < w2.getTrueSelect())
                return 1;
            else if (w1.getTrueSelect() > w2.getTrueSelect())
                return -1;
            else {
                if(w1.getFalseSelect() > w2.getFalseSelect())
                    return 1;
                else if(w1.getFalseSelect() > w2.getFalseSelect())
                    return -1;
                else
                    return 0;
            }
        }
    }
    public static class ComparatorMuchSelectedFalse implements Comparator<Word> {
        @Override
        public int compare(Word w1, Word w2) {
            if(w1.getFalseSelect() < w2.getFalseSelect())
                return 1;
            else if (w1.getFalseSelect() > w2.getFalseSelect())
                return -1;
            else {
                if(w1.getTrueSelect() > w2.getTrueSelect())
                    return 1;
                else if(w1.getTrueSelect() > w2.getTrueSelect())
                    return -1;
                else
                    return 0;
            }
        }
    }
    public static class ComparatorStrangeAZ implements Comparator<Word> {
        @Override
        public int compare(Word w1, Word w2) {
            return w1.getStrange().compareTo(w2.getStrange());
        }
    }
    public static class ComparatorStrangeZA implements Comparator<Word> {
        @Override
        public int compare(Word w1, Word w2) {
            return w1.getStrange().compareTo(w2.getStrange())*-1;
        }
    }
    public static class ComparatorExplainAZ implements Comparator<Word> {
        @Override
        public int compare(Word w1, Word w2) {
            return w1.getExplain().compareTo(w2.getExplain());
        }
    }
    public static class ComparatorExplainZA implements Comparator<Word> {
        @Override
        public int compare(Word w1, Word w2) {
            return w1.getExplain().compareTo(w2.getExplain())*-1;
        }
    }
    public void sort(Comparator comparator){
        Collections.sort(words, comparator);
        Collections.sort(filteredWords, comparator);
        notifyDataSetChanged();
    }
}
