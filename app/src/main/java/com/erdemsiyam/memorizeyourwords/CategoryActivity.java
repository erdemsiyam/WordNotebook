package com.erdemsiyam.memorizeyourwords;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.util.adapter.CategoryRecyclerViewAdapter;
import com.erdemsiyam.memorizeyourwords.util.listener.CategoryAddFABListener;
import com.erdemsiyam.memorizeyourwords.util.listener.CategorySearchListener;
import com.erdemsiyam.memorizeyourwords.util.listener.CategorySwipeListener;
import com.erdemsiyam.memorizeyourwords.util.listener.NavigationItemSelectListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.dift.ui.SwipeToAction;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // our recycler of category list
    private CategoryRecyclerViewAdapter adapter;
    private SwipeToAction swipeToAction; // when recycler item moved right or left then handle methods.
    private DrawerLayout drawerLayout; // our navigation menu
    private FloatingActionButton floatingActionButton; // add category FloatingActionButton.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // changing the splash screen to main screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initComponents(); // creating objects.
        loadData(); // test methods
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // category search icon adding to tool bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_search,menu);
        MenuItem searchItem = menu.findItem(R.id.categorySearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH); // when writing something to search then at the keyboard "enter" button view changed to SearchIcon view
        searchView.setOnQueryTextListener(new CategorySearchListener()); // when using search category(filter) then listener handling.

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) // if navigation menu opened (THE LEFT MENU)
            drawerLayout.closeDrawer(GravityCompat.START); // then this menu closing first.
        else
            super.onBackPressed();
    }

    private void initComponents(){
        //our custimozed tool bar include
        Toolbar toolBar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolBar);

        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.category_activity);

        //navigation menu include
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationItemSelectListener(this));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolBar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //floating action button include for "Add Category"
        floatingActionButton = findViewById(R.id.categoryAddFAButton);
        floatingActionButton.setOnClickListener(new CategoryAddFABListener(this));

    }
    private void loadData(){
        Category category1 = new Category();
            category1.setId(1L);
            category1.setName("Günler");
            category1.setColor("");
            category1.setAlarm(0l);
        Category category2 = new Category();
            category2.setId(2L);
            category2.setName("Aylar");
            category2.setColor("");
            category2.setAlarm(0l);
        Category category3 = new Category();
            category3.setId(3L);
            category3.setName("Hava Durumları");
            category3.setColor("");
            category3.setAlarm(0l);
        Category category4 = new Category();
            category4.setId(4L);
            category4.setName("Ev Eşyaları");
            category4.setColor("");
            category4.setAlarm(0l);
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);
        categories.add(category3);
        categories.add(category4);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CategoryRecyclerViewAdapter(this,categories);
        recyclerView.setAdapter(adapter); // the category list sended.
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        swipeToAction = new SwipeToAction(recyclerView, new CategorySwipeListener(adapter));
    }

    public void deleteCategories(){
        List<Integer> positions = adapter.getSelectedCategoryPositions();
        for(int i = positions.size()-1; i>=0; i--){
            adapter.removeData(positions.get(i));
        }
        adapter.notifyDataSetChanged();
    }


    /*########### GET-SET #############*/
    public CategoryRecyclerViewAdapter getAdapter(){ return adapter;}

}
