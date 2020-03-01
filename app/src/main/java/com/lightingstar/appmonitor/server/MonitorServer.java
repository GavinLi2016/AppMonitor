package com.lightingstar.appmonitor.server;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;

import com.lightingstar.appmonitor.MainActivity;
import com.lightingstar.appmonitor.model.AppConstance;
import com.lightingstar.appmonitor.util.DialogUtil;
import com.lightingstar.appmonitor.util.RecordAppInfoUtil;

import java.util.List;

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
        this.dialogUtil = new DialogUtil(this);
        this.recordAppInfoUtil = new RecordAppInfoUtil();

        return serverMsger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*ActivityManager mActivityManager =(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);


        boolean isRun=isRunningProcess(mActivityManager);
        //Log.i(TAG, String.format("onCreate: %s %s pid=%d uid=%d isRun=%s", mPackName,process, Process.myPid(), Process.myUid(),isRun));

        if(!isRun){
            Intent intent = getPackageManager().getLaunchIntentForPackage(AppConstance.APP_PACKAGE_NAME);
            if(intent!=null){
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }*/
    }

    /**
     * 进程是否存活
     * @return
     */
    public static boolean isRunningProcess(ActivityManager manager) {
        if(manager==null)
            return false;
        List<ActivityManager.RunningAppProcessInfo> runnings = manager.getRunningAppProcesses();
        if (runnings != null) {
            for (ActivityManager.RunningAppProcessInfo info : runnings) {
                if(TextUtils.equals(info.processName,AppConstance.APP_PACKAGE_NAME)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
