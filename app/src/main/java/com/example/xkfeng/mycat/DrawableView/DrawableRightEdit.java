package com.example.xkfeng.mycat.DrawableView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class DrawableRightEdit extends android.support.v7.widget.AppCompatEditText {

    private RightDrawableClickListener rightDrawableClickListener ;
    private Drawable mRightDrawable ;
    private Context mContext ;

    public DrawableRightEdit(Context context) {
        this(context , null) ;
    }

    public DrawableRightEdit(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public DrawableRightEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP){
            mRightDrawable  = getCompoundDrawables()[2] ;
            if (mRightDrawable == null){
                return super.onTouchEvent(event);
            }
            mRightDrawable.setBounds(0,0,mRightDrawable.getIntrinsicWidth() ,
                    mRightDrawable.getIntrinsicHeight());
            if (event.getX() > getWidth()-getTotalPaddingRight()-mRightDrawable.getBounds().width() && rightDrawableClickListener != null){
                rightDrawableClickListener.onRightDrawableClick(mRightDrawable);
            }
        }
        return super.onTouchEvent(event);
    }

    public void setRightDrawableClickListener(RightDrawableClickListener rightDrawableClickListener) {
        this.rightDrawableClickListener = rightDrawableClickListener;
    }

    public interface RightDrawableClickListener{
        public void onRightDrawableClick(Drawable drawable) ;
    }

}
