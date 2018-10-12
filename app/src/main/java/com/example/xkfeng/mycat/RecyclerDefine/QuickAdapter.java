package com.example.xkfeng.mycat.RecyclerDefine;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by initializing on 2018/10/7.
 */

public abstract class QuickAdapter<T> extends RecyclerView.Adapter<QuickAdapter.VH> {


    private List<T> list ;

    public QuickAdapter(List<T> list)
    {
        this.list = list ;
    }

    public abstract int getLayoutId(int viewType) ;
    public abstract void convert(VH vh ,T data , int position) ;

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return VH.get(parent,getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        convert(holder , list.get(position) ,position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class VH extends RecyclerView.ViewHolder
    {
        private SparseArray<View> sparseArray ;
        private View convertView ;

        public VH(View itemView) {
            super(itemView);
            convertView = itemView ;
            sparseArray =  new SparseArray<>() ;
        }

        public static VH get(ViewGroup parent , int layoutId)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId , parent ,false) ;
            return new VH(view) ;
        }

        public <T extends View> T getView(int id)
        {
            View v = sparseArray.get(id);
            if (v == null)
            {
                v = convertView.findViewById(id) ;
                sparseArray.put(id,v);
            }
            return (T)v;
        }

        public void setText(int id ,String string)
        {
            TextView textView = getView(id) ;
            textView.setText(string);
        }

        public void setImage(int id , Drawable drawable)
        {
            ImageView imageView = getView(id) ;
            imageView.setImageDrawable(drawable);
        }
    }
}
