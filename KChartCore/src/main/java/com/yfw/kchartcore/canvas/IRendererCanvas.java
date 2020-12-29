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

    /**
     * 获取绘制数量
     */
    int drawingSize();

    /**
     * 获取绘制实体所在的索引
     */
    int drawingIndexOf(Drawing<T> drawing);

    /**
     * 通过tag获取实体所在的索引
     */
    int drawingIndexTag(String tag);

    void addDrawing(Drawing<T> drawing);

    /**
     * 将绘制添加到指定索引处
     */
    void addDrawing(int index, Drawing<T> drawing);

    /**
     * 添加绘制实体
     *
     * @param isMainIndexDrawing 是否是主视图指标绘制实体，如果是，会移除相关属性以铺满水平方向
     */
    void addDrawing(Drawing<T> drawing, boolean isMainIndexDrawing);

    /**
     * 将绘制添加到指定索引处
     *
     * @param isMainIndexDrawing 是否是主视图指标绘制实体，如果是，会移除相关属性以铺满水平方向
     */
    void addDrawing(int index, Drawing<T> drawing, boolean isMainIndexDrawing);

    /**
     * 替换指定索引绘制
     */
    void replaceDrawing(int index, Drawing<T> drawing);

    /**
     * 根据索引移除绘制
     */
    void removeDrawing(int index);

    /**
     * 根据绘制自身移除
     */
    void removeDrawing(Drawing<T> drawing);

    /**
     * 根据tag移除绘制
     */
    void removeDrawingByTag(String tag);

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
