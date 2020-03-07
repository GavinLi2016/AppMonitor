package com.lightingstar.appmonitor.server;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import com.lightingstar.appmonitor.MainApplication;
import com.lightingstar.appmonitor.model.AppConstance;
import com.lightingstar.appmonitor.util.LogUtil;

public class WindowMonitorService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtil.info("server","server is running now");
        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if (Build.VERSION.SDK_INT >= 16) {
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        }

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();
            if (packageName == null) {
                return;
            }
            //窗口还是上次的app打开的
            if (packageName.equals(MainApplication.appRuningInfo.getPackageName()) ||
                    packageName.equals(AppConstance.APP_PACKAGE_NAME ) ) {
                return;
            }

            MainApplication.sendMessage(event.getPackageName().toString(), AppConstance.WIN_CHANGE_MSG);

            /*ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()
            );

            ActivityInfo activityInfo = tryGetActivity(componentName);
            if (activityInfo != null) {
                LogUtil.info("Current Package", componentName.getPackageName());
                //用户打开了另外一个app，发送通知消息
                MainApplication.sendMessage(componentName.getPackageName(), AppConstance.WIN_CHANGE_MSG);
            }*/
        }

    }

    /*private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }*/

    @Override
    public void onInterrupt() {}
}
