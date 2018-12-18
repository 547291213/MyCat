package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.Fragment.ImageFragment;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewImageActivity extends BaseActivity implements ImageFragment.ImageStateChange {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.tv_intoAboutUs)
    TextView tvIntoAboutUs;
    @BindView(R.id.iv_intoAboutUs)
    ImageView ivIntoAboutUs;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;

    @BindView(R.id.bt_sendBtn)
    Button btSendBtn;
    @BindView(R.id.rl_bottomLayout)
    RelativeLayout rlBottomLayout;
    @BindView(R.id.tv_imgSizeText)
    TextView tvImgSizeText;
    @BindView(R.id.fl_imageFragment)
    FrameLayout flImageFragment;

    private ArrayList<String> selectedImgPathList ;

    private ImageFragment imageFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image_layout);
        ButterKnife.bind(this);

        selectedImgPathList = new ArrayList<>() ;
        initView();

    }

    private void initView() {

        initContentData();
        initIndexTitleLayout();

    }

    private void initContentData() {
        imageFragment = new ImageFragment() ;
        imageFragment.setImageStateChange(this);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_imageFragment, imageFragment);
        transaction.commit() ;

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

    @OnClick({R.id.tv_setBackText, R.id.tv_intoAboutUs, R.id.iv_intoAboutUs,
            R.id.bt_sendBtn})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setBackText:
                finish();
                break;

            case R.id.iv_intoAboutUs:
            case R.id.tv_intoAboutUs:
                startActivity(new Intent(ViewImageActivity.this, AboutActivity.class));
                break;

            case R.id.bt_sendBtn:
                if (selectedImgPathList.size() == 0){
                    ITosast.showShort(ViewImageActivity.this , "暂未选中图片").show();
                    return ;
                }
                /* 消息发送 */
                Intent intent = new Intent() ;
                intent.putStringArrayListExtra("imagePath" ,  selectedImgPathList) ;
                setResult(RESULT_OK ,intent );
                finish();

                break;
        }
    }


    @Override
    public void isSelected(String path, String fileSize, int currentSelectCount, boolean isSelected) {
        if (isSelected) {
            btSendBtn.setText("发送(" + currentSelectCount + ")");
            btSendBtn.setBackground(getResources().getDrawable(R.color.light_red));
            btSendBtn.setClickable(true);
            btSendBtn.setFocusable(true);
            selectedImgPathList.add(path) ;

        } else {
            if (currentSelectCount <= 0) {
                btSendBtn.setText("发送");
                btSendBtn.setBackground(getResources().getDrawable(R.color.lighter_gray));
            } else {
                btSendBtn.setText("发送(" + currentSelectCount + ")");
            }
            selectedImgPathList.remove(path) ;

        }
        tvImgSizeText.setText("已选" + currentSelectCount +"项" );

    }
}
