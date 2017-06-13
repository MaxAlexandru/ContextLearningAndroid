package com.max.app.contextlearning.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataSetDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ContextLearning.db";

    private static final String SQL_CREATE_ENTRIES_RAW =
            "CREATE TABLE " +
                    DataSetDbContract.RawEntries.TABLE_NAME + " (" +
                    DataSetDbContract.RawEntries.COLUMN_NAME_TIME + " INTEGER PRIMARY KEY," +
                    DataSetDbContract.RawEntries.COLUMN_NAME_SENSORS + " TEXT)";
    private static final String SQL_DELETE_ENTRIES_RAW =
            "DROP TABLE IF EXISTS " +
                    DataSetDbContract.RawEntries.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_LABELED =
            "CREATE TABLE " +
                    DataSetDbContract.LabeledEntries.TABLE_NAME + " (" +
                    DataSetDbContract.LabeledEntries._ID + " INTEGER PRIMARY KEY," +
                    DataSetDbContract.LabeledEntries.COLUMN_NAME_SENSORS + " TEXT," +
                    DataSetDbContract.LabeledEntries.COLUMN_NAME_LABEL + " TEXT)";
    private static final String SQL_DELETE_ENTRIES_LABELED =
            "DROP TABLE IF EXISTS " +
                    DataSetDbContract.LabeledEntries.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_ACTIVITY =
            "CREATE TABLE " +
                    DataSetDbContract.ActivityEntries.TABLE_NAME + " (" +
                    DataSetDbContract.ActivityEntries.COLUMN_NAME_TIME + " INTEGER PRIMARY KEY," +
                    DataSetDbContract.ActivityEntries.COLUMN_NAME_LABEL + " TEXT)";
    private static final String SQL_DELETE_ENTRIES_ACTIVITY =
            "DROP TABLE IF EXISTS " +
                    DataSetDbContract.ActivityEntries.TABLE_NAME;

    public DataSetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_RAW);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_LABELED);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_ACTIVITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_RAW);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_LABELED);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_ACTIVITY);
        onCreate(sqLiteDatabase);
    }

    public void addRaw(long time, String sensors) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataSetDbContract.RawEntries.COLUMN_NAME_TIME, time);
        values.put(DataSetDbContract.RawEntries.COLUMN_NAME_SENSORS, sensors);

        db.insert(DataSetDbContract.RawEntries.TABLE_NAME, null, values);
        db.close();
    }

    public void addLabeled(String sensors, String label) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataSetDbContract.LabeledEntries.COLUMN_NAME_SENSORS, sensors);
        values.put(DataSetDbContract.LabeledEntries.COLUMN_NAME_LABEL, label);

        db.insert(DataSetDbContract.LabeledEntries.TABLE_NAME, null, values);
        db.close();
    }

    public void addActivity(long time, String label) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataSetDbContract.ActivityEntries.COLUMN_NAME_TIME, time);
        values.put(DataSetDbContract.ActivityEntries.COLUMN_NAME_LABEL, label);

        db.insert(DataSetDbContract.ActivityEntries.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> getAllRaw() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                DataSetDbContract.RawEntries.COLUMN_NAME_TIME,
                DataSetDbContract.RawEntries.COLUMN_NAME_SENSORS
        };
        Cursor cursor = db.query(DataSetDbContract.RawEntries.TABLE_NAME, projection,
                null, null, null, null, null);
        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext())
            items.add(cursor.getString(0) + "=" + cursor.getString(1));
        cursor.close();
        db.close();

        return items;
    }

    public ArrayList<String> getAllLabeled() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                DataSetDbContract.LabeledEntries.COLUMN_NAME_SENSORS,
                DataSetDbContract.LabeledEntries.COLUMN_NAME_LABEL
        };
        Cursor cursor = db.query(DataSetDbContract.LabeledEntries.TABLE_NAME, projection,
                null, null, null, null, null);
        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext())
            items.add(cursor.getString(0) + "=" + cursor.getString(1));
        cursor.close();
        db.close();

        return items;
    }

    public ArrayList<String> getAllActivity() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                DataSetDbContract.ActivityEntries.COLUMN_NAME_TIME,
                DataSetDbContract.ActivityEntries.COLUMN_NAME_LABEL
        };
        Cursor cursor = db.query(DataSetDbContract.ActivityEntries.TABLE_NAME, projection,
                null, null, null, null, null);
        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext())
            items.add(cursor.getString(0) + "=" + cursor.getString(1));
        cursor.close();
        db.close();

        return items;
    }

    public void deleteAllRaw() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_RAW);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_RAW);
    }

    public void deleteAllLabeled() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_LABELED);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_LABELED);
    }

    public void deleteAllActivity() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_ACTIVITY);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_ACTIVITY);
    }

    public ArrayList<String> getAllRawBetween(long from, long to) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                DataSetDbContract.RawEntries.COLUMN_NAME_TIME,
                DataSetDbContract.RawEntries.COLUMN_NAME_SENSORS
        };

        String selection =  DataSetDbContract.RawEntries.COLUMN_NAME_TIME + " BETWEEN ? AND ?";
        String[] selectionArgs = { String.valueOf(from), String.valueOf(to) };

        Cursor cursor = db.query(DataSetDbContract.RawEntries.TABLE_NAME, projection,
                selection, selectionArgs, null, null, null);
        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext())
            items.add(cursor.getString(0) + "=" + cursor.getString(1));
        cursor.close();
        db.close();

        return items;
    }

    public String getLastRaw() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                DataSetDbContract.RawEntries.COLUMN_NAME_TIME,
                DataSetDbContract.RawEntries.COLUMN_NAME_SENSORS
        };

        String sortOrder = DataSetDbContract.RawEntries.COLUMN_NAME_TIME + " DESC";

        Cursor cursor = db.query(DataSetDbContract.RawEntries.TABLE_NAME, projection,
                null, null, null, null, sortOrder);
        String item = "";
        cursor.moveToNext();
        if (!cursor.isAfterLast())
            item += cursor.getString(0) + "=" + cursor.getString(1);
        cursor.close();
        db.close();

        return item;
    }

}
