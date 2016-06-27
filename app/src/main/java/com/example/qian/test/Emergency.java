package com.example.qian.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zhaoxiaodan.miband.MiBand;

import java.util.Timer;
import java.util.TimerTask;

public class Emergency extends Service {
    static Timer timer = null;
    MiBand miBand;


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BackService","emergency_bind");
        return null;
    }

    @Override
    public void onCreate() {
        MyApplication myApp = (MyApplication)getApplicationContext();

        miBand=myApp.getMiBand();
        Log.d("BackService","emergency_create");
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        int period = 30000;
        int delay = 0;
        if (null == timer ) {
            timer = new Timer();
        }
        Log.d("BackService","emergency_start");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                miBand.startHeartRateScan();
                Log.d("BackService","emergency_looping");
            }
        },delay,period);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BackService","emergency stop");
    }
}
