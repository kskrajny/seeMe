package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DeleteMemberActivity extends AppCompatActivity {

    LinearLayout layout;
    Button delete;
    String toDelete;
    TextView textToDelete;
    DatabaseHandler db;
    String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_member);
        db = DatabaseHandler.getInstance(this);
        layout = findViewById(R.id.layout);
        delete = findViewById(R.id.delete);
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        groupId = sp.getString("groupId", null);
        List<String> list = db.getMembers(groupId);
        for(final String x :list) {
            final TextView textView = new TextView(this);
            textView.setText(x);
            textView.setPadding(0, 5, 0, 5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toDelete == null) {
                        delete.setVisibility(View.VISIBLE);
                        toDelete = x;
                        textToDelete = textView;
                        textView.setTextColor(Color.parseColor("#2980ff"));
                    } else {
                        if(textView.equals(textToDelete)) {
                            delete.setVisibility(View.INVISIBLE);
                            toDelete = null;
                            textToDelete = null;
                            textView.setTextColor(Color.parseColor("#000000"));
                        } else {
                            toDelete = x;
                            textToDelete.setTextColor(Color.parseColor("#000000"));
                            textToDelete = textView;
                            textView.setTextColor(Color.parseColor("#2980ff"));
                        }
                    }
                }
            });
            layout.addView(textView);
        }
        setHeaderFooter();
    }

    public void delete(View view) {
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        db.deleteMember(groupId, toDelete);
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