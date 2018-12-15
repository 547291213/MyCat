package com.example.xkfeng.mycat.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.UserInfoScrollView;
import com.example.xkfeng.mycat.Fragment.MessageFragment;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;
import com.example.xkfeng.mycat.Util.StringUtil;
import com.example.xkfeng.mycat.Util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.tv_targetUserNameText)
    TextView tvTargetUserNameText;
    @BindView(R.id.iv_friendSetImg)
    ImageView ivFriendSetImg;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;

    private UserInfo mUserInfo;
    private String mTargetUser;
    private String mTargetAppKey;
    private boolean isFriend = false;
    private long mGroupId;
    private Dialog mLoadingDialog;
    private int height;
    private boolean isFirst = true ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friendinfo_layout);
        ButterKnife.bind(this);
        mTargetUser = getIntent().getStringExtra(StaticValueHelper.TARGET_ID);
        mTargetAppKey = getIntent().getStringExtra(StaticValueHelper.TARGET_APP_KEY);
        mGroupId = getIntent().getLongExtra(StaticValueHelper.GROUP_ID, -1);
        isFriend = getIntent().getBooleanExtra(StaticValueHelper.IS_FRIEDN, false);

        if (!isFriend) {
            btSendMsgBtn.setText("添加好友");
        }
        initView();
        setIndexTitleLayout();
        initUserInfo();
    }

    private void setArtWordForPersonallyLaber(){
        /**
         * 艺术字
         */
        String fonts = "fonts/zhangcao.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), fonts);
        tvPersonallyLaber.setTypeface(typeface);
        tvLookPersonallyLaberView.setTypeface(typeface);
    }
    /**
     * 初始化View
     */
    private void initView() {
        ViewTreeObserver viewTreeObserver = ivUserinfoImage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llTitleLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                height = ivUserinfoImage.getHeight();
                uisvScrollView.setSmoothScrollingEnabled(true);
                uisvScrollView.setScrollChangedListener(new UserInfoScrollView.ScrollChangedListener() {
                    @Override
                    public void onScrollChanged(int l, int y, int oldl, int oldt) {
                        if (y <= 0) {
                            scrollInitialState(y);
                        } else if (y > 0 && y <= height) {
                            scrollSlidingProcess(y);
                        } else {
                            scrollSlidingBottom(y);
                        }

                    }
                });

            }
        });
    }

    /**
     * ScrollView 滑动的初值状态
     * @param y
     */
    private void scrollInitialState(int y ){
        //设置标题的背景颜色
        llTitleLayout.setBackgroundColor(Color.TRANSPARENT);
        ivFriendSetImg.setImageResource(R.drawable.ic_ellipsis_blue_32);
        /**
         * 起点处下拉
         * 实现布局缩放，图片会跟随布局缩放
         */
        llUserinfoImgBgLayout.setScaleX((float) (1.0 - y * 1.0 / 2000));
        llUserinfoImgBgLayout.setScaleY((float) (1.0 - y * 1.0 / 800));
        llUserinfoImgBgLayout.setScrollY((int) (-uisvScrollView.getScrollY() * llUserinfoImgBgLayout.getScaleY()));

        /**
         * 隐藏返回按钮
         */
        tvSetBackText.setVisibility(View.GONE);

        /**
         * 设置标题栏属性
         */
        setIndexTitleLayout();
    }

    /**
     * ScrolView滑动的中间过程状态处理
     * @param y
     */
    private void scrollSlidingProcess(int y){
        //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
        float scale = (float) y / height;
        float alpha = (255 * scale);
        tvTargetUserNameText.setTextColor(Color.argb((int) alpha, 255, 255, 255));
        llTitleLayout.setBackgroundColor(Color.argb((int) alpha, 144, 151, 166));
        ivFriendSetImg.setImageResource(R.drawable.ic_ellipsis_white_32);
        //显示返回按钮
        tvSetBackText.setVisibility(View.VISIBLE);
        tvSetBackText.setVisibility(View.VISIBLE);
        //设置标题栏属性
        setIndexTitleLayout();
    }

    /**
     * ScrollView滑动到底部处理
     * @param y
     */
    private void scrollSlidingBottom(int y){
        //滑动到ImageView下面设置普通颜色
        llTitleLayout.setBackgroundColor(Color.argb((int) 255, 144, 151, 166));
        ivFriendSetImg.setImageResource(R.drawable.ic_ellipsis_blue_32);
        //显示返回按钮
        tvSetBackText.setVisibility(View.VISIBLE);
        //设置标题属性
        setIndexTitleLayout();
    }


    private void setIndexTitleLayout() {
        //沉浸式状态栏
        DensityUtil.fullScreen(this);

//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        int titlePaddingTop = 0 ;
        if (isFirst){
            isFirst = false ;
            titlePaddingTop = llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this);

        }else {
            titlePaddingTop = llTitleLayout.getPaddingTop() ;
        }
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), titlePaddingTop,
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());


    }

    private void initUserInfo() {
        mLoadingDialog = DialogHelper.createLoadingDialog(this, "正在加载");
        mLoadingDialog.show();
        JMessageClient.getUserInfo(mTargetUser, mTargetAppKey, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {

                switch (i) {
                    case 0:
                        mUserInfo = userInfo;
                        if (userInfo != null) {
                            setUserData(userInfo);
                        }
                        break;

                    default:
                        HandleResponseCode.onHandle(FriendInfoActivity.this, i);
                        break;
                }
                mLoadingDialog.dismiss();
            }
        });
    }

    /**
     * 设置用户相关数据
     * @param userInfo
     */
    private void setUserData(UserInfo userInfo){
        /**
         * 设置显示用户名
         */
        String titleName = TextUtils.isEmpty(userInfo.getNickname()) ? userInfo.getUserName() : userInfo.getNickname();
        tvTargetUserNameText.setText(titleName);
        tvUserInfoUserSex.setText(StringUtil.isEmpty(userInfo.getGender().name()) == true ? "unkonwn" : userInfo.getGender().name());
        tvUserInfoUserBirthday.setText(StringUtil.isEmpty(TimeUtil.ms2date("yyyy-MM-dd", userInfo.getBirthday()).toString()) == true ?
                "unknown" : TimeUtil.ms2date("yyyy-MM-dd", userInfo.getBirthday()).toString());
        tvUserInfoUserCity.setText(StringUtil.isEmpty(userInfo.getAddress().toString()) == true ? "unknown" : userInfo.getAddress().toString());
        tvUserInfoUserLastUpdate.setText("上次活动：" + TimeUtil.unix2Date("yyyy-MM-dd HH:mm", userInfo.getmTime()));
        tvUserNikeName.setText(StringUtil.isEmpty(userInfo.getNickname().toString()) == true ? "~还没有昵称呢！" : userInfo.getNickname().toString());
        tvUserInfoUserName.setText(userInfo.getUserName().toString());
        if (!StringUtil.isEmpty(userInfo.getSignature().toString())) {
            tvSignatureTextView.setText(userInfo.getSignature().toString());
        } else {
            tvSignatureTextView.setText("不是一般的懒，连个性签名都没有！");
        }
        if (userInfo.getAvatar() != null && !StringUtil.isEmpty(userInfo.getAvatarFile().toString())) {
            Bitmap bitmap = BitmapFactory.decodeFile(userInfo.getAvatarFile().toString());
            ivUserinfoHeaderImage.setImageBitmap(bitmap);
        } else {
            ivUserinfoHeaderImage.setImageResource(R.mipmap.log);
        }
    }

    @OnClick({R.id.tv_setBackText, R.id.iv_friendSetImg ,R.id.bt_sendMsgBtn})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setBackText:

                finish();
                break;

            case R.id.iv_friendSetImg:
                Intent intent = new Intent();
                if (mUserInfo != null) {
                    intent.putExtra(StaticValueHelper.USER_NAME, mUserInfo.getUserName());
                    intent.putExtra(StaticValueHelper.NOTENAME, mUserInfo.getNotename());
                    intent.setClass(FriendInfoActivity.this, FriendSettingActivity.class);
                    startActivity(intent);
                } else {
                    ITosast.showShort(FriendInfoActivity.this, "尚未获得用户信息")
                            .show();
                    return;
                }
                break;

            case R.id.bt_sendMsgBtn :

                /**
                 * 判断是不是好友，
                 * 是好友走发送消息的步骤
                 * 不是好友走添加好友的步骤
                 */
                if (isFriend) {
                    sendMsgToFriend();
                } else {
                    addFriend() ;
                }
                break ;



            default:

                ITosast.showShort(FriendInfoActivity.this,  "未知数据类型").show();
                break;
        }
    }


    private void sendMsgToFriend(){
        //走发送消息的流程
        String titleName = null;
        if ((!TextUtils.isEmpty(mUserInfo.getNotename()))) {
            titleName = mUserInfo.getNotename();
        }
        //其次选择备注名称
        else if (!TextUtils.isEmpty(mUserInfo.getNickname())) {
            titleName = mUserInfo.getNickname();
        }
        /**
         * 最后选择用户名
         */
        else {
            titleName = mUserInfo.getUserName();
        }

        Intent intent = new Intent();
        intent.putExtra(StaticValueHelper.USER_NAME, mUserInfo.getUserName());
        intent.putExtra(StaticValueHelper.TARGET_ID, mUserInfo.getUserName());
        intent.putExtra(StaticValueHelper.CHAT_MSG_TITLE, titleName);
        intent.setClass(FriendInfoActivity.this, ChatMsgActivity.class);
        startActivity(intent);
    }

    private void addFriend(){
        //添加好友的流程
        Intent intent1 = new Intent();
        intent1.putExtra(StaticValueHelper.TARGET_ID, mUserInfo.getUserName());
        intent1.putExtra(StaticValueHelper.TARGET_APP_KEY, mUserInfo.getAppKey());
        intent1.putExtra(StaticValueHelper.IS_FRIEDN, mUserInfo.isFriend());
        intent1.setClass(FriendInfoActivity.this, SendFriendRequestActivity.class);
        startActivity(intent1);
    }
}
