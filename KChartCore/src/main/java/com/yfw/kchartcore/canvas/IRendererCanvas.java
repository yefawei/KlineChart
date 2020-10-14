package com.yfw.kchartcore.canvas;

import android.graphics.Canvas;

import com.yfw.kchartcore.drawing.Drawing;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.layout.IParentPortLayout;

/**
 * @日期 : 2020/7/1
 * @描述 : 渲染器绘制
 */
public interface IRendererCanvas<T extends IEntity> {

    void setWidth(int width);

    void setHeight(int height);

    void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider<T> dataProvider);

    void detachedParentPortLayout();

    /**
     * 画板是否有效
     */
    boolean isValid();

    void addDrawing(Drawing<?, T> drawing);

    /**
     * 添加绘制实体
     *
     * @param isMainIndexDrawing 是否是主视图指标绘制实体，如果是，会移除相关属性以铺满水平方向
     */
    void addDrawing(Drawing<?, T> drawing, boolean isMainIndexDrawing);

    void removeDrawing(Drawing<?, T> drawing);

    /**
     * 更新数据值
     *
     * @param emptyBounds true:无边界信息
     */
    void preCalcDataValue(boolean emptyBounds);

    /**
     * 绘制
     */
    void render(Canvas canvas);
}
