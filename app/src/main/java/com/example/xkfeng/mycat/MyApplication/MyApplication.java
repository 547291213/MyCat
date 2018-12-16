package com.example.xkfeng.mycat.MyApplication;

import android.app.Application;
import android.os.Environment;

import com.example.xkfeng.mycat.NetWork.HttpHelper;
import com.example.xkfeng.mycat.NetWork.OkHttpProcesser;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.UserAutoLoginHelper;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;

public class MyApplication extends Application {
    private UserAutoLoginHelper userAutoLoginHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        JMessageClient.init(getApplicationContext(), true);
        userAutoLoginHelper = UserAutoLoginHelper.getUserAutoLoginHelper(getApplicationContext());
        userAutoLoginHelper.setRoaming(true);


        /**
         * 网络框架选择的初始化
         */
        HttpHelper.initHttpProcesser(new OkHttpProcesser());


    }
}
