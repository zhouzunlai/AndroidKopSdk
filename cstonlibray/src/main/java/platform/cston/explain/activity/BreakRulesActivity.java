package platform.cston.explain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.adapter.BreakRulesAdapter;
import platform.cston.explain.bean.CstTopTitleInfo;
import platform.cston.explain.widget.ListViewNoVScroll;
import platform.cston.httplib.bean.IllegalRecordResult;
import platform.cston.httplib.search.CarReportSearch;
import platform.cston.httplib.search.OnResultListener;

/**
 * 违章列表首页
 * Created by daifei on 2016/9/1.
 */
public class BreakRulesActivity extends CstBaseActivity {

    private TextView tvBreakRulesNum;//违章总数
    private TextView tvBreakRuleScore;//违章扣分
    private TextView tvBreakRuleExpense;//违章罚款
    private ScrollView scrollView;

    private ListViewNoVScroll lv;//ListView数据列表

    private String mOpenCarId;//查询的车的id

    private List<IllegalRecordResult.DataEntity.ListsEntity> mData = new ArrayList<>();//违章数据列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.cst_platform_break_rules_report);
        tvBreakRulesNum = (TextView) findViewById(R.id.report_break_rules_num);
        tvBreakRuleScore = (TextView) findViewById(R.id.report_break_rules_score);
        tvBreakRuleExpense = (TextView) findViewById(R.id.report_break_rules_expense);
        scrollView=(ScrollView) findViewById(R.id.break_rules_scroll);
        lv = (ListViewNoVScroll) findViewById(R.id.report_break_rule_lv);
        TextView lHeadLeftTv = (TextView) findViewById(R.id.cst_platform_header_left_text);
        setHeaderLeftTextBtn();
        lHeadLeftTv.setText(getString(R.string.cst_platform_detect_back));
    }

    /**
     * 获取传递过来的车Id、车牌号
     * 通过数据接口获取违章数据
     */
    private void initData() {
        Intent intent = getIntent();
        mOpenCarId = intent.getStringExtra("OPENCARID");
        String lPlate = intent.getStringExtra("PLATE");
        if (null != lPlate)
            setHeaderTitle(lPlate);
        if (null != mOpenCarId)
        CarReportSearch.newInstance().GetCarIllegalRecordResult(mOpenCarId, new OnResultListener.CarIllegalRecordResultListener() {
                @Override
                public void onCarIllegalRecordResult(IllegalRecordResult var1, boolean isError, Throwable ex) {
                    if (isError) {
                        Toast.makeText(BreakRulesActivity.this, "出现问题", Toast.LENGTH_SHORT).show();
                    } else {
                        if (null != var1.getData()) {
                            tvBreakRulesNum.setText("" + var1.getData().violateSum);
                            tvBreakRuleScore.setText("" + var1.getData().violateScore);
                            tvBreakRuleExpense.setText("" + var1.getData().violateMoney);
                            if (null != var1.getData().getLists() && var1.getData().getLists().size() > 0) {
                                mData.addAll(var1.getData().getLists());
                                BreakRulesAdapter lAdapter = new BreakRulesAdapter(BreakRulesActivity.this, mData, mOpenCarId);
                                lv.setAdapter(lAdapter);
                                lv.setFocusable(false);
                                scrollView.smoothScrollTo(0, 0);
                            }
                        }
                    }
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeadreColor(CstTopTitleInfo.ColorStatus.BREAKRULES);
    }

}
