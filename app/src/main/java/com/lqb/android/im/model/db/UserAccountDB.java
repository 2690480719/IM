package com.lqb.android.im.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lqb.android.im.model.dao.UserAccountTable;

// 用户数据库
public class UserAccountDB extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;    //版本号

    // 构造
    public UserAccountDB(Context context) {
        super(context, "account.db", null, DB_VERSION);
    }

    // 数据库创建的时候调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserAccountTable.CREATE_TAB);
    }

    // 数据库更新的时候调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
