package com.example.xkfeng.mycat.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.WaveView;
import com.example.xkfeng.mycat.Fragment.ClassfiedFragment;
import com.example.xkfeng.mycat.Fragment.SelectedPersonFragment;
import com.example.xkfeng.mycat.R;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {


    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.waveViewId)
    WaveView waveViewId;
    @BindView(R.id.lineLayoutId)
    LinearLayout lineLayoutId;
    @BindView(R.id.tv_aboutAuthorTextView)
    TextView tvAboutAuthorTextView;
    @BindView(R.id.tv_aboutAuthorTextView1)
    TextView tvAboutAuthorTextView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_layout);
        ButterKnife.bind(this);

        initView();
    }

    /**
     * 界面效果的初始化
     * 标题栏的初始化
     * 水波纹动画的启动
     * 作者自述
     */
    private void initView() {

        setTitle();

        setWaveView();

        setTvAboutAuthorTextView();
    }

    /**
     * 作者自述
     * 艺术字体实现
     */
    private void setTvAboutAuthorTextView() {

        String fonts = "fonts/boyang.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), fonts);
        tvAboutAuthorTextView.setText("   MyCat 由博主独立自主开发，其中借鉴了不少CSDN大佬的博文，Github开源代码，" +
                "自己也使用了不少的开源框架：OkHttp" +
                "，Retorfit，RxJava，ButterKnife，Glide等等" + "\n\n   " +
                "总而言之:作者本人是开源的受益者，所以也愿意分享自己的成果，也希望自己有朝一日能为开源社区做出贡献" +
                "\n\n   如果您对项目内容和实现有疑惑欢迎你到该作品的官方CSDN评论区提出，" +
                "如果您认为该项目让您受益匪浅，还希望您能高抬贵手去Github Star一下。感激不尽！");
//        MyCat is independently developed by bloggers, and has borrowed from many CSDN guru blog posts,
//        Github open source code, and used many open source frameworks of its own: OkHttp, Retorfit, RxJava, ButterKnife, Glide, etc
//        To sum up: the author himself is a beneficiary of open source, so he is willing to share his own achievements and hopes to make
//        contributions to the open source community one day
//        If you have doubts about the content and implementation of the project, you are welcome to propose in the official CSDN comment area of the work.
//                If you think the project has benefited you a lot, I hope you can give me your honor to go to Github Star. Thanks a lot!
        tvAboutAuthorTextView1.setText("   MyCat is independently developed by bloggers, " +
                "and has borrowed from many CSDN guru blog posts," +
                "Github open source code, and used many open source frameworks of its own: " +
                "OkHttp, Retorfit, RxJava, ButterKnife, Glide, etc\n" +
                "        \n   To sum up: the author himself is a beneficiary of open source, " +
                "so he is willing to share his own achievements and hopes to make" +
                "contributions to the open source community one day\n" +
                "        \n   If you have doubts about the content and implementation of the project, " +
                "you are welcome to propose in the official CSDN comment area of the work." +
                "If you think the project has benefited you a lot, " +
                "I hope you can give me your honor to go to Github Star. Thanks a lot!");

        tvAboutAuthorTextView.setTypeface(typeface);
        tvAboutAuthorTextView1.setTypeface(typeface);
    }


    /**
     * 标题的初始化
     */
    private void setTitle() {

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
    private void setWaveView() {
        waveViewId.startAnimation();
        waveViewId.startImageRotate();
    }
}
