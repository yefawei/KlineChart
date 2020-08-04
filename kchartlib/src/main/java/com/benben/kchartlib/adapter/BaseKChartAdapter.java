package com.benben.kchartlib.adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/7/1
 * @描述 :
 */
public abstract class BaseKChartAdapter<T extends IEntity> implements IAdapter<T> {

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    @Override
    public final void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public final void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    @Override
    public final void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }
}
