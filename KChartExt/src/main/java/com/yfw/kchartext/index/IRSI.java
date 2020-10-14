package com.yfw.kchartext.index;

import com.yfw.kchartcore.index.Index;

/**
 * @日期 : 2020/7/9
 * @描述 : RSI 指标接口
 */
public interface IRSI extends Index {

    /**
     * 7日RSI
     */
    float getRSI7();

    /**
     * 14日RSI
     */
    float getRSI14();

    /**
     * 28日RSI
     */
    float getRSI28();
}
