package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseUsing {

    private DatabaseHelper helper;

    public DatabaseUsing(Context context) {
        //データベースからデータを取り出してきて表示する処理
        //データベースヘルパーオブジェクト生成
        helper = new DatabaseHelper(context);
    }

    /**
     * スタメン情報を取得
     *
     * @param k 1 -> DH無し(9)
     *          2 -> DH制(10)
     *          3 -> 全員打(10)
     *          4 -> 全員打(11)
     *          5 -> 全員打(12)
     *          6 -> 全員打(13)
     *          7 -> 全員打(14)
     *          8 -> 全員打(15)
     */
    public void getPlayersInfo(int k) {

        int players = 9;
        switch (k) {
            case FixedWords.DEFAULT:
                players = 9;
                break;
            case FixedWords.DH:
                players = 10;
                break;
            case FixedWords.ALL10:
                players = 10;
                break;
            case FixedWords.ALL11:
                players = 11;
                break;
            case FixedWords.ALL12:
                players = 12;
                break;
            case FixedWords.ALL13:
                players = 13;
                break;
            case FixedWords.ALL14:
                players = 14;
                break;
            case FixedWords.ALL15:
                players = 15;
                break;
        }

        for (int j = 0; j < players; j++) {
            getDatabaseInfo(k, j);
        }
    }

    /**
     * DBから登録データ取得
     */
    public void getDatabaseInfo(int version, int num) {

        String playerName;
        String playerPosition;

        //ヘルパーから接続オブジェクト取得
        SQLiteDatabase dbR = helper.getReadableDatabase();
        try {
            //SQL文字列作成➡︎検索・表示
            //SELECT文の文字列作成
            String sqlSelect = "SELECT playerName,position FROM lineup WHERE number = " + (version * 100 + num);
            //sql実行（カーソルオブジェクト（実行結果そのもの？）が戻り値）
            Cursor cursor = dbR.rawQuery(sqlSelect, null);
            //cursor.moveToNext() ➡︎データない時（移動ができなかった時）はfalseになる
            if (cursor.moveToNext()) {
                //カラムのインデックス値取得
                int idxName = cursor.getColumnIndex("playerName");
                int idxPosition = cursor.getColumnIndex("position");
                //カラムのインデックス値を元に実際のデータ取得
                playerName = cursor.getString(idxName);
                playerPosition = cursor.getString(idxPosition);
                //名前の空白登録は未登録にする
                if (playerName.equals("")) {
                    playerName = "-----";
                }
            }
            //データが無かった時
            else {
                playerName = "-----";
                playerPosition = "----";
            }
            setPlayerCachedInfo(version, num, playerName, playerPosition);
        }
        //catchないとエラー出る　
        catch (Exception e) {
            //エラーメッセージ出す
            Log.e("キャッチ", "エラー", e);
        } finally {
            //解放
            dbR.close();
        }

    }

    /**
     * 1つ(1人)ずつデータベースに登録されている情報をキャッシュ
     *
     * @param version
     * @param num
     * @param name
     * @param position
     */
    private void setPlayerCachedInfo(int version, int num, String name, String position) {
        switch (version) {
            case FixedWords.DEFAULT:
                CachedPlayerNamesInfo.instance.setNameNormal(num, name);
                CachedPlayerPositionsInfo.instance.setPositionNormal(num, position);
                break;
            case FixedWords.DH:
                CachedPlayerNamesInfo.instance.setNameDh(num, name);
                CachedPlayerPositionsInfo.instance.setPositionDh(num, position);
                break;
            case FixedWords.ALL10:
                CachedPlayerNamesInfo.instance.setNameAll10(num, name);
                CachedPlayerPositionsInfo.instance.setPositionAll10(num, position);
                break;
            case FixedWords.ALL11:
                CachedPlayerNamesInfo.instance.setNameAll11(num, name);
                CachedPlayerPositionsInfo.instance.setPositionAll11(num, position);
                break;
            case FixedWords.ALL12:
                CachedPlayerNamesInfo.instance.setNameAll12(num, name);
                CachedPlayerPositionsInfo.instance.setPositionAll12(num, position);
                break;
            case FixedWords.ALL13:
                CachedPlayerNamesInfo.instance.setNameAll13(num, name);
                CachedPlayerPositionsInfo.instance.setPositionAll13(num, position);
                break;
            case FixedWords.ALL14:
                CachedPlayerNamesInfo.instance.setNameAll14(num, name);
                CachedPlayerPositionsInfo.instance.setPositionAll14(num, position);
                break;
            case FixedWords.ALL15:
                CachedPlayerNamesInfo.instance.setNameAll15(num, name);
                CachedPlayerPositionsInfo.instance.setPositionAll15(num, position);
                break;
        }

    }

    /**
     * データベースへ登録処理(削除→登録)
     */
    public void setDatabaseInfo(int num, String name, String position) {

        SQLiteDatabase dbW = helper.getWritableDatabase();
        int currentVersion = CurrentOrderVersion.instance.getCurrentVersion();

        try {
            //今あるデータ削除➡その後インサート

            deleteSqlData(currentVersion, num, dbW);
            insertSqlData(currentVersion, num, dbW, name, position);
        } catch (Exception e) {
            Log.d("error", "例外発生");
        } finally {
            //データ接続プブジェクト解放
            dbW.close();
        }
    }

    /**
     * 削除処理
     * @param version
     * @param num
     * @param dbW
     */
    private void deleteSqlData(int version, int num, SQLiteDatabase dbW) {
        //削除用文字列
        String sqlDelete = "DELETE FROM lineup WHERE number = ?";
        //上記文字列からPreparedStatement取得（SQLを実行するためのインターフェース）
        SQLiteStatement stmt = dbW.compileStatement(sqlDelete);
        //変数バインド（数字は何番目の？に入れるか,？に入れるもの）（kはオプション選択によって変わる）
        stmt.bindLong(1, version * 100 + num);
        //削除SQL実行
        stmt.executeUpdateDelete();
    }

    /**
     * 登録処理
     * @param version
     * @param num
     * @param dbW
     * @param name
     * @param position
     */
    private void insertSqlData(int version, int num, SQLiteDatabase dbW, String name, String position) {
        //インサート用文字
        String sqlInsert = "INSERT INTO lineup(_id, number, playerName, position) VALUES(?,?,?,?)";
        //PreparedStatement取得 (stmtは削除で使ったものの再利用)
        SQLiteStatement stmt = dbW.compileStatement(sqlInsert);
        //_id(primary key) は設定不要らしい
        stmt.bindLong(2, version * 100 + num);
        stmt.bindString(3, name);
        stmt.bindString(4, position);
        //インサートSQL実行
        stmt.executeInsert();
    }


}
