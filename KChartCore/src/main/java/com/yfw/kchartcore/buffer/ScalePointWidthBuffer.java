package com.yfw.kchartcore.buffer;

/**
 * @日期 : 2020/7/9
 * @描述 : 获取经过缩放后的单点宽度
 */
public class ScalePointWidthBuffer {
    public float mScaleX; // 缓存标记
    public float mScalePointWidth; // 如果缓存标记一致则直接返回该值
}
