package com.example.xkfeng.mycat.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.ListSlideView;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.Fragment.ClassfiedFragment;
import com.example.xkfeng.mycat.Fragment.ReceivedInvitationFragment;
import com.example.xkfeng.mycat.Fragment.SelectedPersonFragment;
import com.example.xkfeng.mycat.Fragment.SendInvitationFragment;
import com.example.xkfeng.mycat.Model.FriendInvitationModel;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.QucikAdapterWrapter;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationDao;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationSql;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class FriendValidationActivity extends BaseActivity {


    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.tv_receivedInvitation)
    TextView tvReceivedInvitation;
    @BindView(R.id.tv_sendInvitation)
    TextView tvSendInvitation;
    @BindView(R.id.vp_invitationPager)
    ViewPager vpInvitationPager;


    private ReceivedInvitationFragment receivedInvitationFragment  ;
    private SendInvitationFragment sendInvitationFragment ;
    private List<Fragment> fragmentList;
    private FragemntAdapter fragemntAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_validation_layout);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        setIndexTitleLayout();

//        initSqlAndData();

//        initQuickAdapter();

//        innitQuickAdapterWrapperAndRecycler();
        initPager() ;

    }


    /**
     * 初始化
     */
    private void initPager() {
        tvReceivedInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpInvitationPager.setCurrentItem(0,true);
                changeTextColor(0);
            }
        });

        tvSendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpInvitationPager.setCurrentItem(1,true);
                changeTextColor(1);
            }
        });

        receivedInvitationFragment = new ReceivedInvitationFragment();
        sendInvitationFragment = new SendInvitationFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(receivedInvitationFragment);
        fragmentList.add(sendInvitationFragment);



        fragemntAdapter = new FriendValidationActivity.FragemntAdapter(getSupportFragmentManager(), fragmentList);

        vpInvitationPager.setOffscreenPageLimit(2);
        vpInvitationPager.setCurrentItem(0, true);
        vpInvitationPager.setAdapter(fragemntAdapter);
        vpInvitationPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                changeTextColor(position);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeTextColor(int position) {
        if (position == 0) {
            tvReceivedInvitation.setTextColor(Color.parseColor("#66CDAA"));
            tvSendInvitation.setTextColor(Color.parseColor("#000000"));
        } else if (position == 1) {
            tvSendInvitation.setTextColor(Color.parseColor("#66CDAA"));
            tvReceivedInvitation.setTextColor(Color.parseColor("#000000"));
        }
    }


    final class FragemntAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public FragemntAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
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


    @OnClick({R.id.tv_setBackText})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setBackText:

                finish();
                break;

        }
    }
}
