package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import static com.skrajny.seeme.R.layout.activity_delete;

public class DeleteActivity extends AppCompatActivity {

    LinearLayout layout;
    Button delete;
    String toDelete;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_delete);
        layout = findViewById(R.id.layout);
        delete = findViewById(R.id.delete);
        toDelete = null;
        db = DatabaseHandler.getInstance(this);
        setHeaderFooter();
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String groupId = sp.getString("groupId", null);
        if(groupId == null)
            finish();
        List<String> list = db.getDatesToDelete(groupId);
        for(final String x :list) {
            final TextView textView = new TextView(this);
            textView.setText(x);
            textView.setTextSize(15);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toDelete == null) {
                        delete.setVisibility(View.VISIBLE);
                        toDelete = x;
                        textView.setTextSize(20);
                    } else {
                        delete.setVisibility(View.INVISIBLE);
                        toDelete = null;
                        textView.setTextSize(15);
                    }
                }
            });
            layout.addView(textView);
        }
    }

    public void delete(View view) {
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String groupId = sp.getString("groupId", null);
        db.deleteDate(groupId, toDelete);
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