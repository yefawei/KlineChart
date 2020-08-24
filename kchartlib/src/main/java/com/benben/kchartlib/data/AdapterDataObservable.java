package com.benben.kchartlib.data;

import android.database.Observable;

/**
 * @日期 : 2020/8/21
 * @描述 :
 */
public class AdapterDataObservable extends Observable<AdapterDataObserver> {

    public void notifyChanged() {
        for (AdapterDataObserver observer : mObservers) {
            observer.onChanged();
        }
    }

    public void notifyFirstInserted(int itemCount) {
        for (AdapterDataObserver observer : mObservers) {
            observer.onFirstInserted(itemCount);
        }
    }

    public void notifyLastUpdated() {
        for (AdapterDataObserver observer : mObservers) {
            observer.notifyLastUpdated();
        }
    }

    public void notifyLastInserted(int itemCount) {
        for (AdapterDataObserver observer : mObservers) {
            observer.onLastInserted(itemCount);
        }
    }
}
