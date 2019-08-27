package com.erdemsiyam.memorizeyourwords.activity;

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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.adapter.CategoryRecyclerViewAdapter;
import com.erdemsiyam.memorizeyourwords.fragment.CategoryAddModalBottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import co.dift.ui.SwipeToAction;

public class CategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    /* Constants */
    public static final String INTENT_CATEGORY_ID = "category_id";
    public static final String INTENT_CATEGORY_NAME = "category_name";
    public static final String INTENT_EXAM_SELECT_INDEX = "examSelectIndex";
    public static final String INTENT_SELECTED_CATEGORY_IDS = "selectedCategoryIds";

    /* UI Components */
    private RecyclerView                recyclerView; // Category list.
    private CategoryRecyclerViewAdapter adapter; // Category list custom adapter.
    private DrawerLayout                drawerLayout; // The Layout for "Left Navigation Menu".
    private FloatingActionButton        fabAddCategory; // "Category Add" Button at bottom left.

    /* Override Methods. */
    @Override
    protected   void    onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme); // Real theme to be included after splash screen. // TO BE DONE LATER.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initComponents(); // UI components are installed.
        loadData(); // Data is loaded into UI components.
    }
    @Override
    public      boolean onCreateOptionsMenu(Menu menu) {
        /* Menu including. */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_category,menu);

        /* "CategorySearch" Menu item creating to searching categories. */
        MenuItem searchItem = menu.findItem(R.id.menu_item_category_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH); // The key "Enter Icon" on the keyboard will be "Search Icon" when searching.
        searchView.setOnQueryTextListener(adapter); // Required "SearchView.OnQueryTextListener" implements and override functions are filled to searching works.

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public      void    onBackPressed() {
        /* At first "Back Click" If the Left Menu is open then this is closed. */
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START); // Left Menu (Drawer Layout) is closed.
        else
            super.onBackPressed(); // If the menu is not open, normal clickback occurs.
    }
    @Override
    protected   void    onResume() {
        /* When comes back to another activities. */
        if(adapter != null) // If adapter already created, refresh its items because data may have changed.
            adapter.notifyDataSetChanged(); // Refreshing items.
        super.onResume();
    }

    /* Override method of NavigationView.OnNavigationItemSelectedListener*/
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        /* There is some Actions by which item selected at navigation menu. */
        switch (menuItem.getItemId()){
            case R.id.nav_about:
                Toast.makeText(this,"About",Toast.LENGTH_LONG).show();

            case R.id.nav_setting:
                // Will be done.
                /*
                (this).getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
                */
        }
        return true;
    } // This is LeftMenu's items click actions declare place.

    /* Initial Methods. */
    private void initComponents(){
        /* Loading UI items. */
        Toolbar toolBar = findViewById(R.id.toolbar_category);
        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.category_activity);
        fabAddCategory = findViewById(R.id.categoryAddFAButton);

        /* Custom Toolbar including to activity.*/
        setSupportActionBar(toolBar);

        /* Navigation View adding to DrawerLayout for "Left Menu". */
        NavigationView navigationView = findViewById(R.id.navigation_category);
        navigationView.setNavigationItemSelectedListener(this); // This class implements "NavigationView.OnNavigationItemSelectedListener" and filled the override method "onNavigationItemSelected". We can say "this".
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolBar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /* "Category Add" Listener giving to Floating Action Button. */
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* A Pop-up shown below to add categories. The Pop-up is "ModalBottomSheetDialog". */
                CategoryAddModalBottomSheetDialog bottomSheetDialog = new CategoryAddModalBottomSheetDialog((CategoryActivity)getApplicationContext());
                bottomSheetDialog.show(((CategoryActivity)getApplicationContext()).getSupportFragmentManager(),CategoryAddModalBottomSheetDialog.TAG);
            }
        });

    }
    private void loadData(){
        /* Category list is retrieved from DB. */
        List<Category> categories = CategoryService.getCategories(this);

        /* RecyclerView is prepared and CustomAdapter is added. */
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CategoryRecyclerViewAdapter(this,categories);
        recyclerView.setAdapter(adapter); // the category list sended.
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        /* SwipeToAction including to RecyclerView and SwipeListener added (inside at CustomAdapter). */
        new SwipeToAction(recyclerView, adapter);
    }

    /* Getter-Setter. */
    public CategoryRecyclerViewAdapter getAdapter(){ return adapter;}

}
