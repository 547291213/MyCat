package com.example.xkfeng.mycat.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.IndexBottomLayout;
import com.example.xkfeng.mycat.DrawableView.RedPointViewHelper;
import com.example.xkfeng.mycat.Fragment.DynamicFragment;
import com.example.xkfeng.mycat.Fragment.FriendFragment;
import com.example.xkfeng.mycat.Fragment.MessageFragment;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ActivityController;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by initializing on 2018/10/7.
 */

public class IndexActivity extends BaseActivity {

    @BindView(R.id.ib_indexBottomMessage)
    IndexBottomLayout ibIndexBottomMessage;
    @BindView(R.id.ib_indexBottomFriend)
    IndexBottomLayout ibIndexBottomFriend;
    @BindView(R.id.ib_indexBottomDynamic)
    IndexBottomLayout ibIndexBottomDynamic;


    private static final String TAG = "IndexActivity";
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.rl_indexMainLayout)
    RelativeLayout rlIndexMainLayout;

    private DisplayMetrics metrics;

    private FrameLayout frameLayout;
    private MessageFragment messageFragment;
    private FriendFragment friendFragment;
    private DynamicFragment dynamicFragment;
    private FragmentManager fragmentManager;

    private View redPointMessage;
    private View redPointFriend;
    private View redPointDynamic;

    private static final String PROJECT_GITHUB = "https://github.com/547291213/MyCat";
    private static final String PROJECT_CSDN = "https://blog.csdn.net/qq_29989087/article/details/82962296";

    //用户最近一次点击Back的事件
    //用于实现在相近时间内两次点击Back退出程序
    private static long lastExitTime = 0;

    //两次点击退出的时间间隔
    private static final int MAX_EXIT_TIME = 2000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            //设置全屏和状态栏透明
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//
//            getWindow().setStatusBarColor(Color.RED);
//        }
        setContentView(R.layout.index_layout);
        ButterKnife.bind(this);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //沉浸式View
        DensityUtil.fullScreen(this);

        //抽屉设置
        setNavView();

        //初始化布局
        initView();


    }

    /**
     * 设置抽屉属性
     * 一 点击事件处理
     * 二 挤压抽屉实现
     */
    private void setNavView() {

        navView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IndexActivity.this, "Image", Toast.LENGTH_SHORT).show();
            }
        });

        //点击事件处理
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_github:

                        /**
                         * 打开项目GITHUB
                         */
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(PROJECT_GITHUB));
                        intent.setAction(Intent.ACTION_VIEW);
                        startActivity(intent);
                        break;

                    case R.id.nav_csdn:
                        /**
                         * 打开项目CSDN
                         */
                        Intent intent1 = new Intent();
                        intent1.setData(Uri.parse(PROJECT_CSDN));
                        intent1.setAction(Intent.ACTION_VIEW);
                        startActivity(intent1);
                        break;

                    case R.id.nav_setting:
                        /**
                         * 打开设置界面
                         */
                        startActivity(new Intent(IndexActivity.this, SettingActivity.class));
                        break;

                    case R.id.nav_clear:

                        break;

                    case R.id.nav_about:
                        //启动到关于我们界面
                        startActivity(new Intent(IndexActivity.this, AboutActivity.class));
                        break;

                }
                return true;
            }
        });


        /**
         *挤压式抽屉实现
         * 1 主布局的Layoutchange监听，用Laout方法重新布局主布局
         */
        rlIndexMainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                rlIndexMainLayout.layout(navView.getRight(), 0,
                        navView.getRight() + metrics.widthPixels, metrics.heightPixels + 100);

            }
        });

        /**
         * 2 对抽屉事件进行监听，
         * 在抽屉滑动的时候完成主布局的重新布局
         * 主布局的重新布局需要借助当前抽屉拖出的宽度。
         * 根据当前抽屉拖出的宽度来设置主布局的位置
         */
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                rlIndexMainLayout.layout(navView.getRight(), rlIndexMainLayout.getTop(),
                        rlIndexMainLayout.getRight() + navView.getRight(),
                        rlIndexMainLayout.getBottom());


            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

                rlIndexMainLayout.layout(0, rlIndexMainLayout.getTop(),
                        rlIndexMainLayout.getRight(),
                        rlIndexMainLayout.getBottom());

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initView() {

        /*
           对Message这一Fragment进行一些处理
         */
        ibIndexBottomMessage.setmBigBitmapSrc(getResources().getDrawable(R.drawable.bubble_big));
        ibIndexBottomMessage.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.bubble_small));
        ibIndexBottomMessage.setmCheckSate(IndexBottomLayout.CHECKED);


        //红点拖拽
        setRedPointDrag();


        frameLayout = findViewById(R.id.fg_indexFragment);

        if (messageFragment == null) {
            messageFragment = new MessageFragment();
        }
        /*
          初始用Message Fragment显示
         */
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fg_indexFragment, messageFragment);
        transaction.commit();


    }

    /**
     * 红点拖拽效果绑定
     */
    private void setRedPointDrag() {
        /**
         * 注意对于需要实现拖拽效果的view需要单独指定一个布局文件，并且次布局最好不能有viewGroup，
         * 否则view上面显示的文字可能在拖拽时不能识别，这样一是为了方便，二是为了减少消耗
         * 布局方式请参考xml文件
         */
//        View view = findViewById(R.id.redpoint_view) ;
//        TextView textView = findViewById(R.id.tv_mDragView);

        /**
         * 需要对每一个红点进行设置
         */
        redPointMessage = ibIndexBottomMessage.findViewById(R.id.redpoint_view);
        RedPointViewHelper stickyViewHelper = new RedPointViewHelper(this, redPointMessage, R.layout.item_drag_view);

        redPointFriend = ibIndexBottomFriend.findViewById(R.id.redpoint_view);
        RedPointViewHelper stickyViewHelper1 = new RedPointViewHelper(this, redPointFriend, R.layout.item_drag_view);

        redPointDynamic = ibIndexBottomDynamic.findViewById(R.id.redpoint_view);
        RedPointViewHelper stickyViewHelper2 = new RedPointViewHelper(this, redPointDynamic, R.layout.item_drag_view);


    }


    /**
     * 设置IndeBottomCheckState状态为未选中
     *
     * @param indexBottomCheckState
     */
    private void setIbIndexBottomCheckState_UnChecked(IndexBottomLayout
                                                              indexBottomCheckState) {
        indexBottomCheckState.setmCheckSate(IndexBottomLayout.UNCHECKED);
    }

    /**
     * 设置IndexBottomCheckState状态为选中
     *
     * @param indexBottomCheckState_checked
     */
    private void setIbIndexBottomCheckState_Checked(IndexBottomLayout
                                                            indexBottomCheckState_checked) {
        indexBottomCheckState_checked.setmCheckSate(IndexBottomLayout.CHECKED);
    }

    /**
     * 根据是否选中，设置不同的Image
     *
     * @param indexBottomLayout_Message
     * @param indexBottomLayout_Person
     * @param indexBottomLayout_Dynamic
     */
    private void setIbIndexBottomImage(IndexBottomLayout indexBottomLayout_Message,
                                       IndexBottomLayout indexBottomLayout_Person,
                                       IndexBottomLayout indexBottomLayout_Dynamic) {
        if (indexBottomLayout_Message.getmCheckSate() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_Message.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_bubble_small));
            indexBottomLayout_Message.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_bubble_big));
            indexBottomLayout_Message.lookRight();
        } else {
            indexBottomLayout_Message.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.bubble_small));
            indexBottomLayout_Message.setmBigBitmapSrc(getResources().getDrawable(R.drawable.bubble_big));
            indexBottomLayout_Person.lookLeft();
        }

        if (indexBottomLayout_Person.getmCheckSate() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_Person.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_person_small));
            indexBottomLayout_Person.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_person_big));
        } else {
            indexBottomLayout_Person.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.person_small));
            indexBottomLayout_Person.setmBigBitmapSrc(getResources().getDrawable(R.drawable.person_big));
        }

        if (indexBottomLayout_Dynamic.getmCheckSate() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_Dynamic.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_star_small));
            indexBottomLayout_Dynamic.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_star_big));
        } else {
            indexBottomLayout_Dynamic.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.star_small));
            indexBottomLayout_Dynamic.setmBigBitmapSrc(getResources().getDrawable(R.drawable.star_big));
            indexBottomLayout_Person.lookRight();
        }
    }

    /**
     * View的点击监听
     *
     * @param view
     */
    public void bottomLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.ib_indexBottomMessage:
                //如果Message fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomMessage.getmCheckSate() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomFriend);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomDynamic);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomMessage);

                    //页面切换
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fg_indexFragment, messageFragment);
                    transaction.commit();
                }
                break;
            case R.id.ib_indexBottomFriend:
                //如果Message fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomFriend.getmCheckSate() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomMessage);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomDynamic);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomFriend);

                    //页面切换
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    if (friendFragment == null) {
                        friendFragment = new FriendFragment();
                    }
                    transaction.replace(R.id.fg_indexFragment, friendFragment);
                    transaction.commit();
                }
                break;
            case R.id.ib_indexBottomDynamic:
                //如果Message fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomDynamic.getmCheckSate() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomFriend);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomMessage);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomDynamic);
                    //页面切换
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    if (dynamicFragment == null) {
                        dynamicFragment = new DynamicFragment();
                    }
                    transaction.replace(R.id.fg_indexFragment, dynamicFragment);
                    transaction.commit();
                }
                break;
        }
        //View根据不同的状态设置显示的图片
        setIbIndexBottomImage(ibIndexBottomMessage, ibIndexBottomFriend, ibIndexBottomDynamic);
        //为当前选中的状态设置图片缩放的动画
        ((IndexBottomLayout) view).setImageScale();

    }

    /**
     * 获取抽屉对象
     * 用于在Fragment中打开抽屉
     *
     * @return 抽屉对象
     */
    public DrawerLayout getDrawerLayout() throws Exception {
        /**
         * 对空对象进行判断和报错
         */
        if (drawerLayout == null) {
            throw new Exception("drawLayout is a null object .");
        }
        return drawerLayout;
    }

    /**
     * 捕捉按键实现在相近时间内来两次点击Back退出程序
     *
     * @param keyCode 按键
     * @param event   事件
     * @return super or true
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(TAG, "onKeyDown: " + keyCode);

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ( (System.currentTimeMillis() - lastExitTime) > MAX_EXIT_TIME) {
                ITosast.showShort(IndexActivity.this, "再按一次退出程序").show();
                lastExitTime = System.currentTimeMillis() ;
            } else {
                //退出程序
                ActivityController.finishAll();
            }
            return true ;
        }
        return super.onKeyDown(keyCode, event);
    }

}
