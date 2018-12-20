package com.example.xkfeng.mycat.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        initView();
    }

    private void initView() {
        initIndexTitleLayout();
        initSearchView();
        initReceiver() ;
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
                return false;
            }
        });

    }

    @OnClick({R.id.tv_setBackText , R.id.iv_intoAboutUs , R.id.tv_intoAboutUs})
    public void onItemClick(View view){
        switch (view.getId()){
            case R.id.tv_setBackText :
                finish();
                break ;

            case R.id.iv_intoAboutUs :
            case R.id.tv_intoAboutUs :
                startActivity(new Intent(SearchContactActivity.this  , AboutActivity.class));
                break ;
        }
    }


    /**
     * 网络状态监听，
     * 当处于没有网络的时候，
     * 让用户不能执行任何操作
     */
    private NetWorkReceiver netWorkReceiver ;
    private void initReceiver(){
        netWorkReceiver = new NetWorkReceiver() ;
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkReceiver , intentFilter) ;
    }
    private class NetWorkReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if ( intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
                ConnectivityManager manager = (ConnectivityManager) SearchContactActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if ( manager.getActiveNetworkInfo() == null ){
                    tvNoNetWork.setVisibility(View.VISIBLE);
                    svSearchContact.setVisibility(View.GONE);
                }else {
                    tvNoNetWork.setVisibility(View.GONE);
                    svSearchContact.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netWorkReceiver != null){
            unregisterReceiver(netWorkReceiver);
            netWorkReceiver = null ;
        }

    }
}
