package com.example.xkfeng.mycat.Fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.ImageAdapter;
import com.example.xkfeng.mycat.Model.ImageFileItem;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends Fragment implements ImageAdapter.UpdateSelectStateListener {

    private Context mContext;
    private Activity mActivity;
    private int mSelectCount;
    private View convertView;
    private ImageAdapter imageAdapter;
    private GridView gridView;
    private Dialog loadingDialog;
    private final int SCAN_OK = 1;
    private final int SCAN_ERROR = 0;
    private MyHandler myHandler = new MyHandler(this);
    private List<ImageFileItem> mImages = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        convertView = inflater.inflate(R.layout.send_img_fragment, container, false);

        gridView = convertView.findViewById(R.id.gv_albumGridView);
        mActivity = getActivity();
        mContext = getContext();
        getImgs();
        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getImgs() {
        loadingDialog = DialogHelper.createLoadingDialog(mContext, "正在加载");
        loadingDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[] {MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.SIZE};
                /**
                 * 使用Cursor就会报错，界面异常弹出，
                 * BUG原因还在查找中。
                 */
                Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");
                if (cursor == null || cursor.getCount() == 0) {
                    myHandler.sendEmptyMessage(SCAN_ERROR);
                }
                else {
                    while (cursor.moveToNext()) {
                        //获取图片的路径
                        String path = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        String fileName = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        if (scannerFile(path)) {
                            ImageFileItem item = new ImageFileItem(path, fileName, size, null, 0);
                            mImages.add(item);
                        }
                    }
                    cursor.close();
//                    //通知Handler扫描图片完成
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
        }

        return false;

    }


    private class MyHandler extends Handler {

        private final WeakReference<ImageFragment> mFragment;

        public MyHandler(ImageFragment mFragment) {

            this.mFragment = new WeakReference<ImageFragment>(mFragment);
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ImageFragment imageFragment = mFragment.get();
            if (imageFragment == null) {
                return;
            }
            switch (msg.what) {
                case SCAN_OK:

                    ITosast.showShort(mContext, "成功").show();
                    imageFragment.loadingDialog.dismiss();
                    imageFragment.imageAdapter = new ImageAdapter(imageFragment, mImages);
                    imageFragment.imageAdapter.setUpdateSelectStateListener(imageFragment);
                    imageFragment.gridView.setAdapter(imageFragment.imageAdapter);
                    break;

                case SCAN_ERROR:
                    imageFragment.loadingDialog.dismiss();
                    ITosast.showShort(mContext, "sd卡暂无准备好").show();
                    break;
            }
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (loadingDialog.isShowing() || loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private  ImageStateChange imageStateChange ;

    public void setImageStateChange(ImageStateChange imageStateChange){
        this.imageStateChange = imageStateChange ;
    }

    @Override
    public void onSelected(String path, String fileSize , int currentSelectCount) {

        if (imageStateChange != null){
            imageStateChange.isSelected(path , fileSize , currentSelectCount , true);
        }
    }

    @Override
    public void onReleased(String path, String fileSize ,  int currentSelectCount) {
        if (imageStateChange != null){
            imageStateChange.isSelected(path , fileSize , currentSelectCount , false);
        }
    }

    public interface ImageStateChange{
        public void isSelected(String path, String fileSize , int currentSelectCount , boolean isSelected) ;
    }
}
