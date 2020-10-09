package com.yfw.kchartext.index;

/**
 * @日期 : 2020/7/9
 * @描述 : MA 指标接口
 */
public interface IMA {

    /**
     * 7日MA
     */
    float getMA7();

    /**
     * 30日MA
     */
    float getMA30();
}
