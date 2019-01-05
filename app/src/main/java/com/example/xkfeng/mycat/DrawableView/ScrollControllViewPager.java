package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollControllViewPager extends ViewPager {

    private boolean isCanScroll = true ; //false 禁止滑动
    public ScrollControllViewPager(@NonNull Context context) {
        super(context);
    }

    public ScrollControllViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean isCanScroll){
        this.isCanScroll =  isCanScroll ;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (!isCanScroll){
            return false ;
        }else
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {

        if (!isCanScroll){
            return false ;
        }else
        return super.onInterceptHoverEvent(event);
    }
}
