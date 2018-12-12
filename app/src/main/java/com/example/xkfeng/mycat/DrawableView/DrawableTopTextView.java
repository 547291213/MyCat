package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class DrawableTopTextView extends android.support.v7.widget.AppCompatTextView {

    private Context mContxt ;

    private TopDrawableClickListener topDrawableClickListener ;

    private Drawable topDrawable ;

    public DrawableTopTextView(Context context) {
        this(context , null);
    }

    public DrawableTopTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContxt = context ;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP ){
            topDrawable = getCompoundDrawables()[1] ;
            if (topDrawable == null){
                return super.onTouchEvent(event) ;
            }

            topDrawable.setBounds(0 , 0 , topDrawable.getIntrinsicWidth() , topDrawable.getIntrinsicHeight());

            if (getY() < topDrawable.getBounds().height() ){
                if (topDrawableClickListener != null){
                    topDrawableClickListener.onTopDrawableClick(topDrawable);
                }
            }
        }
        return super.onTouchEvent(event);
    }


    public void setTopDrawableClickListener(TopDrawableClickListener topDrawableClickListener) {
        this.topDrawableClickListener = topDrawableClickListener;
    }

    public interface TopDrawableClickListener{
        public void onTopDrawableClick(Drawable drawable)  ;
    }
}
