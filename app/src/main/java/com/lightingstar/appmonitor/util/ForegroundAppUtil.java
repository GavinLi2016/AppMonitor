package com.lightingstar.appmonitor.util;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ForegroundAppUtil {
    private static final long END_TIME = System.currentTimeMillis();
    private static final long TIME_INTERVAL = 5 * 1000L;// 7 * 24 * 60 * 60 * 1000L;
    private static final long START_TIME = END_TIME - TIME_INTERVAL;

    /**
     * 获取栈顶的应用包名
     */
    public static String getForegroundActivityName(Context context) {
        String currentClassName = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            currentClassName = "";//manager.getRunningTasks(1).get(0).topActivity.getPackageName();
        } else {
            /*UsageStats initStat = getForegroundUsageStats(context, START_TIME, END_TIME);
            if (initStat != null) {
                currentClassName = initStat.getPackageName();
            }*/
            currentClassName = getForegroundName(context);
        }
        return currentClassName;
    }

    /**
     * 判断当前应用是否在前台
     */
    public static boolean isForegroundApp(Context context) {
        return TextUtils.equals(getForegroundActivityName(context), context.getPackageName());
    }

    /**
     * 获取时间段内，
     */
    public static long getTotleForegroundTime(Context context) {
        UsageStats usageStats = getCurrentUsageStats(context, START_TIME, END_TIME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return usageStats != null ? usageStats.getTotalTimeInForeground() : 0;
        }
        return 0;
    }

    /**
     * 获取记录前台应用的UsageStats对象
     */
    private static UsageStats getForegroundUsageStats(Context context, long startTime, long endTime) {
        UsageStats usageStatsResult = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<UsageStats> usageStatses = getUsageStatsList(context, startTime, endTime);
            if (usageStatses == null || usageStatses.isEmpty()) return null;
            for (UsageStats usageStats : usageStatses) {
                if (usageStatsResult == null || usageStatsResult.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    usageStatsResult = usageStats;
                }
            }
        }
        return usageStatsResult;
    }
    private static String getForegroundName(Context context) {
        String topActivity = "";
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取10分钟之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 5 * 1000, now);
                Log.i(TAG, "Running app number in last 10 minutes : " + stats.size());

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                        Log.i(TAG, "running app is : " + stats.get(i).getPackageName());
                    }
                    topActivity = stats.get(j).getPackageName();
                }
            }
        }*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean isInit = true;
            UsageStatsManager usageStatsManager =
                    (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long ts = System.currentTimeMillis();
            List<UsageStats> queryUsageStats =
                    usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
            UsageEvents usageEvents = usageStatsManager.queryEvents(isInit ? 0 : ts - 5000, ts);
            if (usageEvents == null) {
                return null;
            }

            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents.Event lastEvent = null;
            while (usageEvents.getNextEvent(event)) {
                // if from notification bar, class name will be null
                if (event.getPackageName() == null || event.getClassName() == null) {
                    continue;
                }

                if (lastEvent == null || lastEvent.getTimeStamp() < event.getTimeStamp()) {
                    lastEvent = event;
                }
            }

            if (lastEvent == null) {
                return null;
            }
            topActivity =  lastEvent.getPackageName();
        }

        return topActivity;
    }


    /**
     * 获取记录当前应用的UsageStats对象
     */
    public static UsageStats getCurrentUsageStats(Context context, long startTime, long endTime) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<UsageStats> usageStatses = getUsageStatsList(context, startTime, endTime);
            if (usageStatses == null || usageStatses.isEmpty()) return null;
            for (UsageStats usageStats : usageStatses) {
                if (TextUtils.equals(usageStats.getPackageName(), context.getPackageName())) {
                    return usageStats;
                }
            }
        }
        return null;
    }

    /**
     * 通过UsageStatsManager获取List<UsageStats>集合
     */
    public static List<UsageStats> getUsageStatsList(Context context, long startTime, long endTime) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager manager = (UsageStatsManager) context.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
            //UsageStatsManager.INTERVAL_WEEKLY，UsageStatsManager的参数定义了5个，具体查阅源码
            List<UsageStats> usageStatses = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
            if (usageStatses == null || usageStatses.size() == 0) {// 没有权限，获取不到数据
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
                return null;
            }
            return usageStatses;
        }
        return null;
    }
}
