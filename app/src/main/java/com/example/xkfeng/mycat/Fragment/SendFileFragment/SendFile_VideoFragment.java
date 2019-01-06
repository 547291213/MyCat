package com.example.xkfeng.mycat.Fragment.SendFileFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.DrawableView.SendFile.VideoAdapter;
import com.example.xkfeng.mycat.DrawableView.SendFileController;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SendFile_VideoFragment extends Fragment {
    @BindView(R.id.nlv_sendVideoList)
    NestedListView nlvSendVideoList;
    @BindView(R.id.ll_sendVideoLayout)
    LinearLayout llSendVideoLayout;
    Unbinder unbinder;
    private SendFileController mController;
    private View view;

    private List<FileItem> fileItems;
    private VideoAdapter videoAdapter;
    private Dialog loadingDialog;
    //扫描存储中文件成功
    private static final int SCAN_OK = 1;
    //扫描存储中文件失败
    private static final int SCAN_ERROR = 0;
    private MyHandler myHandler = new MyHandler(this);
    private Context mContext;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_send_video, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        mActivity = getActivity();
        fileItems = new ArrayList<>();
        getVideos();
        return view;
    }

    private void getVideos() {
        //显示加载对话框
        loadingDialog = DialogHelper.createLoadingDialog(mContext, mContext.getResources().getString(R.string.sdcard_in_scanning));
        loadingDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[]{MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.SIZE,
                        MediaStore.Video.VideoColumns.DATE_MODIFIED , MediaStore.Video.Thumbnails.DATA};
                try {
                    String selection = MediaStore.Audio.AudioColumns.MIME_TYPE + "= ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? ";

                    //类型是在http://qd5.iteye.com/blog/1564040找的
                    String[] selectionArgs = new String[]{
                            "video/quicktime", "video/mp4", "application/vnd.rn-realmedia", "aapplication/vnd.rn-realmedia",
                            "video/x-ms-wmv", "video/x-msvideo", "video/3gpp", "video/x-matroska"};

                    Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                            selection, selectionArgs, MediaStore.Video.VideoColumns.DATE_MODIFIED + " desc");

                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME));
                            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                            String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE));
                            String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED));
                            String videoScaledDownPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                            if (scannerFile(filePath)) {
                                FileItem fileItem = new FileItem(filePath, fileName, size, date, 0);
                                fileItem.setVideoScaledDownPath(videoScaledDownPath);
                                Log.d("ScaleDownPath", "run: " + videoScaledDownPath);
                                fileItems.add(fileItem);
                            }
                        }
                        cursor.close();
                        cursor = null;
                        myHandler.sendEmptyMessage(SCAN_OK);
                    } else {
                        myHandler.sendEmptyMessage(SCAN_ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private File file;

    private boolean scannerFile(String path) {
        file = new File(path);
        if (file.exists() && file.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private static class MyHandler extends Handler {

        private final WeakReference<SendFile_VideoFragment> weakReference;

        public MyHandler(SendFile_VideoFragment videoFragment) {
            this.weakReference = new WeakReference<>(videoFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SendFile_VideoFragment videoFragment = weakReference.get();
            if (videoFragment == null) {
                return;
            }
            switch (msg.what) {
                case SCAN_OK:

                    videoFragment.loadingDialog.dismiss();
                    videoFragment.videoAdapter = new VideoAdapter(videoFragment, videoFragment.fileItems);
                    videoFragment.videoAdapter.setUpdateSelectedStateListener(videoFragment.mController);
                    videoFragment.nlvSendVideoList.setAdapter(videoFragment.videoAdapter);
                    break;

                case SCAN_ERROR:
                    videoFragment.loadingDialog.dismiss();
                    ITosast.showShort(videoFragment.getContext(), videoFragment.getString(R.string.sdcard_not_prepare_toast)).show();
                    break;
            }
        }
    }

    public void setmController(SendFileController controller) {
        mController = controller;
    }

    public int getTotalCount() {
        return mController.getTotalCount();
    }

    public long getTotalSize() {
        return mController.getTotalSize();
    }
}
