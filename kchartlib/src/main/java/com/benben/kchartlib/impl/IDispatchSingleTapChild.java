package com.benben.kchartlib.impl;

/**
 * @日期 : 2020/8/28
 * @描述 :
 */
public interface IDispatchSingleTapChild {

    /**
     * 是否可单次点击
     */
    boolean canSingleTap();

    /**
     * 是否在分发的范围
     */
    boolean inDispatchRange(int x, int y);

    /**
     * 单次点击事件
     *
     * @return 如果自身处理则返回true
     */
    boolean onSingleTap(int x, int y);
}
