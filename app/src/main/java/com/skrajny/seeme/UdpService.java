package com.skrajny.seeme;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static java.lang.Integer.parseInt;

public class UdpService extends Service {

    private InetAddress inetAddr;
    private DatagramSocket socket1;
    private DatagramSocket socket2;
    private int port1 = 9090;
    private int port2 = 9091;
    private final int remotePort = 10001;
    private final String remoteAddr = "192.168.8.101";
    private final Binder binder = new LocalBinder();
    private final Queue<String> queue = new LinkedList<>();
    SharedPreferences spTime;
    SharedPreferences spGroup;
    String groupName;

    @Override
    public void onCreate() {
        super.onCreate();
        spGroup = getSharedPreferences("group", MODE_PRIVATE);
        groupName = spGroup.getString("curr_group", "lama");
        spTime = getSharedPreferences("time"+groupName, MODE_PRIVATE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            inetAddr = InetAddress.getByName(remoteAddr);
            socket1 = new DatagramSocket(port1);
            socket2 = new DatagramSocket(port2);
            socket1.setSoTimeout(9000);
            new Timer().scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    try {
                        String mess = queue.peek();
                        if(mess != null) {
                            Log.i("seeme", "Wysylam: "+mess);
                            sendMessage(socket1, remotePort, mess);
                            if (getMessage(socket1).equals("OK")) {
                                queue.remove();
                            }
                        }
                    } catch (SocketTimeoutException ignored) {}
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10000);

            (new Thread(new HelloRunnable())).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class HelloRunnable implements Runnable {
        public void run() {
            try {
                String mess;
                while (true) {
                    mess = getMessage(socket2);

                    sendMessage(socket2, remotePort, "OK");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        handleGottenMess(mess);
        Log.i("seeme", "Odbieram: "+mess);
        return mess;
    }

    public void addMessage(String mess) {
        queue.add(mess);
    }

    private void handleGottenMess(String mess) {
        try {
            String[] set = mess.split(" ");
            String[] dm = set[0].split("/");
            if(dm.length != 2) return;
            if(set.length != 4) return;
            if(parseInt(dm[0]) >= 31 || parseInt(dm[1]) >= 12) return;
            if(parseInt(set[1]) >= 1440) return;
            if(parseInt(set[2]) >= 1440) return;
            if(parseInt(set[1]) >= parseInt(set[2])) return;
            if(!set[3].matches("^[a-zA-Z0-9]+$")) return;
            SharedPreferences.Editor edit = spTime.edit();
            edit.putString(mess, ".");
            edit.commit();
            //TODO powiadomienie o dostarczeniu informacji
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
