package com.example.xkfeng.mycat.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;

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
        Dialog dialog = new Dialog(context , context.getResources().
                getIdentifier("mycat_loading_dialog_style" ,
                        "style",context.getApplicationContext().getPackageName())) ;
        View view = LayoutInflater.from(context).inflate(R.layout.mycat_loading_view , null ,false) ;
        ImageView imageView = view.findViewById(R.id.iv_loadingImg) ;
        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
        drawable.start();
        TextView textView = view.findViewById(R.id.tv_loadingText) ;
        textView.setText(msg);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        return dialog ;
    }
}
