package com.example.xkfeng.mycat.DrawableText;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;

import com.example.xkfeng.mycat.R;

/**
 * Created by initializing on 2018/10/5.
 */

public class DrawableTextEdit extends TextInputEditText {

    private int width;

    private static final String TAG = "DrawableTextEdit";

   // private static Boolean FLAG = true ;

    private int TYPE = 1 ;

    private DrawableListener drawableListener;

    public DrawableTextEdit(Context context) {
        super(context);
    }

    public DrawableTextEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                Drawable right = getCompoundDrawables()[2];
                Drawable left = getCompoundDrawables()[0];

                if ((right!=null) && (event.getX() > getWidth()-getTotalPaddingRight()-right.getBounds().width())&& drawableListener!=null)
                {
                    drawableListener.rightDrawableClick(right);
                }
                else if ((left != null)&&(right!=null) && (event.getX() > getWidth()-getTotalPaddingRight()-right.getBounds().width()-left.getBounds().width()))
                {
                    drawableListener.leftDrawableClick(left);
                }

                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        width = getMeasuredWidth();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = getCompoundDrawables()[0];
        Drawable drawable1 = getCompoundDrawables()[2] ;
        if (drawable != null && drawable1!=null&&width>0 ) {
            if (TYPE==1)
            {
                drawable.setBounds(width-100-drawable1.getBounds().width()-5, 0, width-drawable1.getBounds().width()-10, 50);
                setCompoundDrawablePadding(-100);

            }
            else if (TYPE ==0){
                drawable.setBounds(width-85-drawable1.getBounds().width()-5, 0, width-drawable1.getBounds().width()-30, 60);
                setCompoundDrawablePadding(-drawable1.getBounds().width()+10);

            }
            setCompoundDrawables(drawable, null, drawable1, null);
//            FLAG = false ;
         //   Log.d(TAG, "onDraw: " + drawable.getBounds().left + " Padding-left " );
        }
    }



    public void setTYPE(int TYPE)
    {
        this.TYPE =TYPE ;
    }

    public void setDrawableListener(DrawableListener drawableListener) {
        this.drawableListener = drawableListener;
    }

    public interface DrawableListener {
        public void leftDrawableClick(Drawable drawable);

        public void rightDrawableClick(Drawable drawable);
    }

}
