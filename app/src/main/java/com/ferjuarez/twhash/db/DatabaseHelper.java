package com.ferjuarez.twhash.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ferjuarez.twhash.models.HashTag;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ferjuarez on 11/06/15.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
 
    private static final String DATABASE_NAME = "twhash";
    private static final int DATABASE_VERSION = 2;
 
    /**
     * The data access object used to interact with the Sqlite database to do C.R.U.D operations.
     */
    private Dao<HashTag, Long> hashTagDao;
 
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
 
            /**
             * creates the HashTag database table
             */
            TableUtils.createTable(connectionSource, HashTag.class);
 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            /**
             * Recreates the database when onUpgrade is called by the framework
             */
            TableUtils.dropTable(connectionSource, HashTag.class, false);
            onCreate(database, connectionSource);
 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns an instance of the data access object
     * @return
     * @throws SQLException
     */
    public Dao<HashTag, Long> getHashTagDao() throws SQLException {
        if(hashTagDao == null) {
            hashTagDao = getDao(HashTag.class);
        }
        return hashTagDao;
    }


    public List<String> getPersistedHashTags() throws SQLException {
        List<String> result = new ArrayList<>();
        List<HashTag> list = getHashTagDao().queryForAll();
        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i).getValue());
        }
        return result;
    }

    @Override
    public void close() {
        super.close();
        hashTagDao = null;
    }

}