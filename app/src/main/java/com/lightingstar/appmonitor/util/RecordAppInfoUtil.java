package com.lightingstar.appmonitor.util;

import com.lightingstar.appmonitor.MainApplication;
import com.lightingstar.appmonitor.model.AppRuningInfo;

public class RecordAppInfoUtil {

    public void recordAppRunningInfo(String newPackageName){
        AppRuningInfo lastAppRunningInfo = new AppRuningInfo();
        lastAppRunningInfo.setPackageName(MainApplication.appRuningInfo.getPackageName());
        lastAppRunningInfo.setStartTime(MainApplication.appRuningInfo.getStartTime());
        lastAppRunningInfo.setEndTime(System.currentTimeMillis());

        MainApplication.appRuningInfo.setPackageName(newPackageName);
        MainApplication.appRuningInfo.setStartTime(System.currentTimeMillis());
    }
}
