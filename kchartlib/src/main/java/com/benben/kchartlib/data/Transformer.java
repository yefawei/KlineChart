package com.benben.kchartlib.data;

import androidx.annotation.NonNull;

import com.benben.kchartlib.adapter.IAdapter;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.index.IEntity;
import com.benben.kchartlib.index.range.IndexRange;
import com.benben.kchartlib.index.range.IndexRangeSet;

import java.util.Collection;
import java.util.HashMap;

/**
 * @日期 : 2020/7/9
 * @描述 :
 */
public class Transformer {

    private IDataProvider mDataProvider;

    private float mStartPointX;             // 起始绘制点的中心X坐标(已包含主画板左侧距离)
    private int mStartIndex;                // 当前内容的开始坐标
    private int mStopIndex;                 // 当前内容的结束坐标

    private HashMap<String, Integer> mIndexCount = new HashMap<>();
    private HashMap<String, IndexRange> mIndexMap = new HashMap<>();

    public Transformer(IDataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    /**
     * 获取该索引在屏幕左侧时ScrollX值
     */
    public int getScrollXForLeftIndex(int index) {
        float scrollx = mDataProvider.getScalePointWidth() * (getItemCount() - index) - mDataProvider.getMainCanvasPort().getMainCanvasWidth();
        return Math.round(Math.max(scrollx, 0));
    }

    public float getStartPointX() {
        return mStartPointX;
    }

    public int getStartIndex() {
        return mStartIndex;
    }

    public int getStopIndex() {
        return mStopIndex;
    }

    /**
     * 更新相关的边界
     */
    public void updateBounds() {
        if (mDataProvider.isFullScreen()) {
            mStartIndex = indexOfTranslateX(getItemCount() - 1, xToTranslateX(0));
            mStopIndex = indexOfTranslateX(getItemCount() - 1, xToTranslateX(mDataProvider.getMainCanvasPort().getMainCanvasWidth()));
            mStartPointX = mDataProvider.getScroll() + mDataProvider.getMainCanvasPort().getMainCanvasRight() - mDataProvider.getScalePointWidth() / 2.0f
                    - (getItemCount() - mStartIndex - 1) * mDataProvider.getScalePointWidth();
        } else {
            mStartIndex = 0;
            mStopIndex = getItemCount() - 1;
            mStartPointX = mDataProvider.getScalePointWidth() / 2.0f + mDataProvider.getMainCanvasPort().getMainCanvasLeft();
        }
        for (IndexRange value : mIndexMap.values()) {
            value.resetValue();
        }
        if (getItemCount() > 0) {
            calcMinMax(mDataProvider.getAdapter());
        }
    }

    private int getItemCount() {
        if (mDataProvider.getAdapter() != null) {
            return mDataProvider.getAdapter().getCount();
        }
        return 0;
    }

    /**
     * 根据索引获取转换坐标X
     * 原点在0索引处
     */
    private float getTranslateXForIndex(int index) {
        return (index + 0.5f) * mDataProvider.getScalePointWidth();
    }

    /**
     * 可绘制区域x坐标转成转换坐标x
     */
    private float xToTranslateX(float x) {
        return mDataProvider.getScalePointWidth() * getItemCount() - mDataProvider.getScroll() + x - mDataProvider.getMainCanvasPort().getMainCanvasWidth();
    }

    private int indexOfTranslateX(int itemCount, float translateX) {
        if (itemCount == 0) {
            return 0;
        }
        return indexOfTranslateX(translateX, 0, itemCount);
    }

    private int indexOfTranslateX(float translateX, int start, int end) {
        if (end == start) {
            return start;
        }
        if (end - start == 1) {
            float startValue = getTranslateXForIndex(start);
            float endValue = getTranslateXForIndex(end);
            return Math.abs(translateX - startValue) < Math.abs(translateX - endValue) ? start : end;
        }
        int mid = start + (end - start) / 2;
        float midValue = getTranslateXForIndex(mid);
        if (translateX < midValue) {
            return indexOfTranslateX(translateX, start, mid);
        } else if (translateX > midValue) {
            return indexOfTranslateX(translateX, mid, end);
        } else {
            return mid;
        }
    }

    private void calcMinMax(IAdapter adapter) {
        Collection<IndexRange> values = mIndexMap.values();
        for (IndexRange value : values) {
            value.calcExtendedData();
        }
        for (int i = mStartIndex; i <= mStopIndex; i++) {
            IEntity cur = adapter.getItem(i);
            for (IndexRange value : values) {
                value.calcMinMaxValue(i, cur);
            }
        }
        for (IndexRange value : mIndexMap.values()) {
            value.calcPaddingValue();
        }
    }

    /**
     * 添加相关指标计算类，如果{@link IndexRange#getIndexTag()}一致，会在原有基础计数器+1，
     * 并要求已存对象与传入对象一致，防止重复计算
     */
    public void addIndexRange(IndexRange indexRange) {
        if (indexRange == null) return;
        if (indexRange instanceof IndexRangeSet) {
            ((IndexRangeSet) indexRange).setCanChangeIndex(false);
        }
        Integer count = mIndexCount.get(indexRange.getIndexTag());
        if (count == null) {
            mIndexCount.put(indexRange.getIndexTag(), 1);
            mIndexMap.put(indexRange.getIndexTag(), indexRange);
            return;
        }
        if (mIndexMap.get(indexRange.getIndexTag()) != indexRange) {
            throw new IllegalArgumentException("Inconsistent indexRange instances: " + indexRange.getIndexTag());
        }
        mIndexCount.put(indexRange.getIndexTag(), count + 1);

    }

    /**
     * 移除指标计算类，会先通过{@link IndexRange#getIndexTag()}判断计数器是否等于1，等于就移除，否则计数器-1
     */
    public void removeIndexRange(@NonNull IndexRange indexRange) {
        Integer count = mIndexCount.get(indexRange.getIndexTag());
        if (count == null) return;
        if (count > 1) {
            mIndexCount.put(indexRange.getIndexTag(), count - 1);
        }
        mIndexCount.remove(indexRange.getIndexTag());
        IndexRange remove = mIndexMap.remove(indexRange.getIndexTag());
        if (remove instanceof IndexRangeSet) {
            ((IndexRangeSet) remove).setCanChangeIndex(true);
        }
    }
}
