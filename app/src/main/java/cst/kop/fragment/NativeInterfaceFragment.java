package cst.kop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cst.kop.R;
import cst.kop.activity.ApiActivity;
import cst.kop.activity.MainActivity;
import cst.kop.adapter.RecyclerViewListAdapter;
import cst.kop.beans.NativeBeans;
import cst.kop.widget.OneEditTextDialog;
import platform.cston.explain.activity.BreakRulesActivity;
import platform.cston.explain.activity.CarConditionDetailActivity;
import platform.cston.explain.activity.CarDetectionActivity;
import platform.cston.explain.activity.HistoricaltrackActivity;
import platform.cston.explain.activity.ReportActivity;
import platform.cston.explain.activity.VehicleDynamicsActivity;
import platform.cston.explain.widget.DateActionSheetDialog;
import platform.cston.httplib.bean.CarBrandResult;
import platform.cston.httplib.bean.CarFaultResult;
import platform.cston.httplib.bean.CarListResult;
import platform.cston.httplib.bean.CarModelResult;
import platform.cston.httplib.bean.CarTypeResult;
import platform.cston.httplib.bean.DrivingBehaviorResult;
import platform.cston.httplib.bean.ObdInfoResult;
import platform.cston.httplib.bean.OpenUserResult;
import platform.cston.httplib.bean.TravelStatisticsResult;
import platform.cston.httplib.search.CarBrandInfoSearch;
import platform.cston.httplib.search.CarInfoSearch;
import platform.cston.httplib.search.ObdResultSearch;
import platform.cston.httplib.search.OnResultListener;
import platform.cston.httplib.search.OpenUserInfoSearch;
import platform.cston.httplib.search.ReportRequest;

/**
 * Created by zhou-pc on 2016/9/12.
 */
public class NativeInterfaceFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private List<NativeBeans> mNativeList = new ArrayList<>();
    private List<NativeBeans> mApiList = new ArrayList<>();
    private int mSelectPosition;
    private int mSelectId;

    private String mOpenCarId;
    private String mCarPlate;

    private DateActionSheetDialog dateActionSheet; //日期选择控件
    private OneEditTextDialog mErrCodeDialog;//故障码输入弹出框

    private Calendar mMinCalendar;//最小允许日期
    private Calendar mMaxCalendar;//最大允许日期

    public NativeInterfaceFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NativeInterfaceFragment newInstance(int sectionNumber) {
        NativeInterfaceFragment fragment = new NativeInterfaceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.section_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initDatePicker();
        int selectIndex = getArguments().getInt(ARG_SECTION_NUMBER);
        if (selectIndex == 0) {
            if (mNativeList.isEmpty())
                initNativeDate();
            RecyclerViewListAdapter adapter = new RecyclerViewListAdapter(getActivity(), mNativeList);
            adapter.setOnItemClickLitener(new RecyclerViewListAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    NativeListern(position);
                }
            });
            recyclerView.setAdapter(adapter);
        } else if (selectIndex == 1) {
            if (mApiList.isEmpty())
                initApiDate();
            RecyclerViewListAdapter adapter = new RecyclerViewListAdapter(getActivity(), mApiList);
            adapter.setOnItemClickLitener(new RecyclerViewListAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    ApiListern(position);
                }
            });
            recyclerView.setAdapter(adapter);
        }
        return rootView;
    }

    /**
     * 原生界面数据构造
     */
    private void initNativeDate() {
        mNativeList.add(new NativeBeans("车辆实时动态", "查看车辆的实时动态信息，如当前位置，当前速度等", 0));
        mNativeList.add(new NativeBeans("车辆历史轨迹", "查看车辆历史轨迹列表，并查看轨迹详情", 1));
        mNativeList.add(new NativeBeans("车况检测", "实时监测车辆故障、汽车蓄电池、冷却液温度等车辆异常情况", 2));
        mNativeList.add(new NativeBeans("用车报告", "查看每天、每年、每月的车辆使用详情", 3));
        mNativeList.add(new NativeBeans("违章列表", "查看车辆违章记录，处理事项", 4));
        mNativeList.add(new NativeBeans("车辆实时OBD数据", "查看车辆速度里程、油耗、发动机转速等数据", 5));
        mNativeList.add(new NativeBeans("用车报告（年）", "查看车辆用车年报告H5页面展示", 6));
        mNativeList.add(new NativeBeans("用车报告（月）", "查看车辆用车月报告H5页面展示", 7));
    }

    /**
     * Api接口数据构造
     */
    private void initApiDate() {
        mApiList.add(new NativeBeans("车列表", "查询用户授权车辆信息", 0));
        mApiList.add(new NativeBeans("用户基本资料", "查询用户性别、年龄等基本信息", 1));
        mApiList.add(new NativeBeans("用户车辆行程统计", "查询用户某天的开车里程、油耗、时长等信息", 2));
        mApiList.add(new NativeBeans("用户驾驶行为", "查询用户某天三急，疲劳驾驶等驾驶行为数据", 3));
        mApiList.add(new NativeBeans("故障码查询", "查询具体故障码的详细解释，故障分析。及专家建议", 4));
        mApiList.add(new NativeBeans("车品牌", "查询所有车辆品牌", 5));
        mApiList.add(new NativeBeans("车型", "查询某品牌下的所有车型", 6));
        mApiList.add(new NativeBeans("车款", "查询某车型下的所有车款", 7));
    }

    /**
     * 原生界面列表的点击事件
     *
     * @param position 点击的位置
     */
    private void NativeListern(int position) {
        mSelectPosition = position;
        getCarInfo();
        if (null == mOpenCarId || mOpenCarId.isEmpty() || null == mCarPlate || mCarPlate.isEmpty()) {
            Toast.makeText(getActivity(), "获取车辆信息失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
            return;
        }
        NativieSelectCar(mSelectPosition);
    }

    /**
     * Api数据接口列表的点击事件
     *
     * @param position 点击的位置
     */
    private void ApiListern(int position) {
        mSelectId = mApiList.get(position).id;
        mSelectPosition = position;
        getCarInfo();
        ApiSelectCar(mSelectPosition);
    }


    private void getCarInfo() {
        mOpenCarId = ((MainActivity) getActivity()).mOpenCarId;
        mCarPlate = ((MainActivity) getActivity()).mCarPlate;
    }

    /**
     * 原声界面选择车
     *
     * @param position 选择的选项
     */
    private void NativieSelectCar(int position) {
        int id = mNativeList.get(position).id;
        switch (id) {
            case 0://车辆实时轨迹
                Intent vehicleintent = new Intent();
                vehicleintent.setClass(getActivity(), VehicleDynamicsActivity.class);
                vehicleintent.putExtra("OPENCARID", mOpenCarId);
                startActivity(vehicleintent);
                break;
            case 1://车辆历史轨迹
                Intent trackintent = new Intent();
                trackintent.setClass(getActivity(), HistoricaltrackActivity.class);
                trackintent.putExtra("OPENCARID", mOpenCarId);
                startActivity(trackintent);
                break;
            case 2://车况检测
                Intent detectionintent = new Intent(getActivity(), CarDetectionActivity.class);
                detectionintent.putExtra("OPENCARID", mOpenCarId);
                startActivity(detectionintent);
                break;
            case 3://用车报告
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                intent.putExtra("OPENCARID", mOpenCarId);
                startActivity(intent);
                break;
            case 4://违章列表
                Intent RulesIntent = new Intent(getActivity(), BreakRulesActivity.class);
                RulesIntent.putExtra("OPENCARID", mOpenCarId);
                RulesIntent.putExtra("PLATE", mCarPlate);
                startActivity(RulesIntent);
                break;
            case 5://获取obd数据
                PostNativieCarObd();
                break;
            case 6://年报表
                setDatePickerBtn();
                break;
            case 7://月报告
                setDatePickerBtn();
                break;
        }
    }


    /**
     * Api数据接口选择车
     *
     * @param position 选择的选项
     */
    private void ApiSelectCar(int position) {
        mSelectId = mApiList.get(position).id;
        switch (mSelectId) {
            case 0://请求车列表
                PostApiCarList();
                break;
            case 1://请求用户基本资料
                PostApiUserInfo();
                break;
            case 2://请求用户车辆行程统计，选择日期
                if (null == mOpenCarId || mOpenCarId.isEmpty() || null == mCarPlate || mCarPlate.isEmpty()) {
                    Toast.makeText(getActivity(), "获取车辆信息失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
                    return;
                }
                setDatePickerBtn();
                break;
            case 3://请求用户驾驶行为，选择日期
                if (null == mOpenCarId || mOpenCarId.isEmpty() || null == mCarPlate || mCarPlate.isEmpty()) {
                    Toast.makeText(getActivity(), "获取车辆信息失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
                    return;
                }
                setDatePickerBtn();
                break;
            case 4://故障码查询
                PostApiCarFault("P0123", mOpenCarId);
//                mErrCodeDialog=new OneEditTextDialog(getActivity(), new OneDialogCallback() {
//                    @Override
//                    public void OnPositiveBtnClick(String errorCode) {
//                        if (errorCode.isEmpty()) {
//                            Toast.makeText(getActivity(), "请输入故障码", Toast.LENGTH_SHORT).show();
//                        } else {
//                            mErrCodeDialog.dismiss();
//                            PostApiCarFault(errorCode, mOpenCarId);
//                        }
//                    }
//
//                    @Override
//                    public void OnNegativeBtnClick(Dialog dialog) {
//                        mErrCodeDialog.dismiss();
//                    }
//                });
//                mErrCodeDialog.show();
//                mErrCodeDialog.setDialogTitleAndTip("故障码信息查询", "您的故障码");
//                mErrCodeDialog.setTextHint("请输入故障码");
                break;
            case 5://车品牌
                PostApiCarBrandResult();
                break;
            case 6://车型
                PostApiCarTypeResult("40");//默认车品牌id为40（保时捷918），通过车品牌接口获取的id
//                DialogUtils.showOneEditAlertDialogChoose(getActivity(), "车型查询", "车品牌id", "请输入车品牌id", true, false, false, new DialogCallback() {
//                    @Override
//                    public void OnLeftBtnClick(AlertDialog dlg, String appid, String appsec) {
//
//                    }
//
//                    @Override
//                    public void OnOneLeftBtnClick(AlertDialog dlg, String phone) {
//                        if (phone.isEmpty()) {
//                            Toast.makeText(getActivity(), "请输入故障码", Toast.LENGTH_SHORT).show();
//                        } else {
//                            dlg.dismiss();
//                            PostApiCarTypeResult(phone);
//                        }
//                    }
//                });
                break;
            case 7://车款
                PostApiCarModelResult("2073");//默认车型id为2073（保时捷），通过车型接口获取的id
//                DialogUtils.showOneEditAlertDialogChoose(getActivity(), "车款查询", "车型id", "请输入车型id", true, false, false, new DialogCallback() {
//                    @Override
//                    public void OnLeftBtnClick(AlertDialog dlg, String appid, String appsec) {
//
//                    }
//
//                    @Override
//                    public void OnOneLeftBtnClick(AlertDialog dlg, String phone) {
//                        if (phone.isEmpty()) {
//                            Toast.makeText(getActivity(), "请输入故障码", Toast.LENGTH_SHORT).show();
//                        } else {
//                            dlg.dismiss();
//                            PostApiCarModelResult(phone);
//                        }
//                    }
//                });
                break;
        }
    }

    /**
     * 初始化日期选择控件
     */
    private void initDatePicker() {
        // 最小允许日期
        mMinCalendar = Calendar.getInstance();
        mMinCalendar.set(2013, Calendar.JANUARY, 1, 0, 0, 0);

        // 最大允许日期
        mMaxCalendar = Calendar.getInstance();
        mMaxCalendar.setTime(new Date());

        if (mMaxCalendar.before(mMinCalendar)) {
            mMaxCalendar.set(2013, Calendar.JANUARY, 1, 0, 0, 0);
        }

        dateActionSheet = new DateActionSheetDialog(getActivity(), mMinCalendar.get(Calendar.YEAR),
                mMinCalendar.get(Calendar.MONTH), mMinCalendar.get(Calendar.DAY_OF_MONTH),
                mMaxCalendar.get(Calendar.YEAR),
                mMaxCalendar.get(Calendar.MONTH), mMaxCalendar.get(Calendar.DAY_OF_MONTH)
        );

        dateActionSheet
                .setNowDate(mMaxCalendar.get(Calendar.YEAR),
                        mMaxCalendar.get(Calendar.MONTH) + 1,
                        mMaxCalendar.get(Calendar.DAY_OF_MONTH));

        dateActionSheet.setOnDoneListener(new DateActionSheetDialog.OnDoneListener() {
            @Override
            public void onDone(int year, int month, int dayOfMonth) {
                int selectIndex = getArguments().getInt(ARG_SECTION_NUMBER);
                if (selectIndex == 0) {
                    NativieselectTimeMeth(mSelectPosition, year, month, dayOfMonth);
                } else if (selectIndex == 1) {
                    ApiselectTimeMeth(mSelectPosition, year, month, dayOfMonth);
                }
            }
        });
    }

    /**
     * 显示日期
     */
    private void setDatePickerBtn() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dateActionSheet.setNowDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        dateActionSheet.show();
    }

    /**
     * 原生界面选择日期后，执行的方法
     *
     * @param position
     */
    private void NativieselectTimeMeth(int position, int year, int month, int dayOfMonth) {
        int id = mNativeList.get(position).id;
        switch (id) {
            case 6:
                ReportRequest.getInstance().StarYearReport(getActivity(), mOpenCarId, year, new OnResultListener.OnStratReportListener() {
                    @Override
                    public void OnStratReportResult(boolean isSuccess, String result) {
                        if (!isSuccess) {
                            Toast.makeText(getActivity(), "打开年报告失败：" + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case 7:
                ReportRequest.getInstance().StarMonthReport(getActivity(), mOpenCarId, year, month, new OnResultListener.OnStratReportListener() {
                    @Override
                    public void OnStratReportResult(boolean isSuccess, String result) {
                        if (!isSuccess) {
                            Toast.makeText(getActivity(), "打开月报告失败：" + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }


    /**
     * Api数据接口选择日期后，执行的方法
     *
     * @param position
     */
    private void ApiselectTimeMeth(int position, int year, int month, int dayOfMonth) {
        String mon = "" + month;
        String dayOfmonth = "" + dayOfMonth;
        if (month < 10) {
            mon = "0" + month;
        }
        if (dayOfMonth < 10) {
            dayOfmonth = "0" + dayOfMonth;
        }
        String date = year + "" + mon + dayOfmonth;
        int id = mApiList.get(position).id;
        switch (id) {
            case 2:
                PostApiTravelStatisticsResult(mOpenCarId, date);
                break;
            case 3:
                PostApiDrivingBehaviorResult(mOpenCarId, date);
                break;
        }
    }

    /**
     * 获取车辆obd数据
     */
    private void PostNativieCarObd() {
        ObdResultSearch.newInstance().GetHistoryObdResult(mOpenCarId, new OnResultListener.OnGetObdResultListener() {
            @Override
            public void onGetObdResult(ObdInfoResult var1, boolean isError, Throwable throwable) {
                if (!isError && var1 != null) {//isError=false;返回正常
                    if (var1.getInfo() == null) {
                        Toast.makeText(getActivity(), "OBD数据请求失败：" + var1.getResult(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (var1.getCode().equals("0")) {
                        Intent intent = new Intent(getActivity(), CarConditionDetailActivity.class);
                        intent.putExtra("OBDINFO", var1.getInfo());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "OBD数据请求失败：" + var1.getCode() + ":" + var1.getResult(), Toast.LENGTH_SHORT).show();
                    }
                } else {//isError=true;返回异常
                    if (var1 != null) {//var1！=null 数据不为空，请求未出现异常
                        Toast.makeText(getActivity(), "OBD数据请求失败：" + var1.getCode() + ":" + var1.getResult(), Toast.LENGTH_SHORT).show();
                    } else {//var1==null 数据为空，请求出现异常
                        Toast.makeText(getActivity(), "OBD数据请求失败,数据为空：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });//获取obd数据
    }

    /**
     * 请求车列表
     */
    private void PostApiCarList() {
        CarInfoSearch.newInstance().GetCarInfoResult(new OnResultListener.OnGetCarListResultListener() {
            @Override
            public void onGetCarListResult(CarListResult carListResult, boolean b, Throwable throwable) {
                if (b) {
                    if (carListResult != null) {
                        Toast.makeText(getActivity(), "车列表请求失败：" + carListResult.getResult() + " code is:" + carListResult.getCode(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (null != throwable)
                            Toast.makeText(getActivity(), "车列表请求异常：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (null != carListResult && null != carListResult.getAllCars() && carListResult.getAllCars().size() > 0) {
                        startActivity("车列表", carListResult);
                    } else
                        Toast.makeText(getActivity(), "请求成功，车列表数据为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 请求用户基本资料
     */
    private void PostApiUserInfo() {
        OpenUserInfoSearch.getInstance().GetUserInfoResult(new OnResultListener.OpenUserResultListener() {
            @Override
            public void onOpenUserResult(OpenUserResult var1, boolean isError, Throwable ex) {
                if (!isError && var1 != null) {
                    if (var1.getCode().equals("0")) {
                        startActivity("用户基本资料", var1);
                    } else {
                        Toast.makeText(getActivity(), "用户基本资料请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    }
                } else {//isError=true;返回异常
                    if (var1 != null) {//var1！=null 数据为空，请求未出现异常
                        Toast.makeText(getActivity(), "用户基本资料请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    } else {//var1==null 数据为空，请求出现异常
                        Toast.makeText(getActivity(), "用户基本资料请求失败：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 根据oCId(mOpenCarId),date,获取用户驾驶里程
     *
     * @param openCarId
     * @param date
     */
    private void PostApiTravelStatisticsResult(String openCarId, String date) {
        OpenUserInfoSearch.getInstance().GetTravelStatisticsResult(openCarId, date, new OnResultListener.TravelStatisticsResultListener() {
            @Override
            public void onTravelStatisticsResult(TravelStatisticsResult var1, boolean isError, Throwable ex) {
                if (isError) {
                    Toast.makeText(getActivity(), "用户行程统计请求失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null != var1) {
                    if (var1.getCode().equals("0")) {
                        startActivity("用户行程统计", var1);
                    } else {
                        Toast.makeText(getActivity(), "用户行程统计请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 根据oCId（mOpenCarId），date获取用户驾驶行为信息
     *
     * @param openCarId
     * @param date
     */
    private void PostApiDrivingBehaviorResult(String openCarId, String date) {
        OpenUserInfoSearch.getInstance().GetDrivingBehaviorResult(openCarId, date, new OnResultListener.DrivingBehaviorResultListener() {
            @Override
            public void onDrivingBehaviorResult(DrivingBehaviorResult var1, boolean isError, Throwable ex) {
                if (!isError && var1 != null) {
                    if (var1.getCode().equals("0")) {
                        if (var1.getData() != null) {
                            startActivity("用户驾驶行为", var1);
                        } else {
                            Toast.makeText(getActivity(), "用户驾驶行为请求成功数据为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "用户驾驶行为请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    }
                } else {//isError=true;返回异常
                    if (var1 != null) {//var1！=null 数据为空，请求未出现异常
                        Toast.makeText(getActivity(), "用户驾驶行为请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    } else {//var1==null 数据为空，请求出现异常
                        Toast.makeText(getActivity(), "用户驾驶行为请求失败：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    /**
     * 根据错误码（dtc）,品牌名字(brandName)获取故障信息
     *
     * @param dtc       故障码
     * @param openCarId 车id
     */
    private void PostApiCarFault(String dtc, String openCarId) {
        CarInfoSearch.newInstance().GetCarFaultCodeResult(dtc, openCarId, new OnResultListener.CarFaultResultListener() {
            @Override
            public void onCarFaultResult(CarFaultResult var1, boolean isError, Throwable ex) {
                if (!isError && var1 != null) {
                    if (var1.getCode().equals("0")) {
                        startActivity("故障码", var1);
                    } else {
                        Toast.makeText(getActivity(), "故障码请求错误：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    }
                } else {//isError=true;返回异常
                    if (var1 != null) {//var1！=null 数据为空，请求未出现异常
                        Toast.makeText(getActivity(), "故障码请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    } else {//var1==null 数据为空，请求出现异常
                        Toast.makeText(getActivity(), "故障码请求失败：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 请求车品牌
     */
    private void PostApiCarBrandResult() {
        CarBrandInfoSearch.getInstance().GetCarBrandResult(new OnResultListener.CarBrandResultListener() {
            @Override
            public void onCarBrandResult(CarBrandResult var1, boolean isError, Throwable ex) {
                if (!isError && var1 != null) {
                    if (var1.getCode().equals("0")) {
                        startActivity("车品牌", var1);
                    } else {
                        Toast.makeText(getActivity(), "车品牌请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    }
                } else {//isError=true;返回异常
                    if (var1 != null) {//var1！=null 数据不为空，请求未出现异常
                        Toast.makeText(getActivity(), "车品牌请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    } else {//var1==null 数据为空，请求出现异常
                        Toast.makeText(getActivity(), "车品牌请求失败,数据为空：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 根据车型ID查询车型
     *
     * @param typeId 车型id
     */
    private void PostApiCarTypeResult(String typeId) {
        CarBrandInfoSearch.getInstance().GetCarTypeResult(typeId, new OnResultListener.CarTypeResultListener() {
            @Override
            public void onCarTypeResult(CarTypeResult var1, boolean isError, Throwable ex) {
                if (!isError && var1 != null) {
                    if (var1.getCode().equals("0")) {
                        startActivity("车型", var1);
                    } else {
                        Toast.makeText(getActivity(), "车型请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    }
                } else {//isError=true;返回异常
                    if (var1 != null) {//var1！=null 数据不为空，请求未出现异常
                        Toast.makeText(getActivity(), "车型请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    } else {//var1==null 数据为空，请求出现异常
                        Toast.makeText(getActivity(), "车型请求失败,数据为空：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    /**
     * 根据车款ID获取车款数据
     *
     * @param brandId 车款id
     */
    private void PostApiCarModelResult(String brandId) {
        CarBrandInfoSearch.getInstance().GetCarModelResult(brandId, new OnResultListener.CarModelResultListener() {
            @Override
            public void onCarModelResult(CarModelResult var1, boolean isError, Throwable ex) {
                if (!isError && var1 != null) {
                    if (var1.getCode().equals("0")) {
                        startActivity("车款", var1);
                    } else {
                        Toast.makeText(getActivity(), "车款请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    }
                } else {//isError=true;返回异常
                    if (var1 != null) {//var1！=null 数据不为空，请求未出现异常
                        Toast.makeText(getActivity(), "车款请求失败：" + var1.getResult() + " code is:" + var1.getCode(), Toast.LENGTH_SHORT).show();
                    } else {//var1==null 数据为空，请求出现异常
                        Toast.makeText(getActivity(), "车款请求失败,数据为空：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void startActivity(String title, Parcelable info) {
        Intent intent = new Intent(getActivity(), ApiActivity.class);
        intent.putExtra("APITITLE", title);
        intent.putExtra("SELECTID", mSelectId);
        intent.putExtra("ParcelaInfo", info);
        startActivity(intent);
    }
}
