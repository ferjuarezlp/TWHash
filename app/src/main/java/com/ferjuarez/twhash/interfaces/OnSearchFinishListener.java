package com.ferjuarez.twhash.interfaces;

import com.ferjuarez.twhash.models.Tweet;

import java.util.List;

public interface OnSearchFinishListener {
    public abstract void OnSearchFinished(List<Tweet> tweets, Boolean state);
}