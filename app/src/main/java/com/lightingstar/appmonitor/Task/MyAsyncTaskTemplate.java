package com.lightingstar.appmonitor.Task;

import android.os.AsyncTask;

public class MyAsyncTaskTemplate extends AsyncTask<IMyAsyncTask,Integer, Object> {
    IMyAsyncTask taskObject;
    @Override
    protected Object doInBackground(IMyAsyncTask... objects) {
        taskObject = objects[0];
        return taskObject.doAsyncTask(taskObject.getContext());
    }

    protected void onPostExecute(Object result) {
        taskObject.setResult(result);
    }
}
