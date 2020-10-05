package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewGroupActivity extends AppCompatActivity {

    EditText nameText;
    SQLiteDatabase db = openOrCreateDatabase("db",MODE_PRIVATE,null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        nameText = findViewById(R.id.name);
        setHeaderFooter();
    }

    public void setName(View view) {
        String name = nameText.getText().toString();
        String id = RandomString.getAlphaNumericString();
        db.beginTransaction();
        db.execSQL("INSERT INTO groups values("+id+", "+name+")");
        db.endTransaction();
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