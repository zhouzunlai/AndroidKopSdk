package platform.cston.explain.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.adapter.ConditionResultNormalAdapter;
import platform.cston.explain.bean.CarDetectionEntity;
import platform.cston.explain.bean.CstTopTitleInfo;
import platform.cston.explain.bean.ObdBean;
import platform.cston.httplib.bean.CarConDectionResult;
import platform.cston.httplib.bean.ObdInfoResult;


public class CarConditionDetailActivity extends CstBaseActivity {

    private TextView tvLeft;
    private TextView tvDetailReminder;

    private ListView mListView;

    private List<ObdBean> mNormalList;

    private List<ObdBean> mAbnormalList;

    private ConditionResultNormalAdapter mAdapter;

    private String mSelectType;
    private String mTitle;//头部不标题

    private int mAcc;
    private int mLevel;//用来判断头部该设置何种颜色
    private int mCoolantPosition = 5;

    private boolean isVisible;//是否显示头部下面的小标题

    private DecimalFormat mDecimalFormat = new DecimalFormat("#####0.0");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cst_platform_car_condition_res_normal);
        initCommonData();
        initView();
        initListener();//注册监听事件
    }

    private void initView() {
        mAdapter = new ConditionResultNormalAdapter(CarConditionDetailActivity.this, mNormalList, mAbnormalList,
                mAcc, mSelectType);

        tvLeft = (TextView) findViewById(R.id.cst_platform_header_left_text);
        mListView = (ListView) findViewById(R.id.detail_list);
        tvDetailReminder = (TextView) findViewById(R.id.tv_detail_reminder);
        setHeaderTitle(mTitle);
        setHeaderLeftTextBtn();
        mListView.setAdapter(mAdapter);
        if (isVisible) {//设置为可见
            tvDetailReminder.setVisibility(View.VISIBLE);
            tvDetailReminder.setText(getIntent().getStringExtra("reminder"));
        } else {
            tvDetailReminder.setVisibility(View.GONE);
        }
        if (mSelectType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE)) {//当点击冷却液温度项的时候，将其移动中屏幕中间
            mListView.setSelection(mCoolantPosition);
            if (mTitle.equals(getString(R.string.cst_platform_condiiton_data_analysis))) {
                tvLeft.setText(getString(R.string.cst_platform_detect_coolant_temperature));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLevel == CarDetectionEntity.DETECTION_NORMAL) {
            setHeadreColor(CstTopTitleInfo.ColorStatus.NORMAL);
            tvDetailReminder.setBackgroundResource(R.color.cst_platform_detect_color_normal);
        }
        if (mLevel == CarDetectionEntity.DETECTION_WARNING) {
            setHeadreColor(CstTopTitleInfo.ColorStatus.WARN);
            tvDetailReminder.setBackgroundResource(R.color.cst_platform_detect_color_warn);
        }
        if (mLevel == CarDetectionEntity.DETECTION_ERROR) {
            setHeadreColor(CstTopTitleInfo.ColorStatus.ERROR);
            tvDetailReminder.setBackgroundResource(R.color.cst_platform_detect_color_error);
        }

    }

    private void initCommonData() {
        mNormalList = new ArrayList<>();
        mAbnormalList = new ArrayList<>();
        mTitle = getIntent().getStringExtra("title");
        if (mTitle == null) {
            mTitle = "车况详情";
        }
        mSelectType = getIntent().getStringExtra("selectType");
        if (mSelectType == null) {
            mSelectType = "";
        }
        mLevel = getIntent().getIntExtra("level", 0);
        isVisible = getIntent().getBooleanExtra("visible", false);
        CarConDectionResult obdInfo;
        CarConDectionResult.DataEntity dataEntity;
        CarConDectionResult.DataEntity.ObdDataEntity obdDataEntity = new CarConDectionResult.DataEntity.ObdDataEntity();//核心数据
        ObdInfoResult.ObdInfo info = getIntent().getParcelableExtra("OBDINFO");//获取数据
        obdInfo = getIntent().getParcelableExtra("OBD");
        if (obdInfo != null) {
            dataEntity = obdInfo.getData();
            obdDataEntity = dataEntity.getObdData();//获得最终的Obd数据
            mAcc = dataEntity.acc;
        } else {
            obdDataEntity.batteryVoltage = info.batteryVoltage;
            obdDataEntity.mileage = info.mileage;
            obdDataEntity.fuel = info.fuel;
            obdDataEntity.malfunctionNum = info.malfunctionNum;
            obdDataEntity.troubleMileage = info.troubleMileage;
            obdDataEntity.perResidualFuel = info.perResidualFuel;
            obdDataEntity.residualFuel = info.residualFuel;
            obdDataEntity.rpm = info.rpm;
            obdDataEntity.speed = info.speed;
            obdDataEntity.onflowCt = info.onflowCt;
            obdDataEntity.coolantCt = info.coolantCt;
            obdDataEntity.environmentCt = info.environmentCt;
            obdDataEntity.airPressure = info.airPressure;
            obdDataEntity.fuelPressure = info.fuelPressure;
            obdDataEntity.airFlow = info.airFlow;
            obdDataEntity.pedalPosition = info.pedalPosition;
            obdDataEntity.engineRuntime = info.engineRuntime;
            obdDataEntity.enginePayload = info.enginePayload;
            obdDataEntity.tvp = info.tvp;
            obdDataEntity.lfuelTrim = info.lfuelTrim;
            obdDataEntity.ciaa = info.ciaa;
        }


        ObdBean batteryVoltage = new ObdBean();
        batteryVoltage.kind_name = getString(R.string.cst_platform_condiiton_batteryVoltage);
        batteryVoltage.tipString = getString(R.string.cst_platform_condiiton_battery_voltage_capacity);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.batteryVoltage))) {
            batteryVoltage.current_value = DoubleAccuracy(obdDataEntity.batteryVoltage);
            batteryVoltage.abnormal = setAbnormal(batteryVoltage.current_value, 11.5, 15.0);
        } else {
            batteryVoltage.support = false;
            batteryVoltage.abnormal = false;
        }
        batteryVoltage.normal_value = "11.5-15.0";
        mNormalList.add(batteryVoltage);

        ObdBean mileage = new ObdBean();
        mileage.kind_name = getString(R.string.cst_platform_condiiton_mileage);
        mileage.tipString = getString(R.string.cst_platform_condiiton_total_mileage);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.mileage))) {
            mileage.current_value = DoubleAccuracy(obdDataEntity.mileage);
        } else {
            mileage.support = false;
        }
        mileage.normal_value = "";
        mNormalList.add(mileage);

        ObdBean fuel = new ObdBean();
        fuel.kind_name = getString(R.string.cst_platform_condiiton_fuel);
        fuel.tipString = getString(R.string.cst_platform_condiiton_total_fuel);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.fuel))) {
            fuel.current_value = DoubleAccuracy(obdDataEntity.fuel);
        } else {
            fuel.support = false;
        }
        fuel.normal_value = "";
        mNormalList.add(fuel);

        ObdBean malfunctionNum = new ObdBean();
        malfunctionNum.kind_name = getString(R.string.cst_platform_condiiton_malfunctionNum);
        malfunctionNum.tipString = getString(R.string.cst_platform_condiiton_number_of_failures);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.malfunctionNum))) {
            malfunctionNum.current_value = (int) obdDataEntity.malfunctionNum;
        } else {
            malfunctionNum.support = false;
        }
        malfunctionNum.normal_value = "";
        mNormalList.add(malfunctionNum);

        ObdBean troubleMileage = new ObdBean();
        troubleMileage.kind_name = getString(R.string.cst_platform_condiiton_troubleMileage);
        troubleMileage.tipString = getString(R.string.cst_platform_condiiton_total_failures_mileage);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.troubleMileage))) {
            troubleMileage.current_value = (int) obdDataEntity.troubleMileage;
        } else {
            troubleMileage.support = false;
        }
        troubleMileage.normal_value = "";
        mNormalList.add(troubleMileage);


        ObdBean perResidualFuel = new ObdBean();
        perResidualFuel.kind_name = getString(R.string.cst_platform_condiiton_perResidualFuel);
        perResidualFuel.tipString = getString(R.string.cst_platform_condiiton_residual_oil_percentage);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.perResidualFuel))) {
            perResidualFuel.current_value = DoubleAccuracy(obdDataEntity.perResidualFuel);
            perResidualFuel.abnormal = setAbnormal(perResidualFuel.current_value, 0.0, 100.0);
        } else {
            perResidualFuel.support = false;
            perResidualFuel.abnormal = false;
        }
        perResidualFuel.normal_value = "0-100";

        ObdBean residualFuel = new ObdBean();
        residualFuel.kind_name = getString(R.string.cst_platform_condiiton_residualFuel);
        residualFuel.tipString = getString(R.string.cst_platform_condiiton_remaining_oil);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.residualFuel))) {
            residualFuel.current_value = DoubleAccuracy(obdDataEntity.residualFuel);
        } else {
            residualFuel.support = false;
        }
        residualFuel.normal_value = "";
        mNormalList.add(showBean(perResidualFuel, residualFuel));

        //将其他数据加入mAbnormalList
        ObdBean rpm = new ObdBean();
        rpm.kind_name = getString(R.string.cst_platform_condiiton_rpm);
        rpm.tipString = getString(R.string.cst_platform_condiiton_rotating_laps);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.rpm))) {
            rpm.current_value = DoubleAccuracy(obdDataEntity.rpm);
            rpm.abnormal = setAbnormal(rpm.current_value, 0.0, 6000.0);
        } else {
            rpm.support = false;
            rpm.abnormal = false;
        }
        rpm.normal_value = "0-6000";
        mAbnormalList.add(rpm);

        ObdBean speed = new ObdBean();
        speed.kind_name = getString(R.string.cst_platform_condiiton_speed);
        speed.tipString = getString(R.string.cst_platform_condiiton_rotating_laps);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.speed))) {
            speed.current_value = DoubleAccuracy(obdDataEntity.speed);
            speed.abnormal = setAbnormal(speed.current_value, 0.0, 160.0);
        } else {
            speed.support = false;
            speed.abnormal = false;
        }
        speed.normal_value = "0-160";
        mAbnormalList.add(speed);

        ObdBean onflowCt = new ObdBean();
        onflowCt.kind_name = getString(R.string.cst_platform_condiiton_onflowCt);
        onflowCt.tipString = getString(R.string.cst_platform_condiiton_air_temperature);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.onflowCt))) {
            onflowCt.current_value = DoubleAccuracy(obdDataEntity.onflowCt);
            onflowCt.abnormal = setAbnormal(onflowCt.current_value, -40.0, 80.0);
        } else {
            onflowCt.support = false;
            onflowCt.abnormal = false;
        }
        onflowCt.normal_value = "-40-80";
        mAbnormalList.add(onflowCt);


        ObdBean coolantCt = new ObdBean();
        coolantCt.kind_name = getString(R.string.cst_platform_condiiton_coolantCt);
        coolantCt.tipString = getString(R.string.cst_platform_condiiton_coolant_temperature);

        if (isParamSupportedTemperature(Double.toString(obdDataEntity.coolantCt))) {
            coolantCt.current_value = DoubleAccuracy(obdDataEntity.coolantCt);
            coolantCt.abnormal = setAbnormal(coolantCt.current_value, -40.0, 110.0);
        } else {
            coolantCt.support = false;
            coolantCt.abnormal = false;
        }
        coolantCt.normal_value = "-40-110";
        mAbnormalList.add(coolantCt);

        ObdBean environmentCt = new ObdBean();
        environmentCt.kind_name = getString(R.string.cst_platform_condiiton_environmentCt);
        environmentCt.tipString = getString(R.string.cst_platform_condiiton_environment_temperature);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.environmentCt))) {
            environmentCt.current_value = DoubleAccuracy(obdDataEntity.environmentCt);
            environmentCt.abnormal = setAbnormal(environmentCt.current_value, -40.0, 60.0);
        } else {
            environmentCt.support = false;
            environmentCt.abnormal = false;
        }
        environmentCt.normal_value = "-40-60";
        mAbnormalList.add(environmentCt);


        ObdBean airPressure = new ObdBean();
        airPressure.kind_name = getString(R.string.cst_platform_condiiton_airPressure);
        airPressure.tipString = getString(R.string.cst_platform_condiiton_environmental_stress);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.airPressure))) {
            airPressure.current_value = DoubleAccuracy(obdDataEntity.airPressure);
            airPressure.abnormal = setAbnormal(airPressure.current_value, 50.0, 105.0);
        } else {
            airPressure.support = false;
            airPressure.abnormal = false;
        }
        airPressure.normal_value = "50-105";
        mAbnormalList.add(airPressure);

        ObdBean fuelPressure = new ObdBean();
        fuelPressure.kind_name = getString(R.string.cst_platform_condiiton_fuelPressure);
        fuelPressure.tipString = getString(R.string.cst_platform_condiiton_fuel_pressures);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.fuelPressure))) {
            fuelPressure.current_value = DoubleAccuracy(obdDataEntity.fuelPressure);
            fuelPressure.abnormal = setAbnormal(fuelPressure.current_value, 0.0, 450.0);
        } else {
            fuelPressure.support = false;
            fuelPressure.abnormal = false;
        }
        fuelPressure.normal_value = "0-450";
        mAbnormalList.add(fuelPressure);

        ObdBean airFlow = new ObdBean();
        airFlow.kind_name = getString(R.string.cst_platform_condiiton_airFlow);
        airFlow.tipString = getString(R.string.cst_platform_condiiton_engine_air_inflow);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.airFlow))) {
            airFlow.current_value = DoubleAccuracy(obdDataEntity.airFlow);
            airFlow.abnormal = setAbnormal(airFlow.current_value, 0.0, 655.0);
        } else {
            airFlow.support = false;
            airFlow.abnormal = false;
        }
        airFlow.normal_value = "0-655";
        mAbnormalList.add(airFlow);

        ObdBean tvp = new ObdBean();
        tvp.kind_name = getString(R.string.cst_platform_condiiton_tvp);
        tvp.tipString = getString(R.string.cst_platform_condiiton_throttle_position);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.tvp))) {
            tvp.current_value = DoubleAccuracy(obdDataEntity.tvp);
            tvp.abnormal = setAbnormal(tvp.current_value, 0.0, 100.0);
        } else {
            tvp.support = false;
            tvp.abnormal = false;
        }
        tvp.normal_value = "0-100";
        mAbnormalList.add(tvp);

        ObdBean pedalPosition = new ObdBean();
        pedalPosition.kind_name = getString(R.string.cst_platform_condiiton_pedalPosition);
        pedalPosition.tipString = getString(R.string.cst_platform_condiiton_accelerator_pedal_position);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.pedalPosition))) {
            pedalPosition.current_value = DoubleAccuracy(obdDataEntity.pedalPosition);
            pedalPosition.abnormal = setAbnormal(pedalPosition.current_value, 0.0, 100.0);
        } else {
            pedalPosition.support = false;
            pedalPosition.abnormal = false;
        }
        pedalPosition.normal_value = "0-100";
        mAbnormalList.add(pedalPosition);

        ObdBean engineRuntime = new ObdBean();
        engineRuntime.kind_name = getString(R.string.cst_platform_condiiton_engineRuntime);
        engineRuntime.tipString = getString(R.string.cst_platform_condiiton_engine_running_time);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.engineRuntime))) {
            engineRuntime.current_value = DoubleAccuracy(obdDataEntity.engineRuntime / 60);
            engineRuntime.abnormal = setAbnormal(engineRuntime.current_value, 0.0, 120.0);
        } else {
            engineRuntime.support = false;
            engineRuntime.abnormal = false;
        }
        engineRuntime.normal_value = "0-120";
        mAbnormalList.add(engineRuntime);

        ObdBean enginePayload = new ObdBean();
        enginePayload.kind_name = getString(R.string.cst_platform_condiiton_enginePayload);
        enginePayload.tipString = getString(R.string.cst_platform_condiiton_engine_load);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.enginePayload))) {
            enginePayload.current_value = DoubleAccuracy(obdDataEntity.enginePayload);
            enginePayload.abnormal = setAbnormal(enginePayload.current_value, 0.0, 100.0);
        } else {
            enginePayload.support = false;
            enginePayload.abnormal = false;
        }
        enginePayload.normal_value = "0-100";
        mAbnormalList.add(enginePayload);

        ObdBean lfuelTrim = new ObdBean();
        lfuelTrim.kind_name = getString(R.string.cst_platform_condiiton_lfuelTrim);
        lfuelTrim.tipString = getString(R.string.cst_platform_condiiton_fuel_revised);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.lfuelTrim))) {
            lfuelTrim.current_value = DoubleAccuracy(obdDataEntity.lfuelTrim);
            lfuelTrim.abnormal = setAbnormal(lfuelTrim.current_value, 80.0, 120.0);
        } else {
            lfuelTrim.support = false;
            lfuelTrim.abnormal = false;
        }
        lfuelTrim.normal_value = "80-120";
        mAbnormalList.add(lfuelTrim);


        ObdBean ciaa = new ObdBean();
        ciaa.kind_name = getString(R.string.cst_platform_condiiton_ciaa);
        ciaa.tipString = getString(R.string.cst_platform_condiiton_ignition_advance_angle);
        if (isParamSupportedTemperature(Double.toString(obdDataEntity.ciaa))) {
            ciaa.current_value = DoubleAccuracy(obdDataEntity.ciaa);
            ciaa.abnormal = setAbnormal(ciaa.current_value, -30.0, 60.0);
        } else {
            ciaa.support = false;
            ciaa.abnormal = false;
        }
        ciaa.normal_value = "-30-60";
        mAbnormalList.add(ciaa);

    }

    public void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        mAdapter.setMathOnClick(new ConditionResultNormalAdapter.onMathClickListen() {
            @Override
            public void onMathtemClick(int position) {
                if (position < mNormalList.size()) {
                    showAlertDialog(CarConditionDetailActivity.this, "", mNormalList.get(position).tipString, getString(R.string.cst_platform_ok), true, true, null);
                } else {
                    showAlertDialog(CarConditionDetailActivity.this, "", mAbnormalList.get(position - mNormalList.size()).tipString, getString(R.string.cst_platform_ok), true, true, null);
                }
            }
        });
    }

    public double DoubleAccuracy(double doul) {
        return Double.parseDouble(mDecimalFormat.format(doul));
    }


    /**
     * 一个按钮的提示框
     */
    public static void showAlertDialog(Context context, String title,
                                       String content, String btnText, boolean cancelable,
                                       boolean canceledOnTouchOutside, final DialogInterface.OnClickListener listener) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.setCancelable(cancelable);
        dlg.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.cst_platform_widget_dialog);
        LinearLayout layout = (LinearLayout) window.findViewById(R.id.alert_dialog_choose_layout);
        layout.setVisibility(View.GONE);

        TextView titleTv = (TextView) window.findViewById(R.id.alert_dialog_title_tv);
        TextView contentTv = (TextView) window.findViewById(R.id.alert_dialog_content_tv);
        titleTv.setText(title);
        contentTv.setText(content);

        Button ok = (Button) window.findViewById(R.id.alert_dialog_confirm_btn);
        ok.setText(btnText);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(dlg, -1);
                } else {
                    dlg.dismiss();
                }
            }
        });
    }

    private boolean setAbnormal(double value, double min, double max) {
        if (Double.compare(value, max) <= 0 && Double.compare(value, min) >= 0)
            return false;
        else
            return true;
    }


    private static boolean isParamSupportedTemperature(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        boolean r = value.equals("-9999") || value.equals("-9999.0");
        return !r;
    }

    /**
     * 根据返回结果判断剩余油量显示格式
     *
     * @param perResidualFuel
     * @param residualFuel
     * @return
     */
    private ObdBean showBean(ObdBean perResidualFuel, ObdBean residualFuel) {
        ObdBean result;
        if (!perResidualFuel.support && residualFuel.support) {
            result = residualFuel;
        } else {
            result = perResidualFuel;
        }
        return result;
    }
}
