package com.example.xkfeng.mycat.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Model.ForwardingFriendInfo;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.EmptyRecyclerView;
import com.example.xkfeng.mycat.RecyclerDefine.QucikAdapterWrapter;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;

public class ForwardingActivity extends BaseActivity {


    private static final String TAG = "ForwardingActivity";
    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.et_searchEdit)
    TextView etSearchEdit;

    @BindView(R.id.ll_groupLayout)
    LinearLayout llGroupLayout;
    @BindView(R.id.erv_recentMsgList)
    EmptyRecyclerView ervRecentMsgList;
    @BindView(R.id.tv_messageEmptyView)
    TextView tvMessageEmptyView;

    private DisplayMetrics metrics;
    private boolean isFriendSendBusinessCard;
    private String fromBusinessName;

    private QuickAdapter<ForwardingFriendInfo> quickAdapter;
    private QucikAdapterWrapter<ForwardingFriendInfo> qucikAdapterWrapter;
    private List<ForwardingFriendInfo> forwardingFriendInfoList;
    private List<Conversation> conversationList;
    private static final int RequestCode_intoSearchContact  = 100 ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forwarding_layout);
        ButterKnife.bind(this);

        isFriendSendBusinessCard = getIntent().getBooleanExtra("businessCard", false);
        fromBusinessName = getIntent().getStringExtra("userName");

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        initView();

    }

    private void initView() {
        initIndexTitleLayout();
        initSearchEdit();
        initData();
        initQuickAdapter();
        initWrapterAndRecycler();

    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void initIndexTitleLayout() {
        //沉浸式状态栏
        DensityUtil.fullScreen(this);
//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
    }

    private void initSearchEdit() {
        Drawable left = getResources().getDrawable(R.drawable.searcher);
        left.setBounds(metrics.widthPixels / 2 - DensityUtil.dip2px(this, 10 + 14 * 2) - 5, 0,
                50 + metrics.widthPixels / 2 - DensityUtil.dip2px(this, 10 + 14 * 2) - 5, 30);
//        Log.d(TAG, "setEtSearchEdit: " + metrics.widthPixels);
        etSearchEdit.setCompoundDrawablePadding(-left.getIntrinsicWidth() / 2 + 5);
        etSearchEdit.setCompoundDrawables(left, null, null, null);
        etSearchEdit.setAlpha((float) 0.6);
    }


    private void initData() {

        forwardingFriendInfoList = new ArrayList<>();
        conversationList = new ArrayList<>();
        conversationList = JMessageClient.getConversationList();
        for (Conversation conversation : conversationList) {
            UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
            ForwardingFriendInfo friendInfo = new ForwardingFriendInfo();
            if (conversation.getType() == ConversationType.group) {
                friendInfo.setUserName(conversation.getTitle());
            } else {
                if (!TextUtils.isEmpty(userInfo.getNotename())) {
                    friendInfo.setUserName(userInfo.getNotename());
                } else if (!TextUtils.isEmpty(userInfo.getNickname())) {
                    friendInfo.setUserName(userInfo.getNickname());
                } else {
                    friendInfo.setUserName(userInfo.getUserName());
                }

            }
            if (userInfo.getAvatarFile() != null && userInfo != null) {
                friendInfo.setHeaderBitmap(BitmapFactory.decodeFile(userInfo.getAvatarFile().getAbsolutePath()));
            } else {
                friendInfo.setHeaderBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.log));
            }
            forwardingFriendInfoList.add(friendInfo);
        }

    }

    private void initQuickAdapter() {
        quickAdapter = new QuickAdapter<ForwardingFriendInfo>(forwardingFriendInfoList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_contract;
            }

            @Override
            public void convert(VH vh, final ForwardingFriendInfo data, final int position) {
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (isFriendSendBusinessCard) {

                            Dialog dialog = DialogHelper.createSendFriendBusinessCardDialog(ForwardingActivity.this, ForwardingActivity.this, data.getUserName(), fromBusinessName,
                                    conversationList.get(position));
                            dialog.show();

                        } else {
                            Dialog dialog = DialogHelper.createForwardingDialog(ForwardingActivity.this, ForwardingActivity.this,
                                    data.getUserName(), (UserInfo) conversationList.get(position).getTargetInfo()
                                    , true);
                            dialog.show();
                        }


                    }
                };
                ((ImageView) vh.getView(R.id.iv_headIcon)).setImageBitmap(data.getHeaderBitmap());
                ((TextView) vh.getView(R.id.tv_name)).setText(data.getUserName());
                vh.getView(R.id.ll_contractLayout).setOnClickListener(listener);

            }

        };
    }

    private void initWrapterAndRecycler() {
        qucikAdapterWrapter = new QucikAdapterWrapter<>(quickAdapter);
        View addView = LayoutInflater.from(this).inflate(R.layout.ad_item_layout, null);
        qucikAdapterWrapter.setAdView(addView);

        ervRecentMsgList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ervRecentMsgList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ervRecentMsgList.setItemAnimator(new DefaultItemAnimator());
        ervRecentMsgList.setmEmptyView(tvMessageEmptyView);
        ervRecentMsgList.setAdapter(qucikAdapterWrapter);
    }

    @OnClick({R.id.tv_setBackText, R.id.et_searchEdit, R.id.ll_groupLayout})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setBackText:

                finish();
                break;

            case R.id.et_searchEdit:

                Intent intent = new Intent() ;
                intent.setClass(ForwardingActivity.this , SearchContactActivity.class ) ;
                startActivityForResult(intent , RequestCode_intoSearchContact);


                break;

            case R.id.ll_groupLayout:

                startActivity(new Intent(ForwardingActivity.this, GroupListActivity.class));
                break;
        }
    }

    /**
     * 当在其他界面已经完成了消息转发的时候，直接退回当前界面，
     * 当前界面也立即关闭，回到之前的会话的界面。
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case  RequestCode_intoSearchContact :
                if (resultCode == RESULT_OK && data != null){

                }
                break ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conversationList != null) {
            conversationList = null;
        }
        if (forwardingFriendInfoList != null) {
            forwardingFriendInfoList = null;
        }

        System.gc();
    }

}
