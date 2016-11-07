package platform.cston.explain.widget.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;

import platform.cston.explain.utils.CstPlatformUtils;

/**
 * Created by daifei on 2016/5/28.
 */
public class CstPlatformMyPtrHeadLayout extends MyPtrLayout {

    CstPlatformPtrHeaderView mHeaderView;

    PtrHeaderBottomView mBottomView;

    public CstPlatformMyPtrHeadLayout(Context context) {
        super(context);
        initViews();
    }

    public CstPlatformMyPtrHeadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }


    private void initViews() {
        mHeaderView = new CstPlatformPtrHeaderView(getContext());
        LayoutParams lp = mHeaderView.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams. MATCH_PARENT,
                    CstPlatformUtils.dip2px(getContext(), 148));
        }
        mHeaderView.setLayoutParams(lp);
        mHeaderView.onUIRefreshLoading(this);//进行刷新操作
        setHeaderView(mHeaderView);

        mBottomView = new PtrHeaderBottomView(getContext());
        LayoutParams lb = mBottomView.getLayoutParams();
        if (lb == null) {
            lb = new LayoutParams(LayoutParams.MATCH_PARENT,
                    CstPlatformUtils.dip2px(getContext(), 58));
        }
        mBottomView.setLayoutParams(lb);
        mBottomView.onUIRefreshLoading(this);
        setBottomView(mBottomView);
    }
}
