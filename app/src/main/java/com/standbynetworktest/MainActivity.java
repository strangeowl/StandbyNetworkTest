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
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER);
        int p=dp(32); root.setPadding(p,p,p,p);
        TextView title=new TextView(this); title.setText("Standby Network Test — Screen-Off Partial Lock"); title.setTextSize(24); root.addView(title);
        status=new TextView(this); status.setTextSize(18); status.setPadding(0,dp(24),0,dp(24)); root.addView(status);
        Button start=new Button(this); start.setText("Arm Test");
        start.setOnClickListener(v->{Intent i=new Intent(this,WakeLockService.class);i.setAction(WakeLockService.ACTION_ARM);startForegroundService(i);status.postDelayed(this::refresh,300);}); root.addView(start);
        Button stop=new Button(this); stop.setText("Stop Test");
        stop.setOnClickListener(v->{Intent i=new Intent(this,WakeLockService.class);i.setAction(WakeLockService.ACTION_STOP);startService(i);status.postDelayed(this::refresh,300);});
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-2,-2);sp.topMargin=dp(16);root.addView(stop,sp);
        TextView note=new TextView(this); note.setText("ARMED = no wake lock while screen is on.\nWhen SCREEN_OFF is received, the app acquires a PARTIAL_WAKE_LOCK.\nNo WifiLock. No stayon changes.");
        note.setTextSize(16);note.setGravity(Gravity.CENTER);note.setPadding(0,dp(28),0,0);root.addView(note);
        setContentView(root);
    }
    @Override protected void onResume(){super.onResume();refresh();}
    private void refresh(){status.setText(WakeLockService.statusText());}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
