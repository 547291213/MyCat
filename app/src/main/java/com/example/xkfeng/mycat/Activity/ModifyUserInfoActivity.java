package com.example.xkfeng.mycat.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.UserInfoScrollView;
import com.example.xkfeng.mycat.Fragment.MessageFragment;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;

public class ModifyUserInfoActivity extends BaseActivity {

    @BindView(R.id.iv_userinfoImage)
    ImageView ivUserinfoImage;
    @BindView(R.id.uisv_scrollView)
    UserInfoScrollView uisvScrollView;

    private static final String TAG = "ModifyUserInfoActivity";
    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    private int height;
    private Boolean flag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_userinfo_layout);
        ButterKnife.bind(this);

        /**
         * 主显示布局的初始化
         */
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {

        /**
         * 设置显示用户名
         */
        indexTitleLayout.setMiddleText(JMessageClient.getMyInfo().getUserName() + "的资料");


        ViewTreeObserver viewTreeObserver = ivUserinfoImage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: befor: " +  uisvScrollView.getPaddingTop());
                indexTitleLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d(TAG, "onGlobalLayout: after : " + uisvScrollView.getPaddingTop());
                height = ivUserinfoImage.getHeight();

                uisvScrollView.setScrollChangedListener(new UserInfoScrollView.ScrollChangedListener() {
                    @Override
                    public void onScrollChanged(int l, int y, int oldl, int oldt) {

                        /**
                         * 这里设置标题只在出现滑动的时候显示
                         */
                        if (flag) {
                            flag = false;
                            indexTitleLayout.setVisibility(View.VISIBLE);
                        }

                        if (y <= 0) {
                            //设置标题的背景颜色
                            indexTitleLayout.setBackgroundColor(Color.argb((int) 0, 144, 151, 166));
                            indexTitleLayout.setLeftBtnDrawable(IndexTitleLayout.NULL_DRAWABLE);


                            /**
                             * 设置标题栏属性
                             */
                            setIndexTitleLayout();
                        } else if (y > 0 && y <= height) {
                            //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
                            float scale = (float) y / height;
                            float alpha = (255 * scale);
                            indexTitleLayout.setMiddleTextColor(Color.argb((int) alpha, 255, 255, 255));
                            indexTitleLayout.setBackgroundColor(Color.argb((int) alpha, 144, 151, 166));
                            indexTitleLayout.setLeftBtnDrawable(R.drawable.back_white);

                            /**
                             * 设置标题栏属性
                             */
                            setIndexTitleLayout();
                        } else {
                            //滑动到ImageView下面设置普通颜色
                            indexTitleLayout.setBackgroundColor(Color.argb((int) 255, 144, 151, 166));

                            indexTitleLayout.setLeftBtnDrawable(R.drawable.back_blue);

                            /**
                             * 设置标题栏属性
                             */
                            setIndexTitleLayout();
                        }

                    }
                });

            }
        });
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

        /**
         *
         * 注意------这里特殊
         * 因为该方法需要随用户滑动而不断地调用，
         * 而初始默认paddingTop为0，如果按照之前的调用方式
         * 那么paddingTop就会一直累加
         *
         * paddingTop的具体竖直具体可以参考下面的log打印数据
         *
         */
//        Log.d(TAG, "setIndexTitleLayout: " +
//                " OriginTop:" + indexTitleLayout.getPaddingTop()+
//                " STATUS_TOP:" + MessageFragment.STATUSBAR_PADDING_TOP  +
//                " statusHeight:" + DensityUtil.getStatusHeight(this));


        indexTitleLayout.setPadding(MessageFragment.STATUSBAR_PADDING_lEFT ,
                MessageFragment.STATUSBAR_PADDING_TOP ,
                MessageFragment.STATUSBAR_PADDING_RIGHT ,
                MessageFragment.STATUSBAR_PADDING_BOTTOM);


//        设置点击事件监听
        indexTitleLayout.setTitleItemClickListener(new IndexTitleLayout.TitleItemClickListener() {
            @Override
            public void leftViewClick(View view) throws Exception {
                /**
                 * 退出当前Activity
                 */
                finish();
            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {

            }
        });
    }

}
