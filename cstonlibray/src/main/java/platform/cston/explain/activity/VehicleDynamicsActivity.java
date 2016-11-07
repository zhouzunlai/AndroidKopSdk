package platform.cston.explain.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.receiver.ConnectionChangeReceiver;
import platform.cston.explain.receiver.OnNetChangeListener;
import platform.cston.explain.widget.CarOverlay;
import platform.cston.httplib.bean.VehicleDynamicsResult;
import platform.cston.httplib.search.OnResultListener;
import platform.cston.httplib.search.VehicleDynamics;


/**
 * 车动态页
 * Created by zhou-pc on 2016/4/9.
 */
public class VehicleDynamicsActivity extends Activity implements View.OnClickListener, OnNetChangeListener, OnResultListener.OnGetVehicleDynamicsResultListener {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;

    private ImageView mBtn_location_car;
    private ImageView mBtn_location_self;
    private ImageView mBtn_roadcondition;
    private TextView mText_dynamics_title;

    private ImageView mImg_carstatu_distance;
    private TextView mText_carstatu_distance;
    private ImageView mImg_carstatu_time;
    private TextView mText_carstatu_time;
    private ImageView mImg_carstatu_consumption;
    private TextView mText_carstatu_consumption;

    private LinearLayout mView_carstatu_mile;
    private LinearLayout mView_carstatu_oil;
    private LinearLayout mView_back;

    private LatLng matLng = null;//当前坐标位置
    private LatLng startmatLng = null;//起始坐标位置
    private InfoWindow mInfoWindow;//用于显示地图覆盖物的InfoWindow
    private CarOverlay mOverlay;//实时点覆盖物
    private MapStatusUpdate msUpdate = null;// 地图状态
    private PolylineOptions polyline = null;// 路线覆盖物
    private MarkerOptions startMarker = null;// 起点图标覆盖物
    private BitmapDescriptor bmStart;// 起点图标
    private ConnectionChangeReceiver mNetReceiver;
    private List<LatLng> mPointList = new ArrayList<>();//轨迹点列表

    private boolean mFlag_RoadCondition;//路况开关控制
    private boolean mFlag_LocationCar = true;//定位车开关控制
    private boolean isFirstLoc = true; // 是否首次定位
    private boolean mFlag_NetisAvailable = true;//网络状态是否可用
    private boolean isInUploadStatu = true;//是否改变地图状态
    private boolean isFirstUploadStatu = true;//是否改变地图状态
    private boolean isNeedRequest = false;//是否需要请求数据

    private long timeInterval = 3000;//刷新间隔

    private String openCarId;//查询车辆id
    private Handler mRequestHandler = new Handler();

    private DecimalFormat mDecimalFormat = new DecimalFormat("#####0.0");

    private long mStartTime;
    private long mLastReciverTime;

    private final long OUT_TIME_INTERVAL = 30 * 1000;//接口请求超时间隔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        openCarId = getIntent().getStringExtra("OPENCARID");
        setContentView(R.layout.cst_platform_activity_dynamics);
        initMapView();
        mBtn_location_car = (ImageView) findViewById(R.id.dynamic_btn_location_car);
        mBtn_location_self = (ImageView) findViewById(R.id.dynamic_btn_location_self);
        mBtn_roadcondition = (ImageView) findViewById(R.id.dynamic_btn_roadcondition);
        mView_back = (LinearLayout) findViewById(R.id.dynamics_back);
        mText_dynamics_title = (TextView) findViewById(R.id.dynamics_title_statu);

        mView_carstatu_mile = (LinearLayout) findViewById(R.id.track_mile_layout);
        mView_carstatu_oil = (LinearLayout) findViewById(R.id.track_oil_layout);

        mImg_carstatu_distance = (ImageView) findViewById(R.id.track_mile_image);
        mText_carstatu_distance = (TextView) findViewById(R.id.track_mile_tv);

        mImg_carstatu_time = (ImageView) findViewById(R.id.track_time_image);
        mText_carstatu_time = (TextView) findViewById(R.id.track_time_tv);

        mImg_carstatu_consumption = (ImageView) findViewById(R.id.track_oil_image);
        mText_carstatu_consumption = (TextView) findViewById(R.id.track_oil_tv);

        mBtn_location_car.setOnClickListener(this);
        mBtn_location_self.setOnClickListener(this);
        mBtn_roadcondition.setOnClickListener(this);
        mImg_carstatu_distance.setOnClickListener(this);
        mImg_carstatu_time.setOnClickListener(this);
        mView_back.setOnClickListener(this);
        mImg_carstatu_consumption.setOnClickListener(this);
        mStartTime = 0;
        registerReceiver();
        mLastReciverTime = System.currentTimeMillis();
    }


    /**
     * 初始化地图相关
     */
    private void initMapView() {
        bmStart = BitmapDescriptorFactory.fromResource(R.drawable.cst_platform_map_star_small);// 起点图标
        mOverlay = new CarOverlay(this);
        mMapView = (MapView) findViewById(R.id.dynamics_mapView);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true); // 开启定位图层
        UiSettings settings = mBaiduMap.getUiSettings();
        settings.setOverlookingGesturesEnabled(false);//屏蔽双指下拉时变成3D地图
        settings.setRotateGesturesEnabled(false);//关闭地图旋转功能
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(3000);//设置3秒定位一次
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new MyLocationListenner());
        mLocClient.setLocOption(option);
        mLocClient.start();
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        isInUploadStatu = false;
                        mFlag_LocationCar = false;
                        mBtn_location_car.setBackgroundResource(R.drawable.cst_platform_map_car_position_ico_on);
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dynamic_btn_location_car) {
            if (mFlag_LocationCar)//关闭车定位
            {
                mBtn_location_car.setBackgroundResource(R.drawable.cst_platform_map_car_position_ico_on);
                isInUploadStatu = false;
                mFlag_LocationCar = false;
            } else//开启车定位
            {
                if (msUpdate == null) {
                    mBtn_location_car.setBackgroundResource(R.drawable.cst_platform_map_car_position_ico_on);
                    isInUploadStatu = false;
                    mFlag_LocationCar = false;
                    return;
                }
                mBaiduMap.animateMapStatus(msUpdate);
                mBtn_location_car.setBackgroundResource(R.drawable.cst_platform_map_car_follow_ico_on);
                mFlag_LocationCar = true;
                isInUploadStatu = true;
            }
        } else if (v.getId() == R.id.dynamic_btn_location_self) {
            isInUploadStatu = false;
            mFlag_LocationCar = false;
            MyLocationData data = mBaiduMap.getLocationData();
            if (null != data) {
                LatLng ll = new LatLng(data.latitude,
                        data.longitude);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                mBtn_location_car.setBackgroundResource(R.drawable.cst_platform_map_car_position_ico_on);
            }
        } else if (v.getId() == R.id.dynamics_back) {
            finish();
        } else if (v.getId() == R.id.dynamic_btn_roadcondition) {
            SwitchRoadCondition();
        } else if (v.getId() == R.id.track_mile_image || v.getId() == R.id.track_time_image || v.getId() == R.id.track_oil_image) {
            showOrHideViewByXChange(mView_carstatu_mile, 500);
            showOrHideViewByXChange(mView_carstatu_oil, 500);
            showOrHideViewByXChange(mText_carstatu_time, 500);
        }
    }


    /**
     * View动画方法
     *
     * @param btnView  要显示动画的view
     * @param duration 动画时间
     */
    public void showOrHideViewByXChange(View btnView, long duration) {
        float width = btnView.getWidth();
        if (btnView.getX() == 0) {
            ObjectAnimator translationRight = ObjectAnimator.ofFloat(btnView, "X", -width);
            translationRight.setDuration(duration);
            translationRight.start();
        } else {
            ObjectAnimator translationLeft = ObjectAnimator.ofFloat(btnView, "X", 0f);
            translationLeft.setDuration(duration);
            translationLeft.start();
        }
    }


    /**
     * 根据网络变化，改变顶部抬头显示内容
     *
     * @param var1 网络状态布尔值 true:有网络 false:无网络
     */
    @Override
    public void OnNetChangeResult(boolean var1) {
        mFlag_NetisAvailable = var1;
        if (var1) {
            mText_dynamics_title.setText("实时动态");
            startRequestStatusData();
        } else {
            mText_dynamics_title.setText("未连接...");
            stopGetCarLocation();
        }
    }


    /**
     * 车辆实时动态请求Runnable
     */
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mFlag_NetisAvailable)
                VehicleDynamics.newInstance().GetVehicleDynamicsResult(mStartTime, openCarId, VehicleDynamicsActivity.this);
        }
    };

    /**
     * 车动态返回结果
     *
     * @param var1    车动态对象
     * @param isError 请求是否出错
     * @param ex      异常
     */
    @Override
    public void onGetVehicleDynamicsResult(VehicleDynamicsResult var1, boolean isError, Throwable ex) {
        if (isError) {
            requestFail();
            return;
        }

        if (null != var1 && null != var1.getVehdynInfo()) {
            requestSuccess(var1);
        }

        if (isNeedRequest)
            mRequestHandler.postDelayed(mRunnable, timeInterval);
    }

    /**
     * 请求成功，更新状态
     *
     * @param var1
     */
    private void requestSuccess(VehicleDynamicsResult var1) {
        mLastReciverTime = System.currentTimeMillis();
        mText_dynamics_title.setText("实时动态");
        displayStatisticsInfo(var1);
        updateCarStateInfo(var1);
        if (null != var1.getVehdynInfo().getTrajectlist() && var1.getVehdynInfo().getTrajectlist().size() > 0) {
            VehicleDynamicsResult.VehicleDynamicsInfo.TrajectoryInfo Lastinfo = var1.getVehdynInfo().getTrajectlist().get(var1.getVehdynInfo().getTrajectlist().size() - 1);
            mOverlay.setCarRotat(Double.valueOf(Lastinfo.head));
            if (mStartTime == Lastinfo.time) {
                return;
            }
            mStartTime = Lastinfo.time;
            for (int i = 0; i < var1.getVehdynInfo().getTrajectlist().size(); i++) {
                VehicleDynamicsResult.VehicleDynamicsInfo.TrajectoryInfo info = var1.getVehdynInfo().getTrajectlist().get(i);
                if (null != info.lat && null != info.lng && !info.lat.equals("0") && !info.lat.equals("") && !info.lng.equals("0") && !info.lng.equals("")) {
                    LatLng latLng = new LatLng(Double.valueOf(info.lat), Double.valueOf(info.lng));
                    mPointList.add(gpsToBaiduLatLng(latLng));
                }
            }
        }

        if (mPointList.size() == 0) {
            VehicleDynamicsResult.VehicleDynamicsInfo.TrajectoryInfo currentInfo = var1.getVehdynInfo().getTraject();
            if (null != currentInfo) {
                if (null != currentInfo.lat && null != currentInfo.lng && !currentInfo.lat.equals("0") && !currentInfo.lat.equals("") && !currentInfo.lng.equals("0") && !currentInfo.lng.equals("")) {
                    LatLng latLng = new LatLng(Double.valueOf(currentInfo.lat), Double.valueOf(currentInfo.lng));
                    matLng = gpsToBaiduLatLng(latLng);
                    MapStatus mMapStatus = new MapStatus.Builder().target(matLng).zoom(17.0f).build();
                    msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    if (null != currentInfo.head && !currentInfo.head.equals(""))
                        mOverlay.setCarRotat(Double.valueOf(currentInfo.head));
                    addMarker();
                }
            }
            mStartTime = 0;
        } else {
            matLng = mPointList.get(mPointList.size() - 1);
            drawRealtimePoint(matLng);
        }
    }


    /**
     * 将GPS设备采集的原始GPS坐标转换成百度坐标
     */
    public static LatLng gpsToBaiduLatLng(LatLng GpsLatLng) {

        if (GpsLatLng == null) {
            return null;
        }
        //待转换的原始坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(GpsLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    /**
     * 请求失败处理
     */
    private void requestFail() {
        if (System.currentTimeMillis() - mLastReciverTime
                > OUT_TIME_INTERVAL) {
            setNoSignalView();
        }
        if (isNeedRequest) {
            mRequestHandler.postDelayed(mRunnable, timeInterval);
        }
    }

    /**
     * 更新车辆状态
     *
     * @param var1
     */
    private void updateCarStateInfo(VehicleDynamicsResult var1) {
        if (var1.getVehdynInfo().acc == 0)//停车
        {
            //todo 熄火后，对gps状态进行判断代码
//            if (var1.getVehdynInfo().gprs == 0) {
//                mOverlay.ChangeMarkStatuWithType(4, var1.getVehdynInfo().nogprsduration);
//            } else if (var1.getVehdynInfo().gpsvalid == 0) {
//                mOverlay.ChangeMarkStatuWithType(5, var1.getVehdynInfo().invalidduration);
//            } else {
//                mOverlay.ChangeMarkStatuWithType(0, 0);
//            }
            mOverlay.ChangeMarkStatuWithType(0, 0);//如果需要判断，请删除此行
        } else {
            if (var1.getVehdynInfo().gprs == 0) {
                mOverlay.ChangeMarkStatuWithType(2, var1.getVehdynInfo().nogprsduration);
            } else if (var1.getVehdynInfo().gpsvalid == 0) {
                mOverlay.ChangeMarkStatuWithType(3, var1.getVehdynInfo().invalidduration);
            } else {
                mOverlay.ChangeMarkStatuWithType(1, 0);
            }

            if (!TextUtils.isEmpty(var1.getVehdynInfo().speed)) {
                String speedFormat = getString(R.string.cst_platform_car_speed);
                float carSpeed = Float.parseFloat(var1.getVehdynInfo().speed);
                mOverlay.setSpeed(String.format(speedFormat, (int) carSpeed));
            } else {
                mOverlay.setSpeedGone();
            }

            if (!TextUtils.isEmpty(var1.getVehdynInfo().rpm)) {
                String rpmFormat = getString(R.string.cst_platform_car_rpm);
                mOverlay.setturnSpeed(String.format(rpmFormat, var1.getVehdynInfo().rpm));
            } else {
                mOverlay.setTurnSpeedGone();
            }
        }
    }


    /**
     * 显示里程，时间，百公里油耗
     */
    private void displayStatisticsInfo(VehicleDynamicsResult var1) {
        if (!TextUtils.isEmpty(var1.getVehdynInfo().mile))
            mText_carstatu_distance.setText(var1.getVehdynInfo().mile);
        if (!TextUtils.isEmpty(var1.getVehdynInfo().hfuel)) {
            mText_carstatu_consumption.setText(mDecimalFormat
                    .format(Double.parseDouble(var1.getVehdynInfo().hfuel)));
        } else {
            mText_carstatu_consumption.setText("0.0");
        }
        mText_carstatu_time.setText(secToTime(var1.getVehdynInfo().duration));
    }


    /**
     * 绘制实时点
     */
    private void drawRealtimePoint(LatLng point) {
        mBaiduMap.clear();
        startmatLng = mPointList.get(0);
        if (isFirstUploadStatu) {//第一次显示，需要将轨迹框入地图
            double min_lat = mPointList.get(0).latitude, min_lng = mPointList.get(0).longitude,
                    max_lat = mPointList.get(0).latitude, max_lng = mPointList.get(0).longitude;
            for (int i = 0; i <= mPointList.size() - 1; i++) {
                if (min_lat > mPointList.get(i).latitude)
                    min_lat = mPointList.get(i).latitude;
                if (min_lng > mPointList.get(i).longitude)
                    min_lng = mPointList.get(i).longitude;
                if (max_lat < mPointList.get(i).latitude)
                    max_lat = mPointList.get(i).latitude;
                if (max_lng < mPointList.get(i).longitude)
                    max_lng = mPointList.get(i).longitude;
            }
            LatLng southwest = new LatLng(min_lat, min_lng);
            LatLng northeast = new LatLng(max_lat, max_lng);
            LatLng northwest = new LatLng(max_lat, min_lng);
            LatLng southeast = new LatLng(min_lat, max_lng);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(startmatLng).include(point).include(southwest).include(northeast).include(northwest).include(southeast).build();
            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
        } else {
            MapStatus mMapStatus = new MapStatus.Builder().target(point).build();
            msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        }

        if (null == startMarker) {
            startMarker = new MarkerOptions()
                    .position(startmatLng).icon(bmStart)
                    .zIndex(9).draggable(true);
        }

        if (mPointList.size() > 10000) {
            List<LatLng> list = mPointList.subList(0, 9999);
            mPointList.clear();
            mPointList.addAll(list);
        }

        if (mPointList.size() == 1) {
            LatLng latLng = mPointList.get(0);
            mPointList.add(latLng);
        }
        // 添加路线（轨迹）
        polyline = new PolylineOptions().width(10)
                .color(Color.rgb(247, 89, 245)).points(mPointList);
        addMarker();
    }

    /**
     * 添加地图覆盖物
     */
    private void addMarker() {
        if (null != msUpdate && isInUploadStatu) {
            mBaiduMap.animateMapStatus(msUpdate);
            if (mPointList.size() > 0 && isFirstUploadStatu) {
                isFirstUploadStatu = false;
                MapStatus mMapStatus = new MapStatus.Builder().target(matLng).zoom(17.0f).build();
                msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                mBaiduMap.animateMapStatus(msUpdate);
            }
        }
        // 路线覆盖物
        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
        }
        // 实时点覆盖物
        if (null != mOverlay && null != matLng) {
            mInfoWindow = null;
            mInfoWindow = new InfoWindow(mOverlay, matLng, dip2px(62));
            mBaiduMap.showInfoWindow(mInfoWindow);
        }
        if (null != startMarker) {
            mBaiduMap.addOverlay(startMarker);
        }
    }

    /**
     * 重置覆盖物
     */
    private void clearnMark() {
        polyline = null;
        mOverlay = null;
        startMarker = null;
    }

    /**
     * 路况开关设置
     */
    private void SwitchRoadCondition() {
        if (mFlag_RoadCondition) //关闭路况
        {
            mFlag_RoadCondition = false;
            mBtn_roadcondition.setBackgroundResource(R.drawable.cst_platform_roadcondition_normal);
            mBaiduMap.setTrafficEnabled(false);//路况关
        } else//开启路况
        {
            mFlag_RoadCondition = true;
            mBtn_roadcondition.setBackgroundResource(R.drawable.cst_platform_roadcondition_highlighted);
            mBaiduMap.setTrafficEnabled(true);//路况开
        }
    }

    void startRequestStatusData() {
        if (isNeedRequest == true) {
            return;
        }

        //开始请求状态信息
        stopGetCarLocation();
        isNeedRequest = true;
        // 开始获取车辆动态信息
        mRequestHandler.post(mRunnable);
    }

    /**
     * 关闭获取车辆位置数据
     */
    void stopGetCarLocation() {
        isNeedRequest = false;
        mRequestHandler.removeCallbacks(mRunnable);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        stopGetCarLocation();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        startRequestStatusData();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        stopGetCarLocation();
        mLocClient.stop();
        mOverlay.pause();
        clearnMark();
        mMapView.onDestroy();
        if (null != mNetReceiver)
            unregisterReceiver(mNetReceiver);
    }

    /**
     * 注册网络监听
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetReceiver = new ConnectionChangeReceiver(this);
        this.registerReceiver(mNetReceiver, filter);
    }

    /**
     * 设置手机无信号或者接口超时视图
     */
    private void setNoSignalView() {
        mText_dynamics_title.setText("未连接...");
        mOverlay.ChangeMarkStatuWithType(7, 0);
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc && msUpdate == null) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    /**
     * 把一个秒数转换为：00:00:00格式
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0) {
            return "00:00:00";
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }


    public int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
