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