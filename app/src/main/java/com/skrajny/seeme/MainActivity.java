package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setHeaderFooter();
        Intent udpService = new Intent(MainActivity.this, UdpService.class);
        startService(udpService);
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