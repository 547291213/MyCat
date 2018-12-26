package com.example.xkfeng.mycat.Activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.Model.FilterFriendInfo;
import com.example.xkfeng.mycat.MyApplication.MyApplication;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.SqlHelper.RecordSQLDao;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends BaseActivity {


    @BindView(R.id.bt_searchBtn)
    Button btSearchBtn;
    @BindView(R.id.et_searchEdit)
    EditText etSearchEdit;
    @BindView(R.id.rv_searchRecyclerView)
    RecyclerView rvSearchRecyclerView;
    @BindView(R.id.nlv_FriendAndGroupList)
    NestedListView nlvFriendAndGroupList;
    @BindView(R.id.tv_findNothing)
    TextView tvFindNothing;
    @BindView(R.id.ll_titleLayout)
    RelativeLayout llTitleLayout;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    private QuickAdapter adapter;
    private RecordSQLDao recordSQLDao;
    private List<String> lists;
    public String userName;
    private boolean isSearchClick = false;
    private static final String TAG = "SearchActivity";
    private Dialog loadingDialog;
    private boolean isLoaded = false;
    private FilterUserAdapter filterUserAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        ButterKnife.bind(this);

        userName = JMessageClient.getMyInfo().getUserName();
        initDataList();
        init();

    }


    private void initDataList() {

        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                switch (i) {
                    case 0:
                        MyApplication.friendList = list;
                        break;

                    default:
                        break;
                }
                isLoaded = true;
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        });

    }

    private void init() {
        //数据库Dao类初始化
        recordSQLDao = new RecordSQLDao(this);
        //列表初始化
        lists = new ArrayList<>();
        lists = recordSQLDao.queryData("", userName);
        adapter = new QuickAdapter<String>(lists) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.search_item;
            }

            @Override
            public void convert(VH vh, String data, final int position) {
                //设置显示的数据
                vh.setText(R.id.tv_searchItemView, lists.get(position));
                if (position == 0 && isSearchClick) {
                    addAnimation(vh.getView(R.id.iv_closeImageView));
                    addAnimation(vh.getView(R.id.tv_searchItemView));
                    isSearchClick = false;
                }

                vh.getView(R.id.tv_searchItemView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        etSearchEdit.setText(((TextView) view).getText().toString());
                    }
                });
                //设置点击close图片删除
                vh.getView(R.id.iv_closeImageView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.setList(lists);
                        recordSQLDao.delete(lists.get(position), userName);
                        lists.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        };
        adapter.setList(lists);
        //定义瀑布流管理器，第一个参数是列数，第二个是方向。
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvSearchRecyclerView.setLayoutManager(layoutManager);
        rvSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        rvSearchRecyclerView.setAdapter(adapter);
        rvSearchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //这行主要解决了当加载更多数据时，底部需要重绘，否则布局可能衔接不上。
                layoutManager.invalidateSpanAssignments();
            }
        });


    }

    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.start();
    }


    private class FilterUserAdapter extends BaseAdapter {


        private List<FilterFriendInfo> filterFriendInfos;

        public FilterUserAdapter(List<FilterFriendInfo> filterFriendInfos) {
            this.filterFriendInfos = filterFriendInfos;
        }

        @Override
        public int getCount() {
            return filterFriendInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return filterFriendInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_all_user, null, false);
                viewHolder.headerImg = convertView.findViewById(R.id.civ_headerImg);
                viewHolder.filterName = convertView.findViewById(R.id.tv_filterName);
                viewHolder.rl_allUserLayout = convertView.findViewById(R.id.rl_allUserLayout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final UserInfo userInfo = filterFriendInfos.get(i).getUserInfo();
            if (TextUtils.isEmpty(filterFriendInfos.get(i).getFileterName())) {
                if (!TextUtils.isEmpty(userInfo.getNotename())) {
                    viewHolder.filterName.setText(userInfo.getNotename());
                } else if (!TextUtils.isEmpty(userInfo.getNickname())) {
                    viewHolder.filterName.setText(userInfo.getNickname());
                } else {
                    viewHolder.filterName.setText(userInfo.getUserName());
                }
            } else {
                viewHolder.filterName.setText(filterFriendInfos.get(i).getFileterName());
            }

            if (userInfo.getAvatarFile() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(userInfo.getAvatarFile().toString());
                viewHolder.headerImg.setImageBitmap(bitmap);
            } else {
                viewHolder.headerImg.setImageResource(R.mipmap.log);
            }

            viewHolder.rl_allUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent() ;
                    intent.putExtra(StaticValueHelper.TARGET_ID , userInfo.getUserName()) ;
                    intent.putExtra(StaticValueHelper.TARGET_APP_KEY , userInfo.getAppKey()) ;
                    intent.putExtra(StaticValueHelper.IS_FRIEDN , userInfo.isFriend()) ;
                    intent.setClass(SearchActivity.this , FriendInfoActivity.class) ;
                    startActivity(intent);
                }
            });


            return convertView;
        }


        private class ViewHolder {
            CircleImageView headerImg;
            TextView filterName;
            RelativeLayout rl_allUserLayout;

        }
    }

    @OnClick(R.id.bt_searchBtn)
    public void onSearchBtnClicked() {
        if (!TextUtils.isEmpty(etSearchEdit.getText().toString())) {
            searchMatchUser(etSearchEdit.getText().toString());
            filterUserAdapter = new FilterUserAdapter(filterFriendInfos);
            nlvFriendAndGroupList.setAdapter(filterUserAdapter);

            if (filterFriendInfos == null || filterFriendInfos.size() == 0) {
                //做没有找到相关数据的处理
                tvContact.setVisibility(View.GONE);
                tvFindNothing.setVisibility(View.VISIBLE);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append("没有找到");
                SpannableStringBuilder colorFilter = new SpannableStringBuilder(etSearchEdit.getText().toString());
                colorFilter.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), 0, etSearchEdit.getText().toString().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(colorFilter);
                spannableStringBuilder.append("相关的信息");
                tvFindNothing.setText(spannableStringBuilder);
            } else {
                tvFindNothing.setVisibility(View.GONE);
                tvContact.setVisibility(View.VISIBLE);

            }
            processHistoryAndDatabase();


        } else {
            ITosast.showShort(this, "请输入数据").show();
        }
    }

    /**
     * 处理历史数据和数据库相关的操作
     */
    private void processHistoryAndDatabase() {
        boolean hasData = recordSQLDao.hasData(etSearchEdit.getText().toString().trim(), userName);
        if (!hasData) {
            recordSQLDao.insertData(etSearchEdit.getText().toString().trim(), userName);
            lists = recordSQLDao.queryData("", userName);
            adapter.setList(lists);
            adapter.notifyDataSetChanged();
            isSearchClick = true;

        }
        //清空输入栏的数据
        etSearchEdit.setText("");
    }

    private List<FilterFriendInfo> filterFriendInfos;

    private void searchMatchUser(String mFilter) {
        filterFriendInfos = new ArrayList<>();
        if (!isLoaded) {
            loadingDialog = DialogHelper.createLoadingDialog(this, "正在加载");
            loadingDialog.show();
        }
        while (!isLoaded) {
        }

        for (UserInfo userInfo : MyApplication.friendList) {
            String noteName = userInfo.getNotename();
            String nickName = userInfo.getNickname();
            String userName = userInfo.getUserName();
            if (!TextUtils.isEmpty(noteName) && noteName.contains(mFilter)) {
                filterFriendInfos.add(addData(noteName, userInfo));
                continue;
            }
            if (!TextUtils.isEmpty(nickName) && nickName.contains(mFilter)) {
                filterFriendInfos.add(addData(nickName, userInfo));
                continue;
            }
            if (!TextUtils.isEmpty(userName) && userName.contains(mFilter)) {
                filterFriendInfos.add(addData(userName, userInfo));
                continue;
            }
        }

    }

    private FilterFriendInfo addData(String mFilterName, UserInfo userInfo) {
        FilterFriendInfo filterFriendInfo = new FilterFriendInfo();
        filterFriendInfo.setFileterName(mFilterName);
        filterFriendInfo.setUserInfo(userInfo);
        return filterFriendInfo;
    }

}
