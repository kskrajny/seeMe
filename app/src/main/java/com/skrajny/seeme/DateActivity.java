package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DateActivity extends AppCompatActivity {

    TextView chosen;
    CalendarView calendar;
    Button setDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        chosen = findViewById(R.id.chosen);
        calendar = findViewById(R.id.calendar);
        setDay = findViewById(R.id.setDay);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month++;
                String date = dayOfMonth + "/" + month;
                chosen.setText(date);
                setDay.setVisibility(View.VISIBLE);
            }
        });
        setHeaderFooter();
    }

    public void setDayOfAvail(View view) {
        Intent myIntent = new Intent(DateActivity.this, TimeActivity.class);
        myIntent.putExtra("date", chosen.getText());
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