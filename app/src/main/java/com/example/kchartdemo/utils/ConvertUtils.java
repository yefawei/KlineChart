package com.example.kchartdemo.utils;

import android.content.Context;

/**
 * @日期 : 2020/8/20
 * @描述 :
 */
public class ConvertUtils {

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
