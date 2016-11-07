package platform.cston.explain.widget.pullrefresh;

import android.view.View;

/**
 * Created by daifei on 2016/5/28.
 */
public interface PtrHandler {


    /**
     * check
     */
    public boolean checkCanDoRefresh(final MyPtrLayout frame, final View content,
                                     final View header);

    /**
     * When refresh begin
     */
    public void onRefreshRequest(final MyPtrLayout frame);

    /**
     * When refresh begin move
     */
    public void onRefreshOnMove(final MyPtrLayout frame);

    /**
     * When refresh reset
     */
    public void onRefreshReSet(final MyPtrLayout frame);

    /**
     * When refresh complete
     */
    public void onRefreshComplete(final MyPtrLayout frame);
}
