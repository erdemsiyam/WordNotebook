package com.erdemsiyam.memorizeyourwords.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.androidservice.WordNotificationService;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.adapter.CategoryRecyclerViewAdapter;
import com.erdemsiyam.memorizeyourwords.fragment.CategoryAddModalBottomSheetDialog;
import com.erdemsiyam.memorizeyourwords.util.DonationPurchaseHelper;
import com.erdemsiyam.memorizeyourwords.util.DonationType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import co.dift.ui.SwipeToAction;

public class CategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    /* Indexing Variable */
    private int selectedDonationIndex = -1; // For donation $ value select index.

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
            case R.id.nav_settings:
                /* Go to the SettingActivity. */
                startActivity(new Intent(this,SettingActivity.class));
                break;
            case R.id.nav_rate:
                String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.nav_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:siyamyazilim@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WordNotebook Feedback - User : "+android.os.Build.VERSION.SDK_INT+" API ("+getResources().getString(R.string.feedback_title_warning)+")");
                try {
                    startActivity(emailIntent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this,"E-mail App Not Found. Developer E-Mail is : siyamyazilim@gmail.com", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.nav_donation:
                /* AlertDialog is prepared which words we want to choose. */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.donation_alert_dialog_title);
                String[] options = DonationType.getValuesAsStringArray(); // Enum options are taken as Array of String type.
                builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedDonationIndex = i; // The index is keeping at each click to options at AlertDialog.
                        Toast.makeText(getApplicationContext(),DonationType.getRandomJokeByKey(getApplicationContext(),selectedDonationIndex),Toast.LENGTH_LONG).show();
                    }
                });
                builder.setPositiveButton(R.string.donation_alert_dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DonationType selectedDonationType = DonationType.getTypeByKey(selectedDonationIndex);
                        DonationPurchaseHelper.start(CategoryActivity.this,selectedDonationType);
                    }

                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),DonationType.Fault.getRandomJoke(getApplicationContext()),Toast.LENGTH_LONG).show();
                    }
                });
                builder.create().show(); // AlertDialog is ready.
                break;
            case R.id.nav_about:
                Toast.makeText(this,getResources().getString(R.string.developer)+" Erdem Siyam.",Toast.LENGTH_LONG).show();
                break;
        }
        drawerLayout.closeDrawer(Gravity.LEFT); // Closing "DrawerMenu".
        return true;
    } // This is LeftMenu's items click actions declare place.

    /* Initial Methods. */
    private void initComponents(){
        /* Loading UI items. */
        Toolbar toolBar = findViewById(R.id.toolbar_category);
        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.category_activity);
        fabAddCategory = findViewById(R.id.categoryAddFAButton);

        /* Hiding the app name "Label at Manifest". */
        toolBar.setTitle("");

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
                CategoryAddModalBottomSheetDialog bottomSheetDialog = new CategoryAddModalBottomSheetDialog(getActivity());
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(),CategoryAddModalBottomSheetDialog.TAG);
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

        /* Starting "WordNotificationService" for notify words. */
        WordNotificationService.start(this);
    }

    /* Getter-Setter. */
    public CategoryRecyclerViewAdapter getAdapter(){ return adapter;}
    private CategoryActivity getActivity(){ return this;}
}
