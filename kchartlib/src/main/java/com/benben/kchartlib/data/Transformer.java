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

    private float mStartPointX;             // 起始绘制点的中心X坐标
    private int mStartIndex;                // 当前内容的开始坐标
    private int mStopIndex;                 // 当前内容的结束坐标

    private HashMap<String, Integer> mIndexCount = new HashMap<>();
    private HashMap<String, IndexRange> mIndexMap = new HashMap<>();

    public Transformer(IDataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    private int getItemCount() {
        if (mDataProvider.getAdapter() != null) {
            return mDataProvider.getAdapter().getCount();
        }
        return 0;
    }

    /**
     * 根据索引获取ScrollX
     */
    public int getScrollXForIndex(int index) {
        if (getItemCount() <= index) {
            return 0;
        }
        return Math.round(mDataProvider.getScalePointWidth() * (getItemCount() - index));
    }

    /**
     * 更新相关的边界
     */
    public void updateBounds(int mainCanvasWidth) {
        if (mDataProvider.isFullScreen()) {
            mStartIndex = indexOfScrollX(getItemCount(), mDataProvider.getScroll() + mainCanvasWidth);
            mStopIndex = indexOfScrollX(getItemCount(), mDataProvider.getScroll());
            mStartPointX = -(getScrollXForIndex(mStartIndex) - mDataProvider.getScroll() - mainCanvasWidth);
        } else {
            mStartIndex = 0;
            mStopIndex = getItemCount();
            mStartPointX = mDataProvider.getScalePointWidth() / 2.0f;
        }
        for (IndexRange value : mIndexMap.values()) {
            value.resetValue();
        }
        if (getItemCount() > 0) {
            calcMinMax(mDataProvider.getAdapter());
        }
    }

    private int indexOfScrollX(int itemCount, int scroll) {
        if (itemCount == 0) {
            return 0;
        }
        return indexOfScrollX(scroll, 0, itemCount);
    }

    private int indexOfScrollX(int scroll, int start, int end) {
        if (end == start) {
            return start;
        }
        if (end - start == 1) {
            int startValue = getScrollXForIndex(start);
            int endValue = getScrollXForIndex(end);
            return Math.abs(scroll - startValue) < Math.abs(scroll - endValue) ? start : end;
        }
        int mid = start + (end - start) / 2;
        int midValue = getScrollXForIndex(mid);
        if (scroll < midValue) {
            return indexOfScrollX(scroll, mid, end);
        } else if (scroll > midValue) {
            return indexOfScrollX(scroll, start, mid);
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
    public void addIndexData(IndexRange indexRange) {
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
    public void removeIndexData(@NonNull IndexRange indexRange) {
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
