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

public class DeleteGroupActivity extends AppCompatActivity {

    LinearLayout layout;
    Button delete;
    Pair<String, String> toDelete;
    TextView textToDelete;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_group);
        db = DatabaseHandler.getInstance(this);
        layout = findViewById(R.id.layout);
        delete = findViewById(R.id.delete);
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String privateGroupID = sp.getString("privateGroupId", null);
        List<Pair<String, String>> list = db.getGroups();
        for(final Pair<String, String> x :list) {
            if(x.second.equals(privateGroupID))
                continue;
            final TextView textView = new TextView(this);
            textView.setText(x.first+" "+x.second);
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
        db.deleteGroup(toDelete.second);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("group");
        edit.remove("groupId");
        edit.apply();
        edit.putString("group", "private");
        edit.putString("groupId", sp.getString("privateGroupId", null));
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
