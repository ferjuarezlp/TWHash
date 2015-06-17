package com.ferjuarez.twhash.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ferjuarez on 11/06/15.
 */

public class PreferencesManager {
	private static String TAG = "PreferencesManager";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private static String KEY = "Twhash";
	public static final String KEY_USER_TOKEN = "user_token";
	public static final String KEY_SETTINGS_CACHE = "settings_cache";
	public static final String KEY_SETTINGS_EXCLUSIVE = "settings_exclusive";
	public static final String KEY_SETTINGS_GEO = "settings_geo";
    public static final String KEY_SETTINGS_LAT = "settings_lat";
    public static final String KEY_SETTINGS_LNG = "settings_lng";

    public static final String KEY_SETTINGS_RESULT_TYPE = "settings_result";
    private static volatile PreferencesManager instance = null;

	private PreferencesManager(Context context) {
		this.context = context;
	}
	
	public static PreferencesManager getInstance(Context context) {
		if (instance == null) {
			synchronized (PreferencesManager.class) {
				if (instance == null) {
					instance = new PreferencesManager(context);
				}
			}
		}
		instance.init();
		return instance;
	}
	
	public SharedPreferences getSharedPreferences()
	{
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
	}

	public void setGeolocationMode(boolean geo){
		initEditMode();
		editor.putBoolean(KEY_SETTINGS_GEO, geo);
		editor.commit();
	}

	public boolean isGeolocationEnabled(){
		return prefs.getBoolean(KEY_SETTINGS_GEO, false);
	}

	public void setExclusiveMode(boolean exclusive){
		initEditMode();
		editor.putBoolean(KEY_SETTINGS_EXCLUSIVE, exclusive);
		editor.commit();
	}

	public boolean isExclusiveEnabled(){
		return prefs.getBoolean(KEY_SETTINGS_EXCLUSIVE, false);
	}

	public void setCache(boolean cache){
		initEditMode();
		editor.putBoolean(KEY_SETTINGS_CACHE, cache);
		editor.commit();
	}

	public boolean isCacheEnabled(){
		return prefs.getBoolean(KEY_SETTINGS_CACHE, false);
	}

    public void setResultType(int value){
        initEditMode();
        editor.putInt(KEY_SETTINGS_RESULT_TYPE, value);
        editor.commit();
    }

    public int getResultType(){
        return prefs.getInt(KEY_SETTINGS_RESULT_TYPE, 0);
    }

    public void storeLat(String lat){
        initEditMode();
        editor.putString(KEY_SETTINGS_LAT, lat);
        editor.commit();
    }

    public String getLat(){
        return prefs.getString(KEY_SETTINGS_LAT, "");
    }

    public void storeLng(String lng){
        initEditMode();
        editor.putString(KEY_SETTINGS_LNG, lng);
        editor.commit();
    }

    public String getLng(){
        return prefs.getString(KEY_SETTINGS_LNG, "");
    }

	public void storeToken(String token){
		initEditMode();
		editor.putString(KEY_USER_TOKEN, token);
		editor.commit();
	}

	public String getToken(){
		return prefs.getString(KEY_USER_TOKEN,"");
	}




	private void init(){
        prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }
    
    private void close(){
        editor.commit();
    }
    
    private void initEditMode(){
        editor = prefs.edit();
    }
    
    public void clean(){
    	getSharedPreferences().edit().clear().commit();
    }	
    
    
}