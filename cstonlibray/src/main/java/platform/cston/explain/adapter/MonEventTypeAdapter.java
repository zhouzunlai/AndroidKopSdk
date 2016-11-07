package platform.cston.explain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.utils.DTUtils;
import platform.cston.httplib.bean.MonthReportResult;

/**
 * Created by daifei on 2016/8/26.
 * 月报告采用双重布局
 */
public class MonEventTypeAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<MonthReportResult.DataEntity.MessageEntity> data;

    public MonEventTypeAdapter(Context context, List<MonthReportResult.DataEntity.MessageEntity> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cst_platform_month_report_event_item, null);
            holder = new ViewHolder();
            holder.tvHappenTime = (TextView) convertView.findViewById(R.id.month_event_time);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.month_detail_title);
            holder.tvStartTitle = (TextView) convertView.findViewById(R.id.month_event_type1);
            holder.tvEndTitle = (TextView) convertView.findViewById(R.id.month_event_type2);
            holder.tvStartValue = (TextView) convertView.findViewById(R.id.month_event_value1);
            holder.tvEndValue = (TextView) convertView.findViewById(R.id.month_event_value2);
            holder.tvUnit1 = (TextView) convertView.findViewById(R.id.month_unit1);
            holder.tvUnit2 = (TextView) convertView.findViewById(R.id.month_unit2);
            holder.tvCutOff = (View) convertView.findViewById(R.id.cut_off);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (data.get(i).time != null) {
            holder.tvHappenTime.setText(DTUtils.StrTimeToMMDD(data.get(i).time.split("-")));//时间转换为
        } else {
            holder.tvHappenTime.setText("0");//时间转换为
        }

        if (data.get(i).category != null) {
            if (data.get(i).category.equals("maint")) {
                holder.tvTitle.setText("保养到期，请及时对爱车进行保养");
                holder.tvStartTitle.setText("上次保养里程");
                holder.tvEndTitle.setText("当前总里程");
                holder.tvCutOff.setVisibility(View.VISIBLE);
                if (data.get(i).firstParam == null || data.get(i).firstParam.equals("0")) {
                    holder.tvStartValue.setText("");
                } else {
                    holder.tvStartValue.setText(data.get(i).firstParam);
                }

                if (data.get(i).secondParam == null | data.get(i).secondParam.equals("0")) {
                    holder.tvEndValue.setText("0");
                } else {
                    holder.tvEndValue.setText(data.get(i).secondParam);
                }
                holder.tvEndTitle.setVisibility(View.VISIBLE);
                holder.tvEndValue.setVisibility(View.VISIBLE);
                holder.tvUnit1.setVisibility(View.VISIBLE);
                holder.tvUnit2.setVisibility(View.VISIBLE);
            } else if (data.get(i).category.equals("insurance")) {
                holder.tvTitle.setText("保险到期，请及时续保");
                holder.tvStartTitle.setText("上次购保时间");
                holder.tvEndTitle.setText("保险到期时间");
                holder.tvCutOff.setVisibility(View.VISIBLE);
                if (data.get(i).firstParam.equals("0")) {
                    holder.tvStartValue.setText("");
                } else {
                    holder.tvStartValue.setText(combine(data.get(i).firstParam.split("-")));
                }

                if (data.get(i).secondParam.equals("0")) {
                    holder.tvEndValue.setText("");
                } else {
                    holder.tvEndValue.setText(combine(data.get(i).secondParam.split("-")));
                }
                holder.tvEndTitle.setVisibility(View.VISIBLE);
                holder.tvEndValue.setVisibility(View.VISIBLE);
                holder.tvUnit1.setVisibility(View.GONE);
                holder.tvUnit2.setVisibility(View.GONE);
            } else if (data.get(i).category.equals("inspection")) {
                holder.tvTitle.setText("年审到期，请及时年审");
                holder.tvEndTitle.setVisibility(View.GONE);
                holder.tvEndValue.setVisibility(View.GONE);
                holder.tvStartTitle.setText("年审到期时间");
                holder.tvEndTitle.setText("");
                holder.tvStartValue.setText("");

                if (data.get(i).secondParam.equals("0")) {
                    holder.tvStartValue.setText("");
                } else {
                    holder.tvStartValue.setText(combine(data.get(i).secondParam.split("-")));
                }
                holder.tvUnit1.setVisibility(View.GONE);
                holder.tvUnit2.setVisibility(View.GONE);
                holder.tvCutOff.setVisibility(View.GONE);
            }

        } else {
            holder.tvTitle.setText("未知类型");
            holder.tvStartTitle.setText("未知类型");
            holder.tvEndTitle.setText("未知类型");
            holder.tvStartValue.setText("未知类型");
            holder.tvEndValue.setText("未知类型");
            holder.tvUnit1.setVisibility(View.GONE);
            holder.tvUnit2.setVisibility(View.GONE);
        }


        return convertView;
    }


    class ViewHolder {
        TextView tvHappenTime;
        TextView tvTitle;
        TextView tvStartTitle;
        TextView tvEndTitle;
        TextView tvStartValue;
        TextView tvEndValue;
        TextView tvUnit1;
        TextView tvUnit2;
        View tvCutOff;
    }

    private String combine(String[] arr) {
        String year;
        String month;
        String day;
        year = arr[0];
        if (arr[1].startsWith("0")) {
            month = arr[1].substring(1);
        } else {
            month = arr[1];
        }

        if (arr[2].startsWith("0")) {
            day = arr[2].substring(1);
        } else {
            day = arr[2];
        }

        return year + "年" + month + "月" + day + "日";
    }
}
