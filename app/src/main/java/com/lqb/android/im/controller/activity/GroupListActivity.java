package com.lqb.android.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.lqb.android.im.R;
import com.lqb.android.im.controller.adapter.GroupListAdapter;
import com.lqb.android.im.model.Model;

import java.util.List;

public class GroupListActivity extends Activity {
    // 群组列表
    private ListView lv_grouplist;

    // 群组适配器
    private GroupListAdapter groupListAdapter;

    // 创建群组
    private LinearLayout ll_grouplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_list);

        initView();

        initData();

        initListener();
    }

    private void initView() {
        // 获取listview对象
        lv_grouplist = (ListView) findViewById(R.id.lv_grouplist);

        // 添加头布局
        View headerView = View.inflate(this, R.layout.header_grouplist, null);
        lv_grouplist.addHeaderView(headerView);

        ll_grouplist = (LinearLayout) headerView.findViewById(R.id.ll_grouplist);
    }

    private void initData() {
        // 初始化listview
        groupListAdapter = new GroupListAdapter(this);

        lv_grouplist.setAdapter(groupListAdapter);

        // 从环信服务器获取所有群的信息
        getGroupsFromServer();
    }

    public void getGroupsFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 从网络获取数据
                    final List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    // 更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();

                            // 刷新
                            refresh();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    // 刷新
    private void refresh() {
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    private void initListener() {
        // listView条目点击事件
        lv_grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("TAG", "position:" + position);

                if (position == 0) {
                    return;
                }

                // 跳转到聊天页面
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);

                // 传递会话类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);

                // 群id
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, emGroup.getGroupId());

                startActivity(intent);
            }
        });

        // 跳转到新建群
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 刷新页面
        refresh();
    }
}
