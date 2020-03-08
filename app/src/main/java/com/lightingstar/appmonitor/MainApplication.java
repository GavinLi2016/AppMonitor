package com.lightingstar.appmonitor;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.multidex.MultiDex;

import com.lightingstar.appmonitor.Task.MyAsyncTaskTemplate;
import com.lightingstar.appmonitor.Task.QueryAppInfoTask;
import com.lightingstar.appmonitor.model.AppRuningInfo;
import com.lightingstar.appmonitor.util.LogUtil;
import com.lightingstar.appmonitor.util.sdkinit.ANRWatchDogInit;
import com.lightingstar.appmonitor.util.sdkinit.UMengInit;
import com.lightingstar.appmonitor.util.sdkinit.XBasicLibInit;
import com.lightingstar.appmonitor.util.sdkinit.XUpdateInit;

import java.util.HashSet;

public class MainApplication extends Application {

    public static HashSet<String> forbiddentPackages;

    public static Context sContext;

    public static AppRuningInfo appRuningInfo = new AppRuningInfo();

    private static QueryAppInfoTask queryAppInfoTask;
    private static MyAsyncTaskTemplate myAsyncTaskTemplate = new MyAsyncTaskTemplate();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }

    @Override public void onCreate() {
        super.onCreate();
        sContext = this;

        this.initLibs();
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        XBasicLibInit.init(this);

        XUpdateInit.init(this);

        //运营统计数据运行时不初始化
        if (!MainApplication.isDebug()) {
            UMengInit.init(this);
        }

        //ANR监控
        ANRWatchDogInit.init();

        this.initApp();
    }

    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    private void initApp(){
        //XUI.init(this); //初始化UI框架
        //XUI.debug(true);  //开启UI框架调试日志

        initForbiddentPackages();

        queryAppInfoTask = new QueryAppInfoTask(this);
        myAsyncTaskTemplate.execute(queryAppInfoTask);
        //installedAppInfoTask.setAppInfos((List<AppBasicInfo>) );
    }

    /**
     * 初始化需要禁止运行的app
     */
    private void initForbiddentPackages(){
        forbiddentPackages = new HashSet<>();
        forbiddentPackages.add("com.lightingstar.appmonitor");
        forbiddentPackages.add("com.example.android.appusagestatistics");
    }

    private static Messenger clientMsger;

    public static void setMessage(Messenger messenger){
        clientMsger = messenger;
    }

    public static void sendMessage(String msgContent, int msgType){
        if (clientMsger==null){
            LogUtil.info("info","no client msg sender");
            return;
        }
        Message msg = new Message() ;
        msg.arg1 = 1 ;
        Bundle bundle = new Bundle() ;
        bundle.putString("data" , msgContent);
        msg.setData(bundle);
        msg.what = msgType;
        try {
            clientMsger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
