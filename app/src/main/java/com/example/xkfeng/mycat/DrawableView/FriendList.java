package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xkfeng.mycat.Model.Friend;
import com.example.xkfeng.mycat.R;

import java.util.List;

public class FriendList extends BaseExpandableListAdapter {


    private String[] groupList;
    private List<List<Friend>> lists;
    private Context mContext;
    private FriendListItemOnLongClickListener friendListItemOnLongClickListener;

    public FriendList(Context context, List<List<Friend>> lists, String[] groupList) {
        this.groupList = groupList;
        this.mContext = context;
        this.lists = lists;

    }

    @Override
    public int getGroupCount() {
        return lists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lists.get(groupPosition).size();
    }

    @Override
    public List<Friend> getGroup(int groupPosition) {
        return lists.get(groupPosition);
    }

    @Override
    public Friend getChild(int groupPosition, int childPosition) {
        return lists.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.friendlist_group, null);
        }
        GroupViewHolder holder = new GroupViewHolder();
        holder.groupTitleText = (TextView) convertView
                .findViewById(R.id.tv_groupName);
        holder.groupTitleText.setText(groupList[groupPosition]);
        holder.groupChildCountText = (TextView) convertView
                .findViewById(R.id.tv_groupCount);
        holder.groupChildCountText.setText("[" + lists.get(groupPosition).size() + "]");

        /**
         * Group View 长按事件用接口回调外传
         */
       convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (friendListItemOnLongClickListener != null) {
                    return friendListItemOnLongClickListener.onItemLongClick(v, groupPosition, v.getId());
                } else {
                    return false;
                }
            }
        });

        /**
         *  Group View 点击事件用接口回调外传
         */
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendListItemOnLongClickListener != null) {
                  friendListItemOnLongClickListener.onItemClick(v, groupPosition, v.getId());
                }
            }
        });

        /**
         * 因为自定义了点击事件，那么Android原生的水波纹点击动画也就没有，
         * 为了UI美观和用户体验
         * 用了自定义了Drawabel，用于对是否触摸Group View进行判断和颜色变换处理
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            convertView.setBackground(mContext.getResources().getDrawable(R.drawable.friend_group_drawable));
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.friendlist_child, null);
        }
        ChildViewHolder holder = new ChildViewHolder();
        holder.childImage = (ImageView) convertView.findViewById(R.id.ib_childImage);
        holder.childImage.setBackgroundResource(getChild(groupPosition,
                childPosition).getImagesource());
        holder.childNameText = (TextView) convertView.findViewById(R.id.tv_childNameText);
        holder.childNameText.setText(getChild(groupPosition, childPosition)
                .getName());
        holder.childSignatureText = (TextView) convertView.findViewById(R.id.tv_childSingnatureText);
        holder.childSignatureText.setText(getChild(groupPosition, childPosition)
                .getSinagure());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupViewHolder {

        TextView groupTitleText;
        TextView groupChildCountText;
    }

    private class ChildViewHolder {

        ImageView childImage;
        TextView childNameText;
        TextView childSignatureText;
    }

    /**
     * 接口回调的方法
     * @param friendListItemOnLongClickListener  接口对象
     */
    public void setFriendListItemOnLongClickListener(FriendListItemOnLongClickListener friendListItemOnLongClickListener) {
        this.friendListItemOnLongClickListener = friendListItemOnLongClickListener;
    }

    /**
     *  接口
     *  Group点击事件
     *  Group长按事件
     */
    public interface FriendListItemOnLongClickListener {

        public Boolean onItemLongClick(View view, int position, int id);

        public Boolean onItemClick(View view , int position , int id ) ;

    }

}
