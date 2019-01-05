package com.example.xkfeng.mycat.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.ScrollControllViewPager;
import com.example.xkfeng.mycat.DrawableView.SendFileController;
import com.example.xkfeng.mycat.DrawableView.SendFileView;
import com.example.xkfeng.mycat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendFileActivity extends BaseActivity {


    @BindView(R.id.sfv_sendFileView)
    SendFileView sfvSendFileView;

    private SendFileController sendFileController ;

    private static final String TAG = "SendFileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_file_layout);
        ButterKnife.bind(this);
        sfvSendFileView.initModule();

        sendFileController = new SendFileController(this , sfvSendFileView) ;
        sfvSendFileView.setOnClickListner(sendFileController);
        sfvSendFileView.setOnPagerChangeListener(sendFileController);


    }

}
