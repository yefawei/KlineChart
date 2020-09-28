package com.benben.kchartlib.data;

import android.text.TextUtils;

import androidx.annotation.FloatRange;

import com.benben.kchartlib.adapter.BaseKChartAdapter;
import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IMainCanvasPort;
import com.benben.kchartlib.index.IEntity;
import com.benben.kchartlib.index.range.IndexRange;
import com.benben.kchartlib.index.range.IndexRangeContainer;
import com.benben.kchartlib.index.range.IndexRangeSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * @日期 : 2020/7/9
 * @描述 : 坐标计算类
 */
public class Transformer {

    private static final float[] sEmptyPointXBuffer = new float[]{};

    private IDataProvider mDataProvider;

    private float mStartPointX;             // 起始绘制点的中心X坐标(已包含主画板左侧距离)
    private float[] mPointXBuffer = sEmptyPointXBuffer;
    private int mStartIndex = -1;           // 当前内容的开始坐标
    private int mStopIndex = -1;            // 当前内容的结束坐标

    private HashMap<String, Integer> mIndexCount = new HashMap<>();
    private HashMap<String, IndexRange> mIndexMap = new HashMap<>();

    private OnViewIndexListener mViewIndexListener;

    public Transformer(IDataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    /**
     * 获取滚动到该索引的滚动值
     *
     * @param index              需要滚动到的索引
     * @param inItemWidthPercent 在item的什么位置计算滚动值
     *                           以item的左侧为锚点，inItemWidthPercent = 0.0f
     *                           以item的中间为锚点，inItemWidthPercent = 0.5f
     *                           以item的右侧为锚点，inItemWidthPercent = 1.0f
     * @param inScreenPercent    需要滚动到主视窗{@link IMainCanvasPort#getMainCanvasWidth()}所在百分比位置，
     *                           需要滚动到视窗左侧，inScreenPercent = 0.0f
     *                           需要滚动到视窗中间，inScreenPercent = 0.5f
     *                           需要滚动到视窗右侧，inScreenPercent = 1.0f
     */
    public int getGoToIndexScrollX(int index, @FloatRange(from = 0, to = 1.0) float inItemWidthPercent,
                                   @FloatRange(from = 0, to = 1.0) float inScreenPercent) {
        float scrollx = (getItemCount() - index - inItemWidthPercent) * mDataProvider.getScalePointWidth()
                - mDataProvider.getMainCanvasPort().getMainCanvasWidth() * (1.0f - inScreenPercent);
        return Math.round(Math.max(scrollx, 0));
    }

    /**
     * 起始绘制点的中心X坐标(已包含主画板左侧距离)
     */
    public float getStartPointX() {
        return mStartPointX;
    }

    /**
     * 获取视图起始索引
     */
    public int getStartIndex() {
        return mStartIndex;
    }

    /**
     * 获取视图结束索引
     */
    public int getStopIndex() {
        return mStopIndex;
    }

    /**
     * 根据屏幕的X坐标获取索引
     */
    public int getIndexByScreenX(float screenX) {
        if (mStartIndex == -1) {
            return -1;
        }
        if (screenX <= mStartPointX) {
            return mStartIndex;
        }
        if (screenX >= mPointXBuffer[mStopIndex - mStartIndex]) {
            return mStopIndex;
        }
        return (int) ((screenX - mStartPointX) / mDataProvider.getScalePointWidth() + 0.5f) + mStartIndex;
    }

    /**
     * 获取指定索引此时在屏幕中的位置
     */
    public float getPointInScreenXByIndex(int index) {
        if (index >= mStartIndex && index <= mStopIndex) {
            return mPointXBuffer[index - mStartIndex];
        }
        return (index - mStartIndex) * mDataProvider.getScalePointWidth() + mStartPointX;
    }

    /**
     * 重置数据边界
     */
    public void resetBounds() {
        int oldStartIndex = mStartIndex;
        int oldStopIndex = mStopIndex;
        mPointXBuffer = sEmptyPointXBuffer;
        mStartPointX = 0;
        mStartIndex = -1;
        mStopIndex = -1;
        if (mViewIndexListener != null && (mStartIndex != oldStartIndex || mStopIndex != oldStopIndex)) {
            mViewIndexListener.viewIndex(mStartIndex, mStopIndex);
        }
    }

    /**
     * 更新数据的边界，边界以{@link IMainCanvasPort}提供的数据为准
     */
    public void updateBounds() {
        int oldStartIndex = mStartIndex;
        int oldStopIndex = mStopIndex;
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
        int size = mStopIndex - mStartIndex + 1;
        if (mPointXBuffer.length < size) {
            mPointXBuffer = new float[size];
        } else if (mPointXBuffer.length - size > 20) {
            // buffer差距超20个缩减buffer长度
            mPointXBuffer = new float[size];
        }
        for (int i = 0; i < mPointXBuffer.length; i++) {
            mPointXBuffer[i] = i * mDataProvider.getScalePointWidth() + mStartPointX;
        }
        for (IndexRange value : mIndexMap.values()) {
            value.resetValue();
        }
        if (getItemCount() > 0) {
            calcMinMax(mDataProvider.getAdapter());
        }
        if (mViewIndexListener != null && (mStartIndex != oldStartIndex || mStopIndex != oldStopIndex)) {
            mViewIndexListener.viewIndex(mStartIndex, mStopIndex);
        }
    }

    private int getItemCount() {
        return mDataProvider.getAdapter().getCount();
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
     * 其中左边界传0，右边界传可绘制区域总宽度
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

    /**
     * 计算当前页面的最大最小值
     *
     * @param adapter
     */
    private void calcMinMax(BaseKChartAdapter adapter) {
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
        for (IndexRange value : mIndexMap.values()) {
            value.calcValueEnd();
        }
    }

    /**
     * 添加相关指标计算类，如果{@link IndexRange#getIndexTag()}返回值一致，会被认为是同一类型的指标，
     * 会在原有基础计数器+1，不会被添加进{@link #mIndexMap}计算容器中
     */
    public void addIndexRange(IndexRange indexRange) {
        final IndexRange realIndex = getRealIndexRange(indexRange);
        if (realIndex == null || TextUtils.isEmpty(realIndex.getIndexTag())) return;

        if (realIndex instanceof IndexRangeSet) {
            ((IndexRangeSet) realIndex).setCanChangeIndex(false);
        }
        Integer count = mIndexCount.get(realIndex.getIndexTag());
        if (count == null) {
            mIndexCount.put(realIndex.getIndexTag(), 1);
            mIndexMap.put(realIndex.getIndexTag(), realIndex);
            return;
        }
        if (mIndexMap.get(realIndex.getIndexTag()) != realIndex) {
            throw new IllegalArgumentException("Inconsistent indexRange instances: " + realIndex.getIndexTag());
        }
        mIndexCount.put(realIndex.getIndexTag(), count + 1);
    }

    /**
     * 移除指标计算类，如果{@link IndexRange#getIndexTag()}返回值一致，会被认为是同一类型的指标，
     * 会在原有基础计数器-1，如果
     */
    public void removeIndexRange(IndexRange indexRange) {
        final IndexRange realIndex = getRealIndexRange(indexRange);
        if (realIndex == null || TextUtils.isEmpty(realIndex.getIndexTag())) return;

        Integer count = mIndexCount.get(realIndex.getIndexTag());
        if (count == null) return;
        if (count > 1) {
            mIndexCount.put(realIndex.getIndexTag(), count - 1);
            return;
        }
        mIndexCount.remove(realIndex.getIndexTag());
        IndexRange remove = mIndexMap.remove(realIndex.getIndexTag());
        if (remove instanceof IndexRangeSet) {
            ((IndexRangeSet) remove).setCanChangeIndex(true);
        }
    }

    /**
     * 获取真实的指标计算类
     */
    private IndexRange getRealIndexRange(IndexRange indexRange) {
        if (indexRange instanceof IndexRangeContainer) {
            return getRealIndexRange(((IndexRangeContainer) indexRange).getRealIndexRange());
        } else {
            return indexRange;
        }
    }

    /**
     * 获取当前计算的所有指标tag
     */
    public Set<String> getIndexTags() {
        return mIndexCount.keySet();
    }

    public void setOnViewIndexListener(OnViewIndexListener listener) {
        mViewIndexListener = listener;
    }

    public interface OnViewIndexListener {
        void viewIndex(int startIndex, int endIndex);
    }
}
