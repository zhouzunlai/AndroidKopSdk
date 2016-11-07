package platform.cston.explain.widget.pullrefresh;

import android.graphics.PointF;

/**
 * Created by daifei on 2016/5/28.
 */
public class PtrIndicator {

    private int POS_START = 0;

    protected int mOffsetToRefresh = 0;

    private PointF mPtLastMove = new PointF();

    private float mOffsetX;

    private float mOffsetY;

    private int mCurrentPos = 0;

    private int mLastPos = 0;

    private int mHeaderHeight;

    private float mRatioOfHeaderHeightToRefresh = 0.5f;

    private int mPressedPos = 0;

    private float mResistance = 1.7f;  //scale

    private boolean mIsUnderTouch = false;

    private int mOffsetToKeepHeaderWhileLoading = -1;

    private int mRefreshCompleteY = 0;

    public boolean isUnderTouch() {
        return mIsUnderTouch;
    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public void onRelease() {
        mIsUnderTouch = false;
    }

    public void onUIRefreshComplete() {
        mRefreshCompleteY = mCurrentPos;
    }

    public boolean goDownCrossFinishPosition() {
        return mCurrentPos >= mRefreshCompleteY;
    }

    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        setOffset(offsetX, offsetY / mResistance);
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mRatioOfHeaderHeightToRefresh = ratio;
        mOffsetToRefresh = (int) (mHeaderHeight * ratio);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return mRatioOfHeaderHeightToRefresh;
    }

    public int getOffsetToRefresh() {
        return mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offset) {
        mRatioOfHeaderHeightToRefresh = mHeaderHeight / offset;
        mOffsetToRefresh = offset;
    }

    public void onPressDown(float x, float y) {
        mIsUnderTouch = true;
        mPressedPos = mCurrentPos;
        mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        float offsetX = x - mPtLastMove.x;
        float offsetY = (y - mPtLastMove.y);
        processOnMove(x, y, offsetX, offsetY);
        mPtLastMove.set(x, y);
    }

    protected void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public int getLastPosY() {
        return mLastPos;
    }

    public int getCurrentPosY() {
        return mCurrentPos;
    }

    public int getPOS_START() {
        return POS_START;
    }

    public void setPOS_START(int POS_START) {
        this.POS_START = POS_START;
    }

    /**
     * Update current position before update the UI
     */
    public final void setCurrentPos(int current) {
        mLastPos = mCurrentPos;
        mCurrentPos = current;
        onUpdatePos(current, mLastPos);
    }

    protected void onUpdatePos(int current, int last) {

    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setHeaderHeight(int height) {
        mHeaderHeight = height;
        updateRefreshHeight();
    }

    protected void updateRefreshHeight() {
        mOffsetToRefresh = (int) (mRatioOfHeaderHeightToRefresh * mHeaderHeight);
    }

    public boolean hasLeaveStartPosition() {
        return mCurrentPos > POS_START;
    }

    public boolean hasJustLeaveStartPosition() {
        return mLastPos == POS_START && hasLeaveStartPosition();
    }

    public boolean hasJustBackToStartPosition() {
        return mLastPos != POS_START && isInStartPosition();
    }

    public boolean isOverOffsetToRefresh() {
        return mCurrentPos >= getOffsetToRefresh() + POS_START;
    }

    public boolean hasMovedAfterPressedDown() {
        return mCurrentPos != mPressedPos;
    }

    public boolean isInStartPosition() {
        return mCurrentPos == POS_START;
    }

    public boolean crossRefreshLineFromTopToBottom() {
        return mLastPos < getOffsetToRefresh() + POS_START
                && mCurrentPos >= getOffsetToRefresh() + POS_START;
    }

    public boolean crossRefreshLineFromBottomToTop() {
        return mLastPos > getOffsetToRefresh() + POS_START
                && mCurrentPos <= getOffsetToRefresh() + POS_START;
    }

    public boolean hasJustReachedHeaderHeightFromTopToBottom() {
        return mLastPos < mHeaderHeight && mCurrentPos >= mHeaderHeight;
    }

    public boolean isOverOffsetToKeepHeaderWhileLoading() {
        return mCurrentPos > getOffsetToKeepHeaderWhileLoading() + POS_START;
    }

    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        mOffsetToKeepHeaderWhileLoading = offset;
    }

    /**
     * 获取当loading的时候保持的高度
     */
    public int getOffsetToKeepHeaderWhileLoading() {
        setOffsetToKeepHeaderWhileLoading(mOffsetToKeepHeaderWhileLoading >= 0 ?
                mOffsetToKeepHeaderWhileLoading : mHeaderHeight / 2);
        return mOffsetToKeepHeaderWhileLoading;
    }

    public boolean isAlreadyHere(int to) {
        return mCurrentPos == to;
    }

    public float getLastPercent() {
        final float oldPercent = mHeaderHeight == 0 ? 0 : mLastPos * 1f / mHeaderHeight;
        return oldPercent;
    }

    public float getCurrentPercent() {
        final float currentPercent = mHeaderHeight == 0 ? 0 : mCurrentPos * 1f / mHeaderHeight;
        return currentPercent;
    }

    public boolean willOverTop(int to) {
        return to < POS_START;
    }

    /**
     * 根据移动距离和下拉刷新的高度计算出旋转的角度
     *
     * @param distance 向下移动的距离{@link }
     */
    public int parserDistanceToAngle(float distance) {
        int angle = 0;
        if (Math.abs(distance) <= getOffsetToKeepHeaderWhileLoading()) {
            float scale = Math.abs(distance) / getOffsetToKeepHeaderWhileLoading();
            angle = (int) (scale * 90);

        } else {
            angle = 90;
        }
        return angle;
    }

}
