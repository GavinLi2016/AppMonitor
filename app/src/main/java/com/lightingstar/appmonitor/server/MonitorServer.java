package com.lightingstar.appmonitor.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class MonitorServer extends Service {
    private final Messenger serverMsger = new Messenger(new MessageHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return serverMsger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }
}
