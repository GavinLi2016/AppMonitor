package com.lightingstar.appmonitor.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lightingstar.appmonitor.activity.MainActivity;
import com.lightingstar.appmonitor.model.AppConstance;
import com.lightingstar.appmonitor.util.LogUtil;


public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";



    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.info(TAG, "Boot this system , BootBroadcastReceiver onReceive()");

        if (intent.getAction().equals(AppConstance.ACTION_BOOT) ||
                intent.getAction().equals(AppConstance.ACTION_RESTART)) {
            LogUtil.info(TAG, "start application");

            Intent mBootIntent = new Intent(context, MainActivity.class);
            //下面这句话必须加上才能开机自动运行app的界面
            mBootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mBootIntent);
        }

    }
}
