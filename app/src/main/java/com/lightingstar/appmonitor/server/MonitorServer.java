package com.lightingstar.appmonitor.server;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.lightingstar.appmonitor.activity.MainActivity;
import com.lightingstar.appmonitor.MyApp;
import com.lightingstar.appmonitor.model.AppConstance;
import com.lightingstar.appmonitor.util.DialogUtil;
import com.lightingstar.appmonitor.util.RecordAppInfoUtil;

public class MonitorServer extends Service {

    private DialogUtil dialogUtil;
    private RecordAppInfoUtil recordAppInfoUtil;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String msgContent = msg.getData().get("data").toString();

            switch (msg.what){
                case AppConstance.WIN_CHANGE_MSG: {
                    recordAppInfoUtil.recordAppRunningInfo(msgContent);
                    dialogUtil.showDialog(msgContent);
                    break;
                }
                case AppConstance.APP_CLOSE_MSG: {
                    Intent mBootIntent = new Intent(getApplicationContext(), MainActivity.class);
                    //下面这句话必须加上才能开机自动运行app的界面
                    mBootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(mBootIntent);
                    break;
                }
            }
        }
    };

    private final Messenger serverMsger = new Messenger(handler);

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.dialogUtil = new DialogUtil(this);
        this.recordAppInfoUtil = new RecordAppInfoUtil();
        MyApp.setMessage(serverMsger);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(AppConstance.ACTION_RESTART);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(new ComponentName(AppConstance.APP_PACKAGE_NAME,
                BootBroadcastReceiver.class.getName()));

        sendBroadcast(intent);
        super.onDestroy();
    }
}
