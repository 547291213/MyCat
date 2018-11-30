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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.xkfeng.mycat.Util.DialogHelper;

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
    private int positionOfFirstMsg = 0;
    private Queue<Message> mImgMsgQueue = new LinkedList<>();
    private Dialog reSendDialog;
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
        positionOfFirstMsg = mMsgList.size();
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

    private void reverse(List list) {
        if (list != null) {
            Collections.reverse(list);
        }
    }

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

    public void refreshStartPosition() {
        positionOfFirstMsg += mOffSet;
    }

    private void increaseStartPosition() {
        positionOfFirstMsg++;
    }

    private void checkSendingImg() {
        for (Message message : mMsgList) {
            if (message.getStatus() == MessageStatus.created &&
                    message.getContentType() == ContentType.image) {
                mImgMsgQueue.offer(message);
            }
        }
        if (mImgMsgQueue.size() > 0) {
            Message message = mImgMsgQueue.element();
            sendImgMsg(message);
        }
        notifyDataSetChanged();
    }

    public void setSendImgMsg(int msgId) {
        Message message = mConversation.getMessage(msgId);
        if (message != null) {
            mMsgList.add(message);
            mImgMsgQueue.offer(message);
            increaseStartPosition();
            sendImgMsg(mImgMsgQueue.element());
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
                mImgMsgQueue.poll();
                //if queue isn't null , continue to send next
                if (!mImgMsgQueue.isEmpty()) {
                    sendImgMsg(mImgMsgQueue.element());
                }
                //update data
                notifyDataSetChanged();
            }
        });

    }

    public void addMsgToList(Message message) {
        mMsgList.add(message);
        increaseStartPosition();
        notifyDataSetChanged();
    }

    public void addMsgListToList(List<Message> messages) {
        mMsgList.addAll(messages);
        /**
         * @TODO the jcat isn's do this
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

    public Message getLastMsg() {
        if (mMsgList.size() <= 0) {
            return null;
        } else {
            return mMsgList.get(mMsgList.size() - 1);
        }
    }

    public Message getMessage(int pos) {

        if (pos < 0 || pos >= mMsgList.size()) {
            return null;
        }
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

    private View createViewByType(Message msg, int position) {
        switch (msg.getContentType()) {
            case text:
                return getItemViewType(position) == TYPE_SEND_TEXT ?
                        layoutInflater.inflate(R.layout.mycat_chat_item_send_txt, null, false) :
                        layoutInflater.inflate(R.layout.mycat_chat_item_receive_txt, null, false);

            case image:
                return getItemViewType(position) == TYPE_SEND_IMAGE ?
                        layoutInflater.inflate(R.layout.mycat_chat_item_send_image, null, false) :
                        layoutInflater.inflate(R.layout.mycat_chat_item_receive_image, null, false);

            case file:
                return getItemViewType(position) == TYPE_SEND_FILE ?
                        layoutInflater.inflate(R.layout.mycat_chat_item_send_file, null, false) :
                        layoutInflater.inflate(R.layout.mycat_chat_item_receive_file, null, false);

            case voice:
                return getItemViewType(position) == TYPE_SEND_VOICE ?
                        layoutInflater.inflate(R.layout.mycat_chat_item_send_voice, null, false) :
                        layoutInflater.inflate(R.layout.mycat_chat_item_receive_voice, null, false);

            case video:
                return getItemViewType(position) == TYPE_SEND_MOVIE ?
                        layoutInflater.inflate(R.layout.mycat_chat_item_send_video, null, false) :
                        layoutInflater.inflate(R.layout.mycat_chat_item_receive_video, null, false);

            case location:
                return getItemViewType(position) == TYPE_SEND_POSITION ?
                        layoutInflater.inflate(R.layout.mycat_chat_item_send_location, null, false) :
                        layoutInflater.inflate(R.layout.mycat_chat_item_receive_location, null, false);

            case custom:
            case eventNotification:
            case prompt:
                return layoutInflater.inflate(R.layout.mycat_chat_item_group_change, null, false);
            default:
                return layoutInflater.inflate(R.layout.mycat_chat_item_group_change, null, false);

        }

    }

    @Override
    public Object getItem(int position) {
        return mMsgList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message msg = mMsgList.get(position);
        //消息接收方发送已读回执
        //如果处于消息接收方，并且消息处于未读状态，需要发送消息回执
        if (msg.getDirect() == MessageDirect.receive && !msg.haveRead()) {
            msg.setHaveRead(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {

                }
            });
        }

        final UserInfo userInfo = msg.getFromUser();
        ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = createViewByType(msg, position);
            viewHolder.msgTime = (TextView) convertView.findViewById(R.id.mycat_send_time_txt);
            viewHolder.headIcon = (ImageView) convertView.findViewById(R.id.mycat_avatar_iv);
            viewHolder.displayName = (TextView) convertView.findViewById(R.id.mycat_display_name_tv);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.mycat_msg_content);
            viewHolder.sendingIv = (ImageView) convertView.findViewById(R.id.mycat_sending_iv);
            viewHolder.resend = (ImageButton) convertView.findViewById(R.id.mycat_fail_resend_ib);
//            viewHolder.ivDocument = (ImageView) convertView.findViewById(R.id.iv_document);
            viewHolder.text_receipt = (TextView) convertView.findViewById(R.id.text_receipt);
            switch (msg.getContentType()) {
                case text:
                    viewHolder.ll_businessCard = (LinearLayout) convertView.findViewById(R.id.ll_businessCard);
                    viewHolder.business_head = (ImageView) convertView.findViewById(R.id.business_head);
                    viewHolder.tv_nickUser = (TextView) convertView.findViewById(R.id.tv_nickUser);
                    viewHolder.tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);

                    break;

                case image:

                    viewHolder.picture = (ImageView) convertView.findViewById(R.id.mycat_picture_iv);
                    viewHolder.progressTv = (TextView) convertView.findViewById(R.id.mycat_progress_tv);
                    break;
                case video:
                    break;
                case voice:
                    viewHolder.voice = (ImageView) convertView.findViewById(R.id.mycat_voice_iv);
                    viewHolder.voiceLength = (TextView) convertView.findViewById(R.id.mycat_msg_content);
                    viewHolder.readStatus = (ImageView) convertView.findViewById(R.id.mycat_read_status_iv);
                    break;
                case location:
//                    viewHolder.location = (TextView) convertView.findViewById(R.id.mycat_loc_desc);
                    viewHolder.picture = (ImageView) convertView.findViewById(R.id.mycat_picture_iv);
//                    viewHolder.locationView = convertView.findViewById(R.id.location_view);
                    break;
                case custom:
                case prompt:
                case eventNotification:
//                    viewHolder.groupChange = (TextView) convertView.findViewById(R.id.mycat_group_content);
                    break;
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    class ViewHolder {
        public TextView msgTime;
        public ImageView headIcon;
        public ImageView ivDocument;
        public TextView displayName;
        public TextView txtContent;
        public ImageView picture;
        public TextView progressTv;
        public ImageButton resend;
        public TextView voiceLength;
        public ImageView voice;
        public ImageView readStatus;
        public TextView location;
        public TextView groupChange;
        public ImageView sendingIv;
        public LinearLayout contentLl;
        public TextView sizeTv;
        public LinearLayout videoPlay;
        public TextView alreadySend;
        public View locationView;
        public LinearLayout ll_businessCard;
        public ImageView business_head;
        public TextView tv_nickUser;
        public TextView tv_userName;
        public TextView text_receipt;
        public TextView fileLoad;


    }


    public void showReSendDialog(ViewHolder viewHolder, final Message msg) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.mycat_cancel_btn:
                        reSendDialog.dismiss();
                        break;

                    case R.id.mycat_commit_btn:
                        reSendDialog.dismiss();
                        switch (msg.getContentType()) {
                            case text:
                            case voice:

                                break;

                            case image:

                                break;

                            case file:

                                break;

                            default:

                                break;
                        }
                        break;

                    default:
                        reSendDialog.dismiss();
                        break;
                }
            }
        };

        reSendDialog = DialogHelper.createResendDialog(mContext, listener);
        reSendDialog.show();
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
