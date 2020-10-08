package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ChangeGroupActivity extends AppCompatActivity {

    SharedPreferences sp;
    DatabaseHandler db;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group);
        layout = findViewById(R.id.layout);
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        db = DatabaseHandler.getInstance(this);
        List<Pair<String, String>> list = db.getGroups();
        Log.i("seeme", list.toString());
        for (Pair<String, String> x : list) {
            final String name = x.first;
            final String id = x.second;
            final TextView textView = new TextView(this);
            textView.setText(name);
            textView.setTextSize(20);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setGroupName(name, id);
                }
            });
            layout.addView(textView);
        }

        setHeaderFooter();
    }

    public void setGroupName(String name, String id) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("group");
        edit.remove("groupId");
        edit.apply();
        edit.putString("group", name);
        edit.putString("groupId", id);
        edit.commit();
        recreate();
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