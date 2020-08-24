package com.benben.kchartlib.adapter;

import com.benben.kchartlib.data.AdapterDataObservable;
import com.benben.kchartlib.data.AdapterDataObserver;
import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/7/1
 * @描述 :
 */
public abstract class BaseKChartAdapter<T extends IEntity> implements IAdapter<T> {

    private final AdapterDataObservable mDataSetObservable = new AdapterDataObservable();

    @Override
    public final void registerDataSetObserver(AdapterDataObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public final void unregisterDataSetObserver(AdapterDataObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * 更新数据内容
     */
    @Override
    public final void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    /**
     * 添加头部数据
     *
     * @param itemCount 添加的数量
     */
    @Override
    public void notifyFirstInserted(int itemCount) {
        mDataSetObservable.notifyFirstInserted(itemCount);
    }

    /**
     * 更新末尾数据
     */
    @Override
    public void notifyLastUpdated() {
        mDataSetObservable.notifyLastUpdated();
    }

    /**
     * 添加尾部数据
     *
     * @param itemCount 添加的数量
     */
    @Override
    public void notifyLastInserted(int itemCount) {
        mDataSetObservable.notifyLastInserted(itemCount);
    }
}
