package com.skrajny.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class AddMemberActivity extends AppCompatActivity {

    EditText ipText;
    EditText passwordText;
    SharedPreferences sp;
    DatabaseHandler db;
    String groupId;
    String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        ipText = findViewById(R.id.ip);
        passwordText = findViewById(R.id.password);
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        group = sp.getString("group", "private");
        groupId = sp.getString("groupId", null);
        if(groupId == null)
            finish();
        db = DatabaseHandler.getInstance(this);
        setHeaderFooter();
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
        if(!(address instanceof Inet4Address) || db.checkIP(groupId, ipString)) {
            //TODO obsłużyć wyjątek
        } else {
            // save new member locally
            db.addMember(groupId, ipString);
            // invite new member to join group
            Intent myIntent = new Intent(AddMemberActivity.this, SendMessageActivity.class);
            myIntent.putExtra("mess", createMessage(new String[] {"2", groupId, password, group}));
            myIntent.putExtra("where", ipString);
            startActivity(myIntent);
            // send members data to the new friend
            // send data of the new friend to old members
            List<String> listIp = db.getIp(groupId);
            for(String ip : listIp) {
                Intent myIntent1 = new Intent(AddMemberActivity.this, SendMessageActivity.class);
                myIntent1.putExtra(
                        "mess",
                        createMessage(new String[] {"1", groupId, ip })
                );
                myIntent1.putExtra("where", ipString);
                startActivity(myIntent1);
            }
            Intent myIntent2 = new Intent(AddMemberActivity.this, SendMessageActivity.class);
            myIntent2.putExtra(
                    "mess",
                    createMessage(new String[] {"1", groupId, ipString })
            );
            myIntent2.putExtra("where", groupId);
            startActivity(myIntent2);
        }
    }

    public String createMessage(String[] args) {
        StringBuilder mess = new StringBuilder();
        for(String x : args){
            mess.append(x+" ");
        }
        mess.deleteCharAt(mess.length()-1);
        return mess.toString();
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