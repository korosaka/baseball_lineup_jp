package com.websarva.wings.android.dasenapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //データベースファイル名の定数フィールド
    private static final String DATABASE_NAME = "lineup.db";
    //バージョン情報の定数フィールド
    private static final int DATABASE_VERSION = 1;

    //コンストラクタ
    public DatabaseHelper(Context context){
        //親クラスのコンストラクタ
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //テーブル作成用SQL文字列作成
        //テーブルの作成は最初の一度きり
        //一度テーブルを作るとその名前のテーブルは最初の形で残り、カラムの名前などを上書きするとエラーになる！
        //だからテーブルの内容変えたら、テーブルの名前を変えるorアプリを消去等して、別のテーブルとして作り直す！！
        String sql = "CREATE TABLE lineup(_id INTEGER PRIMARY KEY, number INTEGER, playerName TEXT,position TEXT);";
        //実行
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }
}
