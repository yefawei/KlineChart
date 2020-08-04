package com.benben.kchartlib.adapter;

import android.database.DataSetObserver;

import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/7/1
 * @描述 :
 */
public interface IAdapter<T extends IEntity> {

    /**
     * 获取数量
     */
    int getCount();

    T getItem(int position);

    /**
     * 注册一个数据观察者
     *
     * @param observer 数据观察者
     */
    void registerDataSetObserver(DataSetObserver observer);

    /**
     * 移除一个数据观察者
     *
     * @param observer 数据观察者
     */
    void unregisterDataSetObserver(DataSetObserver observer);

    /**
     * 当数据发生变化时调用
     */
    void notifyDataSetChanged();
}
