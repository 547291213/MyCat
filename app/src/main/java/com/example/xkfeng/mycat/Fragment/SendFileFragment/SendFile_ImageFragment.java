package com.example.xkfeng.mycat.Fragment.SendFileFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.xkfeng.mycat.DrawableView.SendFile.ImageAdapter;
import com.example.xkfeng.mycat.DrawableView.SendFileController;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.MyApplication.MyApplication;
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

public class SendFile_ImageFragment extends Fragment {


    @BindView(R.id.gl_sendImgGrid)
    GridView glSendImgGrid;
    @BindView(R.id.ll_sendImgLayout)
    LinearLayout llSendImgLayout;
    Unbinder unbinder;
    private Context mContext;
    private Activity mActivity;
    private View view;
    private SendFileController mController;
    private ImageAdapter imageAdapter;
    private List<FileItem> fileItems ;

    private final MyHandler myHandler = new MyHandler(this);
    private static final int SCAN_OK = 1;
    private static final int SCAN_ERROR = 0;
    private Dialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_send_img, container, false);
        mContext = getContext();
        mActivity = getActivity();
        unbinder = ButterKnife.bind(this, view);
        fileItems = new ArrayList<>() ;
        getImages();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    private void getImages() {
        //显示加载对话框
        loadingDialog = DialogHelper.createLoadingDialog(mContext, mContext.getResources().getString(R.string.sdcard_in_scanning));
        loadingDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[]{MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.SIZE};
                Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");
                if (cursor == null || cursor.getCount() == 0) {
                    myHandler.sendEmptyMessage(SCAN_ERROR);
                } else {
                    while (cursor.moveToNext()) {
                        //获取图片的路径
                        String path = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        String fileName = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        if (scannerFile(path)) {
                            FileItem item = new FileItem(path, fileName, size, null, 0);
                            fileItems.add(item);
                        }
                    }
                    cursor.close();
                    //通知Handler扫描图片完成
                    myHandler.sendEmptyMessage(SCAN_OK);
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

    public void setmController(SendFileController controller) {
        mController = controller;
    }

    public int getTotalCount() {
        return mController.getTotalCount();
    }

    public long getTotalSize() {
        return mController.getTotalSize();
    }

    private static class MyHandler extends Handler {

        private final WeakReference<SendFile_ImageFragment> weakReference;

        public MyHandler(SendFile_ImageFragment file_imageFragment) {
            weakReference = new WeakReference<>(file_imageFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SendFile_ImageFragment imageFragment = weakReference.get();
            if (imageFragment == null) {
                return;
            }
            switch (msg.what) {
                case SCAN_OK:
                    imageFragment.loadingDialog.dismiss();
                    imageFragment.imageAdapter = new ImageAdapter(imageFragment, imageFragment.fileItems);
                    imageFragment.imageAdapter.setUpdateSelectedStateListener(imageFragment.mController);
                    imageFragment.glSendImgGrid.setAdapter(imageFragment.imageAdapter);
                    break;

                case SCAN_ERROR:
                    imageFragment.loadingDialog.dismiss();
                    ITosast.showShort(imageFragment.getContext(), imageFragment.getString(R.string.sdcard_not_prepare_toast)).show();
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
