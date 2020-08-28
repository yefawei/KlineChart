package com.benben.kchartlib.impl;

/**
 * @日期 : 2020/8/28
 * @描述 :
 */
public interface IDispatchSingleTapParent {

    /**
     * 是否在分发的范围
     */
    boolean inDispatchRange(int x, int y);

    boolean dispatchSingleTap(int x, int y);
}
