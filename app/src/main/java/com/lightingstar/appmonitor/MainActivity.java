package com.lightingstar.appmonitor;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lightingstar.appmonitor.server.MonitorServer;
import com.lightingstar.appmonitor.util.PermissionsUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //startService(new Intent(getApplicationContext(), MonitorServer.class));
        Intent serviceIntent = new Intent(this, MonitorServer.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);


        this.checkPermission();
    }

    private boolean serviceBound = false;
    private Messenger serverMsger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.w("component name:", name.getClassName());
            serverMsger = new Messenger(binder);
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverMsger = null;
            serviceBound = false;
        }
    };

    /**
     * 检查权限是否开启
     */
    private void checkPermission() {
        //弹出框权限
        String[] permissions = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}; //
        PermissionsUtils.showSystemSetting = true;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            checkAccessibilitySetting();
        }

        @Override
        public void forbitPermissons() {
            //这是没有通过权限的时候提示的内容，自定义即可
            Toast.makeText(getApplicationContext(), "您没有允许部分权限，可能会导致部分功能不能正常使用，如需正常使用 请允许权限", Toast.LENGTH_SHORT).show();
            finish();
            //   Tool.exitApp();
        }

        private void checkAccessibilitySetting(){
            if (!PermissionsUtils.getInstance().isAccessibilitySettingsOn(getApplicationContext())){
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private Messenger clientMsger = new Messenger(h);

    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            if (serverMsger != null) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("data", "close");
                msg.setData(data);
                msg.replyTo = clientMsger;
                try {
                    serverMsger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            unbindService(serviceConnection);
            serviceBound = false;
            serverMsger = null;
        }
    }

}
