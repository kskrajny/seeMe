package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DayToSeeActivity extends AppCompatActivity {

    TextView chosen;
    CalendarView calendar;
    Button setDayToSee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_to_see);
        chosen = findViewById(R.id.chosen);
        calendar = findViewById(R.id.calendarToSee);
        setDayToSee = findViewById(R.id.setDayToSee);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month++;
                String date = dayOfMonth + "/" + month;
                chosen.setText(date);
                setDayToSee.setVisibility(View.VISIBLE);
            }
        });
        setHeaderFooter();
    }

    public void setDayToSee(View view) {
        Intent myIntent = new Intent(DayToSeeActivity.this, SeeDailyActivity.class);
        myIntent.putExtra("date", chosen.getText());
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