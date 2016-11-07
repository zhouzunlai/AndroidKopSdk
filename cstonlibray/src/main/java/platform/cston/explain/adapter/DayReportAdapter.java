package platform.cston.explain.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.activity.DayEventActivity;
import platform.cston.explain.utils.DTUtils;
import platform.cston.httplib.bean.DayReportResult;

/**
 * Created by daifei on 2016/8/24.
 * 天报告的adapter
 */
public class DayReportAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private DayReportResult result;
    private List<DayReportResult.DataEntity> data;

    public DayReportAdapter(Context context, DayReportResult result, List<DayReportResult.DataEntity> data) {
        this.context = context;
        this.data = data;
        this.result = result;
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cst_platform_day_report_list_item, null);
            holder = new ViewHolder();
            holder.viewMark = (View) convertView.findViewById(R.id.left_date_icon);
            holder.reportBottom = (LinearLayout) convertView.findViewById(R.id.report_bottom);
            holder.tvMoreDetail = (TextView) convertView.findViewById(R.id.report_more_detail);
            holder.tvHappenTime = (TextView) convertView.findViewById(R.id.report_happen_time);
            holder.tvExpenses = (TextView) convertView.findViewById(R.id.report_expense_tv);
            holder.tvOilMass = (TextView) convertView.findViewById(R.id.report_oil_mass_tv);
            holder.tvAveOilMass = (TextView) convertView.findViewById(R.id.report_ave_oil_mass_tv);
            holder.tvDriveTime = (TextView) convertView.findViewById(R.id.report_drive_time_tv);
            holder.tvMileage = (TextView) convertView.findViewById(R.id.report_mileage_tv);
            holder.tvAveSpeed = (TextView) convertView.findViewById(R.id.report_ave_speed_tv);
            holder.tvrReportReminderTime = (TextView) convertView.findViewById(R.id.report_reminder_tv);
            holder.tvReminder = (TextView) convertView.findViewById(R.id.report_reminder_content);
            holder.tvNum = (TextView) convertView.findViewById(R.id.report_num_tv);
            holder.tvDriveTimeUnit = (TextView) convertView.findViewById(R.id.report_drive_time_unit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (data.get(i).date != null) {
            if (data.get(i).date.equals(DTUtils.LongToStrTimeDay(System.currentTimeMillis()))) {
                holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_yellow);
            } else {
                holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_gray);
            }
        } else {
            holder.viewMark.setBackgroundResource(R.drawable.cst_platform_timer_shaft_gray);
        }

        holder.tvMoreDetail.setVisibility(View.GONE);
        if (data.get(i).date != null) {
            holder.tvHappenTime.setText(DTUtils.StrToDayDate(data.get(i).date));
        } else {
            holder.tvHappenTime.setText("0");
        }

        if (data.get(i).cost != null) {
            holder.tvExpenses.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).cost), 1)));
        } else {
            holder.tvExpenses.setText("0.0");//开销
        }

        if (data.get(i).fuels != null) {
            holder.tvOilMass.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).fuels), 1)));
        } else {
            holder.tvOilMass.setText("0.0");//耗油量
        }

        if (data.get(i).duration != null) {

            if (Double.compare(DTUtils.StrToDouble(data.get(i).duration) / 3600, 1) > 0) {
                holder.tvDriveTime.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).duration) / 3600, 1)));
                holder.tvDriveTimeUnit.setText("h");
            } else {
                if (Double.compare(DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).duration) / 3600, 1), 1) == 0) {
                    holder.tvDriveTime.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).duration) / 3600, 1)));
                    holder.tvDriveTimeUnit.setText("h");
                } else {

                    if (Double.compare(DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).duration) / 60, 1), 1) < 0 && Double.compare(DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).duration) / 60, 1), 0) > 0) {
                        holder.tvDriveTime.setText("1");
                    } else {
                        holder.tvDriveTime.setText(Integer.toString((int) DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).duration) / 60, 0)));
                    }
                    holder.tvDriveTimeUnit.setText("min");
                }
            }

        } else {
            holder.tvDriveTime.setText("0");//驾驶时间
            holder.tvDriveTimeUnit.setText("min");
        }


        if (data.get(i).miles != null) {
            holder.tvMileage.setText(Double.toString(DTUtils.halfUp(DTUtils.StrToDouble(data.get(i).miles), 1)));
        } else {
            holder.tvMileage.setText("0.0");//总里程
        }


        if (data.get(i).fuels != null && data.get(i).miles != null) {
            if (Double.compare(DTUtils.StrToDouble(data.get(i).miles), 0) != 0) {
                if (data.get(i).kilometers != null) {
                    holder.tvAveOilMass.setText(data.get(i).kilometers);//平均耗油量
                } else {
                    holder.tvAveOilMass.setText("0.0");//平均耗油量
                }
            } else {
                holder.tvAveOilMass.setText("0");
            }
        } else {
            holder.tvAveOilMass.setText("0");
        }


        if (data.get(i).miles != null && data.get(i).duration != null) {
            if (Double.compare(DTUtils.StrToDouble(data.get(i).duration), 0) != 0) {

                if (data.get(i).speed != null) {
                    holder.tvAveSpeed.setText(data.get(i).speed);//平均速度
                } else {
                    holder.tvAveSpeed.setText("0");//平均速度
                }
            } else {
                holder.tvAveSpeed.setText("0");
            }
        } else {
            holder.tvAveSpeed.setText("0");
        }


        if (data.get(i).getEvent() != null) {
            if (data.get(i).getEvent().size() > 0 && data.get(i).getEvent().get(0).category != null && data.get(i).getEvent().get(0).type != null) {
                holder.reportBottom.setVisibility(View.VISIBLE);
                if (data.get(i).getEvent().get(0).category.equals("security")) {
                    holder.tvReminder.setText("车辆，" + DTUtils.CheckEventName(data.get(i).getEvent().get(0).category, data.get(i).getEvent().get(0).type));
                } else if (data.get(i).getEvent().get(0).category.equals("drivingBehavior")) {
                    holder.tvReminder.setText(DTUtils.CheckEventName(data.get(i).getEvent().get(0).category, data.get(i).getEvent().get(0).type));
                }
                holder.tvrReportReminderTime.setText(data.get(i).getEvent().get(0).time);
                holder.tvrReportReminderTime.setText(DTUtils.StrToHHmm(data.get(i).getEvent().get(0).time));
                holder.tvNum.setText(data.get(i).getEvent().size() + "");//提示有多少个
                if (data.get(i).getEvent().size() < 10) {
                    holder.tvNum.setBackgroundResource(R.drawable.cst_platform_message_num_sigle);
                } else {
                    holder.tvNum.setBackgroundResource(R.drawable.cst_platform_message_num_mul);
                }
            } else {
                holder.tvReminder.setText("");
                holder.tvrReportReminderTime.setText("");
                holder.tvrReportReminderTime.setText("");
                holder.tvNum.setText("");
                holder.reportBottom.setVisibility(View.GONE);
            }
        } else {
            holder.tvReminder.setText("");
            holder.tvrReportReminderTime.setText("");
            holder.tvrReportReminderTime.setText("");
            holder.tvNum.setText("");
            holder.reportBottom.setVisibility(View.GONE);
        }

        holder.reportBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.getData().clear();
                result.getData().addAll(data);
                Intent intent = new Intent(context, DayEventActivity.class);
                intent.putExtra("position", i);
                intent.putExtra("DayReportResult", result);//将这边的数据传递过去
                context.startActivity(intent);
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
        TextView tvAveSpeed;//平均速度
        TextView tvAveOilMass;
        TextView tvReminder;//提示
        TextView tvNum;//提示的个数
        TextView tvMoreDetail;//更多详情
        TextView tvDriveTimeUnit;
        TextView tvrReportReminderTime;
        LinearLayout reportBottom;
    }

}
