package com.example.xkfeng.mycat.RecyclerDefine;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

public class EmptyRecyclerView extends RecyclerView {

    private View mEmptyView;
    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {

            Adapter adapter = getAdapter();
            if (adapter.getItemCount() == 0) {
                mEmptyView.setVisibility(VISIBLE);
                EmptyRecyclerView.this.setVisibility(GONE);
            } else {
                mEmptyView.setVisibility(GONE);
                EmptyRecyclerView.this.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {

            onChanged();
        }
    };


    public EmptyRecyclerView(Context context) {
        this(context, null);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setmEmptyView(View view){

        mEmptyView = view ;
//        if (view.getParent() != null){
//            ((ViewGroup)view.getParent()).removeView(view);
//        }
//        ((ViewGroup)this.getRootView()).addView(mEmptyView);

    }

    public void setAdapter(RecyclerView.Adapter adapter){
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mObserver);
        mObserver.onChanged();
    }


}
