package platform.cston.explain.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cston.cstonlibray.R;
import platform.cston.explain.adapter.CarDetectionAbnormalAdapter;
import platform.cston.explain.adapter.CarDetectionAdapter;
import platform.cston.explain.adapter.CarDetectionNormalAdapter;
import platform.cston.explain.bean.CarDetectionEntity;
import platform.cston.explain.bean.CarDetectionSubEntity;
import platform.cston.explain.bean.CstTopTitleInfo;
import platform.cston.explain.bean.ObdBean;
import platform.cston.explain.utils.CstPlatformUtils;
import platform.cston.explain.widget.pullrefresh.CstPlatformMyPtrHeadLayout;
import platform.cston.explain.widget.pullrefresh.MyPtrLayout;
import platform.cston.explain.widget.pullrefresh.PtrDefaultHandler;
import platform.cston.explain.widget.pullrefresh.PtrHandler;
import platform.cston.httplib.bean.CarConDectionResult;
import platform.cston.httplib.ex.HttpException;
import platform.cston.httplib.search.ObdResultSearch;
import platform.cston.httplib.search.OnResultListener;

/**
 * Created by daifei on 2016/7/7.
 */
public class CarDetectionActivity extends CstBaseActivity {

    private ViewGroup mErrorLayout;
    private ViewGroup mWarnLayout;
    private ViewGroup mNormalLayout;
    private ViewGroup mDetectionLayout;//检测时候的布局
    private ViewGroup mResultLayout;//查询结果的布局，和正在查询时候的布局分别进行隐藏和显示
    private ListView mErrorListView;
    private ListView mWarnListView;
    private ListView mNormalListView;
    private ListView mDetectionListView;
    private ScrollView mScrollView;
    private CstPlatformMyPtrHeadLayout mRefreshView;

    private CstTopTitleInfo.ColorStatus mTitleTopStatus = CstTopTitleInfo.ColorStatus.NONE;
    private CarConDectionResult mCarDetectionParcelable;
    private CarConDectionResult.DataEntity.ObdDataEntity mObdData;
    private CarConDectionResult.DataEntity mCarConDetectionResult;

    private CarDetectionAdapter mDetectionAdapter;
    private CarDetectionAbnormalAdapter mErrorAdapter;
    private CarDetectionAbnormalAdapter mWarnAdapter;

    private ArrayList<CarDetectionSubEntity> mErrorList = new ArrayList<>();
    private ArrayList<CarDetectionSubEntity> mWarnList = new ArrayList<>();
    private ArrayList<CarDetectionSubEntity> mNormalList = new ArrayList<>();

    private Intent intent;

    private Handler mHandle = new Handler();

    private boolean mCarFailuresAbnormal;//用于判断小项是否有异常
    private boolean mCarFailureHandler;//用于判断小项车是否需要处理
    private boolean mCarFailureMind;//用于判断小项车是否需要注意

    private int mPosition;//用来记录显示檢測項的位置，刷新listview
    private int mTotalscore;//检测分数
    private int DETECTION_ITEM_TIME = 600;//每项检测时间间隔

    private String mOpenCarId;


    private DecimalFormat mDecimalFormat = new DecimalFormat("#####0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    private void initView() {
        setContentView(R.layout.cst_platform_activity_car_detection);
        mRefreshView = (CstPlatformMyPtrHeadLayout) findViewById(R.id.refresh);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        mDetectionListView = (ListView) findViewById(R.id.detection_list);
        mDetectionLayout = (ViewGroup) findViewById(R.id.car_detection);
        mResultLayout = (ViewGroup) findViewById(R.id.car_result);
        mErrorLayout = (ViewGroup) findViewById(R.id.error_layout);
        mWarnLayout = (ViewGroup) findViewById(R.id.warn_layout);
        mNormalLayout = (ViewGroup) findViewById(R.id.normal_layout);
        mErrorListView = (ListView) findViewById(R.id.error_list);
        mWarnListView = (ListView) findViewById(R.id.warn_list);
        mNormalListView = (ListView) findViewById(R.id.normal_list);
        TextView lHeadLeftTv = (TextView) findViewById(R.id.cst_platform_header_left_text);
        lHeadLeftTv.setText(getString(R.string.cst_platform_detect_back));
        setHeaderTitle(getString(R.string.cst_platform_detect_title));
        setPageInfoStatic();
        setHeaderLeftTextBtn();
        initData();
    }


    private void initData() {
        mOpenCarId = getIntent().getStringExtra("OPENCARID");
        if (null == mOpenCarId)
            return;
        initDetectionView();
        mRefreshView.post(new Runnable() {
            @Override
            public void run() {
                mRefreshView.autoRefresh();
            }
        });
    }

    /**
     * 初始化检测页面
     */
    private void initDetectionView() {
        mDetectionListView.scrollTo(0, 0);
        mDetectionLayout.setVisibility(View.VISIBLE);
        mResultLayout.setVisibility(View.GONE);
    }

    /**
     * 初始化检测项
     */
    private void initDetection() {//对将检测的项目进行赋值,将所有项目都设置为可以检测空闲项（表示暂未开始检测）,设置图标和状态
        ArrayList<CarDetectionEntity> mList = new ArrayList<>();
        CarDetectionEntity entity;
        mCarFailuresAbnormal = false;//初始化代表车辆没有故障
        //汽车故障
        entity = new CarDetectionEntity();
        entity.drawable = R.drawable.cst_platform_icon_detection_car_fault;
        entity.pType = CarDetectionEntity.TYPE_DETECTION_CAR_FAULT;
        entity.title = CarDetectionEntity.getTitle(entity.pType);
        entity.state = CarDetectionEntity.DETECTION_IDLE;
        mList.add(entity);

        //汽车蓄电池
        entity = new CarDetectionEntity();
        entity.drawable = R.drawable.cst_platform_icon_detection_car_battery;
        entity.pType = CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY;
        entity.title = CarDetectionEntity.getTitle(entity.pType);
        entity.state = CarDetectionEntity.DETECTION_IDLE;
        mList.add(entity);

        //冷却液温度
        entity = new CarDetectionEntity();
        entity.drawable = R.drawable.cst_platform_icon_detection_car_temperature;
        entity.pType = CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE;
        entity.title = CarDetectionEntity.getTitle(entity.pType);
        entity.state = CarDetectionEntity.DETECTION_IDLE;
        mList.add(entity);

        mDetectionAdapter = new CarDetectionAdapter(this, mList);
        mDetectionListView.setAdapter(mDetectionAdapter);

        mTitleTopStatus = CstTopTitleInfo.ColorStatus.NORMAL;
        setHeadreColor(mTitleTopStatus);
    }

    /**
     * 初始化點擊事件
     */
    private void initListener() {
        //需处理项点击事件
        mErrorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int mPosition, long id) {
                CarDetectionSubEntity info = mErrorList.get(mPosition);
                String name = info.title;
                intent = new Intent(CarDetectionActivity.this, CarExceptionActivity.class);
                if (info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_FAULT)) {//紧急故障
                    intent.putExtra("title", getString(R.string.cst_platform_detect_vehicle_failure));
                } else if (info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY)) {//电瓶电压
                    intent.putExtra("title", getString(R.string.cst_platform_detect_automobile_storage_battery));
                }
                intent.putExtra("reminder", name);
                intent.putExtra("selectType", info.pType);
                intent.putExtra("OBD", mCarDetectionParcelable);//OBD检测数据
                intent.putExtra("urgency", true);//是否是需处理项
                startActivity(intent);
            }
        });
        //需注意项点击事件
        mWarnListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int mPosition, long id) {
                CarDetectionSubEntity info = mWarnList.get(mPosition);
                String name = info.title;
                if (info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_FAULT_EX)) {
                    intent = new Intent(CarDetectionActivity.this, CarConditionDetailActivity.class);
                    intent.putExtra("title", getString(R.string.cst_platform_detect_vehicle_failure));
                    intent.putExtra("reminder", getString(R.string.cst_platform_detect_car_data_has_abnormal));
                    intent.putExtra("visible", true);//是否显示小标题
                    intent.putExtra("level", CarDetectionEntity.DETECTION_WARNING);
                    intent.putExtra("selectType", info.pType);//根据此值来判断是否高亮或居中
                    intent.putExtra("OBD", mCarDetectionParcelable);
                    startActivity(intent);
                } else {
                    intent = new Intent(CarDetectionActivity.this, CarExceptionActivity.class);
                    if (info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_FAULT)) {
                        intent.putExtra("title", getString(R.string.cst_platform_detect_vehicle_failure));
                    } else if (info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE)) {
                        intent.putExtra("title", getString(R.string.cst_platform_detect_coolant_temperature));
                    }
                    intent.putExtra("reminder", name);
                    intent.putExtra("selectType", info.pType);
                    intent.putExtra("OBD", mCarDetectionParcelable);
                    intent.putExtra("urgency", false);//是否需是需处理项
                    startActivity(intent);
                }
            }
        });

        mNormalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int mPosition, long id) {
                CarDetectionSubEntity info = mNormalList.get(mPosition);
                intent = new Intent(CarDetectionActivity.this, CarConditionDetailActivity.class);
                if (info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_FAULT) || info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_FAULT_EX)) {
                    intent.putExtra("title", getString(R.string.cst_platform_detect_vehicle_failure));
                } else if (info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY)) {
                    intent.putExtra("title", getString(R.string.cst_platform_detect_automobile_storage_battery));
                } else if (info.pType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE)) {
                    intent.putExtra("title", getString(R.string.cst_platform_detect_coolant_temperature));
                }
                intent.putExtra("selectType", info.pType);
                intent.putExtra("level", CarDetectionEntity.DETECTION_NORMAL);
                intent.putExtra("OBD", mCarDetectionParcelable);
                startActivity(intent);
            }
        });

        mRefreshView.setPtrHandler(new PtrHandler() {//下拉刷新监听事件
            @Override
            public boolean checkCanDoRefresh(MyPtrLayout frame, View content,
                                             View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshRequest(MyPtrLayout frame) {
                initDetectionView();
                initDetection();//初始化检测
                prepareDetection();//检测前的准备工作
                requestData();//数据请求操作,开始请求数据进行检测
            }

            @Override
            public void onRefreshOnMove(MyPtrLayout frame) {

            }

            @Override
            public void onRefreshReSet(MyPtrLayout frame) {

            }

            @Override
            public void onRefreshComplete(MyPtrLayout frame) {
                changeSubTitle();
            }
        });
    }

    private void prepareDetection() {//检测之前的准备工作，设置状态为可以进行检测
        mPosition = 0;
        if (mDetectionAdapter.getList().size() > 0) {
            mDetectionAdapter.getList().get(0).state = CarDetectionEntity.DETECTION_ING;
        }
        mDetectionAdapter.notifyDataSetChanged();//通知数据源改变
    }


    //数据请求操作
    private void requestData() {
        ObdResultSearch.newInstance().GetCarConDetectionResult(mOpenCarId, new OnResultListener.CarConDectionResultListener() {
            @Override
            public void onCarConDectionResult(CarConDectionResult var1, boolean isError, Throwable ex) {
                if (isError || var1 == null) {
                    cancelDetection();
                    if (ex instanceof HttpException || ex instanceof ConnectException || ex instanceof SocketTimeoutException) { // 网络错误
                        showAlertDialog(CarDetectionActivity.this, "", getString(R.string.cst_platform_request_outtime), getString(R.string.cst_platform_ok), true, true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CarDetectionActivity.this.finish();
                            }
                        });
                    } else {
                        Toast.makeText(CarDetectionActivity.this, "未检测到数据项,请稍后再试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (!var1.getCode().equals("0")) {
                        mCarDetectionParcelable = null;
                        mObdData=null;
                        Toast.makeText(CarDetectionActivity.this, "检测到数据项异常：" + var1.getResult(), Toast.LENGTH_SHORT).show();
                    } else {
                        mCarConDetectionResult=null;
                        mObdData=null;
                        mCarDetectionParcelable = var1;
                        mCarConDetectionResult = mCarDetectionParcelable.getData();
                        mObdData = mCarConDetectionResult.getObdData();//获得最终的Obd数据
                        if (null != mCarConDetectionResult && null != mObdData) {
                            getTotalscore();
                            startDetection();
                        } else {
                            cancelDetection();
                            Toast.makeText(CarDetectionActivity.this, "未检测到数据项", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }


    /**
     * 开始检测
     */
    private void startDetection() {
        mRefreshView.startProgressCount(mDetectionAdapter.getCount(), mTotalscore);
        mHandle.postDelayed(runnableDetection, DETECTION_ITEM_TIME);
    }

    protected void setHeadreColor(CstTopTitleInfo.ColorStatus status) {//根据状态值，设置标题的颜色
        super.setHeadreColor(status);
        mRefreshView.changeSubBackground(status.getColor());
    }

    /**
     * 取消检测
     */
    private void cancelDetection() {
        for (int i = 0; i < mDetectionAdapter.getList().size(); i++) {
            mDetectionAdapter.getList().get(i).state = CarDetectionEntity.DETECTION_IDLE;
        }
        mDetectionAdapter.notifyDataSetChanged();
    }


    private Runnable runnableDetection = new Runnable() {
        @Override
        public void run() {
            if (mPosition < mDetectionAdapter.getList().size() - 1) {
                if (mDetectionListView.getChildAt(mPosition + 1) != null) {
                    if (mDetectionListView.getChildAt(mPosition + 1).getBottom() >= mScrollView.getHeight()) {
                        int scrollY = mDetectionListView.getChildAt(mPosition + 1).getBottom() - mScrollView.getHeight();
                        if (scrollY > CstPlatformUtils.dip2px(mActivity, 44.5f)) {
                            scrollY = CstPlatformUtils.dip2px(mActivity, 44.5f);
                        }
                        mDetectionListView.scrollBy(0, scrollY);
                    }
                }

                setDetectionResult();//设置检测的结果
                mDetectionAdapter.getList().get(mPosition + 1).state = CarDetectionEntity.DETECTION_ING;
                mDetectionAdapter.notifyDataSetChanged();
                mPosition++;
                mHandle.postDelayed(runnableDetection, DETECTION_ITEM_TIME);
            } else {
                setDetectionResult();//设置检测的结果
                mDetectionAdapter.notifyDataSetChanged();
                mHandle.removeCallbacks(runnableDetection);
                endDetection();//在这里拿到的结果
            }
        }
    };


    private void setDetectionResult() {//设置检测首页item项的颜色和值，错误的为红色，警告的胃黄色，正常的为绿色
        if (mPosition < mDetectionAdapter.getList().size()) {
            switch (mDetectionAdapter.getList().get(mPosition).pType) {
                case CarDetectionEntity.TYPE_DETECTION_CAR_FAULT:
                    setDetectionItemResult(mTotalscore, CarDetectionEntity.TYPE_DETECTION_CAR_FAULT);//传递一个数据，判断item是何种状态
                    break;
                case CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY:
                    setDetectionItemResult(mTotalscore, CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY);//需要将这些有异常的数据存储下来，然后对状态进行设计
                    break;
                case CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE:
                    setDetectionItemResult(mTotalscore, CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE);
                    break;
            }
        }
    }


    private void setDetectionItemResult(int mTotalscore, String genre) {//根据返回的结果设置值,设置为警告或者错误，正常

        //根据数据的返回值，进行状态的改变
        switch (genre) {
            case CarDetectionEntity.TYPE_DETECTION_CAR_FAULT:
                if (checkCarFault() == CarDetectionEntity.DETECTION_ERROR) {
                    mDetectionAdapter.getList().get(mPosition).state = CarDetectionEntity.DETECTION_ERROR;
                } else if (checkCarFault() == CarDetectionEntity.DETECTION_WARNING) {
                    mDetectionAdapter.getList().get(mPosition).state = CarDetectionEntity.DETECTION_WARNING;
                } else if (checkCarFault() == CarDetectionEntity.DETECTION_NORMAL) {
                    mDetectionAdapter.getList().get(mPosition).state = CarDetectionEntity.DETECTION_NORMAL;
                }
                break;
            case CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY:
                if (checkBattery() == CarDetectionEntity.DETECTION_ERROR) {
                    mDetectionAdapter.getList().get(mPosition).state = CarDetectionEntity.DETECTION_ERROR;
                } else if (checkBattery() == CarDetectionEntity.DETECTION_NORMAL) {
                    mDetectionAdapter.getList().get(mPosition).state = CarDetectionEntity.DETECTION_NORMAL;
                }
                break;
            case CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE:
                if (checkcoolantCt() == CarDetectionEntity.DETECTION_NORMAL) {
                    mDetectionAdapter.getList().get(mPosition).state = CarDetectionEntity.DETECTION_NORMAL;
                } else if (checkcoolantCt() == CarDetectionEntity.DETECTION_WARNING) {
                    mDetectionAdapter.getList().get(mPosition).state = CarDetectionEntity.DETECTION_WARNING;
                }
                break;
        }

        if (mTotalscore == 100) {//进行警告，错误，安全的文字状态设置
            changeState(CarDetectionEntity.DETECTION_NORMAL);
        } else if (mTotalscore > 60 && mTotalscore < 100) {
            changeState(CarDetectionEntity.DETECTION_WARNING);
        } else if (mTotalscore <= 60) {
            changeState(CarDetectionEntity.DETECTION_ERROR);
        }
    }


    private void changeState(int state) {//改变状态，设置标题的值

        if (state == CarDetectionEntity.DETECTION_ERROR) {
            mTitleTopStatus = CstTopTitleInfo.ColorStatus.ERROR;
        }
        if (state == CarDetectionEntity.DETECTION_WARNING) {
            mTitleTopStatus = CstTopTitleInfo.ColorStatus.WARN;
        }
        if (state == CarDetectionEntity.DETECTION_NORMAL) {
            mTitleTopStatus = CstTopTitleInfo.ColorStatus.NORMAL;
        }
        setHeadreColor(mTitleTopStatus);
    }

    private void endDetection() {//检测完毕之后，处理动画的消失和呈现
        mRefreshView.refreshComplete();

        final Animation enterAnimation = AnimationUtils.loadAnimation(mActivity,
                R.anim.cst_platform_detection_bottom_enter);
        enterAnimation.setAnimationListener(new Animation.AnimationListener() {//动画监听事件
            @Override
            public void onAnimationStart(Animation animation) {//动画开始的时候
                mResultLayout.setVisibility(View.VISIBLE);
                initResult();//对结果进行分类处理，warn,error,normal,将数据添加进对应的链表中，然后进行setAdapter
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation exitAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.cst_platform_detection_top_exit);
        exitAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDetectionLayout.setVisibility(View.GONE);//设置为gone
                mDetectionListView.scrollTo(0, 0);//设置滑动
                mResultLayout.startAnimation(enterAnimation);//结果开始动画
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mDetectionLayout.startAnimation(exitAnimation);
    }

    //初始化数据，设置adapter
    private void initResult() {
        mCarFailuresAbnormal = false;
        mCarFailureMind= false;
        mCarFailureHandler= false;
        mErrorList.clear();
        mWarnList.clear();
        mNormalList.clear();

        //处理进气口温度检测，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.onflowCt))) {
            if (setAbnormal(DoubleAccuracy(mObdData.onflowCt), -40.0, 80.0))
                mCarFailuresAbnormal = true;
        }


        //车辆环境温度(℃)，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.environmentCt))) {
            if (setAbnormal(DoubleAccuracy(mObdData.environmentCt), -40.0, 60.0)) {
                mCarFailuresAbnormal = true;
            }
        }


        //大气压力(Kpa)，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.airPressure))) {
            if (setAbnormal(DoubleAccuracy(mObdData.airPressure), 50.0, 105.0)) {
                mCarFailuresAbnormal = true;
            }
        }


        //燃油压力(Kpa)，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.fuelPressure))) {
            if (setAbnormal(DoubleAccuracy(mObdData.fuelPressure), 0.0, 450.0)) {
                mCarFailuresAbnormal = true;
            }
        }


        //空气流量(g/s)，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.airFlow))) {
            if (setAbnormal(DoubleAccuracy(mObdData.airFlow), 0.0, 655.0)) {
                mCarFailuresAbnormal = true;
            }
        }


        //节气门位置(%)，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.tvp))) {
            if (setAbnormal(DoubleAccuracy(mObdData.tvp), 0.0, 100.0)) {
                mCarFailuresAbnormal = true;
            }
        }


        //油门踏板位置(%)，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.pedalPosition))) {
            if (setAbnormal(DoubleAccuracy(mObdData.pedalPosition), 0.0, 100.0)) {
                mCarFailuresAbnormal = true;
            }
        }


        //发动机负荷(0-100)(%)，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.enginePayload))) {
            if (setAbnormal(DoubleAccuracy(mObdData.enginePayload), 0.0, 100.0)) {
                mCarFailuresAbnormal = true;
            }
        }


        //长期燃油修正值(%)80-120，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.lfuelTrim))) {
            if (setAbnormal(DoubleAccuracy(mObdData.lfuelTrim), 80.0, 120.0)) {
                mCarFailuresAbnormal = true;
            }
        }

        //点火提前角(-30-60)(°)，异常mCarFailuresAbnormal=true;
        if (isParamSupportedTemperature(Double.toString(mObdData.ciaa))) {
            if (setAbnormal(DoubleAccuracy(mObdData.ciaa), -30.0, 60.0)) {
                mCarFailuresAbnormal = true;
            }
        }

        //对需要注意项进行处理
        if (mCarConDetectionResult.mind > 0) {
            CarDetectionSubEntity entity = new CarDetectionSubEntity();
            entity.drawable = R.drawable.cst_platform_icon_detection_car_fault;
            entity.pType = CarDetectionEntity.TYPE_DETECTION_CAR_FAULT;
            entity.title = getString(R.string.cst_platform_detect_something) + mCarConDetectionResult.mind + getString(R.string.cst_platform_detect_general_failure_number);
            mCarFailureMind = true;//需要处理项，小项，车辆故障
            mWarnList.add(entity);
        }

        //对需要处理项进行处理
        if (mCarConDetectionResult.handle > 0) {
            CarDetectionSubEntity entity = new CarDetectionSubEntity();
            entity.drawable = R.drawable.cst_platform_icon_detection_car_fault;
            entity.pType = CarDetectionEntity.TYPE_DETECTION_CAR_FAULT;
            entity.title = getString(R.string.cst_platform_detect_something) + mCarConDetectionResult.handle + getString(R.string.cst_platform_detect_emergency_fault_number);
            mCarFailureHandler = true;//这理由需要处理的项
            mErrorList.add(entity);
        }

        //判断车辆故障是否为正常项目
        {
            CarDetectionSubEntity entity = new CarDetectionSubEntity();
            entity.drawable = R.drawable.cst_platform_icon_detection_car_fault;
            entity.pType = CarDetectionEntity.TYPE_DETECTION_CAR_FAULT_EX;
            if (mCarFailuresAbnormal) {//如果存在异常
                entity.title = getString(R.string.cst_platform_detect_car_data_has_abnormal);
                mWarnList.add(entity);
            } else if (!mCarFailureHandler && !mCarFailureMind && !mCarFailuresAbnormal) {//车辆故障检测正常
                entity.title = CarDetectionEntity.getTitle(entity.pType);
                mNormalList.add(entity);
            }
        }

        //汽车蓄电池(V)
        if (isParamSupportedTemperature(Double.toString(mObdData.batteryVoltage))) {
            CarDetectionSubEntity entity = new CarDetectionSubEntity();
            entity.drawable = R.drawable.cst_platform_icon_detection_car_battery;
            entity.pType = CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY;
            if (!setAbnormal(DoubleAccuracy(mObdData.batteryVoltage), 11.5, 15.0)) {
                entity.title = CarDetectionEntity.getTitle(entity.pType);
                mNormalList.add(entity);
            } else {//如果存在且故障码个数大于0，则加入错误列表
                if (Double.compare(DoubleAccuracy(mObdData.batteryVoltage), 11.5) < 0) {
                    entity.title = getString(R.string.cst_platform_detect_battery_voltage_more_loewr);//电瓶电压过低
                } else if (Double.compare(DoubleAccuracy(mObdData.batteryVoltage), 15.0) > 0) {
                    entity.title = getString(R.string.cst_platform_detect_battery_voltage_more_higher);//电瓶电压过高
                }
                mErrorList.add(entity);
            }
        }


        //冷却液温度(℃),这里要对车型进行判断，是T型号还是L型号，然后才来判断是否正常
        if (isParamSupportedTemperature(Double.toString(mObdData.coolantCt))) {
            CarDetectionSubEntity entity = new CarDetectionSubEntity();
            entity.drawable = R.drawable.cst_platform_icon_detection_car_temperature;
            entity.pType = CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE;
            if (null != mCarConDetectionResult.highcoolantCt && !mCarConDetectionResult.highcoolantCt.equals("null")) {
                entity.title = getString(R.string.cst_platform_detect_coolant_temperature_higher);
                mWarnList.add(entity);
            } else {
                entity.title = CarDetectionEntity.getTitle(entity.pType);
                mNormalList.add(entity);
            }
        }

        if (mNormalList.size() == 0) {
            mNormalLayout.setVisibility(View.GONE);
        } else {
            mNormalLayout.setVisibility(View.VISIBLE);
            CarDetectionNormalAdapter mNormalAdapter = new CarDetectionNormalAdapter(this);
            mNormalAdapter.setList(mNormalList);
            mNormalListView.setAdapter(mNormalAdapter);
        }

        if (mWarnList.size() == 0) {
            mWarnLayout.setVisibility(View.GONE);
        } else {
            mWarnLayout.setVisibility(View.VISIBLE);
            mWarnAdapter = new CarDetectionAbnormalAdapter(this);
            mWarnAdapter.setList(mWarnList);
            mWarnListView.setAdapter(mWarnAdapter);
        }

        if (mErrorList.size() == 0) {
            mErrorLayout.setVisibility(View.GONE);
        } else {
            mErrorLayout.setVisibility(View.VISIBLE);
            mErrorAdapter = new CarDetectionAbnormalAdapter(this);
            mErrorAdapter.setList(mErrorList);
            mErrorListView.setAdapter(mErrorAdapter);
        }
    }


    private void changeSubTitle() {//对转动图片的背景进行设置，如果是警告则设置为红色，如果是安全则是指为绿色，如果是错误则设置为红色
        //CstPlatformMyPtrHeadLayout对mHeaderView，mBottomView，进行背景的颜色设置、
        if (mTitleTopStatus == CstTopTitleInfo.ColorStatus.NORMAL) {
            mRefreshView.changeSubTitle(getString(R.string.cst_platform_detect_normal_change_subtitle));
        } else if (mTitleTopStatus == CstTopTitleInfo.ColorStatus.ERROR) {
            mRefreshView.changeSubTitle(getString(R.string.cst_platform_detect_deal_change_subtitle));
        } else if (mTitleTopStatus == CstTopTitleInfo.ColorStatus.WARN) {
            mRefreshView.changeSubTitle(getString(R.string.cst_platform_detect_notice_change_subtitle));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setHeadreColor(mTitleTopStatus);
    }


    /**
     * 判断检测结果是否支持该项
     *
     * @param value
     * @return
     */
    private static boolean isParamSupportedTemperature(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        boolean r = value.equals("-9999") || value.equals("-9999.0");
        return !r;
    }


    public double DoubleAccuracy(double doul) {
        return Double.parseDouble(mDecimalFormat.format(doul));
    }

    private boolean setAbnormal(double value, double min, double max) {
        if (Double.compare(value, max) <= 0 && Double.compare(value, min) >= 0)
            return false;
        else
            return true;
    }


    //获得总的分数
    public void getTotalscore() {
        double mTotalscore = 1.0;

        if (mCarConDetectionResult.handle != 0)
            mTotalscore *= Math.pow(0.6, mCarConDetectionResult.handle);

        if (mCarConDetectionResult.mind != 0)
            mTotalscore *= Math.pow(0.8, mCarConDetectionResult.mind);

        //进气口温度,如果参数合法并且参数异常
        if (isParamSupportedTemperature(Double.toString(mObdData.onflowCt)) && setAbnormal(mObdData.onflowCt, -40.0, 80.0)) {
            mTotalscore *= 0.9;
        }
        //车辆环境温度
        if (isParamSupportedTemperature(Double.toString(mObdData.environmentCt)) && setAbnormal(mObdData.environmentCt, -40.0, 60.0)) {
            mTotalscore *= 0.9;
        }

        //大气压力
        if (isParamSupportedTemperature(Double.toString(mObdData.airPressure)) && setAbnormal(mObdData.airPressure, 50.0, 105.0)) {
            mTotalscore *= 0.9;
        }

        //燃油压力
        if (isParamSupportedTemperature(Double.toString(mObdData.fuelPressure)) && setAbnormal(mObdData.fuelPressure, 0.0, 450.0)) {
            mTotalscore *= 0.9;
        }

        //空气流量(0-655)
        if (isParamSupportedTemperature(Double.toString(mObdData.airFlow)) && setAbnormal(mObdData.airFlow, 0.0, 655.0)) {
            mTotalscore *= 0.9;
        }

        //节气门位置(0-100)
        if (isParamSupportedTemperature(Double.toString(mObdData.tvp)) && setAbnormal(mObdData.tvp, 0.0, 100.0)) {
            mTotalscore *= 0.9;
        }

        //油门踏板位置(0-100)
        if (isParamSupportedTemperature(Double.toString(mObdData.pedalPosition)) && setAbnormal(mObdData.pedalPosition, 0.0, 100.0)) {
            mTotalscore *= 0.9;
        }

        //发动机负荷(0-100)
        if (isParamSupportedTemperature(Double.toString(mObdData.enginePayload)) && setAbnormal(mObdData.enginePayload, 0.0, 100.0)) {
            mTotalscore *= 0.9;
        }

        //长期燃油修正值(%)80-120
        if (isParamSupportedTemperature(Double.toString(mObdData.lfuelTrim)) && setAbnormal(mObdData.lfuelTrim, 80.0, 120.0)) {
            mTotalscore *= 0.9;
        }

        //点火提前角(-30-60)
        if (isParamSupportedTemperature(Double.toString(mObdData.ciaa)) && setAbnormal(mObdData.ciaa, -30, 60.0)) {
            mTotalscore *= 0.9;
        }

        //电瓶电压（11.5-15.0）
        if (isParamSupportedTemperature(Double.toString(mObdData.batteryVoltage)) && setAbnormal(mObdData.batteryVoltage, 11.5, 15.0)) {
            mTotalscore *= 0.6;
        }

        //冷却液温度
        if (isParamSupportedTemperature(Double.toString(mObdData.coolantCt))) {//在数据项迟滞的情况下才进行扣分
            if (mCarConDetectionResult.highcoolantCt != null && !mCarConDetectionResult.highcoolantCt.equals("null")) {
                mTotalscore *= 0.8;
            }
        }
        mTotalscore *= 100;
        this.mTotalscore = (int) mTotalscore;
    }


    private int checkCarFault() {
        //将故障码装入数据,处理故障码，这里要区分是需处理的故障码还是需要注意的故障码,将数据导入这个里面
        //处理故障码
        if (isParamSupportedTemperature(Double.toString(mCarConDetectionResult.handle)) && mCarConDetectionResult.handle > 0) {
            return CarDetectionEntity.DETECTION_ERROR;//紧急故障
        }

        //"故障码个数(个)"
        ObdBean malfunctionNum = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.malfunctionNum))) {
            malfunctionNum.current_value = (int) mObdData.malfunctionNum;
        } else {
            malfunctionNum.support = false;
        }
        malfunctionNum.normal_value = "";

        if (malfunctionNum.support && malfunctionNum.current_value > 0) {//如果存在且故障码个数大于0，则加入错误列表
            return CarDetectionEntity.DETECTION_WARNING;
        }


        //进气口温度(℃)，这里如果是异常则加入warnninglist,正常则加入mNormalList
        ObdBean onflowCt = new ObdBean();//进气口温度
        if (isParamSupportedTemperature(Double.toString(mObdData.onflowCt))) {
            onflowCt.current_value = DoubleAccuracy(mObdData.onflowCt);
            onflowCt.abnormal = setAbnormal(onflowCt.current_value, -40.0, 80.0);
        } else {
            onflowCt.support = false;
            onflowCt.abnormal = false;
        }

        if (onflowCt.support && onflowCt.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }

        //车辆环境温度(℃)
        ObdBean environmentCt = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.environmentCt))) {
            environmentCt.current_value = DoubleAccuracy(mObdData.environmentCt);
            environmentCt.abnormal = setAbnormal(environmentCt.current_value, -40.0, 60.0);
        } else {
            environmentCt.support = false;
            environmentCt.abnormal = false;
        }

        if (environmentCt.support && environmentCt.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }


        //大气压力(Kpa)
        ObdBean airPressure = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.airPressure))) {
            airPressure.current_value = DoubleAccuracy(mObdData.airPressure);
            airPressure.abnormal = setAbnormal(airPressure.current_value, 50.0, 105.0);
        } else {
            airPressure.support = false;
            airPressure.abnormal = false;
        }

        if (airPressure.support && airPressure.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }


        //燃油压力(Kpa)
        ObdBean fuelPressure = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.fuelPressure))) {
            fuelPressure.current_value = DoubleAccuracy(mObdData.fuelPressure);
            fuelPressure.abnormal = setAbnormal(fuelPressure.current_value, 0.0, 450.0);
        } else {
            fuelPressure.support = false;
            fuelPressure.abnormal = false;
        }

        if (fuelPressure.support && fuelPressure.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }

        //空气流量(g/s)
        ObdBean airFlow = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.airFlow))) {
            airFlow.current_value = DoubleAccuracy(mObdData.airFlow);
            airFlow.abnormal = setAbnormal(airFlow.current_value, 0.0, 655.0);
        } else {
            airFlow.support = false;
            airFlow.abnormal = false;
        }

        if (airFlow.support && airFlow.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }

        //节气门位置(0-100)(%)
        ObdBean tvp = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.tvp))) {
            tvp.current_value = DoubleAccuracy(mObdData.tvp);
            tvp.abnormal = setAbnormal(tvp.current_value, 0.0, 100.0);
        } else {
            tvp.support = false;
            tvp.abnormal = false;
        }

        if (tvp.support && tvp.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }

        //油门踏板位置(0-100)(%)
        ObdBean pedalPosition = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.pedalPosition))) {
            pedalPosition.current_value = DoubleAccuracy(mObdData.pedalPosition);
            pedalPosition.abnormal = setAbnormal(pedalPosition.current_value, 0.0, 100.0);
        } else {
            pedalPosition.support = false;
            pedalPosition.abnormal = false;
        }

        if (pedalPosition.support && pedalPosition.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }

        //发动机负荷(0-100)(%)
        ObdBean enginePayload = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.enginePayload))) {
            enginePayload.current_value = DoubleAccuracy(mObdData.enginePayload);
            enginePayload.abnormal = setAbnormal(enginePayload.current_value, 0.0, 100.0);
        } else {
            enginePayload.support = false;
            enginePayload.abnormal = false;
        }

        if (enginePayload.support && enginePayload.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }


        //长期燃油修正值(%)80-120
        ObdBean lfuelTrim = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.lfuelTrim))) {
            lfuelTrim.current_value = DoubleAccuracy(mObdData.lfuelTrim);
            lfuelTrim.abnormal = setAbnormal(lfuelTrim.current_value, 80.0, 120.0);
        } else {
            lfuelTrim.support = false;
            lfuelTrim.abnormal = false;
        }

        if (lfuelTrim.support && lfuelTrim.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }

        //点火提前角(-30-60)(°)
        ObdBean ciaa = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.ciaa))) {
            ciaa.current_value = DoubleAccuracy(mObdData.ciaa);
            ciaa.abnormal = setAbnormal(ciaa.current_value, -30.0, 60.0);
        } else {
            ciaa.support = false;
            ciaa.abnormal = false;
        }
        if (ciaa.support && ciaa.abnormal) {//数据异常
            return CarDetectionEntity.DETECTION_WARNING;
        }
        return CarDetectionEntity.DETECTION_NORMAL;
    }

    private int checkBattery() {
        //汽车蓄电池(V)
        ObdBean batteryVoltage = new ObdBean();
        if (isParamSupportedTemperature(Double.toString(mObdData.batteryVoltage))) {
            batteryVoltage.current_value = DoubleAccuracy(mObdData.batteryVoltage);
            batteryVoltage.abnormal = setAbnormal(batteryVoltage.current_value, 11.5, 15.0);
        } else {
            batteryVoltage.support = false;
            batteryVoltage.abnormal = false;
        }

        if (batteryVoltage.support && batteryVoltage.abnormal) {//如果存在且故障码个数大于0，则加入错误列表
            return CarDetectionEntity.DETECTION_ERROR;
        }
        return CarDetectionEntity.DETECTION_NORMAL;
    }

    private int checkcoolantCt() {
        if (!isParamSupportedTemperature(Double.toString(mObdData.coolantCt))) {
            return CarDetectionEntity.DETECTION_NO_SUPPORT;
        }
        //冷却液温度(℃)
        if ((mCarConDetectionResult.highcoolantCt == null || mCarConDetectionResult.highcoolantCt.equals("null"))
                && isParamSupportedTemperature(Double.toString(mObdData.coolantCt))) {
            return CarDetectionEntity.DETECTION_NORMAL;
        } else {
            return CarDetectionEntity.DETECTION_WARNING;
        }
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
}
