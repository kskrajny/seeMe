package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AddMemberActivity extends AppCompatActivity {

    EditText ipText;
    EditText passwordText;
    SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
    SQLiteDatabase db = openOrCreateDatabase("db",MODE_PRIVATE,null);
    String groupId;
    String group;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        ipText = findViewById(R.id.ip);
        passwordText = findViewById(R.id.password);
        group = sp.getString("group", "private");
        groupId = sp.getString("groupId", "private");
    }

    public void addMember(View view) {
        String ipString = String.valueOf(ipText.getText());
        String password = String.valueOf(passwordText.getText());
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ipString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if(!(address instanceof Inet4Address)) {
            //TODO obsłużyć wyjątek
        } else {
            // save new member locally
            db.beginTransaction();
            db.execSQL("INSERT INTO "+groupId+"M values("+ipString+")");
            db.endTransaction();
            // invite new member to join group
            Intent myIntent = new Intent(AddMemberActivity.this, SendMessageActivity.class);
            myIntent.putExtra("mess", createMessage(new String[] {"2", groupId, password, group}));
            startActivity(myIntent);
            // send members data to the new friend
            // send data of the new friend to old members
            Cursor c = db.rawQuery("SELECT ip FROM "+groupId+"M", new String[] {} );
            while (c.moveToNext()) {
                String ip = c.getString(c.getColumnIndex("ip"));
                Intent myIntent1 = new Intent(AddMemberActivity.this, SendMessageActivity.class);
                myIntent1.putExtra(
                        "mess",
                        createMessage(new String[] {"1", groupId, ip })
                );
                myIntent1.putExtra("address", ipString);
                startActivity(myIntent1);
            }
            Intent myIntent2 = new Intent(AddMemberActivity.this, SendMessageActivity.class);
            myIntent2.putExtra(
                    "mess",
                    createMessage(new String[] {"1", groupId, ipString })
            );
            startActivity(myIntent2);
        }
    }

    public String createMessage(String[] args) {
        StringBuilder mess = new StringBuilder();
        for(String x : args){
            mess.append(x);
        }
        return mess.toString();
    }
}