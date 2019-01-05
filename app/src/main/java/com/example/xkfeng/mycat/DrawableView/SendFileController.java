package com.example.xkfeng.mycat.DrawableView;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.xkfeng.mycat.Activity.SendFileActivity;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;

public class SendFileController implements
        View.OnClickListener,
        ViewPager.OnPageChangeListener,
        UpdateSelectedStateListener {

    private Context mContext;
    private Activity mActivity;

    private SendFileView sendFileView;

    public SendFileController(SendFileActivity context, SendFileView sendFileView) {
        mContext = context;
        mActivity = context;
        this.sendFileView = sendFileView;

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
