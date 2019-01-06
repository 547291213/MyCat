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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.ViewImageActivity;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_DocumentFragment;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileItem;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class DocumentAdapter extends BaseAdapter {

    private List<FileItem> mList;
    private LayoutInflater mInflater;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private SendFile_DocumentFragment documentFragment;
    private UpdateSelectedStateListener updateSelectedStateListener;
    private Context mContext ;

    public DocumentAdapter(SendFile_DocumentFragment documentFragment, List<FileItem> mList) {
        this.documentFragment = documentFragment;
        this.mList = mList;
        mInflater = LayoutInflater.from(documentFragment.getContext());
        mContext = documentFragment.getContext() ;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        final FileItem fileItem = mList.get(pos);
        ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_document, null, false);
            viewHolder.ll_documentItem = convertView.findViewById(R.id.ll_documentItem);
            viewHolder.cb_documentCheckBox = convertView.findViewById(R.id.cb_documentCheckBox);
            viewHolder.tv_documentTitle = convertView.findViewById(R.id.tv_documentTitle);
            viewHolder.tv_documentSize = convertView.findViewById(R.id.tv_documentSize);
            viewHolder.tv_documentDate = convertView.findViewById(R.id.tv_documentDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path = fileItem.getFilePath();
        viewHolder.tv_documentTitle.setText(path.substring(path.lastIndexOf('/') + 1));
        TimeUtil timeUtil = new TimeUtil(mContext ,Long.valueOf(fileItem.getDate()) * 1000) ;
        viewHolder.tv_documentDate.setText(timeUtil.getDetailTime());

//        viewHolder.tv_documentDate.setText(TimeUtil.ms2date("yyyy-MM-dd" , Long.valueOf(fileItem.getDate()) * 1000));

        viewHolder.tv_documentSize.setText(fileItem.getFileSize());
        viewHolder.cb_documentCheckBox.setChecked(mSelectMap.get(pos));

        viewHolder.ll_documentItem.setOnClickListener(new OnItemClick(viewHolder, fileItem, pos));
        viewHolder.cb_documentCheckBox.setOnClickListener(new OnItemClick(viewHolder, fileItem, pos));

        return convertView;
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
                case R.id.ll_documentItem:

                    if (viewHolder.cb_documentCheckBox.isChecked()) {
                        viewHolder.cb_documentCheckBox.setChecked(false);
                        mSelectMap.delete(pos);
                        updateSelectedStateListener.onUnselected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.document);
                    } else {
                        if (documentFragment.getTotalCount() < 5) {
                            if (documentFragment.getTotalSize() + fileItem.getLongFileSize() < 10485760.0) {
                                viewHolder.cb_documentCheckBox.setChecked(true);
                                mSelectMap.put(pos, true);
                                updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.document);
                                addAnimation(viewHolder.cb_documentCheckBox);
                            } else {
                                ITosast.showShort(documentFragment.getContext(), documentFragment.getString(R.string.sendfile_limit_filesize)).show();
                            }
                        } else {
                            ITosast.showShort(documentFragment.getContext(), documentFragment.getString(R.string.sendfile_limit_filecount)).show();
                        }
                    }
                    break;

                case R.id.cb_documentCheckBox:

                    if (viewHolder.cb_documentCheckBox.isChecked()) {
                        if (documentFragment.getTotalCount() < 5) {
                            if (documentFragment.getTotalSize() + fileItem.getLongFileSize() < 10485760.0) {
                                viewHolder.cb_documentCheckBox.setChecked(true);
                                mSelectMap.put(pos, true);
                                updateSelectedStateListener.onSelected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.document);
                                addAnimation(viewHolder.cb_documentCheckBox);
                            } else {
                                viewHolder.cb_documentCheckBox.setChecked(false);
                                ITosast.showShort(documentFragment.getContext(), documentFragment.getString(R.string.sendfile_limit_filesize)).show();
                            }
                        } else {
                            viewHolder.cb_documentCheckBox.setChecked(false);
                            ITosast.showShort(documentFragment.getContext(), documentFragment.getString(R.string.sendfile_limit_filecount)).show();
                        }

                    } else {
                        mSelectMap.delete(pos);
                        updateSelectedStateListener.onUnselected(fileItem.getFilePath(), fileItem.getLongFileSize(), FileType.document);
                    }
                    break;
            }
        }
    }


    private class ViewHolder {

        LinearLayout ll_documentItem;
        CheckBox cb_documentCheckBox;
        TextView tv_documentTitle;
        TextView tv_documentSize;
        TextView tv_documentDate;
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
