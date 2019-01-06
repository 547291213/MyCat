package com.example.xkfeng.mycat.DrawableView.SendFile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_OtherFragment;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.util.List;

public class OtherAdapter extends BaseAdapter {

    private Context mContext ;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray() ;
    private LayoutInflater mInflater ;
    private UpdateSelectedStateListener updateSelectedStateListener ;
    private SendFile_OtherFragment otherFragment ;
    private List<FileItem> fileItems ;

    public OtherAdapter(SendFile_OtherFragment otherFragment , List<FileItem> fileItems){
        this.otherFragment = otherFragment ;
        this.fileItems = fileItems ;
        mContext = otherFragment.getContext() ;
        mInflater = LayoutInflater.from(mContext) ;

    }

    @Override
    public int getCount() {
        return fileItems.size();
    }

    @Override
    public Object getItem(int i) {
        return fileItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null ;
        if (convertView == null){
            viewHolder = new ViewHolder() ;
            convertView = mInflater.inflate(R.layout.item_other , null ,false) ;
            viewHolder.ll_otherItem = convertView.findViewById(R.id.ll_otherItem) ;
            viewHolder.cb_other = convertView.findViewById(R.id.cb_other) ;
            viewHolder.iv_other = convertView.findViewById(R.id.iv_other) ;
            viewHolder.tv_otherTitle = convertView.findViewById(R.id.tv_otherTitle) ;
            viewHolder.tv_otherDate = convertView.findViewById(R.id.tv_otherDate) ;
            viewHolder.tv_otherSize = convertView.findViewById(R.id.tv_otherSize) ;
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final FileItem fileItem = fileItems.get(position) ;
        viewHolder.cb_other.setChecked(mSelectMap.get(position));
        String path = fileItem.getFilePath();
        viewHolder.tv_otherTitle.setText(path.substring(path.lastIndexOf('/') + 1));
        TimeUtil timeUtil = new TimeUtil(mContext ,Long.valueOf(fileItem.getDate()) * 1000) ;
        viewHolder.tv_otherDate.setText(timeUtil.getDetailTime());

//        viewHolder.tv_documentDate.setText(TimeUtil.ms2date("yyyy-MM-dd" , Long.valueOf(fileItem.getDate()) * 1000));

        viewHolder.tv_otherSize.setText(fileItem.getFileSize());

        viewHolder.ll_otherItem.setOnClickListener(new OnItemClick(viewHolder, fileItem, position));
        viewHolder.cb_other.setOnClickListener(new OnItemClick(viewHolder, fileItem, position));
        return convertView;
    }

    private class ViewHolder{
        LinearLayout ll_otherItem ;
        CheckBox cb_other ;
        ImageView iv_other ;
        TextView tv_otherTitle ;
        TextView tv_otherSize ;
        TextView tv_otherDate ;
    }

    private class OnItemClick implements View.OnClickListener {

        private ViewHolder viewHolder;
        private int pos;
        private FileItem fileItem;

        public OnItemClick(final ViewHolder viewHolder, final FileItem fileItem, int pos) {

            this.viewHolder = viewHolder;
            this.pos = pos;
            this.fileItem = fileItem;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_otherItem:

                    if (viewHolder.cb_other.isChecked()) {
                        viewHolder.cb_other.setChecked(false);
                        mSelectMap.delete(pos);
                        updateSelectedStateListener.onUnselected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.other);
                    } else {
                        if (otherFragment.getTotalCount() < 5) {
                            if (otherFragment.getTotalSize() + fileItem.getLongFileSize() < 10485760.0) {
                                viewHolder.cb_other.setChecked(true);
                                mSelectMap.put(pos, true);
                                updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.other);
                                addAnimation(viewHolder.cb_other);
                            } else {
                                ITosast.showShort(otherFragment.getContext(), otherFragment.getString(R.string.sendfile_limit_filesize)).show();
                            }
                        } else {
                            ITosast.showShort(otherFragment.getContext(), otherFragment.getString(R.string.sendfile_limit_filecount)).show();
                        }
                    }
                    break;

                case R.id.cb_other:

                    if (viewHolder.cb_other.isChecked()) {
                        if (otherFragment.getTotalCount() < 5) {
                            if (otherFragment.getTotalSize() + fileItem.getLongFileSize() < 10485760.0) {
                                viewHolder.cb_other.setChecked(true);
                                mSelectMap.put(pos, true);
                                updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.other);
                                addAnimation(viewHolder.cb_other);
                            } else {
                                viewHolder.cb_other.setChecked(false);
                                ITosast.showShort(otherFragment.getContext(), otherFragment.getString(R.string.sendfile_limit_filesize)).show();
                            }
                        } else {
                            viewHolder.cb_other.setChecked(false);
                            ITosast.showShort(otherFragment.getContext(), otherFragment.getString(R.string.sendfile_limit_filecount)).show();
                        }

                    } else {
                        mSelectMap.delete(pos);
                        updateSelectedStateListener.onUnselected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.other);
                    }
                    break;
            }
        }
    }
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();

    }

    public void setUpdateSelectedStateListener(UpdateSelectedStateListener updateSelectedStateListener){
        this.updateSelectedStateListener = updateSelectedStateListener ;
    }
}
