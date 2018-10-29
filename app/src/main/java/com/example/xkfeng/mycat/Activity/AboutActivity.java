package com.example.xkfeng.mycat.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.WaveView;
import com.example.xkfeng.mycat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {


    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.waveViewId)
    WaveView waveViewId;
    @BindView(R.id.lineLayoutId)
    LinearLayout lineLayoutId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_layout);
        ButterKnife.bind(this);

        initView();
    }

    /**
     *   界面效果的初始化
     *   标题栏的初始化
     *   水波纹动画的启动
     */
    private void initView() {

        setTitle();

        setWaveView();

    }

    /**
     *   标题的初始化
     */
    private void setTitle(){

        indexTitleLayout.setTitleItemClickListener(new IndexTitleLayout.TitleItemClickListener() {
            @Override
            public void leftViewClick(View view) {
                //返回，退出当前Activity
                finish();
            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {
                Toast.makeText(AboutActivity.this, "Right Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 水波纹动画的启动
     */
    private void setWaveView(){
        waveViewId.startAnimation();
        waveViewId.startImageRotate();
    }
}
