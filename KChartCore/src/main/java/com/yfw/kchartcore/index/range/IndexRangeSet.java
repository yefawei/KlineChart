package com.yfw.kchartcore.index.range;

import com.yfw.kchartcore.index.IEntity;

import java.util.ArrayList;

/**
 * @日期 : 2020/7/10
 * @描述 : 组合指标计算类
 */
public final class IndexRangeSet extends IndexRange {

    private final ArrayList<IndexRange> mIndexRanges = new ArrayList<>();

    private String mIndexTag = "Set";
    private boolean mCanChangeIndex = true;

    public IndexRangeSet() {
        super();
    }

    public IndexRangeSet(float paddingPercent) {
        super(paddingPercent);
    }

    public IndexRangeSet(boolean reverse, int sideMode, float paddingPercent, float startValue) {
        super(reverse, sideMode, paddingPercent, startValue);
    }

    @Override
    public String getIndexTag() {
        return mIndexTag;
    }

    @Override
    protected float calcExtendedMaxValue(float curMaxValue, String key, Object obj) {
        for (int i = 0; i < mIndexRanges.size(); i++) {
            curMaxValue = mIndexRanges.get(i).calcExtendedMaxValue(curMaxValue, key, obj);
        }
        return curMaxValue;
    }

    @Override
    protected float calcExtendedMinValue(float curMinValue, String key, Object obj) {
        for (int i = 0; i < mIndexRanges.size(); i++) {
            curMinValue = mIndexRanges.get(i).calcExtendedMinValue(curMinValue, key, obj);
        }
        return curMinValue;
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        for (int i = 0; i < mIndexRanges.size(); i++) {
            curMaxValue = mIndexRanges.get(i).calcMaxValue(index, curMaxValue, entity);
        }
        return curMaxValue;
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        for (int i = 0; i < mIndexRanges.size(); i++) {
            curMinValue = mIndexRanges.get(i).calcMinValue(index, curMinValue, entity);
        }
        return curMinValue;
    }

    public void setCanChangeIndex(boolean can) {
        mCanChangeIndex = can;
    }

    public void addIndex(IndexRange indexRange) {
        if (indexRange == null) return;
        if (!mCanChangeIndex) {
            throw new IllegalStateException("Adding indicators is not allowed at this time!");
        }
        if (isReverse() != indexRange.isReverse()) {
            throw new IllegalArgumentException("Reverse is inconsistent!");
        }
        if (getSideMode() != indexRange.getSideMode()) {
            throw new IllegalArgumentException("SideMode is inconsistent!");
        }
        mIndexRanges.add(indexRange);
        generateTag();
    }

    public void removeIndex(IndexRange indexRange) {
        if (!mCanChangeIndex) {
            throw new IllegalStateException("Indicator removal is not allowed at this time!");
        }
        mIndexRanges.remove(indexRange);
        generateTag();
    }

    private void generateTag() {
        if (mIndexRanges.size() == 0) {
            mIndexTag = "";
            return;
        }
        if (mIndexRanges.size() == 1) {
            mIndexTag = mIndexRanges.get(0).getIndexTag();
            return;
        }
        StringBuilder sb = new StringBuilder("Set");
        for (int i = 0; i < mIndexRanges.size(); i++) {
            sb.append("-");
            sb.append(mIndexRanges.get(i).getIndexTag());
        }
        mIndexTag = sb.toString();
    }
}
