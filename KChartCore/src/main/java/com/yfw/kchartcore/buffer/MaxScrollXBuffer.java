package com.yfw.kchartcore.buffer;

/**
 * @日期 : 2020/7/9
 * @描述 : 最大滚动值缓存
 */
public class MaxScrollXBuffer {
    public float mScaleX; // 缓存标记
    public int mMaxScrollX; // 如果缓存标记一致则直接返回该值
}
