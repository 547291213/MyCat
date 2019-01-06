package com.example.xkfeng.mycat.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.ChatMsgActivity;
import com.example.xkfeng.mycat.Activity.IndexActivity;
import com.example.xkfeng.mycat.Activity.MapViewActivity;
import com.example.xkfeng.mycat.Activity.ModifyUserInfoActivity;
import com.example.xkfeng.mycat.Activity.SendFileActivity;
import com.example.xkfeng.mycat.Activity.ViewImageActivity;
import com.example.xkfeng.mycat.DrawableView.DrawableTopTextView;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

public class AddBoradFragment extends Fragment {

    @BindView(R.id.tv_chatMsgAlbum)
    DrawableTopTextView tvChatMsgAlbum;
    @BindView(R.id.tv_chatMsgPhoto)
    DrawableTopTextView tvChatMsgPhoto;
    @BindView(R.id.tv_chatMsgBusiness)
    DrawableTopTextView tvChatMsgBusiness;
    @BindView(R.id.tv_chatMsgPosition)
    DrawableTopTextView tvChatMsgPosition;
    @BindView(R.id.tv_chatMsgGif)
    DrawableTopTextView tvChatMsgGif;
    @BindView(R.id.tv_chatMsgFile)
    DrawableTopTextView tvChatMsgFile;
    Unbinder unbinder;
    private View view;
    private Context mContext;
    private static final String TAG = "AddBoradFragment";
    private Activity mActivity;
    private OnBusinessItemClickListener onBusinessItemClickListener;
    private  File imageFileDir;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.add_borad_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        mActivity = getActivity();
        mContext = getContext();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_chatMsgAlbum, R.id.tv_chatMsgPhoto, R.id.tv_chatMsgPosition,
            R.id.tv_chatMsgBusiness, R.id.tv_chatMsgGif, R.id.tv_chatMsgFile})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_chatMsgAlbum:
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ITosast.showShort(mContext, "请在应用管理中打开“读写存储”访问权限！").show();
                } else {
                    mActivity.startActivityForResult(new Intent(mContext, ViewImageActivity.class), ChatMsgActivity.RequestCode_CAMERA);

                }
                break;

            case R.id.tv_chatMsgPhoto:

                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {

                    ITosast.showShort(mContext, "请到管理中心去打开权限").show();
                    return ;
                }
                Uri imageUri;
                imageUri = getImageUri();
                //启动程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mActivity.startActivityForResult(intent, ChatMsgActivity.RequestCode_PHOTO);
                break;

            case R.id.tv_chatMsgPosition:

                mActivity.startActivityForResult(new Intent(mContext ,  MapViewActivity.class) ,ChatMsgActivity.RequestCode_LOCATION);
                break;

            case R.id.tv_chatMsgBusiness:
                if (onBusinessItemClickListener != null) {
                    onBusinessItemClickListener.onBusinessItemClick();
                } else {
                    ITosast.showShort(mContext, "发送名片失败").show();
                }
                break;

            case R.id.tv_chatMsgGif:


                ITosast.showShort(getContext(), "暂未处理该类型的消息").show();
                break;

            case R.id.tv_chatMsgFile:
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ITosast.showShort(mContext, "请在应用管理中打开“读写存储”访问权限！").show();
                } else {
                    MediaScannerConnection.scanFile(mContext,new String[]{Environment.getExternalStorageDirectory().toString()}, new String[]{"*.apk", "*.doc", "*.docx", "*.ppt",
                            "*.ppts", "*.xls", "*.xlsx", "*.pdf", "*.png", "*.jpg", "*.mp3", "*.mp4", "*.zip", "*.wps"}, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            startActivity(new Intent(mContext , SendFileActivity.class));
                        }
                    });

                    ITosast.showShort(getContext(), "file").show();
                }

                break;
        }
    }


    /**
     * 获取图片Uri地址
     */
    public Uri getImageUri() {
        Uri imageUri;

        //创建文件
        imageFileDir = new File(Environment.getExternalStorageDirectory(), "/mycat/img/" + System.currentTimeMillis() + ".jpg");
        try {
            //创建目录
            imageFileDir.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            /**
             * 如果图片已经存在
             * 删除已存在的图片
             */
            if (imageFileDir.exists()) {
                imageFileDir.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(mContext ,
                    "com.example.xkfeng.mycat.fileprovider", imageFileDir);
        } else {
            imageUri = Uri.fromFile(imageFileDir);
        }

        return imageUri;
    }

    public File getImageFileDir(){
        return imageFileDir ;
    }

    public void setOnBusinessItemClickListener(OnBusinessItemClickListener onBusinessItemClickListener) {
        this.onBusinessItemClickListener = onBusinessItemClickListener;
    }


    public interface OnBusinessItemClickListener {
        public void onBusinessItemClick();
    }


}
