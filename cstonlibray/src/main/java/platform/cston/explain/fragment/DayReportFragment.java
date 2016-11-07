package platform.cston.explain.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import cston.cstonlibray.R;
import platform.cston.explain.activity.ReportActivity;
import platform.cston.explain.adapter.DayReportAdapter;
import platform.cston.explain.utils.DTUtils;
import platform.cston.explain.widget.DateActionSheetDialog;
import platform.cston.explain.widget.ViewTipModule;
import platform.cston.explain.widget.refresh.PullToRefreshBase;
import platform.cston.explain.widget.refresh.PullToRefreshListView;
import platform.cston.httplib.bean.DayReportResult;
import platform.cston.httplib.search.CarReportSearch;
import platform.cston.httplib.search.OnResultListener;

/**
 * Created by daifei on 2016/8/24.
 */
public class DayReportFragment extends Fragment {

    private ReportActivity mActivity;

    private Calendar mMinCalendar;//最小允许日期
    private Calendar mMaxCalendar;//最大允许日期
    private DateActionSheetDialog mDateActionSheet; //日期选择控件

    private LayoutInflater mInflater;
    private View mListEmptyView;//拉取数据为空时，显示的内容
    private FrameLayout mMainLayout;//父容器，添加到ViewTipModule中展示界面
    private PullToRefreshListView lv;//数据列表
    private RelativeLayout mDataLayout;//展示数据源的View
    private ViewTipModule mViewTipModule;//预加载页面,用于展示数据加载情况的布局

    private DayReportAdapter mAdapter;//日报告数据展示适配器

    private Button mDayPickBtn;//日期选择按钮

    private final int QUERY_TYPE_PULL_UP = 1;//以start为最大时间,加载以前的数据
    private final int QUERY_TYPE_PULL_DOWN = 2;//以start为最小时间,加载之后的数据
    private final int QUERY_TYPE_REFRESH = 3;//第一次获取数据时使用

    private int mQueryType = QUERY_TYPE_REFRESH;//初始化请求类型
    private int mSize = 10;//每页显示数量

    private long mChooseTime;//毫秒数
    private long mQueryStartTime;//查询开始时间

    private String mInitTime;//用于查询日报告的字符串时间
    private String tempDateStr;

    private DayReportResult mResult;//服务器返回的数据项
    private LinkedList<DayReportResult.DataEntity> mDayReportList = new LinkedList<>();//装载日报告列表的链表

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mQueryStartTime = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.cst_platform_report_fragment, container, false);
        lv = (PullToRefreshListView) view.findViewById(R.id.report_lv);
        mDayPickBtn = (Button) view.findViewById(R.id.report_picker_btn);
        mMainLayout = (FrameLayout) view.findViewById(R.id.main_layout);
        mDataLayout = (RelativeLayout) view.findViewById(R.id.data_layout);
        initDatePicker();
        initEmptyListView();
        mDayPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDatePickerBtn();
            }
        });
        mViewTipModule = new ViewTipModule(getActivity(), mMainLayout, mDataLayout,
                new ViewTipModule.Callback() {
                    @Override
                    public void getData() {
                        getDayRport(DTUtils.LongToStrTimeDay(mQueryStartTime), mQueryType);
                    }
                }
        );
        if (TextUtils.isEmpty(mInitTime)) {
            getDayRport(DTUtils.LongToStrTimeDay(mQueryStartTime), mQueryType);
        } else {
            getDayRport(mInitTime, mQueryType);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ReportActivity) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        boolean isArgsValid = false;
        if (args != null) {
            mInitTime = args.getString("day_report_time");
            if (TextUtils.isEmpty(mInitTime)) { // 检查是否有效
                isArgsValid = true;
            }
        }
        if (isArgsValid == false) {
            return;
        }
    }


    public static DayReportFragment newInstance(String time) {
        final DayReportFragment fragment = new DayReportFragment();
        final Bundle args = new Bundle();
        args.putString("day_report_time", time);
        fragment.setArguments(args);
        return fragment;
    }


    private void initEmptyListView() {
        mInflater = LayoutInflater.from(getActivity());
        mListEmptyView = mInflater.inflate(R.layout.cst_platform_common_list_empty_view, null);
        RelativeLayout layout = (RelativeLayout) mListEmptyView
                .findViewById(R.id.common_list_empty_layout);
        layout.setVisibility(View.GONE);
        ImageView logo = (ImageView) mListEmptyView.findViewById(R.id.none_data_prompt_logo);
        logo.setImageResource(R.drawable.cst_platform_none_track_icon);
        TextView promptTv = (TextView) mListEmptyView.findViewById(R.id.none_data_prompt_tv);
        promptTv.setText("您目前暂无车报告数据");
        lv.setEmptyView(mListEmptyView);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (mDayReportList != null && mDayReportList.size() > 0) { //获取滑动第一项，改变底部时间
                    DayReportResult.DataEntity firstItem = mDayReportList.get(firstVisibleItem);
                    setDateTv(DTUtils.StrTimeToLong(firstItem.date));
                }

            }
        });


        //监听上下滑动事件
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils
                        .formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL
                        );
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (mDayReportList == null || mDayReportList.size() <= 0) {
                    lv.onRefreshComplete();
                } else {
                    mQueryType = QUERY_TYPE_PULL_DOWN;
                    DayReportResult.DataEntity firstItem = mDayReportList.get(0);//以第一项结束时间为最小时间
                    if (null != firstItem && null != firstItem.date)
                        getDayRport(firstItem.date, mQueryType);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils
                        .formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL
                        );
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (mDayReportList == null || mDayReportList.size() <= 0) {
                    lv.onRefreshComplete();
                } else {
                    mQueryType = QUERY_TYPE_PULL_UP;
                    DayReportResult.DataEntity lastItem = mDayReportList.get(mDayReportList.size() - 1);  //以最后一项开始时间为最大时间
                    if (null != lastItem && null != lastItem.date)
                        getDayRport(lastItem.date, mQueryType);
                }
            }
        });

    }


    private void getDayRport(final String time, int queryType) {
        CarReportSearch.newInstance().GetCarDayReportResult(time, queryType, mSize, mActivity.openCarId, new OnResultListener.CarDayReportResultListener() {
            @Override
            public void onCarDayReportResult(DayReportResult var1, boolean isError, Throwable ex) {
                lv.onRefreshComplete();
                if (isError) {
                    mViewTipModule.showFailState();
                    return;
                }
                mViewTipModule.showSuccessState();
                if (null == var1 || var1.getData() == null || var1.getData().size() == 0) {//判断数据是否有效，无效return;
                    if (null != var1)
                        if (mQueryType == QUERY_TYPE_REFRESH) {
                            Toast.makeText(getActivity(), "未找到 " + tempDateStr + " 的日报告", Toast.LENGTH_SHORT).show();
                        }
                    return;
                }

                mChooseTime = DTUtils.StrTimeToLong(time);//转换为对应的时间
                switch (mQueryType) {//根据不同请求类型，执行相应操作
                    case QUERY_TYPE_REFRESH://选择日期后，mQueryType=QUERY_TYPE_REFRESH；
                        setDateTv(mChooseTime);
                        mResult = var1;
                        mDayReportList.clear();
                        mDayReportList.addAll(mResult.getData());
                        mAdapter = new DayReportAdapter(getActivity(), mResult, mDayReportList);
                        lv.setAdapter(mAdapter);
                        break;
                    case QUERY_TYPE_PULL_DOWN://列表向下拉，mQueryType=QUERY_TYPE_PULL_DOWN；
                        for (int i = var1.getData().size() - 1; i >= 0; i--)
                            mDayReportList.addFirst(var1.getData().get(i));
                        break;
                    case QUERY_TYPE_PULL_UP://列表向上拉，mQueryType=QUERY_TYPE_PULL_UP；
                        if (var1.getData().size() < 1) {
                            Toast.makeText(getActivity(), "没有更多日报告数据！", Toast.LENGTH_SHORT).show();
                        } else {
                            mDayReportList.addAll(var1.getData());
                        }
                        break;
                }
                mAdapter.notifyDataSetChanged();//刷新列表
            }
        });
    }

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
        setDateTv(mMaxCalendar.getTime().getTime());
        mDateActionSheet = new DateActionSheetDialog(getActivity(), mMinCalendar.get(Calendar.YEAR),
                mMinCalendar.get(Calendar.MONTH), mMinCalendar.get(Calendar.DAY_OF_MONTH),
                mMaxCalendar.get(Calendar.YEAR),
                mMaxCalendar.get(Calendar.MONTH), mMaxCalendar.get(Calendar.DAY_OF_MONTH)
        );
        mDateActionSheet.setNowDate(mMaxCalendar.get(Calendar.YEAR),
                        mMaxCalendar.get(Calendar.MONTH) + 1,
                        mMaxCalendar.get(Calendar.DAY_OF_MONTH));

        mDateActionSheet.setOnDoneListener(new DateActionSheetDialog.OnDoneListener() {
            @Override
            public void onDone(int year, int month, int dayOfMonth) {
                String date = year + "-" + month + "-" + dayOfMonth;
                tempDateStr = ""+year+"年"+month + "月" + dayOfMonth + "日";
                long time = StringToDate(date, "yyyy-MM-dd").getTime();
                time = time + 24 * 60 * 60 - 1;
                mQueryType = QUERY_TYPE_REFRESH;
                getDayRport(DTUtils.LongToStrTimeDay(time), mQueryType);//刷新数据,重新获取数据
                setDateTv(time);//设置时间和日期
            }
        });

    }

    /**
     * 底部日期栏 显示列表项时间
     *
     * @param time 显示的时间戳（10位）
     */
    private void setDateTv(long time) {
        mChooseTime = time;
        mDayPickBtn.setText(getDate(time, "MM月dd日"));
    }


    public Date StringToDate(String dateStr, String formatStr) {
        if (null == dateStr) {
            return null;
        }
        DateFormat dd = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = dd.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
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


    /**
     * 显示日期
     */
    private void setDatePickerBtn() {
        Calendar calendar = Calendar.getInstance();
        mDateActionSheet.setNowDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        mDateActionSheet.show();
    }


}
