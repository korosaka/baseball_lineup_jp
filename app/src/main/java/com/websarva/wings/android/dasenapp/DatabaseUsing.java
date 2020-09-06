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
            putStartingPlayersInCache(orderType, orderNum);
        }

    }

    public void putSubPlayersInCache(int orderType) {

        CachedPlayersInfo.instance.clearSubArray(orderType);
        String selectQuery = "SELECT * FROM " + getSubTableName(orderType);
        SQLiteDatabase dbR = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = dbR.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                SubPlayerListItemData subPlayer =
                        new SubPlayerListItemData(
                                cursor.getInt(getColumnIndex(cursor, FixedWords.COLUMN_PLAYER_ID)),
                                translateDigitBool(cursor.getInt(getColumnIndex(cursor, FixedWords.COLUMN_IS_PITCHER))),
                                translateDigitBool(cursor.getInt(getColumnIndex(cursor, FixedWords.COLUMN_IS_BATTER))),
                                translateDigitBool(cursor.getInt(getColumnIndex(cursor, FixedWords.COLUMN_IS_RUNNER))),
                                translateDigitBool(cursor.getInt(getColumnIndex(cursor, FixedWords.COLUMN_IS_FIELDER))),
                                cursor.getString(getColumnIndex(cursor, FixedWords.COLUMN_NAME)));

                CachedPlayersInfo.instance.addSubMember(orderType, subPlayer);
            }
        } catch (Exception e) {
            Log.e(FixedWords.ERROR_LOG_TAG, FixedWords.ERROR_LOG_MESSAGE, e);
        } finally {
            dbR.close();
            if (cursor != null) cursor.close();
        }

    }

    private boolean translateDigitBool(int digitBool) {
        return digitBool == FixedWords.DIGIT_TRUE;
    }

    /**
     * DBから登録データ取得
     */
    public void putStartingPlayersInCache(int orderType, int orderNum) {

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
                " FROM " + getStartingTableName(orderType) +
                " WHERE " + FixedWords.COLUMN_ORDER_NUMBER + " = ?";
    }

    // TODO refactor
    private int getNameIndex(Cursor cursor) {
        return cursor.getColumnIndex(FixedWords.COLUMN_NAME);
    }

    // TODO refactor
    private int getPositionIndex(Cursor cursor) {
        return cursor.getColumnIndex(FixedWords.COLUMN_POSITION);
    }

    private int getColumnIndex(Cursor cursor, String columnName) {
        return cursor.getColumnIndex(columnName);
    }


    public void registerSubPlayer(
            int orderType, boolean isPitcher, boolean isBatter, boolean isRunner, boolean isFielder, String name) {

        SQLiteDatabase dbW = helper.getWritableDatabase();
        String insertQuery = makeSubInsertQuery(orderType);

        try {
            SQLiteStatement stmt = dbW.compileStatement(insertQuery);
            stmt.bindLong(1, translateBoolToDigit(isPitcher));
            stmt.bindLong(2, translateBoolToDigit(isBatter));
            stmt.bindLong(3, translateBoolToDigit(isRunner));
            stmt.bindLong(4, translateBoolToDigit(isFielder));
            stmt.bindString(5, name);

            stmt.executeInsert();
        } catch (Exception e) {
            Log.e(FixedWords.ERROR_LOG_TAG, FixedWords.ERROR_LOG_MESSAGE, e);
        } finally {
            dbW.close();
        }

    }

    private int translateBoolToDigit(boolean bool) {
        if (bool) return FixedWords.DIGIT_TRUE;
        return FixedWords.DIGIT_FALSE;
    }

    private String makeSubInsertQuery(int orderType) {
        return "INSERT INTO " +
                getSubTableName(orderType) + "(" +
                FixedWords.COLUMN_IS_PITCHER + ", " +
                FixedWords.COLUMN_IS_BATTER + ", " +
                FixedWords.COLUMN_IS_RUNNER + ", " +
                FixedWords.COLUMN_IS_FIELDER + ", " +
                FixedWords.COLUMN_NAME +
                ") VALUES(?,?,?,?,?)";
    }

    /**
     * store data in DB (delete → register)
     */
    public void registerStartingPlayer(int orderNum, String name, String position, int orderType) {

        SQLiteDatabase dbW = helper.getWritableDatabase();

        try {
            deleteStartingPlayer(orderType, orderNum, dbW);
            insertSqlData(orderType, orderNum, dbW, name, position);
        } catch (Exception e) {
            Log.e(FixedWords.ERROR_LOG_TAG, FixedWords.ERROR_LOG_MESSAGE, e);
        } finally {
            dbW.close();
        }
    }

    public void deleteSubPlayer(int orderType, int playerId) {
        SQLiteDatabase dbW = helper.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + getSubTableName(orderType) + " WHERE " + FixedWords.COLUMN_PLAYER_ID + " = ?";
        try {
            SQLiteStatement stmt = dbW.compileStatement(deleteQuery);
            stmt.bindLong(1, playerId);
            stmt.executeUpdateDelete();
        } catch (Exception e) {
            Log.e(FixedWords.ERROR_LOG_TAG, FixedWords.ERROR_LOG_MESSAGE, e);
        } finally {
            dbW.close();
        }
    }

    private void deleteStartingPlayer(int orderType, int orderNum, SQLiteDatabase dbW) {
        String sqlDelete = "DELETE FROM " + getStartingTableName(orderType) + " WHERE " + FixedWords.COLUMN_ORDER_NUMBER + " = ?";
        SQLiteStatement stmt = dbW.compileStatement(sqlDelete);
        stmt.bindLong(1, orderNum);
        stmt.executeUpdateDelete();
    }

    private String getStartingTableName(int orderType) {
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

    private String getSubTableName(int orderType) {
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


    private void insertSqlData(int orderType, int orderNum, SQLiteDatabase dbW, String name, String position) {
        String sqlInsert = makeInsertQuery(orderType);
        int indexOrderNum = 1;
        int indexPlayerName = 2;
        int indexPlayerPosition = 3;

        SQLiteStatement stmt = dbW.compileStatement(sqlInsert);
        stmt.bindLong(indexOrderNum, orderNum);
        stmt.bindString(indexPlayerName, name);
        stmt.bindString(indexPlayerPosition, position);

        stmt.executeInsert();
    }

    private String makeInsertQuery(int orderType) {
        return "INSERT INTO " +
                getStartingTableName(orderType) + "(" +
                FixedWords.COLUMN_ORDER_NUMBER + ", " +
                FixedWords.COLUMN_NAME + ", " +
                FixedWords.COLUMN_POSITION +
                ") VALUES(?,?,?)";
    }


}
