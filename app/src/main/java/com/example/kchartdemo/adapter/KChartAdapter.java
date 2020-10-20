package com.example.kchartdemo.adapter;

import com.example.kchartdemo.data.KlineInfo;
import com.yfw.kchartext.adapter.AbstractKChartAdapter;

import java.util.List;

/**
 * @日期 : 2020/10/13
 * @描述 :
 */
public class KChartAdapter extends AbstractKChartAdapter<KlineInfo> {

    @Override
    public void prepareIndexData(List<String> indexTags) {
        String tag;
        for (int i = 0; i < indexTags.size(); i++) {
            tag = indexTags.get(i);
            switch (tag) {
                case "MA":
                    calculateMA();
                    break;
                case "MACD":
                    calculateMACD();
                    break;
                case "KDJ":
                    calculateKDJ();
                    break;
                case "RSI":
                    calculateRSI();
                    break;
            }
        }
    }

    private void calculateMA() {
        float ma10 = 0;
        float ma30 = 0;
        float ma100 = 0;
        KlineInfo info;
        float closePrice;
        for (int i = 0; i < mKlineInfos.size(); i++) {
            info = mKlineInfos.get(i);

            closePrice = info.getClosePrice();

            ma10 += closePrice;
            ma30 += closePrice;
            ma100 += closePrice;
            if (i >= 10) {
                ma10 -= mKlineInfos.get(i - 10).getClosePrice();
                info.setMA10(ma10 / 10);
            } else {
                info.setMA10(ma10 / (i + 1));
            }
            if (i >= 30) {
                ma30 -= mKlineInfos.get(i - 30).getClosePrice();
                info.setMA30(ma30 / 30);
            } else {
                info.setMA30(ma30 / (i + 1));
            }
            if (i >= 100) {
                ma100 -= mKlineInfos.get(i - 100).getClosePrice();
                info.setMA100(ma100 / 100);
            } else {
                info.setMA100(ma100 / (i + 1));
            }
        }
    }

    private void calculateMACD() {
        float[] ema12s = new float[mKlineInfos.size()];
        float[] ema26s = new float[mKlineInfos.size()];
        float[] difs = new float[mKlineInfos.size()];
        float ema12Sum = 0;
        float ema26Sum = 0;
        float deaSum = 0;
        float dea = 0;
        KlineInfo info;
        float closePrice;
        for (int i = 0; i < mKlineInfos.size(); i++) {
            info = mKlineInfos.get(i);
            closePrice = info.getClosePrice();
            if (i < 12) {
                ema12Sum += closePrice;
            }
            if (i == 11) {
                ema12s[i] = ema12Sum / 12;
            } else if (i > 11) {
                // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                ema12s[i] = ema12s[i - 1] * 11.0f / 13.0f + closePrice * 2.0f / 13.0f;
            }

            if (i < 26) {
                ema26Sum += closePrice;
            }
            if (i == 25) {
                ema26s[i] = ema26Sum / 26;
            } else if (i > 25) {
                // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                ema26s[i] = ema26s[i - 1] * 25.0f / 27.0f + closePrice * 2.0f / 27.0f;
            }

            if (i > 24) {
                // DIF = EMA（12） - EMA（26）
                difs[i] = ema12s[i] - ema26s[i];
                info.setDIF(difs[i]);
            }

            if (i > 32) {
                deaSum = 0;
                for (int index = i - 8; index <= i; index++) {
                    deaSum += difs[index];
                }
                dea = deaSum / 9;
                info.setDEA(dea);
                info.setMACD(difs[i] - dea);
            }
        }
    }

    private void calculateKDJ() {
        int start = 8;
        KlineInfo info;
        KlineInfo lastInfo = null;
        float c;
        float nL;
        float nH;

        float rsv;
        float k;
        for (int i = start; i < mKlineInfos.size(); i++) {
            info = mKlineInfos.get(i);
            c = info.getClosePrice();
            nL = info.getLowPrice();
            nH = info.getHightPrice();

            float lowPrice;
            float highPrice;

            for (int index = i; index > (i - 9); index--) {
                lowPrice = mKlineInfos.get(index).getLowPrice();
                if (lowPrice < nL) {
                    nL = lowPrice;
                }

                highPrice = mKlineInfos.get(index).getHightPrice();
                if (highPrice > nH) {
                    nH = highPrice;
                }
            }

            if ((nH - nL) == 0) {
                rsv = 50;
            } else {
                rsv = (c - nL) / (nH - nL) * 100.0f;
            }

            if (i == start) {
                k = 100.0f / 3.0f + rsv / 3.0f;
                info.setK(k);
                info.setD(100.0f / 3.0f + k / 3.0f);
            } else {
                k = lastInfo.getK() * 2.0f / 3.0f + rsv / 3.0f;
                info.setK(k);
                info.setD(lastInfo.getD() * 2.0f / 3.0f + k / 3.0f);
            }
            info.setJ(3 * info.getK() - 2.0f * info.getD());
            lastInfo = info;
        }
    }

    private void calculateRSI() {
        float prevValue = 0;
        float subtract = 0;
        float prevGain7 = 0;
        float prevLoss7 = 0;
        float prevGain14 = 0;
        float prevLoss14 = 0;
        float prevGain28 = 0;
        float prevLoss28 = 0;
        float tempValue1 = 0;
        KlineInfo info;
        float closePrice;
        for (int i = 0; i < mKlineInfos.size(); i++) {
            info = mKlineInfos.get(i);
            closePrice = info.getClosePrice();
            if (i == 0) {
                prevValue = closePrice;
                continue;
            }
            subtract = closePrice - prevValue;
            prevValue = closePrice;

            if (i <= 7) {
                if (subtract < 0.0f) {
                    prevLoss7 -= subtract;
                } else {
                    prevGain7 += subtract;
                }
            } else {
                prevLoss7 *= 6;
                prevGain7 *= 6;
                if (subtract < 0.0f) {
                    prevLoss7 -= subtract;
                } else {
                    prevGain7 += subtract;
                }
            }
            if (i > 6) {
                prevLoss7 /= 7;
                prevGain7 /= 7;
                tempValue1 = prevGain7 + prevLoss7;
                if (tempValue1 == 0.0f) {
                    info.setRSI7(0);
                } else {
                    info.setRSI7(prevGain7 / tempValue1 * 100.0f);
                }

            }

            if (i <= 14) {
                if (subtract < 0.0f) {
                    prevLoss14 -= subtract;
                } else {
                    prevGain14 += subtract;
                }
            } else {
                prevLoss14 *= 13;
                prevGain14 *= 13;
                if (subtract < 0.0f) {
                    prevLoss14 -= subtract;
                } else {
                    prevGain14 += subtract;
                }
            }
            if (i > 13) {
                prevLoss14 /= 14;
                prevGain14 /= 14;
                tempValue1 = prevGain14 + prevLoss14;
                if (tempValue1 == 0.0f) {
                    info.setRSI14(0);
                } else {
                    info.setRSI14(prevGain14 / tempValue1 * 100.0f);
                }

            }

            if (i <= 28) {
                if (subtract < 0.0f) {
                    prevLoss28 -= subtract;
                } else {
                    prevGain28 += subtract;
                }
            } else {
                prevLoss28 *= 27;
                prevGain28 *= 27;
                if (subtract < 0.0f) {
                    prevLoss28 -= subtract;
                } else {
                    prevGain28 += subtract;
                }
            }
            if (i > 27) {
                prevLoss28 /= 28;
                prevGain28 /= 28;
                tempValue1 = prevGain28 + prevLoss28;
                if (tempValue1 == 0.0f) {
                    info.setRSI28(0);
                } else {
                    info.setRSI28(prevGain28 / tempValue1 * 100.0f);
                }
            }
        }
    }
}
