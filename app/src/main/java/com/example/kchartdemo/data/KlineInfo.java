package com.example.kchartdemo.data;


import android.text.TextUtils;

import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartext.index.IKDJ;
import com.yfw.kchartext.index.IMA;
import com.yfw.kchartext.index.IMACD;
import com.yfw.kchartext.index.IRSI;
import com.yfw.kchartext.index.IVolume;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @日期 : 2020/8/5
 * @描述 :
 */
public class KlineInfo implements IEntity, IVolume, IMA, IMACD, IKDJ, IRSI {

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

    public float ma7;       // MA7
    public float ma30;      // MA30
    public float k;         // KDJ k值
    public float d;         // KDJ d值
    public float j;         // KDJ j值
    public float dif;       // MACD 离差值EMA12-EMA26
    public float dea;       // MACD 9日移动平均值DEA9
    public float macd;      // MACD 平滑移动均线
    public float rsi7;      // 7日RSI
    public float rsi14;     // 14日RSI
    public float rsi28;     // 28日RSI

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
        return timestamp;
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

    @Override
    public float getMA7() {
        return ma7;
    }

    @Override
    public void setMA7(float ma7) {
        this.ma7 = ma7;
    }

    @Override
    public float getMA30() {
        return ma30;
    }

    @Override
    public void setMA30(float ma30) {
        this.ma30 = ma30;
    }

    @Override
    public float getK() {
        return k;
    }

    @Override
    public void setK(float k) {
        this.k = k;
    }

    @Override
    public float getD() {
        return d;
    }

    @Override
    public void setD(float d) {
        this.d = d;
    }

    @Override
    public float getJ() {
        return j;
    }

    @Override
    public void setJ(float j) {
        this.j = j;
    }

    @Override
    public float getDIF() {
        return dif;
    }

    @Override
    public void setDIF(float dif) {
        this.dif = dif;
    }

    @Override
    public float getDEA() {
        return dea;
    }

    @Override
    public void setDEA(float dea) {
        this.dea = dea;
    }

    @Override
    public float getMACD() {
        return macd;
    }

    @Override
    public void setMACD(float macd) {
        this.macd = macd;
    }

    @Override
    public float getRSI7() {
        return rsi7;
    }

    @Override
    public void setRSI7(float rsi7) {
        this.rsi7 = rsi7;
    }

    @Override
    public float getRSI14() {
        return rsi14;
    }

    @Override
    public void setRSI14(float rsi14) {
        this.rsi14 = rsi14;
    }

    @Override
    public float getRSI28() {
        return rsi28;
    }

    @Override
    public void setRSI28(float rsi28) {
        this.rsi28 = rsi28;
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
