package com.yfw.kchartcore.adapter;

import com.yfw.kchartcore.data.AdapterDataObserver;
import com.yfw.kchartcore.index.IEntity;

import java.util.List;

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
     * 准备指标数据
     */
    void prepareIndexData(List<String> indexTags);

    /**
     * 注册一个数据观察者
     *
     * @param observer 数据观察者
     */
    void registerDataSetObserver(AdapterDataObserver observer);

    /**
     * 移除一个数据观察者
     *
     * @param observer 数据观察者
     */
    void unregisterDataSetObserver(AdapterDataObserver observer);

    /**
     * 通知所有数据更新
     */
    void notifyDataSetChanged();

    /**
     * 头部数据有所添加
     */
    void notifyFirstInserted(int itemCount);

    /**
     * 末尾数据有所更新
     */
    void notifyLastUpdated(int index);

    /**
     * 尾部数据有所添加
     */
    void notifyLastInserted(int itemCount);
}
