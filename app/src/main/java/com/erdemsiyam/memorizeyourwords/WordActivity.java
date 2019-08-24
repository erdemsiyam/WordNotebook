package com.erdemsiyam.memorizeyourwords;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.adapter.WordRecyclerViewAdapter;
import com.erdemsiyam.memorizeyourwords.util.fragment.WordEditModalBottomSheetDialog;
import com.erdemsiyam.memorizeyourwords.util.listener.word.WordAddFABListener;
import com.erdemsiyam.memorizeyourwords.util.listener.word.WordFreezeToggleOnClickListener;
import com.erdemsiyam.memorizeyourwords.util.listener.word.WordSearchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WordActivity extends AppCompatActivity {

    public static final int CONTEXT_MENU_DELETE = 1;
    public static final int CONTEXT_MENU_EDIT = 2;
    public static int wordSortSelectedIndex=-1;
    private RecyclerView recyclerViewWord; // our recycler of category list
    private FloatingActionButton btnFabWordAdd; // add category FloatingActionButton.
    private WordRecyclerViewAdapter adapter;
    private Button btnFreezeToggle;
    public long selectedCategoryId;
    private AppCompatImageButton btnBackToCategoryFromWord;
    private AppCompatTextView txtCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        initComponents(); // creating objects.
        loadData(); // test methods
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // word search icon adding to tool bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.word_search,menu);
        MenuItem searchItem = menu.findItem(R.id.wordSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH); // when writing something to search then at the keyboard "enter" button view changed to SearchIcon view
        searchView.setOnQueryTextListener(new WordSearchListener(this)); // when using search category(filter) then listener handling.
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) { // When click a item at recyclerView
        switch (item.getItemId()){
            case CONTEXT_MENU_DELETE:
                getAdapter().deleteWord(item.getGroupId());
                break;
            case CONTEXT_MENU_EDIT:
                int index = item.getGroupId();
                Word word = getAdapter().getWordByIndex(index);
                WordEditModalBottomSheetDialog bottomSheetDialog = new WordEditModalBottomSheetDialog(this,word,index);
                bottomSheetDialog.show(this.getSupportFragmentManager(),"wordEditMBS");
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.wordSort:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Sıralama Seçiniz");
                String[] options = {"En çok doğru seçilenler","En çok yanlış seçilenler","Yabancı A-Z","Yabancı Z-A","Ana Dil A-Z","Ana Dil Z-A"};
                builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        wordSortSelectedIndex = i;
                    }
                });
                builder.setPositiveButton("Sırala", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Comparator<Word> comparator = null;
                        switch (wordSortSelectedIndex){
                            case 0:
                                 comparator = new WordRecyclerViewAdapter.ComparatorMuchSelectedTrue();
                                break;
                            case 1:
                                comparator = new WordRecyclerViewAdapter.ComparatorMuchSelectedFalse();
                                break;
                            case 2:
                                comparator = new WordRecyclerViewAdapter.ComparatorStrangeAZ();
                                break;
                            case 3:
                                comparator = new WordRecyclerViewAdapter.ComparatorStrangeZA();
                                break;
                            case 4:
                                comparator = new WordRecyclerViewAdapter.ComparatorExplainAZ();
                                break;
                            case 5:
                                comparator = new WordRecyclerViewAdapter.ComparatorExplainZA();
                                break;
                            default:
                                return;
                        }
                        getAdapter().sort(comparator);
                        wordSortSelectedIndex=-1;
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        //custimozed tool bar including
        Toolbar toolBar = findViewById(R.id.toolbar_word);
        setSupportActionBar(toolBar);

        //recyclerview
        recyclerViewWord = findViewById(R.id.recyclerViewWord);

        //floating action button For "Add word"
        btnFabWordAdd = findViewById(R.id.btnFabWordAdd);
        btnFabWordAdd.setOnClickListener(new WordAddFABListener(this));

        //freeze button WordFreezeToggleOnClickListener
        btnFreezeToggle = findViewById(R.id.btnFreezeToggle);
        btnFreezeToggle.setOnClickListener(new WordFreezeToggleOnClickListener(this));

        btnBackToCategoryFromWord = findViewById(R.id.btnBackToCategoryFromWord);
        btnBackToCategoryFromWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtCategoryName = findViewById(R.id.txtCategoryName);
    }

    private void loadData() {

        //get intent and words.
        Intent intent = getIntent();
        selectedCategoryId = intent.getLongExtra(CategoryActivity.INTENT_CATEGORY_ID,0L);
        txtCategoryName.setText(intent.getStringExtra(CategoryActivity.INTENT_CATEGORY_NAME));
        List<Word> words = WordService.getWordsByCategoryId(this,selectedCategoryId); // get words from db

        //recyclerview
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewWord.setLayoutManager(layoutManager);
        recyclerViewWord.setHasFixedSize(true);
        adapter = new WordRecyclerViewAdapter(this,words);
        recyclerViewWord.setAdapter(adapter); // the category list sended.
        recyclerViewWord.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }


    public WordRecyclerViewAdapter getAdapter(){ return adapter;}
}
