package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SideBar extends View {
    private static final String TAG = "SideBar";
    private Context mContext;
    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private int choose = -1;
    private List<String> lists;
    private Paint mPaint;
    private OnTouchLetterChanged onTouchLetterChanged;
    private int mWidth;
    private int mHeight;
    private int singleLetterHeight;
    private Paint paint;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        lists = Arrays.asList(INDEX_STRING);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getWidth()-getPaddingLeft()-getPaddingRight();
        mHeight = getHeight()-getPaddingTop()-getPaddingBottom();
        singleLetterHeight = mHeight / lists.size();
        for (int i = 0; i < lists.size(); i++) {
            paint.setColor(Color.parseColor("#606060"));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(20);
            // 选中的状态
            if (i == choose) {
                paint.setColor(Color.parseColor("#4F41FD"));
                paint.setFakeBoldText(true);
            }
            //绘制具体的坐标
            float x = mWidth / 2 - paint.measureText(lists.get(i)) / 2 ;
            float y = i * singleLetterHeight + singleLetterHeight / 2 + getPaddingTop();
            canvas.drawText(lists.get(i), x, y, paint);
            paint.reset();

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int oldChoose = choose;
        int data = (y-getPaddingTop()) / singleLetterHeight;
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //choose = -1;
                invalidate();
                break;

            default:
                if (oldChoose != data) {
                    if (data >= 0 && data < lists.size()) {
                        choose = data;
                        if (onTouchLetterChanged != null) {
                            onTouchLetterChanged.onTouchLetterChanged(lists.get(data));
                        }
                        invalidate();
                    }

                }
                break;
        }
        return true;
    }

    public void setOnTouchLetterChanged(OnTouchLetterChanged onTouchLetterChanged) {
        this.onTouchLetterChanged = onTouchLetterChanged;
    }

    public interface OnTouchLetterChanged {
        public void onTouchLetterChanged(String s);
    }

}
