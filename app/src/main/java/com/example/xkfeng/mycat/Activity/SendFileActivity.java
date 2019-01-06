package com.example.xkfeng.mycat.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.widget.LinearLayout;

import com.example.xkfeng.mycat.DrawableView.SendFileController;
import com.example.xkfeng.mycat.DrawableView.SendFileView;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendFileActivity extends BaseActivity {


    @BindView(R.id.sfv_sendFileView)
    SendFileView sfvSendFileView;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;

    private SendFileController sendFileController;

    private static final String TAG = "SendFileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_file_layout);
        ButterKnife.bind(this);

        setIndexTitleLayout();


        sfvSendFileView.initModule();
        sendFileController = new SendFileController(this, sfvSendFileView);
        sfvSendFileView.setOnClickListner(sendFileController);
        sfvSendFileView.setOnPagerChangeListener(sendFileController);



    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void setIndexTitleLayout() {
        //沉浸式状态栏
        DensityUtil.fullScreen(this);
//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
    }


    public FragmentManager getSupportManager(){
        return  getSupportFragmentManager() ;
    }

}
