package platform.cston.explain.widget.pullrefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Copyright 2012-2014  CST.All Rights Reserved
 *
 * Comments：功能描述
 *
 * @author Caochong
 * @Time: 2015/7/23
 *
 * Modified By: ***
 * Modified Date: ***
 * Why & What is modified:
 */
public class RotateView extends ImageView {

    public RotateView(Context context) {
        super(context);
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        canvas.rotate(mAngle, getWidth() / 2, getHeight() / 2);
        drawable.draw(canvas);

    }

    private int mAngle;

    public void setRotateAngle(int angle) {
        mAngle = angle;
        invalidate();
    }

    public void resetView() {
        mAngle = 0;
        invalidate();


    }


}
