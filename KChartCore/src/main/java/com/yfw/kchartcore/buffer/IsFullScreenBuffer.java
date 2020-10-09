package com.yfw.kchartcore.buffer;

/**
 * @日期 : 2020/7/9
 * @描述 : 数据是否满屏缓存
 */
public class IsFullScreenBuffer {
    public float mScaleX; // 缓存标记
    public boolean mIsFullScreen; // 如果缓存标记一致则直接返回该值
}
