package com.example.max.contextlearning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Max on 3/5/2017.
 */

public class ContextDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Context.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ContextContract.VolumeEntry.TABLE_NAME + " (" +
            ContextContract.VolumeEntry._ID + " INTEGER PRIMARY KEY," +
            ContextContract.VolumeEntry.COLUMN_NAME_DATE + " TEXT," +
            ContextContract.VolumeEntry.COLUMN_NAME_NOISE + " INT, " +
            ContextContract.VolumeEntry.COLUMN_NAME_VOLUME + " INT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContextContract.VolumeEntry.TABLE_NAME;

    public ContextDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void add(String date, int noise, int volume) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContextContract.VolumeEntry.COLUMN_NAME_DATE, date);
        values.put(ContextContract.VolumeEntry.COLUMN_NAME_NOISE, noise);
        values.put(ContextContract.VolumeEntry.COLUMN_NAME_VOLUME, volume);

        db.insert(ContextContract.VolumeEntry.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> getAll() {
        SQLiteDatabase db = getWritableDatabase();

        String[] projection = {
                ContextContract.VolumeEntry._ID,
                ContextContract.VolumeEntry.COLUMN_NAME_DATE,
                ContextContract.VolumeEntry.COLUMN_NAME_NOISE,
                ContextContract.VolumeEntry.COLUMN_NAME_VOLUME
        };

        Cursor cursor = db.query(ContextContract.VolumeEntry.TABLE_NAME, projection,
                null, null, null, null, null);

        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_DATE))
                + "|" + cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_NOISE))
                + "|" + cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_VOLUME));
            items.add(item);
        }
        cursor.close();
        db.close();

        return items;
    }
}
