package com.lightingstar.appmonitor;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
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
import com.lightingstar.appmonitor.util.PermissionsUtil;

public class MainActivity extends AppCompatActivity {

    private boolean permissionPassFlag =false;
    private boolean accessibilityPassFlag =false;

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
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.w("component name:", name.getClassName());
            MainApplication.setMessage(new Messenger(binder));
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    //创建监听权限的接口对象
    PermissionsUtil.IPermissionsResult permissionsResult = new PermissionsUtil.IPermissionsResult() {
        @Override
        public void passPermissons() {
            permissionPassFlag = true;
            checkAccessibilitySetting();
        }

        @Override
        public void forbitPermissons() {
            //这是没有通过权限的时候提示的内容，自定义即可
            Toast.makeText(getApplicationContext(), "您没有允许部分权限，可能会导致部分功能不能正常使用，如需正常使用 请允许权限", Toast.LENGTH_SHORT).show();
            finish();
            //   Tool.exitApp();
        }

        @Override
        public void checkAccessibilitySetting(){
            if (accessibilityPassFlag) return;

            if (!PermissionsUtil.getInstance().isAccessibilitySettingsOn(getApplicationContext())){
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
            else{
                accessibilityPassFlag = true;
            }
        }
    };

    /**
     * 检查权限是否开启
     */
    private void checkPermission() {
        if (permissionPassFlag){
            permissionsResult.checkAccessibilitySetting();
            return;
        }
        //弹出框权限
        String[] permissions = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW
                //,Manifest.permission.RECEIVE_BOOT_COMPLETED
        };
        PermissionsUtil.showSystemSetting = true;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtil.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (serviceBound) {
            //MainApplication.sendMessage("app is closing", AppConstance.APP_CLOSE_MSG);
            unbindService(serviceConnection);
            serviceBound = false;
        }
        super.onDestroy();
    }

}
