package com.yfw.kchartcore.index.range;


import com.yfw.kchartcore.data.Transformer;

/**
 * @日期 2020/9/28
 * @描述 指标计算类容器
 * 该接口的子类不会被{@link Transformer#addIndexRange(IndexRange)}函数所添加并计算
 * 会通过{@link #getRealIndexRange()}获取实际的指标计算类
 */
public interface IndexRangeContainer {

    /**
     * 获取真正的指标计算类
     */
    IndexRange getRealIndexRange();
}
