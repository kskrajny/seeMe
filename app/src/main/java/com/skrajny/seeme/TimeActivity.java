package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TimeActivity extends AppCompatActivity {

    String date;
    SharedPreferences spName;
    SharedPreferences spTime;
    SharedPreferences spGroup;
    String groupName;
    LinearLayout layout;
    TextView startView;
    TextView endView;
    Button setTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        date = getIntent().getExtras().getString("date");
        spName = getSharedPreferences("name", MODE_PRIVATE);
        spGroup = getSharedPreferences("group", MODE_PRIVATE);
        groupName = spGroup.getString("curr_group", "lama");
        spTime = getSharedPreferences("time"+groupName, MODE_PRIVATE);
        layout = findViewById(R.id.layout);

        setHeaderFooter();

        startView = null;
        endView = null;
        setTime = findViewById(R.id.setTime);
        for(int h=0;h<24;h++) {
            for(int m=0;m<60;m+=30) {
                final TextView textView = new TextView(this);
                final int id = 60*h+m;
                textView.setId(id);
                textView.setGravity(Gravity.CENTER);
                textView.setText(h + ":" + m);
                textView.setTextSize(15);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(startView == null) {
                            startView = textView;
                            startView.setTextSize(20);
                        } else if (endView == null) {
                            endView = textView;
                            endView.setTextSize(20);
                            for(int i = startView.getId();i<id;i+=30) {
                                ((TextView)findViewById(i)).setTextSize(20);
                            }
                            setTime.setVisibility(View.VISIBLE);
                        } else {
                            for(int i = startView.getId();i<=id;i+=30) {
                                ((TextView)findViewById(i)).setTextSize(15);
                            }
                            setTime.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                layout.addView(textView);
            }
        }
    }

    public void setTime(View view) {
        int startId = startView.getId();
        int endId = endView.getId();
        String name = spName.getString("name", "Anonymous");
        SharedPreferences.Editor edit = spTime.edit();
        String str = date+" "+startId+" "+endId+" "+name;
        edit.putString(str, ".");
        edit.commit();
        Intent myIntent = new Intent(TimeActivity.this, SendMessageActivity.class);
        myIntent.putExtra("mess", str+groupName);
        startActivity(myIntent);
        finish();
    }

    public void setHeaderFooter() {
        SharedPreferences spGroup = getSharedPreferences("group", MODE_PRIVATE);
        String groupName = spGroup.getString("curr_group", "lama");
        SharedPreferences spName = getSharedPreferences("name", MODE_PRIVATE);
        String name = spName.getString("name", "Anonymous");
        TextView header = findViewById(R.id.headerText);
        TextView footer = findViewById(R.id.footerText);
        header.setText(name);
        footer.setText(groupName);
    }
}