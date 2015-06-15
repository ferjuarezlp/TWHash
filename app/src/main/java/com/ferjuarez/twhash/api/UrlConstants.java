package com.ferjuarez.twhash.api;

/**
 * Created by ferjuarez on 11/06/15.
 */

public class UrlConstants {
    final public static String CONSUMER_KEY = "4ifhRQIptsgoNWWYzBOfA8laa";
    final public static String CONSUMER_SECRET = "RkQyjaqJjtQ6aNOZEKKEHZXWFkPd94ykjnXR8tZQ91ZuiLY7dz";

    final public static String TW_BASE_URL = "https://api.twitter.com/";
    final public static String TW_API_VERSION = "1.1";

    final public static String TW_TOKEN_URL = TW_BASE_URL + "oauth2/token";
    final public static String TW_TIMELINE_URL = TW_BASE_URL + TW_API_VERSION + "/statuses/user_timeline.json?screen_name=";
    final public static String TW_SEARCH_URL = TW_BASE_URL + TW_API_VERSION + "/search/tweets.json";

}