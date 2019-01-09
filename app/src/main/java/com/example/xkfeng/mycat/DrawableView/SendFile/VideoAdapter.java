package com.example.xkfeng.mycat.DrawableView.SendFile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_VideoFragment;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.io.File;
import java.util.List;

public class VideoAdapter extends BaseAdapter {

    private SendFile_VideoFragment videoFragment;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private Context mContext;
    private LayoutInflater mInflater;
    private UpdateSelectedStateListener updateSelectedStateListener;
    private Activity mActivity;
    private static final int MAX_SEND_COUNT = 5;
    private static final double MAX_SEND_SIZE = 10485760.0;
    private List<FileItem> fileItems;

    public VideoAdapter(SendFile_VideoFragment videoFragment, List<FileItem> fileItems) {
        this.videoFragment = videoFragment;
        this.fileItems = fileItems;
        mContext = videoFragment.getContext();
        mActivity = videoFragment.getActivity();
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_video, null, false);
            viewHolder.cb_video = convertView.findViewById(R.id.cb_video);
            viewHolder.ll_videoItem = convertView.findViewById(R.id.ll_videoItem);
            viewHolder.iv_video = convertView.findViewById(R.id.iv_video);
            viewHolder.tv_videoTitle = convertView.findViewById(R.id.tv_videoTitle);
            viewHolder.tv_videoSize = convertView.findViewById(R.id.tv_videoSize);
            viewHolder.tv_videoDate = convertView.findViewById(R.id.tv_videoDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final FileItem item = fileItems.get(position);
        viewHolder.tv_videoTitle.setText(item.getFileName());
        viewHolder.tv_videoSize.setText(item.getFileSize());
        TimeUtil timeUtil = new TimeUtil(mContext, Long.valueOf(item.getDate()) * 1000);
        viewHolder.tv_videoDate.setText(timeUtil.getDetailTime());

        Bitmap bitmap = null  ;
        if (item.getVideoScaledDownPath() != null) {
             bitmap = BitmapFactory.decodeFile(item.getVideoScaledDownPath());
            if (bitmap != null) {
                Glide.with(mContext).load(bitmap).into(viewHolder.iv_video) ;
                viewHolder.iv_video.setTag(item.getVideoScaledDownPath());

            }else {
//                bitmap = getVideoThumbnail(item.getFilePath()) ;
                Glide.with(mContext).load(Uri.fromFile(new File(item.getFilePath()))).into(viewHolder.iv_video) ;

            }
        }

        viewHolder.ll_videoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.cb_video.isChecked()) {
                    viewHolder.cb_video.setChecked(false);
                    mSelectMap.delete(position);
                    updateSelectedStateListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.video);
                } else {
                    if (videoFragment.getTotalCount() < 5) {
                        if (videoFragment.getTotalSize() + item.getLongFileSize() < 10485760.0) {
                            viewHolder.cb_video.setChecked(true);
                            mSelectMap.put(position, true);
                            updateSelectedStateListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.video);
                            addAnimation(viewHolder.cb_video);
                        } else {

                            ITosast.showShort(mContext, videoFragment.getString(R.string.sendfile_limit_filesize)).show();
                        }
                    } else {
                        ITosast.showShort(mContext, videoFragment.getString(R.string.sendfile_limit_filecount)).show();

                    }
                }
            }
        });
        viewHolder.cb_video.setChecked(mSelectMap.get(position));

        viewHolder.cb_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.cb_video.isChecked()) {
                    if (videoFragment.getTotalCount() < 5) {
                        if (videoFragment.getTotalSize() + item.getLongFileSize() < 10485760.0) {
                            viewHolder.cb_video.setChecked(true);
                            mSelectMap.put(position, true);
                            updateSelectedStateListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.video);
                            addAnimation(viewHolder.cb_video);
                        } else {
                            viewHolder.cb_video.setChecked(false);
                            ITosast.showShort(mContext, videoFragment.getString(R.string.sendfile_limit_filesize)).show();

                        }
                    } else {
                        viewHolder.cb_video.setChecked(false);
                        ITosast.showShort(mContext, videoFragment.getString(R.string.sendfile_limit_filecount)).show();


                    }
                } else {
                    mSelectMap.delete(position);
                    updateSelectedStateListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.video);
                }
            }
        });


        return convertView;
    }

    private class ViewHolder {
        LinearLayout ll_videoItem;
        CheckBox cb_video;
        ImageView iv_video;
        TextView tv_videoTitle;
        TextView tv_videoSize;
        TextView tv_videoDate;
    }

    /**
     * 获取视频缩略图
     * 极为耗时，卡顿明显
     * 实在需要采用这种方法，请做好缓存【Lrucache】
     */
    @Deprecated
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap b = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
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
