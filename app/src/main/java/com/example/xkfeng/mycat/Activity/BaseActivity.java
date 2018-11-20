package com.example.xkfeng.mycat.Activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.xkfeng.mycat.Model.User;
import com.example.xkfeng.mycat.NetWork.HttpHelper;
import com.example.xkfeng.mycat.NetWork.OkHttpProcesser;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ActivityController;
import com.example.xkfeng.mycat.Util.UserAutoLoginHelper;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by initializing on 2018/10/5.
 */

public class BaseActivity extends AppCompatActivity {

    private ForceOfflineReceiver receiver;

    private UserAutoLoginHelper userAutoLoginHelper;

    private static boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isFirst) {
            /**
             * 极光SDK初始化
             */
            isFirst = false;
            JMessageClient.init(getApplicationContext(), true);
            userAutoLoginHelper = UserAutoLoginHelper.getUserAutoLoginHelper(getApplicationContext());
            userAutoLoginHelper.setRoaming(true);


            /**
             * 网络框架选择的初始化
             */
            HttpHelper.initHttpProcesser(new OkHttpProcesser());

        }


        ActivityController.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        ActivityController.removeActivity(this);

    }

    /*
       在onResume 和 onStop中的操作确保了
       只在处于栈顶的Activity中可以触发强制退出的广播
     */
    @Override
    protected void onResume() {


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.xkfeng.forceofflinereceiver");
        receiver = new ForceOfflineReceiver();
        registerReceiver(receiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onStop() {

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onStop();
    }

    public class ForceOfflineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            new AlertDialog.Builder(context)
                    .setTitle("Waring")
                    .setMessage("The account is logged in elsewhere")
                    //不可取消
                    .setCancelable(false)
                    .setPositiveButton("Re-loading", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭所有的Activity
                            ActivityController.finishAll();

                            //转换到登陆界面
                            Intent logIntent = new Intent();
                            intent.setClass(context, LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }
}
