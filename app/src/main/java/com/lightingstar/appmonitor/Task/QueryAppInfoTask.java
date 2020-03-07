package com.lightingstar.appmonitor.Task;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.lightingstar.appmonitor.model.AppBasicInfo;

import java.util.ArrayList;
import java.util.List;

public class QueryAppInfoTask implements IMyAsyncTask {
    List<AppBasicInfo> appInfos;
    Context context;

    public QueryAppInfoTask(Context context){
        this.context = context;
    }

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
                appInfo.setPackageName(packageInfo.packageName); //获取应用包名，可用于卸载和启动应用
                appInfo.setName(packageInfo.versionName);//获取应用版本名
                appInfo.setVersion(packageInfo.versionCode);//获取应用版本号
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
