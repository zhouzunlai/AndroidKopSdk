package platform.cston.explain.widget.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cston.cstonlibray.R;
import platform.cston.explain.utils.CstPlatformUtils;


/**
 * Created by daifei on 2016/5/28.
 */
public class PtrHeaderBottomView extends LinearLayout implements CstPlatformPtrUIHandler {

    private TextView mTip1;

    private ImageView mArrow;

    private LinearLayout mContentView;

    private String text_tip1 = "一切正常，请放心出行";

    private String text_tip2 = "释放立即检查";

    private final int durationTime = 200;

    private RotateAnimation mRotate1;

    private RotateAnimation mRotate2;

    private PtrHandler mPtrHandler;


    public PtrHeaderBottomView(Context context) {
        super(context);
        initViews(null);
    }

    public PtrHeaderBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public PtrHeaderBottomView(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    public View getContentView() {
        return mContentView;
    }

    protected void initViews(AttributeSet attrs) {
        View header = LayoutInflater.from(getContext())
                .inflate(R.layout.cst_platform_ptr_default_header_bottom, this);
        mTip1 = (TextView) header.findViewById(R.id.tip1);
        mArrow = (ImageView) header.findViewById(R.id.image);
        mContentView = (LinearLayout) header.findViewById(R.id.check_lin_content);
        resetView(false);
        int pivotX = CstPlatformUtils.dip2px(getContext(), 6);
        int pivotY = CstPlatformUtils.dip2px(getContext(), 3.5f);
        mRotate1 = new RotateAnimation(0f, 180f, pivotX, pivotY);
        mRotate1.setDuration(durationTime);
        mRotate1.setFillAfter(true);
        mRotate2 = new RotateAnimation(180f, 0f, pivotX, pivotY);
        mRotate2.setDuration(durationTime);
        mRotate2.setFillAfter(true);
    }


    private void resetView(boolean isAnim) {
        this.setVisibility(View.VISIBLE);
        mTip1.setText(text_tip1);
        if (!isAnim) {
            mArrow.clearAnimation();
        }

    }

    private void prepareView() {
        mTip1.setText(text_tip2);
        mArrow.startAnimation(mRotate1);
    }


    private void loadingView() {
        this.setVisibility(View.GONE);
    }

    @Override
    public void onUIReset(MyPtrLayout frame, PtrHandler handler) {
        resetView(false);
        handler.onRefreshReSet(frame);
    }

    @Override
    public void onUIRefreshPrepare(MyPtrLayout frame, PtrHandler handler) {
        handler.onRefreshOnMove(frame);
    }


    /** 向上，向下移动抵达刷新高度 */
    @Override
    public void onUIRefreshPosition(MyPtrLayout frame, PtrHandler handler, boolean isDown) {
        if (isDown) {
            prepareView();
        } else {
            mArrow.startAnimation(mRotate2);
            resetView(true);
        }

    }

    @Override
    public void onUIRefreshBegin(MyPtrLayout frame) {
        this.setVisibility(View.GONE);
    }

    @Override
    public void onUIRefreshLoading(MyPtrLayout frame) {
        loadingView();
    }

    @Override
    public void onUIRefreshComplete(MyPtrLayout frame, PtrHandler handler) {
        mPtrHandler = handler;
        if (mPtrHandler != null) {
            mPtrHandler.onRefreshComplete(null);
        }
    }

    @Override
    public void onUIPositionChange(MyPtrLayout frame, boolean isUnderTouch, byte status,
                                   PtrIndicator ptrIndicator) {

    }

    @Override
    public void onChangeSubTile(String text) {
        text_tip1 = text;
        mTip1.setText(text_tip1);
    }

    @Override
    public void onChangeBackground(int resId) {
        if (this.getChildCount() > 0) {

//            this.getChildAt(0).setBackgroundColor(getResources().getColor(resId));
        }

    }

    @Override
    public void onUIChangeArrowAngle(int angle) {

//        mArrow.setRotateAngle(angle);

    }

    @Override
    public void onUIScoreChange(int number, int score) {

    }

    @Override
    public void onProgressStart() {

    }

}
