package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeSet;

public class SeeDailyActivity extends AppCompatActivity {

    String date;
    TextView textView;
    LinearLayout layout;
    SharedPreferences sp;
    ViewGroup.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_daily);
        date = getIntent().getExtras().getString("date");
        textView = findViewById(R.id.dateToSee);
        textView.setText(date);
        layout = findViewById(R.id.layoutDaily);
        sp = getSharedPreferences("name", MODE_PRIVATE);
        params = findViewById(R.id.exampleViewSeeDaily).getLayoutParams();
        TreeSet<String> sortedSet = new TreeSet<>();
        for(String x: sp.getAll().keySet()) {
            if(x.equals("name")) continue;
            String str = x.substring(x.indexOf(" ")+1);
            if(x.startsWith(date)) sortedSet.add(str);
        }
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 30) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                TextView dateView = new TextView(this);
                dateView.setId(60 * h + m);
                dateView.setText(h + ":" + m);
                dateView.setTextSize(15);
                dateView.setLayoutParams(params);
                TextView countView = new TextView(this);
                countView.setId(60 * (h + 24) + m);
                countView.setTextSize(15);
                countView.setLayoutParams(params);
                int count = 0;
                for (String x : sortedSet) {
                    String[] set = x.split(" ");
                    if (Integer.parseInt(set[0]) <= 60 * h + m && Integer.parseInt(set[1]) >= 60 * h + m)
                        count++;
                }
                countView.setText(Integer.toString(count));
                row.addView(dateView);
                row.addView(countView);
                layout.addView(row);
            }
        }
    }
}