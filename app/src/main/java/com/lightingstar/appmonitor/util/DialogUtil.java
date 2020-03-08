package com.lightingstar.appmonitor.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.WindowManager;

import com.lightingstar.appmonitor.activity.MainActivity;
import com.lightingstar.appmonitor.MyApp;
import com.lightingstar.appmonitor.model.AppConstance;

import java.util.List;

public class DialogUtil {
    private AlertDialog dialog;
    private boolean isShow = false;
    private Context context;

    public DialogUtil(Context context){
        this.context = context;
        dialog = prepareDialog();
    }

    public void showDialog(String msg){
        //判断是否要禁止运行该App
        if (!MyApp.forbiddentPackages.contains(msg)){
            return;
        }
        if (!isShow) {
            this.isShow = true;
            this.dialog.setMessage(msg);
            this.dialog.show();
        }
    }

    public  void hideDialog(){
        this.isShow = false;
        this.dialog.dismiss();
    }

    private AlertDialog prepareDialog(){
        final Context context = this.context.getApplicationContext();
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("提示窗口")
                .setCancelable(false)
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isShow = false;
                        dialog.dismiss();
                        if (!MyApp.appRuningInfo.getPackageName().equals(AppConstance.APP_PACKAGE_NAME)
                            && !moveToFront()
                           ) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                }).create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }


        return dialog;
    }

    @TargetApi(11)
    protected boolean moveToFront() {
        boolean findBackendTaskFlag = false;
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            for (int i = 0; i < recentTasks.size(); i++){
                //LogUtil.info("xk", "  "+recentTasks.get(i).baseActivity.toShortString() + "   ID: "+recentTasks.get(i).id+"");
                //LogUtil.info("xk","@@@@  "+recentTasks.get(i).baseActivity.toShortString());
                // bring to front
                if (recentTasks.get(i).baseActivity.toShortString().indexOf(AppConstance.APP_PACKAGE_NAME) > -1) {
                    findBackendTaskFlag = true;
                    manager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                    break;
                }
            }
        }

        return findBackendTaskFlag;
    }
}
