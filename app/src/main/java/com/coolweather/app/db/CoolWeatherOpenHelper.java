package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_PROVINCE="CREATE TABLE Province(" +
            "id integer primary key autoincrement," +
            "name text," +
            "code text)";

    private static final String CREATE_CITY="create table city(" +
            "id integer primary key autoincrement," +
            "name text," +
            "code text," +
            "province_id integer)";

    private static final String CREATE_COUNTY="create table county(" +
            "id integer primary key autoincrement," +
            "name text," +
            "code text," +
            "city_id integer)";

    public CoolWeatherOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
