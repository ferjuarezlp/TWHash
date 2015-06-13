package com.ferjuarez.twhash.api;

/**
 * Created by ferjuarez on 11/06/15.
 */

public interface OnAuthFinishListener {
    public abstract void onAuthFinished(Authenticated authenticated, Boolean state);
}