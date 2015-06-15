package com.ferjuarez.twhash.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;

import com.ferjuarez.twhash.R;
import com.ferjuarez.twhash.adapters.TweetCardAdapter;
import com.ferjuarez.twhash.api.ApiManager;
import com.ferjuarez.twhash.api.Authenticated;
import com.ferjuarez.twhash.api.OnAuthFinishListener;
import com.ferjuarez.twhash.api.OnSearchFinishListener;
import com.ferjuarez.twhash.api.UrlConstants;
import com.ferjuarez.twhash.db.DatabaseHelper;
import com.ferjuarez.twhash.models.Tweet;
import com.ferjuarez.twhash.utils.PreferencesManager;
import com.ferjuarez.twhash.utils.Utils;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements OnAuthFinishListener, OnSearchFinishListener {

    @InjectView(R.id.btnSearch)
    Button btnSearch;
    @InjectView(R.id.chipsMultiComplete)
    ChipsMultiAutoCompleteTextview chipsMultiComplete;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseHelper databaseHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Init Dependency Injection
        ButterKnife.inject(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerTweets);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try {
            chipsMultiComplete.setAdapter(new ArrayAdapter(this,
                    android.R.layout.simple_dropdown_item_1line, getHelper().getPersistedHashTags()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        chipsMultiComplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        auth();
    }



    @OnClick(R.id.btnSearch)
    public void onSearch(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chipsMultiComplete.getWindowToken(), 0);

        Map<String,String> params = new HashMap<>();
        params.put("q", Utils.buildAndPersistSearchQuery(chipsMultiComplete.getText().toString(), getHelper()));
        params.put("result_type", "mixed");
        ApiManager.getInstance(this).doRequest().search(params, PreferencesManager.getInstance(this).getToken(), this);
    }

    private void auth(){
        if(PreferencesManager.getInstance(this).getToken() == null || PreferencesManager.getInstance(this).getToken().equals("")){
            String urlApiKey = null;
            String urlApiSecret = null;
            try {
                urlApiKey = URLEncoder.encode(UrlConstants.CONSUMER_KEY, "UTF-8");
                urlApiSecret = URLEncoder.encode(UrlConstants.CONSUMER_SECRET, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            // Concatenate the encoded consumer key, a colon character, and the
            // encoded consumer secret
            String combined = urlApiKey + ":" + urlApiSecret;

            // Base64 encode the string
            String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);
            ApiManager.getInstance(this).doRequest().auth(UrlConstants.TW_TOKEN_URL, base64Encoded, this);
        } else progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onAuthFinished(Authenticated authenticated, Boolean state) {
        if(state){
            progressBar.setVisibility(View.INVISIBLE);
            PreferencesManager.getInstance(this).storeToken(authenticated.access_token);
        }

    }

    @Override
    public void OnSearchFinished(List<Tweet> tweets, Boolean state) {
        if(state){
            // specify an adapter (see also next example)
            mAdapter = new TweetCardAdapter(tweets);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
