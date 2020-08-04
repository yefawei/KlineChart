package com.benben.kchartlib.index;

import java.util.ArrayList;

/**
 * @日期 : 2020/7/10
 * @描述 : 组合指标计算类
 */
public class IndexSet extends Index {

    private ArrayList<Index> mIndices = new ArrayList<>();

    private String mIndexTag = "Set";
    private boolean mCanChangeIndex = true;

    public IndexSet() {
        super();
    }

    public IndexSet(float paddingPercent) {
        super(paddingPercent);
    }

    public IndexSet(boolean reverse, int sideMode, float paddingPercent, float startValue) {
        super(reverse, sideMode, paddingPercent, startValue);
    }

    @Override
    public String getIndexTag() {
        return mIndexTag;
    }

    @Override
    protected float calcMaxValue(int index, float curMaxValue, IEntity entity) {
        for (Index i : mIndices) {
            curMaxValue = i.calcMaxValue(index, curMaxValue, entity);
        }
        return curMaxValue;
    }

    @Override
    protected float calcMinValue(int index, float curMinValue, IEntity entity) {
        for (Index i : mIndices) {
            curMinValue = i.calcMinValue(index, curMinValue, entity);
        }
        return curMinValue;
    }

    public void setCanChangeIndex(boolean can) {
        mCanChangeIndex = can;
    }

    public void addIndex(Index index) {
        if (index == null) return;
        if (!mCanChangeIndex) {
            throw new IllegalStateException("Adding indicators is not allowed at this time!");
        }
        if (isReverse() != index.isReverse()) {
            throw new IllegalArgumentException("Reverse is inconsistent!");
        }
        if (getSideMode() != index.getSideMode()) {
            throw new IllegalArgumentException("SideMode is inconsistent!");
        }
        mIndices.add(index);
        generateTag();
    }

    public void removeIndex(Index index) {
        if (!mCanChangeIndex) {
            throw new IllegalStateException("Indicator removal is not allowed at this time!");
        }
        mIndices.remove(index);
        generateTag();
    }

    private void generateTag() {
        StringBuilder sb = new StringBuilder("Set");
        for (Index index : mIndices) {
            sb.append("-");
            sb.append(index.getIndexTag());
        }
        mIndexTag = sb.toString();
    }
}
