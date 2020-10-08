package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NameActivity extends AppCompatActivity {

    SharedPreferences sp;
    EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        nameText = findViewById(R.id.name);
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        setHeaderFooter();
    }

    public void setName(View view) {
        SharedPreferences.Editor edit = sp.edit();
        String newName = nameText.getText().toString();
        edit.remove("user");
        edit.putString("user", newName);
        edit.apply();
        setHeaderFooter();
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