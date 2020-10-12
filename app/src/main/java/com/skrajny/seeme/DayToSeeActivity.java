package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DayToSeeActivity extends AppCompatActivity {

    CalendarView calendar;
    Button setDayToSee;
    SimpleDateFormat sdf;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_to_see);
        calendar = findViewById(R.id.calendarToSee);
        cal = Calendar.getInstance();
        setDayToSee = findViewById(R.id.setDayToSee);
        calendar.setMinDate(System.currentTimeMillis());
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.add(Calendar.MONTH, 1);
        calendar.setMaxDate(c.getTimeInMillis());
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                cal.set(year, month, dayOfMonth);
                calendar.setDate(cal.getTimeInMillis());
                setDayToSee.setVisibility(View.VISIBLE);
            }
        });
        setHeaderFooter();
    }

    public void setDayToSee(View view) {
        Intent myIntent = new Intent(DayToSeeActivity.this, SeeDailyActivity.class);
        myIntent.putExtra("date", cal.getTimeInMillis());
        startActivity(myIntent);
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