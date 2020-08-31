package com.skrajny.seeme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void deleteAvailability(View view) {
        Intent myIntent = new Intent(MainActivity.this, DeleteActivity.class);
        startActivity(myIntent);
    }
}