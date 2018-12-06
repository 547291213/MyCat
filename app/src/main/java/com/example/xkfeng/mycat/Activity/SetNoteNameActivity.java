package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class SetNoteNameActivity extends BaseActivity {

    private static final String TAG = "SetNoteNameActivity";
    @BindView(R.id.tv_setNoteTitleBack)
    TextView tvSetNoteTitleBack;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.et_setNoteNameEdit)
    EditText etSetNoteNameEdit;
    @BindView(R.id.bt_setNoteNameCompleteBtn)
    Button btSetNoteNameCompleteBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_notename_layout);
        ButterKnife.bind(this);

        setIndexTitleLayout();
        initData();
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

    private void initData() {
        etSetNoteNameEdit.setText(getIntent().getStringExtra(StaticValueHelper.NOTENAME));

    }

    @OnClick({R.id.tv_setNoteTitleBack, R.id.bt_setNoteNameCompleteBtn})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setNoteTitleBack:
                this.finish();
                break;

            case R.id.bt_setNoteNameCompleteBtn:
                JMessageClient.getUserInfo(getIntent().getStringExtra(StaticValueHelper.USER_NAME), new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        switch (i) {
                            case 0:
                                userInfo.updateNoteName(getIntent().getStringExtra(StaticValueHelper.NOTENAME), new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        switch (i) {
                                            case 0:
                                                Intent intent = new Intent();
                                                intent.putExtra(StaticValueHelper.NOTENAME, etSetNoteNameEdit.getText().toString());
                                                setResult(RESULT_OK, intent);
                                                ITosast.showShort(SetNoteNameActivity.this, "更新成功")
                                                        .show();
                                                finish();
                                                break;

                                            default:
                                                ITosast.showShort(SetNoteNameActivity.this, "更新失败")
                                                        .show();

//                                                HandleResponseCode.onHandle(SetNoteNameActivity.this , i);
                                                
                                                break;
                                        }
                                    }
                                });
                                break;

                            default:
                                ITosast.showShort(SetNoteNameActivity.this, "更新失败").show();
                                break;
                        }
                    }
                });
        }
    }

}