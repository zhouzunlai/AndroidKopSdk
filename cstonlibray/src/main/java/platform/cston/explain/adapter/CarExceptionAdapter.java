package platform.cston.explain.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cston.cstonlibray.R;
import platform.cston.explain.activity.CarConditionDetailActivity;
import platform.cston.explain.bean.CarDetectionEntity;
import platform.cston.explain.bean.CarExceptionBean;
import platform.cston.httplib.bean.CarConDectionResult;

/**
 * Created by daifei on 2016/7/8.
 */
public class CarExceptionAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    private List<CarExceptionBean> mData;

    private CarConDectionResult mCarDetectionParcelable;

    private boolean mFlag;
    Drawable nav_up;


    public CarExceptionAdapter(Context context, List<CarExceptionBean> mData, boolean flag, CarConDectionResult carDetectionParcelable) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.mFlag = flag;
        this.mCarDetectionParcelable = carDetectionParcelable;
        nav_up = mContext.getResources().getDrawable(R.drawable.cst_platform_right_arrow);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cst_platform_car_exception_item, null);
        }

        TextView tv_happen_time = ViewHolder.get(convertView, R.id.tv_happen_time);//故障发生时间
        TextView tv_kind_name = ViewHolder.get(convertView, R.id.tv_kind_name);//故障类型名
        LinearLayout ll_exception = (LinearLayout) convertView.findViewById(R.id.ll_exception);
        TextView car_excaption_content = ViewHolder.get(convertView, R.id.car_excaption_content);//故障编码或者值
        TextView car_exception_reminder = ViewHolder.get(convertView, R.id.car_exception_reminder);//有关故障的提示
        TextView tv_head_cause = ViewHolder.get(convertView, R.id.tv_head_cause);
        TextView car_exception_cause = ViewHolder.get(convertView, R.id.car_exception_cause);//故障可能的原因
        TextView tv_head_effect = ViewHolder.get(convertView, R.id.tv_head_effect);
        TextView car_exception_effect = ViewHolder.get(convertView, R.id.car_exception_effect);//故障的影响
        TextView tv_head_advice = ViewHolder.get(convertView, R.id.tv_head_advice);
        TextView car_exception_advice = ViewHolder.get(convertView, R.id.car_exception_advice);//关于故障的建议

        //设置值
        if (mData.get(position).happen_time == null || mData.get(position).happen_time.isEmpty()) {
            tv_happen_time.setVisibility(View.GONE);
        } else {
            tv_happen_time.setVisibility(View.VISIBLE);
            tv_happen_time.setText(mData.get(position).happen_time);
        }
        tv_happen_time.setText(mData.get(position).happen_time);
        tv_kind_name.setText(mData.get(position).kind_name);
        car_excaption_content.setText(mData.get(position).excaption_content);

        //设置相对应的点击事件，根据情况来表示是否禁用该空间的点击事件
        ll_exception.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = mData.get(position).exception_type;
                Intent intent = new Intent(mContext, CarConditionDetailActivity.class);
                intent.putExtra("selectType", type);
                intent.putExtra("OBD", mCarDetectionParcelable);
                if (mFlag) {//紧急需要处理的结果
                    intent.putExtra("level", CarDetectionEntity.DETECTION_ERROR);
                } else {//需要注意的结果
                    intent.putExtra("level", CarDetectionEntity.DETECTION_WARNING);
                }
                if (type.equals(CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY)) {
                    intent.putExtra("title", mContext.getString(R.string.cst_platform_detect_type_battery));
                    mContext.startActivity(intent);
                } else if (type.equals(CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE)) {
                    intent.putExtra("title", mContext.getString(R.string.cst_platform_condiiton_data_analysis));
                    mContext.startActivity(intent);
                }
            }
        });

        String type = mData.get(position).exception_type;
        if (type.equals(CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE)) {//冷却液
            car_excaption_content.setCompoundDrawables(null, null, nav_up, null);
            car_exception_reminder.setText(mContext.getString(R.string.cst_platform_scope_cooling_fluid));
            if (!mData.get(position).exception_cause.isEmpty()) {
                car_exception_cause.setText(mData.get(position).exception_cause);
            } else {
                car_exception_cause.setText(mContext.getString(R.string.cst_platform_no_cause));
            }

            if (!mData.get(position).exception_effect.isEmpty()) {
                car_exception_effect.setText(mData.get(position).exception_effect);
            } else {
                car_exception_effect.setText(mContext.getString(R.string.cst_platform_no_consequence));
            }

            if (!mData.get(position).exception_advice.isEmpty()) {
                car_exception_advice.setText(mData.get(position).exception_advice);
            } else {
                car_exception_advice.setText(mContext.getString(R.string.cst_platform_no_advice));
            }
        } else if (type.equals(CarDetectionEntity.TYPE_DETECTION_CAR_FAULT)) {
            car_excaption_content.setCompoundDrawables(null, null, null, null);
            if (!mData.get(position).exception_reminder.isEmpty() && !mData.get(position).exception_reminder.equals("null")) {
                car_exception_reminder.setVisibility(View.VISIBLE);
                car_exception_reminder.setText(mData.get(position).exception_reminder);
            } else {
                car_exception_reminder.setVisibility(View.GONE);
                car_exception_reminder.setText("");
            }

            if (!mData.get(position).exception_cause.isEmpty() && !mData.get(position).exception_cause.equals("null")) {//如果不为空
                tv_head_cause.setVisibility(View.VISIBLE);
                car_exception_cause.setVisibility(View.VISIBLE);
                if (mData.get(position).exception_cause.contains("<br/>")) {
                    Pattern CRLF = Pattern.compile("<br/>");
                    Matcher m = CRLF.matcher(mData.get(position).exception_cause);
                    String newstr = m.replaceAll(" ");
                    car_exception_cause.setText(newstr);
                } else
                    car_exception_cause.setText(mData.get(position).exception_cause);
            } else {
                car_exception_cause.setText("");
                tv_head_cause.setVisibility(View.GONE);
                car_exception_cause.setVisibility(View.GONE);
            }

            if (!mData.get(position).exception_effect.isEmpty() && !mData.get(position).exception_effect.equals("null")) {//如果不为空
                tv_head_effect.setVisibility(View.VISIBLE);
                car_exception_effect.setVisibility(View.VISIBLE);
                if (mData.get(position).exception_effect.contains("<br/>")) {
                    Pattern CRLF = Pattern.compile("<br/>");
                    Matcher m = CRLF.matcher(mData.get(position).exception_effect);
                    String newstr = m.replaceAll(" ");
                    car_exception_effect.setText(newstr);
                } else
                    car_exception_effect.setText(mData.get(position).exception_effect);
            } else {
                car_exception_effect.setText("");
                tv_head_effect.setVisibility(View.GONE);
                car_exception_effect.setVisibility(View.GONE);
            }

            if (!mData.get(position).exception_advice.isEmpty() && !mData.get(position).exception_advice.equals("null")) {//如果不为空
                tv_head_advice.setVisibility(View.VISIBLE);
                car_exception_advice.setVisibility(View.VISIBLE);
                if (mData.get(position).exception_advice.contains("<br/>")) {
                    Pattern CRLF = Pattern.compile("<br/>");
                    Matcher m = CRLF.matcher(mData.get(position).exception_advice);
                    String newstr = m.replaceAll(" ");
                    car_exception_advice.setText(newstr);
                } else
                    car_exception_advice.setText(mData.get(position).exception_advice);
            } else {
                car_exception_advice.setText("");
                tv_head_advice.setVisibility(View.GONE);
                car_exception_advice.setVisibility(View.GONE);
            }
        } else if (type.equals(CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY)) {
            car_excaption_content.setCompoundDrawables(null, null, nav_up, null);
            car_exception_reminder.setText(mData.get(position).exception_reminder);
            car_exception_cause.setText(mData.get(position).exception_cause);
            car_exception_effect.setText(mData.get(position).exception_effect);
            car_exception_advice.setText(mData.get(position).exception_advice);
        }
        return convertView;
    }
}
