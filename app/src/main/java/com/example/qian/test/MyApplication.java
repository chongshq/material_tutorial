package com.example.qian.test;

import android.app.Application;
import android.content.Context;

import java.security.PublicKey;

/**
 * Created by john on 2016/2/22.
 */
public class MyApplication extends Application {
    public static final String API_KEY="a4356d015a1e20d94526e87b85cb8d50";
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }
    public static MyApplication getsInstance(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}
