package com.example.xkfeng.mycat.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.FriendList;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.Model.Friend;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FriendFragment extends Fragment {


    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    Unbinder unbinder;
    @BindView(R.id.elv_friendList)
    ExpandableListView elv_FriendList;

    private View view;
    private Context mContext;
    private FriendList friendList;
    private List<List<Friend>> listList ;
    private String [] goupStrings ;
    private static final String TAG = "FriendFragment";
    private PopupMenuLayout popupMenuLayout_CONTENT ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friend_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

            /*
           设置顶部标题栏相关属性
         */
        setIndexTitleLayout();

        /**
         * 设置好友列表
         */
        setFriendList();
    }


    /**
     * 设置好友列表
     * 从服务器，数据库中获取好聊表信息
     */
    private void setFriendList()
    {

        listList = new ArrayList<>() ;
        goupStrings = new String[]{"我的好友" , "我的朋友" , "我的亲友" , "陌生人" , "黑名单"} ;

        List<Friend> list = new ArrayList<>() ;
        for (int i = 0 ; i < 5 ; i++)
        {
            Friend friend = new Friend();
            friend.setName("" + i);
            friend.setSinagure("Hello World" + i );
            friend.setImagesource(R.mipmap.log);
            list.add(friend) ;
            if (i>0)
            {
                listList.add(list) ;

            }
        }

        friendList = new FriendList(getContext() , listList , goupStrings) ;
        elv_FriendList.setAdapter(friendList);


        friendList.setFriendListItemOnLongClickListener(new FriendList.FriendListItemOnLongClickListener() {
            @Override
            public Boolean onItemLongClick(View view, int position, int id) {
                Toast.makeText(mContext, "Group" + position, Toast.LENGTH_SHORT).show();
                List<String> popupMenuList = new ArrayList<>() ;
                popupMenuList.add("分组管理");
                popupMenuLayout_CONTENT = new PopupMenuLayout(mContext ,popupMenuList , PopupMenuLayout.CONTENT_POPUP) ;
                /**
                 * 弹框前，需要得到PopupWindow的大小(也就是PopupWindow中contentView的大小)。
                 * 由于contentView还未绘制，这时候的width、height都是0。
                 * 因此需要通过measure测量出contentView的大小，才能进行计算。
                 */
                popupMenuLayout_CONTENT.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT.getWidth()) ,
                        DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT.getHeight())); ;
                popupMenuLayout_CONTENT.showAsDropDown(view ,
                        DensityUtil.getScreenWidth(getContext())/2 - popupMenuLayout_CONTENT.getContentView().getMeasuredWidth()/2
                        ,-view.getHeight()-popupMenuLayout_CONTENT.getContentView().getMeasuredHeight() );
                return true;
            }

            @Override
            public Boolean onItemClick(View view, int position, int id) {

                if (!elv_FriendList.isGroupExpanded(position))
                {
                    elv_FriendList.expandGroup(position)  ;
                }else {
                    elv_FriendList.collapseGroup(position) ;
                }

                return true;
            }
        });



        elv_FriendList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(mContext, "Group " + groupPosition + "  Child " + childPosition , Toast.LENGTH_SHORT).show();

                return false;
            }
        });



    }

    /**
     * 设置顶部标题栏相关属性
     */
    private void setIndexTitleLayout() {


//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        indexTitleLayout.setPadding(indexTitleLayout.getPaddingLeft(),
                indexTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(mContext),
                indexTitleLayout.getPaddingRight(),
                indexTitleLayout.getPaddingBottom());

//        设置点击事件监听
        indexTitleLayout.setTitleItemClickListener(new IndexTitleLayout.TitleItemClickListener() {
            @Override
            public void leftViewClick(View view) {
                Toast.makeText(mContext, "LeftClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {
                Toast.makeText(mContext, "RightClick", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
