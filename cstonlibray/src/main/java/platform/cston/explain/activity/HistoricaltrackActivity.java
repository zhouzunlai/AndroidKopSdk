package platform.cston.explain.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import platform.cston.explain.adapter.TrackListAdapter;
import platform.cston.explain.widget.DateActionSheetDialog;
import platform.cston.explain.widget.ViewTipModule;
import platform.cston.explain.widget.refresh.PullToRefreshBase;
import platform.cston.explain.widget.refresh.PullToRefreshListView;
import platform.cston.httplib.bean.TrajectoryResult;
import platform.cston.httplib.search.OnResultListener;
import platform.cston.httplib.search.TrajectorySearch;

/**
 * 历史轨迹列表页
 * Created by zhou-pc on 2016/4/13.
 */
public class HistoricaltrackActivity extends Activity implements View.OnClickListener {

    private LayoutInflater inflater;
    private FrameLayout mMainLayout;//父容器
    private LinearLayout mView_back;//返回按钮
    private RelativeLayout mDataLayout;
    private PullToRefreshListView mListView;//展示列表

    private Button mDatePickerBtn;//显示日期按钮
    private View listEmptyView;//拉取数据为空时，显示的内容
    private ViewTipModule mViewTipModule;//预加载页面
    private DateActionSheetDialog dateActionSheet; //日期选择控件
    private Calendar mMinCalendar;//最小允许日期
    private Calendar mMaxCalendar;//最大允许日期

    private final static int SIZE = 10;//每页显示数量
    private final int QUERY_TYPE_PULL_UP = 1;//以start为最大时间,加载以前的轨迹
    private final int QUERY_TYPE_PULL_DOWN = 2;//以start为最小时间
    private final int QUERY_TYPE_REFRESH = 3;//刷新列表

    private int mQueryType = QUERY_TYPE_PULL_UP;//初始化请求类型

    private long chooseTime;//毫秒数
    private long queryStartTime;//查询开始时间

    private String tempDateStr;//显示轨迹的当前日期
    private String openCarId;//获取轨迹的车id

    private TrackListAdapter mAdapter;//列表适配器
    private LinkedList<TrajectoryResult.TrajectoryListInfo> mTrackArrayList = new LinkedList<>();//轨迹列表


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        openCarId = getIntent().getStringExtra("OPENCARID");//获取上个页面传递的车id，用于请求轨迹列表
        setContentView(R.layout.cst_platform_activity_track);
        mMainLayout = (FrameLayout) findViewById(R.id.main_layout);
        mDataLayout = (RelativeLayout) findViewById(R.id.data_layout);
        mView_back = (LinearLayout) findViewById(R.id.dynamics_back);
        mListView = (PullToRefreshListView) findViewById(R.id.track_rule_list);
        mDatePickerBtn = (Button) findViewById(R.id.date_picker_btn);
        initListView();//初始化轨迹列表
        initDatePicker();//初始化日期选择控件
        queryStartTime = System.currentTimeMillis();
        mViewTipModule = new ViewTipModule(this, mMainLayout, mDataLayout,
                new ViewTipModule.Callback() {
                    @Override
                    public void getData() {
                        GetTrajectory(queryStartTime, mQueryType);
                    }
                }
        );
        GetTrajectory(queryStartTime, mQueryType);
        mDatePickerBtn.setOnClickListener(this);
        mView_back.setOnClickListener(this);
    }

    /**
     * 显示日期
     */
    private void setDatePickerBtn() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(chooseTime);
        dateActionSheet.setNowDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        dateActionSheet.show();
    }


    /**
     * 初始化轨迹列表
     */
    private void initListView() {
        mAdapter = new TrackListAdapter(this, mTrackArrayList);
        mListView.setAdapter(mAdapter);
        inflater = LayoutInflater.from(this);
        listEmptyView = inflater.inflate(R.layout.cst_platform_common_list_empty_view, null);
        RelativeLayout layout = (RelativeLayout) listEmptyView
                .findViewById(R.id.common_list_empty_layout);
        layout.setVisibility(View.GONE);
        ImageView logo = (ImageView) listEmptyView.findViewById(R.id.none_data_prompt_logo);
        logo.setImageResource(R.drawable.cst_platform_none_track_icon);
        TextView promptTv = (TextView) listEmptyView.findViewById(R.id.none_data_prompt_tv);
        promptTv.setText("您目前暂无轨迹");
        mListView.setEmptyView(listEmptyView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrajectoryResult.TrajectoryListInfo track = mTrackArrayList.get(position - 1);
                if (track == null) {
                    return;
                }
                if (null != track.traceId && !track.traceId.equals("")) {
                    Intent intent = new Intent();
                    intent.setClass(HistoricaltrackActivity.this, TrackDetailActivity.class);
                    intent.putExtra("TraceId", track.traceId);
                    intent.putExtra("OpenCarId", openCarId);
                    HistoricaltrackActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(HistoricaltrackActivity.this, "未找到该轨迹id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //获取滑动第一项，改变底部时间
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (mTrackArrayList != null && mTrackArrayList.size() > 0) {
                    TrajectoryResult.TrajectoryListInfo firstItem = mTrackArrayList.get(firstVisibleItem);
                    setDateTv(firstItem.startTime);
                }

            }
        });

        //监听上下滑动事件
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils
                        .formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL
                        );
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                mQueryType = QUERY_TYPE_PULL_DOWN;
                if (mTrackArrayList == null || mTrackArrayList.size() <= 0) {
                    mListView.onRefreshComplete();
                } else {
                    TrajectoryResult.TrajectoryListInfo firstItem = mTrackArrayList.get(0);
                    //以第一项结束时间为最小时间
                    GetTrajectory(firstItem.stopTime, mQueryType);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils
                        .formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL
                        );
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                mQueryType = QUERY_TYPE_PULL_UP;
                if (mTrackArrayList == null || mTrackArrayList.size() <= 0) {
                    mListView.onRefreshComplete();
                } else {
                    TrajectoryResult.TrajectoryListInfo lastItem = mTrackArrayList.get(mTrackArrayList.size() - 1);
                    //以最后一项开始时间为最大时间
                    GetTrajectory(lastItem.startTime, mQueryType);
                }
            }
        });
    }


    /**
     * 请求轨迹列表
     *
     * @param time      请求时间毫秒值 （10位）
     * @param queryType 请求类型：上翻：QUERY_TYPE_PULL_UP、下翻：QUERY_TYPE_PULL_DOWN、刷新：QUERY_TYPE_REFRESH
     */
    private void GetTrajectory(final Long time, int queryType) {
        TrajectorySearch.newInstance().GetHistoryTrajectoryResult(time, queryType, SIZE, openCarId, new OnResultListener.OnGetTrajectoryResultListener() {
            @Override
            public void onGetTrajectoryResult(TrajectoryResult var1, boolean isError, Throwable ex) {
                mListView.onRefreshComplete();
                if (isError) {
                    mViewTipModule.showFailState();
                    return;
                }
                mViewTipModule.showSuccessState();
                if (null == var1 || var1.getAllTrajectorys() == null || var1.getAllTrajectorys().size() == 0) {//判断数据是否有效，无效return;
                    if (null != var1)
                        Toast.makeText(HistoricaltrackActivity.this, "Message=" + var1.getResult(), Toast.LENGTH_LONG).show();
                    if (mQueryType == QUERY_TYPE_REFRESH) {
                        Toast.makeText(HistoricaltrackActivity.this, "未找到 " + tempDateStr + " 的轨迹", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                chooseTime = time;
                switch (mQueryType) {//根据不同请求类型，执行相应操作
                    case QUERY_TYPE_REFRESH://选择日期后，mQueryType=QUERY_TYPE_REFRESH；
                        setDateTv(chooseTime);
                        mTrackArrayList.clear();
                        mTrackArrayList.addAll(var1.getAllTrajectorys());
                        break;
                    case QUERY_TYPE_PULL_DOWN://列表向下拉，mQueryType=QUERY_TYPE_PULL_DOWN；
                        for (TrajectoryResult.TrajectoryListInfo item : var1.getAllTrajectorys()) {
                            mTrackArrayList.addFirst(item);
                        }
                        break;
                    case QUERY_TYPE_PULL_UP://列表向上拉，mQueryType=QUERY_TYPE_PULL_UP；
                        if (var1.getAllTrajectorys().size() < 1) {
                            Toast.makeText(HistoricaltrackActivity.this, "没有更多轨迹！", Toast.LENGTH_SHORT).show();
                        } else {
                            mTrackArrayList.addAll(var1.getAllTrajectorys());
                        }
                        break;
                }

                mAdapter.notifyDataSetChanged();//刷新列表

                if (mQueryType == QUERY_TYPE_REFRESH) {//重置焦点，使日期显示相应的时间
                    mListView.getRefreshListView().setSelection(0);
                }

                if (mQueryType == QUERY_TYPE_PULL_DOWN) {//重置焦点，使日期显示相应的时间
                    if (var1.getAllTrajectorys().size() == 1) {
                        mListView.getRefreshListView().setSelection(0);
                    } else {
                        mListView.getRefreshListView()
                                .setSelection(var1.getAllTrajectorys().size());
                    }
                }

            }
        });
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
        setDateTv(mMaxCalendar.getTime().getTime());

        dateActionSheet = new DateActionSheetDialog(this, mMinCalendar.get(Calendar.YEAR),
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
                tempDateStr = month + "月" + dayOfMonth + "日";
                long time = StringToDate(date, "yyyy-MM-dd").getTime();
                time = time + 24 * 60 * 60 - 1;
                mQueryType = QUERY_TYPE_REFRESH;
                GetTrajectory(time, QUERY_TYPE_PULL_UP);
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
        mDatePickerBtn.setText(getDate(time, "MM月dd日"));
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.date_picker_btn) {
            setDatePickerBtn();
        } else if (v.getId() == R.id.dynamics_back) {
            finish();
        }
    }

}
