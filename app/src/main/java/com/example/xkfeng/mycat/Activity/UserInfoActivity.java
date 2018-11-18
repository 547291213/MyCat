package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.UserInfoScrollView;
import com.example.xkfeng.mycat.Fragment.MessageFragment;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StringUtil;
import com.example.xkfeng.mycat.Util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseActivity {

    private static final String TAG = "UserInfoActivity";
    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.bt_modifyUserinfoBtn)
    Button btModifyUserinfoBtn;

    @BindView(R.id.tv_personallyLaber)
    TextView tvPersonallyLaber;
    @BindView(R.id.tv_addPersonallyLaberView)
    TextView tvAddPersonallyLaberView;

    @BindView(R.id.tv_userInfoUserSex)
    TextView tvUserInfoUserSex;
    @BindView(R.id.tv_userInfoUserBirthday)
    TextView tvUserInfoUserBirthday;
    @BindView(R.id.tv_userInfoUserCity)
    TextView tvUserInfoUserCity;
    @BindView(R.id.tv_userInfoUserLastUpdate)
    TextView tvUserInfoUserLastUpdate;
    @BindView(R.id.iv_userinfoImage)
    ImageView ivUserinfoImage;


    @BindView(R.id.uisv_scrollView)
    UserInfoScrollView uisvScrollView;
    @BindView(R.id.tv_userNikeName)
    TextView tvUserNikeName;
    @BindView(R.id.tv_signatureTextView)
    TextView tvSignatureTextView;
    @BindView(R.id.tv_userInfoUserName)
    TextView tvUserInfoUserName;

    private int height;
    private Boolean flag = true;

    private ImageView userInfoBkImage;
    private Matrix matrix;
    private UserInfoScrollView userInfoScrollView;
    private LinearLayout ll_userinfoImgBgLayout;
    private static final int ACTIVITY_RESULT_CODE = 1;
    private UserInfo userInfo;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);
        ButterKnife.bind(this);

        circleImageView = findViewById(R.id.iv_userinfoHeaderImage);


        /**
         *  虽然在初始化的时候顶部标题并不显示
         *  但是仍然需要把padding的属性给设置好
         *  不然在初次滑动的时候设置为alignParentBottom的按钮会出现布局偏移
         *
         */
        setIndexTitleLayout();

        /**
         * 主显示布局的初始化
         */
        initView();


        /**
         * 设置用户数据
         */
        setUserInfo();

    }

    /**
     * 调用极光来获取当前用户的数据
     */
    private void setUserInfo() {

        userInfo = JMessageClient.getMyInfo();
        tvUserInfoUserSex.setText(StringUtil.isEmpty(userInfo.getGender().name()) == true ? "unkonwn" : userInfo.getGender().name());
        tvUserInfoUserBirthday.setText(StringUtil.isEmpty(TimeUtil.ms2date("yyyy-MM-dd", userInfo.getBirthday()).toString()) == true ?
                "unknown" : TimeUtil.ms2date("yyyy-MM-dd", userInfo.getBirthday()).toString());
        tvUserInfoUserCity.setText(StringUtil.isEmpty(userInfo.getAddress().toString()) == true ? "unknown" : userInfo.getAddress().toString());
        tvUserInfoUserLastUpdate.setText("上次活动：" + TimeUtil.unix2Date("yyyy-MM-dd HH:mm", userInfo.getmTime()));
        tvUserNikeName.setText(StringUtil.isEmpty(userInfo.getNickname().toString())==true ? "~快取个昵称吧！" : userInfo.getNickname().toString());
        tvUserInfoUserName.setText(userInfo.getUserName().toString());
        if (!StringUtil.isEmpty(userInfo.getSignature().toString())){
            tvSignatureTextView.setText(userInfo.getSignature().toString());
        }else {
            tvSignatureTextView.setText("还没有个性签名呢，快些一个吧");
        }
        //设置头像
        setUserHeadImage();
    }

    /**
     * 设置用户头像
     * 情况特殊，单独列出
     */
    private void setUserHeadImage(){
        /**
         * 更新用户资料信息
         */
        userInfo = JMessageClient.getMyInfo();
        if (!StringUtil.isEmpty(userInfo.getAvatarFile().toString())) {
            //  circleImageView.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatar()));
            Glide.with(UserInfoActivity.this)
                    .load(userInfo.getAvatarFile())
                    .into(circleImageView);

        } else {
            circleImageView.setImageResource(R.mipmap.log);
        }

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
        tvAddPersonallyLaberView.setTypeface(typeface);


        /**
         * 设置显示用户名
         */
        indexTitleLayout.setMiddleText(JMessageClient.getMyInfo().getUserName() + "的资料");


        userInfoBkImage = findViewById(R.id.iv_userinfoImage);
        userInfoScrollView = findViewById(R.id.uisv_scrollView);

        ll_userinfoImgBgLayout = findViewById(R.id.ll_userinfoImgBgLayout);
        /**
         * 初始化背景图片数据
         */
        matrix = new Matrix();

        ViewTreeObserver viewTreeObserver = userInfoBkImage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: befor: " + userInfoScrollView.getPaddingTop());
                indexTitleLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d(TAG, "onGlobalLayout: after : " + userInfoScrollView.getPaddingTop());
                height = userInfoBkImage.getHeight();
                uisvScrollView.setSmoothScrollingEnabled(true);
                userInfoScrollView.setScrollChangedListener(new UserInfoScrollView.ScrollChangedListener() {
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
//                            userInfoBkImage.startAnimation(animation);

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
                            matrix.setScale((float) (1.0 - y * 1.0 / 2000), (float) (1.0 - y * 1.0 / 800));
                            ll_userinfoImgBgLayout.setScaleX((float) (1.0 - y * 1.0 / 2000));
                            ll_userinfoImgBgLayout.setScaleY((float) (1.0 - y * 1.0 / 800)) ;
                            //userInfoBkImage.setImageMatrix(matrix);
                            ll_userinfoImgBgLayout.setScrollY((int) (-uisvScrollView.getScrollY() * ll_userinfoImgBgLayout.getScaleY()));
//                            ll_userinfoImgBgLayout.scrollTo();
                            Log.d(TAG, "onScrollChanged: scrollY :" + ll_userinfoImgBgLayout.getScrollY() );

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

    @OnClick({R.id.tv_userInfoUserBirthday, R.id.tv_userInfoUserCity, R.id.tv_userInfoUserLastUpdate,
            R.id.bt_modifyUserinfoBtn, R.id.tv_userInfoUserSex})
    public void onModifyClick(View view) {

        /**
         * 两个Activity进行交互，
         * 根据传递的值来判断是否需要更新用户数据
         */
        startActivityForResult(new Intent(UserInfoActivity.this, ModifyUserInfoActivity.class), ACTIVITY_RESULT_CODE);
//        startActivity(new Intent(UserInfoActivity.this, ModifyUserInfoActivity.class));

    }

    @OnClick({R.id.tv_personallyLaber, R.id.tv_addPersonallyLaberView})
    public void onLaberClick(View view) {
        ITosast.showShort(UserInfoActivity.this, "个性标签")
                .show();
    }


    /**
     * 当界面可见
     * 用于当用户在修改了用户资料返回来之后同步更新数据
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_RESULT_CODE:
                if (resultCode == RESULT_OK && "true".equals(data.getStringExtra("modify"))) {

                    /**
                     * 更新用户数据
                     */
                    setUserInfo();
                }
                if (resultCode == RESULT_OK && true == data.getBooleanExtra("headimagemodify" , false)){
                    /**
                     * 更新用户头像
                     */
                    setUserHeadImage();

                    Toast.makeText(this, "change image", Toast.LENGTH_SHORT).show();
                }

                break ;
        }
    }
}
