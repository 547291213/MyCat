package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

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
    private static final String TAG = "RegisterActivity";

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

        //加载背景图片
        //Glide加载有短暂延迟，体验很差，所以放弃
//        Glide.with(this).load(R.drawable.side_nav_bar).into(ivBackImage) ;

    }

    /**
     *   注册按钮点击
     */
    @OnClick(R.id.bt_registerBtn)
    public void onViewClicked() {

        if (!tilPasswrod.getEditText().getText().toString().equals(tilRePasswrod.getEditText().getText().toString()))
        {
            ITosast.showShort(this , "请检测密码输入的合法性");
        }
        else {
            Log.d(TAG, "onViewClicked: " + tilUser.getEditText().toString());
            JMessageClient.register(tilUser.getEditText().getText().toString(), tilPasswrod.getEditText().getText().toString(), new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    switch (i){
                        case 0:
                            ITosast.showShort(RegisterActivity.this, "注册成功");
//                            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent() ;
                            intent.setClass(RegisterActivity.this , LoginActivity.class) ;
                            startActivity(intent);
                            break;
                        case 898001:
                            ITosast.showShort(RegisterActivity.this, "用户名已经存在");
                            break;
                        case 871301:
                            ITosast.showShort(RegisterActivity.this, "密码格式错误");
                            break;
                        case 871304:
                            ITosast.showShort(RegisterActivity.this, "密码错误");
                            break;
                        default:
                            Log.d(TAG, "gotResult: " + s);
                            ITosast.showShort(RegisterActivity.this, s);
                            break;

                    }
                }
            });
        }



    }
}
