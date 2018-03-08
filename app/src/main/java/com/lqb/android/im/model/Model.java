package com.lqb.android.im.model;

import android.content.Context;

import com.lqb.android.im.model.bean.UserInfo;
import com.lqb.android.im.model.dao.UserAccountDao;
import com.lqb.android.im.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 数据模型层全局类
public class Model {
    private Context mContext;

    // 时间比较短，如果线程长时间不用就回收
    private ExecutorService executors = Executors.newCachedThreadPool();

    // 创建对象
    private static Model model = new Model();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;

    // 私有化构造
    private Model() {

    }

    // 获取单例对象
    public static Model getInstance() {
        return model;
    }

    // 初始化的方法
    public void init(Context context) {
        mContext = context;

        // 创建用户账户的数据库的操作类对象
        userAccountDao = new UserAccountDao(mContext);

        // 开启全局监听
        EventListener eventListener = new EventListener(mContext);
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    // 获取全局线程池对象
    public ExecutorService getGlobalThreadPool() {
        return executors;
    }

    // 获取用户账号数据库的操作类对象
    public UserAccountDao getUserAccountDao() {
        return userAccountDao;
    }

    public void loginSuccess(UserInfo account) {

        // 校验
        if (account == null) {
            return;
        }

        if (dbManager != null) {
            dbManager.close();
        }

        // 传上下文和名称
        dbManager = new DBManager(mContext, account.getName());
    }
}
