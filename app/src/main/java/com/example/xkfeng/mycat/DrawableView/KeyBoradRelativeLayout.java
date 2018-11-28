package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

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

    public KeyBoradRelativeLayout(Context context) {
        super(context);
    }

    public KeyBoradRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBoradRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
