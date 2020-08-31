package com.skrajny.seeme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeSet;

import static com.skrajny.seeme.R.layout.activity_delete;

public class DeleteActivity extends AppCompatActivity {

    SharedPreferences sp;
    LinearLayout layout;
    Button delete;
    String toDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_delete);
        sp = getSharedPreferences("name", MODE_PRIVATE);
        layout = findViewById(R.id.layout);
        delete = findViewById(R.id.delete);
        toDelete = null;
        TreeSet<String> sortedSet = new TreeSet<String>(sp.getAll().keySet());
        for(final String x : sortedSet) {
            if(x.equals("name")) continue;
            final TextView textView = new TextView(this);
            textView.setText(x);
            textView.setTextSize(20);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toDelete == null) {
                        delete.setVisibility(View.VISIBLE);
                        toDelete = x;
                        textView.setTextSize(25);
                    } else {
                        delete.setVisibility(View.INVISIBLE);
                        toDelete = null;
                        textView.setTextSize(20);
                    }
                }
            });
            layout.addView(textView);
        }
    }

    public void delete(View view) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(toDelete);
        edit.commit();
        recreate();
    }
}