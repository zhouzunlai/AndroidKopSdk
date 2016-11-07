package platform.cston.explain.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import cston.cstonlibray.R;
import platform.cston.explain.activity.ReportActivity;
import platform.cston.explain.adapter.YearReportAdapter;
import platform.cston.explain.utils.DTUtils;
import platform.cston.explain.widget.DateActionSheetDialog;
import platform.cston.explain.widget.ViewTipModule;
import platform.cston.explain.widget.refresh.PullToRefreshBase;
import platform.cston.explain.widget.refresh.PullToRefreshListView;
import platform.cston.httplib.bean.YearReportResult;
import platform.cston.httplib.search.CarReportSearch;
import platform.cston.httplib.search.OnResultListener;

/**
 * Created by daifei on 2016/8/24.
 */
public class YearReportFragment extends Fragment {

    private PullToRefreshListView lv;
    private YearReportResult result;
    private LinkedList<YearReportResult.DataEntity> yearReportList=new LinkedList<>();//这里换成年报告bean
    private YearReportAdapter adapter;
    private Calendar mMinCalendar;//最小允许日期
    private Calendar mMaxCalendar;//最大允许日期
    private DateActionSheetDialog dateActionSheet; //日期选择控件
    private Button dayPickBtn;
    private ReportActivity mActivity;
    private LayoutInflater inflater;
    private View listEmptyView;//拉取数据为空时，显示的内容
    private long queryStartTime;//查询开始时间
    private String openCarId;


    private final int QUERY_TYPE_PULL_UP = 1;//以start为最大时间,加载以前的轨迹
    private final int QUERY_TYPE_PULL_DOWN = 2;//以start为最小时间
    private final int QUERY_TYPE_REFRESH = 3;//刷新列表
    private final int SIZE = 10;//每页显示数量
    private int mQueryType = QUERY_TYPE_REFRESH;//初始化请求类型

    private ViewTipModule mViewTipModule;//预加载页面
    private FrameLayout mMainLayout;//父容器
    private RelativeLayout mDataLayout;
    private RelativeLayout rlBottom;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.cst_platform_report_fragment,container,false);
        lv=(PullToRefreshListView) view.findViewById(R.id.report_lv);
        dayPickBtn=(Button) view.findViewById(R.id.report_picker_btn);
        rlBottom=(RelativeLayout) view.findViewById(R.id.rl_bottom);
        rlBottom.setVisibility(View.GONE);
        dayPickBtn.setVisibility(View.GONE);
        initListView();
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
                        getYearRport(DTUtils.LongToStrTimeYear(queryStartTime), mQueryType);
                    }
                }
        );
        getYearRport(DTUtils.LongToStrTimeYear(queryStartTime), mQueryType);
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
        TextView promptTv = (TextView) listEmptyView.findViewById(R.id.none_data_prompt_tv);
        promptTv.setText("您目前暂无车报告数据");
        lv.setEmptyView(listEmptyView);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lv.getLayoutParams();
        layoutParams.bottomMargin=0;//your bottom margin value
        lv.setLayoutParams(layoutParams);
//        lv.setLayout

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(DTUtils.getToday("yyyyMM").compareTo(yearReportList.get(i-1).year+"12")<0)
                {
                    mActivity.showMonthFragment(DTUtils.getToday("yyyyMM"));
                }
                else
                {
                    mActivity.showMonthFragment(yearReportList.get(i-1).year+"12");
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
                if (yearReportList != null && yearReportList.size() > 0) {
                    YearReportResult.DataEntity firstItem = yearReportList.get(firstVisibleItem);//这里换成年报告bean
                    setDateTv(firstItem.year);
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
                if (yearReportList == null ||yearReportList.size() <= 0) {
                    lv.onRefreshComplete();
                } else {
                    YearReportResult.DataEntity firstItem = yearReportList.get(0);//这里换成年报告bean
                    //以第一项结束时间为最小时间
                    getYearRport(firstItem.year, mQueryType);
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
                if (yearReportList == null || yearReportList.size() <= 0) {
                    lv.onRefreshComplete();
                } else {
                    YearReportResult.DataEntity lastItem = yearReportList.get(yearReportList.size() - 1);
                    //以最后一项开始时间为最大时间
                    getYearRport(lastItem.year, mQueryType);
                }
            }
        });

    }



    private void getYearRport(final String time,int queryType)
    {
        CarReportSearch.newInstance().GetCarYearReportResult(time,queryType,SIZE,mActivity.openCarId,new OnResultListener.CarYearReportResultListener(){
                    @Override
                    public void onCarYearReportResult(YearReportResult var1, boolean isError, Throwable ex) {
                        lv.onRefreshComplete();

                if (isError) {
                    mViewTipModule.showFailState();
                    return;
                }
                mViewTipModule.showSuccessState();
                if (null == var1 || var1.getData() == null || var1.getData().size() == 0) {//判断数据是否有效，无效return;
                    if (null != var1)
                    if (mQueryType == QUERY_TYPE_REFRESH) {
                    }
                    return;
                }
                switch (mQueryType) {//根据不同请求类型，执行相应操作
                    case QUERY_TYPE_REFRESH://选择日期后，mQueryType=QUERY_TYPE_REFRESH；
                        setDateTv(time);
                        result=var1;
                        yearReportList.clear();
                        yearReportList.addAll(result.getData());
                        adapter=new YearReportAdapter(getActivity(),openCarId,result,yearReportList);//这里换成年报告bean
                        lv.setAdapter(adapter);
                        break;
                    case QUERY_TYPE_PULL_DOWN://列表向下拉，mQueryType=QUERY_TYPE_PULL_DOWN；

                        for(int i=var1.getData().size()-1;i>=0;i--)
                            yearReportList.addFirst(var1.getData().get(i));
                        break;
                    case QUERY_TYPE_PULL_UP://列表向上拉，mQueryType=QUERY_TYPE_PULL_UP；
                        if (var1.getData().size() < 1) {
                            Toast.makeText(getActivity(), "没有更多年报告数据！", Toast.LENGTH_SHORT).show();
                        } else {
                            yearReportList.addAll(var1.getData());
                        }
                        break;
                }

                adapter.notifyDataSetChanged();//刷新列表

            }
        }
            );
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
        setDateTv(""+mMaxCalendar.get(Calendar.YEAR));

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
                String date = year + "-" + month + "-" + dayOfMonth;
                long time = StringToDate(date, "yyyy-MM-dd").getTime();
                time = time + 24 * 60 * 60 - 1;
                mQueryType = QUERY_TYPE_REFRESH;
                getYearRport(DTUtils.LongToStrTimeYear(time), mQueryType);//选择时间的时候刷新数据
                setDateTv(""+year);

            }
        });

    }

    /**
     * 底部日期栏 显示列表项时间
     *
     * @param time 显示的时间戳（10位）
     */
    private void setDateTv(String time) {
        dayPickBtn.setText(time+"年");
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
     * 显示日期
     */
    private void setDatePickerBtn() {
        Calendar calendar = Calendar.getInstance();
        dateActionSheet.setNowDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        dateActionSheet.show();
    }


}
