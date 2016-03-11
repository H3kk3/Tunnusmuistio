package io.h3kk3.tunnusmuistio.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by H3kk3 on 11.3.2016.
 * Tietokanta adapteri tunnustietojen käisttelyä varten.
 */
public class TunnusDbAdapter {

    public static final String COLUMN_NICK = "nick";
    public static final String COLUMN_INUMBER = "inumber";
    public static final String COLUMN_ID = "_id";
    public static final String TAG = "MemoDB";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DB_NAME = "ibanmemo.db";
    private static final String TABLE_NAME = "friendlist";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table friendlist (_id integer primary key autoincrement, " +
            "nick text not null, inumber text not null);";
    private final Context mContext;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            Log.d(TAG, "onCreate() database");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS friendlist");
            onCreate(db);
            Log.d(TAG, "onUpgrade() database");
        }
    }

    public TunnusDbAdapter(Context context) {
        mContext = context;
    }

    public TunnusDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createMemo(String nick, String inumber) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_NICK, nick);
        initialValues.put(COLUMN_INUMBER, inumber);
        return mDb.insert(TABLE_NAME, null, initialValues);
    }

    public Cursor getAll() {
        return mDb.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_NICK,
                COLUMN_INUMBER}, null, null, null, null, null);
    }
}

