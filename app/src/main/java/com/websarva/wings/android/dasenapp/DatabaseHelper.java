package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * when we update a table or add new a table, this version(number) must be changed
     */
    private static final int DATABASE_VERSION = 2;
    private static final int PREVIOUS_DB_VERSION = 1;
    // TODO refactor
    private static final String CREATE_NORMAL_ORDER_TABLE =
            "CREATE TABLE " +
                    FixedWords.NORMAL_ORDER_TABLE + "(" +
                    FixedWords.COLUMN_ORDER_NUMBER + " INTEGER PRIMARY KEY, " +
                    FixedWords.COLUMN_NAME + " TEXT, " +
                    FixedWords.COLUMN_POSITION + " TEXT);";
    private static final String CREATE_DH_ORDER_TABLE =
            "CREATE TABLE " +
                    FixedWords.DH_ORDER_TABLE + "(" +
                    FixedWords.COLUMN_ORDER_NUMBER + " INTEGER PRIMARY KEY, " +
                    FixedWords.COLUMN_NAME + " TEXT, " +
                    FixedWords.COLUMN_POSITION + " TEXT);";

    private static final String CREATE_NORMAL_SUB_MEMBERS_TABLE =
            "CREATE TABLE " +
                    FixedWords.NORMAL_SUB_TABLE + "(" +
                    FixedWords.COLUMN_LIST_POSITION + " INTEGER PRIMARY KEY, " +
                    FixedWords.COLUMN_IS_PITCHER + " INTEGER DEFAULT 0, " +
                    FixedWords.COLUMN_IS_BATTER + " INTEGER DEFAULT 0, " +
                    FixedWords.COLUMN_IS_RUNNER + " INTEGER DEFAULT 0, " +
                    FixedWords.COLUMN_IS_FIELDER + " INTEGER DEFAULT 0, " +
                    FixedWords.COLUMN_NAME + " TEXT);";

    private static final String CREATE_DH_SUB_MEMBERS_TABLE =
            "CREATE TABLE " +
                    FixedWords.DH_SUB_TABLE + "(" +
                    FixedWords.COLUMN_LIST_POSITION + " INTEGER PRIMARY KEY, " +
                    FixedWords.COLUMN_IS_PITCHER + " INTEGER DEFAULT 0, " +
                    FixedWords.COLUMN_IS_BATTER + " INTEGER DEFAULT 0, " +
                    FixedWords.COLUMN_IS_RUNNER + " INTEGER DEFAULT 0, " +
                    FixedWords.COLUMN_IS_FIELDER + " INTEGER DEFAULT 0, " +
                    FixedWords.COLUMN_NAME + " TEXT);";



    public DatabaseHelper(Context context) {
        super(context, FixedWords.DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creating tables is done only once
     * if you want add columns to a table, need to create another table newly
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NORMAL_ORDER_TABLE);
        db.execSQL(CREATE_DH_ORDER_TABLE);
        db.execSQL(CREATE_NORMAL_SUB_MEMBERS_TABLE);
        db.execSQL(CREATE_DH_SUB_MEMBERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == PREVIOUS_DB_VERSION) deleteOldTable(db);
        db.execSQL(CREATE_NORMAL_ORDER_TABLE);
        db.execSQL(CREATE_DH_ORDER_TABLE);
        db.execSQL(CREATE_NORMAL_SUB_MEMBERS_TABLE);
        db.execSQL(CREATE_DH_SUB_MEMBERS_TABLE);
    }

    private void deleteOldTable(SQLiteDatabase db) {
        String deleteSql = "DROP TABLE IF EXISTS " + FixedWords.OLD_ORDER_TABLE + ";";
        db.execSQL(deleteSql);
    }
}
