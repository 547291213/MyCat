package com.example.xkfeng.mycat.Activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.Model.FilterFriendInfo;
import com.example.xkfeng.mycat.Model.User;
import com.example.xkfeng.mycat.MyApplication.MyApplication;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchMoreFriendActivity extends BaseActivity {


    @BindView(R.id.sv_searchContact)
    SearchView svSearchContact;
    @BindView(R.id.tv_findNothing)
    TextView tvFindNothing;
    @BindView(R.id.nlv_allFriendList)
    NestedListView nlvAllFriendList;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;


    private List<UserInfo> friendLlist;
    private FriendListAdapter friendListAdapter;
    private List<FilterFriendInfo> filterFriendInfos;
    private List<FilterFriendInfo> initFriendInfos;

    private UserInfo friendInfo ;
    private String businessCardName ;
    private boolean isSendFriendBusinessCard ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_more_contact_layout);
        ButterKnife.bind(this);

        isSendFriendBusinessCard =  getIntent().getBooleanExtra("isSendFriendBusinessCard" , false) ;
        businessCardName = getIntent().getStringExtra("businessCardName") ;

        initView();
    }

    private void initView() {

        initTitleLayout();
        initSearchView();
        initData();
    }


    private void initTitleLayout() {
        //沉浸式状态栏
        DensityUtil.fullScreen(this);
//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
    }

    private void initSearchView() {
        svSearchContact.setQueryHint("请输入要查找的联系人");
        svSearchContact.onActionViewExpanded();
        svSearchContact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    if (initFriendInfos != null) {
                        friendListAdapter = new FriendListAdapter(initFriendInfos);
                        nlvAllFriendList.setAdapter(friendListAdapter);
                    } else {
                        initFriendInfos = new ArrayList<>();
                        for (UserInfo userInfo : friendLlist) {
                            FilterFriendInfo filterFriendInfo = new FilterFriendInfo();
                            filterFriendInfo.setUserInfo(userInfo);
                            initFriendInfos.add(filterFriendInfo);
                        }
                        friendListAdapter = new FriendListAdapter(initFriendInfos);
                        nlvAllFriendList.setAdapter(friendListAdapter);
                    }
                } else {
                    filterFriendInfos = new ArrayList<>();
                    for (UserInfo userInfo : MyApplication.friendList) {
                        String noteName = userInfo.getNotename();
                        String nickName = userInfo.getNickname();
                        String userName = userInfo.getUserName();
                        if (!TextUtils.isEmpty(noteName) && noteName.contains(newText)) {
                            filterFriendInfos.add(addData(noteName, userInfo));
                            continue;
                        }
                        if (!TextUtils.isEmpty(nickName) && nickName.contains(newText)) {
                            filterFriendInfos.add(addData(nickName, userInfo));
                            continue;
                        }
                        if (!TextUtils.isEmpty(userName) && userName.contains(newText)) {
                            filterFriendInfos.add(addData(userName, userInfo));
                            continue;
                        }
                    }
                    friendListAdapter = new FriendListAdapter(filterFriendInfos);
                    nlvAllFriendList.setAdapter(friendListAdapter);
                }


                return false;
            }
        });
    }

    private FilterFriendInfo addData(String mFilterName, UserInfo userInfo) {
        FilterFriendInfo filterFriendInfo = new FilterFriendInfo();
        filterFriendInfo.setFileterName(mFilterName);
        filterFriendInfo.setUserInfo(userInfo);
        return filterFriendInfo;
    }


    /**
     * 加载，显示好友列表
     */
    private void initData() {
        friendLlist = MyApplication.friendList;
        if (friendLlist == null) {
            friendLlist = new ArrayList<>();
        }
        initFriendInfos = new ArrayList<>();
        for (UserInfo userInfo : friendLlist) {
            FilterFriendInfo filterFriendInfo = new FilterFriendInfo();
            filterFriendInfo.setUserInfo(userInfo);
            initFriendInfos.add(filterFriendInfo);
        }
        friendListAdapter = new FriendListAdapter(initFriendInfos);
        nlvAllFriendList.setAdapter(friendListAdapter);

    }

    private class FriendListAdapter extends BaseAdapter {

        private List<FilterFriendInfo> friendList;

        public FriendListAdapter(List<FilterFriendInfo> friendList) {
            this.friendList = friendList;
        }

        @Override
        public int getCount() {
            return friendList.size();
        }

        @Override
        public Object getItem(int i) {
            return friendList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(SearchMoreFriendActivity.this).inflate(R.layout.item_search, null, false);
                viewHolder.linearLayout = convertView.findViewById(R.id.ll_searchItemLayout);
                viewHolder.circleImageView = convertView.findViewById(R.id.civ_headerImg);
                viewHolder.textView = convertView.findViewById(R.id.tv_friendNameSingle);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserInfo userInfo = friendList.get(i).getUserInfo();
            if (TextUtils.isEmpty(friendList.get(i).getFileterName())) {
                if (!TextUtils.isEmpty(userInfo.getNotename())) {
                    viewHolder.textView.setText(userInfo.getNotename());
                } else if (!TextUtils.isEmpty(userInfo.getNickname())) {
                    viewHolder.textView.setText(userInfo.getNickname());
                } else {
                    viewHolder.textView.setText(userInfo.getUserName());
                }
            } else {
                viewHolder.textView.setText(friendList.get(i).getFileterName());
            }

            if (userInfo.getAvatarFile() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(userInfo.getAvatarFile().toString());
                viewHolder.circleImageView.setImageBitmap(bitmap);
            } else {
                viewHolder.circleImageView.setImageResource(R.mipmap.log);
            }


            viewHolder.linearLayout.setOnClickListener(new OnItemClick(userInfo.getUserName())) ;
            return convertView;
        }

        private class ViewHolder {
            LinearLayout linearLayout;
            CircleImageView circleImageView;
            TextView textView;
        }


        private class OnItemClick implements View.OnClickListener {

            private String userName ;
            public OnItemClick(String  userName ){
                this.userName = userName ;
            }
            @Override
            public void onClick(View view) {
                //发送好友名片
                if (isSendFriendBusinessCard){
                    Conversation conversation = JMessageClient.getSingleConversation(userName) ;
                    if(conversation == null){
                        conversation = Conversation.createSingleConversation(userName) ;
                    }
                    Dialog dialog = DialogHelper.createSendFriendBusinessCardDialog(SearchMoreFriendActivity.this , SearchMoreFriendActivity.this ,
                            userName , businessCardName , conversation) ;
                    dialog.show();

                }
                //转发消息
                else {
                    JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            switch (i) {
                                case 0:
                                    friendInfo = userInfo;
                                    Dialog dialog = DialogHelper.createForwardingDialog(SearchMoreFriendActivity.this, SearchMoreFriendActivity.this,
                                            userName, friendInfo, true);
                                    dialog.show();
                                    break;

                                default:
                                    ITosast.showShort(SearchMoreFriendActivity.this, "获取好友数据失败").show();
                                    return;

                            }

                        }
                    });

                }
            }
        }
    }


}
