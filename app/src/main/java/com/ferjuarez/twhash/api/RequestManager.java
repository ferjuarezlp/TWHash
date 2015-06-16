package com.ferjuarez.twhash.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ferjuarez.twhash.interfaces.OnAuthFinishListener;
import com.ferjuarez.twhash.interfaces.OnSearchFinishListener;
import com.ferjuarez.twhash.models.Tweet;
import com.ferjuarez.twhash.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ferjuarez on 11/06/15.
 */

public class RequestManager {
	private RequestQueue mRequestQueue;
	private Context context;
	public static String TAG = "RequestManager";
	public static int REQUEST_TIMEOUT = 40000;
	
	public RequestManager(Context context) {
		this.context = context;
		mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
	}

	public void auth(String url, final String base64Encoded, final OnAuthFinishListener mRequestFinish){
		StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
                Authenticated auth = jsonToAuthenticated(response);
                if (auth != null && auth.token_type.equals("bearer")) {
                    mRequestFinish.onAuthFinished(auth, true);
                }

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, error.toString() + error.getMessage());
				mRequestFinish.onAuthFinished(null, false);
			}
		}){
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<>();
				params.put("grant_type", "client_credentials");
				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
				params.put("Authorization", "Basic " + base64Encoded);
				params.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		mRequestQueue.add(stringRequest);
	}


	public void search(final Map<String,String> searchParams, final String accessToken, final OnSearchFinishListener requestFinish){
		String urlWithParams = Utils.buildParamsForSearch(UrlConstants.TW_SEARCH_URL, searchParams);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,urlWithParams , new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
                StringBuilder sb = new StringBuilder();
                InputStream stream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
                BufferedReader bReader = null;
                try {
                    bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
				try {
					JSONObject jsonObj = new JSONObject(sb.toString());
					requestFinish.OnSearchFinished(Tweet.jsonToTwitter(jsonObj.getJSONArray("statuses").toString()), true);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, error.toString() + error.getMessage());
				requestFinish.OnSearchFinished(new ArrayList<Tweet>(), false);
			}
		}){

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + accessToken);
				params.put("Content-Type", "application/json");

                return params;
            }
        };

		stringRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		mRequestQueue.add(stringRequest);
	}



	// convert a JSON authentication object into an Authenticated object
	public static Authenticated jsonToAuthenticated(String rawAuthorization) {
		Authenticated auth = null;
		if (rawAuthorization != null && rawAuthorization.length() > 0) {
			try {
				Gson gson = new Gson();
				auth = gson.fromJson(rawAuthorization, Authenticated.class);
			} catch (IllegalStateException ex) {
			}
		}
		return auth;
	}

	public void cancelAllQueue(){
		mRequestQueue.cancelAll(this);
	}
	
}