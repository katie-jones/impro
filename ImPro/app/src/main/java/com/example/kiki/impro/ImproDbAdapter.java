package com.example.kiki.impro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ImproDbAdapter {

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "filters";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_FILENAME = "filename";
    public static final String KEY_QUALITY = "quality";
    public static final String KEY_TYPE = "type";
    public static final String KEY_FILTERSETTINGS = "filtersettings";
    public static final String KEY_ROWID = "_id";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
                    + KEY_ROWID + " integer PRIMARY KEY autoincrement, "
                    + KEY_FILENAME + " text not null, "
                    + KEY_QUALITY + " integer not null, "
                    + KEY_TYPE + " text not null, "
                    + KEY_FILTERSETTINGS + " text not null, "
                    + "UNIQUE (" + KEY_FILENAME + "));";

    private final Context mCtx;

    public ImproDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            // Not used, but you could upgrade the database with ALTER scripts
        }
    }

    public ImproDbAdapter open() throws android.database.SQLException{
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }


    public void close(){
        mDbHelper.close();
    }


    public long createFilter(String filename, int quality, String type, int[] values){
        // check if filename is already in table
        Cursor mCursor = fetchFilter(filename);
        if (mCursor == null || mCursor.getCount() == 0) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_FILENAME,filename);
            initialValues.put(KEY_QUALITY,quality);
            initialValues.put(KEY_TYPE, type);

            // convert array into string
            String values_str = "";
            for (int i = 0; i<values.length; i++) {
                values_str+=String.valueOf(values[i])+" ";
            }
            initialValues.put(KEY_FILTERSETTINGS, values_str);

            return mDb.insert(DATABASE_TABLE, null, initialValues);
        }
        else {
            // filename is already in table
            mCursor.moveToFirst();
            int index = mCursor.getColumnIndexOrThrow(KEY_ROWID);
            Log.e("TAG", "index: " + String.valueOf(index) + " " + String.valueOf(mCursor.getCount()));
            long rowID = mCursor.getLong(index);
            updateFilter(rowID, filename,quality,type,values);

            return rowID;
        }
    }

    public boolean deleteFilter(long rowId){
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) >0;
    }

    public Cursor fetchAllFilters(){
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_FILENAME,
                KEY_QUALITY, KEY_TYPE, KEY_FILTERSETTINGS}, null, null, null, null, null);
    }

    public Cursor fetchFilter(String filename) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
                        KEY_ROWID, KEY_FILENAME,
                        KEY_QUALITY, KEY_TYPE,
                        KEY_FILTERSETTINGS},
                        KEY_FILENAME + "=?", new String[] {filename}, null,
                        null, null, null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    public boolean updateFilter(long rowId, String filename, int quality, String type, int[] values){
        ContentValues args = new ContentValues();
        args.put(KEY_FILENAME,filename);
        args.put(KEY_QUALITY,quality);
        args.put(KEY_TYPE, type);

        // convert array into string
        String values_str = "";
        for (int i = 0; i<values.length; i++) {
            values_str+=String.valueOf(values[i])+" ";
        }
        args.put(KEY_FILTERSETTINGS, values_str);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}