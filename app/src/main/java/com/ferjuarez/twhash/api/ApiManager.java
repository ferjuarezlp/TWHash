package com.ferjuarez.twhash.api;

import android.content.Context;

/**
 * Created by ferjuarez on 11/06/15.
 */

public class ApiManager {
    private static ApiManager instance;
    private RequestManager mRequestManager;

    private ApiManager(Context context) {
        mRequestManager = new RequestManager(context);
    }

    public RequestManager doRequest() {
        return mRequestManager;
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager(context);
        }
        return instance;
    }

    public static synchronized ApiManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException
                    (ApiManager.class.getSimpleName() +
                            " is not initialized, call getInstance(..) method first.");
        }
        return instance;
    }
}