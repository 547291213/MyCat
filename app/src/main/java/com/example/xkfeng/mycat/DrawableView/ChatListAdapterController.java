package com.example.xkfeng.mycat.DrawableView;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

public class ChatListAdapterController {

    private Activity mActivity;
    private Context mContext;
    private ChatListAdapter chatListAdapter;
    private Conversation mConversation;
    public Animation mSendingAnim;
    private ChatListAdapter.ContentLongClickListener contentLongClickListener;
    private List<Message> mMsgList;
    private int del;
    private UserInfo mUserInfo;
    private Queue<Message> mMsgQueue = new LinkedList<Message>();
    private int mSendMsgId;

    public ChatListAdapterController(Context context,
                                     ChatListAdapter chatListAdapter,
                                     Conversation conversation,
                                     List<Message> messageList,
                                     ChatListAdapter.ContentLongClickListener contentLongClickListener,
                                     float density) {

        mContext = context;
        mActivity = (Activity) context;
        mConversation = conversation;

        if (conversation.getType() == ConversationType.single) {
            mUserInfo = (UserInfo) conversation.getTargetInfo();
        }
        mSendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.loading_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        mSendingAnim.setInterpolator(lin);


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

        if (path == null) {

            imageContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int i, String s, File file) {
                    if (i == 0) {
                        ImageView imageView = setPictureScale(jiguang, msg, file.getPath(), viewHolder.picture);
                        Glide.with(mContext).load(imageView);

                    }
                }
            });
        } else {
            ImageView imageView = setPictureScale(jiguang, msg, path, viewHolder.picture);
            Glide.with(mContext).load(imageView);

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
//            try {
//                setPictureScale(path, viewHolder.picture);
//                Picasso.with(mContext).load(new File(path)).into(viewHolder.picture);
//            } catch (NullPointerException e) {
//                Picasso.with(mContext).load(IdHelper.getDrawable(mContext, "jmui_picture_not_found"))
//                        .into(viewHolder.picture);
//            }
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
//            viewHolder.picture.setOnClickListener(new BtnOrTxtListener(position, viewHolder));
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
