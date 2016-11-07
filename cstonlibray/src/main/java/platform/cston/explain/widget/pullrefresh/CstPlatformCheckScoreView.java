package platform.cston.explain.widget.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import cston.cstonlibray.R;
import platform.cston.explain.utils.CstPlatformUtils;

/**
 * Created by daifei on 2016/5/28.
 */
public class CstPlatformCheckScoreView extends ViewGroup {


    private View normalView;

    private View checkView;

    private int score = 0;

    private int progress = 0;

    private final int ALPHA_TIME = 200;

    private final int ALPHA_TIME_IN = 500;

    private AlphaAnimation normalOut;

    private AlphaAnimation normalIn;

    private AlphaAnimation checkIn;

    private AlphaAnimation checkOut;


    private Context mContext;

    private CheckState mState;

    public TextView mTextScore;

    public TextView mTextProgress;

    public ImageView mCheckBg;



    public enum CheckState {
        NORMAL, CHECKING
    }


    public CstPlatformCheckScoreView(Context context) {
        super(context);
        init();

    }

    public CstPlatformCheckScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CstPlatformCheckScoreView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        mContext = getContext();
        if (normalView == null) {
            normalView = LayoutInflater.from(mContext).inflate(R.layout.cst_platform_item_check_normal, null);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            mTextScore = (TextView) normalView.findViewById(R.id.check_text_score);
            normalView.setLayoutParams(lp);
            normalView.setVisibility(View.GONE);
            addView(normalView);
        }
        if (checkView == null) {
            checkView = LayoutInflater.from(mContext).inflate(R.layout.cst_platform_item_check_checking, null);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            mTextProgress = (TextView) checkView.findViewById(R.id.check_text_progress);
            mCheckBg=(ImageView)checkView.findViewById(R.id.image_bg);

            checkView.setLayoutParams(lp);

            addView(checkView);


            int pivot = CstPlatformUtils.dip2px(getContext(), 64);
            RotateAnimation ratate = new RotateAnimation(0f, 360.0f, pivot, pivot);
            ratate.setRepeatCount(Animation.INFINITE);
            ratate.setDuration(800);
            ratate.setInterpolator(new LinearInterpolator());
            mCheckBg.setAnimation(ratate);//设置旋转

            //初始化渐变动画
            normalOut = initAnim(1f, 0f, ALPHA_TIME);
            normalIn = initAnim(0f, 1f, ALPHA_TIME_IN);
            checkOut = initAnim(1f, 0f, ALPHA_TIME);
            checkIn = initAnim(0f, 1f, ALPHA_TIME_IN);
        }


    }

    private AlphaAnimation initAnim(float fromAlpha, float toAlpha, int time) {

        AlphaAnimation animation = new AlphaAnimation(fromAlpha, toAlpha);
        animation.setInterpolator(new LinearInterpolator());//匀速运动
        animation.setDuration(time);
        animation.setAnimationListener(new Animation.AnimationListener() {//设置动画监听事件
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.reset();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (normalView != null) {//测量容器内宽度和高度
            normalView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (checkView != null) {//测量容器内宽度和高度
            checkView.measure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {//两个布局重叠在一起

        int centerW = (r - l) / 2;
        int centerH = (b - t) / 2;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        //布局的时候全部居中
        if (normalView != null) {
            int normalW = normalView.getMeasuredWidth();
            int normalH = normalView.getMeasuredHeight();
            final int left = paddingLeft + centerW - normalW / 2;
            final int top = paddingTop + centerH - normalH / 2;
            final int right = left + normalW;
            final int bottom = top + normalH;
            normalView.layout(left, top, right, bottom);

        }

        if (checkView != null) {
            int checkW = checkView.getMeasuredWidth();
            int checkH = checkView.getMeasuredHeight();
            final int left = paddingLeft + centerW - checkW / 2;
            final int top = paddingTop + centerH - checkH / 2;
            final int right = left + checkW;
            final int bottom = top + checkH;
            checkView.layout(left, top, right, bottom);
        }

    }


    /**
     * 设置检测状态
     *
     * @param state state
     */
    public void setCheckState(CheckState state, boolean isAnim) {

        mState = state;
        switch (state) {
            case NORMAL:
                normalView.setVisibility(View.VISIBLE);
                checkView.setVisibility(View.GONE);
                if (isAnim) {
                    normalView.startAnimation(normalIn);
                    checkView.startAnimation(checkOut);
                }

                break;
            case CHECKING:
                normalView.setVisibility(View.GONE);
                checkView.setVisibility(View.VISIBLE);
                if (isAnim) {
                    normalView.startAnimation(normalOut);
                    checkView.startAnimation(checkIn);
                }

                break;
        }


    }


    public void reset() {
        setProgress(0);
    }

    public int getScore() {
        return score;
    }

    /**
     * 设置检测分数
     *
     * @param score 检测分数
     */
    public void setScore(int score) {
        this.score = score;
        mTextScore.setText(String.valueOf(score));
    }


    public int getProgress() {
        return progress;
    }

    /**
     * 设置检测进度条
     *
     * @param progress 檢測进度
     */
    public void setProgress(int progress) {
        this.progress = progress;
        mTextProgress.setText(String.valueOf(progress));
    }

}
