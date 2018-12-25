package com.example.xkfeng.mycat.Activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.SqlHelper.RecordSQLDao;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;

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
    private QuickAdapter adapter;
    private RecordSQLDao recordSQLDao;
    private List<String> lists;
    public String userName;
    private boolean isSearchClick = false;
    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        ButterKnife.bind(this);

        userName = JMessageClient.getMyInfo().getUserName();
        init();

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

    @OnClick(R.id.bt_searchBtn)
    public void onSearchBtnClicked() {
        if (!TextUtils.isEmpty(etSearchEdit.getText().toString())) {
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
        } else {
            ITosast.showShort(this, "请输入数据").show();
        }
    }


}
