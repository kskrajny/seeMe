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
    TextView currNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        sp = getSharedPreferences("name", MODE_PRIVATE);
        nameText = findViewById(R.id.name);
        currNameText = findViewById(R.id.curr_name);
        currNameText.setText(sp.getString("name", "Anonymous"));
    }

    public void setName(View view) {
        SharedPreferences.Editor edit = sp.edit();
        String newName = nameText.getText().toString();
        edit.putString("name", newName);
        edit.commit();
        currNameText.setText(newName);
    }
}