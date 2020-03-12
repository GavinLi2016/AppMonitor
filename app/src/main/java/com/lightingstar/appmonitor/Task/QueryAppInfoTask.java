package com.lightingstar.appmonitor.Task;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lightingstar.appmonitor.R;
import com.lightingstar.appmonitor.model.AppBasicInfo;

import java.util.ArrayList;
import java.util.List;

public class QueryAppInfoTask implements IMyAsyncTask {
    List<AppBasicInfo> appInfos;
    Context context;

    public List<AppBasicInfo> getAppInfos() {
        return appInfos;
    }

    public QueryAppInfoTask(Context context){
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public List<AppBasicInfo> doAsyncTask(Context context) {
        List<AppBasicInfo> appInfos = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                AppBasicInfo appInfo = new AppBasicInfo();
                String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();

                appInfo.setPackageName(packageInfo.packageName); //获取应用包名，可用于卸载和启动应用
                appInfo.setName(appName);//获取应用版本名
                appInfo.setVersion(packageInfo.versionName);//获取应用版本号
                try {
                    Drawable appIcon = context.getPackageManager()
                            .getApplicationIcon(packageInfo.packageName);
                    appInfo.setAppIcon(appIcon);
                } catch (PackageManager.NameNotFoundException e) {
                    appInfo.setAppIcon(context.getDrawable(R.drawable.ic_launcher));
                }
                appInfos.add(appInfo);

            } else {
                // 系统应用
            }
        }

        return appInfos;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void setResult(Object result) {
        appInfos = (List<AppBasicInfo>) result;
    }
}
