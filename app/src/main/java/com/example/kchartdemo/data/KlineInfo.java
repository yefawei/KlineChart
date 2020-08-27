package com.example.kchartdemo.data;

import com.benben.kchartlib.index.IEntity;
import com.benben.kchartlib.index.IVolume;

/**
 * @日期 : 2020/8/5
 * @描述 :
 */
public class KlineInfo extends IEntity implements IVolume {

    /**
     * timestamp : 1511712000
     * open_price : 5.385
     * max_price : 7.385
     * min_price : 2.308
     * close_price : 7.352
     * pre_close_price : 0.000
     * volume : 403212.2060
     * amount : 1804191.4374
     * usdt_amount : 0.0000
     */

    public long timestamp;
    public float open_price;       // 开盘价
    public float max_price;        // 最高价
    public float min_price;        // 最低价
    public float close_price;      // 收盘价
    public float volume;           // 数量
    public float amount;           // 金额

    @Override
    public float getOpenPrice() {
        return open_price;
    }

    @Override
    public float getHighPrice() {
        return max_price;
    }

    @Override
    public float getLowPrice() {
        return min_price;
    }

    @Override
    public float getClosePrice() {
        return close_price;
    }

    @Override
    public long getDatatime() {
        return timestamp * 1000;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public float getAmount() {
        return amount;
    }
}
