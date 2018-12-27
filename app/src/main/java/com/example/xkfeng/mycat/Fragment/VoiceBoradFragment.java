package com.example.xkfeng.mycat.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xkfeng.mycat.DrawableView.ChatListAdapter;
import com.example.xkfeng.mycat.DrawableView.VoiceUtil.RecordVoiceButton;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;

public class VoiceBoradFragment extends Fragment implements RecordVoiceButton.OnRecoredFinishListener {
    Unbinder unbinder;
    @BindView(R.id.rvb_recordVoice)
    RecordVoiceButton rvbRecordVoice;
    private View view;
    private Context mContext;
    private boolean isClicked = false;
    private static final String TAG = "VoiceBoradFragment";
    private Conversation mConv ;
    private ChatListAdapter  mMsgListAdapter ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.voice_fragment_layout, container, false);
        mContext = getContext();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rvbRecordVoice.setOnRecoredFinishListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setmMsgListAdapter(ChatListAdapter chatListAdapter){
        mMsgListAdapter = chatListAdapter ;
    }

    public void setmConv(Conversation conv){
        this.mConv = conv ;
    }
    @Override
    public void onRecordFinish(String time, String filePath) {
        VoiceContent content = null;
        try {
            content = new VoiceContent(new File(filePath), Integer.parseInt(time)/100);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (mConv == null || mMsgListAdapter == null){
            ITosast.showShort(mContext , "会话尚未绑定 , 发送录音消息失败").show();
            return ;
        }
        Message msg = mConv.createSendMessage(content);
        mMsgListAdapter.addMsgFromReceiveToList(msg);
        if (mConv.getType() == ConversationType.single) {
            UserInfo userInfo = (UserInfo) msg.getTargetInfo();
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);
            JMessageClient.sendMessage(msg, options);

        }
    }
}
