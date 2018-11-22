package com.example.xkfeng.mycat.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.xkfeng.mycat.DrawableView.ListSlideView;

import java.util.List;

public class BottomTabFragmentPageAdapter extends FragmentPagerAdapter {

    private FragmentManager fm ;
    private List<Fragment> fragmentList ;
    public BottomTabFragmentPageAdapter(FragmentManager fm , List<Fragment> fragmentList) {
        super(fm);
        this.fm = fm ;
        this.fragmentList = fragmentList ;
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
