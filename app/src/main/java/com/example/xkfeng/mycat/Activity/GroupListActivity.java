package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupListActivity extends BaseActivity {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.tv_intoAboutUs)
    TextView tvIntoAboutUs;
    @BindView(R.id.iv_intoAboutUs)
    ImageView ivIntoAboutUs;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.nlv_groupList)
    NestedListView nlvGroupList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list_layout);
        ButterKnife.bind(this);

        ITosast.showShort(this , "您暂未加入任何群组").show();
        setIndexTitleLayout();
    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void setIndexTitleLayout() {
        //沉浸式状态栏
        DensityUtil.fullScreen(this);
//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
    }

    @OnClick({R.id.tv_intoAboutUs,R.id.iv_intoAboutUs , R.id.tv_setBackText})
    public void onItemClick(View view){
        switch (view.getId()){
            case R.id.tv_setBackText :

                finish();
                break ;

            case R.id.tv_intoAboutUs :
            case R.id.iv_intoAboutUs :
                startActivity(new Intent(GroupListActivity.this , AboutActivity.class));
                break ;
        }
    }
}
