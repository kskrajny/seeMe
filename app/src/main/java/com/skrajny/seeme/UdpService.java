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
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Integer.parseInt;
@SuppressLint("Assert")

public class UdpService extends Service {

    DatabaseHandler db;
    DatagramSocket socket1;
    DatagramSocket socket2;
    final Binder binder = new LocalBinder();
    final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    SharedPreferences sp;
    int port1 = 9090;
    int port2 = 9091;
    final long DAY = 1000 * 60 * 60 * 24;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        db = DatabaseHandler.getInstance(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            socket1 = new DatagramSocket(port1);
            socket1.setSoTimeout(3000);
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
            Message p = null;
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    p = queue.take();
                    String mess = p.message;
                    String where = p.where;
                    String messID = RandomString.getAlphaNumericString();
                    long date = (new Date()).getTime();
                    Log.i("seeme", p.deadline+" "+date);
                    if(p.deadline >= date) {
                        sendMessage(socket1, port2, mess + " " + messID, where);
                        getMessageClient(messID);
                    }
                } catch (SocketTimeoutException e) {
                    queue.add(p);
                } catch (Exception e) {}
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

    public void sendMessage(DatagramSocket socket, int port, String mess, String where) {
        Log.i("UdpService", "SendMessage "+mess+" "+where);
        InetAddress inetAddr = null;
        try {
            inetAddr = InetAddress.getByName(where);
        } catch (UnknownHostException e) {
            List<String> ip = db.getIp(where);
            for(String x : ip) {
                try {
                    inetAddr = InetAddress.getByName(x);
                    DatagramPacket packet = new DatagramPacket(mess.getBytes(), mess.length(), inetAddr, port);
                    socket.send(packet);
                } catch (Exception ex) { }
            }
            return;
        }
        try {
            DatagramPacket packet = new DatagramPacket(mess.getBytes(), mess.length(), inetAddr, port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMessageClient(String messID) throws Exception {
        byte[] buffer = new byte[32];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket1.receive(packet);
        String mess = new String(packet.getData()).replaceAll("\0", "");
        String[] set = mess.split(" ");
        Log.i("udpService", "GetMessageClient "+mess);
        switch (parseInt(set[0])) {
            case 6:
                /* $type=6 $messID */
                handleAcknowledgment(mess, messID);
                break;
            case 7:
                /* $type=7 $messID */
                handleRejection(mess, messID);
                break;
            default:
                throw new Error();
        }
    }

    public void getMessageServer() {
        byte[] buffer = new byte[100];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket2.receive(packet);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        String mess = new String(packet.getData()).replaceAll("\0", "");
        String[] set = mess.split(" ");
        Log.i("udpService", "GetMessageServer "+mess);
        String where = null;
        try {
            switch (parseInt(set[0])) {
                case 1:
                    /* $type=1 $groupId $ip $messID */
                    where = handleNewMember(mess);
                    break;
                case 2:
                    /* $type=2 $groupId $password $groupName $messID */
                    where = handleInvitation(mess);
                    break;
                case 3:
                    /* $type=3 $date1 $date2 $name $groupId $messID */
                    where = handleDate(mess);
                    break;
                case 4:
                    //handlePrivateInvitation(mess);
                    break;
                case 5:
                    //handlePrivateMessage(mess);
                    break;
            }
        } catch (Exception e) {
            sendMessage(socket2, port1, "7 "+set[set.length-1], where);
            return;
        }
        sendMessage(socket2, port1, "6 "+set[set.length-1], where);
    }

    /*!!!!!!!!!!!! Handlers of responses */
    private void handleAcknowledgment(String mess, String messID) {
        try {
            String[] set = mess.split(" ");
            assert(set.length == 2);
            assert(set[1].equals(messID)); // messID
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

    /* $type=3 $name $date1 $date2 $groupId $messID */
    private String handleDate(String mess) throws Exception {
        String[] set = mess.split(" ");
        Date javaDate1 = sdfrmt.parse(set[2]+" "+set[3]);
        Date javaDate2 = sdfrmt.parse(set[4]+" "+set[5]);
        if(set.length != 8 ||
                !set[1].matches(getString(R.string.goodChars)) ||
                !set[7].matches(getString(R.string.goodChars)))
            throw new Exception();
        if(!db.checkGroupID(set[6]))
            throw new Exception();
        db.addDate(set[6], set[1], javaDate1.getTime(), javaDate2.getTime());
        return set[6];
    }

    /* $type=2 $groupId $password $groupName $messID */
    public String handleInvitation(String mess) throws Exception {
        String[] set = mess.split(" ");
        if(set.length != 5 ||
                !set[1].matches(getString(R.string.goodChars)) ||
                !set[3].matches(getString(R.string.goodChars)) ||
                !set[4].matches(getString(R.string.goodChars)))
            throw new Exception();
        String password = sp.getString("password", "password");
        if(!password.equals(set[2]))
            throw new Exception();
        db.addGroup(set[3], set[1]);
        return set[1];
    }

    /* $type=1 $groupId $ip $messID */
    public String handleNewMember(String mess) throws Exception {
        String[] set = mess.split(" ");
        if(set.length != 4 ||
                !(InetAddress.getByName(set[2]) instanceof Inet4Address) ||
                !set[3].matches(getString(R.string.goodChars)))
            throw new Exception();
        if(!db.checkGroupID(set[1]))
            throw new Exception();
        db.addMember(set[1], set[2]);
        return set[1];
    }

    /*!!!!!!!!!!!! End of Handlers for server */
    public void addMessage(String mess, String where) {
        queue.add(new Message(mess, where, (new Date()).getTime()+DAY));
    }

}
