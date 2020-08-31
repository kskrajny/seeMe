package com.skrajny.seeme;

import android.content.Intent;
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
                String date = dayOfMonth + "/" + month;
                chosen.setText(date);
                setDay.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setDayOfAvail(View view) {
        Intent myIntent = new Intent(DateActivity.this, TimeActivity.class);
        myIntent.putExtra("date", chosen.getText());
        startActivity(myIntent);
        finish();
    }
}