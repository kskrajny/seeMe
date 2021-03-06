package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //TODO pozbycie sie klasy Date, zmiana na klase Calendar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setHeaderFooter();
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String str = sp.getString("groupId", null);
        if(str == null) {
            String id =  RandomString.getAlphaNumericString();
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("groupId", id);
            edit.putString("privateGroupId", id);
            edit.commit();
            DatabaseHandler.getInstance(this, id);
        }
        DatabaseHandler.getInstance(this);
        Intent udpService = new Intent(MainActivity.this, UdpService.class);
        startService(udpService);
        startService(new Intent(getBaseContext(), Cleaner.class));
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

    public void setNamePassword(View view) {
        Intent myIntent = new Intent(MainActivity.this, NamePasswordActivity.class);
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
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String group = sp.getString("group", "private");
        String user = sp.getString("user", "anonymous");
        TextView header = findViewById(R.id.headerText);
        TextView footer = findViewById(R.id.footerText);
        header.setText(user);
        footer.setText(group);
    }
}