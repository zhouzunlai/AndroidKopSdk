package platform.cston.explain.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by daifei on 2016/5/28.
 */
public class ListViewNoVScroll extends ListView {

    public ListViewNoVScroll(Context context) {
        super(context);
    }

    public ListViewNoVScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewNoVScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
