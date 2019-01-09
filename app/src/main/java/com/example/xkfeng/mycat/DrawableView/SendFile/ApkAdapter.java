package com.example.xkfeng.mycat.DrawableView.SendFile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.Activity.IsFirstActivity;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_ApkFragment;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.AppUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.io.File;
import java.util.List;

public class ApkAdapter extends BaseAdapter {

    private List<FileItem> fileItems;
    private SendFile_ApkFragment apkFragment;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private Context mContext;
    private LayoutInflater mInflater;
    private UpdateSelectedStateListener updateSelectedStateListener;

    public ApkAdapter(SendFile_ApkFragment apkFragment, List<FileItem> fileItems) {
        this.apkFragment = apkFragment;
        this.fileItems = fileItems;
        mContext = apkFragment.getContext();
        mInflater = LayoutInflater.from(mContext);


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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_apk, null, false);
            viewHolder.ll_apkItem = convertView.findViewById(R.id.ll_apkItem);
            viewHolder.cb_apk = convertView.findViewById(R.id.cb_apk);
            viewHolder.iv_apk = convertView.findViewById(R.id.iv_apk);
            viewHolder.tv_apkTitle = convertView.findViewById(R.id.tv_apkTitle);
            viewHolder.tv_apkDate = convertView.findViewById(R.id.tv_apkDate);
            viewHolder.tv_apkSize = convertView.findViewById(R.id.tv_apkSize);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final FileItem fileItem = fileItems.get(position);
        viewHolder.cb_apk.setChecked(mSelectMap.get(position));
        String path = fileItem.getFilePath();

        String label = null;
        Drawable drawable = null;
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo != null) {
            label = AppUtil.getAppSnippet(mContext.getApplicationContext(), packageInfo.applicationInfo, new File(path)).label.toString();
            drawable = AppUtil.getAppSnippet(mContext.getApplicationContext(), packageInfo.applicationInfo, new File(path)).icon;
        }
        if (!TextUtils.isEmpty(label)) {
            viewHolder.tv_apkTitle.setText(label);
        } else {
            viewHolder.tv_apkTitle.setText(path.substring(path.lastIndexOf('/') + 1));
        }
        if (drawable != null) {
            Glide.with(mContext).load(drawable).into(viewHolder.iv_apk) ;
//            viewHolder.iv_apk.setImageDrawable(drawable);

        } else {
            viewHolder.iv_apk.setImageResource(R.drawable.ic_apk_48);
        }
        TimeUtil timeUtil = new TimeUtil(mContext, Long.valueOf(fileItem.getDate()) * 1000);
        viewHolder.tv_apkDate.setText(timeUtil.getDetailTime());

//        viewHolder.tv_documentDate.setText(TimeUtil.ms2date("yyyy-MM-dd" , Long.valueOf(fileItem.getDate()) * 1000));


        viewHolder.tv_apkSize.setText(fileItem.getFileSize());

        viewHolder.ll_apkItem.setOnClickListener(new OnItemClick(viewHolder, fileItem, position));
        viewHolder.cb_apk.setOnClickListener(new OnItemClick(viewHolder, fileItem, position));
        return convertView;
    }

    private class ViewHolder {
        LinearLayout ll_apkItem;
        CheckBox cb_apk;
        ImageView iv_apk;
        TextView tv_apkTitle;
        TextView tv_apkSize;
        TextView tv_apkDate;
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
                case R.id.ll_apkItem:

                    if (viewHolder.cb_apk.isChecked()) {
                        viewHolder.cb_apk.setChecked(false);
                        mSelectMap.delete(pos);
                        updateSelectedStateListener.onUnselected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.apk);
                    } else {
                        if (apkFragment.getTotalCount() < 5) {
                            if (apkFragment.getTotalSize() + fileItem.getLongFileSize() < 10485760.0) {
                                viewHolder.cb_apk.setChecked(true);
                                mSelectMap.put(pos, true);
                                updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.apk);
                                addAnimation(viewHolder.cb_apk);
                            } else {
                                ITosast.showShort(apkFragment.getContext(), apkFragment.getString(R.string.sendfile_limit_filesize)).show();
                            }
                        } else {
                            ITosast.showShort(apkFragment.getContext(), apkFragment.getString(R.string.sendfile_limit_filecount)).show();
                        }
                    }
                    break;

                case R.id.cb_apk:

                    if (viewHolder.cb_apk.isChecked()) {
                        if (apkFragment.getTotalCount() < 5) {
                            if (apkFragment.getTotalSize() + fileItem.getLongFileSize() < 10485760.0) {
                                viewHolder.cb_apk.setChecked(true);
                                mSelectMap.put(pos, true);
                                updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.apk);
                                addAnimation(viewHolder.cb_apk);
                            } else {
                                viewHolder.cb_apk.setChecked(false);
                                ITosast.showShort(apkFragment.getContext(), apkFragment.getString(R.string.sendfile_limit_filesize)).show();
                            }
                        } else {
                            viewHolder.cb_apk.setChecked(false);
                            ITosast.showShort(apkFragment.getContext(), apkFragment.getString(R.string.sendfile_limit_filecount)).show();
                        }

                    } else {
                        mSelectMap.delete(pos);
                        updateSelectedStateListener.onUnselected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.apk);
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

    public void setUpdateSelectedStateListener(UpdateSelectedStateListener updateSelectedStateListener) {
        this.updateSelectedStateListener = updateSelectedStateListener;
    }
}
