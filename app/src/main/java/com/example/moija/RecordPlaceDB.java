package com.example.moija;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordPlaceDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recordPlace_DB.db";
    private static final int DATABASE_VERSION = 1;

    public RecordPlaceDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE recordPlace_DB (time TEXT, startPlace TEXT, endPlace TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recordPlace_DB");
        onCreate(db);
    }
}
