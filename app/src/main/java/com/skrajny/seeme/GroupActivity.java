package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    public void newGroup(View view) {
        Intent myIntent = new Intent(GroupActivity.this, NewGroupActivity.class);
        startActivity(myIntent);
    }

    public void changeGroup(View view) {
        Intent myIntent = new Intent(GroupActivity.this, ChangeGroupActivity.class);
        startActivity(myIntent);
    }

    public void addMember(View view) {
        Intent myIntent = new Intent(GroupActivity.this, AddMemberActivity.class);
        startActivity(myIntent);
    }

    public void deleteGroup(View view) {
        Intent myIntent = new Intent(GroupActivity.this, DeleteGroupActivity.class);
        startActivity(myIntent);
    }

    public void deleteMember(View view) {
        Intent myIntent = new Intent(GroupActivity.this, DeleteMemberActivity.class);
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