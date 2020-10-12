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
    private static String private_id = null;

    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_CHATS = "chats";
    private static final String TABLE_DATES = "dates";
    private static final String TABLE_MEMBERS = "members";

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
    private static final String membersQuery = "SELECT * FROM "+TABLE_MEMBERS;
    private static final String ipQuery = "SELECT * FROM "+TABLE_MEMBERS;
    private static final String deleteFromDates = "DELETE FROM "
            +TABLE_DATES+"? WHERE "
            +KEY_NAME+"=? AND"
            +KEY_TIME_1+"=? AND"
            +KEY_TIME_2+"=?";
    /*
    private static final String groupIDQuery = "SELECT * FROM "
            +TABLE_GROUPS+" WHERE "
            +KEY_ID+"=?";
    private static final String checkIP = "SELECT * FROM "
            +TABLE_MEMBERS+"? WHERE "
            +KEY_IP+"=?";
     */

    private static DatabaseHandler sInstance;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getInstance(Context context, String priv_id) {
        if(private_id == null) {
            private_id = priv_id;
        }
        if(sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_GROUPS+"("
                +KEY_ID+ " TEXT PRIMARY KEY, "
                +KEY_NAME+ " TEXT NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_CHATS+"("
                +KEY_IP+" TEXT PRIMARY KEY, "
                +KEY_NAME+" TEXT NOT NULL,"
                +KEY_PASSWORD+" NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_MESSAGES+"("
                +KEY_IP+" TEXT PRIMARY KEY, "
                +KEY_DAY+" INTEGER NOT NULL, "
                +KEY_CONTENT+" text NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_DATES+"private ("
                +KEY_NAME+" TEXT, "
                +KEY_TIME_1+" TEXT NOT NULL, "
                +KEY_TIME_2+" TEXT NOT NULL);");
        ContentValues values = new ContentValues();
        values.put(KEY_ID, private_id);
        values.put(KEY_NAME, "private");
        db.insertOrThrow(TABLE_GROUPS, null, values);
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_DATES+private_id+" ("
                +KEY_NAME+" TEXT, "
                +KEY_TIME_1+" TEXT NOT NULL, "
                +KEY_TIME_2+" TEXT NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_MEMBERS+private_id+" ("
                +KEY_IP+" TEXT PRIMARY KEY)");
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

    public List<String> getMembers(String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(membersQuery+groupId, null);
        List<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(KEY_IP)));
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

    public List<TimeSpan> getDates(String groupID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(datesQuery+groupID, null);
        List<TimeSpan> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (cursor.moveToFirst()) {
            do {
                String d1 = cursor.getString(cursor.getColumnIndex(KEY_TIME_1));
                String d2 = cursor.getString(cursor.getColumnIndex(KEY_TIME_2));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                TimeSpan t = new TimeSpan();
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

    public List<String> getIp(String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ipQuery+groupId, null);
        List<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(KEY_IP)));
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<String> getDatesToDelete(String groupID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(datesQuery+groupID, null);
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

    public void deleteDate(final String groupID, String date) {
        SQLiteDatabase db = getWritableDatabase();
        final String[] set = date.split(" ");
        db.execSQL("DELETE FROM "
                +TABLE_DATES+groupID+" WHERE "
                +KEY_NAME+"=\""+set[0]+"\" AND "
                +KEY_TIME_1+"=\""+set[1]+" "+set[2]+"\" AND "
                +KEY_TIME_2+"=\""+set[3]+" "+set[4]+"\"");
    }

    public void deleteGroup(String groupId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "
                +TABLE_GROUPS+" WHERE "
                +KEY_ID+"=\""+groupId+"\"");
        db.execSQL("DROP TABLE "+TABLE_DATES+groupId);
        db.execSQL("DROP TABLE "+TABLE_MEMBERS+groupId);
    }

    public void deleteMember(String groupId, String ip) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "
                +TABLE_MEMBERS+groupId+" WHERE "
                +KEY_IP+"=\""+ip+"\"");
    }

    public void addGroup(String name, String id) {
        SQLiteDatabase db = getWritableDatabase();
        if(id == null)
            id = RandomString.getAlphaNumericString();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_NAME, name);
        try {
            db.insertOrThrow(TABLE_GROUPS, null, values);
            db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_DATES+id+"("
                    +KEY_NAME+" TEXT, "
                    +KEY_TIME_1+" TEXT NOT NULL, "
                    +KEY_TIME_2+" TEXT NOT NULL);");
            db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_MEMBERS+id+" ("
                    +KEY_IP+" TEXT PRIMARY KEY)");
        } catch (Exception e) {
            Log.e("seeme", String.valueOf(e));
        } finally {
            db.close();
        }
    }

    public void addDate(String groupID, String name, String date1, String date2) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME_1, date1);
        values.put(KEY_TIME_2, date2);
        values.put(KEY_NAME, name);
        try {
            db.insertOrThrow(TABLE_DATES+groupID, null, values);
        } catch (Exception e) {
            Log.e("seeme", String.valueOf(e));
        } finally {
            db.close();
        }
    }

    public void addMember(String groupID, String ip) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IP, ip);
        try {
            db.insertOrThrow(TABLE_MEMBERS+groupID, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public boolean checkGroupID(String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM groups WHERE id=\""+groupId+"\"", null);
        //Cursor cursor = db.rawQuery(groupIDQuery, new String[] {groupId});
        boolean ret = cursor.getCount() == 1;
        db.close();
        return ret;
    }

    public boolean checkIP(String groupId, String ip) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                +TABLE_MEMBERS+groupId+" WHERE "+KEY_IP+"=\""+ip+"\"", null);
        boolean ret = cursor.getCount() == 1;
        db.close();
        return ret;
    }
}
