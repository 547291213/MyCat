package com.example.xkfeng.mycat.Fragment.SendFileFragment;

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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.DrawableView.SendFile.OtherAdapter;
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

public class SendFile_OtherFragment extends Fragment {


    @BindView(R.id.nlv_sendOtherList)
    NestedListView nlvSendOtherList;
    @BindView(R.id.ll_sendOtherLayout)
    LinearLayout llSendOtherLayout;
    Unbinder unbinder;
    private View view;
    private SendFileController mController;
    private List<FileItem> fileItems;
    private OtherAdapter otherAdapter;
    private Context mContext;
    private Dialog loadingDialog;
    //扫描存储中文件成功
    private static final int SCAN_OK = 1;
    //扫描存储中文件失败
    private static final int SCAN_ERROR = 0;
    private final MyHandler myHandler = new MyHandler(this);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_send_other, container, false);
        mContext = getContext();
        fileItems = new ArrayList<>();
        unbinder = ButterKnife.bind(this, view);
        getOthers();
        return view;
    }

    private void getOthers() {
        //显示加载对话框
        loadingDialog = DialogHelper.createLoadingDialog(mContext, mContext.getResources().getString(R.string.sdcard_in_scanning));
        loadingDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[]{MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE,
                        MediaStore.Files.FileColumns.DATE_MODIFIED};

                String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? or "
                        + MediaStore.Files.FileColumns.MIME_TYPE + "= ? or "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "= ? ";

                //, "application/vnd.android.package-archive"
                String[] selectionArgs = new String[]{"application/zip",
                        "application/x-rar-compressed" , "application/octet-stream" };

                Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                        selection, selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");

                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                        String date = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                        if (scannerFile(filePath)) {

                            FileItem fileItem = new FileItem(filePath, fileName, size, date,0);
                            String path = fileItem.getFilePath();
                            String str = path.substring(path.lastIndexOf('/') + 1) ;
                            if (TextUtils.isEmpty(str) || str.startsWith("com.") || str.startsWith(".")){
                                //对该类名称起始的文件不处理
                            }else {
                                fileItems.add(fileItem);
                            }
                        }

                    }
                    cursor.close();
                    cursor = null;
                    myHandler.sendEmptyMessage(SCAN_OK);
                } else {
                    myHandler.sendEmptyMessage(SCAN_ERROR);
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

        private final WeakReference<SendFile_OtherFragment> weakReference;

        public MyHandler(SendFile_OtherFragment otherFragment) {
            this.weakReference = new WeakReference<>(otherFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SendFile_OtherFragment otherFragment = weakReference.get();
            if (otherFragment == null) {
                return;
            }
            switch (msg.what) {
                case SCAN_OK:

                    otherFragment.loadingDialog.dismiss();
                    otherFragment.otherAdapter = new OtherAdapter(otherFragment, otherFragment.fileItems);
                    otherFragment.otherAdapter.setUpdateSelectedStateListener(otherFragment.mController);
                    otherFragment.nlvSendOtherList.setAdapter(otherFragment.otherAdapter);
                    break;

                case SCAN_ERROR:
                    otherFragment.loadingDialog.dismiss();
                    ITosast.showShort(otherFragment.getContext(), otherFragment.getString(R.string.sdcard_not_prepare_toast)).show();
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
