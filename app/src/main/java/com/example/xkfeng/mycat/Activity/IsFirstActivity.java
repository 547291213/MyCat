package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.example.xkfeng.mycat.R;

public class IsFirstActivity extends BaseActivity {

    /**
     * 启动动画只在用户第一次运行程序的时候启动
     * 用SharedPreferendces来实现
     */
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 第一次启动为true
         */
        sharedPreferences = getSharedPreferences("Start_Movie_First", MODE_PRIVATE);
        String isFirst = sharedPreferences.getString("isFirst", "true");
        if (!isFirst.equals("true")) {

            // 如果不是第一次启动app，则正常显示启动屏
            setContentView(R.layout.isfirst_layout);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(IsFirstActivity.this, LoginActivity.class));

                }
            }, 2000);


        } else {
            startActivity(new Intent(IsFirstActivity.this, StartMovieActivity.class));
        }

    }

}
