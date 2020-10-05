package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeGroupActivity extends AppCompatActivity {

    SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);;
    SQLiteDatabase db = openOrCreateDatabase("db",MODE_PRIVATE,null);
    EditText nameText;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group);
        nameText = findViewById(R.id.groupName);
        layout = findViewById(R.id.layout);

        Cursor c = db.rawQuery("SELECT name FROM groups", new String[] {} );
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex("name"));
            final TextView textView = new TextView(this);
            textView.setText(name);
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
        SharedPreferences.Editor edit = sp.edit();
        String[] set = nameText.getText().toString().split("\n");
        edit.remove("group");
        edit.remove("groupId");
        edit.apply();
        if(set.length == 1) {
            String groupId;
            db.beginTransaction();
            db.execSQL("INSERT INTO "+groupId+"M values("+ipString+")");
            db.endTransaction();
        }
        edit.putString("group", set[0]);
        edit.putString("groupId", set[1]);
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