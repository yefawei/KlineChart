package com.yfw.kchartext.adapter;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * @日期 : 2020/8/21
 * @描述 : 默认K线适配器
 */
public class DefaultKChartAdapter extends AbstractKChartAdapter {

    private OnPrepareIndexDataListener mListener;

    /**
     * @param listener 当前需要准备的指标数据
     */
    public DefaultKChartAdapter(@Nullable OnPrepareIndexDataListener listener) {
        mListener = listener;
    }

    @Override
    public void prepareIndexData(List<String> indexTags) {
        if (mListener != null) {
            mListener.prepareIndexData(indexTags);
        }
    }

    public interface OnPrepareIndexDataListener {
        void prepareIndexData(List<String> indexTags);
    }
}
