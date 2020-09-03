package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseUsing {

    private DatabaseHelper helper;

    public DatabaseUsing(Context context) {
        helper = new DatabaseHelper(context);
    }

    public void getPlayersInfo(int orderType) {

        int numberOfPlayers = FixedWords.NUMBER_OF_LINEUP_NORMAL;
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                break;
            case FixedWords.DH_ORDER:
                numberOfPlayers = FixedWords.NUMBER_OF_LINEUP_DH;
                break;
        }

        for (int orderNum = 1; orderNum <= numberOfPlayers; orderNum++) {
            getDatabaseInfo(orderType, orderNum);
        }

    }

    /**
     * DBから登録データ取得
     */
    public void getDatabaseInfo(int orderType, int orderNum) {

        String playerName;
        String playerPosition;
        String sqlSelect = makeSelectQuery(orderType);
        SQLiteDatabase dbR = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = dbR.rawQuery(sqlSelect, new String[]{String.valueOf(orderNum)});
            // if there isn't any data, return false
            if (cursor.moveToNext()) {
                playerName = cursor.getString(getNameIndex(cursor));
                playerPosition = cursor.getString(getPositionIndex(cursor));
                if (playerName.equals(FixedWords.EMPTY)) playerName = FixedWords.HYPHEN_5;
            } else {
                playerName = FixedWords.HYPHEN_5;
                playerPosition = FixedWords.HYPHEN_4;
            }
            CachedPlayersInfo.instance.setPlayerInfoToCache(orderType, orderNum, playerPosition, playerName);
        } catch (Exception e) {
            Log.e(FixedWords.ERROR_LOG_TAG, FixedWords.ERROR_LOG_MESSAGE, e);
        } finally {
            dbR.close();
            if (cursor != null) cursor.close();
        }
    }

    private String makeSelectQuery(int orderType) {
        return "SELECT " + FixedWords.COLUMN_NAME + ", " + FixedWords.COLUMN_POSITION +
                " FROM " + getTableName(orderType) +
                " WHERE " + FixedWords.COLUMN_ORDER_NUMBER + " = ?";
    }

    private int getNameIndex(Cursor cursor) {
        return cursor.getColumnIndex(FixedWords.COLUMN_NAME);
    }

    private int getPositionIndex(Cursor cursor) {
        return cursor.getColumnIndex(FixedWords.COLUMN_POSITION);
    }


    /**
     * store data in DB (delete → register)
     */
    public void registerInfo(int orderNum, String name, String position, int orderType) {

        SQLiteDatabase dbW = helper.getWritableDatabase();

        try {
            deleteSqlData(orderType, orderNum, dbW);
            insertSqlData(orderType, orderNum, dbW, name, position);
        } catch (Exception e) {
            Log.e(FixedWords.ERROR_LOG_TAG, FixedWords.ERROR_LOG_MESSAGE, e);
        } finally {
            dbW.close();
        }
    }

    private void deleteSqlData(int orderType, int orderNum, SQLiteDatabase dbW) {
        String sqlDelete = "DELETE FROM " + getTableName(orderType) + " WHERE " + FixedWords.COLUMN_ORDER_NUMBER + " = ?";
        SQLiteStatement stmt = dbW.compileStatement(sqlDelete);
        stmt.bindLong(1, orderNum);
        stmt.executeUpdateDelete();
    }

    private String getTableName(int orderType) {
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


    private void insertSqlData(int orderType, int orderNum, SQLiteDatabase dbW, String name, String position) {
        String sqlInsert = makeInsertQuery(orderType);

        // TODO refactor (number)
        SQLiteStatement stmt = dbW.compileStatement(sqlInsert);
        stmt.bindLong(1, orderNum);
        stmt.bindString(2, name);
        stmt.bindString(3, position);

        stmt.executeInsert();
    }

    private String makeInsertQuery(int orderType) {
        return "INSERT INTO " +
                getTableName(orderType) + "(" +
                FixedWords.COLUMN_ORDER_NUMBER + ", " +
                FixedWords.COLUMN_NAME + ", " +
                FixedWords.COLUMN_POSITION +
                ") VALUES(?,?,?)";
    }


}
