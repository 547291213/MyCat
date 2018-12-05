package com.example.xkfeng.mycat.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.UserInfoScrollView;
import com.example.xkfeng.mycat.Fragment.MessageFragment;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.StaticValueHelper;
import com.example.xkfeng.mycat.Util.StringUtil;
import com.example.xkfeng.mycat.Util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendInfoActivity extends BaseActivity {

    private static final String TAG = "FriendInfoActivity";
    @BindView(R.id.iv_userinfoImage)
    ImageView ivUserinfoImage;
    @BindView(R.id.ll_userinfoImgBgLayout)
    LinearLayout llUserinfoImgBgLayout;
    @BindView(R.id.iv_userinfoHeaderImage)
    CircleImageView ivUserinfoHeaderImage;
    @BindView(R.id.tv_userNikeName)
    TextView tvUserNikeName;
    @BindView(R.id.tv_signatureTextView)
    TextView tvSignatureTextView;
    @BindView(R.id.tv_userInfoUserName)
    TextView tvUserInfoUserName;
    @BindView(R.id.tv_userInfoUserSex)
    TextView tvUserInfoUserSex;
    @BindView(R.id.tv_userInfoUserBirthday)
    TextView tvUserInfoUserBirthday;
    @BindView(R.id.tv_userInfoUserCity)
    TextView tvUserInfoUserCity;
    @BindView(R.id.tv_userInfoUserLastUpdate)
    TextView tvUserInfoUserLastUpdate;
    @BindView(R.id.rl_imageBkLayout)
    RelativeLayout rlImageBkLayout;
    @BindView(R.id.rl_userInfoMainLayout)
    RelativeLayout rlUserInfoMainLayout;
    @BindView(R.id.tv_personallyLaber)
    TextView tvPersonallyLaber;
    @BindView(R.id.tv_lookPersonallyLaberView)
    TextView tvLookPersonallyLaberView;
    @BindView(R.id.uisv_scrollView)
    UserInfoScrollView uisvScrollView;
    @BindView(R.id.bt_sendMsgBtn)
    Button btSendMsgBtn;
    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;

    private UserInfo mUserInfo;
    private String mTargetUser;
    private String mTargetAppKey;
    private long mGroupId;
    private Dialog mLoadingDialog ;
    private boolean flag  = true ;
    private int height ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friendinfo_layout);
        ButterKnife.bind(this);
        mTargetUser = getIntent().getStringExtra(StaticValueHelper.TARGET_ID);
        mTargetAppKey = getIntent().getStringExtra(StaticValueHelper.TARGET_APP_KEY);
        mGroupId = getIntent().getLongExtra(StaticValueHelper.GROUP_ID, -1);

        initView();
        setIndexTitleLayout();
        initUserInfo();
    }

    /**
     * 初始化View
     */
    private void initView() {

        /**
         * 艺术字
         */
        String fonts = "fonts/font_1.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), fonts);
        tvPersonallyLaber.setTypeface(typeface);
        tvLookPersonallyLaberView.setTypeface(typeface);


        /**
         * 设置显示用户名
         */
        indexTitleLayout.setMiddleText(JMessageClient.getMyInfo().getUserName() + "的资料");

        ViewTreeObserver viewTreeObserver = ivUserinfoImage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: befor: " + uisvScrollView.getPaddingTop());
                indexTitleLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d(TAG, "onGlobalLayout: after : " + uisvScrollView.getPaddingTop());
                height = ivUserinfoImage.getHeight();
                uisvScrollView.setSmoothScrollingEnabled(true);
                uisvScrollView.setScrollChangedListener(new UserInfoScrollView.ScrollChangedListener() {
                    @Override
                    public void onScrollChanged(int l, int y, int oldl, int oldt) {

                        /**
                         * 这里设置标题只在出现滑动的时候显示
                         */
                        if (flag) {
                            flag = false;
                            //设置标题的背景颜色
                            indexTitleLayout.setBackgroundColor(Color.argb((int) 0, 144, 151, 166));
                            indexTitleLayout.setLeftBtnDrawable(IndexTitleLayout.NULL_DRAWABLE);
                            indexTitleLayout.setMiddleTextColor(Color.argb((int) 0, 144, 151, 166));
                            //隐藏返回按钮
                            indexTitleLayout.setLeftBtnVisiavle(View.GONE);
                            indexTitleLayout.setVisibility(View.VISIBLE);

//                            Animation animation = AnimationUtils.loadAnimation(UserInfoActivity.this , R.anim.userinof_bkimg_scale) ;
//                            ivUserinfoImage.startAnimation(animation);

                            Log.d(TAG, "onScrollChanged: startAnimator");
                        }

                        if (y <= 0) {
                            //设置标题的背景颜色
                            indexTitleLayout.setBackgroundColor(Color.argb((int) 0, 144, 151, 166));
                            indexTitleLayout.setLeftBtnDrawable(IndexTitleLayout.NULL_DRAWABLE);

                            //隐藏返回按钮
                            indexTitleLayout.setLeftBtnVisiavle(View.GONE);

                            /**
                             * 起点处下拉
                             * 实现布局缩放，图片会跟随布局缩放
                             */
                             llUserinfoImgBgLayout.setScaleX((float) (1.0 - y * 1.0 / 2000));
                            llUserinfoImgBgLayout.setScaleY((float) (1.0 - y * 1.0 / 800));
                            //ivUserinfoImage.setImageMatrix(matrix);
                            llUserinfoImgBgLayout.setScrollY((int) (-uisvScrollView.getScrollY() * llUserinfoImgBgLayout.getScaleY()));
//                            llUserinfoImgBgLayout.scrollTo();

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


                            //显示返回按钮
                            indexTitleLayout.setLeftBtnVisiavle(View.VISIBLE);


                            /**
                             * 设置标题栏属性
                             */
                            setIndexTitleLayout();
                        } else {
                            //滑动到ImageView下面设置普通颜色
                            indexTitleLayout.setBackgroundColor(Color.argb((int) 255, 144, 151, 166));

                            indexTitleLayout.setLeftBtnDrawable(R.drawable.back_blue);

                            //显示返回按钮
                            indexTitleLayout.setLeftBtnVisiavle(View.VISIBLE);
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


        indexTitleLayout.setPadding(MessageFragment.STATUSBAR_PADDING_lEFT,
                MessageFragment.STATUSBAR_PADDING_TOP,
                MessageFragment.STATUSBAR_PADDING_RIGHT,
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

    private void initUserInfo() {
        mLoadingDialog = DialogHelper.createLoadingDialog(this , "正在加载") ;
        mLoadingDialog.show();
        JMessageClient.getUserInfo(mTargetUser, mTargetAppKey, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                mLoadingDialog.dismiss();
                switch (i) {
                    case 0:
                        mUserInfo = userInfo ;
                        if (userInfo != null){
                            tvUserInfoUserSex.setText(StringUtil.isEmpty(userInfo.getGender().name()) == true ? "unkonwn" : userInfo.getGender().name());
                            tvUserInfoUserBirthday.setText(StringUtil.isEmpty(TimeUtil.ms2date("yyyy-MM-dd", userInfo.getBirthday()).toString()) == true ?
                                    "unknown" : TimeUtil.ms2date("yyyy-MM-dd", userInfo.getBirthday()).toString());
                            tvUserInfoUserCity.setText(StringUtil.isEmpty(userInfo.getAddress().toString()) == true ? "unknown" : userInfo.getAddress().toString());
                            tvUserInfoUserLastUpdate.setText("上次活动：" + TimeUtil.unix2Date("yyyy-MM-dd HH:mm", userInfo.getmTime()));
                            tvUserNikeName.setText(StringUtil.isEmpty(userInfo.getNickname().toString()) == true ? "~快取个昵称吧！" : userInfo.getNickname().toString());
                            tvUserInfoUserName.setText(userInfo.getUserName().toString());
                            if (!StringUtil.isEmpty(userInfo.getSignature().toString())) {
                                tvSignatureTextView.setText(userInfo.getSignature().toString());
                            } else {
                                tvSignatureTextView.setText("还没有个性签名呢，快些一个吧");
                            }
                            if (userInfo.getAvatar()!=null&&!StringUtil.isEmpty(userInfo.getAvatarFile().toString())) {
                                //  circleImageView.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatar()));
                                Glide.with(FriendInfoActivity.this)
                                        .load(userInfo.getAvatarFile())
                                        .into(ivUserinfoHeaderImage);
                            } else {
                                ivUserinfoHeaderImage.setImageResource(R.mipmap.log);
                            }
                        }

                        break;

                    default:
                        HandleResponseCode.onHandle(FriendInfoActivity.this , i);
                        break;
                }
            }
        });
    }
}
