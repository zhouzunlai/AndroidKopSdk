package platform.cston.explain.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.adapter.DayEventTypeAdapter;
import platform.cston.explain.bean.CstTopTitleInfo;
import platform.cston.httplib.bean.DayReportResult;

/**
 * 日报告提醒事件详情页面
 * Created by daifei on 2016/8/30.
 */
public class DayEventActivity extends CstBaseActivity {

    private ListView lv;//展示数据的列表

    private List<DayReportResult.DataEntity.EventEntity> mDataInfo = new ArrayList<>();//日报告提醒事项

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cst_platform_activity_event_type);
        initData();
        initView();
    }

    private void initData() {
        DayReportResult result = getIntent().getParcelableExtra("DayReportResult");
        int position = getIntent().getIntExtra("position", 0);
        if (null == result || null == result.getData() || result.getData().size() <= 0)
            return;
        mDataInfo = result.getData().get(position).getEvent();
//        Collections.reverse(mDataInfo);
        if (null == mDataInfo)
            return;
        setHeaderTitle(combine(mDataInfo.get(0).time));
    }

    private void initView() {
        DayEventTypeAdapter lAdapter = new DayEventTypeAdapter(this, mDataInfo);
        lv = (ListView) findViewById(R.id.event_lv);
        lv.setAdapter(lAdapter);
        TextView lHeadLeftTv = (TextView) findViewById(R.id.cst_platform_header_left_text);
        lHeadLeftTv.setText(getString(R.string.cst_platform_detect_back));
        setHeaderLeftTextBtn();
    }


    private String combine(String time) {
        String year;
        String month;
        String day;
        year = time.substring(0, 4);
        if (time.substring(4, 6).startsWith("0")) {
            month = time.substring(4, 6).substring(1);
        } else {
            month = time.substring(4, 6);
        }
        if (time.substring(6, 8).startsWith("0")) {
            day = time.substring(6, 8).substring(1);
        } else {
            day = time.substring(6, 8);
        }
        return year + "/" + month + "/" + day;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeadreColor(CstTopTitleInfo.ColorStatus.MONTHEVENT);
    }
}
