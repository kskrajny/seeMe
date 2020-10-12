package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeeDailyActivity extends AppCompatActivity {

    TextView textView;
    LinearLayout layout;
    SharedPreferences sp;
    ViewGroup.LayoutParams params;
    String groupId;
    List<Date> hours;
    List<DatabaseHandler.TimeSpan> list;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_daily);
        Long l = getIntent().getExtras().getLong("date");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        textView = findViewById(R.id.dateToSee);
        textView.setText(sdf.format(l));
        layout = findViewById(R.id.layoutDaily);
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        groupId = sp.getString("groupId", null);
        if(groupId == null)
            finish();
        params = findViewById(R.id.exampleViewSeeDaily).getLayoutParams();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(l);
        hours = getHours(cal);
        setHeaderFooter();
        db = DatabaseHandler.getInstance(this);
        list = db.getDates(groupId);
        fillTimeLayout(layout);
    }

    private static List<Date> getHours(Calendar calendar) {
        ArrayList<Date> dates = new ArrayList<Date>();

        Calendar cal1 = (Calendar) calendar.clone();
        Calendar cal2 = (Calendar) calendar.clone();

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
            countView.setId(id);
            countView.setTextSize(15);
            countView.setLayoutParams(params);
            int count = 0;
            for (DatabaseHandler.TimeSpan y : list) {
                if(!x.before(y.date1) && !x.after(y.date2)) {
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