package com.example.xkfeng.mycat.DrawableView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.MediaExtractor;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import com.example.xkfeng.mycat.R;

public class ChatListAdapter extends BaseAdapter {

    private static final String TAG = "ChatListAdapter";
    //Message Type
    //TEXT
    private final int TYPE_SEND_TEXT = 0;
    private final int TYPE_RECEIVE_TEXT = 1;

    //IMAGE
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVE_IMAGE = 3;

    //FILE
    private final int TYPE_SEND_FILE = 4;
    private final int TYPE_RECEIVE_FILE = 5;

    //Voice
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVE_VOICE = 7;

    //Position
    private final int TYPE_SEND_POSITION = 8;
    private final int TYPE_RECEIVE_POSITION = 9;

    //Mv
    private final int TYPE_SEND_MOVIE = 10;
    private final int TYPE_RECEIVE_MOVIE = 11;

    //Group member change
    private final int GROUP_MEMBER_CHANGE = 12;

    //diy Message
    private final int DIY_MESSAGE = 13;

    //sum of the view type
    private final int SUM_VIEW_TYPE = 14;

    private long groupId;

    private Activity mActivity;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int mWidth;
    private Conversation mConversation;
    private List<Message> mMsgList = new ArrayList<>();
    public static final int PAGE_MESSAGE_COUNT = 18;
    private int mOffSet = PAGE_MESSAGE_COUNT;

    //position of the first message
    private int mStart;
    //image-message queue
    private Queue<Message> mMsgQueue = new LinkedList<>();
    private Dialog dialog;
    private boolean mHasLastPage = false;
    private ContentLongClickListener mLongClickListener;


    public ChatListAdapter(Activity activity, Conversation conversation, ContentLongClickListener contentLongClickListener) {
        this.mActivity = activity;
        this.mContext = activity;
        this.mConversation = conversation;
        this.mLongClickListener = contentLongClickListener;

        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;

        layoutInflater = LayoutInflater.from(mContext);
        this.mMsgList = mConversation.getMessagesFromNewest(0, mOffSet);
        reverse(mMsgList);
        mStart = mMsgList.size();
        if (mConversation.getType() == ConversationType.single) {
            //单聊
            UserInfo userInfo = (UserInfo) mConversation.getTargetInfo();
            if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        if (i == 0) {
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        } else {
            //群聊
            GroupInfo groupInfo = (GroupInfo) mConversation.getTargetInfo();
            groupId = groupInfo.getGroupID();
        }
        //检查是否有正在发送的图片

        checkSendingImg();
    }

    /**
     * list reverse
     *
     * @param list for message
     */
    private void reverse(List list) {
        if (list != null) {
            Collections.reverse(list);
        }
    }

    /**
     * 下拉刷新
     */
    public void dropDownRefresh() {
        if (mConversation != null) {
            List<Message> messageList = mConversation.getMessagesFromNewest(mMsgList.size(), PAGE_MESSAGE_COUNT);
            if (messageList != null) {
                for (Message message : messageList) {
                    //mMsgList的为决定了View的位置，
                    //下拉刷新，是拉取之前的数据，
                    mMsgList.add(0, message);
                }
                if (messageList.size() > 0) {
                    checkSendingImg();
                    mOffSet = messageList.size();
                    mHasLastPage = true;
                } else {
                    mHasLastPage = false;
                }
                notifyDataSetChanged();
            }
        }
    }

    public boolean ismHasLastPage() {
        return mHasLastPage;
    }

    public int getmOffSet() {
        return mOffSet;
    }

    //下拉刷新后调用
    public void refreshStartPosition() {
        mStart += mOffSet;
    }

    //有新的消息时，自增首条消息的位置
    private void increaseStartPosition() {
        mStart++;
    }

    //存在处于创建的图片需要发送到队列中
    private void checkSendingImg() {
        //遍历所有消息，如果存在正在创建的图片消息，
        // 添加到队列
        for (Message message : mMsgList) {
            if (message.getStatus() == MessageStatus.created &&
                    message.getContentType() == ContentType.image) {
                mMsgQueue.offer(message);
            }
        }

        if (mMsgQueue.size() > 0) {
            Message message = mMsgQueue.element();
            sendImgMsg(message);
        }
        //update data
        notifyDataSetChanged();
    }

    /**
     * outSize calls
     *
     * @param msgId for image message
     */
    public void setSendImgMsg(int msgId) {
        Message message = mConversation.getMessage(msgId);
        if (message != null) {
            mMsgList.add(message);
            mMsgQueue.offer(message);
            //消息自增
            increaseStartPosition();
            //发送图片消息
            sendImgMsg(mMsgQueue.element());
        }
    }

    // get out of the queue and send
    // picture to server
    private void sendImgMsg(Message imgMsg) {

        MessageSendingOptions messageSendingOptions = new MessageSendingOptions();
        messageSendingOptions.setNeedReadReceipt(true);
        JMessageClient.sendMessage(imgMsg, messageSendingOptions);
        imgMsg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                //out of the queue
                mMsgQueue.poll();
                //if queue isn't null , continue to send next
                if (!mMsgQueue.isEmpty()) {
                    sendImgMsg(mMsgQueue.element());
                }
                //update data
                notifyDataSetChanged();
            }
        });

    }

    /**
     * add message to list ，
     * increase start position
     * update data
     *
     * @param message
     */
    public void addMsgToList(Message message) {
        mMsgList.add(message);
        increaseStartPosition();
        notifyDataSetChanged();
    }

    /**
     * @param messages
     */
    public void addMsgListToList(List<Message> messages) {
        mMsgList.addAll(messages);
        /**
         * @TODU the jcat isn's do this
         */
        for (Message message : messages) {
            increaseStartPosition();
        }

        notifyDataSetChanged();
    }

    public void addMsgFromReceiveToList(Message message) {
        mMsgList.add(message);
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    increaseStartPosition();
                    notifyDataSetChanged();
                } else {

                    /**
                     * 提示发送失败，给予消息重发
                     *
                     */

                    notifyDataSetChanged();
                }
            }
        });
    }

    //back out message
    //用撤回后event下发的消息去替换掉原有的消息
    public void backOutMessage(Message backOutMsg) {
        List<Message> backOutMsgList = new ArrayList<>();
        int i = 0;
        for (Message message : mMsgList) {
            if (backOutMsg.getServerMessageId().equals(message.getServerMessageId())) {
                i = mMsgList.indexOf(message);
                backOutMsgList.add(message);
                break;
            }
        }
        mMsgList.removeAll(backOutMsgList);
        mMsgList.add(i, backOutMsg);
        notifyDataSetChanged();
    }

    /**
     * @return last Msg
     */
    public Message getLastMsg() {
        if (mMsgList.size() <= 0) {
            return null;
        } else {
            return mMsgList.get(mMsgList.size() - 1) ;
        }
    }

    /**
     * get message for position
     *
     * @param pos
     * @return
     */
    public Message getMessage(int pos) {
        return mMsgList.get(pos);
    }

    public void deleteMessage(Message delMsg) {
        Message msgForDelete = null;
        for (Message message : mMsgList) {
            if (delMsg.getServerMessageId().equals(message.getServerMessageId())) {
                msgForDelete = message;
                break;
            }
        }
        mMsgList.remove(msgForDelete);
        notifyDataSetChanged();

    }


    @Override
    public int getCount() {
        return mMsgList.size();
    }

    @Override
    public int getViewTypeCount() {
        return SUM_VIEW_TYPE;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = mMsgList.get(position);
        //是文字类型或者自定义类型（用来显示群成员变化消息）
        switch (msg.getContentType()) {
            case text:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_TEXT
                        : TYPE_RECEIVE_TEXT;
            case image:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_IMAGE
                        : TYPE_RECEIVE_IMAGE;
            case file:
                String extra = msg.getContent().getStringExtra("video");
                if (!TextUtils.isEmpty(extra)) {
                    return msg.getDirect() == MessageDirect.send ? TYPE_SEND_MOVIE
                            : TYPE_RECEIVE_MOVIE;
                } else {
                    return msg.getDirect() == MessageDirect.send ? TYPE_SEND_FILE
                            : TYPE_RECEIVE_FILE;
                }
            case voice:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_VOICE
                        : TYPE_RECEIVE_VOICE;
            case location:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_POSITION
                        : TYPE_RECEIVE_POSITION;
            case eventNotification:
            case prompt:
                return GROUP_MEMBER_CHANGE;
            default:
                return DIY_MESSAGE;
        }
    }

//    private View createViewByType(Message msg , int position){
//        switch (msg.getContentType())
//        {
//            case text:
////                return getItemViewType(position) == TYPE_SEND_TEXT ?
////                        layoutInflater.inflate(R.layout) : layoutInflater.inflate() ;
//        }
//
//    }

    @Override
    public Object getItem(int position) {
        return mMsgList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    class ViewHolder {

    }

    public abstract class ContentLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {

            onContentLoingClick((Integer) v.getTag(), v);
            return true;
        }

        public abstract void onContentLoingClick(int pos, View view);

    }
}
