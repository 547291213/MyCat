package com.example.xkfeng.mycat.DrawableView.SendFile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_MusicFragment;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.util.List;

public class MusicAdapter extends BaseAdapter {

    private SendFile_MusicFragment musicFragment ;
    private List<FileItem> fileItems ;
    private UpdateSelectedStateListener updateSelectedStateListener ;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray() ;
    private Context mContext;
    private Activity mActivity;
    private static final int MAX_SEND_COUNT = 5 ;
    private static final double MAX_SEND_SIZE = 10485760.0 ;
    private LayoutInflater mInflater ;

    public MusicAdapter(SendFile_MusicFragment musicFragment , List<FileItem> fileItems){
        this.musicFragment = musicFragment ;
        this.fileItems = fileItems ;
        mContext = musicFragment.getContext() ;
        mActivity = musicFragment.getActivity() ;
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
            convertView = mInflater.inflate(R.layout.item_music , null , false) ;
            viewHolder.ll_audioItem = convertView.findViewById(R.id.ll_audioItem) ;
            viewHolder.cb_audio = convertView.findViewById(R.id.cb_audio) ;
            viewHolder.tv_audioTitle = convertView.findViewById(R.id.tv_audioTitle) ;
            viewHolder.tv_audioSize = convertView.findViewById(R.id.tv_audioSize) ;
            viewHolder.tv_audioDate = convertView.findViewById(R.id.tv_audioDate) ;
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final FileItem fileItem = fileItems.get(position) ;
        viewHolder.cb_audio.setChecked(mSelectMap.get(position));
        viewHolder.tv_audioTitle.setText(fileItem.getFileName());
        viewHolder.tv_audioSize.setText(fileItem.getFileSize());
        TimeUtil timeUtil = new TimeUtil(mContext ,Long.valueOf(fileItem.getDate()) * 1000) ;
        viewHolder.tv_audioDate.setText(timeUtil.getDetailTime());

        viewHolder.cb_audio.setOnClickListener(new OnItemClick(viewHolder , fileItem , position));
        viewHolder.ll_audioItem.setOnClickListener(new OnItemClick(viewHolder , fileItem , position));
        return convertView;
    }

    private class ViewHolder {
        LinearLayout ll_audioItem ;
        CheckBox cb_audio ;
        TextView tv_audioTitle ;
        TextView tv_audioSize ;
        TextView tv_audioDate ;
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
                case R.id.ll_audioItem:

                    if (viewHolder.cb_audio.isChecked()) {
                        viewHolder.cb_audio.setChecked(false);
                        mSelectMap.delete(pos);
                        updateSelectedStateListener.onUnselected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.musice);
                    } else {
                        if (musicFragment.getTotalCount() < 5) {
                            if (musicFragment.getTotalSize() + fileItem.getLongFileSize() < 10485760.0) {
                                viewHolder.cb_audio.setChecked(true);
                                mSelectMap.put(pos, true);
                                updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.musice);
                                addAnimation(viewHolder.cb_audio);
                            } else {
                                ITosast.showShort(musicFragment.getContext(), musicFragment.getString(R.string.sendfile_limit_filesize)).show();
                            }
                        } else {
                            ITosast.showShort(musicFragment.getContext(), musicFragment.getString(R.string.sendfile_limit_filecount)).show();
                        }
                    }
                    break;

                case R.id.cb_audio:

                    if (viewHolder.cb_audio.isChecked()) {
                        if (musicFragment.getTotalCount() < 5) {
                            if (musicFragment.getTotalSize() + fileItem.getLongFileSize() < 10485760.0) {
                                viewHolder.cb_audio.setChecked(true);
                                mSelectMap.put(pos, true);
                                updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.musice);
                                addAnimation(viewHolder.cb_audio);
                            } else {
                                viewHolder.cb_audio.setChecked(false);
                                ITosast.showShort(musicFragment.getContext(), musicFragment.getString(R.string.sendfile_limit_filesize)).show();
                            }
                        } else {
                            viewHolder.cb_audio.setChecked(false);
                            ITosast.showShort(musicFragment.getContext(), musicFragment.getString(R.string.sendfile_limit_filecount)).show();
                        }

                    } else {
                        mSelectMap.delete(pos);
                        updateSelectedStateListener.onUnselected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.musice);
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
