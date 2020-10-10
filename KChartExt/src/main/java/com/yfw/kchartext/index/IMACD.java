package com.yfw.kchartext.index;

/**
 * @日期 : 2020/7/9
 * @描述 : MACD 指标接口
 */
public interface IMACD {

    /**
     * MACD 离差值EMA12-EMA26
     */
    float getDIF();

    /**
     * MACD 9日移动平均值DEA9
     */
    float getDEA();

    /**
     * MACD 平滑移动均线
     */
    float getMACD();
}