package platform.cston.explain.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.activity.MonEventActivity;
import platform.cston.explain.utils.DTUtils;
import platform.cston.httplib.bean.MonthReportResult;
import platform.cston.httplib.search.OnResultListener;
import platform.cston.httplib.search.ReportRequest;

/**
 * Created by daifei on 2016/8/29.
 */
public class MonReportAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private MonthReportResult result;
    private List<MonthReportResult.DataEntity> data;
    private String openCarId;

    public MonReportAdapter(Context context, String openCarId, MonthReportResult result, List<MonthReportResult.DataEntity> data) {
        this.context = context;
        this.result = result;
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
            holder.ll = (LinearLayout) convertView.findViewById(R.id.report_detail);
            holder.tvContent = (TextView) convertView.findViewById(R.id.report_reminder_content);
            holder.viewMark = (View) convertView.findViewById(R.id.left_date_icon);
            holder.tvMoreDetail = (TextView) convertView.findViewById(R.id.report_more_detail);
            holder.reportBottom = (LinearLayout) convertView.findViewById(R.id.report_bottom);
            holder.tvHappenTime = (TextView) convertView.findViewById(R.id.report_happen_time);
            holder.tvExpenses = (TextView) convertView.findViewById(R.id.report_expense_tv);
            holder.tvOilMass = (TextView) convertView.findViewById(R.id.report_oil_mass_tv);
            holder.tvDriveTime = (TextView) convertView.findViewById(R.id.report_drive_time_tv);
            holder.tvMileage = (TextView) convertView.findViewById(R.id.report_mileage_tv);
            holder.tvDriveDay = (TextView) convertView.findViewById(R.id.report_ave_oil_mass_tv);
            holder.tvMaxMileage = (TextView) convertView.findViewById(R.id.report_ave_speed_tv);
            holder.tvReminder = (TextView) convertView.findViewById(R.id.report_reminder_tv);
            holder.tvNum = (TextView) convertView.findViewById(R.id.report_num_tv);
            holder.tvDriveDayTitle = (TextView) convertView.findViewById(R.id.report_ave_oil_title);
            holder.tvMaxMileageTitle = (TextView) convertView.findViewById(R.id.report_ave_speed_title);
            holder.tvDriveDayUnit = (TextView) convertView.findViewById(R.id.report_ave_oil_unit);
            holder.tvMaxMileageUnit = (TextView) convertView.findViewById(R.id.report_ave_speed_unit);
            holder.ivMore = (ImageView) convertView.findViewById(R.id.iv_more);
            holder.tvDriveTimeUnit = (TextView) convertView.findViewById(R.id.report_drive_time_unit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (data.get(position).year != null && data.get(position).month != null) {
            String time;
            if (data.get(position).month.length() == 1) {
                time = data.get(position).year + "0" + data.get(position).month;
            } else {
                time = data.get(position).year + data.get(position).month;
            }
            if (time.equals(DTUtils.LongToStrTimeMonth(System.currentTimeMillis()))) {
                holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_yellow);
            } else {
                holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_gray);
            }
        } else {
            holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_gray);
        }


        if (data.get(position).getMessageList() != null && data.get(position).getMessageList().size() > 0) {
            holder.reportBottom.setVisibility(View.VISIBLE);

            if (data.get(position).getMessageList().get(0).time != null) {
                holder.tvReminder.setText(combine(data.get(position).getMessageList().get(0).time.split("-")));//设置时间
            } else {
                holder.tvReminder.setText("");//设置时间
            }

            holder.tvNum.setText("" + data.get(position).getMessageList().size());//设置大小

            if (data.get(position).getMessageList().get(0).category != null) {
                if (data.get(position).getMessageList().get(0).category.equals("maint")) {
                    holder.tvContent.setText("保养到期，请及时对爱车进行保养");
                } else if (data.get(position).getMessageList().get(0).category.equals("insurance")) {
                    holder.tvContent.setText("保险到期，请及时续保");
                } else if (data.get(position).getMessageList().get(0).category.equals("inspection")) {
                    holder.tvContent.setText("年审到期，请及时年审");
                }
            }


            if (data.get(position).getMessageList().size() < 10) {
                holder.tvNum.setBackgroundResource(R.drawable.cst_platform_message_num_sigle);
            } else {
                holder.tvNum.setBackgroundResource(R.drawable.cst_platform_message_num_mul);
            }
        } else {
            holder.reportBottom.setVisibility(View.GONE);
        }

        holder.ll.setBackgroundResource(R.color.cst_platform_month_top_bg);

        if (data.get(position).year != null && data.get(position).month != null) {
            holder.tvHappenTime.setText(data.get(position).year + "/" + data.get(position).month);
        } else {
            holder.tvHappenTime.setText("");
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
            holder.tvDriveTime.setText("0");
            holder.tvDriveTimeUnit.setText("min");
        }

        if (data.get(position).driveNum != null) {
            holder.tvDriveDay.setText(data.get(position).driveNum);
        } else {
            holder.tvDriveDay.setText("0");
        }

        if (data.get(position).maxMonth != null) {
            if (Double.compare(DTUtils.StrToDouble(data.get(position).maxMonth), 0) != 0) {
                if (DTUtils.DateToStrMMDD(DTUtils.StrToDate(data.get(position).maxMonth.substring(0, 8))).startsWith("0")) {
                    holder.tvMaxMileage.setText(DTUtils.DateToStrMMDD(DTUtils.StrToDate(data.get(position).maxMonth.substring(0, 8))).replace("0", ""));
                } else {
                    holder.tvMaxMileage.setText(DTUtils.DateToStrMMDD(DTUtils.StrToDate(data.get(position).maxMonth.substring(0, 8))));
                }
            } else {
                holder.tvMaxMileage.setText("");
            }
        } else {
            holder.tvMaxMileage.setText("");
        }


        if (data.get(position).miles != null) {
            holder.tvMileage.setText(DTUtils.getMileage(DTUtils.StrToDouble(data.get(position).miles)));
        } else {
            holder.tvMileage.setText("0.0");
        }
        holder.tvDriveDayTitle.setText(context.getString(R.string.cst_platform_report_time_of_drive));
        holder.tvMaxMileageTitle.setText(context.getString(R.string.cst_platform_report_max_mileage_time));
        holder.tvDriveDayUnit.setText(context.getString(R.string.cst_platform_report_day_unit));
        holder.tvMaxMileageUnit.setVisibility(View.GONE);

        holder.tvMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String month;
                if (data.get(position).month == null) {
                    return;
                }
                if (data.get(position).month.startsWith("0")) {
                    month = data.get(position).month.substring(1);
                } else {
                    month = data.get(position).month;
                }
                ReportRequest.getInstance().StarMonthReport(context, openCarId, Integer.parseInt(data.get(position).year), Integer.parseInt(month),
                        new OnResultListener.OnStratReportListener() {
                            @Override
                            public void OnStratReportResult(boolean isSuccess, String result) {
                                if (!isSuccess) {
                                    Toast.makeText(context, "打开月报告失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        holder.reportBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.getData().clear();
                result.getData().addAll(data);
                Intent intent = new Intent(context, MonEventActivity.class);
                intent.putExtra("MonthReportResult", result);//将这边的数据传递过去
                intent.putExtra("position", position);
                if (data.get(position).year != null && data.get(position).month != null) {
                    intent.putExtra("time", data.get(position).year + "/" + data.get(position).month);
                } else {
                    intent.putExtra("time", "");
                }
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        View viewMark;//如果被选中，则高亮显示
        TextView tvHappenTime;
        TextView tvExpenses;//费用
        TextView tvOilMass;//耗油量
        TextView tvDriveTime;//驾驶时长
        TextView tvMileage;//驾驶里程
        TextView tvDriveDay;//开车天数
        TextView tvMaxMileage;//最大里程数
        TextView tvReminder;//提示
        TextView tvNum;//提示的个数
        TextView tvDriveDayUnit;//单位
        TextView tvMaxMileageUnit;//单位
        TextView tvDriveDayTitle;//驾驶天数title
        TextView tvMaxMileageTitle;//最大里程日title
        TextView tvMoreDetail;//查看详情
        TextView tvContent;
        LinearLayout ll;
        LinearLayout reportBottom;
        ImageView ivMore;
        TextView tvDriveTimeUnit;
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

        return month + "月" + day + "日";
    }
}
