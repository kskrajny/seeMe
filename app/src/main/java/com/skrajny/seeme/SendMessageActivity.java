package com.skrajny.seeme;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendMessageActivity extends AppCompatActivity {

    DatagramSocket socket;
    InetAddress group;
    byte[] buf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        buf = getIntent().getExtras().getString("date").getBytes();
    }

    public void multicast(View view) {
        try {
            socket = new DatagramSocket();
            group = InetAddress.getByName("230.0.0.0");
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, group, 4446);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.close();
        finish();
    }
}