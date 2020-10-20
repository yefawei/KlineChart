package com.yfw.kchartcore.index;

/**
 * @日期 : 2020/7/9
 * @描述 : 蜡烛图接口
 */
public interface ICandle extends Index {

    /**
     * 开盘价
     */
    float getOpenPrice();

    void setOpenPrice(float openPrice);

    /**
     * 最高价
     */
    float getHightPrice();

    void setHightPrice(float hightPrice);

    /**
     * 最低价
     */
    float getLowPrice();

    void setLowPrice(float lowPrice);

    /**
     * 收盘价
     */
    float getClosePrice();

    void setClosePrice(float closePrice);

    /**
     * 时间戳
     */
    long getTimeStamp();

    void setTimeStamp(long timeStamp);
}
