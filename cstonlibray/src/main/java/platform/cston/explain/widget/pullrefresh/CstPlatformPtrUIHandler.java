package platform.cston.explain.widget.pullrefresh;

/**
 * Created by daifei on 2016/5/28.
 */
public interface CstPlatformPtrUIHandler {

    /**
     * Reset ui
     */
    public void onUIReset(MyPtrLayout frame, PtrHandler handler);

    /**
     * prepare for loading
     */
    public void onUIRefreshPrepare(MyPtrLayout frame, PtrHandler handler);

    /**
     * have  arrive offset
     */
    public void onUIRefreshPosition(MyPtrLayout frame, PtrHandler handler, boolean isDown);

    /**
     * perform refreshing UI
     */
    public void onUIRefreshBegin(MyPtrLayout frame);

    public void onUIRefreshLoading(MyPtrLayout frame);

    /**
     * perform UI after refresh
     */
    public void onUIRefreshComplete(MyPtrLayout frame, PtrHandler handler);

    /**
     * perform UI  Change
     */
    public void onUIPositionChange(MyPtrLayout frame, boolean isUnderTouch, byte status,
                                   PtrIndicator ptrIndicator);

    /**
     * perform UI change title after refresh
     */
    public void onChangeSubTile(String text);

    /**
     * perform UI change title background after refresh
     */
    public void onChangeBackground(int resId);

    /**
     * perform UI change arrow arrow when prepare
     */
    public void onUIChangeArrowAngle(int angle);

    /**
     * @param number 检测的项目数量
     * @param score  检测得分
     */
    public void onUIScoreChange(int number, int score);

    /** 执行进度百分比 */
    public void onProgressStart();

}
