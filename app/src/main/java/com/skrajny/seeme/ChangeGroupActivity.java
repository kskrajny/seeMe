package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeSet;

public class ChangeGroupActivity extends AppCompatActivity {

    SharedPreferences spGroup;
    EditText nameText;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group);
        spGroup = getSharedPreferences("group", MODE_PRIVATE);
        nameText = findViewById(R.id.groupName);
        layout = findViewById(R.id.layout);

        TreeSet<String> sortedSet = new TreeSet<String>(spGroup.getAll().keySet());
        for(final String x : sortedSet) {
            if(x.equals("curr_group")) continue;
            final TextView textView = new TextView(this);
            textView.setText(x);
            textView.setTextSize(20);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nameText.setText(textView.getText());
                }
            });
            layout.addView(textView);
        }

        setHeaderFooter();
    }

    public void setGroupName(View view) {
        SharedPreferences.Editor edit = spGroup.edit();
        String newName = nameText.getText().toString();
        edit.remove("curr_group");
        edit.commit();
        edit.putString("curr_group", newName);
        edit.putString(newName, ".");
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