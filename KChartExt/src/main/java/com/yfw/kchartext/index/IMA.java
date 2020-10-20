package com.yfw.kchartext.index;

import com.yfw.kchartcore.index.Index;

/**
 * @日期 : 2020/7/9
 * @描述 : MA 指标接口
 */
public interface IMA extends Index {

    /**
     * 10日MA
     */
    float getMA10();

    void setMA10(float ma10);

    /**
     * 30日MA
     */
    float getMA30();

    void setMA30(float ma30);

    /**
     * 100日MA
     */
    float getMA100();

    void setMA100(float ma100);
}
