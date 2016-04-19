package com.example.qian.test;

import android.app.Application;
import android.content.Context;

import com.zhaoxiaodan.miband.MiBand;

/**
 * Created by john on 2016/2/22.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;


    private String band_addr;
    private MiBand miBand;
    public static final String API_KEY="a4356d015a1e20d94526e87b85cb8d50";


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }

    public String getBand_addr() {
        return band_addr;
    }

    public void setBand_addr(String band_addr) {
        this.band_addr = band_addr;
    }

    public MiBand getMiBand() {
        return miBand;
    }

    public void setMiBand(MiBand miBand) {
        this.miBand = miBand;
    }
    public static MyApplication getsInstance(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}
