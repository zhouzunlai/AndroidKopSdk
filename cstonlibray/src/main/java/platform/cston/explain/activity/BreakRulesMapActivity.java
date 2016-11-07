package platform.cston.explain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import cston.cstonlibray.R;
import platform.cston.explain.bean.CstTopTitleInfo;
import platform.cston.explain.utils.DTUtils;
import platform.cston.httplib.bean.IllegalRecordResult;

/**
 * 违章地点地图展示
 * Created by zhou on 2016/9/9.
 */
public class BreakRulesMapActivity extends CstBaseActivity {

    private boolean isFirstLoc = true; // 是否首次定位
    private MapView mMapView;//百度地图
    private BaiduMap mBaiduMap;//地图管理

    private BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.cst_platform_car_event_car_icon);//在地图上展示的Marker

    private View mPopView;//展示框的页面View

    private InfoWindow mInfoWindow;//点击Marker后弹出的展示框

    private String mOpenCarId;//查询车辆的id

    private long mTimeStamp;//由字符串时间格式转换的时间戳，用于请求车辆轨迹信息

    private IllegalRecordResult.DataEntity.ListsEntity mDateInfo;//弹出层显示的内容，由点击的item传递而来

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mOpenCarId = intent.getStringExtra("OPENCARID");
        mDateInfo = intent.getParcelableExtra("INFO");
        if (null != mDateInfo)
            mTimeStamp = DTUtils.StrTimeToLongLine(mDateInfo.date)/1000;
        else {
            return;
        }
        setContentView(R.layout.cst_platform_activity_carmapstatu);
        setHeaderLeftTextBtn();
        leftTv.setText("违章列表");
        setHeaderTitle("违章");
        initMapView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setHeadreColor(CstTopTitleInfo.ColorStatus.ERROR);
    }

    /**
     * 初始化地图以及
     * 弹出层控件，布局
     */
    private void initMapView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mPopView = inflater.inflate(R.layout.cst_platform_break_rules_map_pop, null);
        mMapView = (MapView) findViewById(R.id.statu_mapview);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (null != marker) {
                    final LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(mPopView, ll, -dip2px(70));
                    popupInfo(mPopView, mDateInfo);
                }
                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        if(mDateInfo.latitude!=0&&mDateInfo.longitude!=0)
        {
            LatLng latLng = new LatLng(mDateInfo.latitude, mDateInfo.longitude);
            addMarker(latLng);
        }
    }


    /**
     * 根据info为布局上的控件设置信息
     *
     * @param info
     */
    private void popupInfo(View mMarkerLy, IllegalRecordResult.DataEntity.ListsEntity info) {
        ViewHolder viewHolder;
        if (mMarkerLy.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.tv_Relus_popmap_time = (TextView) mMarkerLy.findViewById(R.id.relus_popmap_time);
            viewHolder.tv_Relus_popmap_score = (TextView) mMarkerLy.findViewById(R.id.relus_popmap_score);
            viewHolder.tv_Relus_popmap_money = (TextView) mMarkerLy.findViewById(R.id.relus_popmap_money);
            viewHolder.tv_Relus_popmap_address = (TextView) mMarkerLy.findViewById(R.id.relus_popmap_address);
            viewHolder.tv_Relus_popmap_conten = (TextView) mMarkerLy.findViewById(R.id.relus_popmap_conten);
            mMarkerLy.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) mMarkerLy.getTag();
        viewHolder.tv_Relus_popmap_time.setText(DTUtils.LongToStrYMDHMSL(DTUtils.StrTimeToLongLine(info.date)));
        viewHolder.tv_Relus_popmap_score.setText(info.fen);
        viewHolder.tv_Relus_popmap_money.setText(info.money);
        viewHolder.tv_Relus_popmap_address.setText(info.area);
        viewHolder.tv_Relus_popmap_conten.setText(info.act);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }


    private class ViewHolder {
        TextView tv_Relus_popmap_time;
        TextView tv_Relus_popmap_score;
        TextView tv_Relus_popmap_money;
        TextView tv_Relus_popmap_address;
        TextView tv_Relus_popmap_conten;
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
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());

                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

    }


    /**
     * 添加地图覆盖物
     */
    private void addMarker(LatLng latLng) {
        LatLng baiduLatlng = gpsToBaiduLatLng(latLng);
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(baiduLatlng).zoom(16f)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        MarkerOptions ooA = new MarkerOptions().position(baiduLatlng).icon(bdA)
                .zIndex(9).draggable(true);
        mBaiduMap.addOverlay(ooA);
    }

    /**
     * 将GPS设备采集的原始GPS坐标转换成百度坐标
     */
    public LatLng gpsToBaiduLatLng(LatLng GpsLatLng) {

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


    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
    }

    public int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
