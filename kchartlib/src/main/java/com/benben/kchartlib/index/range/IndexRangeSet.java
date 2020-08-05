package com.benben.kchartlib.index.range;

import com.benben.kchartlib.index.IEntity;

import java.util.ArrayList;

/**
 * @日期 : 2020/7/10
 * @描述 : 组合指标计算类
 */
public class IndexRangeSet extends IndexRange {

    private ArrayList<IndexRange> mIndexRanges = new ArrayList<>();

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
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        for (IndexRange i : mIndexRanges) {
            curMaxValue = i.calcMaxValue(index, curMaxValue, entity);
        }
        return curMaxValue;
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        for (IndexRange i : mIndexRanges) {
            curMinValue = i.calcMinValue(index, curMinValue, entity);
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
        StringBuilder sb = new StringBuilder("Set");
        for (IndexRange indexRange : mIndexRanges) {
            sb.append("-");
            sb.append(indexRange.getIndexTag());
        }
        mIndexTag = sb.toString();
    }
}