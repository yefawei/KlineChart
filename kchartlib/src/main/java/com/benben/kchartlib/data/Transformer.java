package com.benben.kchartlib.data;

import com.benben.kchartlib.adapter.IAdapter;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.index.IEntity;
import com.benben.kchartlib.index.Index;
import com.benben.kchartlib.index.IndexSet;

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

    private HashMap<String, Index> mIndexMap = new HashMap<>();

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
        for (Index value : mIndexMap.values()) {
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
        for (int i = mStartIndex; i <= mStopIndex; i++) {
            IEntity cur = adapter.getItem(i);
            for (Index value : mIndexMap.values()) {
                value.calcMinMaxValue(i, cur);
            }
        }
        for (Index value : mIndexMap.values()) {
            value.calcPaddingValue();
        }
    }

    public void addIndexData(Index index) {
        if (index == null) return;
        if (index instanceof IndexSet) {
            ((IndexSet) index).setCanChangeIndex(false);
        }
        mIndexMap.put(index.getIndexTag(), index);
    }

    public void removeIndexData(String indexTag) {
        Index remove = mIndexMap.remove(indexTag);
        if (remove instanceof IndexSet) {
            ((IndexSet) remove).setCanChangeIndex(true);
        }
    }
}
