package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
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

    String[] tabTime;
    String[] tabDate;
    TextView[] ovalTime;
    TextView[] ovalDate;
    LinearLayout t1;
    LinearLayout t2;
    LinearLayout d1;
    LinearLayout d2;
    Button setDay;
    List<Date> dates;
    List<Date> hours;
    SimpleDateFormat sdfDate;
    SimpleDateFormat sdfTime;
    DatabaseHandler db;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        setDay = findViewById(R.id.setDay);
        t1 = findViewById(R.id.Time1);
        t2 = findViewById(R.id.Time2);
        d1 = findViewById(R.id.Date1);
        d2 = findViewById(R.id.Date2);
        tabTime= new String[2];
        tabDate = new String[2];
        ovalTime = new TextView[2];
        ovalDate = new TextView[2];
        ovalDate[0] = findViewById(R.id.oval1);
        ovalTime[0] = findViewById(R.id.oval2);
        ovalDate[1] = findViewById(R.id.oval3);
        ovalTime[1] = findViewById(R.id.oval4);
        dates = getDates();
        hours = getHours();
        sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        sdfTime = new SimpleDateFormat("HH:mm");
        fillTimeLayout(t1, 0);
        fillTimeLayout(t2, 1);
        fillDateLayout(d1, 0);
        fillDateLayout(d2, 1);
        db = DatabaseHandler.getInstance(this);
        setHeaderFooter();
    }

    private static List<Date> getDates() {
        ArrayList<Date> dates = new ArrayList<Date>();

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.MONTH, 1);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    private static List<Date> getHours() {
        ArrayList<Date> dates = new ArrayList<Date>();

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal1.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.MINUTE, 30);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.MINUTE, 30);
        }
        return dates;
    }

    public void setDayOfAvail(View view) {
        int x = tabDate[0].compareTo(tabDate[1]);
        if(x > 0) {
            tabDate[0] = null;
            tabDate[1] = null;
            ovalDate[0].setBackgroundResource(R.drawable.purple_rectangle);
            ovalDate[0].setText("");
            ovalDate[1].setBackgroundResource(R.drawable.purple_rectangle);
            ovalDate[1].setText("");
            count = 2;
            return;
        }
        if(x == 0) {
            int y = tabTime[0].compareTo(tabTime[1]);
            if(y > 0) {
                tabTime[0] = null;
                tabTime[1] = null;
                ovalTime[0].setBackgroundResource(R.drawable.purple_rectangle);
                ovalTime[0].setText("");
                ovalTime[1].setBackgroundResource(R.drawable.purple_rectangle);
                ovalTime[1].setText("");
                count = 2;
                return;
            }
        }
        //TODO dac group ID do SharedPreference
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String groupID = sp.getString("groupID", "private");
        String user = sp.getString("user", "anonymous");
        String date1 = tabDate[0]+" "+tabTime[0];
        String date2 = tabDate[1]+" "+tabTime[1];
        db.addDate(groupID, user, date1, date2);
        Intent myIntent = new Intent(DateActivity.this, SendMessageActivity.class);
        myIntent.putExtra("mess", user+" "+date1+" "+date2+" "+groupID);
        startActivity(myIntent);
        finish();
    }

    private void fillDateLayout(LinearLayout layout, final int i) {
        for(Date x: dates) {
            final TextView textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            textView.setText(sdfDate.format(x));
            textView.setTextSize(15);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tabDate[i] == null) count++;
                    tabDate[i] = String.valueOf(textView.getText());
                    ovalDate[i].setBackgroundResource(R.drawable.light_rectangle);
                    ovalDate[i].setText(textView.getText());
                    if(count == 4) setDay.setVisibility(View.VISIBLE);
                }
            });
            layout.addView(textView);
        }
    }

    private void fillTimeLayout(LinearLayout layout, final int i) {
        for(Date x : hours) {
            final TextView textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            textView.setText(sdfTime.format(x));
            textView.setTextSize(15);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tabTime[i] == null) count++;
                    tabTime[i] = String.valueOf(textView.getText());
                    ovalTime[i].setBackgroundResource(R.drawable.light_rectangle);
                    ovalTime[i].setText(textView.getText());
                    if(count == 4) setDay.setVisibility(View.VISIBLE); }
                });
            layout.addView(textView);
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