package com.example.xkfeng.mycat.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.BottomDialog;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;
import com.suke.widget.SwitchButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendSettingActivity extends BaseActivity {

    private static final String TAG = "FriendSettingActivity";

    @BindView(R.id.tv_nodeName)
    TextView tvNodeName;
    @BindView(R.id.rl_noteNameLayout)
    RelativeLayout rlNoteNameLayout;
    @BindView(R.id.rl_sendBusinessLayout)
    RelativeLayout rlSendBusinessLayout;
    @BindView(R.id.sb_vibrationBtn)
    SwitchButton sbVibrationBtn;
    @BindView(R.id.rl_addBlackListLayout)
    LinearLayout rlAddBlackListLayout;
    @BindView(R.id.bt_deleteFriendBtn)
    Button btDeleteFriendBtn;
    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.tv_intoAboutUs)
    TextView tvIntoAboutUs;
    @BindView(R.id.iv_intoAboutUs)
    CircleImageView ivIntoAboutUs;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;

    private String userName;
    private String noteName;
    private Dialog loadingDialog;
    private CloseDialogHandler handler;
    private static final int CLOSE_LOADING_DIALOG = 0x123;
    private UserInfo mFriendInfo;
    private Conversation conversation ;

    private static final int ACTIVITY_REQUEST_SET_NOTENAME = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_setting_layout);
        ButterKnife.bind(this);

        userName = getIntent().getStringExtra(StaticValueHelper.USER_NAME);

        initView();
    }

    private void initView() {

        handler = new CloseDialogHandler(FriendSettingActivity.this);
        setIndexTitleLayout();
        initData();
        setSwitchBtnClickEvent();


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

    private void initData() {

        loadingDialog = DialogHelper.createLoadingDialog(FriendSettingActivity.this, "正在加载");
        loadingDialog.show();

        if (TextUtils.isEmpty(userName)) {
            ITosast.showShort(FriendSettingActivity.this, "获取用户名失败").show();
            return;
        } else {
            JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    switch (i) {
                        case 0:
                            mFriendInfo = userInfo;
                            sbVibrationBtn.setChecked(mFriendInfo.getBlacklist() == 1);
                            break;

                        default:
                            HandleResponseCode.onHandle(FriendSettingActivity.this, i);
                            break;

                    }

                    handler.sendEmptyMessageAtTime(CLOSE_LOADING_DIALOG, 1000);
                }
            });
        }
        noteName = getIntent().getStringExtra(StaticValueHelper.NOTENAME);
        if (TextUtils.isEmpty(noteName)) {

            tvNodeName.setText(userName);
        } else {
            tvNodeName.setText(noteName);
        }
    }


    private void setSwitchBtnClickEvent() {


        sbVibrationBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                if (mFriendInfo == null) {
                    ITosast.showShort(FriendSettingActivity.this, "用户数据为空，操作失败").show();
                    sbVibrationBtn.setChecked(false);
                    return;
                }
                else if (!mFriendInfo.isFriend()){
                    ITosast.showShort(FriendSettingActivity.this, "当前用户还不是您的好友，操作失败").show();
                    sbVibrationBtn.setChecked(false);
                    return;
                }
                if (isChecked) {
                    //将好友划分到黑名单列表

                    JMessageClient.addUsersToBlacklist(Collections.singletonList(userName), new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            switch (i) {
                                case 0:
                                    ITosast.showShort(FriendSettingActivity.this, "加入黑名单成功").show();
                                    break;

                                default:
                                    ITosast.showShort(FriendSettingActivity.this, "加入黑名单失败").show();
                                    break;
                            }
                        }
                    });
                } else {
                    //将好友从黑名单列表移除
                    JMessageClient.delUsersFromBlacklist(Collections.singletonList(userName), new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            switch (i) {
                                case 0:
                                    ITosast.showShort(FriendSettingActivity.this, "移除成功").show();
                                    break;

                                default:
                                    ITosast.showShort(FriendSettingActivity.this, "移除失败").show();
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    private void delConvAndReturnMainActivity(){
        JMessageClient.deleteSingleConversation(userName);
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
        finish();

    }

    class CloseDialogHandler extends Handler {
        private WeakReference<FriendSettingActivity> mActivity;

        public CloseDialogHandler(FriendSettingActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity == null) {
                return;
            }
            FriendSettingActivity activity = mActivity.get();
            switch (msg.what) {
                case CLOSE_LOADING_DIALOG:
                    if (activity.loadingDialog != null) {
                        activity.loadingDialog.dismiss();
                    }

                    break;
                default:
                    Toast.makeText(activity, "未知消息类型", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick({R.id.rl_sendBusinessLayout,
            R.id.rl_noteNameLayout, R.id.bt_deleteFriendBtn})
    public void inItemClick(View view) {
        switch (view.getId()) {
            case R.id.bt_deleteFriendBtn:
                if (mFriendInfo != null && mFriendInfo.isFriend()) {
                    final BottomDialog bottomDialog = new BottomDialog(FriendSettingActivity.this, getResources().getString(R.string.chat_friend_delete_tip),
                            getResources().getString(R.string.chat_friend_delete_delte), getResources().getString(R.string.chat_friend_delete_cancle));
                    bottomDialog.setBackground(Color.WHITE);
                    bottomDialog.setItem1TextColor(1, Color.GRAY);
                    bottomDialog.setItem1TextColor(2, Color.RED);
                    bottomDialog.setItem1TextColor(3, Color.BLACK);
                    bottomDialog.setItem1TextSize(1, 12);
                    bottomDialog.show();
                    bottomDialog.setCancelable(true);
                    bottomDialog.setCanceledOnTouchOutside(true);
                    bottomDialog.setItemClickListener(new BottomDialog.ItemClickListener() {
                        @Override
                        public void onItem1Click(View view) {
                            bottomDialog.dismiss();
                        }

                        @Override
                        public void onItem2Click(View view) {

                            if (mFriendInfo == null || !mFriendInfo.isFriend()) {
                                ITosast.showShort(FriendSettingActivity.this, "删除失败").show();
                                return;
                            }

                            mFriendInfo.removeFromFriendList(new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    switch (i) {
                                        case 0:
                                            //将好友删除时候还原黑名单设置
                                            List<String> name = new ArrayList<>();
                                            name.add(mFriendInfo.getUserName());
                                            JMessageClient.delUsersFromBlacklist(name, null);
                                            delConvAndReturnMainActivity();
                                            break;

                                        default:
                                            ITosast.showShort(FriendSettingActivity.this , "删除失败").show();
                                            break;
                                    }
                                }
                            });
                        }

                        @Override
                        public void onItem3Click(View view) {
                            bottomDialog.dismiss();
                        }
                    });
                } else {
                    ITosast.showShort(FriendSettingActivity.this, "当前用户还不是您的好友").show();
                }
                break;

            case R.id.rl_sendBusinessLayout:

                Intent forwardIntent = new Intent() ;
                forwardIntent.setClass(FriendSettingActivity.this , ForwardingActivity.class) ;
                forwardIntent.putExtra( "businessCard",true) ;
                forwardIntent.putExtra("userName" , userName) ;
                startActivity(forwardIntent);
                break;

            case R.id.rl_noteNameLayout:

                if (TextUtils.isEmpty(userName)) {
                    return;
                }

                if (mFriendInfo == null) {
                    ITosast.showShort(FriendSettingActivity.this, "用户数据为空，操作失败").show();
                    return;
                }
                else if (!mFriendInfo.isFriend()){
                    ITosast.showShort(FriendSettingActivity.this, "当前用户还不是您的好友，操作失败").show();
                    return;
                }
                Intent intent = new Intent(FriendSettingActivity.this, SetNoteNameActivity.class);
                String data = TextUtils.isEmpty(noteName) ? userName : noteName;
                intent.putExtra(StaticValueHelper.NOTENAME, data);
                intent.putExtra(StaticValueHelper.USER_NAME, userName);
                startActivityForResult(intent, ACTIVITY_REQUEST_SET_NOTENAME);

                break;

            case R.id.tv_setBackText:

                finish();
                break;

            case R.id.tv_intoAboutUs:
            case R.id.iv_intoAboutUs:

                Intent intent1 = new Intent();
                intent1.setClass(FriendSettingActivity.this, AboutActivity.class);
                startActivity(intent1);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_REQUEST_SET_NOTENAME:
                if (resultCode == RESULT_OK && data != null && !TextUtils.isEmpty(data.getStringExtra(StaticValueHelper.NOTENAME))) {
                    tvNodeName.setText(data.getStringExtra(StaticValueHelper.NOTENAME));
                }
                break;

            default:
                Toast.makeText(this, "未知请求类型", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
