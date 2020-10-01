package com.skrajny.seeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GroupActivity extends AppCompatActivity {

    SharedPreferences spGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        spGroup = getSharedPreferences("group", MODE_PRIVATE);
        setHeaderFooter();
    }

    public void onResume() {
        super.onResume();
        setHeaderFooter();
    }

    public void changeGroup(View view) {
        Intent myIntent = new Intent(GroupActivity.this, ChangeGroupActivity.class);
        startActivity(myIntent);
    }

    public void addMember(View view) {

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
        showAdresses(groupName);
    }

    public void showAdresses(String groupName) {
        SharedPreferences spMembers = getSharedPreferences("members"+groupName, MODE_PRIVATE);
        for(String x : spMembers.getAll().keySet()) {
            Log.i("seeme", x);
        }
    }
}