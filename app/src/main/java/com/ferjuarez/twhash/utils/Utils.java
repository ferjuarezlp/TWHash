package com.ferjuarez.twhash.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ferjuarez.twhash.db.DatabaseHelper;
import com.ferjuarez.twhash.models.HashTag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by ferjuarez on 12/06/15.
 */

public class Utils {

    public static boolean checkConnectivity(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return ((networkInfo != null) && (networkInfo.isConnected()));

    }

    public static String buildAndPersistSearchQuery(String textToSearch, DatabaseHelper dbHelper){
        String search = "";
        List<String> items = Arrays.asList(textToSearch.split("\\s* \\s*"));
        for (int i = 0; i < items.size() ; i++) {
            try {
                dbHelper.getHashTagDao().create(new HashTag(items.get(i)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            search = search + items.get(i) + " or ";
        }
        search = search.substring(0, search.lastIndexOf(" or"));
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
            //System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        Log.e("URL WITH PARAMS", urlWithParms.substring(0, urlWithParms.length() - 1));
        return urlWithParms.substring(0,urlWithParms.length()-1);
    }
}
