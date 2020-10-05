package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateActivity extends AppCompatActivity {

    TextView time1;
    TextView time2;
    TextView date1;
    TextView date2;
    LinearLayout t1;
    LinearLayout t2;
    LinearLayout d1;
    LinearLayout d2;
    Button setDay;
    List<Date> dates;
    SimpleDateFormat sdfDate;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        setDay = findViewById(R.id.setDay);
        dates = getDates();
        sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        fillTimeLayout(t1, time1);
        fillTimeLayout(t2, time2);
        fillDateLayout(d1, date1);
        fillDateLayout(d1, date2);
        db = new DatabaseHandler(this);
    }

    private static List<Date> getDates() {
        ArrayList<Date> dates = new ArrayList<Date>();

        Calendar cal1 = Calendar.getInstance();
        Log.i("seeme", String.valueOf(cal1.getTime()));

        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.MONTH, 1);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public void setDayOfAvail(View view) {
        finish();
    }

    private void fillDateLayout(LinearLayout layout, final TextView save) {
        for(Date x: dates) {
            Log.i("seeme", sdfDate.format(x));
            final TextView textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            textView.setText(sdfDate.format(x));
            textView.setTextSize(15);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save.setText(textView.getText());
                }
            });
            layout.addView(textView);
        }
    }

    private void fillTimeLayout(LinearLayout layout, final TextView save) {
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 30) {
                final TextView textView = new TextView(this);
                textView.setGravity(Gravity.CENTER);
                textView.setText(h + ":" + m);
                textView.setTextSize(15);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save.setText(textView.getText());
                    }
                });
                layout.addView(textView);
            }
        }
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