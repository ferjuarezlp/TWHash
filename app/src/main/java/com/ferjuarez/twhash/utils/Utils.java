package com.ferjuarez.twhash.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ferjuarez.twhash.db.DatabaseHelper;
import com.ferjuarez.twhash.models.HashTag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ferjuarez on 12/06/15.
 */

public class Utils {
    public static final int RADIUS = 5000;

    public static void hideKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean checkConnectivity(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return ((networkInfo != null) && (networkInfo.isConnected()));

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static String buildAndPersistSearchQuery(String textToSearch, String operator, DatabaseHelper dbHelper){
        String search = "";
        List<String> items = Arrays.asList(textToSearch.split("\\s* \\s*"));
        for (int i = 0; i < items.size() ; i++) {
            try {
                dbHelper.getHashTagDao().create(new HashTag(items.get(i)));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            search = search + items.get(i) + ""+operator+"";
        }
        search = search.substring(0, search.lastIndexOf(""+operator));
        if(search.contains(",")){
            search.replace(",","");
        }
        return search;
    }

    public static String buildSearchQuery(String textToSearch, String operator){
        String search = "";
        List<String> items = Arrays.asList(textToSearch.split("\\s* \\s*"));
        for (int i = 0; i < items.size() ; i++) {
            search = search + items.get(i) + "" + operator + "";
        }
        search = search.substring(0, search.lastIndexOf(""+operator));
        if(search.contains(",")){
            search.replace(",","");
        }
        try {
            search = URLEncoder.encode(search, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return search;
    }

    public static String buildParamsForSearch(String url, Map<String,String> params){
        String urlWithParms = url + "?";
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            urlWithParms = urlWithParms + entry.getKey() + "=" + entry.getValue() + "&";
        }
        Log.e("URL WITH PARAMS", urlWithParms.substring(0, urlWithParms.length() - 1));
        return urlWithParms.substring(0,urlWithParms.length()-1);
    }

    public static Map<String, String> buildParms(Context context, String query, DatabaseHelper helper){
        Map<String,String> params = new HashMap<>();
        String operator = "%20or%20";
        if(PreferencesManager.getInstance(context).isExclusiveEnabled()){
            operator = "%20and%20";
        }

        if(PreferencesManager.getInstance(context).isCacheEnabled()){
            params.put("q", Utils.buildAndPersistSearchQuery(query,operator, helper));
        } else {
            if(PreferencesManager.getInstance(context).isExclusiveEnabled())
            params.put("q", Utils.buildSearchQuery(query, operator));
        }

        params.put("result_type", Utils.getResultTypeParam(context));

        if(PreferencesManager.getInstance(context).isGeolocationEnabled()){
            params.put("geocode", Utils.getGeocodeParam(context));
        }
        return params;
    }

    public static String getResultTypeParam(Context context){
        switch(PreferencesManager.getInstance(context).getResultType()) {
            case 0:
                return "mixed";
            case 1:
                return "recent";
            case 2:
                return "popular";
        }
        return "mixed";
    }

    public static String getGeocodeParam(Context context){
        String lat = PreferencesManager.getInstance(context).getLat();
        String lng = PreferencesManager.getInstance(context).getLng();
        if(!lat.equals("") && !lng.equals("")){
            return lat + "," + lng + "," + RADIUS + "km";
        } else return "";
    }
}
