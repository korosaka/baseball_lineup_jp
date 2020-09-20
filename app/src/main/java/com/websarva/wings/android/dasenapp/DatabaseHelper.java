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

    private final String CREATE_NORMAL_ORDER_TABLE = makeCreateStartingOrderQuery(FixedWords.NORMAL_ORDER);
    private final String CREATE_DH_ORDER_TABLE = makeCreateStartingOrderQuery(FixedWords.DH_ORDER);
    private final String CREATE_NORMAL_SUB_MEMBERS_TABLE = makeCreateSubMemberQuery(FixedWords.NORMAL_ORDER);
    private final String CREATE_DH_SUB_MEMBERS_TABLE = makeCreateSubMemberQuery(FixedWords.DH_ORDER);

    private String makeCreateStartingOrderQuery(int orderType) {
        return "CREATE TABLE " +
                getStartingTableName(orderType) + "(" +
                FixedWords.COLUMN_ORDER_NUMBER + " INTEGER PRIMARY KEY, " +
                FixedWords.COLUMN_NAME + " TEXT, " +
                FixedWords.COLUMN_POSITION + " TEXT);";
    }

    private String makeCreateSubMemberQuery(int orderType) {
        return "CREATE TABLE " +
                getSubTableName(orderType) + "(" +
                FixedWords.COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FixedWords.COLUMN_IS_PITCHER + " INTEGER DEFAULT 0, " +
                FixedWords.COLUMN_IS_BATTER + " INTEGER DEFAULT 0, " +
                FixedWords.COLUMN_IS_RUNNER + " INTEGER DEFAULT 0, " +
                FixedWords.COLUMN_IS_FIELDER + " INTEGER DEFAULT 0, " +
                FixedWords.COLUMN_NAME + " TEXT);";
    }

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

    public String getStartingTableName(int orderType) {
        String tableName = FixedWords.NORMAL_ORDER_TABLE;
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                break;
            case FixedWords.DH_ORDER:
                tableName = FixedWords.DH_ORDER_TABLE;
                break;
        }
        return tableName;
    }

    public String getSubTableName(int orderType) {
        String tableName = FixedWords.NORMAL_SUB_TABLE;
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                break;
            case FixedWords.DH_ORDER:
                tableName = FixedWords.DH_SUB_TABLE;
                break;
        }
        return tableName;
    }

}
