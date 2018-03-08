package com.lqb.android.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.lqb.android.im.R;
import com.lqb.android.im.controller.adapter.PickContactAdapter;
import com.lqb.android.im.model.Model;
import com.lqb.android.im.model.bean.PickContactInfo;
import com.lqb.android.im.model.bean.UserInfo;
import com.lqb.android.im.utils.Constant;

import java.util.ArrayList;
import java.util.List;


public class PickContactActivity extends Activity {
    private TextView tv_pick_save;
    private ListView lv_pick;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter pickContactAdapter;
    private List<String> mExistMembers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pick_contact);

        // 获取传递过来的数据
        getData();

        initView();

        initData();

        initListener();
    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        if (groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);

            // 获取已经存在的所有群成员
            mExistMembers = group.getMembers();
        }

        if (mExistMembers == null) {
            mExistMembers = new ArrayList<>();
        }
    }

    private void initView() {
        tv_pick_save = (TextView) findViewById(R.id.tv_pick_save);
        lv_pick = (ListView) findViewById(R.id.lv_pick);
    }

    private void initData() {
        // 从本地数据库中获取所有的联系人信息
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();

        mPicks = new ArrayList<>();

        if (contacts != null && contacts.size() >= 0) {
            // 转换
            for (UserInfo contact : contacts) {
                PickContactInfo pickContactInfo = new PickContactInfo(contact, false);
                mPicks.add(pickContactInfo);
            }
        }

        // 初始化listview
        pickContactAdapter = new PickContactAdapter(this, mPicks, mExistMembers);

        lv_pick.setAdapter(pickContactAdapter);
    }

    private void initListener() {
        // listView条目点击事件
        lv_pick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //  checkbox的切换
                CheckBox cb_pick = (CheckBox) view.findViewById(R.id.cb_pick);
                cb_pick.setChecked(!cb_pick.isChecked());

                // 修改数据
                PickContactInfo pickContactInfo = mPicks.get(position);
                pickContactInfo.setIsChecked(cb_pick.isChecked());

                // 刷新页面
                pickContactAdapter.notifyDataSetChanged();
            }
        });

        // 保存按钮的点击监听处理
        tv_pick_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取到已经选择的联系人
                List<String> names = pickContactAdapter.getPickContacts();

                // 给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members", names.toArray(new String[0]));

                // 设置返回的结果码
                setResult(RESULT_OK, intent);

                // 结束当前页面
                finish();
            }
        });
    }
}

