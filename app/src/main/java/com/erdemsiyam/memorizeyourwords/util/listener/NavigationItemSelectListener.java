package com.erdemsiyam.memorizeyourwords.util.listener;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.SettingsDetailFragment;
import com.google.android.material.navigation.NavigationView;

public class NavigationItemSelectListener implements NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    public NavigationItemSelectListener(Context ctx) {this.context = ctx;}
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // which item selected at navigation menu. then these action works.
        switch (menuItem.getItemId()){
            case R.id.nav_about:
                Toast.makeText(context,"About",Toast.LENGTH_LONG).show();

            case R.id.nav_setting:
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsDetailFragment())
                        .addToBackStack(null)
                        .commit();
        }
        return false;
    }
}
