package com.example.kchartdemo.data;


import android.text.TextUtils;

import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartext.index.IVolume;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @日期 : 2020/8/5
 * @描述 :
 */
public class KlineInfo implements IEntity, IVolume {

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

    public String formatTime;

    @Override
    public float getOpenPrice() {
        return open_price;
    }

    @Override
    public void setOpenPrice(float openPrice) {
        open_price = openPrice;
    }

    @Override
    public float getHightPrice() {
        return max_price;
    }

    @Override
    public void setHightPrice(float hightPrice) {
        max_price = hightPrice;
    }

    @Override
    public float getLowPrice() {
        return min_price;
    }

    @Override
    public void setLowPrice(float lowPrice) {
        min_price = lowPrice;
    }

    @Override
    public float getClosePrice() {
        return close_price;
    }

    @Override
    public void setClosePrice(float closePrice) {
        close_price = closePrice;
    }

    @Override
    public long getTimeStamp() {
        return timestamp * 1000;
    }

    @Override
    public void setTimeStamp(long timeStamp) {
        this.timestamp = timeStamp;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public float getAmount() {
        return amount;
    }

    @Override
    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getFormatTime() {
        if (TextUtils.isEmpty(formatTime)) {
            formatTime = FormatTime.formatTime(getTimeStamp());
        }
        return formatTime;
    }

    private static class FormatTime {
        private static Date mDate = new Date();
        private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

        public static String formatTime(long time) {
            mDate.setTime(time);
            return mSimpleDateFormat.format(mDate);
        }
    }
}
