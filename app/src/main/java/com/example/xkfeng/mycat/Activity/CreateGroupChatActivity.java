package com.example.xkfeng.mycat.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.Fragment.ClassfiedFragment;
import com.example.xkfeng.mycat.Fragment.SelectedPersonFragment;
import com.example.xkfeng.mycat.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateGroupChatActivity extends BaseActivity {


    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.tv_selectPersonCreate)
    TextView tvSelectPersonCreate;
    @BindView(R.id.tv_classified)
    TextView tvClassified;
    @BindView(R.id.ll_linearLayout)
    LinearLayout llLinearLayout;
    @BindView(R.id.vp_createGroupPager)
    ViewPager vpCreateGroupPager;

    private SelectedPersonFragment selectedPersonFragment;
    private ClassfiedFragment classfiedFragment;
    private List<Fragment> fragmentList;
    private FragemntAdapter fragemntAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_group_chat_layout);
        ButterKnife.bind(this);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        tvSelectPersonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpCreateGroupPager.setCurrentItem(0,true);
                changeTextColor(0);
            }
        });

        tvClassified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpCreateGroupPager.setCurrentItem(1,true);
                changeTextColor(1);
            }
        });

        classfiedFragment = new ClassfiedFragment();
        selectedPersonFragment = new SelectedPersonFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(classfiedFragment);
        fragmentList.add(selectedPersonFragment);


        fragemntAdapter = new FragemntAdapter(getSupportFragmentManager(), fragmentList);

        vpCreateGroupPager.setOffscreenPageLimit(2);
        vpCreateGroupPager.setCurrentItem(0, true);
        vpCreateGroupPager.setAdapter(fragemntAdapter);
        vpCreateGroupPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            tvSelectPersonCreate.setTextColor(Color.parseColor("#66CDAA"));
            tvClassified.setTextColor(Color.parseColor("#000000"));
        } else if (position == 1) {
            tvClassified.setTextColor(Color.parseColor("#66CDAA"));
            tvSelectPersonCreate.setTextColor(Color.parseColor("#000000"));
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

}
