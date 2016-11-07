package platform.cston.explain.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.adapter.MonEventTypeAdapter;
import platform.cston.explain.bean.CstTopTitleInfo;
import platform.cston.httplib.bean.MonthReportResult;

/**
 * 月报告提醒事件页面
 * Created by daifei on 2016/8/30.
 */
public class MonEventActivity extends CstBaseActivity {

    private ListView lv;//展示数据的列表

    private List<MonthReportResult.DataEntity.MessageEntity> mDataInfo = new ArrayList<>();//月报告提醒事项

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cst_platform_activity_event_type);
        initView();
        initData();
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.event_lv);
        TextView lHeadLeftTv = (TextView) findViewById(R.id.cst_platform_header_left_text);
        setHeaderLeftTextBtn();
        lHeadLeftTv.setText(getString(R.string.cst_platform_detect_back));
    }

    /**
     * 获取从一级页面传递过来的数据
     */
    private void initData() {
        int position = getIntent().getIntExtra("position", 0);
        MonthReportResult result = getIntent().getParcelableExtra("MonthReportResult");
        String time=getIntent().getStringExtra("time");
        if (null == result || null == result.getData() || result.getData().size() <= 0)
            return;
        mDataInfo = result.getData().get(position).getMessageList();
        if (null == mDataInfo || mDataInfo.size() <= 0)
            return;
        MonEventTypeAdapter lAdapter = new MonEventTypeAdapter(MonEventActivity.this, mDataInfo);
        lv.setAdapter(lAdapter);
        if (null != mDataInfo.get(0).time && mDataInfo.get(0).time.contains("-")) {
            String TitleTime = Combine(mDataInfo.get(0).time.split("-"));
            if (null != TitleTime) {
                setHeaderTitle(TitleTime);
            }
        }

        if(time!=null)
            setHeaderTitle(time);
    }

    /**
     * 组装字符串时间格式
     *
     * @param arr
     * @return
     */
    private String Combine(String[] arr) {
        if (arr.length == 2) {
            String year;
            String month;
            year = arr[0];
            if (arr[1].startsWith("0")) {
                month = arr[1].substring(1);
            } else {
                month = arr[1];
            }
            return year + "/" + month;
        } else {
            return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeadreColor(CstTopTitleInfo.ColorStatus.MONTHEVENT);
    }
}
