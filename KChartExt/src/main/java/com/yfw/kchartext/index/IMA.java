package com.yfw.kchartext.index;

import com.yfw.kchartcore.index.Index;

/**
 * @日期 : 2020/7/9
 * @描述 : MA 指标接口
 */
public interface IMA extends Index {

    /**
     * 7日MA
     */
    float getMA7();

    void setMA7(float ma7);

    /**
     * 30日MA
     */
    float getMA30();

    void setMA30(float ma30);
}
