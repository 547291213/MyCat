package com.example.xkfeng.mycat.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.Model.FriendInvitationModel;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationDao;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationSql;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class SendFriendRequestActivity extends BaseActivity {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.et_validationEdit)
    EditText etValidationEdit;
    @BindView(R.id.bt_sendRequestFriednBtn)
    Button btSendRequestFriednBtn;

    private String targetId;
    private String tartgetAppkey;
    private boolean isFirend;
    private UserInfo mUserInfo;

    private FriendInvitationModel friendInvitationModel ;
    private FriendInvitationDao friendInvitationDao ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.send_friend_request_layout);
        ButterKnife.bind(this);

        friendInvitationDao = new FriendInvitationDao(SendFriendRequestActivity.this) ;
        setIndexTitleLayout();
        initData();
        initView();
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
        targetId = getIntent().getStringExtra(StaticValueHelper.TARGET_ID);
        tartgetAppkey = getIntent().getStringExtra(StaticValueHelper.TARGET_APP_KEY);
        isFirend = getIntent().getBooleanExtra(StaticValueHelper.IS_FRIEDN, false);

        mUserInfo = JMessageClient.getMyInfo();
    }

    private void initView() {
        etValidationEdit.setText("我是： " + (TextUtils.isEmpty(mUserInfo.getNickname()) ? mUserInfo.getUserName() : mUserInfo.getNickname()));

    }

    @OnClick({R.id.tv_setBackText, R.id.bt_sendRequestFriednBtn})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setBackText:
                finish();
                break;

            case R.id.bt_sendRequestFriednBtn:
                if (isFirend) {
                    ITosast.showShort(SendFriendRequestActivity.this, "无法重复添加好友！").show();
                    break;
                }
                if (mUserInfo.getUserName().equals(targetId)) {
                    ITosast.showShort(SendFriendRequestActivity.this, "不能添加自己！").show();
                    break;
                }


                ContactManager.sendInvitationRequest(targetId, tartgetAppkey, etValidationEdit.getText().toString(), new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        switch (i) {
                            case 0:
                                friendInvitationModel = new FriendInvitationModel();

                                friendInvitationModel.setState(FriendInvitationSql.SATTE_WAIT_PROCESSED);
                                friendInvitationModel.setmUserName(targetId);
                                friendInvitationModel.setmFromUser(mUserInfo.getUserName());
                                friendInvitationModel.setReason(etValidationEdit.getText().toString());
                                friendInvitationModel.setFromUserTime(System.currentTimeMillis());
                                friendInvitationDao.insertData(friendInvitationModel);
                                ITosast.showShort(SendFriendRequestActivity.this , "发送请求成功").show();
                                break;

                            default:
                                ITosast.showShort(SendFriendRequestActivity.this , "发送请求失败").show();
                                break;
                        }
                    }
                });

                break;
            default:
                ITosast.showShort(SendFriendRequestActivity.this, "未知参数").show();
                break;

        }
    }

}
