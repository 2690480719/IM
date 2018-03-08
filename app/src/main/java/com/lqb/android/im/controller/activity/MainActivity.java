package com.lqb.android.im.controller.activity;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.RadioGroup;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.lqb.android.im.R;
import com.lqb.android.im.controller.fragment.ChatFragment;
import com.lqb.android.im.controller.fragment.ContactListFragment;
import com.lqb.android.im.controller.fragment.SettingFragment;

public class MainActivity extends FragmentActivity {
    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private SettingFragment settingFragment;
    private ContactListFragment contactListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        initView();

        // 初始化数据
        initData();

        // 初始化监听
        initListener();
    }

    private void initView() {
        rg_main = (RadioGroup)findViewById(R.id.rg_main);
    }

    private void initData() {
        // 创建三个fragment对象
        chatFragment = new ChatFragment();
        settingFragment = new SettingFragment();
        contactListFragment = new ContactListFragment();
    }

    private void initListener() {
        // RadioGroup的选择事件
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                Fragment fragment = null;

                switch (checkedId) {
                    // 会话页面
                    case R.id.rb_main_chat:
                        fragment = chatFragment;
                        break;

                    // 联系人页面
                    case R.id.rb_main_contact:
                        fragment = contactListFragment;
                        break;

                    // 设置页面
                    case R.id.rb_main_setting:
                        fragment = settingFragment;
                        break;
                }

                // 实现fragment切换的方法
                switchFragment(fragment);
            }
        });

        // 默认选择会话页面
        rg_main.check(R.id.rb_main_chat);
    }

    // 实现fragment切换的方法
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EMClient.getInstance().logout(true);

        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }
}
