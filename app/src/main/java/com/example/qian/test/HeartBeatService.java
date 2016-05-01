package com.example.qian.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.zhaoxiaodan.miband.MiBand;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by john on 2016/2/19.
 */
public class HeartBeatService extends Service {
    int iconId;
    String title;
    static Timer timer = null;
    MiBand miBand;


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BackService","bind");
        return null;
    }

    @Override
    public void onCreate() {
        MyApplication myApp = (MyApplication)getApplicationContext();

        miBand=myApp.getMiBand();
        Log.d("BackService","create");
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        int period = 30000;
        int delay = 0;
        if (null == timer ) {
                timer = new Timer();
            }
        Log.d("BackService","start");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                miBand.startHeartRateScan();
                Log.d("BackService","looping");
            }
        },delay,period);
        return super.onStartCommand(intent, flags, startId);
    }
}
