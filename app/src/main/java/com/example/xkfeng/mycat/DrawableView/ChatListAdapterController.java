package com.example.xkfeng.mycat.DrawableView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.Activity.FriendInfoActivity;
import com.example.xkfeng.mycat.Activity.PreviewPictureActivity;
import com.example.xkfeng.mycat.Activity.UserInfoActivity;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.FileHelper;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class ChatListAdapterController {

    private Activity mActivity;
    private Context mContext;
    private ChatListAdapter chatListAdapter;
    private Conversation mConversation;
    public Animation mSendingAnim;
    private ChatListAdapter.ContentLongClickListener contentLongClickListener;
    private List<Message> mMsgList;
    private float del;
    private UserInfo mUserInfo;
    private Queue<Message> mMsgQueue = new LinkedList<Message>();
    private int mSendMsgId;
    private Map<Integer, UserInfo> mUserInfoMap = new HashMap<>();


    /**
     * 录音相关参数
     */
    private List<Integer> mIndexList = new ArrayList<>();
    private int mPosition;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private FileInputStream mFIS;
    private FileDescriptor mFD;
    private AnimationDrawable mVoiceAnimationDrawable;
    private boolean isPause = false;   //


    public ChatListAdapterController(Context context,
                                     ChatListAdapter chatListAdapter,
                                     Conversation conversation,
                                     List<Message> messageList,
                                     ChatListAdapter.ContentLongClickListener contentLongClickListener,
                                     float density) {

        mContext = context;
        mActivity = (Activity) context;
        mConversation = conversation;
        mMsgList = messageList;
        del = density;
        this.chatListAdapter = chatListAdapter;
        this.contentLongClickListener = contentLongClickListener;

        if (conversation.getType() == ConversationType.single) {
            mUserInfo = (UserInfo) conversation.getTargetInfo();
        }
        mSendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.loading_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        mSendingAnim.setInterpolator(lin);


        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

    }

    public void handleBusinessCard(final ChatListAdapter.ViewHolder viewHolder, final Message msg, int position) {
        final TextContent[] textContent = {(TextContent) msg.getContent()};
        final String[] mUserName = {textContent[0].getStringExtra("userName")};
        viewHolder.ll_businessCard.setTag(position);
        int key = mUserName[0].hashCode();
        UserInfo userInfo = mUserInfoMap.get(key);
        if (userInfo == null) {
            JMessageClient.getUserInfo(mUserName[0], "", new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    switch (i) {
                        case 0:
                            mUserInfoMap.put((mUserName[0]).hashCode(), userInfo);
                            String name = userInfo.getNickname();
                            //如果没有昵称,名片上面的位置显示用户名
                            //如果有昵称,上面显示昵称,下面显示用户名
                            if (TextUtils.isEmpty(name)) {
                                viewHolder.tv_userName.setText("");
                                viewHolder.tv_nickUser.setText(mUserName[0]);
                            } else {
                                viewHolder.tv_nickUser.setText(name);
                                viewHolder.tv_userName.setText("用户名: " + mUserName[0]);
                            }
                            if (userInfo.getAvatarFile() != null) {
                                viewHolder.business_head.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatarFile().getAbsolutePath()));
                            } else {
                                viewHolder.business_head.setImageResource(R.mipmap.log);
                            }
                            break;

                        default:
                            HandleResponseCode.onHandle(mContext, i);
                            break;
                    }
                }
            });
        } else {
            String name = userInfo.getNickname();
            //如果没有昵称,名片上面的位置显示用户名
            //如果有昵称,上面显示昵称,下面显示用户名
            if (TextUtils.isEmpty(name)) {
                viewHolder.tv_userName.setText("");
                viewHolder.tv_nickUser.setText(mUserName[0]);
            } else {
                viewHolder.tv_nickUser.setText(name);
                viewHolder.tv_userName.setText("用户名: " + mUserName[0]);
            }
            if (userInfo.getAvatarFile() != null) {
                viewHolder.business_head.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatarFile().getAbsolutePath()));
            } else {
                viewHolder.business_head.setImageResource(R.mipmap.log);
            }
        }


        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case created:
                    if (null != mUserInfo) {
                        viewHolder.sendingIv.setVisibility(View.GONE);
                        viewHolder.resend.setVisibility(View.VISIBLE);
                        viewHolder.text_receipt.setVisibility(View.GONE);
                    }
                    break;
                case send_success:
                    viewHolder.text_receipt.setVisibility(View.VISIBLE);
                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    viewHolder.resend.setVisibility(View.GONE);
                    break;
                case send_fail:
                    viewHolder.text_receipt.setVisibility(View.GONE);
                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    viewHolder.resend.setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    sendingTextOrVoice(viewHolder, msg);
                    break;
            }
        } else {
            if (mConversation.getType() == ConversationType.group) {
                if (msg.isAtMe()) {
                    mConversation.updateMessageExtra(msg, "isRead", true);
                }
                if (msg.isAtAll()) {
                    mConversation.updateMessageExtra(msg, "isReadAtAll", true);
                }
                viewHolder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    viewHolder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    viewHolder.displayName.setText(msg.getFromUser().getNickname());
                }
            }
        }
        if (viewHolder.resend != null) {
            viewHolder.resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatListAdapter.showReSendDialog(viewHolder, msg);
                }
            });
        }

        viewHolder.ll_businessCard.setOnClickListener(new BusinessCardClickListener(viewHolder, mUserName[0]));
        viewHolder.ll_businessCard.setOnLongClickListener(contentLongClickListener);

    }

    private class BusinessCardClickListener implements View.OnClickListener {

        private ChatListAdapter.ViewHolder holder;
        private String userName;

        public BusinessCardClickListener(ChatListAdapter.ViewHolder holder, String userName) {
            this.holder = holder;
            this.userName = userName;

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            if (JMessageClient.getMyInfo().getUserName().equals(userName)) {

                intent.setClass(mContext, UserInfoActivity.class);
            } else {
                intent.setClass(mContext, FriendInfoActivity.class);
                intent.putExtra(StaticValueHelper.TARGET_ID, userName);
                UserInfo userInfo = mUserInfoMap.get(userName.hashCode());
                intent.putExtra(StaticValueHelper.IS_FRIEDN, userInfo.isFriend());
            }
            mContext.startActivity(intent);

        }
    }

    public void handleVoiceMsg(final ChatListAdapter.ViewHolder viewHolder, final Message msg, int position) {
        final MessageContent voiceContent = (VoiceContent) msg.getContent();
        final MessageDirect direct = msg.getDirect();
        int voiceLength = ((VoiceContent) voiceContent).getDuration();
        viewHolder.voiceLength.setText(voiceLength + "'");
        //控制语音消息的显示长度，随语音本身的长度变动
        int width = (int) (-0.04 * voiceLength * voiceLength + 4.526 * voiceLength + 75.214);
        viewHolder.txtContent.setWidth((int) (width * del));
        viewHolder.txtContent.setTag(position);
        viewHolder.txtContent.setOnLongClickListener(contentLongClickListener);


        if (direct == MessageDirect.send) {

            viewHolder.voice.setImageResource(R.drawable.send_3);
            switch (msg.getStatus()) {
                case created:
                    viewHolder.sendingIv.setVisibility(View.VISIBLE);
                    viewHolder.resend.setVisibility(View.GONE);
                    viewHolder.text_receipt.setVisibility(View.GONE);
                    break;

                case send_success:
                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    viewHolder.text_receipt.setVisibility(View.VISIBLE);
                    viewHolder.resend.setVisibility(View.GONE);
                    break;

                case send_fail:
                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    viewHolder.resend.setVisibility(View.VISIBLE);
                    viewHolder.text_receipt.setVisibility(View.GONE);
                    break;

                case send_going:
                    sendingTextOrVoice(viewHolder, msg);
                    break;
            }
        } else {
            switch (msg.getStatus()) {
                case receive_success:
                    viewHolder.voice.setImageResource(R.drawable.send_3);
                    // 收到语音，设置未读
                    if (msg.getContent().getBooleanExtra("isRead") == null
                            || !msg.getContent().getBooleanExtra("isRead")) {
                        mConversation.updateMessageExtra(msg, "isRead", false);
                        viewHolder.readStatus.setVisibility(View.VISIBLE);
                        if (mIndexList.size() > 0) {
                            if (!mIndexList.contains(position)) {
                                addToListAndSort(position);
                            }
                        } else {
                            addToListAndSort(position);
                        }
                    } else if (msg.getContent().getBooleanExtra("isRead")) {
                        viewHolder.readStatus.setVisibility(View.GONE);
                    }
                    break;


                case receive_fail:

                    viewHolder.voice.setImageResource(R.drawable.send_3);
                    //接受失败，从服务器上重新下载
                    ((VoiceContent) voiceContent).downloadVoiceFile(msg, new DownloadCompletionCallback() {
                        @Override
                        public void onComplete(int i, String s, File file) {

                        }
                    });
                    break;

                case receive_going:
                default:
                    break;
            }
        }

        if (viewHolder.resend != null) {
            viewHolder.resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (msg.getContent() != null) {
                        chatListAdapter.showReSendDialog(viewHolder, msg);
                    } else {
                        Toast.makeText(mContext, "暂无外部存储", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        viewHolder.txtContent.setOnClickListener(new OnItemClickListener(viewHolder, position));


    }

    private void addToListAndSort(int mPosition) {
        mIndexList.add(mPosition);
        Collections.sort(mIndexList);
    }

    public void playVoice(final ChatListAdapter.ViewHolder viewHolder, Message msg, int position, final boolean isSender) {

        //记录当前位置
        mPosition = position;
        mediaPlayer.reset();
        VoiceContent vc = (VoiceContent) msg.getContent();
        try {
            mFIS = new FileInputStream(vc.getLocalPath());
            mFD = mFIS.getFD();
            mediaPlayer.setDataSource(mFD);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mVoiceAnimationDrawable = (AnimationDrawable) viewHolder.voice.getDrawable();
                    mVoiceAnimationDrawable.start();
                    mediaPlayer.start();
                }
            });


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mVoiceAnimationDrawable.stop();
                    mediaPlayer.reset();
                    if (isSender) {
                        viewHolder.voice.setImageResource(R.drawable.send_3);
                    } else {
                        viewHolder.voice.setImageResource(R.drawable.mycat_voice_receive_3);
                    }
                    //当前音乐不为暂停状态
                    isPause = false ;
                }
            });
        } catch (Exception e) {
            Toast.makeText(mContext, "文件丢失 ，请尝试重新获取",
                    Toast.LENGTH_SHORT).show();
            VoiceContent vc1 = (VoiceContent) msg.getContent();
            vc1.downloadVoiceFile(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        ITosast.showShort(mContext, "下载完成").show();
                    } else {
                        ITosast.showShort(mContext, "下载失败").show();
                    }
                }
            });
        } finally {
            try {
                if (mFIS != null) {
                    mFIS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void handleTextMessage(final ChatListAdapter.ViewHolder viewHolder, final Message msg, int position) {
        final String content = ((TextContent) msg.getContent()).getText();
        viewHolder.txtContent.setVisibility(View.VISIBLE);
        viewHolder.txtContent.setText(content);
        viewHolder.txtContent.setTag(position);
        viewHolder.txtContent.setOnLongClickListener(contentLongClickListener);

        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case created:
                    if (mUserInfo != null) {

                        viewHolder.resend.setVisibility(View.VISIBLE);
                        viewHolder.sendingIv.setVisibility(View.GONE);
                        viewHolder.text_receipt.setVisibility(View.GONE);
                    }

                    break;

                case send_success:
                    viewHolder.resend.setVisibility(View.GONE);
                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    viewHolder.text_receipt.setVisibility(View.VISIBLE);
                    break;

                case send_fail:
                    viewHolder.resend.setVisibility(View.VISIBLE);
                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    viewHolder.text_receipt.setVisibility(View.GONE);
                    break;

                case send_going:
                    sendingTextOrVoice(viewHolder, msg);
                    break;
            }
        } else {

            if (mConversation.getType() == ConversationType.group) {
                if (msg.isAtMe()) {
                    mConversation.updateMessageExtra(msg, "isRead", true);
                }
                if (msg.isAtAll()) {
                    mConversation.updateMessageExtra(msg, "isReadAtAll", true);
                }
                viewHolder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    viewHolder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    viewHolder.displayName.setText(msg.getFromUser().getNickname());
                }
            }
        }

        if (viewHolder.resend != null) {
            viewHolder.resend.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    chatListAdapter.showReSendDialog(viewHolder, msg);
                }
            });
        }
    }

    public void handleImgMessage(final ChatListAdapter.ViewHolder viewHolder, final Message msg, final int position) {
        final ImageContent imageContent = (ImageContent) msg.getContent();
        final String jiguang = imageContent.getStringExtra("jiguang");
        //拿取本地略缩图
        final String path = imageContent.getLocalThumbnailPath();
        //图片实际路径
        if (path == null) {

            imageContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int i, String s, final File file) {
                    if (i == 0) {
                        final ImageView imageView = setPictureScale(jiguang, msg, file.getPath(), viewHolder.picture);
                        Picasso.get().load(file).into(imageView);
                    }
                }
            });
        } else {
            final ImageView imageView = setPictureScale(jiguang, msg, path, viewHolder.picture);
            /**
             * 重要问题记录：
             * 用Glide加载图片会出现：
             * 图片可以加载，但是当图片的位置被改变当前界面就会异常退出。（引起图片位置改变的原因，1：数据拉动，2系统软件盘的弹出。。）
             * 用Picasso无BUG 。。。
             *
             */
            Picasso.get().load(new File(path)).into(imageView);
        }

//        接收图片
        if (msg.getDirect() == MessageDirect.receive) {
            //群聊中显示昵称
            if (mConversation.getType() == ConversationType.group) {
                viewHolder.displayName.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(msg.getFromUser().getNickname())) {
                    viewHolder.displayName.setText(msg.getFromUser().getUserName());
                } else {
                    viewHolder.displayName.setText(msg.getFromUser().getNickname());
                }
            }

            switch (msg.getStatus()) {
                case receive_fail:
                    viewHolder.picture.setImageResource(R.drawable.ic_warnning_red);
                    viewHolder.resend.setVisibility(View.VISIBLE);
                    viewHolder.resend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
                                @Override
                                public void onComplete(int i, String s, File file) {
                                    if (i == 0) {
                                        ITosast.showShort(mContext, "下载成功")
                                                .show();
                                        viewHolder.sendingIv.setVisibility(View.GONE);
                                        chatListAdapter.notifyDataSetChanged();
                                    } else {
                                        ITosast.showShort(mContext, "下载失败")
                                                .show();
                                    }
                                }
                            });
                        }
                    });
                    break;
                default:
            }
            // 发送图片方，直接加载缩略图
        } else {

            //检查状态
            switch (msg.getStatus()) {
                case created:
                    viewHolder.picture.setEnabled(false);
                    viewHolder.resend.setEnabled(false);
                    viewHolder.text_receipt.setVisibility(View.GONE);
                    viewHolder.sendingIv.setVisibility(View.VISIBLE);
                    viewHolder.resend.setVisibility(View.GONE);
                    viewHolder.progressTv.setText("0%");
                    break;
                case send_success:
                    viewHolder.picture.setEnabled(true);
                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.text_receipt.setVisibility(View.VISIBLE);
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    viewHolder.picture.setAlpha(1.0f);
                    viewHolder.progressTv.setVisibility(View.GONE);
                    viewHolder.resend.setVisibility(View.GONE);
                    break;
                case send_fail:
                    viewHolder.resend.setEnabled(true);
                    viewHolder.picture.setEnabled(true);
                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    viewHolder.text_receipt.setVisibility(View.GONE);
                    viewHolder.picture.setAlpha(1.0f);
                    viewHolder.progressTv.setVisibility(View.GONE);
                    viewHolder.resend.setVisibility(View.VISIBLE);
                    break;
                case send_going:
                    viewHolder.picture.setEnabled(false);
                    viewHolder.resend.setEnabled(false);
                    viewHolder.text_receipt.setVisibility(View.GONE);
                    viewHolder.resend.setVisibility(View.GONE);
                    sendingImage(msg, viewHolder);
                    break;
                default:
                    viewHolder.picture.setAlpha(0.75f);
                    viewHolder.sendingIv.setVisibility(View.VISIBLE);
                    viewHolder.sendingIv.startAnimation(mSendingAnim);
                    viewHolder.progressTv.setVisibility(View.VISIBLE);
                    viewHolder.progressTv.setText("0%");
                    viewHolder.resend.setVisibility(View.GONE);
                    //从别的界面返回聊天界面，继续发送
                    if (!mMsgQueue.isEmpty()) {
                        Message message = mMsgQueue.element();
                        if (message.getId() == msg.getId()) {
                            MessageSendingOptions options = new MessageSendingOptions();
                            options.setNeedReadReceipt(true);
                            JMessageClient.sendMessage(message, options);
                            mSendMsgId = message.getId();
                            sendingImage(message, viewHolder);
                        }
                    }
            }
        }
        if (viewHolder.picture != null) {
            // 点击预览图片
            viewHolder.picture.setOnClickListener(new OnItemClickListener(viewHolder, position));

            viewHolder.picture.setTag(position);
            viewHolder.picture.setOnLongClickListener(contentLongClickListener);

        }
        if (msg.getDirect().equals(MessageDirect.send) && viewHolder.resend != null) {
            viewHolder.resend.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    chatListAdapter.showReSendDialog(viewHolder, msg);
                }
            });
        }
    }


    private void sendingImage(final Message msg, final ChatListAdapter.ViewHolder holder) {
        holder.picture.setAlpha(0.75f);
        holder.sendingIv.setVisibility(View.VISIBLE);
        holder.sendingIv.startAnimation(mSendingAnim);
        holder.progressTv.setVisibility(View.VISIBLE);
        holder.progressTv.setText("0%");
        holder.resend.setVisibility(View.GONE);
        //如果图片正在发送，重新注册上传进度Callback
        if (!msg.isContentUploadProgressCallbackExists()) {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double v) {
                    String progressStr = (int) (v * 100) + "%";
                    holder.progressTv.setText(progressStr);
                }
            });
        }
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    if (!mMsgQueue.isEmpty() && mMsgQueue.element().getId() == mSendMsgId) {
                        mMsgQueue.poll();
                        if (!mMsgQueue.isEmpty()) {
                            Message nextMsg = mMsgQueue.element();
                            MessageSendingOptions options = new MessageSendingOptions();
                            options.setNeedReadReceipt(true);
                            JMessageClient.sendMessage(nextMsg, options);
                            mSendMsgId = nextMsg.getId();
                        }
                    }
                    holder.picture.setAlpha(1.0f);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.progressTv.setVisibility(View.GONE);
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConversation.createSendMessage(customContent);
                        chatListAdapter.addMsgToList(customMsg);
                    } else if (status != 0) {
                        holder.resend.setVisibility(View.VISIBLE);
                    }

                    Message message = mConversation.getMessage(msg.getId());
                    mMsgList.set(mMsgList.indexOf(msg), message);
//                    notifyDataSetChanged();
                }
            });

        }
    }

    private void sendingTextOrVoice(final ChatListAdapter.ViewHolder viewHolder, final Message msg) {

        viewHolder.text_receipt.setVisibility(View.GONE);
        viewHolder.resend.setVisibility(View.GONE);
        viewHolder.sendingIv.setVisibility(View.VISIBLE);
        viewHolder.sendingIv.startAnimation(mSendingAnim);

        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {

                    viewHolder.sendingIv.clearAnimation();
                    viewHolder.sendingIv.setVisibility(View.GONE);
                    switch (i) {
                        case 0:
                            viewHolder.resend.setVisibility(View.VISIBLE);
                            break;

                        case 803005:
                            viewHolder.resend.setVisibility(View.VISIBLE);
                            ITosast.showShort(mContext, "发送失败， 你不在群组中")
                                    .show();
                            break;

                        case 803008:
                            //语音消息，添加到列表


                            break;
                    }
                }
            });
        }
    }


    public class OnItemClickListener implements View.OnClickListener {

        private ChatListAdapter.ViewHolder holder;
        private Message msg;
        private int position;

        public OnItemClickListener(ChatListAdapter.ViewHolder viewHolder, int pos) {
            msg = mMsgList.get(pos);
            this.holder = viewHolder;
            this.position = pos;
        }

        @Override
        public void onClick(View view) {
            switch (msg.getContentType()) {
                case text:


                    break;

                case image:
                    if (holder.picture != null && view.getId() == holder.picture.getId()) {
                        if (TextUtils.isEmpty(((ImageContent) msg.getContent()).getLocalThumbnailPath())) {
                            ITosast.showShort(mContext, "获取图片数据失败").show();
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra("imagePath", ((ImageContent) msg.getContent()).getLocalThumbnailPath());
                        intent.setClass(mContext, PreviewPictureActivity.class);
                        mContext.startActivity(intent);
                    }
                    break;

                case file:

                    break;

                case voice:

                    //判断有无SD卡
                    if (!FileHelper.isSdCardExist()) {
                        ITosast.showShort(mContext, "暂无外部存储").show();
                        return;
                    }
                    //如果存在正在播放的录音动画，关闭
                    if (mVoiceAnimationDrawable != null) {
                        mVoiceAnimationDrawable.stop();
                    }
                    //点击了正在播放的录音，暂停处理
                    if (mediaPlayer.isPlaying() && mPosition == position) {
                        holder.voice.setImageResource(R.drawable.mycat_chat_item_voice_anim);
                        mVoiceAnimationDrawable = (AnimationDrawable) holder.voice.getDrawable();
                        pauseVoice(msg.getDirect(), holder.voice);
                        break;
                    }
                    holder.voice.setImageResource(R.drawable.mycat_chat_item_voice_anim);
                    mVoiceAnimationDrawable = (AnimationDrawable) holder.voice.getDrawable();

                    //播放录音（分为继续播放暂停的录音，重新播放新的录音）
                    if (isPause == true && mPosition == position) {
                        mediaPlayer.start();
                        mVoiceAnimationDrawable.start();
                    } else {
                        playVoice(holder, msg, position, msg.getDirect() == MessageDirect.send ? true : false);
                    }
                    break;

                case location:

                    break;

                default:
                    ITosast.showShort(mContext, "未知数据类型").show();
                    break;
            }
        }
    }

    private void pauseVoice(MessageDirect messageDirect, ImageView voice) {
        if (messageDirect == MessageDirect.send) {
            voice.setImageResource(R.drawable.send_3);
        } else {
            voice.setImageResource(R.drawable.mycat_voice_receive_3);
        }
        mediaPlayer.pause();
        isPause = true;
    }

    public void resumePalyVoice() {
        if (isPause == true) {
            mediaPlayer.start();
            mVoiceAnimationDrawable.start();
        }

    }

    public void pauseVoice() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 设置图片最小宽高
     *
     * @param path      图片路径
     * @param imageView 显示图片的View
     */
    private ImageView setPictureScale(String extra, Message message, String path, final ImageView imageView) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        //原图质量的10分之一
        opts.inSampleSize = 10;
        //计算图片缩放比例
        double imageWidth = opts.outWidth;
        double imageHeight = opts.outHeight;
        return setDensity(extra, message, imageWidth, imageHeight, imageView);
    }

    private ImageView setDensity(String extra, Message message, double imageWidth, double imageHeight, ImageView imageView) {
        if (extra != null) {
            imageWidth = 200;
            imageHeight = 200;
        } else {
            if (imageWidth > 350) {
                imageWidth = 550;
                imageHeight = 250;
            } else if (imageHeight > 450) {
                imageWidth = 300;
                imageHeight = 450;
            } else if ((imageWidth < 50 && imageWidth > 20) || (imageHeight < 50 && imageHeight > 20)) {
                imageWidth = 200;
                imageHeight = 300;
            } else if (imageWidth < 20 || imageHeight < 20) {
                imageWidth = 100;
                imageHeight = 150;
            } else {
                imageWidth = 300;
                imageHeight = 450;
            }
        }

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int) imageWidth;
        params.height = (int) imageHeight;
        imageView.setLayoutParams(params);

        return imageView;
    }
}
