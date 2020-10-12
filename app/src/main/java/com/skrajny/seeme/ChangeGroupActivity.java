package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ChangeGroupActivity extends AppCompatActivity {

    SharedPreferences sp;
    DatabaseHandler db;
    Button change;
    Pair<String, String> toChange;
    TextView textToChange;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group);
        layout = findViewById(R.id.layout);
        change = findViewById(R.id.change);
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        db = DatabaseHandler.getInstance(this);
        toChange = null;
        List<Pair<String, String>> list = db.getGroups();
        for (final Pair<String, String> x : list) {
            final String name = x.first;
            final String id = x.second;
            final TextView textView = new TextView(this);
            textView.setText(name+" "+id);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(0, 5, 0, 5);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setTextSize(20);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toChange == null) {
                        change.setVisibility(View.VISIBLE);
                        toChange = x;
                        textToChange = textView;
                        textView.setTextColor(Color.parseColor("#2980ff"));
                    } else {
                        if(textView.equals(textToChange)) {
                            change.setVisibility(View.INVISIBLE);
                            toChange = null;
                            textToChange = null;
                            textView.setTextColor(Color.parseColor("#000000"));
                        } else {
                            toChange = x;
                            textToChange.setTextColor(Color.parseColor("#000000"));
                            textToChange = textView;
                            textView.setTextColor(Color.parseColor("#2980ff"));
                        }
                    }
                }
            });
            layout.addView(textView);
        }
        setHeaderFooter();
    }

    public void changeGroup(View view) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("group");
        edit.remove("groupId");
        edit.apply();
        edit.putString("group", toChange.first);
        edit.putString("groupId", toChange.second);
        edit.commit();
        finish();
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