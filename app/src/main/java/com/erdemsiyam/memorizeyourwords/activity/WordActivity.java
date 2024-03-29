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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.fragment.ExcelExportDialogFragment;
import com.erdemsiyam.memorizeyourwords.fragment.ExcelImportFirstDialogFragment;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.WordGroupType;
import com.erdemsiyam.memorizeyourwords.util.WordSortType;
import com.erdemsiyam.memorizeyourwords.adapter.WordRecyclerViewAdapter;
import com.erdemsiyam.memorizeyourwords.fragment.WordAddModalBottomSheetDialog;
import com.erdemsiyam.memorizeyourwords.fragment.WordEditModalBottomSheetDialog;
import com.google.android.gms.ads.AdListener;
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
    public static int   wordVisibilitySelectedIndex=-1;

    /* Property. */
    public Category     selectedCategory;

    /* UI Components */
    private RecyclerView            recyclerViewWord; // Word list.
    private AppCompatImageButton    imgBtnAddWord; // Add category button.
    private WordRecyclerViewAdapter adapter; // Word list custom adapter.
    private AppCompatImageButton    imgBtnToggleFreeze; // A button to change the appearance of words.
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
        loadFontSizes();
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
            case R.id.wordDeleteLearned:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.learned_words_delete_alert_title);
                builder.setMessage("");
                builder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WordService.deleteAllLearnedByCategoryId(WordActivity.this,selectedCategory.getId());
                        refreshRecyclerView();
                    }
                });
                builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.wordExportExcel:
                /* Version control. */
                if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                    Toast.makeText(this,R.string.excel_message_version_error,Toast.LENGTH_LONG).show();
                    break;
                }
                /* Adding words with Excel Import */
                ExcelExportDialogFragment dialogExportExcel = new ExcelExportDialogFragment(this,selectedCategory.getId());
                dialogExportExcel.show(getSupportFragmentManager(), ExcelExportDialogFragment.TAG);
                break;
            case R.id.wordImportExcel:
                /* Version control. */
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP){
                    Toast.makeText(this,R.string.excel_message_version_error,Toast.LENGTH_LONG).show();
                    break;
                }
                /* Adding words with Excel Import */
                ExcelImportFirstDialogFragment dialogImportExcel = new ExcelImportFirstDialogFragment(this,selectedCategory.getId());
                dialogImportExcel.show(getSupportFragmentManager(), ExcelImportFirstDialogFragment.TAG);
                break;
            case R.id.wordSort:
                /* If user wants sorting the words. Then we will ask sort type with "AlertDialog".  */
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle(R.string.word_sort_alert_title);
                String[] options = WordSortType.getValuesAsStringArray(this); // Enum options are taken as Array of String type.
                builder2.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        wordSortSelectedIndex = i; // The index is keeping at each click to options at AlertDialog.
                    }
                });
                builder2.setPositiveButton(R.string.word_sort_alert_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Get the comparator by selecting of sorting type. */
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
                builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog2 = builder2.create(); // AlertDialog is ready.
                alertDialog2.show(); // AlertDialog is work.
                break;
            case R.id.wordVisibility:
                /* If user wants change visibility of the words. Then we will ask which words, with "AlertDialog". */
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setTitle(R.string.word_visibility_alert_title);
                String[] options2 = WordGroupType.getValuesAsStringArray(this); // Enum options are taken as Array of String type.
                builder3.setSingleChoiceItems(options2, selectedCategory.getVisibilityWordGroupType(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        wordVisibilitySelectedIndex = i; // The index is keeping at each click to options at AlertDialog.
                    }
                });
                builder3.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Get the comparator by selecting of sorting type. */
                        if(wordVisibilitySelectedIndex == -1) return;

                        /* Changing visibility words of the category. */
                        selectedCategory.setVisibilityWordGroupType(wordVisibilitySelectedIndex);
                        CategoryService.updateCategory(WordActivity.this,selectedCategory);

                        /* Refreshed words after selecting word visibility. */
                        List<Word> words = WordService.getWordsByCategoryIdAndWordWordVisibilityType(WordActivity.this,selectedCategory.getId(),WordGroupType.getTypeByKey(selectedCategory.getVisibilityWordGroupType()));
                        getAdapter().refreshRecyclerView(words);

                        wordVisibilitySelectedIndex=-1; // Clearing the index holder for after use.
                    }
                });
                builder3.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog3 = builder3.create(); // AlertDialog is ready.
                alertDialog3.show(); // AlertDialog is work.
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*  When Excel Import/Export permissions request arrive.
            User responses are tracked here to continue their works.*/

        /* Waiting for "ReadFilePermission" from "ExcelImportFirstDialogFragment" */
        if(requestCode == ExcelImportFirstDialogFragment.PERMISSION_REQUEST_CODE && ExcelImportFirstDialogFragment.instance != null){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ExcelImportFirstDialogFragment.instance.openSdCard(); // If allowed, continue the listing files.
            else
                ExcelImportFirstDialogFragment.instance.dismiss(); // If not allowed, close dialog.
        }
        if(requestCode == ExcelExportDialogFragment.PERMISSION_REQUEST_CODE && ExcelExportDialogFragment.instance != null){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                ExcelExportDialogFragment.instance.openSdCard(); // If allowed, continue the listing files.
            else
                ExcelExportDialogFragment.instance.dismiss(); // If not allowed, close dialog.
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* Initial Methods. */
    private void initComponents() {

        /* Loading UI items. */
        Toolbar toolBar = findViewById(R.id.toolbar_word);
        recyclerViewWord = findViewById(R.id.recyclerViewWord);
        imgBtnAddWord = findViewById(R.id.imgBtnAddWord);
        imgBtnToggleFreeze = findViewById(R.id.imgBtnToggleFreeze);
        btnBackToCategoryFromWord = findViewById(R.id.btnBackToCategoryFromWord);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        adViewBannerWord = findViewById(R.id.adViewBannerWord);

        /* Hiding the app name "Label at Manifest". */
        toolBar.setTitle("");

        /* Custom Toolbar including to activity.*/
        setSupportActionBar(toolBar);

        /* Listeners giving to components. */
        imgBtnAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordAddModalBottomSheetDialog bottomSheetDialog = new WordAddModalBottomSheetDialog(getActivity());
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(),WordAddModalBottomSheetDialog.TAG);
            }
        }); // Click Listener For "AddWord".
        imgBtnAddWord.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(WordActivity.this,R.string.ui_add_word,Toast.LENGTH_LONG).show();
                return true;
            }
        }); // Added information when long click.
        imgBtnToggleFreeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.toggleFreeze();
            }
        }); // Toggle word freeze view listener.
        imgBtnToggleFreeze.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(WordActivity.this,R.string.ui_switch_visibility,Toast.LENGTH_LONG).show();
                return true;
            }
        }); // Added information when long click.
        btnBackToCategoryFromWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); // Return to "CategoryActivity".

        /* Advertising load. */
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBannerWord.loadAd(adRequest);
        adViewBannerWord.setAdListener(new BottomAdListener()); // The ad listener customized at below.
    }
    private void loadData() {
        /* Get "WordList" from DB. Need information received from intent.  */
        Intent intent = getIntent();
        long selectedCategoryId = intent.getLongExtra(CategoryActivity.INTENT_CATEGORY_ID,0L);
        selectedCategory = CategoryService.getCategoryById(this,selectedCategoryId);
        if(selectedCategory == null) {
            Toast.makeText(this,getString(R.string.word_page_error_category_not_found),Toast.LENGTH_LONG);
            finish();
        }
        txtCategoryName.setText(selectedCategory.getName());
        List<Word> words = WordService.getWordsByCategoryIdAndWordWordVisibilityType(this,selectedCategoryId,WordGroupType.getTypeByKey(selectedCategory.getVisibilityWordGroupType()));

        /* RecyclerView is prepared and CustomAdapter is added. */
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewWord.setLayoutManager(layoutManager);
        recyclerViewWord.setHasFixedSize(true);
        adapter = new WordRecyclerViewAdapter(this,words);
        recyclerViewWord.setAdapter(adapter); // the category list sended.
        recyclerViewWord.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }
    private void loadFontSizes() {
        txtCategoryName.setTextSize(SettingActivity.getFont(this,-3));
        ((AppCompatTextView)findViewById(R.id.txtMarkTitle)).setTextSize(SettingActivity.getFont(this,-3)); // Setting font of The Nav Element Header Text.
        ((AppCompatTextView)findViewById(R.id.txtLearnedTitle)).setTextSize(SettingActivity.getFont(this,-3)); // Setting font of The Nav Element Header Text.
    }

    /* Getter-Setter. */
    public WordRecyclerViewAdapter getAdapter(){ return adapter; }
    public WordActivity getActivity(){ return this; }

    /* Util method. */
    public void refreshRecyclerView(){
        /* This method works after "ExcelWordsImport" Or "DeleteAllLearned". */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.refreshRecyclerView(WordService.getWordsByCategoryId(getApplicationContext(),selectedCategory.getId()));
            }
        });
    }

    /* Ad Listener Class */
    private class BottomAdListener extends AdListener {
        public BottomAdListener() {
            super();

            /* Hiding ad place when the ad not load yet.*/
            adViewBannerWord.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0,
                0f
                ));
        }
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();

            /* Show ad place when the ad loaded. */
            adViewBannerWord.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0,
                    0.65f
            ));
        }
    }
}
