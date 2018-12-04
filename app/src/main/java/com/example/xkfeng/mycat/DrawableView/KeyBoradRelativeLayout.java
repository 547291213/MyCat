package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.SqlHelper.LoginSQLDao;

/**
 * 聊天消息界面
 * 自定义主布局
 * 用于监听系统软键盘的显示和隐藏
 */
public class KeyBoradRelativeLayout extends RelativeLayout {

    public static final int KEY_BORAD_SHOW = 0;
    public static final int KEY_BORAD_HIDE = 1;
    public static final int KEY_BORAD_MINHEIGHT = 50;
    private android.os.Handler handler = new android.os.Handler();
    private KeyBoradStateListener keyBoradStateListener;
    private static final String TAG = "KeyBoradRelativeLayout";

    private ChatListView mChatListView ;


    public KeyBoradRelativeLayout(Context context) {
        super(context);
    }

    public KeyBoradRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBoradRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(){
        mChatListView  = (ChatListView) findViewById(R.id.clv_messageListView) ;
    }

    private boolean mChatListViewIsNull(){
        return mChatListView == null ;
    }

    public void setChatListAadapter(ChatListAdapter chatListAadapter){

        mChatListView.setAdapter(chatListAadapter);
    }

    public void setToPosition(int position){
        if (mChatListViewIsNull()){return ;}
        mChatListView.smoothScrollToPosition(position);
    }

    public ChatListView getmChatListView(){
        return mChatListView ;
    }

    public void setToBottom(){
        if (mChatListViewIsNull()){return ;}

        mChatListView.clearFocus();
        mChatListView.post(new Runnable() {
            @Override
            public void run() {
                mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
            }
        }) ;
    }


    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (keyBoradStateListener == null){
                    return;
                }
                if (oldh - h > KEY_BORAD_MINHEIGHT){
                   keyBoradStateListener.stateChange(KEY_BORAD_SHOW);
                }else {
                    keyBoradStateListener.stateChange(KEY_BORAD_HIDE);
                }
            }
        });

    }

    public void setKeyBoradStateListener(KeyBoradStateListener keyBoradStateListener) {
        this.keyBoradStateListener = keyBoradStateListener;
    }

    /**
     * 废弃
     */
    @Deprecated
    public interface KeyBoradStateListener{
        public void stateChange(int state) ;
    }
}
