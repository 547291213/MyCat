package com.example.xkfeng.mycat.Activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.IndexBottomLayout;
import com.example.xkfeng.mycat.DrawableView.RedPointViewHelper;
import com.example.xkfeng.mycat.Fragment.DynamicFragment;
import com.example.xkfeng.mycat.Fragment.FriendFragment;
import com.example.xkfeng.mycat.Fragment.MessageFragment;
import com.example.xkfeng.mycat.R;
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
    private DisplayMetrics metrics;

    private FrameLayout frameLayout;
    private MessageFragment messageFragment;
    private FriendFragment friendFragment;
    private DynamicFragment dynamicFragment;
    private android.support.v4.app.FragmentManager fragmentManager;

    private static final String PROJECT_GITHUB = "https://github.com/547291213/MyCat";
    private static final String PROJECT_CSDN = "https://blog.csdn.net/qq_29989087/article/details/82962296";


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
        fullScreen(this);

        //抽屉设置
        setNavView();

        //初始化布局
        initView();


    }

    /**
     * 设置抽屉属性
     * 1 点击事件处理
     */
    private void setNavView() {
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

                        break;

                    case R.id.nav_clear:

                        break;

                    case R.id.nav_about:

                        break;

                }
                return true;
            }
        });
    }

    /**
     * 沉浸式状态栏
     * 并且状态栏颜色跟随顶部View的颜色，追随渐变
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
                // window.setNavigationBarColor(Color.TRANSPARENT);

            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                // attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }


    private void initView() {

        /*
           对Message这一Fragment进行一些处理
         */
        ibIndexBottomMessage.setmBigBitmapSrc(getResources().getDrawable(R.drawable.bubble_big));
        ibIndexBottomMessage.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.bubble_small));
        ibIndexBottomMessage.setmCheckSate(IndexBottomLayout.CHECKED);

        /**
         * 注意对于需要实现拖拽效果的view需要单独指定一个布局文件，并且次布局最好不能有viewGroup，
         * 否则view上面显示的文字可能在拖拽时不能识别，这样一是为了方便，二是为了减少消耗
         * 布局方式请参考xml文件
         */
        TextView textView = findViewById(R.id.tv_mDragView);
        RedPointViewHelper stickyViewHelper = new RedPointViewHelper(this, textView, R.layout.item_drag_view);


        frameLayout = findViewById(R.id.fg_indexFragment);

        if (messageFragment == null) {
            messageFragment = new MessageFragment();
        }
        /*
          初始用Message Fragment显示
         */
        fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fg_indexFragment, messageFragment);
        transaction.commit();


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
                    android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
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
                    android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                    if (friendFragment == null) {
                        friendFragment = new FriendFragment() ;
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
                    android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                    if (dynamicFragment == null) {
                        dynamicFragment = new DynamicFragment() ;
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
        ITosast.show(this, "Click", Toast.LENGTH_SHORT);

    }


}
