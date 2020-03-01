package com.lightingstar.appmonitor.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.lightingstar.appmonitor.MainActivity;
import com.lightingstar.appmonitor.MainApplication;
import com.lightingstar.appmonitor.model.AppRuningInfo;

public class MessageProcessUtil {
    private Context context;
    private AlertDialog dialog;
    private boolean isShow = false;

    public MessageProcessUtil(Context context){
        this.context = context;

        this.dialog = this.prepareDialog();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isShow) {
                isShow = true;
                dialog.setMessage(msg.getData().getCharSequence("data"));
                dialog.show();
            }
        }
    };

    public void auditAppInfo(String packageName){

        //记录app运行时间和计数
        recordAppRunningInfo(packageName);

        //判断是否要禁止运行该App
        if (MainApplication.forbiddentPackages.contains(packageName)){
            sendMessage(packageName);
        }
    }

    private void sendMessage(String packageName){
        Message msg = new Message() ;
        msg.arg1 = 1 ;
        Bundle bundle = new Bundle() ;
        bundle.putString("data" , packageName);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void recordAppRunningInfo(String newPackageName){
        AppRuningInfo lastAppRunningInfo = new AppRuningInfo();
        lastAppRunningInfo.setPackageName(MainApplication.appRuningInfo.getPackageName());
        lastAppRunningInfo.setStartTime(MainApplication.appRuningInfo.getStartTime());
        lastAppRunningInfo.setEndTime(System.currentTimeMillis());

        MainApplication.appRuningInfo.setPackageName(newPackageName);
        MainApplication.appRuningInfo.setStartTime(System.currentTimeMillis());
    }

    private AlertDialog prepareDialog(){
        AlertDialog dialog = new AlertDialog.Builder(context.getApplicationContext()).setTitle("提示窗口")
                .setCancelable(false)
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isShow = false;
                        dialog.dismiss();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }


        return dialog;
    }
}
