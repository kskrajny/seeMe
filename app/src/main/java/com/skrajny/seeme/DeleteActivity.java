package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeSet;

import static com.skrajny.seeme.R.layout.activity_delete;

public class DeleteActivity extends AppCompatActivity {

    SharedPreferences spTime;
    SharedPreferences spGroup;
    String groupName;
    LinearLayout layout;
    Button delete;
    String toDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_delete);
        spGroup = getSharedPreferences("group", MODE_PRIVATE);
        groupName = spGroup.getString("curr_group", "lama");
        spTime = getSharedPreferences("time"+groupName, MODE_PRIVATE);
        layout = findViewById(R.id.layout);
        delete = findViewById(R.id.delete);
        toDelete = null;

        setHeaderFooter();

        TreeSet<String> sortedSet = new TreeSet<String>(spTime.getAll().keySet());
        for(final String x : sortedSet) {
            final TextView textView = new TextView(this);
            textView.setText(x);
            textView.setTextSize(20);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toDelete == null) {
                        delete.setVisibility(View.VISIBLE);
                        toDelete = x;
                        textView.setTextSize(25);
                    } else {
                        delete.setVisibility(View.INVISIBLE);
                        toDelete = null;
                        textView.setTextSize(20);
                    }
                }
            });
            layout.addView(textView);
        }
    }

    public void delete(View view) {
        SharedPreferences.Editor edit = spTime.edit();
        edit.remove(toDelete);
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