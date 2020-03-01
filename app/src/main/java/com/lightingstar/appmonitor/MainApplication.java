package com.lightingstar.appmonitor;

import android.app.Application;
import android.content.Context;

import com.lightingstar.appmonitor.model.AppRuningInfo;
import com.lightingstar.appmonitor.util.MessageProcessUtil;

import java.util.HashSet;

public class MainApplication extends Application {

    public static HashSet<String> forbiddentPackages;

    public static Context sContext;

    public static MessageProcessUtil messageProcessUtil;

    public static final  String APP_PACKAGE_NAME = "com.lightingstar.appmonitor";

    public static AppRuningInfo appRuningInfo = new AppRuningInfo();


    @Override public void onCreate() {
        super.onCreate();
        sContext = this;
        messageProcessUtil = new MessageProcessUtil(this);

        initForbiddentPackages();
    }

    /**
     * 初始化需要禁止运行的app
     */
    private void initForbiddentPackages(){
        forbiddentPackages = new HashSet<>();
        forbiddentPackages.add("com.lightingstar.studentmonitor");
        forbiddentPackages.add("com.example.android.appusagestatistics");
    }
}
