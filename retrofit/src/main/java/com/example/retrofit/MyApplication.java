package com.example.retrofit;

import android.app.Application;
import android.content.Context;

/**
 * @author RH
 * @date 2018/3/6
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
