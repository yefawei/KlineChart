package com.example.kchartdemo;

import com.benben.kchartlib.adapter.BaseKChartAdapter;
import com.example.kchartdemo.data.KlineInfo;

import java.util.ArrayList;

/**
 * @日期 : 2020/8/5
 * @描述 :
 */
public class KChartAdapter extends BaseKChartAdapter<KlineInfo> {

    private ArrayList<KlineInfo> mKlineInfos;

    @Override
    public int getCount() {
        return mKlineInfos == null ? 0 : mKlineInfos.size();
    }

    @Override
    public KlineInfo getItem(int position) {
        return mKlineInfos.get(position);
    }

    public void setData(ArrayList<KlineInfo> klineInfos) {
        mKlineInfos = klineInfos;
        notifyDataSetChanged();
    }
}
