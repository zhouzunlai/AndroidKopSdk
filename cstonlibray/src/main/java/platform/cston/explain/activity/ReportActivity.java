package platform.cston.explain.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cston.cstonlibray.R;
import platform.cston.explain.fragment.DayReportFragment;
import platform.cston.explain.fragment.MonthReportFragment;
import platform.cston.explain.fragment.YearReportFragment;

public class ReportActivity extends FragmentActivity {

    private FragmentManager mFragmentManager;

    private LinearLayout mTab_dayLl;//日选项卡按钮
    private LinearLayout mTab_monLl;//月选项卡按钮
    private LinearLayout mTab_yearLl;//年选项卡按钮

    private TextView tvDay;//日选项卡内容
    private TextView tvMon;//月选项卡内容
    private TextView tvYear;//年选项卡内容

    private DayReportFragment mDayFragment;//日报告页面
    private MonthReportFragment mMonthFragment;//月报告页面
    private YearReportFragment mYearFragment;//年报告页面

    private int TAB_DAY = 0;//表示选项卡日报告
    private int TAB_MONTH = 1;//表示选项卡月报告
    private int TAB_YEAR = 2;//表示选项卡年报告
    private int mTabSelectStatu;//当前选择的选项卡状态

    public String openCarId;//查询的车的id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cst_platform_activity_report);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        openCarId = getIntent().getStringExtra("OPENCARID");
        if (null == openCarId)
            return;
        mFragmentManager = getSupportFragmentManager();
        mDayFragment = new DayReportFragment();
        mMonthFragment = new MonthReportFragment();
        mYearFragment = new YearReportFragment();
        FragmentTransaction lFragmentTransaction = mFragmentManager.beginTransaction();
        lFragmentTransaction.add(R.id.report_fragment, mDayFragment);
        lFragmentTransaction.add(R.id.report_fragment, mMonthFragment);
        lFragmentTransaction.add(R.id.report_fragment, mYearFragment);
        lFragmentTransaction.commit();
    }

    private void initView() {
        mTab_dayLl = (LinearLayout) findViewById(R.id.cst_platform_report_day_btn);
        mTab_monLl = (LinearLayout) findViewById(R.id.cst_platform_report_mon_btn);
        mTab_yearLl = (LinearLayout) findViewById(R.id.cst_platform_report_year_btn);

        tvDay = (TextView) findViewById(R.id.cst_platform_report_day_text);
        tvMon = (TextView) findViewById(R.id.cst_platform_report_mon_text);
        tvYear = (TextView) findViewById(R.id.cst_platform_report_year_text);

        TextView tvHeaderTitle = (TextView) findViewById(R.id.cst_platform_header_title);
        TextView tvLeftText = (TextView) findViewById(R.id.cst_platform_header_left_text);
        tvHeaderTitle.setText("用车报告");
        tvHeaderTitle.setTextSize(16);
        tvLeftText.setText("返回");
        tvLeftText.setVisibility(View.VISIBLE);
        tvLeftText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ChangeTabStatu(TAB_DAY);
    }

    /**
     * 初始化顶部日月年选项卡点击事件，切换背景颜色，改变字体颜色，显示相应的Fragment
     */
    private void initListener() {
        mTab_dayLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTabSelectStatu = TAB_DAY;
                ChangeTabStatu(mTabSelectStatu);
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.hide(mYearFragment);
                fragmentTransaction.hide(mMonthFragment);
                fragmentTransaction.show(mDayFragment);
                fragmentTransaction.commit();
            }
        });
        mTab_monLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTabSelectStatu = TAB_MONTH;
                ChangeTabStatu(mTabSelectStatu);
                FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
                fragmentTransaction1.hide(mYearFragment);
                fragmentTransaction1.hide(mDayFragment);
                fragmentTransaction1.show(mMonthFragment);
                fragmentTransaction1.commit();
            }
        });
        mTab_yearLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTabSelectStatu = TAB_YEAR;
                ChangeTabStatu(mTabSelectStatu);
                FragmentTransaction fragmentTransaction2 = mFragmentManager.beginTransaction();
                fragmentTransaction2.hide(mMonthFragment);
                fragmentTransaction2.hide(mDayFragment);
                fragmentTransaction2.show(mYearFragment);
                fragmentTransaction2.commit();
            }
        });
    }

    /**
     * 显示日报告的页面，从月报告中跳转到此页面
     *
     * @param time 月报告中传递过来的时间，为当月最后一天
     */
    public void showDayFragment(String time) {
        mTabSelectStatu = TAB_DAY;
        ChangeTabStatu(mTabSelectStatu);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(mYearFragment);
        fragmentTransaction.hide(mMonthFragment);
        fragmentTransaction.remove(mDayFragment);
        mDayFragment = DayReportFragment.newInstance(time);
        fragmentTransaction.add(R.id.report_fragment, mDayFragment);
        fragmentTransaction.show(mDayFragment);
        fragmentTransaction.commit();
    }

    /**
     * 显示月报告的页面，从年报告中跳转到此页面
     *
     * @param time 年报告中传递过来的时间，为当前月
     */
    public void showMonthFragment(String time) {
        mTabSelectStatu = TAB_MONTH;
        ChangeTabStatu(mTabSelectStatu);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(mYearFragment);
        fragmentTransaction.hide(mDayFragment);
        fragmentTransaction.remove(mMonthFragment);
        mMonthFragment = mMonthFragment.newInstance(time);
        fragmentTransaction.add(R.id.report_fragment, mMonthFragment);
        fragmentTransaction.show(mMonthFragment);
        fragmentTransaction.commit();
    }

    /**
     * 根据index改变tab的选中状态
     *
     * @param index
     */
    private void ChangeTabStatu(int index) {
        if (index == TAB_DAY) {
            mTab_dayLl.setBackgroundResource(R.drawable.cst_platform_tab_left_press);
            mTab_monLl.setBackgroundResource(R.drawable.cst_platform_tab_mid_normal);
            mTab_yearLl.setBackgroundResource(R.drawable.cst_platform_tab_right_normal);
            tvDay.setTextColor(Color.parseColor("#FF878786"));
            tvMon.setTextColor(Color.parseColor("#ffffff"));
            tvYear.setTextColor(Color.parseColor("#ffffff"));
        } else if (index == TAB_MONTH) {
            mTab_dayLl.setBackgroundResource(R.drawable.cst_platform_tab_left_normal);
            mTab_monLl.setBackgroundResource(R.drawable.cst_platform_tab_mid_press);
            mTab_yearLl.setBackgroundResource(R.drawable.cst_platform_tab_right_normal);
            tvDay.setTextColor(Color.parseColor("#ffffff"));
            tvMon.setTextColor(Color.parseColor("#FF878786"));
            tvYear.setTextColor(Color.parseColor("#ffffff"));
        } else if (index == TAB_YEAR) {
            mTab_dayLl.setBackgroundResource(R.drawable.cst_platform_tab_left_normal);
            mTab_monLl.setBackgroundResource(R.drawable.cst_platform_tab_mid_normal);
            mTab_yearLl.setBackgroundResource(R.drawable.cst_platform_tab_right_press);
            tvDay.setTextColor(Color.parseColor("#ffffff"));
            tvMon.setTextColor(Color.parseColor("#ffffff"));
            tvYear.setTextColor(Color.parseColor("#FF878786"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
