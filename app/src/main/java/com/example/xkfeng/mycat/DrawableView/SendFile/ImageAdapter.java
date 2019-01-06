package com.example.xkfeng.mycat.DrawableView.SendFile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.Activity.PreviewPictureActivity;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_ImageFragment;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.Model.ImageFileItem;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private SendFile_ImageFragment imageFragment;
    private List<FileItem> fileItems;
    private LayoutInflater mInflater;
    private UpdateSelectedStateListener updateSelectedStateListener;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private Context mContext;
    private Activity mActivity;
    private static final int MAX_SEND_COUNT = 5 ;
    private static final double MAX_SEND_SIZE = 10485760.0 ;

    public ImageAdapter(SendFile_ImageFragment imageFragment, List<FileItem> fileItems) {
        this.imageFragment = imageFragment;
        this.fileItems = fileItems;

        mContext = imageFragment.getContext();
        mActivity = imageFragment.getActivity();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final FileItem fileItem = fileItems.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.img_item_picker_layout, null);
            viewHolder.icon = convertView.findViewById(R.id.iv_childImg);
            viewHolder.checkBoxLayout = convertView.findViewById(R.id.ll_checkBoxLayout);
            viewHolder.checkBox = convertView.findViewById(R.id.cb_checkBox);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //点击图片的时候，进入图片预览
        viewHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("imagePath", fileItem.getFilePath());
                intent.setClass(mContext, PreviewPictureActivity.class);
                mContext.startActivity(intent);
                //设置Activity切换动画，水平滑动切换
                mActivity.overridePendingTransition(R.anim.trans_in_horizontal_slide, R.anim.trans_out_horizontal_slide);
            }
        });

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.checkBox.isChecked()){
                    if (imageFragment.getTotalCount() < MAX_SEND_COUNT){
                        if (imageFragment.getTotalCount() + fileItem.getLongFileSize() < MAX_SEND_SIZE){
                            viewHolder.checkBox.setChecked(true);
                            mSelectMap.put(position, true);
                            updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.image);
                            addAnimation(viewHolder.checkBox);
                        }else {
                            viewHolder.checkBox.setChecked(false);
                            ITosast.showShort(mContext, imageFragment.getString(R.string.sendfile_limit_filesize)).show();
                        }
                    }else {
                        viewHolder.checkBox.setChecked(false);
                        ITosast.showShort(mContext, imageFragment.getString(R.string.sendfile_limit_filecount)).show();
                    }
                }else {
                    viewHolder.checkBox.setChecked(false);
                    updateSelectedStateListener.onUnselected(fileItem.getFilePath() , fileItem.getLongFileSize() , FileType.image);
                    mSelectMap.delete(position);
                }
            }
        });


        viewHolder.checkBox.setChecked(mSelectMap.get(position));

        /**
         * 这里可能需要加载大量的图片 ，
         * 套用框架有待验证
         */
        Glide.with(mContext).load(fileItem.getFilePath()).into(viewHolder.icon);
        return convertView;
    }


    private class ViewHolder {
        CheckBox checkBox;
        ImageView icon;
        LinearLayout checkBoxLayout;
    }

    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(200);
        set.start();
    }

    public void setUpdateSelectedStateListener(UpdateSelectedStateListener updateSelectedStateListener) {
        this.updateSelectedStateListener = updateSelectedStateListener;
    }
}
