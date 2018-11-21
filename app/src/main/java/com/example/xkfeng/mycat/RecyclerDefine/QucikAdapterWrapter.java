package com.example.xkfeng.mycat.RecyclerDefine;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class QucikAdapterWrapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int AD_POSITION = 0 ;
    enum ITEM_TYPE {
        AD,
        NORMAL
    }

    private QuickAdapter<T> quickAdapter;
    private View adView;


    public QucikAdapterWrapter(QuickAdapter<T> quickAdapter) {
        this.quickAdapter = quickAdapter;

    }


    @Override
    public int getItemViewType(int position) {

        if (position == AD_POSITION) {
            return ITEM_TYPE.AD.ordinal();
        } else {
            return ITEM_TYPE.NORMAL.ordinal();
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE.AD.ordinal()) {

            return new RecyclerView.ViewHolder(adView) {};

        } else {

            return quickAdapter.onCreateViewHolder(parent, viewType);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (position == AD_POSITION) {
            return;
        } else if (position < AD_POSITION){
            quickAdapter.onBindViewHolder((QuickAdapter.VH) holder, position);
        }else{
            quickAdapter.onBindViewHolder((QuickAdapter.VH) holder, position-1);
        }

    }

    @Override
    public int getItemCount() {

        if (quickAdapter.getItemCount() >= AD_POSITION) {
            return quickAdapter.getItemCount() + 1;
        } else {
            return quickAdapter.getItemCount();
        }

    }

    public void setAdView(View view) {
        this.adView = view;
    }

}
