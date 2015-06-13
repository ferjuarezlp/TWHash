package com.ferjuarez.twhash.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ferjuarez on 12/06/15.
 */

public class Utils {

    public static boolean checkConnectivity(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return ((networkInfo != null) && (networkInfo.isConnected()));

    }
}
