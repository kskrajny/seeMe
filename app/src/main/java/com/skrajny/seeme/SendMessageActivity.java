package com.skrajny.seeme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public class SendMessageActivity extends AppCompatActivity {

    private String mess;
    private String where;
    private UdpService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mess = getIntent().getExtras().getString("mess");
        where = getIntent().getExtras().getString("where");
        Intent intent = new Intent(SendMessageActivity.this, UdpService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    public void share() {
        mService.addMessage(mess, where);
        finish();
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            UdpService.LocalBinder binder = (UdpService.LocalBinder) service;
            mService = binder.getService();
            share();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };
}