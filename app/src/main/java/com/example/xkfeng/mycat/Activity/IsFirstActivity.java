package com.example.xkfeng.mycat.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.CustomDialog;
import com.example.xkfeng.mycat.DrawableView.SpecialProgressBarView;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ActivityController;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.UserAutoLoginHelper;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class IsFirstActivity extends BaseActivity {

    @BindView(R.id.iv_loadingText)
    TextView ivLoadingText;

    private SpecialProgressBarView specialProgressBarView;
    /**
     * 启动动画只在用户第一次运行程序的时候启动
     * 用SharedPreferendces来实现
     */
    private UserAutoLoginHelper userAutoLoginHelper;
    //延迟时间
    private final static int TIME = 5000;
    //进度条值
    private static int increase = 0;
    //Handler跟进进度条值
    private MyHandler handler = new MyHandler(this);

    class MyHandler extends Handler {
        WeakReference<Activity> weakReference;

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                switch (msg.what) {
                    case 0:
                        increase++;
                        if (specialProgressBarView != null && increase <= specialProgressBarView.getMax()) {
                            specialProgressBarView.setProgress(increase);
                            handler.sendEmptyMessageDelayed(0, 1);
                        }
                        break;
                }
            }
        }
    }

    private static final String TAG = "IsFirstActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 第一次启动为true
         */
        userAutoLoginHelper = UserAutoLoginHelper.getUserAutoLoginHelper(IsFirstActivity.this);
        if (TextUtils.isEmpty(userAutoLoginHelper.getUserName()) || TextUtils.isEmpty(userAutoLoginHelper.getUserPassword())) {
            /**
             * 首次使用引导页
             */
            Log.d(TAG, "onCreate: user:" + userAutoLoginHelper.getUserName() + "  password:"+userAutoLoginHelper.getUserPassword());
            startActivity(new Intent(IsFirstActivity.this, StartMovieActivity.class));
        } else {
            // 如果不是第一次启动app，则正常显示启动屏
            setContentView(R.layout.isfirst_layout);
            /**
             * 登陆等待动画
             */

            //启动动画
            specialProgressBarView = findViewById(R.id.bv_loadingAnimator);
            if (specialProgressBarView == null) {
                Toast.makeText(this, "NULL POINT", Toast.LENGTH_SHORT).show();
            } else {
                /**
                 * 设置当前主题的默认背景颜色和字体颜色
                 */
                TypedArray array = getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackground,
                        android.R.attr.textColorPrimary,
                });
                int backgroundColor = array.getColor(0, 0xFF00FF);
                int textColor = array.getColor(1, 0xFF00FF);
                array.recycle();


                specialProgressBarView
//                       .setEndSuccessBackgroundColor(Color.parseColor("#66A269"))//设置进度完成时背景颜色
                        .setEndSuccessBackgroundColor(Color.parseColor("#EC5745"))
                        .setEndSuccessDrawable(R.mipmap.log, null)//设置进度完成时背景图片
                        .setCanEndSuccessClickable(false)//设置进度完成后是否可以再次点击开始
                        .setProgressBarColor(getResources().getColor(R.color.transparent))//进度条颜色
                        .setCanDragChangeProgress(false)//是否进度条是否可以拖拽
                        .setCanReBack(true)//是否在进度成功后返回初始状态
                        .setProgressBarBgColor(Color.parseColor("#491C14"))//进度条背景颜色
                        .setProgressBarHeight(DensityUtil.dip2px(this, 4))//进度条宽度
                        .setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()))//设置字体大小
                        .setTextColorSuccess(Color.parseColor("#66A269"))//设置成功时字体颜色
                        .setTextColorNormal(Color.parseColor("#491C14"))//设置默认字体颜色
                        .setTextColorError(Color.parseColor("#BC5246"));//设置错误时字体颜色

                specialProgressBarView.beginStarting();

                specialProgressBarView.setOnAnimationEndListener(new SpecialProgressBarView.AnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        specialProgressBarView.setMax(100);
                        handler.sendEmptyMessage(0);
                        //   specialProgressBarView.setProgress(1);//在动画结束时设置进度

                    }
                });
                specialProgressBarView.setOntextChangeListener(new SpecialProgressBarView.OntextChangeListener() {
                    @Override
                    public String onProgressTextChange(SpecialProgressBarView specialProgressBarView, int max, int progress) {
                        return progress * 100 / max + "%";
                    }

                    @Override
                    public String onErrorTextChange(SpecialProgressBarView specialProgressBarView, int max, int progress) {
                        return "error";
                    }

                    @Override
                    public String onSuccessTextChange(SpecialProgressBarView specialProgressBarView, int max, int progress) {
                        return "done";
                    }
                });
                /**
                 * 自动登陆
                 */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(IsFirstActivity.this, LoginActivity.class));
                        JMessageClient.login(userAutoLoginHelper.getUserName(), userAutoLoginHelper.getUserPassword(), new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    initUserInfo();
                                    Intent intent = new Intent(getApplication(), IndexActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    //终止当前Activity
                                    IsFirstActivity.this.finish();
                                } else {
                                    /**
                                     * 登陆失败
                                     * 进入到登陆界面
                                     */
                                    startActivity(new Intent(IsFirstActivity.this, LoginActivity.class));

                                }
                            }
                        });
                    }
                }, TIME);

            }
        }
    }

    /**
     * 拉取用户信息
     */
    public void initUserInfo() {
        JMessageClient.getUserInfo(userAutoLoginHelper.getUserName(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0) {

                }
            }
        });
    }
}


