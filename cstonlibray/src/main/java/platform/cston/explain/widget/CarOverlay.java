package platform.cston.explain.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cston.cstonlibray.R;


/**
 * Created by zhou-pc on 2016/4/11.
 */
public class CarOverlay extends RelativeLayout {
    private Context context;
    private RelativeLayout layout;
    private TextView mText_turnSpeed;
    private TextView mText_Speed;
    private TextView mText_ProgressTime;
    private TextView mText_car_statu_wifi_gps;
    private LinearLayout mView_turnSpeed;
    private LinearLayout mView_Speed;
    private RelativeLayout mView_Car;
    private RelativeLayout mView_CarFram;
    private RelativeLayout mView_Progress;
    private ImageView mImg_Car;
    private ImageView mImg_CarStop;
    private Handler durationHandler = new Handler();
    private MyRunnable mDurationTimeRunnable;
    private boolean isHasSetDurationTime;
    private boolean hasStateChanged;
    private long mNoSignalTime;
    private int mCurrentStateType;
    private Bitmap normal_Bitmap;
    private Bitmap highlight_Bitmap;


    public CarOverlay(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public CarOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        this.layout = (RelativeLayout) LayoutInflater.from(this.context).inflate(R.layout.cst_platform_car_view, this);
        mText_turnSpeed = (TextView) this.layout.findViewById(R.id.car_statu_menu_left_text);
        mText_Speed = (TextView) this.layout.findViewById(R.id.car_statu_menu_right_text);
        mText_ProgressTime = (TextView) this.layout.findViewById(R.id.car_statu_menu_progresstime);
        mText_car_statu_wifi_gps = (TextView) this.layout.findViewById(R.id.car_statu_menu_wifi_gps);
        mView_turnSpeed = (LinearLayout) this.layout.findViewById(R.id.car_statu_menu_left);
        mView_Speed = (LinearLayout) this.layout.findViewById(R.id.car_statu_menu_right);
        mView_CarFram = (RelativeLayout) this.layout.findViewById(R.id.car_statu_menu);
        mView_Car= (RelativeLayout) this.layout.findViewById(R.id.car_view);
        mView_Progress = (RelativeLayout) this.layout.findViewById(R.id.car_statu_menu_progress);
        mImg_Car = (ImageView) this.layout.findViewById(R.id.car_statu_menu_car);
        mImg_CarStop = (ImageView) this.layout.findViewById(R.id.car_statu_menu_stop);
        normal_Bitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.cst_platform_car_normal);
        highlight_Bitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.cst_platform_car_highlight);
    }


    public void setSpeed(String Speed) {
        if (!TextUtils.isEmpty(Speed))
            mText_Speed.setText(Speed);
    }

    public void setturnSpeed(String turnSpeed) {
        if (!TextUtils.isEmpty(turnSpeed))
            mText_turnSpeed.setText(turnSpeed);
    }

    public void setTurnSpeedGone()
    {
        mView_turnSpeed.setVisibility(INVISIBLE);
    }

    public void setSpeedGone()
    {
        mView_Speed.setVisibility(INVISIBLE);
    }

    class MyRunnable implements Runnable {

        private long mTime;

        public void setTime(long time) {
            mTime = time;
        }

        @Override
        public void run() {
            mTime++;
            if (mTime < 0) {
                mTime = 0;
            }
            isHasSetDurationTime = true;
            String str = mTime > 999 ? "999...s" : mTime + "s";
            mText_ProgressTime.setText(str);
            durationHandler.postDelayed(this, 1000);
        }
    }


    public void pause() {
        if (mDurationTimeRunnable != null) {
            durationHandler.removeCallbacks(mDurationTimeRunnable);
        }
        isHasSetDurationTime = false;
    }

    private void setCarStateTime() {
        //设置状态持续时间
        if (mDurationTimeRunnable == null) {
            mDurationTimeRunnable = new MyRunnable();
        }

        if (hasStateChanged) {
            mDurationTimeRunnable.setTime(mNoSignalTime);
        }

        if (!isHasSetDurationTime) {
            durationHandler.post(mDurationTimeRunnable);
        }
    }


    public void ChangeMarkStatuWithType(int type,long Time) {
        mNoSignalTime=Time;
        if(mCurrentStateType!=type)
            hasStateChanged = true;
        else
            hasStateChanged = false;
        mCurrentStateType=type;
        if (type == 0) {//停车
            pause();
            mView_Progress.setVisibility(INVISIBLE);
            mImg_CarStop.setVisibility(VISIBLE);
            mView_turnSpeed.setVisibility(INVISIBLE);
            mView_Speed.setVisibility(INVISIBLE);
            mView_CarFram.setBackgroundResource(R.drawable.cst_platform_car_frame_norml);
            mImg_Car.setImageBitmap(normal_Bitmap);
            mView_turnSpeed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_left_normal);
            mView_Speed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_right_normal);
        } else if (type == 1) {//车正常
            pause();
            mView_Progress.setVisibility(INVISIBLE);
            mImg_CarStop.setVisibility(INVISIBLE);
            mView_turnSpeed.setVisibility(VISIBLE);
            mView_Speed.setVisibility(VISIBLE);
            mView_CarFram.setBackgroundResource(R.drawable.cst_platform_car_frame_select);
            mImg_Car.setImageBitmap(highlight_Bitmap);
            mView_turnSpeed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_left_highlight);
            mView_Speed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_right_highlight);
        } else if (type == 2) {//车无网络，未停车
            mImg_Car.setImageBitmap(normal_Bitmap);
            mView_turnSpeed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_left_normal);
            mView_Speed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_right_normal);
            mView_Progress.setVisibility(VISIBLE);
            mImg_CarStop.setVisibility(INVISIBLE);
            mView_turnSpeed.setVisibility(VISIBLE);
            mView_Speed.setVisibility(VISIBLE);
            setCarStateTime();
            mText_car_statu_wifi_gps.setText(context.getString(R.string.cst_platform_no_net));
        } else if (type == 3) {//车无GPS，未停车
            mImg_Car.setImageBitmap(normal_Bitmap);
            mView_turnSpeed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_left_highlight);
            mView_Speed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_right_highlight);
            mView_Progress.setVisibility(VISIBLE);
            mImg_CarStop.setVisibility(INVISIBLE);
            mView_turnSpeed.setVisibility(VISIBLE);
            mView_Speed.setVisibility(VISIBLE);
            setCarStateTime();
            mText_car_statu_wifi_gps.setText(context.getString(R.string.cst_platform_no_nlocation));
        }else if (type == 4) {//车无网络、停车
            mView_Progress.setVisibility(VISIBLE);
            mImg_CarStop.setVisibility(VISIBLE);
            mView_turnSpeed.setVisibility(INVISIBLE);
            mView_Speed.setVisibility(INVISIBLE);
            mImg_Car.setImageBitmap(normal_Bitmap);
            setCarStateTime();
            mText_car_statu_wifi_gps.setText(context.getString(R.string.cst_platform_no_net));
        } else if (type == 5) {//车无GPS、停车
            mView_Progress.setVisibility(VISIBLE);
            mImg_CarStop.setVisibility(VISIBLE);
            mView_turnSpeed.setVisibility(INVISIBLE);
            mView_Speed.setVisibility(INVISIBLE);
            mImg_Car.setImageBitmap(normal_Bitmap);
            setCarStateTime();
            mText_car_statu_wifi_gps.setText(context.getString(R.string.cst_platform_no_nlocation));
        }
        else if (type == 6) {//车置灰
            mImg_Car.setImageBitmap(normal_Bitmap);
            mView_turnSpeed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_left_normal);
            mView_Speed.setBackgroundResource(R.drawable.cst_platform_car_statu_menu_right_normal);
        }else if (type == 7) {//车置灰 隐藏翅膀
            mImg_Car.setImageBitmap(normal_Bitmap);
            mView_turnSpeed.setVisibility(INVISIBLE);
            mView_Speed.setVisibility(INVISIBLE);
        }
    }


    public void setCarRotat(double degrees)
    {
        mView_Car.setRotation((float)degrees);
    }


}
