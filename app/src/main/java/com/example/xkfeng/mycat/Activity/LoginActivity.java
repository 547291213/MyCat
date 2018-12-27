package com.example.xkfeng.mycat.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.arch.lifecycle.LifecycleOwner;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.DrawableView.BottomDialog;
import com.example.xkfeng.mycat.DrawableView.CustomDialog;
import com.example.xkfeng.mycat.DrawableView.DrawableTextEdit;
import com.example.xkfeng.mycat.Model.DaoMaster;
import com.example.xkfeng.mycat.Model.DaoSession;
import com.example.xkfeng.mycat.Model.UserDao;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.SqlHelper.LoginSQLDao;
import com.example.xkfeng.mycat.SqlHelper.LoginhistorySql;
import com.example.xkfeng.mycat.Util.ActivityController;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.RSAEncrypt;
import com.example.xkfeng.mycat.Util.UserAutoLoginHelper;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by initializing on 2018/10/5.
 */

public class LoginActivity extends BaseActivity {


    @BindView(R.id.tiet_userEdit)
    DrawableTextEdit tiet_UserEdit;
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
    ImageView iv_backImage;
    @BindView(R.id.tv_registerUserTv)
    TextView tv_registerUserTv;

    private Drawable drawableNotshow;
    private Drawable drawableShow;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String TAG = "LoginActivity";


    private LoginhistorySql sql;
    private Drawable drawableAccountHead;
    private SQLiteDatabase database;
    private List<String> lists;
    private List<Map<String, String>> mapList;
    private RecyclerView recyclerView;
    private QuickAdapter<String> quickAdapter;
    private PopupWindow window;

    private LoginSQLDao loginSQLDao;

    private UserAutoLoginHelper userAutoLoginHelper;

    private static int TEST_ID = 123456;

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
  完成初始化工作
 */
    private void init() {

        //申请权限
        checkPermission();


        //加载背景图片
        Glide.with(LoginActivity.this).load(getResources().getDrawable(R.drawable.background)).into(iv_backImage);



        //用户drawabkeTextEdit的功能实现
        userEditInit();

        //密码drawableTextEdit的功能实现
        passwordEditInit();

    }

    /*
       注册按钮点击事件功能实现
     */
    @OnClick(R.id.tv_registerUserTv)
    public void setTv_registerUserTv(View view) {
        //启动注册界面
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
        dialog.setBackground(Color.WHITE);
        dialog.setItem1TextColor(1 , Color.BLACK);
        dialog.setItem1TextColor(2 , Color.BLACK);
        dialog.setItem1TextColor(3 , Color.BLACK);
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


        if (TextUtils.isEmpty(tiet_UserEdit.getText()) || TextUtils.isEmpty(tiet_PasswordEdit.getText())) {
            ITosast.showShort(getApplicationContext(), "用户名或者密码不能为空")
                    .setTextColor(getResources().getColor(R.color.pale_white))
                    .setTextSize(18)
                    .show();

        } else {
            //正在登陆
            final CustomDialog dialog = new CustomDialog(this, R.style.CustomDialog);
            dialog.setText("正在登陆");
            dialog.show();

            /**
             * 调用极光进行登陆处理
             */
            JMessageClient.login(tiet_UserEdit.getText().toString(), tiet_PasswordEdit.getText().toString(), new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    //关闭加载对话框
                    dialog.dismiss();
                    switch (i) {
                        case 801003:
                            ITosast.showShort(getApplicationContext(), "用户名不存在").show();
                            break;
                        case 871301:
                            ITosast.showShort(getApplicationContext(), "密码格式错误").show();
                            break;
                        case 801004:
                            ITosast.showShort(getApplicationContext(), "密码错误").show();
                            break;
                        case 0:
                            ITosast.showShort(getApplicationContext(), "登陆成功").show();
                            if (database == null) {
                                new Exception("database is null object");
                            }

                            try {
//                                PublicKey publicKey = RSAEncrypt.getPublicKey(RSAEncrypt.PUBLIC_KEY);
//                                ContentValues contentValues = new ContentValues();
//                                contentValues.put(LoginhistorySql.ID, "" + tiet_UserEdit.getText().toString());
//                                contentValues.put(LoginhistorySql.PASSWORD, RSAEncrypt.encrypt("" + TEST_ID, publicKey));
//                                contentValues.put(LoginhistorySql.ISTOPLOGIN, "false");
//                                contentValues.put(LoginhistorySql.LASTUPDATETIME, simpleDateFormat.format(date));
//                                database.insertOrThrow(LoginhistorySql.TABLE_NAME, null, contentValues);
//                                tiet_PasswordEdit.getText().toString();
                                /**
                                 * 插入数据
                                 */
                                loginSQLDao.insertData(tiet_UserEdit.getText().toString() , tiet_PasswordEdit.getText().toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                /**
                                 * 账号信息本地存储
                                 * 下次系统自动登陆
                                 * 用户名直接录入
                                 * 密码用RSA加密
                                 */
                                PublicKey publicKey = RSAEncrypt.getPublicKey(RSAEncrypt.PUBLIC_KEY);
                                userAutoLoginHelper = UserAutoLoginHelper.getUserAutoLoginHelper(LoginActivity.this);
                                userAutoLoginHelper.setUserName(tiet_UserEdit.getText().toString());
                                userAutoLoginHelper.setUserPassword(RSAEncrypt.encrypt(tiet_PasswordEdit.getText().toString(), publicKey));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            Log.d(TAG, "gotResult: user:" + tiet_UserEdit.getText().toString() + "  password:"
//                                    + tiet_PasswordEdit.getText().toString());
//
//                            Log.d(TAG, "gotResult: getuser:" + userAutoLoginHelper.getUserName() + "  getpassword:"
//                                    + userAutoLoginHelper.getUserPassword());

                            /**
                             * 初始化用户数据
                             */
                            initUserInfo(tiet_UserEdit.getText().toString());

                            break;
                        default:
                            ITosast.showShort(getApplicationContext(), s).show();
                            break;
                    }

                }
            });

        }


    }

    private void initUserInfo(String id) {
        //初始化数据对话框
        final CustomDialog dialog = new CustomDialog(this, R.style.CustomDialog);
        dialog.setText("正在初始化");
        dialog.show();

        //拉取用户数据
        JMessageClient.getUserInfo(id, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {

                //关闭对话框
                dialog.dismiss();

                /**
                 *   当用户可以实现登陆时候
                 *   就将未登陆界面的所有Activity都给移除TASK
                 *
                 */
                ActivityController.finishAll();

                //启动到用户界面
                Intent intent = new Intent(LoginActivity.this
                        , IndexActivity.class);
                startActivity(intent);
            }
        });

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
       DrawableTextEdit 密码按钮功能实现
       * 密码可见与不可见的按钮点击的实现
       * drawableleft  drawableright点击事件的监听
       *
     */
    private void passwordEditInit() {

        tiet_PasswordEdit.setTYPE(0);
        // 设置DrawableRight的相关属性，以及有关事件的监听和处理
        preferences = getSharedPreferences("drawableshow", MODE_PRIVATE);

        drawableNotshow = getResources().getDrawable(R.drawable.not_show);
        drawableShow = getResources().getDrawable(R.drawable.show);
        drawableShow.setBounds(0, 0, 70, 50);
        drawableNotshow.setBounds(0, 0, 70, 50);
//        drawableShow.setBounds(0, 0, 100, 50);
//        drawableNotshow.setBounds(0, 0, 100, 50);


        final Drawable left = getResources().getDrawable(R.drawable.close_blue);
        left.setBounds(0, 0, 1, 1);


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
                    editor.commit();
                    //设置数据为密码的输入样式
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        tiet_PasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    //设置右边图片的样式
                    tiet_PasswordEdit.setCompoundDrawables(left, null, drawableNotshow, null);
                    //设置光标到尾部
                    tiet_PasswordEdit.setSelection(tiet_PasswordEdit.getText().toString().length());
                } else {
                    editor.putBoolean("show", true);
                    editor.commit();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        tiet_PasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                            tiet_PasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        tiet_PasswordEdit.setCompoundDrawables(left, null, drawableShow, null);
                        tiet_PasswordEdit.setSelection(tiet_PasswordEdit.getText().toString().length());

                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                            tiet_PasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
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

    /*
       DrawableTextEdit  用户按钮功能的实现
       Drawableleft  DrawableRight  事件  弹出式窗口实现的输入历史记录
     */
    private void userEditInit() {
        tiet_UserEdit.setTYPE(0);
        drawableAccountHead = getResources().getDrawable(R.drawable.cat_default);
        drawableAccountHead.setAlpha((int) (255 * 0.6));
        drawableAccountHead.setBounds(0, 0, 70, 50);

        final Drawable left = getResources().getDrawable(R.drawable.close_blue);
        left.setAlpha((int) (255 * 0.6));
        left.setBounds(0, 0, 1, 1);


        tiet_UserEdit.setDrawableListener(new DrawableTextEdit.DrawableListener() {
            @Override
            public void leftDrawableClick(Drawable drawable) {
                Toast.makeText(LoginActivity.this, "LeftClick", Toast.LENGTH_SHORT).show();

                tiet_UserEdit.setText("");


            }

            @Override
            public void rightDrawableClick(Drawable drawable) {
                Toast.makeText(LoginActivity.this, "RightClick", Toast.LENGTH_SHORT).show();

                if (recyclerView != null) {
                    // 创建PopupWindow对象，其中：
                    // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
                    // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
                    window = new PopupWindow(recyclerView, tiet_UserEdit.getWidth(), lists.size() * 80, true);
                    //     Log.d(TAG, "leftDrawableClick: " +  listMap.size() );
                    // 设置PopupWindow的背景
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    // 设置PopupWindow是否能响应外部点击事件
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        window.setOutsideTouchable(true);
                    }
                    // 设置PopupWindow是否能响应点击事件
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        window.setTouchable(true);
                    }
                    // 显示PopupWindow，其中：
                    // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
                    window.showAsDropDown(tiet_UserEdit, 0, 0);
                    // 或者也可以调用此方法显示PopupWindow，其中：
                    // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
                    // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
                    // window.showAtLocation(tiet_UserEdit, Gravity.LEFT, 0, 0);


                    //为了更好的用户体验
                    //在有列表数据的时候取消drawableLeft的显示
                    if (mapList.size() > 0) {
                        tiet_UserEdit.setCompoundDrawables(null, null, drawableAccountHead, null);

                    } else {
                        tiet_UserEdit.setCompoundDrawables(left, null, drawableAccountHead, null);
                    }
                }
            }
        });

        tiet_UserEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final Boolean flag = false;
                //如果不唯空对null和空指的双重判断。
                if (!TextUtils.isEmpty(charSequence)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        tiet_UserEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    tiet_UserEdit.setCompoundDrawables(left, null, drawableAccountHead, null);
                    tiet_UserEdit.setSelection(tiet_UserEdit.getText().toString().length());

                } else {
                    tiet_UserEdit.setCompoundDrawables(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    //Sql 和RecyclerView的初始化
    private void sqlRecyclerViewDataInit() {

        /**
         *   GreenDao数据库初始化
         */
//        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "user.db", null);
//        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
//        DaoSession daoSession = daoMaster.newSession();


        /*
            提供数据库通用DAO层设计来实现数据操作。
            在JAVA业务代码中只需要调用相关的方法即可
         */
        loginSQLDao = new LoginSQLDao(this);
        lists = new ArrayList<>();
        mapList = new ArrayList<>();
        lists = loginSQLDao.queryAllData(mapList);

//        //完成数据库的创建和调用
//        sql = new LoginhistorySql(this, "login.db", null, 1);
//        database = sql.getWritableDatabase();
//

//        /*
//          在子线程中获取数据库的数据
//         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Cursor cursor = null;
//                cursor = database.query(LoginhistorySql.TABLE_NAME,
//                        null, null, null, null, null, null);
//                cursor.moveToFirst();
//                while (cursor.moveToNext()) {
//                    Log.d(TAG, "onClick: " + cursor.getString(cursor.getColumnIndex("id")));
//                    Map<String, String> map = new HashMap<>();
//                    map.put("id", cursor.getString(cursor.getColumnIndex(LoginhistorySql.ID)));
//                    map.put("password", cursor.getString(cursor.getColumnIndex(LoginhistorySql.PASSWORD)));
//                    map.put("isTopLogin", cursor.getString(cursor.getColumnIndex(LoginhistorySql.ISTOPLOGIN)));
//                    map.put("lastUpdateTime", cursor.getString(cursor.getColumnIndex(LoginhistorySql.LASTUPDATETIME)));
//                    mapList.add(map);
//                    lists.add(cursor.getString(cursor.getColumnIndex("id")));
//
//                }
//                cursor.close();
//            }
//        }).start();

        /*
          利用万能适配器实现RecyclerView
         */
        recyclerView = new RecyclerView(this);
        quickAdapter = new QuickAdapter<String>(lists) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.login_account_item;
            }

            @Override
            public void convert(QuickAdapter.VH vh, final String data, final int position) {
                vh.setText(R.id.tv_loginHistoryAccount, data);
                vh.getView(R.id.tv_loginHistoryAccount).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(LoginActivity.this, "Click Text" + position, Toast.LENGTH_SHORT).show();
                        //设置输入栏的数据为用户所选择的数据
                        tiet_UserEdit.setText(data);
                        //设置密码输入栏的数据为数据库保存的数据
                        //密码字段需要经过RSA解密
                        try {
                            PrivateKey privateKey = RSAEncrypt.getPrivateKey(RSAEncrypt.PRIVATE_KEY);
                            tiet_PasswordEdit.setText(RSAEncrypt.decrypt(mapList.get(position).get(LoginhistorySql.PASSWORD), privateKey));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //如果PopupWindow不为空就关闭
                        if (window != null) {
                            window.dismiss();
                        }
                        Log.d(TAG, "onClick: password is " + mapList.get(position).get(LoginhistorySql.PASSWORD));
                    }
                });

                vh.getView(R.id.iv_loginHistoryClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /**
                         * 将数据从数据列表删除
                         */
                        mapList.remove(position);
                        lists.remove(position);
                        quickAdapter.notifyDataSetChanged();

                        /**
                         * 将数据从数据库中删除
                         */
                        loginSQLDao.deleteById(data);
                        Toast.makeText(LoginActivity.this, "Click CLose : " + data, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        recyclerView.setAdapter(quickAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }



    /*
         权限检查
       * 会根据是否具有某一权限进行判断，并且根据判断的结果做出不同的处理
       * 没有权限：申请权限
       * 有权限  ：直接处理~
     */
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
//                    .WRITE_EXTERNAL_STORAGE)) {
//                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
//            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
        } else {
            //sql和RecyclerView的数据初始化
            sqlRecyclerViewDataInit();
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //sql和RecyclerView的数据初始化
                    sqlRecyclerViewDataInit();
                    Toast.makeText(this, "已有权限", Toast.LENGTH_SHORT).show();
                } else {
                    //permission denied by user
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (lists != null){

            lists = null ;
        }
        if (mapList != null){
            mapList = null ;
        }

        System.gc();
    }
}
