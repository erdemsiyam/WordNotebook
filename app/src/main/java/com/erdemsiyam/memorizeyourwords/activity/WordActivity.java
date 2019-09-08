package com.erdemsiyam.memorizeyourwords.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.fragment.ExcelImportDialogFragment;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.WordSortType;
import com.erdemsiyam.memorizeyourwords.adapter.WordRecyclerViewAdapter;
import com.erdemsiyam.memorizeyourwords.fragment.WordAddModalBottomSheetDialog;
import com.erdemsiyam.memorizeyourwords.fragment.WordEditModalBottomSheetDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Comparator;
import java.util.List;

public class WordActivity extends AppCompatActivity {

    /* Constants */
    public static final int CONTEXT_MENU_DELETE = 1;
    public static final int CONTEXT_MENU_EDIT = 2;

    /* Indexing Variables */
    public static int   wordSortSelectedIndex=-1;
    public long         selectedCategoryId;

    /* UI Components */
    private RecyclerView            recyclerViewWord; // Word list.
    private Button    btnAddWord; // Add category button.
    private WordRecyclerViewAdapter adapter; // Word list custom adapter.
    private Button    btnToggleFreeze; // A button to change the appearance of words.
    private AppCompatImageButton    btnBackToCategoryFromWord; // Back button to "CategoryActivity".
    private AppCompatTextView       txtCategoryName; // Shows words belongs to which category.
    private AdView                  adViewBannerWord; // Ad banner.

    /* Override Methods. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        initComponents(); // UI components are installed.
        loadData(); // Data is loaded into UI components.
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Menu including. */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_word,menu);

        /* "WordSearch" Menu item creating to searching words. */
        MenuItem searchItem = menu.findItem(R.id.wordSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH); // The key "Enter Icon" on the keyboard will be "Search Icon" when searching.
        searchView.setOnQueryTextListener(adapter); // Required "SearchView.OnQueryTextListener" implements and override functions are filled to searching works.

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        /*  To delete or edit the word we need to long click to word item at RecyclerView,
            "ContextMenu" defined for word in "WordRecyclerAdapter", action defined HERE for long click menu.
            Actions below there. */
        switch (item.getItemId()){
            case CONTEXT_MENU_DELETE: // If the delete key is clicked, the word is deleted which at this index.
                getAdapter().deleteWord(item.getGroupId());
                break;
            case CONTEXT_MENU_EDIT: // If the edit key is clicked, the word is editing which at this index.
                int index = item.getGroupId();
                Word word = getAdapter().getWordByIndex(index); // Getting the clicked word.

                /* A pane appears from below to "EditTheWord". This pane is "BottomSheetDialogFragment". */
                WordEditModalBottomSheetDialog bottomSheetDialog = new WordEditModalBottomSheetDialog(this,word,index);
                bottomSheetDialog.show(this.getSupportFragmentManager(),WordEditModalBottomSheetDialog.TAG);
                break;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Menu item actions defined here. */
        switch (item.getItemId()){
            case R.id.wordImportExcel:
                /* Adding words with Excel Import */
                ExcelImportDialogFragment dialog = new ExcelImportDialogFragment(this,selectedCategoryId);
                dialog.show(getSupportFragmentManager(),ExcelImportDialogFragment.TAG);
                break;
            case R.id.wordSort:
                /* If user wants sorting the words. Then we will ask sort type with "AlertDialog".  */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.word_sort_alert_title);
                String[] options = WordSortType.getValuesAsStringArray(this); // Enum options are taken as Array of String type.
                builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        wordSortSelectedIndex = i; // The index is keeping at each click to options at AlertDialog.
                    }
                });
                builder.setPositiveButton(R.string.word_sort_alert_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Get the comparator by selectiong of sorting type. */
                        Comparator<Word> comparator = null;
                        switch (WordSortType.getTypeByKey(wordSortSelectedIndex)){
                            case MostCorrectlySelected:
                                 comparator = new WordRecyclerViewAdapter.ComparatorMostCorrectlySelected();
                                break;
                            case MostIncorrectlySelected:
                                comparator = new WordRecyclerViewAdapter.ComparatorMostIncorrectlySelected();
                                break;
                            case StrangeAZ:
                                comparator = new WordRecyclerViewAdapter.ComparatorStrangeAZ();
                                break;
                            case StrangeZA:
                                comparator = new WordRecyclerViewAdapter.ComparatorStrangeZA();
                                break;
                            case ExplainAZ:
                                comparator = new WordRecyclerViewAdapter.ComparatorExplainAZ();
                                break;
                            case ExplainZA:
                                comparator = new WordRecyclerViewAdapter.ComparatorExplainZA();
                                break;
                            default:
                                return;
                        }
                        getAdapter().sort(comparator); // And after sorting the words.
                        wordSortSelectedIndex=-1; // Clearing the index holder for after use.
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = builder.create(); // AlertDialog is ready.
                alertDialog.show(); // AlertDialog is work.
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Initial Methods. */
    private void initComponents() {

        /* Loading UI items. */
        Toolbar toolBar = findViewById(R.id.toolbar_word);
        recyclerViewWord = findViewById(R.id.recyclerViewWord);
        btnAddWord = findViewById(R.id.btnAddWord);
        btnToggleFreeze = findViewById(R.id.btnToggleFreeze);
        btnBackToCategoryFromWord = findViewById(R.id.btnBackToCategoryFromWord);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        adViewBannerWord = findViewById(R.id.adViewBannerWord);

        /* Hiding the app name "Label at Manifest". */
        toolBar.setTitle("");

        /* Custom Toolbar including to activity.*/
        setSupportActionBar(toolBar);

        /* Listeners giving to components. */
        btnAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordAddModalBottomSheetDialog bottomSheetDialog = new WordAddModalBottomSheetDialog(getActivity());
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(),WordAddModalBottomSheetDialog.TAG);
            }
        }); // Click Listener For "AddWord".
        btnToggleFreeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.toggleFreeze();
            }
        }); // Toggle word freeze view listener.
        btnBackToCategoryFromWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); // Return to "CategoryActivity".

        /* Advertising load. */
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBannerWord.loadAd(adRequest);
    }
    private void loadData() {
        /* Get "WordList" from DB. Need information received from intent.  */
        Intent intent = getIntent();
        selectedCategoryId = intent.getLongExtra(CategoryActivity.INTENT_CATEGORY_ID,0L);
        txtCategoryName.setText(intent.getStringExtra(CategoryActivity.INTENT_CATEGORY_NAME));
        List<Word> words = WordService.getWordsByCategoryId(this,selectedCategoryId);

        /* RecyclerView is prepared and CustomAdapter is added. */
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewWord.setLayoutManager(layoutManager);
        recyclerViewWord.setHasFixedSize(true);
        adapter = new WordRecyclerViewAdapter(this,words);
        recyclerViewWord.setAdapter(adapter); // the category list sended.
        recyclerViewWord.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    /* Getter-Setter. */
    public WordRecyclerViewAdapter getAdapter(){ return adapter; }
    public WordActivity getActivity(){ return this; }

    /* Util method. */
    public void refreshRecyclerView(){
        /* This method works after "ExcelWordsImport". */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.refreshRecyclerView(WordService.getWordsByCategoryId(getApplicationContext(),selectedCategoryId));
            }
        });
    }
}
