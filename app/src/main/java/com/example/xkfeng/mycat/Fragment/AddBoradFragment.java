package com.example.xkfeng.mycat.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.ChatMsgActivity;
import com.example.xkfeng.mycat.Activity.ViewImageActivity;
import com.example.xkfeng.mycat.DrawableView.DrawableTopTextView;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
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
                    mActivity.startActivityForResult(new Intent(mContext, ViewImageActivity.class), ChatMsgActivity.REQUEST_CAMERA);

                }
                break;

            case R.id.tv_chatMsgPhoto:

                ITosast.showShort(getContext(), "photo").show();
                break;

            case R.id.tv_chatMsgPosition:


                ITosast.showShort(getContext(), "position").show();
                break;

            case R.id.tv_chatMsgBusiness:
                if (onBusinessItemClickListener != null) {
                    onBusinessItemClickListener.onBusinessItemClick();
                } else {
                    ITosast.showShort(mContext, "发送名片失败").show();
                }
                break;

            case R.id.tv_chatMsgGif:


                ITosast.showShort(getContext(), "gif").show();
                break;

            case R.id.tv_chatMsgFile:


                ITosast.showShort(getContext(), "file").show();
                break;
        }
    }

    public void setOnBusinessItemClickListener(OnBusinessItemClickListener onBusinessItemClickListener) {
        this.onBusinessItemClickListener = onBusinessItemClickListener;
    }

    public interface OnBusinessItemClickListener {
        public void onBusinessItemClick();
    }
}
