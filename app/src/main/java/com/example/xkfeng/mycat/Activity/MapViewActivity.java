package com.example.xkfeng.mycat.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.xkfeng.mycat.DrawableView.NestedListView;
import com.example.xkfeng.mycat.Model.NearLocationModel;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapViewActivity extends BaseActivity {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.tv_sendText)
    TextView tvSendText;
    @BindView(R.id.nlv_nearLocation)
    NestedListView nlvNearLocation;


    private MapView mvMapView;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private BaiduMap mBaiduMap;
    private static final String TAG = "MapViewActivity";

    //防止每次定位都重新设置中心点和marker
    private boolean isFirstLocation = true;
    //位置
    private String addr;
    //经纬度
    private double lat;
    private double lon;

    private LatLng latLng;
    private GeoCoder coder = null;

    //地图缩放比例
    private static final int ZOOM = 18;

    //在地图中上次选中的item的位置。
    private static int lastSelectedItemPos = 0;
    //附近地址适配器
    private NearLocationAdapter nearLocationAdapter;
    //附近地址列表内容
    private List<NearLocationModel> locationModels = new ArrayList<>();
    //当前选中的列表项
    private NearLocationModel currenSelectedModel = new NearLocationModel();


    //用户点击位置后实现位置显示

    private boolean isShowLoc = false;
    private double showLongitude;
    private double showLatitude;
    private String showAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_layout);
        ButterKnife.bind(this);

        isShowLoc = getIntent().getBooleanExtra("isShowLoc", false);
        showLongitude = getIntent().getDoubleExtra("longitude", 0);
        showLatitude = getIntent().getDoubleExtra("latitude", 0);
        showAddress = getIntent().getStringExtra("address");


        mvMapView = findViewById(R.id.mv_mapView);
        lastSelectedItemPos = 0;
        initTitle();
        checkPermission();
        setTextClick();

        if (isShowLoc) {
            tvSendText.setVisibility(View.GONE);
        }


    }

    private void initTitle() {
        //沉浸式状态栏
        DensityUtil.fullScreen(this);
//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
    }

    private void checkPermission() {
        /*统一申请权限*/
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]); /*使用ActivityCompat 统一申请权限 */
            ActivityCompat.requestPermissions(MapViewActivity.this, permissions, 1);
        } else {
            if (!isShowLoc) {
                //初始化地图
                initMap();
                //地址数据解析
                addressDataParsing();
                /*开始定位*/
                startLocate();
            } else {
                initMap();
                onlyUseInUserClickLocation();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    if (!isShowLoc) {
                        //初始化地图
                        initMap();
                        //地址数据解析
                        addressDataParsing();
                        /*开始定位*/
                        startLocate();
                    } else {
                        initMap();
                        onlyUseInUserClickLocation();
                    }

                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;


        }
    }

    private void initMap() {

        coder = GeoCoder.newInstance();


        mBaiduMap = mvMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                coder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                mapMoveCenter(latLng);

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //显示卫星图层
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);



    }

    /**
     * 用户点击位置信息的时候，同样调用该Activity，此时，
     * 该Activity的布局需要进行特殊处理
     */
    private void onlyUseInUserClickLocation(){
        if (isShowLoc){
            /**
             * 地图定位在用户点击的位置的地方
             */
            coder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(showLatitude , showLongitude))) ;
            mapMoveCenter(new LatLng(showLatitude , showLongitude));
            //不显示底部附近位置列表
            nlvNearLocation.setVisibility(View.GONE);
        }
    }


    private void addressDataParsing() {
        /**
         * 解析点击处附近的地址信息
         */
        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                String address = reverseGeoCodeResult.getAddress();//解析到的地址
                //清空现有数据
                locationModels.clear();
                NearLocationModel nearLocationModel;
                for (PoiInfo poi : reverseGeoCodeResult.getPoiList()) {
                    Log.d(TAG, "onGetReverseGeoCodeResult: name :" + poi.getName() + " area :" + poi.getArea() + " address :" + poi.getAddress());
                    nearLocationModel = new NearLocationModel();
                    nearLocationModel.setAddress(poi.getAddress());
                    nearLocationModel.setLatitude(poi.getLocation().latitude);
                    nearLocationModel.setLongitude(poi.getLocation().longitude);
                    nearLocationModel.setScale(ZOOM);
                    nearLocationModel.setName(poi.getName());
                    locationModels.add(nearLocationModel);
                }
                if (nearLocationAdapter == null) {
                    nearLocationAdapter = new NearLocationAdapter(locationModels);
                    nlvNearLocation.setAdapter(nearLocationAdapter);
                }
                //更新数据
                nearLocationAdapter.notifyDataSetChanged();
//                nearLocationAdapter.setList(locationModels);
            }
        });
    }


    /**
     * 将地图移动到中心点
     *
     * @param arg0
     */
    private void mapMoveCenter(LatLng arg0) {

        mBaiduMap.clear();

        MapStatus mMapStatus = new MapStatus.Builder()
                .target(arg0)
                .zoom(ZOOM)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_position_red_32);
        OverlayOptions option = new MarkerOptions().position(arg0).icon(mCurrentMarker);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);

    }

    private void setTextClick() {
        tvSetBackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ITosast.showShort(MapViewActivity.this, "发送").show();

                if (TextUtils.isEmpty(currenSelectedModel.getName())) {
                    //数据为空不做任何处理
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("latitude", currenSelectedModel.getLatitude());
                    intent.putExtra("longitude", currenSelectedModel.getLongitude());
                    intent.putExtra("name", currenSelectedModel.getName());
                    intent.putExtra("scale", currenSelectedModel.getScale());
                    intent.putExtra("street", currenSelectedModel.getAddress());
                    MapViewActivity.this.setResult(RESULT_OK, intent);
                }

                MapViewActivity.this.finish();
            }
        });
    }

    /*
     * 定位
     */
    private void startLocate() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        /**
         * 可以获取到当前位置，但是地图显示不正确，地图显示的是默认的位置。
         */
//        mLocationClient.registerNotifyLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 2000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //开启定位
        mLocationClient.start();
    }

    private class MyLocationListener implements BDLocationListener {

        /**
         * 位置有所改变的时候才会回调该方法
         *
         * @param location
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mvMapView == null) {
                return;
            }
            // 开始移动百度地图的定位地点到中心位置
            if (latLng == null) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            lat = location.getLatitude();
            lon = location.getLongitude();

            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);//地图中心点
            MapStatusUpdate state = MapStatusUpdateFactory.zoomBy(ZOOM);//缩放比例
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_position_red_32);
            OverlayOptions option = new MarkerOptions().position(latLng).icon(mCurrentMarker);
            coder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            // 在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
            mBaiduMap.setMapStatus(update);
            mBaiduMap.animateMapStatus(state);


            StringBuffer sb = new StringBuffer(256);
//            sb.append("time : ");
//            sb.append(location.getTime());
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());
//            sb.append("\nlatitude : ");
//            sb.append(location.getLatitude());
//            sb.append("\nlontitude : ");
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");
//            sb.append(location.getRadius());
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());// 单位：公里每小时
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//                sb.append("\nheight : ");
//                sb.append(location.getAltitude());// 单位：米
//                sb.append("\ndirection : ");
//                sb.append(location.getDirection());// 单位度
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                sb.append("\ndescribe : ");
//                sb.append("gps定位成功");
//
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                addr = location.getAddrStr();
//                //运营商信息
//                sb.append("\noperationers : ");
//                sb.append(location.getOperators());
//                sb.append("\ndescribe : ");
//                sb.append("网络定位成功");
//            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                sb.append("\ndescribe : ");
//                sb.append("离线定位成功，离线定位结果也是有效的");
//            } else if (location.getLocType() == BDLocation.TypeServerError) {
//                sb.append("\ndescribe : ");
//                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                sb.append("\ndescribe : ");
//                sb.append("网络不同导致定位失败，请检查网络是否通畅");
//            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                sb.append("\ndescribe : ");
//                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//            }
//            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息

            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.d(TAG, "描述: " + sb.toString());
            //设置并显示中心点
//            setPosition2Center(mBaiduMap, location, true);

        }
    }

    /**
     * 设置中心点和添加marker
     *
     * @param map
     * @param bdLocation
     * @param isShowLoc
     */
    public void setPosition2Center(BaiduMap map, BDLocation bdLocation, Boolean isShowLoc) {
        MyLocationData locData = new MyLocationData.Builder()
//                .accuracy(bdLocation.getRadius())
//                .direction(bdLocation.getRadius())
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        map.setMyLocationData(locData);

        if (isShowLoc) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    private class NearLocationAdapter extends BaseAdapter {

        private List<NearLocationModel> list;

        public NearLocationAdapter(List<NearLocationModel> list) {
            this.list = list;
        }

        public void setList(List<NearLocationModel> list) {
            if (list != null) {
                this.list = list;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(MapViewActivity.this).inflate(R.layout.near_location_item, null, false);
                viewHolder.name = convertView.findViewById(R.id.tv_locationName);
                viewHolder.address = convertView.findViewById(R.id.tv_locationAddress);
                viewHolder.selectedItem = convertView.findViewById(R.id.iv_selectedItem);
                viewHolder.nearLocationLayout = convertView.findViewById(R.id.rl_nearLocationLayout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(list.get(pos).getName());
            viewHolder.address.setText(list.get(pos).getAddress());

            // 对选中的项需要特殊处理
            // 默认选中第一项
            if (lastSelectedItemPos == pos) {
                //当前选中的列表项
                currenSelectedModel = list.get(pos);

                setSelectedItem(viewHolder, true);
            } else {
                setSelectedItem(viewHolder, false);
            }
            viewHolder.nearLocationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //更新点击的位置
                    lastSelectedItemPos = pos;

                    //更新数据
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView name;
            TextView address;
            ImageView selectedItem;
            RelativeLayout nearLocationLayout;
        }


        private void setSelectedItem(ViewHolder viewHolder, boolean isSelected) {
            if (isSelected) {
                viewHolder.address.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.name.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.selectedItem.setVisibility(View.VISIBLE);
            } else {
                viewHolder.name.setTextColor(getResources().getColor(R.color.chat_text_color));
                viewHolder.address.setTextColor(getResources().getColor(R.color.transparent));
                viewHolder.selectedItem.setVisibility(View.GONE);

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        lastSelectedItemPos = 0;
        //在activity执行onResume时必须调用mvMapView. onResume ()
        mvMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastSelectedItemPos = 0;
        //在activity执行onPause时必须调用mvMapView. onPause ()
        mvMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lastSelectedItemPos = 0;
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        // 退出时销毁定位
        if (mLocationClient != null){
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient.stop();
        }

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时必须调用mvMapView.onDestroy()
        mvMapView.onDestroy();
        mvMapView = null;


    }
}
