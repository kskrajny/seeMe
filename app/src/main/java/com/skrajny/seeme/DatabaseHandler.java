package com.skrajny.seeme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database";
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_CHATS = "chats";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IP = "ip";
    private static final String groupsQuery = "SELECT * FROM "+TABLE_GROUPS;
    private static final String addGroupQuery = "INSERT INTO "+TABLE_GROUPS+" values(?, ?)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_GROUPS+ "("
                +KEY_ID+ " varchar NOT NULL PRIMARY KEY, "
                +KEY_NAME+ " varchar NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS chats(ip varchar NOT NULL PRIMARY KEY, name varchar NOT NULL, password NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS messages(ip varchar NOT NULL PRIMARY KEY, received date NOT NULL, content text NOT NULL);");
        addGroup("lama", "qwerty");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
        // Create tables again
        onCreate(db);
    }

    public List<Pair<String, String>> getGroups() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(groupsQuery, null);
        List<Pair<String, String>> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                list.add(
                        new Pair(
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getString(cursor.getColumnIndex("id"))
                        )
                );
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void addGroup(String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(id == null)
            id = RandomString.getAlphaNumericString();
        db.beginTransaction();
        db.execSQL(addGroupQuery, new String[] {id, name});
        db.endTransaction();
    }
}
