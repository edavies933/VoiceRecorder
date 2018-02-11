package com.example.emda.soundrecorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Comparator;

/**
 * Created by emda on 2/11/2018.
 */

public class DatabaseHelperMethod extends SQLiteOpenHelper {
    private Context mContext;

    private static final String LOG_TAG = "DBHelper";

    private static DatabaseChangeListener mDatabaseChangeListener;

    public static final String DATABASE_NAME = "saved_recordings.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DBHelperMethodItem implements BaseColumns {
        public static final String TABLE_NAME = "saved_recordings";

        public static final String COLUMN_NAME_FILE_NAME = "recording_name";
        public static final String COLUMN_NAME_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
        public static final String COLUMN_NAME_FILE_LENGTH = "length";
    }

    private static final String COMMA = ",";
    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBHelperMethodItem.TABLE_NAME + " (" +
                    DBHelperMethodItem._ID + " INTEGER PRIMARY KEY" + COMMA +
                    DBHelperMethodItem.COLUMN_NAME_FILE_NAME + TEXT_TYPE + COMMA +
                    DBHelperMethodItem.COLUMN_NAME_FILE_PATH + TEXT_TYPE + COMMA +
                    DBHelperMethodItem.COLUMN_NAME_FILE_LENGTH + " INTEGER " + COMMA +
                    DBHelperMethodItem.COLUMN_NAME_TIME_ADDED + " INTEGER " + ")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBHelperMethodItem.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DatabaseHelperMethod(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static void setOnDatabaseChangedListener(DatabaseChangeListener listener) {
        mDatabaseChangeListener = listener;
    }

    public RecordedItem getItemAtPosition(int position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DBHelperMethodItem._ID,
                DBHelperMethodItem.COLUMN_NAME_FILE_NAME,
                DBHelperMethodItem.COLUMN_NAME_FILE_PATH,
                DBHelperMethodItem.COLUMN_NAME_FILE_LENGTH,
                DBHelperMethodItem.COLUMN_NAME_TIME_ADDED
        };
        Cursor c = db.query(DBHelperMethodItem.TABLE_NAME, projection, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            RecordedItem item = new RecordedItem();
            item.setId(c.getInt(c.getColumnIndex(DBHelperMethodItem._ID)));
            item.setName(c.getString(c.getColumnIndex(DBHelperMethodItem.COLUMN_NAME_FILE_NAME)));
            item.setFilePath(c.getString(c.getColumnIndex(DBHelperMethodItem.COLUMN_NAME_FILE_PATH)));
            item.setLength(c.getInt(c.getColumnIndex(DBHelperMethodItem.COLUMN_NAME_FILE_LENGTH)));
            item.setTime(c.getLong(c.getColumnIndex(DBHelperMethodItem.COLUMN_NAME_TIME_ADDED)));
            c.close();
            return item;
        }
        return null;
    }
    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { DBHelperMethodItem._ID };
        Cursor c = db.query(DBHelperMethodItem.TABLE_NAME, projection, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public void removeItemWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(DBHelperMethodItem.TABLE_NAME, "_ID=?", whereArgs);
    }

    public void renameRecord(RecordedItem item, String recordingName, String filePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperMethodItem.COLUMN_NAME_FILE_NAME, recordingName);
        cv.put(DBHelperMethodItem.COLUMN_NAME_FILE_PATH, filePath);
        db.update(DBHelperMethodItem.TABLE_NAME, cv,
                DBHelperMethodItem._ID + "=" + item.getId(), null);

        if (mDatabaseChangeListener != null) {
            mDatabaseChangeListener.DatabaseEntryRenamed();
        }
    }



    public long addRecord(String recordingName, String filePath, long length) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperMethodItem.COLUMN_NAME_FILE_NAME, recordingName);
        cv.put(DBHelperMethodItem.COLUMN_NAME_FILE_PATH, filePath);
        cv.put(DBHelperMethodItem.COLUMN_NAME_FILE_LENGTH, length);
        cv.put(DBHelperMethodItem.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());
        long rowId = db.insert(DBHelperMethodItem.TABLE_NAME, null, cv);

        if (mDatabaseChangeListener != null) {
            mDatabaseChangeListener.DatabaseEntryAdded();
        }

        return rowId;
    }


}
