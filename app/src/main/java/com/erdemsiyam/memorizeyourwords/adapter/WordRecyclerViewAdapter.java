package com.erdemsiyam.memorizeyourwords.adapter;

import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import com.erdemsiyam.memorizeyourwords.activity.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.fragment.WordDetailDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class WordRecyclerViewAdapter extends RecyclerView.Adapter<WordRecyclerViewAdapter.WordViewHolder> implements Filterable, SearchView.OnQueryTextListener {

    /* Variables of Top. */
    private WordActivity     wordActivity;      // Instance of dependent Activity.
    private List<Word>       words;             // All words.
    private FreezeType       freezeType = FreezeType.NONE; // At start, appears the all "Strange,Explain".
    private HashSet<Integer> unFreezeIndexs = new HashSet<>(); // Words to show by clicked while freeze.

    /* Constructor (gets instances, creating some objects). */
    public WordRecyclerViewAdapter(WordActivity wordActivity, List<Word> words){
        this.wordActivity = wordActivity;
        this.words = words;
        this.filteredWords= new ArrayList<>(words);
    }

    /* The "ViewHolder" inner class. */
    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{ // impl for when long click to the item then show up context menu
        /* Holder UI components. */
        public ImageButton btnStar,btnCheck;
        public TextView txtStrange,txtExplain;
        public FrameLayout frameLayout;

        /* Constructor. */
        public WordViewHolder(View layout){
            super(layout);
            btnStar = layout.findViewById(R.id.btnWordStar);
            btnCheck = layout.findViewById(R.id.btnWordCheck);
            txtStrange =  layout.findViewById(R.id.txtWordStrange);
            txtExplain =  layout.findViewById(R.id.txtWordExplain);
            frameLayout = layout.findViewById(R.id.elementWord);

            /* At long click on words, we want to appear ContextMenu to "Edit,Delete" them.
               So we implement the "View.OnCreateContextMenuListener",
               to fill override method "onCreateContextMenu" at below in this class.
               And here, we put this object.*/
            frameLayout.setOnCreateContextMenuListener(this);
        }

        /* The override method "ContextMenu" of "View.OnCreateContextMenuListener". */
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            /* We add items to "ContextMenu". The need items are "Edit","Delete". */
            menu.add(this.getAdapterPosition(), WordActivity.CONTEXT_MENU_DELETE,0,R.string.word_context_menu_delete);
            menu.add(this.getAdapterPosition(), WordActivity.CONTEXT_MENU_EDIT,0,R.string.word_context_menu_edit);
        }
    }

    /* Override methods of RecyclerView. */
    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_word, parent, false);
        return new WordViewHolder(layout);
    }
    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        /* UI datas loading. */
        Word word = filteredWords.get(position); // Get the next word to UI data loading.
        holder.btnStar.setImageResource((word.isMark())?R.drawable.ic_star_on:R.drawable.ic_star_off); // Set "star_on" if this word marked.
        holder.btnCheck.setImageResource((word.isLearned())?R.drawable.ic_check_on:R.drawable.ic_check_off); // Set "check_on" if is this word learned.
        holder.txtStrange.setText((freezeType != FreezeType.STRANGE || unFreezeIndexs.contains(position))?word.getStrange():""); // The word's Strange : show if not freeze.
        holder.txtExplain.setText((freezeType != FreezeType.EXPLAIN || unFreezeIndexs.contains(position))?word.getExplain():""); // The word's Explain : show if not freeze.

        /* Giving the listeners for each word. */
        holder.btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordService.changeMark(wordActivity,word.getId(),!word.isMark());
                word.setMark(!word.isMark());
                refreshWord(word);
            }
        });
        holder.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordService.changeLearned(wordActivity,word.getId(),!word.isLearned());
                word.setLearned(!word.isLearned());
                refreshWord(word);
            }
        });
        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* When click to holder, change show according to current freeze type.*/
                switch (freezeType){
                    case NONE: /* Show word detail if not any freeze at word click. */
                            /* A Pop-up show up for word detail when click the word.
                               This Pop-up is "DialogFragment". */
                            WordDetailDialogFragment dialog = new WordDetailDialogFragment(wordActivity,word);
                            dialog.show(wordActivity.getSupportFragmentManager(),WordDetailDialogFragment.TAG);
                        break;
                    case STRANGE: /* Toggle the clicked word's strange visibility .*/
                            unFreezeTheClickedItem(word);
                        break;
                    case EXPLAIN: /* Toggle the clicked word's explain visibility .*/
                            unFreezeTheClickedItem(word);
                        break;
                }
            }
        });
    }
    @Override
    public int getItemCount() { return filteredWords.size(); }
    @Override
    public long getItemId(int position) {
        /* As you can see, not Words list, the FilteredWordsList is shown in ListView.
           Words showing according to filtering by searching . */
        return filteredWords.get(position).getId();
    } //changed


    /*################# FILTERING SECTION #################*/

    /* Variables of "Filtering Section". */
    private List<Word> filteredWords; // Search-filtered words. These words are shown on the ListView.
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Word> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){ // If there is no any search data then put all words to ListView.
                filteredList.addAll(words);
            }else{ // Search by word entered.
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Word c : words){
                    if(c.getStrange().toLowerCase().contains(filterPattern) || c.getExplain().toLowerCase().contains(filterPattern)){
                        filteredList.add(c);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList; // We got the result words, At below OverrideMethod we will put this to ListView.
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredWords.clear();
            filteredWords.addAll((List) results.values);
            notifyDataSetChanged();
        }
    }; // Custom Filter anonim class.

    /* Override method of Filterable. */
    @Override
    public Filter getFilter() { return filter; }

    /* Override methods of SearchView.OnQueryTextListener : Filtering "Words" with query words */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        getFilter().filter(newText); // Will be filtered according to the changing search word.
        return false;
    }


    /*################# OTHER #################*/

    public void addWord(Word newWord) {
        words.add(newWord);
        filteredWords.add(newWord);
        notifyDataSetChanged();
    }
    public void deleteWord(int index){
        Word word = filteredWords.get(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(wordActivity);
        builder.setTitle(R.string.word_delete_alert_title);
        builder.setMessage(word.getStrange()+" : "+ word.getExplain());
        builder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WordService.deleteWord(wordActivity,word); // word deleting from backend
                // word deleting from frontend
                words.remove(word);
                filteredWords.remove(word);
                notifyDataSetChanged(); // todo alttakiyle değiştir kontrol ederek
                //notifyItemInserted(categories.size() - 1);
            }
        });
        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
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
    public void refreshRecyclerView(List<Word> newWords){
        /* This method works after "ExcelWordsImport". */
        words = newWords;
        filteredWords = new ArrayList<>(words);
        notifyDataSetChanged();
    }


    /*################# FREEZE SECTION #################*/

    public enum FreezeType{ NONE, STRANGE, EXPLAIN }
    public void unFreezeTheClickedItem(Word word){
        int index = filteredWords.indexOf(word);
        if(unFreezeIndexs.contains(index))
            unFreezeIndexs.remove(index);
        else
            unFreezeIndexs.add(index);
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
        unFreezeIndexs = new HashSet<>();
    }


    /*################# SORTING #################*/

    public static class ComparatorMostCorrectlySelected implements Comparator<Word> {
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
    public static class ComparatorMostIncorrectlySelected implements Comparator<Word> {
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
