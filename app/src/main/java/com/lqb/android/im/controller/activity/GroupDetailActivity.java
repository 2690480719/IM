package com.lqb.android.im.controller.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.lqb.android.im.R;
import com.lqb.android.im.controller.adapter.GroupDetailAdapter;
import com.lqb.android.im.controller.adapter.PickContactAdapter;
import com.lqb.android.im.model.Model;
import com.lqb.android.im.model.bean.UserInfo;
import com.lqb.android.im.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends Activity{
    private GridView gv_groupdetail;
    private Button bt_groupdetail_out;
    private GroupDetailAdapter groupDetailAdapter;
    private List<UserInfo> mUsers = new ArrayList<>();
    // 群的全部消息
    private EMGroup mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_detail);

        initView();

        getData();

        initGridview();

        initButtonDisplay();

        initListener();
    }

    private void initView() {
        gv_groupdetail = (GridView) findViewById(R.id.gv_groupdetail);
        bt_groupdetail_out = (Button) findViewById(R.id.bt_groupdetail_out);

        // 初始化listview

    }

    // 获取传递过来的数据
    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        if (groupId == null) {
            return;
        } else {
            // 获取群消息
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }

    // 初始化按钮的显示
    private void initButtonDisplay() {
        // 判断当前用户是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {    //是群主
            bt_groupdetail_out.setText("解散群");

            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new  AlertDialog.Builder(GroupDetailActivity.this)
                            .setTitle("确认" )
                            .setMessage("确定解散本群吗？" )
                            .setPositiveButton("是" ,  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 执行删除选中的联系人操作
                                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // 去环信服务器解散群
                                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                                // 发送解散群广播
                                                exitGroupBroadCast();

                                                // 更新页面
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(GroupDetailActivity.this,
                                                                "解散群成功", Toast.LENGTH_SHORT).show();

                                                        // 结束当前页面
                                                        finish();
                                                    }
                                                });

                                            } catch (HyphenateException e) {
                                                e.printStackTrace();

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(GroupDetailActivity.this, "解散群失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            } )
                            .setNegativeButton("否" , null)
                            .show();
                }
            });
        } else {    // 群成员

            bt_groupdetail_out.setText("退群");

            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new  AlertDialog.Builder(GroupDetailActivity.this)
                            .setTitle("确认" )
                            .setMessage("确定退出本群吗？" )
                            .setPositiveButton("是" ,  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 执行删除选中的联系人操作
                                    // 联网
                                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // 告诉环信服务器退群
                                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                                // 发送退群广播
                                                exitGroupBroadCast();

                                                // 更新页面
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();

                                                        finish();
                                                    }
                                                });
                                            } catch (HyphenateException e) {
                                                e.printStackTrace();

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(GroupDetailActivity.this, "退群失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            } )
                            .setNegativeButton("否" , null)
                            .show();
                }
            });
        }
    }

    // 发送退群和解散群广播
    private void exitGroupBroadCast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);

        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

        mLBM.sendBroadcast(intent);
    }

    // 初始化GridView
    private void initGridview() {
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();

        groupDetailAdapter = new GroupDetailAdapter(this, isCanModify, mOnGroupDetailListener);

        gv_groupdetail.setAdapter(groupDetailAdapter);

        getMembersFromHxServer();
    }

    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        // 添加群成员
        @Override
        public void onAddMembers() {
            // 跳转到选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);

            // 传递群id
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

            startActivityForResult(intent, 2);
        }

        // 删除群成员
        @Override
        public void onDeleteMember(final UserInfo user) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 从环信服务器中删除此人
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), user.getHxId());

                        // 更新页面
                        getMembersFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            // 获取返回的准备邀请的群成员信息
            final String[] memberses = data.getStringArrayExtra("members");

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 去环信服务器，发送邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), memberses);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送群邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送群邀请失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void initListener() {
        gv_groupdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 判断当前是否是删除模式，如果是删除模式
                        if (groupDetailAdapter.ismIsDeleteModel()) {
                            // 切换为非删除模式
                            groupDetailAdapter.setmIsDeleteModel(false);

                            // 刷新页面
                            groupDetailAdapter.notifyDataSetChanged();
                        }
                        break;
                }

                return false;
            }
        });
    }

    private void getMembersFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 从环信服务器获取所有的群成员信息
                    EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());

                    List<String> members = emGroup.getMembers();

                    if(members != null && members.size() >=0) {

                        mUsers = new ArrayList<UserInfo>();

                        // 转换
                        for (String member : members){
                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }
                    }

                    // 更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 刷新适配器
                            groupDetailAdapter.refresh(mUsers);
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
