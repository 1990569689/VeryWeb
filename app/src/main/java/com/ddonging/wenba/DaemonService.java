package com.ddonging.wenba;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class DaemonService extends Service {

    private static final String TAG = "DaemonService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground();
        startMainProcess();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("App正在运行")
                .setContentText("点击返回App")
                .setSmallIcon(R.drawable.icon)
                .build();
        startForeground(1, notification);
    }

    private void startMainProcess() {
        String packageName = getPackageName();
        String className = IndexActivity.class.getName();
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}