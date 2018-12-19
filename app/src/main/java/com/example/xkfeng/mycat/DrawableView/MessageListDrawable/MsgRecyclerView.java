package com.example.xkfeng.mycat.DrawableView.MessageListDrawable;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.xkfeng.mycat.DrawableView.ListSlideView;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;

import java.util.HashMap;
import java.util.Map;
/**
 * 当存在item处于打开状态的时候，
 * 1 屏蔽掉任意点击事件
 * 2 对打开菜单的额外选项提供点击
 * 2 对列表的其他任意部分点击都将变成让之前打开的列表关闭
 */

public class MsgRecyclerView extends RecyclerView {
    /**
     * 记录当前已经打开的侧滑菜单栏的数据
     */
    public static Map<QuickAdapter.VH, Integer> itemOpenCount = new HashMap<>();



    private View mEmptyView;


    public MsgRecyclerView(Context context) {
        this(context, null);
    }

    public MsgRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public void setmEmptyView(View view) {
        mEmptyView = view;

    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mObserver);
        mObserver.onChanged();
    }


    public Map<QuickAdapter.VH, Integer> getItemOpenCount() {
        return itemOpenCount;
    }

    public int getItemOpenCountSize() {
        return itemOpenCount.size();
    }

    public void addOpenItem(QuickAdapter.VH vh, int pos) {
        if (!itemOpenCount.containsKey(vh)) {
            itemOpenCount.put(vh, pos);
        }
    }

    public void addOpenItem(QuickAdapter.VH vh) {
        if (!itemOpenCount.containsKey(vh)) {
            itemOpenCount.put(vh, -2);
        }
    }

    public void clearOpenItem() {
        itemOpenCount.clear();
    }

    public void removeItem(QuickAdapter.VH vh) {
        if (itemOpenCount.containsKey(vh)) {
            itemOpenCount.remove(vh);
        }
    }


    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {

            Adapter adapter = getAdapter();
            if (adapter.getItemCount() == 0) {
                mEmptyView.setVisibility(VISIBLE);
                MsgRecyclerView.this.setVisibility(GONE);
            } else {
                mEmptyView.setVisibility(GONE);
                MsgRecyclerView.this.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            onChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            onChanged();
        }
    };



}
