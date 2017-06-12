package com.max.app.contextlearning.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TrainingSetDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ContextTrainingSet.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TrainingSetContract.TrainingSet.TABLE_NAME + " (" +
                    TrainingSetContract.TrainingSet._ID + " INTEGER PRIMARY KEY," +
                    TrainingSetContract.TrainingSet.COLUMN_NAME_LABEL + " TEXT," +
                    TrainingSetContract.TrainingSet.COLUMN_NAME_LIGHT + " TEXT, " +
                    TrainingSetContract.TrainingSet.COLUMN_NAME_PROXIMITY + " TEXT, " +
                    TrainingSetContract.TrainingSet.COLUMN_NAME_NOISE + " TEXT, " +
                    TrainingSetContract.TrainingSet.COLUMN_NAME_GRAVITY + " TEXT, " +
                    TrainingSetContract.TrainingSet.COLUMN_NAME_ACCELERATION + " TEXT, " +
                    TrainingSetContract.TrainingSet.COLUMN_NAME_DEVICE_TMP + " TEXT, " +
                    TrainingSetContract.TrainingSet.COLUMN_NAME_LOCATION + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TrainingSetContract.TrainingSet.TABLE_NAME;

    public TrainingSetDbHelper(Context context) {
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

    public void add(ArrayList<String> data) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TrainingSetContract.TrainingSet.COLUMN_NAME_LABEL, data.get(0));
        values.put(TrainingSetContract.TrainingSet.COLUMN_NAME_LIGHT, data.get(1));
        values.put(TrainingSetContract.TrainingSet.COLUMN_NAME_PROXIMITY, data.get(2));
        values.put(TrainingSetContract.TrainingSet.COLUMN_NAME_NOISE, data.get(3));
        values.put(TrainingSetContract.TrainingSet.COLUMN_NAME_GRAVITY, data.get(4));
        values.put(TrainingSetContract.TrainingSet.COLUMN_NAME_ACCELERATION, data.get(5));
        values.put(TrainingSetContract.TrainingSet.COLUMN_NAME_DEVICE_TMP, data.get(6));
        values.put(TrainingSetContract.TrainingSet.COLUMN_NAME_LOCATION, data.get(7));

        db.insert(TrainingSetContract.TrainingSet.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> getAll() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                TrainingSetContract.TrainingSet._ID,
                TrainingSetContract.TrainingSet.COLUMN_NAME_LABEL,
                TrainingSetContract.TrainingSet.COLUMN_NAME_LIGHT,
                TrainingSetContract.TrainingSet.COLUMN_NAME_PROXIMITY,
                TrainingSetContract.TrainingSet.COLUMN_NAME_NOISE,
                TrainingSetContract.TrainingSet.COLUMN_NAME_GRAVITY,
                TrainingSetContract.TrainingSet.COLUMN_NAME_ACCELERATION,
                TrainingSetContract.TrainingSet.COLUMN_NAME_DEVICE_TMP,
                TrainingSetContract.TrainingSet.COLUMN_NAME_LOCATION
        };

        Cursor cursor = db.query(TrainingSetContract.TrainingSet.TABLE_NAME, projection,
                null, null, null, null, null);
        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            String item = "";
            for (int i = 0; i < cursor.getColumnCount(); i++)
                if (i == cursor.getColumnCount() - 1)
                    item += cursor.getColumnName(i) + " : " + cursor.getString(i);
                else
                    item += cursor.getColumnName(i) + " : " + cursor.getString(i) + " | ";
            items.add(item);
        }
        cursor.close();
        db.close();

        return items;
    }

    public ArrayList<String> getAll(String label) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                TrainingSetContract.TrainingSet._ID,
                TrainingSetContract.TrainingSet.COLUMN_NAME_LABEL,
                TrainingSetContract.TrainingSet.COLUMN_NAME_LIGHT,
                TrainingSetContract.TrainingSet.COLUMN_NAME_PROXIMITY,
                TrainingSetContract.TrainingSet.COLUMN_NAME_NOISE,
                TrainingSetContract.TrainingSet.COLUMN_NAME_GRAVITY,
                TrainingSetContract.TrainingSet.COLUMN_NAME_ACCELERATION,
                TrainingSetContract.TrainingSet.COLUMN_NAME_DEVICE_TMP,
                TrainingSetContract.TrainingSet.COLUMN_NAME_LOCATION
        };

        String selection =  TrainingSetContract.TrainingSet.COLUMN_NAME_LABEL + " = ?";
        String[] selectionArgs = { label + "" };

        Cursor cursor = db.query(TrainingSetContract.TrainingSet.TABLE_NAME, projection,
                selection, selectionArgs, null, null, null);
        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            String item = "";
            for (int i = 0; i < cursor.getColumnCount(); i++)
                if (i == cursor.getColumnCount() - 1)
                    item += cursor.getString(i);
                else
                    item += cursor.getString(i) + "|";
            items.add(item);
        }
        cursor.close();
        db.close();

        return items;
    }

    public void deleteAll() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
