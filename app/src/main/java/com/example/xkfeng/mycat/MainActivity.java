package com.example.xkfeng.mycat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableText.DrawableTextEdit;
import com.example.xkfeng.mycat.Model.LoginModel;
import com.example.xkfeng.mycat.QuickAdapter.QuickAdapter;
import com.example.xkfeng.mycat.SqlHelper.LoginhistorySql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tiet_userEdit)
    DrawableTextEdit tiet_UserEdit;
    @BindView(R.id.til_passwrod)
    TextInputLayout tilPasswrod;
    @BindView(R.id.bt_logBtn)
    Button bt_logBtn;
    private LoginhistorySql sql;
    private Drawable drawableAccountHead;
    private SQLiteDatabase database;
    private static final String TAG = "MainActivity";
    private List<String> lists;
    private List<Map<String, String>> mapList;
    private RecyclerView recyclerView;
    private QuickAdapter<String> quickAdapter;
    private PopupWindow window;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //完成数据库的创建和调用
        sql = new LoginhistorySql(this, "login.db", null, 1);
        database = sql.getWritableDatabase();

        lists = new ArrayList<>();
        mapList = new ArrayList<>();
        /*
          在子线程中获取数据库的数据
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = null;
                cursor = database.query(LoginhistorySql.TABLE_NAME,
                        null, null, null, null, null, null);
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    Log.d(TAG, "onClick: " + cursor.getString(cursor.getColumnIndex("id")));
                    Map<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(cursor.getColumnIndex(LoginhistorySql.ID)));
                    map.put("password", cursor.getString(cursor.getColumnIndex(LoginhistorySql.PASSWORD)));
                    map.put("isTopLogin", cursor.getString(cursor.getColumnIndex(LoginhistorySql.ISTOPLOGIN)));
                    map.put("lastUpdateTime", cursor.getString(cursor.getColumnIndex(LoginhistorySql.LASTUPDATETIME)));
                    mapList.add(map);
                    lists.add(cursor.getString(cursor.getColumnIndex("id")));

                }
                cursor.close();
            }
        }).start();


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
                        Toast.makeText(MainActivity.this, "Click Text" + position, Toast.LENGTH_SHORT).show();
                        //设置输入栏数据为用户所点击的数据
                        tiet_UserEdit.setText(data);
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

                        mapList.remove(position);
                        lists.remove(position);
                        quickAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Click CLose" + position, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        recyclerView.setAdapter(quickAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        bt_logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (database == null) {
                    new Exception("database is null object");
                }
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(System.currentTimeMillis());
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(LoginhistorySql.ID, "159753852");
//                contentValues.put(LoginhistorySql.PASSWORD, "Hello world");
//                contentValues.put(LoginhistorySql.ISTOPLOGIN, "false");
//                contentValues.put(LoginhistorySql.LASTUPDATETIME, simpleDateFormat.format(date));
//                database.insertOrThrow(LoginhistorySql.TABLE_NAME, null, contentValues);
//                tiet_PasswordEdit.getText().toString();


            }
        });


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
                Toast.makeText(MainActivity.this, "LeftClick", Toast.LENGTH_SHORT).show();

                tiet_UserEdit.setText("");


            }

            @Override
            public void rightDrawableClick(Drawable drawable) {
                Toast.makeText(MainActivity.this, "RightClick", Toast.LENGTH_SHORT).show();

                if (recyclerView != null) {
                    // 创建PopupWindow对象，其中：
                    // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
                    // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
                    window = new PopupWindow(recyclerView, tiet_UserEdit.getWidth(), lists.size() * 80, true);
                    //     Log.d(TAG, "leftDrawableClick: " +  listMap.size() );
                    // 设置PopupWindow的背景
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    // 设置PopupWindow是否能响应外部点击事件
                    window.setOutsideTouchable(true);
                    // 设置PopupWindow是否能响应点击事件
                    window.setTouchable(true);
                    // 显示PopupWindow，其中：
                    // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
                    window.showAsDropDown(tiet_UserEdit, 0, 0);
                    // 或者也可以调用此方法显示PopupWindow，其中：
                    // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
                    // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
                    // window.showAtLocation(tiet_UserEdit, Gravity.LEFT, 0, 0);


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
                    tiet_UserEdit.setInputType(InputType.TYPE_CLASS_TEXT);
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
}