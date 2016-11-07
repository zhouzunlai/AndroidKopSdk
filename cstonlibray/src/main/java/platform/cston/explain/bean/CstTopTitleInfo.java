/*
 * Copyright (c) 2016.  CST.All Rights Reserved
 *
 * @author:zhouzunlai
 *
 * @date: 2016.5.7.
 *
 */

package platform.cston.explain.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cston.cstonlibray.R;


public class CstTopTitleInfo implements Parcelable {

    private ColorStatus status = ColorStatus.NONE; // 标题栏颜色

    private String title = null; // 返回标题

    private int staticTitle = 0; // 是否为静态标题(1，是；0，否)，如果不为静态标题，则用"返回"作为返回标题

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStaticTitle() {
        return staticTitle;
    }

    public void setStaticTitle(int staticTitle) {
        this.staticTitle = staticTitle;
    }

    public ColorStatus getStatus() {
        return status;
    }

    public void setStatus(ColorStatus status) {
        this.status = status;
    }

    public enum ColorStatus {

        /**
         * 默认
         */
        NONE(R.color.cst_platform_top_title_bg),

        /**
         * 红色
         */
        ERROR(R.color.cst_platform_detect_color_error),

        /**
         * 黄色
         */
        WARN(R.color.cst_platform_detect_color_warn),

        /**
         * 绿色
         */
        NORMAL(R.color.cst_platform_detect_color_normal),

        MONTHEVENT(R.color.cst_platform_top_title_bg),

        BREAKRULES(R.color.cst_platform_break_rules_bg);

        private int color;

       ColorStatus(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeString(this.title);
        dest.writeInt(this.staticTitle);
    }

    public CstTopTitleInfo() {
    }

    protected CstTopTitleInfo(Parcel in) {
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : ColorStatus.values()[tmpStatus];
        this.title = in.readString();
        this.staticTitle = in.readInt();
    }

    public static final Creator<CstTopTitleInfo> CREATOR = new Creator<CstTopTitleInfo>() {
        @Override
        public CstTopTitleInfo createFromParcel(Parcel source) {
            return new CstTopTitleInfo(source);
        }

        @Override
        public CstTopTitleInfo[] newArray(int size) {
            return new CstTopTitleInfo[size];
        }
    };
}
