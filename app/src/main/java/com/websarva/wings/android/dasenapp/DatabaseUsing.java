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

        int players = FixedWords.NUMBER_OF_LINEUP_NORMAL;
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                break;
            case FixedWords.DH_ORDER:
                players = FixedWords.NUMBER_OF_LINEUP_DH;
                break;
        }

        for (int orderNum = 1; orderNum <= players; orderNum++) {
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
            // moveToNext(): if there isn't any data, return false
            if (cursor.moveToNext()) {
                playerName = cursor.getString(getNameIndex(cursor));
                playerPosition = cursor.getString(getPositionIndex(cursor));
                if (playerName.equals(FixedWords.EMPTY)) playerName = FixedWords.HYPHEN_5;
            } else {
                playerName = FixedWords.HYPHEN_5;
                playerPosition = FixedWords.HYPHEN_4;
            }
            setPlayerCachedInfo(orderType, orderNum, playerName, playerPosition);
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
     * 1つ(1人)ずつデータベースに登録されている情報をキャッシュ
     */
    private void setPlayerCachedInfo(int orderType, int orderNum, String name, String position) {
        switch (orderType) {
            case FixedWords.NORMAL_ORDER:
                CachedPlayerNamesInfo.instance.setNameNormal(orderNum, name);
                CachedPlayerPositionsInfo.instance.setPositionNormal(orderNum, position);
                break;
            case FixedWords.DH_ORDER:
                CachedPlayerNamesInfo.instance.setNameDh(orderNum, name);
                CachedPlayerPositionsInfo.instance.setPositionDh(orderNum, position);
                break;
        }

    }

    /**
     * store data in DB (delete → register)
     */
    public void registerInfo(int orderNum, String name, String position) {

        SQLiteDatabase dbW = helper.getWritableDatabase();
        int orderType = CurrentOrderVersion.instance.getCurrentVersion();

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
