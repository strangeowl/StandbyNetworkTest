package com.standbynetworktest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        int p=dp(32); root.setPadding(p,p,p,p);

        TextView title=new TextView(this); title.setText("Standby Network Test — WifiLock Only"); title.setTextSize(26); root.addView(title);
        status=new TextView(this); status.setTextSize(20); status.setPadding(0,dp(24),0,dp(24)); root.addView(status);

        Button start=new Button(this); start.setText("Start WifiLock");
        start.setOnClickListener(v->{ Intent i=new Intent(this,WakeLockService.class); i.setAction(WakeLockService.ACTION_START); startForegroundService(i); status.postDelayed(this::refresh,300); });
        root.addView(start);

        Button stop=new Button(this); stop.setText("Stop WifiLock");
        stop.setOnClickListener(v->{ Intent i=new Intent(this,WakeLockService.class); i.setAction(WakeLockService.ACTION_STOP); startService(i); status.postDelayed(this::refresh,300); });
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-2,-2); sp.topMargin=dp(16); root.addView(stop,sp);

        TextView note=new TextView(this);
        note.setText("WifiLock only. NO Partial Wake Lock. NO stayon changes.\nLet the TV reach its normal 15-minute automatic standby.");
        note.setTextSize(16); note.setGravity(Gravity.CENTER); note.setPadding(0,dp(28),0,0); root.addView(note);
        setContentView(root);
    }

    @Override protected void onResume(){ super.onResume(); refresh(); }
    private void refresh(){ status.setText(WakeLockService.isHeld() ? "Status: WIFI LOCK ACTIVE" : "Status: INACTIVE"); }
    private int dp(int v){ return Math.round(v*getResources().getDisplayMetrics().density); }
}
