package com.lqb.android.im;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.lqb.android.im.model.Model;

public class IMApplication extends Application{
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        EMOptions options = new EMOptions();

        options.setAutoAcceptGroupInvitation(false);// 设置需要同意后才能接受邀请
        options.setAcceptInvitationAlways(false);   // 设置需要同意后才能接受群邀请

        //初始化 EaseUI
        EaseUI.getInstance().init(this, options);

        //初始化模型层数据类
        Model.getInstance().init(this);

        //初始化全局上下文对象
        mContext = this;
    }

    //获取全局上下文
    public static Context getGlobalApplication() {
        return mContext;
    }
}
