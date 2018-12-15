package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.DrawableRightEdit;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendActivity extends BaseActivity {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.dre_searchUserInputEdit)
    DrawableRightEdit dreSearchUserInputEdit;
    @BindView(R.id.bt_addFriendOkBtn)
    Button btAddFriendOkBtn;
    @BindView(R.id.pciv_messageHeaderImage)
    CircleImageView pcivMessageHeaderImage;
    @BindView(R.id.tv_meessageTitle)
    TextView tvMeessageTitle;
    @BindView(R.id.bt_addFriendBtn)
    Button btAddFriendBtn;
    @BindView(R.id.ll_addFriendInfoLayout)
    LinearLayout llAddFriendInfoLayout;
    @BindView(R.id.tv_intoAboutUs)
    TextView tvIntoAboutUs;
    @BindView(R.id.iv_intoAboutUs)
    ImageView ivIntoAboutUs;

    private Drawable rightDrawable;
    private Drawable leftrawable;
    private UserInfo mTargetUserInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_friend_layout);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        setIndexTitleLayout();

        setEditDrawable();
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

    private void setEditDrawable() {
        leftrawable = dreSearchUserInputEdit.getCompoundDrawables()[0];
        rightDrawable = dreSearchUserInputEdit.getCompoundDrawables()[2];
        if (rightDrawable == null || leftrawable == null) {
            return;
        }
        dreSearchUserInputEdit.setRightDrawableClickListener(new DrawableRightEdit.RightDrawableClickListener() {
            @Override
            public void onRightDrawableClick(Drawable drawable) {
                dreSearchUserInputEdit.setText("");
            }
        });

        dreSearchUserInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    dreSearchUserInputEdit.setCompoundDrawables(leftrawable, null, rightDrawable, null);
                } else {
                    dreSearchUserInputEdit.setCompoundDrawables(leftrawable, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @OnClick({R.id.bt_addFriendOkBtn, R.id.tv_meessageTitle, R.id.bt_addFriendBtn,
            R.id.tv_setBackText, R.id.tv_intoAboutUs, R.id.iv_intoAboutUs})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.bt_addFriendOkBtn:
                if (TextUtils.isEmpty(dreSearchUserInputEdit.getText().toString())) {
                    ITosast.showShort(AddFriendActivity.this, "输入的数据为空")
                            .show();
                    return;
                }
                JMessageClient.getUserInfo(dreSearchUserInputEdit.getText().toString(), new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        switch (i) {
                            case 0:

                                mTargetUserInfo = userInfo;
                                setTargetUserData();
                                break;

                            default:
                                llAddFriendInfoLayout.setVisibility(View.GONE);
                                break;
                        }
                    }
                });
                break;

            case R.id.tv_meessageTitle:

                /**
                 *
                 */
                Intent intent = new Intent();
                intent.putExtra(StaticValueHelper.TARGET_ID, mTargetUserInfo.getUserName());
                intent.putExtra(StaticValueHelper.TARGET_APP_KEY, mTargetUserInfo.getAppKey());
                intent.putExtra(StaticValueHelper.IS_FRIEDN, mTargetUserInfo.isFriend());
                intent.setClass(AddFriendActivity.this, FriendInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.bt_addFriendBtn:
                Intent intent1 = new Intent();
                intent1.putExtra(StaticValueHelper.TARGET_ID, mTargetUserInfo.getUserName());
                intent1.putExtra(StaticValueHelper.TARGET_APP_KEY, mTargetUserInfo.getAppKey());
                intent1.putExtra(StaticValueHelper.IS_FRIEDN, mTargetUserInfo.isFriend());
                intent1.setClass(AddFriendActivity.this, SendFriendRequestActivity.class);
                startActivity(intent1);

                break;

            case R.id.tv_setBackText :
                finish();
                break ;

            case R.id.tv_intoAboutUs :
            case R.id.iv_intoAboutUs :

                Intent intent2 = new Intent() ;
                intent2.setClass(AddFriendActivity.this , AboutActivity.class) ;
                startActivity(intent2);
                break ;

        }


    }

    private void setTargetUserData() {
        if (mTargetUserInfo == null) {
            return;
        }

        llAddFriendInfoLayout.setVisibility(View.VISIBLE);
        if (mTargetUserInfo.getAvatarFile() != null) {
            pcivMessageHeaderImage.setImageBitmap(BitmapFactory.decodeFile(mTargetUserInfo.getAvatarFile().toString()));
        } else {
            pcivMessageHeaderImage.setImageResource(R.mipmap.log);
        }

        if (!TextUtils.isEmpty(mTargetUserInfo.getNotename())) {
            tvMeessageTitle.setText(mTargetUserInfo.getNotename());
        } else {
            tvMeessageTitle.setText(mTargetUserInfo.getUserName());
        }

        if (mTargetUserInfo.isFriend()) {
            btAddFriendBtn.setVisibility(View.GONE);
        }
    }


}
