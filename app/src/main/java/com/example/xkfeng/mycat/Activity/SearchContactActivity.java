package com.example.xkfeng.mycat.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.FriendList;
import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.Model.FilterFriendInfo;
import com.example.xkfeng.mycat.Model.FilterGroupInfo;
import com.example.xkfeng.mycat.Model.Friend;
import com.example.xkfeng.mycat.Model.SearchContact;
import com.example.xkfeng.mycat.MyApplication.MyApplication;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchContactActivity extends BaseActivity {


    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.tv_intoAboutUs)
    TextView tvIntoAboutUs;
    @BindView(R.id.iv_intoAboutUs)
    ImageView ivIntoAboutUs;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.sv_searchContact)
    SearchView svSearchContact;
    @BindView(R.id.nlv_friendLsit)
    NestedListView nlvFriendLsit;
    @BindView(R.id.ll_getMoreContact)
    LinearLayout llGetMoreContact;
    @BindView(R.id.ll_friendInfoLayout)
    LinearLayout llFriendInfoLayout;
    @BindView(R.id.nlv_groupLsit)
    NestedListView nlvGroupLsit;
    @BindView(R.id.ll_getMoreGroup)
    LinearLayout llGetMoreGroup;
    @BindView(R.id.ll_groupInfoLayout)
    LinearLayout llGroupInfoLayout;
    @BindView(R.id.tv_noNetWork)
    TextView tvNoNetWork;
    @BindView(R.id.ac_tv_search_no_results)
    TextView acTvSearchNoResults;

    private ThreadPoolExecutor mExecutor;
    private AsyncTask<String, Void, SearchContact> asyncTask;
    private List<UserInfo> allUser;
    private List<GroupInfo> allGroup;
    private String mFilterName;

    private static final int RequestCode_GETMORECONTACT = 100;


    /**
     * 需要区分是转发消息
     * 还是发送好友的名片
     */
    private boolean isSendFriendBusinessCard = false;
    private String fromBusinessCardFriendName;


    private static final String TAG = "SearchContactActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_contact_layout);
        ButterKnife.bind(this);

        isSendFriendBusinessCard = getIntent().getBooleanExtra("isSendBusiness", false);
        fromBusinessCardFriendName = getIntent().getStringExtra("friendName");
        /**
         * 设置线程池
         */
        mExecutor = new ThreadPoolExecutor(3, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());


        initView();
    }

    private void initView() {
        initIndexTitleLayout();
        initSearchView();
        initReceiver();
    }

    /**
     * 设置顶部标题栏相关属性
     * 标题内容颜色侵染到式状态栏处理
     */
    private void initIndexTitleLayout() {
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
        svSearchContact.setQueryHint("请输入要查找的联系人/群组信息");
        svSearchContact.onActionViewExpanded();
        svSearchContact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: text " + newText);
                mFilterName = newText;
                allUser = new ArrayList<>();
                allGroup = new ArrayList<>();
                asyncTask = new AsyncTask<String, Void, SearchContact>() {
                    @Override
                    protected SearchContact doInBackground(String... strings) {
                        return getFilteredElement(mFilterName);
                    }


                    @Override
                    protected void onPostExecute(SearchContact searchContact) {
                        FriendListAdapter friendListAdapter;
                        GroupListAdapter groupListAdapter;
                        //没有好友数据
                        if (searchContact.getFriendInfoList() == null || searchContact.getFriendInfoList().size() == 0) {
                            llFriendInfoLayout.setVisibility(View.GONE);
                        }
                        //存在好友数据
                        else {
                            llFriendInfoLayout.setVisibility(View.VISIBLE);
                            friendListAdapter = new FriendListAdapter(searchContact.getFriendInfoList());
                            //数据内容少于等于三条
                            if (searchContact.getFriendInfoList().size() <= 3) {
                                nlvFriendLsit.setAdapter(friendListAdapter);
                                llGetMoreContact.setVisibility(View.GONE);
                            }
                            //数据内容大于三条
                            else {
                                nlvFriendLsit.setAdapter(friendListAdapter);
                                llGetMoreContact.setVisibility(View.VISIBLE);
                                String fonts = "fonts/zhangcao.ttf";
                                Typeface typeface = Typeface.createFromAsset(getAssets(), fonts);
                                ((TextView) llGetMoreContact.getChildAt(0)).setTypeface(typeface);
                            }
                        }

                        //没有群组数据
                        if (searchContact.getGroupInfoList() == null || searchContact.getGroupInfoList().size() == 0) {
                            llGroupInfoLayout.setVisibility(View.GONE);
                            //如果既没有好友数据，又没有群组数据
                            if (((searchContact.getFriendInfoList() == null) || (searchContact.getFriendInfoList().size() == 0)) && !TextUtils.isEmpty(mFilterName)) {
                                acTvSearchNoResults.setVisibility(View.VISIBLE);
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                spannableStringBuilder.append("没有搜到");
                                SpannableStringBuilder colorFilterStr = new SpannableStringBuilder(mFilterName);
                                colorFilterStr.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), 0, mFilterName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                spannableStringBuilder.append(colorFilterStr);
                                spannableStringBuilder.append("相关的信息");
                                acTvSearchNoResults.setText(spannableStringBuilder);
                            } else {
                                acTvSearchNoResults.setVisibility(View.GONE);
                            }
                        }
                        //存在群组数据
                        else {
                            groupListAdapter = new GroupListAdapter(searchContact.getGroupInfoList());
                            llGroupInfoLayout.setVisibility(View.VISIBLE);
                            //如果存在的群组数据大于三条
                            if (searchContact.getGroupInfoList().size() > 3) {
                                nlvGroupLsit.setAdapter(groupListAdapter);
                                llGetMoreGroup.setVisibility(View.VISIBLE);

                            }
                            //存在的群组数据小于等于三条
                            else {
                                nlvGroupLsit.setAdapter(groupListAdapter);
                                llGetMoreGroup.setVisibility(View.GONE);

                            }
                        }

                    }
                };
                asyncTask.executeOnExecutor(mExecutor, mFilterName);
                return false;
            }

        });
    }

    private SearchContact getFilteredElement(String mFilterName) {
        SearchContact searchContact = new SearchContact();
        List<FilterFriendInfo> friendList = new ArrayList<>();
        List<FilterGroupInfo> groupList = new ArrayList<>();

        //如果数据为空或者为某特殊字符，直接置空返回
        if (TextUtils.isEmpty(mFilterName) || ",".equals(mFilterName)) {

            searchContact.setFriendInfoList(friendList);
            searchContact.setGroupInfoList(groupList);
            return searchContact;
        }

        //获取所有群组信息，
//        MyApplication.groupList

        //获取所有好友信息
        //当前已经存储了所有的好友信息，
        // 但是还需要获取所有会话列表中非好友的信息。

        List<Conversation> conversationList = JMessageClient.getConversationList();
        for (Conversation conversation : conversationList) {
            if (conversation.getType() == ConversationType.single) {
                UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
                if (!userInfo.isFriend()) {
                    allUser.add(userInfo);
                }
            }
        }

        allUser.addAll(MyApplication.friendList);

        /**
         * 遍历查询满足条件的用户
         */
        for (UserInfo userInfo : allUser) {
            String noteName = userInfo.getNotename();
            String nickName = userInfo.getNickname();
            String userName = userInfo.getUserName();
            if (!TextUtils.isEmpty(noteName) && noteName.contains(mFilterName)) {
                friendList.add(addData(noteName, userInfo));
                continue;
            }
            if (!TextUtils.isEmpty(nickName) && nickName.contains(mFilterName)) {
                friendList.add(addData(nickName, userInfo));
                continue;
            }
            if (!TextUtils.isEmpty(userName) && userName.contains(mFilterName)) {
                friendList.add(addData(noteName, userInfo));
                continue;
            }
        }

        searchContact.setFriendInfoList(friendList);
        searchContact.setGroupInfoList(groupList);
        searchContact.setNameFilter(mFilterName);

        return searchContact;


    }

    private FilterFriendInfo addData(String mFilterName, UserInfo userInfo) {
        FilterFriendInfo filterFriendInfo = new FilterFriendInfo();
        filterFriendInfo.setFileterName(mFilterName);
        filterFriendInfo.setUserInfo(userInfo);
        return filterFriendInfo;
    }

    @OnClick({R.id.tv_setBackText, R.id.iv_intoAboutUs, R.id.tv_intoAboutUs ,R.id.ll_getMoreContact , R.id.ll_getMoreGroup})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setBackText:
                finish();
                break;

            case R.id.iv_intoAboutUs:
            case R.id.tv_intoAboutUs:
                //拒绝无用的页面切换
                //startActivity(new Intent(SearchContactActivity.this, AboutActivity.class));
                break;

            case R.id.ll_getMoreContact:

                ITosast.showShort(this , "GET MORE CONTACT").show();
                Intent intent = new Intent();
                intent.setClass(SearchContactActivity.this, SearchMoreFriendActivity.class);
                startActivityForResult(intent, RequestCode_GETMORECONTACT);
                break;

            case R.id.ll_getMoreGroup:

                ITosast.showShort(this, "获取更多群组信息，暂未处理").show();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RequestCode_GETMORECONTACT:
                //如果在获取更多好友信息界面完成了消息转发或者名片发送
                if (resultCode == RESULT_OK  && data != null){

                }
                break;

            default:

                break;
        }
    }

    private class FriendListAdapter extends BaseAdapter {

        private String nameFilter;
        private List<FilterFriendInfo> mFilterList;

        public FriendListAdapter(List<FilterFriendInfo> list) {
            this.nameFilter = nameFilter;
            this.mFilterList = list;

        }

        @Override
        public int getCount() {
            return mFilterList.size() > 3 ? 3 : mFilterList.size();
        }

        @Override
        public Object getItem(int i) {
            return mFilterList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {

                holder = new ViewHolder();
                view = LayoutInflater.from(SearchContactActivity.this).inflate(R.layout.item_search, null, false);
                holder.circleImageView = view.findViewById(R.id.civ_headerImg);
                holder.textView = view.findViewById(R.id.tv_friendNameSingle);
                holder.searchItemLayout = view.findViewById(R.id.ll_searchItemLayout);
                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            UserInfo friendInfo = mFilterList.get(i).getUserInfo();
            if (friendInfo == null) {

                return view;
            }

            holder.textView.setText(friendInfo.getUserName());
            if (friendInfo.getAvatarFile() != null) {
                holder.circleImageView.setImageBitmap(BitmapFactory.decodeFile(friendInfo.getAvatarFile().toString()));
            } else {
                holder.circleImageView.setImageResource(R.mipmap.log);
            }
            holder.searchItemLayout.setOnClickListener(new OnItemClick(friendInfo.getUserName()));
            return view;
        }


        private class ViewHolder {
            private CircleImageView circleImageView;
            private TextView textView;
            private LinearLayout searchItemLayout;
        }
    }

    private class GroupListAdapter extends BaseAdapter {

        private List<FilterGroupInfo> groupInfoList;

        public GroupListAdapter(List<FilterGroupInfo> groupInfoList) {
            this.groupInfoList = groupInfoList;
        }


        @Override
        public int getCount() {
            return groupInfoList.size() > 3 ? 3 : groupInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return groupInfoList.get(i);
        }


        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder = null;

            if (view == null) {

                holder = new ViewHolder();
                view = LayoutInflater.from(SearchContactActivity.this).inflate(R.layout.item_search, null, false);
                holder.circleImageView = view.findViewById(R.id.civ_headerImg);
                holder.textView = view.findViewById(R.id.tv_friendNameSingle);
                holder.searchItemLayout = view.findViewById(R.id.ll_searchItemLayout);
                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            FilterGroupInfo groupInfo = groupInfoList.get(i);
            if (groupInfo == null) {
                return view;
            }

            holder.textView.setText(String.valueOf(groupInfo.getFilterName()));
            if (groupInfo.getGroupInfo().getAvatarFile() != null) {
                holder.circleImageView.setImageBitmap(BitmapFactory.decodeFile(groupInfo.getGroupInfo().getAvatarFile().toString()));
            } else {
                holder.circleImageView.setImageResource(R.mipmap.log);
            }

            holder.searchItemLayout.setOnClickListener(new OnItemClick(groupInfo.getGroupInfo().getGroupID()));
            return view;
        }

        private class ViewHolder {
            CircleImageView circleImageView;
            TextView textView;
            LinearLayout searchItemLayout;
        }
    }


    private class OnItemClick implements View.OnClickListener {

        /**
         * true : friend
         * false : group
         */
        private boolean isFrind;
        private String friendName;
        private long groupId;

        public OnItemClick(String friendName) {
            this.friendName = friendName;
            isFrind = true;
        }

        public OnItemClick(long groupId) {
            this.groupId = groupId;
            isFrind = true;
        }


        @Override
        public void onClick(View view) {
            //发送给好友
            if (isFrind) {
                //发送好友名片
                if (isSendFriendBusinessCard) {
                    ITosast.showShort(SearchContactActivity.this, "发送给好友，发送名片 " + friendName).show();
                }
                //转发消息
                else {
                    ITosast.showShort(SearchContactActivity.this, "发送给好友，转发消息 " + friendName).show();
                }
            }
            //发送给群组
            else {
                //发送好友名片
                if (isSendFriendBusinessCard) {
                    ITosast.showShort(SearchContactActivity.this, "发送给群组，发送好友名片").show();
                }
                //转发消息
                else {

                    ITosast.showShort(SearchContactActivity.this, "发送给群组 ，转发消息").show();
                }
            }

        }
    }


    /**
     * 网络状态监听，
     * 当处于没有网络的时候，
     * 让用户不能执行任何操作
     */
    private NetWorkReceiver netWorkReceiver;

    private void initReceiver() {
        netWorkReceiver = new NetWorkReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkReceiver, intentFilter);
    }

    private class NetWorkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager manager = (ConnectivityManager) SearchContactActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (manager.getActiveNetworkInfo() == null) {
                    tvNoNetWork.setVisibility(View.VISIBLE);
                    svSearchContact.setVisibility(View.GONE);
                } else {
                    tvNoNetWork.setVisibility(View.GONE);
                    svSearchContact.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
            netWorkReceiver = null;
        }

    }
}
