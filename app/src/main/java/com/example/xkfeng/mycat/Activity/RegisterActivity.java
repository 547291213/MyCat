package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.iv_backImage)
    ImageView ivBackImage;
    @BindView(R.id.til_user)
    TextInputLayout tilUser;
    @BindView(R.id.til_passwrod)
    TextInputLayout tilPasswrod;
    @BindView(R.id.til_rePasswrod)
    TextInputLayout tilRePasswrod;
    @BindView(R.id.bt_registerBtn)
    Button btRegisterBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.register_layout);
        ButterKnife.bind(this);

        initView();


    }

    private void initView(){

        //记载背景图片
        Glide.with(this).load(R.drawable.side_nav_bar).into(ivBackImage) ;

    }

    /**
     *   注册按钮点击
     */
    @OnClick(R.id.bt_registerBtn)
    public void onViewClicked() {

        /**
         * 1  对用户名的合法性进行判断
         */

        /**
         * 2  对用户名是否存在进行判断
         */

        /**
         * 3  对两次输入密码是否一致进行判断
         */

        /**
         * 4  对密码的合法性进行判断
         */

        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent() ;
        intent.setClass(RegisterActivity.this , LoginActivity.class) ;
        startActivity(intent);

    }
}
