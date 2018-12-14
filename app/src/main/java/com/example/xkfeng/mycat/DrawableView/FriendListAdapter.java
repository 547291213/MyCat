package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.xkfeng.mycat.Model.FriendInfo;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.PinyinUtil;

import java.util.List;

import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.internal.operators.observable.ObservableOnErrorNext;

public class FriendListAdapter extends BaseAdapter implements SectionIndexer {

    private List<FriendInfo> friendInfos;
    private Context mContext;
    private OnItemClickListener onItemClickListener;


    public FriendListAdapter(Context context, List<FriendInfo> list) {
        this.mContext = context;
        this.friendInfos = list;

    }

    public void addData(FriendInfo friendInfo) {
        friendInfos.add(friendInfo);
    }

    public void notifyData(List<FriendInfo> friendInfos) {
        this.friendInfos = friendInfos;
        notifyDataSetChanged();
    }

    public FriendInfo getData(int position) {
        if (position >= 0 && position < friendInfos.size()) {
            return friendInfos.get(position);
        }

        return null;
    }

    public int getDataCount() {
        return friendInfos.size();
    }

    @Override
    public int getCount() {
        return friendInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return friendInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.friend_list_content_item, null);
            viewHolder.iv_friendInfoImg = view.findViewById(R.id.iv_friendInfoImg);
            viewHolder.tv_friendInfoText = view.findViewById(R.id.tv_friendInfoText);
            viewHolder.tv_letterText = view.findViewById(R.id.tv_letterText);
            viewHolder.ll_friendInfoLayout = view.findViewById(R.id.ll_friendInfoLayout);
            viewHolder.tv_friendSinatureText = view.findViewById(R.id.tv_friendSinatureText);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        int section = getSectionForPosition(i);
        int height = 0 ;
        if (getPositionForSection(section) == i) {
            viewHolder.tv_letterText.setVisibility(View.VISIBLE);
            viewHolder.tv_letterText.setText(friendInfos.get(i).getFirstLetter());
            height = viewHolder.tv_letterText.getHeight() ;
        } else {
            viewHolder.tv_letterText.setVisibility(View.GONE);
        }

        if (friendInfos.get(i).getUserInfo().getAvatarFile() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(friendInfos.get(i).getUserInfo().getAvatarFile().toString());
            viewHolder.iv_friendInfoImg.setImageBitmap(bitmap);
        } else {
            viewHolder.iv_friendInfoImg.setImageResource(R.mipmap.log);
        }

        viewHolder.tv_friendInfoText.setText(friendInfos.get(i).getTitleName());

        if (!TextUtils.isEmpty(friendInfos.get(i).getUserInfo().getSignature())) {
            viewHolder.tv_friendSinatureText.setText(friendInfos.get(i).getUserInfo().getSignature());
        } else {
            viewHolder.tv_friendSinatureText.setText("该好友尚未设置个性签名 ， ~~~~~~~~~~~~");
        }


        viewHolder.ll_friendInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, i, friendInfos.get(i).getUserInfo());
                }
            }
        });

        final int letterViewHeight = height ;
        viewHolder.ll_friendInfoLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(view, i, friendInfos.get(i).getUserInfo() , letterViewHeight);
                }
                return true;
            }
        });
        return view;
    }

    public final static class ViewHolder {
        LinearLayout ll_friendInfoLayout;
        TextView tv_letterText;
        CircleImageView iv_friendInfoImg;
        TextView tv_friendInfoText;
        TextView tv_friendSinatureText;

    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int i) {
        for (int j = 0; j < friendInfos.size(); j++) {
            if (friendInfos.get(j).getFirstLetter().charAt(0) == i) {
                return j;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int i) {
        return friendInfos.get(i).getFirstLetter().charAt(0);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position, UserInfo targetUserInfo);

        public void onItemLongClick(View view, int position, UserInfo targetUserInfo ,int letterViewHeight);
    }
}
