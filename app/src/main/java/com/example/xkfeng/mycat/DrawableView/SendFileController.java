package com.example.xkfeng.mycat.DrawableView;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.xkfeng.mycat.Activity.SendFileActivity;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_ApkFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_DocumentFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_ImageFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_MusicFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_OtherFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_VideoFragment;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendFileController implements
        View.OnClickListener,
        ViewPager.OnPageChangeListener,
        UpdateSelectedStateListener {

    private Context mContext;
    private Activity mActivity;

    private SendFileView sendFileView;
    private ViewPagerAdapter viewPagerAdapter;

    private List<Fragment> fragments;
    private SendFile_DocumentFragment documentFragment;
    private SendFile_ImageFragment imageFragment;
    private SendFile_MusicFragment musicFragment;
    private SendFile_VideoFragment videoFragment;
    private SendFile_ApkFragment apkFragment;
    private SendFile_OtherFragment otherFragment;

    private int totalCount;
    private long totalSize;

    //选中的文件集合
    private HashMap<FileType, ArrayList<String>> mFileMap = new HashMap<>();

    public SendFileController(SendFileActivity context, SendFileView sendFileView) {
        mContext = context;
        mActivity = context;
        this.sendFileView = sendFileView;

        fragments = new ArrayList<>();
        documentFragment = new SendFile_DocumentFragment();
        imageFragment = new SendFile_ImageFragment();
        musicFragment = new SendFile_MusicFragment();
        videoFragment = new SendFile_VideoFragment();
        apkFragment = new SendFile_ApkFragment();
        otherFragment = new SendFile_OtherFragment();

        documentFragment.setmController(this);
        imageFragment.setmController(this);
        musicFragment.setmController(this);
        videoFragment.setmController(this);
        otherFragment.setmController(this);

        fragments.add(documentFragment);
        fragments.add(imageFragment);
        fragments.add(musicFragment);
        fragments.add(videoFragment);
        fragments.add(apkFragment);
        fragments.add(otherFragment);

        viewPagerAdapter = new ViewPagerAdapter(((SendFileActivity) mContext).getSupportFragmentManager(), fragments);
        this.sendFileView.setViewPagerAdapter(viewPagerAdapter);

    }

    public int getTotalCount() {
        return totalCount;
    }

    public long getTotalSize() {
        return totalSize;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        sendFileView.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.actionbar_file_btn:
                sendFileView.setCurrentItem(0);
                break;

            case R.id.actionbar_image_btn:
                sendFileView.setCurrentItem(1);
                break;

            case R.id.actionbar_music_btn:
                sendFileView.setCurrentItem(2);
                break;

            case R.id.actionbar_video_btn:
                sendFileView.setCurrentItem(3);
                break;

            case R.id.actionbar_apk_btn:
                sendFileView.setCurrentItem(4);
                break;

            case R.id.actionbar_other_btn:
                sendFileView.setCurrentItem(5);
                break;

            case R.id.bt_sendBtn:
                ITosast.showShort(mContext, "send").show();
                break;

            case R.id.tv_setBackText:
                mActivity.finish();
                break;

        }
    }


    @Override
    public void onSelected(String path, long fileSize, FileType type) {

    }

    @Override
    public void onUnselected(String path, long fileSize, FileType type) {

    }
}
