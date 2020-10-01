package com.skrajny.seeme;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
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
    SharedPreferences spGroup;
    String groupName;

    @Override
    public void onCreate() {
        super.onCreate();
        spGroup = getSharedPreferences("group", MODE_PRIVATE);
        groupName = spGroup.getString("curr_group", "lama");
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
                            if (getMessage(socket1).equals("WRONG")) {
                                throw new Error("Got bad message");
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
                try {
                    sendMessage(socket2, remotePort, "WRONG");
                } catch(Exception e2) {
                    e2.printStackTrace();
                }
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
        if(handleDate(mess)) return mess;
        if(handleInvitation(mess)) return mess;
        if(handleNewMember(mess)) return mess;
        throw new Error();
    }

    public void addMessage(String mess) {
        queue.add(mess);
    }

    private boolean handleDate(String mess) {
        try {
            String[] set = mess.split(" ");
            String[] dm = set[0].split("/");
            if(dm.length != 2) return false;
            if(set.length != 5) return false;
            if(parseInt(dm[0]) >= 31 || parseInt(dm[1]) >= 12) return false;
            if(parseInt(set[1]) >= 1440) return false;
            if(parseInt(set[2]) >= 1440) return false;
            if(parseInt(set[1]) >= parseInt(set[2])) return false;
            if(!set[3].matches("^[a-zA-Z0-9]+$")) return false;
            if(!set[4].matches("^[a-zA-Z0-9]+$")) return false;
            if(!spGroup.getAll().containsKey(set[4])) return false;
            SharedPreferences spTime = getSharedPreferences("time"+set[4], MODE_PRIVATE);
            SharedPreferences.Editor edit = spTime.edit();
            edit.putString(mess.substring(0, mess.lastIndexOf(' ')-1), ".");
            edit.commit();
            return true;
            //TODO powiadomienie o dostarczeniu informacji
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean handleInvitation(String mess) {
        try {
            String[] set = mess.split(" ");
            if(set.length != 2) return false;
            if(!set[0].equals("invite")) return false;
            if(!set[1].matches("^[a-zA-Z0-9]+$")) return false;
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = spGroup.edit();
            edit.putString(set[1], ".");
            edit.commit();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean handleNewMember(String mess) {
        try {
            String[] set = mess.split(" ");
            if(set.length != 3) return false;
            if(!set[0].equals("member")) return false;
            if(!set[1].matches("^[a-zA-Z0-9]+$")) return false;
            if(!(InetAddress.getByName(set[2]) instanceof Inet4Address)) return false;
            SharedPreferences spMembers = getSharedPreferences("members"+set[1], MODE_PRIVATE);
            SharedPreferences.Editor edit = spMembers.edit();
            edit.putString(set[2], ".");
            edit.commit();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
