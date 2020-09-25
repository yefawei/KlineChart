package com.benben.kchartlib.drawing;

import android.graphics.Canvas;

import com.benben.kchartlib.impl.IDataProvider;
import com.benben.kchartlib.impl.IParentPortLayout;
import com.benben.kchartlib.index.range.IndexRange;

/**
 * @日期 : 2020/7/1
 * @描述 :
 */
public interface IDrawing {

    void setWidth(int width);

    void setHeight(int height);

    void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider dataProvider);

    void detachedParentPortLayout();

    boolean isAttachedParentPortLayout();

    /**
     * 画板是否有效
     */
    boolean isValid();

    /**
     * 绘制范围是否限制在视图范围内
     */
    boolean drawInViewPort();

    /**
     * 设置绘制范围是否限制在视图范围内
     */
    void setDrawInViewPort(boolean in);

    /**
     * 获取指标范围计算类
     */
    IndexRange getIndexRange();

    void preCalcDataValue();

    /**
     * 绘制空值
     */
    void drawEmpty(Canvas canvas);

    /**
     * 绘制
     */
    void drawData(Canvas canvas);
}
