package com.lqb.android.im.controller.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lqb.android.im.R;
import com.lqb.android.im.model.Model;
import com.lqb.android.im.model.bean.UserInfo;

public class LoginActivity extends Activity {
    private EditText etLoginName;
    private EditText etLoginPwd;
    private Button btLoginRegist;
    private Button btLoginLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_activity);

        // 初始化控件
        initView();

        // 初始化监听
        initListener();
    }

    private void initView() {
        etLoginName = (EditText)findViewById( R.id.et_login_name );
        etLoginPwd = (EditText)findViewById( R.id.et_login_pwd );
        btLoginRegist = (Button)findViewById( R.id.bt_login_regist );
        btLoginLogin = (Button)findViewById( R.id.bt_login_login );
    }

    private void initListener() {
        // 注册按钮的点击事件
        btLoginRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }
        });

        // 登录按钮的点击事件
        btLoginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    //注册的业务逻辑处理
    private void regist() {
        // 获取用户名和密码
        final String registName = etLoginName.getText().toString();
        final String registPwd = etLoginPwd.getText().toString();

        // 校验用户名和密码
        if (TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 去服务器注册账号
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 去环信服务器注册账号
                    EMClient.getInstance().createAccount(registName, registPwd);

                    // 更新页面显示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败" + e.toString(),
                                            Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    // 登录按钮的逻辑处理
    private void login() {
        // 1.获取输入的用户名和密码
        final String loginName = etLoginName.getText().toString();
        final String loginPwd = etLoginPwd.getText().toString();

        // 2.校验输入的用户名和密码
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3.登录逻辑处理
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去环信服务器登录
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                    // 登录成功后的处理
                    @Override
                    public void onSuccess() {
                        // 对模型层数据的处理
                        Model.getInstance().loginSuccess(new UserInfo(loginName));

                        // 保存用户账号信息到本地数据库2
                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 提示登录成功
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                                // 跳转到主页面
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    // 登录失败的处理
                    @Override
                    public void onError(int i, final String s) {
                        // 提示登录失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败"+ s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    // 登录过程中的处理
                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }
}
