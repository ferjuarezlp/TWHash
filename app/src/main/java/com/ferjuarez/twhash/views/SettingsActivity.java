package com.ferjuarez.twhash.views;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ferjuarez.twhash.R;
import com.ferjuarez.twhash.db.DatabaseHelper;
import com.ferjuarez.twhash.utils.PreferencesManager;
import com.ferjuarez.twhash.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @InjectView(R.id.switchCache)
    SwitchCompat switchCache;
    @InjectView(R.id.switchExclusive)
    SwitchCompat switchExclusive;
    @InjectView(R.id.switchGeolocated)
    SwitchCompat switchGeolocated;
    @InjectView(R.id.radioResultType)
    RadioGroup radioResultType;
    @InjectView(R.id.progressBarLocation)
    ProgressBar progressBar;
    @InjectView(R.id.btnClearCache)
    Button btnClearCache;

    private DatabaseHelper databaseHelper = null;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Init Dependency Injection
        ButterKnife.inject(this);

        boolean cacheEnabled = PreferencesManager.getInstance(SettingsActivity.this).isCacheEnabled();
        if(cacheEnabled){
            btnClearCache.setVisibility(View.VISIBLE);
        } else btnClearCache.setVisibility(View.GONE);
        switchCache.setChecked(cacheEnabled);
        switchCache.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.getInstance(SettingsActivity.this).setCache(isChecked);
                if(isChecked) btnClearCache.setVisibility(View.VISIBLE);
                else {
                    btnClearCache.setVisibility(View.GONE);
                }
            }
        });
        switchExclusive.setChecked(PreferencesManager.getInstance(SettingsActivity.this).isExclusiveEnabled());
        switchExclusive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.getInstance(SettingsActivity.this).setExclusiveMode(isChecked);
            }
        });

        switchGeolocated.setChecked(PreferencesManager.getInstance(SettingsActivity.this).isGeolocationEnabled());
        switchGeolocated.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.getInstance(SettingsActivity.this).setGeolocationMode(isChecked);
                if(isChecked){
                    if(Utils.isLocationEnabled(SettingsActivity.this)){
                        buildGoogleApiClient();
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(SettingsActivity.this,getString(R.string.settings_no_location_msg),Toast.LENGTH_SHORT).show();
                        switchGeolocated.setChecked(false);
                        PreferencesManager.getInstance(SettingsActivity.this).setGeolocationMode(false);
                    }
                }
            }
        });

        radioResultType.check(getRadioButtonByPosition(PreferencesManager.getInstance(SettingsActivity.this).getResultType()));
        radioResultType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.rbMixed:
                        PreferencesManager.getInstance(SettingsActivity.this).setResultType(0);
                        break;
                    case R.id.rbRecent:
                        PreferencesManager.getInstance(SettingsActivity.this).setResultType(1);
                        break;
                    case R.id.rbPopular:
                        PreferencesManager.getInstance(SettingsActivity.this).setResultType(2);
                        break;
                }
            }
        });

    }

    @OnClick(R.id.btnClearCache)
    public void onClear(View view) {
        try {
            getHelper().deleteHashTags();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getRadioButtonByPosition(int pos){
        switch (pos){
        case 0:
            return R.id.rbMixed;
        case 1:
            return R.id.rbRecent;
        case 2:
            return R.id.rbPopular;
        }
        return R.id.rbMixed;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient!= null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        progressBar.setVisibility(View.INVISIBLE);
        if (mLastLocation != null) {

            PreferencesManager.getInstance(this).storeLat(String.valueOf(mLastLocation.getLatitude()));
            PreferencesManager.getInstance(this).storeLng(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
