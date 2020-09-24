package com.benben.kchartlib.touch;

import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/8/31
 * @描述 : 点击标记物
 */
public class TapMarkerOption {

    private float mX;
    private float mY;
    private int mIndex;
    private IEntity mEntity;
    private boolean mClick;
    private boolean mCanDispatch;

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        mY = y;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        if (mIndex == index) return;
        mIndex = index;
        if (mClick) {
            mCanDispatch = true;
        }
    }

    public IEntity getEntity() {
        return mEntity;
    }

    public void setEntity(IEntity entity) {
        this.mEntity = entity;
    }

    public boolean isClick() {
        return mClick;
    }

    public void setClick(boolean click) {
        if (mClick) return;
        mClick = click;
        mCanDispatch = click;
    }

    public boolean canDispatch() {
        return mCanDispatch;
    }

    public void consumeDispatch() {
        mCanDispatch = false;
    }

    public void reset() {
        mX = 0;
        mY = 0;
        mIndex = 0;
        mEntity = null;
        mClick = false;
        mCanDispatch = false;
    }
}
