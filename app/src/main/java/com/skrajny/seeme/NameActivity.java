package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NameActivity extends AppCompatActivity {

    SharedPreferences spName;
    EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        spName = getSharedPreferences("name", MODE_PRIVATE);
        nameText = findViewById(R.id.name);
        setHeaderFooter();
    }

    public void setName(View view) {
        SharedPreferences.Editor edit = spName.edit();
        String newName = nameText.getText().toString();
        edit.remove("name");
        edit.commit();
        edit.putString("name", newName);
        edit.commit();
        Log.i("seeme", newName);
        setHeaderFooter();
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