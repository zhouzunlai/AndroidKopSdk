package platform.cston.explain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cston.cstonlibray.R;
import platform.cston.explain.utils.DTUtils;
import platform.cston.httplib.bean.DayReportResult;

/**
 * Created by daifei on 2016/8/26.
 */
public class DayEventTypeAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<DayReportResult.DataEntity.EventEntity> data;
    private Map<String, String> mAddressMap = new HashMap();//用于存储地址缓存的MAP

    public DayEventTypeAdapter(Context context, List<DayReportResult.DataEntity.EventEntity> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {


        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cst_platform_day_report_event_item, null);
            holder.tvTime = (TextView) convertView.findViewById(R.id.day_event_time);
            holder.tvType = (TextView) convertView.findViewById(R.id.day_event_type);
            holder.tvPlace = (TextView) convertView.findViewById(R.id.day_event_place);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (data.get(i).time != null) {
            holder.tvTime.setText(DTUtils.StrToHHmm(data.get(i).time));
        } else {
            holder.tvTime.setText("0.0");
        }

        if (data.get(i) != null) {
            getMapListCoordinateConverterGps(data.get(i), holder.tvPlace);
        } else {
            holder.tvPlace.setText("地址数据不存在");
        }


        if (data.get(i).category != null) {
            if (data.get(i).category.equals("security")) {
                holder.tvType.setText("车辆，" + DTUtils.CheckEventName("security", data.get(i).type));
            } else if (data.get(i).category.equals("drivingBehavior")) {
                holder.tvType.setText(DTUtils.CheckEventName("drivingBehavior", data.get(i).type));
            }
        } else {

            holder.tvType.setText("发生了未知事件" + data.get(i).type);

        }
        return convertView;
    }


    /**
     * 将轨迹点转换为百度地图坐标点，并存入MapLatLng集合
     *
     * @param trackItem 轨迹列表对象
     * @return
     */
    private void getMapListCoordinateConverterGps(DayReportResult.DataEntity.EventEntity trackItem, final TextView startPlace) {
        LatLng StartLatLng = null;
        if (!trackItem.latitude.equals("0") && !trackItem.equals("0")) {
            StartLatLng = new LatLng(Double.parseDouble(trackItem.latitude), Double.parseDouble(trackItem.longitude));
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(StartLatLng);
            getStarAndEndPlaceWithLatlng(StartLatLng, startPlace);
        }
    }


    /**
     * 获取轨迹起始地点信息，并显示
     *
     * @param startLatlng//起点坐标
     * @param startPlace//起点控件
     */
    private void getStarAndEndPlaceWithLatlng(LatLng startLatlng, final TextView startPlace) {
        if (null != startLatlng) {
            final String StarPlaceLatKey = "" + startLatlng.latitude + "#" + startLatlng.longitude;
            if (mAddressMap.containsKey(StarPlaceLatKey)) {
                startPlace.setText(mAddressMap.get(StarPlaceLatKey));
            } else {
                GeoCoder startGeoSearch = GeoCoder.newInstance();
                startGeoSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(startLatlng));
                startGeoSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        if (null == reverseGeoCodeResult) {
                            startPlace.setText("--");
                        } else {
                            mAddressMap.put(StarPlaceLatKey, reverseGeoCodeResult.getAddress());
                            startPlace.setText(reverseGeoCodeResult.getAddress());
                        }
                    }
                });
            }
        }

    }


    class ViewHolder {
        TextView tvTime;
        TextView tvPlace;
        TextView tvType;
    }
}
