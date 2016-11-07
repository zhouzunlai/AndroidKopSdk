/*
 * Copyright (c) 2016.  CST.All Rights Reserved
 *
 * @author:zhouzunlai
 *
 * @date: 2016.5.7.
 *
 */

package platform.cston.explain.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import cston.cstonlibray.R;
import platform.cston.explain.bean.CstTopTitleInfo;
import platform.cston.explain.utils.CstPlatformUtils;
import platform.cston.explain.widget.CstLoadDialog;

public class CstBaseActivity extends Activity {

    protected Activity mActivity;

    protected Resources mResources;

    protected CstLoadDialog mBlockDialog;

    protected CstTopTitleInfo mPageInfo;

    private boolean mDestroyed;

    public String PAGE_INFO = "pageInfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mResources = getResources();
        mPageInfo = new CstTopTitleInfo();
    }

    public void setPageInfoColor(CstTopTitleInfo.ColorStatus status) {
        mPageInfo.setStatus(status);
    }

    public void setPageInfoTitle(String title) {
        mPageInfo.setTitle(title);
    }

    public void setPageInfoStatic() {
        mPageInfo.setStaticTitle(1);
    }

    public CstTopTitleInfo getPageInfo() {
        return mPageInfo;
    }

    /**
     * 设置头部颜色
     */
    protected void setHeadreColor(CstTopTitleInfo.ColorStatus status) {
        ViewGroup titleView = (ViewGroup) findViewById(R.id.cst_platform_top_title_view);
        if (titleView != null) {
            if (status == CstTopTitleInfo.ColorStatus.ERROR) {
                titleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), status.getColor(), null));
            } else if (status == CstTopTitleInfo.ColorStatus.WARN) {
                titleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), status.getColor(), null));
            } else if (status == CstTopTitleInfo.ColorStatus.NORMAL) {
                titleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), status.getColor(), null));
            }else if(status ==  CstTopTitleInfo.ColorStatus.BREAKRULES)
            {
                titleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), status.getColor(), null));
            } else if(status == CstTopTitleInfo.ColorStatus.MONTHEVENT)
            {
                titleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), status.getColor(), null));
            }
            else {
                titleView.setBackgroundColor(ResourcesCompat
                        .getColor(getResources(), CstTopTitleInfo.ColorStatus.NONE.getColor(), null));
            }
        }

        ViewGroup webTitleView = (ViewGroup) findViewById(R.id.cst_platform_webview_title_view);
        if (webTitleView != null) {
            if (status == CstTopTitleInfo.ColorStatus.ERROR) {
                webTitleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), status.getColor(), null));
            } else if (status == CstTopTitleInfo.ColorStatus.WARN) {
                webTitleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), status.getColor(), null));
            } else if (status == CstTopTitleInfo.ColorStatus.NORMAL) {
                webTitleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), status.getColor(), null));
            } else {
                webTitleView.setBackgroundColor(
                        ResourcesCompat.getColor(getResources(), CstTopTitleInfo.ColorStatus.NONE.getColor(), null));
            }
        }

        TextView headerTv = (TextView) findViewById(R.id.cst_platform_header_title);
        if (headerTv != null) {
            if (status == CstTopTitleInfo.ColorStatus.ERROR) {
                headerTv.setTextColor(Color.WHITE);
            } else if (status == CstTopTitleInfo.ColorStatus.WARN) {
                headerTv.setTextColor(Color.WHITE);
            } else if (status == CstTopTitleInfo.ColorStatus.NORMAL) {
                headerTv.setTextColor(Color.WHITE);
            } else if(status ==  CstTopTitleInfo.ColorStatus.BREAKRULES)
            {
                headerTv.setTextColor(Color.WHITE);
            }else if(status == CstTopTitleInfo.ColorStatus.MONTHEVENT)
            {
                headerTv.setTextColor(Color.WHITE);
            }
            else {
                headerTv.setTextColor(ResourcesCompat.getColor(getResources(), R.color.cst_platform_normal_text, null));
            }
        }

        TextView leftTv = (TextView) findViewById(R.id.cst_platform_header_left_text);
        if (leftTv != null) {
            if (status == CstTopTitleInfo.ColorStatus.ERROR) {
                leftTv.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.cst_platform_select_color_header_text_white, null));
                Drawable whiteDrawable = ResourcesCompat
                        .getDrawable(getResources(), R.drawable.cst_platform_select_top_back_white_btn, null);
                whiteDrawable.setBounds(0, 0, whiteDrawable.getMinimumWidth(),
                        whiteDrawable.getMinimumHeight());
                leftTv.setCompoundDrawables(whiteDrawable, null, null, null);
            } else if (status == CstTopTitleInfo.ColorStatus.WARN) {
                leftTv.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.cst_platform_select_color_header_text_white, null));
                Drawable whiteDrawable = ResourcesCompat
                        .getDrawable(getResources(), R.drawable.cst_platform_select_top_back_white_btn, null);
                whiteDrawable.setBounds(0, 0, whiteDrawable.getMinimumWidth(),
                        whiteDrawable.getMinimumHeight());
                leftTv.setCompoundDrawables(whiteDrawable, null, null, null);
            } else if (status == CstTopTitleInfo.ColorStatus.NORMAL) {
                leftTv.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.cst_platform_select_color_header_text_white, null));
                Drawable whiteDrawable = ResourcesCompat
                        .getDrawable(getResources(), R.drawable.cst_platform_select_top_back_white_btn, null);
                whiteDrawable.setBounds(0, 0, whiteDrawable.getMinimumWidth(),
                        whiteDrawable.getMinimumHeight());
                leftTv.setCompoundDrawables(whiteDrawable, null, null, null);
            } else if(status == CstTopTitleInfo.ColorStatus.BREAKRULES)
            {
                leftTv.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.cst_platform_select_color_header_text_white, null));
                Drawable whiteDrawable = ResourcesCompat
                        .getDrawable(getResources(), R.drawable.cst_platform_select_top_back_white_btn, null);
                whiteDrawable.setBounds(0, 0, whiteDrawable.getMinimumWidth(),
                        whiteDrawable.getMinimumHeight());
                leftTv.setCompoundDrawables(whiteDrawable, null, null, null);
            }else if(status == CstTopTitleInfo.ColorStatus.MONTHEVENT)
            {
                leftTv.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.cst_platform_select_color_header_text_white, null));
                Drawable whiteDrawable = ResourcesCompat
                        .getDrawable(getResources(), R.drawable.cst_platform_select_top_back_white_btn, null);
                whiteDrawable.setBounds(0, 0, whiteDrawable.getMinimumWidth(),
                        whiteDrawable.getMinimumHeight());
                leftTv.setCompoundDrawables(whiteDrawable, null, null, null);
            }
            else {
                leftTv.setTextColor(ResourcesCompat
                        .getColor(getResources(), R.color.cst_platform_normal_text, null));
                Drawable drawable = ResourcesCompat
                        .getDrawable(getResources(), R.drawable.cst_platform_select_top_back_btn, null);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                leftTv.setCompoundDrawables(drawable, null, null, null);
            }
        }

        TextView rightTv = (TextView) findViewById(R.id.cst_platform_header_right_text);
        if (rightTv != null) {
            if (status == CstTopTitleInfo.ColorStatus.ERROR) {
                rightTv.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.cst_platform_select_color_header_text_white, null));
            } else if (status == CstTopTitleInfo.ColorStatus.WARN) {
                rightTv.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.cst_platform_select_color_header_text_white, null));
            } else if (status == CstTopTitleInfo.ColorStatus.NORMAL) {
                rightTv.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.cst_platform_select_color_header_text_white, null));
            } else {
                rightTv.setTextColor(ResourcesCompat.getColor(getResources(), R.color.cst_platform_normal_text, null));
            }
        }
    }

    /**
     * 设置头部title
     *
     * @param title 标题
     */
    protected void setHeaderTitle(CharSequence title) {
        TextView headerTv = (TextView) findViewById(R.id.cst_platform_header_title);
        headerTv.setText(title);
        setPageInfoTitle(title == null ? null : title.toString());
    }

    protected void setTitleVisibility(int visibility) {
        TextView headerTv = (TextView) findViewById(R.id.cst_platform_header_title);
        headerTv.setVisibility(visibility);
    }

    protected void setHeaderTitleDrawable(CharSequence title, Drawable drawable) {
        TextView headerTv = (TextView) findViewById(R.id.cst_platform_header_title);
        headerTv.setText(title);
        headerTv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        headerTv.setCompoundDrawablePadding(10);
        setPageInfoTitle(title == null ? null : title.toString());
    }

    /**
     * 设置头部title
     *
     * @param titleResId 标题资源id
     */
    protected void setHeaderTitle(int titleResId) {
        setHeaderTitle(getString(titleResId));
    }

    //头部左边文字
    protected TextView leftTv;

    /**
     * 设置头部左边文字按钮
     */
    protected void setHeaderLeftTextBtn() {
        leftTv = (TextView) findViewById(R.id.cst_platform_header_left_text);
        if (leftTv == null) {
            return;
        }
        CstPlatformUtils.visible(leftTv);
        leftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackBtnClick();
            }
        });
        CstTopTitleInfo lastPageInfo = getIntent().getParcelableExtra(PAGE_INFO);
        if (lastPageInfo == null) {
            leftTv.setText(R.string.platform_back);
        } else {
            if (lastPageInfo.getStaticTitle() == 0) {
                leftTv.setText(R.string.platform_back);
            } else {
                if (lastPageInfo.getTitle() == null) {
                    leftTv.setText(R.string.platform_back);
                } else {
                    String title = lastPageInfo.getTitle();
                    if (title.length() > 5) {
                        title = title.substring(0, 5) + "...";
                    }
                    leftTv.setText(title);
                }
            }
        }
    }


    protected void hiddenHeaderRightImageBtn() {
        findViewById(R.id.cst_header_right_btn).setVisibility(View.GONE);
    }

    /**
     * 设置页面右边图片
     */
    protected void setHeaderRightImageBtn(int imageSrcId) {
        ImageButton img = (ImageButton) findViewById(R.id.cst_header_right_btn);
        CstPlatformUtils.visible(img);
        img.setImageResource(imageSrcId);
    }

    protected void hiddenHeaderRightTextBtn() {
        findViewById(R.id.cst_platform_header_right_text).setVisibility(View.GONE);
    }

    /**
     * 设置头部右边文字按钮
     */
    protected void setHeaderRightTextBtn(CharSequence rightText) {
        TextView rightTv = (TextView) findViewById(R.id.cst_platform_header_right_text);
        rightTv.setText(rightText);
        CstPlatformUtils.visible(rightTv);
    }

    protected void onInitError(boolean needFinish) {
        CstPlatformUtils.show(this, "发生错误，请重试");
        if (needFinish) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBaseActivtiyResumeEvent(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        CstTopTitleInfo lastPageInfo = getIntent().getParcelableExtra(PAGE_INFO);
        if (lastPageInfo != null) {
            setHeadreColor(lastPageInfo.getStatus());
        } else {
            setHeadreColor(CstTopTitleInfo.ColorStatus.NONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mDestroyed = true;
        super.onDestroy();
        if (mBlockDialog != null && mBlockDialog.isShowing()) {
            mBlockDialog.dismiss();
        }
    }

    public boolean isDestroyedCompat() {
        return mDestroyed;
    }

    /**
     * Activity基类onResume事件处理方法
     */
    public void onBaseActivtiyResumeEvent(Activity context) {

    }

    /**
     * 返回按钮点击
     */
    protected void onBackBtnClick() {
        finish();
        CstPlatformUtils.hideSoftInput(mActivity);
    }

}
