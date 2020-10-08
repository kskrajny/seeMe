package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NamePasswordActivity extends AppCompatActivity {

    SharedPreferences sp;
    EditText nameText;
    EditText passwordText;
    TextView currPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_password);
        nameText = findViewById(R.id.name);
        passwordText = findViewById(R.id.password);
        currPass = findViewById(R.id.curr_password);
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        currPass.setText(sp.getString("password", "password"));
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

    public void setPassword(View view) {
        SharedPreferences.Editor edit = sp.edit();
        String newPassword = passwordText.getText().toString();
        edit.remove("password");
        edit.putString("password", newPassword);
        edit.apply();
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