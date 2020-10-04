package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NameActivity extends AppCompatActivity {

    SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
    EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        nameText = findViewById(R.id.name);
        setHeaderFooter();
    }

    public void setName(View view) {
        SharedPreferences.Editor edit = sp.edit();
        String newName = nameText.getText().toString();
        edit.remove("user");
        edit.putString("user", newName);
        edit.apply();
        Log.i("seeme", newName);
        setHeaderFooter();
    }

    public void setHeaderFooter() {
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String group = sp.getString("curr_group", "private");
        String user = sp.getString("curr_user", "anonymous");
        TextView header = findViewById(R.id.headerText);
        TextView footer = findViewById(R.id.footerText);
        header.setText(user);
        footer.setText(group);
    }
}