package com.benben.kchartlib.utils;

import android.graphics.Paint;

/**
 * @日期 : 2020/7/9
 * @描述 : 字体计算工具【注：非线程安全的】
 */
public class FontCalculateUtils {


    private static Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();

    /**
     * 获取baseLine与centerY的偏移距离
     */
    public static float getBaseLineLimitInCenter(Paint paint) {
        paint.getFontMetrics(mFontMetrics);
        return (mFontMetrics.bottom - mFontMetrics.top) / 2 - mFontMetrics.bottom;
    }

    /**
     * 给定文本Y轴中间线，获取此时baseline所在位置
     */
    public static float getBaselineFromCenter(Paint paint, float centerY) {
        paint.getFontMetrics(mFontMetrics);
        return centerY + (mFontMetrics.bottom - mFontMetrics.top) / 2 - mFontMetrics.bottom;
    }

    /**
     * 计算baseline到字体左上角的偏移距离,含字体padding
     */
    public static float getLeftTopPointYOffset(Paint paint) {
        paint.getFontMetrics(mFontMetrics);
        return Math.abs(mFontMetrics.top);
    }

    /**
     * 获取字体高度
     */
    public static float getTextHeight(Paint paint) {
        paint.getFontMetrics(mFontMetrics);
        return mFontMetrics.bottom - mFontMetrics.top;
    }
}
