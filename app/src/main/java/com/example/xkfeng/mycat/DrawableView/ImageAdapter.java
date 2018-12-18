package com.example.xkfeng.mycat.DrawableView;

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
import com.example.xkfeng.mycat.Fragment.ImageFragment;
import com.example.xkfeng.mycat.Model.ImageFileItem;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;

import java.util.List;

public class ImageAdapter extends BaseAdapter {


    private ImageFragment imageFragment;
    /**
     * 记录当前已经选中的图片的数据
     * 记录当已经选中的图片的数目
     */
    private List<ImageFileItem> fileItemList;
    private LayoutInflater layoutInflater;
    private SparseBooleanArray selectedMap = new SparseBooleanArray();
    private Context mContext;
    private Activity mActivity;
    private UpdateSelectStateListener updateSelectStateListener;

    //一次最大发送的图片数目
    private final int MAX_SEND_IMG = 5;
    
    //当前已经选中的图片数目
//    private static int selectedMap.size() = 0;

    public ImageAdapter(ImageFragment imageFragment, List<ImageFileItem> fileItems) {
        this.imageFragment = imageFragment;
        this.fileItemList = fileItems;
        layoutInflater = LayoutInflater.from(imageFragment.getContext());
        mContext = imageFragment.getContext();
        mActivity = imageFragment.getActivity();
    }


    @Override
    public int getCount() {
        return fileItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return fileItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder viewHolder;
        final ImageFileItem imageFileItem = fileItemList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.img_item_picker_layout, null);
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
                intent.putExtra("imagePath", imageFileItem.getmFilePath());
                intent.setClass(mContext, PreviewPictureActivity.class);
                mContext.startActivity(intent);
                //设置Activity切换动画，水平滑动切换
                mActivity.overridePendingTransition(R.anim.trans_in_horizontal_slide, R.anim.trans_out_horizontal_slide);
            }
        });


        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.checkBox.isChecked()) {
                    if (selectedMap.size() < MAX_SEND_IMG) {
                        selectedMap.append(position, true);
                        viewHolder.checkBox.setChecked(true);
                        addAnimation(viewHolder.checkBox);
                        if (updateSelectStateListener != null) {
                            updateSelectStateListener.onSelected(imageFileItem.getmFilePath(), imageFileItem.getFileSize(), selectedMap.size());
                        }

                    } else {
                        viewHolder.checkBox.setChecked(false);
                        ITosast.showShort(mContext, "一次最多发送的图片不能超过五张").show();
                    }
                } else {
                    viewHolder.checkBox.setChecked(false);
                    selectedMap.delete(position);
                    if (updateSelectStateListener != null) {
                        updateSelectStateListener.onReleased(imageFileItem.getmFilePath(), imageFileItem.getFileSize(), selectedMap.size());
                    }
                }
            }
        });

        viewHolder.checkBox.setChecked(selectedMap.get(position));

        /**
         * 这里可能需要加载大量的图片 ，
         * 套用框架有待验证
         */
        Glide.with(mContext).load(imageFileItem.getmFilePath()).into(viewHolder.icon);


        return convertView;
    }


    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        animatorSet.start();
    }

    private class ViewHolder {
        CheckBox checkBox;
        ImageView icon;
        LinearLayout checkBoxLayout;
    }

    public void setUpdateSelectStateListener(UpdateSelectStateListener updateSelectStateListener) {
        this.updateSelectStateListener = updateSelectStateListener;
    }

    public interface UpdateSelectStateListener {
        public void onSelected(String path, String fileSize, int currentSelectCount);

        public void onReleased(String path, String fileSize, int currentSelectCount);
    }
}
