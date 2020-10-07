package com.skrajny.seeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database";

    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_CHATS = "chats";
    private static final String TABLE_DATES = "dates";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IP = "ip";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_DAY = "day";
    private static final String KEY_TIME_1 = "time1";
    private static final String KEY_TIME_2 = "time2";
    private static final String KEY_CONTENT = "content";

    private static final String groupsQuery = "SELECT * FROM "+TABLE_GROUPS;
    private static final String datesQuery = "SELECT * FROM "+TABLE_DATES;
    private static final String deleteFromDates = "DELETE FROM "
            +TABLE_DATES+"? WHERE "
            +KEY_NAME+"=? AND"
            +KEY_TIME_1+"=? AND"
            +KEY_TIME_2+"=?";

    private static DatabaseHandler sInstance;

    public static synchronized DatabaseHandler getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_GROUPS+"("
                +KEY_ID+ " TEXT NOT NULL PRIMARY KEY, "
                +KEY_NAME+ " TEXT NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_CHATS+"("
                +KEY_IP+" TEXT NOT NULL PRIMARY KEY, "
                +KEY_NAME+" TEXT NOT NULL,"
                +KEY_PASSWORD+" NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_MESSAGES+"("
                +KEY_IP+" TEXT NOT NULL PRIMARY KEY, "
                +KEY_DAY+" INTEGER NOT NULL, "
                +KEY_CONTENT+" text NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_DATES+"private ("
                +KEY_NAME+" TEXT, "
                +KEY_TIME_1+" TEXT NOT NULL, "
                +KEY_TIME_2+" TEXT NOT NULL);");
        ContentValues values = new ContentValues();
        values.put(KEY_ID, RandomString.getAlphaNumericString());
        values.put(KEY_NAME, "private");
        db.insertOrThrow(TABLE_GROUPS, null, values);
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_DATES+"private ("
                +KEY_NAME+" TEXT, "
                +KEY_TIME_1+" TEXT NOT NULL, "
                +KEY_TIME_2+" TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            Log.i("seeme", "UPGRADE");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
            onCreate(db);
        }
    }

    public List<Pair<String, String>> getGroups() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(groupsQuery, null);
        List<Pair<String, String>> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                list.add(
                        new Pair(
                            cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_ID))
                        )
                );
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public class TimeSpan {
        Date date1;
        Date date2;
        String name;
    }

    public List<TimeSpan> getDates(String group) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(datesQuery+group, null);
        List<TimeSpan> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (cursor.moveToFirst()) {
            do {
                String d1 = cursor.getString(cursor.getColumnIndex(KEY_TIME_1));
                String d2 = cursor.getString(cursor.getColumnIndex(KEY_TIME_2));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                TimeSpan t = null;
                try {
                    t.date1 = sdf.parse(d1);
                    t.date2 = sdf.parse(d2);
                } catch(ParseException e) {
                    e.printStackTrace();
                }
                list.add(t);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<String> getDatesToDelete(String group) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(datesQuery+group, null);
        List<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String d1 = cursor.getString(cursor.getColumnIndex(KEY_TIME_1));
                String d2 = cursor.getString(cursor.getColumnIndex(KEY_TIME_2));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                list.add(name+" "+d1+" "+d2);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public void deleteDate(final String group, String date) {
        SQLiteDatabase db = getWritableDatabase();
        final String[] set = date.split(" ");
        db.execSQL("DELETE FROM "
                +TABLE_DATES+group+" WHERE "
                +KEY_NAME+"=\""+set[0]+"\" AND "
                +KEY_TIME_1+"=\""+set[1]+" "+set[2]+"\" AND "
                +KEY_TIME_2+"=\""+set[3]+" "+set[4]+"\"");
    }

    public void addGroup(String name, String id) {
        SQLiteDatabase db = getWritableDatabase();
        if(id == null)
            id = RandomString.getAlphaNumericString();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_NAME, name);
        db.beginTransaction();
        try {
            db.insertOrThrow(TABLE_GROUPS, null, values);
            db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_DATES+name+"("
                    +KEY_NAME+" TEXT, "
                    +KEY_TIME_1+" TEXT NOT NULL, "
                    +KEY_TIME_2+" TEXT NOT NULL);");
        } catch (Exception e) {
            Log.e("seeme", String.valueOf(e));
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void addDate(String group, String name, String date1, String date2) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME_1, date1);
        values.put(KEY_TIME_2, date2);
        values.put(KEY_NAME, name);
        try {
            db.insertOrThrow(TABLE_DATES+group, null, values);
        } catch (Exception e) {
            Log.e("seeme", String.valueOf(e));
        } finally {
            db.close();
        }
    }
}
