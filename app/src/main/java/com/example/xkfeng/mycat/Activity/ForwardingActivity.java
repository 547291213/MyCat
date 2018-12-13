package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForwardingActivity extends BaseActivity {


    private static final String TAG = "ForwardingActivity";
    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.et_searchEdit)
    TextView etSearchEdit;
    @BindView(R.id.lv_recentMsgList)
    ListView lvRecentMsgList;
    @BindView(R.id.ll_groupLayout)
    LinearLayout llGroupLayout;

    private DisplayMetrics metrics;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forwarding_layout);
        ButterKnife.bind(this);


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        initView();

    }

    private void initView() {
        initIndexTitleLayout();
        initSearchEdit();
    }


    /**
     * 设置顶部标题栏相关属性
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

    private void initSearchEdit() {
        Drawable left = getResources().getDrawable(R.drawable.searcher);
        left.setBounds(metrics.widthPixels / 2 - DensityUtil.dip2px(this, 10 + 14 * 2), 0,
                50 + metrics.widthPixels / 2 - DensityUtil.dip2px(this, 10 + 14 * 2), 30);
//        Log.d(TAG, "setEtSearchEdit: " + metrics.widthPixels);
        etSearchEdit.setCompoundDrawablePadding(-left.getIntrinsicWidth() / 2 + 5);
        etSearchEdit.setCompoundDrawables(left, null, null, null);
        etSearchEdit.setAlpha((float) 0.6);
    }

    @OnClick({R.id.tv_setBackText, R.id.et_searchEdit ,R.id.ll_groupLayout})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setBackText:

                finish();
                break;

            case R.id.et_searchEdit:

                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ll_groupLayout :

                Intent intent = new Intent(ForwardingActivity.this , GroupListActivity.class) ;
                startActivity(intent);
                break ;
        }
    }


}
