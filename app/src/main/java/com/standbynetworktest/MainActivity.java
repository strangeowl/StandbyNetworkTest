package com.standbynetworktest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        int p = dp(32);
        root.setPadding(p, p, p, p);

        TextView title = new TextView(this);
        title.setText("Standby Network Test");
        title.setTextSize(28);
        root.addView(title);

        status = new TextView(this);
        status.setTextSize(20);
        status.setPadding(0, dp(24), 0, dp(24));
        root.addView(status);

        Button start = new Button(this);
        start.setText("Start Partial Wake Lock");
        start.setOnClickListener(v -> {
            Intent i = new Intent(this, WakeLockService.class);
            i.setAction(WakeLockService.ACTION_START);
            startForegroundService(i);
            refresh();
        });
        root.addView(start, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Button stop = new Button(this);
        stop.setText("Stop Partial Wake Lock");
        stop.setOnClickListener(v -> {
            Intent i = new Intent(this, WakeLockService.class);
            i.setAction(WakeLockService.ACTION_STOP);
            startService(i);
            refresh();
        });
        LinearLayout.LayoutParams stopParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        stopParams.topMargin = dp(16);
        root.addView(stop, stopParams);

        TextView note = new TextView(this);
        note.setText("No WifiLock. No stayon changes.\nLeave this enabled and let the TV enter standby normally.");
        note.setTextSize(16);
        note.setGravity(Gravity.CENTER);
        note.setPadding(0, dp(28), 0, 0);
        root.addView(note);

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        status.setText(WakeLockService.isHeld()
                ? "Status: PARTIAL WAKE LOCK ACTIVE"
                : "Status: INACTIVE");
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
