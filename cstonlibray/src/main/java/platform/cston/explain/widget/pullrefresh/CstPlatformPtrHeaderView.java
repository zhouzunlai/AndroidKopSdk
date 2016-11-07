package platform.cston.explain.widget.pullrefresh;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import cston.cstonlibray.R;


/**
 * Created by daifei on 2016/5/28.
 */
public class CstPlatformPtrHeaderView extends LinearLayout implements CstPlatformPtrUIHandler {//控制转动的容器

    private CstPlatformCheckScoreView mCheckView;

    public CstPlatformPtrHeaderView(Context context) {
        super(context);
        initViews(null);
    }

    public CstPlatformPtrHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public CstPlatformPtrHeaderView(Context context, AttributeSet attrs,
                                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }


    protected void initViews(AttributeSet attrs) {
        View header = LayoutInflater.from(getContext())
                .inflate(R.layout.cst_platform_ptr_default_header, this);
        mCheckView = (CstPlatformCheckScoreView) header.findViewById(R.id.check_score_view);
        resetView(false);


    }


    private void resetView(boolean isAnim) {
        isDetection = false;
        progress = 0;
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = null;
        }
        if (myTask != null) {
            myTask.cancel();
            myTask = null;
        }
        mCheckView.reset();
        mCheckView.setCheckState(CstPlatformCheckScoreView.CheckState.NORMAL, isAnim);

    }

    private void beginView() {
        mCheckView.setCheckState(CstPlatformCheckScoreView.CheckState.NORMAL, true);
    }

    private void loadingView() {
        mCheckView.setCheckState(CstPlatformCheckScoreView.CheckState.CHECKING, true);
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

    @Override
    public void onUIRefreshPosition(MyPtrLayout frame, PtrHandler handler, boolean isDown) {

    }

    @Override
    public void onUIRefreshBegin(MyPtrLayout frame) {
        onUIRefreshLoading(frame);
    }

    @Override
    public void onUIRefreshLoading(MyPtrLayout frame) {
        loadingView();
    }

    @Override
    public void onUIRefreshComplete(MyPtrLayout frame, PtrHandler handler) {
        mCheckView.setScore(score);
        resetView(true);
        if (handler != null) {
            handler.onRefreshComplete(null);
        }
    }

    @Override
    public void onUIPositionChange(MyPtrLayout frame, boolean isUnderTouch, byte status,
                                   PtrIndicator ptrIndicator) {

    }

    @Override
    public void onChangeSubTile(String text) {
    }

    @Override
    public void onChangeBackground(int resId) {
        if (this.getChildCount() > 0) {

//            this.getChildAt(0).setBackgroundColor(getResources().getColor(resId));
        }

    }

    @Override
    public void onUIChangeArrowAngle(int angle) {

    }

    @Override
    public void onUIScoreChange(int number, int score) {
        this.score = score;
        allTime = ITEM_TIME * number;
        singleTime = allTime / PROGRESS;

    }

    @Override
    public void onProgressStart() {
        startProgress();//开始计数

    }

    private long startTime;

    /** 进度条开始记数 */
    private void startProgress() {
        startTime = System.currentTimeMillis();
        if (singleTime != -1) {
            isDetection = true;
            myTimer = new Timer();
            myTask = new TimerTask() {
                @Override
                public void run() {

                    while (isDetection) {
                        long end = System.currentTimeMillis() - startTime;
                        mHandler.sendEmptyMessage(0);
                        progress++;
                        try {
                            Thread.sleep(singleTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            myTimer.schedule(myTask, 0);
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progress <= PROGRESS) {
                mCheckView.setProgress(progress);
            } else {
                isDetection = false;
                progress = 0;
            }
        }
    };

    private final int PROGRESS = 100;

    private int ITEM_TIME = 600;

    private int singleTime = -1;

    private int allTime = 0;

    private int progress;

    private int score = 100;


    private Timer myTimer;

    private TimerTask myTask;

    private boolean isDetection = false;
}
