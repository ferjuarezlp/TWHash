package com.ferjuarez.twhash.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import com.ferjuarez.twhash.R;
import com.ferjuarez.twhash.adapters.TweetCardAdapter;
import com.ferjuarez.twhash.api.ApiManager;
import com.ferjuarez.twhash.api.Authenticated;
import com.ferjuarez.twhash.interfaces.OnAuthFinishListener;
import com.ferjuarez.twhash.interfaces.OnSearchFinishListener;
import com.ferjuarez.twhash.api.UrlConstants;
import com.ferjuarez.twhash.db.DatabaseHelper;
import com.ferjuarez.twhash.models.Tweet;
import com.ferjuarez.twhash.utils.PreferencesManager;
import com.ferjuarez.twhash.utils.Utils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SearchFragment extends Fragment implements OnAuthFinishListener, OnSearchFinishListener {

    @InjectView(R.id.btnSearch)
    Button btnSearch;
    @InjectView(R.id.chipsMultiComplete)
    ChipsMultiAutoCompleteTextView chipsMultiComplete;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.recyclerTweets)
    RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseHelper databaseHelper = null;
    private SearchFragment searchFragment;

    private List<Tweet> tweets = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.inject(this, v);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new TweetCardAdapter(getRetainedData());
        mRecyclerView.setAdapter(mAdapter);

        try {
            chipsMultiComplete.setAdapter(new ArrayAdapter(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, getHelper().getPersistedHashTags()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        chipsMultiComplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        checkAuthentication();
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void retainData(List<Tweet> data) {
        this.tweets = data;
    }

    public List<Tweet> getRetainedData() {
        return tweets;
    }

    private void checkAuthentication(){
        if(PreferencesManager.getInstance(getActivity()).getToken() == null || PreferencesManager.getInstance(getActivity()).getToken().equals("")){
            String urlApiKey = null;
            String urlApiSecret = null;
            try {
                urlApiKey = URLEncoder.encode(UrlConstants.CONSUMER_KEY, "UTF-8");
                urlApiSecret = URLEncoder.encode(UrlConstants.CONSUMER_SECRET, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String combined = urlApiKey + ":" + urlApiSecret;

            // Base64 encode the string
            String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);
            ApiManager.getInstance(getActivity()).doRequest().auth(UrlConstants.TW_TOKEN_URL, base64Encoded, this);
        } else progressBar.setVisibility(View.INVISIBLE);

    }

    @OnClick(R.id.btnSearch)
    public void onSearch(View view) {
        Utils.hideKeyboard(getActivity(), chipsMultiComplete);

        Map<String,String> params = new HashMap<>();
        params.put("q", Utils.buildAndPersistSearchQuery(chipsMultiComplete.getText().toString(), getHelper()));
        params.put("result_type", "mixed");
        ApiManager.getInstance(getActivity()).doRequest().search(params, PreferencesManager.getInstance(getActivity()).getToken(), this);
    }

    @Override
    public void onAuthFinished(Authenticated authenticated, Boolean state) {
        if(state){
            progressBar.setVisibility(View.INVISIBLE);
            PreferencesManager.getInstance(getActivity()).storeToken(authenticated.access_token);
        }
    }

    @Override
    public void OnSearchFinished(List<Tweet> tweets, Boolean state) {
        if(state){
            retainData(tweets);
            mAdapter = new TweetCardAdapter(tweets);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
}