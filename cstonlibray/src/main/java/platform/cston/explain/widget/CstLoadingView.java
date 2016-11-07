package platform.cston.explain.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import cston.cstonlibray.R;


public class CstLoadingView extends View {

    private Context mContext;

    /** The circle's center X coordinate */
    float cx;

    /** The circle's center Y coordinate */
    float cy;

    Matrix mBoardBitmapMatrix;

    //内圈旋转角度
    private float mInnerAngle = 0;

    //外圈旋转角度
    private float mOuterAngle = 0;

    Bitmap mInnerBm;

    Bitmap mOuterBm;

    int bgOffset;

    //加载圈动画
    ObjectAnimator loadAnim;

    public CstLoadingView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CstLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mInnerBm = BitmapFactory.decodeResource(getResources(), R.drawable.cst_platform_load_ico_inner);
        mOuterBm = BitmapFactory.decodeResource(getResources(), R.drawable.cst_platform_load_ico_outer);
        // 背景偏移值
        bgOffset = dip2px(mContext, 24f);
        bgOffset = 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        cx = getWidth() / 2;
        cy = getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //外圈顺时针旋转
        canvas.save();
        //画布旋转x度
        canvas.rotate(mInnerAngle, cx, cy);
        canvas.drawBitmap(mOuterBm, 0, 0, null);
        canvas.restore();
        //内圈逆时针旋转
        canvas.save();
        //画布旋转x度
        canvas.rotate(-mInnerAngle, cx, cy);
        canvas.drawBitmap(mInnerBm, 0, 0, null);
        canvas.restore();
    }

    public  int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public float getInnerAngle() {
        return mInnerAngle;
    }

    public void setInnerAngle(float innerAngle) {
        mInnerAngle = innerAngle;
        invalidate();
    }

    public float getOuterAngle() {
        return mOuterAngle;
    }

    public void setOuterAngle(float outerAngle) {
        mOuterAngle = outerAngle;
    }

    /** 节点动画持续时间 */
    final static long mAnimDuration = 1000;

    volatile boolean mIsAnimaStopped;

    public synchronized void startLoading() {
        loadAnim = ObjectAnimator.ofFloat(this,
                "innerAngle", 0, 359).setDuration(mAnimDuration);
        loadAnim.setInterpolator(new LinearInterpolator());
        loadAnim.setRepeatCount(Animation.INFINITE);
        loadAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        startAnim();
    }

    private void startAnim() {
        mIsAnimaStopped = false;
        loadAnim.start();
    }

    public synchronized void stopLoading() {
        if (loadAnim != null) {
            mIsAnimaStopped = true;
            loadAnim.end();
        }
    }
}
