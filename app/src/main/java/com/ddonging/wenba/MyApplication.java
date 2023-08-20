package com.ddonging.wenba;

import android.app.Application;

import com.ddonging.wenba.run.WebViewGm;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WebViewGm web=new WebViewGm(this);
    }

}
