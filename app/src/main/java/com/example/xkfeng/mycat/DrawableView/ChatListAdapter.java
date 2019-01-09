package com.example.xkfeng.mycat.DrawableView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaExtractor;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.TextContent;
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
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.Activity.ChatMsgActivity;
import com.example.xkfeng.mycat.Activity.FriendInfoActivity;
import com.example.xkfeng.mycat.Activity.GroupNotFriendActivity;
import com.example.xkfeng.mycat.Activity.IsFirstActivity;
import com.example.xkfeng.mycat.Activity.UserInfoActivity;
import com.example.xkfeng.mycat.Model.User;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;
import com.example.xkfeng.mycat.Util.TimeUtil;

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
    public static final int PAGE_MESSAGE_COUNT = 12;
    private int mOffSet = PAGE_MESSAGE_COUNT;
    private int positionOfFirstMsg = 0;
    private Queue<Message> mImgMsgQueue = new LinkedList<>();
    private Dialog reSendDialog;
    private boolean mHasLastPage = false;
    private ContentLongClickListener mLongClickListener;
    private ChatListAdapterController mController;


    private Bitmap myHeaderBitmap = null;

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

        mController = new ChatListAdapterController(mContext, this, conversation, mMsgList, contentLongClickListener, dm.density);
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
            Log.d("ChatMsgActivity", "setSendImgMsg: ");
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
                mImgMsgQueue.poll();
                if (!mImgMsgQueue.isEmpty()) {
                    sendImgMsg(mImgMsgQueue.element());
                }
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
                        layoutInflater.inflate(R.layout.mycat_chat_item_send_txt, null) :
                        layoutInflater.inflate(R.layout.mycat_chat_item_receive_txt, null);

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
        sendReadReceipt(msg);

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = createViewByType(msg, position);
            viewHolder.msgTime = (TextView) convertView.findViewById(R.id.mycat_send_time_txt);
            viewHolder.headIcon = (CircleImageView) convertView.findViewById(R.id.mycat_avatar_iv);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.mycat_msg_content);

            viewHolder.displayName = (TextView) convertView.findViewById(R.id.mycat_display_name_tv);
            viewHolder.sendingIv = (ImageView) convertView.findViewById(R.id.mycat_sending_iv);
            viewHolder.resend = (ImageButton) convertView.findViewById(R.id.mycat_fail_resend_ib);
            viewHolder.ivDocument = (ImageView) convertView.findViewById(R.id.iv_document);
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

                case file:
                    String extra = msg.getContent().getStringExtra("video");
                    if (!TextUtils.isEmpty(extra)) {
//                        viewHolder.picture = (ImageView) convertView.findViewById(R.id.jmui_picture_iv);
//                        viewHolder.progressTv = (TextView) convertView.findViewById(R.id.jmui_progress_tv);
//                        viewHolder.videoPlay = (LinearLayout) convertView.findViewById(R.id.message_item_video_play);
                    } else {
                        viewHolder.progressTv = (TextView) convertView.findViewById(R.id.mycat_progress_tv);
                        viewHolder.contentLl = (LinearLayout) convertView.findViewById(R.id.mycat_send_file_ll);
                        viewHolder.sizeTv = (TextView) convertView.findViewById(R.id.mycat_send_file_size);
                        viewHolder.alreadySend = (TextView) convertView.findViewById(R.id.file_already_send);
                    }
                    if (msg.getDirect().equals(MessageDirect.receive)) {
                        viewHolder.fileLoad = (TextView) convertView.findViewById(R.id.mycat_send_file_load);
                    }
                    break;
                case voice:
                    viewHolder.voice = (ImageView) convertView.findViewById(R.id.mycat_voice_iv);
                    viewHolder.voiceLength = (TextView) convertView.findViewById(R.id.mycat_voice_length_tv);
                    viewHolder.readStatus = (ImageView) convertView.findViewById(R.id.mycat_read_status_iv);
                    break;
                case location:
                    viewHolder.location = (TextView) convertView.findViewById(R.id.mycat_loc_desc);
                    viewHolder.picture = (ImageView) convertView.findViewById(R.id.mycat_picture_iv);
                    viewHolder.locationView = convertView.findViewById(R.id.location_view);
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

        if (viewHolder != null) {

            dealWithTime(viewHolder, msg, position);
            showHeadImage(viewHolder, msg);
            headImgClick(viewHolder, msg, position);
            processVariousMsg(viewHolder, msg, position);
            msgReceiptionSituation(viewHolder, msg);


        }
        return convertView;
    }

    private void sendReadReceipt(Message msg) {
        if (msg.getDirect() == MessageDirect.receive && !msg.haveRead()) {
            msg.setHaveRead(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {

                }
            });
        }
    }


    private void dealWithTime(ViewHolder viewHolder, Message msg, int position) {
        if (viewHolder.msgTime == null) {
            return;
        }
        long nowDate = msg.getCreateTime();
        if (mOffSet == PAGE_MESSAGE_COUNT) {

            if (position == 0 || position % PAGE_MESSAGE_COUNT == 0) {
                viewHoldShowTime(viewHolder, nowDate);
            } else {
                long lastDate = mMsgList.get(position - 1).getCreateTime();
                if (nowDate - lastDate > 300000) {
                    viewHoldShowTime(viewHolder, nowDate);
                } else {
                    viewHoldHideTime(viewHolder);
                }
            }
        } else {
            if (position == 0 || (position - mOffSet) % PAGE_MESSAGE_COUNT == 0) {
                viewHoldShowTime(viewHolder, nowDate);
            } else {
                long lastDate = mMsgList.get(position - 1).getCreateTime();
                if (nowDate - lastDate > 300000) {
                    viewHoldShowTime(viewHolder, nowDate);
                } else {
                    viewHoldHideTime(viewHolder);
                }
            }
        }

    }

    private void viewHoldShowTime(ViewHolder viewHolder, long nowDate) {
        TimeUtil timeUtil = new TimeUtil(mContext, nowDate);
        /**
         * 消息撤回之后，该msgTime为null
         * 不处理界面异常退出
         */
        if (viewHolder.msgTime != null) {
            viewHolder.msgTime.setVisibility(View.VISIBLE);
            viewHolder.msgTime.setText(timeUtil.getDetailTime());
        }

    }

    private void viewHoldHideTime(ViewHolder viewHolder) {
        if (viewHolder.msgTime != null) {
            viewHolder.msgTime.setVisibility(View.GONE);
        }
    }

    private void showHeadImage(final ViewHolder viewHolder, Message msg) {
        if (viewHolder.headIcon == null) {
            return;
        }

        final UserInfo userInfo = msg.getFromUser();

        if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
            viewHolder.headIcon.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatarFile().toString()));
        } else {
            myHeaderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.log);
            viewHolder.headIcon.setImageBitmap(myHeaderBitmap);
        }
    }

    private void headImgClick(ViewHolder viewHolder, final Message msg, int position) {

        /**
         * 当消息撤回之后，headIcon == null ，
         * 不处理程序会异常退出
         */
        if (viewHolder.headIcon == null) {
            return;
        }
        viewHolder.headIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (msg.getDirect() == MessageDirect.send) {
                    intent.setClass(mContext, UserInfoActivity.class);
                    ((Activity) mContext).startActivityForResult(intent, ChatMsgActivity.RequestCode_LookUserInfo);

                } else {
                    UserInfo userInfo = msg.getFromUser();
                    intent.putExtra(StaticValueHelper.TARGET_ID, userInfo.getUserName());
                    intent.putExtra(StaticValueHelper.TARGET_APP_KEY, userInfo.getAppKey());
                    intent.putExtra(StaticValueHelper.GROUP_ID, groupId);
                    intent.putExtra(StaticValueHelper.IS_FRIEDN, userInfo.isFriend());
                    intent.setClass(mContext, FriendInfoActivity.class);

                    ((Activity) mContext).startActivityForResult(intent, ChatMsgActivity.RequestCode_LookUserInfo);
                }
                //如果存在正在播放的录音，暂停。
                if (mController != null){
                    mController.pauseVoice();
                }
            }
        });

        viewHolder.headIcon.setTag(position);
        viewHolder.headIcon.setOnLongClickListener(mLongClickListener);
    }

    private void processVariousMsg(ViewHolder holder, Message msg, int position) {
        switch (msg.getContentType()) {
            case text:
                TextContent textContent = (TextContent) msg.getContent();
                String extraBusiness = textContent.getStringExtra("businessCard");
                if (extraBusiness != null) {
                    holder.txtContent.setVisibility(View.GONE);
                    holder.ll_businessCard.setVisibility(View.VISIBLE);
                    mController.handleBusinessCard(holder, msg, position);
                } else {
                    /**
                     * BUG .
                     * 当前作为发送方，发送消息后，
                     * ll_businessCard为null，加载布局报nullPointerException
                     *
                     * 问题所在：
                     *     xml布局文件错误：已修复
                     *     理论上该行代码可以删除
                     */
                    if (holder.ll_businessCard != null) {
                        holder.ll_businessCard.setVisibility(View.GONE);
                    }
                    holder.txtContent.setVisibility(View.VISIBLE);
                    mController.handleTextMessage(holder, msg, position);
                }
                break;
            case image:
                mController.handleImgMessage(holder, msg, position);

                break;
            case file:
                FileContent fileContent = (FileContent) msg.getContent();
                String extra = fileContent.getStringExtra("video");
                if (!TextUtils.isEmpty(extra)) {
                    //mController.handleVideo(msg, holder, position);
                } else {
                    mController.handleFileMsg(holder, msg, position);
                }
                break;
            case voice:
                mController.handleVoiceMsg(holder, msg, position);
                break;
            case location:
                mController.handleLocationMsg(holder, msg, position);
                break;
            case eventNotification:
                //mController.handleGroupChangeMsg(msg, holder);
                break;
            case prompt:
                //mController.handlePromptMsg(msg, holder);
                break;
            default:
                //mController.handleCustomMsg(msg, holder);

        }
    }

    private void msgReceiptionSituation(ViewHolder viewHolder, Message msg) {

        if (msg.getDirect() == MessageDirect.send && !msg.getContentType().equals(ContentType.custom) &&
                !msg.getContentType().equals(ContentType.custom) && viewHolder.text_receipt != null) {

            if (msg.getUnreceiptCnt() == 0) {
                if (msg.getTargetType() == ConversationType.group) {
                    viewHolder.text_receipt.setText("全部已读");
                } else if (!((UserInfo) msg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                    viewHolder.text_receipt.setText("已读");
                }
                viewHolder.text_receipt.setTextColor(mContext.getResources().getColor(R.color.message_already_receipt));
            } else {

                if (msg.getTargetType() == ConversationType.group) {
                    viewHolder.text_receipt.setTextColor(Integer.parseInt(msg.getUnreceiptCnt() + "人未读"));
                } else if (!((UserInfo) msg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                    viewHolder.text_receipt.setText("未读");
                }
                viewHolder.text_receipt.setTextColor(mContext.getResources().getColor(R.color.message_no_receipt));
            }
        }


    }

    class ViewHolder {
        public TextView msgTime;
        public CircleImageView headIcon;
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


    public void showReSendDialog(final ViewHolder viewHolder, final Message msg) {
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
                                resendTxtOrVoice(viewHolder, msg);
                                break;

                            case image:
                                resendImage(viewHolder, msg);
                                break;

                            case file:
                                resendFile(viewHolder, msg);
                                break;

                            default:
                                ITosast.showShort(mContext, msg.getContentType().toString() + "类型尚未实现消息重发").show();
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
        reSendDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        reSendDialog.show();
    }

    private void resendTxtOrVoice(final ViewHolder holder, Message msg) {
        holder.resend.setVisibility(View.GONE);
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mController.mSendingAnim);

        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {

                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    HandleResponseCode.onHandle(mContext, i);
                }
            });
        }
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(msg, options);
    }


    private void resendImage(final ViewHolder holder, Message msg) {
        holder.resend.setVisibility(View.GONE);
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mController.mSendingAnim);
        holder.picture.setAlpha((float) 0.7);
        holder.progressTv.setVisibility(View.VISIBLE);

        msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
            @Override
            public void onProgressUpdate(final double v) {

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.progressTv.setText(("" + v * 100) + "%");
                    }
                });
            }
        });
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.picture.setAlpha((float) 1.0);
                    holder.progressTv.setVisibility(View.GONE);
                    HandleResponseCode.onHandle(mContext, i);
                }
            });
        }
    }


    private void resendFile(final ViewHolder holder, Message msg) {
        if (holder.contentLl != null)
            holder.contentLl.setBackgroundColor(Color.parseColor("#86222222"));
        holder.resend.setVisibility(View.GONE);
        holder.progressTv.setVisibility(View.VISIBLE);
        try {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(final double progress) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String progressStr = (int) (progress * 100) + "%";
                            holder.progressTv.setText(progressStr);
                        }
                    });
                }
            });
            if (!msg.isSendCompleteCallbackExists()) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        holder.progressTv.setVisibility(View.GONE);
                        //此方法是api21才添加的如果低版本会报错找不到此方法.升级api或者使用ContextCompat.getDrawable
                        holder.contentLl.setBackground(mContext.getResources().getDrawable(R.drawable.mycat_msg_send_bg));
                        if (status != 0) {

                            HandleResponseCode.onHandle(mContext , status);
                            holder.resend.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);
            JMessageClient.sendMessage(msg, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public abstract static class ContentLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {

            onContentLoingClick((Integer) v.getTag(), v);
            return true;
        }

        public abstract void onContentLoingClick(int pos, View view);

    }

    public ChatListAdapterController getmController() {
        return mController;
    }
}
