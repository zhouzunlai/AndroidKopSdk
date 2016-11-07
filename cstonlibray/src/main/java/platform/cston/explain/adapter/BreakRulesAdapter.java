package platform.cston.explain.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.activity.BreakRulesMapActivity;
import platform.cston.explain.utils.DTUtils;
import platform.cston.httplib.bean.IllegalRecordResult;

/**
 * Created by daifei on 2016/9/1.
 */
public class BreakRulesAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String mOpenCarId;
    private List<IllegalRecordResult.DataEntity.ListsEntity> data;


    public BreakRulesAdapter(Context context, List<IllegalRecordResult.DataEntity.ListsEntity> data, String carId) {
        this.context = context;
        this.data = data;
        mOpenCarId = carId;
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
            convertView = inflater.inflate(R.layout.cst_platform_break_rules_item, null);
            holder = new ViewHolder();
            holder.ll = (LinearLayout) convertView.findViewById(R.id.report_break_rules_bg);
            holder.tvHappenTime = (TextView) convertView.findViewById(R.id.report_break_rules_time);
            holder.ivMore = (ImageView) convertView.findViewById(R.id.report_break_rules_arrow);
            holder.tvPlace = (TextView) convertView.findViewById(R.id.report_break_rules_place);
            holder.tvDetail = (TextView) convertView.findViewById(R.id.report_break_rules_detail);
            holder.ivScore = (ImageView) convertView.findViewById(R.id.report_break_rules_score_iv);
            holder.tvScore = (TextView) convertView.findViewById(R.id.report_break_rules_score_tv);
            holder.ivExpense = (ImageView) convertView.findViewById(R.id.report_break_rules_expense_iv);
            holder.tvExpense = (TextView) convertView.findViewById(R.id.report_break_rules_expense_tv);
            holder.ivIsHandle = (ImageView) convertView.findViewById(R.id.report_break_rules_ishandle_iv);
            holder.tvIsHanlde = (TextView) convertView.findViewById(R.id.report_break_rule_ishandle_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (data.get(position).handled != null) {
            if (data.get(position).handled.equals("1"))//处理
            {
                holder.ll.setBackgroundResource(R.drawable.cst_platform_break_rules_handle_bg);
                holder.ivScore.setImageResource(R.drawable.cst_platform_break_rules_score_handle);
                holder.ivExpense.setImageResource(R.drawable.cst_platform_break_rules_expense_handle);
                holder.ivIsHandle.setImageResource(R.drawable.cst_platform_break_rules_handle);
                holder.tvScore.setTextColor(Color.parseColor("#999999"));
                holder.tvExpense.setTextColor(Color.parseColor("#999999"));
                holder.tvIsHanlde.setTextColor(Color.parseColor("#999999"));
                holder.tvIsHanlde.setText("已处理");

            } else//未处理
            {
                holder.ll.setBackgroundResource(R.drawable.cst_paltform_break_rules_unhandle_bg);
                holder.ivScore.setImageResource(R.drawable.cst_platform_break_rules_score_unhandle);
                holder.ivExpense.setImageResource(R.drawable.cst_platform_break_rules_expense_unhandle);
                holder.ivIsHandle.setImageResource(R.drawable.cst_platform_break_rules_unhandle);
                holder.tvScore.setTextColor(Color.parseColor("#ff7c68"));
                holder.tvExpense.setTextColor(Color.parseColor("#ff7c68"));
                holder.tvIsHanlde.setTextColor(Color.parseColor("#ff7c68"));
                holder.tvIsHanlde.setText("未处理");
            }
        } else {
            holder.ll.setBackgroundResource(R.drawable.cst_paltform_break_rules_unhandle_bg);
            holder.ivScore.setImageResource(R.drawable.cst_platform_break_rules_score_unhandle);
            holder.ivExpense.setImageResource(R.drawable.cst_platform_break_rules_expense_unhandle);
            holder.ivIsHandle.setImageResource(R.drawable.cst_platform_break_rules_unhandle);
        }


        if (data.get(position).date != null) {
            holder.tvHappenTime.setText(DTUtils.LongToStrYMDHMS(DTUtils.StrTimeToLongLine(data.get(position).date)));
        } else {
            holder.tvHappenTime.setText("");
        }

        if (data.get(position).area != null) {
            holder.tvPlace.setText(data.get(position).area);
        } else {
            holder.tvPlace.setText("");
        }

        if (data.get(position).act != null) {
            holder.tvDetail.setText(data.get(position).act);
        } else {
            holder.tvDetail.setText("");
        }


        if (data.get(position).fen != null) {
            holder.tvScore.setText(data.get(position).fen);
        } else {
            holder.tvScore.setText("0");
        }

        if (data.get(position).money != null) {
            holder.tvExpense.setText(data.get(position).money);
        } else {
            holder.tvExpense.setText("0");
        }
        if (data.get(position).latitude <= 0 || data.get(position).longitude <= 0) {
            holder.ivMore.setVisibility(View.INVISIBLE);
        } else {
            holder.ivMore.setVisibility(View.VISIBLE);
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BreakRulesMapActivity.class);
                    intent.putExtra("OPENCARID", mOpenCarId);
                    intent.putExtra("INFO", data.get(position));
                    context.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        LinearLayout ll;
        TextView tvHappenTime;
        ImageView ivMore;
        TextView tvPlace;
        TextView tvDetail;
        ImageView ivScore;
        TextView tvScore;
        ImageView ivExpense;
        TextView tvExpense;
        ImageView ivIsHandle;
        TextView tvIsHanlde;
    }
}
