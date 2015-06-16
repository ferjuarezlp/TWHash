package com.ferjuarez.twhash.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ferjuarez on 12/06/15.
 */

public class Tweet {

    @SerializedName("id")
    private String id;

    @SerializedName("text")
    private String text;

    @SerializedName("in_reply_to_status_id")
    private String inReplyToStatus;

    @SerializedName("in_reply_to_user_id")
    private String inReplyToUser;

    @SerializedName("in_reply_to_screen_name")
    private String inReplyToScreenName;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("user")
    private TwitterUser user;

    public static Twitter jsonToTwitter(String result) {
        Twitter twits = null;
        if (result != null && result.length() > 0) {
            try {
                Gson gson = new Gson();
                twits = gson.fromJson(result, Twitter.class);
            } catch (IllegalStateException ex) {
            }
        }
        return twits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInReplyToStatus() {
        return inReplyToStatus;
    }

    public void setInReplyToStatus(String inReplyToStatus) {
        this.inReplyToStatus = inReplyToStatus;
    }

    public String getInReplyToUser() {
        return inReplyToUser;
    }

    public void setInReplyToUser(String inReplyToUser) {
        this.inReplyToUser = inReplyToUser;
    }

    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public TwitterUser getUser() {
        return user;
    }

    public void setUser(TwitterUser user) {
        this.user = user;
    }
}
