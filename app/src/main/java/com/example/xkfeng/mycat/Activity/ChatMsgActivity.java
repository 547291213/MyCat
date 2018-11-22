package com.example.xkfeng.mycat.Activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.SqlHelper.LoginSQLDao;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;


public class ChatMsgActivity extends BaseActivity {


    private static final String TAG = "ChatMsgActivity";

    private Conversation conversation;

    private List<cn.jpush.im.android.api.model.Message> messageList ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message_layout);

        initView();
    }

    private void initView() {
        conversation = JMessageClient.getSingleConversation(getIntent().getStringExtra("userName"));

        if (conversation != null){

            messageList = conversation.getAllMessage() ;

            Log.d(TAG, "initView: " + messageList.get(0).getCreateTime());
        }
    }


}
