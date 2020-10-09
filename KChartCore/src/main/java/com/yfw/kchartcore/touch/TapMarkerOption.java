package com.yfw.kchartcore.touch;


import com.yfw.kchartcore.index.IEntity;

/**
 * @日期 : 2020/8/31
 * @描述 : 触碰信息
 */
public class TapMarkerOption {

    private float mX;           // 触碰时的X坐标
    private float mY;           // 触碰时的Y坐标
    private int mIndex;         // 索引，会根据当前数据量有所更新
    private IEntity mEntity;    // 对应的触碰西信息
    private boolean mClick;     // 处于有效触碰信息状态
    private boolean mCanDispatch;   // 触碰信息是否可分发

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

    /**
     * 更新索引信息
     */
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

    /**
     * 触碰信息是否可分发
     */
    public boolean canDispatch() {
        return mCanDispatch;
    }

    /**
     * 消费掉触碰信息
     */
    public void consumeDispatch() {
        mCanDispatch = false;
    }

    /**
     * 重置所有数据
     */
    public void reset() {
        mX = 0;
        mY = 0;
        mIndex = 0;
        mEntity = null;
        mClick = false;
        mCanDispatch = false;
    }
}
