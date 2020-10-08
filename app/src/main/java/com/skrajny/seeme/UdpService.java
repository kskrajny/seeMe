package com.skrajny.seeme;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Integer.parseInt;
@SuppressLint("Assert")

public class UdpService extends Service {

    DatabaseHandler db;
    InetAddress inetAddr;
    DatagramSocket socket1;
    DatagramSocket socket2;
    final int remotePort = 10001;
    final Binder binder = new LocalBinder();
    final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdfrmt = new SimpleDateFormat("MM/dd/yyyy");
    Date d = new Date();

    @Override
    public void onCreate() {
        db = DatabaseHandler.getInstance(this);
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String remoteAddr = "192.168.8.101";
            inetAddr = InetAddress.getByName(remoteAddr);
            int port1 = 9090;
            socket1 = new DatagramSocket(port1);
            int port2 = 9091;
            socket2 = new DatagramSocket(port2);
            (new Thread(new udpClient())).start();
            (new Thread(new udpServer())).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class udpClient implements Runnable {
        public void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    String mess = queue.take();
                    String messID = "messID";
                    sendMessage(socket1, remotePort, mess+messID);
                    getMessageClient(messID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class udpServer implements Runnable {
        public void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    getMessageServer();
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

    public void sendMessage(DatagramSocket socket, int port, String mess) {
        try {
            DatagramPacket packet = new DatagramPacket(mess.getBytes(), mess.length(), inetAddr, port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMessageClient(String messID) {
        byte[] buffer = new byte[32];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket1.receive(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String mess = new String(packet.getData()).replaceAll("\0", "");
        String[] set = mess.split(" ");
        switch (parseInt(set[0])) {
            case 6:
                handleAcknowledgment(mess, messID);
                break;
            case 7:
                handleRejection(mess, messID);
                break;
            default:
                throw new Error();
        }
    }

    public void getMessageServer() {
        byte[] buffer = new byte[32];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket2.receive(packet);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        String mess = new String(packet.getData()).replaceAll("\0", "");
        String[] set = mess.split(" ");
        try {
            switch (parseInt(set[0])) {
                case 1:
                    //handleNewMember(mess);
                    break;
                case 2:
                    handleInvitation(mess);
                    break;
                case 3:
                    handleDate(mess);
                    break;
                case 4:
                    //handlePrivateInvitation(mess);
                    break;
                case 5:
                    //handlePrivateMessage(mess);
                    break;
            }
        } catch (Exception e) {
            sendMessage(socket2, remotePort, "7 "+set[set.length-1]);
        }
        sendMessage(socket2, remotePort, "6 "+set[set.length-1]);
    }

    /*!!!!!!!!!!!! Handlers of responses */
    private void handleAcknowledgment(String mess, String messID) {
        try {
            String[] set = mess.split(" ");
            assert(set.length == 2);
            assert(set[1].equals(messID)); // messID
            queue.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRejection(String mess, String messID) {
        try {
            String[] set = mess.split(" ");
            assert(set.length == 2);
            assert(set[1].equals(messID)); // messID
            //TODO make log
            queue.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*!!!!!!!!!!!! Handlers of responses */

    /*!!!!!!!!!!!! Handlers of messages coming to server */
    /*
    private void handlePrivateInvitation(String mess) throws Exception {
        String[] set = mess.split(" ");
        if(set.length != 5 ||
                !(InetAddress.getByName(set[2]) instanceof Inet4Address) ||
                !set[2].matches(getString(R.string.goodChars)) ||
                !set[3].matches(getString(R.string.goodChars)) ||
                !set[4].matches(getString(R.string.goodChars)))
            throw new Exception();
        db.beginTransaction();
        db.execSQL("INSERT INTO chats values("+set[1]+","+set[2]+","+set[3]+");");
        db.endTransaction();
    }

    private void handlePrivateMessage(String mess) throws Exception {
        String[] set = mess.split(" ");
        if(set.length != 5 ||
                !(InetAddress.getByName(set[1]) instanceof Inet4Address) ||
                !set[4].matches(getString(R.string.goodChars)))
            throw new Exception();
        Cursor c = db.rawQuery("SELECT password FROM chats WHERE ip=?", new String[] { set[1] });
        String password = c.getString(c.getColumnIndex("password"));
        if(!password.equals(set[3]))
            throw new Exception();
        String date = DateFormat.format("MM/dd/yyyy", d.getTime()).toString();
        db.beginTransaction();
        db.execSQL("INSERT INTO messages values("+set[1]+","+date+","+set[2]+")");
        db.endTransaction();
    }
    */
    private void handleDate(String mess) throws Exception {
        String[] set = mess.split(" ");
        Date javaDate1 = sdfrmt.parse(set[1]); // start
        Date javaDate2 = sdfrmt.parse(set[2]); // stop
        if(set.length != 6 ||
                !set[3].matches(getString(R.string.goodChars)))
            throw new Exception();
        if(!db.checkGroupID(set[4]))
            throw new Exception();
        db.addDate(set[4], set[3], set[1], set[2]);
    }

    public void handleInvitation(String mess) throws Exception {
        String[] set = mess.split(" ");
        if(set.length != 5 ||
                !set[3].matches(getString(R.string.goodChars)) ||
                !set[4].matches(getString(R.string.goodChars)))
            throw new Exception();
        if(!db.checkGroupID(set[4]))
            throw new Exception();
        String password = sp.getString("password", null);
        if(!password.equals(set[2]))
            throw new Exception();
        db.addGroup(set[3], set[1]);
    }
/*
    public void handleNewMember(String mess) throws Exception {
        String[] set = mess.split(" ");
        if(set.length != 4 ||
                !(InetAddress.getByName(set[2]) instanceof Inet4Address) ||
                set[3].matches(getString(R.string.goodChars)))
            throw new Exception();
        Cursor c = db.rawQuery("SELECT id FROM groups WHERE id=?", new String[] { set[4] });
        if(c.getCount() != 1)
            throw new Exception();
        db.beginTransaction();
        db.execSQL("INSERT INTO "+set[1]+"M values("+set[2]+")");
        db.endTransaction();
    }
 */
    /*!!!!!!!!!!!! End of Handlers for server */
    public void addMessage(String mess) {
        queue.add(mess);
    }

}
