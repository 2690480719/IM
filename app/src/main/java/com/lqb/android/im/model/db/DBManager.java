package com.lqb.android.im.model.db;

import android.content.Context;

import com.lqb.android.im.model.dao.ContactTableDao;
import com.lqb.android.im.model.dao.InviteTableDao;

public class DBManager {
    private final DBHelper dbHelper;
    private final ContactTableDao contactTableDao;
    private final InviteTableDao inviteTableDao;

    public DBManager(Context context, String name) {
        // 创建数据库
        dbHelper = new DBHelper(context, name);

        // 创建联系人操作类和邀请信息操作类
        contactTableDao = new ContactTableDao(dbHelper);
        inviteTableDao = new InviteTableDao(dbHelper);
    }

    // 获取联系人表的操作类对象
    public ContactTableDao getContactTableDao(){
        return contactTableDao;
    }

    // 获取邀请信息表的操作类对象
    public InviteTableDao getInviteTableDao(){
        return inviteTableDao;
    }

    // 关闭数据库的方法
    public void close() {
        dbHelper.close();
    }
}
