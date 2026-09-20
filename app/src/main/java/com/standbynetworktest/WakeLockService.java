package com.standbynetworktest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;

public class WakeLockService extends Service {
    public static final String ACTION_START="com.standbynetworktest.START";
    public static final String ACTION_STOP="com.standbynetworktest.STOP";
    private static final String CHANNEL_ID="wifilock_test";
    private static final int NOTIFICATION_ID=1002;
    private static WifiManager.WifiLock wifiLock;

    @Override public void onCreate(){
        super.onCreate();
        NotificationManager nm=getSystemService(NotificationManager.class);
        NotificationChannel c=new NotificationChannel(CHANNEL_ID,"WifiLock Test",NotificationManager.IMPORTANCE_LOW);
        c.setDescription("Shows when the standby network WifiLock test is active.");
        nm.createNotificationChannel(c);
    }

    @Override public int onStartCommand(Intent intent,int flags,int startId){
        String action=intent!=null?intent.getAction():null;
        if(ACTION_STOP.equals(action)){
            releaseLock(); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf(); return START_NOT_STICKY;
        }
        startForeground(NOTIFICATION_ID,buildNotification());
        acquireLock();
        return START_STICKY;
    }

    private Notification buildNotification(){
        return new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle("Standby Network Test")
                .setContentText("WifiLock only is active")
                .setSmallIcon(android.R.drawable.stat_sys_wifi)
                .setOngoing(true).build();
    }

    private void acquireLock(){
        if(wifiLock==null){
            WifiManager wm=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiLock=wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,"StandbyNetworkTest:WifiLock");
            wifiLock.setReferenceCounted(false);
        }
        if(!wifiLock.isHeld()) wifiLock.acquire();
    }

    private void releaseLock(){
        if(wifiLock!=null && wifiLock.isHeld()) wifiLock.release();
        wifiLock=null;
    }

    public static boolean isHeld(){ return wifiLock!=null && wifiLock.isHeld(); }
    @Override public void onDestroy(){ releaseLock(); super.onDestroy(); }
    @Override public IBinder onBind(Intent intent){ return null; }
}
