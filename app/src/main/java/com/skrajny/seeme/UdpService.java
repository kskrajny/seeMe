package com.skrajny.seeme;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class UdpService extends Service {

    private InetAddress inetAddr;
    private DatagramSocket socket1;
    private DatagramSocket socket2;
    private final int port1 = 9090;
    private final int port2 = 9091;
    private final Timer timer1 = new Timer();
    private final Binder binder = new LocalBinder();
    private final Queue<String> queue = new LinkedList<>();


    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("seeMe", "startService in MAIN");
        try {
            inetAddr = InetAddress.getByName("192.168.8.100");
            socket1 = new DatagramSocket(port1);
            socket1.setSoTimeout(10000);
            socket2 = new DatagramSocket(port2);
            socket1.setSoTimeout(9000);
            socket2.setSoTimeout(9000);
            timer1.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Log.i("seeMe", "sender");
                    try {
                        String mess = queue.peek();
                        if(mess != null) {
                            sendMessage(socket1, port1, mess);
                            if (getMessage(socket2).equals("OK")) {
                                queue.remove();
                                Log.i("seeMe", queue.toString());
                            }
                        }
                    } catch (SocketTimeoutException ignored) {}
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10000);
            new Timer().scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Log.i("seeMe", "listener");
                    String mess;
                    try {
                        mess = getMessage(socket1);
                        sendMessage(socket2, port2, "OK");
                    } catch(SocketTimeoutException ignored) {}
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10000);
        } catch (Exception e) {
            Log.i("seeMe", "WYJĄTEK W ON START");
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class LocalBinder extends Binder {
        UdpService getService() {
            return UdpService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void sendMessage(DatagramSocket socket, int port, String mess) throws Exception {
        DatagramPacket packet = new DatagramPacket(mess.getBytes(), mess.length(), inetAddr, port);
        socket.send(packet);
    }

    private String getMessage(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[32];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String mess = new String(packet.getData()).replaceAll("\0", "");
        Log.i("seeMe", "ODEBRANO "+mess);
        return mess;
    }

    public void addMessage(String mess) {
        queue.add(mess);
    }

}