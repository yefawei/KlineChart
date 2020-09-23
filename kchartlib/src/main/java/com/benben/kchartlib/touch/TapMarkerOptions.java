package com.benben.kchartlib.touch;

import com.benben.kchartlib.index.IEntity;

/**
 * @日期 : 2020/8/31
 * @描述 : 点击标记物
 */
public class TapMarkerOptions {

    private float mX;
    private float mY;
    private int mIndex;
    private IEntity mEntity;
    private boolean mClick;

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
        mIndex = index;
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
        mClick = click;
    }

    public void reset() {
        mX = 0;
        mY = 0;
        mIndex = 0;
        mEntity = null;
        mClick = false;
    }
}
