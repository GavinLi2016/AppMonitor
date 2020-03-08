package com.lightingstar.appmonitor;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lightingstar.appmonitor.core.BaseActivity;
import com.lightingstar.appmonitor.core.BaseFragment;
import com.lightingstar.appmonitor.fragment.profile.ProfileFragment;
import com.lightingstar.appmonitor.fragment.task.TaskFragment;
import com.lightingstar.appmonitor.fragment.webapp.WebAppFragment;
import com.lightingstar.appmonitor.util.PermissionsUtil;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xutil.common.CollectionUtils;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private boolean permissionPassFlag =false;
    private boolean accessibilityPassFlag =false;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    /**
     * 底部导航栏
     */
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    private String[] mTitles;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        initViews();

        bottomNavigation.setOnNavigationItemSelectedListener(this);

        //startService(new Intent(getApplicationContext(), MonitorServer.class));

        //this.checkPermission();
    }

    private void initViews() {
        mTitles = ResUtils.getStringArray(R.array.home_titles);
        toolbar.setTitle(mTitles[0]);
        toolbar.inflateMenu(R.menu.menu_main);

        //主页内容填充
        BaseFragment[] fragments = new BaseFragment[]{
                new TaskFragment(),
                new WebAppFragment(),
                new ProfileFragment()
        };
        FragmentAdapter<BaseFragment> adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(mTitles.length - 1);
        viewPager.setAdapter(adapter);
    }



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

    /**
     * 底部导航栏点击事件
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);

            //updateSideNavStatus(menuItem);
            return true;
        }
        return false;
    }

}
