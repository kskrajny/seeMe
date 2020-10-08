package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeeDailyActivity extends AppCompatActivity {

    String date;
    TextView textView;
    LinearLayout layout;
    SharedPreferences sp;
    ViewGroup.LayoutParams params;
    String group;
    List<Date> hours;
    List<DatabaseHandler.TimeSpan> list;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_daily);
        date = getIntent().getExtras().getString("date");
        textView = findViewById(R.id.dateToSee);
        textView.setText(date);
        layout = findViewById(R.id.layoutDaily);
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        group = sp.getString("group", "lama");
        params = findViewById(R.id.exampleViewSeeDaily).getLayoutParams();
        hours = getHours();
        setHeaderFooter();
        db = DatabaseHandler.getInstance(this);
        list = db.getDates(group);
        fillTimeLayout(layout);
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

    private void fillTimeLayout(LinearLayout layout) {
        for(Date x : hours) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            TextView dateView = new TextView(this);
            dateView.setTextSize(15);
            dateView.setLayoutParams(params);
            String str = sdf.format(x);
            dateView.setText(str);
            TextView countView = new TextView(this);
            int id = (int)hash(str);
            Log.i("seeme", id+" "+str);
            countView.setId(id);
            countView.setTextSize(15);
            countView.setLayoutParams(params);
            int count = 0;
            for (DatabaseHandler.TimeSpan y : list) {
                int hash1 = (int)hash(sdf.format(y.date1));
                int hash2 = (int)hash(sdf.format(y.date2));
                if(hash1 <=  id && hash2 >= id) {
                    count++;
                }
            }
            countView.setText(Integer.toString(count));
            row.addView(dateView);
            row.addView(countView);
            layout.addView(row);
        }
    }

    public long hash(String str) {
        long ret = 0;
        long x = (int)Math.pow(11, 4);
        for (char ch : str.toCharArray()) {
            ret += ((long)ch-48)*x;
            x /= 11;
        }
        return ret;
    }

    public void setHeaderFooter() {
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String group = sp.getString("group", "private");
        String user = sp.getString("user", "anonymous");
        TextView header = findViewById(R.id.headerText);
        TextView footer = findViewById(R.id.footerText);
        header.setText(user);
        footer.setText(group);
    };
}