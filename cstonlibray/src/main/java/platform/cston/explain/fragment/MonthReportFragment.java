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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import cston.cstonlibray.R;
import platform.cston.explain.activity.ReportActivity;
import platform.cston.explain.adapter.MonReportAdapter;
import platform.cston.explain.utils.DTUtils;
import platform.cston.explain.widget.DateActionSheetDialog;
import platform.cston.explain.widget.ViewTipModule;
import platform.cston.explain.widget.refresh.PullToRefreshBase;
import platform.cston.explain.widget.refresh.PullToRefreshListView;
import platform.cston.httplib.bean.MonthReportResult;
import platform.cston.httplib.search.CarReportSearch;
import platform.cston.httplib.search.OnResultListener;

/**
 * Created by daifei on 2016/8/24.
 */
public class MonthReportFragment extends Fragment {
    private PullToRefreshListView lv;
    private MonthReportResult result;
    private LinkedList<MonthReportResult.DataEntity> monthReportList=new LinkedList<>();//这里要换成对应的月报告bean
//    private MonReportAdapter adapter;//这是对不
    private MonReportAdapter adapter;//这是对不
    private Calendar mMinCalendar;//最小允许日期
    private Calendar mMaxCalendar;//最大允许日期
    private DateActionSheetDialog dateActionSheet; //日期选择控件
    private Button dayPickBtn;
    private ReportActivity mActivity;
    private LayoutInflater inflater;
    private View listEmptyView;//拉取数据为空时，显示的内容
    private long queryStartTime;//查询开始时间


    private final int QUERY_TYPE_PULL_UP = 1;//以start为最大时间,加载以前的轨迹
    private final int QUERY_TYPE_PULL_DOWN = 2;//以start为最小时间
    private final int QUERY_TYPE_REFRESH = 3;//刷新列表
    private int mQueryType = QUERY_TYPE_REFRESH;//初始化请求类型
    private final static int SIZE = 10;//每页显示数量


    public static final String FRAGMENT_ARG_KEY_TIME = "month_report_time";
    public  String initTime;
    private String openCarId;

    private ViewTipModule mViewTipModule;//预加载页面
    private FrameLayout mMainLayout;//父容器
    private RelativeLayout mDataLayout;
    private long chooseTime;//毫秒数

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.cst_platform_report_fragment,container,false);
        lv=(PullToRefreshListView) view.findViewById(R.id.report_lv);
        dayPickBtn=(Button) view.findViewById(R.id.report_picker_btn);

        mMainLayout = (FrameLayout) view.findViewById(R.id.main_layout);
        mDataLayout = (RelativeLayout)view.findViewById(R.id.data_layout);
        queryStartTime = System.currentTimeMillis();

        initDatePicker();
        dayPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDatePickerBtn();
            }
        });

        mViewTipModule = new ViewTipModule(getActivity(), mMainLayout, mDataLayout,
                new ViewTipModule.Callback() {
                    @Override
                    public void getData() {
                        getMonthRport(DTUtils.LongToStrTimeDay(queryStartTime), mQueryType);
                    }
                }
        );

        if(TextUtils.isEmpty(initTime))
        {
            getMonthRport(DTUtils.LongToStrTimeMonth(queryStartTime), mQueryType);
        }
        else
        {
            getMonthRport(initTime, mQueryType);
        }
        initListView();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=(ReportActivity)activity;
        openCarId=mActivity.openCarId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final Bundle args=getArguments();
        boolean isArgsValid = false;
        if (args != null) {

            initTime = args.getString(FRAGMENT_ARG_KEY_TIME);
            // 检查是否有效
            if (TextUtils.isEmpty(initTime)) {
                isArgsValid = true;
            }
        }
        if (isArgsValid == false) {
            return;
        }
    }


    /**
     * Fragmet之间传递数据
     * @param time
     * @return
     */
    public static MonthReportFragment newInstance(String time)
    {
        final MonthReportFragment fragment=new MonthReportFragment();
        final Bundle args=new Bundle();
        args.putString(FRAGMENT_ARG_KEY_TIME,time);
        fragment.setArguments(args);
        return fragment;
    }


    private void initListView()
    {
        inflater = LayoutInflater.from(getActivity());
        listEmptyView = inflater.inflate(R.layout.cst_platform_common_list_empty_view, null);
        RelativeLayout layout = (RelativeLayout) listEmptyView
                .findViewById(R.id.common_list_empty_layout);
        layout.setVisibility(View.GONE);
        ImageView logo = (ImageView) listEmptyView.findViewById(R.id.none_data_prompt_logo);
        logo.setImageResource(R.drawable.cst_platform_none_track_icon);
        final TextView promptTv = (TextView) listEmptyView.findViewById(R.id.none_data_prompt_tv);
        promptTv.setText("您目前暂无车报告数据");
        lv.setEmptyView(listEmptyView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(monthReportList.get(i-1).year.equals("0")||monthReportList.get(i-1).month.equals("0"))
                    return;

                String month;
                if(monthReportList.get(i-1).month.startsWith("0"))
                {
                    month=monthReportList.get(i-1).month.substring(1);
                }
                else
                {
                    month=monthReportList.get(i-1).month;
                }
                String time=DTUtils.getLastDayOfMonth(Integer.parseInt(monthReportList.get(i-1).year),Integer.parseInt(month));

                if(DTUtils.getToday("yyyyMMdd").compareTo(time)<0)
                {
                    mActivity.showDayFragment(DTUtils.getToday("yyyyMMdd"));
                } else {
                    mActivity.showDayFragment(time);
                }
            }
        });


        //获取滑动第一项，改变底部时间
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (result.getData() != null && result.getData().size() > 0) {
                    MonthReportResult.DataEntity firstItem = monthReportList.get(firstVisibleItem);//这里换成对应的月报告bean
                    setDateTv(firstItem.year,firstItem.month);
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
                mQueryType = QUERY_TYPE_PULL_DOWN;
                if (monthReportList == null || monthReportList.size() <= 0) {
                    lv.onRefreshComplete();
                } else {
                    MonthReportResult.DataEntity firstItem = monthReportList.get(0);//这里换成对应的月报告bean


                    //以第一项结束时间为最小时间
                    if(firstItem.month.length()==1)
                    {
                        getMonthRport(firstItem.year+"0"+firstItem.month,mQueryType);
                    }
                    else
                    {
                        getMonthRport(firstItem.year+firstItem.month,mQueryType);
                    }
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
                mQueryType = QUERY_TYPE_PULL_UP;
                if (monthReportList == null || monthReportList.size() <= 0) {
                    lv.onRefreshComplete();
                } else {
                    MonthReportResult.DataEntity lastItem = monthReportList.get(monthReportList.size() - 1);//这里换成对应的月报告bean
                    //以最后一项开始时间为最大时间
                    if(lastItem.month.length()==1)
                    {
                        getMonthRport(lastItem.year+"0"+lastItem.month,mQueryType);
                    }
                    else
                    {
                        getMonthRport(lastItem.year+lastItem.month,mQueryType);
                    }
                }
            }
        });
    }



    private void getMonthRport(final String time,int queryType)
    {
        if(time.length()<=4) {
            lv.onRefreshComplete();
            return;
        }
        //在这里进行年和月的处理
        String month=time.substring(4);
        if(month.startsWith("0"))
        {
            month=month.substring(1);
        }
        CarReportSearch.newInstance().GetCarMonthReportResult(time.substring(0,4),month,mQueryType,SIZE,mActivity.openCarId,new OnResultListener.CarMonthReportResultListener()
        {
            @Override
            public void onCarMonthReportResult(MonthReportResult var1, boolean isError, Throwable ex) {
                lv.onRefreshComplete();
                if (isError) {
                    mViewTipModule.showFailState();
                    return;
                }
                mViewTipModule.showSuccessState();
                if (null == var1 || var1.getData() == null || var1.getData().size() == 0) {//判断数据是否有效，无效return;
                    return;
                }
                chooseTime = DTUtils.StrTimeMonthToLong(time);//转换为对应的时间

                switch (mQueryType) {//根据不同请求类型，执行相应操作
                    case QUERY_TYPE_REFRESH://选择日期后，mQueryType=QUERY_TYPE_REFRESH；
                        setDateTv(chooseTime);
                        result=var1;
                        monthReportList.clear();
                        monthReportList.addAll(result.getData());
                        adapter=new MonReportAdapter(getActivity(),openCarId,result,monthReportList);
                        lv.setAdapter(adapter);
                        break;
                    case QUERY_TYPE_PULL_DOWN://列表向下拉，mQueryType=QUERY_TYPE_PULL_DOWN；
                        for (int i = var1.getData().size() - 1; i >= 0; i--)
                            monthReportList.addFirst(var1.getData().get(i));
                        break;
                    case QUERY_TYPE_PULL_UP://列表向上拉，mQueryType=QUERY_TYPE_PULL_UP；
                        if (var1.getData().size() < 1) {
                        } else {
                            monthReportList.addAll(var1.getData());
                        }
                        break;
                }
                adapter.notifyDataSetChanged();//刷新列表

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

        dateActionSheet = new DateActionSheetDialog(getActivity(), mMinCalendar.get(Calendar.YEAR),
                mMinCalendar.get(Calendar.MONTH), mMinCalendar.get(Calendar.DAY_OF_MONTH),
                mMaxCalendar.get(Calendar.YEAR),
                mMaxCalendar.get(Calendar.MONTH), mMaxCalendar.get(Calendar.DAY_OF_MONTH)
        );

        dateActionSheet
                .setNowDateYM(mMaxCalendar.get(Calendar.YEAR),
                        mMaxCalendar.get(Calendar.MONTH) + 1,
                        mMaxCalendar.get(Calendar.DAY_OF_MONTH));

        dateActionSheet.setOnDoneListener(new DateActionSheetDialog.OnDoneListener() {
            @Override
            public void onDone(int year, int month, int dayOfMonth) {
                String date = year + "-" + month + "-" + dayOfMonth;
                long time = StringToDate(date, "yyyy-MM-dd").getTime();
                time = time + 24 * 60 * 60 - 1;
                mQueryType = QUERY_TYPE_REFRESH;
                getMonthRport(DTUtils.LongToStrTimeMonth(time), mQueryType);
                setDateTv(time);//设置时间
            }
        });

    }

    /**
     * 底部日期栏 显示列表项时间
     *
     * @param time 显示的时间戳（10位）
     */
    private void setDateTv(long time) {
        chooseTime = time;
        dayPickBtn.setText(getDate(time, "yyyy年MM月"));
    }

    private void setDateTv(String year,String month)
    {
        dayPickBtn.setText(""+year+"年"+month+"月");
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
        dateActionSheet.setNowDateYM(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        dateActionSheet.show();
    }
}