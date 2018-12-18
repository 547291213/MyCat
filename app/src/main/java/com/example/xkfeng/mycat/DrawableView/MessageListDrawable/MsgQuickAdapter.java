package com.example.xkfeng.mycat.DrawableView.MessageListDrawable;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;

public class MsgQuickAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //网络状态界面
    private static long AD_POSITION = 0;

    enum ITEM_TYPE {
        AD,
        NORMAL
    }

    private QuickAdapter<T> quickAdapter;
    private View adView = null;


    public MsgQuickAdapter(QuickAdapter<T> quickAdapter) {
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

        if (viewType == ITEM_TYPE.AD.ordinal() && adView != null) {

            return new RecyclerView.ViewHolder(adView) {
            };

        } else {

            return quickAdapter.onCreateViewHolder(parent, viewType);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (adView==null){
            quickAdapter.onBindViewHolder((QuickAdapter.VH) holder, position);
        }else {
            if (position == AD_POSITION) {
                return;
            } else if (position < AD_POSITION) {
                quickAdapter.onBindViewHolder((QuickAdapter.VH) holder, position);
            } else {
                quickAdapter.onBindViewHolder((QuickAdapter.VH) holder, position - 1);
            }
        }



    }

    @Override
    public int getItemCount() {

        if (quickAdapter.getItemCount() >= AD_POSITION  && adView!= null) {
            return quickAdapter.getItemCount() + 1;
        } else {
            return quickAdapter.getItemCount();
        }

    }

    public void setHeaderView(View view) {
        this.adView = view;


    }

    public void setHeaderViewShow() {
        if (adView == null){
            return ;
        }
        ((LinearLayout)adView.findViewById(R.id.conv_list_header)).setVisibility(View.VISIBLE);

    }

    public void setHeaderViewHide(){
        if (adView == null){
            return ;
        }
        ((LinearLayout)adView.findViewById(R.id.conv_list_header)).setVisibility(View.GONE);

    }

}
