package com.standbynetworktest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;

public class WakeLockService extends Service {
    public static final String ACTION_ARM="com.standbynetworktest.ARM";
    public static final String ACTION_STOP="com.standbynetworktest.STOP";
    private static final String CHANNEL_ID="screenoff_partial_test";
    private static final int NOTIFICATION_ID=1003;
    private static PowerManager.WakeLock wakeLock;
    private static boolean armed=false;
    private BroadcastReceiver screenReceiver;

    @Override public void onCreate(){
        super.onCreate();
        NotificationManager nm=getSystemService(NotificationManager.class);
        NotificationChannel c=new NotificationChannel(CHANNEL_ID,"Screen-Off WakeLock Test",NotificationManager.IMPORTANCE_LOW);
        nm.createNotificationChannel(c);
        screenReceiver=new BroadcastReceiver(){
            @Override public void onReceive(Context context,Intent intent){
                if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction()) && armed) acquirePartial();
                else if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())) releasePartial();
            }
        };
        IntentFilter f=new IntentFilter(); f.addAction(Intent.ACTION_SCREEN_OFF); f.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenReceiver,f);
    }

    @Override public int onStartCommand(Intent intent,int flags,int startId){
        String action=intent!=null?intent.getAction():null;
        if(ACTION_STOP.equals(action)){
            armed=false; releasePartial(); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf(); return START_NOT_STICKY;
        }
        startForeground(NOTIFICATION_ID,notification("Armed — waiting for screen off"));
        armed=true;
        PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
        if(!pm.isInteractive()) acquirePartial();
        else releasePartial();
        return START_STICKY;
    }

    private Notification notification(String text){
        return new Notification.Builder(this,CHANNEL_ID).setContentTitle("Standby Network Test").setContentText(text)
                .setSmallIcon(android.R.drawable.ic_lock_idle_lock).setOngoing(true).build();
    }

    private void acquirePartial(){
        if(wakeLock==null){
            PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
            wakeLock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"StandbyNetworkTest:ScreenOffPartialWakeLock");
            wakeLock.setReferenceCounted(false);
        }
        if(!wakeLock.isHeld()) wakeLock.acquire();
        NotificationManager nm=getSystemService(NotificationManager.class);
        nm.notify(NOTIFICATION_ID,notification("Screen off — Partial Wake Lock ACTIVE"));
    }

    private void releasePartial(){
        if(wakeLock!=null && wakeLock.isHeld()) wakeLock.release();
        wakeLock=null;
        if(armed){
            NotificationManager nm=getSystemService(NotificationManager.class);
            nm.notify(NOTIFICATION_ID,notification("Armed — waiting for screen off"));
        }
    }

    public static String statusText(){
        if(wakeLock!=null && wakeLock.isHeld()) return "Status: SCREEN OFF — PARTIAL WAKE LOCK ACTIVE";
        if(armed) return "Status: ARMED — WAITING FOR SCREEN OFF";
        return "Status: INACTIVE";
    }

    @Override public void onDestroy(){
        armed=false; releasePartial();
        if(screenReceiver!=null) unregisterReceiver(screenReceiver);
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent){return null;}
}
