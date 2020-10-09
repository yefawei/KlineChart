package com.yfw.kchartext.index;

/**
 * @日期 : 2020/7/9
 * @描述 : 成交量指标接口
 */
public interface IVolume {

    /**
     * 成交量
     */
    float getVolume();

    /**
     * 成交额
     */
    float getAmount();
}
