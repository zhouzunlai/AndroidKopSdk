package platform.cston.explain.widget.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by daifei on 2016/5/28.
 */
public class MyPtrLayout extends ViewGroup {


    //constant-------------------------------------------------

    private int mLoadingMinTime = 500;

    private long mLoadingStartTime = 0;

    private int mDurationToCloseHeader = 1000;

    private int mDurationToClose = 200;

    private int mHeaderHeight;

    private int mContentSplHeight; //contentView的位置高度

    private int mBottomSplHeight; //bottomView的位置高度

    //boolean flag-----------------------------------------

    private PtrStatus mStatus = PtrStatus.INIT;

    private boolean mHasSendCancelEvent = false;

    private boolean mKeepHeaderWhenRefresh = true;


    private boolean isMeasure = false;

    //view and instance------------------------------------------------

    private CstPlatformPtrHeaderView mHeaderView;

    private PtrHeaderBottomView mBottomView;

    protected View mContentView;


    private MotionEvent mDownEvent;

    private MotionEvent mLastMoveEvent;

    private PtrIndicator mPtrIndicator;

    private ScrollToAuto mScrollToAuto;

    private PtrHandler mPtrHandler;

    private CstPlatformPtrUIHandler mHeadUiHandler;

    private CstPlatformPtrUIHandler mBottomUiHandler;

    private View mCurrentContentView;

    //animation


    public MyPtrLayout(Context context) {
        super(context);
        init();
    }

    public MyPtrLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mPtrIndicator = new PtrIndicator();
        mScrollToAuto = new ScrollToAuto();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (childCount <= 2) {
            throw new IllegalStateException("MyPtrLayout must host 3 elements");
        } else if (childCount > 3) {
            throw new IllegalStateException("MyPtrLayout only can host 3 elements");
        } else {
            View child1 = getChildAt(0);
            View child2 = getChildAt(1);
            View child3 = getChildAt(2);
            if (child1 instanceof CstPlatformPtrHeaderView) {
                mHeaderView = (CstPlatformPtrHeaderView) child1;
                if (child2 instanceof PtrHeaderBottomView) {
                    mBottomView = (PtrHeaderBottomView) child2;
                    mCurrentContentView = mContentView = child3;
                } else {
                    mBottomView = (PtrHeaderBottomView) child3;
                    mCurrentContentView = mContentView = child2;
                }

            } else if (child2 instanceof CstPlatformPtrHeaderView) {
                mHeaderView = (CstPlatformPtrHeaderView) child2;
                if (child1 instanceof PtrHeaderBottomView) {
                    mBottomView = (PtrHeaderBottomView) child1;
                    mCurrentContentView = mContentView = child3;
                } else {
                    mBottomView = (PtrHeaderBottomView) child3;
                    mCurrentContentView = mContentView = child1;
                }
            } else if (child3 instanceof CstPlatformPtrHeaderView) {
                mHeaderView = (CstPlatformPtrHeaderView) child3;
                if (child1 instanceof PtrHeaderBottomView) {
                    mBottomView = (PtrHeaderBottomView) child1;
                    mCurrentContentView = mContentView = child2;
                } else {
                    mBottomView = (PtrHeaderBottomView) child2;
                    mCurrentContentView = mContentView = child1;
                }
            }


        }

    }


    /**
     * 添加headView
     *
     * @param header 头部视图
     */
    public void setHeaderView(View header) {
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        if (null != header) {
            LayoutParams lp = header.getLayoutParams();
            if (lp == null) {
                lp = new LayoutParams(-1, -2);
                header.setLayoutParams(lp);
            }
            if (header instanceof CstPlatformPtrUIHandler) {
                mHeaderView = (CstPlatformPtrHeaderView) header;
                addView(header);
                setHeadUiHandler((CstPlatformPtrUIHandler) header);
            }
        }
    }

    /**
     * 添加
     *
     * @param bottom
     */
    public void setBottomView(View bottom) {
        if (mBottomView != null && bottom != null && mBottomView != bottom) {
            removeView(mBottomView);
        }
        if (null != bottom) {
        LayoutParams lp = bottom.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            bottom.setLayoutParams(lp);
        }
        if (bottom instanceof CstPlatformPtrUIHandler) {
            mBottomView = (PtrHeaderBottomView) bottom;
            addView(bottom);
            setBottomUiHandler((CstPlatformPtrUIHandler) bottom);
        }
        }
    }


    /**
     * pass uiHandler of headView
     */
    private void setHeadUiHandler(CstPlatformPtrUIHandler ptrUIHandler) {
        if (ptrUIHandler != null) {
            mHeadUiHandler = ptrUIHandler;
        } else {
            throw new IllegalStateException("uiHandler of headView is null");
        }

    }

    private void setBottomUiHandler(CstPlatformPtrUIHandler ptrUIHandler) {
        if (ptrUIHandler != null) {
            mBottomUiHandler = ptrUIHandler;
        } else {
            throw new IllegalStateException("uiHandler of headView is null");
        }

    }

    /**
     * measure
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mBottomView != null) {
            measureBottomView(mBottomView, widthMeasureSpec, heightMeasureSpec);
        }
        if (mHeaderView != null && !isMeasure) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderHeight = mHeaderView.getMeasuredHeight();
            isMeasure = true;
            mPtrIndicator.setHeaderHeight(mBottomView.getMeasuredHeight());
            mPtrIndicator.setPOS_START(mBottomView.getMeasuredHeight());
            mContentSplHeight = mHeaderHeight + mPtrIndicator.getOffsetToRefresh();
            mBottomSplHeight = mHeaderHeight - mPtrIndicator.getOffsetToRefresh();
        }
        if (mHeaderView != null && isMeasure) {
            measureHeadView(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        }

        if (mContentView != null) {
            measureContentView(mContentView, widthMeasureSpec, heightMeasureSpec);

        }

    }

    /**
     * measure content view
     *
     * @param child                   view
     * @param parentWidthMeasureSpec  parent width
     * @param parentHeightMeasureSpec parent height
     */
    private void measureHeadView(View child, int parentWidthMeasureSpec,
                                 int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom(), lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    /**
     * measure bottom view
     *
     * @param child                   view
     * @param parentWidthMeasureSpec  parent width
     * @param parentHeightMeasureSpec parent height
     */
    private void measureBottomView(View child, int parentWidthMeasureSpec,
                                   int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom(), lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    /**
     * measure content view
     *
     * @param child                   view
     * @param parentWidthMeasureSpec  parent width
     * @param parentHeightMeasureSpec parent height
     */
    private void measureContentView(View child, int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();
        int parentHeight = MeasureSpec.getSize(parentHeightMeasureSpec);
        int bottomHeight = MeasureSpec.getSize(mBottomView.getMeasuredHeight());

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), lp.width);
        int heightMeasureSpec = 0;
        switch (mStatus) {
            case PREPARE:
            case INIT:
                heightMeasureSpec = parentHeight - mHeaderHeight - bottomHeight;
                break;

            case LOADING:
                heightMeasureSpec = parentHeight - mHeaderHeight - mPtrIndicator
                        .getOffsetToRefresh();
                break;
            case COMPLETE:
            case RELEASE:
                heightMeasureSpec = parentHeight - mHeaderHeight;
                break;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.EXACTLY);
        final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                0, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        if (isComplete) {
            isComplete = false;
            mContentView.scrollTo(0, 0);
        }

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeaderView != null) {
            final int left = paddingLeft;
            final int top = paddingTop;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);

        }
        if (mBottomView != null) {
            final int left = paddingLeft;
            final int top = paddingTop + mBottomSplHeight;
            final int right = left + mBottomView.getMeasuredWidth();
            final int bottom = top + mBottomView.getMeasuredHeight();
            mBottomView.layout(left, top, right, bottom);
        }

        if (mContentView != null) {
            final int left = paddingLeft;
            final int top = paddingTop + mContentSplHeight;
            final int right = left + mContentView.getMeasuredWidth();
            final int bottom = top + mContentView.getMeasuredHeight();
            mContentView.layout(left, top, right, bottom);
        }

    }


    /**
     * MotionEvent control
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mContentView == null || mHeaderView == null) {
            return super.dispatchTouchEvent(e);
        }
        if ((mStatus != PtrStatus.INIT && mStatus != PtrStatus.PREPARE) || isAutoRefresh()) {
            return true;
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPtrIndicator.onRelease();
                if (mPtrIndicator.hasLeaveStartPosition()) {
                    onRelease(false);
                    if (mPtrIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return super.dispatchTouchEvent(e);
                } else {
                    return super.dispatchTouchEvent(e);
                }

            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mDownEvent = e;
                mPtrIndicator.onPressDown(e.getX(), e.getY());
                mScrollToAuto.abortIfWorking();
                if (mPtrIndicator.hasLeaveStartPosition()) {
                    // do nothing
                } else {
                    return super.dispatchTouchEvent(e);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                mPtrIndicator.onMove(e.getX(), e.getY());
                float offsetY = mPtrIndicator.getOffsetY();
                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mPtrIndicator.hasLeaveStartPosition();

                // 当contentView没有划到顶端，中断移动
                if (moveDown && mPtrHandler != null && !mPtrHandler
                        .checkCanDoRefresh(this, mCurrentContentView, mHeaderView)) {
                    return super.dispatchTouchEvent(e);
                }
                if (e.getY() < mPtrIndicator.getPOS_START() && mPtrHandler
                        .checkCanDoRefresh(this, mCurrentContentView, mHeaderView)) {
                    return super.dispatchTouchEvent(e);
                }

                if (((moveUp && canMoveUp) || moveDown)) {
                    movePosition(offsetY);
                    return true;
                }

        }
        return super.dispatchTouchEvent(e);
    }

    private void sendCancelEvent() {
        MotionEvent last = mDownEvent;
        MotionEvent e = null;
        last = mLastMoveEvent;
        try {
            e = MotionEvent.obtain(last.getDownTime(),
                    last.getEventTime() + ViewConfiguration.getLongPressTimeout(),
                    MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());

        } catch (NullPointerException exception) {
            return;
        }
        super.dispatchTouchEvent(e);

    }

    private void sendDownEvent() {
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = null;
        try {
            e = MotionEvent
                    .obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN,
                            last.getX(), last.getY(), last.getMetaState());
        } catch (NullPointerException exception) {
            return;
        }
        super.dispatchTouchEvent(e);
    }


    /**
     * measure and layout headView
     */
    private void layoutHeadView(int change) {

        if (mHeaderView != null) {
            mContentSplHeight = mContentSplHeight + change;
            mBottomSplHeight = mBottomSplHeight + change;
        }


    }


    /**
     * position move
     *
     * @param offsetY y
     */
    private void movePosition(float offsetY) {

        if (offsetY < 0 && mPtrIndicator.isInStartPosition()) {
            return;
        }

        int toPosition = mPtrIndicator.getCurrentPosY() + (int) offsetY;

        if (mPtrIndicator.willOverTop(toPosition) && mPtrIndicator.isUnderTouch()) {
            toPosition = mPtrIndicator.getPOS_START();
            offsetY = mHeaderHeight - mContentSplHeight;
        }

        mPtrIndicator.setCurrentPos(toPosition);
        int change = toPosition - mPtrIndicator.getLastPosY();
        updatePosition(change);

    }

    /**
     * update position
     *
     * @param change change size
     */
    private void updatePosition(int change) {
        if (change == 0) {
            return;
        }

        boolean isUnderTouch = mPtrIndicator.isUnderTouch();

        if (isUnderTouch && !mHasSendCancelEvent && mPtrIndicator.hasMovedAfterPressedDown()) {
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }
        if ((mPtrIndicator.hasJustLeaveStartPosition() && mStatus == PtrStatus.INIT) ||
                (mPtrIndicator.goDownCrossFinishPosition() && mStatus == PtrStatus.COMPLETE)) {
            mStatus = PtrStatus.PREPARE;
            mHeadUiHandler.onUIRefreshPrepare(this, mPtrHandler);
            mBottomUiHandler.onUIRefreshPrepare(this, mPtrHandler);
        }

        // 返回到初始位置
        if (mPtrIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();
//            // 还原事件给content
            if (isUnderTouch) {
                sendDownEvent();
            }
        }

        // 下拉刷新
        if (mStatus == PtrStatus.PREPARE) {
            // 从上往下移动的时候到达刷新高度
            if (isUnderTouch && !isAutoRefresh() && mPtrIndicator
                    .crossRefreshLineFromTopToBottom()) {
                mBottomUiHandler.onUIRefreshPosition(this, mPtrHandler, true);
                mHeadUiHandler.onUIRefreshPosition(this, mPtrHandler, true);
            }
            // 从下往上移动的时候到达刷新高度
            if (isUnderTouch && !isAutoRefresh() && mPtrIndicator
                    .crossRefreshLineFromBottomToTop()) {
                mBottomUiHandler.onUIRefreshPosition(this, mPtrHandler, false);
                mHeadUiHandler.onUIRefreshPosition(this, mPtrHandler, false);
            }
            // 自动刷新的时候到达header的高度
            if (isAutoRefresh() && mPtrIndicator.hasJustReachedHeaderHeightFromTopToBottom()) {
                tryToPerformRefresh();
            }
        }

        layoutHeadView(change);
//        mHeaderView.offsetTopAndBottom(change);
        mBottomView.offsetTopAndBottom(change);
        mContentView.offsetTopAndBottom(change);
        invalidate();
    }

    public boolean isAutoRefresh() {
        return isAutoRefreshing;
    }

    private boolean isAutoRefreshing = false;

    private enum PtrStatus {

        INIT(1), PREPARE(2), RELEASE(3), LOADING(4), COMPLETE(5);

        PtrStatus(int type) {
            this.type = type;
        }

        public int type = 0;
    }

    /**
     * touch release
     */
    private void onRelease(boolean stayForLoading) {

        tryToPerformRefresh();

        if (mStatus == PtrStatus.RELEASE) {
            // 保持header刷新
            if (mKeepHeaderWhenRefresh) {
                // scroll header back
                if (mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && !stayForLoading) {
                    //先弹到底再回弹到loading

                    mScrollToAuto.tryToScrollToBack(mPtrIndicator.getOffsetToRefresh(),
                            mDurationToClose);


                } else {
                    // do nothing
                }
            } else {
                ;
                tryScrollBackToTopWhileLoading();
            }
        } else {
            if (mStatus == PtrStatus.COMPLETE) {
                notifyUIRefreshComplete();
            } else {
                tryScrollBackToTopAbortRefresh();
            }
        }
    }

    /**
     * setting ptrLayout handler listener ***********************************
     */
    public void setPtrHandler(PtrHandler ptrHandler) {
        mPtrHandler = ptrHandler;
    }

    /**
     * auto refresh  自动刷新调取方法
     */
    public void autoRefresh() {
        isAutoRefreshing = true;
        autoRefresh(true, mDurationToCloseHeader);
    }


    private void autoRefresh(boolean atOnce, int duration) {

        if (mStatus != PtrStatus.INIT) {
            return;
        }


        if (atOnce) {
            mPtrIndicator.setCurrentPos(mPtrIndicator.getOffsetToRefresh());
            mStatus = PtrStatus.LOADING;
            mLoadingStartTime = System.currentTimeMillis();
            if (mPtrHandler != null) {
                mPtrHandler.onRefreshRequest(this);
            }
            if (mHeadUiHandler != null) {
                mHeadUiHandler.onUIRefreshLoading(this);
            }
            if (mBottomUiHandler != null) {
                mBottomUiHandler.onUIRefreshLoading(this);
            }
        }
    }

    /**
     * 开启百分比进度条更新
     *
     * @param number 需要检测的项数量
     * @param score  检测得分
     */
    public void startProgressCount(int number, int score) {
        mHeadUiHandler.onUIScoreChange(number, score);
        if (mHeadUiHandler != null) {
            mHeadUiHandler.onProgressStart();
        }

    }


    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    private void tryScrollBackToTop() {
        if (!mPtrIndicator.isUnderTouch()) {
            mScrollToAuto.tryToScrollTo(mPtrIndicator.getPOS_START(), mDurationToCloseHeader);
        }
    }


    private void notifyUIRefreshComplete() {

        mHeadUiHandler.onUIRefreshComplete(this, mPtrHandler);
        mBottomUiHandler.onUIRefreshComplete(this, mPtrHandler);
        mPtrIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
    }

    private void performRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mHeadUiHandler != null) {
            mHeadUiHandler.onUIRefreshBegin(this);
            mBottomUiHandler.onUIRefreshBegin(this);
        }

        if (mPtrHandler != null) {
            mPtrHandler.onRefreshRequest(this);
        }
    }

    /**
     * Do refresh complete work when time elapsed is greater than {@link #mLoadingMinTime}
     */
    private void performRefreshComplete() {
        mStatus = PtrStatus.COMPLETE;

        // 假如是自动刷新不做任何事情
        if (mScrollToAuto.mIsRunning && isAutoRefresh()) {
            // do nothing
            return;
        }

        notifyUIRefreshComplete();
    }


    final public void refreshComplete() {

        int delay = (int) (mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime));
        if (delay <= 0) {
            performRefreshComplete();
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    performRefreshComplete();
                }
            }, delay);
        }
    }


    /**
     * If at the top and not in loading, reset
     */

    private boolean isComplete = false;

    private boolean tryToNotifyReset() {
        if (isAutoRefresh()) {
            isAutoRefreshing = false;
        }
        if ((mStatus == PtrStatus.COMPLETE || mStatus == PtrStatus.PREPARE) && mPtrIndicator
                .isInStartPosition()) {
            mHeadUiHandler.onUIReset(this, mPtrHandler);
            mBottomUiHandler.onUIReset(this, mPtrHandler);
            mStatus = PtrStatus.INIT;
            isComplete = true;

            return true;
        }
        return false;
    }

    private boolean tryToPerformRefresh() {
        if (mStatus != PtrStatus.PREPARE) {
            return false;
        }
        if ((mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && true) || mPtrIndicator
                .isOverOffsetToRefresh()) {
            mStatus = PtrStatus.RELEASE;
            performRefresh();

        }

        return false;
    }


    protected void onPtrScrollFinish() {
        if (mPtrIndicator.hasLeaveStartPosition()) {
            onRelease(true);
        }
        if (mStatus == PtrStatus.RELEASE) {
            mStatus = PtrStatus.LOADING;
        }
        if (mStatus == PtrStatus.COMPLETE) {
            tryToNotifyReset();
        }


    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {

        return p instanceof LinearLayout.LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void changeSubTitle(String text) {
        if (mBottomUiHandler != null) {
            mBottomUiHandler.onChangeSubTile(text);
        }

    }

    public void changeSubBackground(int resId) {
        this.setBackgroundColor(getResources().getColor(resId));
    }


    /**
     * 自动滚动
     */
    class ScrollToAuto implements Runnable {

        private Scroller mScroller;

        private boolean mIsRunning = false;

        private int mLastFlingY;

        private int mStart;

        private int mTo;//滚动最终到达的位置

        private int mToUp;//向上弹动的高度

        private int mToDown;//向下弹动的高度

        private boolean mIsKickBack1 = false;

        private boolean mIsKickBack2 = false;

        public ScrollToAuto() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean isFinish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (!isFinish) {
                mLastFlingY = curY;
                movePosition(deltaY);
                if (mIsKickBack1 && Math.abs(deltaY) <= 1) {
                    mIsKickBack1 = false;
                    mIsKickBack2 = true;
                    tryToScrollTo(mToDown, mDurationToClose);
                } else if (mIsKickBack2 && Math.abs(deltaY) <= 1) {
                    mIsKickBack2 = false;
                    tryToScrollTo(mTo, mDurationToClose);
                } else {
                    post(this);
                }

            } else {
                finish();
            }
        }

        private void finish() {
            reset();
            onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mIsKickBack1 = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                reset();
            }
        }

        public void tryToScrollTo(int toPosition, int duration) {
            if (!mIsKickBack1 && !mIsKickBack2 && mPtrIndicator.isAlreadyHere(toPosition)) {
                return;
            }
            mStart = mPtrIndicator.getCurrentPosY();
            int distance = toPosition - mStart;
            removeCallbacks(this);

            mLastFlingY = 0;

            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }

        public void tryToScrollToBack(int toPosition, int duration) {

            if (mPtrIndicator.isAlreadyHere(toPosition)) {
                return;
            }
            mStart = mPtrIndicator.getCurrentPosY();
            mTo = toPosition;
            mToUp = toPosition - mPtrIndicator.getOffsetToRefresh() + 20;
            mToDown = mToUp + mPtrIndicator.getOffsetToRefresh() + 30;
            int distance = mToUp - mStart;
            removeCallbacks(this);

            mLastFlingY = 0;

            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
            mIsKickBack1 = true;
        }
    }

}
