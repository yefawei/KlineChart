package com.yfw.kchartcore.data;

import android.text.TextUtils;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.yfw.kchartcore.adapter.BaseKChartAdapter;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.index.range.IndexRange;
import com.yfw.kchartcore.index.range.IndexRangeContainer;
import com.yfw.kchartcore.layout.IMainCanvasPort;

import java.util.ArrayList;
import java.util.List;

/**
 * @日期 : 2020/7/9
 * @描述 : 坐标计算类
 */
public class Transformer<T extends IEntity> {

    private static final float[] sEmptyPointXBuffer = new float[]{};

    private final IDataProvider<T> mDataProvider;

    private float mStartPointX;             // 起始绘制点的中心X坐标(已包含主画板左侧距离)
    private float[] mPointXBuffer = sEmptyPointXBuffer;
    private int mStartIndex = -1;           // 当前内容的开始坐标
    private int mStopIndex = -1;            // 当前内容的结束坐标

    private ArrayList<String> mTags;
    private final List<Range> mRanges = new ArrayList<>();
    private final List<RangeGroup> mGroups = new ArrayList<>();
    private OnViewIndexListener mViewIndexListener;

    public Transformer(IDataProvider<T> dataProvider) {
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
     * 是否是无边界信息
     */
    public boolean isEmptyBounds() {
        return mStartIndex == -1;
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
     * 设为无边界信息，即不绘制任何K线数据
     */
    public void emptyBounds() {
        int oldStartIndex = mStartIndex;
        int oldStopIndex = mStopIndex;
        mPointXBuffer = sEmptyPointXBuffer;
        mStartPointX = 0;
        mStartIndex = -1;
        mStopIndex = -1;
        if (mStartIndex != oldStartIndex || mStopIndex != oldStopIndex) {
            for (int i = 0; i < mRanges.size(); i++) {
                mRanges.get(i).range.resetValue(true);
            }
            for (int i = 0; i < mRanges.size(); i++) {
                mRanges.get(i).range.calcValueEnd(true);
            }
            if (mViewIndexListener != null) {
                mViewIndexListener.viewIndex(mStartIndex, mStopIndex);
            }
        }
    }

    /**
     * 计算数据的边界，边界以{@link IMainCanvasPort}提供的数据为准
     */
    public void calcBounds() {
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
        calcMinMax(mDataProvider.getAdapter());
        if (mStartIndex == oldStartIndex && mStopIndex == oldStopIndex) {
            return;
        }
        if (mViewIndexListener != null) {
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
     */
    private void calcMinMax(BaseKChartAdapter<T> adapter) {
        for (int i = 0; i < mRanges.size(); i++) {
            mRanges.get(i).range.resetValue(false);
        }
        for (int i = 0; i < mRanges.size(); i++) {
            mRanges.get(i).range.calcExtendedData();
        }
        for (int i = mStartIndex; i <= mStopIndex; i++) {
            IEntity cur = adapter.getItem(i);
            for (int j = 0; j < mRanges.size(); j++) {
                mRanges.get(j).range.calcMinMaxValue(i, cur);
            }
        }
        for (int i = 0; i < mRanges.size(); i++) {
            mRanges.get(i).range.calcPaddingValue();
        }
        for (int i = 0; i < mGroups.size(); i++) {
            synchronizationGroupValue(mGroups.get(i));
        }

        for (int i = 0; i < mRanges.size(); i++) {
            mRanges.get(i).range.calcValueEnd(false);
        }
    }

    /**
     * 同步组的最大最小值
     */
    private void synchronizationGroupValue(RangeGroup rangeGroup) {
        float maxValue = Float.MIN_VALUE;
        float minValue = Float.MAX_VALUE;
        Range range;
        for (int i = 0; i < rangeGroup.ranges.size(); i++) {
            range = rangeGroup.ranges.get(i);
            if (maxValue < range.range.getMaxValue()) {
                maxValue = range.range.getMaxValue();
            }
            if (minValue > range.range.getMinIndex()) {
                minValue = range.range.getMinValue();
            }
        }
        for (int i = 0; i < rangeGroup.ranges.size(); i++) {
            range = rangeGroup.ranges.get(i);
            range.range.customMaxMinValue(maxValue, minValue);
        }
    }

    /**
     * 添加相关指标计算类，如果{@link IndexRange#getIndexTag()}返回值一致，会被认为是同一类型的指标，
     * 会在原有基础计数器+1，不会被添加进{@link #mRanges}计算容器中，防止重复计算浪费资源
     *
     * @param groupId 同一组id要求{@link IndexRange#isReverse()} 与 {@link IndexRange#getSideMode()}一致
     */
    public void addIndexRange(int groupId, IndexRange indexRange) {
        final IndexRange realIndex = getRealIndexRange(indexRange);
        if (realIndex == null || TextUtils.isEmpty(realIndex.getIndexTag())) return;

        Range range = getRangeByTag(mRanges, realIndex.getIndexTag());
        addGroup(groupId, realIndex);
        if (range == null) {
            range = new Range();
            range.count = 1;
            range.range = realIndex;
            mRanges.add(range);
            return;
        }
        if (range.range != realIndex) {
            throw new IllegalArgumentException("Inconsistent indexRange instances: " + realIndex.getIndexTag());
        }
        range.count += 1;
    }

    private void addGroup(int groupId, IndexRange indexRange) {
        if (groupId == IndexRange.NO_GROUP) {
            return;
        }
        RangeGroup group = null;
        for (int i = 0; i < mGroups.size(); i++) {
            RangeGroup rangeGroup = mGroups.get(i);
            if (rangeGroup.groudId == groupId) {
                group = rangeGroup;
                break;
            }
        }
        if (group == null) {
            group = new RangeGroup();
            group.groudId = groupId;

            Range range = new Range();
            range.count = 1;
            range.range = indexRange;
            group.ranges.add(range);

            mGroups.add(group);
            return;
        }
        Range inRange = group.ranges.get(0);
        if (inRange.range.isReverse() != indexRange.isReverse()) {
            throw new IllegalArgumentException("Reverse is inconsistent!");
        }
        if (inRange.range.getSideMode() != indexRange.getSideMode()) {
            throw new IllegalArgumentException("SideMode is inconsistent!");
        }
        Range range = getRangeByTag(group.ranges, indexRange.getIndexTag());
        if (range == null) {
            range = new Range();
            range.count = 1;
            range.range = indexRange;
            group.ranges.add(range);
            return;
        }
        range.count += 1;
    }

    /**
     * 移除指标计算类，如果{@link IndexRange#getIndexTag()}返回值一致，会被认为是同一类型的指标，
     * 会在原有基础计数器-1
     */
    public void removeIndexRange(int groupId, IndexRange indexRange) {
        final IndexRange realIndex = getRealIndexRange(indexRange);
        if (realIndex == null || TextUtils.isEmpty(realIndex.getIndexTag())) return;

        Range range = getRangeByTag(mRanges, realIndex.getIndexTag());
        if (range == null) return;
        if (range.range != realIndex) {
            throw new IllegalArgumentException("Inconsistent indexRange instances: " + realIndex.getIndexTag());
        }
        removeGroup(groupId, realIndex);
        if (range.count > 1) {
            range.count -= 1;
            return;
        }
        mRanges.remove(range);
    }

    private void removeGroup(int groupId, IndexRange indexRange) {
        if (groupId == IndexRange.NO_GROUP) {
            return;
        }
        RangeGroup group = null;
        for (int i = 0; i < mGroups.size(); i++) {
            RangeGroup rangeGroup = mGroups.get(i);
            if (rangeGroup.groudId == groupId) {
                group = rangeGroup;
                break;
            }
        }
        if (group == null) return;
        Range range = getRangeByTag(group.ranges, indexRange.getIndexTag());
        if (range == null) return;
        if (range.count > 1) {
            range.count -= 1;
            return;
        }
        group.ranges.remove(range);
        if (group.ranges.isEmpty()) {
            mGroups.remove(group);
        }
    }

    @Nullable
    private Range getRangeByTag(List<Range> ranges, String tag) {
        for (int i = 0; i < ranges.size(); i++) {
            if (ranges.get(i).range.getIndexTag().equals(tag)) {
                return ranges.get(i);
            }
        }
        return null;
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
    public List<String> getIndexTags() {
        if (mTags == null) {
            mTags = new ArrayList<>();
        } else {
            mTags.clear();
        }
        for (int i = 0; i < mRanges.size(); i++) {
            mTags.add(mRanges.get(i).range.getIndexTag());
        }
        return (List<String>) mTags.clone();
    }

    public void setOnViewIndexListener(OnViewIndexListener listener) {
        mViewIndexListener = listener;
    }

    public interface OnViewIndexListener {
        /**
         * 可视索引范围
         *
         * @param startIndex item开始索引
         * @param endIndex   item结束索引
         */
        void viewIndex(int startIndex, int endIndex);
    }

    private static class RangeGroup {
        int groudId;
        List<Range> ranges = new ArrayList<>();
    }

    private static class Range {
        int count;
        IndexRange range;
    }


}
