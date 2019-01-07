package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.VideoMovie.StartMovieView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by initializing on 2018/10/5.
 */

public class StartMovieActivity extends AppCompatActivity {


    @BindView(R.id.vv_startMovie)
    StartMovieView startMovie;
    @BindView(R.id.bt_toLoginBtn)
    Button toLoginBtn;

    private Boolean flag = true;
    private SharedPreferences.Editor editor ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.startmovie_layout);
        ButterKnife.bind(this);

        init();
    }

    //初始化操作
    private void init() {

        /**
         * 第一次启动为true
         */
        if (flag) {
            //后续启动为false
            editor = getSharedPreferences("Start_Movie_First", MODE_PRIVATE).edit();
            editor.putString("isFirst", "false");
            editor.apply();
            flag = false;
        }

        //视频相关操作
        final String videoPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_movie2).toString();
        startMovie.setVideoPath(videoPath);
        startMovie.start();
        startMovie.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        });

        startMovie.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startActivity(new Intent(StartMovieActivity.this, LoginActivity.class));
            }
        });

    }


    /*
       可以通过点击跳过按钮来实现进入到登录Activity
     */
    @OnClick(R.id.bt_toLoginBtn)
    public void onViewClicked() {
        startActivity(new Intent(StartMovieActivity.this, LoginActivity.class));
    }
}
