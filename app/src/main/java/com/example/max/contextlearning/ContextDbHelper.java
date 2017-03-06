package com.example.max.contextlearning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

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
        String date = currentTimeMillis() + "";

        ContentValues values = new ContentValues();
        for (int i = 1; i <= 7; i++) {
            values.put(ContextContract.VolumeEntry.COLUMN_NAME_DATE, date);
            values.put(ContextContract.VolumeEntry.COLUMN_NAME_NOISE, 1000 + (i - 1) * 1500);
            values.put(ContextContract.VolumeEntry.COLUMN_NAME_VOLUME, i);
            sqLiteDatabase.insert(ContextContract.VolumeEntry.TABLE_NAME, null, values);
        }
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
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                ContextContract.VolumeEntry._ID,
                ContextContract.VolumeEntry.COLUMN_NAME_DATE,
                ContextContract.VolumeEntry.COLUMN_NAME_NOISE,
                ContextContract.VolumeEntry.COLUMN_NAME_VOLUME
        };

        String sortOrder = ContextContract.VolumeEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(ContextContract.VolumeEntry.TABLE_NAME, projection,
                null, null, null, null, sortOrder);
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

    public String getLatest() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                ContextContract.VolumeEntry._ID,
                ContextContract.VolumeEntry.COLUMN_NAME_DATE,
                ContextContract.VolumeEntry.COLUMN_NAME_NOISE,
                ContextContract.VolumeEntry.COLUMN_NAME_VOLUME
        };

        String item = null;

        String sortOrder = ContextContract.VolumeEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(ContextContract.VolumeEntry.TABLE_NAME, projection,
                null, null, null, null, sortOrder);
        cursor.moveToNext();
        if (!cursor.isAfterLast())
            item = cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_DATE))
                + "|" + cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_NOISE))
                + "|" + cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_VOLUME));
        cursor.close();
        db.close();

        return item;
    }

    public String getLatestOnVolume(int vol) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                ContextContract.VolumeEntry._ID,
                ContextContract.VolumeEntry.COLUMN_NAME_DATE,
                ContextContract.VolumeEntry.COLUMN_NAME_NOISE,
                ContextContract.VolumeEntry.COLUMN_NAME_VOLUME
        };

        String item = null;

        String selection =  ContextContract.VolumeEntry.COLUMN_NAME_VOLUME + " = ?";
        String[] selectionArgs = { vol + "" };
        String sortOrder = ContextContract.VolumeEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(ContextContract.VolumeEntry.TABLE_NAME, projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.moveToNext();
        if (!cursor.isAfterLast())
            item = cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_DATE))
                    + "|" + cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_NOISE))
                    + "|" + cursor.getString(cursor.getColumnIndex(ContextContract.VolumeEntry.COLUMN_NAME_VOLUME));
        cursor.close();
        db.close();

        return item;
    }

    public void updateOnDate(String date, int vol) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContextContract.VolumeEntry.COLUMN_NAME_VOLUME, vol);

        String selection = ContextContract.VolumeEntry.COLUMN_NAME_DATE + " LIKE ?";
        String[] selectionArgs = { date };

        int count = db.update(
                ContextContract.VolumeEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();
    }

    public void updateOnVol(int vol, String date, int noise) {
        String latest = getLatestOnVolume(vol);
        String[] cols = latest.split("\\|");
        int prevNoise = Integer.parseInt(cols[1]);

        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContextContract.VolumeEntry.COLUMN_NAME_DATE, date);
        values.put(ContextContract.VolumeEntry.COLUMN_NAME_NOISE, (noise + prevNoise) / 2);

        String selection = ContextContract.VolumeEntry.COLUMN_NAME_VOLUME + " LIKE ?";
        String[] selectionArgs = { vol + "" };

        int count = db.update(
                ContextContract.VolumeEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();
    }
}
