package com.lightingstar.appmonitor;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.lightingstar.appmonitor.model.AppRuningInfo;

import java.util.HashSet;

public class MainApplication extends Application {

    public static HashSet<String> forbiddentPackages;

    public static Context sContext;

    public static AppRuningInfo appRuningInfo = new AppRuningInfo();


    @Override public void onCreate() {
        super.onCreate();
        sContext = this;

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

    private static Messenger clientMsger;

    public static void setMessage(Messenger messenger){
        clientMsger = messenger;
    }

    public static void sendMessage(String msgContent, int msgType){
        if (clientMsger==null){
            Log.i("info","no client msg sender");
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
