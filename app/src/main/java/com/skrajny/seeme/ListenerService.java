package com.skrajny.seeme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class ListenerService extends Service {

    int port;
    DatagramPacket request;
    DatagramSocket socket;
    InetAddress addr;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        port = 9090;
        try {
            addr = InetAddress.getByName("192.168.8.100");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket(port);
            //noinspection InfiniteLoopStatement
            /*
            while(true) {
                request = new DatagramPacket(new byte[32], 32);
                socket.receive(request);
                Log.i("seeMe", new String(request.getData()));
                byte[] buffer = "OK".getBytes();
                InetAddress clientAddress = request.getAddress();
                int clientPort = request.getPort();
                DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(response);
            }
            */
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    DatagramPacket response = new DatagramPacket("OK".getBytes(), 2, addr, port);
                    try {
                        socket.send(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000);//put here time 1000 milliseconds=1 second
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
