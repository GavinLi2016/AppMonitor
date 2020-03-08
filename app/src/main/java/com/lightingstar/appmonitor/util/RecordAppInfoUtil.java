package com.lightingstar.appmonitor.util;

import com.lightingstar.appmonitor.MyApp;
import com.lightingstar.appmonitor.model.AppRuningInfo;

public class RecordAppInfoUtil {

    public void recordAppRunningInfo(String newPackageName){
        AppRuningInfo lastAppRunningInfo = new AppRuningInfo();
        lastAppRunningInfo.setPackageName(MyApp.appRuningInfo.getPackageName());
        lastAppRunningInfo.setStartTime(MyApp.appRuningInfo.getStartTime());
        lastAppRunningInfo.setEndTime(System.currentTimeMillis());

        MyApp.appRuningInfo.setPackageName(newPackageName);
        MyApp.appRuningInfo.setStartTime(System.currentTimeMillis());
    }
}
