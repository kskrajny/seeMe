package com.skrajny.seeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TreeSet;

public class AddMemberActivity extends AppCompatActivity {

    EditText ipText;
    EditText passwordText;
    SharedPreferences spGroup;
    String groupName;
    String password;
    SharedPreferences spMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        ipText = findViewById(R.id.ip);
        passwordText = findViewById(R.id.password);
        spGroup = getSharedPreferences("group", MODE_PRIVATE);
        groupName = spGroup.getString("curr_group", "lama");
        spMembers = getSharedPreferences("members"+groupName, MODE_PRIVATE);
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
            SharedPreferences.Editor edit = spMembers.edit();
            edit.putString(ipString, ".");
            edit.commit();
            // invite new member to join group
            Intent myIntent = new Intent(AddMemberActivity.this, SendMessageActivity.class);
            myIntent.putExtra("mess", "invite "+groupName+" "+password);
            startActivity(myIntent);
            // send members data to the new friend
            Intent myIntent1 = new Intent(AddMemberActivity.this, SendMessageActivity.class);
            myIntent1.putExtra("mess", "member "+groupName+" "+ipString);
            startActivity(myIntent1);
            // send data of the new friend to old members
            for(String x : spMembers.getAll().keySet()) {
                Intent myIntent2 = new Intent(AddMemberActivity.this, SendMessageActivity.class);
                myIntent2.putExtra("mess", "member "+groupName+" "+ipString);
                myIntent2.putExtra("address", ipString);
                startActivity(myIntent2);
            }
            //TODO przeniesc komunikacje do UdpService
        }
    }
}