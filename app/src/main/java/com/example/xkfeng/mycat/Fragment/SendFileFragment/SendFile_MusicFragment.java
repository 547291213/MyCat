package com.example.xkfeng.mycat.Fragment.SendFileFragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.DrawableView.SendFile.MusicAdapter;
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

public class SendFile_MusicFragment extends Fragment {

    @BindView(R.id.nlv_sendMusicList)
    NestedListView nlvSendMusicList;
    @BindView(R.id.ll_sendMusicLayout)
    LinearLayout llSendMusicLayout;
    Unbinder unbinder;
    private View view;
    private Context mContext;
    private Activity mActivity;
    private List<FileItem> fileItems ;
    private SendFileController mController;
    private Dialog loadingDialog;
    private final static int SCAN_OK = 1;
    private final static int SCAN_ERROR = 0;
    private MyHandler myHandler = new MyHandler(this) ;
    private MusicAdapter musicAdapter ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_send_music, container, false);

        unbinder = ButterKnife.bind(this, view);
        fileItems = new ArrayList<>() ;
        mContext = getContext() ;
        mActivity = getActivity() ;
        fileItems = new ArrayList<>() ;
        getMusics() ;
        return view;
    }

    private void getMusics(){

        loadingDialog = DialogHelper.createLoadingDialog(mContext , mActivity.getString(R.string.sdcard_in_scanning));
        loadingDialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[] {MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DATE_MODIFIED, MediaStore.Audio.AudioColumns.MIME_TYPE};

                String selection = MediaStore.Audio.AudioColumns.MIME_TYPE + "= ? "
                        + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? ";

                String[] selectionArgs = new String[] {
                        "audio/mpeg", "audio/x-ms-wma", "audio/x-wav", "audio/midi"};

                Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, selectionArgs, MediaStore.Audio.AudioColumns.DATE_MODIFIED + " desc");

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE));
                        String date = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED));
                        if (scannerFile(filePath)) {
                            FileItem fileItem = new FileItem(filePath, fileName, size, date, 0);
                            fileItems.add(fileItem);
                        }

                    }
                    cursor.close();
                    cursor = null;
                    //通知Handler扫描图片完成
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


    public void setmController(SendFileController controller) {
        mController = controller;
    }

    public int getTotalCount() {
        return mController.getTotalCount();
    }

    public long getTotalSize() {
        return mController.getTotalSize();
    }

    private static class MyHandler extends Handler{

        private final WeakReference<SendFile_MusicFragment> weakReference ;

        public MyHandler(SendFile_MusicFragment musicFragment){
            weakReference = new WeakReference<>(musicFragment) ;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SendFile_MusicFragment musicFragment = weakReference.get() ;
            if (musicFragment == null){
                return ;
            }
            switch (msg.what){
                case SCAN_OK :

                    musicFragment.loadingDialog.dismiss();
                    musicFragment.musicAdapter = new MusicAdapter(musicFragment , musicFragment.fileItems) ;
                    musicFragment.musicAdapter.setUpdateSelectedStateListener(musicFragment.mController);
                    musicFragment.nlvSendMusicList.setAdapter(musicFragment.musicAdapter);
                    break ;

                case SCAN_ERROR :

                    musicFragment.loadingDialog.dismiss();
                    ITosast.showShort(musicFragment.mContext , musicFragment.getString(R.string.sdcard_not_prepare_toast)).show();
                    break ;
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
