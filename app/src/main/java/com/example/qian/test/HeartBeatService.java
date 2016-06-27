package com.example.qian.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private  int period ;
    private TimerTask changeableTask;
    BroadcastReceiver toEmergency = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //timer.cancel();
            timer.cancel();
            timer = new Timer();
            changeableTask = new TimerTask() {
                @Override
                public void run() {
                    miBand.startHeartRateScan();
                    Log.d("BackService","looping emergency");
                }
            };
            timer.schedule(changeableTask,0,30000);
            Log.d("BackService",30000+"");
        }
    };
    BroadcastReceiver toNormal = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            timer.cancel();
            timer = new Timer();
            changeableTask = new TimerTask() {
                @Override
                public void run() {
                    miBand.startHeartRateScan();
                    Log.d("BackService","looping normally");
                }
            };
            timer.schedule(changeableTask,0,period);
            Log.d("BackService",period+"");
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BackService","bind");
        return null;
    }

    @Override
    public void onCreate() {
        MyApplication myApp = (MyApplication)getApplicationContext();
        period = 60000*5;
        miBand=myApp.getMiBand();
        Log.d("BackService","create");
        IntentFilter filter = new IntentFilter(SubActivity.TONORMAL);
        getApplicationContext().registerReceiver(toNormal,filter);
        IntentFilter filter1 = new IntentFilter(SubActivity.TOEMERGENCY);
        getApplicationContext().registerReceiver(toEmergency,filter1);
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        int delay = 0;
        if (null == timer ) {
                timer = new Timer();
            }
        Log.d("BackService","start");
        changeableTask = new TimerTask() {
            @Override
            public void run() {
                miBand.startHeartRateScan();
                Log.d("BackService","looping");
            }
        };
        timer.schedule(changeableTask,delay,period);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BackService","normal stop");
        getApplicationContext().unregisterReceiver(toNormal);
        getApplicationContext().unregisterReceiver(toEmergency);
    }
}
