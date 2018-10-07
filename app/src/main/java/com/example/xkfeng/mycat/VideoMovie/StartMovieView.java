package com.example.xkfeng.mycat.VideoMovie;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by initializing on 2018/10/5.
 */

public class StartMovieView extends VideoView {
    public StartMovieView(Context context) {
        super(context);
    }

    public StartMovieView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StartMovieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec) , MeasureSpec.getSize(heightMeasureSpec));
    }
}
