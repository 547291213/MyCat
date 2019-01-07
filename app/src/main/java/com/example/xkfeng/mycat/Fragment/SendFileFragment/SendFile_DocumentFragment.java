package com.example.xkfeng.mycat.Fragment.SendFileFragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProvider;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.DrawableView.SendFile.DocumentAdapter;
import com.example.xkfeng.mycat.DrawableView.SendFileController;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.FileHelper;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BUG :
 * 1 无法读取doc，docx ,ppt 等word文件
 * 2 显示时间错乱
 * 3 显示标题可能有待修改。
 *
 */
public class SendFile_DocumentFragment extends Fragment {

    @BindView(R.id.nlv_sendDocList)
    NestedListView nlvSendDocList;
    @BindView(R.id.ll_senDocLayout)
    LinearLayout llSenDocLayout;
    Unbinder unbinder;
    private SendFileController mController;
    private View view;
    private NestedListView nestedListView;

    private List<FileItem> mDocuments ;
    private DocumentAdapter documentAdapter;
    private Dialog loadingDialog;
    //扫描存储中文件成功
    private static final int SCAN_OK = 1;
    //扫描存储中文件失败
    private static final int SCAN_ERROR = 0;

    private Context mContext;
    private Activity mActivity;

    private final MyHandler myHandler = new MyHandler(this);

    private static final String TAG = "SendFile_DocumentFragme";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_send_doc, container, false);
        nestedListView = view.findViewById(R.id.nlv_sendDocList);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        mActivity = getActivity();
        mDocuments = new ArrayList<>() ;
        getDocuments();

        return view;
    }

    private void getDocuments() {
        //显示加载对话框
        loadingDialog = DialogHelper.createLoadingDialog(mContext, mContext.getResources().getString(R.string.sdcard_in_scanning));
        loadingDialog.show();

        //在子线程中进行数据加载
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[]{MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE,
                        MediaStore.Files.FileColumns.DATE_MODIFIED};
                //分别对应 txt doc pdf ppt xls wps docx pptx xlsx 类型的文档
                String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";

                String[] selectionArgs = new String[]{"text/plain", "application/msword", "application/pdf",
                        "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "application/vnd.ms-works",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
                Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                        selection, selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");

                Log.d(TAG, "run: size :" + cursor.getCount());
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                        String date = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                        if (scannerFile(filePath)) {
                            String path = filePath;
                            String str = path.substring(path.lastIndexOf('/') + 1) ;
                            if (TextUtils.isEmpty(str) || str.startsWith("com.") || str.startsWith(".")
                                    || path.startsWith("/storage/emulated/0/Android/") || path.indexOf(".log") >= 0 || path.startsWith("/storage/emulated/0/tencent/TPush/Logs/")){
                                //对该类名称起始的文件不处理
                            }else {
//                                Log.d("DocumentFile", "fileName " + str + " ,filePath " + filePath);
                                FileItem fileItem = new FileItem(filePath, null, size, date,0);
                                mDocuments.add(fileItem);
                            }
//                            FileItem fileItem = new FileItem(filePath, null, size, date, 0);
//                            mDocuments.add(fileItem);
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

    private static class MyHandler extends Handler {

        private final WeakReference<SendFile_DocumentFragment> documentFragmentWeakReference;

        public MyHandler(SendFile_DocumentFragment documentFragment) {
            this.documentFragmentWeakReference = new WeakReference<>(documentFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SendFile_DocumentFragment documentFragment = documentFragmentWeakReference.get();
            if (documentFragment == null) {
                return;
            }
            switch (msg.what) {
                case SCAN_OK:

                    documentFragment.loadingDialog.dismiss();
                    documentFragment.documentAdapter = new DocumentAdapter(documentFragment, documentFragment.mDocuments);
                    documentFragment.documentAdapter.setUpdateSelectedStateListener(documentFragment.mController);
                    documentFragment.nestedListView.setAdapter(documentFragment.documentAdapter);
                    break;

                case SCAN_ERROR:
                    documentFragment.loadingDialog.dismiss();
                    ITosast.showShort(documentFragment.getContext(), documentFragment.getString(R.string.sdcard_not_prepare_toast)).show();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
