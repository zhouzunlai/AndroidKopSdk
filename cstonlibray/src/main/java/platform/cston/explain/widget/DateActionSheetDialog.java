package platform.cston.explain.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

import cston.cstonlibray.R;


public class DateActionSheetDialog extends ActionSheetDialog {


    public interface OnDoneListener {

        /**
         * 完成事件
         *
         * @param month 月份(1-12)
         */
        public void onDone(int year, int month, int dayOfMonth);
    }

    private OnDoneListener onDoneListener;

    private Context mContext;

    private View rootView;

    DatePicker dp;

    // 日期选择最小日期
    Calendar mMinCalendar;

    // 日期选择最大日期
    Calendar mMaxCalendar;

    /**
     * 初始化一个没有设置最小值或最大值的日期选择控件
     */
    public DateActionSheetDialog(Context context) {
        super(context);
        mContext = context;
        initDatePickerView();
    }

    /**
     * 初始化一个设置最小值的日期选择控件
     *
     * @param minMonth 1-12
     */
    public DateActionSheetDialog(Context context, int minYear, int minMonth, int minDay) {
        super(context);
        mContext = context;
        // 最小允许日期
        mMinCalendar = Calendar.getInstance();
        //minMonth--;
        mMinCalendar.set(minYear, minMonth, minDay, 0, 0, 0);
        initDatePickerView();
    }

    /**
     * 初始化一个设置最大值的日期选择控件
     *
     * @param maxMonth 1-12
     */
    public DateActionSheetDialog(Context context, int maxYear, int maxMonth, int maxDay,
                                 boolean isMax) {
        super(context);
        mContext = context;
        // 最大允许日期
//        maxMonth--;
        mMaxCalendar = Calendar.getInstance();
        mMaxCalendar.set(maxYear, maxMonth, maxDay, 0, 0, 0);
        initDatePickerView();
    }

    /**
     * 初始化一个设置最大值、最大值的日期选择控件
     *
     * @param minMonth 1-12
     * @param maxMonth 1-12
     */
    public DateActionSheetDialog(Context context, int minYear, int minMonth, int minDay,
                                 int maxYear, int maxMonth, int maxDay) {
        super(context);
        mContext = context;
        // 最小允许日期
        mMinCalendar = Calendar.getInstance();
        mMinCalendar.set(minYear, minMonth, minDay, 0, 0, 0);
        // 最大允许日期
        mMaxCalendar = Calendar.getInstance();
        mMaxCalendar.set(maxYear, maxMonth, maxDay, 0, 0, 0);
        if (mMaxCalendar.before(mMinCalendar)) {
            mMaxCalendar.set(minYear, minMonth, minDay, 0, 0, 0);
        }
        initDatePickerView();
    }

    /**
     * 初始化日期选择视图
     */
    void initDatePickerView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        rootView = inflater.inflate(R.layout.cst_platform_common_data_choose, null);
        dp = (DatePicker) rootView.findViewById(R.id.day);
        Button doneBtn = (Button) rootView.findViewById(R.id.done_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                dismiss();
                if (onDoneListener != null) {
                    dp.clearFocus();
                    onDoneListener.onDone(dp.getYear(), (dp.getMonth() + 1), dp.getDayOfMonth());
                }
            }
        });

        dp.init(1970, 0, 2, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year,
                    int monthOfYear, int dayOfMonth) {
                // 检查时间是否在最大最小范围内
                final Calendar newCalendar = Calendar.getInstance();
                newCalendar.set(year, monthOfYear, dayOfMonth, 0,
                        0, 0);
                if (mMaxCalendar != null && newCalendar.after(mMaxCalendar)) {
                    view.init(
                            mMaxCalendar.get(Calendar.YEAR),
                            mMaxCalendar.get(Calendar.MONTH),
                            mMaxCalendar.get(Calendar.DAY_OF_MONTH),
                            this);
                    return;
                } else if (mMinCalendar != null && newCalendar.before(mMinCalendar)) {
                    view.init(
                            mMinCalendar.get(Calendar.YEAR),
                            mMinCalendar.get(Calendar.MONTH),
                            mMinCalendar.get(Calendar.DAY_OF_MONTH),
                            this);
                    return;
                }
            }
        });

        setContentView(rootView);
    }

    /**
     * 设置完成按钮
     */
    public void setOnDoneListener(OnDoneListener l) {
        onDoneListener = l;
    }

    /**
     * 设置时间空间当前时间
     *
     * @param year  年
     * @param month 1-12
     */
    public void setNowDate(int year, int month, int dayOfMonth) {
        try {
            dp.updateDate(year, month - 1, dayOfMonth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNowDateYM(int year, int month, int dayOfMonth)
    {
        try {
            ((ViewGroup)((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            dp.updateDate(year, month - 1, dayOfMonth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


      /**
     * 设置时间空间当前时间
     */
    public void setNowDate(Calendar calendar) {
        if (calendar == null) {
            return;
        }
        int nowYear = calendar.get(Calendar.YEAR);
        int nowMonth = calendar.get(Calendar.MONTH) + 1;
        int nowDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        setNowDate(nowYear, nowMonth, nowDayOfMonth);
    }

}
