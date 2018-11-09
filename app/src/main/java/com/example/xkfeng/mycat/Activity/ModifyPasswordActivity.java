package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class ModifyPasswordActivity extends BaseActivity {


    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.ll_InputLayoutOrinPaw)
    LinearLayout llInputLayoutOrinPaw;
    @BindView(R.id.input_layout_psw)
    LinearLayout inputLayoutPsw;
    @BindView(R.id.ll_inputLayoutRepsw)
    LinearLayout llInputLayoutRepsw;
    @BindView(R.id.main_btn_login)
    TextView mainBtnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.modify_password_layout);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {

        /**
         * 设置标题栏相关
         */
        setIndexTitleLayout();
    }

    /**
     * 修改用户密码
     *
     * @param view TextView
     */
    @OnClick(R.id.main_btn_login)
    public void setMainBtnLoginClick(View view) {

        /**
         *  1 判空
         */
        if (TextUtils.isEmpty(((EditText) llInputLayoutOrinPaw.getChildAt(1)).getText().toString()) ||
                TextUtils.isEmpty(((EditText) inputLayoutPsw.getChildAt(1)).getText().toString()) ||
                TextUtils.isEmpty(((EditText) llInputLayoutRepsw.getChildAt(1)).getText().toString())) {

            ITosast.showShort(ModifyPasswordActivity.this, "输入不能为空")
                    .show();
            return;
        }
        /**
         * 2 判断两次输入的密码是否相同
         */
        else if (! ((EditText)inputLayoutPsw.getChildAt(1)).getText().toString()
                .equals(((EditText)llInputLayoutRepsw.getChildAt(1)).getText().toString())){
            ITosast.showShort(ModifyPasswordActivity.this , "两次输入的密码不一致")
                    .show();
            return ;
        }
        /**
         * 3 修改密码处理
         * 使用极光完成
         */
        else {
            JMessageClient.updateUserPassword(((EditText) llInputLayoutOrinPaw.getChildAt(1)).getText().toString(),
                    ((EditText) llInputLayoutRepsw.getChildAt(1)).getText().toString(),
                    new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                ITosast.showShort(ModifyPasswordActivity.this, "修改成功").show();
                                //返回
                                finish();
                            } else {
                                ITosast.showShort(ModifyPasswordActivity.this , "修改失败").show();
                            }

                        }
                    });
        }
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
        indexTitleLayout.setPadding(indexTitleLayout.getPaddingLeft(),
                indexTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                indexTitleLayout.getPaddingRight(),
                indexTitleLayout.getPaddingBottom());


//        设置点击事件监听
        indexTitleLayout.setTitleItemClickListener(new IndexTitleLayout.TitleItemClickListener() {
            @Override
            public void leftViewClick(View view) throws Exception {
                /**
                 * 退出当前Activity
                 */
                finish();
            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {

            }
        });
    }

}
