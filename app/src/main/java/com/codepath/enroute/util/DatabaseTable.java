package com.codepath.enroute.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.codepath.enroute.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.util.TextUtils;

import static cz.msebera.android.httpclient.HttpHeaders.IF;

/**
 * Created by qunli on 11/25/17.
 */

public class DatabaseTable {

    private static final String TAG = DatabaseTable.class.getSimpleName();

    public static final String COL_PHONE="PHONE";
    public static final String COL_STATION="STATION";

    private static final String DATABASE_NAME="DICTIONARY";
    private static final String PHONE_STATION_VIRTUAL_TABLE="PHONE_MAP";
    private static final int DATABASE_VERSION=1;


    private final DatabaseOpenHelper mDatabaseOpenHelper;

    public DatabaseTable(Context context){
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper{
        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        private static final String PHONE_STATION_TABLE_CREATE =
                "CREATE VIRTUAL TABLE "+ PHONE_STATION_VIRTUAL_TABLE +
                        " USING fts3 ("+
                        COL_PHONE +","+
                        COL_STATION+")";

        DatabaseOpenHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(PHONE_STATION_TABLE_CREATE);
            loadDictionary();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST "+PHONE_STATION_VIRTUAL_TABLE);
            onCreate(db);
        }

        private void loadDictionary(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadWords();
                    }catch (IOException e){
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadWords() throws IOException{
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.phone_station_map);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try{
                String line;
                while((line=reader.readLine())!=null){
                    String[] strings = line.split(",");
                    if (strings.length < 2 ) continue;
                    long id = addWord(strings[0].trim(), strings[1].trim());
                    if (id < 0) {
                        Log.e(TAG, "unable to add word: " + strings[0].trim());
                    }
                }
            }finally {
                reader.close();
            }
        }

        public long addWord(String phone, String station){
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_PHONE, phone);
            initialValues.put(COL_STATION, station);

            return mDatabase.insert(PHONE_STATION_VIRTUAL_TABLE, null, initialValues);
        }
    }


    public Cursor getWordMatches(String query, String[] columns) {
        String selection = COL_PHONE + " = ?";
        String[] selectionArgs = new String[] {query};

        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(PHONE_STATION_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

}
