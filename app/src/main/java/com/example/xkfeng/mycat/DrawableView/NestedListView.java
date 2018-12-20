package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class NestedListView extends ListView implements View.OnTouchListener, AbsListView.OnScrollListener {


    private int listViewTouchEvent;
    private static final int MAX_ITEM_VISIABLE = 1000000;
    private ViewGroup.LayoutParams mParams;
    private static final String TAG = "NestedListView";

    public NestedListView(Context context) {
        this(context, null);
    }

    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listViewTouchEvent = MotionEvent.ACTION_MOVE;

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (getAdapter() != null && getAdapter().getCount() > MAX_ITEM_VISIABLE) {
            if (motionEvent.getAction() == listViewTouchEvent) {
                Log.d(TAG, "onTouch: 数据量过大，超过承载 ");
                scrollBy(0, -1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {


    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if (getAdapter() != null && getAdapter().getCount() > MAX_ITEM_VISIABLE) {
            if (MotionEvent.ACTION_MOVE == listViewTouchEvent) {
                Log.d(TAG, "onTouch: 数据量过大，超过承载 ");
                scrollBy(0, -1);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newHeight = 0;
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            newHeight = heightSize;
        } else {
            ListAdapter adapter = getAdapter();
            if (adapter != null && adapter.getCount() > 0) {
                int listPos = 0;
                for (; listPos < adapter.getCount() && listPos < MAX_ITEM_VISIABLE; listPos++) {
                    View item = adapter.getView(listPos, null, null);
                    if (item instanceof ViewGroup) {
                        item.setLayoutParams(mParams);
                    }
                    item.measure(widthMeasureSpec, heightMeasureSpec);
                    newHeight += item.getMeasuredHeight();
                }
                newHeight += getDividerHeight() * listPos;
            }

            if ((newHeight > heightSize) &&(heightMode == MeasureSpec.AT_MOST)) {
                newHeight = heightSize;
            }
        }

        setMeasuredDimension(widthMeasureSpec, newHeight);

    }
}
