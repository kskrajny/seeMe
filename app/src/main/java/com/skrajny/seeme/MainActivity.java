package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //TODO dodać możliwość ustawienia hasłą

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setHeaderFooter();
        Intent udpService = new Intent(MainActivity.this, UdpService.class);
        startService(udpService);
        SQLiteDatabase db = openOrCreateDatabase("db",MODE_PRIVATE,null);
        db.beginTransaction();
        db.execSQL("CREATE TABLE IF NOT EXISTS groups(id varchar NOT NULL PRIMARY KEY, name varchar NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS chats(ip varchar NOT NULL PRIMARY KEY, name varchar NOT NULL, password NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS messages(ip varchar NOT NULL PRIMARY KEY, received date NOT NULL, content text NOT NULL);");
        db.endTransaction();
    }

    public void onResume() {
        super.onResume();
        setHeaderFooter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void addAvailability(View view) {
        Intent myIntent = new Intent(MainActivity.this, DateActivity.class);
        startActivity(myIntent);
    }

    public void setName(View view) {
        Intent myIntent = new Intent(MainActivity.this, NameActivity.class);
        startActivity(myIntent);
    }

    public void viewAvailability(View view) {
        Intent myIntent = new Intent(MainActivity.this, DayToSeeActivity.class);
        startActivity(myIntent);
    }

    public void setGroup(View view) {
        Intent myIntent = new Intent(MainActivity.this, GroupActivity.class);
        startActivity(myIntent);
    }

    public void deleteAvailability(View view) {
        Intent myIntent = new Intent(MainActivity.this, DeleteActivity.class);
        startActivity(myIntent);
    }

    public void setHeaderFooter() {
        SharedPreferences spGroup = getSharedPreferences("group", MODE_PRIVATE);
        String groupName = spGroup.getString("curr_group", "lama");
        SharedPreferences spName = getSharedPreferences("name", MODE_PRIVATE);
        String name = spName.getString("name", "Anonymous");
        TextView header = findViewById(R.id.headerText);
        TextView footer = findViewById(R.id.footerText);
        header.setText(name);
        footer.setText(groupName);
    }
}