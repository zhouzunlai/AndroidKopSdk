package platform.cston.explain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.utils.DTUtils;
import platform.cston.httplib.bean.YearReportResult;
import platform.cston.httplib.search.OnResultListener;
import platform.cston.httplib.search.ReportRequest;

/**
 * Created by daifei on 2016/8/30.
 */
public class YearReportAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<YearReportResult.DataEntity> data;
    private String openCarId;

    public YearReportAdapter(Context context, String openCarId, YearReportResult result, List<YearReportResult.DataEntity> data) {
        this.context = context;
        this.data = data;
        this.openCarId = openCarId;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cst_platform_day_report_list_item, null);
            holder = new ViewHolder();
            holder.viewMark = (View) convertView.findViewById(R.id.left_date_icon);
            holder.tvHappenTime = (TextView) convertView.findViewById(R.id.report_happen_time);
            holder.ll = (LinearLayout) convertView.findViewById(R.id.report_detail);
            holder.tvExpenses = (TextView) convertView.findViewById(R.id.report_expense_tv);
            holder.tvOilMass = (TextView) convertView.findViewById(R.id.report_oil_mass_tv);
            holder.tvDriveTime = (TextView) convertView.findViewById(R.id.report_drive_time_tv);
            holder.tvMoreDetail = (TextView) convertView.findViewById(R.id.report_more_detail);
            holder.tvMileage = (TextView) convertView.findViewById(R.id.report_mileage_tv);
            holder.tvDriveDay = (TextView) convertView.findViewById(R.id.report_ave_oil_mass_tv);
            holder.tvMaxMileage = (TextView) convertView.findViewById(R.id.report_ave_speed_tv);
            holder.tvDriveDayUnit = (TextView) convertView.findViewById(R.id.report_ave_oil_unit);
            holder.tvMaxMileageUnit = (TextView) convertView.findViewById(R.id.report_ave_speed_unit);
            holder.tvDriveDayTitle = (TextView) convertView.findViewById(R.id.report_ave_oil_title);
            holder.tvMaxMileageTitle = (TextView) convertView.findViewById(R.id.report_ave_speed_title);
            holder.tvMonthUnit = (TextView) convertView.findViewById(R.id.report_ave_speed_unit);
            holder.reportBottom = (LinearLayout) convertView.findViewById(R.id.report_bottom);
            holder.tvDriveTimeUnit = (TextView) convertView.findViewById(R.id.report_drive_time_unit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.reportBottom.setVisibility(View.GONE);
        holder.ll.setBackgroundResource(R.color.cst_platform_year_top_bg);

        //设置高亮原点的显示

        if (data.get(position).year != null) {
            if (data.get(position).year.equals(DTUtils.LongToStrTimeYear(System.currentTimeMillis()))) {
                holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_yellow);
            } else {
                holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_gray);
            }
        } else {
            holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_gray);
        }

        if (data.get(position).year != null) {
            holder.tvHappenTime.setText(data.get(position).year);
        } else {
            holder.tvHappenTime.setText("0");
        }

        if (data.get(position).cost != null) {
            holder.tvExpenses.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).cost), 1)));
        } else {
            holder.tvExpenses.setText("0.0");
        }

        if (data.get(position).fuels != null) {
            holder.tvOilMass.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).fuels), 1)));
        } else {
            holder.tvOilMass.setText("0.0");
        }

        if (data.get(position).duration != null) {
            if (Double.compare(DTUtils.StrToDouble(data.get(position).duration) / 3600, 1) > 0) {
                holder.tvDriveTime.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).duration) / 3600, 1)));
                holder.tvDriveTimeUnit.setText("h");
            } else {
                if (Double.compare(DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).duration) / 3600, 1), 1) == 0) {
                    holder.tvDriveTime.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).duration) / 3600, 1)));
                    holder.tvDriveTimeUnit.setText("h");
                } else {
                    if (Double.compare(DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).duration) / 60, 1), 1) < 0 && Double.compare(DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).duration) / 60, 1), 0) > 0) {
                        holder.tvDriveTime.setText("1");
                    } else {
                        holder.tvDriveTime.setText(Integer.toString((int) DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).duration) / 60, 0)));
                    }
                    holder.tvDriveTimeUnit.setText("min");
                }
            }
        } else {
            holder.tvDriveTime.setText("0");//驾驶时间
            holder.tvDriveTimeUnit.setText("min");
        }

        if (data.get(position).driveNum != null) {
            holder.tvDriveDay.setText(data.get(position).driveNum);
        } else {
            holder.tvDriveDay.setText("0");
        }

        if (data.get(position).maxMonth != null) {
            if (Double.compare(DTUtils.StrToDouble(data.get(position).maxMonth), 0) != 0) {
                holder.tvMaxMileageUnit.setText("月");
                holder.tvMaxMileage.setText(data.get(position).maxMonth + "");//这里要加单位月
            } else {
                holder.tvMaxMileageUnit.setText("");
                holder.tvMaxMileage.setText("");
            }

        } else {
            holder.tvMaxMileageUnit.setText("");
            holder.tvMaxMileage.setText("");//这里要加单位月
        }


        if (data.get(position).miles != null) {

            holder.tvMileage.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(position).miles), 1)));
        } else {
            holder.tvMileage.setText("0.0");
        }

        holder.tvDriveDayTitle.setText("开车天数");
        holder.tvMaxMileageTitle.setText("最大里程月");
        holder.tvDriveDayUnit.setText("天");
        holder.tvMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data.get(position).year == null)
                    return;
                ReportRequest.getInstance().StarYearReport(context, openCarId, Integer.parseInt(data.get(position).year), new OnResultListener.OnStratReportListener() {
                    @Override
                    public void OnStratReportResult(boolean isSuccess, String result) {
                        if (!isSuccess) {
                            Toast.makeText(context, "打开年报告失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return convertView;
    }


    class ViewHolder {
        View viewMark;
        TextView tvHappenTime;
        TextView tvExpenses;//费用
        TextView tvOilMass;//耗油量
        TextView tvDriveTime;//驾驶时长
        TextView tvMileage;//驾驶里程
        TextView tvDriveDay;//开车天数
        TextView tvMaxMileage;//最大里程数
        TextView tvDriveDayUnit;//单位
        TextView tvMaxMileageUnit;//单位
        TextView tvMonthUnit;//单位
        LinearLayout reportBottom;//底部
        TextView tvDriveDayTitle;//驾驶天数title
        TextView tvMaxMileageTitle;//最大里程日title
        TextView tvMoreDetail;//查看详情
        LinearLayout ll;//背景
        TextView tvDriveTimeUnit;
    }
}
