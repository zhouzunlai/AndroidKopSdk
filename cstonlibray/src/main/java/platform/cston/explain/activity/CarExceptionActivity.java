package platform.cston.explain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.adapter.CarExceptionAdapter;
import platform.cston.explain.bean.CarDetectionEntity;
import platform.cston.explain.bean.CarExceptionBean;
import platform.cston.explain.bean.CstTopTitleInfo;
import platform.cston.httplib.bean.CarConDectionResult;

/**
 * Created by daifei on 2016/7/8.
 */
public class CarExceptionActivity extends CstBaseActivity {

    private ListView lvException;

    private TextView tvLeft;//左边按钮文字
    private TextView tvExceptionLevel;//异常的级别提示

    private String mTitle;//标题
    private String mPtype;//检测类型
    private String mReminder;//提示

    private boolean mStatuFlag;//true代表需要及时处理的异常，false代表需要注意的异常

    private List<CarExceptionBean> mData = new ArrayList<>();

    private CarConDectionResult mCarDetectionParcelable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cst_platform_activity_car_exception);
        getData();
        tvExceptionLevel = (TextView) findViewById(R.id.car_exception_level);
        tvLeft = (TextView) findViewById(R.id.cst_platform_header_left_text);
        lvException = (ListView) findViewById(R.id.lv_car_exception);
        tvLeft.setText(getString(R.string.cst_platform_detect_title));
        setHeaderLeftTextBtn();
        tvExceptionLevel.setText(mReminder);
        if (null != mTitle)
            setHeaderTitle(mTitle);
        if (null != mData)
            lvException.setAdapter(new CarExceptionAdapter(CarExceptionActivity.this, mData, mStatuFlag, mCarDetectionParcelable));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mStatuFlag) {
            setHeadreColor(CstTopTitleInfo.ColorStatus.ERROR);
        } else {
            setHeadreColor(CstTopTitleInfo.ColorStatus.WARN);
        }
    }

    //获取数据
    private void getData() {
        Intent intent = getIntent();
        mStatuFlag = intent.getBooleanExtra("urgency", false);
        mTitle = intent.getStringExtra("title");
        mReminder = intent.getStringExtra("reminder");
        mPtype = intent.getStringExtra("selectType");
        mCarDetectionParcelable = intent.getParcelableExtra("OBD");
        if (null != mCarDetectionParcelable) {
            String time = getStringTime(mCarDetectionParcelable.getData().getObdData().time);//故障发生时间

            if (mPtype.equals(CarDetectionEntity.TYPE_DETECTION_CAR_FAULT)) {
                if (mStatuFlag) {
                    ArrayList<CarConDectionResult.DataEntity.HandleListEntity> mHandleList = mCarDetectionParcelable.getData().getHandleList();
                    for (CarConDectionResult.DataEntity.HandleListEntity entity : mHandleList) {
                        CarExceptionBean bean = new CarExceptionBean();
                        bean.happen_time = time;
                        bean.kind_name = getString(R.string.cst_platform_faiulre_code);
                        bean.excaption_content = entity.dtc;
                        bean.exception_reminder = entity.translationChinese;
                        bean.exception_cause = entity.causeAnalysis;
                        bean.exception_effect = entity.consequences;
                        bean.exception_advice = entity.suggestion;
                        bean.exception_type = CarDetectionEntity.TYPE_DETECTION_CAR_FAULT;
                        mData.add(bean);
                    }
                } else {
                    ArrayList<CarConDectionResult.DataEntity.MindListEntity> mMindList = mCarDetectionParcelable.getData().getMindList();
                    for (CarConDectionResult.DataEntity.MindListEntity entity : mMindList) {
                        CarExceptionBean bean = new CarExceptionBean();
                        bean.happen_time = time;
                        bean.kind_name =  getString(R.string.cst_platform_faiulre_code);
                        bean.excaption_content = entity.dtc;
                        bean.exception_reminder = entity.translationChinese;
                        bean.exception_cause = entity.causeAnalysis;
                        bean.exception_effect = entity.consequences;
                        bean.exception_advice = entity.suggestion;
                        bean.exception_type = CarDetectionEntity.TYPE_DETECTION_CAR_FAULT;
                        mData.add(bean);
                    }
                }
            } else if (mPtype.equals(CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY)) {
                CarExceptionBean bean = new CarExceptionBean();
                bean.happen_time = time;
                bean.kind_name = getString(R.string.cst_platform_detect_battery_voltage);
                bean.excaption_content = getString(R.string.cst_platform_current_value) + mCarDetectionParcelable.getData().getObdData().batteryVoltage+"V";
                bean.exception_reminder = getString(R.string.cst_platform_scope_battery_voltage);
                bean.exception_cause = getString(R.string.cst_platform_cause_battery_voltage);
                bean.exception_effect = getString(R.string.cst_platform_effect_battery_voltage);
                bean.exception_advice = getString(R.string.cst_platform_advice_battery_voltage);
                bean.exception_type = CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY;
                mData.add(bean);
            } else if (mPtype.equals(CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE)) {
                CarExceptionBean bean = new CarExceptionBean();
                bean.happen_time = time;
                bean.kind_name = getString(R.string.cst_platform_detect_coolant_temperature);
                bean.excaption_content = getString(R.string.cst_platform_current_value) + (int) mCarDetectionParcelable.getData().getObdData().coolantCt + "℃";
                bean.exception_reminder = getString(R.string.cst_platform_scope_cooling_fluid);
                bean.exception_cause = getString(R.string.cst_platform_cause_cooling_fluid);
                bean.exception_effect = getString(R.string.cst_platform_effect_cooling_fluid);
                bean.exception_advice = getString(R.string.cst_platform_advice_cooling_fluid);
                bean.exception_type = CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE;
                mData.add(bean);
            }
        }
    }


    private String getStringTime(long num) {
        Log.i("","");
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        String date = sdf.format(new Date(num));
        if (date.startsWith("0")) {
            date = date.substring(1);
        }
        return getString(R.string.cst_platform_happen_time) + " " + date;
    }

}
