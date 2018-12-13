package com.example.xkfeng.mycat.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.MediaStoreSignature;
import com.example.xkfeng.mycat.Activity.BaseActivity;
import com.example.xkfeng.mycat.Activity.LoginActivity;
import com.example.xkfeng.mycat.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

public class DialogHelper {

    public static Dialog createResendDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, context.getResources().
                getIdentifier("mycat_default_dialog_style",
                        "style", context.getApplicationContext().getPackageName()));

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_chat_item_resend,
                null, false);
        dialog.setContentView(view);
        Button cancelBtn = (Button) view.findViewById(R.id.mycat_cancel_btn);
        Button resendBtn = (Button) view.findViewById(R.id.mycat_commit_btn);
        cancelBtn.setOnClickListener(listener);
        resendBtn.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createLoadingDialog(Context context, String msg) {
        Dialog dialog = new Dialog(context, context.getResources().
                getIdentifier("mycat_loading_dialog_style",
                        "style", context.getApplicationContext().getPackageName()));
        View view = LayoutInflater.from(context).inflate(R.layout.mycat_loading_view, null, false);
        ImageView imageView = view.findViewById(R.id.iv_loadingImg);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
        drawable.start();
        TextView textView = view.findViewById(R.id.tv_loadingText);
        textView.setText(msg);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    public static Dialog createForceOfflineDailog(final Context context, View.OnClickListener listener) {
        String fonts = "fonts/zhangcao.ttf";
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fonts);

        Dialog dialog = new Dialog(context, context.getResources()
                .getIdentifier("mycat_chat_force_offline_dialog", "style",
                        context.getApplicationContext().getPackageName()));

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_forced_offline, null, false);
        TextView textView = view.findViewById(R.id.mycat_title);
        textView.setTypeface(typeface);

        Button button = view.findViewById(R.id.mycat_cancel_btn);
        button.setTypeface(typeface);
        button.setOnClickListener(listener);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog createForwardingDialog(final Context context, final Activity activity , String name, final UserInfo targetUserInfo , final boolean isSingle) {
        final Dialog dialog = new Dialog(context, context.getResources()
                .getIdentifier("mycat_chat_forwarding_dialog", "style",
                        context.getApplicationContext().getPackageName()));

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_forwarding, null, false);
        final TextView cancle = view.findViewById(R.id.tv_cancle);
        TextView send = view.findViewById(R.id.tv_send);
        TextView userName = view.findViewById(R.id.tv_targetUserName);
        final TextView msgText = view.findViewById(R.id.tv_msgText);

        userName.setText(name);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final Message message = BaseActivity.forwardMsg.get(0);
        switch (message.getContentType()) {
            case text:
                TextContent text = (TextContent) message.getContent();
                if (text.getStringExtra("businessCard") != null) {
                    msgText.setText("[名片]");
                } else {
                    msgText.setText(text.getText());
                }
                break;
            case image:
                msgText.setText("[图片]");
                break;
            case file:
                msgText.setText("[文件]");
                break;
            case voice:
                msgText.setText("[录音]");
                break;

            case location:
                msgText.setText("[位置]");
                break;
            case video:
                msgText.setText("[视频]");
                break;
            default:
                msgText.setText("[其他消息 ]");
                break;
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message == null || targetUserInfo == null) {
                    dialog.dismiss();
                    return;
                }

                final Dialog loadingDialog = DialogHelper.createLoadingDialog(context , "正在发送") ;
                loadingDialog.show();
                Conversation conversation = null ;
                if (isSingle) {
                    conversation = JMessageClient.getSingleConversation(targetUserInfo.getUserName());
                    if (conversation == null) {
                        conversation = Conversation.createSingleConversation(targetUserInfo.getUserName());
                    }
                    MessageSendingOptions options = new MessageSendingOptions();
                    options.setNeedReadReceipt(true);
                    JMessageClient.forwardMessage(message, conversation, options, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            loadingDialog.dismiss();
                            dialog.dismiss();
                            if (i == 0) {
                                ITosast.showShort(context , "已发送").show();
                                activity.finish();

                            } else {
                                HandleResponseCode.onHandle(context, i);
                            }
                        }
                    });
                }

            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);

        return dialog;
    }

}
