package com.lqb.android.im.controller.fragment;

import android.content.Intent;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.lqb.android.im.controller.activity.ChatActivity;

import java.util.List;

public class ChatFragment extends EaseConversationListFragment {
    @Override
    protected void initView() {
        super.initView();

        // 监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);

        // 跳转到会话详情页面
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                // 传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId());

                // 是否是群聊
                if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                }

                startActivity(intent);
            }
        });
    }

    private EMMessageListener emMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            // 设置数据
            EaseUI.getInstance().getNotifier().onNewMesg(list);

            // 刷新列表
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
        }


        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
    }
}
