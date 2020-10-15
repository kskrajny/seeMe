package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.List;

import static com.skrajny.seeme.R.layout.activity_delete;

public class DeleteActivity extends AppCompatActivity {

    LinearLayout layout;
    Button delete;
    String toDelete;
    TextView textToDelete;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_delete);
        layout = findViewById(R.id.layout);
        delete = findViewById(R.id.delete);
        toDelete = null;
        db = DatabaseHandler.getInstance(this);
        setHeaderFooter();
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String groupId = sp.getString("groupId", null);
        if(groupId == null)
            finish();
        List<String> list = db.getDatesToDelete(groupId);
        for(final String x :list) {
            final TextView textView = new TextView(this);
            textView.setText(x);
            textView.setPadding(0, 5, 0, 5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(16);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toDelete == null) {
                        delete.setVisibility(View.VISIBLE);
                        toDelete = x;
                        textToDelete = textView;
                        textView.setTextColor(Color.parseColor("#2980ff"));
                    } else {
                        if(textView.equals(textToDelete)) {
                            delete.setVisibility(View.INVISIBLE);
                            toDelete = null;
                            textToDelete = null;
                            textView.setTextColor(Color.parseColor("#000000"));
                        } else {
                            toDelete = x;
                            textToDelete.setTextColor(Color.parseColor("#000000"));
                            textToDelete = textView;
                            textView.setTextColor(Color.parseColor("#2980ff"));
                        }
                    }
                }
            });
            layout.addView(textView);
        }
    }

    public void delete(View view) {
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        String groupId = sp.getString("groupId", null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String[] set = toDelete.split(" ");
        long time1 = 0;
        long time2 = 0;
        try {
            time1 = sdf.parse(set[1]+" "+set[2]).getTime();
            time2 = sdf.parse(set[3]+" "+set[4]).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.deleteDate(groupId, set[0], time1, time2);
        recreate();
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