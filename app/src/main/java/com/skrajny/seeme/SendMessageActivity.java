package com.skrajny.seeme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SendMessageActivity extends AppCompatActivity {

    private String mess;
    private String address = null;
    private UdpService mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mess = getIntent().getExtras().getString("mess");
        address = getIntent().getExtras().getString("address");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UdpService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        share();
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    public void share() {
        Log.i("seeMe", mess);
        if(mBound) {
            if(address == null)
                mService.addMessage(mess);
            else
                mService.addMessage(mess+"$"+address);
            finish();
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            UdpService.LocalBinder binder = (UdpService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}