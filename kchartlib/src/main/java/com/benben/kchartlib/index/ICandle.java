package com.benben.kchartlib.index;

/**
 * @日期 : 2020/7/9
 * @描述 : 蜡烛图接口
 */
public interface ICandle {

    /**
     * 开盘价
     */
    float getOpenPrice();

    /**
     * 最高价
     */
    float getHighPrice();

    /**
     * 最低价
     */
    float getLowPrice();

    /**
     * 收盘价
     */
    float getClosePrice();

    /**
     * 时间戳
     */
    long getDatatime();
}
