package com.example.xkfeng.mycat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.Dialog.BottomDialog;
import com.example.xkfeng.mycat.DrawableText.DrawableTextEdit;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by initializing on 2018/10/5.
 */

public class LoginActivity extends BaseActivity {


    @BindView(R.id.tiet_userEdit)
    TextInputEditText tiet_UserEdit;
    @BindView(R.id.til_user)
    TextInputLayout til_User;
    @BindView(R.id.tiet_passwordEdit)
    DrawableTextEdit tiet_PasswordEdit;
    @BindView(R.id.til_passwrod)
    TextInputLayout til_Passwrod;
    @BindView(R.id.bt_loginBtn)
    Button bt_loginBtb;
    @BindView(R.id.tv_protocolTv)
    TextView tv_protocolTv;
    @BindView(R.id.tv_forgetPasswordTv)
    TextView tv_forgetPasswordTv;
    @BindView(R.id.rl_loginRelayout)
    RelativeLayout rl_loginRelayout;
    @BindView(R.id.iv_backImage)
    ImageView iv_backImage ;

    private Drawable drawableNotshow;
    private Drawable drawableShow;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        init();
    }

    /*
       密码找回按钮功能实现
     */
    @OnClick(R.id.tv_forgetPasswordTv)
    public void setTv_forgetPasswordTv(View view) {
        final String item1 = "用邮箱验证找回";
        final String item2 = "用手机短信找回";
        final String item3 = "取消";
        final BottomDialog dialog = new BottomDialog(this, item1, item2, item3);
        dialog.setItemClickListener(new BottomDialog.ItemClickListener() {
            @Override
            public void onItem1Click(View view) {
                Toast.makeText(LoginActivity.this, "用邮箱验证找回", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onItem2Click(View view) {

                Toast.makeText(LoginActivity.this, "用手机短信找回", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onItem3Click(View view) {

                Toast.makeText(LoginActivity.this, "取消", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /*
      登陆按钮功能实现
     */
    @OnClick(R.id.bt_loginBtn)
    public void setBt_loginBtb(View view) {

    }

    //服务协议按钮
    @OnClick(R.id.tv_protocolTv)
    public void setTv_protocolTv(View view) {
        startActivity(new Intent(LoginActivity.this, ProtocolActivity.class));

    }


    //大图压缩处理  （备用功能）
    private void loadImage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String fileName = "background.png";
                Bitmap image = null;
                AssetManager am = getResources().getAssets();
                try {
                    InputStream is = am.open(fileName);
                    image = BitmapFactory.decodeStream(is);
                    try {

                        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xkfeng/MyCat/";
                        File file = new File(dir + fileName);
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        image.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();


                        Log.d(TAG, "init: file " + file.length() / 1024 / 1024 + "Mb");
                        Luban.with(LoginActivity.this)
                                .load(file)
                                .ignoreBy(100)
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {
                                    }

                                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    public void onSuccess(final File file) {
                                        Log.d(TAG, "onSuccess: " + file.length() / 1024 / 1024 + "mb");

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                                                if (bitmap != null) {

                                                }

                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                })
                                .launch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
//        Log.d(TAG, "init: " + image.getByteCount());
    }

    /*
      完成初始化工作

     */
    private void init(){

        //加载图片
        Glide.with(LoginActivity.this).load(getResources().getDrawable(R.drawable.background)).into(iv_backImage) ;

        // 设置DrawableRight的相关属性，以及有关事件的监听和处理
        preferences = getSharedPreferences("drawableshow", MODE_PRIVATE);

        drawableNotshow = getResources().getDrawable(R.drawable.not_show);
        drawableShow = getResources().getDrawable(R.drawable.show);
        drawableShow.setBounds(0, 0, 100, 50);
        drawableNotshow.setBounds(0, 0, 100, 50);

        final Drawable left = getResources().getDrawable(R.drawable.close_blue) ;
        left.setBounds(0 , 0 , 1 , 1);



        tiet_PasswordEdit.setDrawableListener(new DrawableTextEdit.DrawableListener() {
            @Override
            public void leftDrawableClick(Drawable drawable) {

                tiet_PasswordEdit.setText("");
                tiet_PasswordEdit.setSelection(0);
             //   Toast.makeText(LoginActivity.this, "LeftClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void rightDrawableClick(Drawable drawable) {
                final Boolean flag = preferences.getBoolean("show", false);
                editor = getSharedPreferences("drawableshow", MODE_PRIVATE).edit();
                if (flag) {
                    //数据的更新
                    editor.putBoolean("show", false);
                    editor.apply();
                    //设置数据为密码的输入样式
                    tiet_PasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    //设置右边图片的样式
                    tiet_PasswordEdit.setCompoundDrawables(left, null, drawableNotshow, null);
                    //设置光标到尾部
                    tiet_PasswordEdit.setSelection(tiet_PasswordEdit.getText().toString().length());
                } else {
                    editor.putBoolean("show", true);
                    editor.apply();
                    tiet_PasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                    tiet_PasswordEdit.setCompoundDrawables(left, null, drawableShow, null);
                    tiet_PasswordEdit.setSelection(tiet_PasswordEdit.getText().toString().length());

                }


            }
        });

        tiet_PasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final Boolean flag = preferences.getBoolean("show", false);
                //如果不唯空对null和空指的双重判断。
                if (!TextUtils.isEmpty(charSequence)) {
                    if (flag) {
                        tiet_PasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                        tiet_PasswordEdit.setCompoundDrawables(left, null, drawableShow, null);
                        tiet_PasswordEdit.setSelection(tiet_PasswordEdit.getText().toString().length());

                    } else {

                        tiet_PasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        tiet_PasswordEdit.setCompoundDrawables(left, null, drawableNotshow, null);
                        tiet_PasswordEdit.setSelection(tiet_PasswordEdit.getText().toString().length());

                    }
                } else {
                    tiet_PasswordEdit.setCompoundDrawables(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
