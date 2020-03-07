package com.lightingstar.appmonitor.Task;

import android.content.Context;

public interface IMyAsyncTask {
    Object doAsyncTask(Context context);

    Context getContext();

    void setResult(Object result);
}
