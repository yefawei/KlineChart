package com.benben.kchartlib.data;

import android.database.Observable;

/**
 * @日期 : 2020/8/21
 * @描述 :
 */
public class AdapterDataObservable extends Observable<AdapterDataObserver> {

    /**
     * 通知更新所有数据
     */
    public void notifyChanged() {
        for (AdapterDataObserver observer : mObservers) {
            observer.onChanged();
        }
    }

    /**
     * 通知头部插入新数据
     */
    public void notifyFirstInserted(int itemCount) {
        for (AdapterDataObserver observer : mObservers) {
            observer.onFirstInserted(itemCount);
        }
    }

    /**
     * 通知最后一个数据有更新
     */
    public void notifyLastUpdated() {
        for (AdapterDataObserver observer : mObservers) {
            observer.notifyLastUpdated();
        }
    }

    /**
     * 通知尾部有插入新数据
     */
    public void notifyLastInserted(int itemCount) {
        for (AdapterDataObserver observer : mObservers) {
            observer.onLastInserted(itemCount);
        }
    }
}
