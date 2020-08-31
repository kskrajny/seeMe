package com.skrajny.seeme;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class SendMessageActivity extends AppCompatActivity {

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        date = getIntent().getExtras().getString("date");
    }

    public void shareTime(View view) {
        Thread thread = new sender(9090, "192.168.8.100", date);
        thread.start();
        try {
            thread.join();
        } catch(Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    private static class sender extends Thread {

        private final int port;
        private final String addr;
        private final byte[] buff;
        private final byte[] receiveData;

        public sender(int port, String addr, String mess) {
            this.port = port;
            this.addr = addr;
            this.buff = mess.getBytes();
            receiveData = new byte[2];
        }

        public void run() {
            try {
                InetAddress inetAddress = InetAddress.getByName(addr);
                DatagramSocket socket = new DatagramSocket(port);
                socket.setSoTimeout(10000);
                DatagramPacket send_packet = new DatagramPacket(buff, buff.length, inetAddress, port);
                socket.send(send_packet);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                if (!(new String(receivePacket.getData()).equals("OK")))
                    throw new Error();
                Log.i("seeMe", new String(receivePacket.getData()));
            } catch (SocketTimeoutException e) {
                Log.i("seeMe", "failed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}