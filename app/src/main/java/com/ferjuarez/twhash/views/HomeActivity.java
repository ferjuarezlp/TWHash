package com.ferjuarez.twhash.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ferjuarez.twhash.R;

public class HomeActivity extends AppCompatActivity{

    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        searchFragment = (SearchFragment) fm.findFragmentByTag("searchFragment");

        // create the fragment and data the first time
        if (searchFragment == null) {
            // add the fragment
            searchFragment = new SearchFragment();
            fm.beginTransaction().add(R.id.container, new SearchFragment(), "searchFragment").commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
