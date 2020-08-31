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
    SharedPreferences sp;
    LinearLayout layout;
    TextView startView;
    TextView endView;
    Button setTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        date = getIntent().getExtras().getString("date");
        sp = getSharedPreferences("name", MODE_PRIVATE);
        layout = findViewById(R.id.layout);
        startView = null;
        endView = null;
        setTime = findViewById(R.id.setTime);
        for(int h=0;h<24;h++) {
            for(int m=0;m<60;m+=30) {
                final TextView textView = new TextView(this);
                textView.setId(60*h+m);
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
                            for(int i = startView.getId();i<endView.getId();i+=30) {
                                ((TextView)findViewById(i)).setTextSize(20);
                            }
                            setTime.setVisibility(View.VISIBLE);
                        } else {
                            for(int i = startView.getId();i<=endView.getId();i+=30) {
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
        String name = sp.getString("name", "Anonymous");
        SharedPreferences.Editor edit = sp.edit();
        String str = date+" "+startId+" "+endId+" "+name;
        edit.putString(str, ".");
        edit.commit();
        Intent myIntent = new Intent(TimeActivity.this, SendMessageActivity.class);
        myIntent.putExtra("date", str);
        startActivity(myIntent);
        finish();
    }



}