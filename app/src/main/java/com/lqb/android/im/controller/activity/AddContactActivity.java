package com.lqb.android.im.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lqb.android.im.R;
import com.lqb.android.im.model.Model;
import com.lqb.android.im.model.bean.UserInfo;

public class AddContactActivity extends Activity {
    private TextView tv_add_find;
    private EditText et_add_name;
    private RelativeLayout rl_add;
    private TextView tv_add_name;
    private Button bt_add_add;
    private UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_contact);

        // 初始化view
        initView();

        initListener();
    }

    private void initView() {
        tv_add_find = (TextView)findViewById( R.id.tv_add_find );
        et_add_name = (EditText)findViewById( R.id.et_add_name );
        rl_add = (RelativeLayout)findViewById( R.id.rl_add );
        tv_add_name = (TextView)findViewById( R.id.tv_add_name );
        bt_add_add = (Button)findViewById( R.id.bt_add_add );
    }

    private void initListener() {
        // 查找按钮的点击事件处理
        tv_add_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });

        // 添加按钮的点击事件处理
        bt_add_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
    }

    // 查找按钮处理
    private void find() {
        // 获取输入的用户名称
        final String name = et_add_name.getText().toString();

        //校验输入的名称
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(AddContactActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 去服务器判断当前用户是否存在
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                userInfo = new UserInfo(name);

                // 更新UI显示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl_add.setVisibility(View.VISIBLE);
                        tv_add_name.setText(userInfo.getName());
                    }
                });
            }
        });
    }

    // 添加按钮处理
    private void add() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器添加好友
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(), "添加好友");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "好友请求发送成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "好友请求发送失败" +
                                    e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
