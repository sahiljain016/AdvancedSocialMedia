package com.gic.memorableplaces.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(@Nullable Context context) {
        super(context, "filters.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public void createUserTable(String NEW_TABLE_SYNTAX) {
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL(NEW_TABLE_SYNTAX);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addRow(ContentValues cv, String TableName) {
        SQLiteDatabase db = getWritableDatabase();

        long insert = db.insert(TableName, null, cv);

        return insert != -1;

    }
}
