package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class WaterView extends View {

    private static final String TAG = "WaterView";


    public WaterView(Context context) {
        super(context);
    }

    public WaterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
