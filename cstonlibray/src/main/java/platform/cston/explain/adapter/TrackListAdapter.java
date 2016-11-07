package platform.cston.explain.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cston.cstonlibray.R;
import platform.cston.httplib.bean.TrajectoryResult;
import platform.cston.httplib.common.util.MapLatLng;
import platform.cston.httplib.common.util.MapUtils;

public class TrackListAdapter extends BaseAdapter {

    private Context mContext;

    private LinkedList<TrajectoryResult.TrajectoryListInfo> mData;//轨迹列表

    private ArrayList<Integer> mShowDateItemPositions;//用于按实际分段的list
    private LayoutInflater inflater;
    private DisplayImageOptions options;//图片加载设置

    private String DATE_FORMATE = "HH:mm";
    private DecimalFormat df = new DecimalFormat("#0.0");

    private Map<String, String> mAddressMap = new HashMap();//用于存储地址缓存的MAP
    private int mImgHight;
    private int mImgwidth;

    public TrackListAdapter(Context context, LinkedList<TrajectoryResult.TrajectoryListInfo> data) {
        super();
        mContext = context;
        mData = data;
        inflater = LayoutInflater.from(context);
        mShowDateItemPositions = new ArrayList<>();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_4444)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public int getCount() {
        computeShowDateItemPositions();
        return mData == null ? 0 : mData.size();
    }

    private void computeShowDateItemPositions() {
        mShowDateItemPositions.clear();
        if (mData == null || mData.size() < 1) {
            return;
        }

        long tempTime = -1;
        long itemTime;
        final int dateLen = mData.size();
        for (int i = 0; i < dateLen; i++) {
            try {
                itemTime = mData.get(i).startTime;
            } catch (Exception e) {
                itemTime = -2;
            }
            if (!isSameDay(tempTime, itemTime)) {
                mShowDateItemPositions.add(i);
                tempTime = itemTime;
            }
        }
    }

    private boolean isShowDateItem(int position) {
        return mShowDateItemPositions.contains(position);
    }

    @Override
    public TrajectoryResult.TrajectoryListInfo getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cst_platform_track_list_item, parent, false);
            holder.mYearTv = (TextView) convertView.findViewById(R.id.time_year_tv);
            holder.mDayTv = (TextView) convertView.findViewById(R.id.time_day_tv);
            holder.mBeginPlaceTv = (TextView) convertView.findViewById(R.id.begin_place_tv);
            holder.mBeginTimeTv = (TextView) convertView.findViewById(R.id.begin_time_tv);
            holder.mEndPlaceTv = (TextView) convertView.findViewById(R.id.end_place_tv);
            holder.mEndTimeTv = (TextView) convertView.findViewById(R.id.end_time_tv);
            holder.mMileTv = (TextView) convertView.findViewById(R.id.track_mile);
            holder.mDrivingTimeTv = (TextView) convertView.findViewById(R.id.driving_time);
            holder.mTrackImg = (ImageView) convertView.findViewById(R.id.track_img);
            holder.mTimeLineV = (View) convertView.findViewById(R.id.item_vertical_line_v);
            holder.mSmallDot = (RelativeLayout) convertView.findViewById(R.id.left_normal_icon);
            holder.mDateDot = (RelativeLayout) convertView.findViewById(R.id.left_date_icon);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.loading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mBeginPlaceTv.setText("--");
        holder.mEndPlaceTv.setText("--");
        holder.mTrackImg.setImageBitmap(null);

        TrajectoryResult.TrajectoryListInfo trackItem = getItem(position);
        if (null != trackItem) {
            RelativeLayout.LayoutParams params
                    = (RelativeLayout.LayoutParams) holder.mTimeLineV
                    .getLayoutParams();
            int dataLen = mData.size();
            long startTime = trackItem.startTime;
            long endTime = trackItem.stopTime;
            holder.mBeginTimeTv.setText("(" + getDate(startTime, DATE_FORMATE) + ")");
            holder.mEndTimeTv.setText("(" + getDate(endTime, DATE_FORMATE) + ")");
            holder.mYearTv.setText(getYYYY(startTime));
            holder.mDayTv.setText(getMD(startTime));
            holder.mMileTv.setText("总里程: " + df.format(trackItem.mileage) + "km");
            holder.mDrivingTimeTv.setText("行驶时长: " + Math.round(trackItem.duration / 60) + "min");
            // 如果只有一项
            if (position == 0 && dataLen == 1) {
                params.height = dip2px(mContext, 0);
            }
            // 如果是最后一项
            else if (position == dataLen - 1) {
                params.height = dip2px(mContext, 36);
                params.setMargins(0, dip2px(mContext, 0), 0, 0);
            } else {
                params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                // 如果是第一项
                if (position == 0) {
                    params.setMargins(0, dip2px(mContext, 36), 0, 0);
                } else {
                    params.setMargins(0, dip2px(mContext, 0), 0, 0);
                }
            }

            if (isShowDateItem(position)) {
                holder.mDateDot.setVisibility(View.VISIBLE);
                holder.mSmallDot.setVisibility(View.GONE);
            } else {
                holder.mSmallDot.setVisibility(View.VISIBLE);
                holder.mDateDot.setVisibility(View.GONE);
            }
            if (null != trackItem.getTrajectlist() && trackItem.getTrajectlist().size() > 0) {
                ArrayList<MapLatLng> latLngs = getMapListCoordinateConverterGps(trackItem, holder.mBeginPlaceTv, holder.mEndPlaceTv);//将轨迹点转换为百度地图坐标点，并存入MapLatLng类集合
                LoadImgewithMapLatlng(latLngs, holder.mTrackImg, holder.progressBar);
            }
        }

        return convertView;
    }


    /**
     * 将轨迹点转换为百度地图坐标点，并存入MapLatLng集合
     *
     * @param trackItem 轨迹列表对象
     * @return
     */
    private ArrayList<MapLatLng> getMapListCoordinateConverterGps(TrajectoryResult.TrajectoryListInfo trackItem, final TextView startPlace, final TextView endPlace) {
        ArrayList<MapLatLng> latLngs = new ArrayList<>();
        LatLng StartLatLng = null;
        LatLng EndLatLng = null;
        for (int i = 0; i < trackItem.getTrajectlist().size(); i++) {
            if (trackItem.getTrajectlist().get(i).latitude != 0 && trackItem.getTrajectlist().get(i).longitude != 0) {
                LatLng latLng = new LatLng(trackItem.getTrajectlist().get(i).latitude, trackItem.getTrajectlist().get(i).longitude);
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                converter.coord(latLng);
                LatLng desendLatLng = converter.convert();
                MapLatLng info = new MapLatLng();
                info.setLatitude(desendLatLng.latitude);
                info.setLongitude(desendLatLng.longitude);
                latLngs.add(info);
            }
        }
        if (latLngs.size() > 0) {
            StartLatLng = new LatLng(latLngs.get(0).getLatitude(), latLngs.get(0).getLongitude());
            EndLatLng = new LatLng(latLngs.get(latLngs.size() - 1).getLatitude(), latLngs.get(latLngs.size() - 1).getLongitude());
            getStarAndEndPlaceWithLatlng(StartLatLng, EndLatLng, startPlace, endPlace);
        }
        return latLngs;
    }


    /**
     * 获取轨迹起始地点信息，并显示
     *
     * @param startLatlng//起点坐标
     * @param endLatlng//终点坐标
     * @param startPlace//起点控件
     * @param endPlace//终点控件
     */
    private void getStarAndEndPlaceWithLatlng(LatLng startLatlng, LatLng endLatlng, final TextView startPlace, final TextView endPlace) {
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

        if (null != endLatlng) {
            final String EndPlaceLatKey = "" + endLatlng.latitude + "#" + endLatlng.longitude;
            if (mAddressMap.containsKey(EndPlaceLatKey)) {
                endPlace.setText(mAddressMap.get(EndPlaceLatKey));
            } else {
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
                            endPlace.setText("--");
                        } else {
                            mAddressMap.put(EndPlaceLatKey, reverseGeoCodeResult.getAddress());
                            endPlace.setText(reverseGeoCodeResult.getAddress());
                        }
                    }
                });
            }
        }
    }


    /**
     * 通过坐标集合获取地图图片，并显示
     *
     * @param latLngs     坐标集合
     * @param mTrackImg
     * @param progressBar
     */
    private void LoadImgewithMapLatlng(final ArrayList<MapLatLng> latLngs, final ImageView mTrackImg, final ProgressBar progressBar) {
        if (latLngs.size() > 0) {
            if (mImgHight == 0 && mImgwidth == 0) {
                ViewTreeObserver vto2 = mTrackImg.getViewTreeObserver();
                vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mTrackImg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        mImgHight = mTrackImg.getHeight();
                        mImgwidth = mTrackImg.getWidth();
                        LoadImage(latLngs, mTrackImg, progressBar);
                    }
                });
            } else {
                LoadImage(latLngs, mTrackImg, progressBar);
            }
        }
    }

    private void LoadImage(final ArrayList<MapLatLng> latLngs, final ImageView mTrackImg, final ProgressBar progressBar) {
        String imgurl = MapUtils.createBaiduTraceImageUri(latLngs, mImgwidth == 0 ? 285 : (mImgwidth / 2), mImgHight == 0 ? 100 : (mImgHight / 2));
        ImageLoader.getInstance().displayImage(imgurl, mTrackImg, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }


    class ViewHolder {
        TextView mYearTv;
        TextView mDayTv;
        TextView mBeginPlaceTv;
        TextView mBeginTimeTv;
        TextView mEndPlaceTv;
        TextView mEndTimeTv;
        TextView mMileTv;
        TextView mDrivingTimeTv;
        ImageView mTrackImg;
        View mTimeLineV;
        RelativeLayout mSmallDot;
        RelativeLayout mDateDot;
        ProgressBar progressBar;
    }

    /**
     * 判断两个时间是同一天
     *
     * @param day1Milliseconds
     * @param day2Milliseconds
     * @return
     */
    public boolean isSameDay(long day1Milliseconds, long day2Milliseconds) {
        if (day1Milliseconds == day2Milliseconds) {
            return true;
        }
        try {
            final Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(day1Milliseconds);

            final Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(day2Milliseconds);

            return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                    && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                    && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取字符窜日期
     *
     * @param formate eg: str = "yyyy-MM-dd HH:mm:ss";
     */
    public String getDate(long time, String formate) {
        String str = formate;

        Date date = new Date(time);
        SimpleDateFormat simpleDate = new SimpleDateFormat(
                str);
        String currentDate = simpleDate.format(date);
        return currentDate;
    }

    public String getYYYY(long timeNow) {
        return getDate(timeNow, "yyyy");
    }

    public String getMD(long timeNow) {
        return getDate(timeNow, "MM/dd");
    }


    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
