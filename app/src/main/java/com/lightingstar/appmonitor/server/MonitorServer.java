package com.lightingstar.appmonitor.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.lightingstar.appmonitor.util.ForegroundAppUtil;

public class MonitorServer extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            String foregroundActivityName = ForegroundAppUtil.getForegroundActivityName(getApplicationContext());
            Toast.makeText(getApplicationContext(), foregroundActivityName, Toast.LENGTH_SHORT).show();
            handler.postDelayed(r, 5000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(r, 5000);
        return START_STICKY;
    }
}
