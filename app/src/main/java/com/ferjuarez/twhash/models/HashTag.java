package com.ferjuarez.twhash.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ferjuarez on 11/06/15.
 */

@DatabaseTable(tableName = "hashtag")
public class HashTag {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    private String value;

    public HashTag(){
        // needed by OrmLite
    }

    public HashTag(String value){
        this.value = value;
    }



}
