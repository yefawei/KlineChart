package com.yfw.kchartext.index;

import com.yfw.kchartcore.index.Index;

/**
 * @日期 : 2020/7/9
 * @描述 : 成交量指标接口
 */
public interface IVolume extends Index {

    /**
     * 成交量
     */
    float getVolume();

    /**
     * 成交额
     */
    float getAmount();
}
