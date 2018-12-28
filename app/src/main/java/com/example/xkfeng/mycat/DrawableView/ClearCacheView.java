package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;

public class ClearCacheView extends LinearLayout {

    private static final String TAG = "ClearCacheView";
    private View convertView;
    private ImageView iv_rotateImage;
    private TextView tv_loadingTip;
    private WaterView wv_waterView;
    private WaveRaiseView wrv_waveRaiseView;
    private AnimationDrawable clearDrawable;
    private RotateAnimation rotateAnimation ;
    private Context mContext;

    public ClearCacheView(Context context) {
        this(context, null);
    }

    public ClearCacheView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        Log.d(TAG, "initView: ");

        convertView = LayoutInflater.from(mContext).inflate(R.layout.clear_cache_layout, null, false);
        iv_rotateImage = convertView.findViewById(R.id.iv_rotateImage) ;
        rotateAnimation = new RotateAnimation(0,359,RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f ) ;
        rotateAnimation.setDuration(1000);
        LinearInterpolator linearInterpolator = new LinearInterpolator() ;
        rotateAnimation.setInterpolator(linearInterpolator);
        rotateAnimation.setRepeatCount(-1);
        iv_rotateImage.startAnimation(rotateAnimation);

        wv_waterView = convertView.findViewById(R.id.wv_waterView) ;
        wrv_waveRaiseView = convertView.findViewById(R.id.wrv_waveRaiseView); ;

        tv_loadingTip = convertView.findViewById(R.id.tv_loadingTip) ;
        clearDrawable = (AnimationDrawable) tv_loadingTip.getCompoundDrawables()[1];
        clearDrawable.start();

        //设置为当前主布局
        addView(convertView);
    }
}
