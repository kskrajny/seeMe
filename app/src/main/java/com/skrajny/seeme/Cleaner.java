package com.skrajny.seeme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Cleaner extends Service {

    final long DAY = 1000 * 60 * 60 * 24;
    DatabaseHandler db;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = DatabaseHandler.getInstance(this);
        try {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask(){
                public void run(){
                    db.cleanDates();
                }
            }, 10000, DAY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}