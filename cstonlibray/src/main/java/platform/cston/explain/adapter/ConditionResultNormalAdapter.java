package platform.cston.explain.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cston.cstonlibray.R;
import platform.cston.explain.bean.CarDetectionEntity;
import platform.cston.explain.bean.ObdBean;
import platform.cston.explain.utils.CstPlatformUtils;

/**
 * Created by daifei on 2015/6/1.
 */
public class ConditionResultNormalAdapter extends BaseAdapter {
    public static final int CAR_CONDITION_DETAILS = 0;//熄火状态下检测数据的标记
    public static final int DETECTION_DETAILS = 1;//点火状态下检测的数据
    private Context context;
    private LayoutInflater inflater;
    private List<ObdBean> unrelatedFirelList;
    private List<ObdBean> relatedFireList;
    private onMathClickListen mClick;
    private List<ObdBean> mList;
    private int color1;
    private int color2;
    private int position = -1;
    private int acc;//熄火或者点火状态的查询的数据
    private String selectType;

    public ConditionResultNormalAdapter(Context context, List<ObdBean> unrelatedFirelList, List<ObdBean> abnormalList, int acc, String selectType) {
        this.context = context;
        this.unrelatedFirelList = unrelatedFirelList;
        this.relatedFireList = abnormalList;
        this.acc = acc;
        this.selectType = selectType;
        inflater = LayoutInflater.from(context);
        Resources mResources = context.getResources();
        color1 = mResources.getColor(R.color.cst_platform_common_list_title_color);
        color2 = mResources.getColor(R.color.cst_platform_red_check);
        mList = new ArrayList<>();
        mList.addAll(unrelatedFirelList);
        mList.addAll(abnormalList);
    }


    public interface onMathClickListen {
        void onMathtemClick(int positn);
    }

    public void setMathOnClick(onMathClickListen click) {
        mClick = click;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public ObdBean getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cst_platform_detail_in_car_condition_item, null);
        }
        LinearLayout fireStateView = ViewHolder.get(convertView, R.id.fire_state_view);
        LinearLayout detailLayout = ViewHolder.get(convertView, R.id.detail_layout);
        LinearLayout wrapperDetail = ViewHolder.get(convertView, R.id.wrapper_detail);//设置选中效果的容器
        TextView fireStateTv = ViewHolder.get(convertView, R.id.fire_state_tv);
        LinearLayout fire_state_view = (LinearLayout) convertView.findViewById(R.id.fire_state_view);
        TextView name = ViewHolder.get(convertView, R.id.detail_item_name);
        TextView value = ViewHolder.get(convertView, R.id.detail_item_value);
        TextView range = ViewHolder.get(convertView, R.id.detail_item_range);
        View shortLine = ViewHolder.get(convertView, R.id.short_line);
        View headLine = ViewHolder.get(convertView, R.id.head_line);
        View longLine = ViewHolder.get(convertView, R.id.long_line);
        detailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClick.onMathtemClick(position);
            }
        });


        /**设置高亮*/
        if (position == this.position) {
            detailLayout.setBackgroundResource(R.color.high_light);
        } else {
            detailLayout.setBackgroundResource(R.color.cst_platform_white);
        }

        if (position == 0) {
            CstPlatformUtils.visible(headLine);
        } else {
            CstPlatformUtils.gone(headLine);
        }

        /**设置长短线*/
        if ((position == mList.size() - 1) || position == unrelatedFirelList.size() - 1) {
            CstPlatformUtils.gone(shortLine);
            CstPlatformUtils.visible(longLine);
        } else {
            CstPlatformUtils.gone(longLine);
            CstPlatformUtils.visible(shortLine);
        }

        /**如果是第unrelatedFirelList.size()项 就显示空白*/
        if (position == unrelatedFirelList.size()) {
            /**熄火状态的文字*/
            if (acc == CAR_CONDITION_DETAILS) {//
                CstPlatformUtils.visible(fire_state_view);
                fireStateTv.setText(context.getResources().getString(R.string.detection_fire_statue_tv));
            } else if (acc == DETECTION_DETAILS) {
                //是否是熄火状态
                CstPlatformUtils.gone(fireStateView);
            }
        } else {
            CstPlatformUtils.gone(fireStateView);
        }


        if (selectType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_TEMPERATURE)) {
            {//冷却温度
                if ((unrelatedFirelList.size() - 1 < position && position <= mList.size() - 1) &&
                        relatedFireList.get(position - unrelatedFirelList.size()).kind_name.equals(context.getString(R.string.cst_platform_condiiton_coolantCt))) {
                    wrapperDetail.setBackgroundResource(R.color.cst_platform_yellow);
                } else {
                    wrapperDetail.setBackgroundResource(R.color.cst_platform_white);
                }
            }
        } else if (selectType.equals(CarDetectionEntity.TYPE_DETECTION_CAR_BATTERY)) {
            if (position <= unrelatedFirelList.size() - 1 &&
                    unrelatedFirelList.get(position).kind_name.equals(context.getString(R.string.cst_platform_condiiton_batteryVoltage))) {
                wrapperDetail.setBackgroundResource(R.color.cst_platform_yellow);
            } else {
                wrapperDetail.setBackgroundResource(R.color.cst_platform_white);
            }
        } else {
            wrapperDetail.setBackgroundResource(R.color.cst_platform_white);
        }


        if (position <= unrelatedFirelList.size() - 1) {
            final ObdBean meterInfo = unrelatedFirelList.get(position);
            name.setText(meterInfo.kind_name);
            if (meterInfo.support) {
                if (isInteger(meterInfo.kind_name))
                    value.setText(Integer.toString((int) meterInfo.current_value));
                else
                    value.setText(Double.toString(meterInfo.current_value));
                value.setTextColor(meterInfo.abnormal ? color2 : color1);
                name.setTextColor(meterInfo.abnormal ? color2 : color1);
            } else {
                value.setText("-");
                value.setTextColor(color1);
                name.setTextColor(color1);
            }

            if (!TextUtils.isEmpty(meterInfo.normal_value)) {
                range.setText(meterInfo.normal_value);
            } else {
                range.setText("");
            }

        }
        if (unrelatedFirelList.size() - 1 < position && position <= mList.size() - 1) {
            final ObdBean item = relatedFireList.get(position - unrelatedFirelList.size());
            name.setText(item.kind_name);
            if (item.support) {
                if (isInteger(item.kind_name))
                    value.setText(Integer.toString((int) item.current_value));
                else
                    value.setText(Double.toString(item.current_value));

                value.setTextColor(item.abnormal ? color2 : color1);
                name.setTextColor(item.abnormal ? color2 : color1);
            } else {
                value.setText("-");
                value.setTextColor(color1);
                name.setTextColor(color1);
            }

            if (!TextUtils.isEmpty(item.normal_value)) {
                range.setText(item.normal_value);
            } else {
                range.setText("");
            }
        }
        return convertView;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private boolean isInteger(String category) {
        if (category.equals(context.getString(R.string.cst_platform_condiiton_malfunctionNum))) {
            return true;
        } else if (category.equals(context.getString(R.string.cst_platform_condiiton_perResidualFuel))) {
            return true;
        } else if (category.equals(context.getString(R.string.cst_platform_condiiton_residualFuel))) {
            return true;
        } else if (category.equals(context.getString(R.string.cst_platform_condiiton_onflowCt))) {
            return true;
        } else if (category.equals(context.getString(R.string.cst_platform_condiiton_coolantCt))) {
            return true;
        } else if (category.equals(context.getString(R.string.cst_platform_condiiton_environmentCt))) {
            return true;
        } else if (category.equals(context.getString(R.string.cst_platform_condiiton_airPressure))) {
            return true;
        } else if (category.equals(context.getString(R.string.cst_platform_condiiton_engineRuntime))) {
            return true;
        } else if (category.equals(context.getString(R.string.cst_platform_condiiton_enginePayload))) {
            return true;
        }
        return false;
    }
}
