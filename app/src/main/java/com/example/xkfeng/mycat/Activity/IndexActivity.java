package com.example.xkfeng.mycat.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.DrawableView.IndexBottomLayout;
import com.example.xkfeng.mycat.DrawableView.RedPointViewHelper;
import com.example.xkfeng.mycat.DrawableView.ViewPagerSlide;
import com.example.xkfeng.mycat.Fragment.BottomTabFragmentPageAdapter;
import com.example.xkfeng.mycat.Fragment.DynamicFragment;
import com.example.xkfeng.mycat.Fragment.FriendFragment;
import com.example.xkfeng.mycat.Fragment.MessageFragment;
import com.example.xkfeng.mycat.Model.LocationBean;
import com.example.xkfeng.mycat.Model.MsgEvent;
import com.example.xkfeng.mycat.Model.WeatherBean;
import com.example.xkfeng.mycat.NetWork.HttpHelper;
import com.example.xkfeng.mycat.NetWork.NetCallBackResultBean;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RxBus.RxBus;
import com.example.xkfeng.mycat.Util.ActivityController;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.vp_indexFragmentPager)
    ViewPagerSlide vpIndexFragmentPager;

    private DisplayMetrics metrics;

    private MessageFragment messageFragment;
    private FriendFragment friendFragment;
    private DynamicFragment dynamicFragment;
    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList ;
    private BottomTabFragmentPageAdapter pageAdapter ;

    private View redPointMessage;
    private View redPointFriend;
    private View redPointDynamic;

    private RedPointViewHelper stickyViewHelper;
    private RedPointViewHelper stickyViewHelper1;
    private RedPointViewHelper stickyViewHelper2;

    private static final String PROJECT_GITHUB = "https://github.com/547291213/MyCat";
    private static final String PROJECT_CSDN = "https://blog.csdn.net/qq_29989087/article/details/82962296";

    private UserInfo userInfo;
    private static final int REQUEST_USERINFO = 1;

    //用户最近一次点击Back的事件
    //用于实现在相近时间内两次点击Back退出程序
    private static long lastExitTime = 0;

    //两次点击退出的时间间隔
    private static final int MAX_EXIT_TIME = 2000;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private LocationManager locationManager;
    private String locationProvider;


    public static boolean isFirst = true;

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

        //初始化页面和碎片
        initPagerAndFragment() ;

    }

    private void initPagerAndFragment() {

        fragmentList = new ArrayList<>() ;
        messageFragment = new MessageFragment();
        friendFragment = new FriendFragment() ;
        dynamicFragment = new DynamicFragment() ;

        fragmentList.add(messageFragment) ;
        fragmentList.add(friendFragment) ;
        fragmentList.add(dynamicFragment) ;

        fragmentManager = getSupportFragmentManager() ;

        pageAdapter = new BottomTabFragmentPageAdapter(fragmentManager , fragmentList) ;
        //设置适配器
        vpIndexFragmentPager.setAdapter(pageAdapter);
        //禁止左右滑动
        vpIndexFragmentPager.setSlide(false);
        //当前显示的item
        vpIndexFragmentPager.setCurrentItem(0);
        //一共三个页面，缓存两个页面，另一个正在显示
        vpIndexFragmentPager.setOffscreenPageLimit(2);

    }


    /**
     * 每次界面可见的时候获取当前天气
     */
    @Override
    protected void onResume() {
        super.onResume();
        //获取RxBus发送的事件
        getRxBusEvent();

        //获取权限
        getUserPermission();


    }

    /**
     * 获取并且处理RxBus发送的消息事件
     */
    private void getRxBusEvent() {
        RxBus.getInstance()
//                .toObservable(this , MsgEvent.class) // 防止内存泄漏
                .toObservable(MsgEvent.class)  //对象类型(可能存在内存泄漏)
//                .compose(provider.<MsgEvent>bindToLifecycle())  //防内存泄漏
                .observeOn(AndroidSchedulers.mainThread())  //观察者事件发生线程
                .subscribeOn(Schedulers.io()) //被观察者事件发生线程
                .subscribe(new Observer<MsgEvent>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final MsgEvent msgEvent) {
                        navView.getMenu().findItem(R.id.nav_city).setTitle(msgEvent.getT().getLocationData());
                        navView.getMenu().findItem(R.id.nav_weather).setTitle(msgEvent.getT().getWeather());
//                        navView.postInvalidate();
//                        navView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout() {
//
//
////                        if ((MenuItem) navView.findViewById(R.id.nav_city) != null && (MenuItem) navView.findViewById(R.id.nav_weather) != null) {
////                            navView.getMenu().getItem(2).setTitle();
////                            navView.getMenu().getItem(3).setTitle(msgEvent.getT().getWeather());
////                            ((MenuItem) navView.findViewById(R.id.nav_city)).setTitle(msgEvent.getT().getLocationData());
////                            ((MenuItem) navView.findViewById(R.id.nav_weather)).setTitle(msgEvent.getT().getWeather());
////                        }
//                                navView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            }
//                        });

//                        Log.d(TAG, "onNext: Success :" + msgEvent.getT().getLocationData() + " Weather:" + msgEvent.getT().getWeather());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @SuppressLint("MissingPermission")
    private String getLocationInfoMation() {

        //获取地址位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //获取所有可用的地理位置提供器
        List<String> providers = locationManager.getProviders(true);

        //如果是GPS
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
//            ITosast.showShort(this, "当前无法获取位置,请检查您的网络设置").show();
            return "null";
        }

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
// != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // : Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return ;
//        }
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationProvider);

        //如果位置信息不为空
        if (location != null) {
            return location.getLongitude() + "," + location.getLatitude();
        }

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // : Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return ;
//        }

        locationManager.requestLocationUpdates(locationProvider, 3000, 1, listener);


        return "null";

    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            /**
             * 重新获取位置信息
             */
            getCityAndWeather(location.getLongitude() + "," + location.getLatitude());


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    /**
     * 获取位置需要得到的的权限
     * 访问网络
     */
    private void getUserPermission() {

        //权限获取
        if (ActivityCompat.checkSelfPermission(IndexActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(IndexActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
                    , REQUEST_LOCATION_PERMISSION);

        } else {
            //访问网络
            String strings = getLocationInfoMation();
            getCityAndWeather(strings);
        }

    }

    /**
     * 获取当前位置的城市和天气
     *
     * @param strings 当前位置的经度和维度
     */
    private void getCityAndWeather(String strings) {

        /**
         * 只在用户登陆的时候进行提示处理
         * 非登陆的时候，只返回
         */
        if ("null".equals(strings)) {
            if (isFirst) {
                ITosast.showShort(this, "无法获取位置信息，可能会影响部分功能的使用").show();
                isFirst = false;
            }
            return;
        }

//        Toast.makeText(this, "正在获取数据 ：" + strings, Toast.LENGTH_SHORT).show();
        HttpHelper httpHelper = HttpHelper.getInstance(getApplicationContext());
        httpHelper.getRequest("https://free-api.heweather.com/s6/weather/now?location=" +
                        strings + "&key=722dda481604441db9967f3fabd76ed1", null,
                HttpHelper.JSON_DATA_1,
                new NetCallBackResultBean<WeatherBean>() {
                    @Override
                    public void Failed(String string) {

                        ITosast.showShort(IndexActivity.this, "访问天气数据失败").show();
                    }

                    @Override
                    public void onSuccess(List<Map<String, Object>> result) {
                    }

                    @Override
                    public void onSuccess(WeatherBean weatherBean) {
//                        Log.d(TAG, "onSuccess: " + IPUtil.getIPAddress(getApplicationContext()));
//
//                        Log.d(TAG, "onSuccess: " + weatherBean.getHeWeather6().get(0).getBasic().getLocation());
                        String locationData = null;
                        String weather = null;
                        if (weatherBean.getHeWeather6() != null) {
                            if (!TextUtils.isEmpty(weatherBean.getHeWeather6().get(0).getBasic().getLocation()) &&
                                    weatherBean.getHeWeather6().get(0).getBasic().getLocation().equals
                                            (weatherBean.getHeWeather6().get(0).getBasic().getParent_city())) {
                                locationData = weatherBean.getHeWeather6().get(0).getBasic().getLocation();
                                weather = weatherBean.getHeWeather6().get(0).getNow().getCond_txt();
                            } else if (!TextUtils.isEmpty(weatherBean.getHeWeather6().get(0).getBasic().getLocation()) &&
                                    !weatherBean.getHeWeather6().get(0).getBasic().getLocation().equals
                                            (weatherBean.getHeWeather6().get(0).getBasic().getParent_city())) {
                                locationData = weatherBean.getHeWeather6().get(0).getBasic().getParent_city() +
                                        weatherBean.getHeWeather6().get(0).getBasic().getLocation();
                                weather = weatherBean.getHeWeather6().get(0).getNow().getCond_txt();
                            }
                            LocationBean locationBean = new LocationBean(locationData, weather);

                            /**
                             * 将消息和数据发送到主线程，
                             * 在主线程中进行UI数据修改
                             */
                            RxBus.getInstance().post(new MsgEvent("location", locationBean));
                        }

//                        Log.d(TAG, "onSuccess: " + weatherBean.getHeWeather6().get(0).getNow().getCond_txt());

                    }
                });

    }

    /**
     * 设置用户资料
     */
    private void setUserHeadInfo() {
        /**
         * 更新用户头像
         */
        userInfo = JMessageClient.getMyInfo();
        if (userInfo.getAvatar() != null && !StringUtil.isEmpty(userInfo.getAvatarFile().toString())) {
            //  circleImageView.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatar()));
            Glide.with(IndexActivity.this)
                    .load(userInfo.getAvatarFile())
                    .into((ImageView) navView.getHeaderView(0).findViewById(R.id.iv_navigationHeaderImage));
        } else {
            ((ImageView) navView.getHeaderView(0).findViewById(R.id.iv_navigationHeaderImage)).setImageResource(R.mipmap.log);
        }

        /**
         * 更新用户昵称
         */
        ((TextView) navView.getHeaderView(0).findViewById(R.id.tv_signatureTextView)).setText(userInfo.getNickname().toString());

        /**
         * 设置用户名
         */
        ((TextView) navView.getHeaderView(0).findViewById(R.id.tv_userName)).setText(JMessageClient.getMyInfo().getUserName().toString());

    }

    /**
     * 设置抽屉属性
     * 一 点击事件处理
     * 二 挤压抽屉实现
     */
    private void setNavView() {

        /**
         * 用户头像点击进行页面跳转
         * 进行用户属性的设置
         */
        View view = navView.getHeaderView(0);

        /**
         * 设置用户资料
         */
        setUserHeadInfo();

        view.findViewById(R.id.iv_navigationHeaderImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 跳转到用户资料栏
                 */
                startActivityForResult(new Intent(IndexActivity.this, UserInfoActivity.class), REQUEST_USERINFO);
//                Toast.makeText(IndexActivity.this, "Image", Toast.LENGTH_SHORT).show();

            }
        });


        /**
         * 加载用户名和昵称
         */
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
        /**
         * 需要对每一个红点进行设置
         */
        redPointMessage = ibIndexBottomMessage.findViewById(R.id.redpoint_view);
        stickyViewHelper = new RedPointViewHelper(this, redPointMessage, R.layout.item_drag_view);
        stickyViewHelper.setRedPointViewReleaseOutRangeListener(new RedPointViewHelper.RedPointViewReleaseOutRangeListener() {
            @Override
            public void onReleaseOutRange() {
                Toast.makeText(IndexActivity.this, "redPointMessage", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRedViewClickDown() {

            }

            @Override
            public void onRedViewCLickUp() {

            }
        });
        stickyViewHelper.setRedPointViewText("8");

        redPointFriend = ibIndexBottomFriend.findViewById(R.id.redpoint_view);
        stickyViewHelper1 = new RedPointViewHelper(this, redPointFriend, R.layout.item_drag_view);
        stickyViewHelper1.setRedPointViewText("99");
        stickyViewHelper1.setRedPointViewReleaseOutRangeListener(new RedPointViewHelper.RedPointViewReleaseOutRangeListener() {
            @Override
            public void onReleaseOutRange() {
                Toast.makeText(IndexActivity.this, "redPointFriend", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRedViewClickDown() {

            }

            @Override
            public void onRedViewCLickUp() {

            }
        });

        redPointDynamic = ibIndexBottomDynamic.findViewById(R.id.redpoint_view);
        stickyViewHelper2 = new RedPointViewHelper(this, redPointDynamic, R.layout.item_drag_view);
        stickyViewHelper2.setRedPointViewText("12");
        stickyViewHelper2.setRedPointViewReleaseOutRangeListener(new RedPointViewHelper.RedPointViewReleaseOutRangeListener() {
            @Override
            public void onReleaseOutRange() {
                Toast.makeText(IndexActivity.this, "redPointDynamic", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRedViewClickDown() {

            }

            @Override
            public void onRedViewCLickUp() {

            }
        });

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
                stickyViewHelper.setViewShow();
                if (ibIndexBottomMessage.getmCheckSate() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomFriend);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomDynamic);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomMessage);

                    //页面切换
                    vpIndexFragmentPager.setCurrentItem(0);
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
                    vpIndexFragmentPager.setCurrentItem(1);
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
                    vpIndexFragmentPager.setCurrentItem(2);
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
            if ((System.currentTimeMillis() - lastExitTime) > MAX_EXIT_TIME) {
                ITosast.showShort(IndexActivity.this, "再按一次退出程序").show();
                lastExitTime = System.currentTimeMillis();
            } else {
                //退出程序
                ActivityController.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_USERINFO:

                /**
                 * 更新用户数据
                 */
                setUserHeadInfo();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length <= 0) {

                    ITosast.showShort(IndexActivity.this, "获取权限失败").show();
                    return;
                }
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {

                        ITosast.showShort(IndexActivity.this, "获取权限失败").show();
                        return;
                    }
                }
                String strings = getLocationInfoMation();
                getCityAndWeather(strings);
                break;


        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //移除监听器
            locationManager.removeUpdates(listener);
        }
    }
}
