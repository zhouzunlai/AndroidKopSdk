package platform.cston.explain.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
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
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.receiver.ConnectionChangeReceiver;
import platform.cston.explain.receiver.OnNetChangeListener;
import platform.cston.httplib.bean.TrajectoryDetailResult;
import platform.cston.httplib.search.OnResultListener;
import platform.cston.httplib.search.TrajectoryDetailSearch;


/**
 * 历史轨迹详情页
 * Created by zhou-pc on 2016/4/9.
 */
public class TrackDetailActivity extends Activity implements View.OnClickListener, OnNetChangeListener {

    private MapView mMapView;//百度地图
    private BaiduMap mBaiduMap;//地图管理
    private LocationClient mLocClient;//定位相关

    private ImageView mBtn_location_self;//定位自己
    private ImageView mBtn_roadcondition;//路况
    private TextView mText_dynamics_title;//页面标题


    private ImageView mImg_carstatu_distance;//左上角，行驶距离图标
    private TextView mText_carstatu_distance;//左上角，行驶距离
    private ImageView mImg_carstatu_time;//左上角，行驶时间图标
    private TextView mText_carstatu_time;//左上角，行驶时间
    private ImageView mImg_carstatu_consumption;//左上角，百公里油耗图标
    private TextView mText_carstatu_consumption;//左上角，百公里油耗

    private TextView mText_begin_time;//开始时间
    private TextView mText_begin_place;//开始地点
    private TextView mText_end_time;//结束时间
    private TextView mText_end_place;//结束地点

    private LinearLayout mView_carstatu_mile;//距离View,用于实现动画
    private LinearLayout mView_carstatu_oil;//油耗View,用于实现动画
    private LinearLayout mView_back;//返回按钮

    private ConnectionChangeReceiver mNetReceiver;//广播，监听网络变化
    private BitmapDescriptor bmStart;// 起点图标
    private BitmapDescriptor bmEnd; // 终点图标
    private MarkerOptions startMarker = null;// 起点图标覆盖物
    private MarkerOptions endMarker = null;// 终点图标覆盖物
    private PolylineOptions polyline = null; // 路线覆盖物
    private MapStatusUpdate msUpdate = null;//地图状态

    private DecimalFormat mDecimalFormat = new DecimalFormat("#####0.0");

    private boolean mFlag_RoadCondition;//路况开关
    private boolean isFirstLoc; // 是否首次定位
    private String openTraceId;//轨迹id
    private String openCarId;//车id
    private double Offset = 0.00005;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        openTraceId = getIntent().getStringExtra("TraceId");//上一页面传递的轨迹id
        openCarId = getIntent().getStringExtra("OpenCarId");//上一页面传递的车id
        setContentView(R.layout.cst_platform_activity_track_detail);
        initMapView();
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

        mText_begin_time = (TextView) findViewById(R.id.begin_time_tv);
        mText_begin_place = (TextView) findViewById(R.id.begin_place_tv);
        mText_end_time = (TextView) findViewById(R.id.end_time_tv);
        mText_end_place = (TextView) findViewById(R.id.end_place_tv);

        mView_back.setOnClickListener(this);
        mBtn_location_self.setOnClickListener(this);
        mBtn_roadcondition.setOnClickListener(this);
        mImg_carstatu_time.setOnClickListener(this);
        mImg_carstatu_distance.setOnClickListener(this);
        mImg_carstatu_consumption.setOnClickListener(this);
        registerReceiver();
        getTrajectoryDetail(openCarId, openTraceId);
    }

    /**
     * 初始化地图相关
     */
    private void initMapView() {
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
        option.setScanSpan(30000);//设置30秒定位一次
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new MyLocationListenner());
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dynamic_btn_location_self) {
            MyLocationData data = mBaiduMap.getLocationData();
            if (null != data) {
                LatLng ll = new LatLng(data.latitude,
                        data.longitude);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
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
        if (var1)
            mText_dynamics_title.setText("历史轨迹");
        else
            mText_dynamics_title.setText("无网络...");
    }


    /**
     * 获取轨迹详情
     *
     * @param lOpenCarId   车id
     * @param lOpenTraceId 查询的轨迹id，来自于车轨迹列表
     */
    private void getTrajectoryDetail(String lOpenCarId, String lOpenTraceId) {
        TrajectoryDetailSearch.newInstance().GetHistoryTrajectoryDetailResult(lOpenCarId, lOpenTraceId, new OnResultListener.OnGetTrajectoryDetailResultListener() {
            @Override
            public void onGetTrajectoryDetailResult(TrajectoryDetailResult var1, boolean isError, Throwable ex) {
                if (!isError && null != var1) {
                    setTrackTextInfo(var1);//设置轨迹显示内容
                    drawHistoryTrack(CoordinateConverterGps(var1));//转换坐标点，并在地图上绘制轨迹
                } else {
                    Toast.makeText(TrackDetailActivity.this, "当前查询无轨迹详情", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 更新轨迹显示内容
     *
     * @param var 轨迹详情对象
     */
    private void setTrackTextInfo(TrajectoryDetailResult var) {
        mText_carstatu_distance.setText(mDecimalFormat.format(
                Math.min(var.mileage, 999.9)));
        mText_carstatu_time.setText(secToTime((int) var.duration));
        mText_carstatu_consumption.setText(mDecimalFormat
                .format(getFuel100Km(var.mileage,
                        var.fuel)));
        if (var.startTime != 0)
            mText_begin_time.setText("(" + getHM(var.startTime) + ")");
        if (var.stopTime != 0)
            mText_end_time.setText("(" + getHM(var.stopTime) + ")");
    }


    /**
     * 将GPS坐标集合转换为百度地图坐标集合
     *
     * @param var 轨迹详情对象
     * @return List<LatLng> 转换后坐标集合
     */
    private List<LatLng> CoordinateConverterGps(TrajectoryDetailResult var) {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < var.getTrajectlist().size(); i++) {
            if (var.getTrajectlist().get(i).latitude != 0 && var.getTrajectlist().get(i).longitude != 0) {
                LatLng latLng = new LatLng(var.getTrajectlist().get(i).latitude, var.getTrajectlist().get(i).longitude);
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                converter.coord(latLng);
                LatLng desendLatLng = converter.convert();
                points.add(desendLatLng);
            }
        }
        return points;
    }


    /**
     * 绘制历史轨迹
     *
     * @param points
     */
    private void drawHistoryTrack(final List<LatLng> points) {
        // 绘制新覆盖物前，清空之前的覆盖物
        mBaiduMap.clear();
        if (points == null || points.size() == 0) {
            Looper.prepare();
            Toast.makeText(TrackDetailActivity.this, "当前查询无轨迹点", Toast.LENGTH_SHORT).show();
            Looper.loop();
            resetMarker();
        } else if (points.size() >= 1) {
            LatLng startLatLng = new LatLng(points.get(0).latitude, points.get(0).longitude);
            LatLng endLatLng = new LatLng(points.get(points.size() - 1).latitude, points.get(points.size() - 1).longitude);
            setStarAndendPlace(startLatLng, endLatLng);//设置起始点位置名称
            setMapStatu(points);//设置地图显示区域
            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.cst_platform_map_star_small);
            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.cst_platform_map_end_small);
            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(startLatLng).icon(bmStart)
                    .zIndex(9).draggable(true);
            // 添加终点图标
            endMarker = new MarkerOptions().position(endLatLng)
                    .icon(bmEnd).zIndex(9).draggable(true);
            if (points.size() > 1) {
                // 添加路线（轨迹）
                polyline = new PolylineOptions().width(10)
                        .color(Color.rgb(247, 89, 245)).points(points);
            }
            addMarker();
        }
    }


    /**
     * 获取轨迹起始位置地址信息
     *
     * @param starLatlng 起始点位置坐标
     * @param endLatlng  结束点位置坐标
     */
    private void setStarAndendPlace(LatLng starLatlng, LatLng endLatlng) {
        GeoCoder startGeoSearch = GeoCoder.newInstance();
        startGeoSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(starLatlng));
        startGeoSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (null == reverseGeoCodeResult) {
                    mText_begin_place.setText("--");
                } else {
                    mText_begin_place.setText(reverseGeoCodeResult.getAddressDetail().street);
                }
            }
        });

        GeoCoder endGeoSearch = GeoCoder.newInstance();
        endGeoSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(endLatlng));
        endGeoSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (null == reverseGeoCodeResult) {
                    mText_end_place.setText("--");
                } else {
                    mText_end_place.setText(reverseGeoCodeResult.getAddressDetail().street);
                }
            }
        });
    }

    /**
     * 计算地图显示区域
     *
     * @param point 坐标集合
     */
    private void setMapStatu(List<LatLng> point) {
        try {
            double min_latitude = point.get(0).latitude, min_longitude = point.get(0).longitude,
                    max_latitude = point.get(0).latitude, max_longitude = point.get(0).longitude;
            for (int i = 0; i <= point.size() - 1; i++) {
                if (min_latitude > point.get(i).latitude)
                    min_latitude = point.get(i).latitude;
                if (min_longitude > point.get(i).longitude)
                    min_longitude = point.get(i).longitude;
                if (max_latitude < point.get(i).latitude)
                    max_latitude = point.get(i).latitude;
                if (max_longitude < point.get(i).longitude)
                    max_longitude = point.get(i).longitude;
            }

            min_latitude = min_latitude - Offset;
            min_longitude = min_longitude - Offset;
            max_latitude = max_latitude + Offset;
            max_longitude = max_longitude + Offset;
            LatLng southwest = new LatLng(min_latitude, min_longitude);
            LatLng northeast = new LatLng(max_latitude, max_longitude);
            LatLng northwest = new LatLng(max_latitude, min_longitude);
            LatLng southeast = new LatLng(min_latitude, max_longitude);

            LatLng llC = point.get(0);
            LatLng llD = point.get(point.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).include(southwest).include(northeast).include(northwest).include(southeast).build();
            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
        } catch (Exception e) {

        }
    }

    /**
     * 添加覆盖物
     */
    protected void addMarker() {

        if (null != msUpdate) {
            mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mBaiduMap.animateMapStatus(msUpdate);
                }
            });
            mBaiduMap.animateMapStatus(msUpdate);
        }

        if (null != startMarker) {
            mBaiduMap.addOverlay(startMarker);
        }

        if (null != endMarker) {
            mBaiduMap.addOverlay(endMarker);
        }

        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
        }
    }

    /**
     * 重置覆盖物
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polyline = null;
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


    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        if (null != mMapView)
            mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        if (null != mMapView)
            mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件\
        if (null != mLocClient)
            mLocClient.stop();
        if (null != mMapView)
            mMapView.onDestroy();
        if (null != mBaiduMap)
            mBaiduMap = null;
        resetMarker();
        if (null != mNetReceiver)//注销广播
            unregisterReceiver(mNetReceiver);
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetReceiver = new ConnectionChangeReceiver(this);
        this.registerReceiver(mNetReceiver, filter);
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
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
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
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }

    /**
     * 算100KM油耗
     */
    public static double getFuel100Km(double mile, double fuel) {
        if (fuel <= 0) {
            return 0.0;
        }
        return mile <= 0 ? 99.9 : Math.min(100 * (fuel / mile), 99.9);
    }


    public static String getHM(long timeNow) {
        return getDate(timeNow, "HH:mm");
    }

    /**
     * 获取字符窜日期
     *
     * @param formate eg: str = "yyyy-MM-dd HH:mm:ss";
     */
    public static String getDate(long time, String formate) {
        String str = formate;

        Date date = new Date(time);
        SimpleDateFormat simpleDate = new SimpleDateFormat(
                str);
        String currentDate = simpleDate.format(date);
        return currentDate;
    }


}
