package com.example.xkfeng.mycat.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.SqlHelper.RecordSQLDao;
import com.example.xkfeng.mycat.Util.ITosast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {


    @BindView(R.id.bt_searchBtn)
    Button btSearchBtn;
    @BindView(R.id.et_searchEdit)
    EditText etSearchEdit;
    @BindView(R.id.rv_searchRecyclerView)
    RecyclerView rvSearchRecyclerView;
    @BindView(R.id.tv_deleteAllHistorySearch)
    TextView tvDeleteAllHistorySearch;

    private QuickAdapter adapter;
    private RecordSQLDao recordSQLDao;
    private List<String> lists;
    public static final String USER_NAME = "admin";
    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        ButterKnife.bind(this);

        init();

    }

    private void init() {

        //数据库Dao类初始化
        recordSQLDao = new RecordSQLDao(this);
        //列表初始化
        lists = new ArrayList<>();
//        lists.add("Hello Test") ;
        etSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    //更新数据
                    lists = recordSQLDao.queryData("", USER_NAME);
                    adapter.setList(lists);
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "onTextChanged: " + lists.size());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        adapter = new QuickAdapter<String>(lists) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.search_item;
            }

            @Override
            public void convert(VH vh, String data, final int position) {
                //设置显示的数据
                vh.setText(R.id.tv_searchItemView , lists.get(position));
//                Log.d(TAG, "convert: data is " + data);
                //设置点击close图片删除
                vh.getView(R.id.iv_closeImageView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                          数据库删除
                          列表更新
                          同步到RecyclerView
                         */
                        recordSQLDao.delete(lists.get(position) ,USER_NAME) ;

                        adapter.setList(lists);
                        lists.remove(position) ;
                        adapter.notifyDataSetChanged();

                    }
                });
            }

        };

        rvSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSearchRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        rvSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        rvSearchRecyclerView.setAdapter(adapter);


        ;

    }

    @OnClick(R.id.bt_searchBtn)
    public void onSearchBtnClicked() {
        if (!TextUtils.isEmpty(etSearchEdit.getText().toString())) {
            boolean hasData = recordSQLDao.hasData(etSearchEdit.getText().toString().trim(), USER_NAME);
            if (!hasData) {
                recordSQLDao.insertData(etSearchEdit.getText().toString().trim(), USER_NAME);
            } else {
               // ITosast.showShort(this , "内容已经存在");
            }
            //更新历史数据
            lists = recordSQLDao.queryData("" ,USER_NAME) ;
            adapter.setList(lists);
            adapter.notifyDataSetChanged();
            //清空输入栏的数据
            etSearchEdit.setText("");

        }else {
            ITosast.showShort(this , "请输入数据").show();
        }
    }

    @OnClick(R.id.tv_deleteAllHistorySearch)
    public void onDeleteAllHistoryBtnClicked() {
        //清除数据库所有数据
        recordSQLDao.deleteAllData();
        //清除列表所有数据并且更新到RecyclerView
        lists.clear();
        adapter.notifyDataSetChanged();
    }

}
