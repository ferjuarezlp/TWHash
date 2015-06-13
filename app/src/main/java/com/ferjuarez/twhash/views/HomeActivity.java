package com.ferjuarez.twhash.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ferjuarez.twhash.R;
import com.ferjuarez.twhash.adapters.TweetCardAdapter;
import com.ferjuarez.twhash.api.ApiManager;
import com.ferjuarez.twhash.api.Authenticated;
import com.ferjuarez.twhash.api.OnAuthFinishListener;
import com.ferjuarez.twhash.api.OnSearchFinishListener;
import com.ferjuarez.twhash.api.UrlConstants;
import com.ferjuarez.twhash.models.Tweet;
import com.ferjuarez.twhash.utils.PreferencesManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements OnAuthFinishListener, OnSearchFinishListener {

    @InjectView(R.id.btnSearch)
    Button btnSearch;
    @InjectView(R.id.editTextSearch)
    EditText editTextSearchResult;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Init Dependency Injection
        ButterKnife.inject(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


    }

    @OnClick(R.id.btnAuth)
    public void onAuth(View view) {
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
    }

    @OnClick(R.id.btnSearch)
    public void onSearch(View view) {
        ApiManager.getInstance(this).doRequest().search(UrlConstants.TW_TIMELINE_URL + "ferjuarezlp", PreferencesManager.getInstance(this).getToken(), this);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAuthFinished(Authenticated authenticated, Boolean state) {
        PreferencesManager.getInstance(this).storeToken(authenticated.access_token);
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
