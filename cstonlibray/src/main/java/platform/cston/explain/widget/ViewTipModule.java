package platform.cston.explain.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cston.cstonlibray.R;


public class ViewTipModule {

    private Context mContext;

    private ViewGroup mMainLayout;

    private View mDataLayot;

    private View mTipLayot;

    private LoadingView mLoadingView;

    private ImageView tipImage;

    private Callback mCallBack;

    private EmptyViewClickCallback mEmptyViewClickCallback;

    //加载视图
    private LinearLayout mLoadingLayout;

    //加载失败
    private LinearLayout mLoadFailLayout;

    //提示文本
    private TextView mTipTextView;

    //加载失败按钮
    private Button mLoadFailBtn;

    private boolean mIsDefaultLoading = true;

    private boolean mIsEnableTouchClick = false;

    public ViewTipModule(Context context, ViewGroup mianLayout, View dataLayot, Callback callBack) {
        this.mContext = context;
        this.mMainLayout = mianLayout;
        this.mDataLayot = dataLayot;
        this.mCallBack = callBack;
        init();
    }

    public ViewTipModule(Context context, ViewGroup mianLayout, View dataLayot,
                         boolean isDefaultLoading, Callback callBack) {
        this.mContext = context;
        this.mMainLayout = mianLayout;
        this.mDataLayot = dataLayot;
        this.mCallBack = callBack;
        this.mIsDefaultLoading = isDefaultLoading;
        init();
    }

    public ViewTipModule(Context context, ViewGroup mianLayout, View dataLayot,
                         boolean isDefaultLoading, boolean isEnableTouchClick, EmptyViewClickCallback callBack) {
        this.mContext = context;
        this.mMainLayout = mianLayout;
        this.mDataLayot = dataLayot;
        this.mEmptyViewClickCallback = callBack;
        this.mIsDefaultLoading = isDefaultLoading;
        this.mIsEnableTouchClick = isEnableTouchClick;
        init();
    }

    @Deprecated
    public ViewTipModule(Context context, ViewGroup mianLayout, Callback callBack) {
        this(context, mianLayout, null, callBack);
    }

    void init() {
        // 获取提示视图
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mTipLayot = inflater.inflate(R.layout.cst_platform_common_load_page_fail, null);
        // 空白视图触摸事件
        LinearLayout mTipLayout = (LinearLayout) mTipLayot.findViewById(R.id.load_page_fail_layout);
        if (mIsEnableTouchClick) {
            mTipLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mEmptyViewClickCallback != null) {
                        mEmptyViewClickCallback.setEmptyViewClick();
                    }
                    return false;
                }
            });
        } else {
            mTipLayout.setOnTouchListener(null);
        }

        mLoadingView = (LoadingView) mTipLayot.findViewById(R.id.loading_v);
        tipImage = (ImageView) mTipLayot.findViewById(R.id.tip_img);

        mLoadingLayout = (LinearLayout) mTipLayot.findViewById(R.id.loading_layout);
        mLoadFailLayout = (LinearLayout) mTipLayot.findViewById(R.id.load_fail_layout);
        mTipTextView = (TextView) mTipLayot.findViewById(R.id.tip_text);
        mLoadFailBtn = (Button) mTipLayot.findViewById(R.id.load_fail_btn);

        // 添加视图到主布局文件
        FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        fllp.gravity = Gravity.CENTER;
        this.mMainLayout.addView(mTipLayot, fllp);
        if (mIsDefaultLoading) {
            // 默认显示加载状态
            showLodingState();
        }

    }

    /**
     * 数据加载成功视图状态
     */
    public void showLodingState() {
        // 切换到提示状态
        changeToTip();
        //开始显示加载视图
        showLoadingView();
    }

    private void showLoadingView() {
        mLoadingView.startLoading();
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadFailLayout.setVisibility(View.GONE);
        tipImage.setVisibility(View.GONE);
        mTipTextView.setText("加载中...");
    }

    /**
     * 数据加载成功视图状态
     */
    public void showSuccessState() {
        // 切换到数据状态
        changeToData();
    }

    /**
     * 数据加载失败视图状态
     */
    public void showFailState() {
        // 切换到提示状态
        changeToTip();
        tipImage.setImageResource(R.drawable.cst_platform_home_list_no_data_ico);
        tipImage.setVisibility(View.VISIBLE);
        mLoadingLayout.setVisibility(View.GONE);
        mLoadFailLayout.setVisibility(View.VISIBLE);
        mLoadFailBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //开始显示加载视图
                showLoadingView();
                if (mCallBack != null) {
                    // 加载数据
                    mCallBack.getData();
                }
            }
        });
    }

    /**
     * 暂无数据视图状态
     */
    public void showNoDataState() {
        // 切换到提示状态
        changeToTip();
        mLoadingView.setVisibility(View.GONE);
        mLoadFailLayout.setVisibility(View.GONE);

        mLoadingLayout.setVisibility(View.VISIBLE);
        tipImage.setVisibility(View.VISIBLE);
        tipImage.setImageResource(R.drawable.cst_platform_home_list_no_data_ico);

        mTipTextView.setText("暂无数据");
    }

    /**
     * 暂无数据视图状态
     *
     * @param text 提示文本
     */
    public void showNoDataState(String text) {
        showNoDataState();
        mTipTextView.setText(text);
    }

    /**
     * 暂无数据视图状态
     */
    public void showNoDataStateWithImgAndText(int drawableRes, String text) {
        // 切换到暂无数据状态
        showNoDataState();
        mTipTextView.setText(text);
        tipImage.setImageResource(drawableRes);
    }

    /**
     * 只显示无数据文本
     */
    public void showNoDataText(String text) {
        // 切换到暂无数据状态
        showNoDataState();
        mTipTextView.setText(text);
        tipImage.setVisibility(View.GONE);
    }

    /**
     * 只默认图片的页面
     */
    public void showDefaultImage() {
        // 切换到暂无数据状态
        showNoDataState();
        mTipTextView.setText("");
        tipImage.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏所有视图
     */
    public void hideAllLayout() {
        // 隐藏数据视图
        if (mDataLayot != null) {
            mDataLayot.setVisibility(View.INVISIBLE);
        }
        // 隐藏提示视图
        mTipLayot.setVisibility(View.GONE);
        mLoadingView.stopLoading();
    }

    /**
     * 切换到数据状态
     */
    private void changeToData() {
        // 显示数据视图
        if (mDataLayot != null) {
            mDataLayot.setVisibility(View.VISIBLE);
        }
        // 隐藏提示视图
        mTipLayot.setVisibility(View.GONE);
    }

    /**
     * 切换到提示状态
     */
    private void changeToTip() {
        // 隐藏数据视图
        if (mDataLayot != null) {
            mDataLayot.setVisibility(View.INVISIBLE);
        }
        // 显示提示视图
        mTipLayot.setVisibility(View.VISIBLE);
    }

    public interface Callback {

        public void getData();

    }

    public interface EmptyViewClickCallback {

        public void setEmptyViewClick();

    }

}
